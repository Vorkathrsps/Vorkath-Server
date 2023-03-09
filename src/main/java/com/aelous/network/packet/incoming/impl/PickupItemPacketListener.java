package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.DistancedActionTask;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.utility.Color;

import java.util.Optional;

import static com.aelous.utility.CustomItemIdentifiers.ESCAPE_KEY;

/**
 * This packet listener is used to pick up ground items
 * that exist in the world.
 *
 * @author Patrick van Elderen | 13 feb. 2019 : 12:57:23
 * @see <a href="https://www.rune-server.ee/members/_Patrick_/">Rune-Server profile</a>
 */
public class PickupItemPacketListener implements PacketListener {

    /**
     * Handles picking up the item
     *
     * @param player   The {@link Player} picking up the item
     * @param item       The item
     * @param tile The coordinates of the item
     */
    private void pickup(Player player, Item item, Tile tile) {
        Optional<GroundItem> groundItem = GroundItemHandler.getGroundItem(item.getId(), tile, player);
        if (groundItem.isPresent()) {
            GroundItemHandler.pickup(player, item.getId(), tile);
        }
    }

    /**
     * @param player   The {@link Player}
     * @param tile The coordinates of the spot
     * @return If the player is standing on this spot
     */
    private boolean onSpot(Player player, Tile tile) {
        return player.tile().isWithinDistance(tile,1);
    }

    public static void respawn(Item item, Tile position, int ticks) {
        TaskManager.submit(new Task("PickupItemPacketListener_respawn_task", ticks) {
            @Override
            protected void execute() {
                GroundItem next = new GroundItem(item, position.copy(), null);
                GroundItemHandler.createGroundItem(next);
                stop();
            }
        });
    }

    @Override
    public void handleMessage(final Player player, Packet packet) {
        final int y = packet.readLEShort();
        final int itemId = packet.readShort();
        final int x = packet.readLEShort();

        final Tile tile = new Tile(x, y, player.tile().getLevel());

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        if (player.busy()) {
            return;
        }

        // Make sure distance isn't way off..
        if (Math.abs(player.tile().getX() - x) > 25 || Math.abs(player.tile().getY() - y) > 25) {
            player.getMovementQueue().clear();
            return;
        }

        if (!player.locked() && !player.dead()) {
            boolean newAccount = player.getAttribOr(AttributeKey.NEW_ACCOUNT, false);
            if (newAccount) {
                player.message("You have to select your game mode before you can continue.");
                return;
            }

            if (GameServer.properties().debugMode && player.getPlayerRights().isOwner(player)) {
                player.debugMessage(String.format("pickup item - Item: %d Location: %s", itemId, tile));
            }

            player.stopActions(false);
            player.getSkills().stopSkillable();
            player.getCombat().reset();

            player.putAttrib(AttributeKey.INTERACTED_GROUNDITEM, itemId);
            player.putAttrib(AttributeKey.INTERACTION_OPTION, 3);

            //Do actions...

            if (onSpot(player, tile)) {
                if (itemId == ESCAPE_KEY) {
                    if (player.getSkills().combatLevel() < 126) {
                        player.message(Color.RED.wrap("You need to be at least level 126 to pickup this key."));
                        return;
                    }
                }
                pickup(player, new Item(itemId), tile);
            } else {
                player.setDistancedTask(new DistancedActionTask() {
                    @Override
                    public void onReach() {
                        if (itemId == ESCAPE_KEY) {
                            if (player.getSkills().combatLevel() < 126) {
                                player.message(Color.RED.wrap("You need to be at least level 126 to pickup this key."));
                                stop();
                                return;
                            }
                        }
                        pickup(player, new Item(itemId), tile);
                        stop();
                    }

                    @Override
                    public boolean reached() {
                        return onSpot(player, tile);
                    }
                });
            }
           /* GroundItem groundItem = new GroundItem(new Item(itemId), tile, player);
            player.getRouteFinder().routeGroundItem(groundItem, distance -> pickup(player, new Item(itemId), tile, distance));*/
        }
    }
}

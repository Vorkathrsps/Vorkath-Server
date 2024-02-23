package com.cryptic.model.content.areas.wilderness.wildernesskeys;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import java.util.Objects;

/**
 * @Author: Origin
 * @Date: 5/17/2023
 */
public class WildernessKeys extends PacketInteraction {
    /**
     * Rolls the drops for the wilderness keys
     * @param player
     * @param npc
     */
    public static void rollWildernessKey(Player player, NPC npc) {
        if (WildernessArea.inWilderness(npc.tile())) {
            if (Utils.rollDie(85, 1)) {
                Item item = new Item(ItemIdentifiers.KEY_298, 1);
                GroundItem groundItem = new GroundItem(item, npc.tile(), player);
                GroundItemHandler.createGroundItem(groundItem);
                player.message(Color.PURPLE.wrap("<img=2010>You've received a Wilderness Key drop!"));
            }
        }
    }

    /**
     * Handles the spade interaction to spawn the @KeyNpc
     * @param player
     *            the player
     * @param item
     *            the item
     * @param option
     *            the type
     * @return
     */
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (item.getId() == ItemIdentifiers.SPADE) {
            Tile digTile = new Tile(3028, 3915, 0);
            KeyNpc npc = new KeyNpc(10938, new Tile(player.tile().getX(), player.tile().getY(), player.tile().getZ()), player);
            boolean playerOnDigTile = Objects.equals(player.tile(), digTile);
            boolean playerInRange = player.tile().inSqRadius(digTile, 8);
            boolean clickDelay = player.getClickDelay().elapsed(500);
            if (!playerOnDigTile && playerInRange && clickDelay) {
                player.getClickDelay().reset();
                player.getPacketSender().sendPositionalHint(digTile, 2);
                player.message(Color.BLUE.wrap("Use your spade to dig on the marked tile."));
            } else player.message("Please wait before doing this again.");
            if (clickDelay) {
                player.waitForTile(digTile, () -> {
                    if (player.tile().equals(3028, 3915, 0) && player.getInventory().contains(ItemIdentifiers.KEY_298)) {
                        player.getClickDelay().reset();
                        player.getInventory().remove(ItemIdentifiers.KEY_298);
                        player.getPacketSender().sendEntityHintRemoval(true);
                        player.getCombat().setTarget(npc);
                        player.getPacketSender().sendEntityHint(npc);
                        World.getWorld().registerNpc(npc);
                    }
                });
            }
            return true;
        }
        return false;
    }

    /**
     * Handles the @KeyNpc death
     * @param player
     * @param npc
     */
    public void onDeath(Player player, NPC npc) {
        player.getPacketSender().sendEntityHintRemoval(true);
        npc.clearAttrib(AttributeKey.OWNING_PLAYER);
        World.getWorld().unregisterNpc(npc);
    }
}


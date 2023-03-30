package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.content.skill.impl.smithing.Bar;
import com.aelous.model.content.skill.impl.smithing.EquipmentMaking;
import com.aelous.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.npc.impl.MaxHitDummyNpc;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.OPEN_CHEST_3194;
import static org.apache.logging.log4j.util.Unbox.box;

/**
 * @author Ynneh | 14/04/2022 - 00:18
 * <https://github.com/drhenny>
 */
public class ObjectInteractionHandler implements PacketListener {

    private static final Logger logger = LogManager.getLogger(ObjectInteractionHandler.class);

    @Override
    public void handleMessage(Player player, Packet packet) throws Exception {

        int opcode = packet.getOpcode();

        int x = -1, y = -1, id = -1, option = -1;

        if (opcode == 132) {
            x = packet.readLEShortA();
            id = packet.readInt();
            y = packet.readUnsignedShortA();
            option = 1;
        }

        if (opcode == 252) {
            x = packet.readLEShortA();
            id = packet.readInt();
            y = packet.readUnsignedShortA();
            option = 2;
        }

        if (opcode == 70) {
            x = packet.readLEShort();
            y = packet.readShort();
            id = packet.readInt();
            option = 3;
        }

        if (opcode == 228) {
            x = packet.readLEShortA();
            id = packet.readInt();
            y = packet.readLEShortA();
            option = 4;
        }

        if (opcode == -1)
            return;

        int height = player.tile().getLevel();

        Tile tile = new Tile(x, y, height);

        var object = tile.getObject(id, -1, -1);
        if (object == null && tile.getZ() > 3) {
            object = new Tile(tile.x, tile.y, tile.getZ() % 4).getObject(id, -1, -1);
        }

        player.debug("click %s opt %d", object, option);

        if (player.dead() || player.busy() || player.locked() || object == null)
            return;

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        player.afkTimer.reset();

        int finalOption = option;

        if (object.definition() == null) {
            logger.error("ObjectDefinition for object {} is null for player " + player.toString() + ".", box(id));
            return;
        }
        player.stopActions(false);
        player.putAttrib(AttributeKey.INTERACTION_OBJECT, object);
        player.putAttrib(AttributeKey.INTERACTION_OPTION, finalOption);
        GameObject finalObject = object;
        player.getRouteFinder().routeObject(object, () -> handleAction(player, finalObject, finalOption));
            x = object.getX();
            y = object.getY();
            final int sizeX = object.definition().sizeX;
            final int sizeY = object.definition().sizeY;
            boolean inversed = (object.getRotation() & 0x1) != 0;
            int faceCoordX = x * 2 + (inversed ? sizeY : sizeX);
            int faceCoordY = y * 2 + (inversed ? sizeX : sizeY);
            Tile position = new Tile(faceCoordX, faceCoordY);
            player.getCombat().reset();
            player.setPositionToFace(position);
    }

    private void handleAction(Player player, GameObject object, int option) {
        /** Definitions **/
        if (object == null || object.definition() == null) {
            logger.error("ObjectDefinition for object {} is null for player " + player.toString() + ".", box(object.getId()));
            return;
        }

        /** Override handler **/
        if (PacketInteractionManager.checkObjectInteraction(player, object, option))
            return;

        /** Controller **/
        if (player.getController() != null && player.getController().handleObjectClick(player, object, option))
            return;

        /** Object name **/
        final String name = object.definition().name;

        if (object.definition().id == 10556 || object.definition().id == 21177) {
            player.teleport(GameServer.properties().defaultTile);
            return;
        }

        /** Farming handler **/
        if (player.farming().handleObjectInteraction(object.getId(), object.getX(), object.getY(), option))
            return;

        final boolean bank = object.getId() == OPEN_CHEST_3194 || name.equalsIgnoreCase("Bank booth") || name.equalsIgnoreCase("Bank chest") || name.equalsIgnoreCase("Grand Exchange booth");

        switch (option) {

            case 1: {
                if (name.equalsIgnoreCase("anvil")) {
                    if (object.tile().equals(2794, 2793)) {
                        player.smartPathTo(object.tile());
                        player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> EquipmentMaking.openInterface(player));
                    } else if (object.tile().equals(3343, 9652)) {
                        player.smartPathTo(object.tile());
                        player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> EquipmentMaking.openInterface(player));
                    } else
                        EquipmentMaking.openInterface(player);
                    return;
                }

                if (bank) {
                    player.getBank().open();
                    return;
                }

                if (object.getId() == 35965) {
                    TeleportInterface.open(player);
                    return;
                }

                if (object.getId() == 31858) {
                    player.animate(new Animation(645));
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL, true);
                }

                if (name.equalsIgnoreCase("furnace")) {
                    Arrays.stream(Bar.values()).forEach(b -> player.getPacketSender().sendInterfaceModel(b.getFrame(), 150, b.getBar()));
                    player.getPacketSender().sendChatboxInterface(2400);
                    return;
                }
                player.getPacketSender().sendMessage("Nothing interesting happens.");
                break;
            }

            case 2: {
                if (bank) {
                    player.getBank().open();
                    return;
                }

                if (object.getId() == 31858) {
                    player.animate(new Animation(645));
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENT, true);
                }

                if (name.equalsIgnoreCase("furnace")) {
                    Arrays.stream(Bar.values()).forEach(b -> player.getPacketSender().sendInterfaceModel(b.getFrame(), 150, b.getBar()));
                    player.getPacketSender().sendChatboxInterface(2400);
                    return;
                }
                player.getPacketSender().sendMessage("Nothing interesting happens.");
                break;
            }

            case 3: {
                if (name.equalsIgnoreCase("Grand Exchange booth")) {
                    TradingPost.open(player);
                    return;
                }

                if (object.getId() == 31858) {
                    player.animate(new Animation(645));
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR, true);
                    return;
                }

                player.getPacketSender().sendMessage("Nothing interesting happens.");
                break;
            }

            case 4: {
                player.getPacketSender().sendMessage("Nothing interesting happens.");
                break;
            }
        }
    }
}

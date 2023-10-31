package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.content.skill.impl.smithing.Bar;
import com.cryptic.model.content.skill.impl.smithing.EquipmentMaking;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.OPEN_CHEST_3194;
import static org.apache.logging.log4j.util.Unbox.box;

public class ObjectInteractionHandler implements PacketListener {

    private static final Logger logger = LogManager.getLogger(ObjectInteractionHandler.class);

    @Override
    public void handleMessage(Player player, Packet packet) throws Exception {

        int opcode = packet.getOpcode();

        if (opcode == -1) {
            return;
        }

        switch (opcode) {
            case 132 -> handlePacket132(player, packet);
            case 252 -> handlePacket252(player, packet);
            case 70 -> handlePacket70(player, packet);
            case 228 -> handlePacket228(player, packet);
            default -> {
            }
        }
    }

    private void handlePacket132(Player player, Packet packet) {
        int x = packet.readLEShortA();
        int id = packet.readInt();
        int y = packet.readUnsignedShortA();
        int option = 1;
        handleObjectInteraction(player, x, y, id, option);
    }

    private void handlePacket252(Player player, Packet packet) {
        int x = packet.readLEShortA();
        int id = packet.readInt();
        int y = packet.readUnsignedShortA();
        int option = 2;
        handleObjectInteraction(player, x, y, id, option);
    }

    private void handlePacket70(Player player, Packet packet) {
        int x = packet.readLEShort();
        int y = packet.readShort();
        int id = packet.readInt();
        int option = 3;
        handleObjectInteraction(player, x, y, id, option);
    }

    private void handlePacket228(Player player, Packet packet) {
        int x = packet.readLEShortA();
        int id = packet.readInt();
        int y = packet.readLEShortA();
        int option = 4;
        handleObjectInteraction(player, x, y, id, option);
    }

    private void handleObjectInteraction(Player player, int x, int y, int id, int option) {
        int height = player.tile().getLevel();
        var tile = Tile.get(x, y, height);
        var object = tile == null ? null : tile.getObject(id, -1, -1);

        player.debug("click obj %s at %s option %d", tile, object, option);

        if (tile == null || player.dead() || player.busy() || player.locked() || object == null)
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

        if (object.definition() == null) {
            logger.error("ObjectDefinition for object {} is null for player " + player + ".", box(id));
            return;
        }

        if (player.getCombat() != null) {
            if (player.getCombat().inCombat()) {
                player.getCombat().reset();
            }
        }

        player.stopActions(false);
        player.putAttrib(AttributeKey.INTERACTION_OBJECT, object);
        player.putAttrib(AttributeKey.INTERACTION_OPTION, option);
        BooleanSupplier next_to_object = () -> player.tile().nextTo(new Tile(object.getX(), object.getY(), object.getZ()));
        player.getRouteFinder().routeObject(object, () -> player.waitUntil(next_to_object, () -> {
            int sizeX = object.definition().sizeX;
            int sizeY = object.definition().sizeY;
            boolean inversed = (object.getRotation() & 0x1) != 0;
            int faceCoordX = x * 2 + (inversed ? sizeY : sizeX);
            int faceCoordY = y * 2 + (inversed ? sizeX : sizeY);
            Tile position = new Tile(faceCoordX, faceCoordY);
            player.setPositionToFace(position);
            handleAction(player, object, option);
        }));
    }

    private void handleAction(Player player, GameObject object, int option) {

        if (object == null || object.definition() == null) {
            logger.error("ObjectDefinition for object {} is null for player " + player.toString() + ".", box(Objects.requireNonNull(object).getId()));
            return;
        }

        if (PacketInteractionManager.checkObjectInteraction(player, object, option))
            return;

        if (player.getController() != null && player.getController().handleObjectClick(player, object, option))
            return;

        final String name = object.definition().name;

        if (object.definition().id == 10556 || object.definition().id == 21177) {
            player.teleport(GameServer.properties().defaultTile.tile());
            return;
        }

        final boolean bank = object.getId() == OPEN_CHEST_3194 || name.equalsIgnoreCase("Bank booth") || name.equalsIgnoreCase("Bank chest") || name.equalsIgnoreCase("Grand Exchange booth");

        switch (option) {
            case 1 -> {
                player.getFarming().handleObjectClick(object.tile().x, object.tile().y, 1);
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

                if (object.getId() == 35965 || object.getId() == 47347) {
                    player.setCurrentTabIndex(3);
                    player.getInterfaceManager().open(88000);
                    player.getnewteleInterface().drawInterface(88005);
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
            }
            case 2 -> {
                player.getFarming().handleObjectClick(object.tile().x, object.tile().y, 2);
                if (bank) {
                    player.getBank().open();
                    return;
                }

                if (object.getId() == 31858) {
                    player.animate(new Animation(645));
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENTS, true);
                }

                if (name.equalsIgnoreCase("furnace")) {
                    Arrays.stream(Bar.values()).forEach(b -> player.getPacketSender().sendInterfaceModel(b.getFrame(), 150, b.getBar()));
                    player.getPacketSender().sendChatboxInterface(2400);
                    return;
                }
                player.getPacketSender().sendMessage("Nothing interesting happens.");
            }
            case 3 -> {
                player.getFarming().handleObjectClick(object.tile().x, object.tile().y, 3);
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
            }
            case 4 -> {
                player.getPacketSender().sendMessage("Nothing interesting happens.");
            }
        }
    }
}

package com.cryptic.model.entity.masks.impl.updating;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.masks.UpdateFlag;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.ByteOrder;
import com.cryptic.network.packet.PacketBuilder;
import com.cryptic.network.packet.PacketBuilder.AccessType;
import com.cryptic.network.packet.PacketType;
import com.cryptic.network.packet.ValueType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

/**
 * Represents a player's npc updating task, which loops through all local
 * npcs and updates their masks according to their current attributes.
 *
 * @author Relex lawl
 */
@Slf4j
public class NPCUpdating {

    /**
     * Handles the actual npc updating for the associated player.
     *
     * @return The NPCUpdating instance.
     */
    public static void update(Player player) {
        final PacketBuilder packet = new PacketBuilder(65, PacketType.VARIABLE_SHORT);
        try (final PacketBuilder update = new PacketBuilder()) {
            packet.initializeAccess(AccessType.BIT);
            List<NPC> localNpcs = player.getLocalNpcs();
            Tile playerTile = player.tile();
            packet.putBits(8, localNpcs.size());
            if (localNpcs.size() > 0) {
                List<NPC> updatedNpcs = new ArrayList<>();
                for (NPC npc : localNpcs) {
                    if (npc == null) continue;
                    if (npc.getIndex() != -1 && World.getWorld().getNpcs().contains(npc) && !npc.hidden() && !npc.isTeleportJump() && playerTile.isViewableFrom(npc.tile())) {
                        updateMovement(npc, packet);
                        npc.inViewport(true);
                        if (npc.getUpdateFlag().isUpdateRequired()) {
                            appendUpdates(npc, player, update, false);
                        }
                    } else {
                        updatedNpcs.add(npc);
                        packet.putBits(1, 1);
                        packet.putBits(2, 3);
                    }
                }
                localNpcs.removeAll(updatedNpcs);
                updatedNpcs.clear();
            }
            for (var region : player.getRegions()) {
                for (var npc : region.getNpcs()) {
                    if (npc == null || npc.hidden()) continue;
                    if (localNpcs.contains(npc)) continue;
                    if (player.tile().isViewableFrom(npc.tile())) {
                        localNpcs.add(npc);
                        addNPC(player, npc, packet, npc.isTeleportJump());
                        npc.inViewport(true);
                        if ((npc.getUpdateFlag().isUpdateRequired() || sendNewNpcUpdates(npc))) {
                            appendUpdates(npc, player, update, true);
                        }
                    }
                }
            }
            if (update.buffer().writerIndex() > 0) {
                packet.putBits(14, 16383);
                packet.initializeAccess(AccessType.BYTE);
                packet.writeBuffer(update.buffer());
            } else {
                packet.initializeAccess(AccessType.BYTE);
            }
            player.getSession().write(packet);
        } catch (
            Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean sendNewNpcUpdates(NPC npc) {
        return npc.getInteractingEntity() != null || (npc.getInteractingEntity() == null && npc.lastTileFaced != null);
    }


    /**
     * Adds an npc to the associated player's client.
     *
     * @param npc     The npc to add.
     * @param builder The packet builder to write information on.
     * @return The NPCUpdating instance.
     */
    private static void addNPC(Player player, NPC npc, PacketBuilder builder, boolean legacyTeleport) {
        int yOffset = npc.tile().getY() - player.tile().getY();
        int xOffset = npc.tile().getX() - player.tile().getX();
        builder.putBits(14, npc.getIndex());
        builder.putBits(5, yOffset);
        builder.putBits(5, xOffset);
        builder.putBits(1, legacyTeleport ? 0 : 1);
        builder.putBits(14, npc.id());
        builder.putBits(1, (npc.getUpdateFlag().isUpdateRequired() || sendNewNpcUpdates(npc)) ? 1 : 0);
        boolean updateFacing = npc.walkRadius() == 0;
        builder.putBits(1, updateFacing ? 1 : 0);
        if (updateFacing) {
            Tile tile = new Tile(face(npc).x * 2 + 1, face(npc).y * 2 + 1);
            builder.putBits(14, tile.getX()); //face x
            builder.putBits(14, tile.getY()); //face y
        }
    }

    public static Tile face(NPC npc) {
        Tile currentTile = npc.tile();
        int spawnDirection = npc.spawnDirection();
        Tile dir = switch (spawnDirection) {
            case 1 -> currentTile.transform(0, 10); // n
            case 6 -> currentTile.transform(0, -10); // s
            case 4 -> currentTile.transform(10, 0); // e
            case 3 -> currentTile.transform(-10, 0); // w
            case 0 -> currentTile.transform(-10, 10); // se
            case 2 -> currentTile.transform(10, 10); // ne
            case 5 -> currentTile.transform(-10, -10); // sw
            case 7 -> currentTile.transform(10, -10); // nw
            default -> currentTile; // default case
        };
        return dir;
    }

    /**
     * Updates the npc's movement queue.
     *
     * @param npc The npc who's movement is updated.
     * @param out The packet builder to write information on.
     * @return The NPCUpdating instance.
     */
    private static void updateMovement(NPC npc, PacketBuilder out) {
        if (npc.getRunningDirection().toInteger() == -1) {
            if (npc.getWalkingDirection().toInteger() == -1) {
                if (npc.getUpdateFlag().isUpdateRequired()) {
                    out.putBits(1, 1);
                    out.putBits(2, 0);
                } else {
                    out.putBits(1, 0);
                }
            } else {
                out.putBits(1, 1);
                out.putBits(2, 1);
                out.putBits(3, npc.getWalkingDirection().toInteger());
                out.putBits(1, npc.getUpdateFlag().isUpdateRequired() ? 1 : 0);
            }
        } else {
            out.putBits(1, 1);
            out.putBits(2, 2);
            out.putBits(3, npc.getWalkingDirection().toInteger());
            out.putBits(3, npc.getRunningDirection().toInteger());
            out.putBits(1, npc.getUpdateFlag().isUpdateRequired() ? 1 : 0);
        }
    }

    /**
     * Appends a mask update for {@code npc}.
     *
     * @param npc    The npc to update masks for.
     * @param block  The packet builder to write information on.
     * @param newNpc
     * @return The NPCUpdating instance.
     */
    private static void appendUpdates(NPC npc, Player player, PacketBuilder block, boolean newNpc) {
        var sendFaceTile = newNpc && npc.getInteractingEntity() == null && npc.lastTileFaced != null;
        var sendLockon = newNpc && npc.getInteractingEntity() != null;
        int mask = 0;
        UpdateFlag flag = npc.getUpdateFlag();
        if (flag.flagged(Flag.ANIMATION) && npc.getAnimation() != null) {
            mask |= 0x10;
        }
        if (flag.flagged(Flag.GRAPHIC) && npc.graphic() != null) {
            mask |= 0x80;
        }
        if (flag.flagged(Flag.FIRST_SPLAT)) {
            mask |= 0x8;
        }
        if (flag.flagged(Flag.ENTITY_INTERACTION) || sendLockon) {
            mask |= 0x20;
        }
        if (flag.flagged(Flag.FORCED_CHAT) && npc.getForcedChat() != null) {
            mask |= 0x1;
        }
        if (flag.flagged(Flag.LUMINANCE)) {
            mask |= 0x40;
        }
        if (flag.flagged(Flag.TRANSFORM)) {
            mask |= 0x2;
        }
        if (sendFaceTile || (flag.flagged(Flag.FACE_TILE) && npc.getFaceTile() != null)) {
            mask |= 0x4;
        }
        block.put(mask);
        if (flag.flagged(Flag.ANIMATION) && npc.getAnimation() != null) {
            updateAnimation(block, npc);
        }
        if (flag.flagged(Flag.GRAPHIC) && npc.graphic() != null) {
            updateGraphics(block, npc);
        }
        if (flag.flagged(Flag.FIRST_SPLAT)) {
            updateSingleHit(block, npc, player);
        }
        if (flag.flagged(Flag.ENTITY_INTERACTION) || sendLockon) {
            Entity entity = npc.getInteractingEntity();
            block.putShort(entity == null ? -1 : entity.getIndex() + (entity instanceof Player ? 32768 : 0));
        }
        if (flag.flagged(Flag.FORCED_CHAT) && npc.getForcedChat() != null) {
            block.putString(npc.getForcedChat());
        }
        if (flag.flagged(Flag.LUMINANCE)) {
            writeLuminanceOverlay(block, npc);
        }
        if (flag.flagged(Flag.TRANSFORM)) {
            block.putShort(npc.id(), ByteOrder.LITTLE);
        }
        if (flag.flagged(Flag.FACE_TILE) || sendFaceTile) {
            final Tile position = sendFaceTile ? npc.lastTileFaced : npc.getFaceTile();
            int x = position == null ? 0 : position.getX();
            int y = position == null ? 0 : position.getY();
            block.putShort(x * 2 + 1, ByteOrder.LITTLE);
            block.putShort(y * 2 + 1, ByteOrder.LITTLE);
        }
    }

    /**
     * Updates {@code npc}'s current animation and displays it for all local players.
     *
     * @param builder The packet builder to write information on.
     * @param npc     The npc to update animation for.
     * @return The NPCUpdating instance.
     */
    private static void updateAnimation(PacketBuilder builder, NPC npc) {
        builder.putShort(npc.getAnimation().getId(), ByteOrder.LITTLE);
        builder.put(npc.getAnimation().getDelay(), ValueType.C);
    }

    /**
     * Updates {@code npc}'s current graphics and displays it for all local players.
     *
     * @param builder The packet builder to write information on.
     * @param npc     The npc to update graphics for.
     * @return The NPCUpdating instance.
     */

    private static void updateGraphics(PacketBuilder builder, NPC npc) {
        builder.putShort(npc.graphic().id());
        builder.putInt((npc.graphic().getHeight().ordinal() * 50 << 16) | (npc.graphic().delay() & 0xFFFF));
    }

    /*
    private static final Map<Integer, Graphic> graphic = new HashMap<>();

    private static void updateGraphics(PacketBuilder builder, NPC npc) {
        graphic.put(npc.graphic().id(), npc.graphic());
        builder.put(graphic.size(), ValueType.A);
        for (var entry : graphic.entrySet()) {
            int key = entry.getKey();
            Graphic graphic = entry.getValue();
            builder.put(key, ValueType.S);
            builder.putShort(graphic.id(), ValueType.A, ByteOrder.LITTLE);
            builder.putInt((graphic.getHeight().ordinal() * 50 << 16) | (graphic.getDelay() & 0xFFFF), ByteOrder.LITTLE);
        }
    }*/


    private static void updateForcedMovement(NPC npc, PacketBuilder builder) {
        int startX = npc.getForceMovement().getStart().getLocalX(npc.getLastKnownRegion());
        int startY = npc.getForceMovement().getStart().getLocalY(npc.getLastKnownRegion());
        int endX = npc.getForceMovement().getEnd() == null ? 0 : npc.getForceMovement().getEnd().getX();
        int endY = npc.getForceMovement().getEnd() == null ? 0 : npc.getForceMovement().getEnd().getY();
        builder.put(startX, ValueType.S);
        builder.put(startY, ValueType.S);
        builder.put(startX + endX, ValueType.S);
        builder.put(startY + endY, ValueType.S);
        builder.putShort(npc.getForceMovement().getSpeed(), ValueType.A, ByteOrder.LITTLE);
        builder.putShort(npc.getForceMovement().getReverseSpeed(), ValueType.A);
        builder.putShort(npc.getForceMovement().getAnimation(), ValueType.A, ByteOrder.LITTLE);
        builder.put(npc.getForceMovement().getDirection(), ValueType.S);
    }

    private static void updateForcedMovement(Player player, NPC npc, PacketBuilder builder) {
        int startX = npc.getForceMovement().getStart().getLocalX(player.getLastKnownRegion());
        int startY = npc.getForceMovement().getStart().getLocalY(player.getLastKnownRegion());

        builder.put(startX, ValueType.S);
        builder.put(startY, ValueType.S);

        if (npc.getForceMovement().getEnd() == null) {
            builder.put(0, ValueType.S);
            builder.put(0, ValueType.S);
        } else if (npc.getForceMovement().getEnd().x < 64 || npc.getForceMovement().getEnd().y < 64) {
            // expect a delta like 1,1
            builder.put(startX + npc.getForceMovement().getEnd().x, ValueType.S);
            builder.put(startY + npc.getForceMovement().getEnd().y, ValueType.S);
        } else {
            // expect an absolute coordinate, like 3200 3200
            builder.put(npc.getForceMovement().getEnd().getLocalX(player.getLastKnownRegion()), ValueType.S);
            builder.put(npc.getForceMovement().getEnd().getLocalY(player.getLastKnownRegion()), ValueType.S);
        }
        builder.putShort(npc.getForceMovement().getSpeed(), ValueType.A, ByteOrder.LITTLE);
        builder.putShort(npc.getForceMovement().getReverseSpeed(), ValueType.A, ByteOrder.BIG);
        builder.put(npc.getForceMovement().getDirection(), ValueType.S);
    }

    private static void writeLuminanceOverlay(PacketBuilder builder, NPC npc) {
        builder.putShort(npc.tinting().delay());
        builder.putShort(npc.tinting().duration() + npc.tinting().delay());
        builder.put(npc.tinting().hue());
        builder.put(npc.tinting().saturation());
        builder.put(npc.tinting().luminance());
        builder.put(npc.tinting().opacity());
    }

    /**
     * Updates the npc's single hit.
     *
     * @param builder The packet builder to write information on.
     * @param npc     The npc to update the single hit for.
     * @return The NPCUpdating instance.
     */

    private static void updateSingleHit(PacketBuilder builder, NPC npc, Player player) {
        int count = Math.min(npc.nextHits.size(), 4); // count
        builder.put(count);
        int npcHp = npc.hp();
        int npcMaxHp = npc.getCombatInfo() == null ? 1 : npc.getCombatInfo().stats == null ? 1 : npc.getCombatInfo().stats.hitpoints;
        for (Hit hit : npc.nextHits.subList(0, count)) {
            builder.putShort(hit.getDamage());
            builder.put(hit.getMark(hit.getSource(), hit.getTarget(), player));
            builder.putShort(npcHp);
            builder.putShort(npcMaxHp);
        }
    }
}

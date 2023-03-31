package com.aelous.model.entity.masks.impl.updating;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.hit.Splat;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.masks.UpdateFlag;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.ByteOrder;
import com.aelous.network.packet.PacketBuilder;
import com.aelous.network.packet.PacketBuilder.AccessType;
import com.aelous.network.packet.PacketType;
import com.aelous.network.packet.ValueType;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a player's npc updating task, which loops through all local
 * npcs and updates their masks according to their current attributes.
 *
 * @author Relex lawl
 */

public class NPCUpdating {

    /**
     * The maximum number of npcs to load per cycle. This prevents the update packet from becoming too large (the
     * client uses a 5000 byte buffer) and also stops old spec PCs from crashing when they login or teleport.
     */
    private static final int NEW_NPCS_PER_CYCLE = 20;

    /**
     * Handles the actual npc updating for the associated player.
     *
     * @return The NPCUpdating instance.
     */
    public static synchronized void update(Player player) {
        PacketBuilder update = new PacketBuilder();
        PacketBuilder packet = new PacketBuilder(65, PacketType.VARIABLE_SHORT);
        packet.initializeAccess(AccessType.BIT);
        synchronized (player.getLocalNpcs()) {
            List<NPC> localNpcs = player.getLocalNpcs();
            Tile playerTile = player.tile();
            packet.putBits(8, localNpcs.size());
            Iterator<NPC> npcIterator = localNpcs.iterator();
            while (npcIterator.hasNext()) {
                NPC npc = npcIterator.next();
                if (npc.getIndex() != -1 && World.getWorld().getNpcs().contains(npc) && !npc.hidden() && playerTile.isWithinDistance(npc.tile()) && !npc.isNeedsPlacement()) {
                    updateMovement(npc, packet);
                    synchronized (npc) {
                        npc.inViewport(true); // Mark as in viewport
                        if (npc.getUpdateFlag().isUpdateRequired()) {
                            appendUpdates(npc, update, false);
                        }
                    }
                } else {
                    npcIterator.remove();
                    packet.putBits(1, 1);
                    packet.putBits(2, 3);
                }
            }
            int localNpcCount = localNpcs.size();
            int added = 0;
            for (NPC npc : World.getWorld().getNpcs()) {
                if (localNpcCount >= 255) // Originally 255
                    break;
                if (added >= NEW_NPCS_PER_CYCLE) {
                    break;
                }
                if (npc == null || localNpcs.contains(npc) || npc.hidden() || npc.isNeedsPlacement())
                    continue;
                if (npc.tile().isWithinDistance(playerTile)) {
                    added++;
                    localNpcs.add(npc);
                    addNPC(player, npc, packet);
                    synchronized (npc) {
                        npc.inViewport(true); // Mark as in viewport
                        if (npc.getUpdateFlag().isUpdateRequired() || sendNewNpcUpdates(npc)) {
                            appendUpdates(npc, update, true);
                        }
                    }
                    localNpcCount++;
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
        synchronized (player.getSession()) {
            player.getSession().write(packet);
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
    private static void addNPC(Player player, NPC npc, PacketBuilder builder) {
        int yOffset = npc.tile().getY() - player.tile().getY();
        int xOffset = npc.tile().getX() - player.tile().getX();
        builder.putBits(14, npc.getIndex());
        builder.putBits(5, yOffset);
        builder.putBits(5, xOffset);
        builder.putBits(1, 0);
        builder.putBits(14, npc.id());
        builder.putBits(1, npc.getUpdateFlag().isUpdateRequired() || sendNewNpcUpdates(npc) ? 1 : 0);

        //Facing update. We don't want to update facing for npcs that walk.
        boolean updateFacing = npc.walkRadius() == 0;
        builder.putBits(1, updateFacing ? 1 : 0);
        if (updateFacing) {
            // lastTileFaced already has *2+1 applied
            Tile tile = new Tile(face(npc).x * 2 + 1, face(npc).y * 2 + 1);
            builder.putBits(14, tile.getX()); //face x
            builder.putBits(14, tile.getY()); //face y
        }
    }

    public static Tile face(NPC npc) {
        Tile dir = npc.tile();
        switch (npc.spawnDirection()) {
            case 1:
                dir = npc.tile().transform(0, 10);
                break; // n
            case 6:
                dir = npc.tile().transform(0, -10);
                break; // s
            case 4:
                dir = npc.tile().transform(10, 0);
                break; // e
            case 3:
                dir = npc.tile().transform(-10, 0);
                break; // w
            case 0:
                dir = npc.tile().transform(-10, 10);
                break; // se
            case 2:
                dir = npc.tile().transform(10, 10);
                break; // ne
            case 5:
                dir = npc.tile().transform(-10, -10);
                break; // sw
            case 7:
                dir = npc.tile().transform(10, -10);
                break; // nw
        }
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
    private static void appendUpdates(NPC npc, PacketBuilder block, boolean newNpc) {
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
            updateSingleHit(block, npc);
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
            block.putShort(npc.transmog() <= 0 ? npc.id() : npc.transmog(), ValueType.A, ByteOrder.LITTLE);
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
        builder.putInt(((npc.graphic().getHeight().ordinal() * 50) << 16)
            + (npc.graphic().delay() & 0xffff));
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
    private static void updateSingleHit(PacketBuilder builder, NPC npc) {
        builder.put(Math.min(npc.splats.size(), 4)); // count
        for (int i = 0; i < Math.min(npc.splats.size(), 4); i++) {
            Splat splat = npc.splats.get(i);
            builder.putShort(splat.getDamage());
            builder.put(splat.getType().getId());
            builder.putShort(npc.hp());
            builder.putShort(npc.getCombatInfo() == null ? 1 : npc.getCombatInfo().stats == null ? 1 : npc.getCombatInfo().stats.hitpoints);
        }
    }
}

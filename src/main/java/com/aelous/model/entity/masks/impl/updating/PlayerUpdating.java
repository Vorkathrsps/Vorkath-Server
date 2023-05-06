package com.aelous.model.entity.masks.impl.updating;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.hit.Splat;
import com.aelous.model.entity.masks.FaceDirection;
import com.aelous.model.entity.masks.impl.chat.ChatMessage;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.masks.UpdateFlag;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.ByteOrder;
import com.aelous.network.packet.PacketBuilder;
import com.aelous.network.packet.PacketBuilder.AccessType;
import com.aelous.network.packet.PacketType;
import com.aelous.network.packet.ValueType;
import com.aelous.utility.chainedwork.Chain;

import java.util.Iterator;

/**
 * Represents the associated player's player updating.
 *
 * @author relex lawl
 */

public class PlayerUpdating {

    /**
     * The max amount of players that can be added per cycle.
     */
    private static final int MAX_NEW_PLAYERS_PER_CYCLE = 25;

    /**
     * Loops through the associated player's {@code localPlayer} list and updates them.
     *
     * @return The PlayerUpdating instance.
     */

    public static synchronized void update(final Player player) {
        PacketBuilder update = new PacketBuilder();
        PacketBuilder packet = new PacketBuilder(81, PacketType.VARIABLE_SHORT);
        packet.initializeAccess(AccessType.BIT);
        updateMovement(player, packet);
        synchronized (player.getLocalPlayers()) {
            appendUpdates(player, update, player, false, true);
            packet.putBits(8, player.getLocalPlayers().size());
            for (Iterator<Player> playerIterator = player.getLocalPlayers().iterator(); playerIterator.hasNext(); ) {
                Player otherPlayer = playerIterator.next();
                if (otherPlayer.getIndex() != -1 && World.getWorld().getPlayers().get(otherPlayer.getIndex()) != null && !otherPlayer.looks().hidden() && otherPlayer.tile().isWithinDistance(player.tile()) && !otherPlayer.isNeedsPlacement() && canSee(player, otherPlayer)) {
                    updateOtherPlayerMovement(packet, otherPlayer);
                    if (otherPlayer.getUpdateFlag().isUpdateRequired()) {
                        appendUpdates(player, update, otherPlayer, false, false);
                    }
                } else {
                    playerIterator.remove();
                    packet.putBits(1, 1);
                    packet.putBits(2, 3);
                }
            }
            int playersAdded = 0;
            for (Player otherPlayer : World.getWorld().getPlayers()) {
                if (player.getLocalPlayers().size() >= 79 || playersAdded > MAX_NEW_PLAYERS_PER_CYCLE)
                    break;
                if (otherPlayer == null || otherPlayer == player || player.getLocalPlayers().contains(otherPlayer) || !otherPlayer.tile().isWithinDistance(player.tile()) || otherPlayer.looks().hidden() || !canSee(player, otherPlayer)) {
                    continue;
                }
                player.getLocalPlayers().add(otherPlayer);
                addPlayer(player, otherPlayer, packet);
                appendUpdates(player, update, otherPlayer, true, false);
                playersAdded++;
            }
        }
        if (update.buffer().writerIndex() > 0) {
            packet.putBits(11, 2047);
            packet.initializeAccess(AccessType.BYTE);
            packet.puts(update.buffer());
        } else {
            packet.initializeAccess(AccessType.BYTE);
        }
        synchronized (player.getSession()) {
            player.getSession().write(packet);
        }
    }



    private static boolean canSee(Player player, Player otherPlayer) {
        return true;
    }

    /**
     * Adds a new player to the associated player's client.
     *
     * @param target  The player to add to the other player's client.
     * @param builder The packet builder to write information on.
     * @return The PlayerUpdating instance.
     */
    private static void addPlayer(Player player, Player target, PacketBuilder builder) {
        builder.putBits(11, target.getIndex());
        builder.putBits(1, 1);
        builder.putBits(1, 1);
        int yDiff = target.tile().getY() - player.tile().getY();
        int xDiff = target.tile().getX() - player.tile().getX();
        builder.putBits(5, yDiff); // localized position relative to POV
        builder.putBits(5, xDiff);
    }

    /**
     * Updates the associated player's movement queue.
     *
     * @param builder The packet builder to write information on.
     * @return The PlayerUpdating instance.
     */
    private static void updateMovement(Player player, PacketBuilder builder) {
        /*
         * Check if the player is teleporting.
         */
        if (player.isNeedsPlacement()) {
            //player.forceChat("needs placement tp!");
            /*
             * They are, so an update is required.
             */
            builder.putBits(1, 1);

            /*
             * This value indicates the player teleported.
             */
            builder.putBits(2, 3);

            /*
             * This is the new player height.
             */
            builder.putBits(2, player.tile().getLevel());

            /*
             * This indicates that the client should discard the walking queue.
             */
            builder.putBits(1, player.isResetMovementQueue() ? 1 : 0);

            /*
             * This flag indicates if an update block is appended.
             */
            builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);

            /*
             * These are the positions.
             */
            builder.putBits(7,
                player.tile().getLocalY(player.getLastKnownRegion()));
            builder.putBits(7,
                player.tile().getLocalX(player.getLastKnownRegion()));
        } else
            /*
             * Otherwise, check if the player moved.
             */
            if (player.getWalkingDirection().toInteger() == -1) {
                /*
                 * The player didn't move. Check if an update is required.
                 */
                if (player.getUpdateFlag().isUpdateRequired()) {
                    /*
                     * Signifies an update is required.
                     */
                    builder.putBits(1, 1);

                    /*
                     * But signifies that we didn't move.
                     */
                    builder.putBits(2, 0);
                } else
                    /*
                     * Signifies that nothing changed.
                     */
                    builder.putBits(1, 0);
            } else /*
             * Check if the player was running.
             */
                if (player.getRunningDirection().toInteger() == -1) {
                    /*
                     * The player walked, an update is required.
                     */
                    builder.putBits(1, 1);

                    /*
                     * This indicates the player only walked.
                     */
                    builder.putBits(2, 1);

                    /*
                     * This is the player's walking direction.
                     */

                    builder.putBits(3, player.getWalkingDirection().toInteger());

                    /*
                     * This flag indicates an update block is appended.
                     */
                    builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
                } else {

                    /*
                     * The player ran, so an update is required.
                     */
                    builder.putBits(1, 1);

                    /*
                     * This indicates the player ran.
                     */
                    builder.putBits(2, 2);

                    /*
                     * This is the walking direction.
                     */
                    builder.putBits(3, player.getWalkingDirection().toInteger());

                    /*
                     * And this is the running direction.
                     */
                    builder.putBits(3, player.getRunningDirection().toInteger());

                    /*
                     * And this flag indicates an update block is appended.
                     */
                    builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
                }
    }

    /**
     * Updates another player's movement queue.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update movement for.
     * @return The PlayerUpdating instance.
     */
    private static void updateOtherPlayerMovement(PacketBuilder builder, Player target) {

        /*
         * Check which type of movement took place.
         */
        if (target.getWalkingDirection().toInteger() == -1) {
            /*
             * If no movement did, check if an update is required.
             */
            if (target.getUpdateFlag().isUpdateRequired()) {
                /*
                 * Signify that an update happened.
                 */
                builder.putBits(1, 1);

                /*
                 * Signify that there was no movement.
                 */
                builder.putBits(2, 0);
            } else {
                /*
                 * Signify that nothing changed.
                 */
                builder.putBits(1, 0);
            }
        } else if (target.getRunningDirection().toInteger() == -1) {
            /*
             * The player moved but didn't run. Signify that an update is
             * required.
             */
            builder.putBits(1, 1);

            /*
             * Signify we moved one tile.
             */
            builder.putBits(2, 1);

            /*
             * Write the primary sprite (i.e. walk direction).
             */
            builder.putBits(3, target.getWalkingDirection().toInteger());

            /*
             * Write a flag indicating if a block update happened.
             */
            builder.putBits(1, target.getUpdateFlag().isUpdateRequired() ? 1 : 0);
        } else {
            /*
             * The player ran. Signify that an update happened.
             */
            builder.putBits(1, 1);

            /*
             * Signify that we moved two tiles.
             */
            builder.putBits(2, 2);

            /*
             * Write the primary sprite (i.e. walk direction).
             */
            builder.putBits(3, target.getWalkingDirection().toInteger());

            /*
             * Write the secondary sprite (i.e. run direction).
             */
            builder.putBits(3, target.getRunningDirection().toInteger());

            /*
             * Write a flag indicating if a block update happened.
             */
            builder.putBits(1, target.getUpdateFlag().isUpdateRequired() ? 1 : 0);
        }
    }

    /**
     * Appends a player's update mask blocks.
     *
     * @param builder          The packet builder to write information on.
     * @param target           The player to update masks for.
     * @param updateAppearance Update the player's appearance without the flag being set?
     * @param noChat           Do not allow player to chat?
     * @return The PlayerUpdating instance.
     */
    private static void appendUpdates(Player player, PacketBuilder builder, Player target, boolean updateAppearance, boolean noChat) {
        var sendFacing = updateAppearance && target != player && target.getInteractingEntity() == null && target.lastTileFaced != null;
        var sendLockon = updateAppearance && target != player && target.getInteractingEntity() != null;

        // no update?
        if (!target.getUpdateFlag().isUpdateRequired() && !updateAppearance && !sendFacing && !sendLockon)
            return;

       /* if (player.getCachedUpdateBlock() != null && !player.equals(target) && !updateAppearance && !noChat) {
            builder.puts(player.getCachedUpdateBlock());
            return;
        }*/

        final UpdateFlag flag = target.getUpdateFlag();
        int mask = 0;
        if (flag.flagged(Flag.FORCED_MOVEMENT) && target.getForceMovement() != null) {
            mask |= 0x400;
        }
        if (flag.flagged(Flag.GRAPHIC) && target.graphic() != null) {
            mask |= 0x100;
        }
        if (flag.flagged(Flag.ANIMATION) && target.getAnimation() != null) {
            mask |= 0x8;
        }
        if (flag.flagged(Flag.FORCED_CHAT) && target.getForcedChat() != null) {
            mask |= 0x4;
        }
        if (flag.flagged(Flag.CHAT) && target.getCurrentChatMessage() != null && !noChat) {
            mask |= 0x80;
        }
        if (flag.flagged(Flag.ENTITY_INTERACTION) || sendLockon) {
            mask |= 0x1;
        }
        if (flag.flagged(Flag.APPEARANCE) || updateAppearance) {
            mask |= 0x10;
        }
        if (flag.flagged(Flag.FACE_TILE) || sendFacing) {
            mask |= 0x2;
        }
        if (flag.flagged(Flag.FIRST_SPLAT)) {
            mask |= 0x20;
        }
        if (flag.flagged(Flag.LUMINANCE)) { //used for new updating mask
            mask |= 0x200;
        }
        if (mask >= 0x100) {
            mask |= 0x40;
            builder.putShort(mask, ByteOrder.LITTLE);
        } else {
            builder.put(mask);
        }
        if (flag.flagged(Flag.FORCED_MOVEMENT) && target.getForceMovement() != null) {
            updateForcedMovement(player, builder, target);
        }
        if (flag.flagged(Flag.GRAPHIC) && target.graphic() != null) {
            updateGraphics(builder, target);
        }
        if (flag.flagged(Flag.ANIMATION) && target.getAnimation() != null) {
            updateAnimation(builder, target);
        }
        if (flag.flagged(Flag.FORCED_CHAT) && target.getForcedChat() != null) {
            updateForcedChat(builder, target);
        }
        if (flag.flagged(Flag.CHAT) && target.getCurrentChatMessage() != null && !noChat) {
            updateChat(builder, target);
        }
        if (flag.flagged(Flag.ENTITY_INTERACTION) || sendLockon) {
            updateEntityInteraction(builder, target);
        }
        if (flag.flagged(Flag.APPEARANCE) || updateAppearance) {
            target.looks().update(builder, target);
        }
        if (flag.flagged(Flag.FACE_TILE) || sendFacing) {
            var tile = sendFacing ? target.lastTileFaced : target.getFaceTile();
            updateFacingPosition(builder, tile == null ? 0 : tile.getX(), tile == null ? 0 : tile.getY());
        }
        if (flag.flagged(Flag.FIRST_SPLAT)) {
            writehit1(builder, target);
        }
        if (flag.flagged(Flag.LUMINANCE)) {
            writeLuminanceOverlay(builder, target);
        }
        if (!player.equals(target) && !updateAppearance && !noChat) {
            player.setCachedUpdateBlock(builder.buffer());
        }
    }

    /**
     * This update block is used to update player chat.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update chat for.
     * @return The PlayerUpdating instance.
     */
    private static void updateChat(PacketBuilder builder, Player target) {
        ChatMessage message = target.getCurrentChatMessage();
        byte[] bytes = message.getText();
        builder.putShort(((message.getColour() & 0xff) << 8) | (message.getEffects() & 0xff), ByteOrder.LITTLE);
        builder.put(target.getPlayerRights().getRightsId());
        builder.put(target.getMemberRights().ordinal());
        builder.put(target.getIronManStatus().ordinal());
        builder.put(bytes.length, ValueType.C);
        for (int ptr = bytes.length - 1; ptr >= 0; ptr--) {
            builder.put(bytes[ptr]);
        }
    }

    /**
     * This update block is used to update forced player chat.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update forced chat for.
     * @return The PlayerUpdating instance.
     */
    private static void updateForcedChat(PacketBuilder builder, Player target) {
        builder.putString(target.getForcedChat());
    }

    /**
     * This update block is used to update forced player movement.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update forced movement for.
     * @return The PlayerUpdating instance.
     */
    private static void updateForcedMovement(Player player, PacketBuilder builder, Player target) {
        int startX = target.getForceMovement().getStart().getLocalX(player.getLastKnownRegion());
        int startY = target.getForceMovement().getStart().getLocalY(player.getLastKnownRegion());
        int endX = target.getForceMovement().getEnd() == null ? 0 : target.getForceMovement().getEnd().getX();
        int endY = target.getForceMovement().getEnd() == null ? 0 : target.getForceMovement().getEnd().getY();
        builder.put(startX, ValueType.S);
        builder.put(startY, ValueType.S);
        builder.put(startX + endX, ValueType.S);
        builder.put(startY + endY, ValueType.S);
        builder.putShort(target.getForceMovement().getSpeed(), ValueType.A, ByteOrder.LITTLE);
        builder.putShort(target.getForceMovement().getReverseSpeed(), ValueType.A, ByteOrder.BIG);
        builder.putShort(target.getForceMovement().getAnimation(), ValueType.A, ByteOrder.LITTLE);
        builder.put(target.getForceMovement().getDirection(), ValueType.S);
    }

    /**
     * This update block is used to update a player's animation.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update animations for.
     * @return The PlayerUpdating instance.
     */
    private static void updateAnimation(PacketBuilder builder, Player target) {
        builder.putShort(target.getAnimation().getId(), ByteOrder.LITTLE);
        builder.put(target.getAnimation().getDelay(), ValueType.C);
    }

    /**
     * This update block is used to update a player's graphics.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update graphics for.
     * @return The PlayerUpdating instance.
     */
    private static void updateGraphics(PacketBuilder builder, Player target) {
        builder.putShort(target.graphic().id(), ByteOrder.LITTLE);
        builder.putInt(((target.graphic().getHeight().ordinal() * 50) << 16) + (target.graphic().delay() & 0xffff));
    }

    /**
     * This update block is used to update a player's single hit.
     * @param builder    The packet builder used to write information on.
     * @param target    The player to update the single hit for.
     * @return The PlayerUpdating instance.
     */

    /**
     * // Tinted hitsplat
     * boolean tinted = true;
     * if (hit.getSource() != null && hit.getSource().equals(player)) {
     * tinted = false;
     * }
     * if (hit.getTarget() != null && hit.getTarget().equals(player)) {
     * tinted = false;
     * }
     * packet.put((tinted ? 1 : 0));
     */
    private static void writehit1(PacketBuilder builder, Player target) {
        builder.put(Math.min(target.splats.size(), 4)); // count
        for (int i = 0; i < Math.min(target.splats.size(), 4); i++) {
            Splat splat = target.splats.get(i);
            builder.putShort(splat.getDamage());
            builder.put(splat.getType().getId());
            builder.putShort(target.hp());
            builder.putShort(target.maxHp());
        }
    }

    /**
     * This update block is used to update a player's double hit.
     *
     * @param builder The packet builder used to write information on.
     * @param target  The player to update the double hit for.
     * @return The PlayerUpdating instance.
     */
    private static void writeLuminanceOverlay(PacketBuilder builder, Player target) {
        builder.putShort(target.tinting().delay());
        builder.putShort(target.tinting().duration() + target.tinting().delay());
        builder.put(target.tinting().hue());
        builder.put(target.tinting().saturation());
        builder.put(target.tinting().luminance());
        builder.put(target.tinting().opacity());
    }

    /**
     * This update block is used to update a player's face position.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update face position for.
     * @return The PlayerUpdating instance.
     */
    private static void updateFacingPosition(PacketBuilder builder, int x, int y) {
        builder.putShort(x, ValueType.A, ByteOrder.LITTLE);
        builder.putShort(y, ByteOrder.LITTLE);
    }

    /**
     * This update block is used to update a player's entity interaction.
     *
     * @param builder The packet builder to write information on.
     * @param target  The player to update entity interaction for.
     * @return The PlayerUpdating instance.
     */
    private static void updateEntityInteraction(PacketBuilder builder, Player target) {
        Entity entity = target.getInteractingEntity();
        if (entity != null) {
            int index = entity.getIndex();
            if (entity instanceof Player)
                index += -32768;
            builder.putShort(index, ByteOrder.LITTLE);
        } else {
            builder.putShort(-1, ByteOrder.LITTLE);
        }
    }
}

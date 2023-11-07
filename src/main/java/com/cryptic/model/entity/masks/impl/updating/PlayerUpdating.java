package com.cryptic.model.entity.masks.impl.updating;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.masks.UpdateFlag;
import com.cryptic.model.entity.masks.impl.chat.ChatMessage;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.region.Region;
import com.cryptic.network.packet.ByteOrder;
import com.cryptic.network.packet.PacketBuilder;
import com.cryptic.network.packet.PacketBuilder.AccessType;
import com.cryptic.network.packet.PacketType;
import com.cryptic.network.packet.ValueType;

import java.util.Iterator;
import java.util.List;

/**
 * Represents the associated player's player updating.
 *
 * @author relex lawl
 */

public class PlayerUpdating {

    /**
     * The maximum amount of local players.
     */
    private static final int MAXIMUM_LOCAL_PLAYERS = 255;

    /**
     * The maximum number of players to load per cycle. This prevents the update packet from becoming too large (the
     * client uses a 5000 byte buffer) and also stops old spec PCs from crashing when they login or teleport.
     */
    private static final int NEW_PLAYERS_PER_CYCLE = 20;

    /**
     * Loops through the associated player's {@code localPlayer} list and updates them.
     *
     * @return The PlayerUpdating instance.
     */

    public static void update(final Player player) {
        final PacketBuilder out = new PacketBuilder(81, PacketType.VARIABLE_SHORT);
        try (final PacketBuilder builder = new PacketBuilder()) {
            out.initializeAccess(AccessType.BIT);
            updateMovement(player, out);
            out.putBits(8, player.getLocalPlayers().size());
            appendUpdates(player, builder, player, false, true);
            Iterator<Player> playerIterator = player.getLocalPlayers().iterator();
            while (playerIterator.hasNext()) {
                Player otherPlayer = playerIterator.next();
                if (shouldUpdatePlayer(player, otherPlayer)) {
                    updateOtherPlayer(player, otherPlayer, out, builder);
                } else {
                    playerIterator.remove();
                    out.putBits(1, 1);
                    out.putBits(2, 3);
                }
            }

            List<Player> localPlayers = player.getLocalPlayers();
            int added = 0, count = localPlayers.size();
            for (Player otherPlayer : World.getWorld().getPlayers()) {
                if (count >= MAXIMUM_LOCAL_PLAYERS || added >= NEW_PLAYERS_PER_CYCLE) {
                    break;
                }

                if (shouldAddNewPlayer(player, otherPlayer)) {
                    localPlayers.add(otherPlayer);
                    count++;
                    added++;

                    addPlayer(player, otherPlayer, out);
                    appendUpdates(player, builder, otherPlayer, true, false);
                }
            }

            if (builder.buffer().writerIndex() > 0) {
                out.putBits(11, 2047);
                out.initializeAccess(AccessType.BYTE);
                out.puts(builder.buffer());
                builder.buffer().clear();
            } else {
                out.initializeAccess(AccessType.BYTE);
            }

            player.getSession().write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean shouldUpdatePlayer(Player player, Player otherPlayer) {
        return otherPlayer.getIndex() != -1
            && World.getWorld().getPlayers().get(otherPlayer.getIndex()) != null
            && !otherPlayer.looks().hidden()
            && otherPlayer.tile().isWithinDistance(player.tile())
            && !otherPlayer.isNeedsPlacement()
            && canSee(player, otherPlayer);
    }

    private static void updateOtherPlayer(Player player, Player otherPlayer, PacketBuilder out, PacketBuilder builder) {
        updateOtherPlayerMovement(out, otherPlayer);
        if (otherPlayer.getUpdateFlag().isUpdateRequired()) {
            appendUpdates(player, builder, otherPlayer, false, false);
        }
    }

    private static boolean shouldAddNewPlayer(Player player, Player otherPlayer) {
        return otherPlayer != null
            && otherPlayer != player
            && !player.getLocalPlayers().contains(otherPlayer)
            && otherPlayer.tile().isWithinDistance(player.tile())
            && !otherPlayer.looks().hidden()
            && canSee(player, otherPlayer);
    }


    private static boolean canSee(Player player, Player otherPlayer) {
        return true;
    }

    /**
     * Adds a new player to the associated player's client.
     *
     * @param otherPlayer The player to add to the other player's client.
     * @param builder     The packet builder to write information on.
     * @return The PlayerUpdating instance.
     */
    private static void addPlayer(Player player, Player otherPlayer, PacketBuilder builder) {
        builder.putBits(11, otherPlayer.getIndex());
        builder.putBits(1, 1);
        builder.putBits(1, 1);
        int yDiff = otherPlayer.tile().getY() - player.tile().getY();
        int xDiff = otherPlayer.tile().getX() - player.tile().getX();
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
            builder.putBits(7, player.tile().getLocalY(player.getLastKnownRegion()));
            builder.putBits(7, player.tile().getLocalX(player.getLastKnownRegion()));
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
     * @param player  The player to update movement for.
     * @return The PlayerUpdating instance.
     */
    private static void updateOtherPlayerMovement(PacketBuilder builder, Player player) {

        /*
         * Check which type of movement took place.
         */
        if (player.getWalkingDirection().toInteger() == -1) {
            /*
             * If no movement did, check if an update is required.
             */
            if (player.getUpdateFlag().isUpdateRequired()) {
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
        } else if (player.getRunningDirection().toInteger() == -1) {
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
            builder.putBits(3, player.getWalkingDirection().toInteger());

            /*
             * Write a flag indicating if a block update happened.
             */
            builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
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
            builder.putBits(3, player.getWalkingDirection().toInteger());

            /*
             * Write the secondary sprite (i.e. run direction).
             */
            builder.putBits(3, player.getRunningDirection().toInteger());

            /*
             * Write a flag indicating if a block update happened.
             */
            builder.putBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
        }
    }

    /**
     * Appends a player's update mask blocks.
     *
     * @param builder          The packet builder to write information on.
     * @param otherPlayer      The player to update masks for.
     * @param updateAppearance Update the player's appearance without the flag being set?
     * @param noChat           Do not allow player to chat?
     * @return The PlayerUpdating instance.
     */
    private static void appendUpdates(Player player, PacketBuilder builder, Player otherPlayer, boolean updateAppearance, boolean noChat) {
        var sendFacing = updateAppearance && otherPlayer != player && otherPlayer.getInteractingEntity() == null && otherPlayer.lastTileFaced != null;
        var sendLockon = updateAppearance && otherPlayer != player && otherPlayer.getInteractingEntity() != null;

        // no update?
        if (!otherPlayer.getUpdateFlag().isUpdateRequired() && !updateAppearance && !sendFacing && !sendLockon)
            return;

       /* if (player.getCachedUpdateBlock() != null && !player.equals(target) && !updateAppearance && !noChat) {
            builder.puts(player.getCachedUpdateBlock());
            return;
        }*/

        final UpdateFlag flag = otherPlayer.getUpdateFlag();
        int mask = 0;
        if (flag.flagged(Flag.FORCED_MOVEMENT) && otherPlayer.getForceMovement() != null) {
            mask |= 0x400;
        }
        if (flag.flagged(Flag.GRAPHIC) && otherPlayer.graphic() != null) {
            mask |= 0x100;
        }
        if (flag.flagged(Flag.ANIMATION) && otherPlayer.getAnimation() != null) {
            mask |= 0x8;
        }
        if (flag.flagged(Flag.FORCED_CHAT) && otherPlayer.getForcedChat() != null) {
            mask |= 0x4;
        }
        if (flag.flagged(Flag.CHAT) && otherPlayer.getCurrentChatMessage() != null && !noChat) {
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
        if (flag.flagged(Flag.FORCED_MOVEMENT) && otherPlayer.getForceMovement() != null) {
            updateForcedMovement(player, builder, otherPlayer);
        }
        if (flag.flagged(Flag.GRAPHIC) && otherPlayer.graphic() != null) {
            updateGraphics(builder, otherPlayer);
        }
        if (flag.flagged(Flag.ANIMATION) && otherPlayer.getAnimation() != null) {
            updateAnimation(builder, otherPlayer);
        }
        if (flag.flagged(Flag.FORCED_CHAT) && otherPlayer.getForcedChat() != null) {
            updateForcedChat(builder, otherPlayer);
        }
        if (flag.flagged(Flag.CHAT) && otherPlayer.getCurrentChatMessage() != null && !noChat) {
            updateChat(builder, otherPlayer);
        }
        if (flag.flagged(Flag.ENTITY_INTERACTION) || sendLockon) {
            updateEntityInteraction(builder, otherPlayer);
        }
        if (flag.flagged(Flag.APPEARANCE) || updateAppearance) {
            otherPlayer.looks().update(builder, otherPlayer);
        }
        if (flag.flagged(Flag.FACE_TILE) || sendFacing) {
            var tile = sendFacing ? otherPlayer.lastTileFaced : otherPlayer.getFaceTile();
            updateFacingPosition(builder, tile == null ? 0 : tile.getX(), tile == null ? 0 : tile.getY());
        }
        if (flag.flagged(Flag.FIRST_SPLAT)) {
            writehit1(builder, otherPlayer, otherPlayer, player);
        }
        if (flag.flagged(Flag.LUMINANCE)) {
            writeLuminanceOverlay(builder, otherPlayer);
        }
        if (!player.equals(otherPlayer) && !updateAppearance && !noChat) {
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
        builder.putInt(((target.graphic().getHeight().ordinal() * 50) << 16) | (target.graphic().delay() & 0xffff));
    }

    /**
     * This update block is used to update a player's single hit.
     *
     * @param builder The packet builder used to write information on.
     * @param target  The player to update the single hit for.
     * @return The PlayerUpdating instance.
     */
    private static void writehit1(PacketBuilder builder, Player player, Player otherPlayer, Player observer) {
        int count = Math.min(player.nextHits.size(), 4); // count
        builder.put(count);
        int playerHp = player.hp();
        int playerMaxHp = player.maxHp();
        for (Hit hit : player.nextHits.subList(0, count)) {
            builder.putShort(hit.getDamage());
            builder.put(hit.getMark(hit.getSource(), otherPlayer, observer));
            builder.putShort(playerHp);
            builder.putShort(playerMaxHp);
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

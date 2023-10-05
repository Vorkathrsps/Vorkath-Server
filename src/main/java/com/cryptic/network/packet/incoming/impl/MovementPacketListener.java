package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.annotate.Init;
import com.cryptic.core.task.Task;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.content.duel.DuelRule;
import com.cryptic.model.content.skill.impl.mining.Pickaxe;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.IncomingHandler;
import com.cryptic.utility.Debugs;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.timers.TimerKey;
import io.netty.buffer.Unpooled;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This packet listener is called when a player has clicked on either the
 * mini-map or the actual game map to move around.
 *
 * @author Gabriel Hannason
 */
public class MovementPacketListener implements PacketListener {

    public static void main(String[] args) {
        final Packet packet = new Packet(-1, Unpooled.copiedBuffer(new byte[]{(byte) 0, (byte) 28, (byte) 5, (byte) -1, (byte) -1, (byte) -128, (byte) 14, (byte) 0}));
        int size = packet.getSize();
        boolean shiftTeleport = packet.readByte() == 1; //We already send shift teleport as a command, but lets read the byte anyway.
        int path1 = packet.readByte();
        int path2 = packet.readByte();
        System.out.println("was " + Arrays.toString(packet.getBuffer().array()) + " -> size " + size + " shift tp " + shiftTeleport + " path1 " + path1 + " path2 " + path2);
    }

    @Override
    public void handleMessage(Player player, Packet packet) {
        Entity freezer = player.getAttribOr(AttributeKey.FROZEN_BY, null);
        int size = packet.getSize();
        player.afkTimer.reset();
        var minimapClick = packet.getOpcode() == 248;

        if (player.locked() || player.dead()) {
            return;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        boolean newAccount = player.getAttribOr(AttributeKey.NEW_ACCOUNT, false);

        if (newAccount) {
            player.message("You have to select your game mode before you can continue.");
            return;
        }

        if (freezer != null) {
            if (player.frozen() && player.tile().distance(freezer.tile()) >= 10 || !freezer.isRegistered()) {
                CombatFactory.unfreezeWhenOutOfRange(player);
            }

            if (player.frozen() && freezer.isPlayer()
                && (!WildernessArea.inWilderness(player.tile()) && WildernessArea.inWilderness(freezer.tile()))
                || (WildernessArea.inWilderness(player.tile()) && !WildernessArea.inWilderness(freezer.tile()))
            ) {
                CombatFactory.unfreezeWhenOutOfRange(player);
            }
        }
        if (player.getZ() > 3 && player.getInstancedArea() == null) {
            player.debug("You're at z %s but not in an InstanceArea.. sus mate", player.getZ());
        }

        if (!checkReqs(player, packet.getOpcode())) {
            return;
        }

        // Stop our distanced action task because we reset the walking queue by walking
        player.stopDistancedTask();

        /* Clear non walkable actions */
        player.stopActions(false);

        if (packet.getOpcode() == IncomingHandler.MINIMAP_MOVEMENT_OPCODE || packet.getOpcode() == IncomingHandler.GAME_MOVEMENT_OPCODE) {
            player.getCombat().reset();//Reset combat when moving
            player.getCombat().setCastSpell(null);
        }

        // Close dialogues
        player.getInterfaceManager().closeDialogue();

        player.getRunePouch().close();

        if (player.loopTask != null && player.loopTask.isRunning()) {
            player.loopTask.stop();
        }

        //Haha some friends thought they were smart, wield ammy of avarice type ::unskull and go in wild.
        if (WildernessArea.isInWilderness(player)) {
            if (player.getEquipment().contains(ItemIdentifiers.AMULET_OF_AVARICE)) {
                Skulling.assignSkullState(player, SkullType.WHITE_SKULL);
            }
        }

        if (!WildernessArea.isInWilderness(player)) {

            if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                player.getTimers().cancel(TimerKey.SPECIAL_TELEBLOCK);
                player.getTimers().cancel(TimerKey.TELEBLOCK);
                player.getTimers().cancel(TimerKey.TELEBLOCK_IMMUNITY);
                player.message("The teleport block fades as you leave the wilderness...");
                player.getTimers().cancel(TimerKey.BLOCK_SPEC_AND_TELE);
                player.getPacketSender().sendEffectTimer(0, EffectTimer.TELEBLOCK);
            }

            int wildernessStreak = player.getAttribOr(AttributeKey.WILDERNESS_KILLSTREAK, 0);
            if (wildernessStreak >= 1) {
                player.message("[<col=ca0d0d>Streak</col>] Your wilderness streak ends at <col=ca0d0d>" + wildernessStreak + "</col> as you exit the wilderness.");
                player.clearAttrib(AttributeKey.WILDERNESS_KILLSTREAK);
            }
        }

        int steps = (size - 5) / 2;
        if (steps < 0) {
            return;
        }
        var plane = packet.readByte();
        final int firstStepX = packet.readLEShortA();
        final int[][] path = new int[steps][2];
        for (int i = 0; i < steps; i++) {
            path[i][0] = packet.readByte();
            path[i][1] = packet.readByte();
        }
        final int firstStepY = packet.readLEShort();
        var shiftTeleport = packet.readByteC();

        final Tile[] tiles = new Tile[steps + 1];
        tiles[0] = new Tile(firstStepX, firstStepY, player.tile().getLevel());
        for (int i = 0; i < steps; i++) {
            tiles[i + 1] = new Tile(path[i][0] + firstStepX, path[i][1] + firstStepY, player.tile().getLevel());
        }

        Tile end = tiles[tiles.length - 1];

        if (shiftTeleport == 1 && minimapClick && tiles.length > 0) {
            player.teleport(tiles[tiles.length-1]);
            return;
        }

        if (Debugs.MOB_STEPS.enabled) {
            ArrayList<GroundItem> markers = new ArrayList<>(tiles.length);
            for (Tile step : tiles) {
                GroundItem marker = new GroundItem(new Item(ItemIdentifiers.BRONZE_ARROW, 1), new Tile(step.getX(), step.getY()), null);
                GroundItemHandler.createGroundItem(marker);
                markers.add(marker);
            }
            Task.runOnceTask(10, c -> {
                markers.forEach(GroundItemHandler::sendRemoveGroundItem);
            });
        }

        if (player.tile().distance(end) >= 64) {
            return;
        }

        player.smartPathTo(new Tile(end.x, end.y, player.getZ()));

        player.putAttrib(AttributeKey.MOVEMENT_PACKET_STEPS, new ArrayDeque<>(Arrays.asList(tiles)));

        if (player.recentAnim != null && ANIMS_TO_RESET.stream().anyMatch(e -> e == player.recentAnim.getId()))
            player.animate(Animation.DEFAULT_RESET_ANIMATION);

        if (player.recentAnim != null && player.recentAnim.getId() == 618) {
            player.animate(Animation.DEFAULT_RESET_ANIMATION);
        }

    }

    static List<Integer> ANIMS_TO_RESET = new ArrayList<>();

    @Init
    public static void init() {
        ANIMS_TO_RESET.addAll(Arrays.stream(Pickaxe.values()).map(e -> e.anim).toList());
    }

    private boolean checkReqs(Player player, int opcode) {
        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            player.debugMessage("MovementPacket checkReqs need bank pin.");
            return false;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return false;
        }

        /*
         * JAK/ SHADOWRS:
         * DO NOT put frozen/stunned checks here. that should go in the movement handler code. you want to allow the REQUEST
         * to walk to always come through, then decide if you can/cant later.
         */

        if (player.getTrading().getButtonDelay().active() || player.getDueling().getButtonDelay().active()) {
            player.message("You cannot do that right now.");
            return false;
        }

        // Duel, disabled movement?
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MOVEMENT.ordinal()]) {
            if (opcode != IncomingHandler.COMMAND_MOVEMENT_OPCODE) {
                DialogueManager.sendStatement(player, "Movement has been disabled in this duel!");
            }
            return false;
        }

        if (player.isNeedsPlacement() || player.getMovementQueue().isMovementBlocked() || player.hasAttrib(AttributeKey.NO_MOVEMENT_NIGHTMARE)) {
            player.debugMessage("cant walk: " + (player.isNeedsPlacement() ? "tping" : "movelocked"));
            return false;
        }

        return true;
    }
}

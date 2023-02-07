package com.aelous.network.packet.incoming.impl;

import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

/**
 * @author Ynneh | 01/04/2022 - 09:44
 * <https://github.com/drhenny>
 */
public class KeyPressedListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) throws Exception {

        int key = packet.readInt();

        long lastStroke = packet.readLong();

        /**
         * snare = 220
         * cannot move = 221
         * high alch = 223
         * low alch = 224
         * bury bones = 232
         * open double gate? = 283
         * eat = 317
         * open gate = 319 359? 618?
         * potion = 334
         * 337 = pray?
         * 342 = bees
         * 344 = whistle
         * 356 = pick up / 358?
         * 361 367 370 = rapid range att?
         * 362 = accurate?
         * 364 = knifes?
         * 365 = darts?
         * 366 long?
         * 367 = drop?
         * 385 = dds sec
         * 386 = msb spec
         * 390 = dlong
         * 391 some kind of ranged special
         * 394 = staff attack?
         * 396 = slash something?
         * 401 hit something
         * 403 = dds stab
         * 405-415 = range attack something?
         * 430 = genie lamp
         * 433-452 = prayer something?
         * 458 = stunned
         * 504 = jump over something 799
         * 666 = onyx bolt spec?
         * 719 = chinchompa
         * 793 = stab?
         *
         * stopped at 800
         *
         */

        if (player.soundmode) {
            if (key == 112) {
                player.sendSound(player.lastSoundId, 1);
                player.getPacketSender().sendMessage("Playing SoundId: <col=ff0000>" + player.lastSoundId);
                player.lastSoundId++;
                return;
            }
            if (key == 113) {
                if (player.lastSoundId == 0) {
                    player.getPacketSender().sendMessage("Cannot go back! at first id: 0");
                    return;
                }
                player.sendSound(player.lastSoundId, 1);
                player.getPacketSender().sendMessage("Playing SoundId: <col=0000ff>" + player.lastSoundId);
                player.lastSoundId--;
                return;
            }
            return;
        }

        //System.err.println("Key="+key+" lastStroke="+lastStroke);

    }
}

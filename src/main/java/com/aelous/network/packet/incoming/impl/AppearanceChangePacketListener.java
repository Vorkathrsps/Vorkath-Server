package com.aelous.network.packet.incoming.impl;

import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppearanceChangePacketListener implements PacketListener {

    private static final Logger logger = LogManager.getLogger(AppearanceChangePacketListener.class);

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player.dead()) {
            return;
        }
        
        player.afkTimer.reset();

        try {

            final boolean gender = packet.readByte() == 1;
            final int head = packet.readByte();
            final int jaw = packet.readByte();
            final int torso = packet.readByte();
            final int arms = packet.readByte();
            final int hands = packet.readByte();
            final int legs = packet.readByte();
            final int feet = packet.readByte();
            final int hairColor = packet.readByte();
            final int torsoColor = packet.readByte();
            final int legsColor = packet.readByte();
            final int feetColor = packet.readByte();
            final int skinColor = packet.readByte();

            if (skinColor == 10 && !player.getMemberRights().isRegularMemberOrGreater(player)) {
                player.message("You need to be a Ruby member to use this skin!");
                return;
            }

            if (skinColor == 11 && !player.getMemberRights().isSuperMemberOrGreater(player)) {
                player.message("You need to be a Sapphire member to use this skin!");
                return;
            }

            if (skinColor == 12 && !player.getMemberRights().isEliteMemberOrGreater(player)) {
                player.message("You need to be a Emerald member to use this skin!");
                return;
            }

            if (skinColor == 13 && !player.getMemberRights().isExtremeMemberOrGreater(player)) {
                player.message("You need to be a Diamond member to use this skin!");
                return;
            }

            if (skinColor == 14 && !player.getMemberRights().isLegendaryMemberOrGreater(player)) {
                player.message("You need to be a Dragontone member to use this skin!");
                return;
            }

            if (skinColor == 15 && !player.getMemberRights().isVIPOrGreater(player)) {
                player.message("You need to be a Onyx member to use this skin!");
                return;
            }

            if (skinColor == 16 && !player.getMemberRights().isSponsorOrGreater(player)) {
                player.message("You need to be a Zenyte member to use this skin!");
                return;
            }

            player.looks().female(gender);
            player.looks().looks(new int[] {head, jaw, torso, arms, hands, legs, feet});
            player.looks().colors(new int[] {hairColor, torsoColor, legsColor, feetColor, skinColor});
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.stopActions(true);
            player.getInterfaceManager().close();
        } catch(Exception e) {
            logger.catching(e);
        }
    }

}

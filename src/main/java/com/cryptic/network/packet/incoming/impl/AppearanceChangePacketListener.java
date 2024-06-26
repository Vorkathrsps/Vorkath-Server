package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.Appearance;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
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

        var newAccount = player.<Boolean>getAttribOr(AttributeKey.NEW_ACCOUNT, false);

        try {

            final boolean gender = packet.readUnsignedByte() == 1;
            final int head = packet.readUnsignedByte();
            final int jaw = packet.readUnsignedByte();
            final int torso = packet.readUnsignedByte();
            final int arms = packet.readUnsignedByte();
            final int hands = packet.readUnsignedByte();
            final int legs = packet.readUnsignedByte();
            final int feet = packet.readUnsignedByte();
            final int hairColor = packet.readUnsignedByte();
            final int torsoColor = packet.readUnsignedByte();
            final int legsColor = packet.readUnsignedByte();
            final int feetColor = packet.readUnsignedByte();
            final int skinColor = packet.readUnsignedByte();

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
            player.looks().looks(new int[]{head, jaw, torso, arms, hands, legs, feet});
            player.looks().colors(new byte[]{(byte) hairColor, (byte) torsoColor, (byte) legsColor, (byte) feetColor, (byte) skinColor});
            player.stopActions(true);
            player.getInterfaceManager().close();
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        } catch (Exception e) {
            logger.error("sadge", e);
        }
    }

}

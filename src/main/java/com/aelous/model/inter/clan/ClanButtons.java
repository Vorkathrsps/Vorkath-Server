package com.aelous.model.inter.clan;

import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;

/**
 * Handles the buttons for clan chat.
 * @author PVE
 * @Since juli 07, 2020
 */
public class ClanButtons {

    /**
     * Handles clicking the buttons on interface.
     */
    public static boolean handle(Player player, int button) {

        switch (button) {

            /* Joining */
            case 33806 -> {
                if (player.getClan() == null) {
                    player.setNameScript("Which channel would you like to join?", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            ClanManager.join(player, (String) value);
                            return true;
                        }
                    });
                } else {
                    ClanManager.leave(player, false);
                }
                return true;
            }

            /* Clan setup */
            case 33809 -> {
                ClanManager.manage(player);
                return true;
            }

            /* Name Set */
            case 47252 -> {

                player.setNameScript("Enter a new name for your clan below.", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        ClanManager.changeName(player, (String) value);
                        return true;
                    }
                });
                return true;
            }

            /* Lootshare */
            case 47821 -> {
                player.getClan().setLootshare(!player.getClan().isLootshare());
                player.getPacketSender().sendConfig(267, player.getClan().isLootshare() ? 1 : 0);
                String onOrOff = player.getClan().isLootshare() ? "on" : "off";
                player.message("You have turned lootshare " + onOrOff + ".");
                return true;
            }

            /* Lock clan */
            case 47823 -> {
                player.getClan().setLocked(!player.getClan().isLocked());
                player.getPacketSender().sendConfig(268, player.getClan().isLocked() ? 1 : 0);
                String lockedOrUnlocked = player.getClan().isLocked() ? "locked" : "unlocked";
                player.message("You have " + lockedOrUnlocked + " the clan chat.");
                return true;
            }

            /* Who Can Enter */
            case 47255 -> {
                player.getClan().setJoinable(ClanRank.ANYONE);
                player.getPacketSender().sendString(47815, "Anyone");
                return true;
            }
            case 48007 -> {
                player.getClan().setJoinable(ClanRank.FRIEND);
                player.getPacketSender().sendString(47815, "Any friends");
                return true;
            }
            case 48006 -> {
                player.getClan().setJoinable(ClanRank.RECRUIT);
                player.getPacketSender().sendString(47815, "Recruit+");
                return true;
            }
            case 48005 -> {
                player.getClan().setJoinable(ClanRank.CORPORAL);
                player.getPacketSender().sendString(47815, "Corporal+");
                return true;
            }
            case 48004 -> {
                player.getClan().setJoinable(ClanRank.SERGEANT);
                player.getPacketSender().sendString(47815, "Sergeant+");
                return true;
            }
            case 48003 -> {
                player.getClan().setJoinable(ClanRank.LIEUTENANT);
                player.getPacketSender().sendString(47815, "Lieutenant+");
                return true;
            }
            case 48002 -> {
                player.getClan().setJoinable(ClanRank.CAPTAIN);
                player.getPacketSender().sendString(47815, "Captain+");
                return true;
            }
            case 48001 -> {
                player.getClan().setJoinable(ClanRank.GENERAL);
                player.getPacketSender().sendString(47815, "General+");
                return true;
            }
            case 48000 -> {
                player.getClan().setJoinable(ClanRank.LEADER);
                player.getPacketSender().sendString(47815, "Only me");
                return true;
            }

            /* Who Can Talk */
            case 47258 -> {
                player.getClan().setTalkable(ClanRank.ANYONE);
                player.getPacketSender().sendString(47816, "Anyone");
                return true;
            }
            case 48017 -> {
                player.getClan().setTalkable(ClanRank.FRIEND);
                player.getPacketSender().sendString(47816, "Any friends");
                return true;
            }
            case 48016 -> {
                player.getClan().setTalkable(ClanRank.RECRUIT);
                player.getPacketSender().sendString(47816, "Recruit+");
                return true;
            }
            case 48015 -> {
                player.getClan().setTalkable(ClanRank.CORPORAL);
                player.getPacketSender().sendString(47816, "Corporal+");
                return true;
            }
            case 48014 -> {
                player.getClan().setTalkable(ClanRank.SERGEANT);
                player.getPacketSender().sendString(47816, "Sergeant+");
                return true;
            }
            case 48013 -> {
                player.getClan().setTalkable(ClanRank.LIEUTENANT);
                player.getPacketSender().sendString(47816, "Lieutenant+");
                return true;
            }
            case 48012 -> {
                player.getClan().setTalkable(ClanRank.CAPTAIN);
                player.getPacketSender().sendString(47816, "Captain+");
                return true;
            }
            case 48011 -> {
                player.getClan().setTalkable(ClanRank.GENERAL);
                player.getPacketSender().sendString(47816, "General+");
                return true;
            }
            case 48010 -> {
                player.getClan().setTalkable(ClanRank.LEADER);
                player.getPacketSender().sendString(47816, "Only me");
                return true;
            }

            /* Who Can Manage */
            case 47261 -> {
                player.getClan().setManagable(ClanRank.LEADER);
                player.getPacketSender().sendString(47817, "Only me");
                return true;
            }
            case 48026 -> {
                player.getClan().setManagable(ClanRank.RECRUIT);
                player.getPacketSender().sendString(47817, "Recruit+");
                return true;
            }
            case 48025 -> {
                player.getClan().setManagable(ClanRank.CORPORAL);
                player.getPacketSender().sendString(47817, "Corporal+");
                return true;
            }
            case 48024 -> {
                player.getClan().setManagable(ClanRank.SERGEANT);
                player.getPacketSender().sendString(47817, "Sergeant+");
                return true;
            }
            case 48023 -> {
                player.getClan().setManagable(ClanRank.LIEUTENANT);
                player.getPacketSender().sendString(47817, "Lieutenant+");
                return true;
            }
            case 48022 -> {
                player.getClan().setManagable(ClanRank.CAPTAIN);
                player.getPacketSender().sendString(47817, "Captain+");
                return true;
            }
            case 48021 -> {
                player.getClan().setManagable(ClanRank.GENERAL);
                player.getPacketSender().sendString(47817, "General+");
                return true;
            }

            /* Promote */
            case 47840 -> {
                ClanManager.promote(player, ClanRank.ANYONE);
                return true;
            }
            case 47839 -> {
                ClanManager.promote(player, ClanRank.RECRUIT);
                return true;
            }
            case 47838 -> {
                ClanManager.promote(player, ClanRank.CORPORAL);
                return true;
            }
            case 47837 -> {
                ClanManager.promote(player, ClanRank.SERGEANT);
                return true;
            }
            case 47836 -> {
                ClanManager.promote(player, ClanRank.LIEUTENANT);
                return true;
            }
            case 47835 -> {
                ClanManager.promote(player, ClanRank.CAPTAIN);
                return true;
            }
            case 47834 -> {
                ClanManager.promote(player, ClanRank.GENERAL);
                return true;
            }
        }

        return false;

    }
}

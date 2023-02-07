package com.aelous.model.inter.clan;

import com.aelous.GameConstants;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.rights.MemberRights;
import com.aelous.model.entity.player.rights.PlayerRights;
import com.aelous.utility.Utils;

import java.util.Arrays;

/**
 * Class handles the management of clans.
 *
 * @author PVE
 * @Since juli 07, 2020
 */
public class ClanManager {

    public static void getClan(Player player) {
        String name = player.getUsername().toLowerCase().trim();

        Clan clan = ClanRepository.get(name);

        if (clan != null) {
            ClanRepository.get(name);
            player.message("The clan channel you are trying to create already exists.");
            return;
        }

        clan = new Clan(name);

        clan.init();

        if (World.getWorld().getPlayerByName(name).isPresent()) {
            clan.add(new ClanMember(World.getWorld().getPlayerByName(name).get(), ClanRank.LEADER));
        }

        ClanRepository.add(clan);
        ClanRepository.save();

        player.message("You have successfully created your clan.");

    }

    public static void join(Player player, String name) {
        if (name == null || name.length() == 0 || player.getClan() != null) {
            return;
        }

        player.message("Attempting to join channel...");

        Clan clan = ClanRepository.get(name);

        if (clan == null) {
            player.message("This channel does not exist.");
            return;
        }

        ClanMember member = new ClanMember(player, ClanRank.ANYONE);

        clan.setRank(member);

        if (!clan.canJoin(member)) {
            player.message("You do not have sufficient permissions to join this channel!");
            return;
        }

        if (clan.isLocked() && !clan.canJoin(member)) {
            player.message("The clan chat is currently locked, you cannot join this channel!");
            return;
        }

        if (!clan.add(member)) {
            player.message("This channel is currently full!");
            return;
        }

        player.setClan(clan);
        player.setClanChat(clan.getOwner());

        player.getPacketSender().sendString(33802, "</col>Talking in: <col=FFFF64><shad=0>" + Utils.formatName(clan.getName()));
        player.getPacketSender().sendString(33803, "</col>Owner: <col=ffffff>" + Utils.formatName(clan.getOwner()));
        player.getPacketSender().sendString(33816, "</col>Slogan: <col=ffffff>" + Utils.capitalizeSentence(clan.getSlogan()));

        player.message("Now talking in clan channel " + Utils.formatName(clan.getName()) + ".");
        player.message("To talk, start each line of chat with the / symbol.");
        player.getPacketSender().sendString(33812, "Leave Chat");
        update(clan);
    }

    public void test(String title, Runnable runnable) {

    }

    private static void refreshFor(Player player) {
        player.getPacketSender().sendString(33802, "</col>Talking in: <col=ffffff>None");
        player.getPacketSender().sendString(33803, "</col>Owner: <col=ffffff>None");
        player.getPacketSender().sendString(33816, "</col>Slogan: <col=ffffff>None");
        player.getPacketSender().sendString(33815, "");

        player.getPacketSender().sendString(33812, "Join Chat");

        for (int i = 0; i < 100; i++) {
            player.getPacketSender().sendString(33821 + i, "");
        }
    }

    public static void leave(Player player, boolean save) {
        Clan clan = player.getClan();

        if (clan == null) {
            player.message("You are not currently in a clan chat channel.");
            return;
        }

        refreshFor(player);

        player.message("You have left the clan chat channel.");

        clan.remove(clan.get(player.getUsername()));
        if (save) {
            player.setSavedClan(player.getClan().getOwner() == null ? "" : player.getClan().getOwner());
        } else {
            player.setSavedClan("");
            player.setClanChat("");
        }

        player.setClan(null);
        update(clan);
    }

    public static void message(Player player, String message) {
        Clan clan = player.getClan();

        if (clan == null) {
            player.message("You are not currently in a clan chat channel.");
            return;
        }

        ClanMember member = clan.get(player.getUsername());
        if (player.muted()) {
            player.message("You are muted and cannot send messages.");
            return;
        }

        if (!clan.canTalk(member)) {
            player.message("You do not have sufficient permissions to talk in this channel!");
            return;
        }

        String bracketColor = "<col=16777215>";
        String clanNameColor = "<col=255>";
        String nameColor = "<col=0>";
        String chatColor = "<col=993D00>";

        String clanPrefix = "" + bracketColor + "[" + clanNameColor + clan.getName() + bracketColor + "]";

        String rightsPrefix = "";
        if (player.getPlayerRights() != PlayerRights.PLAYER) {
            rightsPrefix = "<img=" + player.getPlayerRights().getSpriteId() + ">";
        }
        String memberPrefix = "";
        if (player.getMemberRights() != MemberRights.NONE) {
            memberPrefix = "<img=" + player.getMemberRights().getSpriteId() + ">";
        }

        for (ClanMember other : clan.members()) {
            other.getPlayer().getPacketSender().sendClanMessage(player.getUsername(), 16, (clanPrefix + nameColor + rightsPrefix + " " + memberPrefix + Utils.capitalizeFirst(player.getUsername()) + ": " + chatColor + Utils.capitalizeSentence(Utils.filterMessage(message))));
        }
    }

    public static void systemMessage(Clan clan, String message) {
        for (ClanMember member : clan.members()) {
            member.getPlayer().getPacketSender().sendMessage(Utils.capitalizeSentence(message));
        }
    }

    private static void update(Clan clan) {
        for (ClanMember member : clan.members()) {
            Player player = member.getPlayer();

            player.getPacketSender().sendString(33802, "</col>Talking in: <col=FFFF64><shad=0>" + Utils.formatName(clan.getName()));
            player.getPacketSender().sendString(33815, clan.members().size() + "/" + clan.getMemberLimit());

            int index = 0;
            for (int i = 0; i < 100 - index; i++) {
                player.getPacketSender().sendString(33821 + i, "");
            }

            for (ClanMember other : clan.members()) {
                String name = Utils.formatName(other.getName());

                if (other.getRank() != ClanRank.ANYONE) {
                    String rank = "<img=" + other.getRank().getRankIndex() + ">";
                    player.getPacketSender().sendString(33821 + index++, rank.concat(name));
                } else {
                    player.getPacketSender().sendString(33821 + index++, name);
                }
            }
        }
    }

    public static void changeName(Player player, String input) {
        if (Arrays.stream(GameConstants.BAD_STRINGS).anyMatch(input::contains)) {
            player.message("That name is not permitted!");
            return;
        }

        Clan clan = player.getClan();

        if (clan == null) {
            return;
        }

        clan.setName(input);

        systemMessage(clan, Utils.formatName(player.getUsername()) + " has changed the clan name.");

        for (ClanMember member : clan.members()) {
            member.getPlayer().getPacketSender().sendString(33802, "</col>Talking in: <col=FFFF64><shad=0>" + Utils.formatName(clan.getName()));
        }

        player.getPacketSender().sendString(47814, Utils.formatName(clan.getName()));

        ClanRepository.save();
    }

    public static void changeSlogan(Player player, String input) {
        boolean allowed = true;



        for (String bad : GameConstants.BAD_STRINGS) {
            if (input.contains(bad)) {
                allowed = false;
                break;
            }
        }

        if (!allowed) {
            player.message("Your slogan consisted of some words that were inappropriate!");
            return;
        }

        Clan clan = player.getClan();

        if (clan == null) {
            return;
        }

        clan.setSlogan(Utils.capitalizeSentence(input));

        systemMessage(clan, Utils.formatName(player.getUsername()) + " has changed the clan slogan.");

        for (ClanMember member : clan.members()) {
            member.getPlayer().getPacketSender().sendString(33816, "</col>Slogan: <col=ffffff>" + Utils.capitalizeSentence(clan.getSlogan()));
        }

        ClanRepository.save();
    }

    public static void setMemberLimit(Player player, int amount) {
        if (amount > 100) {
            player.message("You can only have a maximum of 100 clan members.");
            return;
        }

        Clan clan = player.getClan();
        if (clan == null) {
            player.message("You are currently not in a clan.");
            return;
        }

        clan.setMemberLimit(amount);

        systemMessage(clan, Utils.formatName(player.getUsername()) + " has changed the clan member limit.");

        for (ClanMember member : clan.members()) {
            member.getPlayer().getPacketSender().sendString(33815, clan.members().size() + "/" + clan.getMemberLimit());
        }
    }

    public static void manage(Player player) {
        if (player.getClan() == null) {
            getClan(player);
            return;
        }

        String name = player.getUsername().toLowerCase().trim();

        Clan clan = ClanRepository.get(name);
        if (clan == null) {
            return;
        }

        ClanMember member = clan.get(player.getUsername());

        if (!clan.getOwner().equalsIgnoreCase(player.getUsername())) {
            if (!clan.canManage(member)) {
                player.message("You do not have sufficient permissions to manage this channel!");
                return;
            }
        }

        int index = 0;
        for (int i = 0; i < 100; i++) {
            player.getPacketSender().sendString(44001 + index, "");
            player.getPacketSender().sendString(44801 + index, "");
        }

        for (ClanMember other : clan.members()) {
            player.getPacketSender().sendString(44001 + index, "" + Utils.formatName(other.getName()));
            player.getPacketSender().sendString(44801 + index, "<img=" + other.getRank().getRankIndex() + ">" + other.getRank().getName());
            index++;
        }

        player.getPacketSender().sendString(47814, Utils.formatName(clan.getName()));
        player.getInterfaceManager().open(40172);
    }

    public static void kickMember(Player player, String name) {
        Clan clan = player.getClan();

        if (clan == null) {
            player.message("You are not in a clan channel.");
            return;
        }

        if (World.getWorld().getPlayerByName(name).isPresent()) {
            Player victim = World.getWorld().getPlayerByName(name).get();
            ClanMember kick = clan.get(victim.getUsername());
            if (kick == null) return;

            if (kick.getRank().owner()) {
                player.message("You can't kick the owner of this clan chat.");
                return;
            }

            ClanMember clanMember = clan.get(player.getUsername());
            if (clanMember == null) return;

            //Check if the clan member has kick rights
            if (!clan.canKick(clanMember)) {
                player.message("You do not have sufficient permission to kick in this channel!");
                return;
            }

            //Check if the clan member being kicked is actually in the clan
            if (!clan.clanContains(kick)) {
                player.message(victim.getUsername() + " is not in this channel.");
                return;
            }

            kick(victim);
            victim.message(Utils.formatName(player.getUsername()) + " has kicked you from the clan channel.");
            player.message("You have successfully kicked " + Utils.formatName(name) + " from the clan channel.");
        }
    }

    private static void kick(Player player) {
        Clan clan = player.getClan();
        ClanMember clanMember = clan.get(player.getUsername());

        if (clanMember.getRank() == ClanRank.ADMIN) {
            player.getPacketSender().sendMessage("Error.. you cannot kick administrators from your clan chat channel.");
            return;
        }
        //If we had a rank, strip rank
        if (clanMember.getRank() != ClanRank.ANYONE) {
            clan.clearRank(clanMember);
        }

        //Remove from the clan, as we got kicked
        clan.remove(clanMember);

        //Reset clan attributes
        player.setSavedClan("");
        player.setClanChat("");
        player.setClan(null);

        //Refresh the interface
        refreshFor(player);

        //Update the clan
        update(clan);
    }

    public static void promote(Player player, ClanRank rank) {
        player.getPacketSender().sendString(47841, rank.getName());

        Clan clan = player.getClan();

        if (clan == null) {
            clan = ClanRepository.get(player.getUsername());

            if (clan == null) {
                player.message("You are not in a clan channel.");
                return;
            }
        }

        if (player.getClanPromote() == null) {
            player.message("You have not chosen a member to promote!");
            return;
        }

        ClanMember other = clan.get(player.getClanPromote());

        if (other == null) {
            player.message("This player is not in this channel.");
            return;
        }

        if (!clan.clanContains(other)) {
            player.message(Utils.formatName(other.getName()) + " is not in your clan channel.");
            return;
        }

        if (other.getRank().owner()) {
            player.message("You can't promote the owner of this clan chat.");
            return;
        }

        other.setRank(rank);
        clan.add(other);
        update(clan);
        manage(player);
        ClanRepository.save();
    }

}

package com.aelous.model.content.raids.party;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Origin
 * 4/20/2022 3:40PM
 */

public class RaidsParty {

    /**
     * variables
     */
    private Player leader;

    /**
     * ArrayList to store player index
     */
    private static final List<Player> partyList = new CopyOnWriteArrayList<>();

    /**
     * Raids Party Constructor
     *
     * @param leader
     */
    public RaidsParty(Player leader) {
        if (leader != null) {
            this.leader = leader;
            addMember(leader);
        }
    }

    /**
     * Getter for leader
     *
     * @return
     */
    public Player getLeader() {
        return this.leader;
    }

    /**
     * Getter for arrayList
     *
     * @return
     */
    public List<Player> getPartyMembers() {
        return partyList;
    }

    /**
     * Creating the team
     *
     * @param player
     */
    public void createTeam(Player player) {
        if (player != null) {
            player.chambersParty = new RaidsParty(player);
        }
    }

    /**
     * Clear Team
     */
    public void clearTeamList() {
        partyList.clear();
    }

    /**
     * Add Member
     *
     * @param player
     */
    public void addMember(Player player) {
        World.getWorld().getPlayers().nonNullStream().filter(f -> player != null).filter(f -> !partyList.contains(player)).filter(f -> !player.getRaids().raiding(player)).forEach(f -> addToList(player));
    }

    /**
     * Remove member
     *
     * @param player
     */
    public void removeMember(Player player) {
        partyList.stream().filter(f -> player != null).filter(f -> partyList.contains(player)).filter(f -> Objects.equals(player, f)).forEach(p -> removeFromList(player));
    }

    /**
     * remove from list
     *
     * @param player
     */
    public static void removeFromList(Player player) {
        if (player != null) {
            partyList.remove(player);
        }
    }

    /**
     * Add to list
     *
     * @param player
     */

    public static void addToList(Player player) {
        if (player != null) {
            partyList.add(player);
        }
    }

    /**
     * Configure new leader
     *
     * @param player
     */

    public Optional<Player> findNewLeader(Player player, int index) {
        return partyList.stream().filter(f -> partyList.get(index) == player).findFirst();
    }

    public void configureNewLeader(Player player, int index) {
        partyList.stream().filter(f -> player != null).filter(f -> player.getRaids() != null).filter(f -> partyList.size() > 0).forEach(p ->
        {
            /**
             * TODO redo when sober
             */
            if (player.raidsParty.getLeader() == leader) {
                player.getPacketSender().sendMessage("You've left the raiding party.");
                player.getRaids().exit(player);
                removeFromList(player);
            } else {
                p.getPacketSender().sendMessage(player.getUsername() + "has left the party.");
                Optional<Player> newleader = findNewLeader(player, index);
                if (newleader.isPresent()) {
                    this.leader = player;
                    p.getPacketSender().sendMessage(player.getUsername() + "is the new party leader.");
                }
            }
        });
    }

    /**
     * Disband raids party
     *
     * @param player
     * @return
     */
    public void disbandRaidsParty(Player player) {
        partyList.stream().filter(f -> player != null).filter(f -> player.getRaids() != null).forEach(p ->
        {
            int index = partyList.size();
            if (partyList.size() <= 0) {
                player.getRaids().exit(player);
                clearTeamList();
                return;
            }
            if (player.chambersParty.getLeader() == player && partyList.size() > 0) {
                player.getPacketSender().sendMessage("You've left the raiding party.");
                removeFromList(player);
                player.getRaids().exit(player);
                configureNewLeader(p, index);
                return;
            } else if (player.chambersParty.getLeader() == player) {
                player.getRaids().exit(player);
                player.getPacketSender().sendMessage("You've disbanded the raids party");
            }

            p.getPacketSender().sendMessage(player.getUsername() + "has disbanded the party.");
            player.getRaids().exit(player);
            clearTeamList();
        });
    }

    /**
     * Join
     */
    public static void join(Player player) {
        int index = 0;

        RaidsParty raidsParty = player.chambersParty;

        if (raidsParty == null) {
            return;
        }

        if (raidsParty.getPartyMembers().size() < index + 1) {
            player.getPacketSender().sendMessage("you cannot join an invalid party.");
            return;
        }

        if (player.getRaids().raiding(player)) {
            player.getPacketSender().sendMessage("you cannot join an active raid.");
            return;
        }

        Player raidPartyMember = raidsParty.getPartyMembers().get(index);

        if (raidPartyMember != null) {
            raidsParty.addMember(player);
            player.getPacketSender().sendMessage(raidPartyMember.getUsername() + "has joined the raids party!");
        }
    }


    /**
     * Leave raids party
     */
    public static void leaveRaidsParty(Player player) {
        partyList.stream().filter(f -> player != null).filter(f -> player.getRaids() != null).forEach(p ->
        {
            player.getPacketSender().sendMessage("you have left the raids party.");
            p.getPacketSender().sendMessage(player.getUsername() + "has left the raids party.");
            removeFromList(player);
            player.getRaids().exit(player);
        });
    }

    /**
     * Kick Member
     *
     * @param player
     */
    public void kick(Player player, int index) {
        RaidsParty raidsParty = player.chambersParty;
        if (raidsParty == null) {
            return;
        }
        if (raidsParty.getPartyMembers().size() < index + 1) {
            player.getPacketSender().sendMessage("There are no members in your party.");
            return;
        }
        if (raidsParty.getLeader() != player) {
            player.getPacketSender().sendMessage("Only the leader of this party can kick members.");
            return;
        }

        Player raidPartyMember = raidsParty.getPartyMembers().get(index);

        if (raidPartyMember != null) {
            raidsParty.removeMember(player);
            player.getPacketSender().sendMessage(raidPartyMember.getUsername() + "has been successfully kicked from the party.");
        }
    }
}

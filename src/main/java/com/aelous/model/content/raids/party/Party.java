package com.aelous.model.content.raids.party;

import com.aelous.model.content.raids.RaidsType;
import com.aelous.model.content.raids.chamber_of_xeric.ChamberOfXerics;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.COXArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.aelous.model.content.raids.RaidsType.CHAMBER_OF_XERICS;
import static com.aelous.model.entity.attributes.AttributeKey.PERSONAL_POINTS;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | April, 26, 2021, 16:56
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Party {

    public static final List<Item> COX_REWARDS = Arrays.asList(new Item(DEXTEROUS_PRAYER_SCROLL), new Item(ARCANE_PRAYER_SCROLL), new Item(TWISTED_BUCKLER), new Item(DRAGON_HUNTER_CROSSBOW), new Item(DINHS_BULWARK), new Item(ANCESTRAL_HAT), new Item(ANCESTRAL_ROBE_TOP), new Item(ANCESTRAL_ROBE_BOTTOM), new Item(DRAGON_CLAWS), new Item(ELDER_MAUL), new Item(KODAI_WAND), new Item(TWISTED_BOW));
    public static final List<Item> TOB_REWARDS = Arrays.asList(new Item(AVERNIC_DEFENDER), new Item(GHRAZI_RAPIER), new Item(SANGUINESTI_STAFF), new Item(JUSTICIAR_FACEGUARD), new Item(JUSTICIAR_LEGGUARDS), new Item(JUSTICIAR_CHESTGUARD), new Item(SCYTHE_OF_VITUR));

    public static final int REWARDS_CONTAINER_ID = 12137;
    public static final int TOTAL_POINTS = 12003;
    public static final int NAME_FRAME = 12004;
    public static final int POINTS = 12005;
    public static final int COS_CONFIG_ID = 1123;
    public static final int TOB_CONFIG_ID = 1124;
    public static final int HP_CONFIG_ID = 1125;
    private static final int PARTY_INTERFACE = 12100;
    private static final int LEADER_FRAME = 12117;

    private Player leader;
    private final List<Player> members;
    public ArrayList<NPC> monsters = new ArrayList<>();
    public ArrayList<GameObject> objects = new ArrayList<>();
    private RaidsType raidsSelected = CHAMBER_OF_XERICS;
    private int height;
    private int kills;
    private int raidStage = 0;
    public GameObject greatOlmCrystal;
    public GameObject greatOlmRewardCrystal;
    public GameObject chestRewardLight;

    public Party(Player leader) {
        this.leader = leader;
        members = new ArrayList<>();
        members.add(leader);
    }

    public Player getLeader() {
        return leader;
    }

    public List<Player> getMembers() {
        return members;
    }

    public int getSize() {
        return members.size();
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setRaidStage(int raidStage) {
        this.raidStage = raidStage;
    }

    public int getRaidStage() {
        return raidStage;
    }

    public RaidsType getRaidsSelected() {
        return raidsSelected;
    }

    public void setRaidsSelected(RaidsType raidsSelected) {
        this.raidsSelected = raidsSelected;
    }

    private NPC mommaMuttadile;

    public NPC getMommaMuttadile() {
        return mommaMuttadile;
    }

    public void setMommaMuttadile(NPC mommaMuttadile) {
        this.mommaMuttadile = mommaMuttadile;
    }

    private GameObject meatTree;

    public GameObject getMeatTree() {
        return meatTree;
    }

    public void setMeatTree(GameObject meatTree) {
        this.meatTree = meatTree;
    }

    public void removeMember(Player player) {
        members.remove(player);
        if (members.size() > 0 && player == leader) {
            leader = members.get(0);
        }
    }

    public void forPlayers(Consumer<Player> action) {
        members.forEach(action);
    }

    public void addPersonalPoints(Player player, int points) {
        boolean eliteMember = player.getMemberRights().isEliteMemberOrGreater(player);
        boolean extremeMember = player.getMemberRights().isExtremeMemberOrGreater(player);
        boolean LegendaryMember = player.getMemberRights().isLegendaryMemberOrGreater(player);
        boolean vipMember = player.getMemberRights().isVIPOrGreater(player);
        boolean sponsorMember = player.getMemberRights().isSponsorOrGreater(player);

        var percentageBoost = 0;

        if (eliteMember) {
            percentageBoost += 5;
        } else if (extremeMember) {
            percentageBoost += 10;
        } else if (LegendaryMember) {
            percentageBoost += 15;
        } else if (vipMember) {
            percentageBoost += 20;
        } else if (sponsorMember) {
            percentageBoost += 25;
        }

        var extraPoints = points * percentageBoost / 100;
        points += extraPoints;
        var increaseBy = player.<Integer>getAttribOr(PERSONAL_POINTS, 0) + points;
        player.putAttrib(PERSONAL_POINTS, increaseBy);
    }

    public int totalPoints() {
        return members.stream().mapToInt(m -> m.<Integer>getAttribOr(PERSONAL_POINTS, 0)).sum();
    }

    public void teamMessage(String message) {
        forPlayers(p -> p.message(message));
    }

    private static void clearInterface(Player player) {
        //Party members
        for (int i = 12117; i <= 12121; i++) {
            player.getPacketSender().sendString(i, "");
        }
    }

    public static void createParty(Player player) {
        player.raidsParty = new Party(player);
    }

    public static void openPartyInterface(Player player, boolean updateMembers) {
        clearInterface(player);//Clear previous frames

        //Default COX
        player.getPacketSender().sendConfig(COS_CONFIG_ID, 1).sendConfig(TOB_CONFIG_ID, 0).sendConfig(HP_CONFIG_ID, 0);
        player.getPacketSender().sendItemOnInterface(REWARDS_CONTAINER_ID, COX_REWARDS);

        //Set leader info
        Player partyLeader = player.raidsParty.getLeader();
        player.getPacketSender().sendString(12103, "Raiding party setup - " + partyLeader.getUsername() + "'s party");
        player.getPacketSender().sendString(LEADER_FRAME, "<col=ffffff>" + partyLeader.getUsername());

        //Set the raids we're going to enter, by default COX
        Party party = partyLeader.raidsParty;
        party.raidsSelected = CHAMBER_OF_XERICS;
        player.setRaids(new ChamberOfXerics());

        //Open interface
        player.getInterfaceManager().open(PARTY_INTERFACE);

        if (updateMembers)
            displayPartyMembers(player, player.raidsParty);
    }

    public static void displayPartyMembers(Player player, Party party) {
        if (party == null) {
            return;
        }

        if (party.getMembers().size() != 0) {
            for (int i = 0; i < party.getMembers().size(); i++) {
                if (party.getMembers().get(i) == party.getLeader())
                    continue;
                player.getPacketSender().sendString(LEADER_FRAME + i, "" + "<col=9f9f9f>" + party.getMembers().get(i).getUsername());
            }
        }
    }

    public static void refreshInterface(Player leader, Party party) {
        if (party == null) {
            return;
        }

        for (Player partyMembers : party.getMembers()) {
            //Clear ghost entries
            for (int i = 0; i < 4; i++) {
                partyMembers.getPacketSender().sendString(LEADER_FRAME + i, "");
                leader.getPacketSender().sendString(LEADER_FRAME + i, "");
            }

            //Shift party members
            for (int i = 0; i < party.getMembers().size(); i++) {
                if (leader.raidsParty != null) {
                    partyMembers.getPacketSender().sendString(LEADER_FRAME + i, "" + "<col=9f9f9f>" + leader.raidsParty.getMembers().get(i).getUsername());
                    leader.getPacketSender().sendString(LEADER_FRAME + i, "" + "<col=9f9f9f>" + leader.raidsParty.getMembers().get(i).getUsername());
                    partyMembers.getPacketSender().sendConfig(COS_CONFIG_ID, leader.raidsParty.raidsSelected == CHAMBER_OF_XERICS ? 1 : 0).sendConfig(TOB_CONFIG_ID, leader.raidsParty.raidsSelected == RaidsType.THEATRE_OF_BLOOD ? 1 : 0).sendConfig(HP_CONFIG_ID, leader.raidsParty.raidsSelected == CHAMBER_OF_XERICS ? 1 : 0);
                    partyMembers.getPacketSender().sendItemOnInterface(REWARDS_CONTAINER_ID, leader.raidsParty.raidsSelected == CHAMBER_OF_XERICS ? COX_REWARDS : leader.raidsParty.raidsSelected == RaidsType.THEATRE_OF_BLOOD ? TOB_REWARDS : COX_REWARDS);
                }
            }
        }
    }

    public static void kick(Player player, int index) {
        Party party = player.raidsParty;
        if (party == null) {
            return;
        }

        //There has to be a party member
        if (party.getMembers().size() < index + 1) {
            player.message("There is no member to kick.");
            return;
        }

        //We have at least 2 party members (leader included), check if we are the leader.
        if (party.getLeader() != player) {
            player.message("Only the leader of this party can kick members.");
            return;
        }

        //We are the leader lets continue and grab the party member in the list.
        Player partyMember = party.getMembers().get(index);
        if (partyMember != null) {
            party.removeMember(partyMember);
            partyMember.getInterfaceManager().close();
            player.message(partyMember.getUsername() + " has been successfully kicked from the party.");
            partyMember.message("You have been kicked out the raids party.");
            refreshInterface(party.getLeader(), party);
        }
    }

    public static void leaveParty(Player player, boolean destroyFromBoard) {
        Party party = player.raidsParty;
        if (party == null) {
            return;
        }
        Player partyLeader = party.getLeader();
        if (partyLeader == player) {
            disbandParty(player, destroyFromBoard);
            return;
        }
        player.message("<col=ef20ff>You leave " + player.raidsParty.getLeader().getUsername() + "'s party.");
        party.removeMember(player);
        player.raidsParty = null;
        refreshInterface(partyLeader, partyLeader.raidsParty);
        player.getInterfaceManager().close();
    }

    public static void disbandParty(Player player, boolean destroyFromBoard) {
        final var raidsParty = player.raidsParty;
        if (player.raidsParty.getLeader() == player) {
            for (Player member : player.raidsParty.getMembers()) {
                member.message("<col=ef20ff>" + player.getUsername() + " has disbanded the party.");
                member.raidsParty = null;
                if (!destroyFromBoard) {
                    if (member.getRaids() != null) {
                        member.getRaids().exit(member);
                    }
                } else {
                    member.getInterfaceManager().close();
                }
            }
        }
        raidsParty.members.clear();
    }

    public static void onLogout(Player player) {
        if (player.raidsParty != null) {
            leaveParty(player, false);
            if (player.getRaids() != null) {
                player.getRaids().exit(player);
            }
        }
    }

    public static void startRaid(Player p) {
        Party party = p.raidsParty;
        if (party.getLeader() != p) {
            p.message("Only the party leader can start the fight.");
            return;
        }

        if (party.getRaidsSelected() == RaidsType.THEATRE_OF_BLOOD) {
            p.message("The Theatre of blood is under construction. Please choose another.");
            return;
        }

        p.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Start raid.", "Nevermind.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        Party party = p.raidsParty;
                        if (party == null) {
                            stop();
                            return;
                        }
                        stop();

                        p.getInterfaceManager().close();
                        if (p.getRaids() != null) {
                            if (p.raidsParty.getLeader() == player) {
                                    p.getRaids().startup(p);
                                    party.getMembers().forEach(member -> member.message("<col=ef20ff>The raid has begun!"));
                            }
                        }
                    }
                    if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

}

package com.aelous.model.content.raids.theatre.interactions;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.party.TheatreParty;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import org.apache.commons.lang.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TheatreInterface extends TheatreParty {

    public TheatreInterface(Player leader) {
        super(leader);
    }

    public boolean create(Player player, int button) {
        if (button == 73054) {
            if (player.getTheatreParty() == null) {
                createParty();
                leader.setTheatreParty(this);
                leader.getPacketSender().sendString(73054, "Invite");
                leader.getPacketSender().sendString(73074, Color.ORANGE.wrap(leader.getDisplayName()));
                refreshPartyUi(player.getTheatreParty());
                leader.getPacketSender().sendString(73083, "--");
                leader.getPacketSender().sendString(73092, "--");
                leader.getPacketSender().sendString(73101, "--");
                leader.getPacketSender().sendString(73110, "--");
            } else {
                if (player.getTheatreParty().getLeader() == leader) {
                    request(player, button);
                } else {
                    player.message(Color.RED.wrap("You do not have permission to perform this action."));
                }
            }
            return true;
        }
        return false;
    }

    public boolean abandon(Player player, int button) {
        var party = player.getTheatreParty();
        if (button == 73055) {
            clearInterface(player);
            if (party != null) {
                if (party.getLeader().equals(player)) {
                    for (Player p : party.getParty()) {
                        clearInterface(p);
                        p.setTheatreParty(null);
                    }
                    player.setTheatreParty(null);
                    party.clear();
                } else {
                    party.getParty().remove(player);
                    clearInterface(player);
                    player.setTheatreParty(null);
                }
                refreshPartyUi(party);
            }
        }
        return false;
    }

    public void clearInterface(Player player) {
        for (int memberSlot = 0; memberSlot < 5; memberSlot++) {
            int offset = memberSlot > 0 ? memberSlot * 9 : 0;
            player.getPacketSender().sendString(73054, "Create");
            player.getPacketSender().sendString(73055, "Disband");
            player.getPacketSender().sendString(73074 + offset, "--");
            player.getPacketSender().sendString(73075 + offset, "--");
            player.getPacketSender().sendString(73076 + offset, "--");
            player.getPacketSender().sendString(73077 + offset, "--");
            player.getPacketSender().sendString(73078 + offset, "--");
            player.getPacketSender().sendString(73079 + offset, "--");
            player.getPacketSender().sendString(73080 + offset, "--");
            player.getPacketSender().sendString(73081 + offset, "--");
            player.getPacketSender().sendString(73082 + offset, "--");
        }
    }

    public void wipeStatsForSlot(Player player, int i) {
        int offset = i > 0 ? i * 9 : 0;
        player.getPacketSender().sendString(73054, "Create");
        player.getPacketSender().sendString(73055, "Disband");
        player.getPacketSender().sendString(73074 + offset, "--");
        player.getPacketSender().sendString(73075 + offset, "--");
        player.getPacketSender().sendString(73076 + offset, "--");
        player.getPacketSender().sendString(73077 + offset, "--");
        player.getPacketSender().sendString(73078 + offset, "--");
        player.getPacketSender().sendString(73079 + offset, "--");
        player.getPacketSender().sendString(73080 + offset, "--");
        player.getPacketSender().sendString(73081 + offset, "--");
        player.getPacketSender().sendString(73082 + offset, "--");
    }

    public void refreshPartyUi(TheatreParty party) {
        for (Player p2 : party.getParty()) {
            for (int i = 0; i < 4; i++) {
                Player m = i == 0 ? party.leader : i >= party.getParty().size() ? null : party.getParty().get(i);

                if (m == null) {
                    wipeStatsForSlot(p2, i);
                    continue;
                }
                int offset = i > 0 ? i * 9 : i;

                p2.getPacketSender().sendString(73074 + offset, Color.ORANGE.wrap(m.getDisplayName()));
                String combatLevel = Integer.toString(m.getSkills().combatLevel());
                p2.getPacketSender().sendString(73075 + offset, combatLevel);
                String attack = Integer.toString(m.getSkills().level(Skills.ATTACK));
                p2.getPacketSender().sendString(73076 + offset, attack);
                String strength = Integer.toString(m.getSkills().level(Skills.STRENGTH));
                p2.getPacketSender().sendString(73077 + offset, strength);
                String ranged = Integer.toString(m.getSkills().level(Skills.RANGED));
                p2.getPacketSender().sendString(73078 + offset, ranged);
                String magic = Integer.toString(m.getSkills().level(Skills.MAGIC));
                p2.getPacketSender().sendString(73079 + offset, magic);
                String defence = Integer.toString(m.getSkills().level(Skills.DEFENCE));
                p2.getPacketSender().sendString(73080 + offset, defence);
                String hitpoints = Integer.toString(m.getSkills().level(Skills.HITPOINTS));
                p2.getPacketSender().sendString(73081 + offset, hitpoints);
                String prayer = Integer.toString(m.getSkills().level(Skills.PRAYER));
                p2.getPacketSender().sendString(73082 + offset, prayer);

            }
        }
    }

    public boolean request(Player player, int button) {
        if (button == 73054) {
            if (leader.equals(player.getTheatreParty().getLeader())) {
                this.sendLeaderDialogue();
                return true;
            }
        }
        return false;
    }

    public void open(Player player) {
        if (player != null) {
            player.getPacketSender().sendInterface(73050);
            player.getPacketSender().sendString(73052, "Theatre Of Blood Party");
        }
    }

    public boolean close(Player player, int button) {
        if (player != null && button == 73053) {
            player.getInterfaceManager().close();
            return true;
        }
        return false;
    }

    int[] buttons = new int[]{73083, 73092, 73101, 73110};

    public boolean kick(Player player, int button) {
        Map<Integer, Integer> buttonToPartyIndex = new HashMap<>();
        buttonToPartyIndex.put(73083, 1);
        buttonToPartyIndex.put(73092, 2);
        buttonToPartyIndex.put(73101, 3);
        buttonToPartyIndex.put(73110, 4);

        TheatreParty party = player.getTheatreParty();

        if (!buttonToPartyIndex.containsKey(button)) {
            return false;
        }

        if (!party.getLeader().equals(player)) {
            return false;
        }

        int playerIndexToKick = buttonToPartyIndex.get(button);
        if (playerIndexToKick >= 0 && playerIndexToKick < party.getParty().size()) {
            Player playerToKick = party.getParty().get(playerIndexToKick);

            party.getParty().remove(playerToKick);
            clearInterface(playerToKick);
            playerToKick.setTheatreParty(null);
            refreshPartyUi(party);
            return true;
        }

        return false;
    }


    public boolean refresh(TheatreParty party, int button) {
        if (button == 73057) {
            refreshPartyUi(party);
            System.out.println("refreshing");
            return true;
        }
        return false;
    }

    public void invite(Player player, Player member) {
        if (!player.getTheatreParty().getLeader().equals(leader)) {
            player.message("You are not the party leader and cannot invite members.");
            return;
        }

        if (member.getTheatreParty() != null) {
            leader.message(member.getUsername() + " is already in a party.");
            return;
        }

        DialogueManager.sendStatement(player, "Requesting..");
        member.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, player.getUsername() + " has invited you to join their party.", "Accept", "Decline");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        if (member.getTheatreParty() != null) {
                            DialogueManager.sendStatement(leader, member.getUsername() + " is already in a party.");
                            return;
                        } else {
                            if (party != null) {
                                party.add(member);
                                member.setTheatreParty(leader.getTheatreParty());
                                member.message("You've joined " + leader.getUsername() + "'s raid party.");
                                DialogueManager.sendStatement(leader, member.getUsername() + " has joined your raid party.");
                                member.getPacketSender().sendString(73055, "Leave");
                                refreshPartyUi(member.getTheatreParty());
                            }
                            stop();
                        }
                    }
                    if (option == 2) {
                        DialogueManager.sendStatement(player, member.getUsername() + " has declined your request to join your raid party.");
                        member.message("You decline " + player.getUsername() + "'s request to join their party.");
                        stop();
                    }
                }
            }
        });
    }

    public void sendLeaderDialogue() {
        if (leader != null && leader.getTheatreParty() != null) {
            leader.setNameScript("Who would you like to invite?", value -> {

                String name = (String) value;
                Optional<Player> target = World.getWorld().getPlayerByName(name);

                if (target.isPresent()) {
                    if (target.get().tile().region() != 14642) {
                        leader.message(Utils.formatName(name) + " is nowhere near the raids area.");
                        return false;
                    }

                    this.invite(leader, target.get());

                } else {
                    leader.message(Utils.formatName(name) + " is not online and cannot join your party.");
                    leader.getInterfaceManager().closeDialogue();
                }
                return true;
            });
        }
    }

}

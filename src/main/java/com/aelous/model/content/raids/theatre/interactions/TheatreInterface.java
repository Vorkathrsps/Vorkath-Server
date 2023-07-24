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
                update(leader, Integer.toString(leader.getSkills().combatLevel()), Integer.toString(leader.getSkills().level(Skills.ATTACK)), Integer.toString(leader.getSkills().level(Skills.STRENGTH)), Integer.toString(leader.getSkills().level(Skills.RANGED)), Integer.toString(leader.getSkills().level(Skills.MAGIC)), Integer.toString(leader.getSkills().level(Skills.DEFENCE)), Integer.toString(leader.getSkills().level(Skills.HITPOINTS)), Integer.toString(leader.getSkills().level(Skills.PRAYER)));
                leader.getPacketSender().sendString(73083, "--------");
                leader.getPacketSender().sendString(73092, "--------");
                leader.getPacketSender().sendString(73101, "--------");
                leader.getPacketSender().sendString(73110, "--------");
                return true;
            } else {
                if (leader == player) {
                    request(leader, button);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean abandon(Player player, int button) {
        if (button == 73055) {
            if (!player.equals(leader)) {
                player.message("You cannot perform this action.");
            } else {
                for (var p : party) {
                    if (p != null) {
                        this.clearParty(p);
                        player.message("cleared");
                        p.setTheatreParty(null);
                    }
                }
                party.clear();
            }
            return true;
        }
        return false;
    }

    private void clearParty(Player player) {
        String emptyString = "--------";

        for (int i = 0; i < 5; i++) {
            int offset = i * 9;
            player.getPacketSender().sendString(73054, "Create");
            player.getPacketSender().sendString(73074 + offset, emptyString);
            player.getPacketSender().sendString(73075 + offset, "--");
            player.getPacketSender().sendString(73076 + offset, "--");
            player.getPacketSender().sendString(73077 + offset, "--");
            player.getPacketSender().sendString(73078 + offset, "--");
            player.getPacketSender().sendString(73079 + offset, "--");
            player.getPacketSender().sendString(73080 + offset, "--");
            player.getPacketSender().sendString(73081 + offset, "--");
            player.getPacketSender().sendString(73082 + offset, "--");
        }

        if (!player.equals(leader)) {
            player.message("Your party has been disbanded.");
        } else {
            player.message("You have disbanded your party.");
        }
    }



    public boolean request(Player player, int button) {
        if (leader != null) {
            if (leader == player) {
                if (button == 73054) {
                    this.sendLeaderDialogue();
                    return true;
                }
            }
        }
        return false;
    }

    public void open(Player player) {
        if (player != null) {
            player.setTheatreInterface(this);
            player.getPacketSender().sendInterface(73050);
            player.getPacketSender().sendString(73052, "Theatre Of Blood Party");
        }
    }

    public boolean close(Player player, int button) {
        if (player != null && button == 73053) {
            //player.getPacketSender().sendInterfaceRemoval();
            player.getInterfaceManager().close();
            return true;
        }
        return false;
    }

    public void update(Player leader, String combatLevel, String attack, String strength, String ranged, String magic, String defence, String hitpoints, String prayer) {
        if (leader != null && leader.getTheatreParty() != null) {
            // Send the information for the party leader
            leader.getPacketSender().sendString(73074, Color.ORANGE.wrap(leader.getDisplayName()));
            leader.getPacketSender().sendString(73075, combatLevel);
            leader.getPacketSender().sendString(73076, attack);
            leader.getPacketSender().sendString(73077, strength);
            leader.getPacketSender().sendString(73078, ranged);
            leader.getPacketSender().sendString(73079, magic);
            leader.getPacketSender().sendString(73080, defence);
            leader.getPacketSender().sendString(73081, hitpoints);
            leader.getPacketSender().sendString(73082, prayer);

            // Send the information for other party members
            for (int i = 0; i < party.size(); i++) {
                if (!party.get(i).equals(leader)) {
                    Player m = party.get(i);
                    int offset = i * 9;

                    m.getPacketSender().sendString(73074 + offset, Color.ORANGE.wrap(m.getDisplayName()));
                    m.getPacketSender().sendString(73075 + offset, combatLevel);
                    m.getPacketSender().sendString(73076 + offset, attack);
                    m.getPacketSender().sendString(73077 + offset, strength);
                    m.getPacketSender().sendString(73078 + offset, ranged);
                    m.getPacketSender().sendString(73079 + offset, magic);
                    m.getPacketSender().sendString(73080 + offset, defence);
                    m.getPacketSender().sendString(73081 + offset, hitpoints);
                    m.getPacketSender().sendString(73082 + offset, prayer);

                    leader.getPacketSender().sendString(73074 + offset, Color.ORANGE.wrap(m.getDisplayName()));
                    leader.getPacketSender().sendString(73075 + offset, combatLevel);
                    leader.getPacketSender().sendString(73076 + offset, attack);
                    leader.getPacketSender().sendString(73077 + offset, strength);
                    leader.getPacketSender().sendString(73078 + offset, ranged);
                    leader.getPacketSender().sendString(73079 + offset, magic);
                    leader.getPacketSender().sendString(73080 + offset, defence);
                    leader.getPacketSender().sendString(73081 + offset, hitpoints);
                    leader.getPacketSender().sendString(73082 + offset, prayer);
                }
            }
        }
    }

    public boolean kick(Player player, int button) {
        return false;
    }

    public boolean refresh(Player player, int button) {
        return false;
    }

    public void invite(Player player, Player member) {
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
                                //member.setTheatreParty(leader.getTheatreParty());
                                member.message("You've joined " + leader.getUsername() + "'s raid party.");
                                DialogueManager.sendStatement(leader, member.getUsername() + " has joined your raid party.");
                                update(leader, Integer.toString(leader.getSkills().combatLevel()), Integer.toString(leader.getSkills().level(Skills.ATTACK)), Integer.toString(leader.getSkills().level(Skills.STRENGTH)), Integer.toString(leader.getSkills().level(Skills.RANGED)), Integer.toString(leader.getSkills().level(Skills.MAGIC)), Integer.toString(leader.getSkills().level(Skills.DEFENCE)), Integer.toString(leader.getSkills().level(Skills.HITPOINTS)), Integer.toString(leader.getSkills().level(Skills.PRAYER)));
                                update(member, Integer.toString(member.getSkills().combatLevel()), Integer.toString(member.getSkills().level(Skills.ATTACK)), Integer.toString(member.getSkills().level(Skills.STRENGTH)), Integer.toString(member.getSkills().level(Skills.RANGED)), Integer.toString(member.getSkills().level(Skills.MAGIC)), Integer.toString(member.getSkills().level(Skills.DEFENCE)), Integer.toString(member.getSkills().level(Skills.HITPOINTS)), Integer.toString(member.getSkills().level(Skills.PRAYER)));
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

    public boolean createAndInvite(Player player, int button) {
        if (player != null && player.getTheatreParty() != null && player.getTheatreParty().getLeader() == player && button == 73054) {
            this.sendLeaderDialogue();
            return true;
        }
        if (player != null && button == 73054) {
            if (player.theatreParty == null) {
                //  party.createParty();
                player.getPacketSender().sendString(73054, "Invite");
                player.getPacketSender().sendString(73074, Color.ORANGE.wrap(player.getDisplayName()));
                this.update(player, Integer.toString(player.getSkills().combatLevel()), Integer.toString(player.getSkills().level(Skills.ATTACK)), Integer.toString(player.getSkills().level(Skills.STRENGTH)), Integer.toString(player.getSkills().level(Skills.RANGED)), Integer.toString(player.getSkills().level(Skills.MAGIC)), Integer.toString(player.getSkills().level(Skills.DEFENCE)), Integer.toString(player.getSkills().level(Skills.HITPOINTS)), Integer.toString(player.getSkills().level(Skills.PRAYER)));
                player.getPacketSender().sendString(73083, "--------");
                player.getPacketSender().sendString(73092, "--------");
                player.getPacketSender().sendString(73101, "--------");
                player.getPacketSender().sendString(73110, "--------");
                return true;
            } else {
                player.message("You already have an existing party.");
                return false;
            }
        }
        return false;
    }

    public String greenString(String text) {
        return "\u001B[32m" + text + "\u001B[0m";
    }
}

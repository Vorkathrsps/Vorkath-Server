package com.cryptic.model.content.raids.theatreofblood.interactions;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatreofblood.party.RaidParty;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.DialogueManager;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TheatreInterface extends RaidParty {

    public TheatreInterface(Player owner, List<Player> players) {
        super(owner, players);
    }

    public boolean create(Player player, int button) {
        if (button == 76004) {
            if (player.getRaidParty() == null) {
                player.setRaidParty(this);
                player.getRaidParty().addOwner();
                player.getPacketSender().sendString(76004, "Invite");
                player.getPacketSender().sendString(76024, Color.ORANGE.wrap(player.getDisplayName()));
                refreshPartyUi(player.getRaidParty());
                player.getPacketSender().sendString(76033, "--");
                player.getPacketSender().sendString(76042, "--");
                player.getPacketSender().sendString(76051, "--");
                player.getPacketSender().sendString(76060, "--");
            } else {
                if (player.getRaidParty().getOwner() == player) {
                    request(player, button);
                } else {
                    player.message(Color.RED.wrap("You do not have permission to perform this action."));
                }
            }
            return true;
        } else if (button == 76007) {
            refresh(player.getRaidParty());
            return true;
        }
        return false;
    }

    private static final Logger DEBUG = Logger.getLogger(TheatreInterface.class.getName());

    public void handleLogout(Player player) {
        try {
            DEBUG.info("Handling logout for player: " + player.getUsername());

            var party = player.getRaidParty();
            DEBUG.info("Party: " + party);

            clearInterface(getOwner());

            if (party != null) {
                DEBUG.info("Party owner: " + party.getOwner().getUsername());
                if (party.getOwner().equals(getOwner())) {
                    DEBUG.info("Player is party owner.");
                    for (Player p : party.getPlayers()) {
                        clearInterface(p);
                        p.message(Color.RED.wrap("The raiding party has been disbanded."));
                        p.setRaidParty(null);
                        DEBUG.info("Cleared interface and set TheatreParty to null for player: " + p.getUsername());
                    }
                    party.clear();
                    DEBUG.info("Cleared the party.");
                } else {
                    party.getPlayers().remove(player);
                    clearInterface(player);
                    player.setRaidParty(null);
                    DEBUG.info("Removed player from the party and cleared interface. Set TheatreParty to null for player: " + player.getUsername());
                }
                refreshPartyUi(party);
                DEBUG.info("Refreshed party UI.");
            }

            DEBUG.info("Logout handling complete for player: " + player.getUsername());
        } catch (Throwable t) {
            DEBUG.log(Level.SEVERE, "An error occurred during logout handling in " + getClass().getName() +  "for player: " + player.getUsername(), t);
        }
    }


    public boolean abandon(Player player, int button) {
        var party = player.getRaidParty();
        if (button == 76005) {
            clearInterface(player);
            if (party != null) {
                if (party.getOwner() == null) {
                    return false;
                }
                if (party.getOwner().equals(player)) {
                    for (Player p : party.getPlayers()) {
                        clearInterface(p);
                        p.setRaidParty(null);
                    }
                    player.setRaidParty(null);
                    party.clear();
                } else {
                    party.getPlayers().remove(player);
                    clearInterface(player);
                    player.setRaidParty(null);
                }
                refreshPartyUi(party);
            }
        }
        return false;
    }

    public void clearInterface(Player player) {
        for (int memberSlot = 0; memberSlot < 5; memberSlot++) {
            int offset = memberSlot > 0 ? memberSlot * 9 : 0;
            player.getPacketSender().sendString(76004, "Create/Invite");
            player.getPacketSender().sendString(76005, "Disband/Leave");
            player.getPacketSender().sendString(76024 + offset, "--"); //name
            player.getPacketSender().sendString(76033 + offset, "--");
            player.getPacketSender().sendString(76025 + offset, "--");
            player.getPacketSender().sendString(76026 + offset, "--");
            player.getPacketSender().sendString(76027 + offset, "--");
            player.getPacketSender().sendString(76028 + offset, "--");
            player.getPacketSender().sendString(76029 + offset, "--");
            player.getPacketSender().sendString(76030 + offset, "--");
            player.getPacketSender().sendString(76031 + offset, "--");
            player.getPacketSender().sendString(76032 + offset, "--"); //party names
        }
    }

    public void wipeStatsForSlot(Player player, int i) {
        int offset = i > 0 ? i * 9 : 0;
        player.getPacketSender().sendString(76004, "Create/Invite");
        player.getPacketSender().sendString(76005, "Disband/Leave");
        player.getPacketSender().sendString(76024 + offset, "--"); //name
        player.getPacketSender().sendString(76033 + offset, "--");
        player.getPacketSender().sendString(76025 + offset, "--");
        player.getPacketSender().sendString(76026 + offset, "--");
        player.getPacketSender().sendString(76027 + offset, "--");
        player.getPacketSender().sendString(76028 + offset, "--");
        player.getPacketSender().sendString(76029 + offset, "--");
        player.getPacketSender().sendString(76030 + offset, "--");
        player.getPacketSender().sendString(76031 + offset, "--");
        player.getPacketSender().sendString(76032 + offset, "--"); //party names
    }

    public void refresh(RaidParty party) {
        refreshPartyUi(party);
    }

    public void refreshPartyUi(RaidParty party) {
        if (party == null) {
            return;
        }

        for (Player p2 : party.getPlayers()) {

            for (int i = 0; i < 4; i++) {
                Player m = i == 0 ? party.getOwner() : i >= party.getPlayers().size() ? null : party.getPlayers().get(i);

                if (m == null) {
                    wipeStatsForSlot(p2, i);
                    continue;
                }

                int offset = i > 0 ? i * 9 : i;

                p2.getPacketSender().sendString(76024 + offset, Color.ORANGE.wrap(m.getUsername()));
                String combatLevel = Integer.toString(m.getSkills().combatLevel());
                p2.getPacketSender().sendString(76025 + offset, combatLevel);
                String attack = Integer.toString(m.getSkills().level(Skills.ATTACK));
                p2.getPacketSender().sendString(76026 + offset, attack);
                String strength = Integer.toString(m.getSkills().level(Skills.STRENGTH));
                p2.getPacketSender().sendString(76027 + offset, strength);
                String ranged = Integer.toString(m.getSkills().level(Skills.RANGED));
                p2.getPacketSender().sendString(76028 + offset, ranged);
                String magic = Integer.toString(m.getSkills().level(Skills.MAGIC));
                p2.getPacketSender().sendString(76029 + offset, magic);
                String defence = Integer.toString(m.getSkills().level(Skills.DEFENCE));
                p2.getPacketSender().sendString(76030 + offset, defence);
                String hitpoints = Integer.toString(m.getSkills().level(Skills.HITPOINTS));
                p2.getPacketSender().sendString(76031 + offset, hitpoints);
                String prayer = Integer.toString(m.getSkills().level(Skills.PRAYER));
                p2.getPacketSender().sendString(76032 + offset, prayer);

            }
        }
    }

    public boolean request(Player player, int button) {
        if (button == 76004) {
            if (player.equals(player.getRaidParty().getOwner())) {
                this.sendLeaderDialogue();
                return true;
            }
        }
        return false;
    }

    public TheatreInterface open(Player player) {
        if (player != null) {
            player.getInterfaceManager().open(76000);
            player.getPacketSender().sendString(76002, "Theatre Of Blood Party");
            if (player.getRaidParty() != null) {
                var party = player.getRaidParty();
                refreshPartyUi(party);
            } else {
                clearInterface(player);
            }
        }
        return this;
    }

    public boolean close(Player player, int button) {
        if (player != null && button == 76003) {
            player.getInterfaceManager().close();
            return true;
        }
        return false;
    }

    public boolean kick(Player player, int button) {
        Map<Integer, Integer> buttonToPartyIndex = new HashMap<>();
        buttonToPartyIndex.put(73083, 1);
        buttonToPartyIndex.put(73092, 2);
        buttonToPartyIndex.put(73101, 3);
        buttonToPartyIndex.put(73110, 4);

        RaidParty party = player.getRaidParty();

        if (!buttonToPartyIndex.containsKey(button)) {
            return false;
        }

        if (!party.getOwner().equals(player)) {
            return false;
        }

        int playerIndexToKick = buttonToPartyIndex.get(button);
        if (playerIndexToKick >= 0 && playerIndexToKick < party.getPlayers().size()) {
            Player playerToKick = party.getPlayers().get(playerIndexToKick);

            party.getPlayers().remove(playerToKick);
            clearInterface(playerToKick);
            playerToKick.setRaidParty(null);
            refreshPartyUi(party);
            return true;
        }

        return false;
    }

    public void invite(Player player, Player member) {
        try {
            if (!player.getRaidParty().getOwner().equals(player)) {
                player.message("You are not the party leader and cannot invite members.");
                DEBUG.warning("Player " + player.getUsername() + " attempted to invite a member without being the party leader.");
                return;
            }

            if (member.getRaidParty() != null) {
                player.getRaidParty().getOwner().message(member.getUsername() + " is already in a party.");
                DEBUG.warning("Player " + player.getUsername() + " attempted to invite " + member.getUsername() + " who is already in a party.");
                return;
            }

            player.getDialogueManager().sendStatement( "Requesting..");
            member.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    sendOption(getOwner().getUsername() + " has invited you to join their party.", "Accept", "Decline");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            if (member.getRaidParty() != null) {
                                DialogueManager.sendStatement(getOwner(), member.getUsername() + " is already in a party.");
                                return;
                            } else {
                                if (getOwner().getRaidParty() != null) {
                                    getPlayers().add(member);
                                    member.setRaidParty(getOwner().getRaidParty());
                                    member.message("You've joined " + getOwner().getUsername() + "'s raid party.");
                                    DialogueManager.sendStatement(getOwner(), member.getUsername() + " has joined your raid party.");
                                    member.getPacketSender().sendString(73055, "Leave");
                                    refreshPartyUi(member.getRaidParty());
                                }
                                stop();
                            }
                        }
                        if (option == 2) {
                            player.getDialogueManager().sendStatement( member.getUsername() + " has declined your request to join your raid party.");
                            member.message("You decline " + player.getUsername() + "'s request to join their party.");
                            stop();
                        }
                    }
                }
            });
        } catch (Exception e) {
            DEBUG.log(Level.SEVERE, "An error occurred during invitation.", e);
        }
    }

    public void sendLeaderDialogue() {
        if (getOwner() != null && getOwner().getRaidParty() != null) {
            getOwner().setNameScript("Who would you like to invite?", value -> {

                String name = (String) value;
                Optional<Player> target = World.getWorld().getPlayerByName(name);

                if (target.isPresent()) {
                    if (target.get().tile().region() != 14642) {
                        getOwner().message(Utils.formatName(name) + " is nowhere near the raids area.");
                        return false;
                    }

                    this.invite(getOwner(), target.get());

                } else {
                    getOwner().message(Utils.formatName(name) + " is not online and cannot join your party.");
                    getOwner().getInterfaceManager().closeDialogue();
                }
                return true;
            });
        }
    }

}

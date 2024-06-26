package com.cryptic.model.content.raids;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.cache.definitions.identifiers.ObjectIdentifiers;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.model.content.raids.chamber_of_xeric.ChamberOfXerics;
import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.content.raids.party.dialogue.PartyDialogue;
import com.cryptic.model.World;
import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.inter.clan.*;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import java.util.Optional;

import static com.cryptic.model.content.raids.party.Party.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.RECRUITING_BOARD;

/**
 * @author Origin | April, 26, 2021, 17:25
 */
public class RaidPartyActions extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (option == 1) {
            if (object.getId() == ObjectIdentifiers.CHAMBERS_OF_XERIC) {
                Clan clan = player.getClan();
                if (clan == null) {
                    player.message(Color.RED.wrap("<img=13> You must first join or make a clan channel to start this raid.</img>"));
                    return true;
                }
                if (isMember(clan)) return true;
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendNpcChat(NpcIdentifiers.CAPTAIN_RIMOR, Expression.DEFAULT, "Hello " + player.getUsername(), "Would you like to start your raid?");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            sendOption("Would you like to start this raid?", "Yes", "No");
                            setPhase(1);
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(1)) {
                            if (option == 1) {
                                startRaid(player, clan);
                                stop();
                            } else {
                                stop();
                            }
                        }

                    }
                });
                return true;
            }
        }
        return false;
    }

    private boolean isMember(Clan clan) {
        for (ClanMember member : clan.members()) {
            Player clanMember = member.getPlayer();
            ClanRank memberRank = member.getRank();
            if (!memberRank.equals(ClanRank.LEADER)) {
                clanMember.message(Color.RED.wrap("<img=13> Only the leader of this clan can start the raid.</img>"));
                return true;
            }
        }
        return false;
    }

    public void startRaid(Player player, Clan clan) {
        Party party = player.raidsParty;
        if (party != null) {
            for (NPC monster : party.monsters) monster.remove();
            party.monsters.clear();
        }
        Party.createParty(player);
        party = player.raidsParty;
        party.setRaidsSelected(RaidsType.CHAMBER_OF_XERICS);
        for (ClanMember member : clan.members()) {
            final Player clanMember = member.getPlayer();
            if (clanMember == null || !clanMember.isRegistered()) continue;
            final int memberRegion = clanMember.tile().region();
            if (memberRegion != 4919) continue;
            party.addMember(clanMember);
            clanMember.raidsParty = party;
            clanMember.setRaids(RaidsType.CHAMBER_OF_XERICS.equals(party.getRaidsSelected()) ? new ChamberOfXerics() : null);
        }
        player.getRaids().startup(player);
        party.setRaidStage(7);
        Tile bossRoomTile = new Tile(3232, 5724, party.getHeight());
        for (Player member : party.getMembers()) {
            member.teleport(bossRoomTile);
        }
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        Party party = player.raidsParty;
        if (party == null) {
            return false;
        }

        if (button == 12115) {
            if (party.getLeader() != player) {
                player.message("Only the leader of this party can select a raids type.");
                return true;
            }

            player.getPacketSender().sendConfig(COS_CONFIG_ID, 1);
            player.getPacketSender().sendConfig(TOB_CONFIG_ID, 0);
            player.getPacketSender().sendItemOnInterface(REWARDS_CONTAINER_ID, COX_REWARDS);
            party.setRaidsSelected(RaidsType.CHAMBER_OF_XERICS);
            player.setRaids(new ChamberOfXerics());
            refreshInterface(party.getLeader(), party);
            return true;
        }

        if (button == 12116) {
            if (party.getLeader() != player) {
                player.message("Only the leader of this party can select a raids type.");
                return true;
            }

            player.getPacketSender().sendConfig(COS_CONFIG_ID, 0);
            player.getPacketSender().sendConfig(TOB_CONFIG_ID, 1);
            player.getPacketSender().sendItemOnInterface(REWARDS_CONTAINER_ID, TOB_REWARDS);
            party.setRaidsSelected(RaidsType.THEATRE_OF_BLOOD);
            refreshInterface(party.getLeader(), party);
            return true;
        }

        if (button == 12140) {
            if (player.tile().region() != 4919) {
                player.message("You can't invite players from here.");
                return true;
            }

            player.setNameScript("Who would you like to invite?", value -> {

                String name = (String) value;
                Optional<Player> target = World.getWorld().getPlayerByName(name);

                if (target.isPresent()) {
                    if (target.get().tile().region() != 4919) {
                        player.message(Utils.formatName(name) + " is nowhere near the raids area.");
                        return false;
                    }
                    invite(player, target.get());
                } else {
                    player.message(Utils.formatName(name) + " is not online and cannot join your party.");
                    player.getInterfaceManager().closeDialogue();
                }
                return true;
            });
        }

        if (button == 12143) {
            if (player.raidsParty.getLeader() != player) {
                player.message(Color.RED.wrap("Only the leader can start this raiding party."));
                return true;
            }
            Party.startRaid(player);//Start raid
            return true;
        }
        if (button == 12146) {
            Party.leaveParty(player, true);
            return true;
        }
        if (button == 12125) {
            Party.kick(player, 1);
            return true;
        }
        if (button == 12128) {
            Party.kick(player, 2);
            return true;
        }
        if (button == 12131) {
            Party.kick(player, 3);
            return true;
        }
        if (button == 12134) {
            Party.kick(player, 4);
            return true;
        }
        return false;
    }

    public static void invite(Player p, Player target) {
        if (p.raidsParty == null) {
            p.message("You need to create a party before requesting " + target.getUsername() + " to join it.");
            return;
        }

        if (target.raidsParty != null) {
            p.message(target.getUsername() + " is already in a party.");
            return;
        }

        if (p.raidsParty.getLeader() != p) {
            p.message("You need to be the leader of your party in order to invite a player to join it.");
            return;
        }

        p.getDialogueManager().sendStatement("Requesting..");
        target.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendOption(p.getUsername() + " has invited you to join their party.", "Accept", "Decline");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        if (target.raidsParty != null) {
                            player.getDialogueManager().sendStatement(target.getUsername() + " is already in a party.");
                            return;
                        } else {
                            Party party = p.raidsParty;
                            if (party != null) {
                                if (party.getRaidStage() >= 1) {
                                    target.getInterfaceManager().close();
                                    target.message(Color.RED.wrap("Could not join party, the party had already begun."));
                                    return;
                                }
                                party.addMember(target);
                                target.raidsParty = party;
                                target.message("You've joined " + p.getUsername() + "'s raid party.");
                                sendStatement(target.getUsername() + " has joined your raid party.");
                                Party.openPartyInterface(p, true);
                                Party.openPartyInterface(target, true);
                            }
                            stop();
                        }
                    }
                    if (option == 2) {
                        sendStatement(target.getUsername() + " has declined your request to join your raid party.");
                        target.message("You decline " + p.getUsername() + "'s request to join their party.");
                        stop();
                    }
                }
            }
        });
    }
}

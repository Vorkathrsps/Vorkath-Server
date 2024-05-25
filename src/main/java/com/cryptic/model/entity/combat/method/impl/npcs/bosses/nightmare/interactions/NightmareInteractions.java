package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.interactions;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.instance.NightmareInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import java.util.ArrayList;
import java.util.Optional;

public class NightmareInteractions extends PacketInteraction {
    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (option == 1) {
            if (npc.getId() == 9461) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendStatement("You are about to begin an encounter with the Nightmare.", "Dying during this encounter will not be considered a safe death.", "Are you sure you wish to begin?");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            sendOption("Are you sure you wish to begin?", "Yes", "No");
                            setPhase(1);
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(1)) {
                            if (option == 1) {
                                player.setNightmareInstance(new NightmareInstance(player, new ArrayList<>()).build());
                                stop();
                            } else if (option == 2) {
                                stop();
                            }
                        }
                    }

                });
                return true;
            }
        } else if (option == 2) {
            if (npc.getId() == 9461) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendOption("Would you like to join an active instance?", "Yes", "No");
                        setPhase(0);
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(0)) {
                            if (option == 1) {
                                player.setNameScript("Enter the player name you'd like to join.", value -> {
                                    String name = (String) value;
                                    Optional<Player> target = World.getWorld().getPlayerByName(name);

                                    if (target.isEmpty() || target.get().getNightmareInstance() == null) {
                                        player.message(Color.DARK_RED.wrap(Utils.formatName(name) + " is not in an active instance."));
                                        stop();
                                        return false;
                                    }

                                    if (!target.get().getNightmareInstance().isJoinable()) {
                                        player.message(Color.DARK_RED.wrap("You can no longer join this instance."));
                                        stop();
                                        return false;
                                    }

                                    player.setNightmareInstance(target.get().getNightmareInstance().join(player));
                                    player.getInterfaceManager().closeDialogue();
                                    stop();
                                    return true;
                                });
                            }

                            if (option == 2) {
                                stop();
                            }
                        }
                    }
                });
            }
        }

        return false;
    }
}

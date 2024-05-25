package com.cryptic.model.content.areas.dungeons.kraken_cove;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.kraken.KrakenInstance;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.kraken.KrakenState;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenInstanceD;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_537;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_538;
import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;

/**
 * @author Origin
 * april 26, 2020
 */
public class KrakenCove extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == CREVICE_537) {
                var task_id = player.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
                var task = SlayerCreature.lookup(task_id);
                if (CombatFactory.inCombat(player)) {
                    DialogueManager.sendStatement(player, "You can't go in here when under attack.");
                    player.message("You can't go in here when under attack.");
                    return false;
                }
               // if (task == null) {
                   // player.message(Color.RED.wrap("You need a slayer task to enter the kraken's cave."));
                  //  return false;
              //  }
               /* if (!Slayer.creatureMatches(player, 494)) {
                    if (!task.matches(task_id)) {
                        player.message(Color.RED.wrap("You need a slayer task to enter the kraken's cave."));
                        return false;
                    }
                } else {
                    player.teleport(new Tile(2280, 10022));
                    return true;
                }*/
                var krakenInstance = new KrakenInstance(player, KrakenState.ALIVE);
                krakenInstance.build();
                return true;
            }
            if (obj.getId() == CREVICE_538) {
                if (player.getInstancedArea() != null) {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            SlayerTask slayerTask = World.getWorld().getSlayerTasks();
                            var assignment = slayerTask.getCurrentAssignment(player);
                          /*  if (assignment != null && !ArrayUtils.contains(assignment.getNpcs(), 494)) {
                                player.message(Color.RED.wrap("You need a slayer task to enter the kraken's cave."));
                                return;
                            }
                            if (assignment == null) {
                                player.message(Color.RED.wrap("You need a slayer task to enter the kraken's cave."));
                                return;
                            }*/
                            sendOption("Leave the instance? You cannot return.", "Yes, I want to leave.", "No, I'm staying for now.");
                            setPhase(0);
                        }

                        @Override
                        protected void select(int option) {
                            if (option == 1) {
                                player.teleport(new Tile(2280, 10016));
                                stop();
                            } else if (option == 2) {
                                stop();
                            }
                        }
                    });
                } else {
                    player.teleport(new Tile(2280, 10016));
                }
                return true;
            }
        } else if (option == 2) {
            if (obj.getId() == CREVICE_537) {
                player.getDialogueManager().start(new KrakenInstanceD());
                return true;
            }
        } else if (option == 3) {// Look inside
            if (obj.getId() == CREVICE_537) {
                int count = 0;
                for (Player p : World.getWorld().getPlayers()) {
                    if (p != null && p.tile().inArea(2269, 10023, 2302, 10046))
                        count++;
                    String strEnd = count == 1 ? "" : "s";
                    String isAre = count == 1 ? "is" : "are";
                    DialogueManager.sendStatement(player, "There " + isAre + " currently " + count + " player" + strEnd + " in the cave.");
                }
                return true;
            }
        }
        return false;
    }
}

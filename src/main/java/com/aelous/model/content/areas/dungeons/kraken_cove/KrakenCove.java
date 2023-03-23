package com.aelous.model.content.areas.dungeons.kraken_cove;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenInstanceD;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_537;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_538;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 26, 2020
 */
public class KrakenCove extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == CREVICE_537) {
                if (CombatFactory.inCombat(player)) {
                    DialogueManager.sendStatement(player, "You can't go in here when under attack.");
                    player.message("You can't go in here when under attack.");
                } else {
                    player.teleport(new Tile(2280, 10022));
                }
                return true;
            } else if (obj.getId() == CREVICE_538) {
                if (player.getInstancedArea() != null) {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION,"Leave the instance? You cannot return.", "Yes, I want to leave.", "No, I'm staying for now.");
                            setPhase(0);
                        }

                        @Override
                        protected void select(int option) {
                            if(option == 1) {
                                player.teleport(new Tile(2280, 10016));
                                stop();
                            } else if(option == 2) {
                                stop();
                            }
                        }
                    });
                } else {
                    player.teleport(new Tile(2280, 10016));
                }
                return true;
            }
        } else if(option == 2) {
            if (obj.getId() == CREVICE_537) {
                player.getDialogueManager().start(new KrakenInstanceD());
                return true;
            }
        } else if(option == 3) {// Look inside
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

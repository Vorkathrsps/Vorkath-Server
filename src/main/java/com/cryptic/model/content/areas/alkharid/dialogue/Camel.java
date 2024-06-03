package com.cryptic.model.content.areas.alkharid.dialogue;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.CAMEL;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.OLLIE_THE_CAMEL;

/**
 * @author Origin | April, 14, 2021, 18:20
 * 
 */
public class Camel extends PacketInteraction {

    private final static List<Integer> CAMELS = Arrays.asList(OLLIE_THE_CAMEL, CAMEL);

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        for(int camel : CAMELS) {
            if(npc.id() == camel) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendOption(DEFAULT_OPTION_TITLE, "Ask the camel about its dung.", "Say something unpleasant.", "Neither - I'm a polite person.");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if(isPhase(1)) {
                            sendStatement("The camel didn't seem to appreciate that question.");
                            setPhase(2);
                        } else if(isPhase(2)) {
                            stop();
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if(isPhase(0)) {
                            if(option == 1) {
                                sendPlayerChat(Expression.NODDING_THREE, "I'm sorry to bother you, but could", "you spare a little dung?");
                                setPhase(1);
                            } else if(option == 2) {
                                sendPlayerChat(Expression.NODDING_ONE, "I wonder if that camel has flees..");
                                player.message("The camel spits at you, and you jump back hurriedly.");
                                setPhase(2);
                            } else if(option == 3) {
                                player.message("You decide not to be rude.");
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
}

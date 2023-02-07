package com.aelous.model.content.items.combine;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | March, 16, 2021, 14:39
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class KrakenTentacle extends PacketInteraction {

    public static final boolean REGULAR_DROP = false;

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == ABYSSAL_WHIP || usedWith.getId() == ABYSSAL_WHIP) && (use.getId() == KRAKEN_TENTACLE || usedWith.getId() == KRAKEN_TENTACLE)) {// tent on norm whip
            makeWhip(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 2) {
            if (item.getId() == ABYSSAL_TENTACLE) {
                player.message("Nothing interesting happens...");
                return true;
            }
            return false;
        }
        if (option == 4) {
            if (item.getId() == ABYSSAL_TENTACLE) {
                if(REGULAR_DROP) {
                    return true;
                }
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.STATEMENT, "<col=7f0000>Warning!</col>", "Reverting the tentacle to a whip does NOT return the kraken tentacle, only the whip. Are you sure?");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            send(DialogueType.OPTION, "Are you sure you wish to do this?", "Yes, revert the tentacle to a abyssal whip.", "No, I'll keep my tentacle.");
                            setPhase(1);
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(1)) {
                            if (option == 1) {
                                if (player.inventory().count(ABYSSAL_TENTACLE) > 0) {
                                    // Apply inv change
                                    player.inventory().remove(new Item(ABYSSAL_TENTACLE));
                                    player.inventory().add(new Item(ABYSSAL_WHIP));
                                    // Do some nice graphics
                                    player.animate(713);
                                }
                                stop();
                            } else if (option == 2) {
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

    private void makeWhip(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, "<col=7f0000>Warning!</col>", "The tentacle will gradually consume your whip and destroy it. You", "won't be able to get the whip out again.", "The combined item is not tradeable.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, "Are you sure you wish to do this?", "Yes, let the tentacle consume the whip.", "No, I'll keep my whip.");
                    setPhase(1);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        if (player.inventory().count(ABYSSAL_WHIP) > 0 && player.inventory().count(KRAKEN_TENTACLE) > 0) {
                            // Apply inv change
                            player.inventory().remove(new Item(ABYSSAL_WHIP));
                            player.inventory().remove(new Item(KRAKEN_TENTACLE));
                            player.inventory().add(new Item(ABYSSAL_TENTACLE));
                            // Do some nice graphics
                            player.animate(713);
                        }
                        stop();
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }
}

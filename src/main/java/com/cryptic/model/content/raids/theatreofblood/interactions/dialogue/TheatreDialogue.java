package com.cryptic.model.content.raids.theatreofblood.interactions.dialogue;

import com.cryptic.model.content.raids.theatreofblood.TheatreInstance;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.utility.Color;

public class TheatreDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        if (player.getRaidParty() != null) {
            if (player.getRaidParty().getOwner() != player) {
                player.message(Color.RED.wrap("Only the Owner of the raiding party can start this raid."));
                return;
            }
        }
        sendOption("Would you like to begin this raid?", "Yes", "No");
        setPhase(0);
    }

    @Override
    protected void select(int option) {
        if (isPhase(0)) {
            if (option == 1) {
                if (player.getRaidParty() == null) {
                    player.message(Color.RED.wrap("You need to make a party before you can start this raid."));
                    stop();
                    return;
                }

                var theatreParty = player.getRaidParty();
                var players = theatreParty.getPlayers();

                if (players == null) {
                    return;
                }

                for (var p : players) {
                    if (p.tile().region() != 14642) {
                        p.getRaidParty().getOwner().message(Color.RED.wrap(p.getUsername()) + " is not currently in the raiding area.");
                        stop();
                        return;
                    }
                }

                //TODO possible just recycle theatre party .getOwner() instead of using player, seems safer.

                TheatreInstance theatreInstance = new TheatreInstance(player, players);
                player.setTheatreInstance(theatreInstance);
                player.getTheatreInstance().buildParty().startRaid();
                stop();
            } else if (option == 2) {
                stop();
            }
        }
    }
}

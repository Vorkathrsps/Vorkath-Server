package com.cryptic.model.content.raids.party.dialogue;

import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;

/**
 * @author Origin | April, 27, 2021, 10:17
 * 
 */
public class PartyDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendOption(DEFAULT_OPTION_TITLE, "Create raiding party", "Nevermind");
        setPhase(0);
    }

    @Override
    protected void select(int option) {
        if(isPhase(0)) {
            if(option == 1) {
                if(player.tile().region() != 4919) {
                    stop();
                    player.message("You can't invite players from here.");
                    return;
                }
                Party.createParty(player);
                Party.openPartyInterface(player,false);
                stop();
            }
            if(option == 2) {
                stop();
            }
        }
    }
}

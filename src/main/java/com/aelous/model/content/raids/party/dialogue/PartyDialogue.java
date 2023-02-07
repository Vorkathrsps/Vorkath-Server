package com.aelous.model.content.raids.party.dialogue;

import com.aelous.model.content.raids.party.Party;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;

/**
 * @author Patrick van Elderen | April, 27, 2021, 10:17
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class PartyDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Create raiding party", "Nevermind");
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

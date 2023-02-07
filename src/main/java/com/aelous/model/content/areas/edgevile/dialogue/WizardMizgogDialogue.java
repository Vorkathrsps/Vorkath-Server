package com.aelous.model.content.areas.edgevile.dialogue;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.ItemActionDialogue;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.model.entity.attributes.AttributeKey.RC_DIALOGUE;

/**
 * @author Patrick van Elderen | December, 16, 2020, 15:20
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WizardMizgogDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        player.putAttrib(AttributeKey.RUNECRAFTING,true);
        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Level 1-9 runecrafting", "Level 14-35 runecrafting", "Level 44-99 runecrafting", "Nevermind.");
        setPhase(0);
    }

    @Override
    protected void select(int option) {
        if(isPhase(0)) {
            if(option == 1) {
                stop();
                player.putAttrib(RC_DIALOGUE, 1);
                ItemActionDialogue.sendInterface(player, ItemIdentifiers.AIR_RUNE, ItemIdentifiers.MIND_RUNE, ItemIdentifiers.WATER_RUNE, ItemIdentifiers.EARTH_RUNE);
            } else if(option == 2) {
                stop();
                player.putAttrib(RC_DIALOGUE, 2);
                ItemActionDialogue.sendInterface(player, ItemIdentifiers.FIRE_RUNE, ItemIdentifiers.BODY_RUNE, ItemIdentifiers.COSMIC_RUNE, ItemIdentifiers.CHAOS_RUNE);
            } else if(option == 3) {
                stop();
                player.putAttrib(RC_DIALOGUE, 3);
                ItemActionDialogue.sendInterface(player, ItemIdentifiers.NATURE_RUNE, ItemIdentifiers.LAW_RUNE, ItemIdentifiers.DEATH_RUNE, ItemIdentifiers.BLOOD_RUNE);
            } else if(option == 4) {
                stop();
            }
        }
    }
}

package com.cryptic.model.content.mechanics;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.player.MagicSpellbook;

/**
 * A dialogue made for easy access to magic books.
 */
public class MagicalAltarDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendOption(DEFAULT_OPTION_TITLE, "Normal Spellbook", "Ancient Spellbook.", "Lunar Spellbook.", "Cancel");
        setPhase(1);
    }

    @Override
    public void select(int option) {
        if (isPhase(1)) {
            switch (option) {
                case 1 -> {
                    stop();
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.NORMAL, true);
                }
                case 2 -> {
                    stop();
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.ANCIENTS, true);
                }
                case 3 -> {
                    stop();
                    MagicSpellbook.changeSpellbook(player, MagicSpellbook.LUNAR, true);
                }
                case 4 -> stop();
            }
        }
    }
}

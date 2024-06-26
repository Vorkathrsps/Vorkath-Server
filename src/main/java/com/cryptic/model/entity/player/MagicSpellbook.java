package com.cryptic.model.entity.player;

import com.cryptic.interfaces.Varbits;
import com.cryptic.model.entity.combat.magic.autocasting.Autocasting;
import com.cryptic.utility.Varbit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a player's magic spellbook.
 * 
 * @author relex lawl
 */

public enum MagicSpellbook {

    NORMAL(938),
    ANCIENTS(838),
    LUNAR(29999),
    ARCEUUS(839);

    /**
     * The MagicSpellBook constructor.
     * @param interfaceId    The spellbook's interface id.
     */
    MagicSpellbook(int interfaceId) {
        this.interfaceId = interfaceId;
    }

    private static final Logger logger = LogManager.getLogger(MagicSpellbook.class);

    /**
     * The spellbook's interface id
     */
    private final int interfaceId;

    /**
     * Gets the interface to switch tab interface to.
     * @return    The interface id of said spellbook.
     */
    public int getInterfaceId() {
        return interfaceId;
    }

    /**
     * Gets the MagicSpellBook for said id.
     * @param id    The ordinal of the SpellBook to fetch.
     * @return        The MagicSpellBook who's ordinal is equal to id.
     */
    public static MagicSpellbook forId(int id) {
        for (MagicSpellbook book : MagicSpellbook.values()) {
            if (book.ordinal() == id) {
                return book;
            }
        }
        return NORMAL;
    }

    /**
     * Changes the magic spellbook for a player.
     * @param player        The player changing spellbook.
     * @param book            The new spellbook.
     */
    public static void changeSpellbook(Player player, MagicSpellbook book, boolean notify) {
        if (book == null) {
            book = NORMAL;
            logger.error("baddie", new RuntimeException("tried to set null spellbook."));
        }
        if (book == LUNAR) {
            if (player.getSkills().level(Skills.DEFENCE) < 40) {
                player.message("You need at least level 40 Defence to use the Lunar spellbook.");
                return;
            }
        }

        player.setSpellbook(book);
        Autocasting.setAutocast(player, null);

        if (notify) {
            player.message("You have changed your magic spellbook.");
        }

        if (book == MagicSpellbook.NORMAL) {
            player.varps().setVarbit(Varbit.SPELLBOOK, 0);
        } else if (book == MagicSpellbook.ANCIENTS) {
            player.varps().setVarbit(Varbit.SPELLBOOK, 1);
            player.varps().setVarbit(Varbits.DESERT_TREASURE, 15);
        } else if (book == MagicSpellbook.LUNAR) {
            player.varps().setVarbit(Varbit.SPELLBOOK, 2);
            player.varps().setVarbit(Varbits.LUNAR_DIPLOMACY, 28);
        } else if (book == MagicSpellbook.ARCEUUS) {
            player.varps().setVarbit(Varbit.SPELLBOOK, 3);
        }
    }
}

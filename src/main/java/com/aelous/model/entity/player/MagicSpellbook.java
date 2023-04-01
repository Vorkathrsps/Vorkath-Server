package com.aelous.model.entity.player;

import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a player's magic spellbook.
 * 
 * @author relex lawl
 */

public enum MagicSpellbook {

    NORMAL(938),
    ANCIENT(838),
    LUNAR(29999);

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
                //System.out.println(player.getUsername() + " needs at least level 40 Defence to use the Lunar spellbook.");
                return;
            }
        }

        //Update spellbook
        player.setSpellbook(book);

        // Reset autocast
        Autocasting.setAutocast(player, null);

        if (notify) {
            //Send notification message
            player.message("You have changed your magic spellbook.");
        }

        //Send the new spellbook interface to the client side tabs
        player.getInterfaceManager().setSidebar(6, player.getSpellbook().getInterfaceId());

        int id = player.getSpellbook().getInterfaceId();
        if (id == 29999) {
            player.getPacketSender().updateTab(2, 0);
        } else if (id == 838) {
            player.getPacketSender().updateTab(1, 0);
        } else if (id == 938) {
            player.getPacketSender().updateTab(0, 0);
        } else {
            LogManager.getLogger(MagicSpellbook.class).error("For some reason, the spellbook interface ID for " + player.getUsername() + " is " + id);
        }
    }
}

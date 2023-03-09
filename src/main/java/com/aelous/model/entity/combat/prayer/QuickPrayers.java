package com.aelous.model.entity.combat.prayer;

import com.aelous.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;

/**
 * Handles quick prayers.
 * @author Professor Oak
 */
public class QuickPrayers extends Prayers {

    /**
     * The player.
     */
    private final Player player;

    /**
     * The array holding the player's quick prayers.
     */
    private DefaultPrayerData[] prayers = new DefaultPrayerData[DefaultPrayerData.values().length];

    /**
     * Is the player currently selecting quick prayers?
     */
    private boolean selectingPrayers;

    /**
     * Are the quick prayers currently enabled?
     */
    private boolean enabled;

    /**
     * The constructor
     */
    public QuickPrayers(Player player) {
        this.player = player;
    }

    /**
     * Sends the current quick-prayer toggle-state for each prayer.
     */
    public void sendChecks() {
        for (DefaultPrayerData prayer : DefaultPrayerData.values()) {
            sendCheck(prayer);
        }
    }

    /**
     * Sends quick-prayer toggle-state for the specified prayer.
     */
    private void sendCheck(DefaultPrayerData prayer) {
        player.getPacketSender().sendConfig(CONFIG_START + prayer.ordinal(), prayers[prayer.ordinal()] != null ? 0 : 1);
    }

    /**
     *Unchecks the specified prayers but the exception.
     */
    private void uncheck(int[] toDeselect, int exception) {
        for (int i : toDeselect) {
            if (i == exception) {
                continue;
            }
            uncheck(DefaultPrayerData.values()[i]);
        }
    }

    /**
     * Unchecks the specified prayer.
     */
    private void uncheck(DefaultPrayerData prayer) {
        if (prayers[prayer.ordinal()] != null) {
            prayers[prayer.ordinal()] = null;
            sendCheck(prayer);
        }
    }

    /**
     * Handles the action for clicking a prayer.
     */
    private void toggle(int index) {
        DefaultPrayerData prayer = DefaultPrayerData.values()[index];

        //Has the player already selected this quick prayer?
        //Then reset it.
        if (prayers[prayer.ordinal()] != null) {
            uncheck(prayer);
            return;
        }

        if (!canUse(player, prayer,true)) {
            uncheck(prayer);
            return;
        }

        prayers[prayer.ordinal()] = prayer;
        sendCheck(prayer);

        switch (index) {
            case THICK_SKIN, ROCK_SKIN, STEEL_SKIN -> uncheck(DEFENCE_PRAYERS, index);
            case BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH -> {
                uncheck(STRENGTH_PRAYERS, index);
                uncheck(RANGED_PRAYERS, index);
                uncheck(MAGIC_PRAYERS, index);
            }
            case CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES -> {
                uncheck(ATTACK_PRAYERS, index);
                uncheck(RANGED_PRAYERS, index);
                uncheck(MAGIC_PRAYERS, index);
            }
            case SHARP_EYE, HAWK_EYE, EAGLE_EYE, MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT -> {
                uncheck(STRENGTH_PRAYERS, index);
                uncheck(ATTACK_PRAYERS, index);
                uncheck(RANGED_PRAYERS, index);
                uncheck(MAGIC_PRAYERS, index);
            }
            case CHIVALRY, PIETY, RIGOUR, AUGURY -> {
                uncheck(DEFENCE_PRAYERS, index);
                uncheck(STRENGTH_PRAYERS, index);
                uncheck(ATTACK_PRAYERS, index);
                uncheck(RANGED_PRAYERS, index);
                uncheck(MAGIC_PRAYERS, index);
            }
            case PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE, RETRIBUTION, REDEMPTION, SMITE -> uncheck(OVERHEAD_PRAYERS, index);
        }
    }


    /**
     * Checks if the player has manually turned off
     * any of the quick prayers.
     * If all quick prayers are turned off,
     * disable them completely.
     */
    public void checkActive() {
        if (enabled) {
            for (DefaultPrayerData prayer : prayers) {
                if (prayer == null)
                    continue;
                if (usingPrayer(player, prayer.ordinal())) {
                    return;
                }
            }
            enabled = false;
            player.getPacketSender().sendQuickPrayersState(false);
        }
    }

    /**
     * Handles an incoming button.
     * Check if it's related to quick prayers.
     */
    public boolean handleButton(int button) {
        switch(button) {
        case TOGGLE_QUICK_PRAYERS:

            if (player.getSkills().level(Skills.PRAYER) <= 0) {
                player.message("You don't have enough Prayer points.");
                return true;
            }
            if (enabled) {
                for (DefaultPrayerData prayer : prayers) {
                    if (prayer == null)
                        continue;

                    //Deactivate quick prayers
                    deactivatePrayer(player, prayer.ordinal());

                    //But in OSRS it resets ALL prayers, even the non quick prayers.
                    Prayers.closeAllPrayers(player);
                }
                enabled = false;
            } else {
                boolean found = false;
                for (DefaultPrayerData prayer : prayers) {
                    if (prayer == null)
                        continue;
                    activatePrayer(player, prayer.ordinal());
                    found = true;
                }

                if (!found) {
                    player.message("You have not setup any quick-prayers yet.");
                }

                enabled = found;
            }

            player.getPacketSender().sendQuickPrayersState(enabled);
            break;

        case SETUP_BUTTON:
            if (selectingPrayers) {
                player.getInterfaceManager().setSidebar(5, 5608);
                player.getPacketSender().sendTab(5);
                selectingPrayers = false;
            } else {
                sendChecks();
                player.getInterfaceManager().setSidebar(5, QUICK_PRAYERS_TAB_INTERFACE_ID);
                player.getPacketSender().sendTab(5);
                selectingPrayers = true;
            }
            break;

        case CONFIRM_BUTTON:
            if (selectingPrayers) {
                player.getInterfaceManager().setSidebar(5, 5608);
                selectingPrayers = false;
            }
            break;
        }

        //Clicking prayers
        if (button >= 17202 && button <= 17230) {
            if (selectingPrayers) {
                final int index = button - 17202;
                toggle(index);
            }
            return true;
        }

        return false;
    }

    /**
     * Sets enabled state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the selected quick prayers
     */
    public DefaultPrayerData[] getPrayers() {
        return prayers;
    }

    /**
     * Sets the selected quick prayers
     */
    public void setPrayers(DefaultPrayerData[] prayers) {
        this.prayers = prayers;
    }

    /**
     * Toggle button
     */
    private static final int TOGGLE_QUICK_PRAYERS = 1500;

    /**
     * The button for starting to setup quick prayers.
     */
    private static final int SETUP_BUTTON = 1506;

    /**
     * The confirmation button in the interface.
     */
    private static final int CONFIRM_BUTTON = 17232;

    /**
     * The actual main interface id.
     */
    private static final int QUICK_PRAYERS_TAB_INTERFACE_ID = 17200;

    /**
     * The interface config buttons start.
     */
    private static final int CONFIG_START = 620;
}

package com.aelous.model.entity.combat.prayer.default_prayer;

import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.timers.TimerKey;
import org.jetbrains.annotations.Nullable;

/**
 * All of the prayers that can be activated and deactivated. This currently only
 * has support for prayers present in the <b>317 protocol</b>.
 *
 * @author Swiffy
 */
public class Prayers {

    public static boolean overheadPrayerActivated(Player player) {
        return usingPrayer(player, PROTECT_FROM_MAGIC) || usingPrayer(player, PROTECT_FROM_MISSILES) || usingPrayer(player, PROTECT_FROM_MELEE) || usingPrayer(player, RETRIBUTION) || usingPrayer(player, REDEMPTION) || usingPrayer(player, SMITE);
    }

    private static final int CANNOT_USE = 447;
    private static final int TURN_OFF_AND_ON = 435;

    /**
     * Gets the protecting prayer based on the argued combat type.
     *
     * @param type the combat type.
     * @return the protecting prayer.
     */
    public static int getProtectingPrayer(@Nullable CombatType type) {
        if (type == null) return -1;
        return switch (type) {
            case MELEE -> PROTECT_FROM_MELEE;
            case MAGIC -> PROTECT_FROM_MAGIC;
            case RANGED -> PROTECT_FROM_MISSILES;
        };
    }

    public static boolean usingPrayer(Entity mob, int prayer) {
        if (prayer < 0 || prayer >= mob.getPrayerActive().length)
            return false;
        return mob.getPrayerActive()[prayer];
    }

    /**
     * Activates a prayer with specified <code>buttonId</code>.
     *
     * @param buttonId The button the player is clicking.
     */
    public static boolean togglePrayer(Player player, final int buttonId) {
        DefaultPrayerData defaultPrayerData = DefaultPrayerData.getActionButton().get(buttonId);
        if (defaultPrayerData != null) {
            if (!player.getPrayerActive()[defaultPrayerData.ordinal()])
                activatePrayer(player, defaultPrayerData.ordinal());
            else
                deactivatePrayer(player, defaultPrayerData.ordinal());
            return true;
        }
        return false;
    }

    /**
     * Activates said prayer with specified <code>prayerId</code> and de-activates
     * all non-stackable prayers.
     *
     * @param entity   The player activating prayer.
     * @param prayerId The id of the prayer being turned on, also known as the ordinal in the respective enum.
     */
    public static void activatePrayer(Entity entity, final int prayerId) {

        //Get the prayer data.
        DefaultPrayerData pd = DefaultPrayerData.getPrayerData().get(prayerId);

        //Check if it's availble
        if (pd == null) {
            return;
        }
        //System.out.println("Prayer data is not null.");
        //Check if we're already praying this prayer.
        if (entity.getPrayerActive()[prayerId]) {
            return;
        }

        //If we're a player, make sure we can use this prayer.
        if (entity.isPlayer()) {
            Player player = entity.getAsPlayer();
            if (player.getSkills().level(Skills.PRAYER) <= 0) {
                player.getPacketSender().sendConfig(pd.getConfigId(), 0);
                player.message("You do not have enough Prayer points.");
                return;
            }
            if (!canUse(player, pd, true)) {
                return;
            }
            if (pd.soundId != -1)
                player.playSound(pd.soundId);
        }

        switch (prayerId) {
            case THICK_SKIN, ROCK_SKIN, STEEL_SKIN -> resetPrayers(entity, DEFENCE_PRAYERS, prayerId);
            case BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH -> {
                resetPrayers(entity, STRENGTH_PRAYERS, prayerId);
                resetPrayers(entity, RANGED_PRAYERS, prayerId);
                resetPrayers(entity, MAGIC_PRAYERS, prayerId);
            }
            case CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES -> {
                resetPrayers(entity, ATTACK_PRAYERS, prayerId);
                resetPrayers(entity, RANGED_PRAYERS, prayerId);
                resetPrayers(entity, MAGIC_PRAYERS, prayerId);
            }
            case SHARP_EYE, HAWK_EYE, EAGLE_EYE, MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT -> {
                resetPrayers(entity, STRENGTH_PRAYERS, prayerId);
                resetPrayers(entity, ATTACK_PRAYERS, prayerId);
                resetPrayers(entity, RANGED_PRAYERS, prayerId);
                resetPrayers(entity, MAGIC_PRAYERS, prayerId);
            }
            case CHIVALRY, PIETY, RIGOUR, AUGURY -> {
                resetPrayers(entity, DEFENCE_PRAYERS, prayerId);
                resetPrayers(entity, STRENGTH_PRAYERS, prayerId);
                resetPrayers(entity, ATTACK_PRAYERS, prayerId);
                resetPrayers(entity, RANGED_PRAYERS, prayerId);
                resetPrayers(entity, MAGIC_PRAYERS, prayerId);
            }
            case PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE ->
                resetPrayers(entity, OVERHEAD_PRAYERS, prayerId);
            case RETRIBUTION, REDEMPTION, SMITE -> resetPrayers(entity, OVERHEAD_PRAYERS, prayerId);
        }

        // No prayers currently active and we're gonna turn one on, note the tick we're starting prayer.
        if (entity.isPlayer() && hasNoPrayerOn(entity.getAsPlayer())) {
            entity.getAsPlayer().putAttrib(AttributeKey.PRAYER_ON_TICK, World.getWorld().cycleCount());
        }

        entity.setPrayerActive(prayerId, true);

        if (entity.isPlayer()) {
            Player p = entity.getAsPlayer();
            p.getPacketSender().sendConfig(pd.getConfigId(), 1);
            if (pd.getHint() != -1) {
                int hintId = getPrayerHeadIcon(entity);
                p.setHeadHint(hintId);
            }
        }
    }

    /**
     * Checks if the player can use the specified prayer.
     */
    public static boolean canUse(Player player, DefaultPrayerData prayer, boolean msg) {
        if (player.hp() < 0 || player.locked()) {
            player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
            return false;
        }
        if (player.getSkills().xpLevel(Skills.PRAYER) < (prayer.getRequirement())) {
            if (msg) {
                player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
                player.playSound(CANNOT_USE);
                player.message("You need a Prayer level of at least " + prayer.getRequirement() + " to use " + prayer.getPrayerName() + ".");
            }
            return false;
        }
        if (prayer == DefaultPrayerData.CHIVALRY && player.getSkills().xpLevel(Skills.DEFENCE) < 60) {
            if (msg) {
                player.playSound(CANNOT_USE);
                player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
                player.message("You need a Defence level of at least 60 to use Chivalry.");
            }
            return false;
        }
        if (prayer == DefaultPrayerData.PIETY && player.getSkills().xpLevel(Skills.DEFENCE) < 70) {
            if (msg) {
                player.playSound(CANNOT_USE);
                player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
                player.message("You need a Defence level of at least 70 to use Piety.");
            }
            return false;
        }
        if ((prayer == DefaultPrayerData.RIGOUR || prayer == DefaultPrayerData.AUGURY) && player.getSkills().xpLevel(Skills.DEFENCE) < 70) {
            if (msg) {
                player.playSound(CANNOT_USE);
                player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
                player.message("You need a Defence level of at least 70 to use that prayer.");
            }
            return false;
        }
        if (prayer == DefaultPrayerData.PROTECT_ITEM) {
            if (player.getIronManStatus() == IronMode.ULTIMATE) {
                if (msg) {
                    player.playSound(CANNOT_USE);
                    player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
                    player.message("As an Ultimate Iron Man, you cannot use the protect item prayer.");
                }
                return false;
            }

            if (Skulling.skulled(player) && player.getSkullType() == SkullType.RED_SKULL) {
                if (msg) {
                    player.playSound(CANNOT_USE);
                    player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
                    DialogueManager.sendStatement(player, "You cannot use the Protect Item prayer with a red skull!");
                }
                return false;
            }
        }

        if (player.getTimers().has(TimerKey.OVERHEADS_BLOCKED)) {
            if (prayer == DefaultPrayerData.PROTECT_FROM_MELEE || prayer == DefaultPrayerData.PROTECT_FROM_MISSILES || prayer == DefaultPrayerData.PROTECT_FROM_MAGIC) {
                if (msg) {
                    player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
                    player.playSound(CANNOT_USE);
                    player.message("You cannot use overhead prayers right now.");
                }
                return false;
            }
        }

        //Prayer locks
        boolean locked = false;

        boolean preserve_unlocked = player.getAttribOr(AttributeKey.PRESERVE, false);
        boolean rigour_unlocked = player.getAttribOr(AttributeKey.RIGOUR, false);
        boolean augury_unlocked = player.getAttribOr(AttributeKey.AUGURY, false);

        if (prayer == DefaultPrayerData.PRESERVE && !preserve_unlocked
            || prayer == DefaultPrayerData.RIGOUR && !rigour_unlocked
            || prayer == DefaultPrayerData.AUGURY && !augury_unlocked) {
            locked = true;
        }

        if (locked) {
            if (msg) {
                player.message("You have not unlocked that Prayer yet.");
                player.playSound(CANNOT_USE);
                player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
            }
            return false;
        }

        //Duel, disabled prayer?
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_PRAYER.ordinal()]) {
            if (msg) {
                DialogueManager.sendStatement(player, "Prayer has been disabled in this duel!");
                player.playSound(CANNOT_USE);
                player.getPacketSender().sendConfig(prayer.getConfigId(), 0);
            }
            return false;
        }

        return true;
    }

    /**
     * Deactivates said prayer with specified <code>prayerId</code>.
     *
     * @param mob      The player deactivating prayer.
     * @param prayerId The id of the prayer being deactivated.
     */
    public static void deactivatePrayer(Entity mob, int prayerId) {
        if (!mob.getPrayerActive()[prayerId]) {
            return;
        }
        DefaultPrayerData pd = DefaultPrayerData.getPrayerData().get(prayerId);
        mob.getPrayerActive()[prayerId] = false;
        if (mob.isPlayer()) {
            Player p = mob.getAsPlayer();
            p.getPacketSender().sendConfig(pd.getConfigId(), 0);
            if (pd.getHint() != -1) {
                int hintId = getPrayerHeadIcon(mob);
                p.setHeadHint(hintId);
            }

            p.getQuickPrayers().checkActive();
        }
    }

    public static void closeAllPrayers(Entity mob) {
        for (DefaultPrayerData prayer : DefaultPrayerData.values()) {
            mob.getPrayerActive()[prayer.ordinal()] = false;
            mob.setHeadHint(getPrayerHeadIcon(mob));
            if (mob.isPlayer()) {
                mob.getAsPlayer().getPacketSender().sendConfig(prayer.getConfigId(), 0);
            }
        }
        if (mob.isPlayer()) {
            mob.getAsPlayer().getQuickPrayers().setEnabled(false);
            mob.getAsPlayer().getPacketSender().sendQuickPrayersState(false);
        }
    }

    /**
     * Gets the player's current head hint if they activate or deactivate
     * a head prayer.
     *
     * @param mob The player to fetch head hint index for.
     * @return The player's current head hint index.
     */
    private static int getPrayerHeadIcon(Entity mob) {
        boolean[] prayers = mob.getPrayerActive();
        if (prayers[PROTECT_FROM_MELEE])
            return 0;
        if (prayers[PROTECT_FROM_MISSILES])
            return 1;
        if (prayers[PROTECT_FROM_MAGIC])
            return 2;
        if (prayers[RETRIBUTION])
            return 3;
        if (prayers[SMITE])
            return 4;
        if (prayers[REDEMPTION])
            return 5;
        return -1;
    }

    public static double compute(Player player) {
        double rate = 0;

        if (usingPrayer(player,THICK_SKIN))
            rate += 0.083;
        if (usingPrayer(player,BURST_OF_STRENGTH))
            rate += 0.083;
        if (usingPrayer(player,CLARITY_OF_THOUGHT))
            rate += 0.083;
        if (usingPrayer(player,SHARP_EYE))
            rate += 0.083;
        if (usingPrayer(player,MYSTIC_WILL))
            rate += 0.083;
        if (usingPrayer(player,ROCK_SKIN))
            rate += 0.086;
        if (usingPrayer(player,SUPERHUMAN_STRENGTH))
            rate += 0.086;
        if (usingPrayer(player,IMPROVED_REFLEXES))
            rate += 0.086;
        if (usingPrayer(player,RAPID_RESTORE))
            rate += 0.081;
        if (usingPrayer(player,RAPID_HEAL))
            rate += 0.082;
        if (usingPrayer(player,PROTECT_ITEM))
            rate += 0.082;
        if (usingPrayer(player,HAWK_EYE))
            rate += 0.086;
        if (usingPrayer(player,MYSTIC_LORE))
            rate += 0.086;
        if (usingPrayer(player,STEEL_SKIN))
            rate += 0.0912;
        if (usingPrayer(player,ULTIMATE_STRENGTH))
            rate += 0.33;
        if (usingPrayer(player,INCREDIBLE_REFLEXES))
            rate += 0.33;
        if (usingPrayer(player,PROTECT_FROM_MELEE))
            rate += 0.33;
        if (usingPrayer(player,PROTECT_FROM_MAGIC))
            rate += 0.33;
        if (usingPrayer(player,PROTECT_FROM_MISSILES))
            rate += 0.33;
        if (usingPrayer(player,EAGLE_EYE))
            rate += 0.33;
        if (usingPrayer(player,MYSTIC_MIGHT))
            rate += 0.33;
        if (usingPrayer(player,RETRIBUTION))
            rate += 0.083;
        if (usingPrayer(player,REDEMPTION))
            rate += 0.086;
        if (usingPrayer(player,SMITE))
            rate += 0.56;
        if (usingPrayer(player,PRESERVE))
            rate += 0.082;
        if (usingPrayer(player,CHIVALRY))
            rate += 0.50;
        if (usingPrayer(player,PIETY))
            rate += 0.50;
        if (usingPrayer(player,RIGOUR))
            rate += 0.50;
        if (usingPrayer(player,AUGURY))
            rate += 0.50;

        //System.out.println("rate: "+rate);
        return rate;
    }

    public static void onLogin(Player player) {
        player.getTimers().addOrSet(TimerKey.PRAYER_TICK, 1); // Drain 1 tick later.
    }

    public static void drainPrayer(Player player) {
        // Drain this tick, and reset the timer to 1 to drain next tick. Forever and ever and evvvvvvvver
        player.getTimers().extendOrRegister(TimerKey.PRAYER_TICK, 1);

        if (player.getTimers().has(TimerKey.PRAYER_TICK)) {

            // Dont drain if dead, or no prayers on.
            if (player.dead() || hasNoPrayerOn(player) ||
                World.getWorld().cycleCount() <= player.<Integer>getAttribOr(AttributeKey.PRAYER_ON_TICK, 0)) {
                player.putAttrib(AttributeKey.PRAYERINCREMENT, 0D); // reset
                return;
            }
            //player.message(String.format("on:%s now:%s drain:%s", player.<Integer>getAttrib(AttributeKey.PRAYER_ON_TICK), World.getWorld().getElapsedTicks(), player.<Integer>getAttrib(AttributeKey.PRAYERINCREMENT)));
            int pray = EquipmentInfo.prayerBonuses(player);
            double drain = compute(player);
            if (drain > 0) {
                //player.debugMessage(String.format("drain: %f  bonus:%d  saved:%f", drain, pray, pray < 1 ? 0.0 : (drain / (1 + (0.0333 * pray)))));
                drain = (0.6 * (pray / drain));
                if (pray > 0) {
                    drain = (2 + (pray * 0.6)) / drain;
                }
                //player.debugMessage(String.format("drain: %f  bonus:%d  saved:%f", drain, pray, pray < 1 ? 0.0 : (drain / (1 + (0.0333 * pray)))));

                if (player.getSkills().level(Skills.PRAYER) > 0) {
                    boolean inf_pray = player.getAttribOr(AttributeKey.INF_PRAY, false);
                    if (!inf_pray) {
                        double totalDrains = player.getAttribOr(AttributeKey.PRAYERINCREMENT, 0.0D);
                        player.putAttrib(AttributeKey.PRAYERINCREMENT, totalDrains + drain);
                        if (totalDrains > 1.0) {
                            player.putAttrib(AttributeKey.PRAYERINCREMENT, totalDrains - 1);
                            player.getSkills().setLevel(Skills.PRAYER, Math.max(0, player.getSkills().level(Skills.PRAYER) - 1));

                        }
                    }
                }

                if (player.getSkills().level(Skills.PRAYER) < 1) {
                    // Cant get smited when dead dead, you must be smited before like RS.
                    closeAllPrayers(player);
                    player.message("You have run out of prayer points, you must recharge at an altar.");
                }
            }
        }
    }

    public static boolean hasNoPrayerOn(Player player) {
        int prayersOn = 0;
        for (int i = 0; i < player.getPrayerActive().length; i++) {
            if (player.getPrayerActive()[i])
                prayersOn++;
        }
        return prayersOn == 0;
    }

    /**
     * Resets <code> prayers </code> with an exception for <code> prayerID </code>
     *
     * @param prayers  The array of prayers to reset
     * @param prayerID The prayer ID to not turn off (exception)
     */
    public static void resetPrayers(Entity mob, int[] prayers, int prayerID) {
        for (int prayer : prayers) {
            if (prayer != prayerID)
                deactivatePrayer(mob, prayer);
        }
    }

    /**
     * Resets prayers in the array
     */
    public static void resetPrayers(Entity player, int[] prayers) {
        for (int prayer : prayers) {
            deactivatePrayer(player, prayer);
        }
    }

    /**
     * Checks if action button ID is a prayer button.
     *
     * @param actionButtonID action button being hit.
     */
    public static boolean isButton(final int actionButtonID) {
        return DefaultPrayerData.getActionButton().containsKey(actionButtonID);
    }

    public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4,
        ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8, RAPID_HEAL = 9,
        PROTECT_ITEM = 10, HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14,
        INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17,
        PROTECT_FROM_MELEE = 18, EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, PRESERVE = 24,
        CHIVALRY = 25, PIETY = 26, RIGOUR = 27, AUGURY = 28;

    /**
     * Contains every prayer that counts as a defense prayer.
     */
    public static final int[] DEFENCE_PRAYERS = {THICK_SKIN, ROCK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY};

    /**
     * Contains every prayer that counts as a strength prayer.
     */
    public static final int[] STRENGTH_PRAYERS = {BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CHIVALRY, PIETY};

    /**
     * Contains every prayer that counts as an attack prayer.
     */
    public static final int[] ATTACK_PRAYERS = {CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY, PIETY};

    /**
     * Contains every prayer that counts as a ranged prayer.
     */
    public static final int[] RANGED_PRAYERS = {SHARP_EYE, HAWK_EYE, EAGLE_EYE, RIGOUR};

    /**
     * Contains every prayer that counts as a magic prayer.
     */
    public static final int[] MAGIC_PRAYERS = {MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT, AUGURY};

    /**
     * Contains every prayer that counts as an overhead prayer, excluding protect from summoning.
     */
    public static final int[] OVERHEAD_PRAYERS = {PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE, RETRIBUTION, REDEMPTION, SMITE};

    /**
     * Contains every protection prayer
     */
    public static final int[] PROTECTION_PRAYERS = {PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE};
}

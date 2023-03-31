package com.aelous.model.entity.combat;

import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.content.packet_actions.GlobalStrings;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.RestoreSpecialAttackTask;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.method.impl.specials.magic.EldritchNMS;
import com.aelous.model.entity.combat.method.impl.specials.magic.VoidWaker;
import com.aelous.model.entity.combat.method.impl.specials.magic.VolatileNMS;
import com.aelous.model.entity.combat.method.impl.specials.melee.*;
import com.aelous.model.entity.combat.method.impl.specials.range.*;
import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

import java.util.Arrays;

import static com.aelous.utility.ItemIdentifiers.TOXIC_STAFF_UNCHARGED;

/**
 * Holds constants that hold data for all of the special attacks that can be
 * used.
 *
 * @author lare96
 */
public enum CombatSpecial {

    //Range
    MAGIC_SHORTBOW(new int[]{ItemIdentifiers.MAGIC_SHORTBOW, ItemIdentifiers.MAGIC_SHORTBOW_I}, 55, 1.0, 1.2, new MagicShortbow(), WeaponType.BOW),
    WEBWEAVER_BOW(new int[]{ItemIdentifiers.WEBWEAVER_BOW}, 50, 1.0, 1.5, new WebWeaverBow(), WeaponType.BOW),
    DARK_BOW(new int[]{ItemIdentifiers.DARK_BOW, ItemIdentifiers.DARK_BOW_12765, ItemIdentifiers.DARK_BOW_12766, ItemIdentifiers.DARK_BOW_12767, ItemIdentifiers.DARK_BOW_12768, ItemIdentifiers.DARK_BOW_20408}, 55, 1.30, 1.24, new DarkBow(), WeaponType.BOW),
    ARMADYL_CROSSBOW(new int[]{ItemIdentifiers.ARMADYL_CROSSBOW}, 40, 1.10, 2.0, new ArmadylCrossbow(), WeaponType.CROSSBOW),
    ZARYTE_CROSSBOW(new int[]{ItemIdentifiers.ZARYTE_CROSSBOW}, 75, 1.10, 2.0, new ZaryteCrossbow(), WeaponType.CROSSBOW),
    ZARYTE_I(new int[]{ItemIdentifiers.ZARYTE_CROSSBOW}, 50, 1.40, 5.0, new ZaryteCrossbow(), WeaponType.CROSSBOW),
    DRAGON_CROSSBOW(new int[]{ItemIdentifiers.DRAGON_CROSSBOW}, 60, 1.30, 2.0, new DragonCrossbow(), WeaponType.CROSSBOW),
    BALLISTA(new int[]{ItemIdentifiers.LIGHT_BALLISTA, ItemIdentifiers.HEAVY_BALLISTA, ItemIdentifiers.HEAVY_BALLISTA_23630}, 65, 1.25, 1.25, new Ballista(), WeaponType.THROWN),
    DRAGON_THROWNAXE(new int[]{ItemIdentifiers.DRAGON_THROWNAXE, ItemIdentifiers.DRAGON_THROWNAXE_21207}, 25, 1.0, 1.0, new DragonThrownaxe(), WeaponType.THROWN),
    DRAGON_KNIFE(new int[]{ItemIdentifiers.DRAGON_KNIFE, ItemIdentifiers.DRAGON_KNIFEP, ItemIdentifiers.DRAGON_KNIFEP_22808, ItemIdentifiers.DRAGON_KNIFEP_22810}, 25, 1.0, 1.0, new DragonKnife(), WeaponType.THROWN),
    TOXIC_BLOWPIPE(new int[]{ItemIdentifiers.TOXIC_BLOWPIPE}, 50, 1.50, 1.50, new ToxicBlowpipeSpecialAttack(), WeaponType.THROWN),
    MORRIGANS_THROWING_AXE(new int[]{ItemIdentifiers.MORRIGANS_THROWING_AXE}, 50, 1.20, 1.0, new MorrigansThrowingAxe(), WeaponType.THROWN),
    MORRIGANS_JAVALIN(new int[]{ItemIdentifiers.MORRIGANS_JAVELIN_23619, ItemIdentifiers.MORRIGANS_JAVELIN}, 50, 1.0, 1.0, new MorrigansJavelin(), WeaponType.THROWN),

    //Magic
    STAFF_OF_THE_DEAD(new int[]{ItemIdentifiers.STAFF_OF_THE_DEAD}, 100, 1.0, 1.0, null, WeaponType.MAGIC_STAFF),
    TOXIC_STAFF_OF_THE_DEAD(new int[]{ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD}, 100, 1.0, 1.0, null, WeaponType.MAGIC_STAFF),
    STAFF_OF_LIGHT(new int[]{ItemIdentifiers.STAFF_OF_LIGHT}, 100, 1.0, 1.0, null, WeaponType.MAGIC_STAFF),
    VOLATILE_NIGHTMARE_STAFF(new int[]{ItemIdentifiers.VOLATILE_NIGHTMARE_STAFF}, 55, 1.0, 1.50, new VolatileNMS(), WeaponType.MAGIC_STAFF),
    ELDRITCH_NIGHTMARE_STAFF(new int[]{ItemIdentifiers.ELDRITCH_NIGHTMARE_STAFF}, 75, 1.0, 1.0, new EldritchNMS(), WeaponType.MAGIC_STAFF),

    VOIDWAKER(new int[]{ItemIdentifiers.VOIDWAKER}, 50,  1.0, 1.0, new VoidWaker(), WeaponType.LONGSWORD),

    DRAGON_BATTLEAXE(new int[]{ItemIdentifiers.DRAGON_BATTLEAXE}, 100, 1.0, 1.0, null, WeaponType.AXE),
    BARRELCHEST_ANCHOR(new int[]{ItemIdentifiers.BARRELCHEST_ANCHOR}, 50, 1.0, 1.10F, new BarrelchestAnchor(), WeaponType.MACE),
    GRANITE_MAUL(new int[]{ItemIdentifiers.GRANITE_MAUL, ItemIdentifiers.GRANITE_MAUL_12848, ItemIdentifiers.GRANITE_MAUL_24225}, 50, 1.0, 1.10F, new GraniteMaul(), WeaponType.HAMMER),
    DRAGON_LONGSWORD(new int[]{ItemIdentifiers.DRAGON_LONGSWORD}, 25, 1.25D, 1.25F, new DragonLongsword(), WeaponType.LONGSWORD),
    DRAGON_DAGGER(new int[]{ItemIdentifiers.DRAGON_DAGGER, ItemIdentifiers.DRAGON_DAGGERP, ItemIdentifiers.DRAGON_DAGGERP_5680, ItemIdentifiers.DRAGON_DAGGERP_5698, ItemIdentifiers.DRAGON_DAGGER_20407}, 25, 1.15, 1.15, new DragonDagger(), WeaponType.DAGGER),
    ABYSSAL_DAGGER(new int[]{ItemIdentifiers.ABYSSAL_DAGGER, ItemIdentifiers.ABYSSAL_DAGGER_P, ItemIdentifiers.ABYSSAL_DAGGER_P_13269, ItemIdentifiers.ABYSSAL_DAGGER_P_13271}, 50, 0.85, 1.25, new AbyssalDagger(), WeaponType.DAGGER),
    ARMADYL_GODSWORD(new int[]{ItemIdentifiers.ARMADYL_GODSWORD, 20593}, 50, 1.25D * 1.10F, 1.50F, new ArmadylGodsword(), WeaponType.TWOHANDED),
    ANCIENT_GODSWORD(new int[]{ItemIdentifiers.ANCIENT_GODSWORD}, 50, 1.10D, 1.50F, new AncientGodsword(), WeaponType.TWOHANDED),
    BANDOS_GODSWORD(new int[]{ItemIdentifiers.BANDOS_GODSWORD, ItemIdentifiers.BANDOS_GODSWORD_OR}, 50, 1.20D, 1.0, new BandosGodsword(), WeaponType.TWOHANDED),
    SARADOMIN_GODSWORD(new int[]{ItemIdentifiers.SARADOMIN_GODSWORD, ItemIdentifiers.SARADOMIN_GODSWORD_OR}, 50, 1.10D, 1.1, new SaradominGodsword(), WeaponType.TWOHANDED),
    ZAMORAK_GODSWORD(new int[]{ItemIdentifiers.ZAMORAK_GODSWORD, ItemIdentifiers.ZAMORAK_GODSWORD_OR}, 50, 1.10D, 1.1, new ZamorakGodsword(), WeaponType.TWOHANDED),
    DRAGON_SCIMITAR(new int[]{ItemIdentifiers.DRAGON_SCIMITAR, ItemIdentifiers.DRAGON_SCIMITAR_OR, ItemIdentifiers.DRAGON_SCIMITAR_20406}, 55, 1.0, 1.25F, new DragonScimitar(), WeaponType.LONGSWORD),

    DRAGON_SPEAR(new int[]{ItemIdentifiers.DRAGON_SPEAR, ItemIdentifiers.DRAGON_SPEARP, ItemIdentifiers.DRAGON_SPEARP_5716, ItemIdentifiers.DRAGON_SPEARP_5730}, 25, 1.0, 1.0, new DragonSpear(), WeaponType.SPEAR),
    ZAMORAKIAN_SPEAR(new int[]{ItemIdentifiers.ZAMORAKIAN_SPEAR}, 25, 1.0, 1.0, new ZamorakianSpear(), WeaponType.SPEAR),
    ABYSSAL_TENTACLE(new int[]{ItemIdentifiers.ABYSSAL_TENTACLE}, 50, 1.0, 1.0, new AbyssalTentacle(), WeaponType.WHIP),
    ABYSSAL_WHIP(new int[]{ItemIdentifiers.ABYSSAL_WHIP, ItemIdentifiers.VOLCANIC_ABYSSAL_WHIP, ItemIdentifiers.FROZEN_ABYSSAL_WHIP}, 50, 1.0, 1.25F, new AbyssalWhip(), WeaponType.WHIP),
    BLESSED_SARADOMIN_SWORD(new int[]{ItemIdentifiers.SARADOMINS_BLESSED_SWORD, ItemIdentifiers.SARAS_BLESSED_SWORD_FULL}, 65, 1.25, 1.0, new SaradominBlessedSword(), WeaponType.TWOHANDED),
    SARADOMIN_SWORD(new int[]{ItemIdentifiers.SARADOMIN_SWORD}, 100, 1.0, 1.0, new SaradominSword(), WeaponType.TWOHANDED),
    ANCIENT_MACE(new int[]{ItemIdentifiers.ANCIENT_MACE}, 100, 1.0, 1.0, new AncientMace(), WeaponType.MACE),
    DRAGON_2H_SWORD(new int[]{ItemIdentifiers.DRAGON_2H_SWORD}, 60, 1.0, 1.0, new Dragon2HSword(), WeaponType.TWOHANDED),
    BARRELSCHEST_ANCHOR(new int[]{ItemIdentifiers.BARRELCHEST_ANCHOR}, 50, 1.10, 1.10, new BarrelchestAnchor(), WeaponType.HAMMER),
    DRAGON_MACE(new int[]{ItemIdentifiers.DRAGON_MACE, 16254, 16255, 16256, 16257, 16258, 16259, 16260, 16261, 16262}, 25, 1.50D, 1.25F, new DragonMace(), WeaponType.MACE),
    INQUISITORS_MACE(new int[]{ItemIdentifiers.INQUISITORS_MACE}, 50, 1.10, 1.25, new InquisitorsMace(), WeaponType.MACE),
    URSINE_CHAINMACE(new int[]{ItemIdentifiers.URSINE_CHAINMACE}, 50, 1.0, 2.0, new UrsineMace(), WeaponType.MACE),
    DRAGON_WARHAMMER(new int[]{ItemIdentifiers.DRAGON_WARHAMMER, ItemIdentifiers.DRAGON_WARHAMMER_20785}, 50, 1.50D, 1.50F, new DragonWarhammer(), WeaponType.HAMMER),
    STATIUS_WARHAMMER(new int[]{ItemIdentifiers.STATIUSS_WARHAMMER, ItemIdentifiers.STATIUSS_WARHAMMER_23620}, 35, 1.25D, 1.50F, new StatiusWarhammer(), WeaponType.HAMMER),
    DRAGON_CLAWS(new int[]{ItemIdentifiers.DRAGON_CLAWS}, 50, 1.25, 3.00, new DragonClaws(), WeaponType.CLAWS),
    DRAGON_HALBERD(new int[]{ItemIdentifiers.DRAGON_HALBERD}, 30, 1.10, 1.35, new DragonHalberd(), WeaponType.HALBERD),
    CRYSTAL_HALBERD(new int[]{13080, 13081, 13082, 13083, 13084, 13085, 13086, 13087, 13088, 13089, 13090, 13091, 13092, 13093, 13094, 13095, 13096, 13097, 13098, 13099, 13100, 13101}, 30, 1.10, 1.35, new CrystalHalberd(), WeaponType.HALBERD),
    ABYSSAL_BLUDGEON(new int[]{ItemIdentifiers.ABYSSAL_BLUDGEON}, 50, 1.0, 1.0, new AbyssalBludgeon(), WeaponType.HAMMER),
    DRAGON_SWORD(new int[]{ItemIdentifiers.DRAGON_SWORD}, 40, 1.25D, 1.25F, new DragonSword(), WeaponType.SWORD),
    DINHS_BULWARK(new int[]{ItemIdentifiers.DINHS_BULWARK}, 50, 1.0, 2.0F, new DinhsBulwark(), WeaponType.DINHS_BULWARK),
    GRANITE_HAMMER(new int[]{ItemIdentifiers.GRANITE_HAMMER}, 60, 1.0, 1.50F, new GraniteHammer(), WeaponType.HAMMER),
    VESTAS_BLIGHTED_LONGSWORD(new int[]{ItemIdentifiers.VESTAS_BLIGHTED_LONGSWORD}, 25, 1.20D, 2.50F, new VestaLongsword(), WeaponType.LONGSWORD),
    VESTA_SPEAR(new int[]{ItemIdentifiers.VESTAS_SPEAR}, 50, 1.20D, 1.50F, new VestaSpear(), WeaponType.SPEAR);

    /**
     * The weapon ID's that perform this special when activated.
     */
    private final int[] identifiers;

    /**
     * The amount of special energy this attack will drain.
     */
    private int drainAmount;

    /**
     * The strength bonus when performing this special attack.
     */
    private final double specialMultiplier;

    /**
     * The accuracy bonus when performing this special attack.
     */
    private final double accuracyMultiplier;

    /**
     * The combat type used when performing this special attack.
     */
    private final CombatMethod combatMethod;

    /**
     * The weapon interface used by the identifiers.
     */
    private final WeaponType weaponType;

    /**
     * Create a new {@link CombatSpecial}.
     *
     * @param identifiers        the weapon ID's that perform this special when activated.
     * @param drainAmount        the amount of special energy this attack will drain.
     * @param specialMultiplier  the strength bonus when performing this special attack.
     * @param accuracyMultiplier the accuracy bonus when performing this special attack.
     * @param combatMethod       the combat type used when performing this special attack.
     * @param weaponType         the weapon interface used by the identifiers.
     */
    CombatSpecial(int[] identifiers, int drainAmount, double specialMultiplier, double accuracyMultiplier, CombatMethod combatMethod, WeaponType weaponType) {
        this.identifiers = identifiers;
        this.drainAmount = drainAmount;
        this.specialMultiplier = specialMultiplier;
        this.accuracyMultiplier = accuracyMultiplier;
        this.combatMethod = combatMethod;
        this.weaponType = weaponType;
    }

    /**
     * Drains the special bar for the argued {@link Entity}.
     *
     * @param mob    the mob who's special bar will be drained.
     * @param amount the amount of energy to drain from the special bar.
     */
    public static void drain(Entity mob, int amount) {
        Entity target = mob.getCombat().getTarget();

        if (target != null) {
            CombatFactory.check_spec_and_tele(mob.getAsPlayer(), target);
        }

        mob.desecreaseSpecialAttack(amount);
        mob.setSpecialActivated(false);

        if (!mob.isRecoveringSpecialAttack()) {
            TaskManager.submit(new RestoreSpecialAttackTask(mob));
        }

        if (mob.isPlayer()) {
            Player player = mob.getAsPlayer();
            CombatSpecial.updateBar(player);

            //The special attack bar had already been drained so ofc we used a special attack.
            player.putAttrib(AttributeKey.SPECIAL_ATTACK_USED, true);
        }
    }
    /**
     * Updates the special bar with the amount of special energy the argued
     * {@link Player} has.
     *
     * @param player the player who's special bar will be updated.
     */
    public static void updateBar(Player player) {

        player.getPacketSender().updateSpecialAttackOrb();

        if (player.getCombat().getWeaponType().getSpecialBar() == -1 || player.getCombat().getWeaponType().getSpecialMeter() == -1) {
            return;
        }
        int specialCheck = 10;
        int specialBar = player.getCombat().getWeaponType().getSpecialMeter();
        int specialAmount = player.getSpecialAttackPercentage() / 10;

        // send one packet, not 10. @shadowrs
        player.getPacketSender().sendString(99900, "specbar:" + specialCheck + ":" + specialBar + ":" + specialAmount);

        GlobalStrings.send(player.getCombat().getWeaponType().getSpecialMeter(),
            player.isSpecialActivated() ? ("<col=ffff00> Special Attack: " + player.getSpecialAttackPercentage() + "%") : ("</col> Special Attack: " + player.getSpecialAttackPercentage() + "%"),
            player);
    }

    /**
     * Assigns special bars to the attack style interface if needed.
     *
     * @param player the player to assign the special bar for.
     */
    public static void assign(Player player) {
        //player.debugMessage("Enter CombatSpecial assign");
        if (player.getCombat().getWeaponType().getSpecialBar() == -1) {
            //player.debugMessage("Don't assign spec bar");
            player.setSpecialActivated(false);
            player.setCombatSpecial(null);
            CombatSpecial.updateBar(player);
            return;
        }

        WeaponType weaponType = player.getCombat().getWeaponType();
        //player.debugMessage("Current weapon interface: " + weaponInterface);
        for (CombatSpecial special : CombatSpecial.values()) {
            WeaponType specialAttackBar = special.getWeaponInterface();
            if (weaponType == specialAttackBar) {
                //player.debugMessage("looping combat specials...");
                Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
                if (Arrays.stream(special.getIdentifiers()).anyMatch(id -> weapon != null && weapon.getId() == id)) {
                    //player.debugMessage("set spec bar");
                    player.getPacketSender().sendInterfaceDisplayState(player.getCombat().getWeaponType().getSpecialBar(), false);
                    player.setCombatSpecial(special);
                    return;
                }
            }
        }

        player.getPacketSender().sendInterfaceDisplayState(player.getCombat().getWeaponType().getSpecialBar(), true);
        player.setCombatSpecial(null);
        player.setSpecialActivated(false);
    }

    public static boolean specialAttackButton(Player player, int button) {
        if (player.dead() || player.locked()) {
            return false;
        }
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_SPECIAL_ATTACKS.ordinal()]) {
            player.message("Special attacks have been disabled for this duel.");
            return false;
        }

        /*Dragon axe
        special attack anim: 2876 gfx: 479

        Crystal axe
        special attack anim: 2876 gfx: 1689

        Dragon pickaxe
        special attack anim: 7138

        Crystal pickaxe
        special attack anim: 8329
        */

        if (button == 12322) {
            Item weapon = player.getEquipment().get(EquipSlot.WEAPON);

            if (weapon != null) {
                switch (weapon.getId()) {
                    case 22296 -> {
                        doStaffOfTheDead(player, true);
                        return true;
                    }
                    case ItemIdentifiers.STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED -> {
                        doStaffOfTheDead(player, false);
                        return true;
                    }
                    case ItemIdentifiers.DRAGON_BATTLEAXE -> {
                        doDragonBattleaxe(player);
                        return true;
                    }
                    default -> {
                        CombatSpecial.activate(player);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void doStaffOfTheDead(Player player, boolean staffOfLight) {
        //Toxic & Regular Staff of the Dead
        if (CombatFactory.takeSpecialEnergy(player, 100)) {
            player.animate(staffOfLight ? 7967 : 7083);
            player.graphic(staffOfLight ? 1516 : 1228, GraphicHeight.HIGH_3, 0);
            player.getTimers().addOrSet(TimerKey.SOTD_DAMAGE_REDUCTION, 60);
            player.message("<col=3d5d2b>Spirits of deceased evildoers offer you their protection.");
        }
    }

    private static void doDragonBattleaxe(Player player) {
        if (CombatFactory.takeSpecialEnergy(player, 100)) {
            player.forceChat("Raarrrrrgggggghhhhhhhh!");
            var drained = 0;
            int[] array = new int[]{0, 1, 4, 6};
            for (int stat : array) {
                drained += (int) Math.ceil(player.getSkills().level(stat) / 10.0);
                player.getSkills().setLevel(stat, (int) (player.getSkills().level(stat) - Math.floor(player.getSkills().level(stat) / 10.0)));
            }
            var boost = player.getSkills().level(Skills.STRENGTH) + 10 + (drained / 4); // you can only ever get to 119 lol
            player.getSkills().setLevel(Skills.STRENGTH, boost >= 119 ? 120 : boost); // fuck this formula wiki must be wrong
            player.graphic(246);
            player.animate(1056);
        }
    }

    /**
     * Activates a player's special attack.
     */
    public static void activate(Player player) {

        //Make sure the player has a valid special attack
        if (player.getCombatSpecial() == null) {
            return;
        }

        //Duel, disabled special attacks?
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_SPECIAL_ATTACKS.ordinal()]) {
            DialogueManager.sendStatement(player, "Special attacks have been disabled in this duel!");
            return;
        }

        //Get the special attack..
        final CombatSpecial spec = player.getCombatSpecial();

        //Check if player has already activated special attack,
        //If that's the case - turn if off.
        if (player.isSpecialActivated()) {
            player.setSpecialActivated(false);
            CombatSpecial.updateBar(player);

            // the button deactivates, but we queue up another gmaul request
            if (spec == CombatSpecial.GRANITE_MAUL) {
                queueGraniteMaulSpecial(player);
            }
        } else {
            //Set special attack activated
            player.setSpecialActivated(true);

            //Update special bar
            CombatSpecial.updateBar(player);

            //Handle instant special attacks here.
            //Example: Granite Maul, Dragon battleaxe...
            if (spec == CombatSpecial.GRANITE_MAUL) {


                int specPercentage = player.getSpecialAttackPercentage();

                //Make sure the player has enough special attack
                if (specPercentage < player.getCombatSpecial().getDrainAmount()) {
                    player.message("You do not have enough special attack energy left!");
                    player.setSpecialActivated(false);
                    CombatSpecial.updateBar(player);
                    return;
                }

                // granite maul triggers when traditional combat code is executed
                // in player.process, and NOT here in packet handler code.
                queueGraniteMaulSpecial(player);
            } else if (spec == CombatSpecial.DRAGON_THROWNAXE) {

                int specPercentage = player.getSpecialAttackPercentage();

                //Make sure the player has enough special attack
                if (specPercentage < player.getCombatSpecial().getDrainAmount()) {
                    player.message("You do not have enough special attack energy left!");
                    player.setSpecialActivated(false);
                    CombatSpecial.updateBar(player);
                    return;
                }

                if (player.getTimers().has(TimerKey.THROWING_AXE_DELAY)) {
                    return;
                }

                //Dragon thrownaxes disregard all combat delays
                player.getTimers().cancel(TimerKey.COMBAT_ATTACK);
            }
        }
    }

    /**
     * runite
     */
    private static void queueGraniteMaulSpecial(Player player) {
        var graniteMaulSpecials = player.<Integer>getAttribOr(AttributeKey.GRANITE_MAUL_SPECIALS, 0);
        player.putAttrib(AttributeKey.GRANITE_MAUL_SPECIALS, graniteMaulSpecials + 1);
        player.putAttrib(AttributeKey.GRANITE_MAUL_TIMEOUT_TICKS, 5);
        if (player.getCombat().lastTarget != null) {
            player.setEntityInteraction(player.getCombat().lastTarget);
            player.getCombat().setTarget(player.getCombat().lastTarget);
        }
    }

    /**
     * Gets the weapon ID's that perform this special when activated.
     *
     * @return the weapon ID's that perform this special when activated.
     */
    public int[] getIdentifiers() {
        return identifiers;
    }

    /**
     * Gets the amount of special energy this attack will drain.
     *
     * @return the amount of special energy this attack will drain.
     */
    public int getDrainAmount() {
        return drainAmount;
    }

    public void setDrainAmount(int drainAmount) {
        this.drainAmount = drainAmount;
    }

    /**
     * Gets the strength bonus when performing this special attack.
     *
     * @return the strength bonus when performing this special attack.
     */
    public double getSpecialMultiplier() {
        return specialMultiplier;
    }

    /**
     * Gets the accuracy bonus when performing this special attack.
     *
     * @return the accuracy bonus when performing this special attack.
     */
    public double getAccuracyMultiplier() {
        return accuracyMultiplier;
    }

    /**
     * Gets the combat type used when performing this special attack.
     *
     * @return the combat type used when performing this special attack.
     */
    public CombatMethod getCombatMethod() {
        return combatMethod;
    }

    /**
     * Gets the weapon interface used by the identifiers.
     *
     * @return the weapon interface used by the identifiers.
     */
    public WeaponType getWeaponInterface() {
        return weaponType;
    }
}

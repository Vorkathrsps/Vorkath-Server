package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;

import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.AttackType;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.CORPOREAL_BEAST;
import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.CombatType.MELEE;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.utility.ItemIdentifiers.ARCLIGHT;
import static com.aelous.utility.ItemIdentifiers.SALVE_AMULET_E;

/**
 * @Author Origin
 */
public class MeleeAccuracy {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);//doesHit(entity, enemy, style, 1);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getAttackRoll(attacker, defender, style);
        double defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - (Math.floor(defenceBonus + 2D)) / (2D * (Math.floor(attackBonus + 1D)));
        else
            successfulRoll = attackBonus / (2D * (Math.floor(defenceBonus + 1D)));

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    private static double getPrayerDefenseBonus(Entity defender) {
        double prayerBonus = 1;
        if (Prayers.usingPrayer(defender, THICK_SKIN))
            prayerBonus *= 1.05D; // 5% def level boost
        else if (Prayers.usingPrayer(defender, ROCK_SKIN))
            prayerBonus *= 1.10D; // 10% def level boost
        else if (Prayers.usingPrayer(defender, STEEL_SKIN))
            prayerBonus *= 1.15D; // 15% def level boost
        if (Prayers.usingPrayer(defender, CHIVALRY))
            prayerBonus *= 1.20D; // 20% def level boost
        else if (Prayers.usingPrayer(defender, PIETY))
            prayerBonus *= 1.25D; // 25% def level boost
        else if (Prayers.usingPrayer(defender, RIGOUR))
            prayerBonus *= 1.25D; // 25% def level boost
        else if (Prayers.usingPrayer(defender, AUGURY))
            prayerBonus *= 1.25D; // 25% def level boost
        return prayerBonus;
    }

    private static double getPrayerAttackBonus(Entity attacker, CombatType style) {
        double prayerBonus = 1;
        if (style == MELEE) {
            if (Prayers.usingPrayer(attacker, CLARITY_OF_THOUGHT))
                prayerBonus *= 1.05D; // 5% attack level boost
            else if (Prayers.usingPrayer(attacker, IMPROVED_REFLEXES))
                prayerBonus *= 1.10D; // 10% attack level boost
            else if (Prayers.usingPrayer(attacker, INCREDIBLE_REFLEXES))
                prayerBonus *= 1.15D; // 15% attack level boost
            else if (Prayers.usingPrayer(attacker, CHIVALRY))
                prayerBonus *= 1.15D; // 15% attack level boost
            else if (Prayers.usingPrayer(attacker, PIETY))
                prayerBonus *= 1.20D; // 20% attack level boost
        }
        return prayerBonus;
    }

    public static double getEffectiveDefence(Entity defender) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.ceil(getAttackLevel(defender) * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel += 3.0D;
            case CONTROLLED -> effectiveLevel += 1.0D;
        }

        effectiveLevel += 8.0D;

        return Math.floor(effectiveLevel);
    }

    public static double getEffectiveMelee(Entity attacker, Entity defender, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        final Item weapon = attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON);
        AttackType attackType = attacker.getCombat().getFightType().getAttackType();
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.ceil(getAttackLevel(attacker) * getPrayerAttackBonus(attacker, style));
        double specialMultiplier = attacker.getAsPlayer().getCombatSpecial() == null ? 0 : attacker.getAsPlayer().getCombatSpecial().getAccuracyMultiplier();

        switch (fightStyle) {
            case ACCURATE -> effectiveLevel += 3.0D;
            case CONTROLLED -> effectiveLevel += 1.0D;
        }

        effectiveLevel += 8.0D;

        if (attacker.isPlayer()) {

            if (attacker.getAsPlayer().isSpecialActivated()) {
                effectiveLevel *= effectiveLevel * specialMultiplier;
            }

            if (style.equals(MELEE) && (FormulaUtils.voidMelee((Player) attacker)))
                effectiveLevel *= 1.10D;

            if (attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                effectiveLevel *= 1.15D;
            }

            if (attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                effectiveLevel *= 1.2D;
            }

            if (weapon != null && FormulaUtils.hasViggorasChainMace(attacker.getAsPlayer())) {
                effectiveLevel *= 1.5D;
            }

            if (FormulaUtils.obbyArmour(attacker.getAsPlayer()) && FormulaUtils.hasObbyWeapon(attacker.getAsPlayer())) {
                effectiveLevel *= 1.1D;
            }

            if (attacker.getAsPlayer().getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                double inquisitorsBonus = 0;
                if (FormulaUtils.wearingInquisitorsPiece(attacker.getAsPlayer())) {
                    inquisitorsBonus *= 1.0025D;
                    effectiveLevel = inquisitorsBonus;
                }
                if (FormulaUtils.wearingFullInquisitors(attacker.getAsPlayer())) {
                    effectiveLevel *= inquisitorsBonus + 1.0D;
                }
            }
        }

        if (defender.isNpc()) {
            NPC npc = (NPC) defender;

            if (npc.id() == CORPOREAL_BEAST) {
                if (weapon != null && attacker.getAsPlayer().getEquipment().corpbeastArmour(weapon) && attackType != null && attackType.equals(AttackType.STAB)) {
                    effectiveLevel -= 1.5D;
                }
            }

            if (attacker.getAsPlayer().getEquipment().hasAt(EquipSlot.WEAPON, ARCLIGHT)) {
                if (npc.def() != null && npc.def().name != null && FormulaUtils.isDemon(npc)) {
                    effectiveLevel *= 1.7D;
                }
            }
        }

        return Math.floor(effectiveLevel);
    }

    public static int getAttackLevel(Entity attacker) {
        int rangeLevel = 1;
        if (attacker instanceof NPC) {
            NPC npc = ((NPC) attacker);
            if (npc.combatInfo() != null && npc.combatInfo().stats != null)
                rangeLevel = npc.combatInfo().stats.attack;
        } else {
            rangeLevel = attacker.skills().level(Skills.ATTACK);
        }
        return rangeLevel;
    }

    private static int getGearDefenceBonus(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        final AttackType type = defender instanceof NPC ? AttackType.SLASH : defender.getCombat().getFightType().getAttackType();
        int bonus = 0;
        if (style == MELEE) {
            if (type == AttackType.STAB)
                bonus = defenderBonus.stabdef;
            else if (type == AttackType.CRUSH)
                bonus = defenderBonus.crushdef;
            else if (type == AttackType.SLASH)
                bonus = defenderBonus.slashdef;
        }
        return bonus;
    }

    private static int getGearAttackBonus(Entity attacker, CombatType style) {
        final AttackType type = attacker.getCombat().getFightType().getAttackType();
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == MELEE) {
            if (type == AttackType.STAB)
                bonus = attackerBonus.stab;
            else if (type == AttackType.CRUSH)
                bonus = attackerBonus.crush;
            else if (type == AttackType.SLASH)
                bonus = attackerBonus.slash;
        }
        return bonus;
    }

    public static double getAttackRoll(Entity attacker, Entity defender, CombatType style) {
        double effectiveMeleeLevel = getEffectiveMelee(attacker, defender, style);

        double effectiveMeleeBonus = getGearAttackBonus(attacker, style);

        return effectiveMeleeLevel * Math.floor(effectiveMeleeBonus + 64D);
    }

    public static double getDefenceRoll(Entity defender, CombatType style) {
        double effectiveDefenceLevel = getEffectiveDefence(defender);

        int equipDefenceBonus = getGearDefenceBonus(defender, style);

        return effectiveDefenceLevel * Math.floor(equipDefenceBonus + 64D);
    }
}

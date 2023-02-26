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
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 */
public class MeleeAccuracy {

    private static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);//doesHit(entity, enemy, style, 1);
    }

    public static boolean rollForOSmumtenSpecial(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getAttackRoll(attacker, defender, style);
        double defenceBonus = getDefenceRoll(defender, style);

        double firstRoll = calculateSuccessfulRoll(attackBonus, defenceBonus, srand.nextDouble());
        double secondRoll = calculateSuccessfulRoll(attackBonus, defenceBonus, srand.nextDouble());

        return firstRoll > secondRoll;
    }

    private static double calculateSuccessfulRoll(double attackBonus, double defenceBonus, double randomValue) {
        double roll = 0.5 + (attackBonus - defenceBonus) / (2 * defenceBonus);
        return roll * randomValue;
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getAttackRoll(attacker, defender, style);
        double defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;
        double selectedChance = srand.nextInt(10000) / 10000.0;

        if (attackBonus > defenceBonus)
            successfulRoll = 1 - (defenceBonus + 2) / (2 * (attackBonus + 1));
         else
            successfulRoll = attackBonus / (2 * (defenceBonus + 1));


        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }


    /*public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getAttackRoll(attacker, defender, style);
        double defenceBonus = getDefenceRoll(defender, style);

        double successfulRoll;
        double selectedChance = srand.nextDouble() * 0.75 + 0.25; // Randomize chance of success

        if (attackBonus > defenceBonus) {
            double A = Math.floor(2 * (attackBonus - 1));
            double D = Math.floor(2 * defenceBonus);
            successfulRoll = A / (2 * (D + 1));
        } else {
            double A = Math.floor(2 * attackBonus);
            double D = Math.floor(2 * (defenceBonus - 1));
            successfulRoll = A / (2 * (D + 1));
        }

        successfulRoll = Math.max(0.05, Math.min(successfulRoll, 0.95));

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSuccess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }*/

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
        double effectiveLevel = Math.ceil(getDefenceLevel(defender) * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE:
                effectiveLevel += 3.0D;
                break;
            case CONTROLLED:
                effectiveLevel += 1.0D;
                break;
        }

        effectiveLevel += 8.0D;

        return Math.floor(effectiveLevel);
    }

    public static double getEffectiveMelee(Entity attacker, Entity defender, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        var getEquipment = attacker.getAsPlayer().getEquipment();
        final Item weapon = attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON);
        AttackType attackType = attacker.getCombat().getFightType().getAttackType();
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.ceil(getAttackLevel(attacker) * getPrayerAttackBonus(attacker, style));
        double specialMultiplier = attacker.getAsPlayer().getCombatSpecial() == null ? 1 : attacker.getAsPlayer().getCombatSpecial().getAccuracyMultiplier();

        switch (fightStyle) {
            case ACCURATE -> effectiveLevel += 3.0D;
            case CONTROLLED -> effectiveLevel += 1.0D;
        }

        effectiveLevel += 8.0D;

        if (attacker.isPlayer()) {
            if (attacker.getAsPlayer().isSpecialActivated()) {
                effectiveLevel = effectiveLevel * specialMultiplier;
            }
            if (FormulaUtils.regularVoidEquipmentBaseMelee((Player) attacker)) {
                effectiveLevel *= 1.10;
            }
            if (FormulaUtils.eliteVoidEquipmentMelee((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee((Player) attacker)) {
                effectiveLevel *= 1.125;
            }
            if (getEquipment.contains(ItemIdentifiers.SALVE_AMULET)) {
                effectiveLevel *= 1.15D;
            }
            if (getEquipment.contains(ItemIdentifiers.SALVE_AMULETI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
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
                if (weapon != null && getEquipment.corpbeastArmour(weapon) && attackType != null && attackType.equals(AttackType.STAB)) {
                    effectiveLevel -= 1.5D;
                }
            }

            if (FormulaUtils.isDragon(npc)) {
                if (FormulaUtils.hasDragonHunterLance((Player) attacker)) {
                    effectiveLevel *= 1.20D;
                }
            }

            if (FormulaUtils.hasArchLight((Player) attacker)) {
                if (npc.def() != null && npc.def().name != null && FormulaUtils.isDemon(npc)) {
                    effectiveLevel *= 1.7D;
                }
            }
        }

        return Math.floor(effectiveLevel);
    }

    public static int getAttackLevel(Entity attacker) {
        int attackLevel = 1;
        if (attacker instanceof NPC) {
            NPC npc = ((NPC) attacker);
            if (npc.combatInfo() != null && npc.combatInfo().stats != null)
                attackLevel = npc.combatInfo().stats.attack;
        } else {
            attackLevel = attacker.skills().level(Skills.ATTACK);
        }
        return attackLevel;
    }

    public static int getDefenceLevel(Entity defender) {
        int defenceLevel = 1;
        if (defender instanceof NPC) {
            NPC npc = ((NPC) defender);
            if (npc.combatInfo() != null && npc.combatInfo().stats != null)
                defenceLevel = npc.combatInfo().stats.defence;
        } else {
            defenceLevel = defender.skills().level(Skills.DEFENCE);
        }
        return defenceLevel;
    }

    private static int getGearDefenceBonus(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        final AttackType type = defender instanceof NPC ? AttackType.SLASH : defender.getCombat().getFightType().getAttackType();
        int bonus = 1;
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
        int bonus = 1;
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
        double effectiveLevel = getEffectiveMelee(attacker, defender, style);
        double effectiveBonus = getGearAttackBonus(attacker, style);

        return Math.floor((effectiveLevel * (effectiveBonus + 64)) + 0.5);
    }

    public static double getDefenceRoll(Entity defender, CombatType style) {
        double effectiveLevel = getEffectiveDefence(defender);
        int effectiveBonus = getGearDefenceBonus(defender, style);

        return Math.floor((effectiveLevel * (effectiveBonus + 64)) + 0.5);
    }
}

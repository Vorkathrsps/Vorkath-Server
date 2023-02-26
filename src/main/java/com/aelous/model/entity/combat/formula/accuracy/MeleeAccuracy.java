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

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        int attackBonus = (int) Math.floor(getAttackRoll(attacker, defender, style));
        int defenceBonus = (int) Math.floor(getDefenceRoll(defender, style));
        double successfulRoll;
        double selectedChance = srand.nextInt(10000) / 10000.0;

        if (attackBonus > defenceBonus)
            successfulRoll = 1 - (defenceBonus + 2D) / (2D * (attackBonus + 1D));
        else
            successfulRoll = attackBonus / (2D * (defenceBonus + 1D));


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


    public static int getEffectiveDefence(Entity defender) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getDefenceLevel(defender) * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel += 3;
            case CONTROLLED -> effectiveLevel += 1;
        }

        effectiveLevel += 8;

        return (int) Math.floor(effectiveLevel);
    }

    public static int getEffectiveMelee(Entity attacker, Entity defender, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        var getEquipment = attacker.getAsPlayer().getEquipment();
        final Item weapon = attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON);
        AttackType attackType = attacker.getCombat().getFightType().getAttackType();
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getAttackLevel(attacker) * getPrayerAttackBonus(attacker, style));
        double specialMultiplier = attacker.getAsPlayer().getCombatSpecial() == null ? 1 : attacker.getAsPlayer().getCombatSpecial().getAccuracyMultiplier();

        switch (fightStyle) {
            case ACCURATE -> effectiveLevel += 3;
            case CONTROLLED -> effectiveLevel += 1;
        }

        effectiveLevel += 8;

        if (attacker.isPlayer()) {
            if (attacker.getAsPlayer().isSpecialActivated()) {
                effectiveLevel = (int) Math.floor(effectiveLevel * specialMultiplier);
            }
            if (FormulaUtils.regularVoidEquipmentBaseMelee((Player) attacker)) {
                effectiveLevel = (int) Math.floor(effectiveLevel * 1.1);
            }
            if (FormulaUtils.eliteVoidEquipmentMelee((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee((Player) attacker)) {
                effectiveLevel = (int) Math.floor(effectiveLevel * 1.125);
            }
            if (getEquipment.contains(ItemIdentifiers.SALVE_AMULET)) {
                effectiveLevel = (int) Math.floor(effectiveLevel * 1.15);
            }
            if (getEquipment.contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                effectiveLevel = (int) Math.floor(effectiveLevel * 1.2);
            }
            if (weapon != null && FormulaUtils.hasViggorasChainMace(attacker.getAsPlayer())) {
                effectiveLevel = (int) Math.floor(effectiveLevel * 1.5);
            }
            if (FormulaUtils.obbyArmour(attacker.getAsPlayer()) && FormulaUtils.hasObbyWeapon(attacker.getAsPlayer())) {
                effectiveLevel = (int) Math.floor(effectiveLevel * 1.1);
            }
            if (attacker.getAsPlayer().getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                double inquisitorsBonus = 0;
                if (FormulaUtils.wearingInquisitorsPiece(attacker.getAsPlayer())) {
                    inquisitorsBonus *= 1.0025;
                    effectiveLevel = (int) Math.floor(inquisitorsBonus);
                }
                if (FormulaUtils.wearingFullInquisitors(attacker.getAsPlayer())) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * (inquisitorsBonus + 1));
                }
            }
        }

        if (defender.isNpc()) {
            NPC npc = (NPC) defender;

            if (npc.id() == CORPOREAL_BEAST) {
                if (weapon != null && getEquipment.corpbeastArmour(weapon) && attackType != null && attackType.equals(AttackType.STAB)) {
                    effectiveLevel -= 1.5;
                }
            }

            if (FormulaUtils.isDragon(npc)) {
                if (FormulaUtils.hasDragonHunterLance((Player) attacker)) {
                    effectiveLevel *= 1.20D;
                    effectiveLevel = (int) Math.floor(effectiveLevel);
                }
            }

            if (FormulaUtils.hasArchLight((Player) attacker)) {
                if (npc.def() != null && npc.def().name != null && FormulaUtils.isDemon(npc)) {
                    effectiveLevel *= 1.7D;
                    effectiveLevel = (int) Math.floor(effectiveLevel);
                }
            }
        }

        return (int) Math.floor(effectiveLevel);
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

    public static int getAttackRoll(Entity attacker, Entity defender, CombatType style) {
        int effectiveLevel = (int) Math.floor(getEffectiveMelee(attacker, defender, style));

        int effectiveBonus = (int) Math.floor(getGearAttackBonus(attacker, style));

        return (int) Math.floor(effectiveLevel * (effectiveBonus + 64));
    }

    public static int getDefenceRoll(Entity defender, CombatType style) {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveDefence(defender));

        int effectiveBonus = (int) Math.floor(getGearDefenceBonus(defender, style));

        return (int) Math.floor(effectiveDefenceLevel * (effectiveBonus + 64));
    }
}

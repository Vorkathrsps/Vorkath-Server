package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;

import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.CombatType.RANGED;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.EAGLE_EYE;

/**
 * @Author Origin
 */
public class RangeAccuracy {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);//doesHit(entity, enemy, style, 1);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        int attackBonus = (int) Math.floor(getAttackRoll(attacker, style));
        int defenceBonus = (int) Math.floor(getDefenceRoll(defender, style));
        double successfulRoll;
        double selectedChance = srand.nextInt(10000) / 10000.0;

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - (Math.floor(defenceBonus + 2D)) / (2D * (Math.floor(attackBonus + 1D)));
        else
            successfulRoll = attackBonus / (2D * (Math.floor(defenceBonus + 1D)));

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }
    public static double getPrayerAttackBonus(Entity attacker) {
        double prayerBonus = 1D;
        if (Prayers.usingPrayer(attacker, SHARP_EYE))
            prayerBonus *= 1.05D; // 5% range level boost
        else if (Prayers.usingPrayer(attacker, HAWK_EYE))
            prayerBonus *= 1.10D; // 10% range level boost
        else if (Prayers.usingPrayer(attacker, EAGLE_EYE))
            prayerBonus *= 1.15D; // 15% range level boost
        else if (Prayers.usingPrayer(attacker, RIGOUR))
            prayerBonus *= 1.20D; // 20% range level boost
        return prayerBonus;
    }

    public static double getPrayerDefenseBonus(Entity defender) {
        double prayerBonus = 1D;
        if (Prayers.usingPrayer(defender, RIGOUR)) {
            prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }

    public static int getEffectiveDefence(Entity defender) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel(defender) * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            case CONTROLLED -> effectiveLevel = (int) Math.floor(effectiveLevel + 1);
        }

        effectiveLevel = (int) Math.floor(effectiveLevel + 8);

        return effectiveLevel;
    }

    public static int getEffectiveRanged(Entity attacker, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel(attacker) * getPrayerAttackBonus(attacker));
        double specialMultiplier = 1;

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            if (player.getCombatSpecial() != null) {
                specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
            }
        }

        if (fightStyle == FightStyle.ACCURATE) {
            effectiveLevel = (int) Math.floor(effectiveLevel + 3);
        }

        effectiveLevel = (int) Math.floor(effectiveLevel + 8);

        if(attacker.isPlayer()) { //additional bonuses here
            if (style.equals(RANGED)) {
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BOW_OF_FAERDHINEN)) {
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRYSTAL_HELM)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.05D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRYSTAL_BODY)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRYSTAL_LEGS)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.10D);
                    }
                }

                if (FormulaUtils.regularVoidEquipmentBaseRanged((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.10D);
                }

                if (FormulaUtils.eliteVoidEquipmentRanged((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.125D);
                }

                if (attacker.isPlayer() && attacker.getAsPlayer().isSpecialActivated()) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * specialMultiplier);
                }

            }
        }

        return effectiveLevel;
    }

    public static int getRangeLevel(Entity attacker) {
        int rangeLevel = 1;
        if (attacker instanceof NPC) {
            NPC npc = ((NPC) attacker);
            if (npc.combatInfo() != null && npc.combatInfo().stats != null)
                rangeLevel = npc.combatInfo().stats.ranged;
        } else {
            rangeLevel = attacker.skills().level(Skills.RANGED);
        }
        return rangeLevel;
    }

    public static int getGearAttackBonus(Entity attacker, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == RANGED) {
            bonus = attackerBonus.range;
        }
        return bonus;
    }

    public static int getGearDefenceBonus(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == RANGED) {
            bonus = attackerBonus.rangedef;
        }
        return bonus;
    }

    public static int getAttackRoll(Entity attacker, CombatType style) {
        int effectiveRangeLevel = (int) Math.floor(getEffectiveRanged(attacker, style));
        int equipmentRangeBonus = getGearAttackBonus(attacker, style);
        return (int) Math.floor(effectiveRangeLevel * (equipmentRangeBonus + 64D));
    }

    public static int getDefenceRoll(Entity defender, CombatType style) {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveDefence(defender));
        int equipmentRangeBonus = getGearDefenceBonus(defender, style);
        return (int) Math.floor(effectiveDefenceLevel * (equipmentRangeBonus + 64D));
    }
}

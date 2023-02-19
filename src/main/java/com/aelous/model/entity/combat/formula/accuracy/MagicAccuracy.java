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

import java.security.SecureRandom;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.AUGURY;

/**
 * @Author Origin
 */

public class MagicAccuracy {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getAttackRoll(attacker,  style);
        double defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - (defenceBonus + 2D) / (2D * (attackBonus + 1D));
        else
            successfulRoll = attackBonus / (2D * (defenceBonus + 1D));

            //System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    public static int getEquipmentBonusAttacker(Entity attacker, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == CombatType.MAGIC) {
            bonus = attackerBonus.mage;
        }
        return bonus;
    }

    public static int getEquipmentBonusDefender(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == CombatType.MAGIC) {
            bonus = defenderBonus.magedef;
        }
        return bonus;
    }

    public static double getDefenceLevelDefender(Entity defender, FightStyle style) {
        double effectiveLevel = Math.floor(defender.skills().level(Skills.DEFENCE) * getPrayerBonusDefender(defender));
        switch (style) {
            case DEFENSIVE -> {
                effectiveLevel += 3.0D;
            }
            case CONTROLLED -> {
                effectiveLevel += 1.0D;
            }
        }
        effectiveLevel += 8.0D;
        return effectiveLevel;
    }

    public static double getDefenceRoll(Entity defender, CombatType style) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        double effectiveDefenceLevel = getDefenceLevelDefender(defender, fightStyle);

        effectiveDefenceLevel *= 0.3D;
        effectiveDefenceLevel = Math.floor(effectiveDefenceLevel);

        double magicLevel = getMagicLevelDefender(defender);
        magicLevel *= getPrayerBonusDefender(defender);
        magicLevel = Math.floor(magicLevel);

        magicLevel *= 0.7D;
        magicLevel = Math.floor(magicLevel);

        double effectivemagicLevel = Math.floor(effectiveDefenceLevel + magicLevel + 8D);
        double equipmentDefenceBonus = Math.floor(getEquipmentBonusDefender(defender, style));

        return effectivemagicLevel * (equipmentDefenceBonus + 64D);
    }

    public static int getMagicLevelAttacker(Entity attacker) {
        int magicLevel = 1;
        if (attacker instanceof NPC) {
            NPC npc = ((NPC) attacker);
            if (npc.combatInfo() != null && npc.combatInfo().stats != null)
                magicLevel = npc.combatInfo().stats.magic;
            } else {
                magicLevel = attacker.skills().level(Skills.MAGIC);
            }
        return magicLevel;
    }

    public static int getMagicLevelDefender(Entity defender) {
        return defender.skills().level(Skills.MAGIC);
    }

    public static double getPrayerBonus(Entity attacker, CombatType style) {
        double prayerBonus = 1D;
        if (style == CombatType.MAGIC) {
            if (Prayers.usingPrayer(attacker, MYSTIC_WILL))
                prayerBonus *= 1.05D; // 5% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_LORE))
                prayerBonus *= 1.10D; // 10% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_MIGHT))
                prayerBonus *= 1.15D; // 15% magic level boost
            else if (Prayers.usingPrayer(attacker, AUGURY))
                prayerBonus *= 1.25D; // 25% magic level boost
        }
        return prayerBonus;
    }

    public static double getPrayerBonusDefender(Entity defender) {
        double prayerBonus = 1D;
            if (Prayers.usingPrayer(defender, AUGURY))
                prayerBonus *= 1.25D; //
        return prayerBonus;
    }

    public static double getEffectiveLevelAttacker(Entity attacker, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.floor(getMagicLevelAttacker(attacker) * getPrayerBonus(attacker, style));
        switch (fightStyle) {
            case ACCURATE -> {
                effectiveLevel += 3.0D;
            }
            case CONTROLLED -> {
                effectiveLevel += 1.0D;
            }
        }

        effectiveLevel += 8.0;

        if (attacker.isPlayer()) {
            if (style.equals(CombatType.MAGIC)) {
                if (FormulaUtils.voidMagic((Player) attacker)) {
                effectiveLevel *= 1.45D; //45%
                }
            }
        }

        return Math.floor(effectiveLevel);
    }

    public static double getAttackRoll(Entity attacker, CombatType style) {
        double effectiveMagicLevel = getEffectiveLevelAttacker(attacker, style);

        double equipmentAttackBonus = getEquipmentBonusAttacker(attacker, style);

        double maxRoll = effectiveMagicLevel * (equipmentAttackBonus + 64D);

        return (int) maxRoll;
    }

}

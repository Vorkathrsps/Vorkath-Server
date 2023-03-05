package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin;

import java.security.SecureRandom;
import java.text.DecimalFormat;

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
        int attackBonus = (int) Math.floor(getAttackRoll(attacker, style));
        int defenceBonus = (int) Math.floor(getDefenceRoll(defender, style));
        double successfulRoll;
        double selectedChance = srand.nextInt(10000) / 10000.0;

        if (attackBonus > defenceBonus)
            successfulRoll = 1 - (Math.floor(defenceBonus + 2D)) / (2 * (Math.floor(attackBonus + 1D)));
        else
            successfulRoll = attackBonus / (2 * (Math.floor(defenceBonus + 1D)));

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    public static int getEquipmentBonusAttacker(Entity attacker, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == CombatType.MAGIC) {
            if (attacker instanceof Player) {
                if (!WildernessArea.inWild((Player) attacker) && ((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                    bonus = attackerBonus.mage += Math.min(attackerBonus.mage * 3, attackerBonus.mage * attackerBonus.mage);
                } else {
                    bonus = attackerBonus.mage;
                }
            }
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

    public static int getDefenceLevelDefender(Entity defender, FightStyle style) {
        int effectiveLevel = defender instanceof NPC ? ((NPC) defender).combatInfo().stats.defence : (int) Math.floor(defender.skills().level(Skills.DEFENCE) * getPrayerBonusDefender(defender));
        switch (style) {
            case DEFENSIVE -> effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            case CONTROLLED -> effectiveLevel = (int) Math.floor(effectiveLevel + 1);
        }
        effectiveLevel = (int) Math.floor(effectiveLevel + 8);
        return effectiveLevel;
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
        if (defender instanceof NPC) {
            return ((NPC) defender).combatInfo().stats.magic;
        } else {
            return defender.skills().level(Skills.MAGIC);
        }
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

    public static int getEffectiveLevelAttacker(Entity attacker, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int effectiveLevel = (int) Math.floor(getMagicLevelAttacker(attacker) * getPrayerBonus(attacker, style));
        switch (fightStyle) {
            case ACCURATE -> effectiveLevel += 3;
            case CONTROLLED -> effectiveLevel += 1;
        }

        effectiveLevel += 8.0D;

        if (attacker.isPlayer()) {
            if (style.equals(CombatType.MAGIC)) {
                if (FormulaUtils.regularVoidEquipmentBaseMagic((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.45D);
                }

                if (FormulaUtils.eliteVoidEquipmentBaseMagic((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.70);
                }

                if (attacker.getAsPlayer().getSpellbook().equals(MagicSpellbook.ANCIENT) && FormulaUtils.hasZurielStaff((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.10);
                }
            }
        }

        return effectiveLevel;
    }

    public static int getAttackRoll(Entity attacker, CombatType style) {
        int effectiveMagicLevel = (int) Math.floor(getEffectiveLevelAttacker(attacker, style));
        int equipmentAttackBonus = getEquipmentBonusAttacker(attacker, style);
        return (int) Math.floor(effectiveMagicLevel * (equipmentAttackBonus + 64));
    }


    public static int getDefenceRoll(Entity defender, CombatType style) {
        int magicLevel = getMagicLevelDefender(defender);
        int magicDefence = getDefenceLevelDefender(defender, FightStyle.DEFENSIVE);
        int effectiveLevel = (int) Math.floor(((magicDefence * getPrayerBonusDefender(defender) * 1.3D) * 0.7D) + magicLevel);
        int equipmentDefenceBonus = getEquipmentBonusDefender(defender, style);
        return (int) Math.floor(effectiveLevel * (equipmentDefenceBonus + 64));
    }
}

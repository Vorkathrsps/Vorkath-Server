package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;

import java.security.SecureRandom;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.AUGURY;

/**
 * @Author Origin
 */
public class MagicAccuracyNpc {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        int attackBonus = (int) Math.floor(getAttackRoll(attacker, style));
        int defenceBonus = (int) Math.floor(getDefenceRoll(defender));
        double successfulRoll;
        double selectedChance = srand.nextInt(10000) / 10000.0;

        if (attackBonus > defenceBonus)
            successfulRoll = 1 - (Math.floor(defenceBonus + 2D)) / (2 * (Math.floor(attackBonus + 1D)));
        else
            successfulRoll = attackBonus / (2 * (Math.floor(defenceBonus + 1D)));

        System.out.println("NPCStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + successfulRoll + " rolledChance=" + selectedChance + " sucessful=" + (successfulRoll > selectedChance));

        return successfulRoll > selectedChance;
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

    public static int getEquipmentBonus(Entity attacker, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        int bonus = 0;
        if (style == CombatType.MAGIC) {
            bonus = attackerBonus.mage;
        }
        return bonus;
    }

    public static int getMagicLevelNpc(Entity defender) {
        return defender.getAsNpc().getCombatInfo().stats.magic;
    }

    public static int getMagicDefenceLevelNpc(Entity defender) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (defender instanceof NPC) {
            bonus = defenderBonus.magedef;
        }
        return bonus;
    }

    public static int getEffectiveLevelDefender(Entity defender) {
        return (int) Math.floor(getMagicLevelNpc(defender) + 9);
    }

    public static int getDefenceRoll(Entity defender) {
        return (int) Math.floor(getEffectiveLevelDefender(defender) * (getMagicDefenceLevelNpc(defender) + 64));
    }

    public static int getMagicLevel(Entity attacker) {
        return attacker.getSkills().level(Skills.MAGIC);
    }

    public static int getEffectiveLevelAttacker(Entity attacker, CombatType style) {
        return (int) Math.floor(getMagicLevel(attacker) * getPrayerBonus(attacker, style));
    }

    public static int getAttackRoll(Entity attacker, CombatType style) {
        return (int) Math.floor(getEffectiveLevelAttacker(attacker, style) * (getEquipmentBonus(attacker, style) + 64));
    }
}

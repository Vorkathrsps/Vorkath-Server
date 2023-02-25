package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;

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
        double attackBonus = getAttackRoll(attacker, style);
        double defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - (Math.floor(defenceBonus + 2D)) / (2D * (Math.floor(attackBonus + 1D)));
        else
            successfulRoll = attackBonus / (2D * (Math.floor(defenceBonus + 1D)));

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

        if (attacker.isPlayer()) {
            if (style.equals(CombatType.MAGIC)) {
                if (FormulaUtils.voidMagic((Player) attacker)) {
                    bonus *= 1.45D; //45%
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                    bonus *= 3;
                }
                if (FormulaUtils.isUndead(attacker.getCombat().getTarget())) { //UNDEAD BONUSES
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI_25278)) {
                        bonus *= 1.20D;
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                        bonus *= 1.10D;
                    }
                    if (attacker.getCombat().getTarget().isNpc()) {
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                            bonus *= 1.05D;
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                            bonus *= 1.10D;
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                            bonus *= 1.15D;
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.OCCULT_NECKLACE_OR)) {
                            bonus *= 1.05D;
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.THAMMARONS_SCEPTRE) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.ACCURSED_SCEPTRE_A) ) {
                            bonus *= 1.50D;
                        }
                    }
                }
                if (task != null && Slayer.creatureMatches((Player) attacker, attacker.getAsNpc().id())) {
                    //might cause null pointer
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                        bonus *= 1.15D;
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET_I)) {
                        bonus *= 1.18D;
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                        bonus *= 1.20D;
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                        bonus *= 1.25D;
                    }
                }
            }
        }
            return bonus;
        }

    public static int getMagicLevelNpc(Entity defender) {
        return defender.getAsNpc().combatInfo().stats.magic;
    }

    public static int getMagicDefenceLevelNpc(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus = 0;
        if(defender instanceof NPC) {
            if (style == CombatType.MAGIC) {
                bonus = defenderBonus.magedef;
            }
        }
        return bonus + 64;
    }

    public static double getEffectiveLevelDefender(Entity defender) {
        return getMagicLevelNpc(defender) + 9D;
    }

    public static double getDefenceRoll(Entity defender, CombatType style) {
        return getEffectiveLevelDefender(defender) * Math.floor(getMagicDefenceLevelNpc(defender, style));
    }

    public static int getMagicLevel(Entity attacker) {
        return attacker.skills().level(Skills.MAGIC);
    }

    public static int getEffectiveLevelAttacker(Entity attacker, CombatType style) {
        return (int) Math.ceil(getMagicLevel(attacker) * getPrayerBonus(attacker, style) + 9);
    }

    public static double getAttackRoll(Entity attacker, CombatType style) {
        return getEffectiveLevelAttacker(attacker, style) * Math.floor(getEquipmentBonus(attacker, style) + 64D);
    }

}

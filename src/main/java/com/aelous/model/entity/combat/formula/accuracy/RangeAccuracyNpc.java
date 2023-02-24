package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.maxhit.RangeMaxHit;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.CombatType.RANGED;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.EAGLE_EYE;
import static com.aelous.utility.ItemIdentifiers.TWISTED_BOW;

/**
 * @Author Origin
 */
public class RangeAccuracyNpc {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);//doesHit(entity, enemy, style, 1);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getAttackRoll(attacker, defender, style);
        double defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;
        double selectedChance = srand.nextDouble();
        double maxDamage = RangeMaxHit.maxHit(attacker.getAsPlayer(), defender, false, false);
        double dps = ((maxDamage * selectedChance) / 2);
        if (attackBonus > defenceBonus)
            successfulRoll = 1D - ((defenceBonus + 2D) / (2D * Math.floor(attackBonus + 1D)));
        else
            successfulRoll = (attackBonus / (2D * Math.floor(defenceBonus + 1D)));

        System.out.println("chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance));

        System.out.println("DPS: " + new DecimalFormat("0.000").format(dps));

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

    public static int getDefenceNpc(Entity defender) {
        return defender.getAsNpc().combatInfo().stats.defence;
    }

    public static double getEffectiveRanged(Entity attacker, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.ceil(getRangeLevel(attacker) * getPrayerAttackBonus(attacker));

        switch (fightStyle) {
            case ACCURATE:
                effectiveLevel += 3.0D;
                break;
        }

        effectiveLevel += 8;

        if(attacker.isPlayer()) {
            if (style.equals(RANGED)) {
                if (FormulaUtils.voidRanger((Player) attacker)) {
                    effectiveLevel *= 1.10D;
                    effectiveLevel = Math.floor(effectiveLevel);
                }
            }
            if (FormulaUtils.isUndead(attacker)) { //UNDEAD BONUSES
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI_25278)) {
                    effectiveLevel *= 1.20D;
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                    effectiveLevel *= 1.10D;
                }
            }//END OF UNDEAD
            if (WildernessArea.inWilderness(attacker.tile())) {
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRAWS_BOW)) {
                    effectiveLevel *= 1.50D;
                }
            }//END OF WILDERNESS BUFFS
            if (attacker.isNpc()) {
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                    effectiveLevel *= 1.05D;
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                    effectiveLevel *= 1.10D;
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                    effectiveLevel *= 1.15D;
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.NECKLACE_OF_ANGUISH_OR)) {
                    effectiveLevel *= 1.05D;
                }
            }//CUSTOM WILDERNESS SLAYER BUFFS
            if (task != null && Slayer.creatureMatches((Player) attacker, attacker.getAsNpc().id())) {
                //might cause null pointer
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                    effectiveLevel *= 1.15D;
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET_I)) {
                    effectiveLevel *= 1.18D;
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                    effectiveLevel *= 1.20D;
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                    effectiveLevel *= 1.25D;
                }
            }
        }

        return Math.floor(effectiveLevel);
    }

    public static double getEffectiveLevelDefender(Entity defender) {
        return getDefenceNpc(defender) + 9;
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

    public static double twistedBowBonus(Entity attacker, Entity defender) {
        double bonus = 0;
        Player player = (Player) attacker;
        final Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
            if (weapon != null) {
                if (Stream.of(TWISTED_BOW).anyMatch(w -> w == weapon.getId())) {

                    double magicLevel = 0;

                    if (attacker.isPlayer()) {
                        if (defender instanceof NPC) {
                            NPC n = (NPC) defender;
                            if (n.combatInfo() != null && n.combatInfo().stats != null)
                                magicLevel = n.combatInfo().stats.magic > 350 && player.raidsParty != null ? 350 : n.combatInfo().stats.magic > 250D ? 250D : n.combatInfo().stats.magic;
                        } else {
                            magicLevel = defender.getAsPlayer().skills().getMaxLevel(Skills.MAGIC);
                        }

                        bonus += 140 + ((3 * magicLevel - 10) / 100) - (((3 * magicLevel / 10) - 100)) * ((3 * magicLevel / 10) - 100) / 100;
                        bonus /= 100;
                        if (bonus > 2.4D)
                            bonus = (int) 2.4;
                    }
                }
            }
            return bonus;
        }

    public static int getGearAttackBonus(Entity attacker, Entity defender, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == RANGED) {
            bonus = attackerBonus.range;
        }
        if (attacker.getAsPlayer().getEquipment().contains(TWISTED_BOW)) {
            bonus *= twistedBowBonus(attacker, defender);
        }
        return bonus;
    }

    public static int getRangeDefenceLevelNpc(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus = 0;
        if(defender instanceof NPC) {
            if (style == CombatType.RANGED) {
                bonus = defenderBonus.rangedef;
            }
        }
        return bonus;
    }

    public static double getAttackRoll(Entity attacker, Entity defender, CombatType style) {
        double effectiveRangeLevel = getEffectiveRanged(attacker, style);

        double equipmentRangeBonus = getGearAttackBonus(attacker, defender, style);

        return Math.ceil(effectiveRangeLevel * Math.floor(equipmentRangeBonus + 64));
    }

    public static double getDefenceRoll(Entity defender, CombatType style) {
        double effectiveDefenceLevel = getEffectiveLevelDefender(defender);

        int equipmentRangeBonus = getRangeDefenceLevelNpc(defender, style);

        return Math.floor(effectiveDefenceLevel + 9) * Math.floor(equipmentRangeBonus + 64);
    }

}

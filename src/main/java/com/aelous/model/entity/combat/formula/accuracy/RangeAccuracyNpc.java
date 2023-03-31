package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
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
import java.util.stream.Stream;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.CombatType.RANGED;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.EAGLE_EYE;
import static com.aelous.utility.ItemIdentifiers.DRAGON_HUNTER_CROSSBOW;
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
        int attackBonus = (int) Math.floor(getAttackRoll(attacker, defender, style));
        int defenceBonus = (int) Math.floor(getDefenceRoll(defender, style));
        double successfulRoll;
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - (Math.floor(defenceBonus + 2D)) / (2D * (Math.floor(attackBonus + 1D)));
        else
            successfulRoll = attackBonus / (2D * (Math.floor(defenceBonus + 1D)));

        System.out.println("chanceOfSucessNPC=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance));

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
        return defender.getAsNpc().getCombatInfo().stats.defence;
    }

    public static int getEffectiveRanged(Entity attacker, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel(attacker) * getPrayerAttackBonus(attacker));

        switch (fightStyle) {
            case ACCURATE:
                effectiveLevel = (int) Math.floor(effectiveLevel + 3);
                break;
        }

        effectiveLevel = (int) Math.floor(effectiveLevel + 9);

        if(attacker.isPlayer()) {
            if (style.equals(RANGED)) {
                if (FormulaUtils.regularVoidEquipmentBaseRanged((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.1);
                }

                if (FormulaUtils.eliteVoidEquipmentRanged((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.125);
                }
            }
            if (FormulaUtils.isUndead(attacker)) { //UNDEAD BONUSES
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI_25278)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.2);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.1);
                }
            }//END OF UNDEAD
            if (WildernessArea.inWilderness(attacker.tile())) {
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRAWS_BOW)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.5);
                }
            }//END OF WILDERNESS BUFFS
            if (attacker.isNpc()) {
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.05);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.1);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.15);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.NECKLACE_OF_ANGUISH_OR)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.05);
                }
            }//CUSTOM WILDERNESS SLAYER BUFFS
            if (task != null && Slayer.creatureMatches((Player) attacker, attacker.getAsNpc().id())) {
                //might cause null pointer
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.15);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET_I)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.18);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.20);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.25);
                }
            }
        }

        return effectiveLevel;
    }

    public static double getEffectiveLevelDefender(Entity defender) {
        return Math.floor(getDefenceNpc(defender) + 9);
    }

    public static int getRangeLevel(Entity attacker) {
        int rangeLevel = 1;
        if (attacker instanceof NPC) {
            NPC npc = ((NPC) attacker);
            if (npc.getCombatInfo() != null && npc.getCombatInfo().stats != null) {
                rangeLevel = npc.getCombatInfo().stats.ranged;
            }
        } else {
            rangeLevel = attacker.getSkills().level(Skills.RANGED);
        }
        return rangeLevel;
    }

    public static double twistedBowBonus(Entity attacker, Entity defender) {
        double bonus = 1;
        Player player = (Player) attacker;
        final Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
        if (weapon != null) {
            if (Stream.of(TWISTED_BOW).anyMatch(w -> w == weapon.getId())) {

                double magicLevel = 1;

                if (attacker.isPlayer()) {
                    if (defender instanceof NPC) {
                        NPC n = (NPC) defender;
                        if (n.getCombatInfo() != null && n.getCombatInfo().stats != null)
                            magicLevel = n.getCombatInfo().stats.magic > 350 && player.raidsParty != null ? 350 : n.getCombatInfo().stats.magic > 250D ? 250D : n.getCombatInfo().stats.magic;
                    } else {
                        magicLevel = defender.getAsPlayer().getSkills().getMaxLevel(Skills.MAGIC);
                    }

                    bonus += 140 + (((10*3*magicLevel) / 10) - 10) - ((Math.floor(3 * magicLevel / 10 - 100)) * 2);
                    //bonus += 140 + ((3 * magicLevel - 10) / 100) - (((3 * magicLevel / 10) - 100)) * ((3 * magicLevel / 10) - 100) / 100;
                    bonus = Math.floor(bonus / 100);
                    if (bonus > 2.4D)
                        bonus = (int) 2.4;
                }
            }
        }
        return Math.floor(bonus);
    }


    public static int getGearAttackBonus(Entity attacker, Entity defender, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 1;
        if (style == RANGED) {
            bonus = (bonus + attackerBonus.range);
        }
        if (attacker instanceof Player) {
            if (attacker.getAsPlayer().getEquipment().contains(TWISTED_BOW)) {
                bonus = (int) Math.floor(bonus * twistedBowBonus(attacker, defender));
            }
        }
        if (attacker.isPlayer()) {
            if (attacker.getAsPlayer().getEquipment().contains(DRAGON_HUNTER_CROSSBOW)) {
                if (defender instanceof NPC && FormulaUtils.isDragon(defender)) {
                    bonus = (int) Math.floor(bonus * 1.25D);
                } else {
                    bonus = (int) Math.floor(bonus * 1.30D);
                }
            }
        }
        return bonus;
    }

    public static int getRangeDefenceLevelNpc(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus = 1;
        if(defender instanceof NPC) {
            if (style == CombatType.RANGED) {
                bonus = (bonus + defenderBonus.rangedef);
            }
        }
        return bonus;
    }

    public static int getAttackRoll(Entity attacker, Entity defender, CombatType style) {
        int effectiveRangeLevel = (int) Math.floor(getEffectiveRanged(attacker, style));

        int equipmentRangeBonus = getGearAttackBonus(attacker, defender, style);

        return (int) Math.floor(effectiveRangeLevel * (equipmentRangeBonus + 64D));
    }

    public static int getDefenceRoll(Entity defender, CombatType style) {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveLevelDefender(defender));

        int equipmentRangeBonus = getRangeDefenceLevelNpc(defender, style);

        return (int) Math.floor(effectiveDefenceLevel * (equipmentRangeBonus + 64D));
    }

}

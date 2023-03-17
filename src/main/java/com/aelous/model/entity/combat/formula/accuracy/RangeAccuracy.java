package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;

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
public class RangeAccuracy {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);//doesHit(entity, enemy, style, 1);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        int attackBonus = getAttackRoll(attacker, defender, style);
        int defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;

        byte[] seed = new byte[16];
        new SecureRandom().nextBytes(seed);
        SecureRandom random = new SecureRandom(seed);

        if (attackBonus > defenceBonus) {
            successfulRoll = (int) 1D - ((defenceBonus + 2D) / (2D * (attackBonus + 1D)));
        } else {
            successfulRoll = attackBonus / (2D * (defenceBonus + 1D));
        }

        double selectedChance = random.nextDouble();

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

    public static int getEffectiveRanged(Entity attacker, Entity defender, CombatType style) {
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
                if (attacker.getAsPlayer().getEquipment().contains(DRAGON_HUNTER_CROSSBOW)) {
                    if (defender instanceof NPC && FormulaUtils.isDragon(defender)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.25D);
                    } else {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.30D);
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

                double bonus = 1;
                Player player = (Player) attacker;
                final Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
                if (weapon != null) {
                    if (Stream.of(TWISTED_BOW).anyMatch(w -> w == weapon.getId())) {

                        double magicLevel = 1;

                        if (attacker.isPlayer()) {
                            if (defender instanceof NPC n) {
                                if (n.combatInfo() != null && n.combatInfo().stats != null)
                                    magicLevel = n.combatInfo().stats.magic > 350 && player.raidsParty != null ? 350 : n.combatInfo().stats.magic > 250D ? 250D : n.combatInfo().stats.magic;
                            } else {
                                magicLevel = defender.getAsPlayer().getSkills().getMaxLevel(Skills.MAGIC);
                            }

                            bonus += 140 + (((10 * 3 * magicLevel) / 10) - 10) - ((Math.floor(3 * magicLevel / 10 - 100)) * 2);
                            bonus = Math.floor(bonus / 100);
                            if (bonus > 2.4D)
                                bonus = (int) 2.4;
                        }
                        if (attacker.isPlayer() && defender.isNpc()) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * bonus);
                        }
                    }
                }
            }
        }

        return effectiveLevel;
    }

    public static int getRangeLevel(Entity attacker) {
        int rangeLevel = 1;
        if (attacker instanceof NPC npc) {
            if (npc.combatInfo() != null && npc.combatInfo().stats != null)
                rangeLevel = npc.combatInfo().stats.ranged;
        } else {
            rangeLevel = attacker.getSkills().level(Skills.RANGED);
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

    public static int getAttackRoll(Entity attacker, Entity defender, CombatType style) {
        int effectiveRangeLevel = (int) Math.floor(getEffectiveRanged(attacker, defender, style));
        int equipmentRangeBonus = getGearAttackBonus(attacker, style);
        return (int) Math.floor(effectiveRangeLevel * (equipmentRangeBonus + 64));
    }

    public static int getDefenceRoll(Entity defender, CombatType style) {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveDefence(defender));
        int equipmentRangeBonus = getGearDefenceBonus(defender, style);
        return (int) Math.floor(effectiveDefenceLevel * (equipmentRangeBonus + 64));
    }
}

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
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;

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
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - (Math.floor(defenceBonus + 2D)) / (2D * (attackBonus + 1D));
        else
            successfulRoll = attackBonus / (2D * (Math.floor(defenceBonus + 1D)));

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    public static int getEquipmentBonusAttacker(Entity attacker, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (attacker instanceof Player) {
            if (!WildernessArea.inWild((Player) attacker) && ((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                bonus = attackerBonus.mage += Math.min(attackerBonus.mage * 3, attackerBonus.mage * attackerBonus.mage);
            } else {
                bonus = attackerBonus.mage;
            }
        } else if (attacker instanceof NPC) {
            bonus = attacker.getAsNpc().combatInfo().bonuses.magic;
        }
        return bonus;
    }

    public static int getEquipmentBonusDefender(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        return defender instanceof NPC ? defender.getAsNpc().combatInfo().bonuses.magicdefence : defenderBonus.magedef;
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
        return attacker instanceof NPC && attacker.getAsNpc().combatInfo() != null ? attacker.getAsNpc().combatInfo().stats.magic : attacker.skills().level(Skills.MAGIC);
    }

    public static int getMagicLevelDefender(Entity defender) {
        return defender instanceof NPC ? defender.getAsNpc().combatInfo().stats.magic : defender.skills().level(Skills.MAGIC);
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
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 3);
                }
                if (FormulaUtils.isUndead(attacker.getCombat().getTarget())) { //UNDEAD BONUSES
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI_25278)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.20D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.10D);
                    }
                    if (attacker.getCombat().getTarget().isNpc()) {
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.05D);
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.10D);
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.OCCULT_NECKLACE_OR)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.05D);
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.THAMMARONS_SCEPTRE) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.ACCURSED_SCEPTRE_A)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.50D);
                        }
                    }
                }
                if (task != null && Slayer.creatureMatches((Player) attacker, attacker.getAsNpc().id())) {
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET_I)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.18D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.20D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.25D);
                    }
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
        int effectiveLevel = (int) Math.floor(((magicDefence * getPrayerBonusDefender(defender) * 0.3D) * 0.7D) + magicLevel);
        int equipmentDefenceBonus = getEquipmentBonusDefender(defender, style);
        return (int) Math.floor(effectiveLevel * (equipmentDefenceBonus + 64));
    }
}

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
        return successful(attacker, defender, style);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        int attackBonus = (int) Math.floor(getAttackRoll(attacker, style));
        int defenceBonus = (int) Math.floor(getDefenceRoll(defender));
        double successfulRoll;
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - ((defenceBonus + 2D) / (2D * (attackBonus + 1D)));
        else
            successfulRoll = attackBonus / (2D * ((defenceBonus + 1D)));

       // System.out.println("ATTK: " + attackBonus);
       // System.out.println("DEF: " + defenceBonus);


       // System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    private static double getPrayerDefenseBonus(Entity defender) {
        double prayerBonus = 1D;
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
        double prayerBonus = 1D;
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
        return prayerBonus;
    }


    public static int getEffectiveDefence(Entity defender) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = defender instanceof NPC ? ((NPC) defender).combatInfo().stats.defence : (int) Math.floor(getDefenceLevel(defender) * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = effectiveLevel + 3;
            case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
        }

        effectiveLevel = effectiveLevel + 8;

        return effectiveLevel;
    }

    public static int getEffectiveMelee(Entity attacker, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getAttackLevel(attacker) * getPrayerAttackBonus(attacker, style));

        switch (fightStyle) {
            case ACCURATE -> effectiveLevel = effectiveLevel + 3;
            case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
        }

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            if (player.getCombatSpecial() != null) {
                double specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
                if (attacker.getAsPlayer().isSpecialActivated()) {
                    effectiveLevel = (int) (effectiveLevel * specialMultiplier);
                }
            }
        }

        effectiveLevel = (int) Math.floor(effectiveLevel + 8);

        if (attacker.isPlayer()) {
            if (style.equals(MELEE)) {
                if (FormulaUtils.regularVoidEquipmentBaseMelee((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.1D);
                }
                if (FormulaUtils.eliteVoidEquipmentMelee((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.125D);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.2D);
                }
                if (weapon != null && FormulaUtils.hasViggorasChainMace(attacker.getAsPlayer())) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.5D);
                }
                if (FormulaUtils.obbyArmour(attacker.getAsPlayer()) && FormulaUtils.hasObbyWeapon(attacker.getAsPlayer())) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.1D);
                }
            /*if (attacker.getAsPlayer().getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                double inquisitorsBonus = 0;
                if (FormulaUtils.wearingInquisitorsPiece(attacker.getAsPlayer())) {
                    inquisitorsBonus *= 1.0025;
                    effectiveLevel = (int) Math.floor(effectiveLevel * inquisitorsBonus);
                }
                if (FormulaUtils.wearingFullInquisitors(attacker.getAsPlayer())) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * (inquisitorsBonus + 1));
                }
            }*/
            }
        }

        //if (defender.isNpc()) {
        //  NPC npc = (NPC) defender;

        //  if (npc.id() == CORPOREAL_BEAST) {
        //     if (weapon != null && getEquipment.corpbeastArmour(weapon) && attackType != null && attackType.equals(AttackType.STAB)) {
        //         effectiveLevel -= 1.5;
        //     }
        //  }

          /*  if (FormulaUtils.isDragon(npc)) {
                if (FormulaUtils.hasDragonHunterLance((Player) attacker)) {
                    effectiveLevel *= 1.20;
                    effectiveLevel = (int) Math.floor(effectiveLevel);
                }
            }

            if (FormulaUtils.hasArchLight((Player) attacker)) {
                if (npc.def() != null && npc.def().name != null && FormulaUtils.isDemon(npc)) {
                    effectiveLevel *= 1.7;
                    effectiveLevel = (int) Math.floor(effectiveLevel);
                }
            }*/
        // }

        return effectiveLevel;
    }

    public static int getAttackLevel(Entity attacker) {
        return attacker instanceof NPC && attacker.getAsNpc().combatInfo().stats != null ? attacker.getAsNpc().combatInfo().stats.attack : attacker.getSkills().level(Skills.ATTACK);
    }

    public static int getDefenceLevel(Entity defender) {
        return defender instanceof NPC && defender.getAsNpc().combatInfo().stats != null ? defender.getAsNpc().combatInfo().stats.defence : defender.getSkills().level(Skills.DEFENCE);
    }

    private static int getGearDefenceBonus(Entity defender) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        final AttackType type = defender instanceof NPC ? AttackType.SLASH : defender.getCombat().getFightType().getAttackType();
        int bonus = 1;
        if (type == AttackType.STAB)
            bonus = (bonus + defenderBonus.stabdef);
        else if (type == AttackType.CRUSH)
            bonus = (bonus + defenderBonus.crushdef);
        else if (type == AttackType.SLASH)
            bonus = (bonus + defenderBonus.slashdef);
        return bonus;
    }

    private static int getGearAttackBonus(Entity attacker) {
        final AttackType type = attacker.getCombat().getFightType().getAttackType();
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (type == AttackType.STAB)
            bonus = (bonus + attackerBonus.stab);
        else if (type == AttackType.CRUSH)
            bonus = (bonus + attackerBonus.crush);
        else if (type == AttackType.SLASH)
            bonus = (bonus + attackerBonus.slash);
        return bonus;
    }

    public static int getAttackRoll(Entity attacker, CombatType style) {
        int effectiveLevel = (int) Math.floor(getEffectiveMelee(attacker, style));
        int effectiveBonus = getGearAttackBonus(attacker);
        return (int) Math.floor(effectiveLevel * (effectiveBonus + 64));
    }

    public static int getDefenceRoll(Entity defender) {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveDefence(defender));
        int effectiveBonus = getGearDefenceBonus(defender);
        return (int) Math.floor(effectiveDefenceLevel * (effectiveBonus + 64));
    }
}

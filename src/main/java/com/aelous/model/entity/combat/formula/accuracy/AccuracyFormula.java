package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;
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

import static com.aelous.model.entity.combat.CombatType.MELEE;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.CORPOREAL_BEAST;

/**
 * @Author Origin
 */
public class AccuracyFormula {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getEffectiveAttackBonus(attacker, defender, style);
        double defenceBonus = getEffectiveDefense(defender, style);
        double successfulRoll;
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - ((defenceBonus + 2D) / (2D * Math.floor(attackBonus + 1D)));
        else
            successfulRoll = (attackBonus / (2D * Math.floor(defenceBonus + 1D)));

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    public static double getEffectiveAttackBonus(Entity attacker, Entity defender, CombatType style) {

            /** X Level **/
            int effectiveAttack = attacker.getSkills().level(Skills.ATTACK);

            double gearBonus = gearBonus(attacker, defender, style);

            effectiveAttack *= gearBonus;

            /** Prayer Bonus **/
            effectiveAttack *= (1 + getEffectivePrayerAttackBonus(attacker, style));

            FightStyle fightStyle = defender.getCombat().getFightType().getStyle();

            switch (fightStyle) {
                case ACCURATE:
                    effectiveAttack += 3.0D;
                    break;
                case CONTROLLED:
                    effectiveAttack += 1.0D;
                    break;
            }

            /** Attack Bonuses **/
            return effectiveAttack * Math.floor(getAttackBonus(attacker, style) + 64D);
        }

    private static double gearBonus(Entity attacker, Entity other, CombatType style) {
        double bonus = 1D;

        if (attacker.isPlayer()) {

            Player player = (Player) attacker;

            final Item weapon = player.getEquipment().get(EquipSlot.WEAPON);

            AttackType attackType = player.getCombat().getFightType().getAttackType();

            if (other.isNpc()) {
                NPC npc = (NPC) other;

                if (npc.id() == CORPOREAL_BEAST) {
                    if (weapon != null && player.getEquipment().corpbeastArmour(weapon) && attackType != null && attackType.equals(AttackType.STAB)) {
                        bonus -= 0.5D;
                    }
                }

                if (player.getEquipment().hasAt(EquipSlot.WEAPON, ARCLIGHT)) {
                    if (npc.def() != null && npc.def().name != null && FormulaUtils.isDemon(npc)) {
                        bonus *= 1.7D;
                    }
                }
            }

            if (player.getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                bonus += 0.15D;
            }

            if (player.getEquipment().contains(ItemIdentifiers.SALVE_AMULETI) || player.getEquipment().contains(SALVE_AMULET_E) || player.getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                bonus += 0.2D;
            }

            if (weapon != null && FormulaUtils.hasMeleeWildernessWeapon(player)) {
                bonus += 0.5D;
            }

            if (FormulaUtils.obbyArmour(player) && FormulaUtils.hasObbyWeapon(player)) {
                bonus += 0.1D;
            }

            if (player.getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                double inquisitorsBonus = 0;
                if (FormulaUtils.wearingInquisitorsPiece(player)) {
                    inquisitorsBonus += 0.0025D;
                    bonus = inquisitorsBonus;
                }
                if (FormulaUtils.wearingFullInquisitors(player)) {
                    bonus += inquisitorsBonus + 1.0D;
                }
            }
        }
        return bonus;
    }

    public static double getEffectiveDefense(Entity defender, CombatType type) {
        double effectiveDefence = 1D;
        if (defender instanceof NPC) {
            NPC npc = ((NPC) defender);
            if (npc.getCombatInfo() != null && npc.getCombatInfo().stats != null)
                effectiveDefence = npc.getCombatInfo().stats.defence;
        } else {
            effectiveDefence = defender.getAsPlayer().getSkills().level(Skills.DEFENCE) + (type == MELEE ? 0 : 9D);
            effectiveDefence *= (1 + getPrayerDefenseBonus(defender));
        }
        return effectiveDefence * Math.floor(getDefenseBonus(defender, type) + 64D);
    }
    private static int getDefenseBonus(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        final AttackType type = defender instanceof NPC ? AttackType.SLASH : defender.getCombat().getFightType().getAttackType();
        int bonus = 0;
        if (style == MELEE) {
            if (type == AttackType.STAB)
                bonus = defenderBonus.stabdef;
            else if (type == AttackType.CRUSH)
                bonus = defenderBonus.crushdef;
            else if (type == AttackType.SLASH)
                bonus = defenderBonus.slashdef;
        }
        return bonus;
    }

    private static int getAttackBonus(Entity attacker, CombatType style) {
        final AttackType type = attacker.getCombat().getFightType().getAttackType();
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == MELEE) {
            if (type == AttackType.STAB)
                bonus = attackerBonus.stab;
            else if (type == AttackType.CRUSH)
                bonus = attackerBonus.crush;
            else if (type == AttackType.SLASH)
                bonus = attackerBonus.slash;
        }
        return bonus;
    }

    private static double getPrayerDefenseBonus(Entity defender) {
        double prayerBonus = 0;
        if (Prayers.usingPrayer(defender, THICK_SKIN))
            prayerBonus += 0.05D; // 5% def level boost
        else if (Prayers.usingPrayer(defender, ROCK_SKIN))
            prayerBonus += 0.10D; // 10% def level boost
        else if (Prayers.usingPrayer(defender, STEEL_SKIN))
            prayerBonus += 0.15D; // 15% def level boost
        if (Prayers.usingPrayer(defender, CHIVALRY))
            prayerBonus += 0.20D; // 20% def level boost
        else if (Prayers.usingPrayer(defender, PIETY))
            prayerBonus += 0.25D; // 25% def level boost
        else if (Prayers.usingPrayer(defender, RIGOUR))
            prayerBonus += 0.25D; // 25% def level boost
        else if (Prayers.usingPrayer(defender, AUGURY))
            prayerBonus += 0.25D; // 25% def level boost
        return prayerBonus;
    }

    private static double getEffectivePrayerAttackBonus(Entity attacker, CombatType style) {
        double prayerBonus = 0;
        if (style == MELEE) {
            if (Prayers.usingPrayer(attacker, CLARITY_OF_THOUGHT))
                prayerBonus += 0.05D; // 5% attack level boost
            else if (Prayers.usingPrayer(attacker, IMPROVED_REFLEXES))
                prayerBonus += 0.10D; // 10% attack level boost
            else if (Prayers.usingPrayer(attacker, INCREDIBLE_REFLEXES))
                prayerBonus += 0.15D; // 15% attack level boost
            else if (Prayers.usingPrayer(attacker, CHIVALRY))
                prayerBonus += 0.15D; // 15% attack level boost
            else if (Prayers.usingPrayer(attacker, PIETY))
                prayerBonus += 0.20D; // 20% attack level boost
        }
        return prayerBonus;
    }
}

package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;

import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.AttackType;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.combat.weapon.FightType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.IntStream;

import static com.aelous.model.entity.combat.CombatType.MELEE;
import static com.aelous.model.entity.combat.CombatType.RANGED;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.CORPOREAL_BEAST;


public class AccuracyFormula {

    public static final SecureRandom srand = new SecureRandom();
    private static final Logger logger = LogManager.getLogger(AccuracyFormula.class);

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);//doesHit(entity, enemy, style, 1);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        double attackBonus = getEffectiveAttackBonus(attacker, defender, style);
        double defenceBonus = getEffectiveDefense(attacker, defender, style);
        double successfulRoll;
        double selectedChance = srand.nextDouble();

        if (attackBonus > defenceBonus)
            successfulRoll = 1D - (defenceBonus + 2D) / (2D * (attackBonus + 1D));
        else
            successfulRoll = attackBonus / (2D * (defenceBonus + 1D));

        if (attacker instanceof Player) {
            if (attacker.getAsPlayer().combatDebug)
               attacker.getAsPlayer().getPacketSender().sendMessage("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));
        } else
            System.out.println("NPCStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + successfulRoll + " rolledChance=" + selectedChance + " sucessful=" + (successfulRoll > selectedChance));

        return successfulRoll > selectedChance;
    }

    public static double getEffectiveAttackBonus(Entity attacker, Entity defender, CombatType style) {

        if (attacker instanceof Player) {
            /** X Level **/
            int effectiveAttack = attacker.skills().level(style == MELEE ? Skills.ATTACK : style == RANGED ? Skills.RANGED : Skills.MAGIC);

            double gearBonus = gearBonus(attacker, defender, style);

            effectiveAttack *= gearBonus;

            /** Prayer Bonus **/
            effectiveAttack *= (1 + getEffectivePrayerAttackBonus(attacker, style));

            FightType fightType = attacker.getCombat().getFightType();

            /** AttackStyle **/
            int styleBonus = fightType.equals(FightStyle.ACCURATE) ? 3 : fightType.equals(FightStyle.CONTROLLED) ? 1 : 0;

            if (styleBonus > 0)
                effectiveAttack += styleBonus;

            /** Attack Bonuses **/
            return effectiveAttack * getAttackBonus(attacker, style);
        } else {
            NPC npc = ((NPC) attacker);
            int effective = 1;
            if (npc.combatInfo() != null && npc.combatInfo().stats != null) {
                effective = style == MELEE ? npc.combatInfo().stats.attack : style == CombatType.MAGIC ? npc.combatInfo().stats.magic : npc.combatInfo().stats.ranged;
                return effective * (1D + 64D);
            }
        }
        return 1D;
    }

    private static double gearBonus(Entity attacker, Entity other, CombatType style) {
        double bonus = 1D;

        if (attacker.isPlayer()) {

            Player player = (Player) attacker;

            final Item helm = player.getEquipment().get(EquipSlot.HEAD);

            final Item weapon = player.getEquipment().get(EquipSlot.WEAPON);

            AttackType attackType = player.getCombat().getFightType().getAttackType();

            if (weapon != null) {
                /**
                 * TBOW effects
                 */
                if (Arrays.asList(TWISTED_BOW).stream().anyMatch(w -> w.intValue() == weapon.getId())) {

                    double magicLevel = 0;

                    if (other instanceof NPC) {
                        NPC n = (NPC) other;
                        if (n.combatInfo() != null && n.combatInfo().stats != null)
                            magicLevel = n.combatInfo().stats.magic > 350 && player.raidsParty != null ? 350 : n.combatInfo().stats.magic > 250D ? 250D : n.combatInfo().stats.magic;
                    } else {
                        magicLevel = other.getAsPlayer().skills().getMaxLevel(Skills.MAGIC);
                    }

                    bonus += 140 + ((3 * magicLevel - 10) / 100) - (((3 * magicLevel / 10) - 100)) * ((3 * magicLevel / 10) - 100) / 100;
                    bonus /= 100;
                    if (bonus > 2.4)
                        bonus = 2.4D;
                }
            }

            if (style.equals(MELEE) && (FormulaUtils.voidMelee(player)))
                bonus = 1.10D;
           // else if (style.equals(RANGED) && (FormulaUtils.voidRanger(player)))
             //   bonus = 1.10;
            else if (style.equals(CombatType.MAGIC) && (FormulaUtils.voidMagic(player)))
                bonus = 1.45D; //45%

            if (other.isNpc()) {
                NPC npc = (NPC) other;

                if (npc.id() == CORPOREAL_BEAST) {
                    if (weapon != null && player.getEquipment().corpbeastArmour(weapon) && attackType != null && attackType.equals(AttackType.STAB)) {
                        bonus -= 0.5D;
                    }
                }

                if (helm != null && Slayer.creatureMatches(player, npc.id())) {
                    if (player.getEquipment().wearingSlayerHelm() || (IntStream.range(8901, 8921).anyMatch(id -> id == helm.getId()))) {
                        bonus += 0.125D;
                    }
                }

                //Arclight
                if (player.getEquipment().hasAt(EquipSlot.WEAPON, ARCLIGHT)) {
                    if (npc.def() != null && npc.def().name != null && FormulaUtils.isDemon(npc)) {
                        bonus += 0.7D;
                    }
                }

                //Dragon hunter crossbow and lance
                if (player.getEquipment().hasAt(EquipSlot.WEAPON, DRAGON_HUNTER_CROSSBOW) || player.getEquipment().hasAt(EquipSlot.WEAPON, DRAGON_HUNTER_LANCE)) {
                    if (npc.def() != null && npc.def().name != null && FormulaUtils.isDragon(npc)) {
                        bonus += 0.3D;
                    }
                }

                if (npc.id() == 6593 && style.equals(CombatType.MAGIC)) {
                    bonus += 0.5D;
                }

                if (player.getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                    bonus += 0.15D;
                }

                if (player.getEquipment().contains(ItemIdentifiers.SALVE_AMULETI) || player.getEquipment().contains(SALVE_AMULET_E) || player.getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                    bonus += 0.2D;
                }

                if (weapon != null && (FormulaUtils.hasCrawsBow(player) || FormulaUtils.hasViggorasChainMace(player))) {
                    bonus += 0.5D;
                }

                if (weapon != null && FormulaUtils.hasThammaronSceptre(player)) {
                    bonus += 1.00D;
                }

                if (FormulaUtils.obbyArmour(player) && FormulaUtils.hasObbyWeapon(player)) {
                    bonus += 0.1D;
                }

                //PetDefinitions accuracy bonus

                final boolean anyCombatStyle = style.equals(MELEE) || style.equals(RANGED) || style.equals(CombatType.MAGIC);


                //Custom effect not from OSRS, OSRS is 2.5% this is 5%
                if (player.getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                    if (player.getEquipment().hasAt(EquipSlot.HEAD, INQUISITORS_GREAT_HELM)) {
                        bonus += 0.01D;//1% accuracy boost
                    }

                    if (player.getEquipment().hasAt(EquipSlot.BODY, INQUISITORS_HAUBERK)) {
                        bonus += 0.01D;//1% accuracy boost
                    }

                    if (player.getEquipment().hasAt(EquipSlot.LEGS, INQUISITORS_PLATESKIRT)) {
                        bonus += 0.01D;//1% accuracy boost
                    }

                    if (player.getEquipment().hasAt(EquipSlot.HEAD, INQUISITORS_GREAT_HELM) || player.getEquipment().hasAt(EquipSlot.BODY, INQUISITORS_HAUBERK) || player.getEquipment().hasAt(EquipSlot.LEGS, INQUISITORS_PLATESKIRT)) {
                        bonus += 0.02D;//2% accuracy boost
                    }
                }
            }

            if (player.getEquipment().containsAny(11998, 12000) && style.equals(CombatType.MAGIC)) {
                bonus += 0.10D;
            }

        }
        return bonus;
    }

    public static double getEffectiveDefense(Entity attacker, Entity defender, CombatType type) {
        double effectiveDefence = 1D;
        if (defender instanceof NPC) {
            NPC npc = ((NPC) defender);
            if (npc.combatInfo() != null && npc.combatInfo().stats != null)
                effectiveDefence = npc.combatInfo().stats.defence;
        } else {
            effectiveDefence = defender.getAsPlayer().skills().level(Skills.DEFENCE) + (type == MELEE ? 0 : 9D);
            effectiveDefence *= (1 + getPrayerDefenseBonus(defender));
        }
        return effectiveDefence * (getDefenseBonus(defender, type) + 64D);
    }

    private static double getPrayerDefenseBonus(Entity defender) {
        double prayerBonus = 0D;
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
        } else if (style == RANGED) {
            bonus = defenderBonus.rangedef;
        } else if (style == CombatType.MAGIC) {
            bonus = defenderBonus.magedef;
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
        } else if (style == RANGED) {
            bonus = attackerBonus.range;
        } else if (style == CombatType.MAGIC) {
            bonus = attackerBonus.mage;
        }
        return (bonus + 64);
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
        } else if (style == RANGED) {
            if (Prayers.usingPrayer(attacker, SHARP_EYE))
                prayerBonus += 0.05D; // 5% range level boost
            else if (Prayers.usingPrayer(attacker, HAWK_EYE))
                prayerBonus += 0.10D; // 10% range level boost
            else if (Prayers.usingPrayer(attacker, EAGLE_EYE))
                prayerBonus += 0.15D; // 15% range level boost
            else if (Prayers.usingPrayer(attacker, RIGOUR))
                prayerBonus += 0.20D; // 20% range level boost
        } else if (style == CombatType.MAGIC) {
            if (Prayers.usingPrayer(attacker, MYSTIC_WILL))
                prayerBonus += 0.05D; // 5% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_LORE))
                prayerBonus += 0.10D; // 10% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_MIGHT))
                prayerBonus += 0.15D; // 15% magic level boost
            else if (Prayers.usingPrayer(attacker, AUGURY))
                prayerBonus += 0.25D; // 25% magic level boost
        }
        return prayerBonus;
    }

}

package com.aelous.model.entity.combat.formula.maxhit;

import com.aelous.model.World;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.ranged.RangedData;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;

import static com.aelous.utility.ItemIdentifiers.*;

public class RangedMaxHitFormula {
    public RangedMaxHitFormula() {
    }

    public int calculateMaximumHit(Player player, boolean isSpecialActivated) {
        double specialMultiplier = player.getCombatSpecial() == null ? 0 : player.getCombatSpecial().getSpecialMultiplier();
        double calculateMaxHit = 0.5 + (((double) calculateEffectiveRangedStrength(player, getRangedLevel(player), getPrayerBonus(player), getAttackStyleBonus(player)) * (getEquipmentRangedStrength(player) + 64)) / 640);
        calculateMaxHit *= this.getEquipmentBonus(player);
        int maxHit = (int) calculateMaxHit;
        return isSpecialActivated ? (int) (specialMultiplier * maxHit) : maxHit;
    }

    private int getEquipmentRangedStrength(Player player) {
        RangedData.RangedWeapon rangeWeapon = player.getCombat().getRangedWeapon();
        boolean ignoreArrows = rangeWeapon != null && rangeWeapon.ignoreArrowsSlot();
        EquipmentInfo.Bonuses bonuses = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo(), !ignoreArrows);
        return bonuses.rangestr;
    }

    private int calculateEffectiveRangedStrength(Player player, int rangedLevel, double prayerBonus, int atkStyle) {
        double innerCalculation = Math.floor(((rangedLevel + atkStyle + 8) * prayerBonus));
        double voidModifier = getVoidModifier(player);
        innerCalculation *= voidModifier;
        return (int) innerCalculation;
    }

    private int getRangedLevel(Player player) {
        return player.skills().level(Skills.RANGED);
    }

    private double getPrayerBonus(Player player) {
        double prayerBonus = 1D;
        if (Prayers.usingPrayer(player, Prayers.SHARP_EYE)) {
            prayerBonus = 1.05D;
        } else if (Prayers.usingPrayer(player, Prayers.HAWK_EYE)) {
            prayerBonus = 1.10D;
        } else if (Prayers.usingPrayer(player, Prayers.EAGLE_EYE)) {
            prayerBonus = 1.15D;
        } else if (Prayers.usingPrayer(player, Prayers.RIGOUR)) {
            prayerBonus = 1.23D;
        }

        boolean ignoreMSBBonus = (player.getEquipment().hasAt(EquipSlot.WEAPON, MAGIC_SHORTBOW)
            || player.getEquipment().hasAt(EquipSlot.WEAPON, MAGIC_SHORTBOW_I)) && player.isSpecialActivated();

        if (ignoreMSBBonus) {
            prayerBonus = 1.0D;
        }

        return prayerBonus;
    }

    private int getAttackStyleBonus(Player player) {
        FightStyle style = player.getCombat().getFightType().getStyle();
        return style.equals(FightStyle.ACCURATE) ? 3 : 0;
    }

    private double getEquipmentBonus(Player player) {
        double otherBonus = 1.0;

        boolean hasCrystalHelm = player.getEquipment().hasAt(EquipSlot.HEAD, CRYSTAL_HELM);
        boolean hasCrystalBody = player.getEquipment().hasAt(EquipSlot.BODY, CRYSTAL_BODY);
        boolean hasCrystalLegs = player.getEquipment().hasAt(EquipSlot.LEGS, CRYSTAL_LEGS);

        if (hasCrystalHelm) {
            otherBonus = 1.025;
        }

        if (hasCrystalBody) {
            otherBonus = 1.075;
        }

        if (hasCrystalLegs) {
            otherBonus = 1.05;
        }

        if (player.isSpecialActivated() && FormulaUtils.wearingDarkBowWithDragonArrows(player)) {
            otherBonus *= 1.02;
        }

        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
        Entity target = player.getCombat().getTarget();

        if (target instanceof NPC npc) {
            boolean isSlayerMatch = Slayer.creatureMatches(player, npc.id()) || npc.isCombatDummy();
            boolean isUndead = FormulaUtils.isUndead(npc) || npc.isCombatDummy();

            if (isSlayerMatch && hasCrystalBody && hasCrystalLegs) {
                otherBonus *= (1.15) * (1.075) * (1.05);
            }

            if (isUndead) {
                if (FormulaUtils.hasSalveAmulet(player)) {
                    otherBonus *= 1.15;
                } else if (FormulaUtils.hasSalveAmuletE(player) || FormulaUtils.hasSalveAmuletEI(player) || FormulaUtils.hasSalveAmuletI(player)) {
                    otherBonus *= 1.20;
                }
            }

            if (FormulaUtils.hasRangedWildernessWeapon(player) && WildernessArea.inWild(player)) {
                otherBonus *= 1.50;
            }
        }

        if (weapon != null && weapon.getId() == TWISTED_BOW && target instanceof NPC npcTarget) {
            int magicLevel = npcTarget.getCombatInfo() != null && npcTarget.getCombatInfo().stats != null
                ? npcTarget.getCombatInfo().stats.magic
                : 0;

            double damage = 250D + (((10 * 3 * magicLevel) / 10D) - 14) - (Math.floor((3 * magicLevel / 10D) - 140) * 2);
            damage /= 100;
            damage = Math.min(250D, damage);
            otherBonus *= Math.min(2D, 1D + damage);
        }

        return otherBonus;
    }


    private double getVoidModifier(Player player) {
        if (FormulaUtils.regularVoidEquipmentBaseRanged(player)) {
            return 1.10D;
        }

        if (FormulaUtils.eliteVoidEquipmentRanged(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged(player)) {
            return 1.125D;
        }

        return 1.0D;
    }
}

package com.aelous.model.entity.combat.formula.maxhit;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;

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
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 *
 */
public class RangeMaxHit {

    public static double getBaseDamage(Player player, boolean factorInAmmoRangeStr) {
        EquipmentInfo.Bonuses bonuses = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo(), !factorInAmmoRangeStr);
        return (1.3 + (getEffectiveRanged(player) / 10) + (bonuses.rangestr / 80D) + (getEffectiveRanged(player) * bonuses.rangestr / 640));
    }

    public static int getRangedlevel(Player player) {
        return player.getSkills().level(Skills.RANGED);
    }

    public static double getEffectiveRanged(Player player) {
        return Math.floor(((getRangedlevel(player)) * getPrayerBonus(player)) * getOtherBonus(player, true)) + getStyleBonus(player);
    }

    public static double getPrayerBonus(Player player) {
        double prayerBonus = 1;
        if (Prayers.usingPrayer(player, Prayers.SHARP_EYE)) {
            prayerBonus = 1.05;
        } else if (Prayers.usingPrayer(player, Prayers.HAWK_EYE)) {
            prayerBonus = 1.10;
        } else if (Prayers.usingPrayer(player, Prayers.EAGLE_EYE)) {
            prayerBonus = 1.15;
        } else if (Prayers.usingPrayer(player, Prayers.RIGOUR)) {
            prayerBonus = 1.23;
        }

        var isMSB = player.getEquipment().hasAt(EquipSlot.WEAPON, MAGIC_SHORTBOW) || player.getEquipment().hasAt(EquipSlot.WEAPON, MAGIC_SHORTBOW_I);
        var ignoreMSBBonus = isMSB && player.isSpecialActivated();

        if (ignoreMSBBonus) {
            prayerBonus = 1.0;
        }

        return prayerBonus;
    }

    public static int getStyleBonus(Player player) {
        FightStyle style = player.getCombat().getFightType().getStyle();
        return style.equals(FightStyle.ACCURATE) ? 3 : 0;
    }

    public static double getOtherBonus(Player player, boolean includeNpcMax) {
        double otherBonus = 1;

        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);

        Entity target = player.getCombat().getTarget();

        if (FormulaUtils.regularVoidEquipmentBaseRanged(player)) {
            otherBonus *= 1.10;
        }

        // Elite Void effect adds extra 2.5%.
        if (FormulaUtils.eliteVoidEquipmentRanged(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged(player)) {
            otherBonus *= 1.125;
        }

        if (player.getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
            otherBonus *= 1.15;
        }

        if (player.getEquipment().contains(ItemIdentifiers.SALVE_AMULETI) || player.getEquipment().contains(SALVE_AMULET_E) || player.getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
            otherBonus *= 1.20;
        }

        // Append the Twisted bow computation, if we have enough data..
        if (weapon != null && (weapon.getId() == TWISTED_BOW) && target != null && target.isNpc() && includeNpcMax) {
            int magicLevel = 0;

            if (((NPC) target).getCombatInfo() != null && ((NPC) target).getCombatInfo().stats != null)
                magicLevel = ((NPC) target).getCombatInfo().stats.magic;

            //double damage = (250D + ((((10 * 3) * magicLevel) / 10D) - 14) / 100D) - (((((3 * magicLevel) /10D) - 140) / 100D) * 2);
            double damage = 250D + (((10*3*magicLevel) / 10D) - 14) - ((Math.floor((3 * magicLevel / 10D) - 140)) * 2);
            damage /= 100;
            damage = Math.min(250D, damage);
            otherBonus *= Math.min(2D, 1D + damage);
        }

        if (player.getEquipment().hasAt(EquipSlot.HEAD, CRYSTAL_HELM)) {
            otherBonus *= 1.025;//2.5% damage boost
        }

        if (player.getEquipment().hasAt(EquipSlot.BODY, CRYSTAL_BODY)) {
            otherBonus *= 1.075;//7.5% damage boost
        }

        if (player.getEquipment().hasAt(EquipSlot.LEGS, CRYSTAL_LEGS)) {
            otherBonus *= 1.05;//5.0% damage boost
        }

        if ((player.getEquipment().hasAt(EquipSlot.WEAPON, DRAGON_HUNTER_CROSSBOW) && target != null && includeNpcMax)) {
            if (target.isNpc() && target.getAsNpc().id() == NpcIdentifiers.COMBAT_DUMMY || FormulaUtils.isDragon(target)) {
                otherBonus *= 1.25;
            } else {
                otherBonus *= 1.30;
            }
        }

        var weakSpot = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WEAK_SPOT);

        if (weakSpot && target != null && target.isNpc()) {
            if (Slayer.creatureMatches(player, target.getAsNpc().id())) {
                otherBonus += 0.10;
            }
        }

        //Craws Bow
        if (FormulaUtils.hasCrawsBow(player) && target != null && target.isNpc() && includeNpcMax) {
            otherBonus += 0.50;
        }

        return otherBonus;
    }

    public static int maxHit(Player player, Entity target, boolean factorInAmmoRangeStr, boolean includeNpcMax) {
        int maxHit;
        maxHit = !factorInAmmoRangeStr ? (int) Math.floor(getBaseDamage(player, false)) : (int) Math.floor(getBaseDamage(player, true));

        double specialMultiplier = player.getCombatSpecial() == null ? 0 : player.getCombatSpecial().getSpecialMultiplier();

        if (player.isSpecialActivated()) {
            maxHit = (int) (specialMultiplier * maxHit);
        }

        return maxHit;
    }
}


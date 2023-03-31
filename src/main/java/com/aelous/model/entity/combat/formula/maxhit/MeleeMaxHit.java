package com.aelous.model.entity.combat.formula.maxhit;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.AttackType;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Origin
 * @Since January 16 2022
 */
public class MeleeMaxHit {

    /**
     * The max hit
     * @param player The player performing the hit
     * @param includeNpcMax The npc is a PvP combat dummy
     * @return return the max hit based on the given calculations
     */

    public static int maxHit(Player player, boolean includeNpcMax) {

        double specialMultiplier = player.getCombatSpecial() == null ? 1 : player.getCombatSpecial().getSpecialMultiplier();
        /**
         * Max Hit
         *
         */

        int maxHit = (int) Math.floor(getBaseDamage(player) * slayerPerkBonus(player));

        if (player.isSpecialActivated()) {
            maxHit = (int) (maxHit * specialMultiplier);
        }

        return (int) Math.floor(maxHit);
    }

    public static int getBaseDamage(Player player) {
        return (int) (Math.floor(0.5 + (getEffectiveStrength(player)) * (getStrengthBonus(player) + 64) + 320) / 640.0);
    }

    public static int getStrengthBonus(Player player) {
        EquipmentInfo.Bonuses bonuses = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        return bonuses.str;
    }

    public static int getStrengthLevel(Player player) {
        return player.getSkills().level(Skills.STRENGTH);
    }

    private static double getPrayerBonus(Player player) {
        /**
         * Prayer Bonus
         *
         */
        double prayerBonus = 1;
        if (Prayers.usingPrayer(player, Prayers.BURST_OF_STRENGTH)) {
            prayerBonus *= 1.05;
        } else if (Prayers.usingPrayer(player, Prayers.SUPERHUMAN_STRENGTH)) {
            prayerBonus *= 1.10;
        } else if (Prayers.usingPrayer(player, Prayers.ULTIMATE_STRENGTH)) {
            prayerBonus *= 1.15;
        } else if (Prayers.usingPrayer(player, Prayers.CHIVALRY)) {
            prayerBonus *= 1.18;
        } else if (Prayers.usingPrayer(player, Prayers.PIETY)) {
            prayerBonus *= 1.23;
        }
        return prayerBonus;
    }

    public static int getStyleBonus(Player player) {
        FightStyle style = player.getCombat().getFightType().getStyle();
        return style.equals(FightStyle.AGGRESSIVE) ? 3 : style.equals(FightStyle.ACCURATE) ? 1 : 0;
    }

    public static double slayerPerkBonus(Player player) {
        Entity target = player.getCombat().getTarget();

        double slayerPerkBonus = 1.0;

        var weakSpot = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WEAK_SPOT);
        if(weakSpot && target != null && target.isNpc()) {
            if(Slayer.creatureMatches(player, target.getAsNpc().id())) {
                slayerPerkBonus *= 1.10;
            }
        }
        return slayerPerkBonus;
    }

    public static double getPetBonus(Player player, boolean includeNpcMax) {
        double petBonus = 1;
        Entity target = player.getCombat().getTarget();
        /**
         * PetDefinitions bonuses
         *
         */

        return petBonus;
    }

    public static double getOtherBonus(Player player, boolean includeNpcMax) {

        FightStyle style = player.getCombat().getFightType().getStyle();
        double otherBonus = 1;

        Entity target = player.getCombat().getTarget();

        /**
         * Other bonuses
         *
         */
        if (FormulaUtils.regularVoidEquipmentBaseMelee(player)) {
            otherBonus *= 1.10;
        }

        if (FormulaUtils.eliteVoidEquipmentMelee(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee(player)) {
            otherBonus *= 1.125;
        }

        if (FormulaUtils.fullDharok(player)) {
            int hitpoints = player.hp();
            double max = player.maxHp();
            double mult = Math.max(0, ((max - (double) hitpoints) / max) * 100D) + 100D;
            otherBonus *= (mult / 100);
        }

        var wearingAnyBlackMask = FormulaUtils.wearingBlackMask(player) || FormulaUtils.wearingBlackMaskImbued(player) || player.getEquipment().wearingSlayerHelm();

        if(wearingAnyBlackMask && target != null && target.isNpc() && includeNpcMax) {
            NPC npc = target.getAsNpc();
            if(npc.id() == NpcIdentifiers.COMBAT_DUMMY) {
                otherBonus *= 1.1667;
            }

            if(Slayer.creatureMatches(player, npc.id())) {
                otherBonus *= 1.1667;
            }
        }

        if(player.getEquipment().hasAt(EquipSlot.AMULET, SALVE_AMULETEI) && !wearingAnyBlackMask && target != null && includeNpcMax) {
            if(target.isNpc() && target.getAsNpc().id() == NpcIdentifiers.COMBAT_DUMMY) {
                otherBonus *= 1.20;
            }

            if(FormulaUtils.isUndead(target)) {
                otherBonus *= 1.20;
            }
        }

        if (player.getEquipment().hasAt(EquipSlot.WEAPON, RED_SLAYER_HELMET_I) && target != null && target.isNpc() && includeNpcMax) {
            otherBonus *= 1.10;
        }

        if(player.getEquipment().hasAt(EquipSlot.WEAPON, ARCLIGHT) && target != null && includeNpcMax) {
            if(target.isNpc() && target.getAsNpc().id() == NpcIdentifiers.COMBAT_DUMMY) {
                otherBonus *= 1.70;
            }

            if(FormulaUtils.isDemon(target)) {
                otherBonus *= 1.70;
            }
        }

        if(player.getEquipment().hasAt(EquipSlot.WEAPON, DARKLIGHT) && target != null && includeNpcMax) {
            if(target.isNpc() && target.getAsNpc().id() == NpcIdentifiers.COMBAT_DUMMY) {
                otherBonus *= 1.60;
            }

            if(FormulaUtils.isDemon(target)) {
                otherBonus *= 1.60;
            }
        }

        if(player.getEquipment().hasAt(EquipSlot.WEAPON, DARKLIGHT) && target != null && includeNpcMax) {
            if(target.isNpc() && target.getAsNpc().id() == NpcIdentifiers.COMBAT_DUMMY) {
                otherBonus *= 1.175;
            }

            if(target.isNpc() && target.getAsNpc().def() != null && (target.getAsNpc().def().name.equalsIgnoreCase("Kurask") || target.getAsNpc().def().name.equalsIgnoreCase("Turoth"))) {
                otherBonus *= 1.175;
            }
        }

        if (FormulaUtils.obbyArmour(player) && FormulaUtils.hasObbyWeapon(player)) {
            otherBonus *= 1.10;
        }

        if(FormulaUtils.berserkerNecklace(player) && FormulaUtils.hasObbyWeapon(player)) {
            otherBonus *= 1.10;
        }

        if (player.getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
            if (player.getEquipment().hasAt(EquipSlot.HEAD, INQUISITORS_GREAT_HELM) || player.getEquipment().hasAt(EquipSlot.BODY, INQUISITORS_HAUBERK) || player.getEquipment().hasAt(EquipSlot.LEGS, INQUISITORS_PLATESKIRT)) {
                otherBonus *= 1.05;
            }
        }

        if(player.getEquipment().hasAt(EquipSlot.WEAPON, DRAGON_HUNTER_LANCE) && target != null && includeNpcMax) {
            if(target.isNpc() && target.getAsNpc().id() == NpcIdentifiers.COMBAT_DUMMY || FormulaUtils.isDragon(target)) {
                otherBonus *= 1.20;
            }

            if(FormulaUtils.isDragon(target)) {
                otherBonus *= 1.20;
            }
        }

        //• Gadderhammer: 1.25 or 2.0 vs shades
        if(player.getEquipment().hasAt(EquipSlot.WEAPON, GADDERHAMMER)) {
            if(target != null && target.isNpc()) {
                NPC npc = target.getAsNpc();
                NpcDefinition def = npc.def();
                var isShade = def != null && def.name.equalsIgnoreCase("Shade");
                otherBonus *= isShade ? 1.25 : 2.00;
            }
        }

        if (FormulaUtils.hasMeleeWildernessWeapon(player) && target != null && target.isNpc() && includeNpcMax) {
            otherBonus *= 1.50;
        }

        return otherBonus;
    }

    public static int getEffectiveStrength(Player player) {
        return (int) (Math.floor(((((getStrengthLevel(player)) * getPrayerBonus(player)) + getStyleBonus(player)) + 8) * getOtherBonus(player, true)) * getPetBonus(player, true));
    }
}

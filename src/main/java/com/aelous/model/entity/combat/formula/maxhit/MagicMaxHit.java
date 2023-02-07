package com.aelous.model.entity.combat.formula.maxhit;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 *
 */
public class MagicMaxHit {

   /* public static int getMagicLevel(Player player) {
        return player.skills().level(Skills.MAGIC);
    }

    public static boolean hasTomeOfFire(Player player) {
        return player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
    }

    public static double getMagicMultiplier(Player player) {
        EquipmentInfo.Bonuses b = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        return 1 + ((b.magestr > 0 ? b.magestr : 1.0) / 100);
    }

    public static int getMagicBonuses(Player player) {
        EquipmentInfo.Bonuses b = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        return b.magestr;
    }

    public static double baseMaxHit(Player player, boolean includeNpcMax) {
        CombatSpell spell = player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() : player.getCombat().getAutoCastSpell();
        int spell_maxhit = spell.baseMaxHit();
        Entity target = player.getCombat().getTarget();
        String spell_name = spell.name();
        if (spell_name.equals("Magic Dart")) {
            spell_maxhit = (int) (10 + Math.floor(getMagicLevel(player) / 10D));
        }
        if (spell_name.equals("Trident of the seas")) {
            spell_maxhit = 20;
            spell_maxhit = (int) Math.round((Math.max(spell_maxhit, spell_maxhit + (Math.max(0, getMagicLevel(player) - 75)) / 3)) * (1 + (getMagicBonuses(player) / 100.0)));
        }
        if (spell_name.equals("Trident of the swamp")) {
            spell_maxhit = 23;
            spell_maxhit = (int) Math.round((Math.max(spell_maxhit, spell_maxhit + (Math.max(0, getMagicLevel(player) - 75)) / 3)) * (1 + (getMagicBonuses(player) / 100.0)));
        }
        if (spell_name.equals("Saradomin Strike") || spell_name.equals("Claws of Guthix") || spell_name.equals("Flames of Zamorak")) {
            if (player.getTimers().has(TimerKey.CHARGE_SPELL)) {
                spell_maxhit = 30;
            }
        }
        if (spell_name.toLowerCase().contains("fire") && hasTomeOfFire(player)) {
            spell_maxhit *= 1.50;
        }

        if (player.getEquipment().hasAt(EquipSlot.AMULET, OCCULT_NECKLACE_OR) || player.getEquipment().hasAt(EquipSlot.HANDS, TORMENTED_BRACELET_OR)) {
            spell_maxhit += 1;
        }

        //After all modifiers spell max hits
        if (spell_name.equals("Petrificus Totalus") && target != null && target.isPlayer()) {
            spell_maxhit = 40;
        }

        if (spell_name.equals("Cruciatus Curse") && target != null && target.isPlayer()) {
            spell_maxhit = 41;
        }

        if (spell_name.equals("Expelliarmus") && target != null && target.isPlayer()) {
            spell_maxhit = 50;
        }

        if (spell_name.equals("Sectumsempra") && target != null && target.isPlayer()) {
            spell_maxhit = 55;
        }

        if (spell_name.equals("Avada Kedavra") && target != null && target.isPlayer()) {
            spell_maxhit = 82;
        }

        double multiplier = getMagicMultiplier(player);

        // #Custom slayer effects
        var weakSpot = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WEAK_SPOT);
        if (weakSpot && target != null && target.isNpc()) {
            if (Slayer.creatureMatches(player, target.getAsNpc().id())) {
                multiplier += 0.10;
            }
        }

        int weapon = player.getEquipment().get(3) == null ? -1 : player.getEquipment().get(3).getId();
        if (spell_name.equals("Volatile spell")) {
            int baseLevel = getMagicLevel(player);
            if (baseLevel > 99)
                baseLevel = 99;
            double levelTimes = 0.67;
            multiplier -= 0.15;
            spell_maxhit = (int) (baseLevel * levelTimes);
        }


        if (FormulaUtils.hasThammaronSceptre(player) && target != null && target.isNpc() && includeNpcMax) {
            multiplier += 0.25;
        }

        if (player.getEquipment().hasAt(EquipSlot.WEAPON, TURQUOISE_SLAYER_HELMET_I) && target != null && target.isNpc() && includeNpcMax) {
            multiplier += 0.10;
        }

        if (FormulaUtils.wearingTwistedSlayerHelmetI(player) && target != null && target.isNpc() && includeNpcMax) {
            multiplier += 0.10;
        }
        if (spell_name.equals("Sanguinesti spell")) {
            boolean holy_staff = weapon == ItemIdentifiers.HOLY_SANGUINESTI_STAFF;
            if (holy_staff) {
                spell_maxhit += 10;
            }
            spell_maxhit += multiplier;
        }
        return (int) Math.floor(spell_maxhit);
    }

    public static int maxHit(Player player, boolean includeNpcMax) {
        int maxHit = (int) baseMaxHit(player, true);
        return (int) Math.floor(maxHit);
    }*/

    public static int maxHit(Player player, boolean includeNpcMax) {
        int baseMaxHit = 0;
        CombatSpell spell = player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() : player.getCombat().getAutoCastSpell();
        if (spell != null) {
            EquipmentInfo.Bonuses b = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            Entity target = player.getCombat().getTarget();
            String spell_name = spell.name();
            int level = player.skills().level(Skills.MAGIC);

            //Find the base maximum damage a spell can deal.
            int spell_maxhit = spell.baseMaxHit();

            //• Slayer dart
            if (spell_name.equals("Magic Dart")) {
                spell_maxhit = (int) (10 + Math.floor(level/10D));
            }

            //• Trident of the seas
            if (spell_name.equals("Trident of the seas")) {
                spell_maxhit = 20;
                spell_maxhit = (int) Math.round((Math.max(spell_maxhit, spell_maxhit + (Math.max(0, level - 75)) / 3)) * (1 + (b.magestr / 100.0)));
            }

            //• Trident of the swamp
            if (spell_name.equals("Trident of the swamp")) {
                spell_maxhit = 23;
                spell_maxhit = (int) Math.round((Math.max(spell_maxhit, spell_maxhit + (Math.max(0, level - 75)) / 3)) * (1 + (b.magestr / 100.0)));
            }

            //• God spells (level 60) in combination with Charge (level 80): the base max hit is 30.
            if (spell_name.equals("Saradomin Strike") || spell_name.equals("Claws of Guthix") || spell_name.equals("Flames of Zamorak")) {
                if (player.getTimers().has(TimerKey.CHARGE_SPELL)) {
                    spell_maxhit = 30;
                }
            }

            if (spell_name.toLowerCase().contains("fire") && hasTomeOfFire) {
                spell_maxhit *= 1.50;
            }

            double multiplier = 1 + ((b.magestr > 0 ? b.magestr : 1.0) / 100);

            if (FormulaUtils.hasThammaronSceptre(player) && target != null && target.isNpc() && includeNpcMax) {
                multiplier += 0.25;
            }

            if (player.getEquipment().hasAt(EquipSlot.WEAPON, TURQUOISE_SLAYER_HELMET_I) && target != null && target.isNpc() && includeNpcMax) {
                multiplier += 0.10;
            }

            if (FormulaUtils.wearingTwistedSlayerHelmetI(player) && target != null && target.isNpc() && includeNpcMax) {
                multiplier += 0.10;
            }

            // #Custom slayer effects
            var weakSpot = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WEAK_SPOT);
            if (weakSpot && target != null && target.isNpc()) {
                if (Slayer.creatureMatches(player, target.getAsNpc().id())) {
                    multiplier += 0.10;
                }
            }

            int weapon = player.getEquipment().get(3) == null ? -1 : player.getEquipment().get(3).getId();
            if (spell_name.equals("Volatile spell")) {
                int baseLevel = level;
                if (baseLevel > 99)
                    baseLevel = 99;
                double levelTimes = 0.67;
                multiplier -= 0.15;
                spell_maxhit = (int) (baseLevel * levelTimes);
            }

            int maxHit = (int) Math.round(spell_maxhit * multiplier);

            // #Custom Armour effects
            if (player.getEquipment().hasAt(EquipSlot.AMULET, OCCULT_NECKLACE_OR) || player.getEquipment().hasAt(EquipSlot.HANDS, TORMENTED_BRACELET_OR)) {
                maxHit += 1;
            }

            if (spell_name.equals("Sanguinesti spell")) {
                boolean holy_staff = weapon == ItemIdentifiers.HOLY_SANGUINESTI_STAFF;
                if (holy_staff) {
                    maxHit += 10;
                }
            }
            return maxHit;
        }
        return baseMaxHit;
    }
}

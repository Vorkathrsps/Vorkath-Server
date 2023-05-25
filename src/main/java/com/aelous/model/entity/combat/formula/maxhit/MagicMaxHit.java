package com.aelous.model.entity.combat.formula.maxhit;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 */
public class MagicMaxHit {

    public static int maxHit(Player player, boolean includeNpcMax) {
        try {
            int baseMaxHit = 0;
            CombatSpell spell = player.getCombat().getCastSpell();
            if (spell == null) {
                spell = player.getCombat().getAutoCastSpell();
            }
            if (spell == null) {
                spell = player.getCombat().getPoweredStaffSpell();
            }
            if (spell != null) {
                EquipmentInfo.Bonuses b = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
                boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
                Entity target = player.getCombat().getTarget();
                String spellName = spell.name().toLowerCase();
                int level = player.getSkills().level(Skills.MAGIC);

                // Find the base maximum damage a spell can deal.
                int spellMaxHit = spell.baseMaxHit();

                // Slayer dart
                if (spellName.equals("magic dart") || player.getEquipment().hasAt(EquipSlot.WEAPON, ACCURSED_SCEPTRE_A)) {
                    spellMaxHit = (int) (10 + Math.floor(level / 10D));
                }

                // Trident of the seas
                if (spellName.equals("trident of the seas")) {
                    spellMaxHit = 20;
                    spellMaxHit = (int) Math.round((Math.max(spellMaxHit, spellMaxHit + (Math.max(0, level - 75)) / 3)) * (1 + (b.magestr / 100.0)));
                }

                if (player.getCombat().getPoweredStaffSpell() != null && player.getCombat().getCastSpell() == null) {
                    if (player.getEquipment().hasAt(EquipSlot.WEAPON, TUMEKENS_SHADOW)) {
                        spellMaxHit += (player.getSkills().level(Skills.MAGIC) / 3) + 1;
                    }
                }

                // Trident of the swamp
                if (spellName.equals("trident of the swamp")) {
                    spellMaxHit = 23;
                    spellMaxHit = (int) Math.round((Math.max(spellMaxHit, spellMaxHit + (Math.max(0, level - 75)) / 3)) * (1 + (b.magestr / 100.0)));
                }

                // God spells (level 60) in combination with Charge (level 80): the base max hit is 30.
                if (spellName.equals("saradomin strike") || spellName.equals("claws of guthix") || spellName.equals("flames of zamorak")) {
                    if (player.getTimers().has(TimerKey.CHARGE_SPELL)) {
                        spellMaxHit = 30;
                    }
                }

                if (spellName.contains("fire") && hasTomeOfFire) {
                    spellMaxHit *= 1.50;
                }

                double multiplier = 1 + ((b.getMagestr() > 0 ? b.getMagestr() : 1.0) / 100);

                if (FormulaUtils.hasThammaronSceptre(player) && target != null && target.isNpc() && includeNpcMax) {
                    multiplier += 0.25;
                }

                if (spell.spellbook() == MagicSpellbook.ANCIENT) {
                    if (player.getEquipment().contains(VIRTUS_MASK)) {
                        multiplier += 0.04;
                    }
                    if (player.getEquipment().contains(VIRTUS_ROBE_TOP)) {
                        multiplier += 0.04;
                    }
                    if (player.getEquipment().contains(VIRTUS_ROBE_BOTTOMS)) {
                        multiplier += 0.04;
                    }
                }

                if (player.getEquipment().hasAt(EquipSlot.WEAPON, TURQUOISE_SLAYER_HELMET_I) && target != null && target.isNpc() && includeNpcMax) {
                    multiplier += 0.10;
                }
                var weakSpot = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WEAK_SPOT);
                if (weakSpot && target != null && target.isNpc()) {
                    if (Slayer.creatureMatches(player, target.getAsNpc().id())) {
                        multiplier += 0.10;
                    }
                }

                int weaponId = player.getEquipment().get(3) == null ? -1 : player.getEquipment().get(3).getId();
                if (spellName.equals("volatile spell")) {
                    int baseLevel = level;
                    if (baseLevel > 99)
                        baseLevel = 99;
                    double levelTimes = 0.67;
                    multiplier -= 0.15;
                    spellMaxHit = (int) (baseLevel * levelTimes);
                }

                int maxHit = (int) Math.round(spellMaxHit * multiplier);

                if (player.getEquipment().hasAt(EquipSlot.AMULET, OCCULT_NECKLACE_OR) || player.getEquipment().hasAt(EquipSlot.HANDS, TORMENTED_BRACELET_OR)) {
                    maxHit += 1;
                }

                if (player.getSpellbook().equals(MagicSpellbook.ANCIENT) && FormulaUtils.hasAncientSceptre(player)) {
                    maxHit *= 1.10D;
                }

                if (spellName.equals("sanguinesti spell")) {
                    boolean holyStaff = weaponId == ItemIdentifiers.HOLY_SANGUINESTI_STAFF;
                    if (holyStaff) {
                        maxHit += 10;
                    }
                }
                return maxHit;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

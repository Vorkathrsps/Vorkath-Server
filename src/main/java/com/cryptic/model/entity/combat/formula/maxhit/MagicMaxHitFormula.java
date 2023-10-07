package com.cryptic.model.entity.combat.formula.maxhit;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.timers.TimerKey;
import lombok.NonNull;
import org.apache.commons.lang.ArrayUtils;

/**
 * @Author: Origin
 * @Date: 6/30/2023
 */
public class MagicMaxHitFormula {
    private static final int[] fireSpells = new int[]{1158, 1169, 1539, 1181, 1189, 22608};
    private static final int[] waterSpells = new int[]{1154, 1163, 1175, 1185, 22658};
    private static final int[] godSpells = new int[]{1191, 1192, 1190};
    private static final int[] boltSpells = new int[]{1160, 1163, 1166, 1166, 1169};
    private static final int[] ancientSpells = new int[]{12939, 12987, 12901, 12861, 12963, 13011, 12919, 12881, 12951, 12999, 12911, 12871, 12975, 13023, 12929, 12891};

    public int calculateBaseMaxHitForPoweredStaves(@NonNull final Player player, int baseMaxHit) {
        int magicLevel = player.skills().level(Skills.MAGIC);
        if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.THAMMARONS_SCEPTRE)) {
            baseMaxHit += Math.min(baseMaxHit, (magicLevel - 60) / 3);
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.ACCURSED_SCEPTRE_A)) {
            baseMaxHit += Math.min(baseMaxHit, (magicLevel - 69) / 3);
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.TRIDENT_OF_THE_SEAS)) {
            baseMaxHit += Math.min(baseMaxHit, (magicLevel - 75) / 3);
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.TRIDENT_OF_THE_SWAMP)) {
            baseMaxHit += Math.min(baseMaxHit, (magicLevel - 78) / 3);
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.SANGUINESTI_STAFF)) {
            baseMaxHit += Math.min(baseMaxHit, (magicLevel - 81) / 3);
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.TUMEKENS_SHADOW)) {
            baseMaxHit += Math.min(baseMaxHit, (magicLevel - 84) / 3);
        }
        return baseMaxHit;
    }

    public double calculateMagicDamageBonus(@NonNull final Player player) {
        EquipmentInfo.Bonuses bonuses = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        double mageStrength = bonuses.getMagestr();
        CombatSpell castSpell = player.getCombat().getCastSpell();
        CombatSpell autoCastSpell = player.getCombat().getAutoCastSpell();

        if (player.getCombat().getPoweredStaffSpell() != null && player.getCombat().getCastSpell() == null) {
            if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.TUMEKENS_SHADOW)) {
                mageStrength += Math.floor((double) (player.getSkills().level(Skills.MAGIC) - 1) / 3);
            }
        }

        if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.SMOKE_BATTLESTAFF) && player.getSpellbook().equals(MagicSpellbook.NORMAL)) {
            mageStrength += 10.0;
        }

        if (FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic(player) || FormulaUtils.eliteVoidEquipmentBaseMagic(player)) {
            mageStrength += 25.0;
        }

        if (FormulaUtils.hasSalveAmuletI(player)) {
            mageStrength += 15.0;
        }

        if (FormulaUtils.hasSalveAmuletEI(player)) {
            mageStrength += 20.0;
        }

        if ((castSpell != null && ArrayUtils.contains(ancientSpells, castSpell.spellId())) || (autoCastSpell != null && ArrayUtils.contains(ancientSpells, autoCastSpell.spellId()))) {

            if (player.getEquipment().hasAt(EquipSlot.HEAD, ItemIdentifiers.VIRTUS_MASK)
                || player.getEquipment().hasAt(EquipSlot.BODY, ItemIdentifiers.VIRTUS_ROBE_TOP)
                || player.getEquipment().hasAt(EquipSlot.LEGS, ItemIdentifiers.VIRTUS_ROBE_BOTTOM)) {
                mageStrength += 4.0;
            }

        }


        return mageStrength;
    }

    public double getTomeBonus(@NonNull final Player player, CombatSpell spell) {
        if (spell == null) return 1;
        if (ArrayUtils.contains(waterSpells, spell.spellId())) {
            if (player.getEquipment().hasAt(EquipSlot.SHIELD, ItemIdentifiers.TOME_OF_WATER)) {
                return 2.0;
            }
        }
        if (ArrayUtils.contains(fireSpells, spell.spellId())) {
            if (player.getEquipment().hasAt(EquipSlot.SHIELD, ItemIdentifiers.TOME_OF_FIRE)) {
                return 1.5;
            }
        }
        return 1;
    }

    public double getSlayerBonus(@NonNull final Player player) {
        Entity target = player.getCombat().getTarget();
        boolean isSlayerMatch = target instanceof NPC npc && Slayer.creatureMatches(player, npc.id()) || target instanceof NPC dummy && dummy.isCombatDummy();
        return isSlayerMatch && FormulaUtils.hasSlayerHelmet(player) ? 1.15 : 1;
    }

    public int calculateMaxMagicHit(@NonNull final Player player) {
        CombatSpell spell =
            player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() :
                player.getCombat().getAutoCastSpell() != null ? player.getCombat().getAutoCastSpell() :
                    player.getCombat().getPoweredStaffSpell() != null ? player.getCombat().getPoweredStaffSpell() : null;

        int baseMaxHit = spell != null ? spell.baseMaxHit() : 0;

        if (player.getCombat().getPoweredStaffSpell() != null) {
            baseMaxHit = calculateBaseMaxHitForPoweredStaves(player, baseMaxHit);
        }

        int magicLevel = player.skills().level(Skills.MAGIC);

        if (FormulaUtils.hasMagicWildernessWeapon(player) && WildernessArea.isInWilderness(player)) {
            baseMaxHit *= 1.50;
        }

        if (player.getEquipment().contains(ItemIdentifiers.CHAOS_GAUNTLETS) && isCastingBoltSpell(spell)) {
            baseMaxHit += 3;
        }

        if (this.isCastingGodSpell(spell) && player.getTimers().has(TimerKey.CHARGE_SPELL)) {
            baseMaxHit = 30;
        }

        if (player.getEquipment().contains(ItemIdentifiers.VOLATILE_NIGHTMARE_STAFF) && player.isSpecialActivated()) {
            baseMaxHit = Math.min(58, 58 * magicLevel / 99 + 1);
        }

        double magicDamageBonus = calculateMagicDamageBonus(player) / 100.0;

        baseMaxHit *= (1 + magicDamageBonus);
        baseMaxHit = (int) Math.floor(baseMaxHit);

        if (!player.getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
            baseMaxHit *= getSlayerBonus(player);
            baseMaxHit = (int) Math.floor(baseMaxHit);
        }

        baseMaxHit *= getTomeBonus(player, spell);
        baseMaxHit = (int) Math.floor(baseMaxHit);

        return baseMaxHit;
    }

    private boolean isCastingGodSpell(CombatSpell spell) {
        if (spell == null) {
            return false;
        }
        return ArrayUtils.contains(godSpells, spell.spellId());
    }

    private boolean isCastingBoltSpell(CombatSpell spell) {
        if (spell == null) {
            return false;
        }
        return ArrayUtils.contains(boltSpells, spell.spellId());
    }
}


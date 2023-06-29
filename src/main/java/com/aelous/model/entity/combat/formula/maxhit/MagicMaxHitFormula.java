package com.aelous.model.entity.combat.formula.maxhit;

import com.aelous.model.World;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;

public class MagicMaxHitFormula {

    public int calculateBaseMaxHitForPoweredStaves(Player player, int baseMaxHit) {
        int magicLevel = player.skills().level(Skills.MAGIC);
        if (player.getEquipment().contains(ItemIdentifiers.THAMMARONS_SCEPTRE)) {
            baseMaxHit += (magicLevel - 60) / 3;
        } else if (player.getEquipment().contains(ItemIdentifiers.ACCURSED_SCEPTRE_A)) {
            baseMaxHit += Math.min(20, (magicLevel - 69) / 3);
        } else if (player.getEquipment().contains(ItemIdentifiers.TRIDENT_OF_THE_SEAS)) {
            baseMaxHit += Math.min(18, (magicLevel - 75) / 3);
        } else if (player.getEquipment().contains(ItemIdentifiers.TRIDENT_OF_THE_SWAMP)) {
            baseMaxHit += Math.min(17, (magicLevel - 78) / 3);
        } else if (player.getEquipment().contains(ItemIdentifiers.SANGUINESTI_STAFF)) {
            baseMaxHit += Math.min(15, (magicLevel - 81) / 3);
        } else if (player.getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
            baseMaxHit += Math.min(baseMaxHit, (magicLevel - 84) / 3);
        }
        return baseMaxHit;
    }

    public double calculateMagicDamageBonus(Player player) {
        EquipmentInfo.Bonuses bonuses = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        double mageStrength = bonuses.getMagestr();
        if (player.getCombat().getPoweredStaffSpell() != null && player.getCombat().getCastSpell() == null) {
            if (player.getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                mageStrength += Math.floor((double) (player.getSkills().level(Skills.MAGIC) - 1) / 3);
                return mageStrength;
            }
        }

        if (player.getEquipment().contains(ItemIdentifiers.SMOKE_BATTLESTAFF) && player.getSpellbook().equals(MagicSpellbook.NORMAL)) {
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

        return mageStrength;
    }

    public double getTomeBonus(Player player, CombatSpell spell) {
        int[] fireSpells = new int[]{1158, 1169, 1539, 1181, 1192, 1189, 22608};
        int[] waterSpells = new int[]{1154, 1163, 1175, 1185, 22658};
        for (var spells : waterSpells) {
            if (spell != null) {
                if (player.getEquipment().contains(ItemIdentifiers.TOME_OF_WATER)) {
                    if (spells == spell.spellId()) {
                        return 2.0;
                    }
                }
            }
        }
        for (var spells : fireSpells) {
            if (spell != null) {
                if (player.getEquipment().contains(ItemIdentifiers.TOME_OF_FIRE)) {
                    if (spells == spell.spellId()) {
                        return 1.5;
                    }
                }
            }
        }
        return 1;
    }

    public double getSlayerBonus(Player player) {
        Entity target = player.getCombat().getTarget();
        boolean isSlayerMatch = target instanceof NPC npc && Slayer.creatureMatches(player, npc.id()) || target instanceof NPC dummy && dummy.isCombatDummy();
        return isSlayerMatch && FormulaUtils.hasSlayerHelmet(player) ? 1.15 : 1;
    }

    public int calculateMaxMagicHit(Player player) {
        CombatSpell spell =
            player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() :
                player.getCombat().getAutoCastSpell() != null ? player.getCombat().getAutoCastSpell() :
                    player.getCombat().getPoweredStaffSpell() != null ? player.getCombat().getPoweredStaffSpell() : null;

        int baseMaxHit = spell != null ? spell.baseMaxHit() : 0;
        if (player.getCombat().getPoweredStaffSpell() != null) {
            baseMaxHit = calculateBaseMaxHitForPoweredStaves(player, baseMaxHit);
        }

        if (FormulaUtils.hasMagicWildernessWeapon(player) && WildernessArea.inWild(player)) {
            baseMaxHit *= 1.50;
        }

        if (player.getEquipment().contains(ItemIdentifiers.CHAOS_GAUNTLETS) && isCastingBoltSpell(spell)) {
            baseMaxHit += 3;
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

    private boolean isCastingBoltSpell(CombatSpell spell) {
        int[] boltSpells = new int[]{1160, 1163, 1166, 1166, 1169};
        for (var spells : boltSpells) {
            if (spell.spellId() == spells) {
                return true;
            }
        }
        return false;
    }
}


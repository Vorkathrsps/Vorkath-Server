package com.aelous.model.entity.combat.magic.autocasting;

import com.aelous.model.inter.impl.BonusesInterface;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.weapon.FightType;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.aelous.utility.ItemIdentifiers.*;

public class Autocasting {

    public static HashMap<Integer, CombatSpells> LEGACY_AUTOCAST_SPELLS = new HashMap<>();

    static {
        // Modern
        LEGACY_AUTOCAST_SPELLS.put(1830, CombatSpells.WIND_STRIKE);
        LEGACY_AUTOCAST_SPELLS.put(1831, CombatSpells.WATER_STRIKE);
        LEGACY_AUTOCAST_SPELLS.put(1832, CombatSpells.EARTH_STRIKE);
        LEGACY_AUTOCAST_SPELLS.put(1833, CombatSpells.FIRE_STRIKE);
        LEGACY_AUTOCAST_SPELLS.put(1834, CombatSpells.WIND_BOLT);
        LEGACY_AUTOCAST_SPELLS.put(1835, CombatSpells.WATER_BOLT);
        LEGACY_AUTOCAST_SPELLS.put(1836, CombatSpells.EARTH_BOLT);
        LEGACY_AUTOCAST_SPELLS.put(1837, CombatSpells.FIRE_BOLT);
        LEGACY_AUTOCAST_SPELLS.put(1838, CombatSpells.WIND_BLAST);
        LEGACY_AUTOCAST_SPELLS.put(1839, CombatSpells.WATER_BLAST);
        LEGACY_AUTOCAST_SPELLS.put(1840, CombatSpells.EARTH_BLAST);
        LEGACY_AUTOCAST_SPELLS.put(1841, CombatSpells.FIRE_BLAST);
        LEGACY_AUTOCAST_SPELLS.put(1842, CombatSpells.WIND_WAVE);
        LEGACY_AUTOCAST_SPELLS.put(1843, CombatSpells.WATER_WAVE);
        LEGACY_AUTOCAST_SPELLS.put(1844, CombatSpells.EARTH_WAVE);
        LEGACY_AUTOCAST_SPELLS.put(1845, CombatSpells.FIRE_WAVE);
        LEGACY_AUTOCAST_SPELLS.put(50050, CombatSpells.AIR_SURGE);
        LEGACY_AUTOCAST_SPELLS.put(50070, CombatSpells.WATER_SURGE);
        LEGACY_AUTOCAST_SPELLS.put(50090, CombatSpells.EARTH_SURGE);
        LEGACY_AUTOCAST_SPELLS.put(50110, CombatSpells.FIRE_SURGE);

        // Ancients
        LEGACY_AUTOCAST_SPELLS.put(13189, CombatSpells.SMOKE_RUSH);
        LEGACY_AUTOCAST_SPELLS.put(13241, CombatSpells.SHADOW_RUSH);
        LEGACY_AUTOCAST_SPELLS.put(13247, CombatSpells.BLOOD_RUSH);
        LEGACY_AUTOCAST_SPELLS.put(6162, CombatSpells.ICE_RUSH);
        LEGACY_AUTOCAST_SPELLS.put(13215, CombatSpells.SMOKE_BURST);
        LEGACY_AUTOCAST_SPELLS.put(13267, CombatSpells.SHADOW_BURST);
        LEGACY_AUTOCAST_SPELLS.put(13167, CombatSpells.BLOOD_BURST);
        LEGACY_AUTOCAST_SPELLS.put(13125, CombatSpells.ICE_BURST);
        LEGACY_AUTOCAST_SPELLS.put(13202, CombatSpells.SMOKE_BLITZ);
        LEGACY_AUTOCAST_SPELLS.put(13254, CombatSpells.SHADOW_BLITZ);
        LEGACY_AUTOCAST_SPELLS.put(13158, CombatSpells.BLOOD_BLITZ);
        LEGACY_AUTOCAST_SPELLS.put(13114, CombatSpells.ICE_BLITZ);
        LEGACY_AUTOCAST_SPELLS.put(13228, CombatSpells.SMOKE_BARRAGE);
        LEGACY_AUTOCAST_SPELLS.put(13280, CombatSpells.SHADOW_BARRAGE);
        LEGACY_AUTOCAST_SPELLS.put(13178, CombatSpells.BLOOD_BARRAGE);
        LEGACY_AUTOCAST_SPELLS.put(13136, CombatSpells.ICE_BARRAGE);
    }

    public static final Set<Integer> ANCIENT_SPELL_AUTOCAST_STAFFS = Set.of(KODAI_WAND, MASTER_WAND,
        ANCIENT_STAFF, NIGHTMARE_STAFF, VOLATILE_NIGHTMARE_STAFF, ELDRITCH_NIGHTMARE_STAFF, TOXIC_STAFF_OF_THE_DEAD, STAFF_OF_THE_DEAD, STAFF_OF_LIGHT);

    public static final Set<Integer> SPECIAL_AUTOCAST_STAFFS = Set.of(TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP,
        SANGUINESTI_STAFF, HOLY_SANGUINESTI_STAFF, TUMEKENS_SHADOW, ACCURSED_SCEPTRE_A);

    //TODO add checks if wearing when changing spellbooks etc, this seriously bugs shit out

    public static boolean toggleAutocast(final Player player, int actionButtonId) {
        final CombatSpell cbSpell = CombatSpells.getCombatSpell(actionButtonId);
        boolean isWearingPoweredStaff = player.getEquipment().containsAny(TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP, SANGUINESTI_STAFF, TUMEKENS_SHADOW, DAWNBRINGER);

        if (cbSpell == null) {
            return false;
        }

        if (cbSpell.levelRequired() > player.getSkills().level(Skills.MAGIC)) {
            player.message("You need a Magic level of at least %d to cast this spell.", cbSpell.levelRequired());
            setAutocast(player, null);
            return false;
        }

        CombatSpell spellBeingCast = player.getCombat().getCastSpell();

        if (spellBeingCast != null && spellBeingCast == cbSpell) {
            setAutocast(player, null);
        } else {
            setAutocast(player, cbSpell);
        }
        return true;
    }

    public static boolean handleLegacyAutocast(Player player, int button) {
        if (LEGACY_AUTOCAST_SPELLS.containsKey(button)) {
            setAutocast(player, LEGACY_AUTOCAST_SPELLS.get(button).getSpell());
            WeaponInterfaces.updateWeaponInterface(player);
            return true;
        }

        if (button == 2004 || button == 6161) {
            setAutocast(player, null);
            WeaponInterfaces.updateWeaponInterface(player);
            return true;
        }
        return false;
    }

    public static void setAutocast(Player player, CombatSpell spell) {
        if (spell == null) {
            player.getPacketSender().sendAutocastId(-1);
            player.getPacketSender().sendAutocastId(-1).sendConfig(108, 0).setDefensiveAutocastState(0);
        } else {
            if (player.<Boolean>getAttribOr(AttributeKey.DEFENSIVE_AUTOCAST, false)) {
                player.getPacketSender().sendAutocastId(spell.spellId()).sendConfig(108, 0).setDefensiveAutocastState(1);
            } else {
                player.getPacketSender().sendAutocastId(spell.spellId()).sendConfig(108, 1).setDefensiveAutocastState(0);
            }
            player.putAttrib(AttributeKey.AUTOCAST_SELECTED, spell);
        }
        player.getCombat().setAutoCastSpell(spell);
        BonusesInterface.sendBonuses(player);
        updateConfigsOnAutocast(player, spell != null);
    }

    private static final List<FightType> STAFF_FIGHT_TYPES = List.of(
        FightType.STAFF_BASH,
        FightType.STAFF_FOCUS,
        FightType.STAFF_POUND
    );

    private static void updateConfigsOnAutocast(final Player player, boolean autocast) {
        if (autocast) {
            for (final FightType type : STAFF_FIGHT_TYPES) {
                player.getPacketSender().sendConfig(type.getParentId(), 3);
            }
        }
    }
}

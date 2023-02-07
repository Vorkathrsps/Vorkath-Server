package com.aelous.model.entity.combat.magic.impl;

import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

import java.util.ArrayList;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * A set of constants representing the staves that can be used in place of
 * runes.
 *
 * @author lare96
 */
public enum PlayerMagicStaff {

    AIR(new int[]{STAFF_OF_AIR, AIR_BATTLESTAFF, MYSTIC_AIR_STAFF, SMOKE_BATTLESTAFF}, new int[]{AIR_RUNE}),
    WATER(new int[]{STAFF_OF_WATER, WATER_BATTLESTAFF, MYSTIC_WATER_STAFF, KODAI_WAND}, new int[]{WATER_RUNE}),
    EARTH(new int[]{STAFF_OF_EARTH, EARTH_BATTLESTAFF, MYSTIC_EARTH_STAFF}, new int[]{EARTH_RUNE}),
    FIRE(new int[]{STAFF_OF_FIRE, FIRE_BATTLESTAFF, MYSTIC_FIRE_STAFF, SMOKE_BATTLESTAFF}, new int[]{FIRE_RUNE}),
    MUD(new int[]{MUD_BATTLESTAFF, MYSTIC_MUD_STAFF}, new int[]{WATER_RUNE, EARTH_RUNE}),
    LAVA(new int[]{LAVA_BATTLESTAFF, MYSTIC_LAVA_STAFF}, new int[]{FIRE_RUNE, EARTH_RUNE}),
    SMOKE(new int[]{SMOKE_BATTLESTAFF, MYSTIC_SMOKE_STAFF}, new int[]{SMOKE_RUNE}),
    STEAM(new int[] {STEAM_BATTLESTAFF, MYSTIC_STEAM_STAFF, STEAM_BATTLESTAFF_12795, MYSTIC_STEAM_STAFF_12796}, new int[] {STEAM_RUNE}),
    DUST(new int[] {DUST_BATTLESTAFF, MYSTIC_DUST_STAFF}, new int[] {DUST_RUNE}),
    MIST(new int[] {MIST_BATTLESTAFF, MYSTIC_MIST_STAFF}, new int[] {MIST_RUNE}),
    TOME_OF_FIRE(new int[] { ItemIdentifiers.TOME_OF_FIRE, TOME_OF_FIRE_EMPTY }, new int[] { FIRE_RUNE });

    /**
     * The staves that can be used in place of runes.
     */
    private final int[] staves;

    /**
     * The runes that the staves can be used for.
     */
    private final int[] runes;

    /**
     * Create a new {@link PlayerMagicStaff}.
     *
     * @param itemIds the staves that can be used in place of runes.
     * @param runeIds the runes that the staves can be used for.
     */
    PlayerMagicStaff(int[] itemIds, int[] runeIds) {
        this.staves = itemIds;
        this.runes = runeIds;
    }

    /**
     * Suppress items in the argued array if any of the items match the runes
     * that are represented by the staff the argued player is wielding.
     *
     * @param player        the player to suppress runes for.
     * @param runesRequired the runes to suppress.
     * @return the new array of items with suppressed runes removed.
     */
    public static List<Item> suppressRunes(final Player player, final List<Item> runesRequired) {
        var result = new ArrayList<>(runesRequired);
        if (player.getCombat().getWeaponType() == WeaponType.MAGIC_STAFF
            || player.getEquipment().hasAt(EquipSlot.SHIELD, ItemIdentifiers.TOME_OF_FIRE)
            || player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE_EMPTY)) {
            for (PlayerMagicStaff staff : values()) {
                if (player.getEquipment().containsAny(staff.staves)) {
                    for (int id : staff.runes) {
                        result.removeIf(item -> item.getId() == id);
                    }
                }
            }
        }
        return result;
    }
}

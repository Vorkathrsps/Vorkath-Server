package com.cryptic.model.entity.sounds.impl.weapons;

import com.cryptic.utility.ItemIdentifiers;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ynneh | 10/03/2022 - 19:23
 * <https://github.com/drhenny>
 */
public enum WeaponSound {

    ABYSSAL_WHIP(List.of(4151), 1080, 1081),
    GRANITE_MAUL(List.of(ItemIdentifiers.GRANITE_MAUL), 1079, -1),
    DRAGON_DAGGER(Arrays.asList(5698, 1215), 1, 385),



    ;

    public final List<Integer> weaponIds;
    public final int defaultSound;
    public final int specialSound;

    WeaponSound(List<Integer> weaponIds, int defaultSound, int specialSound) {
        this.weaponIds = weaponIds;
        this.defaultSound = defaultSound;
        this.specialSound = specialSound;
    }

}

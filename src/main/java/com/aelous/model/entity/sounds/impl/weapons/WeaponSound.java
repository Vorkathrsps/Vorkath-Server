package com.aelous.model.entity.sounds.impl.weapons;

import com.aelous.utility.ItemIdentifiers;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ynneh | 10/03/2022 - 19:23
 * <https://github.com/drhenny>
 */
public enum WeaponSound {

    ABYSSAL_WHIP(Arrays.asList(4151), 1080, 1081),
    GRANITE_MAUL(Arrays.asList(ItemIdentifiers.GRANITE_MAUL), 1079, -1),
    DRAGON_DAGGER(Arrays.asList(5698, 1215), 1, 385),



    ;

    public List<Integer> weaponIds;
    public int defaultSound, specialSound;

    WeaponSound(List<Integer> weaponIds, int defaultSound, int specialSound) {
        this.weaponIds = weaponIds;
        this.defaultSound = defaultSound;
        this.specialSound = specialSound;
    }

}

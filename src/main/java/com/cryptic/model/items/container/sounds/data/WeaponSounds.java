package com.cryptic.model.items.container.sounds.data;

import com.cryptic.model.entity.combat.weapon.FightType;
import com.esotericsoftware.kryo.util.ObjectMap;
import lombok.Data;

import java.util.Map;

@Data
public class WeaponSounds {
    private Map<FightType, Integer> styles;
    private int area_sound;
    @Override
    public String toString() {
        return "WeaponSounds {" +
            "styles=" + styles +
            '}';
    }

    public int forFightType(FightType fightType) {
        return styles.getOrDefault(fightType, -1);
    }

}

package com.cryptic.model.items.container.sounds;

import com.cryptic.model.entity.combat.weapon.FightType;
import lombok.Data;

import java.util.Map;

@Data
public class SoundData {
    private Map<FightType, Integer> styles;

    @Override
    public String toString() {
        return "SoundData{" +
            "styles=" + styles +
            '}';
    }

    public int forFightType(FightType fightType) {
        return styles.getOrDefault(fightType, -1);
    }
}

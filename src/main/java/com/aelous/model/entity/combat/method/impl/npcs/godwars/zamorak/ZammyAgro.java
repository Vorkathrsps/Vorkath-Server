package com.aelous.model.entity.combat.method.impl.npcs.godwars.zamorak;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.AggressionCheck;

import java.util.Arrays;
import java.util.List;

/**
 * @author Patrick van Elderen | April, 29, 2021, 14:04
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class ZammyAgro implements AggressionCheck {

    private final List<Integer> ZAMORAK_PROTECTION_EQUIPMENT = Arrays.asList(1033, 1035, 2414, 2417, 2653, 2655,
        2657, 2659, 3478, 4039, 6764, 8056, 8059, 10368, 10370, 10372, 10374,
        10444, 10450, 10456, 10460, 10468, 10474, 10776, 10786, 10790, 11808,
        11824, 11889, 11892, 12638, 13333, 13334, 19936);

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        for(int armour : ZAMORAK_PROTECTION_EQUIPMENT) {
            if(entity.isPlayer()) {
                if(entity.getAsPlayer().getEquipment().contains(armour))
                    return false;
            }
        }
        return true;
    }
}

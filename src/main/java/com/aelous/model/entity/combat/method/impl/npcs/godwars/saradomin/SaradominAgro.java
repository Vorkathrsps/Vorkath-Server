package com.aelous.model.entity.combat.method.impl.npcs.godwars.saradomin;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.AggressionCheck;

import java.util.Arrays;
import java.util.List;

/**
 * @author Patrick van Elderen | April, 29, 2021, 14:17
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SaradominAgro implements AggressionCheck {

    private final List<Integer> SARADOMIN_PROTECTION_EQUIPMENT = Arrays.asList(542, 544, 1718, 2412, 2415,
        2661, 2663, 2665, 2667, 3479, 3840, 4037, 6762, 8055, 8058, 10384, 10386,
        10388, 10390, 10440, 10446, 10452, 10458, 10464, 10470, 10778, 10784, 10792,
        11806, 11838, 11891, 12598, 12637, 12809, 13331, 13332, 19933);

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        for(int armour : SARADOMIN_PROTECTION_EQUIPMENT) {
            if(entity.isPlayer()) {
                if(entity.getAsPlayer().getEquipment().contains(armour))
                    return false;
            }
        }
        return true;
    }
}

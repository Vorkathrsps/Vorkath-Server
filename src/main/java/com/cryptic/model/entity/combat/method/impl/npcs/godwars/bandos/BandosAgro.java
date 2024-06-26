package com.cryptic.model.entity.combat.method.impl.npcs.godwars.bandos;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.AggressionCheck;

import java.util.Arrays;
import java.util.List;

/**
 * @author Origin | April, 29, 2021, 14:16
 * 
 */
public class BandosAgro implements AggressionCheck {

    private final List<Integer> BANDOS_PROTECTION_EQUIPMENT = Arrays.asList(11061, 11804, 11832, 11834,
        11836, 12265, 12267, 12269, 12271, 12273, 12275, 12480, 12482, 12484,
        12486, 12488, 12498, 12500, 12502, 12504, 12608, 19924);

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        for(int armour : BANDOS_PROTECTION_EQUIPMENT) {
            if(entity.isPlayer()) {
                if(entity.getAsPlayer().getEquipment().contains(armour))
                    return false;
            }
        }
        return true;
    }
}

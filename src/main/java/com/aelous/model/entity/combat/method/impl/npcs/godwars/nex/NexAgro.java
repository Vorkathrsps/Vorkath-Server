package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.AggressionCheck;

import java.util.Arrays;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 12, 2022
 */
public class NexAgro implements AggressionCheck {

    private final List<Integer> NEX_PROTECTION_EQUIPMENT = Arrays.asList(ANCIENT_STAFF, BOOK_OF_DARKNESS, ANCIENT_ROBE_LEGS, ANCIENT_ROBE_TOP,
        ANCIENT_BRACERS, ANCIENT_BLESSING, ANCIENT_DHIDE_BOOTS, ANCIENT_DHIDE_BODY, ANCIENT_CHAPS, ANCIENT_DHIDE_SHIELD, ANCIENT_CLOAK, ANCIENT_COIF, ANCIENT_HALO, ANCIENT_GODSWORD,
        ZARYTE_VAMBRACES, TORVA_FULL_HELM, TORVA_PLATEBODY, TORVA_PLATELEGS, ROBE_BOTTOM_OF_DARKNESS, ROBE_TOP_OF_DARKNESS, GLOVES_OF_DARKNESS, BOOK_OF_DARKNESS, HOOD_OF_DARKNESS);

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        for(int armour : NEX_PROTECTION_EQUIPMENT) {
            if(entity.isPlayer()) {
                if(entity.getAsPlayer().getEquipment().contains(armour))
                    return false;
            }
        }
        return true;
    }
}

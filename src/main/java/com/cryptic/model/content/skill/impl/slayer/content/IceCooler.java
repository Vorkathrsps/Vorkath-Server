package com.cryptic.model.content.skill.impl.slayer.content;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.DesertLizardsCombat;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

import java.util.Arrays;
import java.util.List;

/**
 * @author PVE
 * @Since augustus 05, 2020
 */
public class IceCooler {

    private static final List<Integer> DESERT_LIZARDS = Arrays.asList(NpcIdentifiers.DESERT_LIZARD, NpcIdentifiers.DESERT_LIZARD_460, NpcIdentifiers.DESERT_LIZARD_461);

    public static boolean onItemOnNpc(Player player, NPC npc) {
        for (int DESERT_LIZARDS : DESERT_LIZARDS) {
            if (npc.id() == DESERT_LIZARDS) {
                int item = player.getAttrib(AttributeKey.ITEM_ID);

                if (item == ItemIdentifiers.ICE_COOLER) {
                    DesertLizardsCombat.iceCooler(player, npc, true);
                }
                return true;
            }
        }
        return false;
    }
}

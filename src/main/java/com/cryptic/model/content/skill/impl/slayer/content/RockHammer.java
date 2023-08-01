package com.cryptic.model.content.skill.impl.slayer.content;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.Gargoyle;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

import java.util.Arrays;
import java.util.List;

/**
 * @author Origin
 * mei 15, 2020
 */
public class RockHammer {

    private static final List<Integer> GARGOYLES = Arrays.asList(NpcIdentifiers.GARGOYLE);

    public static boolean onItemOnNpc(Player player, NPC npc) {
        for (int GARGOYLES : GARGOYLES) {
            if (npc.id() == GARGOYLES) {
                int item = player.getAttrib(AttributeKey.ITEM_ID);

                if (item == ItemIdentifiers.ROCK_HAMMER) {
                    Gargoyle.smash(player, npc, true);
                }
                return true;
            }
        }
        return false;
    }
}

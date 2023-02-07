package com.aelous.model.content.skill.impl.slayer.content;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.Gargoyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import java.util.Arrays;
import java.util.List;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
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

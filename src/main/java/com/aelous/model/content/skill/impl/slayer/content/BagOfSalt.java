package com.aelous.model.content.skill.impl.slayer.content;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import java.util.Arrays;
import java.util.List;

/**
 * @author PVE
 * @Since juli 31, 2020
 */
public class BagOfSalt {

    private static final List<Integer> ROCKSLUGS = Arrays.asList(NpcIdentifiers.ROCKSLUG, NpcIdentifiers.ROCKSLUG_422);

    public static boolean onItemOnNpc(Player player, NPC npc) {
        for (int rockslug : ROCKSLUGS) {
            if (npc.id() == rockslug) {
                int item = player.getAttrib(AttributeKey.ITEM_ID);

                if (item == ItemIdentifiers.BAG_OF_SALT) {
                    player.inventory().remove(new Item(item, 1));
                    if (npc.hp() <= 5) {
                        player.animate(1574);
                        npc.graphic(327);
                        npc.hp(0, 0);
                        npc.die();
                    } else {
                        player.message("You use the bag of salt, but the rockslug is not weak enough.");
                    }
                }
                return true;
            }
        }
        return false;
    }
}

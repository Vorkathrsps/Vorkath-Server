package com.cryptic.model.content.skill.impl.slayer.content;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

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

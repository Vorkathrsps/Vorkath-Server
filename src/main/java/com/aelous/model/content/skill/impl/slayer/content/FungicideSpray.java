package com.aelous.model.content.skill.impl.slayer.content;

import com.aelous.model.content.packet_actions.interactions.items.ItemOnItem;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import java.util.Arrays;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author PVE
 * @Since augustus 05, 2020
 */
public class FungicideSpray {

    private static final List<Integer> ZYGOMITES = Arrays.asList(NpcIdentifiers.ZYGOMITE, NpcIdentifiers.ZYGOMITE_1024, NpcIdentifiers.ANCIENT_ZYGOMITE);
    private static final List<Integer> FUNGICIDE_SPRAYS = Arrays.asList(FUNGICIDE_SPRAY_1, FUNGICIDE_SPRAY_2, FUNGICIDE_SPRAY_3, FUNGICIDE_SPRAY_4, FUNGICIDE_SPRAY_5, FUNGICIDE_SPRAY_6, FUNGICIDE_SPRAY_7, FUNGICIDE_SPRAY_8, FUNGICIDE_SPRAY_9, FUNGICIDE_SPRAY_10);

    public static boolean onItemOnNpc(Player player, NPC npc) {
        for (int zygomite : ZYGOMITES) {
            if (npc.id() == zygomite) {
                int item = player.getAttrib(AttributeKey.ITEM_ID);
                int slot = ItemOnItem.slotOf(player, item);

                for (int spray : FUNGICIDE_SPRAYS) {
                    if (item == spray) {
                        switch (item) {
                            case ItemIdentifiers.FUNGICIDE_SPRAY_10 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_10), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_9), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_9 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_9), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_8), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_8 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_8), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_7), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_7 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_7), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_6), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_6 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_6), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_5), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_5 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_5), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_4), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_4 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_4), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_3), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_3 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_3), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_2), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_2 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_2), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_1), slot);
                            }
                            case ItemIdentifiers.FUNGICIDE_SPRAY_1 -> {
                                player.inventory().remove(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_1), slot);
                                player.inventory().add(new Item(ItemIdentifiers.FUNGICIDE_SPRAY_0), slot);
                            }
                        }
                        if (npc.hp() <= 1) {
                            npc.hp(0, 0);
                            npc.die();
                        } else {
                            player.message("You use the fungicide spray, but the zygomite is not weak enough.");
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}

package com.aelous.model.content.items.tools;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;

import java.util.Arrays;
import java.util.Optional;

/**
 * This class contains all the data for all armour/weaponry/costmetics etc sets.
 *
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 24, 2020
 */
public class ItemPacks {

    public enum ItemSets {
        //Armour sets
        GUTHANS_SET(ItemIdentifiers.GUTHANS_ARMOUR_SET, new Item(ItemIdentifiers.GUTHANS_HELM), new Item(ItemIdentifiers.GUTHANS_PLATEBODY), new Item(ItemIdentifiers.GUTHANS_CHAINSKIRT), new Item(ItemIdentifiers.GUTHANS_WARSPEAR)),
        VERACS_SET(ItemIdentifiers.VERACS_ARMOUR_SET, new Item(ItemIdentifiers.VERACS_BRASSARD), new Item(ItemIdentifiers.VERACS_FLAIL), new Item(ItemIdentifiers.VERACS_HELM), new Item(ItemIdentifiers.VERACS_PLATESKIRT)),
        TORAGS_SET(ItemIdentifiers.TORAGS_ARMOUR_SET, new Item(ItemIdentifiers.TORAGS_HAMMERS), new Item(ItemIdentifiers.TORAGS_HELM), new Item(ItemIdentifiers.TORAGS_PLATEBODY), new Item(ItemIdentifiers.TORAGS_PLATELEGS)),
        AHRIMS_SET(ItemIdentifiers.AHRIMS_ARMOUR_SET, new Item(ItemIdentifiers.AHRIMS_HOOD), new Item(ItemIdentifiers.AHRIMS_ROBESKIRT), new Item(ItemIdentifiers.AHRIMS_ROBETOP), new Item(ItemIdentifiers.AHRIMS_STAFF)),
        KARILS_SET(ItemIdentifiers.KARILS_ARMOUR_SET, new Item(ItemIdentifiers.KARILS_COIF), new Item(ItemIdentifiers.KARILS_CROSSBOW), new Item(ItemIdentifiers.KARILS_LEATHERSKIRT), new Item(ItemIdentifiers.KARILS_LEATHERTOP)),
        DHAROKS_SET(ItemIdentifiers.DHAROKS_ARMOUR_SET, new Item(ItemIdentifiers.DHAROKS_PLATELEGS), new Item(ItemIdentifiers.DHAROKS_PLATEBODY), new Item(ItemIdentifiers.DHAROKS_HELM), new Item(ItemIdentifiers.DHAROKS_GREATAXE)),
        ANCESTRAL_ARMOUR_SET(ItemIdentifiers.ANCESTRAL_ROBES_SET, new Item(ItemIdentifiers.ANCESTRAL_HAT), new Item(ItemIdentifiers.ANCESTRAL_ROBE_TOP), new Item(ItemIdentifiers.ANCESTRAL_ROBE_BOTTOM)),
        JUSTICIAR_ARMOUR_SET(ItemIdentifiers.JUSTICIAR_ARMOUR_SET, new Item(ItemIdentifiers.JUSTICIAR_FACEGUARD), new Item(ItemIdentifiers.JUSTICIAR_CHESTGUARD), new Item(ItemIdentifiers.JUSTICIAR_LEGGUARDS)),
        INQUISITORS_ARMOUR_SET(ItemIdentifiers.INQUISITORS_ARMOUR_SET, new Item(ItemIdentifiers.INQUISITORS_GREAT_HELM), new Item(ItemIdentifiers.INQUISITORS_HAUBERK), new Item(ItemIdentifiers.INQUISITORS_PLATESKIRT)),
        BRONZE_SET_LG(ItemIdentifiers.BRONZE_SET_LG, new Item(ItemIdentifiers.BRONZE_FULL_HELM), new Item(ItemIdentifiers.BRONZE_PLATEBODY), new Item(ItemIdentifiers.BRONZE_PLATELEGS), new Item(ItemIdentifiers.BRONZE_KITESHIELD)),
        BRONZE_SET_SK(ItemIdentifiers.BRONZE_SET_SK, new Item(ItemIdentifiers.BRONZE_FULL_HELM), new Item(ItemIdentifiers.BRONZE_PLATEBODY), new Item(ItemIdentifiers.BRONZE_PLATESKIRT), new Item(ItemIdentifiers.BRONZE_KITESHIELD)),
        IRON_SET_LG(ItemIdentifiers.IRON_SET_LG, new Item(ItemIdentifiers.IRON_FULL_HELM), new Item(ItemIdentifiers.IRON_PLATEBODY), new Item(ItemIdentifiers.IRON_PLATELEGS), new Item(ItemIdentifiers.IRON_KITESHIELD)),
        IRON_SET_SK(ItemIdentifiers.IRON_SET_SK, new Item(ItemIdentifiers.IRON_FULL_HELM), new Item(ItemIdentifiers.IRON_PLATEBODY), new Item(ItemIdentifiers.IRON_PLATESKIRT), new Item(ItemIdentifiers.IRON_KITESHIELD)),
        STEEL_SET_LG(ItemIdentifiers.STEEL_SET_LG, new Item(ItemIdentifiers.STEEL_FULL_HELM), new Item(ItemIdentifiers.STEEL_PLATEBODY), new Item(ItemIdentifiers.STEEL_PLATELEGS), new Item(ItemIdentifiers.STEEL_KITESHIELD)),
        STEEL_SET_SK(ItemIdentifiers.STEEL_SET_SK, new Item(ItemIdentifiers.STEEL_FULL_HELM), new Item(ItemIdentifiers.STEEL_PLATEBODY), new Item(ItemIdentifiers.STEEL_PLATESKIRT), new Item(ItemIdentifiers.STEEL_KITESHIELD)),
        BLACK_SET_LG(ItemIdentifiers.BLACK_SET_LG, new Item(ItemIdentifiers.BLACK_FULL_HELM), new Item(ItemIdentifiers.BLACK_PLATEBODY), new Item(ItemIdentifiers.BLACK_PLATELEGS), new Item(ItemIdentifiers.BLACK_KITESHIELD)),
        BLACK_SET_SK(ItemIdentifiers.BLACK_SET_SK, new Item(ItemIdentifiers.BLACK_FULL_HELM), new Item(ItemIdentifiers.BLACK_PLATEBODY), new Item(ItemIdentifiers.BLACK_PLATESKIRT), new Item(ItemIdentifiers.BLACK_KITESHIELD)),
        MITHRIL_SET_LG(ItemIdentifiers.MITHRIL_SET_LG, new Item(ItemIdentifiers.MITHRIL_FULL_HELM), new Item(ItemIdentifiers.MITHRIL_PLATEBODY), new Item(ItemIdentifiers.MITHRIL_PLATELEGS), new Item(ItemIdentifiers.MITHRIL_KITESHIELD)),
        MITHRIL_SET_SK(ItemIdentifiers.MITHRIL_SET_SK, new Item(ItemIdentifiers.MITHRIL_FULL_HELM), new Item(ItemIdentifiers.MITHRIL_PLATEBODY), new Item(ItemIdentifiers.MITHRIL_PLATESKIRT), new Item(ItemIdentifiers.MITHRIL_KITESHIELD)),
        ADAMANT_SET_LG(ItemIdentifiers.ADAMANT_SET_LG, new Item(ItemIdentifiers.ADAMANT_FULL_HELM), new Item(ItemIdentifiers.ADAMANT_PLATEBODY), new Item(ItemIdentifiers.ADAMANT_PLATELEGS), new Item(ItemIdentifiers.ADAMANT_KITESHIELD)),
        ADAMANT_SET_SK(ItemIdentifiers.ADAMANT_SET_SK, new Item(ItemIdentifiers.ADAMANT_FULL_HELM), new Item(ItemIdentifiers.ADAMANT_PLATEBODY), new Item(ItemIdentifiers.ADAMANT_PLATESKIRT), new Item(ItemIdentifiers.ADAMANT_KITESHIELD)),
        RUNE_ARMOUR_SET_LG(ItemIdentifiers.RUNE_ARMOUR_SET_LG, new Item(ItemIdentifiers.RUNE_FULL_HELM), new Item(ItemIdentifiers.RUNE_PLATEBODY), new Item(ItemIdentifiers.RUNE_PLATELEGS), new Item(ItemIdentifiers.RUNE_KITESHIELD)),
        RUNE_ARMOUR_SET_SK(ItemIdentifiers.RUNE_ARMOUR_SET_SK, new Item(ItemIdentifiers.RUNE_FULL_HELM), new Item(ItemIdentifiers.RUNE_PLATEBODY), new Item(ItemIdentifiers.RUNE_PLATESKIRT), new Item(ItemIdentifiers.RUNE_KITESHIELD)),
        MYSTIC_SET_LIGHT(ItemIdentifiers.MYSTIC_SET_LIGHT, new Item(ItemIdentifiers.MYSTIC_HAT_LIGHT), new Item(ItemIdentifiers.MYSTIC_ROBE_TOP_LIGHT), new Item(ItemIdentifiers.MYSTIC_ROBE_BOTTOM_LIGHT), new Item(ItemIdentifiers.MYSTIC_GLOVES_LIGHT), new Item(ItemIdentifiers.MYSTIC_BOOTS_LIGHT)),
        MYSTIC_SET_BLUE(ItemIdentifiers.MYSTIC_SET_BLUE, new Item(ItemIdentifiers.MYSTIC_HAT), new Item(ItemIdentifiers.MYSTIC_ROBE_TOP), new Item(ItemIdentifiers.MYSTIC_ROBE_BOTTOM), new Item(ItemIdentifiers.MYSTIC_GLOVES), new Item(ItemIdentifiers.MYSTIC_BOOTS)),
        MYSTIC_SET_DARK(ItemIdentifiers.MYSTIC_SET_DARK, new Item(ItemIdentifiers.MYSTIC_HAT_DARK), new Item(ItemIdentifiers.MYSTIC_ROBE_TOP_DARK), new Item(ItemIdentifiers.MYSTIC_ROBE_BOTTOM_DARK), new Item(ItemIdentifiers.MYSTIC_GLOVES_DARK), new Item(ItemIdentifiers.MYSTIC_BOOTS_DARK)),
        MYSTIC_SET_DUSK(ItemIdentifiers.MYSTIC_SET_DUSK, new Item(ItemIdentifiers.MYSTIC_HAT_DUSK), new Item(ItemIdentifiers.MYSTIC_ROBE_TOP_DUSK), new Item(ItemIdentifiers.MYSTIC_ROBE_BOTTOM_DUSK), new Item(ItemIdentifiers.MYSTIC_GLOVES_DUSK), new Item(ItemIdentifiers.MYSTIC_BOOTS_DUSK)),

        //Cosmetics
        PARTY_HAT_SET(ItemIdentifiers.PARTYHAT_SET, new Item(ItemIdentifiers.RED_PARTYHAT), new Item(ItemIdentifiers.YELLOW_PARTYHAT), new Item(ItemIdentifiers.BLUE_PARTYHAT), new Item(ItemIdentifiers.GREEN_PARTYHAT), new Item(ItemIdentifiers.PURPLE_PARTYHAT), new Item(ItemIdentifiers.WHITE_PARTYHAT)),
        HALLOWEEN_MASK_SET(ItemIdentifiers.HALLOWEEN_MASK_SET, new Item(ItemIdentifiers.GREEN_HALLOWEEN_MASK), new Item(ItemIdentifiers.BLUE_HALLOWEEN_MASK), new Item(ItemIdentifiers.RED_HALLOWEEN_MASK)),

        // Runes
        FIRE_RUNES(ItemIdentifiers.FIRE_RUNE_PACK, new Item(ItemIdentifiers.FIRE_RUNE, 100)),
        WATER_RUNES(ItemIdentifiers.WATER_RUNE_PACK, new Item(ItemIdentifiers.WATER_RUNE, 100)),
        AIR_RUNES(ItemIdentifiers.AIR_RUNE_PACK, new Item(ItemIdentifiers.AIR_RUNE, 100)),
        EARTH_RUNES(ItemIdentifiers.EARTH_RUNE_PACK, new Item(ItemIdentifiers.EARTH_RUNE, 100)),
        MIND_RUNES(ItemIdentifiers.MIND_RUNE_PACK, new Item(ItemIdentifiers.MIND_RUNE, 100)),
        CHAOS_RUNES(ItemIdentifiers.CHAOS_RUNE_PACK, new Item(ItemIdentifiers.CHAOS_RUNE, 100)),

        // Farming
        SACKS(ItemIdentifiers.SACK_PACK, new Item(ItemIdentifiers.EMPTY_SACK + 1, 100)),
        BASKETS(ItemIdentifiers.BASKET_PACK, new Item(ItemIdentifiers.BASKET + 1, 100)),
        POT(ItemIdentifiers.PLANT_POT_PACK, new Item(ItemIdentifiers.PLANT_POT + 1, 100)), // <-- Insert weed meme here

        // Slayer supplies
        BROAD_ARROWHEADS(ItemIdentifiers.BROAD_ARROWHEAD_PACK, new Item(ItemIdentifiers.BROAD_ARROWHEADS, 100)),
        UNFINISHED_BROAD_BOLTS(ItemIdentifiers.UNFINISHED_BROAD_BOLT_PACK, new Item(ItemIdentifiers.UNFINISHED_BROAD_BOLTS, 100)),

        // Fishing
        BAIT(ItemIdentifiers.BAIT_PACK, new Item(ItemIdentifiers.FISHING_BAIT, 100)),
        FEATHERS(ItemIdentifiers.FEATHER_PACK, new Item(ItemIdentifiers.FEATHER, 100)),

        // Herblore
        VIAL(ItemIdentifiers.EMPTY_VIAL_PACK, new Item(ItemIdentifiers.VIAL + 1, 100)),
        VIAL_OF_WATER(ItemIdentifiers.WATERFILLED_VIAL_PACK, new Item(ItemIdentifiers.VIAL_OF_WATER + 1, 100)),
        EMPTY_JUGS(ItemIdentifiers.EMPTY_JUG_PACK, new Item(ItemIdentifiers.JUG + 1, 100)),
        AMYLASE(ItemIdentifiers.AMYLASE_PACK, new Item(ItemIdentifiers.AMYLASE_CRYSTAL, 100)),

        CANNON(ItemIdentifiers.DWARF_CANNON_SET, new Item(ItemIdentifiers.CANNON_BASE), new Item(ItemIdentifiers.CANNON_BARRELS), new Item(ItemIdentifiers.CANNON_FURNACE), new Item(ItemIdentifiers.CANNON_STAND), new Item(ItemIdentifiers.CANNONBALL, 10_000))
        ;

        ItemSets(final int setId, final Item... items) {
            this.setId = setId;
            this.items = items;
        }

        private final int setId;
        private final Item[] items;

        public int getSetId() {
            return setId;
        }

        public Item[] getItems() {
            return items;
        }

        public static Optional<ItemSets> get(int itemId) {
            for (ItemSets itemSets : ItemSets.values()) {
                if (itemSets.getSetId() == itemId) {
                    return Optional.of(itemSets);
                }
            }
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "Set{" +
                "setId=" + setId +
                ", items=" + Arrays.toString(items) +
                '}';
        }
    }

    public static boolean open(Player player, Item item) {
        Optional<ItemSets> itemSet = ItemSets.get(item.getId());
        if (itemSet.isPresent()) {
            //Check if player has a set in his inventory...
            if (!player.inventory().contains(itemSet.get().getSetId())) {
                return false;
            }
            player.inventory().remove(new Item(itemSet.get().getSetId()), true);
            player.inventory().addOrBank(itemSet.get().getItems());
            return true;
        }
        return false;
    }

}

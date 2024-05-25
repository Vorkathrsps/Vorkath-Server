package com.cryptic.model.content.items.loot.impl;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class MysteryBox implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.BANKERS_NOTE, 1, -1, true),
                new CollectionItem(ItemIdentifiers.BLACK_PARTYHAT, 1, -1, true),
                new CollectionItem(ItemIdentifiers.CORRUPTED_TWISTED_BOW, 1, -1, true),
                new CollectionItem(ItemIdentifiers.CORRUPTED_TUMEKENS_SHADOW, 1, -1, true),
                new CollectionItem(ItemIdentifiers.CORRUPTED_VOIDWAKER, 1, -1, true),
                new CollectionItem(ItemIdentifiers.CORRUPTED_ARMADYL_GODSWORD, 1, -1, true),
                new CollectionItem(ItemIdentifiers.CORRUPTED_DRAGON_CLAWS, 1, -1, true),
                new CollectionItem(ItemIdentifiers.VIRTUS_MASK, 1, -1, true),
                new CollectionItem(ItemIdentifiers.VIRTUS_ROBE_TOP, 1, -1, true),
                new CollectionItem(ItemIdentifiers.VIRTUS_ROBE_BOTTOM, 1, -1, true),
                new CollectionItem(ItemIdentifiers.ULTOR_RING_28307, 1, -1, true),
                new CollectionItem(ItemIdentifiers.MAGUS_RING_28313, 1, -1, true),
                new CollectionItem(ItemIdentifiers.BELLATOR_RING_28316, 1, -1, true),
                new CollectionItem(ItemIdentifiers.VESTAS_LONGSWORD, 2, -1, true),
                new CollectionItem(ItemIdentifiers.VESTAS_CHAINBODY, 2, -1, true),
                new CollectionItem(ItemIdentifiers.VESTAS_PLATESKIRT, 2, -1, true),
                new CollectionItem(ItemIdentifiers.STATIUSS_WARHAMMER, 2, -1, true),
                new CollectionItem(ItemIdentifiers.STATIUSS_FULL_HELM, 2, -1, true),
                new CollectionItem(ItemIdentifiers.STATIUSS_PLATEBODY, 2, -1, true),
                new CollectionItem(ItemIdentifiers.STATIUSS_PLATELEGS, 2, -1, true),
                new CollectionItem(ItemIdentifiers.ZURIELS_STAFF, 2, -1, true),
                new CollectionItem(ItemIdentifiers.ZURIELS_HOOD, 2, -1, true),
                new CollectionItem(ItemIdentifiers.ZURIELS_ROBE_TOP, 2, -1, true),
                new CollectionItem(ItemIdentifiers.ZURIELS_ROBE_BOTTOM, 2, -1, true),
                new CollectionItem(ItemIdentifiers.MORRIGANS_COIF, 2, -1, true),
                new CollectionItem(ItemIdentifiers.MORRIGANS_LEATHER_BODY, 2, -1, true),
                new CollectionItem(ItemIdentifiers.MORRIGANS_LEATHER_CHAPS, 2, -1, true),
                new CollectionItem(ItemIdentifiers.PRIMORDIAL_BOOTS, 5, -1, false),
                new CollectionItem(ItemIdentifiers.PEGASIAN_BOOTS, 5, -1, false),
                new CollectionItem(ItemIdentifiers.ETERNAL_BOOTS, 5, -1, false),
                new CollectionItem(ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD, 5, -1, false),
                new CollectionItem(ItemIdentifiers.STAFF_OF_THE_DEAD, 5, -1, false),
                new CollectionItem(ItemIdentifiers.TRIDENT_OF_THE_SEAS, 5, -1, false),
                new CollectionItem(ItemIdentifiers.TRIDENT_OF_THE_SWAMP, 5, -1, false),
                new CollectionItem(ItemIdentifiers.TOXIC_BLOWPIPE, 5, -1, false),
                new CollectionItem(ItemIdentifiers.SERPENTINE_HELM, 10, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_CHESTPLATE, 10, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_TASSETS, 10, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_BOOTS, 10, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_CHESTPLATE, 10, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_CHAINSKIRT, 10, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_GODSWORD, 15, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_GODSWORD, 15, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_GODSWORD, 15, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_WARHAMMER, 15, -1, false),
                new CollectionItem(ItemIdentifiers.MYSTERY_BOX, 15, -1, false),
                new CollectionItem(ItemIdentifiers.AMULET_OF_TORTURE, 15, -1, false),
                new CollectionItem(ItemIdentifiers.NECKLACE_OF_ANGUISH, 15, -1, false),
                new CollectionItem(ItemIdentifiers.TORMENTED_BRACELET, 15, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_GODSWORD, 2, -1, true),
                new CollectionItem(ItemIdentifiers.ABYSSAL_WHIP, 35, -1, false),
                new CollectionItem(ItemIdentifiers.DHAROKS_ARMOUR_SET, 35, -1, false),
                new CollectionItem(ItemIdentifiers.KARILS_ARMOUR_SET, 35, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHANS_ARMOUR_SET, 35, -1, false),
                new CollectionItem(ItemIdentifiers.AHRIMS_ARMOUR_SET, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MORRIGANS_JAVELIN, 60, 500, false),
                new CollectionItem(ItemIdentifiers.MORRIGANS_THROWING_AXE, 60, 500, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_CROSSBOW, 15, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_CROSSBOW, 25, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGONFIRE_WARD, 15, -1, false),
                new CollectionItem(ItemIdentifiers.COINS_995, 25, 25_000_000, false),
                new CollectionItem(ItemIdentifiers.DRAGON_PICKAXE, 30, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGONFIRE_SHIELD, 25, -1, false),
                new CollectionItem(ItemIdentifiers.OCCULT_NECKLACE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.AMULET_OF_FURY + 1, 50, 5, false),
                new CollectionItem(ItemIdentifiers.FEROCIOUS_GLOVES, 25, -1, true),
                new CollectionItem(ItemIdentifiers.INFERNAL_CAPE, 15, -1, true),
                new CollectionItem(ItemIdentifiers.AVERNIC_DEFENDER, 15, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_HUNTER_LANCE, 15, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_HUNTER_CROSSBOW, 15, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAKIAN_HASTA, 45, -1, false),
                new CollectionItem(ItemIdentifiers.BERSERKER_RING_I, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SEERS_RING_I, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARCHERS_RING_I, 50, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_BOOTS + 1, 50, 5, false),
                new CollectionItem(ItemIdentifiers.SUPERIOR_DRAGON_BONES + 1, 60, 500, false),
                new CollectionItem(ItemIdentifiers.LAVA_DRAGON_BONES + 1, 60, 500, false),
                new CollectionItem(ItemIdentifiers.SUPER_COMBAT_POTION4 + 1, 60, 500, false),
                new CollectionItem(ItemIdentifiers.BASTION_POTION4 + 1, 60, 500, false),
            };
    }

    @Override
    public String name() {
        return "Mystery Box";
    }

    @Override
    public int id() {
        return CustomItemIdentifiers.BOX_OF_VALOR;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.MYSTERY_BOXES_OPENED;
    }

    @Override
    public LogType logType() {
        return LogType.MYSTERY_BOX;
    }
}

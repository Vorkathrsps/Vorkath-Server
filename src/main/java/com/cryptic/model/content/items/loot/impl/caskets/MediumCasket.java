package com.cryptic.model.content.items.loot.impl.caskets;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class MediumCasket implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.CLIMBING_BOOTS_G, 35, -1, true),
                new CollectionItem(ItemIdentifiers.ADAMANT_FULL_HELM_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATELEGS_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATESKIRT_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_KITESHIELD_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_FULL_HELM_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATELEGS_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATESKIRT_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_KITESHIELD_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_SHIELD_H1, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_SHIELD_H2, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_SHIELD_H3, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_SHIELD_H4, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_SHIELD_H5, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_HELM_H1, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_HELM_H2, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_HELM_H3, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_HELM_H4, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_HELM_H5, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY_H1, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY_H2, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY_H3, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY_H4, 35, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY_H5, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_FULL_HELM_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_PLATEBODY_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_PLATELEGS_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_PLATESKIRT_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_FULL_HELM_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_PLATEBODY_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_PLATELEGS_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_PLATESKIRT_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_KITESHIELD_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_DHIDE_BODY_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_DHIDE_BODY_T, 35, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_DHIDE_CHAPS_G, 35, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_DHIDE_CHAPS_T, 35,-1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_MITRE, 35,-1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_CLOAK, 35,-1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_MITRE, 35,-1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_CLOAK, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_MITRE, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_CLOAK, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_MITRE, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_CLOAK, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_MITRE, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_CLOAK, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_CROZIER, 35,-1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_CROZIER, 35,-1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_MITRE, 35,-1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_CLOAK, 35,-1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_STOLE, 35,-1, false),
                new CollectionItem(ItemIdentifiers.PURPLE_SWEETS, 25, 25, false),
                new CollectionItem(ItemIdentifiers.HOLY_BLESSING, 25, -1, false),
                new CollectionItem(ItemIdentifiers.UNHOLY_BLESSING, 25, -1, false),
                new CollectionItem(ItemIdentifiers.PEACEFUL_BLESSING, 25, -1, false),
                new CollectionItem(ItemIdentifiers.WAR_BLESSING, 25, -1, false),
                new CollectionItem(ItemIdentifiers.HONOURABLE_BLESSING, 25, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_BLESSING, 25, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_STOLE, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_STOLE, 25, -1, false),
                new CollectionItem(ItemIdentifiers.RED_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.ORANGE_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.PINK_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.PURPLE_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.WHITE_BOATER, 25, -1, false),
                new CollectionItem(ItemIdentifiers.RED_HEADBAND, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_HEADBAND, 40, -1, false),
                new CollectionItem(ItemIdentifiers.BROWN_HEADBAND, 40, -1, false),
                new CollectionItem(ItemIdentifiers.WHITE_HEADBAND, 40, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_HEADBAND, 40, -1, false),
                new CollectionItem(ItemIdentifiers.GOLD_HEADBAND, 40, -1, false),
                new CollectionItem(ItemIdentifiers.PINK_HEADBAND, 40, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_HEADBAND, 40, -1, false),
                new CollectionItem(ItemIdentifiers.CRIER_HAT, 40, -1, false),
                new CollectionItem(ItemIdentifiers.CRIER_COAT, 40, -1, false),
                new CollectionItem(ItemIdentifiers.CRIER_BELL, 40, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_CANE, 40, -1, false),
                new CollectionItem(ItemIdentifiers.ARCEUUS_BANNER, 40, -1, false),
                new CollectionItem(ItemIdentifiers.PISCARILIUS_BANNER, 40, -1, false),
                new CollectionItem(ItemIdentifiers.HOSIDIUS_BANNER, 40, -1, false),
                new CollectionItem(ItemIdentifiers.SHAYZIEN_BANNER, 40, -1, false),
                new CollectionItem(ItemIdentifiers.LOVAKENGJ_BANNER, 40, -1, false),
                new CollectionItem(ItemIdentifiers.CABBAGE_ROUND_SHIELD, 50, -1, false),
                new CollectionItem(ItemIdentifiers.CAT_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PENGUIN_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.LEPRECHAUN_HAT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_LEPRECHAUN_HAT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.WOLF_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.WOLF_CLOAK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_UNICORN_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PURPLE_ELEGANT_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PURPLE_ELEGANT_BLOUSE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PURPLE_ELEGANT_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PURPLE_ELEGANT_SKIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_ELEGANT_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.WHITE_ELEGANT_BLOUSE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.WHITE_ELEGANT_SKIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_ELEGANT_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PINK_ELEGANT_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PINK_ELEGANT_BLOUSE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PINK_ELEGANT_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PINK_ELEGANT_SKIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GOLD_ELEGANT_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GOLD_ELEGANT_BLOUSE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GOLD_ELEGANT_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GOLD_ELEGANT_SKIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_FULL_HELM, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATEBODY, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PLATELEGS, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_LONGSWORD, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_DAGGER, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_BATTLEAXE, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_AXE, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_PICKAXE, 100, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_DHIDE_BODY, 100, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_DHIDE_CHAPS, 100, -1, false),
                new CollectionItem(ItemIdentifiers.YEW_SHORTBOW, 100, -1, false),
                new CollectionItem(ItemIdentifiers.FIRE_BATTLESTAFF, 100, -1, false),
                new CollectionItem(ItemIdentifiers.YEW_LONGBOW, 100, -1, false),
                new CollectionItem(ItemIdentifiers.AMULET_OF_POWER, 100, -1, false),
                new CollectionItem(ItemIdentifiers.YEW_COMP_BOW, 100, -1, false),
                new CollectionItem(ItemIdentifiers.STRENGTH_AMULET_T, 100, -1, false),
                new CollectionItem(ItemIdentifiers.LOBSTER + 1, 150, 100, false),
                new CollectionItem(ItemIdentifiers.SWORDFISH + 1, 150, 100, false),
                new CollectionItem(ItemIdentifiers.AIR_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.MIND_RUNE, 150, 150, false),
                new CollectionItem(ItemIdentifiers.WATER_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.EARTH_RUNE, 150, 150, false),
                new CollectionItem(ItemIdentifiers.FIRE_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.CHAOS_RUNE, 150, 150, false),
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.LAW_RUNE, 150, 150, false),
                new CollectionItem(ItemIdentifiers.DEATH_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.COINS_995, 150, 100_000, false),
                new CollectionItem(ItemIdentifiers.RANGER_BOOTS, 8, -1, true),
                new CollectionItem(ItemIdentifiers.WIZARD_BOOTS, 8, -1, true),
                new CollectionItem(ItemIdentifiers.HOLY_SANDALS, 8, -1, true),
                new CollectionItem(ItemIdentifiers.SPIKED_MANACLES, 8, -1, true),
            };
    }

    @Override
    public String name() {
        return "Medium Casket";
    }

    @Override
    public int id() {
        return ItemIdentifiers.REWARD_CASKET_MEDIUM;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.MEDIUM_CLUE_REWARD_OPENED;
    }

    @Override
    public LogType logType() {
        return null;
    }
}

package com.cryptic.model.content.mechanics.death;

import com.cryptic.utility.ItemIdentifiers;

public enum Breakable {
    FIRE_CAPE(ItemIdentifiers.FIRE_CAPE, ItemIdentifiers.FIRE_CAPE_BROKEN, 150000, -1),
    VOID_RANGE_HELM_OR(ItemIdentifiers.VOID_RANGER_HELM_OR, ItemIdentifiers.VOID_RANGER_HELM_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    VOID_MELEE_HELM_OR(ItemIdentifiers.VOID_MELEE_HELM_OR, ItemIdentifiers.VOID_MELEE_HELM_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    VOID_MAGE_HELM_OR(ItemIdentifiers.VOID_MAGE_HELM_OR, ItemIdentifiers.VOID_MAGE_HELM_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    ELITE_VOID_TOP_OR(ItemIdentifiers.ELITE_VOID_TOP_OR, ItemIdentifiers.ELITE_VOID_TOP_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    ELITE_VOID_ROBE_OR(ItemIdentifiers.ELITE_VOID_ROBE_OR, ItemIdentifiers.ELITE_VOID_ROBE_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    VOID_KNIGHT_TOP_OR(ItemIdentifiers.VOID_KNIGHT_TOP_OR, ItemIdentifiers.VOID_KNIGHT_TOP_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    VOID_KNIGHT_ROBE_OR(ItemIdentifiers.VOID_KNIGHT_ROBE_OR, ItemIdentifiers.VOID_KNIGHT_ROBE_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    VOID_KNIGHT_GLOVES_OR(ItemIdentifiers.VOID_KNIGHT_GLOVES_OR, ItemIdentifiers.VOID_KNIGHT_GLOVES_BROKEN, 150_000, ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT),
    MAGMA_HELM(ItemIdentifiers.MAGMA_HELM, ItemIdentifiers.MAGMA_MUTAGEN, -1, ItemIdentifiers.SERPENTINE_HELM),
    TANZANITE_HELM(ItemIdentifiers.TANZANITE_HELM, ItemIdentifiers.TANZANITE_MUTAGEN, -1, ItemIdentifiers.SERPENTINE_HELM),
    FIGHTER_TORSO_OR(ItemIdentifiers.FIGHTER_TORSO_OR, ItemIdentifiers.FIGHTER_TORSO_BROKEN, 100_000, ItemIdentifiers.BOUNTY_HUNTER_ORNAMENT_KIT),
    FIGHTER_TORSO(ItemIdentifiers.FIGHTER_TORSO, ItemIdentifiers.FIGHTER_TORSO_BROKEN, 100_000, -1),
    HOLY_BOOK_OR(ItemIdentifiers.HOLY_BOOK_OR, ItemIdentifiers.HOLY_BOOK, -1, ItemIdentifiers.SHATTERED_RELICS_VARIETY_ORNAMENT_KIT),
    UNHOLY_BOOK_OR(ItemIdentifiers.UNHOLY_BOOK_OR, ItemIdentifiers.UNHOLY_BOOK, -1, ItemIdentifiers.SHATTERED_RELICS_VARIETY_ORNAMENT_KIT),
    BOOK_OF_BALANCE_OR(ItemIdentifiers.BOOK_OF_BALANCE_OR, ItemIdentifiers.BOOK_OF_BALANCE, -1, ItemIdentifiers.SHATTERED_RELICS_VARIETY_ORNAMENT_KIT),
    BOOK_OF_WAR_OR(ItemIdentifiers.BOOK_OF_WAR_OR, ItemIdentifiers.BOOK_OF_WAR, -1, ItemIdentifiers.SHATTERED_RELICS_VARIETY_ORNAMENT_KIT),
    BOOK_OF_LAW_OR(ItemIdentifiers.BOOK_OF_LAW_OR, ItemIdentifiers.BOOK_OF_LAW, -1, ItemIdentifiers.SHATTERED_RELICS_VARIETY_ORNAMENT_KIT),
    BOOK_OF_DARKNESS_OR(ItemIdentifiers.BOOK_OF_DARKNESS_OR, ItemIdentifiers.BOOK_OF_DARKNESS, -1, ItemIdentifiers.SHATTERED_RELICS_VARIETY_ORNAMENT_KIT),
    MASORI_ASSEMBLER(ItemIdentifiers.MASORI_ASSEMBLER, ItemIdentifiers.MASORI_ASSEMBLER_BROKEN, 1_000_000, -1),
    DRAGON_DEFENDER_T(ItemIdentifiers.DRAGON_DEFENDER_T, ItemIdentifiers.DRAGON_DEFENDER_BROKEN, 150_000, ItemIdentifiers.DRAGON_DEFENDER_ORNAMENT_KIT),
    AVERNIC_DEFENDER(ItemIdentifiers.AVERNIC_DEFENDER, ItemIdentifiers.AVERNIC_DEFENDER_BROKEN, 1_000_000, -1),
    RUNE_DEFENDER_T(ItemIdentifiers.RUNE_DEFENDER_T, ItemIdentifiers.RUNE_DEFENDER_BROKEN, 50_000, ItemIdentifiers.RUNE_DEFENDER_ORNAMENT_KIT),
    INFERNAL_CAPE(ItemIdentifiers.INFERNAL_CAPE, ItemIdentifiers.INFERNAL_CAPE_BROKEN, 1_000_000, -1);


    final int id;
    final int brokenId;
    final int coinAmount;
    final int itemConversion;

    Breakable(int id, int brokenId, int coinAmount, int itemConversion) {
        this.id = id;
        this.brokenId = brokenId;
        this.coinAmount = coinAmount;
        this.itemConversion = itemConversion;
    }

}

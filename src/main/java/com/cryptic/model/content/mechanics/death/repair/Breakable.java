package com.cryptic.model.content.mechanics.death.repair;

import com.cryptic.utility.ItemIdentifiers;

import java.text.NumberFormat;

public enum Breakable {
    FIRE_CAPE(ItemIdentifiers.FIRE_CAPE, ItemIdentifiers.FIRE_CAPE_BROKEN, 150000, -1),
    VOID_RANGE_HELM(ItemIdentifiers.VOID_RANGER_HELM, ItemIdentifiers.VOID_RANGER_HELM_BROKEN, 100_000, -1),
    VOID_MAGE_HELM(ItemIdentifiers.VOID_MAGE_HELM, ItemIdentifiers.VOID_MAGE_HELM_BROKEN, 100_000, -1),
    VOID_MELEE_HELM(ItemIdentifiers.VOID_MELEE_HELM, ItemIdentifiers.VOID_MELEE_HELM_BROKEN, 100_000, -1),
    VOID_ROBE_TOP(ItemIdentifiers.VOID_KNIGHT_TOP, ItemIdentifiers.VOID_KNIGHT_TOP_BROKEN, 100_000, -1),
    ELITE_VOID_TOP(ItemIdentifiers.ELITE_VOID_TOP, ItemIdentifiers.ELITE_VOID_TOP_BROKEN, 200_000, -1),
    VOID_ROBE_BOTTOM(ItemIdentifiers.VOID_KNIGHT_ROBE, ItemIdentifiers.VOID_KNIGHT_ROBE_BROKEN, 100_000, -1),
    ELITE_VOID_BOTTOM(ItemIdentifiers.ELITE_VOID_ROBE, ItemIdentifiers.ELITE_VOID_ROBE_BROKEN, 200_000, -1),
    VOID_GLOVES(ItemIdentifiers.VOID_KNIGHT_GLOVES, ItemIdentifiers.VOID_KNIGHT_GLOVES_BROKEN, 100_000, -1),
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
    INFERNAL_CAPE(ItemIdentifiers.INFERNAL_CAPE, ItemIdentifiers.INFERNAL_CAPE_BROKEN, 1_000_000, -1),
    STATIUS_FULL_HELM(ItemIdentifiers.STATIUSS_FULL_HELM_BH, ItemIdentifiers.STATIUSS_FULL_HELM_BHINACTIVE, 50_000_000, -1),
    STATIUS_PLATEBODY(ItemIdentifiers.STATIUSS_PLATEBODY_BH, ItemIdentifiers.STATIUSS_PLATEBODY_BHINACTIVE, 50_000_000, -1),
    STATIUS_PLATELEGS(ItemIdentifiers.STATIUSS_PLATELEGS_BH, ItemIdentifiers.STATIUSS_PLATELEGS_BHINACTIVE, 50_000_000, -1),
    CORRUPTED_STATIUS_FULL_HELM(ItemIdentifiers.CORRUPTED_STATIUSS_FULL_HELM_BH, ItemIdentifiers.CORRUPTED_STATIUSS_FULL_HELM_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_STATIUS_PLATEBODY(ItemIdentifiers.CORRUPTED_STATIUSS_PLATEBODY_BH, ItemIdentifiers.CORRUPTED_STATIUSS_PLATEBODY_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_STATIUS_PLATELEGS(ItemIdentifiers.CORRUPTED_STATIUSS_PLATELEGS_BH, ItemIdentifiers.CORRUPTED_STATIUSS_PLATELEGS_BHINACTIVE, 25_000_000, -1),
    VESTAS_LONGSWORD(ItemIdentifiers.VESTAS_LONGSWORD_BH, ItemIdentifiers.VESTAS_LONGSWORD_BHINACTIVE, 50_000_000, -1),
    VESTA_PLATEBODY(ItemIdentifiers.VESTAS_CHAINBODY_BH, ItemIdentifiers.VESTAS_CHAINBODY_BHINACTIVE, 50_000_000, -1),
    VESTA_CHAINSKIRT(ItemIdentifiers.VESTAS_PLATESKIRT_BH, ItemIdentifiers.VESTAS_PLATESKIRT_BHINACTIVE, 50_000_000, -1),
    CORRUPTED_VESTA_PLATEBODY(ItemIdentifiers.CORRUPTED_VESTAS_CHAINBODY_BH, ItemIdentifiers.CORRUPTED_VESTAS_CHAINBODY_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_VESTA_CHAINSKIRT(ItemIdentifiers.CORRUPTED_VESTAS_PLATESKIRT_BH, ItemIdentifiers.CORRUPTED_VESTAS_PLATESKIRT_BHINACTIVE, 25_000_000, -1),
    MORRIGANS_COIF(ItemIdentifiers.MORRIGANS_COIF_BH, ItemIdentifiers.MORRIGANS_COIF_BHINACTIVE, 50_000_000, -1),
    MORRIGANS_BODY(ItemIdentifiers.MORRIGANS_LEATHER_BODY_BH, ItemIdentifiers.MORRIGANS_LEATHER_BODY_BHINACTIVE, 50_000_000, -1),
    MORRIGANS_LEGS(ItemIdentifiers.MORRIGANS_LEATHER_CHAPS_BH, ItemIdentifiers.MORRIGANS_LEATHER_CHAPS_BHINACTIVE, 50_000_000, -1),
    CORRUPTED_MORRIGANS_COIF(ItemIdentifiers.CORRUPTED_MORRIGANS_COIF_BH, ItemIdentifiers.CORRUPTED_MORRIGANS_COIF_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_MORRIGANS_BODY(ItemIdentifiers.CORRUPTED_MORRIGANS_LEATHER_BODY_BH, ItemIdentifiers.CORRUPTED_MORRIGANS_LEATHER_BODY_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_MORRIGANS_LEGS(ItemIdentifiers.CORRUPTED_MORRIGANS_LEATHER_CHAPS_BH, ItemIdentifiers.CORRUPTED_MORRIGANS_LEATHER_CHAPS_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_ZURIEL_HOOD(ItemIdentifiers.CORRUPTED_ZURIELS_HOOD_BH, ItemIdentifiers.CORRUPTED_ZURIELS_HOOD_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_ZURIEL_TOP(ItemIdentifiers.CORRUPTED_ZURIELS_ROBE_TOP_BH, ItemIdentifiers.CORRUPTED_ZURIELS_ROBE_TOP_BHINACTIVE, 25_000_000, -1),
    CORRUPTED_ZURIEL_LEGS(ItemIdentifiers.CORRUPTED_ZURIELS_ROBE_BOTTOM_BH, ItemIdentifiers.CORRUPTED_ZURIELS_ROBE_BOTTOM_BHINACTIVE, 25_000_000, -1),
    ZURIEL_HOOD(ItemIdentifiers.ZURIELS_HOOD_BH, ItemIdentifiers.ZURIELS_HOOD_BHINACTIVE, 50_000_000, -1),
    ZURIEL_TOP(ItemIdentifiers.ZURIELS_ROBE_TOP_BH, ItemIdentifiers.ZURIELS_ROBE_TOP_BH, 50_000_000, -1),
    ZURIEL_LEGS(ItemIdentifiers.ZURIELS_ROBE_BOTTOM_BH, ItemIdentifiers.ZURIELS_ROBE_BOTTOM_BHINACTIVE, 50_000_000, -1),
    ZURIEL_STAFF(ItemIdentifiers.ZURIELS_STAFF_BH, ItemIdentifiers.ZURIELS_STAFF_BHINACTIVE, 50_000_000, -1),
    STATIUS_WARHAMMER(ItemIdentifiers.STATIUSS_WARHAMMER_BH, ItemIdentifiers.STATIUSS_WARHAMMER_BHINACTIVE, 50_000_000, -1),
    ITHELL_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C_25884, ItemIdentifiers.CRYSTAL_OF_ITHELL, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    IORWERTH_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C_25886, ItemIdentifiers.CRYSTAL_OF_IORWERTH, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    TRAHAEARN_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C_25888, ItemIdentifiers.CRYSTAL_OF_TRAHAEARN, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    CADARN_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C_25890, ItemIdentifiers.CRYSTAL_OF_CADARN, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    CRWYS_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C_25892, ItemIdentifiers.CRYSTAL_OF_CRWYS, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    MEILYR_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C_25894, ItemIdentifiers.CRYSTAL_OF_MEILYR, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    HEFIN_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C, ItemIdentifiers.CRYSTAL_OF_HEFIN, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    AMLODD_BOWFA(ItemIdentifiers.BOW_OF_FAERDHINEN_C_25896, ItemIdentifiers.CRYSTAL_OF_AMLODD, -1, ItemIdentifiers.BOW_OF_FAERDHINEN_INACTIVE),
    IMBUED_ZAMORAK_CAPE(ItemIdentifiers.IMBUED_ZAMORAK_CAPE, ItemIdentifiers.IMBUED_ZAMORAK_CAPE_BROKEN, 500_000, -1),
    IMBUED_SARADOMIN_CAPE(ItemIdentifiers.IMBUED_SARADOMIN_CAPE, ItemIdentifiers.IMBUED_SARADOMIN_CAPE_BROKEN, 500_000, -1),
    IMBUED_GUTHIX_CAPE(ItemIdentifiers.IMBUED_GUTHIX_CAPE, ItemIdentifiers.IMBUED_GUTHIX_CAPE_BROKEN, 500_000, -1),
    AVAS_ASSEMBLER(ItemIdentifiers.AVAS_ASSEMBLER, ItemIdentifiers.AVAS_ASSEMBLER_BROKEN, 500_000, -1),
    DRAGON_DEFENDER(ItemIdentifiers.DRAGON_DEFENDER, ItemIdentifiers.DRAGON_DEFENDER_BROKEN, 50_000, -1);

    public final int id;
    public final int brokenId;
    public final int coinAmount;
    public final int itemConversion;

    Breakable(int id, int brokenId, int coinAmount, int itemConversion) {
        this.id = id;
        this.brokenId = brokenId;
        this.coinAmount = coinAmount;
        this.itemConversion = itemConversion;
    }

    public int getRepairCost() {
        return (int) (coinAmount * 1.50);
    }

    public String getFormattedRepairCost() {
        int repairCost = getRepairCost();
        NumberFormat numberFormat = NumberFormat.getInstance();
        return numberFormat.format(repairCost);
    }

}

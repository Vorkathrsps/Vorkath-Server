package com.cryptic.model.content.collection_logs;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.items.Item;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.ItemIdentifiers;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author PVE
 * @Since juli 15, 2020
 */
public enum Collection {

    CALLISTO(AttributeKey.CALLISTOS_KILLED, LogType.BOSSES, "Callisto", new int[]{NpcIdentifiers.CALLISTO, NpcIdentifiers.CALLISTO_6609}, AttributeKey.CALLISTO_LOG_CLAIMED, new Item[]{new Item(995, 15_000_000), new Item(6199, 1)},
        //Drops
        new Item(ItemIdentifiers.CALLISTO_CUB), new Item(DRAGON_PICKAXE), new Item(DRAGON_2H_SWORD), new Item(TYRANNICAL_RING), new Item(VOIDWAKER_HILT), new Item(CLAWS_OF_CALLISTO)),

    CERBERUS(AttributeKey.CERBERUS_KILLED, LogType.BOSSES, "Cerberus", new int[]{NpcIdentifiers.CERBERUS}, AttributeKey.CERBERUS_LOG_CLAIMED, new Item[]{new Item(995, 25_000_000), new Item(6199, 1)},
        //Drops
        new Item(ItemIdentifiers.HELLPUPPY), new Item(PRIMORDIAL_CRYSTAL), new Item(PEGASIAN_CRYSTAL), new Item(ETERNAL_CRYSTAL), new Item(SMOULDERING_STONE), new Item(JAR_OF_SOULS)),

    CHAOS_ELEMENTAL(AttributeKey.CHAOS_ELEMENTALS_KILLED, LogType.BOSSES, "Chaos Elemental", new int[]{NpcIdentifiers.CHAOS_ELEMENTAL}, AttributeKey.CHAOS_ELEMENTAL_LOG_CLAIMED, new Item[]{new Item(995, 5_000_000), new Item(6199, 1)},
        //Drops
        new Item(ItemIdentifiers.PET_CHAOS_ELEMENTAL), new Item(DRAGON_2H_SWORD), new Item(DRAGON_PICKAXE)),

    CHAOS_FANATIC(AttributeKey.CHAOS_FANATICS_KILLED, LogType.BOSSES, "Chaos Fanatic", new int[]{NpcIdentifiers.CHAOS_FANATIC}, AttributeKey.CHAOS_FANATIC_LOG_CLAIMED,
        //Drops
        new Item[]{new Item(995, 5_000_000),
            new Item(6199, 1)},
        new Item(ODIUM_WARD),
        new Item(MALEDICTION_WARD)),

    CORPOREAL_BEAST(AttributeKey.CORPOREAL_BEASTS_KILLED, LogType.BOSSES, "Corporeal Beast", new int[]{NpcIdentifiers.CORPOREAL_BEAST}, AttributeKey.CORPOREAL_BEAST_LOG_CLAIMED, new Item[]{new Item(995, 50_000_000), new Item(6199, 3)},
        //Drops
        new Item(ItemIdentifiers.PET_DARK_CORE), new Item(ELYSIAN_SIGIL), new Item(SPECTRAL_SIGIL), new Item(ARCANE_SIGIL), new Item(SPIRIT_SHIELD), new Item(HOLY_ELIXIR)),

    CRAZY_ARCHAEOLOGIST(AttributeKey.CRAZY_ARCHAEOLOGISTS_KILLED, LogType.BOSSES, "Crazy Archaeologist", new int[]{NpcIdentifiers.CRAZY_ARCHAEOLOGIST}, AttributeKey.CRAZY_ARCHAEOLOGIST_LOG_CLAIMED, new Item[]{new Item(995, 5_000_000), new Item(6199, 1)},
        //Drops
        new Item(ODIUM_SHARD_2), new Item(MALEDICTION_SHARD_2), new Item(FEDORA)),

    DEMONIC_GORILLA(AttributeKey.DEMONIC_GORILLAS_KILLED, LogType.BOSSES, "Demonic Gorilla",
        new int[]{
            NpcIdentifiers.DEMONIC_GORILLA,
            NpcIdentifiers.DEMONIC_GORILLA_7145,
            NpcIdentifiers.DEMONIC_GORILLA_7146,
            NpcIdentifiers.DEMONIC_GORILLA_7147,
            NpcIdentifiers.DEMONIC_GORILLA_7148,
            NpcIdentifiers.DEMONIC_GORILLA_7149
        },

        AttributeKey.DEMONIC_GORILLA_LOG_CLAIMED, new Item[]{new Item(COINS_995, 25_000_000), new Item(MYSTERY_BOX, 1)},
        //Drops
        new Item(DRAGON_JAVELIN),
        new Item(MONKEY_TAIL),
        new Item(BALLISTA_LIMBS),
        new Item(BALLISTA_SPRING),
        new Item(HEAVY_FRAME),
        new Item(LIGHT_FRAME),
        new Item(UNCUT_ZENYTE)),

    KING_BLACK_DRAGON(AttributeKey.KING_BLACK_DRAGONS_KILLED, LogType.BOSSES, "King Black Dragon",
        new int[]{
            NpcIdentifiers.KING_BLACK_DRAGON},
        AttributeKey.KING_BLACK_DRAGON_LOG_CLAIMED,
        new Item[]{
            new Item(COINS_995, 25_000_000),
            new Item(MYSTERY_BOX, 2)
        },
        //Drops
        new Item(ItemIdentifiers.PRINCE_BLACK_DRAGON), new Item(KBD_HEADS), new Item(DRAGON_PICKAXE), new Item(DRACONIC_VISAGE)),

    KRAKEN(AttributeKey.KRAKENS_KILLED, LogType.BOSSES, "Kraken",
        new int[]
            {
                NpcIdentifiers.KRAKEN
            },
        AttributeKey.KRAKEN_LOG_CLAIMED,
        new Item[]{
            new Item(COINS_995, 15_000_000),
            new Item(MYSTERY_BOX)
        },
        //Drops
        new Item(ItemIdentifiers.PET_KRAKEN), new Item(ABYSSAL_TENTACLE), new Item(TRIDENT_OF_THE_SEAS), new Item(JAR_OF_DIRT)),

    LAVA_DRAGON(AttributeKey.LAVA_DRAGONS_KILLED, LogType.BOSSES, "Lava Dragon",
        new int[]
            {
                NpcIdentifiers.LAVA_DRAGON
            },

        AttributeKey.LAVA_DRAGON_LOG_CLAIMED,

        new Item[]
            {
                new Item(MYSTERY_BOX, 2)
            },
        //Drops
        new Item(DRACONIC_VISAGE)),

    LIZARDMAN_SHAMAN(AttributeKey.LIZARDMAN_SHAMANS_KILLED, LogType.BOSSES, "Lizardman Shaman",

        new int[]
            {
                NpcIdentifiers.LIZARDMAN_SHAMAN,
                NpcIdentifiers.LIZARDMAN_SHAMAN_6767
            },
        AttributeKey.LIZARDMAN_SHAMAN_LOG_CLAIMED,

        new Item[]
            {
                new Item(MYSTERY_BOX, 3)
            },
        //Drops
        new Item(DRAGON_WARHAMMER)),

    SCORPIA(AttributeKey.SCORPIAS_KILLED, LogType.BOSSES, "Scorpia",
        new int[]
            {
                NpcIdentifiers.SCORPIA
            },

        AttributeKey.SCORPIA_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 15_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(ItemIdentifiers.SCORPIAS_OFFSPRING),
        new Item(ODIUM_WARD)
    ),

    THERMONUCLEAR_SMOKE_DEVIL(AttributeKey.THERMONUCLEAR_SMOKE_DEVILS_KILLED, LogType.BOSSES, "Thermonuclear Smoke Devil",

        new int[]
            {
                NpcIdentifiers.THERMONUCLEAR_SMOKE_DEVIL
            },
        AttributeKey.THERMONUCLEAR_SMOKE_DEVIL_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 15_000_000),
                new Item(MYSTERY_BOX, 1)
            },

        new Item(ItemIdentifiers.PET_SMOKE_DEVIL), new Item(OCCULT_NECKLACE)),

    VENENATIS(AttributeKey.VENENATIS_KILLED, LogType.BOSSES, "Venenatis",

        new int[]
            {
                NpcIdentifiers.VENENATIS, NpcIdentifiers.VENENATIS_6610
            },

        AttributeKey.VENENATIS_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 25_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(ItemIdentifiers.VENENATIS_SPIDERLING),
        new Item(TREASONOUS_RING),
        new Item(DRAGON_PICKAXE),
        new Item(VOIDWAKER_GEM),
        new Item(FANGS_OF_VENENATIS)
    ),

    VETION(AttributeKey.VETIONS_KILLED, LogType.BOSSES, "Vet'ion",

        new int[]
            {
                NpcIdentifiers.VETION_REBORN
            },

        AttributeKey.VETION_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 25_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(VETION_JR_13180),
        new Item(DRAGON_PICKAXE),
        new Item(RING_OF_THE_GODS),
        new Item(VOIDWAKER_BLADE),
        new Item(SKULL_OF_VETION)
    ),

    GENERAL_GRAARDOR(AttributeKey.GENERAL_GRAARDOR_KILLED, LogType.BOSSES, "General Graardor",

        new int[]
            {
                NpcIdentifiers.GENERAL_GRAARDOR,
                NpcIdentifiers.GENERAL_GRAARDOR_6494
            },

        AttributeKey.GENERAL_GRAARDOR_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 25_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(PET_GENERAL_GRAARDOR),
        new Item(BANDOS_CHESTPLATE),
        new Item(BANDOS_TASSETS),
        new Item(BANDOS_BOOTS),
        new Item(BANDOS_HILT)
    ),

    KREE_ARRA(AttributeKey.KREE_ARRA_KILLED, LogType.BOSSES, "Kree' Arra",

        new int[]
            {
                NpcIdentifiers.KREEARRA,
                NpcIdentifiers.KREEARRA_6492
            },

        AttributeKey.KREE_ARRA_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 25_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(PET_KREEARRA),
        new Item(ARMADYL_HELMET),
        new Item(ARMADYL_CHESTPLATE),
        new Item(ARMADYL_CHAINSKIRT),
        new Item(ARMADYL_HILT)
    ),

    VORKATH(AttributeKey.VORKATHS_KILLED, LogType.BOSSES, "Vorkath",

        new int[]
            {
                NpcIdentifiers.VORKATH
            },

        AttributeKey.VORKATH_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 50_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(ItemIdentifiers.VORKI),
        new Item(VORKATHS_HEAD),
        new Item(DRACONIC_VISAGE),
        new Item(DRAGON_CROSSBOW),
        new Item(SKELETAL_VISAGE),
        new Item(JAR_OF_DECAY),
        new Item(DRAGONBONE_NECKLACE)
    ),

    ZULRAH(AttributeKey.ZULRAHS_KILLED, LogType.BOSSES, "Zulrah",

        new int[]
            {
                NpcIdentifiers.ZULRAH, NpcIdentifiers.ZULRAH_2044, NpcIdentifiers.ZULRAH_2043
            },

        AttributeKey.ZULRAH_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 50_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(PET_SNAKELING),
        new Item(TANZANITE_MUTAGEN),
        new Item(MAGMA_MUTAGEN),
        new Item(JAR_OF_SWAMP),
        new Item(MAGIC_FANG),
        new Item(TANZANITE_FANG),
        new Item(ZULANDRA_TELEPORT),
        new Item(UNCUT_ONYX),
        new Item(ZULRAHS_SCALES)
    ),

    ALCHEMICAL_HYDRA(AttributeKey.ALCHY_KILLED, LogType.BOSSES, "Alchemical Hydra", new int[]{NpcIdentifiers.ALCHEMICAL_HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA_8616, NpcIdentifiers.ALCHEMICAL_HYDRA_8617, NpcIdentifiers.ALCHEMICAL_HYDRA_8618, NpcIdentifiers.ALCHEMICAL_HYDRA_8619, NpcIdentifiers.ALCHEMICAL_HYDRA_8620, NpcIdentifiers.ALCHEMICAL_HYDRA_8621, NpcIdentifiers.ALCHEMICAL_HYDRA_8622}, AttributeKey.ALCHEMICAL_HYDRA_LOG_CLAIMED, new Item[]{new Item(995, 25_000_000), new Item(6199, 2)},
        //Drops
        new Item(ItemIdentifiers.IKKLE_HYDRA), new Item(HYDRAS_CLAW), new Item(HYDRA_TAIL), new Item(FEROCIOUS_GLOVES), new Item(BRIMSTONE_RING), new Item(DRAGON_KNIFE), new Item(DRAGON_THROWNAXE), new Item(JAR_OF_CHEMICALS), new Item(ALCHEMICAL_HYDRA_HEADS)),

    GIANT_MOLE(AttributeKey.KC_GIANTMOLE, LogType.BOSSES, "Giant Mole",

        new int[]
            {
                NpcIdentifiers.GIANT_MOLE
            },

        AttributeKey.GIANT_MOLE_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 10_000_000),
                new Item(MYSTERY_BOX, 1)
            },

        new Item(ItemIdentifiers.BABY_MOLE),
        new Item(MOLE_SKIN),
        new Item(MOLE_CLAW)
    ),


    THE_NIGHTMARE(AttributeKey.THE_NIGHTMARE_KC, LogType.BOSSES, "The nightmare", new int[]{THE_NIGHTMARE_9430}, AttributeKey.THE_NIGTHMARE_LOG_CLAIMED, new Item[]{new Item(ItemIdentifiers.LITTLE_NIGHTMARE), new Item(ItemIdentifiers.CRYSTAL_KEY)},
        new Item(INQUISITORS_MACE), new Item(INQUISITORS_GREAT_HELM), new Item(INQUISITORS_HAUBERK), new Item(INQUISITORS_PLATESKIRT), new Item(NIGHTMARE_STAFF), new Item(ELDRITCH_ORB), new Item(HARMONISED_ORB), new Item(VOLATILE_ORB)),

    // mboxes
    DONATOR_MYSTERY_BOX(AttributeKey.MYSTERY_BOXES_OPENED, LogType.MYSTERY_BOX, "Mystery Box",

        new int[]
            {
                CustomItemIdentifiers.BOX_OF_VALOR
            },

        AttributeKey.DONATOR_MYSTERY_BOX_LOG_CLAIMED,

        new Item[]
            {
                new Item(CustomItemIdentifiers.BOX_OF_VALOR, 35)
            },

        new Item(BANKERS_NOTE),
        new Item(BLACK_PARTYHAT),
        new Item(CORRUPTED_TWISTED_BOW),
        new Item(CORRUPTED_TUMEKENS_SHADOW),
        new Item(CORRUPTED_VOIDWAKER),
        new Item(CORRUPTED_ARMADYL_GODSWORD),
        new Item(CORRUPTED_DRAGON_CLAWS),
        new Item(VIRTUS_MASK),
        new Item(VIRTUS_ROBE_TOP),
        new Item(VIRTUS_ROBE_BOTTOM),
        new Item(ULTOR_RING_28307),
        new Item(MAGUS_RING_28313),
        new Item(BELLATOR_RING_28316),
        new Item(VESTAS_LONGSWORD),
        new Item(VESTAS_CHAINBODY),
        new Item(VESTAS_PLATESKIRT),
        new Item(STATIUSS_WARHAMMER),
        new Item(STATIUSS_FULL_HELM),
        new Item(STATIUSS_PLATEBODY),
        new Item(STATIUSS_PLATELEGS),
        new Item(ZURIELS_STAFF),
        new Item(ZURIELS_HOOD),
        new Item(ZURIELS_ROBE_TOP),
        new Item(ZURIELS_ROBE_BOTTOM),
        new Item(MORRIGANS_COIF),
        new Item(MORRIGANS_LEATHER_BODY),
        new Item(MORRIGANS_LEATHER_CHAPS),
        new Item(PRIMORDIAL_BOOTS),
        new Item(PEGASIAN_BOOTS),
        new Item(ETERNAL_BOOTS),
        new Item(TOXIC_STAFF_OF_THE_DEAD),
        new Item(STAFF_OF_THE_DEAD),
        new Item(TRIDENT_OF_THE_SEAS),
        new Item(TRIDENT_OF_THE_SWAMP),
        new Item(TOXIC_BLOWPIPE),
        new Item(SERPENTINE_HELM),
        new Item(BANDOS_CHESTPLATE),
        new Item(BANDOS_TASSETS),
        new Item(BANDOS_BOOTS),
        new Item(ARMADYL_CHESTPLATE),
        new Item(ARMADYL_CHAINSKIRT),
        new Item(ARMADYL_GODSWORD),
        new Item(BANDOS_GODSWORD),
        new Item(SARADOMIN_GODSWORD),
        new Item(ZAMORAK_GODSWORD),
        new Item(ANCIENT_GODSWORD),
        new Item(ABYSSAL_WHIP)
    ),

    CRYSTAL_KEY(AttributeKey.CRYSTAL_KEYS_OPENED, LogType.KEYS, "Crystal key",

        new int[]
            {
                ItemIdentifiers.CRYSTAL_KEY
            },

        AttributeKey.CRYSTAL_KEY_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 10_000_000),
                new Item(ENHANCED_CRYSTAL_KEY, 20)
            },
        //Drops
        new Item(UNCUT_ONYX),
        new Item(ENHANCED_CRYSTAL_KEY),
        new Item(DRAGONSTONE_FULL_HELM),
        new Item(DRAGONSTONE_PLATEBODY),
        new Item(DRAGONSTONE_PLATELEGS),
        new Item(DRAGONSTONE_BOOTS),
        new Item(DRAGONSTONE_GAUNTLETS),
        new Item(AMULET_OF_GLORY6)),

    ENHANCED_CRYSTAL_KEY_(AttributeKey.ENHANCED_CRYSTAL_KEYS_OPENED, LogType.KEYS, "Enhanced Crystal key",

        new int[]
            {
                ItemIdentifiers.ENHANCED_CRYSTAL_KEY
            },

        AttributeKey.ENHANCED_CRYSTAL_KEY_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 10_000_000),
                new Item(MYSTERY_BOX, 1)
            },
        //Drops
        new Item(CRYSTAL_PICKAXE),
        new Item(CRYSTAL_AXE),
        new Item(CRYSTAL_HARPOON),
        new Item(CRYSTAL_BOW),
        new Item(AMULET_OF_FURY)),


    LARRANS_KEY_I(AttributeKey.LARRANS_KEYS_TIER_ONE_USED, LogType.KEYS, "Larran's key",

        new int[]
            {
                LARRANS_KEY
            },

        AttributeKey.LARRANS_KEY_TIER_I_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 50_000_000),
                new Item(MYSTERY_BOX, 5)
            },

        new Item(BOW_OF_FAERDHINEN),
        new Item(CRYSTAL_HELM),
        new Item(CRYSTAL_BODY),
        new Item(CRYSTAL_LEGS),
        new Item(ECLIPSE_MOON_HELM),
        new Item(ECLIPSE_MOON_CHESTPLATE),
        new Item(ECLIPSE_MOON_TASSETS),
        new Item(BLOOD_MOON_HELM),
        new Item(BLOOD_MOON_CHESTPLATE),
        new Item(BLOOD_MOON_TASSETS),
        new Item(BLUE_MOON_HELM),
        new Item(BLUE_MOON_CHESTPLATE),
        new Item(BLUE_MOON_TASSETS),
        new Item(ECLIPSE_ATLATL),
        new Item(BLUE_MOON_SPEAR),
        new Item(TONALZTICS_OF_RALOS),
        new Item(DAGONHAI_HAT),
        new Item(DAGONHAI_ROBE_TOP),
        new Item(DAGONHAI_ROBE_BOTTOM),
        new Item(CRYSTAL_OF_AMLODD),
        new Item(CRYSTAL_OF_CADARN),
        new Item(CRYSTAL_OF_CRWYS),
        new Item(CRYSTAL_OF_IORWERTH),
        new Item(CRYSTAL_OF_ITHELL),
        new Item(CRYSTAL_OF_TRAHAEARN)
    ),

    REVENANTS(AttributeKey.REVENANTS_KILLED, LogType.OTHER, "Revenants",

        new int[]
            {
                NpcIdentifiers.REVENANT_IMP,
                NpcIdentifiers.REVENANT_CYCLOPS,
                NpcIdentifiers.REVENANT_DARK_BEAST,
                NpcIdentifiers.REVENANT_DEMON,
                NpcIdentifiers.REVENANT_DRAGON,
                NpcIdentifiers.REVENANT_GOBLIN,
                NpcIdentifiers.REVENANT_HELLHOUND,
                NpcIdentifiers.REVENANT_HOBGOBLIN,
                NpcIdentifiers.REVENANT_KNIGHT,
                NpcIdentifiers.REVENANT_ORK,
                NpcIdentifiers.REVENANT_PYREFIEND,
                REVENANT_MALEDICTUS
            },

        AttributeKey.REVENANTS_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 50_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(VIGGORAS_CHAINMACE),
        new Item(CRAWS_BOW),
        new Item(THAMMARONS_SCEPTRE),
        new Item(AMULET_OF_AVARICE),
        new Item(BRACELET_OF_ETHEREUM_UNCHARGED),
        new Item(ANCIENT_RELIC),
        new Item(ANCIENT_EFFIGY),
        new Item(ANCIENT_MEDALLION),
        new Item(ItemIdentifiers.ANCIENT_STATUETTE),
        new Item(ANCIENT_TOTEM),
        new Item(ANCIENT_EMBLEM),
        new Item(REVENANT_CAVE_TELEPORT),
        new Item(REVENANT_ETHER)
    ),

    SLAYER(null, LogType.OTHER, "Slayer",

        new int[]{
            NpcIdentifiers.CRAWLING_HAND_448,
            NpcIdentifiers.CRAWLING_HAND_449,
            NpcIdentifiers.CRAWLING_HAND_450,
            NpcIdentifiers.CRAWLING_HAND_451,
            NpcIdentifiers.CRAWLING_HAND_452,
            NpcIdentifiers.CRAWLING_HAND_453,
            NpcIdentifiers.CRAWLING_HAND_454,
            NpcIdentifiers.CRAWLING_HAND_455,
            NpcIdentifiers.CRAWLING_HAND_456,
            NpcIdentifiers.CRAWLING_HAND_457,
            NpcIdentifiers.CRUSHING_HAND,
            NpcIdentifiers.COCKATRICE_419,
            NpcIdentifiers.COCKATRICE_420,
            NpcIdentifiers.COCKATHRICE,
            NpcIdentifiers.BASILISK_417,
            NpcIdentifiers.BASILISK_418,
            NpcIdentifiers.BASILISK_9283,
            NpcIdentifiers.BASILISK_9284,
            NpcIdentifiers.BASILISK_9285,
            NpcIdentifiers.BASILISK_9286,
            NpcIdentifiers.BASILISK_KNIGHT,
            NpcIdentifiers.BASILISK_SENTINEL,
            NpcIdentifiers.BASILISK_YOUNGLING,
            NpcIdentifiers.MONSTROUS_BASILISK,
            NpcIdentifiers.MONSTROUS_BASILISK_9287,
            NpcIdentifiers.MONSTROUS_BASILISK_9288,
            NpcIdentifiers.KURASK_410,
            NpcIdentifiers.KURASK_411,
            NpcIdentifiers.KING_KURASK,
            NpcIdentifiers.ABYSSAL_DEMON_415,
            NpcIdentifiers.ABYSSAL_DEMON_416, NpcIdentifiers.ABYSSAL_DEMON_7241, NpcIdentifiers.GREATER_ABYSSAL_DEMON, NpcIdentifiers.ABYSSAL_SIRE, NpcIdentifiers.ABYSSAL_SIRE_5887, NpcIdentifiers.ABYSSAL_SIRE_5888, NpcIdentifiers.ABYSSAL_SIRE_5889, NpcIdentifiers.ABYSSAL_SIRE_5890, NpcIdentifiers.ABYSSAL_SIRE_5891, NpcIdentifiers.ABYSSAL_SIRE_5908,
            NpcIdentifiers.GARGOYLE, NpcIdentifiers.GARGOYLE_1543, NpcIdentifiers.MARBLE_GARGOYLE_7408, NpcIdentifiers.TUROTH, NpcIdentifiers.TUROTH_427, NpcIdentifiers.TUROTH_428, NpcIdentifiers.TUROTH_429, NpcIdentifiers.TUROTH_430, NpcIdentifiers.TUROTH_431, NpcIdentifiers.TUROTH_432, NpcIdentifiers.CAVE_HORROR, NpcIdentifiers.CAVE_HORROR_1048, NpcIdentifiers.CAVE_HORROR_1049, NpcIdentifiers.CAVE_HORROR_1050, NpcIdentifiers.CAVE_HORROR_1051, NpcIdentifiers.CAVE_ABOMINATION,
            NpcIdentifiers.TALONED_WYVERN, NpcIdentifiers.SPITTING_WYVERN, NpcIdentifiers.LONGTAILED_WYVERN, NpcIdentifiers.ANCIENT_WYVERN, NpcIdentifiers.KING_BLACK_DRAGON, NpcIdentifiers.KING_BLACK_DRAGON_6502, NpcIdentifiers.BLACK_DRAGON, NpcIdentifiers.BLACK_DRAGON_253, NpcIdentifiers.BLACK_DRAGON_254, NpcIdentifiers.BLACK_DRAGON_255, NpcIdentifiers.BLACK_DRAGON_256, NpcIdentifiers.BLACK_DRAGON_257, NpcIdentifiers.BLACK_DRAGON_258, NpcIdentifiers.BLACK_DRAGON_259, NpcIdentifiers.BLACK_DRAGON_7861, NpcIdentifiers.BLACK_DRAGON_7862, NpcIdentifiers.BLACK_DRAGON_7863, NpcIdentifiers.BLACK_DRAGON_8084, NpcIdentifiers.BLACK_DRAGON_8085, NpcIdentifiers.BRUTAL_BLACK_DRAGON, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092,
            NpcIdentifiers.VORKATH_8061, NpcIdentifiers.ADAMANT_DRAGON, NpcIdentifiers.ADAMANT_DRAGON_8090, NpcIdentifiers.RUNE_DRAGON, NpcIdentifiers.RUNE_DRAGON_8031, NpcIdentifiers.RUNE_DRAGON_8091, NpcIdentifiers.LAVA_DRAGON, NpcIdentifiers.MITHRIL_DRAGON, NpcIdentifiers.MITHRIL_DRAGON_8088, NpcIdentifiers.MITHRIL_DRAGON_8089, NpcIdentifiers.SKELETAL_WYVERN_466, NpcIdentifiers.SKELETAL_WYVERN_467, NpcIdentifiers.SKELETAL_WYVERN_468, NpcIdentifiers.SPIRITUAL_MAGE, NpcIdentifiers.SPIRITUAL_MAGE_2244, NpcIdentifiers.SPIRITUAL_MAGE_3161, NpcIdentifiers.SPIRITUAL_MAGE_3168,
            NpcIdentifiers.KRAKEN, NpcIdentifiers.DARK_BEAST, NpcIdentifiers.DARK_BEAST_7250, NpcIdentifiers.NIGHT_BEAST, NpcIdentifiers.SMOKE_DEVIL, NpcIdentifiers.SMOKE_DEVIL_6639, NpcIdentifiers.SMOKE_DEVIL_6655, NpcIdentifiers.SMOKE_DEVIL_8482, NpcIdentifiers.SMOKE_DEVIL_8483, NpcIdentifiers.NUCLEAR_SMOKE_DEVIL, NpcIdentifiers.THERMONUCLEAR_SMOKE_DEVIL, NpcIdentifiers.KALPHITE_QUEEN_6500, NpcIdentifiers.KALPHITE_QUEEN_6501, NpcIdentifiers.WYRM, NpcIdentifiers.WYRM_8611, NpcIdentifiers.DRAKE_8612, NpcIdentifiers.DRAKE_8613, NpcIdentifiers.HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA_8616, NpcIdentifiers.ALCHEMICAL_HYDRA_8617, NpcIdentifiers.ALCHEMICAL_HYDRA_8618, NpcIdentifiers.ALCHEMICAL_HYDRA_8619, NpcIdentifiers.ALCHEMICAL_HYDRA_8620, NpcIdentifiers.ALCHEMICAL_HYDRA_8621, NpcIdentifiers.ALCHEMICAL_HYDRA_8622, NpcIdentifiers.ALCHEMICAL_HYDRA_8634,
            NpcIdentifiers.BASILISK_KNIGHT, NpcIdentifiers.BASILISK_SENTINEL}, AttributeKey.SLAYER_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 75_000_000),
                new Item(MYSTERY_BOX, 5)
            },

        new Item(CRAWLING_HAND_7975), new Item(COCKATRICE_HEAD), new Item(BASILISK_HEAD), new Item(KURASK_HEAD), new Item(ABYSSAL_HEAD), new Item(IMBUED_HEART), new Item(ETERNAL_GEM), new Item(DUST_BATTLESTAFF), new Item(MIST_BATTLESTAFF), new Item(ABYSSAL_WHIP), new Item(GRANITE_MAUL_24225), new Item(LEAFBLADED_SWORD), new Item(LEAFBLADED_BATTLEAXE), new Item(BLACK_MASK), new Item(GRANITE_LONGSWORD), new Item(WYVERN_VISAGE), new Item(DRACONIC_VISAGE),
        new Item(DRAGON_BOOTS), new Item(ABYSSAL_DAGGER), new Item(TRIDENT_OF_THE_SEAS), new Item(KRAKEN_TENTACLE), new Item(DARK_BOW), new Item(OCCULT_NECKLACE), new Item(DRAGON_CHAINBODY_3140), new Item(DRAGON_THROWNAXE), new Item(DRAGON_HARPOON), new Item(DRAGON_SWORD), new Item(DRAGON_KNIFE), new Item(DRAKES_TOOTH), new Item(DRAKES_CLAW), new Item(HYDRA_TAIL), new Item(HYDRAS_FANG), new Item(HYDRAS_EYE), new Item(HYDRAS_HEART), new Item(BASILISK_JAW)
    ),
    KRIL_TSUTSOROTH(AttributeKey.KRIL_TSUTSAROTHS_KILLED, LogType.BOSSES, "K'ril Tsutsaroth",

        new int[]
            {
                NpcIdentifiers.KRIL_TSUTSAROTH,
                NpcIdentifiers.KRIL_TSUTSAROTH_6495
            },

        AttributeKey.KRIL_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 25_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(PET_KRIL_TSUTSAROTH),
        new Item(ZAMORAKIAN_SPEAR),
        new Item(STAFF_OF_THE_DEAD),
        new Item(ZAMORAK_HILT)
    ),

    NEX(AttributeKey.NEX_KILLED, LogType.BOSSES, "Nex",

        new int[]
            {
                NpcIdentifiers.NEX,
                NpcIdentifiers.NEX_11279,
                NpcIdentifiers.NEX_11280,
                NpcIdentifiers.NEX_11282,
                NpcIdentifiers.NEX_11281
            },

        AttributeKey.NEX_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 50_000_000),
                new Item(MYSTERY_BOX, 5)
            },

        new Item(ItemIdentifiers.NEXLING),
        new Item(TORVA_FULL_HELM),
        new Item(TORVA_PLATEBODY),
        new Item(TORVA_PLATELEGS),
        new Item(ZARYTE_CROSSBOW),
        new Item(ZARYTE_VAMBRACES),
        new Item(ANCIENT_GODSWORD)
    ),

    DUKE_SUCELLUS(AttributeKey.DUKE_KILLED, LogType.BOSSES, "Duke Sucellus",

        new int[]
            {
                12166,
                12167,
                12191,
                12192,
                12193,
                12194,
                12195,
                12196
            },

        AttributeKey.DUKE_LOG_CLAIMED,

        new Item[]
            {
                new Item(COINS_995, 35_000_000),
                new Item(MYSTERY_BOX, 2)
            },

        new Item(BARON),
        new Item(VIRTUS_MASK),
        new Item(VIRTUS_ROBE_TOP),
        new Item(VIRTUS_ROBE_BOTTOM),
        new Item(MAGUS_RING_28313),
        new Item(CHROMIUM_INGOT),
        new Item(EYE_OF_THE_DUKE)
    );


    @Getter
    private final AttributeKey attributeKey;
    @Getter
    private final LogType logType;
    @Getter
    private final String name;
    @Getter
    private final int[] key;
    private final AttributeKey rewardClaimed;
    @Getter
    private final Item[] reward;
    @Getter
    private final Item[] obtainables;

    Collection(AttributeKey attributeKey, LogType logType, String name, int[] key, AttributeKey rewardClaimed, Item[] reward, Item... obtainables) {
        this.attributeKey = attributeKey;
        this.logType = logType;
        this.name = name;
        this.key = key;
        this.rewardClaimed = rewardClaimed;
        this.reward = reward;
        this.obtainables = obtainables;
    }

    public AttributeKey getRewardClaimedKey() {
        return rewardClaimed;
    }

    /**
     * The amount of items we can obtain.
     */
    public int totalCollectables() {
        return obtainables.length;
    }

    /**
     * Gets all the data for a specific type.
     *
     * @param logType the log type that is being sorted at alphabetical order
     */
    public static List<Collection> getAsList(LogType logType) {
        return Arrays.stream(values()).filter(type -> type.getLogType() == logType).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
    }
}

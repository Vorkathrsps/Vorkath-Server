package com.aelous.model.content.skill.impl.smithing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * An emurated type containing data about all the
 * equipment which can be created using the Smithing skill
 * in the game.
 *
 * @author Professor Oak
 */
public enum SmithableEquipment {
    BRONZE_DAGGER("Dagger", 2349, 1205, 1, 1119, 0, 1094, 1, 12.5,1, 1125),
    BRONZE_AXE("Axe", 2349, 1351, 1, 1120, 0, 1091, 1, 12.5,1, 1126),
    BRONZE_MACE("Mace", 2349, 1422, 1, 1120, 1, 1093,  2, 12.5,1, 1129),
    BRONZE_MED_HELM("Med helm", 2349, 1139, 1, 1122, 0, 1102, 3, 12.5,1, 1127),
    BRONZE_DART_TIPS("Dart tips", 2349, 819, 10, 1123, 0, 1107, 4, 12.5,1, 1128),
    BRONZE_SWORD("Sword", 2349, 1277, 1, 1119, 1, 1085, 4, 12.5,1, 1124),
    BRONZE_ARROWTIPS("Arrowtips", 2349, 39, 15, 1123, 1, 1108, 5, 12.5,1, 1130),
    BRONZE_SCIMITAR("Scimitar", 2349, 1321, 1, 1119, 2, 1087, 5, 25,2, 1116),
    BRONZE_LONG_SWORD("Long sword", 2349, 1291, 1, 1119, 3, 1086, 6, 25,2, 1089),
    BRONZE_THROWING_KNIVES("Throwing knives", 2349, 864, 5, 1123, 2, 1106, 7, 12.5,1, 1131),
    BRONZE_FULL_HELM("Full helm", 2349, 1155, 1, 1122, 1, 1103, 7, 25,2, 1113),
    BRONZE_SQUARE_SHIELD("Square shield", 2349, 1173, 1, 1122, 2, 1104, 8, 25,2, 1114),
    BRONZE_WARHAMMER("Warhammer", 2349, 1337, 1, 1120, 2, 1083, 9, 37.5,3, 1118),
    BRONZE_BATTLE_AXE("Battle axe", 2349, 1375, 1, 1120, 3, 1092, 10, 37.5,3, 1095),
    BRONZE_CHAINBODY("Chainbody", 2349, 1103, 1, 1121, 0, 1098, 11, 37.5,3, 1109),
    BRONZE_KITE_SHIELD("Kite shield", 2349, 1189, 1, 1122, 3, 1105, 12, 37.5,3, 1115),
    BRONZE_CLAWS("Claws", 2349, 3095, 1, 1120, 4, 8429, 13, 2, 25,8428),
    BRONZE_2_HAND_SWORD("2 hand sword", 2349, 1307, 1, 1119, 4, 1088, 14, 37.5, 3, 1090),
    BRONZE_PLATESKIRT("Plate skirt", 2349, 1087, 1, 1121, 2, 1100, 16, 37.5,3, 1111),
    BRONZE_PLATELEGS("Plate legs", 2349, 1075, 1, 1121, 1, 1099, 16, 37.5,3, 1110),
    BRONZE_PLATEBODY("Plate body", 2349, 1117, 1, 1121, 3, 1101, 18, 62.5,5, 1112),
    BRONZE_NAILS("Nails", 2349, 4819, 15, 1122, 4, 13358, 4, 12.5,1, 13357),
    BRONZE_UNF_BOLTS("Bolts (unf)", 2349, 9375, 10, 1121, 4, 11461, 3, 12.5,1, 11459),

    IRON_DAGGER("Dagger", 2351, 1203, 1, 1119, 0, 1094, 15, 25,1, 1125),
    IRON_AXE("Axe", 2351, 1349, 1, 1120, 0, 1091, 16, 25,1, 1126),
    IRON_MACE("Mace", 2351, 1420, 1, 1120, 1, 1093, 17, 25,1, 1129),
    IRON_MED_HELM("Med helm", 2351, 1137, 1, 1122, 0, 1102, 18, 25,1, 1127),
    IRON_DART_TIPS("Dart tips", 2351, 820, 10, 1123, 0, 1107, 19, 25,1, 1128),
    IRON_SWORD("Sword", 2351, 1279, 1, 1119, 1, 1085, 19, 25,1, 1124),
    IRON_ARROWTIPS("Arrowtips", 2351, 40, 15, 1123, 1, 1108, 20, 25,1, 1130),
    IRON_SCIMITAR("Scimitar", 2351, 1323, 1, 1119, 2, 1087, 20, 50,2, 1116),
    IRON_LONG_SWORD("Long sword", 2351, 1293, 1, 1119, 3, 1086, 21, 50,2, 1089),
    IRON_THROWING_KNIVES("Throwing knives", 2351, 863, 5, 1123, 2, 1106, 22, 25,1, 1131),
    IRON_FULL_HELM("Full helm", 2351, 1153, 1, 1122, 1, 1103, 22, 50,2, 1113),
    IRON_SQUARE_SHIELD("Square shield", 2351, 1175, 1, 1122, 2, 1104, 23, 50,2, 1114),
    IRON_WARHAMMER("Warhammer", 2351, 1335, 1, 1120, 2, 1083, 24, 75,3, 1118),
    IRON_BATTLE_AXE("Battle axe", 2351, 1363, 1, 1120, 3, 1092, 25, 75,3, 1095),
    IRON_CHAINBODY("Chainbody", 2351, 1101, 1, 1121, 0, 1098, 26, 75,3, 1109),
    IRON_KITE_SHIELD("Kite shield", 2351, 1191, 1, 1122, 3, 1105, 27, 75,3, 1115),
    IRON_CLAWS("Claws", 2351, 3096, 1, 1120, 4, 8429, 28, 2, 50,8428),
    IRON_2_HAND_SWORD("2 hand sword", 2351, 1309, 1, 1119, 4, 1088, 29, 75,3, 1090),
    IRON_PLATESKIRT("Plate skirt", 2351, 1081, 1, 1121, 2, 1100, 31, 75,3, 1111),
    IRON_PLATELEGS("Plate legs", 2351, 1067, 1, 1121, 1, 1099, 31, 75,3, 1110),
    IRON_PLATEBODY("Plate body", 2351, 1115, 1, 1121, 3, 1101, 33, 125,5, 1112),
    IRON_NAILS("Nails", 2351, 4820, 15, 1122, 4, 13358, 19, 25,1, 13357),
    IRON_UNF_BOLTS("Bolts (unf)", 2351, 9377, 10, 1121, 4, 11461, 19, 25,1, 11459),

    STEEL_DAGGER("Dagger", 2353, 1207, 1, 1119, 0, 1094, 30, 37.5,1, 1125),
    STEEL_AXE("Axe", 2353, 1353, 1, 1120, 0, 1091, 31, 37.5,1, 1126),
    STEEL_MACE("Mace", 2353, 1424, 1, 1120, 1, 1093, 32, 37.5,1, 1129),
    STEEL_MED_HELM("Med helm", 2353, 1141, 1, 1122, 0, 1102, 33, 37.5,1, 1127),
    STEEL_DART_TIPS("Dart tips", 2353, 821, 10, 1123, 0, 1107, 34, 37.5,1, 1128),
    STEEL_SWORD("Sword", 2353, 1281, 1, 1119, 1, 1085, 34, 37.5,1, 1124),
    STEEL_ARROWTIPS("Arrowtips", 2353, 41, 15, 1123, 1, 1108, 35, 37.5,1, 1130),
    STEEL_SCIMITAR("Scimitar", 2353, 1325, 1, 1119, 2, 1087, 35, 75,2, 1116),
    STEEL_LONG_SWORD("Long sword", 2353, 1295, 1, 1119, 3, 1086, 36, 75,2, 1089),
    STEEL_THROWING_KNIVES("Throwing knives", 2353, 865, 5, 1123, 2, 1106, 37, 37.5,1, 1131),
    STEEL_FULL_HELM("Full helm", 2353, 1157, 1, 1122, 1, 1103, 37, 75,2, 1113),
    STEEL_SQUARE_SHIELD("Square shield", 2353, 1177, 1, 1122, 2, 1104, 38, 75,2, 1114),
    STEEL_WARHAMMER("Warhammer", 2353, 1339, 1, 1120, 2, 1083, 39, 112.5,3, 1118),
    STEEL_BATTLE_AXE("Battle axe", 2353, 1365, 1, 1120, 3, 1092, 40, 112.5,3, 1095),
    STEEL_CHAINBODY("Chainbody", 2353, 1105, 1, 1121, 0, 1098, 41, 112.5,3, 1109),
    STEEL_KITE_SHIELD("Kite shield", 2353, 1193, 1, 1122, 3, 1105, 42, 112.5,3, 1115),
    STEEL_CLAWS("Claws", 2353, 3097, 1, 1120, 4, 8429, 43, 75,2, 8428),
    STEEL_2_HAND_SWORD("2 hand sword", 2353, 1311, 1, 1119, 4, 1088,44,112.5, 3, 1090),
    STEEL_PLATESKIRT("Plate skirt", 2353, 1083, 1, 1121, 2, 1100,46, 112.5,3, 1111),
    STEEL_PLATELEGS("Plate legs", 2353, 1069, 1, 1121, 1, 1099,46, 112.5,3, 1110),
    STEEL_PLATEBODY("Plate body", 2353, 1119, 1, 1121, 3, 1101, 48, 187.5,5, 1112),
    STEEL_NAILS("Nails", 2353, 1539, 15, 1122, 4, 13358, 34, 37.5,1, 13357),
    STEEL_UNF_BOLTS("Bolts (unf)", 2353, 9378, 10, 1121, 4, 11461, 33, 37.5,1, 11459),
    CANNONBALL("Cannon ball", 2353, 2, 4, 1123, 3, 1096, 35, 25.6,1, 1132),
    STEEL_STUDS("Studs", 2353, 2370, 1, 1123, 4, 1134, 36, 37.5,1, 1135),

    MITHRIL_DAGGER("Dagger", 2359, 1209, 1, 1119, 0, 1094, 50, 50,1, 1125),
    MITHRIL_AXE("Axe", 2359, 1355, 1, 1120, 0, 1091, 51, 50,1, 1126),
    MITHRIL_MACE("Mace", 2359, 1428, 1, 1120, 1, 1093, 52, 50,1, 1129),
    MITHRIL_MED_HELM("Med helm", 2359, 1143, 1, 1122, 0, 1102, 53, 50,1, 1127),
    MITHRIL_DART_TIPS("Dart tips", 2359, 822, 10, 1123, 0, 1107, 54, 50,1, 1128),
    MITHRIL_SWORD("Sword", 2359, 1285, 1, 1119, 1, 1085, 54, 50,1, 1124),
    MITHRIL_ARROWTIPS("Arrowtips", 2359, 42, 15, 1123, 1, 1108, 55,50, 1, 1130),
    MITHRIL_SCIMITAR("Scimitar", 2359, 1329, 1, 1119, 2, 1087, 55, 100,2, 1116),
    MITHRIL_LONG_SWORD("Long sword", 2359, 1299, 1, 1119, 3, 1086, 56, 100,2, 1089),
    MITHRIL_THROWING_KNIVES("Throwing knives", 2359, 866, 5, 1123, 2, 1106, 50,57, 1, 1131),
    MITHRIL_FULL_HELM("Full helm", 2359, 1159, 1, 1122, 1, 1103, 57, 100,2, 1113),
    MITHRIL_SQUARE_SHIELD("Square shield", 2359, 1181, 1, 1122, 2, 1104, 58, 100,2, 1114),
    MITHRIL_WARHAMMER("Warhammer", 2359, 1343, 1, 1120, 2, 1083, 59, 150,3, 1118),
    MITHRIL_BATTLE_AXE("Battle axe", 2359, 1369, 1, 1120, 3, 1092, 60, 150,3, 1095),
    MITHRIL_CHAINBODY("Chainbody", 2359, 1109, 1, 1121, 0, 1098, 61, 150,3, 1109),
    MITHRIL_KITE_SHIELD("Kite shield", 2359, 1197, 1, 1122, 3, 1105, 62, 150,3, 1115),
    MITHRIL_CLAWS("Claws", 2359, 3099, 1, 1120, 4, 8429, 63, 100,2, 8428),
    MITHRIL_2_HAND_SWORD("2 hand sword", 2359, 1315, 1, 1119, 4, 1088, 64, 150,3, 1090),
    MITHRIL_PLATESKIRT("Plate skirt", 2359, 1085, 1, 1121, 2, 1100, 66,150, 3, 1111),
    MITHRIL_PLATELEGS("Plate legs", 2359, 1071, 1, 1121, 1, 1099, 66, 150,3, 1110),
    MITHRIL_PLATEBODY("Plate body", 2359, 1121, 1, 1121, 3, 1101, 68, 250,5, 1112),
    MITHRIL_NAILS("Nails", 2359, 4822, 15, 1122, 4, 13358, 54, 50,1, 13357),
    MITHRIL_UNF_BOLTS("Bolts (unf)", 2359, 9379, 10, 1121, 4, 11461, 53, 50,1, 11459),

    ADAMANT_DAGGER("Dagger", 2361, 1211, 1, 1119, 0, 1094, 70, 62.5,1, 1125),
    ADAMANT_AXE("Axe", 2361, 1357, 1, 1120, 0, 1091, 71, 62.5,1, 1126),
    ADAMANT_MACE("Mace", 2361, 1430, 1, 1120, 1, 1093, 72, 62.5,1, 1129),
    ADAMANT_MED_HELM("Med helm", 2361, 1145, 1, 1122, 0, 1102, 73, 62.5,1, 1127),
    ADAMANT_DART_TIPS("Dart tips", 2361, 823, 10, 1123, 0, 1107, 74, 62.5,1, 1128),
    ADAMANT_SWORD("Sword", 2361, 1287, 1, 1119, 1, 1085, 74, 62.5,1, 1124),
    ADAMANT_ARROWTIPS("Arrowtips", 2361, 43, 15, 1123, 1, 1108, 75, 62.5,1, 1130),
    ADAMANT_SCIMITAR("Scimitar", 2361, 1331, 1, 1119, 2, 1087, 75, 125,2, 1116),
    ADAMANT_LONG_SWORD("Long sword", 2361, 1301, 1, 1119, 3, 1086, 76, 125,2, 1089),
    ADAMANT_THROWING_KNIVES("Throwing knives", 2361, 867, 5, 1123, 2, 1106, 77, 62.5,1, 1131),
    ADAMANT_FULL_HELM("Full helm", 2361, 1161, 1, 1122, 1, 1103, 77, 125,2, 1113),
    ADAMANT_SQUARE_SHIELD("Square shield", 2361, 1183, 1, 1122, 2, 1104, 78, 125,2, 1114),
    ADAMANT_WARHAMMER("Warhammer", 2361, 1345, 1, 1120, 2, 1083, 79, 187.5,3, 1118),
    ADAMANT_BATTLE_AXE("Battle axe", 2361, 1371, 1, 1120, 3, 1092, 80, 187.5,3, 1095),
    ADAMANT_CHAINBODY("Chainbody", 2361, 1111, 1, 1121, 0, 1098, 81, 187.5,3, 1109),
    ADAMANT_KITE_SHIELD("Kite shield", 2361, 1199, 1, 1122, 3, 1105, 82, 187.5,3, 1115),
    ADAMANT_CLAWS("Claws", 2361, 3100, 1, 1120, 4, 8429, 83, 125,2, 8428),
    ADAMANT_2_HAND_SWORD("2 hand sword", 2361, 1317, 1, 1119, 4, 1088, 84, 187.5,3, 1090),
    ADAMANT_PLATESKIRT("Plate skirt", 2361, 1091, 1, 1121, 2, 1100, 86, 187.5,3, 1111),
    ADAMANT_PLATELEGS("Plate legs", 2361, 1073, 1, 1121, 1, 1099, 86, 187.5,3, 1110),
    ADAMANT_PLATEBODY("Plate body", 2361, 1123, 1, 1121, 3, 1101, 88, 312.5,5, 1112),
    ADAMANT_NAILS("Nails", 2361, 4823, 15, 1122, 4, 13358, 74, 62.5,1, 13357),
    ADAMANT_UNF_BOLTS("Bolts (unf)", 2361, 9380, 10, 1121, 4, 11461, 73, 62.5,1, 11459),

    RUNE_DAGGER("Dagger", 2363, 1213, 1, 1119, 0, 1094, 85, 75,1, 1125),
    RUNE_AXE("Axe", 2363, 1359, 1, 1120, 0, 1091, 86, 75,1, 1126),
    RUNE_MACE("Mace", 2363, 1432, 1, 1120, 1, 1093, 87, 75,1, 1129),
    RUNE_MED_HELM("Med helm", 2363, 1147, 1, 1122, 0, 1102, 88, 75,1, 1127),
    RUNE_DART_TIPS("Dart tips", 2363, 824, 10, 1123, 0, 1107, 89, 75,1, 1128),
    RUNE_SWORD("Sword", 2363, 1289, 1, 1119, 1, 1085, 89, 75,1, 1124),
    RUNE_ARROWTIPS("Arrowtips", 2363, 44, 15, 1123, 1, 1108, 90, 75,1, 1130),
    RUNE_SCIMITAR("Scimitar", 2363, 1333, 1, 1119, 2, 1087, 90, 150,2, 1116),
    RUNE_LONG_SWORD("Long sword", 2363, 1303, 1, 1119, 3, 1086, 91, 150,2, 1089),
    RUNE_THROWING_KNIVES("Throwing knives", 2363, 868, 5, 1123, 2, 1106, 75,92, 1, 1131),
    RUNE_FULL_HELM("Full helm", 2363, 1163, 1, 1122, 1, 1103, 92, 150,2, 1113),
    RUNE_SQUARE_SHIELD("Square shield", 2363, 1185, 1, 1122, 2, 1104, 93, 150,2, 1114),
    RUNE_WARHAMMER("Warhammer", 2363, 1347, 1, 1120, 2, 1083, 94, 225,3, 1118),
    RUNE_BATTLE_AXE("Battle axe", 2363, 1373, 1, 1120, 3, 1092, 95, 225,3, 1095),
    RUNE_CHAINBODY("Chainbody", 2363, 1113, 1, 1121, 0, 1098, 96, 225,3, 1109),
    RUNE_KITE_SHIELD("Kite shield", 2363, 1201, 1, 1122, 3, 1105, 97, 225,3, 1115),
    RUNE_CLAWS("Claws", 2363, 3101, 1, 1120, 4, 8429, 98, 150,2, 8428),
    RUNE_2_HAND_SWORD("2 hand sword", 2363, 1319, 1, 1119, 4, 1088, 99, 225,3, 1090),
    RUNE_PLATESKIRT("Plate skirt", 2363, 1093, 1, 1121, 2, 1100, 99, 225,3, 1111),
    RUNE_PLATELEGS("Plate legs", 2363, 1079, 1, 1121, 1, 1099, 99, 225,3, 1110),
    RUNE_PLATEBODY("Plate body", 2363, 1127, 1, 1121, 3, 1101, 99, 375,5, 1112),
    RUNE_NAILS("Nails", 2363, 4824, 15, 1122, 4, 13358, 89, 75,1, 13357),
    RUNE_UNF_BOLTS("Bolts (unf)", 2363, 9381, 10, 1121, 4, 11461, 88, 75,1, 11459),
    ;

    private final String name;
    private final int barId;
    private final int itemId;
    private final int amount;
    private final int itemFrame;
    private final int itemSlot;
    private final int nameFrame;
    private final int requiredLevel;
    private final double experience;
    private final int barsRequired;
    private final int barFrame;

    SmithableEquipment(String name, int barId, int itemId, int amount, int itemFrame, int itemSlot, int nameFrame, int requiredLevel, double experience, int barsRequired, int barFrame) {
        this.name = name;
        this.barId = barId;
        this.itemId = itemId;
        this.amount = amount;
        this.itemFrame = itemFrame;
        this.itemSlot = itemSlot;
        this.nameFrame = nameFrame;
        this.requiredLevel = requiredLevel;
        this.experience = experience;
        this.barsRequired = barsRequired;
        this.barFrame = barFrame;
    }

    public static final ImmutableSet<SmithableEquipment> RUNE_ITEMS = Sets.immutableEnumSet(RUNE_DAGGER, RUNE_AXE, RUNE_MACE, RUNE_MED_HELM, RUNE_DART_TIPS, RUNE_SWORD, RUNE_ARROWTIPS, RUNE_SCIMITAR, RUNE_LONG_SWORD,
        RUNE_THROWING_KNIVES, RUNE_FULL_HELM, RUNE_SQUARE_SHIELD, RUNE_WARHAMMER, RUNE_BATTLE_AXE, RUNE_CHAINBODY, RUNE_KITE_SHIELD,
        RUNE_CLAWS, RUNE_2_HAND_SWORD, RUNE_PLATESKIRT, RUNE_PLATELEGS, RUNE_PLATEBODY, RUNE_NAILS, RUNE_UNF_BOLTS);

    public static final ImmutableSet<SmithableEquipment> ADAMANT_ITEMS = Sets.immutableEnumSet(ADAMANT_DAGGER, ADAMANT_AXE, ADAMANT_MACE, ADAMANT_MED_HELM, ADAMANT_DART_TIPS, ADAMANT_SWORD, ADAMANT_ARROWTIPS, ADAMANT_SCIMITAR, ADAMANT_LONG_SWORD,
        ADAMANT_THROWING_KNIVES, ADAMANT_FULL_HELM, ADAMANT_SQUARE_SHIELD, ADAMANT_WARHAMMER, ADAMANT_BATTLE_AXE, ADAMANT_CHAINBODY, ADAMANT_KITE_SHIELD,
        ADAMANT_CLAWS, ADAMANT_2_HAND_SWORD, ADAMANT_PLATESKIRT, ADAMANT_PLATELEGS, ADAMANT_PLATEBODY, ADAMANT_NAILS, ADAMANT_UNF_BOLTS);

    public static final ImmutableSet<SmithableEquipment> MITHRIL_ITEMS = Sets.immutableEnumSet(MITHRIL_DAGGER, MITHRIL_AXE, MITHRIL_MACE, MITHRIL_MED_HELM, MITHRIL_DART_TIPS, MITHRIL_SWORD, MITHRIL_ARROWTIPS, MITHRIL_SCIMITAR, MITHRIL_LONG_SWORD,
        MITHRIL_THROWING_KNIVES, MITHRIL_FULL_HELM, MITHRIL_SQUARE_SHIELD, MITHRIL_WARHAMMER, MITHRIL_BATTLE_AXE, MITHRIL_CHAINBODY, MITHRIL_KITE_SHIELD,
        MITHRIL_CLAWS, MITHRIL_2_HAND_SWORD, MITHRIL_PLATESKIRT, MITHRIL_PLATELEGS, MITHRIL_PLATEBODY, MITHRIL_NAILS, MITHRIL_UNF_BOLTS);

    public static final ImmutableSet<SmithableEquipment> STEEL_ITEMS = Sets.immutableEnumSet(STEEL_DAGGER, STEEL_AXE, STEEL_MACE, STEEL_MED_HELM, STEEL_DART_TIPS, STEEL_SWORD, STEEL_ARROWTIPS, STEEL_SCIMITAR, STEEL_LONG_SWORD,
        STEEL_THROWING_KNIVES, STEEL_FULL_HELM, STEEL_SQUARE_SHIELD, STEEL_WARHAMMER, STEEL_BATTLE_AXE, STEEL_CHAINBODY, STEEL_KITE_SHIELD,
        STEEL_CLAWS, STEEL_2_HAND_SWORD, STEEL_PLATESKIRT, STEEL_PLATELEGS, STEEL_PLATEBODY, STEEL_NAILS, STEEL_UNF_BOLTS, STEEL_STUDS, CANNONBALL);

    public static final ImmutableSet<SmithableEquipment> IRON_ITEMS = Sets.immutableEnumSet(IRON_DAGGER, IRON_AXE, IRON_MACE, IRON_MED_HELM, IRON_DART_TIPS, IRON_SWORD, IRON_ARROWTIPS, IRON_SCIMITAR, IRON_LONG_SWORD,
        IRON_THROWING_KNIVES, IRON_FULL_HELM, IRON_SQUARE_SHIELD, IRON_WARHAMMER, IRON_BATTLE_AXE, IRON_CHAINBODY, IRON_KITE_SHIELD,
        IRON_CLAWS, IRON_2_HAND_SWORD, IRON_PLATESKIRT, IRON_PLATELEGS, IRON_PLATEBODY, IRON_NAILS, IRON_UNF_BOLTS);

    public static final ImmutableSet<SmithableEquipment> BRONZE_ITEMS = Sets.immutableEnumSet(BRONZE_DAGGER, BRONZE_AXE, BRONZE_MACE, BRONZE_MED_HELM, BRONZE_DART_TIPS, BRONZE_SWORD, BRONZE_ARROWTIPS, BRONZE_SCIMITAR, BRONZE_LONG_SWORD,
        BRONZE_THROWING_KNIVES, BRONZE_FULL_HELM, BRONZE_SQUARE_SHIELD, BRONZE_WARHAMMER, BRONZE_BATTLE_AXE, BRONZE_CHAINBODY, BRONZE_KITE_SHIELD,
        BRONZE_CLAWS, BRONZE_2_HAND_SWORD, BRONZE_PLATESKIRT, BRONZE_PLATELEGS, BRONZE_PLATEBODY, BRONZE_NAILS, BRONZE_UNF_BOLTS);

    public int getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public int getItemFrame() {
        return itemFrame;
    }

    public int getItemSlot() {
        return itemSlot;
    }

    public int getNameFrame() {
        return nameFrame;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public double getExperience() {
        return experience;
    }

    public int getBarsRequired() {
        return barsRequired;
    }

    public int getBarFrame() {
        return barFrame;
    }

    public int getBarId() {
        return barId;
    }

    public String getName() {
        return name;
    }
}

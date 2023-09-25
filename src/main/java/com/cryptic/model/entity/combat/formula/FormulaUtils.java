package com.cryptic.model.entity.combat.formula;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.ItemIdentifiers;

import java.util.Arrays;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * This is a utility class for the combat max hits.
 *
 * @Author Origin
 * @Since November 06, 2021
 */
public class FormulaUtils {

    /**
     * Checks if the NPC is a demon
     *
     * @param target The npc
     * @return true if the npc is in fact a demon, false otherwise.
     */
    public static boolean isDemon(Entity target) {
        if (target.isNpc()) {
            NPC npc = target.getAsNpc();
            NpcDefinition def = npc.def();
            String name = "";
            if (def != null) {
                name = def.name;
            }
            return name.equalsIgnoreCase("Imp") || name.equalsIgnoreCase("Imp Champion") || name.equalsIgnoreCase("Lesser demon") || name.equalsIgnoreCase("Lesser Demon Champion") || name.equalsIgnoreCase("Greater demon") || name.equalsIgnoreCase("Black demon") || name.equalsIgnoreCase("Abyssal demon") || name.equalsIgnoreCase("Greater abyssal demon") || name.equalsIgnoreCase("Ice demon") || name.equalsIgnoreCase("Bloodveld") || name.equalsIgnoreCase("Insatiable Bloodveld") || name.equalsIgnoreCase("Mutated Bloodveld") || name.equalsIgnoreCase("Insatiable Mutated Bloodveld") || name.equalsIgnoreCase("Demonic gorilla") || name.equalsIgnoreCase("hellhound") || name.equalsIgnoreCase("Skeleton Hellhound") || name.equalsIgnoreCase("Greater Skeleton Hellhound") || name.equalsIgnoreCase("Nechryael") || name.equalsIgnoreCase("Death spawn") || name.equalsIgnoreCase("Greater Nechryael") || name.equalsIgnoreCase("Nechryarch") || name.equalsIgnoreCase("Chaotic death spawn");
        }
        return false;
    }

    static int[] undeadNpcs = new int[]{NpcIdentifiers.ABERRANT_SPECTRE, NpcIdentifiers.ABHORRENT_SPECTRE, NpcIdentifiers.ANKOU, NpcIdentifiers.ANKOU_2515, NpcIdentifiers.ANKOU_2516, NpcIdentifiers.ANKOU_2517, NpcIdentifiers.ANKOU_2518, NpcIdentifiers.ANKOU_2519, NpcIdentifiers.ANKOU_2516, NpcIdentifiers.ASYN_SHADE, NpcIdentifiers.ASYN_SHADOW, NpcIdentifiers.ASYN_SHADOW_5632, NpcIdentifiers.BANSHEE, NpcIdentifiers.VETION, NpcIdentifiers.VETION_REBORN, NpcIdentifiers.CRAWLING_HAND, NpcIdentifiers.CRAWLING_HAND_449, NpcIdentifiers.CRAWLING_HAND_448, NpcIdentifiers.CRAWLING_HAND_451, NpcIdentifiers.CRAWLING_HAND_453, NpcIdentifiers.CRAWLING_HAND_452, NpcIdentifiers.CRUSHING_HAND, NpcIdentifiers.DARK_ANKOU, NpcIdentifiers.DEVIANT_SPECTRE, NpcIdentifiers.FIYR_SHADE, NpcIdentifiers.FIYR_SHADOW, NpcIdentifiers.FORGOTTEN_SOUL, NpcIdentifiers.FORGOTTEN_SOUL_10524, NpcIdentifiers.FORGOTTEN_SOUL_10526, NpcIdentifiers.FORGOTTEN_SOUL_10525, NpcIdentifiers.GHOST, NpcIdentifiers.GHOST_86, NpcIdentifiers.GHOST_87, NpcIdentifiers.GHOST_88, NpcIdentifiers.GHOST_89, NpcIdentifiers.GHOST_90, NpcIdentifiers.GHOST_91, NpcIdentifiers.GHOST_92, NpcIdentifiers.GHOST_93, NpcIdentifiers.GIANT_SKELETON, NpcIdentifiers.GIANT_SKELETON_681, NpcIdentifiers.GIANT_SKELETON_6440, NpcIdentifiers.HEADLESS_BEAST, NpcIdentifiers.HEADLESS_BEAST_10506, NpcIdentifiers.HEADLESS_BEAST_HARD, NpcIdentifiers.LOAR_SHADE, NpcIdentifiers.LOAR_SHADOW, NpcIdentifiers.MONKEY_ZOMBIE, NpcIdentifiers.MONKEY_ZOMBIE_5283, NpcIdentifiers.MONKEY_ZOMBIE_5282, NpcIdentifiers.MUMMY, NpcIdentifiers.MUMMY_721, NpcIdentifiers.MUMMY_722, NpcIdentifiers.MUMMY_723, NpcIdentifiers.MUMMY_724, NpcIdentifiers.MUMMY_725, NpcIdentifiers.PESTILENT_BLOAT, NpcIdentifiers.PESTILENT_BLOAT_10812, NpcIdentifiers.PESTILENT_BLOAT_11184, NpcIdentifiers.PESTILENT_BLOAT_10813, NpcIdentifiers.PHRIN_SHADE, NpcIdentifiers.PHRIN_SHADOW, NpcIdentifiers.REPUGNANT_SPECTRE, NpcIdentifiers.REVENANT_CYCLOPS, NpcIdentifiers.REVENANT_DARK_BEAST, NpcIdentifiers.REVENANT_DEMON, NpcIdentifiers.REVENANT_DRAGON, NpcIdentifiers.REVENANT_GOBLIN, NpcIdentifiers.REVENANT_HELLHOUND, NpcIdentifiers.REVENANT_HOBGOBLIN, NpcIdentifiers.REVENANT_IMP, NpcIdentifiers.REVENANT_KNIGHT, NpcIdentifiers.REVENANT_MALEDICTUS, NpcIdentifiers.REVENANT_ORK, NpcIdentifiers.REVENANT_PYREFIEND, NpcIdentifiers.SKELETAL_MYSTIC, NpcIdentifiers.SKELETAL_MYSTIC_7605, NpcIdentifiers.SKELETAL_MYSTIC_7605, NpcIdentifiers.SKELETON_HELLHOUND, NpcIdentifiers.SKELETON_HELLHOUND_6387, NpcIdentifiers.SKELETON_HELLHOUND_6613, NpcIdentifiers.SKELETON_MAGE, NpcIdentifiers.SKELETON_MAGE_4312, NpcIdentifiers.SKELETON_MAGE_4318, NpcIdentifiers.SLASH_BASH, NpcIdentifiers.TARN, NpcIdentifiers.MUTANT_TARN, NpcIdentifiers.TARN_6476, NpcIdentifiers.UNDEAD_DRUID, NpcIdentifiers.ULFRIC, NpcIdentifiers.VORKATH, NpcIdentifiers.VORKATH_8059, NpcIdentifiers.VORKATH_8060, NpcIdentifiers.VORKATH_8061, NpcIdentifiers.VORKATH_8058, NpcIdentifiers.ZOMBIFIED_SPAWN, NpcIdentifiers.ZOMBIFIED_SPAWN_8063};
    public static boolean isUndead(Entity target) {
        if (target.isNpc()) {
            NPC npc = target.getAsNpc();
            int npcId = npc.id();

            for (int undead : undeadNpcs) {
                if (npcId == undead) {
                    return true;
                }
            }
        }
        return false;
    }


    public static int[] isRevenant() {
        return new int[]{NpcIdentifiers.REVENANT_HELLHOUND, NpcIdentifiers.REVENANT_IMP,
            NpcIdentifiers.REVENANT_KNIGHT, NpcIdentifiers.REVENANT_HOBGOBLIN,
            NpcIdentifiers.REVENANT_GOBLIN, NpcIdentifiers.REVENANT_IMP,
            NpcIdentifiers.REVENANT_DRAGON, NpcIdentifiers.REVENANT_DARK_BEAST,
            NpcIdentifiers.REVENANT_DEMON, NpcIdentifiers.REVENANT_ORK,
            NpcIdentifiers.REVENANT_PYREFIEND, NpcIdentifiers.REVENANT_CYCLOPS,
            NpcIdentifiers.REVENANT_MALEDICTUS};
    }

    public static boolean wearingFullVirtus(Player player) {
        return player.getEquipment().containsAll(ItemIdentifiers.VIRTUS_MASK, ItemIdentifiers.VIRTUS_ROBE_TOP, ItemIdentifiers.VIRTUS_ROBE_BOTTOMS);
    }

    /**
     * Checks if the NPC is a dragon.
     *
     * @param target The mob
     * @return returns true if the npc is a dragon, false otherwise.
     */
    public static boolean isDragon(Entity target) {
        if (target.isNpc()) {
            NPC npc = target.getAsNpc();
            NpcDefinition def = npc.def();
            String name = "";
            if (def != null) {
                name = def.name;
            }
            boolean exceptions = name.contains("Elvarg") || name.contains("Revenant dragon");
            return name.contains("The Great Olm") || name.contains("El Fuego") || name.contains("Hungarian horntail") || name.contains("Wyvern") || name.contains("Basilisk (Right claw)") || name.contains("Basilisk (Left claw)") || name.contains("Basilisk") || name.contains("Great Olm") || name.contains("Wyrm") || name.contains("Drake") || name.contains("Hydra") || name.contains("Vorkath") || name.contains("Galvek") || name.contains("dragon") || name.contains("Dragon") && !exceptions;
        }
        return false;
    }

    public static boolean obbyArmour(Player player) {
        ItemContainer eq = player.getEquipment();
        return ((eq.hasAt(EquipSlot.HEAD, 21298) && eq.hasAt(EquipSlot.BODY, 21301) && eq.hasAt(EquipSlot.LEGS, 21304)));
    }

    public static boolean hasMeleeWildernessWeapon(Player player) {
        return player.getEquipment().containsAny(VIGGORAS_CHAINMACE, URSINE_CHAINMACE);
    }

    public static boolean hasRangedWildernessWeapon(Player player) {
        return player.getEquipment().containsAny(CRAWS_BOW, WEBWEAVER_BOW);
    }

    public static boolean wearingDarkBowWithDragonArrows(Player player) {
        return player.getEquipment().containsAll(DARK_BOW, DRAGON_ARROW) || player.getEquipment().containsAll(DARK_BOW_BH, DRAGON_ARROW);
    }

    public static boolean hasMagicWildernessWeapon(Player player) {
        return player.getEquipment().containsAny(THAMMARONS_SCEPTRE, ACCURSED_SCEPTRE_A);
    }

    public static boolean wearingFullAhrims(Player player) {
        return player.getEquipment().containsAll(AHRIMS_HOOD, AHRIMS_ROBETOP, AHRIMS_ROBESKIRT, AHRIMS_STAFF);
    }

    public static boolean wearingFullDharok(Player player) {
        return player.getEquipment().containsAll(DHAROKS_HELM, DHAROKS_GREATAXE, DHAROKS_PLATEBODY, DHAROKS_PLATELEGS);
    }

    public static boolean wearingFullGuthan(Player player) {
        return player.getEquipment().containsAll(GUTHANS_HELM, GUTHANS_WARSPEAR, GUTHANS_CHAINSKIRT, GUTHANS_PLATEBODY);
    }

    public static boolean wearingFullVerac(Player player) {
        return player.getEquipment().containsAll(VERACS_HELM, VERACS_FLAIL, VERACS_BRASSARD, VERACS_PLATESKIRT);
    }

    public static boolean wearingFullTorag(Player player) {
        return player.getEquipment().containsAll(TORAGS_HELM, TORAGS_HAMMERS, TORAGS_PLATEBODY, TORAGS_PLATELEGS);
    }

    public static boolean wearingFullKarils(Player player) {
        return player.getEquipment().containsAll(KARILS_COIF, KARILS_CROSSBOW, KARILS_LEATHERSKIRT, KARILS_LEATHERTOP);
    }


    public static boolean wearingAmuletOfDamned(Player player) {
        return player.getEquipment().contains(AMULET_OF_THE_DAMNED_FULL);
    }

    public static boolean wearingFullInquisitors(Player player) {
        return player.getEquipment().containsAll(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT);
    }

    public static boolean wearingInquisitorsPiece(Player player) {
        return player.getEquipment().containsAny(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT);
    }

    public static boolean wearingSpearsOrHalberds(Player player) {
        return player.getEquipment().containsAny(ItemIdentifiers.VESTAS_SPEAR, ItemIdentifiers.LEAFBLADED_SPEAR,
            ItemIdentifiers.GUTHANS_WARSPEAR, ItemIdentifiers.ZAMORAKIAN_SPEAR,
            ItemIdentifiers.ZAMORAKIAN_HASTA, ItemIdentifiers.OSMUMTENS_FANG,
            ItemIdentifiers.OSMUMTENS_FANG_OR, ItemIdentifiers.CRYSTAL_HALBERD,
            ItemIdentifiers.CRYSTAL_HALBERD_FULL, ItemIdentifiers.NEW_CRYSTAL_HALBERD_FULL, ItemIdentifiers.DRAGON_HALBERD);
    }

    public static boolean isWearingPoisonEquipmentOrWeapon(Player player) { //add any extra poison weapons here
        return player.getEquipment().containsAny(
            SERPENTINE_HELM, MAGMA_HELM, TANZANITE_HELM, TOXIC_BLOWPIPE, DRAGON_DAGGERP, DRAGON_DAGGERP_5680, DRAGON_DAGGER_20407);
    }

    public static boolean isWearingDamageReductionStaff(Player player) {
        return player.getEquipment().containsAny(STAFF_OF_THE_DEAD, TOXIC_STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED, STAFF_OF_LIGHT);
    }

    public static boolean fullDharok(Player player) {
        return player.getEquipment().containsAll(DHAROKS_HELM, DHAROKS_GREATAXE, DHAROKS_PLATELEGS, DHAROKS_PLATEBODY);
    }

    public static boolean hasThammaronSceptre(Player player) {
        ItemContainer eq = player.getEquipment();
        return (eq.hasAt(EquipSlot.WEAPON, 22555) && (WildernessArea.isInWilderness(player)));
    }

    public static boolean hasSlayerHelmet(Player player) {
        return player.getEquipment().containsAny(SLAYER_HELMET, SLAYER_HELMET_I, SLAYER_HELMET_I_25177, SLAYER_HELMET_I_26674, BLACK_SLAYER_HELMET, GREEN_SLAYER_HELMET, BLACK_SLAYER_HELMET_I, PURPLE_SLAYER_HELMET, RED_SLAYER_HELMET, RED_SLAYER_HELMET_I, PURPLE_SLAYER_HELMET_I, TURQUOISE_SLAYER_HELMET, HYDRA_SLAYER_HELMET, TURQUOISE_SLAYER_HELMET_I, HYDRA_SLAYER_HELMET_I, TWISTED_SLAYER_HELMET, TWISTED_SLAYER_HELMET_I, TZKAL_SLAYER_HELMET, TZKAL_SLAYER_HELMET_I, TZTOK_SLAYER_HELMET, TZTOK_SLAYER_HELMET_I, VAMPYRIC_SLAYER_HELMET_I, VAMPYRIC_SLAYER_HELMET);
    }

    public static boolean hasSalveAmulet(Player player) {
        return player.getEquipment().contains(SALVE_AMULET);
    }

    public static boolean hasSalveAmuletI(Player player) {
        return player.getEquipment().containsAny(SALVE_AMULETI, SALVE_AMULETI_25250, SALVE_AMULETI_26763);
    }

    public static boolean hasSalveAmuletE(Player player) {
        return player.getEquipment().contains(SALVE_AMULET_E);
    }

    public static boolean hasSalveAmuletEI(Player player) {
        return player.getEquipment().containsAny(SALVE_AMULETEI, SALVE_AMULETEI_25278, SALVE_AMULETEI_26782);
    }

    public static boolean hasCrawsBow(Player player) {
        return ((player.getEquipment().hasAt(EquipSlot.WEAPON, CRAWS_BOW) && WildernessArea.isInWilderness(player)));
    }

    public static boolean hasAmuletOfAvarice(Player player) {
        ItemContainer eq = player.getEquipment();
        return (eq.hasAt(EquipSlot.WEAPON, 22557) && WildernessArea.isInWilderness(player));
    }

    public static boolean berserkerNecklace(Player player) {
        return player.getEquipment().hasAt(EquipSlot.AMULET, BERSERKER_NECKLACE) || player.getEquipment().hasAt(EquipSlot.AMULET, BERSERKER_NECKLACE_OR);
    }

    public static boolean hasArchLight(Player player) {
        return player.getEquipment().contains(ARCLIGHT);
    }

    public static boolean hasAncientSceptre(Player player) {
        return player.getEquipment().contains(ANCIENT_SCEPTRE);
    }

    public static boolean hasOsmumtensFang(Player player) {
        return player.getEquipment().containsAny(OSMUMTENS_FANG, OSMUMTENS_FANG_OR);
    }

    public static boolean hasBowOfFaerdhenin(Player player) {
        return player.getEquipment().containsAny(BOW_OF_FAERDHINEN, BOW_OF_FAERDHINEN_27187, BOW_OF_FAERDHINEN_C, BOW_OF_FAERDHINEN_C_25869, BOW_OF_FAERDHINEN_C_25884, BOW_OF_FAERDHINEN_C_25886, BOW_OF_FAERDHINEN_C_25888, BOW_OF_FAERDHINEN_C_25890, BOW_OF_FAERDHINEN_C_25892, BOW_OF_FAERDHINEN_C_25892, BOW_OF_FAERDHINEN_C_25896, BOW_OF_FAERDHINEN_C_25896);
    }

    public static boolean hasZurielStaff(Player player) {
        return player.getEquipment().containsAny(ZURIELS_STAFF, ZURIELS_STAFF_23617);
    }

    public static boolean hasDragonHunterLance(Player player) {
        return player.getEquipment().contains(ARCLIGHT);
    }

    public static boolean hasObbyWeapon(Player player) {
        ItemContainer eq = player.getEquipment();
        int[] weaponry = new int[]{6528, 6523, 6525};
        return ((eq.hasAt(EquipSlot.WEAPON, weaponry[0]) || (eq.hasAt(EquipSlot.WEAPON, weaponry[1]) || (eq.hasAt(EquipSlot.WEAPON, weaponry[2])))));
    }

    public static boolean regularVoidEquipmentBaseMagic(Player player) {
        return player.getEquipment().containsAll(VOID_KNIGHT_GLOVES, VOID_KNIGHT_ROBE, VOID_KNIGHT_TOP, VOID_MAGE_HELM);
    }

    public static boolean regularVoidEquipmentBaseMelee(Player player) {
        return player.getEquipment().containsAll(VOID_KNIGHT_GLOVES, VOID_KNIGHT_ROBE, VOID_KNIGHT_TOP, VOID_MELEE_HELM);
    }

    public static boolean regularVoidEquipmentBaseRanged(Player player) {
        return player.getEquipment().containsAll(VOID_KNIGHT_GLOVES, VOID_KNIGHT_ROBE, VOID_KNIGHT_TOP, VOID_RANGER_HELM);
    }

    public static boolean eliteVoidEquipmentBaseMagic(Player player) {
        return player.getEquipment().containsAll(ELITE_VOID_TOP, ELITE_VOID_ROBE, VOID_KNIGHT_GLOVES, VOID_MAGE_HELM);
    }

    public static boolean eliteVoidEquipmentMelee(Player player) {
        return player.getEquipment().containsAll(ELITE_VOID_TOP, ELITE_VOID_ROBE, VOID_KNIGHT_GLOVES, VOID_MELEE_HELM);
    }

    public static boolean eliteVoidEquipmentRanged(Player player) {
        return player.getEquipment().containsAll(ELITE_VOID_TOP, ELITE_VOID_ROBE, VOID_KNIGHT_GLOVES, VOID_RANGER_HELM);
    }

    public static boolean eliteTrimmedVoidEquipmentBaseMagic(Player player) {
        return player.getEquipment().containsAll(ELITE_VOID_TOP_LOR, ELITE_VOID_ROBE_LOR, VOID_KNIGHT_GLOVES_LOR, VOID_MAGE_HELM_LOR);
    }

    public static boolean eliteTrimmedVoidEquipmentBaseMelee(Player player) {
        return player.getEquipment().containsAll(ELITE_VOID_TOP_LOR, ELITE_VOID_ROBE_LOR, VOID_KNIGHT_GLOVES_LOR, VOID_MELEE_HELM_LOR);
    }

    public static boolean eliteTrimmedVoidEquipmentBaseRanged(Player player) {
        return player.getEquipment().containsAll(ELITE_VOID_TOP_LOR, ELITE_VOID_ROBE_LOR, VOID_KNIGHT_GLOVES_LOR, VOID_RANGER_HELM_LOR);
    }

    private static final int[] BLACK_MASK = new int[]{BLACK_MASK_1, BLACK_MASK_2, BLACK_MASK_3, BLACK_MASK_4, BLACK_MASK_5, BLACK_MASK_6, BLACK_MASK_7, BLACK_MASK_8, BLACK_MASK_9, BLACK_MASK_10};
    private static final int[] BLACK_MASK_IMBUED = new int[]{BLACK_MASK_1_I, BLACK_MASK_2_I, BLACK_MASK_3_I, BLACK_MASK_4_I, BLACK_MASK_5_I, BLACK_MASK_6_I, BLACK_MASK_7_I, BLACK_MASK_8_I, BLACK_MASK_9_I, BLACK_MASK_10_I};

    public static boolean wearingBlackMask(Player player) {
        return Arrays.stream(BLACK_MASK).anyMatch(mask -> player.getEquipment().hasAt(EquipSlot.HEAD, mask));
    }

    public static boolean sigilList(Player player) {
        return player.getInventory().containsAny(SIGIL_OF_FORTIFICATION);
    }

    public static boolean wearingBlackMaskImbued(Player player) {
        return Arrays.stream(BLACK_MASK_IMBUED).anyMatch(mask -> player.getEquipment().hasAt(EquipSlot.HEAD, mask));
    }
}

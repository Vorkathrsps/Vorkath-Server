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
import java.util.Objects;

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
            return name.equalsIgnoreCase("Imp") || name.equalsIgnoreCase("Imp Champion") || name.equalsIgnoreCase("Lesser demon") || name.equalsIgnoreCase("Lesser Demon Champion") || name.equalsIgnoreCase("Greater demon") || name.equalsIgnoreCase("Black demon") || name.equalsIgnoreCase("Abyssal demon") || name.equalsIgnoreCase("Greater abyssal demon") || name.equalsIgnoreCase("Ice demon") || name.equalsIgnoreCase("Bloodveld") || name.equalsIgnoreCase("Insatiable Bloodveld") || name.equalsIgnoreCase("Mutated Bloodveld") || name.equalsIgnoreCase("Insatiable Mutated Bloodveld") || name.equalsIgnoreCase("Demonic gorilla") || name.equalsIgnoreCase("hellhound") || name.equalsIgnoreCase("Skeleton Hellhound") || name.equalsIgnoreCase("Greater Skeleton Hellhound") || name.equalsIgnoreCase("Nechryael") || name.equalsIgnoreCase("Death spawn") || name.equalsIgnoreCase("Greater Nechryael") || name.equalsIgnoreCase("Nechryarch") || name.equalsIgnoreCase("Chaotic death spawn") || name.equalsIgnoreCase("duke sucellus") || name.equalsIgnoreCase("bloodveld") || name.equalsIgnoreCase("blood reaver") || name.equalsIgnoreCase("demonic gorilla") || name.equalsIgnoreCase("scarred black demon") || name.equalsIgnoreCase("nechryarch") || name.equalsIgnoreCase("scarred lesser demon") || name.equalsIgnoreCase("greater nechryael") || name.equalsIgnoreCase("abyssal demon") || name.equalsIgnoreCase("abyssal sire") || name.equalsIgnoreCase("cerberus") || name.equalsIgnoreCase("k'ril tsutsaroth") || name.equalsIgnoreCase("balfrug kreeyath") || name.equalsIgnoreCase("tstanon karlak") || name.equalsIgnoreCase("zakl'n gritch") || name.equalsIgnoreCase("icefiend") || name.equalsIgnoreCase("pyrefiend") || name.equalsIgnoreCase("waterfiend") || name.equalsIgnoreCase("pyrelord") || name.equalsIgnoreCase("infernal pyrelord") || name.equalsIgnoreCase("mutated bloodveld") || name.equalsIgnoreCase("insatiable mutated bloodveld") || name.equalsIgnoreCase("ice demon") || name.equalsIgnoreCase("greater abyssal demon") || name.equalsIgnoreCase("black demon") || name.equalsIgnoreCase("greater demon") || name.equalsIgnoreCase("lesser demon champion") || name.equalsIgnoreCase("skotizo") || name.equalsIgnoreCase("tortured gorilla");
        }
        return false;
    }


    public static final int[] AVIANSIES = new int[]{
        NpcIdentifiers.AVIANSIE,
        NpcIdentifiers.AVIANSIE_3170,
        NpcIdentifiers.AVIANSIE_3171,
        NpcIdentifiers.AVIANSIE_3172,
        NpcIdentifiers.AVIANSIE_3173,
        NpcIdentifiers.AVIANSIE_3174,
        NpcIdentifiers.AVIANSIE_3175,
        NpcIdentifiers.AVIANSIE_3176,
        NpcIdentifiers.AVIANSIE_3177,
        NpcIdentifiers.AVIANSIE_3178,
        NpcIdentifiers.AVIANSIE_3179,
        NpcIdentifiers.AVIANSIE_3180,
        NpcIdentifiers.AVIANSIE_3181,
        NpcIdentifiers.AVIANSIE_3182,
        NpcIdentifiers.AVIANSIE_3183,
        NpcIdentifiers.SPIRITUAL_MAGE_3168,
        NpcIdentifiers.SPIRITUAL_WARRIOR_3166
    };
    public static final int[] BARROWS_BROTHERS = new int[]{NpcIdentifiers.GUTHAN_THE_INFESTED, NpcIdentifiers.DHAROK_THE_WRETCHED, NpcIdentifiers.TORAG_THE_CORRUPTED, NpcIdentifiers.VERAC_THE_DEFILED, NpcIdentifiers.KARIL_THE_TAINTED};

    static int[] undeadNpcs = new int[]{NpcIdentifiers.ABERRANT_SPECTRE, NpcIdentifiers.ABHORRENT_SPECTRE, NpcIdentifiers.ANKOU, NpcIdentifiers.ANKOU_2515, NpcIdentifiers.ANKOU_2516, NpcIdentifiers.ANKOU_2517, NpcIdentifiers.ANKOU_2518, NpcIdentifiers.ANKOU_2519, NpcIdentifiers.ANKOU_2516, NpcIdentifiers.ASYN_SHADE, NpcIdentifiers.ASYN_SHADOW, NpcIdentifiers.ASYN_SHADOW_5632, NpcIdentifiers.BANSHEE, NpcIdentifiers.VETION, NpcIdentifiers.VETION_REBORN, NpcIdentifiers.CRAWLING_HAND, NpcIdentifiers.CRAWLING_HAND_449, NpcIdentifiers.CRAWLING_HAND_448, NpcIdentifiers.CRAWLING_HAND_451, NpcIdentifiers.CRAWLING_HAND_453, NpcIdentifiers.CRAWLING_HAND_452, NpcIdentifiers.CRUSHING_HAND, NpcIdentifiers.DARK_ANKOU, NpcIdentifiers.DEVIANT_SPECTRE, NpcIdentifiers.FIYR_SHADE, NpcIdentifiers.FIYR_SHADOW, NpcIdentifiers.FORGOTTEN_SOUL, NpcIdentifiers.FORGOTTEN_SOUL_10524, NpcIdentifiers.FORGOTTEN_SOUL_10526, NpcIdentifiers.FORGOTTEN_SOUL_10525, NpcIdentifiers.GHOST, NpcIdentifiers.GHOST_86, NpcIdentifiers.GHOST_87, NpcIdentifiers.GHOST_88, NpcIdentifiers.GHOST_89, NpcIdentifiers.GHOST_90, NpcIdentifiers.GHOST_91, NpcIdentifiers.GHOST_92, NpcIdentifiers.GHOST_93, NpcIdentifiers.GIANT_SKELETON, NpcIdentifiers.GIANT_SKELETON_681, NpcIdentifiers.GIANT_SKELETON_6440, NpcIdentifiers.HEADLESS_BEAST, NpcIdentifiers.HEADLESS_BEAST_10506, NpcIdentifiers.HEADLESS_BEAST_HARD, NpcIdentifiers.LOAR_SHADE, NpcIdentifiers.LOAR_SHADOW, NpcIdentifiers.MONKEY_ZOMBIE, NpcIdentifiers.MONKEY_ZOMBIE_5283, NpcIdentifiers.MONKEY_ZOMBIE_5282, NpcIdentifiers.MUMMY, NpcIdentifiers.MUMMY_721, NpcIdentifiers.MUMMY_722, NpcIdentifiers.MUMMY_723, NpcIdentifiers.MUMMY_724, NpcIdentifiers.MUMMY_725, NpcIdentifiers.PESTILENT_BLOAT, NpcIdentifiers.PESTILENT_BLOAT_10812, NpcIdentifiers.PESTILENT_BLOAT_11184, NpcIdentifiers.PESTILENT_BLOAT_10813, NpcIdentifiers.PHRIN_SHADE, NpcIdentifiers.PHRIN_SHADOW, NpcIdentifiers.REPUGNANT_SPECTRE, NpcIdentifiers.REVENANT_CYCLOPS, NpcIdentifiers.REVENANT_DARK_BEAST, NpcIdentifiers.REVENANT_DEMON, NpcIdentifiers.REVENANT_DRAGON, NpcIdentifiers.REVENANT_GOBLIN, NpcIdentifiers.REVENANT_HELLHOUND, NpcIdentifiers.REVENANT_HOBGOBLIN, NpcIdentifiers.REVENANT_IMP, NpcIdentifiers.REVENANT_KNIGHT, NpcIdentifiers.REVENANT_MALEDICTUS, NpcIdentifiers.REVENANT_ORK, NpcIdentifiers.REVENANT_PYREFIEND, NpcIdentifiers.SKELETAL_MYSTIC, NpcIdentifiers.SKELETAL_MYSTIC_7605, NpcIdentifiers.SKELETAL_MYSTIC_7605, NpcIdentifiers.SKELETON_HELLHOUND, NpcIdentifiers.SKELETON_HELLHOUND_6387, NpcIdentifiers.SKELETON_HELLHOUND_6613, NpcIdentifiers.SKELETON_MAGE, NpcIdentifiers.SKELETON_MAGE_4312, NpcIdentifiers.SKELETON_MAGE_4318, NpcIdentifiers.SLASH_BASH, NpcIdentifiers.TARN, NpcIdentifiers.MUTANT_TARN, NpcIdentifiers.TARN_6476, NpcIdentifiers.UNDEAD_DRUID, NpcIdentifiers.ULFRIC, NpcIdentifiers.VORKATH, NpcIdentifiers.VORKATH_8059, NpcIdentifiers.VORKATH_8060, NpcIdentifiers.VORKATH_8061, NpcIdentifiers.VORKATH_8058, VORKATHS_HEAD_21912, NpcIdentifiers.ZOMBIFIED_SPAWN, NpcIdentifiers.ZOMBIFIED_SPAWN_8063, NpcIdentifiers.REVENANT_DRAGON};
    public static int[] vampyres = new int[]{3137,
        3234,
        3237,
        3239,
        3690,
        3691,
        3692,
        3693,
        3694,
        3695,
        3696,
        3697,
        3698,
        3699,
        3700,
        3701,
        3702,
        3703,
        3704,
        3705,
        3706,
        3707,
        3708,
        4427,
        4428,
        4429,
        4430,
        4431,
        4432,
        4433,
        4434,
        4436,
        4437,
        4438,
        4439,
        4442,
        4443,
        4481,
        4482,
        4486,
        4487,
        5634,
        5635,
        5636,
        5637,
        5638,
        5639,
        5640,
        5641,
        5642,
        8326,
        8327,
        8676,
        9586,
        9587,
        9614,
        9615,
        9616,
        9617,
        9683,
        9684,
        9704,
        9727,
        9728,
        9729,
        9730,
        9731,
        9732,
        9733,
        9734,
        3790,
        3710,
        3711,
        3712,
        3713,
        3714,
        3715,
        3716,
        3717,
        3718,
        3719,
        3720,
        3721,
        3722,
        3723,
        3724,
        3725,
        3726,
        3727,
        3728,
        3729,
        3730,
        3731,
        3732,
        3748,
        3749,
        3750,
        3751,
        3752,
        3753,
        3754,
        3755,
        3756,
        3757,
        3758,
        3759,
        3760,
        3761,
        3762,
        3763,
        3768,
        3769,
        3770,
        3771,
        8251,
        8252,
        8253,
        8254,
        8255,
        8256,
        8257,
        8258,
        8259,
        8300,
        8301,
        8302,
        8303,
        8304,
        8305,
        8306,
        8307,
        9590,
        9591,
        9599,
        9600,
        9601,
        9602,
        9603,
        9604,
        9605,
        9606,
        9607,
        9608,
        9735,
        9736,
        9737,
        9738,
        9739,
        9740,
        9741,
        9742,
        9756,
        9757,
        9758,
        9759,
        9760,
        9761,
        9762,
        9763,
        11169,
        11170,
        11171,
        11172,
        11173};
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

    public static boolean isVampyre(Entity target) {
        if (target.isNpc()) {
            NPC npc = target.getAsNpc();
            int npcId = npc.id();
            for (int vampyre : vampyres) {
                if (npcId == vampyre) {
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
        return player.getEquipment().containsAll(ItemIdentifiers.VIRTUS_MASK, ItemIdentifiers.VIRTUS_ROBE_TOP, VIRTUS_ROBE_BOTTOM);
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
            return name.contains("Corporeal Beast") || name.contains("The Great Olm") || name.contains("El Fuego") || name.contains("Hungarian horntail") || name.contains("Wyvern") || name.contains("Basilisk (Right claw)") || name.contains("Basilisk (Left claw)") || name.contains("Basilisk") || name.contains("Great Olm") || name.contains("Wyrm") || name.contains("Drake") || name.contains("Hydra") || name.contains("Vorkath") || name.contains("Galvek") || name.contains("dragon") || name.contains("Dragon") && !exceptions;
        }
        return false;
    }

    public static boolean isWearingObsidianArmour(Player player) {
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
        return player.getEquipment().containsAny(AMULET_OF_THE_DAMNED_FULL, AMULET_OF_THE_DAMNED);
    }

    public static boolean wearingFullInquisitors(Player player) {
        return player.getEquipment().containsAll(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT);
    }

    public static boolean wearingInquisitorsPiece(Player player) {
        return player.getEquipment().containsAny(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT);
    }

    public static boolean wearingSpearsOrHalberds(Player player) {
        return player.getEquipment().containsAny(
            VESTAS_SPEAR, LEAFBLADED_SPEAR,
            GUTHANS_WARSPEAR, ZAMORAKIAN_SPEAR,
            ZAMORAKIAN_HASTA, OSMUMTENS_FANG,
            OSMUMTENS_FANG_OR, CRYSTAL_HALBERD,
            CRYSTAL_HALBERD_FULL, NEW_CRYSTAL_HALBERD_FULL,
            DRAGON_HALBERD, DRAGON_DAGGER, DRAGON_DAGGERP,
            DRAGON_DAGGERP_5680, DRAGON_DAGGER_20407,
            DRAGON_DAGGERP_5698, ABYSSAL_DAGGER,
            ABYSSAL_DAGGER_P_13271, ABYSSAL_DAGGER_P_13269,
            ABYSSAL_DAGGER_BH, ABYSSAL_DAGGER_BHP,
            ABYSSAL_DAGGER_BHP_27867, ABYSSAL_DAGGER_BHP_27865);
    }

    public static boolean isWearingPoisonEquipmentOrWeapon(Player player) { //add any extra poison weapons here
        return player.getEquipment().containsAny(
            SERPENTINE_HELM, MAGMA_HELM,
            TANZANITE_HELM, TOXIC_BLOWPIPE,
            ABYSSAL_TENTACLE, ABYSSAL_TENTACLE_OR,
            DRAGON_DAGGERP, DRAGON_DAGGERP_5680,
            ABYSSAL_DAGGER_BHP, ABYSSAL_DAGGER_P,
            ABYSSAL_DAGGER_BHP_27867, ABYSSAL_DAGGER_BHP_27865,
            ABYSSAL_TENTACLE, EMERALD_BOLTS_E, EMERALD_DRAGON_BOLTS_E);
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
        return player.getEquipment().containsAny(SLAYER_HELMET, TWISTED_SLAYER_HELMET, GREEN_SLAYER_HELMET, RED_SLAYER_HELMET, BLACK_SLAYER_HELMET, PURPLE_SLAYER_HELMET, TURQUOISE_SLAYER_HELMET, HYDRA_SLAYER_HELMET, VAMPYRIC_SLAYER_HELMET, TZKAL_SLAYER_HELMET);
    }

    public static boolean hasSlayerHelmetImbued(Player player) {
        return player.getEquipment().containsAny(SLAYER_HELMET_I, TWISTED_SLAYER_HELMET_I, GREEN_SLAYER_HELMET_I, RED_SLAYER_HELMET_I, BLACK_SLAYER_HELMET_I, PURPLE_SLAYER_HELMET_I, TURQUOISE_SLAYER_HELMET_I, HYDRA_SLAYER_HELMET_I, VAMPYRIC_SLAYER_HELMET_I, TZKAL_SLAYER_HELMET_I);
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

    public static boolean hasCrystalBow(Player player) {
        return player.getEquipment().containsAny(CRYSTAL_BOW, NEW_CRYSTAL_BOW);
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
        return Arrays.stream(BLACK_MASK).filter(Objects::nonNull).anyMatch(mask -> player.getEquipment().hasAt(EquipSlot.HEAD, mask));
    }

    public static boolean hasInfernalAxe(Player player) {
        return player.getEquipment().hasAt(EquipSlot.WEAPON, INFERNAL_AXE);
    }

    public static boolean hasRingOfWealthImbued(Player player) {
        return player.getEquipment().hasAt(EquipSlot.RING, RING_OF_WEALTH_I);
    }

    public static boolean sigilList(Player player) {
        return player.getInventory().containsAny(SIGIL_OF_FORTIFICATION);
    }

    public static boolean wearingBlackMaskImbued(Player player) {
        return Arrays.stream(BLACK_MASK_IMBUED).filter(Objects::nonNull).anyMatch(mask -> player.getEquipment().hasAt(EquipSlot.HEAD, mask));
    }
}

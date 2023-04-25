package com.aelous.model.entity.combat.formula;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.entity.Entity;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;

import java.util.Arrays;

import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.BLACK_MASK_9_I;

/**
 * This is a utility class for the combat max hits.
 * @author Patrick van Elderen <https://github.com/PVE95>
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
        if(target.isNpc()) {
            NPC npc = target.getAsNpc();
            NpcDefinition def = npc.def();
            String name = "";
            if(def != null) {
                name = def.name;
            }
            return name.equalsIgnoreCase("Imp") || name.equalsIgnoreCase("Imp Champion") || name.equalsIgnoreCase("Lesser demon") || name.equalsIgnoreCase("Lesser Demon Champion") || name.equalsIgnoreCase("Greater demon") || name.equalsIgnoreCase("Black demon") || name.equalsIgnoreCase("Abyssal demon") || name.equalsIgnoreCase("Greater abyssal demon") || name.equalsIgnoreCase("Ice demon") || name.equalsIgnoreCase("Bloodveld") || name.equalsIgnoreCase("Insatiable Bloodveld") || name.equalsIgnoreCase("Mutated Bloodveld") || name.equalsIgnoreCase("Insatiable Mutated Bloodveld") || name.equalsIgnoreCase("Demonic gorilla") || name.equalsIgnoreCase("hellhound") || name.equalsIgnoreCase("Skeleton Hellhound") || name.equalsIgnoreCase("Greater Skeleton Hellhound") || name.equalsIgnoreCase("Nechryael") || name.equalsIgnoreCase("Death spawn") || name.equalsIgnoreCase("Greater Nechryael") || name.equalsIgnoreCase("Nechryarch") || name.equalsIgnoreCase("Chaotic death spawn");
        }
        return false;
    }

    public static boolean isUndead(Entity target) {
        if(target.isNpc()) {
            NPC npc = target.getAsNpc();
            NpcDefinition def = npc.def();
            String name = "";
            if(def != null) {
                name = def.name;
            }
            return name.equalsIgnoreCase("ancient revenant") || name.equalsIgnoreCase("revenant") || name.equalsIgnoreCase("Aberrant spectre") || name.equalsIgnoreCase("Ankou") || name.equalsIgnoreCase("Banshee") || name.equalsIgnoreCase("Crawling Hand") || name.equalsIgnoreCase("Ghast") || name.equalsIgnoreCase("Ghost") || name.equalsIgnoreCase("Mummy") || name.contains("revenant") || name.equalsIgnoreCase("Shade") || name.equalsIgnoreCase("Skeleton") || name.equalsIgnoreCase("Skogre") || name.equalsIgnoreCase("Summoned Zombie") || name.equalsIgnoreCase("Tortured soul") || name.equalsIgnoreCase("Undead chicken") || name.equalsIgnoreCase("Undead cow") || name.equalsIgnoreCase("Undead one") || name.equalsIgnoreCase("Zogre") || name.equalsIgnoreCase("Zombified Spawn") || name.equalsIgnoreCase("Zombie") || name.equalsIgnoreCase("Zombie rat") || name.equalsIgnoreCase("Vet'ion") || name.equalsIgnoreCase("Ahrim the Blighted") || name.equalsIgnoreCase("Dharok the Wretched") || name.equalsIgnoreCase("Guthan the Infested") || name.equalsIgnoreCase("Karil the Tainted") || name.equalsIgnoreCase("Torag the Corrupted") || name.equalsIgnoreCase("Verac the Defiled") || name.equalsIgnoreCase("Pestilent Bloat") || name.equalsIgnoreCase("Mi-Gor") || name.equalsIgnoreCase("Treus Dayth") || name.equalsIgnoreCase("Nazastarool") || name.equalsIgnoreCase("Slash Bash") || name.equalsIgnoreCase("Ulfric") || name.equalsIgnoreCase("Vorkath");
        }
        return false;
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

    public static boolean isWearingPoisonEquipmentOrWeapon(Player player) {
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
        return (eq.hasAt(EquipSlot.WEAPON, 22555) && (WildernessArea.inWild(player)));
    }

    public static boolean hasCrawsBow(Player player) {
        return ((player.getEquipment().hasAt(EquipSlot.WEAPON, CRAWS_BOW)  && WildernessArea.inWild(player)));
    }

    public static boolean hasAmuletOfAvarice(Player player) {
        ItemContainer eq = player.getEquipment();
        return (eq.hasAt(EquipSlot.WEAPON, 22557) && WildernessArea.inWild(player));
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
        return player.getEquipment().containsAny(BOW_OF_FAERDHINEN, BOW_OF_FAERDHINEN_27187, BOW_OF_FAERDHINEN_C, BOW_OF_FAERDHINEN_C_25869,BOW_OF_FAERDHINEN_C_25884,BOW_OF_FAERDHINEN_C_25886,BOW_OF_FAERDHINEN_C_25888, BOW_OF_FAERDHINEN_C_25890,BOW_OF_FAERDHINEN_C_25892,BOW_OF_FAERDHINEN_C_25892,BOW_OF_FAERDHINEN_C_25896,BOW_OF_FAERDHINEN_C_25896);
    }

    public static boolean hasZurielStaff(Player player) {
        return player.getEquipment().contains(ZURIELS_STAFF);
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

    private static final int[] BLACK_MASK = new int[] {BLACK_MASK_1, BLACK_MASK_2, BLACK_MASK_3, BLACK_MASK_4, BLACK_MASK_5, BLACK_MASK_6, BLACK_MASK_7, BLACK_MASK_8, BLACK_MASK_9, BLACK_MASK_10};
    private static final int[] BLACK_MASK_IMBUED = new int[] {BLACK_MASK_1_I, BLACK_MASK_2_I, BLACK_MASK_3_I, BLACK_MASK_4_I, BLACK_MASK_5_I, BLACK_MASK_6_I, BLACK_MASK_7_I, BLACK_MASK_8_I, BLACK_MASK_9_I, BLACK_MASK_10_I};

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

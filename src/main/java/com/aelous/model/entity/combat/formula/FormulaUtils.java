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

    public static boolean hasViggorasChainMace(Player player) {
        return ((player.getEquipment().hasAt(EquipSlot.WEAPON, VIGGORAS_CHAINMACE) && WildernessArea.inWild(player)));
    }

    public static boolean wearingFullInquisitors(Player player) {
        return player.getEquipment().containsAll(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT);
    }

    public static boolean wearingInquisitorsPiece(Player player) {
        return player.getEquipment().containsAny(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT);
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

    public static boolean hasObbyWeapon(Player player) {
        ItemContainer eq = player.getEquipment();
        int[] weaponry = new int[]{6528, 6523, 6525};
        return ((eq.hasAt(EquipSlot.WEAPON, weaponry[0]) || (eq.hasAt(EquipSlot.WEAPON, weaponry[1]) || (eq.hasAt(EquipSlot.WEAPON, weaponry[2])))));
    }

    public static boolean voidBase(Player player) {
        return ((player.getEquipment().hasAt(EquipSlot.BODY, 8839) && player.getEquipment().hasAt(EquipSlot.LEGS, 8840)) || (player.getEquipment().hasAt(EquipSlot.BODY, 13072) && player.getEquipment().hasAt(EquipSlot.LEGS, 13073))) && player.getEquipment().hasAt(EquipSlot.HANDS, 8842);
    }

    public static boolean voidRanger(Player player) {
        return player.getEquipment().hasAt(EquipSlot.HEAD, 11664) && voidBase(player);
    }

    public static boolean voidMelee(Player player) {
        return player.getEquipment().hasAt(EquipSlot.HEAD, 11665) && voidBase(player);
    }

    public static boolean voidMagic(Player player) {
        return player.getEquipment().hasAt(EquipSlot.HEAD, 11663) && voidBase(player);
    }

    public static boolean wearingEliteVoid(Player p) {
        return (p.getEquipment().contains(11665) || p.getEquipment().contains(11664) || p.getEquipment().contains(11663)) && p.getEquipment().hasAt(EquipSlot.BODY, 13072) && p.getEquipment().hasAt(EquipSlot.LEGS, 13073) && p.getEquipment().hasAt(EquipSlot.HANDS, 8842);
    }

    private static final int[] BLACK_MASK = new int[] {BLACK_MASK_1, BLACK_MASK_2, BLACK_MASK_3, BLACK_MASK_4, BLACK_MASK_5, BLACK_MASK_6, BLACK_MASK_7, BLACK_MASK_8, BLACK_MASK_9, BLACK_MASK_10};
    private static final int[] BLACK_MASK_IMBUED = new int[] {BLACK_MASK_1_I, BLACK_MASK_2_I, BLACK_MASK_3_I, BLACK_MASK_4_I, BLACK_MASK_5_I, BLACK_MASK_6_I, BLACK_MASK_7_I, BLACK_MASK_8_I, BLACK_MASK_9_I, BLACK_MASK_10_I};

    public static boolean wearingBlackMask(Player player) {
        return Arrays.stream(BLACK_MASK).anyMatch(mask -> player.getEquipment().hasAt(EquipSlot.HEAD, mask));
    }

    public static boolean wearingBlackMaskImbued(Player player) {
        return Arrays.stream(BLACK_MASK_IMBUED).anyMatch(mask -> player.getEquipment().hasAt(EquipSlot.HEAD, mask));
    }

    private static final int[] TWISTED_SLAYER_HELMET_I = new int[] {ItemIdentifiers.TWISTED_SLAYER_HELMET_I};
    public static boolean wearingTwistedSlayerHelmetI(Player player) {
        return Arrays.stream(TWISTED_SLAYER_HELMET_I).anyMatch(mask -> player.getEquipment().hasAt(EquipSlot.HEAD, mask));
    }

}

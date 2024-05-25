package com.cryptic.model.cs2.impl.weaponinterface;

import lombok.Getter;

import java.util.List;

@Getter
public enum AttackStyleDefinition {
    UNARMED(List.of(188), "Unarmed"),
    AXE(List.of(35), "Axe"),
    BLUNT(List.of(26), "Blunt"),
    BOW(List.of(64, 106), "Bow"),
    CLAWS(List.of(65), "Claws"),
    CROSSBOW(List.of(567, 37), "Crossbow"),
    SALAMANDER(List.of(586), "Salamander"),
    CHINCHOMPA(List.of(572), "Chinchompas"),
    GUN(List.of(96), "Gun"),
    SLASH_SWORD(List.of(21), "Slash sword"),
    TWO_HANDED(List.of(61), "Two-handed sword"),
    PICKAXE(List.of(67), "Pickaxe"),
    POLEARM(List.of(66), "Polearm"),
    POLESTAFF(List.of(-1), "Polestaff"),
    SCYTHE(List.of(1143), "Scythe"),
    SPEAR(List.of(36), "Spear"),
    SPIKED(List.of(39), "Spiked"),
    STAB_SWORD(List.of(25), "Stab sword"),
    STAFF(List.of(1), "Staff"),
    THROWN(List.of(24), "Thrown"),
    WHIP(List.of(150), "Whip"),
    BLADED_STAFF(List.of(1), "Bladed Staff"),
    THROWN_MAGIC(List.of(-1), "Powered Staff"),
    BANNER(List.of(92), "Banner"),
    UNUSED_HALBERD(List.of(-1), "Unused"),
    UNUSED_MAUL(List.of(-1), "Bludgeon"),
    BULWARK(List.of(1014), "Bulwark");

    final List<Integer> ids;
    final String category;

    AttackStyleDefinition(List<Integer> ids, String category) {
        this.ids = ids;
        this.category = category;
    }

    public static int getVarbit(int id) {
        for (AttackStyleDefinition category : values()) {
            if (category.getIds().contains(id)) {
                return category.ordinal();
            }
        }
        return 0;
    }

    public static String getName(int id) {
        for (AttackStyleDefinition category : values()) {
            if (category.getIds().contains(id)) {
                return category.getCategory();
            }
        }
        return "Unknown type";
    }
}

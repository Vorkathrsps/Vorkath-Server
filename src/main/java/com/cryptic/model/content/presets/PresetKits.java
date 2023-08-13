package com.cryptic.model.content.presets;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import lombok.Getter;

import java.util.List;

public enum PresetKits {
    MAIN_MELEE(
        new Item[]{
            Item.of(ItemIdentifiers.SUPER_ATTACK4),
            Item.of(ItemIdentifiers.SUPER_STRENGTH4),
            Item.of(ItemIdentifiers.SARADOMIN_BREW4),
            Item.of(ItemIdentifiers.SUPER_RESTORE4),
            Item.of(ItemIdentifiers.DRAGON_DAGGER),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.EARTH_RUNE, 135),
            Item.of(ItemIdentifiers.DEATH_RUNE, 38),
            Item.of(ItemIdentifiers.ASTRAL_RUNE, 40)
        },
        List.of(
            new Item(ItemIdentifiers.HELM_OF_NEITIZNOT),
            new Item(ItemIdentifiers.FIRE_CAPE),
            new Item(ItemIdentifiers.AMULET_OF_GLORY6),
            new Item(ItemIdentifiers.HOLY_BLESSING),
            new Item(ItemIdentifiers.DRAGON_SCIMITAR),
            new Item(ItemIdentifiers.RUNE_PLATEBODY),
            new Item(ItemIdentifiers.RUNE_KITESHIELD),
            new Item(ItemIdentifiers.RUNE_PLATELEGS),
            new Item(ItemIdentifiers.DRAGON_GLOVES),
            new Item(ItemIdentifiers.CLIMBING_BOOTS),
            new Item(ItemIdentifiers.RING_OF_RECOIL)
        ),
        MagicSpellbook.LUNAR,
        new Skill[]{
            Skill.fromId(Skill.ATTACK.getId()),
            Skill.fromId(Skill.STRENGTH.getId()),
            Skill.fromId(Skill.DEFENCE.getId()),
            Skill.fromId(Skill.RANGED.getId()),
            Skill.fromId(Skill.PRAYER.getId()),
            Skill.fromId(Skill.MAGIC.getId()),
            Skill.fromId(Skill.HITPOINTS.getId())
        },
        new int[]
            {
                99, 99, 99, 99, 99, 99, 99
            },
        73271, AttributeKey.MAIN_MELEE_PRESET),
    ZERKER_MELEE(
        new Item[]{
            Item.of(ItemIdentifiers.SUPER_ATTACK4),
            Item.of(ItemIdentifiers.SUPER_STRENGTH4),
            Item.of(ItemIdentifiers.SARADOMIN_BREW4),
            Item.of(ItemIdentifiers.SUPER_RESTORE4),
            Item.of(ItemIdentifiers.DRAGON_DAGGER),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.EARTH_RUNE, 135),
            Item.of(ItemIdentifiers.DEATH_RUNE, 38),
            Item.of(ItemIdentifiers.ASTRAL_RUNE, 40)
        },
        List.of(
            new Item(ItemIdentifiers.BERSERKER_HELM),
            new Item(ItemIdentifiers.FIRE_CAPE),
            new Item(ItemIdentifiers.AMULET_OF_GLORY6),
            new Item(ItemIdentifiers.HOLY_BLESSING),
            new Item(ItemIdentifiers.DRAGON_SCIMITAR),
            new Item(ItemIdentifiers.RUNE_PLATEBODY),
            new Item(ItemIdentifiers.RUNE_KITESHIELD),
            new Item(ItemIdentifiers.RUNE_PLATELEGS),
            new Item(ItemIdentifiers.RUNE_GLOVES),
            new Item(ItemIdentifiers.CLIMBING_BOOTS),
            new Item(ItemIdentifiers.RING_OF_RECOIL)
        ),
        MagicSpellbook.LUNAR,
        new Skill[]{
            Skill.fromId(Skill.ATTACK.getId()),
            Skill.fromId(Skill.STRENGTH.getId()),
            Skill.fromId(Skill.DEFENCE.getId()),
            Skill.fromId(Skill.RANGED.getId()),
            Skill.fromId(Skill.PRAYER.getId()),
            Skill.fromId(Skill.MAGIC.getId()),
            Skill.fromId(Skill.HITPOINTS.getId())
        },
        new int[]
            {
                60, 99, 45, 99, 52, 99, 99
            },
        73272, AttributeKey.ZERKER_MELEE_PRESET),
    PURE_MELEE(
        new Item[]{
            Item.of(ItemIdentifiers.SUPER_ATTACK4),
            Item.of(ItemIdentifiers.SUPER_STRENGTH4),
            Item.of(ItemIdentifiers.SARADOMIN_BREW4),
            Item.of(ItemIdentifiers.SUPER_RESTORE4),
            Item.of(ItemIdentifiers.DRAGON_DAGGER),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.COOKED_KARAMBWAN),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK),
            Item.of(ItemIdentifiers.SHARK)
        },
        List.of(
            new Item(ItemIdentifiers.GHOSTLY_HOOD),
            new Item(ItemIdentifiers.FIRE_CAPE),
            new Item(ItemIdentifiers.AMULET_OF_GLORY6),
            new Item(ItemIdentifiers.HOLY_BLESSING),
            new Item(ItemIdentifiers.DRAGON_SCIMITAR),
            new Item(ItemIdentifiers.GHOSTLY_ROBE),
            new Item(ItemIdentifiers.UNHOLY_BOOK),
            new Item(ItemIdentifiers.BLACK_DHIDE_CHAPS),
            new Item(ItemIdentifiers.MITHRIL_GLOVES),
            new Item(ItemIdentifiers.CLIMBING_BOOTS),
            new Item(ItemIdentifiers.RING_OF_RECOIL)
        ),
        MagicSpellbook.NORMAL,
        new Skill[]{
            Skill.fromId(Skill.ATTACK.getId()),
            Skill.fromId(Skill.STRENGTH.getId()),
            Skill.fromId(Skill.DEFENCE.getId()),
            Skill.fromId(Skill.RANGED.getId()),
            Skill.fromId(Skill.PRAYER.getId()),
            Skill.fromId(Skill.MAGIC.getId()),
            Skill.fromId(Skill.HITPOINTS.getId())
        },
        new int[]
            {
                60, 99, 1, 99, 52, 99, 99
            },
        73273, AttributeKey.PURE_MELEE_PRESET);

    @Getter Item[] inventoryItemList;
    @Getter List<Item> equipmentItemList;
    @Getter
    MagicSpellbook spellbook;
    @Getter int[] alterLevels;
    @Getter Skill[] currentLevels;
    @Getter int buttonIdentification;
    @Getter AttributeKey attributeKey;

    PresetKits(Item[] inventoryItemList, List<Item> equipmentItemList, MagicSpellbook spellbook, Skill[] currentLevels, int[] alterLevels, int buttonIdentification, AttributeKey attributeKey) {
        this.inventoryItemList = inventoryItemList;
        this.equipmentItemList = equipmentItemList;
        this.spellbook = spellbook;
        this.currentLevels = currentLevels;
        this.alterLevels = alterLevels;
        this.buttonIdentification = buttonIdentification;
        this.attributeKey = attributeKey;
    }
}

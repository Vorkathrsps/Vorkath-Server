package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DefaultKits implements Kit {
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
            new Item(ItemIdentifiers.BRONZE_ARROW),
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
        73274, AttributeKey.MAIN_MELEE_PRESET),
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

    Item[] inventoryItemList;
    List<Item> equipmentItemList;
    MagicSpellbook spellbook;
    int[] alteredLevels;
    Skill[] currentLevels;
    int buttonIdentification;
    AttributeKey attributeKey;

    DefaultKits(Item[] inventoryItemList, List<Item> equipmentItemList, MagicSpellbook spellbook, Skill[] currentLevels, int[] alteredLevels, int buttonIdentification, AttributeKey attributeKey) {
        this.inventoryItemList = inventoryItemList;
        this.equipmentItemList = equipmentItemList;
        this.spellbook = spellbook;
        this.currentLevels = currentLevels;
        this.alteredLevels = alteredLevels;
        this.buttonIdentification = buttonIdentification;
        this.attributeKey = attributeKey;
    }

    @Override
    public List<Item> getInventoryItemList() {
        logger.info("Getting inventory item list For DefaultKits");
        return Arrays.stream(inventoryItemList).collect(Collectors.toList());
    }

    @Override
    public MagicSpellbook getSpellbook() {
        logger.info("Getting spellbook For DefaultKits");
        return spellbook;
    }

    @Override
    public AttributeKey getAttributeKey() {
        logger.info("Getting attribute key For DefaultKits");
        return attributeKey;
    }

    @Override
    public int getButtonIdentification() {
        logger.info("Getting button identification For DefaultKits");
        return buttonIdentification;
    }

    @Override
    public Skill[] getCurrentLevels() {
        logger.info("Getting current levels For DefaultKits");
        return currentLevels;
    }

    @Override
    public int[] getAlteredLevels() {
        logger.info("Getting altered levels For DefaultKits");
        return alteredLevels;
    }

    @Override
    public List<Item> getEquipmentList() {
        logger.info("Getting equipment list For DefaultKits");
        return equipmentItemList;
    }

}

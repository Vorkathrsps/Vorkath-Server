package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import lombok.Getter;

import java.util.List;

public enum SavedKits implements Kit {

    ONE(73291);

    @Getter final int buttonIdentification;

    SavedKits(int buttonIdentification) {
        this.buttonIdentification = buttonIdentification;
    }

    @Override
    public int getButtonIdentification() {
        logger.info("Getting button identification For SavedKits");
        return buttonIdentification;
    }

    @Override
    public Skill[] getCurrentLevels() {
        logger.info("Getting current levels For SavedKits");
        return new Skill[0];
    }

    @Override
    public int[] getAlteredLevels() {
        logger.info("Getting altered levels For SavedKits");
        return new int[0];
    }

    @Override
    public List<Item> getInventoryItemList() {
        logger.info("Getting inventory item list For SavedKits");
        return List.of();
    }

    @Override
    public List<Item> getEquipmentList() {
        logger.info("Getting equipment list For SavedKits");
        return null;
    }

    @Override
    public MagicSpellbook getSpellbook() {
        logger.info("Getting spellbook For SavedKits");
        return null;
    }

    @Override
    public AttributeKey getAttributeKey() {
        logger.info("Getting attribute key For SavedKits");
        return null;
    }

}

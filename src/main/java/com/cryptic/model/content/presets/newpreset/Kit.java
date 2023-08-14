package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Kit {
    Logger logger = LogManager.getLogger(Kit.class);
    int getButtonIdentification();
    Skill[] getCurrentLevels();
    int[] getAlteredLevels();
    List<Item> getInventoryItemList();
    List<Item> getEquipmentList();
    MagicSpellbook getSpellbook();
    AttributeKey getAttributeKey();
}


package com.aelous.model.content.skill.impl.fletching;

import com.aelous.model.items.Item;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 17, 2020
 */
public interface Fletchable {

    int getAnimation();

    Item getUse();

    Item getWith();

    FletchableItem[] getFletchableItems();

    Item[] getIngediants();

    String getProductionMessage();

    String getName();
}

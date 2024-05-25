package com.cryptic.model.content.skill.impl.fletching;

import com.cryptic.model.items.Item;

/**
 * @author Origin
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

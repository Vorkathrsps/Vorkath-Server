package com.aelous.model.content.skill.impl.crafting;

import com.aelous.model.items.Item;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 16, 2020
 */
public interface Craftable {

    /**
     * Gets the craftable name.
     *
     * @return The craftable name.
     */
    String getName();

    /**
     * Gets the craftable animation.
     *
     * @return The craftable animation.
     */
    int getAnimation();

    /**
     * Gets the craftable used item.
     *
     * @return The craftable used item.
     */
    Item getUse();

    /**
     * Gets the craftable item used with.
     *
     * @return The craftable item used with.
     */
    Item getWith();

    /**
     * Gets the craftable items.
     *
     * @return The craftable items.
     */
    CraftableItem[] getCraftableItems();

    /**
     * Gets the craftable ingredients.
     *
     * @param index The ingredient index.
     * @return The craftable ingredients.
     */
    Item[] getIngredients(int index);

    /**
     * Gets the craftable production message.
     *
     * @return The craftable production message.
     */
    String getProductionMessage();
}

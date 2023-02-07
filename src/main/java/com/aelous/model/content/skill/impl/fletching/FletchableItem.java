package com.aelous.model.content.skill.impl.fletching;

import com.aelous.model.items.Item;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 17, 2020
 */
public final class FletchableItem {

    private final Item product;

    private final int level;

    private final double experience;

    public FletchableItem(Item product, int level, double experience) {
        this.product = product;
        this.level = level;
        this.experience = experience;
    }

    public Item getProduct() {
        return product;
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }
}

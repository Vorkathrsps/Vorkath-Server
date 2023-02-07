package com.aelous.model.items;

/**
 * Represents a required item. Used when skilling.
 * @author Professor Oak
 */
public class RequiredItem {

    /**
     * The {@link Item}.
     */
    private final Item item;

    /**
     * Should this item be deleted eventually?
     */
    private final boolean delete;

    /**
     * Should we replace the item with another
     */
    private final Item replaceWith;

    public RequiredItem(Item item, boolean delete) {
        this.item = item;
        this.delete = delete;
        this.replaceWith = null;
    }


    public RequiredItem(Item item, boolean delete, Item replaceWith) {
        this.item = item;
        this.delete = delete;
        this.replaceWith = replaceWith;
    }

    public RequiredItem(Item item) {
        this.item = item;
        this.delete = false;
        this.replaceWith = null;
    }

    public Item getItem() {
        return item;
    }

    public boolean isDelete() {
        return delete;
    }

    public Item getReplaceWith() {
        return replaceWith;
    }
}

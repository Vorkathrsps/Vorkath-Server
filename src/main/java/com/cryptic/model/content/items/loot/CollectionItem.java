package com.cryptic.model.content.items.loot;

public class CollectionItem {
    public final int id;
    public final int rarity;
    public final int amount;
    public boolean isRare;
    public CollectionItem(int id, int rarity, int amount) {
        this.id = id;
        this.rarity = rarity;
        this.amount = amount;
    }

    public CollectionItem(int id, int rarity, int amount, boolean isRare) {
        this.id = id;
        this.rarity = rarity;
        this.amount = amount;
        this.isRare = isRare;
    }

    @Override
    public String toString() {
        return "CollectionItem{" +
            "id=" + id +
            ", rarity=" + rarity +
            ", amount=" + amount +
            ", isRare=" + isRare +
            '}';
    }
}

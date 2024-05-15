package com.cryptic.model.content.items.boxes;

public class MysteryBoxItem {
    public final int id;
    public final int rarity;
    public final int amount;
    public boolean isRare;
    public MysteryBoxItem(int id, int rarity, int amount) {
        this.id = id;
        this.rarity = rarity;
        this.amount = amount;
    }

    public MysteryBoxItem(int id, int rarity, int amount, boolean isRare) {
        this.id = id;
        this.rarity = rarity;
        this.amount = amount;
        this.isRare = isRare;
    }

    @Override
    public String toString() {
        return "MysteryBox{" +
            "id=" + id +
            ", rarity=" + rarity +
            '}';
    }
}

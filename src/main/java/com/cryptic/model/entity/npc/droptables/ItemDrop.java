package com.cryptic.model.entity.npc.droptables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class ItemDrop {
    @JsonProperty("item")
    private String item;
    @JsonProperty("minimumAmount")
    private int minimumAmount;
    @JsonProperty("maximumAmount")
    private int maximumAmount;
    @JsonProperty("chance")
    private int chance;
    @JsonProperty("rareDrop")
    private boolean rareDrop;

    @JsonProperty("sendToBank")
    private boolean sendToBank;

    public ItemDrop(String item, int minimumAmount, int maximumAmount, int chance, boolean rareDrop, boolean sendToBank) {
        this.item = item;

        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.chance = chance;
        this.rareDrop = rareDrop;
        this.sendToBank = sendToBank;
    }
    public ItemDrop(String item, int minimumAmount, int maximumAmount, int chance, boolean rareDrop) {
        this.item = item;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.chance = chance;
        this.rareDrop = rareDrop;
        this.sendToBank = false;
    }
    public ItemDrop(String item, int minimumAmount, int maximumAmount, int chance) {
        this.item = item;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.chance = chance;
        this.rareDrop = false;
        this.sendToBank = false;
    }

    public void postLoad() {
        if(item != null) {
            if(item.equalsIgnoreCase("null"))
                return;
            if(ItemRepository.getItemId(item) <= 0) {
                System.out.println("Error, could not find item " + item);
            }
        }
    }

    public String getItem() {
        return item;
    }
    public int getMinimumAmount() {
        return minimumAmount;
    }

    public int getMaximumAmount() {
        return maximumAmount;
    }

    public int getChance() {
        return chance;
    }

    public boolean isRareDrop() {
        return rareDrop;
    }

    public boolean isSendToBank() {
        return sendToBank;
    }
}

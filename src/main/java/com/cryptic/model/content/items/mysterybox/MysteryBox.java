package com.cryptic.model.content.items.mysterybox;

import com.cryptic.model.content.items.mysterybox.impl.DonatorMysteryBox;
import com.cryptic.model.entity.attributes.AttributeKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class MysteryBox {

    /**
     * Map of all the mystery boxes.
     */
    private static final Map<Integer, MysteryBox> MYSTERY_BOXES = new HashMap<>();

    /**
     * Handles loading the mystery boxes.
     */
    public static void load() {
        MysteryBox MYSTERY_BOX = new DonatorMysteryBox();

        MYSTERY_BOXES.put(MYSTERY_BOX.mysteryBoxId(), MYSTERY_BOX);
    }

    /**
     * Handles getting the mystery box.
     */
    public static Optional<MysteryBox> getMysteryBox(int item) {
        return MYSTERY_BOXES.containsKey(item) ? Optional.of(MYSTERY_BOXES.get(item)) : Optional.empty();
    }

    /**
     * The name of the mystery box.
     */
    protected abstract String name();

    /**
     * The item identification of the mystery box.
     */
    protected abstract int mysteryBoxId();

    /**
     * Roll chances and return a reward
     */
    public abstract MboxItem rollReward();

    /**
     * Collect mutliple tiers of rares/common arrays into one array
     */
    public abstract MboxItem[] allPossibleRewards();

    public abstract AttributeKey key();
}

package com.aelous.model.content.skill.impl.fishing;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import java.util.*;

/**
 * All tools pertaining to doing the fishing action should be added here for better unity with the system as a whole.
 * It is a huge plus to have definitions for specific tools on its own class versus merging the fishing spot
 * definition and required item together.
 *
 * @author Mack
 * @see FishSpotType
 */
public enum FishingToolType {

    DRAGON_HARPOON(618, 61, 21028, 1.20, new ArrayList<>(List.of(FishSpotType.HARPOON, FishSpotType.HARPOON_SHARK))),
    NONE(-1, -1, -1, 1.0);

    private int animId;
    private int levelRequired;
    private int toolId;
    private double boostMod;
    private ArrayList<FishSpotType> usableSpots;
    private int[] secondaryItems;

    FishingToolType(int animId, int levelRequired, int toolId, double boostMod, ArrayList<FishSpotType> fishingSpots, int... secondaryItems) {
        this.animId = animId;
        this.levelRequired = levelRequired;
        this.toolId = toolId;
        this.boostMod = boostMod;
        this.usableSpots = fishingSpots;
        this.secondaryItems = secondaryItems;
    }

    FishingToolType(int animId, int levelRequired, int toolId, double boostMod) {
        this.animId = animId;
        this.levelRequired = levelRequired;
        this.toolId = toolId;
        this.boostMod = boostMod;
    }

    public int id() {
        return toolId;
    }

    public int animationId() {
        return animId;
    }

    public int levelRequired() {
        return levelRequired;
    }

    public int[] secondarySupplies() {
        return secondaryItems;
    }

    public double boost() {
        return boostMod;
    }

    public static Optional<FishingToolType> locateItemFor(Player player) {
        if (!player.getEquipment().isEmpty() || !player.inventory().isEmpty()) {
            Item item = Arrays.stream(player.inventory().getItems()).filter(Objects::nonNull).filter(i -> getType(i).isPresent() && getType(i).get() != FishingToolType.NONE).findFirst().orElse(null);
            if (item != null)
                return getType(item);
            item = Arrays.stream(player.getEquipment().getItems()).filter(Objects::nonNull).filter(i -> getType(i).isPresent() && getType(i).get() != FishingToolType.NONE).findFirst().orElse(null);
            if (item != null)
                return getType(item);
        }
        return Optional.empty();
    }

    public static Optional<FishingToolType> getType(Item item) {
        return Arrays.stream(FishingToolType.values()).filter(toolType -> item.getId() == toolType.id()).findAny();
    }

    public static boolean canUseOnSpot(FishingToolType tool, FishSpotType spot) {
        return (tool.usableSpots.contains(spot));
    }
}

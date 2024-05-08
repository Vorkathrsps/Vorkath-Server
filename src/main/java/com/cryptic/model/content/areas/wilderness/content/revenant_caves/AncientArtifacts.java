package com.cryptic.model.content.areas.wilderness.content.revenant_caves;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import java.util.ArrayList;

import static com.cryptic.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.cryptic.utility.ItemIdentifiers.COINS_995;

/**
 * It can be given to the Emblem Trader wandering around in the Revenant Caves
 * in exchange for {@code rewardAmount} Coins. Much like the bracelet of
 * ethereum, the emblem is always lost on death, even if it is the only item in
 * the player's inventory.
 * 
 * @author Origin | Zerikoth (PVE) | 23 sep. 2019 : 14:33
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 * @version 1.0
 */
public enum AncientArtifacts {
    
    ANCIENT_EMBLEM(21807,500_000),
    ANCIENT_TOTEM(21810,1_000_000),
    ANCIENT_STATUETTE(21813,1_900_000),
    ANCIENT_MEDALLION(22299,4_000_000),
    ANCIENT_EFFIGY(22302,8_100_000),
    ANCIENT_RELIC(22305,15_900_000);
    
    /**
     * The emblem item
     */
    private final int itemId;
    
    /**
     * The amount of coins is being rewarded
     */
    private final int rewardAmount;
    
    /**
     * The {@code EmblemTrader} constructor
     * 
     * @param itemId       The emblem
     * @param rewardAmount the coins amount we receive
     */
    AncientArtifacts(int itemId, int rewardAmount) {
        this.itemId = itemId;
        this.rewardAmount = rewardAmount;
    }

    public int getItemId() {
        return itemId;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }
    
    /**
     * Exchanges the emblems for coins
     * 
     * @param player The player trying to sell his emblems
     * @param sell   perform the sale don't just price check
     */
    public static int exchange(Player player, boolean sell) {
        ArrayList<AncientArtifacts> list = new ArrayList<>();
        for (AncientArtifacts emblem : AncientArtifacts.values()) {
            if (player.inventory().contains(emblem.getItemId())) {
                list.add(emblem);
            }
        }
        if (list.isEmpty()) return 0;
        int value = 0;
        for (AncientArtifacts emblem : list) {
            int amount = player.inventory().count(emblem.getItemId());
            if (amount > 0) {
                if (sell) {
                    if(!player.inventory().contains(emblem.getItemId())) return 0;
                    player.inventory().remove(emblem.getItemId(), amount);
                    int increase = emblem.getRewardAmount() * amount;
                    player.inventory().addOrDrop(new Item(COINS_995, increase));
                }
                value += (emblem.getRewardAmount() * amount);
            }
        }
        return value;
    }
    
}

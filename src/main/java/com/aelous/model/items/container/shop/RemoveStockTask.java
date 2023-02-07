package com.aelous.model.items.container.shop;

import com.aelous.core.task.Task;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class RemoveStockTask extends Task {

    private final Shop shop;

    public RemoveStockTask(Shop shop) {
        super("RemoveShopStockTask", 100, false);
        this.shop = shop;
    }

    @Override
    protected void execute() {
        boolean cancelTask = true;
        for (Item item : shop.container.getItems()) {
            if (item != null && removeStock(item)) {
                // We had to unstock an item, so don't cancel.
                cancelTask = false;
            }
        }

        if (cancelTask) {
            // No more items to unstock.
            stop();
        }
    }

    //Unused for now.
    private static int restockCalc(int overflow, int curr) {
        int missing = overflow - curr;
        int amount = (int) (missing * 0.3);
        if (amount < 1) {
            amount = 1;
        }
        return amount;
    }

    private boolean removeStock(Item item) {
        //int originalAmount = shop.container.count(item.getId());
        boolean stocksItem = shop.itemCache.containsKey(item.getId());
        if (!stocksItem) {
            shop.container.remove(item);
            for (Player player : shop.players) {
                shop.refresh(player, false);
            }
            return true;
        }
        int originalAmount = shop.itemCache.get(item.getId());
        if (item.getAmount() > originalAmount) {
            //shop.container.remove(item.getId(), 1);
            item.setAmount(item.getAmount() - 1);
            for (Player player : shop.players) {
                shop.refresh(player, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onStop() {
        shop.removeStockTask = null;
    }
}

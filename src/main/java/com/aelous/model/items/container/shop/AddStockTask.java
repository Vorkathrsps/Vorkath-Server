package com.aelous.model.items.container.shop;

import com.aelous.core.task.Task;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

/**
 * A {@link Task} implementation that will restock shop items.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class AddStockTask extends Task {

    /**
     * The shop to restock items for.
     */
    private final Shop shop;


    /**
     * Creates a new {@link AddStockTask}.
     *
     * @param shop The shop to restock items for.
     */
    public AddStockTask(Shop shop) {
        super("AddShopStockTask", 25, false);
        this.shop = shop;
    }

    @Override
    protected void execute() {
        boolean cancelTask = true;
        for (Item item : shop.container.getItems()) {
            if (item != null && restock(item)) {
                // We had to restock an item, so don't cancel.
                cancelTask = false;
            }
        }

        if (cancelTask) {
            // No more items to restock.
            stop();
        }
    }

    @Override
    public void onStop() {
        shop.addStockTask = null;
    }

    /**
     * Restocks the single {@code item} at {@code index}.
     *
     * @param item The item to restock.
     * @return {@code true} if a restock was performed.
     */
    private boolean restock(Item item) {
        boolean stocksItem = shop.itemCache.containsKey(item.getId());
        if (!stocksItem) {
            return false;
        }
        int initialAmount = shop.itemCache.get(item.getId());
        if (item.getAmount() < initialAmount) {
            // Increase by restock amount, to a maximum of the initial amount.
            int newAmount = Math.min( 1 + item.getAmount(), initialAmount);
            item.setAmount(newAmount);
            for (Player player : shop.players) {
                shop.refresh(player, false);
            }
            return true;
        }
        return false;
    }
}

package com.aelous.model.items.container.shop.currency.impl;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.shop.currency.Currency;

/**
 * The currency that provides basic functionality for all tangible currencies.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class ItemCurrency implements Currency {

    /** The item id for this currency. */
    public final int itemId;

    /** Constructs a new {@link ItemCurrency}. */
    public ItemCurrency(int itemId) {
        this.itemId = itemId;
    }
    public int getItemId() {
        return this.itemId;
    }
    @Override
    public boolean tangible() {
        return true;
    }

    @Override
    public boolean takeCurrency(Player player, int amount) {
        return player.inventory().remove(new Item(itemId, amount));
    }

    @Override
    public void recieveCurrency(Player player, int amount) {
        player.inventory().add(new Item(itemId, amount));
    }

    @Override
    public int currencyAmount(Player player, int cost) {
        return player.inventory().count(itemId);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return new Item(itemId).name();
    }
}

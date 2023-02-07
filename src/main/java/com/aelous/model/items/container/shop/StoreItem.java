package com.aelous.model.items.container.shop;

import com.aelous.model.items.Item;
import com.aelous.model.items.container.shop.currency.CurrencyType;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * A simple wrapper class which holds extra attributes for the item object.
 *
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 4-1-2017.
 */
public final class StoreItem extends Item {

    /**
     * The time in minutes an item in the store has to wait before it can be reduced.
     */
    public static final int RESTOCK_RATE = 5;

    /**
     * Gets the optional value for this shop item.
     */
    public OptionalInt value;

    /**
     * Gets the optional currency for this shop item.
     */
    public Optional<CurrencyType> currency;

    /**
     * The time in minutes it takes to restock this store item.
     */
    private int restockTimer = RESTOCK_RATE;

    /**
     * Creates a new {@link Item}.
     */
    public StoreItem(int id, int amount, OptionalInt value, Optional<CurrencyType> currency) {
        super(id, amount);
        this.value = value;
        this.currency = currency;
    }

    public StoreItem(int id, int amount) {
        this(id, amount, OptionalInt.empty(), Optional.empty());
    }

    public int getShopValue() {
        return value.orElse(getValue());
    }

    public int sellValue() {
        return (int) (value.orElse(getValue()) * 0.75);
    }

    public void setShopValue(int value) {
        this.value = OptionalInt.of(value);
    }

    public CurrencyType getShopCurrency(Shop shop) {
        return currency.orElse(shop.currencyType);
    }

    public boolean canReduce() {
        if (--restockTimer <= 0) {
            restockTimer = RESTOCK_RATE;
            return true;
        }
        return false;
    }

    @Override
    public StoreItem copy() {
        return new StoreItem(getId(), getAmount(), value, currency);
    }

    @Override
    public void incrementAmountBy(int amount) {
        super.incrementAmountBy(amount);
    }

    @Override
    public void decrementAmountBy(int amount) {
        super.decrementAmountBy(amount);
    }

    @Override
    public Item createWithId(int newId) {
        Item r = super.createWithId(newId);
        return new StoreItem(r.getId(), r.getAmount());
    }

    @Override
    public Item createWithAmount(int newAmount) {
        Item r = super.createWithAmount(newAmount);
        return new StoreItem(r.getId(), r.getAmount());
    }

    @Override
    public Item createAndIncrement(int addAmount) {
        Item r = super.createAndIncrement(addAmount);
        return new StoreItem(r.getId(), r.getAmount());
    }

    @Override
    public Item createAndDecrement(int removeAmount) {
        Item r = super.createAndDecrement(removeAmount);
        return new StoreItem(r.getId(), r.getAmount());
    }

}

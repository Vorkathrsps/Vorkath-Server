package com.cryptic.model.items.container.shop.currency.impl;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.shop.currency.Currency;

public class VoidIslandPointCurrency implements Currency {
    @Override
    public boolean tangible() {
        return false;
    }

    @Override
    public boolean takeCurrency(Player player, int amount) {
        int voidIslandPointCurrency = player.<Integer>getAttribOr(AttributeKey.VOID_ISLAND_POINTS, 0);
        if (voidIslandPointCurrency >= amount) {
            player.putAttrib(AttributeKey.VOID_ISLAND_POINTS, voidIslandPointCurrency - amount);
            return true;
        } else {
            player.message("You do not have enough Void Island points.");
            return false;
        }
    }

    @Override
    public void recieveCurrency(Player player, int amount) {

    }

    @Override
    public int currencyAmount(Player player, int cost) {
        return player.<Integer>getAttribOr(AttributeKey.VOID_ISLAND_POINTS, 0);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return "Void Island Points";
    }
}

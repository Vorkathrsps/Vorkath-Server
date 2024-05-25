package com.cryptic.model.items.container.shop.currency.impl;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.shop.currency.Currency;

/**
 * @author Origin | June, 10, 2021, 19:10
 * 
 */
public class BossPointsCurrency implements Currency {

    @Override
    public boolean tangible() {
        return false;
    }

    @Override
    public boolean takeCurrency(Player player, int amount) {
        int bossPoints = player.getAttribOr(AttributeKey.BOSS_POINTS, 0);
        if (bossPoints >= amount) {
            player.putAttrib(AttributeKey.BOSS_POINTS, bossPoints - amount);
            return true;
        } else {
            player.message("You do not have enough boss points.");
            return false;
        }
    }

    @Override
    public void recieveCurrency(Player player, int amount) {
        //Empty can't receive currency from shops
    }

    @Override
    public int currencyAmount(Player player, int cost) {
        return player.getAttribOr(AttributeKey.BOSS_POINTS, 0);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return "Boss points";
    }
}

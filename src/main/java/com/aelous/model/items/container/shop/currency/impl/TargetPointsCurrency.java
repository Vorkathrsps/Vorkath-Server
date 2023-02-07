package com.aelous.model.items.container.shop.currency.impl;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.shop.currency.Currency;

/**
 * @author Patrick van Elderen | March, 18, 2021, 14:47
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TargetPointsCurrency implements Currency {

    @Override
    public boolean tangible() {
        return false;
    }

    @Override
    public boolean takeCurrency(Player player, int amount) {
        int targetPoints = player.getAttribOr(AttributeKey.TARGET_POINTS, 0);
        if (targetPoints >= amount) {
            player.putAttrib(AttributeKey.TARGET_POINTS, targetPoints - amount);
            return true;
        } else {
            player.message("You do not have enough vote points.");
            return false;
        }
    }

    @Override
    public void recieveCurrency(Player player, int amount) {
    }

    @Override
    public int currencyAmount(Player player, int cost) {
        return player.getAttribOr(AttributeKey.TARGET_POINTS, 0);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return "Target points";
    }
}

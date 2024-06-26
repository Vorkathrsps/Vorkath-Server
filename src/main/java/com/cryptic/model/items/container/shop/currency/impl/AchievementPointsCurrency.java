package com.cryptic.model.items.container.shop.currency.impl;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.shop.currency.Currency;

public class AchievementPointsCurrency implements Currency {

    @Override
    public boolean tangible() {
        return false;
    }

    @Override
    public boolean takeCurrency(Player player, int amount) {
        int achievementPoints = player.getAttribOr(AttributeKey.ACHIEVEMENT_POINTS, 0);
        if (achievementPoints >= amount) {
            player.putAttrib(AttributeKey.ACHIEVEMENT_POINTS, achievementPoints - amount);
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
        return player.getAttribOr(AttributeKey.ACHIEVEMENT_POINTS, 0);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return "Achievement points";
    }
}

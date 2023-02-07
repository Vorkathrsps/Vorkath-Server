package com.aelous.model.items.container.shop.currency.impl;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.shop.currency.Currency;

public class TournamentPointsCurrency implements Currency {

    @Override
    public boolean tangible() {
        return false;
    }

    @Override
    public boolean takeCurrency(Player player, int amount) {
        int tournamentPoints = player.getAttribOr(AttributeKey.TOURNAMENT_POINTS, 0);
        if (tournamentPoints >= amount) {
            player.putAttrib(AttributeKey.TOURNAMENT_POINTS, tournamentPoints - amount);
            return true;
        } else {
            player.message("You do not have enough tournament points.");
            return false;
        }
    }

    @Override
    public void recieveCurrency(Player player, int amount) {
        //Empty can't receive currency from shops
    }

    @Override
    public int currencyAmount(Player player, int cost) {
        return player.getAttribOr(AttributeKey.TOURNAMENT_POINTS, 0);
    }

    @Override
    public boolean canRecieveCurrency(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return "Tournament points";
    }
}

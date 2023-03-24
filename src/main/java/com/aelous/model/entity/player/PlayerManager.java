package com.aelous.model.entity.player;

import com.aelous.model.content.areas.wilderness.slayer.WildernessSlayerCasket;

/**
 * @Author: Origin
 * @Date: 3/24/2023
 */
public class PlayerManager {
    private Player player;
    private final WildernessSlayerCasket wildernessSlayerCasket = new WildernessSlayerCasket(player);
    public PlayerManager(Player player) {
        this.player = player;
    }
    public WildernessSlayerCasket getWildernessSlayerCasket() {
        return wildernessSlayerCasket;
    }

}

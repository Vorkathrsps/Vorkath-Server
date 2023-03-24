package com.aelous.model.entity.player;

import com.aelous.model.content.areas.wilderness.slayer.WildernessSlayerCasket;

/**
 * @Author: Origin
 * @Date: 3/24/2023
 */
public class PlayerManager extends Player {
    private final WildernessSlayerCasket wildernessSlayerCasket = new WildernessSlayerCasket(this);
    public PlayerManager() {
    }
    public WildernessSlayerCasket getWildernessSlayerCasket() {
        return wildernessSlayerCasket;
    }

}

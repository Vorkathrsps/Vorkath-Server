package com.cryptic.model.content.raids.theatre.controller;

import com.cryptic.model.entity.player.Player;

public interface TheatreDeath {
    void handleRaidDeath(Player player);
    boolean inRaid(Player player);
}

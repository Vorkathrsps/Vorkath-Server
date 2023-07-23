package com.aelous.model.content.raids.theatre.controller;

import com.aelous.model.entity.player.Player;

public interface TheatreDeath {
    void handleRaidDeath(Player player);
    boolean inRaid(Player player);
}

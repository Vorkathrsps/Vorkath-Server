package com.aelous.model.content.raids.theatre.controller;

import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.entity.player.Player;

import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class TheatreController {

    private final List<Raid> raid;

    public TheatreController(List<Raid> raid) {
        this.raid = raid;
    }

    public void build(Player player, TheatreArea theatreArea) {
        for (Raid r : raid) {
            r.buildRaid(player, theatreArea);
            break;
        }
    }
}


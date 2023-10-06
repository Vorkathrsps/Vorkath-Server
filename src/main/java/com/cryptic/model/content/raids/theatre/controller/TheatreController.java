package com.cryptic.model.content.raids.theatre.controller;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.player.Player;

import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class TheatreController {

    private final List<TheatreHandler> npcs;

    public TheatreController(List<TheatreHandler> npcs) {
        super();
        this.npcs = npcs;
    }

    public void build(Player player, TheatreInstance theatreInstance) {
        for (TheatreHandler r : npcs) {
            r.build(player, theatreInstance);
        }
    }

}


package com.cryptic.model.content.raids.theatre.controller;

import com.cryptic.model.content.raids.theatre.Theatre;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.entity.player.Player;

import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class TheatreController {

    private final List<TheatreRaid> theatreRaid;

    public TheatreController(List<TheatreRaid> theatreRaid) {
        super();
        this.theatreRaid = theatreRaid;
    }

    public void build(Player player, Theatre theatre, TheatreArea theatreArea) {
        for (TheatreRaid r : theatreRaid) {
            r.buildRaid(player, theatre, theatreArea);
        }
    }

}


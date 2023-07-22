package com.aelous.model.content.raids.theatre.controller;

import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.stage.TheatrePhase;
import com.aelous.model.content.raids.theatre.stage.TheatreStage;
import com.aelous.model.entity.player.Player;

import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class TheatreController {

    private final List<TheatreRaid> theatreRaid;

    public TheatreController(List<TheatreRaid> theatreRaid) {
        this.theatreRaid = theatreRaid;
    }

    public void build(Player player, Theatre theatre, TheatreArea theatreArea) {
        for (TheatreRaid r : theatreRaid) {
            r.buildRaid(player, theatre, theatreArea);
        }
    }

}


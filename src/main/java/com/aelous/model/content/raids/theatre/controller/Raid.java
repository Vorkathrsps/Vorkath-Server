package com.aelous.model.content.raids.theatre.controller;

import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.entity.player.Player;

public interface Raid {

    void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea);

}

package com.aelous.model.content.raids.theatre.boss.xarpus;

import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.xarpus.handler.XarpusProcess;
import com.aelous.model.content.raids.theatre.controller.TheatreRaid;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Xarpus implements TheatreRaid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        XarpusProcess xarpus = (XarpusProcess) new XarpusProcess(10767, new Tile(3169, 4386, theatreArea.getzLevel() + 1), player).spawn(false);
        xarpus.setInstance(theatreArea);
        xarpus.spawn(false);
    }

}

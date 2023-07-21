package com.aelous.model.content.raids.theatre.boss.xarpus;

import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.xarpus.handler.XarpusProcess;
import com.aelous.model.content.raids.theatre.controller.Raid;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Xarpus implements Raid {
    @Override
    public void buildRaid(Player player, TheatreArea theatreArea) {
        XarpusProcess xarpus = (XarpusProcess) new XarpusProcess(10767, new Tile(3169, 4386, theatreArea.getzLevel()), player).spawn(false);
        xarpus.setInstance(theatreArea);
        xarpus.spawn(false);
    }

}

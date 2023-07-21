package com.aelous.model.content.raids.theatre.boss.bloat;

import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.bloat.handler.BloatProcess;
import com.aelous.model.content.raids.theatre.controller.Raid;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Bloat implements Raid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        BloatProcess bloat = (BloatProcess) new BloatProcess(8359, new Tile(3299, 4440, theatreArea.getzLevel()), player).spawn(false);
        bloat.setInstance(theatreArea);
    }

}

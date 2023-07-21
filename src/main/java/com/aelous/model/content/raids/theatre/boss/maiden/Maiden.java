package com.aelous.model.content.raids.theatre.boss.maiden;

import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.maiden.handler.MaidenProcess;
import com.aelous.model.content.raids.theatre.controller.Raid;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Maiden implements Raid {
    @Override
    public void buildRaid(Player player, TheatreArea theatreArea) {
        MaidenProcess maiden = (MaidenProcess) new MaidenProcess(8360, new Tile(3162, 4444, theatreArea.getzLevel()), player).spawn(false);
        maiden.setInstance(theatreArea);
    }

}

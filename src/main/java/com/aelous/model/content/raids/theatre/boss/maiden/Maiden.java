package com.aelous.model.content.raids.theatre.boss.maiden;

import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.maiden.handler.MaidenProcess;
import com.aelous.model.content.raids.theatre.controller.Raid;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Maiden implements Raid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        MaidenProcess maiden = (MaidenProcess) new MaidenProcess(10814, new Tile(3162, 4444, theatreArea.getzLevel()), player, theatre, theatreArea).spawn(false);
        maiden.setInstance(theatreArea);
    }

}

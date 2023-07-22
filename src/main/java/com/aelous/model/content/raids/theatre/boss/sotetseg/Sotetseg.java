package com.aelous.model.content.raids.theatre.boss.sotetseg;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.sotetseg.handler.SotetsegProcess;
import com.aelous.model.content.raids.theatre.controller.TheatreRaid;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Sotetseg implements TheatreRaid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        SotetsegProcess sotetsegProcess = (SotetsegProcess) new SotetsegProcess(NpcIdentifiers.SOTETSEG_10865, new Tile(3277, 4326, theatreArea.getzLevel()), player, theatre, theatreArea).spawn(false);
        sotetsegProcess.setInstance(theatreArea);
    }
}

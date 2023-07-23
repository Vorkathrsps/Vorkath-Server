package com.aelous.model.content.raids.theatre.boss.sotetseg;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.sotetseg.handler.SotetsegProcess;
import com.aelous.model.content.raids.theatre.controller.TheatreRaid;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Sotetseg implements TheatreRaid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        SotetsegProcess sotetsegProcess = (SotetsegProcess) new SotetsegProcess(NpcIdentifiers.SOTETSEG_10865, new Tile(3277, 4326, theatreArea.getzLevel()), player, theatre, theatreArea).spawn(false);
        sotetsegProcess.setHitpoints(this.scale(sotetsegProcess, theatre));
        sotetsegProcess.setInstance(theatreArea);
    }

    @Override
    public int scale(NPC npc, Theatre theatre) {
        int scaledHitpoints;

        if (theatre.getParty().size() <= 3) {
            scaledHitpoints = (int) (npc.hp() * 0.75);
        } else if (theatre.getParty().size() == 4) {
            scaledHitpoints = (int) (npc.hp() * 0.875);
        } else {
            scaledHitpoints = npc.hp();
        }
        return scaledHitpoints;
    }

}

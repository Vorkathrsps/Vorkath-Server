package com.cryptic.model.content.raids.theatre.boss.sotetseg;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.raids.theatre.Theatre;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.content.raids.theatre.boss.sotetseg.handler.SotetsegProcess;
import com.cryptic.model.content.raids.theatre.controller.TheatreRaid;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class Sotetseg implements TheatreRaid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        SotetsegProcess sotetsegProcess = (SotetsegProcess) new SotetsegProcess(NpcIdentifiers.SOTETSEG_10865, new Tile(3277, 4326, theatreArea.getzLevel()), player, theatre, theatreArea).spawn(false);
        sotetsegProcess.setHitpoints(this.scale(sotetsegProcess, player));
        sotetsegProcess.setInstance(theatreArea);
    }

    @Override
    public int scale(NPC npc, Player player) {
        int scaledHitpoints;

        if (player.getTheatreParty().getParty().size() <= 3) {
            scaledHitpoints = (int) (npc.hp() * 0.75);
        } else if (player.getTheatreParty().getParty().size() == 4) {
            scaledHitpoints = (int) (npc.hp() * 0.875);
        } else {
            scaledHitpoints = npc.hp();
        }
        return scaledHitpoints;
    }

}

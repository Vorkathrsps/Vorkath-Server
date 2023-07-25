package com.aelous.model.content.raids.theatre.boss.bloat;

import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.bloat.handler.BloatProcess;
import com.aelous.model.content.raids.theatre.controller.TheatreRaid;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class Bloat implements TheatreRaid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        BloatProcess bloat = (BloatProcess) new BloatProcess(8359, new Tile(3299, 4440, theatreArea.getzLevel()), player, theatre, theatreArea).spawn(false);
        bloat.setHitpoints(this.scale(bloat, player));
        bloat.setInstance(theatreArea);
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

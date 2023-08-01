package com.cryptic.model.content.raids.theatre.boss.bloat;

import com.cryptic.model.content.raids.theatre.Theatre;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.content.raids.theatre.boss.bloat.handler.BloatProcess;
import com.cryptic.model.content.raids.theatre.controller.TheatreRaid;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

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

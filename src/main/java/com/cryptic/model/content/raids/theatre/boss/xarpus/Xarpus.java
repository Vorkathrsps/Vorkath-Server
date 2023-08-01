package com.cryptic.model.content.raids.theatre.boss.xarpus;

import com.cryptic.model.content.raids.theatre.Theatre;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.content.raids.theatre.boss.xarpus.handler.XarpusProcess;
import com.cryptic.model.content.raids.theatre.controller.TheatreRaid;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class Xarpus implements TheatreRaid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        XarpusProcess xarpus = (XarpusProcess) new XarpusProcess(10767, new Tile(3169, 4386, theatreArea.getzLevel() + 1), player).spawn(false);
        xarpus.setHitpoints(this.scale(xarpus, player));
        xarpus.setInstance(theatreArea);
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

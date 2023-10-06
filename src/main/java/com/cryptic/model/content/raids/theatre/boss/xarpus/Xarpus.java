package com.cryptic.model.content.raids.theatre.boss.xarpus;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.xarpus.handler.XarpusProcess;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class Xarpus implements TheatreHandler {
    @Override
    public void build(Player player, TheatreInstance theatreInstance) {
        XarpusProcess xarpus = (XarpusProcess) new XarpusProcess(10767, new Tile(3169, 4386, theatreInstance.getzLevel() + 1), player).spawn(false);
        xarpus.setHitpoints(this.scale(xarpus, player));
        xarpus.setInstance(theatreInstance);
    }

    @Override
    public int scale(NPC npc, Player player) {
        int scaledHitpoints;

        if (player.getTheatreInstance().getPlayers().size() <= 3) {
            scaledHitpoints = (int) (npc.hp() * 0.75);
        } else if (player.getTheatreInstance().getPlayers().size() == 4) {
            scaledHitpoints = (int) (npc.hp() * 0.875);
        } else {
            scaledHitpoints = npc.hp();
        }
        return scaledHitpoints;
    }

}

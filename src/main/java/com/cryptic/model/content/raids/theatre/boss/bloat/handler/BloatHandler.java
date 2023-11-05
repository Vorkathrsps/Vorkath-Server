package com.cryptic.model.content.raids.theatre.boss.bloat.handler;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.bloat.Bloat;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class BloatHandler implements TheatreHandler {
    @Override
    public void build(Player player, TheatreInstance theatreInstance) {
        Bloat bloat = new Bloat(8359, new Tile(3299, 4440, theatreInstance.getzLevel()), player, theatreInstance);
        bloat.setHitpoints(this.scale(bloat, player, false));
        bloat.setInstancedArea(theatreInstance);
        bloat.spawn(false);
    }

    @Override
    public int scale(NPC npc, Player player, boolean hardMode) {
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

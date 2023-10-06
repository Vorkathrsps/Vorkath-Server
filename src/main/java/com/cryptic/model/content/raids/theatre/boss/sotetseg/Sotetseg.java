package com.cryptic.model.content.raids.theatre.boss.sotetseg;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.sotetseg.handler.SotetsegProcess;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class Sotetseg implements TheatreHandler {
    @Override
    public void build(Player player, TheatreInstance theatreInstance) {
        SotetsegProcess sotetseg = (SotetsegProcess) new SotetsegProcess(NpcIdentifiers.SOTETSEG_10865, new Tile(3277, 4326, theatreInstance.getzLevel()), player, theatreInstance).spawn(false);
        sotetseg.setHitpoints(this.scale(sotetseg, player));
        sotetseg.setInstance(theatreInstance);
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

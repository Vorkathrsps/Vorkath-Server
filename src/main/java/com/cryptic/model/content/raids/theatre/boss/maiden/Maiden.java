package com.cryptic.model.content.raids.theatre.boss.maiden;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.maiden.handler.MaidenProcess;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class Maiden implements TheatreHandler {
    @Override
    public void build(Player player, TheatreInstance theatreInstance) {
        MaidenProcess maiden = (MaidenProcess) new MaidenProcess(10814, new Tile(3162, 4444, theatreInstance.getzLevel()), player, theatreInstance).spawn(false);
        maiden.setHitpoints(this.scale(maiden, player));
        maiden.setInstance(theatreInstance);
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

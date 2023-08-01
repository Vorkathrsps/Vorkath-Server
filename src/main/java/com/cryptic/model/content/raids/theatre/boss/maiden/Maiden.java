package com.cryptic.model.content.raids.theatre.boss.maiden;

import com.cryptic.model.content.raids.theatre.Theatre;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.content.raids.theatre.boss.maiden.handler.MaidenProcess;
import com.cryptic.model.content.raids.theatre.controller.TheatreRaid;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class Maiden implements TheatreRaid {
    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        MaidenProcess maiden = (MaidenProcess) new MaidenProcess(10814, new Tile(3162, 4444, theatreArea.getzLevel()), player, theatre, theatreArea).spawn(false);
        maiden.setHitpoints(this.scale(maiden, player));
        maiden.setInstance(theatreArea);
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

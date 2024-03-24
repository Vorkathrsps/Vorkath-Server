package com.cryptic.model.content.raids.theatre.boss.sotetseg.handler;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.sotetseg.Sotetseg;
import com.cryptic.model.content.raids.theatre.controller.RaidBuilder;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class SotetsegBuilder implements RaidBuilder {
    @Override
    public void build(Player player, InstancedArea instance) {
        if (instance instanceof TheatreInstance theatreInstance) {
            Sotetseg sotetseg = new Sotetseg(NpcIdentifiers.SOTETSEG_10865, new Tile(3277, 4326, theatreInstance.getzLevel()), theatreInstance);
            sotetseg.setHitpoints(this.scale(sotetseg, player, false));
            sotetseg.setInstancedArea(theatreInstance);
            sotetseg.spawn(false);
        }
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

package com.cryptic.model.content.raids.theatre.boss.verzik.pillars;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

public class Pillar extends NPC {
    @Getter TheatreInstance theatreInstance;
    public Pillar(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
    }
    @Override
    public void die() {
        this.getTheatreInstance().getVerzikPillarNpcs().remove(this);
        var players = this.getTheatreInstance().getPlayers();
        var objects = Lists.newArrayList(this.getTheatreInstance().getVerzikPillarObjects().iterator());
        for (var o : objects) {
            if (o == null) continue;
            if (!o.tile().isWithinDistance(this.tile(), 1)) continue;
            if (o.getId() == 32687) {
                o.setId(32688);
                Chain.noCtx().delay(1, () -> {
                    for (Player player : players) {
                        if (player == null) continue;
                        if (!player.tile().isWithinDistance(this.tile(), 1) && !o.tile().isWithinDistance(player.tile(), 1)) continue;
                        player.hit(this, 10);
                        Direction direction = Direction.SOUTH;
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(direction.x(), direction.y()), 30, 60, 1114, 0);
                        player.setForceMovement(forceMovement);
                    }
                }).then(2, () -> o.setId(32689)).then(2, () -> o.animate(8104)).then(2, o::remove);
            }
        }
    }
}

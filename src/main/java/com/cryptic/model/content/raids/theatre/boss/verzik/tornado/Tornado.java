package com.cryptic.model.content.raids.theatre.boss.verzik.tornado;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;

public class Tornado extends NPC {
    TheatreInstance theatreInstance;
    public Tornado(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.noRetaliation(true);
        this.setIgnoreOccupiedTiles(true);
        this.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 40);
    }
}

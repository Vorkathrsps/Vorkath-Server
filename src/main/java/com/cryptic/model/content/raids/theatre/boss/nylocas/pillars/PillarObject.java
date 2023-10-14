package com.cryptic.model.content.raids.theatre.boss.nylocas.pillars;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class PillarObject extends GameObject {
    TheatreInstance theatreInstance;
    public PillarObject(int id, Tile tile, int type, int rotation, TheatreInstance theatreInstance) {
        super(id, tile, type, rotation);
        this.theatreInstance = theatreInstance;
        theatreInstance.getPillarObject().add(this);
    }

}

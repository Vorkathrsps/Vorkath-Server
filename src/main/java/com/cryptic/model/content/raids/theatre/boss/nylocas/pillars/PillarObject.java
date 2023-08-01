package com.cryptic.model.content.raids.theatre.boss.nylocas.pillars;

import com.cryptic.model.content.raids.theatre.boss.nylocas.Vasilias;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class PillarObject extends GameObject {
    Vasilias vasilias;
    public PillarObject(int id, Tile tile, int type, int rotation, Vasilias vasilias) {
        super(id, tile, type, rotation);
        this.vasilias = vasilias;
        vasilias.pillarObject.add(this);
    }

}

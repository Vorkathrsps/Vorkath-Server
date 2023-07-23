package com.aelous.model.content.raids.theatre.boss.nylocas.pillars;

import com.aelous.model.content.raids.theatre.boss.nylocas.Vasilias;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;

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

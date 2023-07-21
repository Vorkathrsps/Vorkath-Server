package com.aelous.model.content.raids.theatre.boss.nylocas.pillars;

import com.aelous.model.content.raids.theatre.boss.nylocas.VasiliasHandler;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class PillarObject extends GameObject {
    VasiliasHandler vasiliasHandler;
    public PillarObject(int id, Tile tile, int type, int rotation, VasiliasHandler vasiliasHandler) {
        super(id, tile, type, rotation);
        this.vasiliasHandler = vasiliasHandler;
        vasiliasHandler.pillarObject.add(this);
    }

}

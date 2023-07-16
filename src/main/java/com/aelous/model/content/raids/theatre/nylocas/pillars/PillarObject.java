package com.aelous.model.content.raids.theatre.nylocas.pillars;

import com.aelous.model.content.raids.theatre.nylocas.VasiliasListener;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class PillarObject extends GameObject {
    VasiliasListener vasiliasListener;
    public PillarObject(int id, Tile tile, int type, int rotation, VasiliasListener vasiliasListener) {
        super(id, tile, type, rotation);
        this.vasiliasListener = vasiliasListener;
        vasiliasListener.pillarObject.add(this);
    }

}

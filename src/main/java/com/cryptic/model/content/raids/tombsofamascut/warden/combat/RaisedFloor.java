package com.cryptic.model.content.raids.tombsofamascut.warden.combat;

import com.cryptic.model.map.position.Tile;

public class RaisedFloor extends Tile {
    final int delay;

    public RaisedFloor(int x, int y, int level, int delay) {
        super(x, y, level);
        this.delay = delay;
    }

}

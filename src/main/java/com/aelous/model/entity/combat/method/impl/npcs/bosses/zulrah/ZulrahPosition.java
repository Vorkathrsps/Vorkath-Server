package com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah;

import com.aelous.model.map.position.Tile;

/**
 * Created by Bart on 3/6/2016.
 *
 * Represents a possible tile where Zulrah may find itself positioned.
 */
public enum ZulrahPosition {

    CENTER(new Tile(0, 0), new Tile(0, -10)),

    WEST(new Tile(-10, 0), new Tile(10, 0)),

    SOUTH(new Tile(0, -10), new Tile(0, 10)),

    EAST(new Tile(10, 10), new Tile(-10, 0));

    ZulrahPosition(Tile tile, Tile direction) {
        this.tile = tile;
        this.direction = direction;
    }

    private Tile tile, direction;

    public Tile getTile() {
        return tile;
    }

    public Tile getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "ZulrahPosition{" +
            "position=" + tile +
            ", direction=" + direction +
            '}';
    }
}

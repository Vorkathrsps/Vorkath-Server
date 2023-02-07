package com.aelous.utility.loaders;

import com.aelous.model.map.position.Tile;

/**
 * Represents the definition of an object spawn.
 * @author Professor Oak
 *
 */
public class ObjectSpawnDefinition {

    private int face = 0;
    private int type = 10;
    private int id;
    private Tile tile;
    private boolean enabled = true;
    public boolean PVPWorldExclusive = false;
    public boolean economyExclusive = false;

    public void setFace(int face) {
        this.face = face;
    }

    public int getFace() {
        return face;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

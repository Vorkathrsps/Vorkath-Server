package com.aelous;

import com.aelous.model.map.position.Tile;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class PlainTile {
    public final int x, y, z;

    public PlainTile(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Tile tile() {
        return new Tile(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainTile plainTile = (PlainTile) o;
        return x == plainTile.x && y == plainTile.y && z == plainTile.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

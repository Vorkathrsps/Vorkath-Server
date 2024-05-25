package com.cryptic.model.map.position;

public class Coordinates {
    final int bitpack;
    Tile tile = Tile.of(0,0,0);
    public Coordinates(int bitpack) {
        this.bitpack = bitpack;
    }
}

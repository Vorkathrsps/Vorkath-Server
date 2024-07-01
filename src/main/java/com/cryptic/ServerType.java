package com.cryptic;

import com.cryptic.model.map.position.Tile;
import lombok.Getter;

@Getter
public enum ServerType {
    VORKATH("Vorkath",new Tile(3092, 3501, 0),new Tile(3104, 3509)),
    VARLAMORE("Varlamore",new Tile(1647, 3108, 0),new Tile(3104, 3509));

    ServerType(String name,Tile homeTile, Tile crashedStarLocation) {
        this.name = name;
        this.homeTile = homeTile;
        this.crashedStarLocation = crashedStarLocation;
    }

    public final String name;

    private final Tile homeTile;

    private final Tile crashedStarLocation;

}

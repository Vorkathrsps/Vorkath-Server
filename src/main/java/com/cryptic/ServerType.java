package com.cryptic;

import com.cryptic.model.map.position.Tile;
import lombok.Getter;

@Getter
public enum ServerType {
    VORKATH("Vorkath",new Tile(3092, 3501, 0)),
    VARLAMORE("Varlamore",new Tile(1647, 3108, 0));

    ServerType(String name,Tile homeTile) {
        this.name = name;
        this.homeTile = homeTile;
    }

    public final String name;

    private final Tile homeTile;

}

package com.cryptic.model.content.teleport.newinterface;

import com.cryptic.model.map.position.Tile;

public class SpecificTeleport {

    int button;

    Tile tile;
    String text;
    String description;
    boolean favorited;

    int favoritebutton;
    public SpecificTeleport(int button, Tile tile, String text, String description, boolean favorited, int favoritebutton){
        this.button = button;
        this.tile = tile;
        this.text = text;
        this.description = description;
        this.favorited = favorited;
        this.favoritebutton = favoritebutton;
    }
}

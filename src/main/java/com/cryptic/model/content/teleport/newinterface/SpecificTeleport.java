package com.cryptic.model.content.teleport.newinterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpecificTeleport {

    int button;
    int tile;
    String text;
    String description;
    boolean favorited;
    int favoritebutton;

    @Override
    public String toString() {
        return "SpecificTeleport{" +
            "button=" + button +
            ", tile=" + tile +
            ", text='" + text + '\'' +
            ", description='" + description + '\'' +
            ", favorited=" + favorited +
            ", favoritebutton=" + favoritebutton +
            '}';
    }
}

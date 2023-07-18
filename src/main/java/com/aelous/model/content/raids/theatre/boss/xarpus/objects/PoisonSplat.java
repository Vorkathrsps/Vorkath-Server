package com.aelous.model.content.raids.theatre.boss.xarpus.objects;

import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PoisonSplat extends GameObject {
    @Getter List<GameObject> splat = new ArrayList<>();
    public PoisonSplat(int id, Tile tile) {
        super(id, tile);
        addToList();
    }

    public boolean addToList() {
        return splat.add(this);
    }

    public boolean removeFromList() {
        return splat.remove(this);
    }

    public void clearList() {
        splat.clear();
    }

}

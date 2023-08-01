package com.cryptic.model.content.minigames.impl.tempoross.skilling;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;

import java.util.ArrayList;
import java.util.List;

public class FishingSpots extends NPC {

    public static final Tile[] tiles = new Tile[]
        {
            new Tile(3037, 2996),
            new Tile(3051, 2995),
            new Tile(3048, 3002),
            new Tile(3036, 2963),
            new Tile(3048, 2957),
            new Tile(3046, 2954)
        };

    public static final List<NPC> fishingSpots = new ArrayList<>();

    public FishingSpots(int id, Tile tile) {
        super(id, tile);
        this.respawns(true);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        fishingSpots.add(this);
    }

    public void spawnSpots() {
        this.spawn(true);
    }

    public void clearSpawns() {
        for (var o : fishingSpots) {
            o.remove();
        }
        fishingSpots.clear();
    }

}

package com.aelous.utility;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;

/**
 * @author Simplemind
 */
public class LocationUtilities {

    public static ArrayList<NPC> copyNpcSpawnsFrom(World world, Tile from, int radius) {
        ArrayList<NPC> copy = new ArrayList<>();
        world.getNpcs().forEach(npc -> {
            if (from.area(radius).contains(npc.spawnTile())) {
                copy.add(npc);
            }
        });

        return copy;
    }

}

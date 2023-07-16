package com.aelous.model.content.raids.theatre.nylocas;

import com.aelous.model.World;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import java.util.Random;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

public class VasiliasProcess {
    int[] npcs = new int[]{NYLOCAS_ISCHYROS_8342,NYLOCAS_TOXOBOLOS_8343,NYLOCAS_HAGIOS};
    int wave = 0;

    public VasiliasProcess() {

    }
    @Getter private static final Tile[] toSpawn = new Tile[]{
        new Tile(3282, 4249),
        new Tile(3295, 4235),
        new Tile(3309, 4248),
        new Tile(3282, 4248),
        new Tile(3296, 4235),
        new Tile(3309, 4249)
    };

    public Tile getRandomTile() {
        Tile[] tileArray = toSpawn;
        if (tileArray.length == 0) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(tileArray.length);
        return tileArray[randomIndex].transform(0, 0, 0);
    }
    public int getRandomNPC() {
        Random random = new Random();
        int randomIndex = random.nextInt(npcs.length);
        return npcs[randomIndex];
    }

    public void startSpiderSpawnTask() {
        Chain.noCtxRepeat().repeatingTask(20, t -> {
            VasiliasLogic vasilias = new VasiliasLogic(getRandomNPC(), getRandomTile());
            vasilias.spawn(false);
            World.getWorld().registerNpc(vasilias);
            wave++;
        });
    }

}

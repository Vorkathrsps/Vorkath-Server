package com.aelous.model.content.raids.theatre.nylocas;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.controller.TheatreController;
import com.aelous.model.content.raids.theatre.nylocas.handler.VasiliasNpcHandler;
import com.aelous.model.content.raids.theatre.nylocas.pillars.PillarNpc;
import com.aelous.model.content.raids.theatre.nylocas.pillars.PillarObject;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class VasiliasListener extends TheatreController {
    public List<NPC> pillarNpc = new ArrayList<>();
    public List<NPC> vasiliasNpc = new ArrayList<>();
    public List<GameObject> pillarObject = new ArrayList<>();
    @Nonnull Player player;
    int[] npcs = new int[]{NYLOCAS_ISCHYROS_8342, NYLOCAS_TOXOBOLOS_8343, NYLOCAS_HAGIOS};
    AtomicInteger wave = new AtomicInteger();
    @Getter int finalInterpolatedTransmog;

    public VasiliasListener() {

    }

    public VasiliasListener(@NotNull Player player) {
        this.player = player;
    }

    @Getter
    private static final Tile[] fromTile = new Tile[]{ // these are spawn points?
        new Tile(3282, 4249),
        new Tile(3295, 4235),
        new Tile(3309, 4248),
        new Tile(3282, 4248),
        new Tile(3296, 4235),
        new Tile(3309, 4249)
    };

    public void clearListener() {
        for (var v : vasiliasNpc) {
            v.die();
        }
        vasiliasNpc.clear();
        for (var n : pillarNpc) {
            n.remove();
        }
        pillarNpc.clear();
        for (var o : pillarObject) {
            o.remove();
        }
        pillarObject.clear();
    }

    public Tile getRandomTile() {
        Tile[] tileArray = fromTile;
        if (tileArray.length == 0) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(tileArray.length);
        return tileArray[randomIndex].transform(0, 0, 0);
    }

    public int getRandomNPC() {
        Random random = new Random();
        finalInterpolatedTransmog = random.nextInt(npcs.length);
        return npcs[finalInterpolatedTransmog];
    }

    public void startSpiderSpawnTask() {
        Chain.noCtxRepeat().repeatingTask(5, t -> {
            VasiliasNpcHandler vasilias = new VasiliasNpcHandler(getRandomNPC(), getRandomTile(), this, player);
            vasilias.spawn(false);
            World.getWorld().registerNpc(vasilias);
            if (this.wave.get() == 60) {
                Vasilias boss = new Vasilias(8355, new Tile(3294, 4247, 0), player);
                boss.spawn(false);
                World.getWorld().registerNpc(boss);
                this.wave.getAndSet(0);
                t.stop();
                return;
            }
            this.wave.getAndIncrement();
        });
    }

    @Override
    public void buildRoom() {
        PillarNpc pillarNpc1 = new PillarNpc(8358, new Tile(3290, 4252, player.getZ()), new PillarObject(32862, new Tile(3289, 4253, player.getZ()), 10, 1, this), this);
        PillarNpc pillarNpc2 = new PillarNpc(8358, new Tile(3299, 4252, player.getZ()), new PillarObject(32862, new Tile(3300, 4253, player.getZ()), 10, 2, this), this);
        PillarNpc pillarNpc3 = new PillarNpc(8358, new Tile(3299, 4243, player.getZ()), new PillarObject(32862, new Tile(3300, 4242, player.getZ()), 10, 3, this), this);
        PillarNpc pillarNpc4 = new PillarNpc(8358, new Tile(3290, 4243, player.getZ()), new PillarObject(32862, new Tile(3289, 4242, player.getZ()), 10, 0, this), this);
        pillarNpc1.spawn(false);
        pillarNpc1.spawnPillarObject();
        pillarNpc2.spawn(false);
        pillarNpc2.spawnPillarObject();
        pillarNpc3.spawn(false);
        pillarNpc3.spawnPillarObject();
        pillarNpc4.spawn(false);
        pillarNpc4.spawnPillarObject();
    }

    @Override
    public void initiate() {
        startSpiderSpawnTask();
    }

    @Override
    public void clearRoom() {
        clearListener();
    }
}

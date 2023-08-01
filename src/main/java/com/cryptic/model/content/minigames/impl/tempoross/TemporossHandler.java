package com.cryptic.model.content.minigames.impl.tempoross;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.minigames.impl.tempoross.area.TemporossArea;
import com.cryptic.model.content.minigames.impl.tempoross.skilling.FishingSpots;
import com.cryptic.model.content.minigames.impl.tempoross.process.Tempoross;
import com.cryptic.model.content.minigames.impl.tempoross.skilling.SpiritPool;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/19/2023
 */
public class TemporossHandler {
    Player player;
    @Getter TemporossArea temporossArea;
    @Nonnull Tempoross boss;
    @Nonnull public static SpiritPool pool_one, pool_two;
    Tile entrance = new Tile(3047, 2992);
    public static final List<NPC> bossList = new ArrayList<>();

    public TemporossHandler(@Nonnull Player player, TemporossArea temporossArea) {
        this.player = player;
        this.temporossArea = temporossArea;
    }

    public void startInstance() {
        boss = new Tempoross(NpcIdentifiers.TEMPOROSS_10574, new Tile(3043, 2973, temporossArea.getzLevel()), player);
        pool_one = new SpiritPool(NpcIdentifiers.SPIRIT_POOL, new Tile(3046, 2981, temporossArea.getzLevel()), player);
        pool_two = new SpiritPool(NpcIdentifiers.SPIRIT_POOL, new Tile(3046, 2971, temporossArea.getzLevel()), player);
        temporossArea.addNpc(pool_one);
        temporossArea.addNpc(pool_two);
        temporossArea.addNpc(boss);
        temporossArea.addPlayer(this.player);
        player.teleport(entrance.transform(0,0,temporossArea.getzLevel()));
        boss.spawn(false);
        pool_one.spawn(false);
        pool_two.spawn(false);
        spawnFishingSpots(temporossArea);
        player.setInstance(temporossArea);
        boss.setInstance(temporossArea);
        pool_one.setInstance(temporossArea);
        pool_two.setInstance(temporossArea);
        bossList.add(boss);
        bossList.add(pool_one);
        bossList.add(pool_two);
    }

    public void spawnFishingSpots(TemporossArea temporossArea) {
        for (var tiles : FishingSpots.tiles) {
            tiles = tiles.transform(0, 0, temporossArea.getzLevel());
            FishingSpots fishingSpots = new FishingSpots(10568, tiles);
            fishingSpots.spawnSpots();
        }
    }
}

package com.cryptic.model.content.areas.wilderness.content.boss_event;

import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.cryptic.model.content.collection_logs.LogType.BOSSES;
import static com.cryptic.utility.CustomNpcIdentifiers.BRUTAL_LAVA_DRAGON_FLYING;
import static com.cryptic.utility.CustomNpcIdentifiers.CORRUPTED_NECHRYARCH;
import static com.cryptic.utility.ItemIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Origin
 * april 03, 2020
 */
public class WildernessBossEvent {

    private static final Logger logger = LogManager.getLogger(WildernessBossEvent.class);

    @Getter
    private static final WildernessBossEvent INSTANCE = new WildernessBossEvent();

    /**
     * An array of possible boss spawns. Chosen at random when a boss spawns.
     */
    private static final Tile[] POSSIBLE_SPAWNS = {
        new Tile(3182, 3790),//east of chins
        new Tile(3304, 3898),//gdz
        new Tile(3307, 3934),//52s
        new Tile(3219, 3661),//east of graves
    };

    public static Tile currentSpawnPos;

    /**
     * The interval at which server-wide Wilderness events occur.
     * Whilst in production mode every hour otherwise every 30 seconds.
     */
    public static final int BOSS_EVENT_INTERVAL = 6000;

    /**
     * The active event being ran in the Wilderness.
     */
    private BossEvent activeEvent = BossEvent.NOTHING;

    /**
     * The rotation of events, executed in sequence.
     */
    private static final BossEvent[] EVENT_ROTATION = {BossEvent.REVENANT_MALEDICTUS};

    public static boolean ANNOUNCE_5_MIN_TIMER = false;

    /**
     * The NPC reference for the active event.
     */
    @Getter
    private Optional<NPC> activeNpc = Optional.empty();

    public void bossDeath(Entity entity) {
        entity.getCombat().getDamageMap().forEach((key, hits) -> {
            Player player = (Player) key;
            player.message(Color.RED.wrap("You've dealt " + hits.getDamage() + " damage to the world boss!"));
            // Only people nearby are rewarded. This is to avoid people 'poking' the boss to do some damage
            // without really risking being there.
            if (entity.tile().isWithinDistance(player.tile(), 10) && hits.getDamage() >= 1) {
                NPC npc = null;
                if (activeNpc.isPresent()) {
                    npc = activeNpc.get();
                }

                if (npc == null) {
                    return;
                }

                //Always drops
                if (npc.id() == SKOTIZO || npc.id() == TEKTON_7542) {
                    GroundItemHandler.createGroundItem(new GroundItem(new Item(ASHES), npc.tile(), player));
                }

                if (npc.id() == CORRUPTED_NECHRYARCH) {
                    GroundItemHandler.createGroundItem(new GroundItem(new Item(ASHES), npc.tile(), player));
                }

                if (npc.id() == BRUTAL_LAVA_DRAGON_FLYING) {
                    GroundItemHandler.createGroundItem(new GroundItem(new Item(LAVA_DRAGON_BONES), npc.tile(), player));
                }

                //Always drop random BM
                GroundItemHandler.createGroundItem(new GroundItem(new Item(BLOOD_MONEY, World.getWorld().random(10_000, 15_000)), npc.tile(), player));

                //Always log kill timers
                player.getBossTimers().submit(npc.def().name, (int) player.getCombat().getFightTimer().elapsed(TimeUnit.SECONDS), player);

                //Always increase kill counts
                player.getBossKillLog().addKill(npc);


                //Random drop from the table
                ScalarLootTable table = ScalarLootTable.forNPC(npc.id());
                if (table != null) {
                    Item reward = table.randomItem(World.getWorld().random());
                    if (reward != null) {
                        player.message("You received a drop roll from the table for dealing more then 100 damage!");
                        //System.out.println("Drop roll for "+player.getUsername()+" for killing world boss "+npc.def().name);

                        // bosses, find npc ID, find item ID
                        BOSSES.log(player, npc.id(), reward);

                        //Niffler doesn't loot world boss loot
                        GroundItemHandler.createGroundItem(new GroundItem(reward, npc.tile(), player));

                        Utils.sendDiscordInfoLog("Player " + player.getUsername() + " got drop item " + reward.toString(), "npcdrops");
                    }
                }
            }
        });

        //Dissmiss broadcast when boss has been killed.
        World.getWorld().clearBroadcast();
        World.getWorld().sendWorldMessage("<col=6a1a18><img=2013> " + activeEvent.description + " has been killed. It will respawn shortly.");
    }

    public LocalDateTime last = LocalDateTime.now().minus((long) (BOSS_EVENT_INTERVAL * 0.6d), ChronoUnit.SECONDS);
    public LocalDateTime next = LocalDateTime.now().plus((long) (BOSS_EVENT_INTERVAL * 0.6d), ChronoUnit.SECONDS);

    public static void onServerStart() {
        // every 60 mins
        TaskManager.submit(new WildernessBossEventTask());
    }

    public String timeTill(boolean displaySeconds) {
        LocalDateTime now = LocalDateTime.now();
        long difference = now.until(next, ChronoUnit.SECONDS);
        if (difference < 60 && displaySeconds) {
            return difference + " seconds";
        }
        difference = now.until(next, ChronoUnit.MINUTES);
        if (difference <= 2) {
            return 1 + difference + " minutes";
        } else if (difference <= 59) {
            return difference + " minutes";
        } else {
            return (difference / 60) + "h " + difference % 60 + "min";
        }
    }

    int lastEvent = 0;

    public void startBossEvent() {
        terminateActiveEvent(true);
        if (++lastEvent > EVENT_ROTATION.length - 1) lastEvent = 0;
        activeEvent = EVENT_ROTATION[lastEvent];
        last = LocalDateTime.now();
        next = LocalDateTime.now().plusSeconds((long) (BOSS_EVENT_INTERVAL * 0.6d));
        Tile tile = POSSIBLE_SPAWNS[World.getWorld().random().nextInt(POSSIBLE_SPAWNS.length - 1)];
        currentSpawnPos = tile;
        ANNOUNCE_5_MIN_TIMER = false;
        NPC boss = new NPC(activeEvent.npc, tile);
        boss.respawns(false);
        boss.walkRadius(1);
        boss.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 1);
        World.getWorld().registerNpc(boss);
        Utils.sendDiscordInfoLog("The wilderness event boss has been spawned: " + boss.def().name + " at " + tile.toString() + ".");
        this.activeNpc = Optional.of(boss);
        World.getWorld().sendWorldMessage("<col=6a1a18><img=2012> " + activeEvent.description + " has been spotted " + activeEvent.spawnLocation(boss.tile()) + " in level " + WildernessArea.getWildernessLevel(boss.tile()) + " Wild!");
        World.getWorld().sendWorldMessage("<col=6a1a18>It despawns in 60 minutes. Hurry!");
        World.getWorld().sendBroadcast("<img=2012>" + activeEvent.description + " has been spotted " + activeEvent.spawnLocation(boss.tile()) + " in level " + WildernessArea.getWildernessLevel(boss.tile()) + " Wild!");
    }

    public void terminateActiveEvent(boolean force) {
        if (activeEvent != BossEvent.NOTHING) {
            boolean despawned = false;
            for (NPC n : World.getWorld().getNpcs()) {
                if (n != null && n.id() == activeEvent.npc && (n.hp() > 0 || force)) {
                    n.stopActions(true);
                    World.getWorld().unregisterNpc(n);
                    despawned = true;
                }
            }
            ANNOUNCE_5_MIN_TIMER = false;
            this.activeNpc = Optional.empty();

            if (despawned) {
                currentSpawnPos = null; // reset current pos
                Utils.sendDiscordInfoLog("The wilderness event boss has been despawned");
                logger.info("The wilderness event boss has been despawned");
                World.getWorld().sendBroadcast(activeEvent.description + " has despawned");
            }
        }
    }
}

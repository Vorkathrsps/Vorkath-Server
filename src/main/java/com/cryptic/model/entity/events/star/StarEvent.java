package com.cryptic.model.entity.events.star;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class StarEvent {
    public static final int STAR_EVENT_INTERVAL = 6000;
    public static boolean ANNOUNCE_TIMER = false;
    private static final StarEvent instance = new StarEvent();
    private Optional<CrashedStar> activeStar = Optional.empty();
    private LocalDateTime last = LocalDateTime.now().minus((long) (STAR_EVENT_INTERVAL * 0.6D), ChronoUnit.SECONDS);
    private LocalDateTime next = LocalDateTime.now().plus((long) (STAR_EVENT_INTERVAL * 0.6D), ChronoUnit.SECONDS);

    private final Tile[] POSSIBLE_SPAWNS = {
        new Tile(0, 0), // Home
        new Tile(3090, 3962), // Mage bank
        new Tile(2968, 3857) // 44s
    };
    public static Tile currentSpawnPos;

    private SecureRandom secureRandom = new SecureRandom();

    private StarEvent() {
    }

    public static StarEvent getInstance() {
        return instance;
    }

    public Optional<CrashedStar> getActiveStar() {
        return activeStar;
    }

    public void startCrashedStarEvent() {
        if (activeStar.isPresent()) {
            terminateActiveStar();
        }

        last = LocalDateTime.now();
        next = LocalDateTime.now().plus((long) (STAR_EVENT_INTERVAL * 0.6D), ChronoUnit.SECONDS);

        int index = secureRandom.nextInt(POSSIBLE_SPAWNS.length);

        currentSpawnPos = POSSIBLE_SPAWNS[index];
        if (index == 0) {
            currentSpawnPos = GameServer.serverType.getCrashedStarLocation();
        }
        ANNOUNCE_TIMER = false;

        CrashedStar star = new CrashedStar(41019, currentSpawnPos);
        star.spawn();
        activeStar = Optional.of(star);

        Chain.noCtx().delay(2, () -> star.setId(41020));

        World.getWorld().sendBroadcast("A Crashed Star has landed at " + spawnLocation(currentSpawnPos));
        World.getWorld().sendWorldMessage("A Crashed Star has landed at " + spawnLocation(currentSpawnPos));
    }

    public String spawnLocation(Tile tile) {
        if (tile.equals(POSSIBLE_SPAWNS[0])) {
            return "Home";
        } else if (tile.equals(POSSIBLE_SPAWNS[1])) {
            return "Mage bank";
        } else if (tile.equals(POSSIBLE_SPAWNS[2])) {
            return "::44s";
        }
        return "N/A";
    }

    public void terminateActiveStar() {
        ANNOUNCE_TIMER = false;
        activeStar.ifPresent(s -> {
            s.remove();
            s.setDustCount(0);
            activeStar = Optional.empty();
        });
    }
}

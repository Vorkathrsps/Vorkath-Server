package com.aelous.model.entity.events;

import com.aelous.model.World;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class StarEvent {
    public static final int STAR_EVENT_INTERVAL = 6000;
    public static boolean ANNOUNCE_TIMER = false;
    private static final StarEvent instance = new StarEvent();
    private Optional<CrashedStar> activeStar = Optional.empty();
    public LocalDateTime last = LocalDateTime.now().minus((long) (STAR_EVENT_INTERVAL * 0.6D), ChronoUnit.SECONDS);
    public LocalDateTime next = LocalDateTime.now().plus((long) (STAR_EVENT_INTERVAL * 0.6D), ChronoUnit.SECONDS);

    private final Tile[] POSSIBLE_SPAWNS = {
        new Tile(3104, 3509),//home
        new Tile(3090, 3962), //mage bank
        new Tile(2968, 3857) //44s
    };
    public static Tile currentSpawnPos;

    public static StarEvent getInstance() {
        return instance;
    }
    public Optional<CrashedStar> getActiveStar() {
        return activeStar;
    }

    SecureRandom secureRandom = new SecureRandom();
    public void startCrashedStarEvent() {
        if (activeStar.isPresent()) {
            terminateActiveStar();
        }

        last = LocalDateTime.now();
        next = LocalDateTime.now().plus((long) (STAR_EVENT_INTERVAL * 0.6D), ChronoUnit.SECONDS);

        currentSpawnPos = POSSIBLE_SPAWNS[secureRandom.nextInt(POSSIBLE_SPAWNS.length)];
        ANNOUNCE_TIMER = false;

        CrashedStar star = new CrashedStar(41019, currentSpawnPos);
        star.spawn();
        this.activeStar = Optional.of(star);

        Chain.noCtx().delay(2, () -> star.setId(41020));

        World.getWorld().sendBroadcast("A Crashed Star has landed at " + spawnLocation(currentSpawnPos));
        World.getWorld().sendWorldMessage("A Crashed Star has landed at " + spawnLocation(currentSpawnPos));
    }

    public String spawnLocation(Tile tile) {
        if (tile.equals(new Tile(3104, 3509))) {
            return "Home";
        } else if (tile.equals(new Tile(3090, 3962))) {
            return "Mage bank";
        } else if (tile.equals(new Tile(2968, 3857))) {
            return "::44s";
        }
        return "Nothing";
    }

    public void terminateActiveStar() {
        ANNOUNCE_TIMER = false;
        activeStar.ifPresent(s -> {
            activeStar.get().remove();
            activeStar.get().setDustCount(0);
            this.activeStar = Optional.empty();
        });
    }

}

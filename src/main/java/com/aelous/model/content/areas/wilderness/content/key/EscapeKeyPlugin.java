package com.aelous.model.content.areas.wilderness.content.key;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.CustomItemIdentifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class EscapeKeyPlugin {
    private static final Logger log = LoggerFactory.getLogger(EscapeKeyPlugin.class);

    private static final String PICKUP_KEY_MESSAGE = "<col=800000>%s</col> has picked up the Escape key %s.";

    private static final String KEY_SPAWN_MESSAGE = "An Escape key has spawned near the <col=800000>%s (level %d)</col>!";

    private static final String KEY_DROP_MESSAGE = "<col=800000>%s</col> has died and dropped the Escape key near the <col=800000>%s (level %d)</col>!";

    private static final String KEY_ESCAPE_MESSAGE = "<col=800000>%s</col> has made it to safety with the Escape key!";

    private static final String KEY_TRADE_MESSAGE = "<col=800000>%s</col> has given the Escape key to <col=800000>%s</col> at <col=800000>%d</col> wilderness level!";

    private static final String SPRITE = "<img=936> ";

    public static boolean ANNOUNCE_5_MIN_TIMER = false;

    public static boolean ESCAPED = true;

    /**
     * Announces when the player has picked up the key.
     */
    public static void announceKeyPickup(Player player, Tile tile) {
        String currentDescription = null;
        int wildernessLevel = WildernessArea.wildernessLevel(player.tile());

        for (EscapeKeyLocation location : EscapeKeyLocation.values()) {
            if (location.tile().equals(tile)) {
                currentDescription = "near the <col=800000>" + location.getDescription() + "</col> (level <col=800000>" + wildernessLevel + "</col>)";
                break;
            }
        }

        if (currentDescription == null) {
            currentDescription = "around the level <col=800000>" + WildernessArea.wildernessLevel(player.tile()) + "</col>";
        }

        String message = String.format(PICKUP_KEY_MESSAGE, player.getUsername(), currentDescription);
        World.getWorld().sendWorldMessage(SPRITE + message);
    }

    public static void announceKeySpawn(Tile tile) {
        EscapeKeyLocation currentPos = null;
        int wildernessLevel = WildernessArea.wildernessLevel(tile);

        for (EscapeKeyLocation key_pos : EscapeKeyLocation.values()) {
            if (key_pos.tile().equals(tile)) {
                currentPos = key_pos;
                break;
            }
        }

        if (currentPos == null) {
            String message = "An Escape key has spawned somewhere in level <col=800000>" + wildernessLevel + "</col> Wilderness!";
            World.getWorld().sendWorldMessage(SPRITE + message);
            return;
        }

        String message = String.format(KEY_SPAWN_MESSAGE, currentPos.getDescription(), wildernessLevel);
        World.getWorld().sendWorldMessage(SPRITE + message);
    }

    public static void announceKeyDrop(Player killed, Tile tile) {
        EscapeKeyLocation currentPos = null;
        int wildernessLevel = WildernessArea.wildernessLevel(tile);

        for (EscapeKeyLocation key_pos : EscapeKeyLocation.values()) {
            if (key_pos.tile().distance(tile) < 5) {
                currentPos = key_pos;
                break;
            }
        }

        if (currentPos == null) {
            String message = killed.getUsername() + " has died and dropped the Escape key at <col=800000>" + wildernessLevel + "</col> Wilderness!";
            World.getWorld().sendWorldMessage(SPRITE + message);
            return;
        }

        String message = String.format(KEY_DROP_MESSAGE, killed.getUsername(), currentPos.getDescription(), wildernessLevel);
        World.getWorld().sendWorldMessage(SPRITE + message);
    }

    public static void announceKeyTrade(Player player, Player other) {
        checkNotNull(player, "player");
        checkNotNull(other, "other");

        int wildernessLevel = WildernessArea.wildernessLevel(player.tile());

        if (wildernessLevel < 6) {
            return;
        }

        String message = String.format(KEY_TRADE_MESSAGE, player.getUsername(), other.getUsername(), wildernessLevel);
        World.getWorld().sendWorldMessage(SPRITE + message);
    }

    public static void announceKeyEscape(Player player) {
        if(ESCAPED) {
            return;
        }
        String message = String.format(KEY_ESCAPE_MESSAGE, player.getUsername());
        World.getWorld().sendWorldMessage(SPRITE + message);
        World.getWorld().clearBroadcast();
        ESCAPED = true;
    }

    public static final Duration SPAWN_DURATION = Duration.ofHours(1);

    public static LocalDateTime last = LocalDateTime.now();
    public static LocalDateTime next = LocalDateTime.now().plus(SPAWN_DURATION.toSeconds(), ChronoUnit.SECONDS);

    private static final Task spawnTask = new Task("EscapeKeyPluginTask",100) {

        @Override
        protected void execute() {
            LocalDateTime now = LocalDateTime.now();
            long difference = last.until(now, ChronoUnit.MINUTES);
            if (difference >= SPAWN_DURATION.toMinutes()) {
                //log.trace("An escape key is spawning.");
                ANNOUNCE_5_MIN_TIMER = false;
                ESCAPED = true;
                EscapeKeyLocation location = spawnKeys();
                if (location != null) {
                    //log.trace("An escape key has spawned at: {} (absolute: {})", location, location.tile());
                }
                last = now;
                next = LocalDateTime.now().plus(SPAWN_DURATION.toSeconds(), ChronoUnit.SECONDS);
            }
        }
    };

    public static String timeTill(boolean showSeconds) {
        LocalDateTime now = LocalDateTime.now();
        long difference = now.until(next, ChronoUnit.SECONDS);
        if (difference < 60 && showSeconds) {
            return difference+" seconds";
        }
        difference = now.until(next, ChronoUnit.MINUTES);
        if (difference <= 2) {
            return 1+difference+" minutes";
        } else if (difference <= 59) {
            return difference+" minutes";
        } else {
            return (difference / 60)+"h "+difference % 60+"min";
        }
    }

    public static EscapeKeyLocation spawnKeys() {
        if (GroundItemHandler.getGroundItems().stream().anyMatch(groundItem -> groundItem.getItem().getId() == CustomItemIdentifiers.ESCAPE_KEY)) {
            //log.trace("Another key is already on the floor, new key will not spawn.");
            return null;
        }
        EscapeKeyLocation currentLocation = EscapeKeyLocation.findRandom();
        announceKeySpawn(currentLocation.tile());

        GroundItem key = new GroundItem(Item.of(CustomItemIdentifiers.ESCAPE_KEY), currentLocation.tile(), null);
        GroundItemHandler.createGroundItem(key);
        key.setTimer(3600);//Wilderness key has a two hour timer.
        return currentLocation;
    }
    public static void onServerStart() {
        TaskManager.submit(spawnTask);
    }

    /**
     * Checks if the player has a key.
     */
    public static boolean hasKey(Player player) {
        return player.inventory().contains(CustomItemIdentifiers.ESCAPE_KEY);
    }
}

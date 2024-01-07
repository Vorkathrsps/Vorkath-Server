package com.cryptic.model.content.tournaments;


import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;

import java.util.Random;

/**
 * @author Origin | Zerkikoth | PVE
 */
public class TournamentUtils {
    public static final Area LOBBY_AREA = new Area(3322, 4942, 3324, 4956);
    public static final Area FIGHT_AREA = new Area(3267, 4931, 3318, 4988);
    private static int getMinX(Area area) {
        return Math.min(area.x1(), area.x2());
    }

    private static int getMinY(Area area) {
        return Math.min(area.y1, area.y2);
    }

    private static int getMaxX(Area area) {
        return Math.max(area.x1, area.x2);
    }

    private static int getMaxY(Area area) {
        return Math.max(area.y1, area.y2);
    }

    public static Tile getRandomTile(Area area) {
        Random random = new Random();
        int minX = getMinX(area);
        int minY = getMinY(area);
        int maxX = getMaxX(area);
        int maxY = getMaxY(area);

        int randomX = random.nextInt(maxX - minX + 1) + minX;
        int randomY = random.nextInt(maxY - minY + 1) + minY;

        return new Tile(randomX, randomY);
    }

    public static final Tile TORN_START_TILE = new Tile(3294, 4958, 0);
    public static final Tile EXIT_TILE = new Tile(3076, 3503, 0);
    public static final int THORVALD = 8146;
    public static final int GHOST_ID = NpcIdentifiers.GHOST_DISCIPLE;
    public static final int TOURNAMENT_REGION = 13133;
    public static final int TOURNAMENT_INTERFACE = 19999;
    public static final int TOURNAMENT_WALK_INTERFACE = 21100;
    public static final int TOURNAMENT_WALK_TIMER = 21102;
    public static final int TOURNAMENT_TEXT_FRAME = 20002;
    public static final int TOURNAMENT_TIME_LEFT_FRAME = 20003;
    public static final int PRIZE_FRAME = 20007;
    public static final int FIGHT_IMMUME_TIMER = 50;//Equals 30 waiting seconds. This gives time to pot/setup prayers and inventory.
}

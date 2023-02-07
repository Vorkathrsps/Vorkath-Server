package com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks;

import com.aelous.model.World;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;

/**
 * @author Patrick van Elderen | May, 16, 2021, 13:28
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Attacks {

    public static final int LEFTOVER_CRYSTALS = 1338;
    public static final int DARK_GREEN_FLYING = 1339;
    public static final int GREEN_CRYSTAL_FLYING = 1340;
    public static final int PURPLE_ORB = 1341;
    public static final int GREEN_ORB = 1343;
    public static final int RED_ORB = 1345;
    public static final int FIRE_BLAST = 1347;
    public static final int SMALL_FIRE_BLAST = 1348;
    public static final int GREEN_PROJECTILE = 1349;
    public static final int SMALL_FIRE = 1351;

    public static final int CRYSTAL = 1352;

    public static final int DARK_GREEN_SMALL_PROJECTILE = 1354;
    public static final int BLUE_SMALL_PROJECTILE = 1355;

    public static final int GREEN_LIGHTNING = 1356;
    public static final int FALLING_CRYSTAL = 1357;
    public static final int GREEN_PUFF = 1358;

    public static final int WHITE_CIRCLE = 1359;
    public static final int GREEN_CIRCLE = 1360;
    public static final int ORANGE_CIRCLE = 1361;
    public static final int PURPLE_CIRCLE = 1362;
    public static final int RED_CIRCLE = 246;

    public static final int BLUE_BUBBLES = 1363;

    public static Tile randomLocation(int height) {
        ArrayList<Tile> positions = new ArrayList<>();

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 17; y++) {
                positions.add(new Tile(3228 + x, 5731 + y, height));
            }
        }
        return positions.get(World.getWorld().random(positions.size() - 1));
    }
}

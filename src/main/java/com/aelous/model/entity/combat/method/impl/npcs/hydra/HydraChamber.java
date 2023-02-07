package com.aelous.model.entity.combat.method.impl.npcs.hydra;

import com.aelous.model.World;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.aelous.model.entity.masks.Direction.*;

/**
 * @author Patrick van Elderen | December, 22, 2020, 18:06
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class HydraChamber {

    /**
     * The delay between the fire wall attacks.
     */
    public static long fireWallDelay = 45000L;

    /**
     * The delay between the poison pool attacks.
     */
    public static long poisonPoolDelay = 15000L;

    /**
     * The delay between the lightning attacks.
     */
    public static long lightningDelay = 25000L;

    /**
     * The duration for the fire wall effect.
     */
    public static int fireWallDuration = 43;

    /**
     * The hydra's spawn location.
     */
    public static Tile hydraSpawnLoc = new Tile(36, 17, 0);

    /**
     * The blue vent central spot.
     */
    public static Tile blueVentLoc = new Tile(34, 24, 0);

    /**
     * The green vent central spot.
     */
    public static Tile greenVentLoc = new Tile(43, 24, 0);

    /**
     * The red vent central spot.
     */
    public static Tile redVentLoc = new Tile(43, 15, 0);

    /**
     * The blue vent object.
     */
    public static GameObject blueVent = new GameObject(34570, blueVentLoc, 10, 0, false);

    /**
     * The red vent object.
     */
    public static GameObject redVent = new GameObject(34568, redVentLoc, 10, 0, false);

    /**
     * The green vent object.
     */
    public static GameObject greenVent = new GameObject(34569, greenVentLoc, 10, 0, false);

    /**
     * The list of vents.
     */
    public static List<GameObject> vents = List.of(blueVent, redVent, greenVent);

    /**
     * The spot the hydra will send the first projectile for lightning.
     */
    public static Tile centralLightningSpot = new Tile(39, 14, 0);

    /**
     * The forbidden spots for the attacks.
     */
    public static List<Tile> forbiddenSpots = List.of(blueVentLoc, greenVentLoc, redVentLoc, new Tile(48, 29, 0), new Tile(49, 30, 0), new Tile(49, 29, 0), new Tile(49, 30, 0), new Tile(28, 29, 0), new Tile(28, 30, 0), new Tile(29, 29, 0), new Tile(29, 30, 0), new Tile(48, 9, 0), new Tile(49, 9, 0), new Tile(48, 10, 0), new Tile(49, 10, 0));

    /**
     * The spots the lightning spawns
     */
    public static List<Tile> lightningSpots = List.of(new Tile(44, 15, 0), new Tile(34, 15, 0), new Tile(44, 24, 0), new Tile(33, 24, 0));

    /**
     * Returns if the location is within the instance's chamber.
     */
    public static boolean isWithinChamber(Tile baseLocation, Tile location) {
        var xOffset = location.x - baseLocation.x;
        var zOffset = location.y - baseLocation.y;

        return xOffset >= 28 && xOffset <= 49 && zOffset >= 9 && zOffset <= 30 && !isForbiddenSpot(new Tile(xOffset, zOffset, location.level));
    }

    /**
     * Returns if the tile is a forbidden one.
     */
    public static boolean isForbiddenSpot(Tile tile) {
        for (Tile forbiddenSpot : forbiddenSpots) {
            if (forbiddenSpot == tile) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if the tile is within the blue vent.
     */
    public static boolean isWithinBlueVent(Tile tile, int size) {
        return Utils.isWithinDiagonalDistance(tile, blueVentLoc, size, 1, 0);
    }

    /**
     * Returns if the tile is within the gree vent.
     */
    public static boolean isWithinGreenVent(Tile tile, int size) {
        return Utils.isWithinDiagonalDistance(tile, greenVentLoc, size, 1, 0);
    }

    /**
     * Returns if the tile is within the red vent.
     */
    public static boolean isWithinRedVent(Tile tile, int size) {
        // this is bottom left of region vs vents.. theyre like 40 away..
        return Utils.isWithinDiagonalDistance(tile, redVentLoc, size, 1, 0);
    }

    /**
     * Returns the pool graphic for the poison pool attack.
     */
    public static int getPoolGraphic(NPC hydra, Tile targetTile) {
        var center = Utils.getCenterLocation(hydra);
        var dir = Direction.of(targetTile.x - center.x, targetTile.y - center.y);
        for (PoisonPools pool : PoisonPools.values()) {
            if (pool.offsetX == dir.x() && pool.offsetZ == dir.y()) {
                return pool.graphicId;
            }
        }
        return 1654;
    }

    /**
     * Returns the list of pool tiles.
     */
    public static ArrayList<Tile> getPoolTiles(Tile baseLocation, Tile tile, int poolAmount) {
        ArrayList<Tile> pools = new ArrayList<>();
        for (int i = 0; i < poolAmount; i++) {
            Tile randTile;
            for (int x = 0; x < 100; x++) {
                randTile = tile.transform(Utils.random(-3, 3), Utils.random(-3, 3), 0);
                var matches = false;
                for (Tile pool : pools) {
                    if (pool == randTile) {
                        matches = true;
                        break;
                    }
                }
                if (!matches && isWithinChamber(baseLocation, tile)) {
                    pools.add(randTile);
                    break;
                }
            }
        }
        return pools;
    }

    /**
     * Returns the location the tile has to move to reach the target.
     */
    public static Tile getMoveLocation(Tile baseLocation, Tile currentLoc, Tile target) {
        var dir = Direction.of(target.x - (baseLocation.x + currentLoc.x), target.y - (baseLocation.y + currentLoc.y)).opposite();
        return currentLoc.transform(dir.x, dir.y, 0);
    }

    /**
     * The poison pools graphics based on the position offset.
     *
     * @author Gabriel || Wolfsdarker
     */
    public enum PoisonPools {

        NORTH(0, 1, 1659),

        NORTH_EAST(1, 1, 1660),

        EAST(1, 0, 1661),

        SOUTH_EAST(1, -1, 1654),

        SOUTH(0, -1, 1655),

        SOUTH_WEST(-1, -1, 1656),

        WEST(-1, 0, 1657),

        NORTH_WEST(-1, 1, 1658);

        public int offsetX, offsetZ, graphicId;

        PoisonPools(int offsetX, int offsetZ, int graphicId) {
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
            this.graphicId = graphicId;
        }
    }

    /**
     * The fire wall spots.
     *
     * @author Gabriel || Wolfsdarker
     */
    enum FireWallSpots {

        SOUTH_WEST(new Area(28, 9, 38, 19), new ArrayList<>(List.of(new Tile(35, 18, 0), new Tile(35, 21, 0), new Tile(37, 16, 0), new Tile(40, 16, 0))), new ArrayList<>(List.of(new Tile(28, 18, 0), new Tile(35, 21, 0), new Tile(37, 9, 0), new Tile(40, 16, 0)))),

        NORTH_WEST(new Area(28, 20, 38, 30), new ArrayList<>(List.of(new Tile(35, 18, 0), new Tile(35, 21, 0), new Tile(37, 23, 0), new Tile(40, 23, 0))), new ArrayList<>(List.of(new Tile(28, 18, 0), new Tile(35, 21, 0), new Tile(37, 23, 0), new Tile(40, 30, 0)))),

        NORTH_EAST(new Area(39, 20, 49, 30), new ArrayList<>(List.of(new Tile(42, 18, 0), new Tile(42, 21, 0), new Tile(37, 23, 0), new Tile(40, 23, 0))), new ArrayList<>(List.of(new Tile(42, 18, 0), new Tile(49, 21, 0), new Tile(37, 23, 0), new Tile(40, 30, 0)))),

        SOUTH_EAST(new Area(39, 9, 49, 20), new ArrayList<>(List.of(new Tile(42, 18, 0), new Tile(42, 21, 0), new Tile(37, 16, 0), new Tile(40, 16, 0))), new ArrayList<>(List.of(new Tile(42, 18, 0), new Tile(49, 21, 0), new Tile(37, 9, 0), new Tile(40, 16, 0))));

        public Area requiredArea;
        public ArrayList<Tile> projectileSpots, spots;

        FireWallSpots(Area requiredArea, ArrayList<Tile> projectileSpots, ArrayList<Tile> spots) {
            this.requiredArea = requiredArea;
            this.projectileSpots = projectileSpots;
            this.spots = spots;

        }

        /**
         * Spawns the fire wall based on the fire wall spot.
         */
        public void spawnFireWall(Tile base, int index, ArrayList<Tile> spots) {
            switch(this) {
                case SOUTH_WEST -> spawnWall(base, index, index == 0 ? WEST : SOUTH, spots);
                case NORTH_WEST -> spawnWall(base, index, index == 0 ? WEST : NORTH, spots);
                case NORTH_EAST -> spawnWall(base, index, index == 0 ? EAST : NORTH, spots);
                case SOUTH_EAST -> spawnWall(base, index, index == 0 ? EAST : SOUTH, spots);
            }
        }

        /**
         * Spawns the wall based on the direction and the spots.
         */
        @SuppressWarnings("DuplicatedCode")
        private void spawnWall(Tile base, int index, Direction direction, ArrayList<Tile> hydraSpots) {
            var delay = 0;
            switch (direction) {
                case SOUTH -> {
                    // fuck knows what this loop does probs end up re-writing it
                    for (int z = spots.get(index * 2 + 1).getZ(); z < spots.get(index * 2).getZ(); z--) {
                        for (int x = spots.get(index * 2).getX(); x < spots.get(index * 2 + 1).getX(); x++) {
                            var tile = base.transform(x, z, 0);
                            spawn(tile, delay, hydraSpots);
                        }
                        delay += 10;
                    }
                }
                case NORTH -> {
                    // fuck knows what this loop does probs end up re-writing it
                    for (int z = spots.get(index * 2).getZ(); z < spots.get(index * 2 + 1).getZ(); z--) {
                        for (int x = spots.get(index * 2).getX(); x < spots.get(index * 2 + 1).getX(); x++) {
                            var tile = base.transform(x, z, 0);
                            spawn(tile, delay, hydraSpots);
                        }
                        delay += 10;
                    }
                }
                case EAST -> {
                    // fuck knows what this loop does probs end up re-writing it
                    for (int x = spots.get(index * 2).getX(); x < spots.get(index * 2 + 1).getX(); x++) {
                        for (int z = spots.get(index * 2).getZ(); z < spots.get(index * 2 + 1).getZ(); z--) {
                            var tile = base.transform(x, z, 0);
                            spawn(tile, delay, hydraSpots);
                        }
                        delay += 10;
                    }
                }
                case WEST -> {
                    // fuck knows what this loop does probs end up re-writing it
                    for (int x = spots.get(index * 2 + 1).getX(); x < spots.get(index * 2).getX(); x++) {
                        for (int z = spots.get(index * 2).getZ(); z < spots.get(index * 2 + 1).getZ(); z--) {
                            var tile = base.transform(x, z, 0);
                            spawn(tile, delay, hydraSpots);
                        }
                        delay += 10;
                    }
                }
            }
        }

        /**
         * Spawns the graphics.
         */
        private void spawn(Tile tile, int delay, ArrayList<Tile> spots) {
            World.getWorld().tileGraphic(1668, tile, 0, delay);
            spots.add(tile);
        }

    }

    /**
     * Returns the spots where the fire wall will be sent.
     */
   public static FireWallSpots getWallFireSpots(Tile playerLoc, Tile base) {
        for (FireWallSpots value : FireWallSpots.values()) {
            if (value.requiredArea.contains(playerLoc.transform(-base.x, -base.y))) {
                return value;
            }
        }
        return FireWallSpots.SOUTH_WEST;
    }

}

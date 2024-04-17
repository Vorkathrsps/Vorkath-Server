package com.cryptic.model.map.position;

import com.cryptic.PlainTile;
import com.cryptic.model.World;
import com.cryptic.model.content.areas.riskzone.RiskFightArea;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.region.Region;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.utility.Utils;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.cryptic.model.map.route.RouteFinder.*;

public class Tile implements Cloneable {
    private static final Logger log = LogManager.getLogger(Tile.class);
    public int playerCount;
    public int npcCount;
    public ArrayList<GameObject> gameObjects = new ArrayList<>();

    public GameObject object(int objectID) {
        return new GameObject(objectID, this, 10, 0);
    }

    public void addObject(GameObject gameObject) {
        for (GameObject object : Lists.newArrayList(gameObjects)) {
            if ((object.getType() == 10 && gameObject.getType() == 10) || (object.getType() == 4 && gameObject.getType() == 4 && object.getRotation() == gameObject.getRotation())) {
                removeObject(object);
            }
        }

        gameObject.setTile(this);
        gameObject.clip(false);
        gameObjects.add(gameObject);
        checkActive();
    }

    public void removeObject(GameObject gameObject) {
        if (gameObjects == null) {
            return;
        }
        gameObject.clip(true);
        gameObject.setTile(null);
        gameObjects.remove(gameObject);
        checkActive();
    }

    private boolean active;
    private Region region;

    @Nonnull
    public Region getRegion() {
        if (region != null) return region;
        return region = Region.get(x, y);
    }

    public static void update(Player player) {
        for (Region region : player.getRegions()) {
            for (Tile tile : region.activeTiles) {
                if (player.getZ() != tile.getZ()) continue;
                tile.updateGameObjects(player);
            }
        }
    }

    public void checkActive() {
        boolean newActive = isAnyCustomGameObjectNearby();

        if (newActive != this.active) {
            this.active = newActive;
            updateRegionActiveTiles();
        }
    }

    private boolean isAnyCustomGameObjectNearby() {
        return gameObjects.stream().anyMatch(GameObject::isCustom);
    }

    private void updateRegionActiveTiles() {
        Region region = this.getRegion();
        if (this.active) {
            region.activeTiles.add(this);
        } else {
            region.activeTiles.remove(this);
        }
    }

    public void updateGameObjects(Player player) {
        if (gameObjects.isEmpty()) return;
        for (GameObject object : gameObjects) {
            if (object.isCustom()) {
                object.send(player);
            }
        }
        //  log.info("sync {} has {}", this, gameObjects.size());
    }

    public boolean homeRegion() {
        return inArea(EDGEVILE_HOME_AREA) || region() == 7991 || region() == 7992 || region() == 8247;
    }

    public boolean raidsPartyArea() {
        return region() == 4919;
    }

    public static final Area HOME = new Area(2002, 3558, 2017, 3573, -1);

    public static final Area EDGEVILE_HOME_AREA = new Area(3069, 3464, 3129, 3524);

    public static Tile of(int x, int y) {
        return new Tile(x, y, 0);
    }

    public static Tile of(int x, int y, int z) {
        return new Tile(x, y, z);
    }

    /**
     * The Position constructor.
     *
     * @param x     The x-type coordinate of the position.
     * @param y     The y-type coordinate of the position.
     * @param level The height of the position.
     */
    public Tile(int x, int y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
        updateFirstChunk();
    }


    public int clipping;

    public int defaultClipping;

    public int projectileClipping; //I know I didn't want to do this either :[/

    public boolean isTileFree() {
        return isFloorFree() && isWallsFree();
    }

    public boolean isTileFreeCheckDecor() {
        return isFloorFree() && isWallsFree() && isFloorFreeCheckDecor();
    }

    public boolean isFloorFreeCheckDecor() {
        return (clipping & (UNMOVABLE_MASK | DECORATION_MASK | OBJECT_MASK)) == 0;
    }

    public boolean isFloorFree() {
        boolean movable = (clipping & UNMOVABLE_MASK) == 0;
        boolean object = (clipping & OBJECT_MASK) == 0;
        return movable && object;
    }

    public boolean isWallsFree() {
        boolean north = (clipping & NORTH_MASK) == 0;
        boolean east = (clipping & EAST_MASK) == 0;
        boolean south = (clipping & SOUTH_MASK) == 0;
        boolean west = (clipping & WEST_MASK) == 0;
        boolean north_east = (clipping & NORTH_EAST_MASK) == 0;
        boolean south_east = (clipping & SOUTH_EAST_MASK) == 0;
        boolean north_west = (clipping & NORTH_WEST_MASK) == 0;
        boolean south_west = (clipping & SOUTH_WEST_MASK) == 0;
        return north && east && south && west && north_east && south_east && north_west && south_west;
    }

    /**
     * The Position constructor.
     *
     * @param x The x-type coordinate of the position.
     * @param y The y-type coordinate of the position.
     */
    public Tile(int x, int y) {
        this(x, y, 0);
        updateFirstChunk();
    }

    /**
     * The x coordinate of the position.
     */
    public final int x;

    /**
     * Gets the x coordinate of this position.
     *
     * @return The associated x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * The y coordinate of the position.
     */
    public final int y;

    /**
     * Gets the y coordinate of this position.
     *
     * @return The associated y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * The height level of the position.
     */
    public int level;

    /**
     * Gets the height level of this position.
     *
     * @return The associated height level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the height level of this position.
     *
     * @return The Position instance.
     */
    public Tile setLevel(int level) {
        this.level = level;
        return this;
    }

    //public Tile getDelta(Tile position) {
    // return new Tile(x - position.x, y - position.y);
    // }

    public Tile getDelta(Tile location, Tile other) {
        return new Tile(other.x - location.x, other.y - location.y, other.getZ() - location.getZ());
    }

    public int getZ() {
        return level;
    }

    public Tile relative(int changeX, int changeY) {
        return transform(changeX, changeY);
    }

    /**
     * Gets the local x coordinate relative to a specific region.
     *
     * @param tile The region the coordinate will be relative to.
     * @return The local x coordinate.
     */
    public int getLocalX(Tile tile) {
        return x - 8 * tile.getRegionX();
    }

    /**
     * Gets the local y coordinate relative to a specific region.
     *
     * @param tile The region the coordinate will be relative to.
     * @return The local y coordinate.
     */
    public int getLocalY(Tile tile) {
        return y - 8 * tile.getRegionY();
    }

    /**
     * Gets the local x coordinate relative to a specific region.
     *
     * @return The local x coordinate.
     */
    public int getLocalX() {
        return x - 8 * getRegionX();
    }

    /**
     * Gets the local y coordinate relative to a specific region.
     *
     * @return The local y coordinate.
     */
    public int getLocalY() {
        return y - 8 * getRegionY();
    }

    /**
     * Gets the region x coordinate.
     *
     * @return The region x coordinate.
     */
    public int getRegionX() {
        return (x >> 3) - 6;
    }

    /**
     * Gets the region y coordinate.
     *
     * @return The region y coordinate.
     */
    public int getRegionY() {
        return (y >> 3) - 6;
    }

    /**
     * Adds steps/coordinates to this position.
     */
    public Tile add(int x, int y) {
        return transform(x, y);
    }

    public Tile add(Direction direction) {
        return transform(direction.x(), direction.y());
    }

    /**
     * Checks if this location is within interaction range of another.
     *
     * @param other The other location.
     * @return <code>true</code> if the location is in range, <code>false</code>
     * if not.
     */
    public int distance(Tile other) {
        final int deltaX = other.getX() - getX(), deltaY = other.getY() - getY();
        return Math.max(Math.abs(deltaX), Math.abs(deltaY));
    }

    public void faceObjectTile(GameObject obj) {
        int sizeX = obj.definition().sizeX;
        int sizeY = obj.definition().sizeY;
        boolean inversed = (obj.getRotation() & 0x1) != 0;
        int faceCoordX = obj.x * 2 + (inversed ? sizeY : sizeX);
        int faceCoordY = obj.y * 2 + (inversed ? sizeX : sizeY);
        new Tile(faceCoordX, faceCoordY);
    }

    public Tile faceObject(GameObject obj) {
        int sizeX = obj.definition().sizeX;
        int sizeY = obj.definition().sizeY;
        boolean inversed = (obj.getRotation() & 0x1) != 0;
        int faceCoordX = obj.x * 2 + (inversed ? sizeY : sizeX);
        int faceCoordY = obj.y * 2 + (inversed ? sizeX : sizeY);
        return new Tile(faceCoordX, faceCoordY);
    }
    public Tile getDistanceTo(Tile other) {
        final int deltaX = other.getX() - getX(), deltaY = other.getY() - getY();
        return new Tile(Math.abs(deltaX), Math.abs(deltaY));
    }

    public double getDistance(final Tile other) {
        final int xdiff = getX() - other.getX();
        final int ydiff = getY() - other.getY();
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    public double getDistance(final int x, final int y) {
        final int xdiff = getX() - x;
        final int ydiff = getY() - y;
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
    }

   /* public int getDistance(Tile other) {
        var dx = Math.abs(other.getX() - getX());
        var dy = Math.abs(other.getY() - getY());
        var min = Math.min(dx, dy);
        var max = Math.max(dx, dy);
        return min + (max - min);
    }*/

    public int getChevDistance(Tile other) {
        return Math.max(Math.abs(getX() - other.getX()), Math.abs(getY() - other.getY()));
    }

    public int getManhattanDistance(Tile other) {
        return Math.abs(other.getX() - getX()) + Math.abs(other.getY() - getY());
    }

    public int getManHattanDist(Tile pos, Tile other) {
        return getManhattanDistance(pos.getX(), pos.getY(), other.getX(), other.getY());
    }

    public int getDistance(int x1, int y1, int x2, int y2) {
        int diffX = Math.abs(x1 - x2);
        int diffY = Math.abs(y1 - y2);
        return Math.max(diffX, diffY);
    }

    public double getDistanceToLargeNpc(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getManhattan(Tile pos, Tile other) {
        return calculateManhattanDistance(pos.getX(), pos.getY(), other.getX(), other.getY());
    }

    public int calculateManhattanDistance(int x1, int y1, int x2, int y2) {
        int deltaX = Math.abs(x2 - x1);
        int deltaY = Math.abs(y2 - y1);
        return deltaX + deltaY;
    }

    /**
     * Checks if this location is within range of another.
     *
     * @param other The other location.
     * @return <code>true</code> if the location is in range,
     * <code>false</code> if not.
     */
    public boolean isWithinDistance(Tile other) {
        if (level != other.level)
            return false;
        int deltaX = Math.abs(x - other.x);
        int deltaY = Math.abs(y - other.y);
        return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
    }

    public Tile getSouthwestTile(Entity target) {
        int x = target.getX();
        int y = target.getY();
        int z = target.getZ();
        int newX = x - 1;
        int newY = y + 1;
        return new Tile(newX, newY, z);
    }

    public Tile getSouthEastTile(Entity target) {
        int x = target.getX();
        int y = target.getY();
        int z = target.getZ();
        int newX = x + 1;  // Move one tile to the east
        int newY = y + 1;  // Move one tile to the south
        return new Tile(newX, newY, z);
    }


    public double calculateDistance(int x1, int y1, int x2, int y2) {
        int deltaX = x2 - x1;
        int deltaY = y2 - y1;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }


    /**
     * Checks if the position is within distance of another.
     *
     * @param other    The other position.
     * @param distance The distance.
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isWithinDistance(Tile other, int distance) {
        return isWithinDistance(other, true, distance);
    }

    public boolean isWithinDistance(Tile other, boolean checkHeight, int distance) {
        return (!checkHeight || other.level == level) && Math.abs(x - other.x) <= distance && Math.abs(y - other.y) <= distance;
    }

    public static int getManhattanDistance(int x, int y, int x2, int y2) {
        return Math.abs(x - x2) + Math.abs(y - y2);
    }

    public static int getManhattanDistance(Tile pos, Tile other) {
        return getManhattanDistance(pos.getX(), pos.getY(), other.getX(), other.getY());
    }


    /**
     * Checks if a coordinate is within range of another.
     *
     * @return <code>true</code> if the location is in range,
     * <code>false</code> if not.
     */
    /**
     * Checks if a coordinate is within range of another.
     *
     * @return <code>true</code> if the location is in range,
     * <code>false</code> if not.
     */
    public boolean isWithinDistance(Entity attacker, Entity victim, int distance) {
        if (attacker.xLength() == 1 && attacker.yLength() == 1 &&
            victim.xLength() == 1 && victim.yLength() == 1 && distance == 1) {
            return distance(victim.tile()) <= distance;
        }
        List<Tile> myTiles = entityTiles(attacker);
        List<Tile> theirTiles = entityTiles(victim);
        for (Tile myTile : myTiles) {
            for (Tile theirTile : theirTiles) {
                if (myTile.isWithinDistance(theirTile, distance)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The list of tiles this entity occupies.
     *
     * @param mob The entity.
     * @return The list of tiles this entity occupies.
     */
    public List<Tile> entityTiles(Entity mob) {
        List<Tile> myTiles = new ArrayList<Tile>();
        myTiles.add(mob.tile());
        if (mob.xLength() > 1) {
            for (int i = 1; i < mob.xLength(); i++) {
                myTiles.add(Tile.create(mob.tile().getX() + i,
                    mob.tile().getY(), mob.tile().getLevel()));
            }
        }
        if (mob.yLength() > 1) {
            for (int i = 1; i < mob.yLength(); i++) {
                myTiles.add(Tile.create(mob.tile().getX(),
                    mob.tile().getY() + i, mob.tile().getLevel()));
            }
        }
        int myHighestVal = (mob.xLength() > mob.yLength() ? mob.xLength() : mob.yLength());
        if (myHighestVal > 1) {
            for (int i = 1; i < myHighestVal; i++) {
                myTiles.add(Tile.create(mob.tile().getX() + i,
                    mob.tile().getY() + i, mob.tile().getLevel()));
            }
        }
        return myTiles;
    }

    /**
     * Checks if this location is within interaction range of another.
     *
     * @param other The other location.
     * @return <code>true</code> if the location is in range,
     * <code>false</code> if not.
     */
    public boolean isWithinInteractionDistance(Tile other) {
        if (level != other.level) {
            return false;
        }
        int deltaX = other.x - x, deltaY = other.y - y;
        return deltaX <= 2 && deltaX >= -3 && deltaY <= 2 && deltaY >= -3;
    }

    /**
     * Checks if {@code position} has the same values as this position.
     *
     * @param tile The position to check.
     * @return The values of {@code position} are the same as this position's.
     */
    public boolean sameAs(Tile tile) {
        return tile.x == x && tile.y == y && tile.level == level;
    }

    public double distanceToPoint(int pointX, int pointY) {
        return Math.sqrt(Math.pow(x - pointX, 2)
            + Math.pow(y - pointY, 2));
    }

    /**
     * Creates a position.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @return The location.
     */
    public static Tile create(int x, int y, int z) {
        return new Tile(x, y, z);
    }

    public Tile copy() {
        return new Tile(x, y, level);
    }

    @Override
    public Tile clone() {
        return copy();
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + level + ", " + hcode() + "].";
    }

    public String hcode() {
        return "Tile@" + Integer.toHexString(hashCode());
    }

    public boolean isViewableFrom(Tile other) {
        if (this.getLevel() != other.getLevel())
            return false;
        Tile p = Utils.delta(this, other);
        return p.x <= 14 && p.x >= -15 && p.y <= 14 &&
            p.y >= -15;
    }

    /**
     * Returns the delta coordinates. Note that the returned position is not an
     * actual position, instead it's values represent the delta values between
     * the two arguments.
     *
     * @param a the first position.
     * @param b the second position.
     * @return the delta coordinates contained within a position.
     */
    public static Tile delta(Tile a, Tile b) {
        return new Tile(b.x - a.x, b.y - a.y);
    }

    /**
     * Gets the longest horizontal or vertical delta between the two positions.
     *
     * @param other The other position.
     * @return The longest horizontal or vertical delta.
     */
    public int getLongestDelta(Tile other) {
        int deltaX = Math.abs(getX() - other.getX());
        int deltaY = Math.abs(getY() - other.getY());
        return Math.max(deltaX, deltaY);
    }

    public Tile minus(Tile tile) {
        return new Tile(x - tile.x, y - tile.y, level - tile.level);
    }

    public Tile plus(Tile tile) {
        return new Tile(x + tile.x, y + tile.y, level + tile.level);
    }

    public boolean inArea(int lowestX, int lowestY, int highestX, int highestY) {
        return x >= lowestX && y >= lowestY && x <= highestX && y <= highestY;
    }

    public boolean inArea(Area area) {
        return x >= area.x1 && y >= area.y1 && x <= area.x2 && y <= area.y2;
    }

    public boolean withinArea(Area area) {
        return x >= area.x2 && y >= area.y2 && x <= area.x1 && y <= area.y1;
    }

    public boolean inAreaZ(Area area) {
        return x >= area.x1 && y >= area.y1 && x <= area.x2 && y <= area.y2 && level == area.level;
    }

    /**
     * @param player The player object
     * @param area   The array of Area objects
     * @return
     */
    public boolean inArea(Player player, Area[] area) {
        for (Area a : area) {
            if (a.level >= 0) {
                if (player.getZ() != a.level) {
                    continue;
                }
            }
            if (player.getX() >= a.x1 && player.getX() <= a.x2 && player.getY() >= a.y1 && player.getY() <= a.y2) {
                return true;
            }
        }
        return false;
    }

    public int getDeltaX(Tile otherTile) {
        return otherTile.getX() - this.x;
    }

    public int getDeltaY(Tile otherTile) {
        return otherTile.getY() - this.y;
    }

    public boolean inBounds(Area boundary) {
        return boundary.inBounds(x, y, level, 0);
    }

    public boolean insideBounds(Boundary boundary, Tile tile) {
        return boundary.inside(this);
    }

    /**
     * Used for barrage/retribution 3x3 close targets.
     *
     * @param tile
     * @param span
     */
    public boolean inSqRadius(Tile tile, int span) {
        return this.inAreaZ(new Area(tile.x - span, tile.y - span, tile.x + span, tile.y + span, tile.level));
    }

    public int region() {
        return ((x >> 6) << 8) | (y >> 6);
    }

    public int getRegionId() {
        return (((getX() >> 6) << 8) + (getY() >> 6));
    }

    public Tile regionCorner() {
        return new Tile((region() >> 8) << 6, (region() & 0xFF) << 6);
    }

    public int chunk() {
        return ((x >> 3) << 16) | (y >> 3);
    }

    public int chunkX() {
        return x >> 3;
    }

    public int chunkY() {
        return y >> 3;
    }

    public Tile chunkCorner() {
        return new Tile(((chunk() >> 16) << 3), ((chunk() & 0xFFFF) << 3));
    }

    public static Tile chunkToTile(int chunkId) {
        return new Tile(((chunkId >> 16) << 3), ((chunkId & 0xFFFF) << 3));
    }

    public static Tile regionToTile(int regionId) {
        return new Tile(((regionId >> 8) << 6), ((regionId & 0xFF) << 6));
    }

    public static int coordsToRegion(int x, int y) {
        return ((x >> 6) << 8) | (y >> 6);
    }

    public Tile transform(Tile tile) {
        return new Tile(x + tile.x, y + tile.y, level + tile.level);
    }

    public Tile transform(int dx, int dy, int dz) {
        return new Tile(x + dx, y + dy, level + dz);
    }


    public Tile transform(int dx, int dy) {
        return new Tile(x + dx, y + dy, level);
    }

    public int palletteHash(int rotation) {
        return ((level & 0x3) << 24) | ((chunkX() & 0x3FF) << 14) | ((chunkY() & 0x7FF) << 3) | ((rotation & 0x3) << 1);
    }

    public int regionX() {
        return x >> 6;
    }

    public int localX() {
        return x & 0x3F;
    }

    public int localY() {
        return y & 0x3F;
    }

    public int regionY() {
        return level >> 6;
    }

    public int tectonicPlateX() {
        return x >> 13;
    }

    public int tectonicPlateY() {
        return y >> 13;
    }

    public int hash18() {
        return (level << 16) + (tectonicPlateX() << 8) + tectonicPlateY();
    }

    public int hash30() {
        return (level << 28) | (x << 14) | y;
    }

    public Area area(int radius) {
        return new Area(x - radius, y - radius, x + radius, y + radius, level);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tile))
            return false;
        Tile o = (Tile) obj;
        return o.x == x && o.y == y && o.level == level;
    }

    public boolean equals(int x, int y, int level) {
        return this.x == x && this.y == y && this.level == level;
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    public static final boolean overlaps(int currX, int currY, int unitSizeX, int unitSizeY, int targetX, int targetY, int targetSizeX, int targetSizeY) {
        if (currX >= targetX + targetSizeX || targetX >= currX + unitSizeX) {
            return false;
        }
        if (currY >= targetY + targetSizeY || targetY >= currY + unitSizeY) {
            return false;
        }
        return true;
    }

    /**
     * If the player and grounditem height are the same.
     *
     * @param p
     * @param item
     * @return
     */
    public static boolean sameH(Player p, GroundItem item) {
        return p.tile().level == item.getTile().level;
    }

    /**
     * If the player and mapobj height are the same.
     *
     * @param p
     * @param obj
     * @return
     */
    public static boolean sameH(Player p, GameObject obj) {
        return p.tile().level == obj.tile().level;
    }

    /**
     * Checks if the player is inside the revenant cave boundary.
     *
     * @return true if we're inside the boundary, false otherwise.
     */
    public boolean insideRevCave() {
        return region() >= 12957 && region() <= 12959 || region() >= 12701 && region() <= 12703;
    }

    public boolean allowObjectPlacement() {
        if (ObjectManager.objWithTypeExists(10, this)) {
            return false;
        }
        if (ObjectManager.objWithTypeExists(11, this)) {
            return false;
        }
        if ((World.getWorld().floorAt(this) & 0x4) != 0) {
            return false;
        }
        return true;
    }

    public boolean memberZone() {
        return region() == 13462 || region() == 9772;
    }

    public boolean memberCave() {
        return region() == 9369 || region() == 9370;
    }

    public boolean insideRiskArea() {
        return inArea(RiskFightArea.NH_AREA);
    }

    public static boolean standingOn(Entity entity, Entity other) {
        int firstSize = entity.getSize();
        int secondSize = other.getSize();
        int x = entity.tile().getX();
        int y = entity.tile().getY();
        int vx = other.tile().getX();
        int vy = other.tile().getY();
        for (int i = x; i < x + firstSize; i++) {
            for (int j = y; j < y + firstSize; j++) {
                if (i >= vx && i < secondSize + vx && j >= vy && j < secondSize + vy) {//does this need to be <= not just < ? nah it size + x pos so 5+1=6, smaller=5 fine
                    return true;
                }
            }
        }
        return false;
    }

    public Tile getAxisDistances(Entity source, Tile other) {
        Tile p1 = this.getComparisonPoint(source, other);
        Tile p2 = other.getComparisonPoint(source, this);
        return new Tile(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getY() - p2.getY()));
    }

    private Tile getComparisonPoint(Entity source, Tile other) {
        int x, y;
        if (other.x <= this.x) {
            x = this.x;
        } else if (other.x >= this.x + source.getSize() - 1) {
            x = this.x + source.getSize() - 1;
        } else {
            x = other.x;
        }
        if (other.y <= this.y) {
            y = this.y;
        } else if (other.y >= this.y + source.getSize() - 1) {
            y = this.y + source.getSize() - 1;
        } else {
            y = other.y;
        }
        return new Tile(x, y);
    }

    public Tile translateAndCenterNpcPosition(Entity source, Entity target) {
        var vectorX = (this.unitVectorX(target.getCentrePosition()) / 2);
        var vectorY = (this.unitVectorY(target.getCentrePosition()) / 2);
        var height = this.getZ();
        this.transform(vectorX, vectorY, height);
        var centerX = (this.getX() + source.getSize() / 2);
        var centerY = (this.getY() + source.getSize() / 2);
        return new Tile(centerX, centerY, height);
    }

    public int unitVectorX(Tile target) {
        int diff = target.getX() - getX();
        if (diff != 0)
            diff /= Math.abs(diff);
        return diff;
    }

    public int unitVectorY(Tile target) {
        int diff = target.getY() - getY();
        if (diff != 0)
            diff /= Math.abs(diff);
        return diff;
    }

    public boolean isRight(Tile t) {
        return x > t.x;
    }

    public boolean isLeft(Tile t) {
        return x < t.x;
    }

    public boolean isAbove(Tile t) {
        return y > t.y;
    }

    public boolean isUnder(Tile t) {
        return y < t.y;
    }

    public double distanceTo(Tile other) {
        if (level != other.level) {
            return Integer.MAX_VALUE - 1;
        }
        return distanceFormula(x, y, other.x, other.y);
    }

    public static double distanceFormula(int x, int y, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
    }

    public List<Tile> area(int radius, Predicate<Tile> filter) {
        List<Tile> list = new ArrayList<>((int) Math.pow((1 + radius), 2));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= +radius; y++) {
                Tile pos = relative(x, y);
                if (filter.test(pos))
                    list.add(pos);
            }
        }
        return list;
    }

    //Stops people using certain commands outside of our 'safezones'.
    public boolean inSafeZone() {
        if (inArea(3044, 3455, 3114, 3524)) { // Edgeville
            return true;
        }
        if (inArea(2527, 4709, 2549, 4724)) { // Mage bank (inside)
            return true;
        }
        if (homeRegion()) {
            return true;
        }
        if (memberZone()) {
            return true;
        }
        if (raidsPartyArea()) {
            return true;
        }
        return false;
    }

    public boolean nextTo(Tile destination) {
        int dx = Math.abs(x - destination.x);
        int dy = Math.abs(y - destination.y);
        return (dx <= 2 && dy <= 2 || dx >= 0 && dy >= 0);
    }

    public boolean inFrontOf(Tile destination) {
        int dx = Math.abs(destination.x - x);
        int dy = Math.abs(destination.y - y);

        // Check if the destination tile is within a 3-tile wide column horizontally or diagonally
        return (dx <= 3 && dy <= 3);
    }

    public int clip() {
        return RegionManager.getClipping(x, y, level);
    }

    public String clipstr() {
        return World.getWorld().clipstr(clip());
    }

    public Set<Tile> expandedBounds(int radius) {
        return expandedBounds(radius, null);
    }

    public Set<Tile> expandedBounds(int radius, Predicate<Tile> filter) {
        Set<Tile> list = new HashSet<>((int) Math.pow((1 + radius), 2));
        final Tile src = this;
        for (int x = -radius; x <= radius; x++) {
            list.add(relative(x, -radius));
            list.add(relative(x, +radius));
        }
        for (int y = -radius; y <= radius; y++) {
            list.add(relative(+radius, y));
            list.add(relative(-radius, y));
        }
        return list;
    }

    private int firstChunkX, firstChunkY;

    public void updateFirstChunk() {
        firstChunkX = x >> 3;
        firstChunkY = y >> 3;
    }

    public int getFirstChunkX() {
        return firstChunkX;
    }

    public int getFirstChunkY() {
        return firstChunkY;
    }

    public int getBaseLocalX() {
        return x - 8 * (firstChunkX - 6);
    }

    public int getBaseLocalY() {
        return y - 8 * (firstChunkY - 6);
    }

    public int getBaseX() {
        return (this.getFirstChunkX() - 6) * 8;
    }

    public int getBaseY() {
        return (this.getFirstChunkY() - 6) * 8;
    }

    public boolean allowEntrance(int mask) {
        return (RegionManager.getClipping(x, y, getLevel()) & mask) == 0;
    }

    public boolean allowStandardEntrance() {
        return allowEntrance(WEST_MASK)
            || allowEntrance(EAST_MASK)
            || allowEntrance(SOUTH_MASK)
            || allowEntrance(NORTH_MASK);
    }

    public static void occupy(Entity entity) {
        if (entity.occupyingTiles) {
            fill(entity, entity.getPreviousTile(), -1);
            entity.occupyingTiles = false;
        }
        if (!(entity.isPlayer() && entity.player().looks().hidden())) {
            if (entity.isNpc() && !entity.npc().def().occupyTiles)
                return;
            fill(entity, entity.tile(), 1);
            entity.occupyingTiles = true;
        }
    }

    public static void unoccupy(Entity mob) {
        if (mob.occupyingTiles) {
            fill(mob, mob.getPreviousTile(), -1);
            mob.occupyingTiles = false;
        }
    }

    private static void fill(Entity entity, Tile pos, int increment) {
        int size = entity.getSize();
        int absX = pos.getX();
        int absY = pos.getY();
        int z = pos.getZ();
        for (int x = absX; x < (absX + size); x++) {
            for (int y = absY; y < (absY + size); y++) {
                Tile tile = Tile.get(x, y, z, true);
                if (tile == null) continue;
                if (entity.isPlayer())
                    tile.playerCount += increment;
                else
                    tile.npcCount += increment;
            }
        }
    }

    public static @Nullable Tile get(int x, int y, int z) {
        return get(x, y, z, false);
    }

    public static @Nullable Tile get(Tile position) {
        return get(position.getX(), position.getY(), position.getZ(), false);
    }

    public static @Nullable Tile get(Tile position, boolean create) {
        return Region.get(position.getX(), position.getY()).getTile(position.getX(), position.getY(), position.getZ(), create);
    }

    public static @Nullable Tile get(int x, int y, int z, boolean create) {
        return Region.get(x, y).getTile(x, y, z, create);
    }

    public static boolean isOccupied(@NotNull Entity entity, int stepX, int stepY) {
        int size = entity.getSize();
        int absX = entity.getAbsX();
        int absY = entity.getAbsY();
        int z = entity.getZ();
        int eastMostX = absX + (size - 1);
        int northMostY = absY + (size - 1);
        for (int x = stepX; x < (stepX + size); x++) {
            for (int y = stepY; y < (stepY + size); y++) {
                if (x >= absX && x <= eastMostX && y >= absY && y <= northMostY) {
                    continue;
                }
                Tile tile = Tile.get(x, y, z, true);
                if (tile == null) continue;
                var npcCount = tile.npcCount;
                if (entity.isNpc() && entity.tile().equals(tile)) npcCount--;
                if (tile.playerCount > 0 || npcCount > 0)
                    return true;
            }
        }
        return false;
    }


    public void flagDecoration() {
        RegionManager.addClipping(x, y, level, 0x40000);
    }

    public void unflagDecoration() {
        RegionManager.removeClipping(x, y, level, 0x40000);
    }

    public Tile center(int size) {
        return transform((int) Math.ceil(size / 2.0), (int) Math.ceil(size / 2.0), 0);
    }

    public Tile withHeight(int z) {
        return new Tile(x, y, z);
    }


    public static GameObject getObject(int id, int x, int y, int z, int type, int rot) {
        GameObject object = new Tile(x, y, z).getObject(id, type, rot);
        //if (object == null) {
        //   LogManager.getLogger("GameObject").info("request {} {} {} {} {} {} found {}", id, x, y, z, type, rot, object);
        //}
        return object;
    }

    public GameObject getObject(int id, int type, int direction) {
        return MapObjects.get(t -> {
            return t.tile().equals(x, y, level)
                && (id == -1 || t.getId() == id)
                && (type == -1 || t.getType() == type) // -1 means match any id
                && (direction == -1 || t.getRotation() == direction);
        }, new Tile(x, y, level)).orElse(null);
    }

    public Tile tileToDir(Direction n) {
        return transform(n.x * 10, n.y * 10);
    }

    public GroundItem showTempItem(int itemId) {
        GroundItem gi = new GroundItem(new Item(itemId), this, null);
        GroundItemHandler.createGroundItem(gi);
        gi.setTimer(1);
        return gi;
    }

    public PlainTile toPlain() {
        return new PlainTile(x, y, level);
    }

    public Region[] getSurroundingRegions() {
        final var region = this.region();
        var surrounding = new Region[9];
        surrounding[0] = RegionManager.getRegion(region);
        surrounding[1] = RegionManager.getRegion(region - 1);
        surrounding[2] = RegionManager.getRegion(region + 1);
        surrounding[3] = RegionManager.getRegion(region - 256);
        surrounding[4] = RegionManager.getRegion(region + 256);
        surrounding[5] = RegionManager.getRegion(region + 257);
        surrounding[6] = RegionManager.getRegion(region - 257);
        surrounding[7] = RegionManager.getRegion(region - 255);
        surrounding[8] = RegionManager.getRegion(region + 255);
        // System.out.println("regions "+ Arrays.toString(Arrays.stream(surrounding).map(r -> r.getRegionId()).toArray()));
        return surrounding;
    }
}

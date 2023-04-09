package com.aelous.model.map.position;

import com.aelous.model.content.areas.riskzone.RiskFightArea;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.model.map.region.Region;
import com.aelous.model.map.region.RegionManager;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static com.aelous.model.map.route.RouteFinder.*;

/**
 * Represents a single world tile position.
 *
 * @author relex lawl
 */
public class Tile implements Cloneable {

    public static final Area GAMBLING_ZONE = new Area(2338, 4934, 2381, 4993);
    private int playerCount;
    private int npcCount;

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
     * @param x        The x-type coordinate of the position.
     * @param y        The y-type coordinate of the position.
     * @param level        The height of the position.
     */
    public Tile(int x, int y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
        updateFirstChunk();
    }

    /**
     * The Position constructor.
     * @param x        The x-type coordinate of the position.
     * @param y        The y-type coordinate of the position.
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
     * @return    The associated x coordinate.
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
     * @return    The associated y coordinate.
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
     * @return    The associated height level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the height level of this position.
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
     * @param tile    The region the coordinate will be relative to.
     * @return             The local x coordinate.
     */
    public int getLocalX(Tile tile) {
        return x - 8 * tile.getRegionX();
    }

    /**
     * Gets the local y coordinate relative to a specific region.
     * @param tile     The region the coordinate will be relative to.
     * @return             The local y coordinate.
     */
    public int getLocalY(Tile tile) {
        return y - 8 * tile.getRegionY();
    }

    /**
     * Gets the local x coordinate relative to a specific region.
     * @return             The local x coordinate.
     */
    public int getLocalX() {
        return x - 8 * getRegionX();
    }

    /**
     * Gets the local y coordinate relative to a specific region.
     * @return             The local y coordinate.
     */
    public int getLocalY() {
        return y - 8 * getRegionY();
    }

    /**
     * Gets the region x coordinate.
     * @return The region x coordinate.
     */
    public int getRegionX() {
        return (x >> 3) - 6;
    }

    /**
     * Gets the region y coordinate.
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

    /**
     * Checks if this location is within interaction range of another.
     *
     * @param other
     *            The other location.
     * @return <code>true</code> if the location is in range, <code>false</code>
     *         if not.
     */
    public int distance(Tile other) {
        int deltaX = x - other.x;
        int deltaY = y - other.y;
        return (int) Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
    }

    public int getDistance(Tile other) {
        var dx = Math.abs(other.getX() - getX());
        var dy = Math.abs(other.getY() - getY());
        var min = Math.min(dx, dy);
        var max = Math.max(dx, dy);
        return min + (max - min);
    }

    public int getChevDistance(Tile other) {
        return Math.max(Math.abs(other.getX() - getX()), Math.abs(other.getY() - getY()));
    }

    public int getManHattanDist(Tile pos, Tile other) {
        return getManhattanDistance(pos.getX(), pos.getY(), other.getX(), other.getY());
    }
    public int getManHattanDist(int x, int y, int x2, int y2) {
        return Math.abs(x - x2) + Math.abs(y - y2);
    }

    /**
     * Checks if this location is within range of another.
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

    /**
     * Checks if the position is within distance of another.
     * @param other The other position.
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
     * @param tile    The position to check.
     * @return            The values of {@code position} are the same as this position's.
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
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     * @param z
     *            The z coordinate.
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
        return "[" + x + ", " + y + ", " + level + "].";
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
     * @param a the first position.
     * @param b the second position.
     * @return the delta coordinates contained within a position.
     */
    public static Tile delta(Tile a, Tile b) {
        return new Tile(b.x - a.x, b.y - a.y);
    }

    /**
     * Gets the longest horizontal or vertical delta between the two positions.
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

    public boolean inAreaZ(Area area) {
        return x >= area.x1 && y >= area.y1 && x <= area.x2 && y <= area.y2 && level == area.level;
    }

    /**
     *
     * @param player The player object
     * @param area The array of Area objects
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

    public boolean inBounds(Area boundary) {
        return boundary.inBounds(x, y, level, 0);
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

    @Override
    public int hashCode() {
        return (level << 30) | (x << 14) | y;
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
        List<Tile> list = new ArrayList<>((int)Math.pow((1 + radius), 2));
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
        if(homeRegion()) {
            return true;
        }
        if(memberZone()) {
            return true;
        }
        if (raidsPartyArea()) {
            return true;
        }
        return false;
    }

    public boolean nextTo(Tile destination) {
        return (x == destination.x + 1 && y == destination.y) ||
            (x == destination.x - 1 && y == destination.y) ||
            (x == destination.x && y == destination.y + 1) ||
            (x == destination.x && y == destination.y - 1);
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
        Set<Tile> list = new HashSet<>((int)Math.pow((1 + radius), 2));
        final Tile src = this;
        for (int x = -radius; x <= radius; x++) {
            list.add(relative(x,  - radius));
            list.add(relative(x,  + radius));
        }
        for (int y = -radius; y <= radius; y++) {
            list.add(relative(+radius, y));
            list.add(relative( -radius, y));
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
        if(entity.occupyingTiles) {
            fill(entity, entity.getPreviousTile(), -1);
            entity.occupyingTiles = false;
        }
        if(!(entity.isPlayer() && entity.player().looks().hidden())) {
            if(entity.isNpc() && !entity.npc().def().occupyTiles)
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
        for(int x = absX; x < (absX + size); x++) {
            for(int y = absY; y < (absY + size); y++) {
                Tile tile = Tile.get(x, y, z, true);
                if (tile == null) continue;
                if(entity.isPlayer())
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
        return Region.get(position.getX(), position.getY()). getTile(position.getX(), position.getY(), position.getZ(), create);
    }

    public static @Nullable Tile get(int x, int y, int z, boolean create) {
        return Region.get(x, y).getTile(x, y, z, create);
    }

    public static boolean isOccupied(Entity entity, int stepX, int stepY) {
        int size = entity.getSize();
        int absX = entity.getAbsX();
        int absY = entity.getAbsY();
        int z = entity.getZ();
        int eastMostX = absX + (size - 1);
        int northMostY = absY + (size - 1);
        for(int x = stepX; x < (stepX + size); x++) {
            for(int y = stepY; y < (stepY + size); y++) {
                if(x >= absX && x <= eastMostX && y >= absY && y <= northMostY) {
                    /* stepping within itself, allow it */
                    continue;
                }
                Tile tile = Tile.get(x, y, z, true);
                if (tile == null) continue;
                if(tile.playerCount > 0 || tile.npcCount > 0)
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

    public GameObject getObject(int id, int type, int rot) {
        Optional<GameObject> obj = World.getWorld().getSpawnedObjs().stream().filter(o ->
            (type == -1 || o.getType() == type)
                && (rot == -1 || o.getRotation() == rot)
                && (id == -1 || o.getId() == id)
                && o.tile().equals(new Tile(x, y, level))
        ).findFirst();
        if (obj.isPresent()) {
        //    LogManager.getLogger("GameObject").info("request2 {} {} {} {} {} {} found {}", id, x, y, level, type, rot, obj.orElse(null));
        }
        GameObject gameObject = obj.orElse(MapObjects.get(o -> {
            return (type == -1 || o.getType() == type)
                && (rot == -1 || o.getRotation() == rot)
                && (id == -1 || o.getId() == id);
        }, new Tile(this.x, this.y, this.getZ())).orElse(null));
        if (gameObject == null) {
       //     LogManager.getLogger("GameObject").info("request3 {} {} {} {} {} {} found {}", id, x, y, level, type, rot, gameObject);
        }
        return gameObject;
    }

    public Tile tileToDir(Direction n) {
        return transform(n.x * 10, n.y * 10);
    }

    public GroundItem showTempItem(int i) {
        GroundItem gi = new GroundItem(new Item(i), this, null);
        GroundItemHandler.createGroundItem(gi);
        gi.setTimer(i);
        return gi;
    }
}

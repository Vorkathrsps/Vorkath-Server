package com.cryptic.model.map.object;

import com.cryptic.cache.definitions.ObjectDefinition;
import com.cryptic.GameEngine;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.npcs.raids.cox.vasa.objects.Crystals;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.ClipUtils;
import com.cryptic.utility.SecondsTimer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * This file manages a game object entity on the globe.
 *
 * @author Relex lawl / iRageQuit2012
 * @author Jak Shadowrs
 * @author Runite team
 */
@SuppressWarnings("ALL")
public class GameObject {

    /**
     * The object's id.
     */
    private int id;

    public int originalId;

    /**
     * The object's type (default=10).
     */
    private int type = 10;

    /**
     * The object's current direction to face.
     */
    private int rotation;

    private boolean interactAble = true;
    @Setter
    private boolean custom = false;

    @Nullable
    private Tile tile;

    @Getter
    public final int x, y, z; // exact pos.

    public GameObject setTile(Tile tile) {
        if (tile != null && this.tile != null && !this.tile.equals(tile))
            throw new RuntimeException("You can't change the tile of a GameObject. Create a new one. "+this.tile+" -> "+tile);
        this.tile = tile; // ugly way of setting 'removed' state
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject that = (GameObject) o;
        return id == that.id && originalId == that.originalId && type == that.type && rotation == that.rotation && interactAble == that.interactAble && custom == that.custom && x == that.x && y == that.y && z == that.z && lastAnimationTick == that.lastAnimationTick && skipClipping == that.skipClipping && Objects.equals(tile, that.tile) && Objects.equals(spawnedFor, that.spawnedFor) && Objects.equals(timer, that.timer) && Objects.equals(changedTimestamps, that.changedTimestamps) && Objects.equals(attribs, that.attribs) && Objects.equals(walkTo, that.walkTo) && Objects.equals(skipReachCheck, that.skipReachCheck) && crystals == that.crystals;
    }

    public @Nullable Tile linkedTile() {
        return tile;
    }

    public static GameObject spawn(int i, int x, int y, int z, int i1, int i2) {
        return new GameObject(i, new Tile(x,y,z), i1, i2).spawn();
    }

    public static GameObject spawn(int i, Tile pos, int i1, int i2) {
        return spawn(i, pos.x, pos.y, pos.getZ(), i1, i2);
    }

    public GameObject spawn() {
        custom = true;
        Tile.get(x, y, z, true).addObject(this);
        World.getWorld().getPlayers().forEachFiltered(p -> tile().area(64).contains(p, true), player ->
            send(player));
        return this;
    }

    public void send(Player player) {
        if (id != -1)
            sendCreate(player);
        else
            sendRemove(player);
    }

    public void sendCreate(Player player) {
        player.getPacketSender().sendObject(this);
    }

    public void sendRemove(Player player) {
        player.getPacketSender().sendObjectRemoval(this);
    }

    public boolean isCustom() {
        return custom || id != originalId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return z;
    }

    /**
     * The {@link Player} which this {@link GameObject}
     * was spawned for.
     */
    private Optional<Player> spawnedFor = Optional.empty();

    /**
     * GameObject constructor to call upon a new game object.
     *
     * @param id   The new object's id.
     * @param tile The new object's position on the globe.
     */

    public GameObject(int id, Tile tile) {
        this.id = id;
        this.originalId = id;
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.level;
    }

    /**
     * GameObject constructor to call upon a new game object.
     *
     * @param id   The new object's id.
     * @param tile The new object's position on the globe.
     */
    public GameObject(Optional<Player> spawnedFor, int id, Tile tile) {
        this.id = id;
        this.originalId = id;
        this.spawnedFor = spawnedFor;
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.level;
    }

    /**
     * GameObject constructor to call upon a new game object.
     *
     * @param id   The new object's id.
     * @param tile The new object's position on the globe.
     * @param type The new object's type.
     */
    public GameObject(int id, Tile tile, int type) {
        this.id = id;
        this.originalId = id;
        this.type = type;
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.level;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * GameObject constructor to call upon a new game object.
     *
     * @param id       The new object's id.
     * @param tile     The new object's position on the globe.
     * @param type     The new object's type.
     * @param rotation The new object's facing position.
     */
    public GameObject(int id, Tile tile, int type, int rotation) {
        this.id = id;
        this.originalId = id;
        this.type = type;
        this.rotation = rotation;
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.level;
    }

    public GameObject(Tile tile, int id, int type, int rotation) {
        this.id = id;
        this.originalId = id;
        this.type = type;
        this.rotation = rotation;
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.level;
    }

    public GameObject(int id, Tile tile, int type, int rot, boolean custom) {
        this(id, tile, type, rot);
        this.custom = custom;
    }

    public GameObject(GameObject object, Tile tile) {
        this(object.id, tile, object.type, object.rotation);
        this.interactAble = object.interactAble;
    }

    /**
     * GameObject constructor to call upon a new game object.
     *
     * @param id                The new object's id.
     * @param disappear_seconds The new object's timer before being removed
     * @param tile              The new object's position on the globe.
     */
    public GameObject(int id, int disappear_seconds, Tile tile) {
        this.id = id;
        this.originalId = id;
        this.timer = new SecondsTimer(disappear_seconds);
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.level;
    }

    /**
     * GameObject constructor to call upon a new game object.
     *
     * @param id       The new object's id.
     * @param tile     The new object's position on the globe.
     * @param type     The new object's type.
     * @param rotation The new object's facing position.
     * @param seconds  The new object's seconds before disappearing.
     */
    public GameObject(int id, Tile tile, int type, int rotation, int seconds) {
        this.id = id;
        this.originalId = id;
        this.type = type;
        this.rotation = rotation;
        if (seconds != -1) {
            this.timer = new SecondsTimer(seconds);
        }
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.level;
    }

    /**
     * Gets the object's id.
     *
     * @return id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the object's type.
     *
     * @return type.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the object's type.
     *
     * @param type New type value to assign.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the object's current face direction.
     *
     * @return face.
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Sets the object's face direction.
     *
     * @param rotation Face value to which object will face.
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    // osrs format.
    public ObjectDefinition definition() {
        return World.getWorld().definitions().get(ObjectDefinition.class, id);
    }

    /**
     * Gets the player this object was spawned for.
     */
    public Optional<Player> getSpawnedfor() {
        return spawnedFor;
    }

    /**
     * Sets the player this object was spawned for.
     */
    public GameObject setSpawnedfor(Optional<Player> spawnedFor) {
        this.spawnedFor = spawnedFor;
        return this;
    }

    /**
     * The amount of time before the object dissapears
     **/
    private SecondsTimer timer;

    public SecondsTimer getTimer() {
        return timer;
    }

    public boolean custom() {
        return custom;
    }

    public void onTick() {
    }

    private long lastAnimationTick;

    public void animate(int id) {
        long currentTick = World.getWorld().currentTick();
        if (lastAnimationTick != currentTick) {
            lastAnimationTick = currentTick;
            World.getWorld().getPlayers().forEach(p -> {
                if (p != null && p.tile().inSqRadius(this.tile(),64)) {
                    p.getPacketSender().sendObjectAnimation(this, id);
                }
            });
        }
    }

    public int getSize() {
        if (definition() == null)
            return 1;
        return (definition().sizeX + definition().sizeY) - 1;
    }

    @Override
    public GameObject clone() {
        return new GameObject(getId(), tile(), getType(), getRotation());
    }

    @Override
    public String toString() {
        return "Object{" +
            "id=" + id +
            ", tile=" + tile() +
            ", name=" + (definition() != null ? definition().name : "unknown") +
            ", type=" + getType() +
            ", face=" + getRotation() +
                ", " +(custom ? "custom":"cache") +
                ", linktile=" + tile +
            '}';
    }

    /**
     * very important this LAZILY INITIALIZED otherwise ~100,000 gameobjects with a list will fuuuck your memory.
     */
    private List<Integer> changedTimestamps;

    public boolean stuck() {
        if (changedTimestamps == null) return false;
        changedTimestamps.removeIf(p -> p < GameEngine.gameTicksIncrementor - 50); // remove older than 50t aka 30s
        System.out.println("stuckdoors " + changedTimestamps.size() + " on tick " + GameEngine.gameTicksIncrementor);
        return changedTimestamps.size() >= 10;
    }

    public void copyAndAddOpenTimestamp(GameObject door) {
        changedTimestamps = door.changedTimestamps;
        //We must make sure that changedTimestamps is lazy initialized otherwise the memory will get out of control.
        if (changedTimestamps == null) {
            changedTimestamps = new ArrayList<>();
        }
        changedTimestamps.add(GameEngine.gameTicksIncrementor);
    }

    private Map<AttributeKey, Object> attribs;

    public Map<AttributeKey, Object> attribs() {
        return attribs;
    }

    public <T> T getAttrib(AttributeKey key) {
        return attribs == null ? null : (T) attribs.get(key);
    }

    public <T> T getAttribOr(AttributeKey key, Object defaultValue) {
        return attribs == null ? (T) defaultValue : (T) attribs.getOrDefault(key, defaultValue);
    }

    public void clearAttrib(AttributeKey key) {
        if (attribs != null)
            attribs.remove(key);
    }

    public GameObject putAttrib(AttributeKey key, Object v) {
        if (attribs == null)
            attribs = new EnumMap<>(AttributeKey.class);
        attribs.put(key, v);
        return this;
    }

    public GameObject cloneAttribs(GameObject source) {
        attribs = source.attribs;
        return this;
    }

    public static int INCREMENTING_MAPOBJ_UUID = 1;

    public boolean interactAble() {
        return interactAble;
    }

    public GameObject interactAble(boolean interactAble) {
        this.interactAble = interactAble;
        return this;
    }

    public boolean isOwnedObject() {
        return this instanceof OwnedObject;
    }

    public OwnedObject asOwnedObject() {
        return ((OwnedObject) this);
    }

    public Tile tile() {
        return new Tile(x, y, z);
    }

    public GameObject remove() {
        setId(-1);
        return this;
    }

    public GameObject add() {
        ObjectManager.addObj(this);
        return this;
    }

    public void setId(int newId) {
        if (custom && newId == -1) {
            // System.out.println("despawn custom "+this);
            if (tile != null) {
                tile.removeObject(this);
            }
            World.getWorld().getPlayers().forEachFiltered(p -> tile().area(64).contains(p, true), player ->
                sendRemove(player));
        } else {
            // there is no tile.remove because we keep the object, but change ID to a new one.
            // replacing, unclip old and reclip new
            //   System.out.println("replace "+this);
            clip(true);
            id = newId;
            Tile.get(x, y, z, true).checkActive();
            clip(false);
            World.getWorld().getPlayers().forEachFiltered(p -> tile().area(64).contains(p, true), player ->
                send(player));
        }
    }

    public Tile walkTo;
    public Predicate<Tile> skipReachCheck;
    public boolean skipClipping;

    public GameObject skipClipping(boolean skipClipping) {
        this.skipClipping = skipClipping;
        return this;
    }

    public GameObject clip(boolean remove) {
        if (id == -1 || skipClipping)
            return this;
        // when osrs data is rdy
        ObjectDefinition def = definition();
        if (def == null)
            return this;
        if (type == 22) {
            if (def.isClippedDecoration()) {
                if (def.clipType == 1) {
                    if (remove) {
                        tile.unflagDecoration();
                    } else {
                        tile.flagDecoration();
                    }
                }
            }
        } else if (type >= 9) {
            int xLength, yLength;
            if (rotation == 1 || rotation == 3) {
                xLength = def.sizeY; // invert the direction the clip will go in on purpose
                yLength = def.sizeX;
            } else {
                xLength = def.sizeX;
                yLength = def.sizeY;
            }
            if (def.clipType != 0) {
                if (remove) {
                    ClipUtils.removeClipping(x, y, z, xLength, yLength, def.tall, false);
                    if (def.tall)
                        ClipUtils.removeClipping(x, y, z, xLength, yLength, true, true);
                } else {
                    ClipUtils.addClipping(x, y, z, xLength, yLength, def.tall, false);
                    if (def.tall)
                        ClipUtils.addClipping(x, y, z, xLength, yLength, true, true);
                }
            }
        } else if (type >= 0 && type <= 3) {
            if (def.clipType != 0) {
                if (remove) {
                    ClipUtils.removeVariableClipping(x, y, z, type, rotation, def.tall, false);
                    if (def.tall)
                        ClipUtils.removeVariableClipping(x, y, z, type, rotation, true, true);
                } else {
                    ClipUtils.addVariableClipping(x, y, z, type, rotation, def.tall, false);
                    if (def.tall)
                        ClipUtils.addVariableClipping(x, y, z, type, rotation, true, true);
                }
            }
        }
        return this;
    }

    private Crystals crystals;

    public Crystals getCrystalObjects() {
        return crystals;
    }

    public GameObject setCrystals(Crystals crystals) {
        this.crystals = crystals;
        return this;
    }

    public GameObject replaceWith(GameObject obj, boolean attribTransfer) {
        ObjectManager.removeObj(this);
        GameObject newobj = ObjectManager.addObj(obj);
        if (attribTransfer) { // Used for doors, getting stuck open.
            newobj.cloneAttribs(this);
        }
        return newobj;
    }

    public Area bounds() {
        return new Area(tile().x, tile().y, tile().x + definition().sizeX - 1, tile().y + definition().sizeY - 1, tile().getZ());
    }
}

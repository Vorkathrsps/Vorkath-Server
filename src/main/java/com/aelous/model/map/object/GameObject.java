package com.aelous.model.map.object;

import com.aelous.cache.definitions.ObjectDefinition;
import com.aelous.GameEngine;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.raids.cox.vasa.objects.Crystals;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.ClipUtils;
import com.aelous.utility.SecondsTimer;

import java.util.*;
import java.util.function.Predicate;

/**
 * This file manages a game object entity on the globe.
 *
 * @author Relex lawl / iRageQuit2012
 */
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
    private boolean custom = false;

    public Tile tile;

    public static GameObject spawn(int i, int x, int y, int z, int i1, int i2) {
        return new GameObject(i, new Tile(x,y,z), i1, i2).spawn();
    }

    public static GameObject spawn(int i, Tile pos, int i1, int i2) {
        return spawn(i, pos.x, pos.y, pos.getZ(), i1, i2);
    }

    public GameObject spawn() {
        return ObjectManager.addObj(this);
    }

    public int getX() {
        return tile.getX();
    }

    public int getY() {
        return tile.getY();
    }

    public int getHeight() {
        return tile.getZ();
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
        this.tile = tile;
        this.id = id;
        this.originalId = id;
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
        this.tile = tile;
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
        this.tile = tile;
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
        this.tile = tile;
    }

    public GameObject(Tile tile, int id, int type, int rotation) {
        this.id = id;
        this.originalId = id;
        this.type = type;
        this.rotation = rotation;
        this.tile = tile;
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
        this.tile = tile;
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
        this.tile = tile;
        this.originalId = id;
        this.type = type;
        this.rotation = rotation;

        if (seconds != -1) {
            this.timer = new SecondsTimer(seconds);
        }
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
    public boolean equals(Object o) {
        if (!(o instanceof GameObject))
            return false;
        GameObject object = (GameObject) o;
        if (getSpawnedfor().isPresent()) {
            if (object.getSpawnedfor().isEmpty()) {
                return false;
            }
            if (!getSpawnedfor().get().equals(object.getSpawnedfor().get())) {
                return false;
            }
        }
        return object.tile().equals(tile())
            && object.getId() == getId()
            && object.getRotation() == getRotation()
            && object.getType() == getType();
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

    // Checks if this mapobj is still valid in the world.
    // First find the equivilent obj type on this coord (ignore invalid stuff like walls, floor decoration)
    // Then compare the object ID that matches. Otherwise when a tree gets replaced with a stump it'd still be classed as valid
    // Due to the tree and stump having the same object type.
    public boolean valid() {
        return valid(false);
    }

    public boolean valid(boolean uuidMatch) {
        GameObject currentAtTile = objByType(type, tile.x, tile.y, tile.level);
        if (currentAtTile == null) return false;
        if (uuidMatch && currentAtTile.getAttribOr(AttributeKey.MAPOBJ_UUID, -2) != getAttribOr(AttributeKey.MAPOBJ_UUID, -1)) {
            //System.out.printf(this+" vs "+currentAtTile+" didnt match!%n");
            return false;
        }
        return id == currentAtTile.getId();
    }

    public boolean isOwnedObject() {
        return this instanceof OwnedObject;
    }

    public OwnedObject asOwnedObject() {
        return ((OwnedObject) this);
    }

    public static GameObject objByType(int type, int x, int y, int level) {
        Optional<GameObject> obj = World.getWorld().getSpawnedObjs().stream().filter(o -> o.getType() == type && o.tile().equals(new Tile(x, y, level))).findAny();
        return obj.orElse(null);
    }

    public Tile tile() {
        return tile;
    }

    public GameObject remove() {
        ObjectManager.removeObj(this);
        return this;
    }

    public GameObject add() {
        ObjectManager.addObj(this);
        return this;
    }

    public void setId(int newId) {
        ObjectManager.removeObj(this);
        id = newId;
        ObjectManager.addObj(this);
    }

    public Tile walkTo;
    public Predicate<Tile> skipReachCheck;
    public boolean skipClipping;

    public GameObject skipClipping(boolean skipClipping) {
        this.skipClipping = skipClipping;
        return this;
    }

    public GameObject clip(boolean remove) {
        if (id == -1 || skipClipping || tile().getZ() > 3)
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
                        /*if (tile.x >= 2944 && tile.x<= 3330 && tile.y >= 3521 && tile.y <= 3522) {
                            System.out.println("wildy obj "+this);
                        }*/
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
                    ClipUtils.removeClipping(tile.x, tile.y, tile.level, xLength, yLength, def.tall, false);
                    if (def.tall)
                        ClipUtils.removeClipping(tile.x, tile.y, tile.level, xLength, yLength, true, true);
                } else {
                    ClipUtils.addClipping(tile.x, tile.y, tile.level, xLength, yLength, def.tall, false);
                    if (def.tall)
                        ClipUtils.addClipping(tile.x, tile.y, tile.level, xLength, yLength, true, true);
                }
            }
        } else if (type >= 0 && type <= 3) {
            if (def.clipType != 0) {
                if (remove) {
                    ClipUtils.removeVariableClipping(tile.x, tile.y, tile.level, type, rotation, def.tall, false);
                    if (def.tall)
                        ClipUtils.removeVariableClipping(tile.x, tile.y, tile.level, type, rotation, true, true);
                } else {
                    ClipUtils.addVariableClipping(tile.x, tile.y, tile.level, type, rotation, def.tall, false);
                    if (def.tall)
                        ClipUtils.addVariableClipping(tile.x, tile.y, tile.level, type, rotation, true, true);
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
}

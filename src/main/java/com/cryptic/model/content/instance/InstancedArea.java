package com.cryptic.model.content.instance;

import com.cryptic.model.World;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.region.RegionZData;
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;

@SuppressWarnings("ALL")
public class InstancedArea {

    private static final Logger logger = LogManager.getLogger(InstancedArea.class);
    private static final Marker marker = MarkerManager.getMarker("InstanceAreaMarker");
    private static final Marker markerZ = MarkerManager.getMarker("InstanceAreaMarkerZ");

    /**
     * The areas for this instanced area.
     * When a player leaves these areas they are removed from the instance.
     */
    public final List<Area> areas = Lists.newArrayList();

    /**
     * The {@link Player}s contained in this instance.
     */
    protected final List<Player> players = Lists.newArrayList();

    /**
     * The {@link NPC}s contained in this instance.
     */
    protected final List<NPC> npcs = Lists.newArrayList();

    /**
     * The height level that the instance takes place in.
     */
    private final int zLevel;

    /**
     * Determines if the {@link InstancedArea#zLevel} should be automatically freed when the
     * instance is disposed.
     */
    private boolean freeHeightLevel;

    /**
     * Has the instance been disposed? A disposed instance has all it's
     * objects, npcs, ground items removed from the world. Players are removed
     * from the instance but not from the world.
     */
    private boolean disposed;

    public int disposeCooldown = 5;

    /**
     * Contains configuration fields for this instance.
     */
    private final InstanceConfiguration configuration;

    /**
     * Creates a {@link InstancedArea}.
     *
     * This will create a new instance and also reserve a free height level using {@link InstanceHeight}.
     * You can choose your own height level using the other constructor but it's recommended you use
     * this constructor because it will automatically handle the height level reserving and freeing.
     *
     * @param configuration the {@link InstanceConfiguration}.
     * @param areas a varargs of {@link Area} which will contain the players.
     *                   If a player leaves this area they are automatically removed
     *                   from the instance.
     */
    public InstancedArea(InstanceConfiguration configuration, Area... areas) {
        this(configuration, InstanceHeight.getFreeAndReserve(), areas);
        this.freeHeightLevel = true;
    }

    public Chain<?> listener;

    private final List<GameObject> gameobjs;

    private Set<Integer> regions;

    /**
     * Creates a {@link InstancedArea}.
     *
     * @param configuration the {@link InstanceConfiguration}.
     * @param height The height level this instance takes place on.
     * @param areas a varargs of {@link Area} which will contain the players.
     *                   If a player leaves this area they are automatically removed
     *                   from the instance.
     */
    public InstancedArea(InstanceConfiguration configuration, int height, Area... areas) {
        this.configuration = configuration;
        this.areas.addAll(Arrays.stream(areas).map(area -> new Area(area, height)).toList());
        this.zLevel = height;
        this.freeHeightLevel = false;
        this.regions = RegionManager.areasToRegions.apply(areas);
        this.gameobjs = new ArrayList<>(RegionManager.loadGroupMapFiles.apply(regions, height));
        listenAfter();
    }

    public void listenAfter() {
        listener = Chain.noCtxRepeat().repeatingTask(1, c -> {
            if (disposeCooldown-- > 0)
                return;
            if (disposed) {
                c.stop();
                return;
            }
            if (players.size() == 0 && getConfiguration().isCloseOnPlayersEmpty()) {
                dispose();
                c.stop();
            }
        });
    }

    /**
     * Kill all npcs inside this instance.
     */
    public void killNpcs() {
        npcs.forEach(it -> it.hit(it, it.hp()));
    }

    /**
     * Add an {@link NPC} to the instance and calls {@link Entity#setInstance(InstancedArea)} on the npc.
     * This is the method that's called when you spawn a npc to place into the instance.
     */
    public void addNpc(NPC npc) {
        if (disposed) {
            logger.error(marker, "Attempting to add npc to instance after disposed {} {}", npc, this);
            return;
        }

        if (!npcs.contains(npc))
            npcs.add(npc);
        npc.setInstancedArea(this);
       // logger.trace(marker, "Add to instance npc={}, instance={}", npc, this);
    }

    /**
     * Set {@link InstancedArea#disposed} to true. Unregister all npcs from the game and
     * remove all players from the instance. Ground items and objects contained in this instance
     * will be removed in the next ground item/object cycle. Regions instanced will be automatically
     * removed because the instance will be freed from memory by the JVM.
     */
    public void dispose() {
        if (disposed) {
            logger.trace(marker, "Trying to dispose instance that is already disposed {}", this);
            return;
        }

        logger.trace(marker, "Disposing instance {}", this);
        disposed = true;

        if (freeHeightLevel) {
            InstanceHeight.free(zLevel);
        }

        logger.trace("dispose {} npcs ", npcs.size());
        Lists.newArrayList(getNpcs()).forEach(npc -> {
            World.getWorld().unregisterNpc(npc);
            removeNpc(npc);
        });

        Lists.newArrayList(getPlayers()).forEach(player -> {
            player.getPacketSender().sendEffectTimer(0, EffectTimer.MONSTER_RESPAWN);
            this.removePlayer(player);
        });

        for (GroundItem gi : GroundItemHandler.getGroundItems()) {
            if (!inInstanceArea(gi.getTile())) continue;
            GroundItemHandler.sendRemoveGroundItem(gi);
        }
        regions.forEach(r -> {
            var reg = RegionManager.getRegion(r);
            if (reg.customZObjectTiles != null) {
                RegionZData store = reg.customZObjectTiles.remove(zLevel);
                for (int z = 0; z < 4; z++) {
                    for (int x = 0; x < 64; x++) {
                        for (int y = 0; y < 64; y++) {
                            if (store.tiles != null
                                && z < store.tiles.length
                                && store.tiles[z] != null
                                && x < store.tiles[z].length
                                && y < store.tiles[z][x].length) {
                                var t = (Tile) store.tiles[z][x][y];
                                if (t != null) {
                                    if (t.gameObjects != null && !t.gameObjects.isEmpty()) {
                                        new ArrayList<GameObject>(t.gameObjects).forEach(o -> {
                                            o.setCustom(true);
                                            if (o.linkedTile() != null)
                                                o.linkedTile().removeObject(o);
                                        });
                                        if (t.gameObjects != null) // required as removeOnRegion^ can nullify the backing field
                                            t.gameObjects.clear();
                                    }
                                    t.gameObjects = null;
                                }
                                store.tiles[z][x][y] = null;
                            }
                        }
                    }
                }
            }
            reg.recentCachedBaseZData = null;
            reg.recentCachedBaseZLevel = -1;
        });
        regions.clear();
        gameobjs.clear();
        npcs.clear();
        players.clear();
    }

    /**
     * Remove an {@link NPC} to the instance and calls {@link Mob#setInstance(InstancedArea)} on the npc.
     */
    public void removeNpc(NPC npc) {
        npcs.remove(npc);
        npc.setInstancedArea(null);
        //logger.trace(marker, "Remove from instance npc={}, instance={}", npc, this);
    }

    /**
     * Get an unmodifiable list that contains the {@link NPC}s inside this instance.
     */
    public List<NPC> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

    /**
     * Add a {@link Player} to the instance and calls {@link Mob#setInstance(InstancedArea)} on the player.
     * This should not be called manually from any instance code, this is called by the {@link NPC} deregistration.
     */
    public void addPlayer(Player player) {
        if (disposed) {
            logger.error(marker, "Attempting to add player to instance after diposed {} {}", player, this);
            return;
        }

        if (!players.contains(player)) {
            players.add(player);
        }
        if (player.getInstancedArea() != this)
            player.setInstancedArea(this);
        //logger.trace(marker, "Add to instance player={}, instance={}", player, this);
    }

    /**
     * Remove an {@link Player} to the instance and calls {@link Mob#setInstance(InstancedArea)} on the player.
     */
    public void removePlayer(Player player) {
        players.remove(player);
        if (player.getInstancedArea() == this)
            player.setInstancedArea(null);
       // logger.trace(marker, "Remove from instance player={}, instance={}", player, this);

        if (!disposed && players.isEmpty() && configuration.isCloseOnPlayersEmpty()) {//probs needs this flag
            logger.trace(marker, "Players list is empty, closing instance {}", this);
            dispose();
        }
    }

    /**
     * Get an unmodifiable list that contains the {@link Player}s inside this instance.
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * If you used the {@link InstancedArea#InstancedArea(InstanceConfiguration, int, Area...)}
     * constructor this will return the height you supplied to that constructor. If you used the
     * {@link InstancedArea#InstancedArea(InstanceConfiguration, Area...)} constructor it will
     * return the height level reserved by {@link InstanceHeight}.
     * @return the height level.
     */
    public int getReservedHeight() {
        return zLevel;
    }

    /**
     * The area or location of this instanced area
     *
     * @return the area
     */
    public List<Area> getAreas() {
        return areas;
    }

    /**
     * Check if an instance is disposed.
     * @return <code>true</code> if the instance is disposed.
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Get the {@link InstanceConfiguration}.
     * @return the {@link InstanceConfiguration}
     */
    public InstanceConfiguration getConfiguration() {
        return configuration;
    }

    public void tick(Entity mob) { }

    public boolean handleDeath(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return "InstancedArea{" +
            "class=" + getClass() +
            ", players=" + players.size() +
            ", npcs=" + npcs.size() +
            ", disposed=" + disposed +
            ", areas=" + areas +
            ", zLevel=" + zLevel +
            ", configuration=" + configuration +
            '}';
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inInstanceArea(Tile tile) {
        for (Area area : getAreas()) {
            if (area.contains(tile, false)) { // first, verify the x,y area
                // now verify in the 4 allowed height levels
                var remainder = getzLevel() % 4;
                var z = getzLevel();
                if (remainder != 0) {
                    // level 6 becomes 6-2 = 4. base z achieved.
                    z = getzLevel() - remainder;
                    logger.debug(markerZ, "normalized z level to {} via {}", z, remainder);
                }
                var inside = tile.getZ() >= z && tile.getZ() <= z + 3;
                logger.debug(markerZ, "inside={} check {} z range{}-{} for {}", inside, area, z, z+3, tile);
                if (inside) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inInstanceArea(Entity mob) {
        var v = inInstanceArea(mob.tile());
        if (!v && mob.isPlayer()) {
            logger.debug(markerZ, "tile not in any areas! {} vs {}", mob.tile(), Arrays.toString(areas.toArray()));
        }
        return v;
    }

    public int getzLevel() {
        return zLevel;
    }
}

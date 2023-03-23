package com.aelous.model.content.instance;

import com.aelous.model.World;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
public class InstancedArea {

    private static final Logger logger = LogManager.getLogger(InstancedArea.class);
    private static final Marker marker = MarkerManager.getMarker("InstanceAreaMarker");
    private static final Marker markerZ = MarkerManager.getMarker("InstanceAreaMarkerZ");

    /**
     * The areas for this instanced area.
     * When a player leaves these areas they are removed from the instance.
     */
    private final List<Area> areas = Lists.newArrayList();

    /**
     * The {@link Player}s contained in this instance.
     */
    protected final List<Player> players = Lists.newArrayList();

    /**
     * The {@link NPC}s contained in this instance.
     */
    protected final List<NPC> npcs = Lists.newArrayList();

    protected final List<GameObject> gameobjs = Lists.newArrayList();

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
        listenAfter();

    }

    public void listenAfter() {
        listener = Chain.noCtx().name("instancecheck").repeatingTask(1, c -> {
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
        npc.setInstance(this);
        logger.trace(marker, "Add to instance npc={}, instance={}", npc, this);
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

        //all this works problem is list is empty for bot player and npc
        logger.trace("dispose {} npcs ", npcs.size());
        Lists.newArrayList(getNpcs()).forEach(npc -> {
            World.getWorld().unregisterNpc(npc);
            removeNpc(npc);
        });

        //System.out.println("dispose players "+getPlayers());
        Lists.newArrayList(getPlayers()).forEach(player -> {
            player.getPacketSender().sendEffectTimer(0, EffectTimer.MONSTER_RESPAWN);
            this.removePlayer(player);
        });

        logger.trace(marker, "Disposed instance: remove {} objects from {}", gameobjs.size(), this);
        for (GameObject gameobj : Lists.newArrayList(gameobjs)) {
            gameobj.remove();
        }
        gameobjs.clear();

        for (GroundItem gi : GroundItemHandler.getGroundItems()) {
            if (!inInstanceArea(gi.getTile()))
                continue;

            GroundItemHandler.sendRemoveGroundItem(gi);
        }
    }

    public void addGameObj(GameObject o) {
        if (disposed) {
            logger.error(marker, "Attempting to add o to instance after diposed {} {}", o, this);
            return;
        }
        if (!gameobjs.contains(o)) {
            gameobjs.add(o);
        }
        logger.trace(marker, "Add to instance o={}, instance={}", o, this);
    }

    /**
     * Remove an {@link NPC} to the instance and calls {@link Mob#setInstance(InstancedArea)} on the npc.
     */
    public void removeNpc(NPC npc) {
        npcs.remove(npc);
        npc.setInstance(null);
        logger.trace(marker, "Remove from instance npc={}, instance={}", npc, this);
    }

    /**
     * Get an unmodifiable list that contains the {@link NPC}s inside this instance.
     */
    public List<NPC> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

    public List<GameObject> getGameobjs() {
        return Collections.unmodifiableList(gameobjs);
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
        player.setInstance(this);
        logger.trace(marker, "Add to instance player={}, instance={}", player, this);
    }

    /**
     * Remove an {@link Player} to the instance and calls {@link Mob#setInstance(InstancedArea)} on the player.
     */
    public void removePlayer(Player player) {
        players.remove(player);
        player.setInstance(null);
        logger.trace(marker, "Remove from instance player={}, instance={}", player, this);

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
     * Get the {@link InstancedArea#zLevel} and adds the {@link InstanceConfiguration#getRelativeHeight()} to it.
     *
     * Use {@link InstancedArea#getReservedHeight} to get the height level reserved from {@link InstanceHeight}
     * (or you're entered height if you used the {@link InstancedArea#InstancedArea(InstanceConfiguration, int, Area...)}
     * constructor).)
     *
     * @return the height that all actions inside this instance will take place in
     */
    public int getZLevel() {
        return zLevel + configuration.getRelativeHeight();
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
     * Resolves a relative height level for this instance.
     *
     * Takes the {@param height} and adds {@link InstancedArea#getZLevel()}} to it,
     * giving us a relative height level for the instance.
     *
     * @param height the absolute height (0-3).
     * @return the relative height.
     */
    public int resolveHeight(int height) {
        return height + getZLevel();
    }

    /**
     * Resolve a position to be at the correct coordinates for this instance.
     *
     * It takes the {@param position} and returns a new position with that
     * positions height, plus the value of {@link InstancedArea#getZLevel()},
     * giving a relative position for this instance. Does not change the X/Y positions.
     *
     * @param tile The coordinates without any instance height adjusted (meaning 0 through 3).
     * @return {@link Tile} with the height adjusted to be inside the instance.
     */
    public Tile resolve(Tile tile) {
        return tile.withHeight(tile.getZ() + getZLevel());
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
            ", objs=" + gameobjs.size() +
            '}';
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inInstanceArea(Tile tile) {
        for (Area area : getAreas()) {
            if (area.contains(tile, false)) { // first, verify the x,y area
                // now verify in the 4 allowed height levels
                var remainder = getZLevel() % 4;
                var z = getZLevel();
                if (remainder != 0) {
                    // level 6 becomes 6-2 = 4. base z achieved.
                    z = getZLevel() - remainder;
                    logger.debug(markerZ, "normalized z level to {} via {}", z, remainder);
                }
                var inside = tile.getZ() >= z && tile.getZ() <= z + 3;
                logger.debug(markerZ, "inside={} check {} z{} at custom {} for {}", inside, area, z, z+3, tile);
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

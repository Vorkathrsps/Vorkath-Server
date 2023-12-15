package com.cryptic.model.content.instance;

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
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class InstancedArea {

    private static final Logger logger = LogManager.getLogger(InstancedArea.class);
    private static final Marker marker = MarkerManager.getMarker("InstanceAreaMarker");
    private static final Marker markerZ = MarkerManager.getMarker("InstanceAreaMarkerZ");

    private final List<Area> areas = Lists.newArrayList();
    protected final List<Player> players = Lists.newArrayList();
    protected final List<NPC> npcs = Lists.newArrayList();
    private final int zLevel;
    private boolean freeHeightLevel;
    private boolean disposed;
    public int disposeCooldown = 5;
    private final InstanceConfiguration configuration;

    public InstancedArea(InstanceConfiguration configuration, Area... areas) {
        this(configuration, InstanceHeight.getFreeAndReserve(), areas);
        this.freeHeightLevel = true;
    }

    public Chain<?> listener;
    private final List<GameObject> gameobjs = Lists.newArrayList();
    private IntOpenHashSet regions = IntOpenHashSet.of();

    public InstancedArea(InstanceConfiguration configuration, int height, Area... areas) {
        this.configuration = configuration;
        this.areas.addAll(Arrays.stream(areas).map(area -> new Area(area, height)).toList());
        this.zLevel = height;
        this.freeHeightLevel = false;
        regions.addAll(RegionManager.areasToRegions.apply(areas));
        gameobjs.addAll(RegionManager.loadGroupMapFiles.apply(regions, height));
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

    public void killNpcs() {
        npcs.forEach(it -> it.hit(it, it.hp()));
    }

    public void addNpc(NPC npc) {
        if (disposed) {
            logger.error(marker, "Attempting to add npc to instance after disposed {} {}", npc, this);
            return;
        }

        if (!npcs.contains(npc))
            npcs.add(npc);
        npc.setInstancedArea(this);
        logger.trace(marker, "Add to instance npc={}, instance={}", npc, this);
    }

    public void dispose() {
        if (disposed) {
            logger.trace(marker, "Trying to dispose instance that is already disposed {}", this);
            return;
        }
        logger.trace(marker, "Disposing instance {}", this);
        disposed = true;
        if (freeHeightLevel) InstanceHeight.free(zLevel);
        for (GroundItem groundItem : Lists.newArrayList(GroundItemHandler.getGroundItems())) {
            if (!inInstanceArea(groundItem.getTile())) continue;
            GroundItemHandler.sendRemoveGroundItem(groundItem);
        }
        if (players != null)
            for (var p : Lists.newArrayList(players)) {
                if (p == null) continue;
                p.getPacketSender().sendEffectTimer(0, EffectTimer.MONSTER_RESPAWN);
                this.removePlayer(p);
            }
        if (npcs != null)
            for (var n : Lists.newArrayList(npcs)) {
                if (n == null) continue;
                n.remove();
            }
        if (gameobjs != null)
            for (var o : Lists.newArrayList(gameobjs)) {
                if (o != null && o.linkedTile() != null) {
                    o.linkedTile().removeObject(o);
                }
            }
        System.out.println("game objs: " + gameobjs.size());
        if (regions != null)
            for (var r : Lists.newArrayList(regions)) {
                if (r == null) continue;
                var reg = RegionManager.getRegion(r);
                if (reg.customZObjectTiles != null) reg.customZObjectTiles.remove(zLevel);
                reg.recentCachedBaseZData = null;
                reg.recentCachedBaseZLevel = -1;
            }
        logger.trace(marker, "disposing regions {} disposing game objs {} disposing npcs {} disposing players {} ", regions.size(), gameobjs.size(), npcs.size(), players.size());
        areas.clear();
        npcs.clear();
        players.clear();
        regions.clear();
        gameobjs.clear();
        logger.trace(marker, "players size {} npcs size {} game object size {} region size {} ", players.size(), npcs.size(), gameobjs.size(), regions.size());
    }

    public void removeNpc(NPC npc) {
        npcs.remove(npc);
        npc.setInstancedArea(null);
        logger.trace(marker, "Remove from instance npc={}, instance={}", npc, this);
    }

    public List<NPC> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

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
        logger.trace(marker, "Add to instance player={}, instance={}", player, this);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (player.getInstancedArea() == this)
            player.setInstancedArea(null);
        logger.trace(marker, "Remove from instance player={}, instance={}", player, this);

        if (!disposed && players.isEmpty() && configuration.isCloseOnPlayersEmpty()) {//probs needs this flag
            logger.trace(marker, "Players list is empty, closing instance {}", this);
            dispose();
        }
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getReservedHeight() {
        return zLevel;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Get the {@link InstanceConfiguration}.
     *
     * @return the {@link InstanceConfiguration}
     */
    public InstanceConfiguration getConfiguration() {
        return configuration;
    }

    public void tick(Entity mob) {
    }

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
                logger.debug(markerZ, "inside={} check {} z range{}-{} for {}", inside, area, z, z + 3, tile);
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

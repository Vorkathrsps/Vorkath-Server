package com.aelous.model.items.ground;

import com.aelous.model.content.instance.InstancedArea;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.GameMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.inter.lootkeys.LootKey;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An instance of a ground item (items shown on the floor when they are
 * dropped).
 *
 * @author Patrick van Elderen
 *
 */
public final class GroundItem {
    private static final Logger logger = LogManager.getLogger(GroundItem.class);

    public GameMode droppedFromGamemode;

    public enum State {
        SEEN_BY_OWNER,
        SEEN_BY_EVERYONE,
        HIDDEN
    }

    /**
     * The ground item
     */
    private Item item;

    /**
     * The position of the ground item
     */
    private Tile tile;

    /**
     * The owner of the ground item
     */
    private final Player player;

    /**
     * Checks if the ground item was already removed
     */
    private boolean removed;

    /**
     * The amount of ticks before the type changes to PUBLIC
     */
    private int timer;

    /**
     * The current state of the ground item
     */
    private State state = State.SEEN_BY_OWNER;

    private boolean respawns = false;

    private int respawnTimer = 100;

    /**
     * USERNAME
     */
    private String pkedFrom;

    private boolean vanishes = false;

    /**
     * If other players cannot see our items, maybe we've not played for 30 minutes OR its food in the wild (brew dropping)
     */
    private boolean hidden = false;

    private LootKey lootKey;

    private InstancedArea instance;

    public InstancedArea getInstance() {
        return instance;
    }

    public GroundItem setInstance(InstancedArea instance) {
        this.instance = instance;
        return this;
    }

    /**
     * Constructs a new ground item object
     *
     * @param item
     *            The ground item
     * @param tile
     *            The position of the ground item
     * @param owner
     *            The player that owns the ground item
     */
    public GroundItem(Item item, Tile tile, Player owner) {
        this.item = item;
        this.tile = tile;
        this.player = owner;
        if (owner != null) {
            this.droppedFromGamemode = owner.getGameMode();
            if (owner.<Long>getAttribOr(AttributeKey.GAME_TIME,0L) < 3000L) {
                hidden = true;
            }
        }

        if (owner == null) {
            broadcasted = true;
            state = State.SEEN_BY_EVERYONE;
        }
    }

    /**
     * Gets the associated item.
     *
     * @return the associated item
     */
    public Item getItem() {
        return item;
    }

    /**
     * Sets the ground items item
     *
     * @param item
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Returns the items location
     *
     * @return the position of the ground item
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Sets the location of the ground item
     *
     * @param tile
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public long getOwnerHash() {
        return player == null ? -1 : player.getLongUsername();
    }

    /**
     * Checks if the item was already removed
     *
     * @return removed
     */
    public boolean isRemoved() {
        return removed;
    }

    /**
     * Activates or disables ground items
     *
     * @param removed
     */
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    /**
     * Decreases ground item timer by one.
     */
    public int decreaseTimer() {
        return timer--;
    }

    public GroundItem setTimer(int timer) {
        this.timer = timer;
        return this;
    }

    /**
     * Gets the ground item timer.
     *
     * @return the ground item timer
     */
    public int getTimer() {
        return timer;
    }

    public State getState() {
        return state;
    }

    /**
     * Sets the current ground item state
     *
     * @param state
     *            The current state
     */
    public GroundItem setState(State state) {
        this.state = state;
        return this;
    }

    /**
     * Gets the item owner's username.
     *
     * @return the droppers username
     */
    public Player getPlayer() {
        return player;
    }

    public int respawnTimer() {
        return respawnTimer;
    }

    public boolean respawns() {
        return respawns;
    }

    public boolean vanishes() {
        return vanishes;
    }

    public GroundItem vanishes(boolean vanishes) {
        this.vanishes = vanishes;
        return this;
    }

    public GroundItem respawns(boolean b) {
        respawns = b;
        return this;
    }

    /**
     *
     * @param v Delay before respawn, in game ticks.
     * @return
     */
    public GroundItem respawnTimer(int v) {
        respawnTimer = v;
        return this;
    }

    /**
     * USERNAME
     * @return
     */
    public String pkedFrom() {
        return pkedFrom;
    }

    /**
     *  Mark item as from PvP to avoid ironmen picking it up.
     */
    public GroundItem pkedFrom(String name) {
        pkedFrom = name;
        return this;
    }

    private final long spawned = System.currentTimeMillis();
    private boolean broadcasted = false;
    private boolean forceBroadcast;

    public boolean broadcasted() {
        return broadcasted;
    }

    public GroundItem broadcasted(boolean b) {
        broadcasted = b;
        return this;
    }

    public GroundItem forceBroadcast(boolean b) {
        forceBroadcast = b;
        return this;
    }

    public boolean shouldBroadcast() {
        return forceBroadcast || (!hidden && System.currentTimeMillis() >= spawned + 60_000);
    }

    public boolean inDistance(Player player) {
        return player.distanceToPoint(getTile().getX(), getTile().getY()) <= 60;
    }

   /* public boolean isVisible(Player player) {
        if (player.tile().getZ() == getTile().getZ() && getInstance() != player.getInstance()) {
            logger.info("Z match but instance not {} {} {}", player.tile().getZ(), getInstance(), player.getInstance());
        }
        return getInstance() == player.getInstance()
            && player.tile().getZ() == getTile().getZ()
            && inDistance(player);
    }*/

    public boolean ownerMatches(Player vs) {
        return vs.getLongUsername().equals(getOwnerHash());
    }

    public GroundItem spawn() {
        GroundItemHandler.createGroundItem(this);
        return this;
    }

    public GroundItem linkLootKey(LootKey lootKey) {
        this.lootKey = lootKey;
        return this;
    }

    public LootKey lootKey() {
        return lootKey;
    }

    public GroundItem hidden() {
        hidden = true;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String toString() {
        return "GroundItem [item=" + item + ", owner=" + player + ", removed=" + removed + ", timer=" + timer + ", state=" + state + ", position="+ getTile() +", instance=" + instance+"]";
    }

}

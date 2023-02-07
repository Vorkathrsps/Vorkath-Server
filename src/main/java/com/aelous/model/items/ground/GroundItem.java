package com.aelous.model.items.ground;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;

/**
 * An instance of a ground item (items shown on the floor when they are
 * dropped).
 * 
 * @author Patrick van Elderen
 *
 */
public final class GroundItem {

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

    private String pkedFrom;

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

        if (owner == null) {
            broadcasted = true;
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

    public void setTimer(int timer) {
        this.timer = timer;
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
    public void setState(State state) {
        this.state = state;
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

    public String pkedFrom() {
        return pkedFrom;
    }

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
        return forceBroadcast || (System.currentTimeMillis() >= spawned + 60_000);
    }

    public boolean ownerMatches(Player vs) {
        return vs.getLongUsername().equals(getOwnerHash());
    }

    @Override
    public String toString() {
        return "GroundItem [item=" + item + ", owner=" + player + ", removed=" + removed + ", timer=" + timer + ", state=" + state + ", position="+ getTile()+"]";
    }

}

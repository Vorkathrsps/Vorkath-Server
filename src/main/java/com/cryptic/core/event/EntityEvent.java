package com.cryptic.core.event;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.map.position.Tile;

public class EntityEvent<T extends Entity> extends Event<T> {

    public EntityEvent(ContinuationScope continuationScope, T context) {
        super(continuationScope, context);
    }

    public EntityEvent(T context) {
        this(EventWorker.CONTINUATION_SCOPE, context);
    }

    public void waitForTile(Tile tile, int timeout) {
        waitUntil(() -> tile.equals(context.tile()), timeout);
    }

    public void waitForTile(Tile tile) {
        waitForTile(tile, Integer.MAX_VALUE);
    }

    public void waitForTile(int x, int y, int z, int timeout) {
        waitForTile(new Tile(x, y, z), timeout);
    }

    public void waitForTile(int x, int y, int z) {
        waitForTile(new Tile(x, y, z));
    }

    public void waitForTile(int x, int y) {
        waitForTile(x, y, 0);
    }

}

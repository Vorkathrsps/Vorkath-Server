package com.aelous.core;

import com.aelous.model.World;
import com.aelous.model.entity.NodeType;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 * A synchronization task executed under a {@link GameSyncExecutor}. The
 * mob instance associated to {@code index} must be the mutex of a
 * synchronization block wrapped around the code.
 * 
 * @author lare96 <http://github.org/lare96>
 */
public abstract class GameSyncTask {

    /**
     * The type of mob using this synchronization task.
     */
    private final NodeType type;

    /**
     * The flag that determines if this should be executed concurrently.
     */
    private final boolean concurrent;

    /**
     * The indexes of the mobs that will be updated. Order is important.
     */
    private final List<Integer> indices;

    /**
     * Creates a new {@link GameSyncTask}.
     *  @param type
     *            the type of mob using this synchronization task.
     * @param concurrent
     *            the flag that determines if this should be executed
     * @param indices
     *            the indexes of the mobs who are updated in this task.
     */
    public GameSyncTask(NodeType type, boolean concurrent, List<Integer> indices) {
        Preconditions.checkArgument(type == NodeType.PLAYER || type == NodeType.NPC, "Invalid node type.");
        this.type = type;
        this.concurrent = concurrent;
        this.indices = indices;
    }

    /**
     * Creates a new {@link GameSyncTask} that will be executed concurrently.
     *
     * @param type
     *            the type of mob using this synchronization task.
     * @param indices
     */
    public GameSyncTask(NodeType type, List<Integer> indices) {
        this(type, true, indices);
    }

    /**
     * Determines if the {@code index} about to be passed onto the
     * synchronization task logic is a valid mob.
     * 
     * @param index
     *            the index about to be used.
     * @return {@code true} if the index is valid, {@code false} otherwise.
     */
    protected boolean checkIndex(int index) {
        return type == NodeType.PLAYER ? World.getWorld().getPlayers().get(index) != null : World.getWorld().getNpcs().get(index) != null;
    }

    /**
     * Executes the synchronization logic.
     * 
     * @param index
     *            the mob index the synchronization is being done for.
     */
    public abstract void execute(final int index) throws Exception;

    /**
     * Determines if this should be executed concurrently.
     * 
     * @return {@code true} if the task is concurrent, {@code false} otherwise.
     */
    public final boolean isConcurrent() {
        return concurrent;
    }

    /**
     * The indices of the mobs that are used in this task.
     *
     * @return the list of indices.
     */
    public final List<Integer> getIndices() {
        return indices;
    }
}

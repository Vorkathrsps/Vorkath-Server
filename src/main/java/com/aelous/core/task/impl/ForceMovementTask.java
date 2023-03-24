package com.aelous.core.task.impl;

import com.aelous.core.task.Task;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

/**
 * A {@link Task} implementation that handles forced movement.
 * An example of forced movement is the Wilderness ditch.
 *
 * @author Professor Oak
 */
/**
 * A {@link Task} implementation that handles forced movement.
 * An example of forced movement is the Wilderness ditch.
 * @author Professor Oak
 */
public class ForceMovementTask extends Task {

    private final Player player;
    private final Tile end;
    private final Tile start;
    public Tile finish;

    public int ticks;

    /**
     * Force Walking ForceMovement
     * @param player
     * @param delay
     * @param forceM
     */
    public ForceMovementTask(Player player, int delay, ForceMovement forceM) {
        super("ForceMovementTask", delay, player, true);
        this.player = player;
        this.start = forceM.getStart().copy();
        this.end = forceM.getEnd().copy();
        player.getMovementQueue().clear();
        player.setForceMovement(forceM);
        int x = start.getX() + end.getX();
        int y = start.getY() + end.getY();
        finish = new Tile(x, y, player.getZ());
        player.stepAbs(x, y, MovementQueue.StepType.FORCED_WALK);
        player.getMovementQueue().forceMove(finish);
    }

    public ForceMovementTask(Player player, ForceMovement forcemove) {
        super("ForceMovementTask", 1, player, true);
        this.player = player;
        this.start = forcemove.getStart().copy();
        this.end = forcemove.getEnd().copy();
        player.getMovementQueue().clear();
        player.setForceMovement(forcemove);
        int x = start.getX() + end.getX();
        int y = start.getY() + end.getY();
        finish = new Tile(x, y, player.getZ());
        player.stepAbs(x, y, MovementQueue.StepType.FORCED_WALK);
        player.getMovementQueue().forceMove(finish);
    }

    @Override
    protected void execute() {
        ticks++;

        if (ticks == 10) {
            System.err.println("finished.....");
            player.teleport(finish);
            stop();
        }
    }
}

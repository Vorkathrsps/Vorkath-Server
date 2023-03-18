package com.aelous.model.entity;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.RegionManager;
import com.aelous.model.map.route.ClipUtils;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.Debugs;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.aelous.model.entity.attributes.AttributeKey.MOVEMENT_PACKET_STEPS;
import static com.aelous.model.map.route.routes.DumbRoute.getStepDirection;

/**
 * A queue of {@link Direction}s which a {@link Entity} will follow.
 *
 * @author Graham Edgecombe Edited by Gabbe
 */
public class MovementQueue {

    /**
     * The maximum size of the queue. If any additional steps are added, they are
     * discarded.
     */
    private static final int MAXIMUM_SIZE = 100;

    /**
     * The mob whose walking queue this is.
     */
    private final Entity entity;
    private boolean blockMovement = false;

    /**
     * Are we running.
     */
    private boolean running;

    // runite
    public int followX = -1, followY = -1;

    // runite
    public int lastFollowX = -1, lastFollowY = -1;

    /**
     * Creates a walking queue for the specified mob.
     *
     * @param entity
     *            The mob.
     */
    public MovementQueue(Entity entity) {
        this.entity = entity;
    }

    /**
     * Checks if we can walk from one position to another.
     *
     * @param from
     * @param to
     * @param size
     * @return
     */
    public static boolean canWalk(Tile from, Tile to, int size) {
        return RegionManager.canMove(from, to, size, size);
    }
    /**
     * Steps away from a Gamemob
     *
     * @param entity
     *            The gamemob to step away from
     */
    public static void clippedStep(Entity entity, int size) {
        if (entity.getMovementQueue().canWalk(-size, 0))
            entity.getMovementQueue().walkTo(-size, 0);
        else if (entity.getMovementQueue().canWalk(size, 0))
            entity.getMovementQueue().walkTo(size, 0);
        else if (entity.getMovementQueue().canWalk(0, -size))
            entity.getMovementQueue().walkTo(0, -size);
        else if (entity.getMovementQueue().canWalk(0, size))
            entity.getMovementQueue().walkTo(0, size);
    }


    public void interpolate(Tile tile) {
        interpolate(tile.x, tile.y, StepType.REGULAR);
    }

    public void interpolate(int toX, int toY) {
        interpolate(toX, toY, StepType.REGULAR);
    }

    public void interpolate(Tile tile, StepType type) {
        interpolate(tile.x, tile.y, type, Integer.MAX_VALUE);
    }

    public void interpolate(int tx, int tz, StepType type) {
         interpolate(tx, tz, type, Integer.MAX_VALUE);
    }

    public void step(int x, int y, StepType type) {
        entity.stepAbs(x, y, type);
    }


    public void step(Direction dir) {
        step(dir, StepType.REGULAR);
    }

    public void step(Direction dir, StepType type) {
        entity.stepAbs(dir.x, dir.y, type);
    }

    public void step(int x, int y) {
        entity.stepAbs(x, y, StepType.REGULAR);
    }

    public void interpolate(int destX, int destY, StepType stepType, int maxSteps) {
        if (stepType == StepType.REGULAR) {
            DumbRoute.route(entity, destX, destY);
            return;
        }

        entity.resetSteps();
        int x = entity.getAbsX();
        int y = entity.getAbsY();
        int z = entity.getZ();
        int size = entity.getSize();
        while (true) {
            com.aelous.model.map.route.Direction stepDir = getStepDirection(ClipUtils.NONE, x, y, z, size, destX, destY);
            if (stepDir == null)
                return;
            x += stepDir.deltaX;
            y += stepDir.deltaY;
            if (!entity.addStep(x, y) || (x == destX && y == destY))
                return;
        }

    }

    public static boolean dumbReachable(int destX, int destY, Tile start) {
        int x = start.getX();
        int y = start.getY();
        int z = start.getZ();
        int size = 1;
        var maxSteps = 0;
        while (true) {
            com.aelous.model.map.route.Direction stepDir = getStepDirection(ClipUtils.REGULAR, x, y, z, size, destX, destY);
            if (stepDir == null || maxSteps++ > 50)
                break;
            x += stepDir.deltaX;
            y += stepDir.deltaY;
            if ((x == destX && y == destY))
                return true;
        }
        return false;
    }

    public void walkTo(Tile tile) {
        walkTo(tile.x, tile.y);
    }

    /**
     * Adds a step to walk to the queue. This method shouldn't be used for player walking.
     *
     * @param x
     *            X to walk to
     * @param y
     *            Y to walk to
     */
    public void walkTo(int x, int y) {
        // theres nothing on the map at x/y 0-128 just black regions, if you called this method
        // you probably want to translate(+x, +y) not walk to exact coordinates
        if (x > 128 || y > 128) {
            // walk to exact
            interpolate(new Tile(x, y, entity.getZ()));
        } else {
            // translate difs
            Tile tile = entity.tile().transform(x, y);
            interpolate(tile, StepType.REGULAR);
        }
    }


    public void forceMove(Tile step) {
    // TODO
    }


    public boolean canMove() {
        return canMove(false);
    }
    /**
     * note; you don't want to send messages here. this should be silent checks.
     * @return
     */
    public boolean canMove(boolean sendMessage) {
        if (entity.isNeedsPlacement()) {
            return false;
        }

        if (entity.frozen()) {
            if (sendMessage) {
                entity.message("A magical force stops you from moving.");
            }
            return false;
        }

        List<Integer> ALWAYS_LOCKED_FROM_MOVEMENT = Arrays.asList(GREAT_OLM_RIGHT_CLAW_7553, GREAT_OLM_LEFT_CLAW_7555, GREAT_OLM_7554, COMBAT_DUMMY, UNDEAD_COMBAT_DUMMY, FUMUS, UMBRA, CRUOR, GLACIES, AWAKENED_ALTAR, AWAKENED_ALTAR_7290, AWAKENED_ALTAR_7292, AWAKENED_ALTAR_7294);
        if(entity.isNpc()) {
            NPC npc = entity.getAsNpc();
            if(ALWAYS_LOCKED_FROM_MOVEMENT.stream().anyMatch(n -> n == npc.id()) || npc.completelyLockedFromMoving()) {
                return false;
            }
        }
        if (entity.<Boolean>getAttribOr(AttributeKey.LOCKED_FROM_MOVEMENT, false) || entity.isMoveLocked()) {
            return false;
        }

        if (blockMovement) {
            //System.out.println("movement blocked in canMove()");
            if (sendMessage) {
                entity.message("You can't move right now.");
            }
            return false;
        }

        if(entity.stunned()) {
            if (sendMessage) {
                entity.message("You're stunned and cannot move.");
            }
            return false;
        }
        if (entity.dead())
            return false;
        return true;
    }

    public boolean canWalk(int deltaX, int deltaY) {
        if (!canMove()) {
            return false;
        }

        final Tile to = new Tile(entity.tile().getX() + deltaX,
            entity.tile().getY() + deltaY, entity.tile().getLevel());
        if (entity.tile().getLevel() == -1 && to.getLevel() == -1)
            return true;
        return canWalk(entity.tile(), to, entity.getSize());
    }

    public boolean canWalkNoLogicCheck(int deltaX, int deltaY) {
        // removed canMove on purpose. you can spear a frozen target and they'll move
        final Tile to = new Tile(entity.tile().getX() + deltaX,
            entity.tile().getY() + deltaY, entity.tile().getLevel());
        if (entity.tile().getLevel() == -1 && to.getLevel() == -1)
            return true;
        return canWalk(entity.tile(), to, entity.getSize());
    }

    /**
     * @return true if the mob is moving.
     */
    public boolean isMoving() {
        return !isAtDestination();
    }


    /**
     * Stops the movement.
     */
    public MovementQueue clear() {
        entity.clearAttrib(MOVEMENT_PACKET_STEPS);
        //no need to add step at current location (graham pathing)
        Debugs.WALK.debug(entity, "walk reset");
        reset(); // runite reset
        return this;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    public boolean isRunning() {
        if(entity.isPlayer()) {
            return ((Player) entity).getAttribOr(AttributeKey.IS_RUNNING, false);
        }
        return running;
    }

    public MovementQueue setBlockMovement(boolean blockMovement) {
        if (entity.isNpc() && entity.getAsNpc().permaBlockedMovement()) {
            return this;
        }
        this.blockMovement = blockMovement;
        return this;
    }

    public boolean isMovementBlocked() {
        return blockMovement;
    }

    public boolean hasMoved() {
        return entity.getWalkingDirection() != Direction.NONE || entity.getRunningDirection() != Direction.NONE || entity.isNeedsPlacement();
    }


    public enum StepType {
        REGULAR, FORCED_WALK, FORCED_RUN
    }

    public int readOffset, writeOffset;

    public boolean isAtDestination() {
        return readOffset >= writeOffset;
    }

    public StepType stepType = StepType.REGULAR;

    public void reset() {
        readOffset = 0;
        writeOffset = 0;
        stepType = StepType.REGULAR;
    }

    protected int[] stepsX, stepsY;

    protected boolean step(Entity entity) {
        if(isAtDestination())
            return false;

        int stepX = stepsX[readOffset];
        int stepY = stepsY[readOffset];

        Tile position = entity.tile();
        int absX = position.getX();
        int absY = position.getY();
        int diffX = stepX - absX;
        int diffY = stepY - absY;

        if(diffX < 0)
            diffX = -1;
        else if(diffX > 0)
            diffX = 1;

        if(diffY < 0)
            diffY = -1;
        else if(diffY > 0)
            diffY = 1;

        int newX = absX + diffX;
        int newY = absY + diffY;

        if(!entity.getRouteFinder().allowStep(newX, newY))
            return false;
        var dirMoved = Direction.getDirection(diffX, diffY);
        var nt = entity.tile().transform(dirMoved.x == 0 ? 0 : (dirMoved.x * 10), dirMoved.y == 0 ? 0 : (dirMoved.y * 10));
        entity.lastTileFaced = entity.tile.transform(nt.x * 2 + 1, nt.y * 2 + 1);

        entity.setTile(new Tile(newX, newY, entity.getZ()));
        if (entity.isPlayer())
            entity.getAsPlayer().getMovementQueue().handleRegionChange();
        if(newX == stepX && newY == stepY)
            readOffset++;
        return true;
    }

    public int[] getStepsX() {
        if(stepsX == null)
            stepsX = new int[50];
        return stepsX;
    }

    public int[] getStepsY() {
        if(stepsY == null)
            stepsY = new int[50];
        return stepsY;
    }

    public void outOfReach() {
        //player.privateSound(154); // TODO
        entity.message("I can't reach that!");
    }

    public boolean forcedStep() {
        return stepType == StepType.FORCED_WALK ||
            stepType == StepType.FORCED_RUN;
    }
}

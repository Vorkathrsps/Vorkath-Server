package com.cryptic.model.entity.player;

import com.cryptic.model.content.mechanics.MultiwayCombat;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.model.map.region.Region;
import com.cryptic.model.map.route.RouteFinder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.Optional;

import static com.cryptic.model.entity.attributes.AttributeKey.MOVEMENT_PACKET_STEPS;

@Slf4j
public class PlayerMovement extends MovementQueue {

    public Player player;

    /**
     * Creates a walking queue for the specified mob.
     *
     * @param entity The mob.
     */
    public PlayerMovement(Entity entity) {
        super(entity);
        this.player = entity.getAsPlayer();
    }

    @Setter
    private Entity following;

    /**
     * Processes the movement queue.
     * <p>
     * Polls through the queue of steps and handles them.
     */
    public void process() {
        player.setPreviousTile(player.tile());
        final Tile beforeWalk = player.tile();
        player.setWalkingDirection(Direction.NONE);
        player.setRunningDirection(Direction.NONE);
        if (following != null) {
            if (!following.isRegistered() || !following.tile().isWithinDistance(player.tile())) {
                //player.setPositionToFace(null);
                following = null;
            } else {
                int destX, destY;
                // processed is to determine who had pid to fix who moves first
                if (following.processed && following.getMovement().hasMoved()) {
                    destX = following.getMovement().lastFollowX;
                    destY = following.getMovement().lastFollowY;
                } else {
                    destX = following.getMovement().followX;
                    destY = following.getMovement().followY;
                }

                if (destX == -1 || destY == -1) {
                    final Tile walkable = RouteFinder.findWalkable(following.getX(), following.getY(), following.getZ());
                    following.getMovement().lastFollowX = following.getMovement().followX = destX = walkable.x;
                    following.getMovement().lastFollowY = following.getMovement().followY = destY = walkable.y;
                }
                player.smartPathTo(new Tile(destX, destY)); // supports running
            }
        }
        //System.out.println(Arrays.toString(stepsX).substring(0, 30)+", "+Arrays.toString(stepsY).substring(0, 30));
        if (!step(player)) {
            player.updateRunEnergy();
            return;
        }
        final Tile afterWalk = player.tile();
        boolean forceRun = stepType == StepType.FORCED_RUN;
        boolean ran = (forceRun || (isRunning() && stepType != StepType.FORCED_WALK)) && step(player);
        player.setWalkingDirection(Direction.getDirection(beforeWalk, afterWalk));
        player.setRunningDirection(Direction.getDirection(afterWalk, player.tile()));
        if (ran) {
            if (!forceRun)
                player.drainRunEnergy();
        } else {
            player.updateRunEnergy();
        }

        /**
         * Are we currently moving?
         */
        boolean isMoving = true;
        int diffX = player.tile().getX() - player.getPreviousTile().getX();
        int diffY = player.tile().getY() - player.getPreviousTile().getY();

        //System.out.println("diffX " + diffX + " diffY " + diffY+" "+isRunning());
        lastFollowX = followX;
        lastFollowY = followY;
        followX = player.getPreviousTile().getX();
        followY = player.getPreviousTile().getY();
        if (diffX >= 2)
            followX++;
        else if (diffX <= -2)
            followX--;
        if (diffY >= 2)
            followY++;
        else if (diffY <=-2)
            followY--;

        handleRegionChange();
        MultiwayCombat.tileChanged(player);
        Tile.occupy(player);
        if (isMoving) {
            player.clearAttrib(MOVEMENT_PACKET_STEPS);
            if (!player.getController().isEmpty()) {
                for (Controller controller : player.getController()) {
                    controller.onMovement(player);
                }
            }
        }
    }

    /**
     * Checks if we're currently following the given {@link Entity}.
     *
     * @param entity
     * @return
     */
    public boolean isFollowing(Entity entity) {
        return following != null;
    }

    public void resetFollowing() {
        following = null;
    }

    public boolean movementPacketThisCycle() {
        return player.isPlayer() && Optional.ofNullable(player.<Deque<Tile>>getAttribOr(AttributeKey.MOVEMENT_PACKET_STEPS, null)).map(v -> v != null ? v.peekLast() : null).orElse(null) != null;
    }

    public void handleRegionChange() {
        final int diffX = player.tile().getX() - player.getLastKnownRegion().getRegionX() * 8;
        final int diffY = player.tile().getY() - player.getLastKnownRegion().getRegionY() * 8;
        boolean regionChanged = false;
        if (diffX < 16)
            regionChanged = true;
        else if (diffX >= 88)
            regionChanged = true;
        if (diffY < 16)
            regionChanged = true;
        else if (diffY >= 88)
            regionChanged = true;
        if (regionChanged || player.getRegionHeight() != player.tile().getLevel()) {
            System.out.println("Region changed for " + player.toString() + " region: " + player.tile().region());
            player.removeFromRegions();
            player.getPacketSender().sendMapRegion();
            player.setRegionHeight(player.tile().getLevel());
            player.setActiveMap(new Tile(player.tile().x, player.tile().y, player.tile().level));
        }
    }
}

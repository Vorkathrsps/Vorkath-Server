package com.aelous.core.task.impl;

import com.aelous.core.task.Task;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.RouteFinder;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.Debugs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A {@link Task} implementation which handles the following of a
 * {@link Entity}
 */
public class MobFollowTask extends Task {

    private static final Logger logger = LogManager.getLogger(MobFollowTask.class);
    private final Entity entity;
    private Entity following;
    private boolean ignoreDeath;

    public MobFollowTask(Entity entity, Entity following) {
        super("MobFollowTask", 1, entity, true);
        this.entity = entity;
        this.following = following;
    }

    @Override
    protected void execute() {
        if (entity.isNpc() && entity.getAsNpc().finished()) {
            stop();
            return;
        }
        Debugs.CB_FOLO.debug(entity, "folo start", entity.getCombat().getTarget(), false);
        // Combat dummy can't follow
        if (entity.isNpc() && entity.getAsNpc().isCombatDummy()) {
            return;
        }

        if (entity.isNpc()) {
            NPC npc = (NPC) entity;
            if (npc.isPet() && entity.tile().distance(following.tile()) > 6) {
                npc.teleport(following.tile());
                return;
            }
        }

        if (entity.tile().distance(following.tile()) > 64 || entity.tile().level != following.tile().level) {
            stop();
            return;
        }
        //System.out.println("execute follow task");
        // Update interaction
        entity.setEntityInteraction(following);

        //If entity kills a another entity, don't make them move.
        if (following.dead()) {
            //System.out.println("Got a PK, resetting movement");
            entity.getMovementQueue().clear();
            return;
        }

        //If mob is a killed npc, don't make them move.
        if (entity.isNpc() && entity.dead()) {
            entity.getMovementQueue().clear();
            return;
        }
        // Block if our movement is locked.
        if (!entity.getMovementQueue().canMove()) {
            return;
        }

        boolean combatFollow = CombatFactory.isAttacking(entity) && entity.getCombat().getTarget().equals(following);

        Debugs.CB_FOLO.debug(entity, "goal dist " + entity.tile().distance(following.tile()) + " " + combatFollow, following, false);

        if (!combatFollow && following.boundaryBounds().inside(entity.tile(), entity.getSize()) && (!following.getMovementQueue().isMoving() && !following.getMovement().isMoving()) && !following.getPreviousTile().equals(entity.tile())) {
            final Tile walkable = RouteFinder.findWalkable(following.tile());
            entity.getRouteFinder().routeAbsolute(walkable.x, walkable.y);
            return;
        }

        int destX, destY;
        if (following.getMovement().hasMoved()) {
            destX = following.getMovement().lastFollowX;
            destY = following.getMovement().lastFollowY;
        } else {
            destX = following.getMovement().followX;
            destY = following.getMovement().followY;
        }

        if (entity.isNpc()) {
            //if (destX == -1 || destY == -1)
            DumbRoute.step(entity, following, 1); // npcs cant run
            //else if (!mob.isAt(destX, destY))
            //    DumbRoute.step(mob, destX, destY);
        } else {
            if (destX == -1 || destY == -1) {
                final Tile walkable = RouteFinder.findWalkable(following.getX(), following.getY(), following.getZ());
                following.getMovement().lastFollowX = following.getMovement().followX = destX = walkable.x;
                following.getMovement().lastFollowY = following.getMovement().followY = destY = walkable.y;
            }
            entity.smartPathTo(new Tile(destX, destY)); // supports running
        }
    }

    public MobFollowTask setFollowing(Entity following) {
        this.following = following;
        return this;
    }

    public Entity getFollow() {
        return following;
    }
}

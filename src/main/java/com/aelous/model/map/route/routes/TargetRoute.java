package com.aelous.model.map.route.routes;

import com.aelous.model.entity.Entity;
import com.aelous.model.map.route.RouteType;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.VERZIK_VITUR_8369;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.VERZIK_VITUR_8370;

// I'm not 100% sure I like this class...
public class TargetRoute {

    public static void set(Entity entity, Entity target, int distance) {
        set(entity, target, distance, null);
    }

    public static void set(Entity entity, Entity target, Runnable finishAction) {
        set(entity, target, 1, finishAction);
    }

    public static void set(Entity entity, Entity target, int distance, Runnable finishAction) {
        TargetRoute r = entity.getRouteFinder().targetRoute;
        if (r == null) {
            r = entity.getRouteFinder().targetRoute = new TargetRoute();
        }
        r.target = target;
        r.distance = distance;
        r.finishAction = finishAction;
    }

    public static void reset(Entity entity) {
        TargetRoute r = entity.getRouteFinder().targetRoute;
        if (r != null) {
            r.reset();
        }
    }

    public static void beforeMovement(Entity entity) {
        TargetRoute r = entity.getRouteFinder().targetRoute;
        if (r != null && r.target != null) {
            r.beforeMovement0(entity);
        }
    }

    public static void afterMovement(Entity entity) {
        TargetRoute r = entity.getRouteFinder().targetRoute;
        if (r != null && r.target != null && entity.getMovement().isAtDestination()) {
            r.afterMovement0(entity);
            //System.err.println("after movement.. 2");
        }
    }

    /**
     * Separator
     */
    public Entity target;

    private int distance;

    private Runnable finishAction;

    private boolean abs;

    private RouteType route;

    public boolean withinDistance;

    public void reset() {
        target = null;
        finishAction = null;
        route = null;
    }

    private void beforeMovement0(Entity entity) {
        if (target.isNpc() && target.getAsNpc().walkTo != null) {
            abs = true;
            route =
                entity.getRouteFinder()
                    .routeAbsolute(
                        target.getAsNpc().walkTo.getX(),
                        target.getAsNpc().walkTo.getY());
            return;
        }
        if (entity.getInteractingEntity() != target) {
            entity.setEntityInteraction(target);
        }
        abs = false;
        route = entity.getRouteFinder().routeEntity(target);
        withinDistance = false;
        if (entity.getZ() != target.getZ())
            return;
        if (distance == 1) {
            if (route.reachable && route.finished(entity.tile()))
                withinDistance = true;
        } else {
            int absX = entity.getAbsX();
            int absY = entity.getAbsY();
            int size = entity.getSize();
            int targetX = target.getAbsX();
            int targetY = target.getAbsY();
            int targetSize = target.getSize();
            if (!inTarget(absX, absY, size, targetX, targetY, targetSize))
                if (inRange(absX, absY, size, targetX, targetY, targetSize, distance))
                if ((skipClipCheck()
                || ProjectileRoute.allow(
                absX,
                absY,
                entity.getZ(),
                size,
                targetX,
                targetY,
                targetSize))) {
                withinDistance = true;
                entity.getMovement().reset();
            }
        }
    }

    private boolean skipClipCheck() {
        return (target.isNpc() && target.getAsNpc().id() == 7706)
            || (target.isNpc() && target.getAsNpc().id() == VERZIK_VITUR_8370);
    }

    public boolean allowStep(Entity entity, int stepX, int stepY) {
        if (target == null)
            return true;
        TargetRoute r = target.getRouteFinder().targetRoute;
        if (r != null
            && (target.getWalkingDirection() != null || target.getRunningDirection() != null)
            && target.getMovement().isAtDestination()
            && r.distance <= distance
            && r.route != null
            && entity.isAt(r.route.absX, r.route.absY)
            && r.route.xLength == entity.getSize()) {
            withinDistance = r.withinDistance;
            return false;
        }
        if (route.reachable) {
            int size = entity.getSize();
            int targetX = target.getAbsX();
            int targetY = target.getAbsY();
            int targetSize = target.getSize();
            if (inTarget(stepX, stepY, size, targetX, targetY, targetSize)) {
                withinDistance = false;
                return target.getMovement().isAtDestination(); // todo - test
            }
            if (distance == 1) {
                withinDistance = (stepX == route.finishX && stepY == route.finishY);
                return true;
            }
            if (inRange(stepX, stepY, size, targetX, targetY, targetSize, distance)
                && ProjectileRoute.allow(
                stepX, stepY, entity.getZ(), size, targetX, targetY, targetSize)) {
                withinDistance = true;
                entity.getMovement().reset();
                return true;
            }
        }
        return true;
    }

    private void afterMovement0(Entity entity) {
        // Specific tile upstairs at the pirate boat, you can attack from here.
        if (entity.tile().equals(3018, 3961, 2)) {
            withinDistance = true;
        }

        if (finishAction != null) {
            /** Interactions */
            if (abs)
                withinDistance = route.finished(entity.tile());
            if (withinDistance || (target.isNpc() && target.getAsNpc().skipReachCheck != null && target.getAsNpc().skipReachCheck.test(entity.tile())))
                finishAction.run();
            else if (entity.isPlayer()) {
                entity.getAsPlayer().getMovement().outOfReach();
            }
            reset();
        } else if (!withinDistance) {
            /** Combat */
            if (entity.isPlayer()) {
                entity.getAsPlayer().getMovement().outOfReach();
            }
            entity.getCombat().reset(); // Out of distance reset combat
            reset();
        }
    }

    /**
     * Misc checks
     */
    protected static boolean inTarget(
        int absX, int absY, int size, int targetX, int targetY, int targetSize) {
        if (absX > (targetX + (targetSize - 1)) || absY > (targetY + (targetSize - 1)))
            return false;
        if (targetX > (absX + (size - 1)) || targetY > (absY + (size - 1))) return false;
        return true;
    }

    public static boolean inRange(
        int absX, int absY, int size, int targetX, int targetY, int targetSize, int distance) {
        if (absX < targetX) {
            /** West of target */
            int closestX = absX + (size - 1);
            int diffX = targetX - closestX;
            if (diffX > distance) return false;
        } else if (absX > targetX) {
            /** East of target */
            int closestTargetX = targetX + (targetSize - 1);
            int diffX = absX - closestTargetX;
            if (diffX > distance) return false;
        }
        if (absY < targetY) {
            /** South of target */
            int closestY = absY + (size - 1);
            int diffY = targetY - closestY;
            if (diffY > distance) return false;
        } else if (absY > targetY) {
            /** North of target */
            int closestTargetY = targetY + (targetSize - 1);
            int diffY = absY - closestTargetY;
            if (diffY > distance) return false;
        }
        return true;
    }
}

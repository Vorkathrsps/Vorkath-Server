package com.aelous.model.entity.npc;

import com.aelous.model.World;
import com.aelous.model.entity.combat.Combat;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.RegionManager;
import com.aelous.utility.Debugs;
import com.aelous.utility.Utils;

/**
 * Will make all {@link NPC}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 *
 * @author lare96
 */
public class NPCMovementCoordinator {

    /**
     * The npc we are coordinating movement for.
     */
    private final NPC npc;

    /**
     * The coordinate state this npc is in.
     */
    private CoordinateState coordinateState;

    public static boolean RANDOM_WALK_ENABLED = true;

    public NPCMovementCoordinator(NPC npc) {
        this.npc = npc;
        this.coordinateState = CoordinateState.HOME;
    }

    public void process() {
        if (npc.def().isPet || npc.completelyLockedFromMoving())
            return;

        //Only debug if enabled, string concatenation in large loops is expensive.
        if (Debugs.NPC_MOVE.enabled) {
            Debugs.NPC_MOVE.debug(npc, "area state " + coordinateState, npc.getCombat().getTarget(), true);
        }

        //If walk radius is 0, that means the npc shouldn't RANDOM walk around. (still can melee)
        //HOWEVER: Only if npc is home. Because the npc might be retreating
        //from a fight.
        if (npc.walkRadius() == 0) {
            if (coordinateState == CoordinateState.HOME) {
                if (npc.combatInfo() != null && !npc.combatInfo().retreats) {
                    Debugs.NPC_RETREAT.debug(npc, "retreat at home", npc.getCombat().getTarget(), true);
                    return;
                }
                checkTooFarAwayFromSpawn();
                if (coordinateState == CoordinateState.HOME) { // still home after distance check
                    Debugs.NPC_RETREAT.debug(npc, "still at home", npc.getCombat().getTarget(), true);
                    return;
                }
            }
        }

        if (!npc.getMovementQueue().canMove()) {
            Debugs.NPC_RETREAT.debug(npc, "Can't move", npc.getCombat().getTarget(), true);
            return;
        }

        // change states here
        updateCoordinator();

        //Apparently OSRS NPCs use simple pathfinding for retreating, not smart pathfinding.
        switch (coordinateState) {
            case HOME -> {
                if (!RANDOM_WALK_ENABLED) {
                    return;
                }

                if(!npc.isRandomWalkAllowed()) {
                    Debugs.NPC_RETREAT.debug(npc, "Not allowed to random walk", npc.getCombat().getTarget(), true);
                    return;
                }

                // not in combat, random walk 12% chance
                if (!npc.getMovementQueue().isMoving()) {
                    if (World.getWorld().random(8) <= 1) {
                        Combat combat = npc.getCombat();
                        if(combat != null && (npc.dead() || combat.getTarget() != null)) {
                            Debugs.NPC_RETREAT.debug(npc, "can't random walk", npc.getCombat().getTarget(), true);
                            return;
                        }

                        Tile pos = generateLocalPosition();
                        if (pos != null) {
                            if (RegionManager.getRegion(npc.tile().region()) == null) {
                                System.err.println("npc cant move @ "+pos+" "+npc);
                            } else if (npc.getMovementQueue().canWalk(pos.x, pos.y)) {
                                npc.getMovementQueue().walkTo(pos.getX(), pos.getY());
                            }
                        }
                    }
                }
            }
            // walk back to spawn location
            case RETREATING, AWAY -> {
                if (Debugs.NPC_RETREAT.enabled) {
                    Debugs.NPC_RETREAT.debug(npc, "returning home", npc.getCombat().getTarget(), true);
                }
                if (CombatFactory.inCombat(npc)) {
                    Debugs.NPC_RETREAT.debug(npc, "combat block", npc.getCombat().getTarget(), true);
                    return;
                }
                npc.getRouteFinder().routeAbsolute(npc.spawnTile().x, npc.spawnTile().y);
            }
        }
    }

    public void updateCoordinator() {
        /*
         * Handle retreating from combat.
         * cant use inCombat cos target will be null when walking away
         */
        if (CombatFactory.wasRecentlyAttacked(npc)) {
            // away is set
            if (coordinateState == CoordinateState.AWAY) {
                coordinateState = CoordinateState.RETREATING;
                if (Debugs.NPC_RETREAT.enabled) {
                    Debugs.NPC_RETREAT.debug(npc, "retreat", npc.getCombat().getTarget(), true);
                }
                return;
            }

            // so when under att and retreating, it resets to home when back at spawn
            if (coordinateState == CoordinateState.RETREATING) {
                if (npc.tile().equals(npc.spawnTile())) {
                    coordinateState = CoordinateState.HOME;
                }
                if (Debugs.NPC_RETREAT.enabled) {
                    Debugs.NPC_RETREAT.debug(npc, "retreating", npc.getCombat().getTarget(), true);
                }
                return;
            }
        }

        // only do checks if we're not already away/retreating
        checkTooFarAwayFromSpawn();
    }

    private void checkTooFarAwayFromSpawn() {
        if (coordinateState == CoordinateState.HOME) {
            // calc distance away from spawn tile
            int deltaX;
            int deltaY;

            if (npc.spawnTile().getX() > npc.tile().getX()) {
                deltaX = npc.spawnTile().getX() - npc.tile().getX();
            } else {
                deltaX = npc.tile().getX() - npc.spawnTile().getX();
            }

            if (npc.spawnTile().getY() > npc.tile().getY()) {
                deltaY = npc.spawnTile().getY() - npc.tile().getY();
            } else {
                deltaY = npc.tile().getY() - npc.spawnTile().getY();
            }

            // if we're outside our walk radious, status = AWAY
            final int cap = Math.max(npc.walkRadius(), npc.getCombat().inCombat() ? 10 : 0);
            if ((deltaX > cap) || (deltaY > cap)) {
                coordinateState = CoordinateState.AWAY;
            } else {
                // otherwise set to HOME, we're inside the boundary
                coordinateState = CoordinateState.HOME;
            }
        }
    }

    /**
     * for random walking 12% chance
     */
    private Tile generateLocalPosition() {
        int dir = -1;
        int x = 0, y = 0;
        if (!RegionManager.blockedNorth(npc.tile())) {
            dir = 0;
        } else if (!RegionManager.blockedEast(npc.tile())) {
            dir = 4;
        } else if (!RegionManager.blockedSouth(npc.tile())) {
            dir = 8;
        } else if (!RegionManager.blockedWest(npc.tile())) {
            dir = 12;
        }
        int random = Utils.getRandom(3);

        boolean found = false;

        if (random == 0) {
            if (!RegionManager.blockedNorth(npc.tile())) {
                y = 1;
                found = true;
            }
        } else if (random == 1) {
            if (!RegionManager.blockedEast(npc.tile())) {
                x = 1;
                found = true;
            }
        } else if (random == 2) {
            if (!RegionManager.blockedSouth(npc.tile())) {
                y = -1;
                found = true;
            }
        } else if (random == 3) {
            if (!RegionManager.blockedWest(npc.tile())) {
                x = -1;
                found = true;
            }
        }
        if (!found) {
            if (dir == 0) {
                y = 1;
            } else if (dir == 4) {
                x = 1;
            } else if (dir == 8) {
                y = -1;
            } else if (dir == 12) {
                x = -1;
            }
        }
        if (x == 0 && y == 0)
            return null;
        int spawnX = npc.spawnTile().getX();
        int spawnY = npc.spawnTile().getY();
        if (x == 1) {
            if (npc.tile().getX() + x > spawnX + 1)
                return null;
        }
        if (x == -1) {
            if (npc.tile().getX() - x < spawnX - 1)
                return null;
        }
        if (y == 1) {
            if (npc.tile().getY() + y > spawnY + 1)
                return null;
        }
        if (y == -1) {
            if (npc.tile().getY() - y < spawnY - 1)
                return null;
        }
        return new Tile(x, y, npc.tile().getZ());
    }


    public CoordinateState getCoordinateState() {
        return coordinateState;
    }

    public void setCoordinateState(CoordinateState coordinateState) {
        this.coordinateState = coordinateState;
    }

    public enum CoordinateState {
        HOME,
        AWAY,
        RETREATING
    }
}

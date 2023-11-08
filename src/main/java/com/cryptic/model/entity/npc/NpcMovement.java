package com.cryptic.model.entity.npc;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.combat.Combat;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;

public class NpcMovement extends MovementQueue {

    public NPC npc;
    /**
     * Creates a walking queue for the specified mob.
     *
     * @param entity The mob.
     */
    public NpcMovement(Entity entity) {
        super(entity);
        this.npc = entity.getAsNpc();
    }
    
    public void process() {
        randomWalk();
        npc.setPreviousTile(npc.tile());
        final Tile beforeWalk = npc.tile();
        npc.setWalkingDirection(Direction.NONE);
        npc.setRunningDirection(Direction.NONE);
        if (step(npc)) {
            final Tile afterWalk = npc.tile();
            npc.setWalkingDirection(Direction.getDirection(beforeWalk, afterWalk));
            npc.setRunningDirection(Direction.getDirection(afterWalk, npc.tile()));
            Tile.occupy(npc);
            if (beforeWalk.region() != npc.tile().region()) {
                npc.setLastKnownRegion(beforeWalk);
            }
            if (npc.getLastKnownRegion() != null) {
                if (!npc.getLastKnownRegion().equals(npc.tile())) {
                    var lastRegion = npc.getLastKnownRegion().getRegion();
                    var currentRegion = npc.tile().getRegion();
                    if (lastRegion != currentRegion) {
                        lastRegion.getNpcs().remove(npc);
                        currentRegion.getNpcs().add(npc);
                    }
                }
            }
        }
    }


    private void randomWalk() {
        if(!npc.isRandomWalkAllowed()) return;
        if(!World.getWorld().rollDie(4, 1)) return;
        Combat combat = npc.getCombat();
        if(combat != null && (npc.dead() || combat.getTarget() != null)) return;
        var t = npc.getSpawnArea().randomTile();
        int x = t.x;
        int y = t.y;
        npc.getRouteFinder().routeAbsolute(x,y);
    }

}

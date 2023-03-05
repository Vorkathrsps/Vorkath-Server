package com.aelous.model.entity.npc;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.combat.Combat;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.map.position.Tile;

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
            Tile.occupy(npc); // should be in step block

            if (beforeWalk.region() != npc.tile().region()) {
                npc.setLastKnownRegion(beforeWalk);
            }
        }
    }


    private void randomWalk() {
        if(!npc.isRandomWalkAllowed())
            return;
        if(!World.getWorld().rollDie(4, 1))
            return;
        if (npc.closePlayers(15).length == 0)
            return;
        Combat combat = npc.getCombat();
        if(combat != null && (npc.dead() || combat.getTarget() != null))
            return;

        var t = npc.getSpawnArea().randomTile();
        int x = t.x;
        int y = t.y;
        npc.getRouteFinder().routeAbsolute(x,y);
    }

}

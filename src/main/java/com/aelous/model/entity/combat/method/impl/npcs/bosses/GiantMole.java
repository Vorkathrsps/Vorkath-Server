package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

public class GiantMole extends CommonCombatMethod {

    private static final int BURROW_DOWN_ANIM = 3314;
    private static final int BURROW_SURFACE_ANIM = 3315;

    private static final int[][] BURROW_POINTS = {
        {-21, 38},
        {-15, 22},
        {-19, 1},
        {-15, -14},
        {-20, -33},
        {-3, -33},
        {1, -22},
        {12, -11},
        {10, 15},
        {22, 35},
        {18, 51},
    };

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }

    private void burrow(NPC npc, Entity target) {
        int[] offsets = Utils.randomElement(BURROW_POINTS);
        Tile burrowDestination = npc.spawnTile().relative(offsets[0], offsets[1]);
        target.getCombat().reset();//When mole digs reset combat
        npc.lockNoDamage();
        npc.faceEntity(null);
        npc.getMovement().reset();
        npc.animate(BURROW_DOWN_ANIM);
        Chain.bound(null).runFn(3, () -> {
            npc.teleport(burrowDestination);
            npc.animate(BURROW_SURFACE_ANIM);
        }).then(2, npc::unlock);
    }

}

package com.aelous.model.content.raids.theatre.nylocas.combat;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;
import com.aelous.utility.Utils;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class VasiliasCombat extends CommonCombatMethod {

    @Override
    public void init(NPC npc) {

    }

    @Override
    public void preDefend(Hit hit) {
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (withinDistance(1)) {
            entity.animate(entity.attackAnimation());
            target.hit(entity, Utils.random(entity.getAsNpc().getCombatInfo().maxhit));
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }
}

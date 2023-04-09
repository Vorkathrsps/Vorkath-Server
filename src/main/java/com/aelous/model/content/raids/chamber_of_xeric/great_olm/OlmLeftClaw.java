package com.aelous.model.content.raids.chamber_of_xeric.great_olm;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;

/**
 * @author Sharky
 * @Since January 27, 2023
 */
public class OlmLeftClaw extends CommonCombatMethod {
    @Override
    public void init(NPC npc) {
        super.init(npc);
    }

    @Override
    public boolean prepareAttack(Entity Entity, Entity target) {
        return false;
    }

    @Override
    public int getAttackSpeed(Entity Entity) {
        return 0;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 0;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        return super.customOnDeath(hit);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }

    @Override
    public void doFollowLogic() {

    }
}

package com.cryptic.model.content.raids.chamber_of_xeric.great_olm;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;

/**
 * @author Sharky
 * @Since January 27, 2023
 */
public class OlmRightClawCombat extends CommonCombatMethod {
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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        return super.customOnDeath(hit);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public void doFollowLogic() {

    }
}

package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 13, 2022
 */
public class NexMinion extends NPC {

    private boolean hasNoBarrier;

    public NexMinion(int id, Tile tile) {
        super(id, tile);
        completelyLockedFromMoving(true);
        capDamage(0);
    }

    public void breakBarrier() {
        capDamage(-1);
        hasNoBarrier = true;
    }

    @Override
    public void sequence() {
        if (dead() || !hasNoBarrier)
            return;
        super.sequence();
        Entity target = null;
        if(getCombatMethod() != null) {
            if (getCombatMethod() instanceof CommonCombatMethod) {
                CommonCombatMethod method = (CommonCombatMethod) getCombatMethod();
                method.set(this, null);
                target = Utils.randomElement(method.getPossibleTargets(this));
            }
        }
        if(target != null)
            getCombat().setTarget(target);
    }

    @Override
    public void die() {
        ZarosGodwars.moveNextStage();
        super.die();
    }
}

package com.aelous.model.entity.combat.method.impl.npcs.thralls;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.Player;

public class MagicThrall extends CommonCombatMethod {

    @Override
    public void preDefend(Hit hit) {
    }
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        setThrallTarget(entity, entity.getAsPlayer());
        return true;
    }

    public void setThrallTarget(Entity entity, Player player) {
        var playerTarget = player.getCombat().getTarget();

        assert entity.getFaceTile() != null;
        if (!entity.getFaceTile().equals(playerTarget.getX(), playerTarget.getY())) {
            entity.face(playerTarget);
        }

        if (player.getCombat().getTarget().isNpc() || player.getCombat().inCombat()) {
            entity.getCombat().setTarget(playerTarget);
            entity.setEntityInteraction(playerTarget);
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return CombatFactory.MAGIC_COMBAT.getAttackDistance(entity);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }
}

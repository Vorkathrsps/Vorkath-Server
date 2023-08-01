package com.cryptic.model.entity.combat.method.impl.npcs.thralls;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;

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
    public int moveCloseToTargetTileRange(Entity entity) {
        return CombatFactory.MAGIC_COMBAT.moveCloseToTargetTileRange(entity);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }
}

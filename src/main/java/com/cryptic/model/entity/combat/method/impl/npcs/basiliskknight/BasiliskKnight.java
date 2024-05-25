package com.cryptic.model.entity.combat.method.impl.npcs.basiliskknight;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

public class BasiliskKnight extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity == null || target == null) {
            return false;
        }

        if (!withinDistance(8)) {
            return false;
        }

        if (Utils.rollDie(50, 1)) {
            if (!withinDistance(1)) {
                return false;
            } else {
                melee(entity, (Player) target);
            }
        } else {
            range(entity, (Player) target);
        }

        return true;
    }

    void melee(Entity basilisk, Player target) {
        if (basilisk == null || target == null) {
            return;
        }

        basilisk.animate(8499);

        target.hit(basilisk, Utils.random(1, 20), 1, CombatType.MELEE);
    }

    void range(Entity basilisk, Player target) {
        if (basilisk == null || target == null) {
            return;
        }

        basilisk.animate(8500);

        target.hit(basilisk, Utils.random(1, 20), 1, CombatType.RANGED);
    }

    void drainStats(Entity basilisk, Player target) {

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 3;
    }
}

package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;

public class AbyssalWhip extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        entity.animate(1658);
        entity.submitHit(target, 0, this).postDamage(h -> {
            if (!h.isAccurate()) {
                h.block();
                return;
            }
            if (target instanceof Player player) {
                if (player.dead()) return;
                var attacker = (Player) entity;
                if (attacker.dead()) return;
                drainEnergy(target, player, attacker);
            }
        });
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_WHIP.getDrainAmount());
        return true;
    }

    private static void drainEnergy(Entity target, Player player, Player attacker) {
        double a_run = attacker.getAttribOr(AttributeKey.RUN_ENERGY, 100.0);
        double t_run = player.getAttribOr(AttributeKey.RUN_ENERGY, 100.0);
        if (!(t_run > 0.0)) return;
        double drain = t_run / 10;
        if (!(drain > 0.0)) return;
        player.setRunningEnergy((t_run - drain), true);
        attacker.setRunningEnergy((a_run + drain), true);
        target.graphic(341, GraphicHeight.LOW, 0);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}

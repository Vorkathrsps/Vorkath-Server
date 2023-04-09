package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;

public class AbyssalWhip extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        entity.animate(1658);
        //todo it.player().world().spawnSound(it.player().tile(), 2713, 0, 10)
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        target.graphic(341, GraphicHeight.HIGH, 0);
        if (target.isPlayer()) {
            Player t = (Player) target;
            Player player = (Player) entity;
            double target_cur_energy = t.getAttribOr(AttributeKey.RUN_ENERGY, 100.0);
            double player_cur_energy = player.getAttribOr(AttributeKey.RUN_ENERGY, 100.0);
            if (target_cur_energy > 0.0) {
                double drain = target_cur_energy / 10;
                if (drain > 0) {
                    t.setRunningEnergy((target_cur_energy - drain), true);
                    player.setRunningEnergy((player_cur_energy + drain), true);
                }
            }
        }
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_WHIP.getDrainAmount());
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
}

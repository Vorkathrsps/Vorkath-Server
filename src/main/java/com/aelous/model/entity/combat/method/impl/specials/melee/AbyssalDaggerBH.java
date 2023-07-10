package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.maxhit.MeleeMaxHit;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
public class AbyssalDaggerBH extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(3300);
        entity.graphic(1283, GraphicHeight.LOW, 0);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE) ,1, CombatType.MELEE).checkAccuracy();

        for (int index = 0; index < 2; index++) {
            hit.submit();
        }

        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_DAGGER_BH.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed() + 1;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}

package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.chainedwork.Chain;
import com.google.common.graph.Graph;

public class UrsineMace extends CommonCombatMethod {
    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.animate(9963);
        entity.performGraphic(new Graphic(2342, GraphicHeight.HIGH, 0));

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),2, CombatType.MELEE).checkAccuracy();
        //TODO gfx

        if (hit.isAccurate()) {
            if (target.isPlayer()) {
                target.freeze(6, target);
                target.skills().setLevel(Skills.AGILITY, target.skills().level(Skills.AGILITY) - 20);
                Chain.bound(null).runFn(2, () -> {
                    for (int index = 0; index < 4; index++) {
                        Chain.bound(null).name("ursinebleed").runFn(index * 2, () -> {
                            Hit bleed = target.hit(entity, 4, 1, CombatType.MELEE).checkAccuracy();
                            bleed.submit();
                        });
                    }
                });
            } else {
                if (hit.isAccurate() && target.isNpc()) {
                    Chain.bound(null).runFn(2, () -> {
                        for (int index = 0; index < 4; index++) {
                            Chain.bound(null).name("ursinebleed").runFn(index * 2, () -> {
                                Hit bleed = target.hit(entity, 4, 1, CombatType.MELEE).checkAccuracy();
                                bleed.submit();
                            });
                        }
                    });
                }
            }
        }

        CombatSpecial.drain(entity, CombatSpecial.URSINE_CHAINMACE.getDrainAmount());
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

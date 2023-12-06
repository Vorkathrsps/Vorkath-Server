package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.chainedwork.Chain;

public class UrsineMace extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(9963);
        entity.performGraphic(new Graphic(2341, GraphicHeight.HIGH, 0));
        entity.performGraphic(new Graphic(2342, GraphicHeight.HIGH, 0));

        Hit hit =  entity.submitHit(target, 1, this);

        entity.sendSound(3869, hit.getDelay());
        if (hit.isAccurate()) {
            if (target.isPlayer()) {
                target.freeze(6, target, true);
                target.getSkills().setLevel(Skills.AGILITY, target.getSkills().level(Skills.AGILITY) - 20);
                Chain.bound(null).runFn(2, () -> {
                    for (int index = 0; index < 4; index++) {
                        Chain.bound(null).name("ursinebleed").runFn(index * 2, () -> {
                            Hit bleed = target.hit(entity, 4, 1, CombatType.MELEE).checkAccuracy(true);
                            bleed.submit();
                            entity.sendSound(2708, bleed.getDelay());
                        });
                    }
                });
            } else {
                if (hit.isAccurate() && target.isNpc()) {
                    Chain.bound(null).runFn(2, () -> {
                        for (int index = 0; index < 4; index++) {
                            Chain.bound(null).name("ursinebleed").runFn(index * 2, () -> {
                                Hit bleed = target.hit(entity, 4, 1, CombatType.MELEE).checkAccuracy(true);
                                bleed.submit();
                                entity.sendSound(2708, bleed.getDelay());
                            });
                        }
                    });
                }
            }
        }

        CombatSpecial.drain(entity, CombatSpecial.URSINE_CHAINMACE.getDrainAmount());
return true;
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

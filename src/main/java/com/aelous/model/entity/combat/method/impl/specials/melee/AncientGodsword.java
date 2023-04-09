package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.masks.impl.tinting.Tinting;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.chainedwork.Chain;

public class AncientGodsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        short delay = 0;
        short duration = 240;
        byte hue = 0;
        byte sat = 6;
        byte lum = 28;
        byte opac = 108;

        final Player player = (Player) entity;

        int animation = 9171;

        player.animate(animation);

        player.graphic(1996);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy();

        hit.submit();

        if (!target.dead()) {
            if (!target.isNullifyDamageLock()) {
                if (hit.isAccurate()) {
                    target.setTinting(new Tinting(delay, duration, hue, sat, lum, opac), target);

                    Chain.bound(null).name("bloodsacrifice").cancelWhen(() -> {
                        return !entity.tile().isWithinDistance(target.tile(),5) || target.dead() || entity.dead(); // cancels as expected
                    }).runFn(8, () -> {
                        Hit hit2 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).setAccurate(true);
                        target.performGraphic(new Graphic(2001, GraphicHeight.HIGH, 1));
                        hit2.setDamage(25);
                        hit2.submit();
                        entity.heal(25);
                    });
                }
            }
        }
        CombatSpecial.drain(entity, CombatSpecial.ANCIENT_GODSWORD.getDrainAmount());
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

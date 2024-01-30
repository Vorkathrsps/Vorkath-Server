package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.masks.impl.tinting.Tinting;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.chainedwork.Chain;

import java.util.function.BooleanSupplier;

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
        player.animate(9171);
        player.graphic(1996);

        entity.submitHit(target, 0, this)
            .postDamage(hit -> {
                if (entity.dead() || target.dead() || target.isNullifyDamageLock()) {
                    hit.invalidate();
                    return;
                }
                if (!hit.isAccurate()) {
                    hit.block();
                    return;
                }
                if (hit.isAccurate() && hit.getDamage() <= 0) {
                    hit.block();
                    return;
                }
                BooleanSupplier distance = () -> !entity.tile().isWithinDistance(target.tile(), 5);
                target.setTinting(new Tinting(delay, duration, hue, sat, lum, opac));
                Chain.bound(null).cancelWhen(distance).runFn(8, () -> {
                    entity.submitAccurateHit(target, 0, 25, this)
                        .postDamage(h2 -> {
                            entity.heal(25);
                            target.graphic(2001, GraphicHeight.HIGH, 0);
                        });
                });
            });
        CombatSpecial.drain(entity, CombatSpecial.ANCIENT_GODSWORD.getDrainAmount());
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

package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.ranged.RangedData;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.security.SecureRandom;

public class WebWeaverBow extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        SecureRandom secureRandom = new SecureRandom();

        int delay = (int) (Math.floor(3 + entity.tile().getManHattanDist(entity.tile(), target.tile()) / 6D));

        RangedData.RangedWeapon rangeWeapon = entity.getCombat().getRangedWeapon();
        boolean ignoreArrows = rangeWeapon != null && rangeWeapon.ignoreArrowsSlot();

        double maxHit = entity.getCombat().getMaximumRangedDamage();
        double hitLogic = (maxHit * (secureRandom.nextDouble() * 0.4));

        boolean chanceToPoison = World.getWorld().rollDie(35, 1);
        entity.animate(new Animation(9964));
        entity.performGraphic(new Graphic(2354, GraphicHeight.HIGH, 0));
        if (chanceToPoison) target.poison(4);
        var hit = entity.submitHit(target, delay, this).postDamage(h -> {
            if (!h.isAccurate()) {
                h.block();
                return;
            }

            h.setDamage((int) hitLogic);
        });
        Chain.bound(null).runFn(delay, () -> target.performGraphic(new Graphic(2355, GraphicHeight.LOW, delay))).then(0, () -> {
            for (int i = 0; i < 3; i++) {
                Chain.bound(null).runFn(1, hit::submit);
            }
        });
        CombatSpecial.drain(entity, CombatSpecial.WEBWEAVER_BOW.getDrainAmount());
        return true;

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}

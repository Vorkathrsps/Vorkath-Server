package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
public class VoidWaker extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        boolean isDummy = target.isNpc() && target.getAsNpc().isCombatDummy();
        double maxHit = entity.getCombat().getMaximumMeleeDamage();
        double minHit = maxHit * 0.5;
        double random = Utils.THREAD_LOCAL_RANDOM.get().nextInt((int) (maxHit * 1.5 + 1 - minHit));
        double hitDamage = minHit + random;
        entity.animate(new Animation(1378));
        target.performGraphic(new Graphic(2363, GraphicHeight.LOW, 0));
        if (isDummy) hitDamage = maxHit * 1.5;
        int finalDamage = (int) Math.floor(hitDamage);
        new Hit(entity, target, 0, this)
            .checkAccuracy(false)
            .setDamage(finalDamage)
            .submit()
            .postDamage(h -> {
                if (target instanceof Player) {
                    if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) h.setDamage(h.getDamage() / 2);
                }
            });
        CombatSpecial.drain(entity, CombatSpecial.VOIDWAKER.getDrainAmount());
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

package com.aelous.model.entity.combat.method.impl.specials.magic;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;

import java.security.SecureRandom;

public class VoidWaker extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        SecureRandom randomGarunteedAccuracy = new SecureRandom();

        double maxHit = entity.getCombat().getMaximumMeleeDamage();
        double minhit = maxHit * 0.5;
        double hitLogic = minhit + randomGarunteedAccuracy.nextInt((int) (maxHit * 1.5 + 1 - minhit));

        entity.animate(new Animation(1378));

        Hit hit = target.hit(entity, (int) Math.floor(hitLogic), 0, CombatType.MAGIC);

        if (target instanceof NPC npc) {
            if (npc.id() == NpcIdentifiers.CORPOREAL_BEAST) {
                hit = target.hit(entity, (int) Math.floor(hitLogic), 0, CombatType.MAGIC).checkAccuracy();
            }
        }

        hit.setAccurate(true);

        if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
            hit.setDamage(hit.getDamage() / 2);
        }

        hit.submit();

        target.performGraphic(new Graphic(2363, GraphicHeight.LOW, 0));

        CombatSpecial.drain(entity, CombatSpecial.VOIDWAKER.getDrainAmount());
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

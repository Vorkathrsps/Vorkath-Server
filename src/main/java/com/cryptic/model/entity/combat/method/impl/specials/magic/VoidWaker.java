package com.cryptic.model.entity.combat.method.impl.specials.magic;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;

import java.security.SecureRandom;

public class VoidWaker extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        SecureRandom random = new SecureRandom();

        boolean isDummy = target.isNpc() && target.getAsNpc().isCombatDummy();
        double maxHit = entity.getCombat().getMaximumMeleeDamage();
        double minHit = maxHit * 0.5;
        double hitDamage = minHit + random.nextInt((int) (maxHit * 1.5 + 1 - minHit));

        entity.animate(new Animation(1378));

        CombatType combatType = CombatType.MAGIC;

        if (isDummy) {
            hitDamage = maxHit * 1.5;
            combatType = CombatType.MELEE;
        }

        int finalDamage = (int) Math.floor(hitDamage);

        Hit hit = target.hit(entity, finalDamage, 0, combatType).setAccurate(true);

        if (target instanceof NPC npc && npc.id() == NpcIdentifiers.CORPOREAL_BEAST) {
            hit = target.hit(entity, finalDamage, 0, CombatType.MAGIC).checkAccuracy();
        }

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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}

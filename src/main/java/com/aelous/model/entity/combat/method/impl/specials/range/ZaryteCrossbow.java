package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.Player;

public class ZaryteCrossbow extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        int distance = entity.tile().getChevDistance(target.tile());

        player.animate(9166);

        int duration = 41 + 11 + (5 * distance);
        Projectile projectile = new Projectile(entity, target, 1995, 41, duration, 38, 36, 0, target.getSize(), 5);

        final int hitDelay = entity.executeProjectile(projectile);

        CombatFactory.decrementAmmo(player);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), hitDelay, CombatType.RANGED).checkAccuracy();
        hit.submit();
        CombatSpecial.drain(entity, CombatSpecial.ZARYTE_CROSSBOW.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}

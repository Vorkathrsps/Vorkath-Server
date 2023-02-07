package com.aelous.model.entity.combat.method.impl.npcs.bosses.demonicgorillas;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

/**
 * @author Patrick van Elderen | March, 13, 2021, 22:10
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DemonicGorillaRangedStrategy extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        //mob.forceChat("RANGED!");
        entity.animate(7227);
        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        Projectile projectile = new Projectile(entity, target, 1302, 35, 25 * tileDist, 45, 30, 0);
        projectile.sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().postDamage(h -> ((DemonicGorilla)entity).getCombatAI().handleAfterHit(h)).submit();
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

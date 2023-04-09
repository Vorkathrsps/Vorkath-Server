package com.aelous.model.entity.combat.method.impl.npcs.waterbirth;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

/**
 * @author Patrick van Elderen | March, 04, 2021, 17:06
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Wallasalki extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());
        var delay = Math.max(1, (20 + (tileDist * 12)) / 30);

        new Projectile(entity, target, 136, 15, 12 * tileDist, 30, 31, 0).sendProjectile();

        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
        target.hit(entity, hit, delay, CombatType.MAGIC).checkAccuracy().submit();

       // if (hit > 0)
           // target.delayedGraphics(137, GraphicHeight.HIGH, 2);
       // else
           // target.delayedGraphics(85, GraphicHeight.HIGH, 2);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}

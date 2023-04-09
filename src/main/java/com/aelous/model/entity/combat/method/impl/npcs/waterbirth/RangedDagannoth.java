package com.aelous.model.entity.combat.method.impl.npcs.waterbirth;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

/**
 * @author Patrick van Elderen | March, 04, 2021, 17:05
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RangedDagannoth extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        new Projectile(entity, target, 294, 15, 12 * tileDist, 25, 31, 0).sendProjectile();

        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED);
        target.hit(entity, hit, delay, CombatType.RANGED).checkAccuracy().submit();
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

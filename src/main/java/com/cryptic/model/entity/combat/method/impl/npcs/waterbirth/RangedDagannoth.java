package com.cryptic.model.entity.combat.method.impl.npcs.waterbirth;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

/**
 * @author Origin | March, 04, 2021, 17:05
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RangedDagannoth extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 294, 41, duration, 43, 31, 0, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED);
        target.hit(entity, hit, delay, CombatType.RANGED).checkAccuracy(true).submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}

package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.player.Player;

public class ZaryteCrossbow extends CommonCombatMethod {

    private static final Animation ANIMATION = new Animation(9166, Priority.HIGH);

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(ANIMATION);

        //Fire projectile
        new Projectile(entity, target, 1995, 50, 70, 44, 35, 0).sendProjectile();

        //Decrement ammo by 1
        CombatFactory.decrementAmmo(player);

        var tileDist = player.tile().getChevDistance(target.tile());
        double hitDelay = 1 + (Math.floor(1 + tileDist) / 3D);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), (int) hitDelay, CombatType.RANGED).checkAccuracy();
        hit.submit();
        CombatSpecial.drain(entity, CombatSpecial.ZARYTE_CROSSBOW.getDrainAmount());
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

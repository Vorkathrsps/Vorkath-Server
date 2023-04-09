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
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;

public class MagicShortbow extends CommonCombatMethod {

    private static final Animation ANIMATION = new Animation(1074, Priority.HIGH);
    private static final Graphic GRAPHIC = new Graphic(250, GraphicHeight.HIGH);

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(ANIMATION);
        player.performGraphic(GRAPHIC);

        //Send 2 arrow projectiles
        new Projectile(player, target, 249, 40, 70, 43, 31, 0).sendProjectile();
        new Projectile(player, target, 249, 33, 74, 48, 31, 0).sendProjectile();

        //Remove 2 arrows from ammo
        CombatFactory.decrementAmmo(player);
        CombatFactory.decrementAmmo(player);

        Hit hit1 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),3, CombatType.RANGED).checkAccuracy();
        hit1.submit();

        Hit hit2 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),2, CombatType.RANGED).checkAccuracy();
        hit2.submit();
        CombatSpecial.drain(entity, CombatSpecial.MAGIC_SHORTBOW.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed() + 1;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}

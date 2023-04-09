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
import com.aelous.utility.timers.TimerKey;

public class DragonThrownaxe extends CommonCombatMethod {

    private static final Animation ANIMATION = new Animation(7521, Priority.HIGH);
    private static final Graphic GRAPHIC = new Graphic(1317, GraphicHeight.HIGH);

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(ANIMATION);
        player.performGraphic(GRAPHIC);

        new Projectile(player, target, 1318, 45, 65, 40, 33, 0).sendProjectile();

        CombatFactory.decrementAmmo(player);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),0, CombatType.RANGED).checkAccuracy();
        hit.submit();
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_THROWNAXE.getDrainAmount());

        player.getTimers().register(TimerKey.THROWING_AXE_DELAY,1);
        player.getTimers().register(TimerKey.COMBAT_ATTACK,1); // 1 tick delay before another normal melee
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed() + 1;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 4;
    }
}

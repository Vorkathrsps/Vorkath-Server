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

public class Ballista extends CommonCombatMethod {

    private static final Animation ANIMATION = new Animation(7222, Priority.HIGH);
    private static final Graphic GRAPHIC = new Graphic(344, GraphicHeight.HIGH);

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();
        int delay = (int) (Math.floor(3 + entity.tile().distance(target.tile()) / 6D));
        double distance = entity.tile().getChevDistance(target.tile());

        player.animate(ANIMATION);

        Projectile projectile = new Projectile(player, target, 1301, 41, delay, 45, 36, 0);
        
        player.executeProjectile(projectile);

        CombatFactory.decrementAmmo(player);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();

        hit.submit();

        target.performGraphic(new Graphic(344, GraphicHeight.HIGH, (int) (41 + 11 + (5 * distance))));

        CombatSpecial.drain(entity, CombatSpecial.BALLISTA.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }

}

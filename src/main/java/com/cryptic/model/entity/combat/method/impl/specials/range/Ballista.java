package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;

public class Ballista extends CommonCombatMethod {

    private static final Animation ANIMATION = new Animation(7222, Priority.HIGH);
    private static final Graphic GRAPHIC = new Graphic(344, GraphicHeight.HIGH);

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(ANIMATION);

        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration = (66 + 11 + (5 * tileDist));
        Projectile p1 = new Projectile(entity, target, 1301, 66, duration, 40, 30, 0, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p1);

        CombatFactory.decrementAmmo(player);

        entity.submitHit(target, delay, this);
        target.graphic(344, GraphicHeight.HIGH, p1.getSpeed());
        CombatSpecial.drain(entity, CombatSpecial.BALLISTA.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }

}

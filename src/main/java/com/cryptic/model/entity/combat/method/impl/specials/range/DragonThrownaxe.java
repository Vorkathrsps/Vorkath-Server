package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.timers.TimerKey;

public class DragonThrownaxe extends CommonCombatMethod {

    private static final Animation ANIMATION = new Animation(7521, Priority.HIGH);
    private static final Graphic GRAPHIC = new Graphic(1317, GraphicHeight.HIGH);

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(ANIMATION);
        player.performGraphic(GRAPHIC);
        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration = (32 + (7 * tileDist));
        Projectile p1 = new Projectile(entity, target, 1318, 32, duration, 40, 30, 6, 1, 7);
        final int delay = entity.executeProjectile(p1);
        CombatFactory.decrementAmmo(player);
        var hit = entity.submitHit(target, delay, this);
        player.sendPublicSound(2528, hit.getDelay());
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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 4;
    }
}

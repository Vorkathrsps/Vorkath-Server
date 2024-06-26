package com.cryptic.model.entity.combat.method.impl.npcs.waterbirth;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import org.jetbrains.annotations.NotNull;


public class WallasalkiCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(@NotNull Entity entity, @NotNull Entity target) {
        if (!withinDistance(8)) return false;
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 136, 51, duration, 43, 31, 8, entity.getSize(), 64, 0);
        final int delay = entity.executeProjectile(p);
        Hit hit = new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
        if (hit.getDamage() > 0) target.graphic(137, GraphicHeight.HIGH, p.getSpeed());
        else target.graphic(85, GraphicHeight.HIGH, p.getSpeed());
        return true;
    }

    @Override
    public int getAttackSpeed(@NotNull Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}

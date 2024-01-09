package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;

public class MagicShortbow extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        entity.animate(1074);
        entity.graphic(256, GraphicHeight.HIGH, 30);
        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration1 = (20 + 11 + (5 * tileDist));
        int duration2 = (50 + 11 + (5 * tileDist));
        Projectile p1 = new Projectile(entity, target, 249, 20, duration1, 40, 36, 15, entity.getSize(), 5);
        Projectile p2 = new Projectile(entity, target, 249, 50, duration2, 40, 36, 15, entity.getSize(), 5);

        final int delay1 = entity.executeProjectile(p1);
        final int delay2 = entity.executeProjectile(p2);

        CombatFactory.decrementAmmo(player);
        CombatFactory.decrementAmmo(player);

        for (int i = 0; i < 2; i++) {
            entity.submitHit(target, i == 1 ? delay1 : delay2, this);
        }

        CombatSpecial.drain(entity, CombatSpecial.MAGIC_SHORTBOW.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed() + 1;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}

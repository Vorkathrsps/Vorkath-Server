package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.Player;

public class ZaryteCrossbow extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();
        int distance = entity.tile().getChevDistance(target.tile());
        player.animate(9166);
        int duration = 41 + 11 + (5 * distance);
        Projectile projectile = new Projectile(entity, target, 1995, 41, duration, 38, 36, 5, 1, 5);
        final int hitDelay = entity.executeProjectile(projectile);
        CombatFactory.decrementAmmo(player);
        entity.submitHit(target, hitDelay, this);
        CombatSpecial.drain(entity, CombatSpecial.ZARYTE_CROSSBOW.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}

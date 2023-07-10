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
import com.aelous.utility.chainedwork.Chain;

public class MagicShortbow extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        entity.animate(1074);
        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration1 = (21 + 11 + (3 * tileDist));
        int duration2 = (41 + 11 + (3 * tileDist));
        Projectile p1 = new Projectile(entity, target, 249, 21, duration1, 40, 30, 16, target.getSize(), 5);
        Projectile p2 = new Projectile(entity, target, 249, 41, duration2, 40, 30, 16, target.getSize(), 5);

        final int delay1 = entity.executeProjectile(p1);
        final int delay2 = entity.executeProjectile(p2);

        CombatFactory.decrementAmmo(player);
        CombatFactory.decrementAmmo(player);

        for (int i = 0; i < 2; i++) {
            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), i == 1 ? delay1 : delay2, CombatType.RANGED).checkAccuracy();
            hit.submit();
        }

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

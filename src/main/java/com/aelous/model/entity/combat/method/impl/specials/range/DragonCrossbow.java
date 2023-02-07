package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;

/**
 * The crossbow has a special attack, Annihilate, which drains 60% of the special attack bar and hits up to 9 enemies in a 3x3 area.
 * The primary target of Annihilate will take 20% extra damage, all other targets will take 20% less damage.
 * If used in a single-way combat area, the attack will still work but only hit one target with 20% extra damage.
 * It ignores enchanted bolt effects.
 */
public class DragonCrossbow extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity attacker, Entity entity) {
        final Player player = attacker.getAsPlayer();

        player.animate(4230);

        new Projectile(attacker, target, 698, 50, 70, 44, 35, 0).sendProjectile();
        target.performGraphic(new Graphic(1466, GraphicHeight.HIGH, 3));

        //Decrement ammo by 1
        CombatFactory.decrementAmmo(player);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),2, CombatType.RANGED).checkAccuracy();
        hit.submit();
        CombatSpecial.drain(attacker, CombatSpecial.DRAGON_CROSSBOW.getDrainAmount());
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

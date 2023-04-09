package com.aelous.model.entity.combat.method.impl.npcs.fightcaves;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Utils;

/**
 * Handles Jad's combat.
 *
 * @author Professor Oak
 */
public class TztokJadCombatScript extends CommonCombatMethod {

    private static final int MAX_DISTANCE = 10;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        Player player = target.getAsPlayer();

        // Select attack type..

        //Disable healers until further notice
        /*TzTokJad jad = (TzTokJad) mob.getAsNpc();

        if (jad.hp() <= jad.maxHp() / 2) {
            jad.spawnHealers(player);
        }*/

        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            if (Utils.securedRandomChance(0.50D)) {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
                entity.animate(entity.attackAnimation());
            }
        } else if (Utils.securedRandomChance(0.50D)) {
            entity.animate(2656);
            var tileDist = entity.tile().distance(target.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(entity, target, 448, 51, duration, 128, 31, 0, target.getSize(), 10);
            entity.graphic(447, GraphicHeight.HIGH_5, p.getSpeed());
            final int delay = entity.executeProjectile(p);
            target.performGraphic(new Graphic(157, GraphicHeight.LOW, p.getSpeed()));
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
        } else if (Utils.securedRandomChance(0.50D)) {
            entity.animate(2652);
            var tileDist = entity.tile().distance(target.tile());
            int duration = (41 + 11 + (5 * tileDist));
            Projectile p = new Projectile(entity, target, -1, 41, duration, 0, 0, 0, target.getSize(), 5);
            final int delay = entity.executeProjectile(p);
            target.graphic(451, GraphicHeight.LOW, 0);
            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();
            hit.submit();
            target.graphic(157, GraphicHeight.LOW, p.getSpeed());
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return MAX_DISTANCE;
    }
}

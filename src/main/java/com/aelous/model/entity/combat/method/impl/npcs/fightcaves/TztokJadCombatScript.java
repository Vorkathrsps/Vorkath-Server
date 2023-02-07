package com.aelous.model.entity.combat.method.impl.npcs.fightcaves;

import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;

/**
 * Handles Jad's combat.
 *
 * @author Professor Oak
 */
public class TztokJadCombatScript extends CommonCombatMethod {

    private static final int MAX_DISTANCE = 10;

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        Player player = target.getAsPlayer();

        // Select attack type..

        //Disable healers until further notice
        /*TzTokJad jad = (TzTokJad) mob.getAsNpc();

        if (jad.hp() <= jad.maxHp() / 2) {
            jad.spawnHealers(player);
        }*/

        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            if(World.getWorld().rollDie(2,1)) {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
                entity.animate(entity.attackAnimation());
            } else {
                if (World.getWorld().rollDie(2, 1)) {
                    /*
                     * Magic attack
                     */
                    entity.animate(2656);
                    entity.graphic(447, GraphicHeight.HIGH_5, 0);
                    new Projectile(entity, target, 448, 50, 120, 128, 31, 0).sendProjectile();
                    target.performGraphic(new Graphic(157, GraphicHeight.LOW, 6));
                    target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 6, CombatType.MAGIC).checkAccuracy().submit();
                } else {
                    /*
                     * Ranged attack
                     */
                    entity.animate(2652);
                    target.graphic(451);
                    target.performGraphic(new Graphic(157, GraphicHeight.LOW, 4));
                    target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 4, CombatType.RANGED).checkAccuracy().submit();
                }
            }
        } else {
            if (World.getWorld().rollDie(2, 1)) {
                /*
                 * Magic attack
                 */
                entity.animate(2656);
                entity.graphic(447, GraphicHeight.HIGH_5, 0);
                new Projectile(entity, target, 448, 50, 120, 128, 31, 0).sendProjectile();
                target.performGraphic(new Graphic(157, GraphicHeight.LOW, 5));
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 5, CombatType.MAGIC).checkAccuracy().submit();
            } else {
                /*
                 * Ranged attack
                 */
                entity.animate(2652);
                target.graphic(451);
                target.performGraphic(new Graphic(157, GraphicHeight.LOW, 4));
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 4, CombatType.RANGED).checkAccuracy().submit();
            }
        }
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

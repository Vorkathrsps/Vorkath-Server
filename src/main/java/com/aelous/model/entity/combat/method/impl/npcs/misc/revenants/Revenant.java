package com.aelous.model.entity.combat.method.impl.npcs.misc.revenants;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;

import java.security.SecureRandom;

/**
 * @author Patrick van Elderen | Zerikoth
 * <p>
 * Revenants use all three forms of attack.
 * Their attacks have very high if not 100% accuracy, and will often deal high damage.
 * They will react to a player's overhead prayers and defensive bonuses.
 * By default, all revenants attack with Magic, but can quickly adapt based on the player's defensive bonuses and prayers.
 */
public class Revenant extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        SecureRandom secureRandom = new SecureRandom();
        double chance = secureRandom.nextDouble();
        NPC npc = (NPC) entity;

        if (npc.hp() < npc.maxHp() / 2) {
            if (chance < 0.5D) {
                npc.graphic(1221);
                npc.heal(npc.maxHp() / 3);
            }
        }
        if (CombatFactory.canAttack(entity, CombatFactory.MELEE_COMBAT, target) && World.getWorld().random(2) == 1)
            meleeAttack(npc, target);
        else if (World.getWorld().rollDie(2, 1))
            rangedAttack(npc, target);
        else
            magicAttack(npc, target);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }

    private void meleeAttack(NPC npc, Entity target) {
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
        npc.animate(npc.attackAnimation());
    }

    private void rangedAttack(NPC npc, Entity target) {
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 206, 41, duration, 43, 31, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        npc.animate(npc.attackAnimation());
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
    }

    private void magicAttack(NPC npc, Entity target) {
        npc.animate(npc.attackAnimation());
        var tileDist = npc.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(npc, target, 1415, 51, duration, 43, 31, 0, target.getSize(), 10);
        final int delay = npc.executeProjectile(p);
        int damage = CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC);
        target.hit(npc, damage, delay, CombatType.MAGIC).checkAccuracy().submit();
        target.performGraphic(damage > 0 ? new Graphic(1454, GraphicHeight.HIGH, p.getSpeed()) : new Graphic(85, GraphicHeight.HIGH, p.getSpeed()));
        npc.freeze(8, target);
    }
}

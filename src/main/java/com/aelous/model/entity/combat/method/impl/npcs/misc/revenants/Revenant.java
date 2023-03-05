package com.aelous.model.entity.combat.method.impl.npcs.misc.revenants;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
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
    public void prepareAttack(Entity entity, Entity target) {

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
        int tileDist = npc.tile().getChevDistance(target.tile());
        int delay = (int) (1 + Math.floor(3 + tileDist) / 6D);

        npc.animate(npc.attackAnimation());
        //int hitdelay = npc.executeProjectile(new Projectile(npc, target, 206, delay, tileDist, 30,31,0,1));
      //  target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), hitdelay + 1, CombatType.RANGED).checkAccuracy().submit();
    }

    private void magicAttack(NPC npc, Entity target) {
        var tileDist = npc.tile().transform(1, 1, 0).distance(target.tile());
        int delay = (int) (1D + Math.floor(1 + tileDist) / 3D);
        delay = (int) Math.min(Math.max(1.0 , delay), 5.0);

        npc.animate(npc.attackAnimation());
       // int hitdelay = npc.executeProjectile(new Projectile(npc, target, 1415, delay, tileDist, 30,31,0,1));
        int damage = CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC);
       // target.hit(npc, damage, hitdelay + 1, CombatType.MAGIC).checkAccuracy().submit();
      //  target.performGraphic(damage > 0 ? new Graphic(1454, GraphicHeight.HIGH, hitdelay + 1) : new Graphic(85, GraphicHeight.HIGH, hitdelay * 5));
        npc.freeze(8, target);
    }
}

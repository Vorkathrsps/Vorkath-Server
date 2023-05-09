package com.aelous.model.entity.combat.method.impl.npcs.godwars.armadyl;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;

public class Aviansie extends CommonCombatMethod {

    private int get_animation(int npc) {
        return npc == 3168 ? 6975 : 6956;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity.isNpc()) {
            NPC npc = (NPC) entity;
            entity.animate(get_animation(npc.id()));
            var tileDist = entity.tile().distance(target.tile());
            int duration = (41 + 11 + (5 * tileDist));
            Projectile p = new Projectile(entity, target, projectile(npc.id()), 41, duration, 43, 31, 0, target.getSize(), 5);
            final int delay = entity.executeProjectile(p);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
        }
        return true;
    }

    private int projectile(int npc) {
        return switch (npc) {
            case 3170, 3171, 3172, 3173, 3175, 3178, 3182, 3181, 3179, 3180 -> 1192;
            default -> 1193;
        };
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 7;
    }
}

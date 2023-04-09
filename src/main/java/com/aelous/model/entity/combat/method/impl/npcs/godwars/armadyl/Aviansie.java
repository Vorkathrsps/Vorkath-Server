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
            new Projectile(entity, target, projectile(npc.id()), 29, 65, 95, 33, 0).sendProjectile();
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy().submit();
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

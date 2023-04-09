package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;

public class LavaBeast extends CommonCombatMethod {

        @Override
    public boolean prepareAttack(Entity entity, Entity target) {
            //10% chance that the boss skulls you!
            if (World.getWorld().rollDie(10, 1)) {
                Skulling.assignSkullState(((Player) target), SkullType.RED_SKULL);
                target.message("The "+entity.getMobName()+" has redskulled you, be careful!");
            }

            NPC npc = (NPC) entity;

        entity.animate(7678);
        new Projectile(entity, target, 1403, 20, 60, 30, 30, 0,10,14).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy().submit();
            return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 15;
    }
}

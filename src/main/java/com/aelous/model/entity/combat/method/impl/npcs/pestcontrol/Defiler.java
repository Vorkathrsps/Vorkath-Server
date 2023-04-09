package com.aelous.model.entity.combat.method.impl.npcs.pestcontrol;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | May, 05, 2021, 13:39
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Defiler extends CommonCombatMethod {

    private void melee(NPC npc, Entity entity) {
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
    }

    private void range(NPC npc, Entity entity) {
        npc.animate(npc.attackAnimation());
        new Projectile(npc, target, 657, 50, 80, 50, 30, 0).sendProjectile();
        Chain.bound(null).name("DefilerRangeTask").runFn(2, () -> target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), CombatType.RANGED).checkAccuracy().submit());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC npc = (NPC) entity;
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            melee(npc, target);
        } else {
            range(npc, target);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}

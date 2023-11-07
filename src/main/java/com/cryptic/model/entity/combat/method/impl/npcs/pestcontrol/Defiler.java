package com.cryptic.model.entity.combat.method.impl.npcs.pestcontrol;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.chainedwork.Chain;

/**
 * @author Origin | May, 05, 2021, 13:39
 * 
 */
public class Defiler extends CommonCombatMethod {

    private void melee(NPC npc, Entity entity) {
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void range(NPC npc, Entity entity) {
        npc.animate(npc.attackAnimation());
        new Projectile(npc, target, 657, 50, 80, 50, 30, 0).sendProjectile();
        Chain.bound(null).name("DefilerRangeTask").runFn(2, () -> target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), CombatType.RANGED).checkAccuracy(true).submit());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC npc = (NPC) entity;
        if (withinDistance(1)) {
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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }
}

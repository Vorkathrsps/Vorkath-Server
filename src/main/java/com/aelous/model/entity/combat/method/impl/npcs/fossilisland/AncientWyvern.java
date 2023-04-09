package com.aelous.model.entity.combat.method.impl.npcs.fossilisland;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.utility.Utils;

public class AncientWyvern extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity wyvern, Entity target) {
        int roll = Utils.random(2);
        if(roll == 1) {
            doMagic(wyvern, target);
        } else {
            if (CombatFactory.canReach(wyvern, CombatFactory.MELEE_COMBAT, target)) {
                if (Utils.random(2) == 1) {
                    doMelee(wyvern, target);
                } else {
                    doTailWhip(wyvern, target);
                }
            } else {
                doMagic(wyvern, target);
            }
        }
        return true;
    }

    private void doMagic(Entity wyvern, Entity entity) {
        wyvern.animate(7657);

        new Projectile(wyvern, target, 136, 25, 55, 90, 45, 0).sendProjectile();
        target.performGraphic(new Graphic(137, GraphicHeight.HIGH, 2));
        target.hit(wyvern, Utils.random(25), 2, CombatType.MAGIC).checkAccuracy().submit();
    }

    private void doTailWhip(Entity wyvern, Entity entity) {
        wyvern.animate(wyvern.attackAnimation());
        target.hit(wyvern, Utils.random(wyvern.getAsNpc().getCombatInfo().maxhit), 1, CombatType.MELEE).checkAccuracy().submit();
    }

    private void doMelee(Entity wyvern, Entity entity) {
        wyvern.animate(7658);
        target.hit(wyvern, Utils.random(wyvern.getAsNpc().getCombatInfo().maxhit), 1, CombatType.MELEE).checkAccuracy().submit();
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

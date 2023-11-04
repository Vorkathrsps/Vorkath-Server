package com.cryptic.model.entity.combat.method.impl.npcs.fossilisland;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.utility.Utils;

public class AncientWyvern extends CommonCombatMethod {

    Projectile p = null;
    @Override
    public boolean prepareAttack(Entity wyvern, Entity target) {
        int roll = Utils.random(2);
        if(roll == 1) {
            doMagic(wyvern, target);
        } else {
            if (withinDistance(1)) {
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
        wyvern.animate(7658);
        var tileDist = wyvern.tile().distance(entity.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile project = p.sendMagicProjectile(wyvern, entity, 162);
        //Projectile p = new Projectile(wyvern, entity, 162, 51, duration, 43, 31, 0, target.getSize(), 10);
        int delay = wyvern.executeProjectile(project);
        target.performGraphic(new Graphic(137, GraphicHeight.HIGH, p.getSpeed()));
        target.hit(wyvern, Utils.random(25), delay, CombatType.MAGIC).checkAccuracy(true).submit();
    }

    private void doTailWhip(Entity wyvern, Entity entity) {
        wyvern.animate(wyvern.attackAnimation());
        target.hit(wyvern, Utils.random(wyvern.getAsNpc().getCombatInfo().maxhit), 1, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void doMelee(Entity wyvern, Entity entity) {
        wyvern.animate(7658);
        target.hit(wyvern, Utils.random(wyvern.getAsNpc().getCombatInfo().maxhit), 1, CombatType.MELEE).checkAccuracy(true).submit();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}

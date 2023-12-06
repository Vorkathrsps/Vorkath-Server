package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.utility.Utils;

public class DragonClaws extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(7514);
        entity.graphic(1171);

        Hit hit1 = target.hit(entity, Math.max(4, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE)),1, CombatType.MELEE).checkAccuracy(true);
        Hit hit2;
        Hit hit3;
        Hit hit4;

        entity.sendSound(2537, hit1.getDelay());

        final int maxHit = entity.getCombat().getMaximumMeleeDamage();

        if (hit1.isAccurate()) {
            if (hit1.getDamage() > 4)
                hit1.setDamage(hit1.getDamage() - 1);
            hit2 = target.hit(entity, hit1.getDamage() / 2,1, CombatType.MELEE);
            hit3 = target.hit(entity, hit2.getDamage() / 2 ,2, CombatType.MELEE);
            hit4 = target.hit(entity, hit3.getDamage() + (Utils.get(1) == 1 ? 1 : 0) ,2, CombatType.MELEE);
        }
        else {
            hit2 = target.hit(entity, Utils.get((int) (maxHit * 0.375d), (int) (maxHit * 0.875d)),1, CombatType.MELEE).checkAccuracy(true);
            if (hit2.isAccurate()) {
                hit3 = target.hit(entity, hit2.getDamage() / 2, 2, CombatType.MELEE);
                hit4 = target.hit(entity, hit3.getDamage() + (Utils.get(1) == 1 ? 1 : 0), 2, CombatType.MELEE);
            }
            else {
                hit3 = target.hit(entity, Utils.get((int) (maxHit * 0.25), (int) (maxHit * 0.75)), 2, CombatType.MELEE).checkAccuracy(true);
                if (hit3.isAccurate()) {
                    hit4 = target.hit(entity, hit3.getDamage() + (Utils.get(1) == 1 ? 1 : 0), 2, CombatType.MELEE);
                }
                else {
                    hit4 = target.hit(entity, Utils.get((int) (maxHit * 0.25), (int) (maxHit * 1.25)), 2, CombatType.MELEE).checkAccuracy(true);
                    if (!hit4.isAccurate()) {
                        if (Utils.get(1) == 1) {
                            hit3.setDamage(1); hit4.setDamage(1);
                        }
                    }
                }
            }
        }

        if (hit1.isAccurate()) {
            hit2.submit();
            hit1.submit();
        } else {
            hit1.submit();
            hit2.submit();
        }
        hit3.submit();
        hit4.submit();
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_CLAWS.getDrainAmount());
        return true;
    }



    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}

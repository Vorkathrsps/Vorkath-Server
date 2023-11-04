package com.cryptic.model.entity.combat.method.impl.npcs.bosses.superiorbosses;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

public class Kerberos extends CommonCombatMethod {

    private void rangedAttack() {
        new Projectile(entity, target, 1381, 25, 106, 125, 31, 0, 15, 220).sendProjectile();
        entity.animate(4492);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 4, CombatType.RANGED).checkAccuracy(true).submit();
        target.performGraphic(new Graphic(1715, GraphicHeight.HIGH, 4));
    }

    private void magicAttack() {
        new Projectile(entity, target, 1382, 25, 106, 125, 31, 0, 15, 220).sendProjectile();
        entity.animate(4492);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 4, CombatType.RANGED).checkAccuracy(true).submit();
        target.performGraphic(new Graphic(1710, GraphicHeight.HIGH, 4));
    }

    private void meleeAttack() {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void doubleAttack() {
        entity.forceChat("RAWRRRRRRRRRRRRRR");
        Chain.bound(null).runFn(1, this::rangedAttack).then(3, this::magicAttack);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (withinDistance(1) && Utils.rollPercent(25)) {
            meleeAttack();
        } else if (Utils.rollPercent(50)) {
            rangedAttack();
        } else if (Utils.rollPercent(10)) {
            doubleAttack();
        } else {
            magicAttack();
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

package com.cryptic.model.entity.combat.method.impl.npcs.karuulm;

import com.cryptic.model.World;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * The combat script for the wyrm.
 * @author Patrick van Elderen | December, 22, 2020, 14:16
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WyrmCombatScript extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var inMeleeDistance = withinDistance(1);
        if (inMeleeDistance && World.getWorld().rollDie(2, 1))
            basicAttack(entity, target);
        else
            magicAttack(entity, target);
        return true;
    }

    private void basicAttack(Entity entity, Entity target) {
        entity.animate(8270);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void magicAttack(Entity entity, Entity target) {
        entity.animate(8271);
        //new Projectile(entity.getCentrePosition(), target.tile(),1,1634, 125,30,36, 31,0,10,10).sendProjectile();

        int delay = entity.getProjectileHitDelay(target);
        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
        target.hit(entity, hit, delay, CombatType.MAGIC).checkAccuracy(true).submit();
        if (hit > 0) {
            target.performGraphic(new Graphic(1635, GraphicHeight.LOW, delay));
        }
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

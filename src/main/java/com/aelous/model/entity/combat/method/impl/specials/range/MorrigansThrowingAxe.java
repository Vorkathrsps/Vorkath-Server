package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.timers.TimerKey;

/**
 * Morrigan's throwing axe has a special attack, Hamstring, that consumes 50% of the player's special attack energy and deals between 20% and 120% of the user's max hit.
 * <p>
 * Against players, it will also increase the rate in which the target's run energy is drained by sixfold.
 * When this occurs, the target will receive a message in their chatbox stating "You've been hamstrung! For the next minute, your run energy will drain 6x faster."
 */
public class MorrigansThrowingAxe extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(929);
        entity.graphic(1626, GraphicHeight.HIGH, 0);

        //Fire projectile
        new Projectile(entity, target, 1625, 50, 70, 44, 35, 3).sendProjectile();

        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED);
        target.hit(entity, hit, 1, CombatType.RANGED).checkAccuracy().postDamage(this::handleAfterHit).submit();
        CombatSpecial.drain(entity, CombatSpecial.MORRIGANS_THROWING_AXE.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return entity.getCombat().getFightType().getStyle().equals(FightStyle.DEFENSIVE) ? 6 : 4;
    }

    public void handleAfterHit(Hit hit) {
        if(hit.getTarget() instanceof Player) {
            Player player = (Player) hit.getTarget();
            player.getTimers().register(TimerKey.HAMSTRUNG, 100);
            player.message("You've been hamstrung! For the next minute, your run energy will drain 6x faster.");
        }
    }
}

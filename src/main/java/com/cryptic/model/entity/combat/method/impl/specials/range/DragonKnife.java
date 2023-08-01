package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.Player;

/**
 * The dragon knife has a special attack, Duality, which consumes 25% of the player's special attack energy.
 * It causes the player to throw two dragon knives at once, with each knife having their own accuracy and damage rolls.
 * This special attack is similar to dragon dagger's, albeit without an extra increase in accuracy and damage.
 */
public class DragonKnife extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        boolean poisonKnive = player.getEquipment().containsAny(22806, 22808, 22810);

        player.animate(poisonKnive ? 8292 : 8291);

        int projectileId = poisonKnive ? 1629 : 699;

        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration = (40 + 11 + (3 * tileDist));
        Projectile p1 = new Projectile(entity, target, projectileId, 40, duration, 40, 30, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p1);
        for (int i = 0; i < 2; i++) {
            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();
            hit.submit();
        }
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_KNIFE.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return entity.getCombat().getFightType().getStyle().equals(FightStyle.DEFENSIVE) ? 6 : 4;
    }
}

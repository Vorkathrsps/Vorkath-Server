package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.Player;

/**
 * The dragon knife has a special attack, Duality, which consumes 25% of the player's special attack energy.
 * It causes the player to throw two dragon knives at once, with each knife having their own accuracy and damage rolls.
 * This special attack is similar to dragon dagger's, albeit without an extra increase in accuracy and damage.
 */
public class DragonKnife extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();
        int delay = (int) (Math.floor(3 + entity.tile().distance(target.tile()) / 6D));

        boolean poisonKnive = player.getEquipment().containsAny(22806, 22808, 22810);

        player.animate(poisonKnive ? 8292 : 8291);

        // Get proper projectile id
        int projectileId = poisonKnive ? 1629 : 699;

        // Send projectiles
        Projectile projectile = new Projectile(player, target, projectileId, 41, delay, 41, 36, 0);

        entity.executeProjectile(projectile);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();

        hit.submit();

        Hit hi2 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();

        hi2.submit();

        CombatSpecial.drain(entity, CombatSpecial.DRAGON_KNIFE.getDrainAmount());
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
}

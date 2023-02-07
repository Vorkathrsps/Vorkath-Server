package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;

public class SmokeDevil extends CommonCombatMethod {

    private boolean smokeAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        Player player = (Player) target;
        new Projectile(entity, target, 643, 15, entity.projectileSpeed(target), 65, 31, 0, 0, 0).sendProjectile();
        int delay = entity.getProjectileHitDelay(target);
        if (player.getEquipment().getId(EquipSlot.HEAD) != 4164 && !player.getEquipment().wearingSlayerHelm()) {
            target.hit(entity, 18, delay, CombatType.MAGIC).submit();
            player.message("<col=ff0000>The devil's smoke blinds and damages you!");
            player.message("<col=ff0000>A facemask can protect you from this attack.");
            return true;
        }
        return false;
    }

    private void magicAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        new Projectile(entity, target, 643, 15, entity.projectileSpeed(target), 65, 31, 0, 0, 0).sendProjectile();
        int delay = entity.getProjectileHitDelay(target);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().postDamage(this::handleAfterHit).submit();
    }

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        if (smokeAttack(entity, target))
            return;
        magicAttack(entity, target);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }

    public void handleAfterHit(Hit hit) {
        hit.getTarget().graphic(643);
    }
}

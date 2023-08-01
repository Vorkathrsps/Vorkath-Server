package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;

public class SmokeDevil extends CommonCombatMethod {

    private boolean smokeAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        Player player = (Player) target;
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 643, 41, duration, 43, 0, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
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
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 643, 51, duration, 43, 0, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
        target.graphic(643, GraphicHeight.LOW, p.getSpeed());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (smokeAttack(entity, target))
            return true;
        magicAttack(entity, target);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

}

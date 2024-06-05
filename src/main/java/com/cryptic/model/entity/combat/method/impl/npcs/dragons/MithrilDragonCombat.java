package com.cryptic.model.entity.combat.method.impl.npcs.dragons;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.Prayer;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;


/**
 * @author Origin | Zerikoth | PVE
 * @date maart 14, 2020 09:50
 */
public class MithrilDragonCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var random = World.getWorld().random().nextInt(0, 7);
        switch (random) {
            case 0,1 -> {
                if (isReachable()) doMelee();
                else doDragonBreath();
            }
            case 2,3 -> doMagic();
            case 4,5 -> doRanged();
            case 6,7 -> doDragonBreath();
        }
        return true;
    }

    private void doMelee() {
        entity.animate(80);
        new Hit(entity, target, 1, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void doMagic() {
        entity.animate(6722);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        var duration = 51 + 10 * (tileDist);
        Projectile p = new Projectile(entity, target, 136, 51, duration, 17, 23, 10, entity.getSize(), 36, 0);
        final int delay = entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
    }

    private void doRanged() {
        entity.animate(6722);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        var duration = 51 + 10 * (tileDist);
        Projectile p = new Projectile(entity, target, 16, 51, duration, 17, 23, 10, entity.getSize(), 36, 0);
        final int delay = entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
    }

    private void doDragonBreath() {
        entity.animate(81);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        var duration = 51 + 10 * (tileDist);
        Projectile p = new Projectile(entity, target, 54, 51, duration, 43, 31, 32, entity.getSize(), 127, 0);
        final int delay = entity.executeProjectile(p);
        if (target instanceof Player player) {
            int antifire = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire > 0;

            var max_damage = 50.0D;

            Hit hit = new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();

            if (player.getEquipment().containsAny(ItemIdentifiers.DRAGONFIRE_WARD, ItemIdentifiers.DRAGONFIRE_WARD_22003, ItemIdentifiers.DRAGONFIRE_SHIELD, ItemIdentifiers.DRAGONFIRE_SHIELD_11284, ItemIdentifiers.ANTIDRAGON_SHIELD, ItemIdentifiers.ANTIDRAGON_SHIELD_8282)) {
                player.message("Your shield absorbs most of the dragon fire!");
                max_damage *= 0.10D;
            }

            if (player.getPrayer().isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) {
                player.message("Your prayer absorbs most of the dragon's breath!");
                max_damage *= 0.60D;
            }

            if (antifire > 0) {
                player.message("Your potion protects you from the heat of the dragon's breath!");
                max_damage *= 0.30D;
            }

            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false)) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max_damage = 0;
            }

            if (hasShield && hasPotion) {
                max_damage = 0;
            }

            var damage = Utils.random(1, (int) max_damage);

            if (max_damage == 0) {
                damage = 0;
            }

            if (damage == 50) player.message("You are badly burned by the dragon fire!");

            int finalDamage = damage;
            hit.postDamage(post -> {
                if (post.isAccurate() && post.getDamage() > 0) post.setDamage(finalDamage);
                else hit.block();
            });
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}

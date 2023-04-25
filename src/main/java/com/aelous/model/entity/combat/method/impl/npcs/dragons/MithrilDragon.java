package com.aelous.model.entity.combat.method.impl.npcs.dragons;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Utils;


/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 14, 2020 09:50
 */
public class MithrilDragon extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            if (Utils.rollDie(3, 1)) {
                doMelee(entity, target);
            } else {
                breathFire(entity, target);
            }
        } else if (!CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            if (Utils.rollDie(3, 1)) {
                doMagic(entity, target);
            } else {
                doRanged(entity, target);
            }
        }
        return true;
    }

    private void doMelee(Entity entity, Entity target) {
        entity.animate(80);
        target.hit(entity, Utils.random(28), 1, CombatType.MELEE).checkAccuracy().submit();
    }

    private void doMagic(Entity entity, Entity target) {
        entity.animate(6722);
        new Projectile(entity, target, 136, 40, entity.projectileSpeed(target), 20, 31, 0, 10, 36).sendProjectile();
        target.hit(entity, Utils.random(18), entity.getProjectileHitDelay(target), CombatType.MAGIC).checkAccuracy().submit();
    }

    private void doRanged(Entity entity, Entity target) {
        entity.animate(6722);
        new Projectile(entity, target, 16, 40, entity.projectileSpeed(target), 20, 31, 0, 10, 36).sendProjectile();
        target.hit(entity, Utils.random(22), entity.getProjectileHitDelay(target), CombatType.RANGED).checkAccuracy().submit();
    }

    private void breathFire(Entity entity, Entity target) {
        if(target instanceof Player player) {
            double max = 50.0;
            int antifire_charges = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire_charges > 0;

            boolean memberEffect = player.getMemberRights().isExtremeMemberOrGreater(player) && !WildernessArea.inWild(player);
            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false) || memberEffect) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max = 0.0;
            }

            //Does our player have an anti-dragon shield?
            if (max > 0 && (player.getEquipment().hasAt(EquipSlot.SHIELD, 11283) || player.getEquipment().hasAt(EquipSlot.SHIELD, 11284) ||
                player.getEquipment().hasAt(EquipSlot.SHIELD, 1540))) {
                player.message("Your shield absorbs most of the dragon fire!");
                max *= 0.3;
            }

            //Has our player recently consumed an antifire potion?
            if (max > 0 && antifire_charges > 0) {
                player.message("Your potion protects you from the heat of the dragon's breath!");
                max *= 0.3;
            }

            //Is our player using protect from magic?
            if (max > 0 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                player.message("Your prayer absorbs most of the dragon's breath!");
                max *= 0.6;
            }

            if (hasShield && hasPotion) {
                max = 0.0;
            }

            entity.animate(81);
            int hit = Utils.random((int) max);
            var tileDist = entity.tile().distance(target.tile());
            int duration = (41 + 11 + (5 * tileDist));
            Projectile p1 = new Projectile(entity, target, 54, 51, duration, 43, 31, 0, target.getSize(), 5);
            final int delay = entity.executeProjectile(p1);
            target.hit(entity, hit, delay, CombatType.MAGIC).submit();
            if (max == 50 && hit > 0) {
                player.message("You are badly burned by the dragon fire!");
            }

            entity.animate(81);
            entity.graphic(1, GraphicHeight.HIGH, 0);
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}

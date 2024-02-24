package com.cryptic.model.entity.combat.method.impl.npcs.dragons;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.intellij.openapi.project.Project;

/**
 * @author Origin
 * april 28, 2020
 */
public class AdamantDragonCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity dragon, Entity entity) {
        var rand = Utils.random(4);
        NPC npc = (NPC) dragon;

        if (rand == 1) {
            doDragonBreath();
        } else if (rand == 2) {
            doRangedAttack();
        } else if (rand == 3) {
            doMagicBlast();
        } else {
            if (withinDistance(1)) {
                doMelee(npc);
            } else {
                int roll = Utils.random(3);
                if (roll == 1) {
                    doDragonBreath();
                } else if (roll == 2) {
                    doRangedAttack();
                } else if (roll == 3) {
                    doMagicBlast();
                }
            }
        }
        return true;
    }

    private void doMelee(NPC npc) {
        if (!withinDistance(1)) return;
        npc.animate(npc.attackAnimation());
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void doDragonBreath() {
        entity.animate(81);
        var tileDist = entity.tile().distance(target.tile());
        var duration = 51 + 10 * (tileDist);
        Projectile p = new Projectile(entity, target, 54, 51, duration, 43, 31, 32, entity.getSize(), 127, 0);
        final int delay = (int) (p.getSpeed() / 30D);
        entity.executeProjectile(p);
        if (target instanceof Player player) {
            double max = 50.0;
            int antifire_charges = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire_charges > 0;

            boolean memberEffect = player.getMemberRights().isExtremeMemberOrGreater(player) && !WildernessArea.isInWilderness(player);
            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false) || memberEffect) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max = 0.0;
            }
            if (max > 0 && hasShield) {
                player.message("Your shield absorbs most of the dragon fire!");
                max *= 0.3;
            }
            if (max > 0 && hasPotion) {
                player.message("Your potion protects you from the heat of the dragon's breath!");
                max *= 0.3;
            }
            if (max > 0 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                player.message("Your prayer absorbs most of the dragon's breath!");
                max *= 0.6;
            }

            if (hasShield && hasPotion) {
                max = 0.0;
            }

            int hit = World.getWorld().random((int) max);
            new Hit(entity, target, (int) max, delay, CombatType.MAGIC).checkAccuracy(true).submit();
            if (max == 65 && hit > 0) {
                player.message("You are badly burned by the dragon fire!");
            }
        }
    }

    private void doRangedAttack() {
        entity.animate(81);
        var tileDist = entity.tile().distance(target.tile());
        var duration = 51 + 10 * (tileDist);
        Projectile p = new Projectile(entity, target, 27, 51, duration, 17, 28, 10, entity.getSize(), 180, 0);
        final int delay = (int) (p.getSpeed() / 30D);
        entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
    }

    private void doMagicBlast() {
        entity.animate(81);
        var tileDist = entity.tile().distance(target.tile());
        var duration = 51 + 10 * (tileDist);
        Projectile p = new Projectile(entity, target, 165, 51, duration, 17, 23, 10, entity.getSize(), 180, 0);
        final int delay = (int) (p.getSpeed() / 30D);
        entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
    }

    private boolean inBlastTile(Entity entity, Area area) {
        return (target.tile().inArea(area));
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }
}

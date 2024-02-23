package com.cryptic.model.entity.combat.method.impl.npcs.fightcaves;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

import javax.annotation.Nonnull;

import static com.cryptic.model.entity.attributes.AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE;

/**
 * @Author: Origin
 * @Date: 7/14/2023
 */
public class TztokJadCombat extends CommonCombatMethod {
    private static final int MAX_DISTANCE = 10;

    @Override
    public void init(NPC npc) {
        npc.getCombatInfo().setAggressive(true);
        npc.putAttrib(ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity.dead()) {
            return false;
        }
        if (withinDistance(1)) {
            if (Utils.rollDie(4, 1)) {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
                entity.animate(entity.attackAnimation());
            } else if (Utils.rollDie(2, 1)) {
                magicAttack(entity, target);
            } else {
                rangeAttack(entity, target);
            }
            return true;
        } else {
            if (Utils.rollDie(2, 1)) {
                magicAttack(entity, target);
            } else {
                rangeAttack(entity, target);
            }
        }
        return true;
    }

    private void magicAttack(@Nonnull Entity entity, @Nonnull Entity target) {
        if (entity.dead()) {
            return;
        }
        entity.animate(2656);
        entity.graphic(447, GraphicHeight.HIGH_10, 30);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (80 + 11 + (8 * tileDist));
        int duration2 = (81 + 12 + (9 * tileDist));
        int duration3 = (82 + 13 + (10 * tileDist));

        final Projectile[] projectileOrder = {
            new Projectile(entity, target, 448, 80, duration, 128, 31, 12, 4, 8),
            new Projectile(entity, target, 449, 81, duration2, 128, 31, 12, 4, 9),
            new Projectile(entity, target, 450, 82, duration3, 128, 31, 12, 4, 10),
        };

        int delay = entity.executeProjectile(projectileOrder[0]);
        for (int i = 1; i < projectileOrder.length; i++) {
            entity.executeProjectile(projectileOrder[i]);
        }

        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
        target.graphic(157, GraphicHeight.MIDDLE, projectileOrder[2].getSpeed());
    }

    private void rangeAttack(@Nonnull Entity entity, @Nonnull Entity target) {
        if (entity.dead()) {
            return;
        }
        entity.animate(2652);
        World.getWorld().tileGraphic(451, target.tile(), 0, 60);
        new Hit(entity, target, 4, CombatType.RANGED).checkAccuracy(true).submit();
        target.graphic(157, GraphicHeight.LOW, 120);
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        var player = (Player) hit.getAttacker();
        if (player.getInstancedArea() != null) {
            player.getInstancedArea().dispose();
            player.teleport(2439, 5169, 0);
            player.getInventory().addOrDrop(new Item(ItemIdentifiers.FIRE_CAPE, 1));
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return MAX_DISTANCE;
    }

    @Override
    public void doFollowLogic() {
        follow(MAX_DISTANCE);
    }
}

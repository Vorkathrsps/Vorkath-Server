package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Utils;

import java.util.List;

/**
 * @author PVE
 * @Since augustus 05, 2020
 */
public class AbyssalDemon extends CommonCombatMethod {

    private void teleportAttack(Entity entity, Entity target) {
        List<Tile> tiles = target.tile().area(1, tile -> !tile.equals(target.tile()) && ProjectileRoute.hasLineOfSight(entity, tile));
        Tile destination = Utils.randomElement(tiles);
        if(destination == null) return;
        entity.graphic(409);
        entity.teleport(destination);
        entity.setFaceTile(target.tile());
    }

    private void basicAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) return false;
        if (Utils.rollDie(8, 1)) teleportAttack(entity, target);
        else basicAttack(entity, target);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}

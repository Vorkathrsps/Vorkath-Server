package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;

import java.util.List;

/**
 * @author PVE
 * @Since augustus 05, 2020
 */
public class AbyssalDemon extends CommonCombatMethod {

    private void teleportAttack(Entity entity, Entity target) {
        Entity e = Utils.rollDie(2, 1) ? entity : target;
        List<Tile> tiles = target.tile().area(1, pos -> World.getWorld().clipAt(pos) == 0 && !pos.equals(entity.tile()) && !ProjectileRoute.allow(entity, pos));
        Tile destination = Utils.randomElement(tiles);
        if(destination == null)
            return;
        e.teleport(destination);
        e.graphic(409);
        if (e == target)
            target.getCombat().reset();
    }

    private void basicAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (Utils.rollDie(4, 1))
            teleportAttack(entity, target);
        else
            basicAttack(entity, target);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}

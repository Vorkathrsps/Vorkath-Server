package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.weapon.WeaponType;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * Represents the combat method for melee attacks.
 *
 * @author Professor Oak
 */
public class MeleeCombatMethod extends CommonCombatMethod {

    private void attackWithScythe(Entity target) {
        entity.animate(entity.attackAnimation());

        final Tile el = entity.getCentrePosition();
        final Tile vl = target.getCentrePosition();

        int gfx;

        Direction dir = Direction.getDirection(el, vl);

        switch (dir) {
            case SOUTH, SOUTH_EAST -> {
                dir = Direction.SOUTH;
                gfx = 478;
            }
            case NORTH, NORTH_WEST -> {
                gfx = 506;
                dir = Direction.NORTH;
            }
            case EAST, NORTH_EAST -> {
                gfx = 1172;
                dir = Direction.EAST;
            }
            default -> {
                gfx = 1231;
                dir = Direction.WEST;
            }
        }

        Tile gfxTile = entity.getCentrePosition().transform(dir.x, dir.y);

        World.getWorld().tileGraphic(gfx, gfxTile, 96, 20);

        //entity.graphic(478, 100, 0);
        if(target.getAsNpc().getSize() > 2 || target.getAsNpc().isCombatDummy()) {
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
            target.hit(entity, (int) (CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE) * 0.50D), 0, CombatType.MELEE).checkAccuracy().submit();
            target.hit(entity, (int) (CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE) * 0.25D), 0, CombatType.MELEE).checkAccuracy().submit();
        } else {
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity instanceof NPC npc) {
            if (npc.getId() == 10865 || npc.getId() == 10814 || npc.getId() == 8340) {
                return false;
            }
            if (!withinDistance(1)) {
                return false;
            }
        }

        if (target.isNpc() && entity instanceof Player player) {
            if (player.getEquipment().containsAny(HOLY_SCYTHE_OF_VITUR, SANGUINE_SCYTHE_OF_VITUR, SCYTHE_OF_VITUR)) {
                attackWithScythe(target);
                return true;
            }
        }

        final Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        entity.animate(new Animation(entity.attackAnimation(), Priority.HIGH));
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        if (entity.getCombat().getWeaponType() == WeaponType.HALBERD) {
            return 2;
        }
        return 1;
    }

}

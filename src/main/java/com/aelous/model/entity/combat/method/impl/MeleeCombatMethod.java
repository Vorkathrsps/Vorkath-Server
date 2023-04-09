package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

import static com.aelous.utility.ItemIdentifiers.SCYTHE_OF_VITUR;

/**
 * Represents the combat method for melee attacks.
 *
 * @author Professor Oak
 */
public class MeleeCombatMethod extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (target.isNpc() && entity.isPlayer()) {
            Player player = (Player) entity;
            if (player.getEquipment().hasAt(EquipSlot.WEAPON, SCYTHE_OF_VITUR)) {
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

                World.getWorld().tileGraphic(gfx, gfxTile, 96, 30);

                //entity.graphic(478, 100, 0);
                if(target.getAsNpc().getSize() > 2 || target.getAsNpc().isCombatDummy()) {
                    target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
                    target.hit(entity, (int) (CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE) * 0.50D), 0, CombatType.MELEE).checkAccuracy().submit();
                    target.hit(entity, (int) (CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE) * 0.25D), 0, CombatType.MELEE).checkAccuracy().submit();
                } else {
                    target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
                }
                return true;
            }
        }
        if (entity.isNpc()) {
            if (!withinDistance(1))
                return false;
        }

        final Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        entity.animate(entity.attackAnimation());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        if (entity.getCombat().getWeaponType() == WeaponType.HALBERD) {
            return 2;
        }
        return 1;
    }

}

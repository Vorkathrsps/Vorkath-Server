package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
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

        Hit[] hit = new Hit[]
            {
                new Hit(entity, target, this, 0, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE)),
                new Hit(entity, target, this, 0, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE)),
                new Hit(entity, target, this, 0, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE))
            };

        hit[0].checkAccuracy(true).submit();
        if (target.getAsNpc().getSize() > 2 || target.getAsNpc().isCombatDummy()) {
            hit[1].checkAccuracy(true).submit();
            hit[2].checkAccuracy(true).submit();
            if (hit[0].isAccurate() && hit[1].isAccurate()) {
                hit[1].setDamage(hit[0].getDamage() / 2);
            }
            if (hit[1].isAccurate() && hit[2].isAccurate()) {
                hit[2].setDamage((int) (hit[1].getDamage() / 2.12));
            }
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity instanceof NPC npc) {
            if (npc.getId() == 10865 || npc.getId() == 10814 || npc.getId() == 8340 || npc.getId() == NpcIdentifiers.VERZIK_VITUR || npc.getId() == NpcIdentifiers.VERZIK_VITUR_8372 || npc.getId() == NpcIdentifiers.VERZIK_VITUR_8373 || npc.getId() == NpcIdentifiers.VERZIK_VITUR_8374 || npc.getId() == NpcIdentifiers.VERZIK_VITUR_8375 || npc.getId() == 8386) {
                return false;
            }
            if (!withinDistance(1)) {
                return false;
            }
        }

        if (target.isNpc() && entity instanceof Player player) {
            if (player.getEquipment().containsAny(HOLY_SCYTHE_OF_VITUR, SANGUINE_SCYTHE_OF_VITUR, SCYTHE_OF_VITUR, CORRUPTED_SCYTHE_OF_VITUR)) {
                attackWithScythe(target);
                return true;
            }
        }

        entity.animate(new Animation(entity.attackAnimation(), Priority.HIGH));
         entity.submitHit(target, 0, this);
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

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
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.NYLOCAS_VASILIAS_8356;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * Represents the combat method for melee attacks.
 *
 * @author Professor Oak
 */
public class MeleeCombatMethod extends CommonCombatMethod {
    public static final int[] immune_to_melee = new int[]{NpcIdentifiers.NYLOCAS_HAGIOS, NpcIdentifiers.NYLOCAS_TOXOBOLOS_8343, NpcIdentifiers.NYLOCAS_VASILIAS_8357, NYLOCAS_VASILIAS_8356};
    int[] cannot_attack = new int[]{10865, 10814, 8340, 8250, 8372, 8373, 8374, 8375, 8369, 8370, 8386};

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
            if (npc.getCombat().getCombatType() == null) {
                if (ArrayUtils.contains(cannot_attack, npc.id())) return false;
            }
            if (!withinDistance(1)) return false;
        }

        if (target.isNpc() && entity instanceof Player player) {
            if (player.getEquipment().containsAny(HOLY_SCYTHE_OF_VITUR, SANGUINE_SCYTHE_OF_VITUR, SCYTHE_OF_VITUR, CORRUPTED_SCYTHE_OF_VITUR)) {
                attackWithScythe(target);
                return true;
            }
        }

        entity.animate(entity.attackAnimation());
        Hit hit = new Hit(entity, target, 0, this);

        if (entity instanceof Player player) {
            var weapon = player.getEquipment().getWeapon();
            var fightType = player.getCombat().getFightType();
            if (fightType != null) {
                switch (fightType) {
                    case UNARMED_PUNCH, UNARMED_BLOCK -> player.sendPublicSound(2566, 0);
                    case UNARMED_KICK -> player.sendPublicSound(2565, 0);
                }
            }
            if (weapon != null) {
                var sound = World.getWorld().getSoundLoader().getInfo(player.getEquipment().getWeapon().getId());
                if (sound != null) {
                    player.sendPublicSound(sound.forFightType(player.getCombat().getFightType()), hit.getDelay());
                }
            }
        }

        if (isImmune(target, hit)) return true;
        else hit.checkAccuracy(true).submit();
        return true;
    }

    private boolean isImmune(Entity target, Hit hit) {
        if (target instanceof NPC npc) {
            if (ArrayUtils.contains(immune_to_melee, npc.id())) {
                hit.checkAccuracy(false).block().submit();
                return true;
            }
        }
        return false;
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

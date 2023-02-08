package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.ranged.RangedData;
import com.aelous.model.entity.combat.ranged.RangedData.RangedWeapon;
import com.aelous.model.entity.combat.ranged.drawback.*;
import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;

/**
 * Represents the combat method for ranged attacks.
 *
 * @author Professor Oak
 */
public class RangedCombatMethod extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity attacker, Entity target) {
        //TODO sound here
        attacker.animate(attacker.attackAnimation());

        if (attacker.isNpc()) {
            new Projectile(attacker, target, attacker.getAsNpc().combatInfo().projectile, 41, 60, 40, 36, 15).sendProjectile();
            return;
        }

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();

            CombatFactory.decrementAmmo(player);

            WeaponType weaponType = player.getCombat().getWeaponType();
            RangedData.RangedWeaponType weaponTypeSpecial = player.getCombat().getRangedWeapon().getType();
            int delay = (int) (Math.floor(3 + attacker.tile().distance(target.tile()) / 6D));
            double distance = attacker.tile().getChevDistance(target.tile());
            int endHeight = 0;
            int startHeight = 0;
            int startSpeed = 0;
            var graphic = -1;
            var weaponId = player.getEquipment().getId(EquipSlot.WEAPON);
            var ammoId = player.getEquipment().getId(EquipSlot.AMMO);
            var drawbackBow = ArrowDrawBack.find(weaponId, ammoId);
            var drawBackKnife = KnifeDrawback.find(weaponId);
            var drawbackDart = DartDrawback.find(weaponId);
            var thrownDrawBack = ThrownaxeDrawback.find(weaponId);
            var boltDrawBack = BoltDrawBack.find(weaponId, graphic);
            var chinChompaDrawBack = ChinchompaDrawBack.find(weaponId, graphic);

            switch (weaponType) {
                case BOW -> {
                    if (drawbackBow != null) {
                        player.graphic(drawbackBow.gfx, GraphicHeight.HIGH, 0);
                        graphic = drawbackBow.projectile;
                        startSpeed = drawbackBow.startSpeed;
                        startHeight = drawbackBow.startHeight;
                        endHeight = drawbackBow.endHeight;
                    }
                }
                case THROWN -> {
                    if (drawBackKnife != null) {
                        player.graphic(drawBackKnife.gfx, GraphicHeight.HIGH, 0);
                        graphic = drawBackKnife.projectile;
                        startSpeed = drawBackKnife.startSpeed;
                        startHeight = drawBackKnife.startHeight;
                        endHeight = drawBackKnife.endHeight;
                    }
                }
                case CROSSBOW -> {
                    if (boltDrawBack != null) {
                        graphic = boltDrawBack.projectile;
                        startSpeed = boltDrawBack.startSpeed;
                        startHeight = boltDrawBack.startHeight;
                        endHeight = boltDrawBack.endHeight;
                        player.graphic(boltDrawBack.gfx, GraphicHeight.HIGH, 0);
                    }
                }
                case CHINCHOMPA -> {
                    if (chinChompaDrawBack != null) {
                        graphic = chinChompaDrawBack.projectile;
                        startSpeed = chinChompaDrawBack.startSpeed;
                        startHeight = chinChompaDrawBack.startHeight;
                        endHeight = chinChompaDrawBack.endHeight;
                        target.graphic(chinChompaDrawBack.gfx, GraphicHeight.HIGH, (int) (startSpeed + 11 + (5 * distance)));
                    }
                }
            }

            switch (weaponTypeSpecial) {
                case BALLISTA -> {
                    if (drawbackBow != null) {
                        graphic = drawbackBow.projectile;
                        startSpeed = drawbackBow.startSpeed;
                        startHeight = drawbackBow.startHeight;
                        endHeight = drawbackBow.endHeight;
                        target.graphic(drawbackBow.gfx, GraphicHeight.HIGH, (int) (startSpeed + 11 + (5 * distance)));
                    }
                }
                case THROWING_AXES -> {
                    if (thrownDrawBack != null) {
                        player.graphic(thrownDrawBack.gfx, GraphicHeight.HIGH, 0);
                        graphic = thrownDrawBack.projectile;
                        startSpeed = thrownDrawBack.startSpeed;
                        startHeight = thrownDrawBack.startHeight;
                        endHeight = thrownDrawBack.endHeight;
                    }
                }
                case DARTS -> {
                    if (drawbackDart != null) {
                        player.graphic(drawbackDart.gfx, GraphicHeight.HIGH, 0);
                        graphic = drawbackDart.projectile;
                        startSpeed = drawbackDart.startSpeed;
                        startHeight = drawbackDart.startHeight;
                        endHeight = drawbackDart.endHeight;
                    }
                }
            }

            if (graphic != -1) {
                Projectile projectile = new Projectile(attacker, target, graphic, startSpeed, (int) delay, startHeight, endHeight, 0, target.getSize());
                player.executeProjectile(projectile);

                Hit hit = target.hit(attacker, CombatFactory.calcDamageFromType(attacker, target, CombatType.RANGED), (int) delay, CombatType.RANGED).checkAccuracy().postDamage(this::handleAfterHit);

                hit.submit();
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        RangedWeapon weapon = entity.getCombat().getRangedWeapon();
        if (weapon != null) {

            // Long range fight type has longer attack distance than other types
            if (entity.getCombat().getFightType() == weapon.getType().getLongRangeFightType()) {
                return weapon.getType().getLongRangeDistance();
            }

            return weapon.getType().getDefaultDistance();
        }
        return 6;
    }

    public void handleAfterHit(Hit hit) {
        if (hit.getAttacker() == null) {
            return;
        }

        final RangedWeapon rangedWeapon = hit.getAttacker().getCombat().getRangedWeapon();

        if (rangedWeapon == null) {
            return;
        }

    }
}

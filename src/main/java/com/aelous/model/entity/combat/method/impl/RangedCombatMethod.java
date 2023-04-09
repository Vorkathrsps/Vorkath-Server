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
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
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
    public boolean prepareAttack(Entity attacker, Entity target) {
        //TODO sound here
        attacker.animate(new Animation(attacker.attackAnimation()));

        if (attacker.isNpc()) {
            new Projectile(attacker, target, attacker.getAsNpc().getCombatInfo().projectile, 41, 60, 40, 36, 15).sendProjectile();
            return true;
        }

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();

            CombatFactory.decrementAmmo(player);

            WeaponType weaponType = player.getCombat().getWeaponType();
            RangedData.RangedWeaponType weaponTypeSpecial = player.getCombat().getRangedWeapon().getType();
            int duration = 0;
            int stepMultiplier = 0;
            int distance = attacker.tile().getChevDistance(target.tile());
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
                        attacker.performGraphic(new Graphic(drawbackBow.gfx, GraphicHeight.HIGH, 0));
                        graphic = drawbackBow.projectile;
                        startSpeed = drawbackBow.startSpeed;
                        startHeight = drawbackBow.startHeight;
                        endHeight = drawbackBow.endHeight;
                        stepMultiplier = drawbackBow.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
                case THROWN -> {
                    if (drawBackKnife != null) {
                        target.performGraphic(new Graphic(drawBackKnife.gfx, GraphicHeight.HIGH, 0));
                        graphic = drawBackKnife.projectile;
                        startSpeed = drawBackKnife.startSpeed;
                        startHeight = drawBackKnife.startHeight;
                        endHeight = drawBackKnife.endHeight;
                        stepMultiplier = drawBackKnife.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
                case CROSSBOW -> {
                    if (boltDrawBack != null) {
                        attacker.performGraphic(new Graphic(boltDrawBack.gfx, GraphicHeight.HIGH, 0));
                        graphic = boltDrawBack.projectile;
                        startSpeed = boltDrawBack.startSpeed;
                        startHeight = boltDrawBack.startHeight;
                        endHeight = boltDrawBack.endHeight;
                        stepMultiplier = boltDrawBack.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
                case CHINCHOMPA -> {
                    if (chinChompaDrawBack != null) {
                        graphic = chinChompaDrawBack.projectile;
                        startSpeed = chinChompaDrawBack.startSpeed;
                        startHeight = chinChompaDrawBack.startHeight;
                        endHeight = chinChompaDrawBack.endHeight;
                        stepMultiplier = chinChompaDrawBack.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
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
                        stepMultiplier = drawbackBow.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
                case THROWING_AXES -> {
                    if (thrownDrawBack != null) {
                        graphic = thrownDrawBack.projectile;
                        startSpeed = thrownDrawBack.startSpeed;
                        startHeight = thrownDrawBack.startHeight;
                        endHeight = thrownDrawBack.endHeight;
                        stepMultiplier = thrownDrawBack.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
                case DARTS -> {
                    if (drawbackDart != null) {
                        graphic = drawbackDart.projectile;
                        startSpeed = drawbackDart.startSpeed;
                        startHeight = drawbackDart.startHeight;
                        endHeight = drawbackDart.endHeight;
                        stepMultiplier = drawbackDart.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
            }

            Projectile projectile = new Projectile(attacker, target, graphic, startSpeed, duration, startHeight, endHeight, 0, target.getSize(), stepMultiplier);

            final int hitDelay = attacker.executeProjectile(projectile);

            Hit hit = Hit.builder(attacker, target, CombatFactory.calcDamageFromType(attacker, target, CombatType.RANGED), hitDelay, CombatType.RANGED).checkAccuracy();

            hit.submit();

            if (graphic != -1) {

                if (weaponType == WeaponType.CHINCHOMPA) {
                    if (chinChompaDrawBack != null) {
                        target.performGraphic(new Graphic(chinChompaDrawBack.gfx, GraphicHeight.HIGH, projectile.getSpeed()));
                    }
                }

                if (weaponTypeSpecial == RangedData.RangedWeaponType.BALLISTA) {
                    if (drawbackBow != null) {
                        target.performGraphic(new Graphic(drawbackBow.gfx, GraphicHeight.HIGH, projectile.getSpeed()));
                    }
                }
            }
        }
        return true;
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
}

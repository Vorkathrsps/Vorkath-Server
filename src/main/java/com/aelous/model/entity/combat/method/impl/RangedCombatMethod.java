package com.aelous.model.entity.combat.method.impl;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
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
import com.aelous.utility.ItemIdentifiers;

import java.util.ArrayList;

/**
 * Represents the combat method for ranged attacks.
 *
 * @author Professor Oak
 */
public class RangedCombatMethod extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity attacker, Entity target) {
        attacker.animate(new Animation(attacker.attackAnimation()));

        if (attacker.isNpc()) {
            int tileDist = attacker.tile().transform(3, 3).distance(target.tile());
            int duration = (41 + 11 + (5 * tileDist));
            Projectile p = new Projectile(attacker, target, attacker.getAsNpc().getCombatInfo().projectile, 41, duration, 43, 31, 0, target.getSize(), 5);
            attacker.executeProjectile(p);
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
                        attacker.performGraphic(new Graphic(drawbackBow.gfx, player.getEquipment().contains(ItemIdentifiers.VENATOR_BOW) ? GraphicHeight.LOW : GraphicHeight.HIGH, 0));
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
                default -> {
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
                        attacker.performGraphic(new Graphic(thrownDrawBack.gfx, GraphicHeight.HIGH, 0));
                        graphic = thrownDrawBack.projectile;
                        startSpeed = thrownDrawBack.startSpeed;
                        startHeight = thrownDrawBack.startHeight;
                        endHeight = thrownDrawBack.endHeight;
                        stepMultiplier = thrownDrawBack.stepMultiplier;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
                case DARTS, TOXIC_BLOWPIPE -> {
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

            if (player.getEquipment().contains(ItemIdentifiers.DARK_BOW) || player.getEquipment().contains(ItemIdentifiers.DARK_BOW_BH)) {
                int duration1 = (41 + 11 + (5 * distance));
                int duration2 = (51 + 11 + (5 * distance));
                Projectile p1 = new Projectile(attacker, target, graphic, 41, duration1, 41, 31, 0, target.getSize(), 5);
                Projectile p2 = new Projectile(attacker, target, graphic, 51, duration2, 51, 41, 0, target.getSize(), 5);
                final int d1 = attacker.executeProjectile(p1);
                final int d2 = attacker.executeProjectile(p2);
                Hit hit1 = Hit.builder(attacker, target, CombatFactory.calcDamageFromType(attacker, target, CombatType.RANGED), d1, CombatType.RANGED).checkAccuracy();
                Hit hit2 = Hit.builder(attacker, target, CombatFactory.calcDamageFromType(attacker, target, CombatType.RANGED), d2, CombatType.RANGED).checkAccuracy();
                hit1.submit();
                hit2.submit();
            } else {
                Projectile projectile = new Projectile(attacker, target, graphic, startSpeed, duration, startHeight, endHeight, 0, target.getSize(), stepMultiplier);

                final int hitDelay = attacker.executeProjectile(projectile);

                Hit hit = Hit.builder(attacker, target, CombatFactory.calcDamageFromType(attacker, target, CombatType.RANGED), hitDelay, CombatType.RANGED).checkAccuracy().postDamage(this::handleAfterHit);

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

    public void handleAfterHit(Hit hit) {
        if (hit.getSource() == null) {
            return;
        }

        final RangedWeapon rangedWeapon = hit.getSource().getCombat().getRangedWeapon();
        if (rangedWeapon == null) {
            return;
        }

        boolean chins = rangedWeapon == RangedWeapon.CHINCHOMPA;

        if (chins) {
            hit.getTarget().performGraphic(new Graphic(157, 100, 0));
            chinChompa(hit.getSource(), hit.getTarget(), hit.getDelay());
        }
    }

    private void chinChompa(Entity source, Entity target, int delay) {
        var targets = new ArrayList<Entity>();
        if (target.isPlayer()) {
            World.getWorld().getPlayers().forEachInArea(target.tile().area(1), t -> {
                if (source.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1) {
                    targets.add(t);
                }
            });
        } else {
            World.getWorld().getNpcs().forEachInArea(target.tile().area(1), t -> {
                if (source.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1) {
                    targets.add(t);
                }
            });
        }

        for (Entity targ : targets) {
            if (targ == target || targ == source) {
                //don't hit us, or the target we've already hit
                continue;
            }
            if (targ.isNpc()) {
                var n = targ.getAsNpc();

                if (n.id() == NpcIdentifiers.ROCKY_SUPPORT || n.id() == NpcIdentifiers.ROCKY_SUPPORT_7710 || n.def().isPet) {
                    continue;
                }
            }
            if (!CombatFactory.canAttack(source,this, targ)) { // Validate they're in an attackable location
                continue;
            }

            final Hit hit = targ.hit(source, CombatFactory.calcDamageFromType(source, targ, CombatType.RANGED), delay, CombatType.RANGED);
            hit.checkAccuracy().submit();

            targ.graphic(157, GraphicHeight.HIGH,100);//TODO for Origin, idk how to do delayed gfx on ur base

            targ.putAttrib(AttributeKey.LAST_DAMAGER, source);
            targ.putAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME, System.currentTimeMillis());
            targ.graphic(-1);
        }
        targets.clear();
    }
}

package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.ranged.RangedData;
import com.cryptic.model.entity.combat.ranged.RangedData.RangedWeapon;
import com.cryptic.model.entity.combat.ranged.drawback.*;
import com.cryptic.model.entity.combat.weapon.WeaponType;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

import java.util.ArrayList;

public class RangedCombatMethod extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity attacker, Entity target) {
        attacker.animate(new Animation(attacker.attackAnimation(), Priority.HIGH));

        if (attacker.isNpc()) {
            int tileDist = attacker.tile().transform(3, 3).distance(target.tile());
            int duration = (41 + 11 + (5 * tileDist));
            Projectile p = new Projectile(attacker, target, attacker.getAsNpc().getCombatInfo().projectile, 41, duration, 43, 31, 0, 1, 5);
            attacker.executeProjectile(p);
            return true;
        }

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();

            WeaponType weaponType = player.getCombat().getWeaponType();

            if (player.getCombat().getWeaponType() == null) {
                return false;
            }

            RangedData.RangedWeaponType weaponTypeSpecial = player.getCombat().getRangedWeapon().getType();
            int duration = 0;
            int stepMultiplier = 0;
            int distance = attacker.tile().distance(target.tile());
            int endHeight = 0;
            int startHeight = 0;
            int startSpeed = 0;
            int curve = 0;
            var graphic = -1;
            var weaponId = player.getEquipment().getId(EquipSlot.WEAPON);
            var ammoId = player.getEquipment().getId(EquipSlot.AMMO);
            var drawbackBow = ArrowDrawBack.find(weaponId, ammoId);
            var drawbackBowDouble = DblArrowDrawBack.find(ammoId);
            var drawBackKnife = KnifeDrawback.find(weaponId);
            var drawbackDart = DartDrawback.find(weaponId);
            var thrownDrawBack = ThrownaxeDrawback.find(weaponId);
            var boltDrawBack = BoltDrawBack.find(weaponId, graphic);
            var chinChompaDrawBack = ChinchompaDrawBack.find(weaponId, graphic);

            switch (weaponType) { //TODO convert these to store the data inside of a readable object like toml or json
                case BOW -> {
                    if (drawbackBow != null) {
                        attacker.performGraphic(new Graphic(drawbackBow.gfx, player.getEquipment().contains(ItemIdentifiers.VENATOR_BOW) ? GraphicHeight.LOW : GraphicHeight.HIGH, 0));
                        graphic = drawbackBow.projectile;
                        startSpeed = drawbackBow.startSpeed;
                        startHeight = drawbackBow.startHeight;
                        endHeight = drawbackBow.endHeight;
                        stepMultiplier = drawbackBow.stepMultiplier;
                        curve = 15;
                        duration = startSpeed + 5 + (stepMultiplier * distance);
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
                        curve = 15;
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
                        curve = 5;
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
                        curve = 15;
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
                        curve = 15;
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
                        curve = 15;
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
                        curve = 5;
                        duration = startSpeed + 11 + (stepMultiplier * distance);
                    }
                }
            }

            if (player.getEquipment().contains(ItemIdentifiers.DARK_BOW) || player.getEquipment().contains(ItemIdentifiers.DARK_BOW_BH)) {
                if (drawbackBowDouble != null) attacker.graphic(drawbackBowDouble.gfx, GraphicHeight.HIGH, 0);
                int duration1 = 41 + 5 + (5 * distance);
                int duration2 = 41 + 14 + (10 * distance);
                Projectile p1 = new Projectile(attacker, target, graphic, 41, duration1, 40, 36, 5, 1, 5);
                Projectile p2 = new Projectile(attacker, target, graphic, 41, duration2, 40, 36, 25, 1, 10);
                final int d1 = attacker.executeProjectile(p1);
                final int d2 = attacker.executeProjectile(p2);
                player.submitHit(target, d1, this);
                player.submitHit(target, d2, this);
            } else {
                Projectile projectile = new Projectile(attacker, target, graphic, startSpeed, duration, startHeight, endHeight, curve, 1, stepMultiplier);
                final int hitDelay = attacker.executeProjectile(projectile);
                Hit hit = player.submitHit(target, hitDelay, this);
                var sound = World.getWorld().getSoundLoader().getInfo(player.getEquipment().getWeapon().getId());
                if (sound != null) {
                    player.sendSound(sound.forFightType(player.getCombat().getFightType()), hit.getDelay());
                }
                if (graphic != -1) {
                    if (weaponType == WeaponType.CHINCHOMPA) {
                        if (chinChompaDrawBack != null) {
                            chinChompa(hit.getSource(), hit.getTarget(), hit.getDelay());
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

            CombatFactory.decrementAmmo(player);

        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
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
                continue;
            }
            if (targ.isNpc()) {
                var n = targ.getAsNpc();

                if (n.id() == NpcIdentifiers.ROCKY_SUPPORT || n.id() == NpcIdentifiers.ROCKY_SUPPORT_7710 || n.def().isPet) {
                    continue;
                }
            }
            if (!CombatFactory.canAttack(source, this, targ)) { // Validate they're in an attackable location
                continue;
            }

            final Hit hit = targ.hit(source, CombatFactory.calcDamageFromType(source, targ, CombatType.RANGED), delay, CombatType.RANGED);

            hit.checkAccuracy(true).submit();

            targ.putAttrib(AttributeKey.LAST_DAMAGER, source);
            targ.putAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME, System.currentTimeMillis());
            targ.graphic(-1);
        }
        targets.clear();
    }
}

package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.mechanics.MultiwayCombat;
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
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.Region;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

public class RangedCombatMethod extends CommonCombatMethod {
    public static final int[] immune_to_range = new int[]{NYLOCAS_HAGIOS, NYLOCAS_HAGIOS_8347, NYLOCAS_VASILIAS_8357, NYLOCAS_VASILIAS_8356, NYLOCAS_ISCHYROS_8342, NYLOCAS_ISCHYROS_8345, NYLOCAS_VASILIAS_8355, 7145};

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

            switch (weaponType) {
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
                Hit hit1 = new Hit(attacker, target, d1, this);
                Hit hit2 = new Hit(attacker, target, d2, this);
                if (isImmune(target, hit1)) return true;
                else hit1.checkAccuracy(true).submit();
                if (isImmune(target, hit2)) return true;
                else hit2.checkAccuracy(true).submit();
            } else {
                Projectile projectile = new Projectile(attacker, target, graphic, startSpeed, duration, startHeight, endHeight, curve, 1, stepMultiplier);
                final int hitDelay = attacker.executeProjectile(projectile);
                Hit hit = new Hit(attacker, target, hitDelay, this);
                var sound = World.getWorld().getSoundLoader().getInfo(player.getEquipment().getWeapon().getId());
                if (sound != null)
                    player.sendPrivateSound(sound.forFightType(player.getCombat().getFightType()), hit.getDelay());
                if (isImmune(target, hit)) return true;
                else hit.checkAccuracy(true).submit();
                checkVenatorBow(attacker, target, player, hit);
                if (graphic != -1) {
                    if (weaponType == WeaponType.CHINCHOMPA) {
                        if (chinChompaDrawBack != null) {
                            chinChompa(hit.getSource(), hit.getTarget(), hit.getDelay());
                            target.performGraphic(new Graphic(chinChompaDrawBack.gfx, GraphicHeight.HIGH, projectile.getSpeed()));
                        }
                    }

                    if (weaponTypeSpecial == RangedData.RangedWeaponType.BALLISTA) {
                        if (drawbackBow != null)
                            target.performGraphic(new Graphic(drawbackBow.gfx, GraphicHeight.HIGH, projectile.getSpeed()));
                    }
                }
            }


            CombatFactory.decrementAmmo(player);

        }
        return true;
    }

    private static void checkVenatorBow(Entity attacker, Entity target, Player player, Hit hit) {
        if (player.getEquipment().contains(ItemIdentifiers.VENATOR_BOW) && MultiwayCombat.includes(target.tile())) {
            player.sendPrivateSound(6797);
            hit.postDamage(_ -> sendBouncingArrows(attacker, target));
        }
    }

    private static void sendBouncingArrows(Entity attacker, Entity originalTarget) {
        final Entity original = originalTarget;
        Area area = original.getCentrePosition().area(original.getSize()).enlarge(2);
        final Region region = original.tile().getRegion();
        final Result firstResult = getResult(attacker, original, region, area);
        if (firstResult.closestToPoint().isPresent()) {
            Entity secondTarget = firstResult.temp().stream().filter(entity -> !entity.equals(original)).findFirst().orElse(null);
            if (secondTarget != null) {
                Tile secondCenterPosition = secondTarget.getCentrePosition();
                var distance = secondCenterPosition.distanceTo(original.tile());
                int duration = (int) (15 + (distance));
                Projectile projectile = new Projectile(original, secondTarget, 2007, 0, duration, 31, 31, 127, 1, 0, 0);
                int delay = attacker.executeProjectile(projectile);
                attacker.sendPrivateSound(6672, projectile.getSpeed());
                Hit hit = new Hit(attacker, secondTarget, delay, CombatType.RANGED).checkAccuracy(true).submit();
                if (hit != null) hit.postDamage(h -> h.setDamage(h.getDamage() * 2 / 3));
                area = secondCenterPosition.area(secondTarget.getSize());
                Result secondResult = getResult(attacker, secondTarget, region, area);
                if (secondResult.closestToPoint().isPresent()) {
                    boolean returnToOriginal = World.getWorld().random(2) == 0;
                    Entity thirdTarget;
                    if (returnToOriginal) {
                        thirdTarget = original;
                    } else {
                        List<Entity> potentialThirdTargets = secondResult.temp().stream().filter(entity -> !entity.equals(original) && !entity.equals(secondTarget)).toList();
                        if (!potentialThirdTargets.isEmpty()) {
                            int randomIndex = World.getWorld().random(potentialThirdTargets.size() - 1);
                            thirdTarget = potentialThirdTargets.get(randomIndex);
                        } else {
                            thirdTarget = original;
                        }
                    }
                    if (thirdTarget != null) {
                        distance = secondCenterPosition.distanceTo(thirdTarget.tile());
                        duration = (int) (projectile.getSpeed() + 15 + (distance));
                        projectile = new Projectile(secondTarget, thirdTarget, 2007, projectile.getSpeed(), duration, 31, 31, 127, 1, 0, 0);
                        delay = attacker.executeProjectile(projectile);
                        attacker.sendPrivateSound(6672, projectile.getSpeed());
                        new Hit(attacker, thirdTarget, delay, CombatType.RANGED).checkAccuracy(true).submit();
                    }
                }
            }
        }
    }

    @NotNull
    private static Result getResult(Entity attacker, Entity target, Region region, Area area) {
        Set<Entity> temp = new HashSet<>();

        var cached = NpcDefinition.cached;
        for (NPC npc : region.getNpcs()) {
            if (npc == null || npc.hidden() || !npc.tile().isViewableFrom(attacker.tile())) continue;
            var def = cached.get(npc.getId());
            if (def.isPet || !def.isInteractable || def.actions[1] == null) continue;
            if (area.contains(npc.tile()) && !npc.equals(target)) {
                temp.add(npc);
                break;
            }
        }

        OptionalInt closestToPoint = temp.stream()
            .filter(Objects::nonNull)
            .mapToInt(npc -> npc.getCentrePosition().getNorthEastTile().distance(target.tile()))
            .sorted()
            .findFirst();
        return new Result(temp, closestToPoint);
    }

    private record Result(Set<Entity> temp, OptionalInt closestToPoint) {
    }


    private boolean isImmune(Entity target, Hit hit) {
        if (target instanceof NPC npc) {
            if (ArrayUtils.contains(immune_to_range, npc.id())) {
                hit.checkAccuracy(false).block().submit();
                hit.setImmune(true);
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

                if (n.id() == ROCKY_SUPPORT || n.id() == ROCKY_SUPPORT_7710 || n.def().isPet) {
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

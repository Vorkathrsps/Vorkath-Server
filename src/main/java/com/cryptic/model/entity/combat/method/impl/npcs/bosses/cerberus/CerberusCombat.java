package com.cryptic.model.entity.combat.method.impl.npcs.bosses.cerberus;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.TickDelay;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: Origin
 * @SubAuthor: Patrick
 * Date: 6/1/2023
 */
public class CerberusCombat extends CommonCombatMethod {

    private final TickDelay comboAttackCooldown = new TickDelay();
    private final TickDelay spreadLavaCooldown = new TickDelay();
    private final TickDelay spawnSoulCooldown = new TickDelay();
    private static final Area area = new Area(1358, 1257, 1378, 1257);

    private void rangedAttack(Entity entity, Entity target) {
        if (entity.dead() || target.dead()) return;
        if (comboAttackCooldown.remaining() < 60)
            entity.animate(4492);
        int tileDist = (int) entity.tile().getDistance(target.tile());
        int duration = (80 + 11 + (10 * tileDist));
        var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
        Projectile p = new Projectile(tile, target, 1245, 80, duration, 70, 31, 0, entity.getSize(), 15);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true);
        hit.submit();
        target.graphic(1244, GraphicHeight.HIGH, p.getSpeed());
    }

    private void magicAttack(Entity entity, Entity target) {
        if (entity.dead() || target.dead())
            return; // because in combo attack, its called with a delay in which target can die.
        if (comboAttackCooldown.remaining() < 60) // combo attack anim in progress!
            entity.animate(4492);
        int tileDist = (int) entity.tile().getDistance(target.tile());
        int duration = (80 + 11 + (10 * tileDist));
        var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
        Projectile p = new Projectile(tile, target, 1242, 80, duration, 70, 31, 0, entity.getSize(), 15);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
        hit.submit();
        if (hit.getDamage() > 0) {
            target.graphic(1243, GraphicHeight.HIGH, p.getSpeed());
        }
    }

    private void meleeAttack() {
        if (entity.dead() || target.dead())
            return; // because in combo attack, its called with a delay in which target can die.
        if (comboAttackCooldown.remaining() < 60) // combo attack anim in progress!
            entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void comboAttack(Entity entity, Entity target) {
        // 1:06 https://www.youtube.com/watch?v=hRWrhYgoIbY
        // funny the magic projectile sends before anim.

        comboAttackCooldown.delay(66); // ~40 seconds cooldown

        magicAttack(entity, target); // magic anim wont trigger since combo timer just started

        entity.getUpdateFlag().set(Flag.ANIMATION, false); // overwrite magic anim that above method does

        Chain.noCtx().runFn(2, () -> {
            entity.animate(4490); // triple attack anim 2t after magic.
            rangedAttack(entity, target);
        }).then(2, this::meleeAttack);
    }

    @Override
    public void postAttack() {
        //System.out.printf("%s%n", comboAttackCooldown.remaining());
        if (comboAttackCooldown.remaining() == 66) // just happened
            entity.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 9); // account for combo attack 3 in 1
    }

    boolean spawned = false;

    private void spawnSouls(Entity target) {
        if (spawned) return;

        if (target == null) {
            return;
        }

        if (target.dead()) {
            return;
        }

        NPC[] npcs = new NPC[]{new NPC(5869, new Tile(1239, 1256, 0)), new NPC(5867, new Tile(1240, 1256, 0)), new NPC(5868, new Tile(1241, 1256, 0))};

        for (var npc : npcs) {
            npc.spawn(false);
            npc.face(target);
        }

        NPC melee = npcs[0];
        NPC archer = npcs[1];
        NPC magician = npcs[2];

        Chain.noCtx().runFn(2, () -> {
            melee.setPositionToFace(target.tile());
            melee.animate(8528);
        }).then(1, () -> {
            if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MELEE)) {
                target.hit(melee, 30);
            } else {
                target.hits.block();
            }
            if (!target.getAsPlayer().getEquipment().contains(ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD)) {
                target.getSkills().setLevel(Skills.PRAYER, Math.max(0, target.getSkills().level(Skills.PRAYER) - 30));
            } else {
                target.getSkills().setLevel(Skills.PRAYER, Math.max(0, target.getSkills().level(Skills.PRAYER) - 15));
            }
        });
        Chain.noCtx().runFn(3, () -> {
            archer.setPositionToFace(target.tile());
            archer.animate(8528); //TODO
        }).then(1, () -> {
            if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MISSILES)) {
                target.hit(archer, 30);
                if (!target.getAsPlayer().getEquipment().contains(ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD)) {
                    target.getSkills().setLevel(Skills.PRAYER, Math.max(0, target.getSkills().level(Skills.PRAYER) - 30));
                } else {
                    target.getSkills().setLevel(Skills.PRAYER, Math.max(0, target.getSkills().level(Skills.PRAYER) - 15));
                }
            }
        });
        Chain.noCtx().runFn(4, () -> {
            magician.setPositionToFace(target.tile());
            magician.animate(8528);
        }).then(1, () -> {
            if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
                target.hit(magician, 30);
            }
            if (!target.getAsPlayer().getEquipment().contains(ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD)) {
                target.getSkills().setLevel(Skills.PRAYER, Math.max(0, target.getSkills().level(Skills.PRAYER) - 30));
            } else {
                target.getSkills().setLevel(Skills.PRAYER, Math.max(0, target.getSkills().level(Skills.PRAYER) - 15));
            }
        }).then(1, () -> {
            melee.remove();
            archer.remove();
            magician.remove();
        });

    }

    private void spreadLava() {
        if (target.dead() || target.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
            return;
        }
        spreadLavaCooldown.delay(30);
        entity.animate(4493);
        entity.forceChat("Grrrrrrrrrr");
        Tile[] positions = {target.tile().copy(),
            new Tile(entity.getAbsX() + Utils.random(-1, entity.getSize() - 2), entity.getAbsY() + Utils.random(-4, entity.getSize() + 4), entity.getZ()),
            new Tile(entity.getAbsX() + Utils.random(-1, entity.getSize() - 2), entity.getAbsY() + Utils.random(-4, entity.getSize() + 4), entity.getZ())};
        for (Tile pos : positions) {
            entity.runFn(1, () -> {
                World.getWorld().tileGraphic(1246, new Tile(pos.getX(), pos.getY(), pos.getZ()), 0, 0);
                World.getWorld().tileGraphic(1246, new Tile(pos.getX() - 1, pos.getY() - 1, pos.getZ()), 0, 0);
            }).then(2, () -> {
                if (target == null)
                    return;
                if (target.tile().equals(pos)) {
                    target.hit(entity, World.getWorld().random(10, 15));
                } else if (Utils.getDistance(target.tile(), pos) == 1) {
                    target.hit(entity, 7);
                }
            }).then(2, () -> {
                World.getWorld().tileGraphic(1247, new Tile(pos.getX(), pos.getY(), pos.getZ()), 0, 0);
            }).then(1, () -> {
                if (target == null)
                    return;
                if (target.tile().equals(pos)) {
                    target.hit(entity, World.getWorld().random(10, 18));
                } else if (Utils.getDistance(target.tile(), pos) == 1) {
                    target.hit(entity, 10);
                }
            });
        }
    }

    @Override
    public void onDeath(@Nullable Player killer, NPC npc) {
        comboAttackCooldown.reset();
        spreadLavaCooldown.reset();
        spawnSoulCooldown.reset();
    }

    @Override
    public boolean prepareAttack(@NotNull Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }

        if (!comboAttackCooldown.isDelayed()) {
           // entity.forceChat("combo");
            comboAttack(entity, target);
        } else if (withinDistance(1) && Utils.rollDie(4, 1)) {
           // entity.forceChat("m1");
            meleeAttack();
        } else if (entity.hp() <= 200 && !spreadLavaCooldown.isDelayed() && comboAttackCooldown.isDelayed()) {
           // entity.forceChat("lava");
            spreadLava();
        } else if (Utils.rollDie(1, 2)) {
           // entity.forceChat("ra");
            rangedAttack(entity, target);
        } else if (Utils.rollDie(1, 4)) {
           // entity.forceChat("mage");
            magicAttack(entity, target);
        } else {
            entity.forceChat("Arrrrroooooooooo!");
        spawnSouls(target);
        }

        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 6;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}

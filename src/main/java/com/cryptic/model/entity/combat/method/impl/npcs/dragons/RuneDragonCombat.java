package com.cryptic.model.entity.combat.method.impl.npcs.dragons;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.Prayer;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

/**
 * @author Origin
 * april 29, 2020
 */
public class RuneDragonCombat extends CommonCombatMethod {
    boolean sparkAttack = false;

    @Override
    public boolean prepareAttack(Entity dragon, Entity target) {
        NPC npc = (NPC) dragon;
        var random = World.getWorld().random().nextInt(0, 7);
        switch (random) {
            case 0, 1 -> {
                if (isReachable()) doMelee(npc, target);
                else doDragonBreath();
            }
            case 2, 3 -> doRangedAttack();
            case 4, 5 -> doMagicBlast();
            case 6, 7 -> doDragonBreath();
        }
        return true;
    }

    private void sparkAttack(NPC npc, Entity target) {
        npc.animate(81);
        for (int i = 0; i < 2; i++) {
            Tile pos = target.tile().relative(Utils.get(-1, 1), Utils.get(-1, 1));
            final NPC spark = new NPC(8032, pos);
            int[] ticks = new int[]{0};
            spark.spawn(false);
            Chain.noCtxRepeat().repeatingTask(1, step -> {
                System.out.println(ticks[0]);
                if (ticks[0] == 8) {
                    this.sparkAttack = false;
                    spark.remove();
                    step.stop();
                    return;
                }
                spark.stepAbs(spark.tile().transform(World.getWorld().random(-1, 1), World.getWorld().random(-1, 1)), MovementQueue.StepType.REGULAR);
                if (spark.tile().equals(target.tile())) {
                    int maxDamage = 8;
                    if (target.getAsPlayer().getEquipment().contains(ItemIdentifiers.INSULATED_BOOTS))
                        maxDamage = 2;
                    target.hit(entity, maxDamage);
                }
                ticks[0]++;
            }).then(1, () -> {
                this.sparkAttack = false;
                spark.remove();
                System.out.println("removing on cancel");
            });
        }
    }


    private void doMelee(NPC npc, Entity target) {
        npc.animate(npc.attackAnimation());
        target.hit(npc, Utils.random(npc.getCombatInfo().maxhit), CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void doRangedAttack() {
        entity.animate(81);
        final int damage = CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1486, 41, duration, 43, 31, 16, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        if (Utils.rollDie(5, 2)) {
            target.hit(entity, damage, delay, CombatType.RANGED).submit();
            entity.heal(damage, entity.maxHp());
        } else {
            target.hit(entity, damage, delay, CombatType.RANGED).checkAccuracy(true).submit();
        }
    }

    private void doMagicBlast() {
        entity.animate(81);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 162, 51, duration, 43, 31, 16, entity.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        target.hit(entity,  CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
    }

    private void doDragonBreath() {
        entity.animate(81);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        var duration = 51 + 10 * (tileDist);
        Projectile p = new Projectile(entity, target, 54, 51, duration, 43, 31, 32, entity.getSize(), 127, 0);
        final int delay = entity.executeProjectile(p);
        if (target instanceof Player player) {
            int antifire = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire > 0;

            var max_damage = 50.0D;

            Hit hit = new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();

            if (player.getEquipment().containsAny(ItemIdentifiers.DRAGONFIRE_WARD, ItemIdentifiers.DRAGONFIRE_WARD_22003, ItemIdentifiers.DRAGONFIRE_SHIELD, ItemIdentifiers.DRAGONFIRE_SHIELD_11284, ItemIdentifiers.ANTIDRAGON_SHIELD, ItemIdentifiers.ANTIDRAGON_SHIELD_8282)) {
                player.message("Your shield absorbs most of the dragon fire!");
                max_damage *= 0.10D;
            }

            if (player.getPrayer().isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) {
                player.message("Your prayer absorbs most of the dragon's breath!");
                max_damage *= 0.60D;
            }

            if (antifire > 0) {
                player.message("Your potion protects you from the heat of the dragon's breath!");
                max_damage *= 0.30D;
            }

            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false)) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max_damage = 0;
            }

            if (hasShield && hasPotion) {
                max_damage = 0;
            }

            var damage = Utils.random(1, (int) max_damage);

            if (max_damage == 0) {
                damage = 0;
            }

            if (damage == 50) player.message("You are badly burned by the dragon fire!");

            int finalDamage = damage;
            hit.postDamage(post -> {
                if (post.isAccurate() && post.getDamage() > 0) post.setDamage(finalDamage);
                else hit.block();
            });
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return sparkAttack ? 10 : entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}

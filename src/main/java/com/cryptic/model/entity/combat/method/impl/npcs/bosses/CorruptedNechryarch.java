package com.cryptic.model.entity.combat.method.impl.npcs.bosses;

import com.google.common.collect.Lists;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.TickDelay;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick van Elderen | May, 03, 2021, 16:19
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class CorruptedNechryarch extends CommonCombatMethod {

    private final TickDelay acidAttackCooldown = new TickDelay();
    private final List<Tile> acidPools = Lists.newArrayList();

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
       if (!acidAttackCooldown.isDelayed()) {
            acid_attack(entity, target);
        }
        boolean close = target.tile().isWithinDistance(entity.tile(),2);
        if (close && World.getWorld().rollDie(3))
            melee_attack(entity, target);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

    private void acid_attack(Entity entity, Entity target) {
        acidAttackCooldown.delay(50);
        Tile lastAcidPos = entity.tile();

        ArrayList<Player> targets = new ArrayList<>();
        World.getWorld().getPlayers().forEach(p -> {
            if (p != null && p.tile().inSqRadius(entity.tile(), 8)) {
                targets.add(p);
            }
        });
        for (int cycle = 0; cycle < 1; cycle++) {
            Player random = Utils.randomElement(targets);
            if(random == null) {
                return;
            }
            // so this start delay needs to increase per target so the attack appears
            // in sequence..
            Chain.bound(null).runFn(2, () -> {
                Tile lockonTile = random.tile();
                var tileDist = entity.tile().transform(3, 3, 0).distance(random.tile());
                var delay = Math.max(1, (20 + (tileDist * 12)) / 30);

                //new Projectile(lastAcidPos, lockonTile, -1, 5005, 12 * tileDist, 10, 35, 35, 0, 16, 64).sendProjectile();
                World.getWorld().tileGraphic(5001, lastAcidPos, 0, 0);
                World.getWorld().tileGraphic(5004, lastAcidPos, 0, 0);
                acidPools.add(lastAcidPos);
                for (Player player : targets) {
                    Chain.bound(null).runFn(delay, () -> {
                        if (player.tile().equals(lockonTile)) {
                            int damage = World.getWorld().random(1, 30);
                            player.hit(entity, damage);
                            entity.heal(damage);
                        }
                    }).then(20, acidPools::clear);
                }
                // after fixed delay of 2s
            });
        }
    }

    private void melee_attack(Entity entity, Entity target) {
        entity.animate(4672);
        World.getWorld().getPlayers().forEach(p -> {
            if (p != null && p.tile().isWithinDistance(entity.tile(), 2)) {
                p.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
            }
        });
    }

  /*  private void magic_attack(Entity entity, Entity target) {
        entity.animate(7550); // there
        Arrays.stream(entity.closePlayers(16, 16)).forEach(p -> {
            if (p != null && ProjectileRoute.allow(entity, p)) {
                var tileDist = entity.tile().transform(3, 3, 0).distance(p.tile());
                var delay = Math.max(1, (20 + (tileDist * 12)) / 30);
                new Projectile(entity, target, 5000, 30, 12 * tileDist, 120, 43, 0, 16, 64).sendProjectile();
                p.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
                if (World.getWorld().rollDie(10)) {
                    Chain.bound(null).runFn(delay + 2, () -> {
                        //after hit effects
                        for (int i = 0; i < 5; i++) {
                            p.hit(entity,3);
                            p.graphic(5002);
                        }
                    });
                }
            }
        });
    }*/
}

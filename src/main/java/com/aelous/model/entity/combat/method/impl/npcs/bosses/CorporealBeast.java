package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

public class CorporealBeast extends CommonCombatMethod {

    private final int splashing_magic_gfx = 315;
    private final int splashing_magic_tile_gfx = 317;
    private final int corporeal_beast_animation = 1680;
    private final int splashing_magic_attack_damage = 30;

    public static final Area CORPOREAL_BEAST_AREA = new Area(2974, 4371, 2998, 4395);

    /**
     * If the player steps under the Corporeal Beast, it may perform a stomp attack that will always deal 30–51 damage.
     * This attack is on a timer that checks if any players are under the Corporeal Beast every 7 ticks (4.2 seconds). OK
     */

    private Task stompTask;

    private void checkStompTask() {
        if (stompTask == null) {
            stompTask = new Task("checkStompTask", 7) {
                @Override
                protected void execute() {
                    if (entity.dead() || !entity.isRegistered() || !target.tile().inArea(CORPOREAL_BEAST_AREA)) {
                        stop();
                        return;
                    }
                    World.getWorld().getPlayers().forEachInArea(CORPOREAL_BEAST_AREA, p -> {
                        if (p.boundaryBounds().inside(entity.tile(), entity.getSize())) {
                            stompAttack(entity.getAsNpc(), p);
                        }
                    });
                }

                @Override
                public void onStop() {
                    entity.getCombat().reset();
                }
            }.bind(entity);
            TaskManager.submit(stompTask);
        }
    }

    public void stompAttack(NPC corp, Player player) {
        int maxHit = Utils.random(31, 51);
        corp.animate(1686);
        player.hit(corp, maxHit, 0, CombatType.MELEE).checkAccuracy().submit();
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        checkStompTask();
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && target.tile().equals(entity.tile()) && Utils.securedRandomChance(0.333D)) {
            stompAttack((NPC) entity, (Player) target);
        } else if (Utils.securedRandomChance(0.5D)) {
            entity.animate(corporeal_beast_animation);
            entity.getAsNpc().getCombatInfo().maxhit = 55;
            int duration = (60 + -5 + (10 * tileDist));
            Projectile p = new Projectile(entity, target, 314, 60, duration, 43, 31, 0, target.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
            stat_draining_magic_attack(target);
        } else if (Utils.securedRandomChance(0.5D)) {
            entity.animate(corporeal_beast_animation);
            entity.getAsNpc().getCombatInfo().maxhit = 65;
            splashing_magic_attack(((NPC) entity), target);
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 4);
        } else {
            entity.animate(corporeal_beast_animation);
            entity.getAsNpc().getCombatInfo().maxhit = 65;
            int duration = (60 + -5 + (10 * tileDist));
            Projectile p = new Projectile(entity, target, 316, 60, duration, 40, 25, 0, target.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }

    private void stat_draining_magic_attack(Entity target) {
        Player player = (Player) target;
        if (Utils.securedRandomChance(0.333D)) {
            int reduction = Utils.random(3);
            if (Utils.securedRandomChance(0.5D)) {
                if (player.getSkills().level(Skills.MAGIC) < reduction) {
                    target.getSkills().setLevel(Skills.MAGIC, 0);
                } else {
                    player.getSkills().setLevel(Skills.MAGIC, player.getSkills().level(Skills.MAGIC) - reduction);
                }
                player.message("Your Magic has been slightly drained.");
            } else if (player.getSkills().level(Skills.PRAYER) > reduction) {
                if (player.getSkills().level(Skills.PRAYER) < reduction) {
                    player.getSkills().setLevel(Skills.PRAYER, 0);
                } else {
                    player.getSkills().setLevel(Skills.PRAYER, player.getSkills().level(Skills.PRAYER) - reduction);
                }
                player.message("Your Prayer has been slightly drained.");
            }
        }
    }

    private void splashing_magic_attack(NPC npc, Entity target) {
        npc.animate(corporeal_beast_animation);

        int x = target.tile().x; //The target's x tile
        int z = target.tile().y; //The target's z tile

        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        int duration = (60 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 315, 60, duration, 40, 0, 0, target.getSize(), 10);

        Tile initial_splash = new Tile(x, z, target.tile().level);
        int initial_splash_distance = npc.tile().distance(initial_splash) / 2;

        final int delay = entity.executeProjectile(p);

        Projectile p2 = new Projectile(target.tile(), target.tile().transform(2, 0, 0), 0, 315, p.getSpeed(), 0, 0, 0, 0);
        Projectile p3 = new Projectile(target.tile(), target.tile().transform(0, 2, 0), 0, 315, p.getSpeed(), 0, 0, 0, 0);
        Projectile p4 = new Projectile(target.tile(), target.tile().transform(-2, 0, 0), 0, 315, p.getSpeed(), 0, 0, 0, 0);
        Projectile p5 = new Projectile(target.tile(), target.tile().transform(0, -2, 0), 0, 315, p.getSpeed(), 0, 0, 0, 0);

        entity.executeProjectile(p2);
        entity.executeProjectile(p3);
        entity.executeProjectile(p4);
        entity.executeProjectile(p5);

        Chain.bound(null).name("initial_splash_distance_1_task").runFn(initial_splash_distance, () -> {
            if (target.tile().inSqRadius(p2.getEnd(), 1) && target.tile().inArea(2974, 4371, 2998, 4395)) {
                target.hit(entity, Utils.random(splashing_magic_attack_damage), p2.getSpeed(), CombatType.MAGIC).checkAccuracy().submit();
            }
            if (target.tile().inSqRadius(p3.getEnd(), 1) && target.tile().inArea(2974, 4371, 2998, 4395)) {
                target.hit(entity, Utils.random(splashing_magic_attack_damage), p2.getSpeed(), CombatType.MAGIC).checkAccuracy().submit();
            }
            if (target.tile().inSqRadius(p4.getEnd(), 1) && target.tile().inArea(2974, 4371, 2998, 4395)) {
                target.hit(entity, Utils.random(splashing_magic_attack_damage), p2.getSpeed(), CombatType.MAGIC).checkAccuracy().submit();
            }
            if (target.tile().inSqRadius(p5.getEnd(), 1) && target.tile().inArea(2974, 4371, 2998, 4395)) {
                target.hit(entity, Utils.random(splashing_magic_attack_damage), p2.getSpeed(), CombatType.MAGIC).checkAccuracy().submit();
            }
        });

        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
    }
}

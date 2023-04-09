package com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Boundary;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Tuple;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.security.SecureRandom;
import java.util.*;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.ZOMBIFIED_SPAWN_8063;

public class Vorkath extends CommonCombatMethod {

    public static final SecureRandom RANDOM = new SecureRandom();

    private static final Animation ATTACK_ANIMATION = new Animation(7952, Priority.HIGH);
    private static final Animation MELEE_ATTACK_ANIMATION = new Animation(7953, Priority.HIGH);
    private static final Animation MELEE_ATTACK_ANIMATION_2 = new Animation(7951, Priority.HIGH);
    public static final Animation FIREBALL_ATTACK_ANIMATION = new Animation(7960, Priority.HIGH);
    private static final Animation FIREBALL_SPIT_ATTACK_ANIMATION = new Animation(7957, Priority.HIGH);
    private static final Graphic RANGED_END_GRAPHIC = new Graphic(1478, GraphicHeight.MIDDLE);
    private static final Graphic MAGIC_END_GRAPHIC = new Graphic(1480, GraphicHeight.MIDDLE);
    private static final Graphic REGULAR_DRAGONFIRE_END_GRAPHIC = new Graphic(157, GraphicHeight.MIDDLE);
    private static final Graphic VENOMOUS_DRAGONFIRE_END_GRAPHIC = new Graphic(1472, GraphicHeight.MIDDLE);
    private static final Graphic PRAYER_DRAGONFIRE_END_GRAPHIC = new Graphic(1473, GraphicHeight.MIDDLE);

    private static final int BREATH_START_HEIGHT = 25;
    private static final int BREATH_END_HEIGHT = 31;
    private static final int BREATH_DELAY = 30;
    private static final int BREATH_DURATION_START = 46;
    private static final int BREATH_DURATION_INCREMENT = 8;
    private static final int BREATH_CURVE = 15;
    private static final int BREATH_OFFSET = 255;
    private static final int TILE_OFFSET = 1;

    boolean poison;

    public enum Resistance {
        PARTIAL, FULL
    }

    public Resistance resistance = null;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
       if (entity.<Integer>getAttribOr(AttributeKey.VORKATH_CB_COOLDOWN, 0) > 0)
            return false;

        int count = entity.getAttribOr(VORKATH_NORMAL_ATTACK_COUNT, 6);
        int attackType = 0;

        if (count-- < 1) {
            count = 6; // reset back
            int major = entity.<Integer>getAttribOr(VORKATH_LAST_MAJOR_ATTACK, 0) == 0 ? 1 : 0; // switch last attack
            entity.putAttrib(VORKATH_LAST_MAJOR_ATTACK, major);
            attackType = major == 0 ? 6 : 7; // decide the major attack
        } else {
            if (entity.hasAttrib(VORKATH_LINEAR_ATTACKS)) // finish the remaining grouped triple attacks
                attackType = 4;
            else {
                // choose a random attack, only melee if close
                attackType = !CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) ? 2 + RANDOM.nextInt(4) : 1 + RANDOM.nextInt(5);
            }
        }
        entity.putAttrib(VORKATH_NORMAL_ATTACK_COUNT, count);

        bomb();

       // switch (attackType) {
            //case 1 -> melee();
            //case 2 -> mage();
            //case 3 -> range();
            //case 4 -> tripleOrdered();
         //   case 1 -> bomb();
            //case 6 -> acidSpitball();
            //case 7 -> zombified();
      //  }
        return true;
    }

    private void bomb() {
        entity.animate(FIREBALL_ATTACK_ANIMATION);
        Tile targPos = target.tile().copy();
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        //Projectile p1 = new Projectile(entity.tile(), targPos, 1491, 51, duration, 165, 0, 0, target.getSize(), 10);
        //new Projectile(entity.tile(), targPos, 1491, 41, duration, 165, 0, 0, 0, 5).sendProjectile();
        new Projectile(entity.tile().transform(1, -4, 0), targPos.transform(-4, 1, 0), 0, 1491, 165, 30, 200, 0, 0).sendProjectile();

        //inal int delay = entity.executeProjectile(p1);
        entity.runUninterruptable(7, () -> World.getWorld().getPlayers().forEachInArea(targPos.area(1), p -> p.hit(entity, p.tile().equals(targPos) ? Utils.random(121) : Utils.random(60), 0)));
        target.graphic(1466, GraphicHeight.LOW, 30);
        Chain.bound(null).runFn(tileDist, () -> entity.setEntityInteraction(target));
    }

    private void range() {
        entity.animate(ATTACK_ANIMATION);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1477, 41, duration, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, Utils.random(32), delay, CombatType.RANGED).checkAccuracy().submit();
        target.graphic(1478, GraphicHeight.MIDDLE, p.getSpeed());
        Chain.bound(null).runFn(1, () -> entity.setEntityInteraction(target));
    }

    private void mage() {
        entity.animate(ATTACK_ANIMATION);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1479, 51, duration, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, Utils.random(30), delay, CombatType.MAGIC).checkAccuracy().submit();
        target.graphic(1480, GraphicHeight.MIDDLE, p.getSpeed());
        Chain.bound(null).runFn(1, () -> entity.setEntityInteraction(target));
    }

    private void melee() {
        //mob.forceChat("melee");
        entity.animate(MELEE_ATTACK_ANIMATION_2);
        target.hit(entity, Utils.random(32), CombatType.MELEE).checkAccuracy().submit();
        Chain.bound(null).runFn(1, () -> entity.setEntityInteraction(target));
    }

    private void tripleOrdered() {
        if (!entity.hasAttrib(VORKATH_LINEAR_ATTACKS) || entity.<List<Integer>>getAttrib(VORKATH_LINEAR_ATTACKS).isEmpty()) {
            List<Integer> attackIds = new LinkedList<>(Arrays.asList(0, 1, 2));
            Collections.shuffle(attackIds);
            entity.putAttrib(VORKATH_LINEAR_ATTACKS, attackIds);
        }
        LinkedList<Integer> attackIds = entity.getAttrib(VORKATH_LINEAR_ATTACKS);
        switch (attackIds.pop()) {
            case 0 -> {
                //mob.forceChat("venom");
                // venom
                entity.animate(ATTACK_ANIMATION);
                new Projectile(entity, target, 1470, BREATH_DELAY, entity.projectileSpeed(target), BREATH_START_HEIGHT, BREATH_END_HEIGHT, 1, true).sendProjectile();
                target.runOnceTask(3, r -> target.performGraphic(VENOMOUS_DRAGONFIRE_END_GRAPHIC));
                if (Utils.random(4) <= 3)
                    target.venom(entity);
                fireDamage();
            }
            case 1 -> {
                //mob.forceChat("purple");
                // purple prayer
                entity.animate(ATTACK_ANIMATION);
                new Projectile(entity, target, 1471, BREATH_DELAY, entity.projectileSpeed(target), BREATH_START_HEIGHT, BREATH_END_HEIGHT, 1, true).sendProjectile();
                target.runOnceTask(3, r -> target.performGraphic(PRAYER_DRAGONFIRE_END_GRAPHIC));
                fireDamage();
                if (target.isPlayer()) {
                    for (int i = 0; i < target.getAsPlayer().getPrayerActive().length; i++) {
                        Prayers.deactivatePrayer(target, i);
                    }
                    target.getAsPlayer().message("Your prayers have been disabled!");
                }
            }
            case 2 -> {
                //mob.forceChat("normal");
                // normal orange dragonfire from kbd
                target.runOnceTask(3, r -> target.performGraphic(REGULAR_DRAGONFIRE_END_GRAPHIC));
                new Projectile(entity, target, 393, BREATH_DELAY, entity.projectileSpeed(target), BREATH_START_HEIGHT, BREATH_END_HEIGHT, 1, true).sendProjectile();
                entity.animate(ATTACK_ANIMATION);
                fireDamage();
            }
        }
        if (attackIds.isEmpty()) // attacks done
            entity.clearAttrib(VORKATH_LINEAR_ATTACKS);
    }

    private void fireDamage() {
        if (target instanceof Player) {
            Player player = (Player) target;
            int max = 73;
            var antifire_charges = player.<Integer>getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            var hasShield = CombatConstants.hasAntiFireShield(player);
            var superAntifire = player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false);
            var prayerProtection = Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC);

            //If player is wearing a anti-dragon shield max hit is 20
            if(hasShield) {
                max = 20;
            }

            //If player is using protect from magic max hit is 30
            if(prayerProtection) {
                max = 30;
            }

            //If player is using protect from magic and anti-fire shield max hit 20
            if(hasShield && prayerProtection) {
                max = 20;
            }

            //If player is using anti-fire shield and antifire potion max hit 10
            if(hasShield && antifire_charges > 0) {
                max = 10;
            }

            //If player is using anti-fire shield and super antifire potion max hit 0
            if(hasShield && superAntifire) {
                max = 0;
            }

            //If player is using protect from magic and anti-fire potion max hit remains 20
            if(prayerProtection && antifire_charges > 0) {
                max = 20;
            }

            //If player is using protect from magic and super anti-fire potion max hit is 10
            if(prayerProtection && superAntifire) {
                max = 10;
            }

            //If player is using anti-fire shield, protect from magic and anti-fire potion max hit is 10
            if(hasShield && prayerProtection && antifire_charges > 0) {
                max = 10;
            }

            //If player is using anti-fire shield, protect from magic and super anti-fire potion max hit is 0
            if(hasShield && prayerProtection && superAntifire) {
                max = 0;
            }

            var hit = World.getWorld().random(max);
            var delay = entity.getProjectileHitDelay(target);
            player.hit(entity, hit, delay, CombatType.MAGIC).submit();
            if (hit > 30) {
                // maxhit wasnt reduced by any factors
                player.message("You are badly burned by the dragon fire!");
            }
        }
    }

    private void poisonPools() {
        //mob.forceChat("Poison pools");
        if (!poison) {
            resistance = Resistance.PARTIAL;
            poison = true;
            entity.lockNoDamage();
            Boundary npcBounds = entity.boundaryBounds();
            Boundary poisonBounds = entity.boundaryBounds(6);
            List<GameObject> poisons = new ArrayList<>();
            List<Tile> poisonTiles = new ArrayList<>();
            for (int x = poisonBounds.getMinimumX(); x < poisonBounds.getMaximumX(); x++) {
                for (int y = poisonBounds.getMinimumY(); y < poisonBounds.getMaximumY(); y++) {
                    if (Utils.random(3) == 2) {
                        Tile tile = new Tile(x, y, entity.tile().getLevel());
                        if (!npcBounds.inside(tile)) {
                            GameObject obj = new GameObject(32000, tile, 10, Utils.random(3)).setSpawnedfor(Optional.of(target.getAsPlayer()));
                            poisons.add(obj);
                            poisonTiles.add(tile);
                        }
                    }
                }
            }
            Player[] yo = entity.closePlayers(64);
            for (GameObject object : poisons) {
                /*World.getWorld().getObjects().removeIf(o -> o.tile().equals(object.tile())
                    && o.getType() == object.getType());
                World.getWorld().getObjects().add(object);*/
                for (Player player : yo) {
                    player.getPacketSender().sendObject(object);
                }
            }

            for (Tile poisonTile : poisonTiles) {
                for (Player player : yo) {
                    new Projectile(entity.getCentrePosition(), poisonTile, 1, 1483, 50, 20, 30, 0, 25).sendFor(player);
                }
            }

            Task.repeatingTask(t -> {
                if (finished(entity) || t.tick >= 23) {
                    t.stop();
                } else {
                    for (Player p : yo) {
                        if (poisonTiles.contains(p.tile())) {
                            int hit = Utils.random(8);
                            p.hit(entity, hit, SplatType.POISON_HITSPLAT);
                            entity.heal(hit);
                        }
                    }
                }
            });

            Chain.bound(null).runFn(1 + 23 + 1, () -> {
                resistance = null;
                poison = false;
               // World.getWorld().getObjects().removeAll(poisons);
                poisons.forEach(object -> {
                  //  MapObjects.remove(object);
                    for (Player player : yo) {
                        player.getPacketSender().sendObjectRemoval(object);
                    }
                });
                poisons.clear();
                poisonTiles.clear();
            });
            entity.unlock(); // was locked so no damage can by taken
            Chain.bound(null).runFn(23, () -> entity.setEntityInteraction(target));
        }
    }

    private void acidSpitball() {
        //mob.forceChat("acidSpitball");
        entity.animate(FIREBALL_SPIT_ATTACK_ANIMATION); // this anim lasts like 15+ seconds
        poisonPools(); // this is a 2 in 1, does pools first then spitballs
        entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 27);
        entity.runUninterruptable(2, this::startSpitball);
    }

    private void startSpitball() {
        //mob.forceChat("speed spitball");
        final Area area = new Area(2257, 4053, 2286, 4077).transform(0, 0, 0, 0, target.tile().level);
        Optional<Player> first = World.getWorld().getPlayers().search(p -> p.tile().inAreaZ(area));
        first.ifPresent(player -> TaskManager.submit(new Task() {
            int loops = 25;

            @Override
            protected void execute() {
                if (Vorkath.finished(entity)) {
                    stop();
                    return;
                }
                if (loops-- > 0) {
                    //mob.forceChat("pew " + loops);
                    Tile landed = player.tile();
                    new Projectile(entity.getCentrePosition(), landed, 0, 1482, 75, 20, 20, 20, 1).sendProjectile();

                    Chain.bound(null).runFn(1, () -> World.getWorld().getPlayers().forEachFiltered(p -> p.tile().equals(landed), p -> p.hit(entity, World.getWorld().random(1, 15), 1)));
                    //System.out.println("pew");
                    return;
                }
                entity.animate(-1);
                stop();
                Chain.bound(null).runFn(1, () -> {
                    entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 0);
                    entity.setEntityInteraction(target);
                });
            }
        }));
    }

    public NPC spawn = null;

    private void zombified() {
        resistance = Resistance.FULL;
        entity.lockNoDamage();
        entity.animate(7960);
        new Projectile(entity, target, 395, BREATH_DELAY, 60, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 1, true).sendProjectile();
        target.freeze(30, entity);
        target.graphic(369, GraphicHeight.LOW, 30);
        Tile pos = Utils.randomElement(entity.getCentrePosition().area(7, t ->
            World.getWorld().meleeClip(t.x, t.y, t.level) == 0
                && t.isWithinDistance(target.getCentrePosition(), 10)
                && !t.isWithinDistance(target.getCentrePosition(), 3)
                && entity.getAsNpc().spawnTile().y - t.y <= 8));
        new Projectile(entity.getCentrePosition(), pos, 0, 1484, 65, 20, 20, 20, 1).sendProjectile();
        entity.repeatingTask(t -> { // ok to run forever, shouldnt get interrupted
            entity.putAttrib(VORKATH_CB_COOLDOWN, 5);
            if (t.tick == 3) {
                spawn = new NPC(ZOMBIFIED_SPAWN_8063, pos).respawns(false);
                spawn.faceEntity(target);
                spawn.getCombat().setTarget(target);
                World.getWorld().registerNpc(spawn);

                //Add the spawn to the instance list
                if (target != null && target instanceof Player) {
                    Player player = (Player) target;
                    player.getInstancedArea().addNpc(spawn);
                }
            } else if (t.tick > 3) {
                if (t.tick > 20) {
                    if (spawn != null && !spawn.dead() && !spawn.finished()) {
                        spawn.hit(spawn, spawn.hp());

                        //Remove the spawn from the instance list
                        if (target != null && target instanceof Player) {
                            Player player = (Player) target;
                            player.getInstancedArea().addNpc(spawn);
                        }
                    }
                }
                if (spawn != null && (spawn.dead() || spawn.finished())) {
                    t.stop();
                    spawn = null;

                    //Remove the spawn from the instance list
                    if (target != null && target instanceof Player) {
                        Player player = (Player) target;
                        player.getInstancedArea().addNpc(spawn);
                    }
                } else {
                    if (spawn != null && spawn.tile().getChevDistance(target.tile()) <= 1) {
                        spawn.hit(spawn, spawn.hp());
                        target.hit(entity,60, 1);
                        spawn.graphic(157);

                        //Remove the spawn from the instance list
                        if (target != null && target instanceof Player) {
                            Player player = (Player) target;
                            player.getInstancedArea().addNpc(spawn);
                        }
                    }
                }
            }
        }).onStop(() -> {
            resistance = null;
            target.getTimers().cancel(TimerKey.FROZEN);
            target.getTimers().cancel(TimerKey.REFREEZE);
            entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 0);
            entity.unlock();
            entity.getCombat().attack(target);
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 15;
    }

    public static boolean finished(Entity entity) {
        if (entity != null) {
            Tuple<Integer, Player> player = entity.getAsNpc().getAttribOr(AttributeKey.OWNING_PLAYER, new Tuple<>(-1, null));
            return entity.isNpc() && (entity.dead() || !entity.isRegistered()) || (player.second() != null && (player.second().dead()
                || !player.second().isRegistered() || player.second().tile().getChevDistance(entity.tile()) > 30));
        }
        return false;
    }
}

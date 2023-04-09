package com.aelous.model.entity.combat.method.impl.npcs.verzik;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.VERZIKS_THRONE_32737;
import static com.aelous.model.entity.combat.method.impl.npcs.verzik.VerzikPhase.INIT_PHASE_2;
import static com.aelous.utility.ItemIdentifiers.DAWNBRINGER;

/**
 * @author Patrick van Elderen <<a href="https://github.com/PVE95">...</a>>
 * @Since January 07, 2022
 */
public class VerzikVitur extends CommonCombatMethod {

    private static final int NYLOCAS_ATHANATOS_SPAWN_ANIMATION_ID = 8079;
    private static final int OUT_OF_CHAIR = 8111;
    private static final int CHAIR_ATTACK = 8109;
    private static final Tile SPIDER_SPAWN = new Tile(3171, 4315);
    private static final Area ARENA = new Area(3154, 4303, 3182, 4322);
    private int bombCount = 0;
    private int electricCount = 0;
    private VerzikPhase phase = VerzikPhase.PHASE_1;

    @Override
    public void init(NPC npc) {
        npc.getCombatInfo().scripts.agro_ = (n, t) -> phase != INIT_PHASE_2;
    }

    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        mob.faceEntity(target);
        Player[] targets = mob.closePlayers(32);
        if (mob.npc().id() == VERZIK_VITUR_8370) {
            mob.animate(CHAIR_ATTACK);
            for (Player t : targets) {
                if (t.player().dead() || !t.tile().inArea(ARENA)) {
                    continue;
                }
                final Tile targetPos = t.tile().copy();
                var tileDist = entity.tile().distance(targetPos);
                int duration = (85 + -5 + (10 * tileDist));
                Projectile p = new Projectile(entity, targetPos, 1580, 85, duration, 105, 0, 0, target.getSize(), 10);
                int delay = p.send(mob, targetPos);
                Chain.bound(mob).name("VerzikViturPrepareAttackTask1").runFn(delay, () -> {
                    if (t.tile().isWithinDistance(targetPos, 1)) {
                        int dmg = Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MAGIC) ? World.getWorld().random(1, 60) : World.getWorld().random(1, 137);
                        t.hit(mob, dmg);
                    }
                });
                World.getWorld().tileGraphic(1582, targetPos, 0, p.getSpeed());
            }
        }
        if (mob.npc().id() == VERZIK_VITUR_8372) {
            mob.animate(8114);
            if (bombCount < 4) {
                for (Player t : targets) {
                    if (t.player().dead() || !t.tile().inArea(ARENA)) {
                        continue;
                    }
                    final Tile targetPos = target.tile().copy();
                    var tileDist = entity.tile().distance(target.tile());
                    int duration = (51 + -5 + (10 * tileDist));
                    Projectile p = new Projectile(entity, targetPos, 1583, 51, duration, 105, 0, 0, target.getSize(), 10);
                    int delay = p.send(mob, targetPos);
                    Chain.bound(mob).name("VerzikViturPrepareAttackTask2").runFn(delay, () -> {
                        if (t != null && t.tile().equals(targetPos)) {
                            t.hit(mob, World.getWorld().random(1, 60));
                        }
                    });
                    World.getWorld().tileGraphic(1584, targetPos, 0, p.getSpeed());
                }
                bombCount++;
            } else {
                if (electricCount < 2) {
                    for (Entity t : targets) {
                        if (t == null || t.player().dead() || !t.tile().isWithinDistance(mob.tile(), 32) || !t.tile().inArea(ARENA)) {
                            continue;
                        }
                        if (Utils.rollPercent(50)) {
                            var targetPos = t.tile().copy();
                            var tileDist = mob.tile().distance(t.tile());
                            int duration = (51 + -5 + (10 * tileDist));
                            Projectile p = new Projectile(mob, targetPos, 1594, 51, duration, 70, 20, 0, target.getSize(), 10);
                            int delay = p.send(mob, t);
                            t.hit(mob, World.getWorld().random(1, 40), delay, CombatType.MAGIC).checkAccuracy().submit();
                        } else {
                            var targetPos = t.tile().copy();
                            var tileDist = mob.tile().distance(t.tile());
                            int duration = (41 + 11 + (5 * tileDist));
                            Projectile p = new Projectile(mob, targetPos, 1593, 41, duration, 43, 31, 0, target.getSize(), 10);
                            int delay = p.send(mob, t);
                            t.hit(mob, World.getWorld().random(1, 40), delay, CombatType.RANGED).checkAccuracy().submit();
                        }
                    }
                    electricCount++;
                    bombCount = 0;
                } else {
                    final Tile targetPos = target.tile().copy();
                    var tileDist = mob.tile().distance(targetPos);
                    int duration = (41 + 11 + (5 * tileDist));
                    Projectile p = new Projectile(mob, targetPos, 1586, 41, duration, 43, 31, 0, target.getSize(), 10);
                    int delay = p.send(mob.getCentrePosition(), target.getCentrePosition());
                    if (target.tile().equals(targetPos)) {
                        Hit hit = target.hit(mob, World.getWorld().random(1, 60), delay, null).checkAccuracy();
                        hit.submit();
                    }
                    Task task = new Task("VerzikViturPrepareAttackTask3", 1) {
                        int count = 0;
                        NPC nylocasAthanatos;
                        NPC bomber;

                        @Override
                        public void execute() {
                            count++;
                            if (count == 5 && nylocasAthanatos == null && bomber == null) {
                                nylocasAthanatos = new NPC(NYLOCAS_ATHANATOS, targetPos).spawn(false);
                                nylocasAthanatos.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);
                                nylocasAthanatos.animate(NYLOCAS_ATHANATOS_SPAWN_ANIMATION_ID);
                                nylocasAthanatos.face(mob);
                                nylocasAthanatos.setInstance(mob.getInstancedArea());
                                nylocasAthanatos.getInstancedArea().addNpc(nylocasAthanatos);
                                bomber = new NPC(NYLOCAS_MATOMENOS_8385, SPIDER_SPAWN.transform(0, 0, target.getZ())).spawn(false);
                                bomber.getCombat().setTarget(target);
                                bomber.animate(8098);
                                bomber.setInstance(mob.getInstancedArea());
                                bomber.getInstancedArea().addNpc(bomber);
                                Chain.noCtx().runFn(2, () -> bomber.face(target));
                            }
                            if (count >= 5 && nylocasAthanatos != null) {
                                if (nylocasAthanatos.hp() < nylocasAthanatos.maxHp()) {
                                    nylocasAthanatos.hit(nylocasAthanatos, nylocasAthanatos.hp());
                                }
                            }
                            if (count >= 15) {
                                if (bomber != null)
                                    bomber.hit(nylocasAthanatos, nylocasAthanatos.hp());
                                if (nylocasAthanatos != null)
                                    nylocasAthanatos.hit(nylocasAthanatos, nylocasAthanatos.hp());
                                stop();
                            }
                            if ((count % 2 == 0) && nylocasAthanatos != null && !nylocasAthanatos.dead()) {
                                var tileDist = entity.tile().distance(target.tile());
                                int duration = (51 + -5 + (10 * tileDist));
                                Projectile p = new Projectile(entity, targetPos, 1578, 100, duration, 50, 0, 0, target.getSize(), 10);
                                int delay = p.send(mob, targetPos);
                                mob.healHit(mob, 6, delay);
                            }
                        }
                    };
                    TaskManager.submit(task);
                    electricCount = 0;
                }
            }
        }
        if (mob.npc().id() == VERZIK_VITUR_8374) {
            int random = World.getWorld().random(0, 2);
            if (random == 0) {
                if (withinDistance(1)) {
                    meleeAttack(mob, target);
                }
            } else if (random == 1) {
                mob.animate(8124);
                for (Entity t : targets) {
                    if (t == null || t.player().dead() || !t.tile().isWithinDistance(mob.tile(), 32) || !t.tile().inArea(ARENA)) {
                        continue;
                    }
                    var tileDist = entity.tile().distance(t.tile());
                    int duration = (45 + -5 + (10 * tileDist));
                    Projectile p = new Projectile(entity, t, 1594, 45, duration, 70, 20, 0, target.getSize(), 10);
                    int delay = p.send(mob, t);
                    t.hit(mob, World.getWorld().random(1, 40), delay, CombatType.MAGIC).checkAccuracy().submit();
                }
            } else if (random == 2) {
                mob.animate(8125);
                for (Entity t : targets) {
                    if (t == null || t.player().dead() || !t.tile().isWithinDistance(mob.tile(), 32) || !t.tile().inArea(ARENA)) {
                        continue;
                    }
                    var tileDist = entity.tile().distance(t.tile());
                    int duration = (75 + -5 + (10 * tileDist));
                    Projectile p = new Projectile(entity, t, 1593, 75, duration, 43, 31, 0, target.getSize(), 10);
                    int delay = p.send(mob, t);
                    t.hit(mob, World.getWorld().random(1, 40), delay, CombatType.RANGED).checkAccuracy().submit();
                }
            }
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity mob) {
        return phase == VerzikPhase.PHASE_1 ? 12 : phase == VerzikPhase.PHASE_2 ? 4 : 7;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 12;
    }

    @Override
    public void doFollowLogic() {
        // Prevents Verzik from following
    }

    @Override
    public void preDefend(Hit hit) {
        if (hit.getTarget().isNpc()) {
            if (phase == VerzikPhase.PHASE_1) {
                hit.setSplatType(SplatType.VERZIK_SHIELD_HITSPLAT);
            }
            if (hit.getTarget().npc().id() == VERZIK_VITUR_8371 || hit.getTarget().npc().id() == VERZIK_VITUR_8375) {
                hit.setDamage(0);
                hit.setSplatType(SplatType.BLOCK_HITSPLAT);
            }
        }
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        if (hit.getTarget().isNpc()) {
            return transform(hit.getTarget());
        }
        return true;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }

    private void meleeAttack(Entity mob, Entity target) {
        mob.animate(8123);
        target.hit(mob, World.getWorld().random(1, 40), 0, CombatType.MELEE).submit();
    }

    private boolean transform(Entity mob) {
        var area = mob.getInstancedArea();
        if (mob.npc().id() == VERZIK_VITUR_8370) {
            mob.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, false);
            mob.npc().canAttack(false);
            mob.animate(OUT_OF_CHAIR);
            mob.faceEntity(null);
            for (Player p : mob.closePlayers()) {
                p.removeAll(new Item(DAWNBRINGER));
                p.getCombat().reset();
            }
            Chain.bound(null).runFn(4, () -> {
                phase = INIT_PHASE_2;
                mob.getCombat().reset();
                mob.npc().transmog(VERZIK_VITUR_8371);
                mob.npc().def(World.getWorld().definitions().get(NpcDefinition.class, VERZIK_VITUR_8371));
                mob.heal(mob.maxHp());
                mob.npc().animate(-1);
                mob.npc().canAttack(true);
            }).waitUntil(1, () -> {
                mob.resetFreeze();
                mob.smartPathTo(new Tile(3167, 4311, mob.tile().level));
                return mob.tile().equals(3167, 4311, mob.getZ());
            }, () -> {
                phase = VerzikPhase.PHASE_2;
                var o = GameObject.spawn(VERZIKS_THRONE_32737, 3167, 4324, mob.getZ(),10,0);
                area.addGameObj(o);
                mob.npc().transmog(VERZIK_VITUR_8372);
                mob.npc().def(World.getWorld().definitions().get(NpcDefinition.class, VERZIK_VITUR_8372));
            });
            return true;
        } else if (mob.npc().id() == VERZIK_VITUR_8372) {
            mob.getCombat().reset();
            mob.npc().canAttack(false);
            mob.animate(8119);
            mob.npc().transmog(VERZIK_VITUR_8374);
            mob.npc().def(World.getWorld().definitions().get(NpcDefinition.class, VERZIK_VITUR_8374));
            mob.heal(mob.maxHp());
            phase = VerzikPhase.PHASE_3;
            Chain.bound(null).runFn(3, () -> {
                mob.animate(-1);
                mob.forceChat("Behold my true nature!");
                mob.npc().canAttack(true);
            });
            return true;
        } else if (mob.npc().id() == VERZIK_VITUR_8374) {
            mob.animate(8128);
            GameObject treasure = new GameObject(TREASURE_ROOM, new Tile(3167, 4324, mob.getZ()), 10, 0);
            Chain.noCtx().runFn(2, () -> {
                mob.animate(-1);
                mob.npc().transmog(VERZIK_VITUR_8375);
            }).then(6, () -> {
                for (NPC npc : mob.getInstancedArea().getNpcs()) {
                    npc.remove();
                }
                mob.npc().remove();
                treasure.spawn();
                area.addGameObj(treasure);
            });
            return true;
        }
        return false;
    }

}

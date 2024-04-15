package com.cryptic.model.entity.combat.method.impl.npcs.bosses.zulrah;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.TickAndStop;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.stream.Stream;

import static com.cryptic.model.entity.attributes.AttributeKey.PLAYER_UID;

/**
 * Created by Bart on 3/6/2016.
 */
@SuppressWarnings("ALL")
public class Zulrah {

    private static boolean instanceFinished(Object mob) {
        if (mob instanceof NPC) {
            NPC npc = (NPC) mob;
            if (npc.dead() || !npc.isRegistered()) {
                return true;
            }
            Tuple<Long, Player> player = npc.getAttribOr(AttributeKey.OWNING_PLAYER, new Tuple<>(-1L, null));
            if (player.second() != null && (player.second().tile().getChevDistance(npc.tile()) > 20 || !player.second().isRegistered())) {
                return true;
            }
        }
        return false;
    }

    public static void startZulrahBattle(NPC npc, Entity target) {
        TaskManager.submit(new Task() {
            @Override
            protected void execute() {
                if (instanceFinished(npc)) {
                    stop();
                    return;
                }
                if (target instanceof Player) {
                    Player player = (Player) target;
                    ObjectManager.removeObj(new GameObject(11701, new Tile(2263, 3071, player.tile().getLevel())));
                    if (!player.getDialogueManager().isActive()) {
                        stop();

                        //System.out.println("zulrah battle begin");
                        // start the battle!
                        TaskManager.submit(new Task() {
                            int rotIndex = 0;
                            boolean init = true;
                            ZulrahPattern rot = ZulrahPattern.PATTERN_1; // randomize when you add new ones
                            int cooldown = 1;

                            @Override
                            protected void execute() {
                                if (instanceFinished(npc)) {
                                    stop();
                                    return;
                                }
                                //System.out.println("zulrah tick "+cooldown+" on idx "+rotIndex+" "+init);
                                if (cooldown-- < 1) {
                                    ZulrahPhase nextPhase = rot.getPhases().get(rotIndex++ % rot.getPhases().size());
                                    doPatternPhase(npc, nextPhase, target, init);
                                    cooldown = getPatternPhaseTime(npc, nextPhase, target, init);
                                    init = false;
                                }
                            }
                        }.bind(npc));
                    }
                }
            }
        }.bind(npc));
    }

    private static int getPatternPhaseTime(NPC npc, ZulrahPhase phase, Entity target, boolean init) {
        int cooldown = 0;
        if (!init) {
            cooldown += 3;
        }
        if (!phase.hasConfig(ZulrahConfig.NO_ATTACK)) {
            switch (phase.getForm()) {
                case MELEE:
                    cooldown += 19;
                    break;
                case MAGIC:
                case RANGE:
                    cooldown += 5 * 3;
                    break;
                case JAD_RM:
                case JAD_MR:
                    cooldown += 10 * 3;
                    break;
            }
        }
        if (phase.hasConfig(ZulrahConfig.FULL_TOXIC_FUMES)) {
            cooldown += 13;
        } else if (phase.hasConfig(ZulrahConfig.SNAKELINGS_CLOUDS_SNAKELINGS)) {
            cooldown += 19;
        } else if (phase.hasConfig(ZulrahConfig.EAST_SNAKELINGS_REST_FUMES)) {
            cooldown += 22;
        } else if (phase.hasConfig(ZulrahConfig.SNAKELING_FUME_MIX)) {
            cooldown += 16;
        }
        cooldown += 3; // sink
        return cooldown;
    }

    private static void doPatternPhase(NPC npc, ZulrahPhase phase, Entity target, boolean init) {
        if (!init) {
            Tile targetTile = npc.spawnTile().transform(phase.getZulrahPosition().getTile().x,
                phase.getZulrahPosition().getTile().y, phase.getZulrahPosition().getTile().level);
            npc.teleport(targetTile);
            Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
                npc.transmog(phase.getForm().getId(), false);
                npc.setPositionToFace(targetTile.transform(2, 2, 0).transform(phase.getZulrahPosition().getDirection().x,
                    phase.getZulrahPosition().getDirection().y, phase.getZulrahPosition().getDirection().level));
                npc.animate(5073);
                npc.lockDelayDamage();
            }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
                _doPatternPhasePart2(npc, phase, target);
            });
            return;
        }
        _doPatternPhasePart2(npc, phase, target);
    }

    private static void _doPatternPhasePart2(NPC npc, ZulrahPhase phase, Entity target) {
        int cooldown = 0;
        final int[] recurringCumlativeTimer = {0};

        npc.setEntityInteraction(target);
        npc.unlock();

        if (!phase.hasConfig(ZulrahConfig.NO_ATTACK)) {
            switch (phase.getForm()) {
                case MELEE:
                    doMeleePhase(npc, target);
                    cooldown += 19;
                    break;
                case MAGIC:
                    cooldown += 5 * 3;
                    for (int i = 0; i < 5; i++) {
                        recurringCumlativeTimer[0]++;
                        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(recurringCumlativeTimer[0] * 3, () -> {
                            if (Utils.getRandom(6) == 1) {
                                doRangedAttack(npc, target);
                            } else {
                                doMagicAttack(npc, target);
                            }
                        });
                    }
                    break;
                case RANGE:
                    cooldown += 5 * 3;
                    for (int i = 0; i < 5; i++) {
                        recurringCumlativeTimer[0]++;
                        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(recurringCumlativeTimer[0] * 3, () -> {
                            doRangedAttack(npc, target);
                        });
                    }
                    break;
                case JAD_RM:
                case JAD_MR:
                    cooldown += 10 * 3;
                    final boolean[] range = {phase.getForm() == ZulrahForm.JAD_RM};
                    for (int i = 0; i < 10; i++) {
                        recurringCumlativeTimer[0]++;
                        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(recurringCumlativeTimer[0] * 3, () -> {
                            if (range[0]) {
                                doRangedAttack(npc, target);
                            } else {
                                doMagicAttack(npc, target);
                            }
                            range[0] = !range[0];
                        });
                    }
                    break;
            }
        }

        Chain.bound(null).cancelWhen(() -> instanceFinished(npc)).runFn(cooldown < 1 ? 1 : cooldown, () -> {
            npc.setEntityInteraction(null);
            if (phase.hasConfig(ZulrahConfig.FULL_TOXIC_FUMES)) {
                fillToxicFumes(npc, target);
            } else if (phase.hasConfig(ZulrahConfig.SNAKELINGS_CLOUDS_SNAKELINGS)) {
                snakelingsCloudsSnakelings(npc, target);
            } else if (phase.hasConfig(ZulrahConfig.EAST_SNAKELINGS_REST_FUMES)) {
                eastSnakelingsRestFumes(npc, target);
            } else if (phase.hasConfig(ZulrahConfig.SNAKELING_FUME_MIX)) {
                snakelingFumeMix(npc, target);
            }
        });
        if (phase.hasConfig(ZulrahConfig.FULL_TOXIC_FUMES)) {
            cooldown += 13;
        } else if (phase.hasConfig(ZulrahConfig.SNAKELINGS_CLOUDS_SNAKELINGS)) {
            cooldown += 19;
        } else if (phase.hasConfig(ZulrahConfig.EAST_SNAKELINGS_REST_FUMES)) {
            cooldown += 22;
        } else if (phase.hasConfig(ZulrahConfig.SNAKELING_FUME_MIX)) {
            cooldown += 16;
        }

        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(cooldown, () -> {
            target.stopActions(false);
            npc.getCombat().reset();
            npc.animate(5072);
            npc.lockDelayDamage();
        }).cancelWhen(() -> instanceFinished(npc)).then(2, () -> {
            npc.unlock();
        });
    }

    private static void doMagicAttack(NPC npc, Entity target) {
        npc.animate(5069);
        var tileDist = npc.tile().distance(target.tile());
        int duration = (40 + 50 + (5 * tileDist));
        var tile = npc.tile().translateAndCenterNpcPosition(npc, target);
        Projectile p = new Projectile(tile, target, 1046, 40, duration, 92, 12, 0, 5, 5);
        final int delay = npc.executeProjectile(p);
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
        target.venom(npc);
    }

    private static void doRangedAttack(NPC npc, Entity target) {
        //npc.forceChat("range attack");
        npc.animate(5069);
        var tileDist = npc.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        var tile = npc.tile().translateAndCenterNpcPosition(npc, target);
        Projectile p = new Projectile(tile, target, 1044, 41, duration, 65, 31, 0, npc.getSize(), 5);
        final int delay = npc.executeProjectile(p);
        int max = npc.getCombatInfo().maxhit;
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
        target.venom(npc);
    }

    private static void doMeleePhase(NPC npc, Entity target) {
        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
            _doMeleePhaseInner(npc, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(10, () -> {
            _doMeleePhaseInner(npc, target);
        });
    }

    private static void _doMeleePhaseInner(NPC npc, Entity target) {
        npc.setEntityInteraction(null);
        npc.animate(5806);
        Tile p1 = target.tile().copy();
        npc.setPositionToFace(p1);
        Chain.bound(null).cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
            npc.setPositionToFace(p1);
        }).cancelWhen(() -> instanceFinished(npc)).then(4, () -> {
            if (p1.area(1).contains(target) && !isMeleeSafespot(npc, target.tile())) {
                target.stun(4);
                target.hit(npc, Utils.random(41), 0, CombatType.MELEE).checkAccuracy(true).submit();
            }
        }).cancelWhen(() -> instanceFinished(npc)).then(2, () -> {
            npc.setEntityInteraction(target);
        }).cancelWhen(() -> instanceFinished(npc)).then(2, () -> {
            npc.setEntityInteraction(null);
        });
    }

    /**
     * kill off alive snakelins on boss death
     */
    public static void death(Player killer, NPC npc) {
        if (!Stream.of(NpcIdentifiers.ZULRAH, NpcIdentifiers.ZULRAH_2043, NpcIdentifiers.ZULRAH_2044).anyMatch(i -> i == npc.id())) {
            return;
        }
        if (killer == null) return;
        for (var region : killer.getRegions()) {
            for (var n : region.getNpcs()) {
                if (n == null) continue;
                if (n.tile().getZ() != killer.getZ()) continue;
                if (n.id() == NpcIdentifiers.SNAKELING) {
                    n.hit(n, n.hp());
                }
            }
        }
        ObjectManager.addObj(new GameObject(11701, new Tile(2263, 3071, killer.tile().getLevel())));
    }

    public static boolean is(Entity entity) {
        return entity.isNpc() && entity.getAsNpc().id() >= NpcIdentifiers.ZULRAH && entity.getAsNpc().id() <= NpcIdentifiers.ZULRAH_2044;
    }

    /**
     * dummy class. do not use for submitting hits otherwise there will be NPEs due to lack of flexibility in customizing internals
     */
    public static final class EmptyCombatMethod extends CommonCombatMethod {

        private Entity entity;
        private Entity target;
        private int max;

        public static EmptyCombatMethod make() {
            return new EmptyCombatMethod();
        }

        @Override
        public boolean prepareAttack(Entity entity, Entity target) {
            return true;
        }

        @Override
        public int getAttackSpeed(Entity entity) {
            return 1;
        }

        @Override
        public int moveCloseToTargetTileRange(Entity entity) {
            return 1;
        }

        public EmptyCombatMethod max(int max) {
            this.max = max;
            return this;
        }
    }

    private static void fillToxicFumes(NPC npc, Entity target) {
        Tile spawnTile = npc.spawnTile();
        Chain.bound(null).cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(4, -4));
            spitFume(npc, spawnTile.transform(2, -4), target, 3);
            spitFume(npc, spawnTile.transform(5, -4), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            // South-west
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-2, -4));
            spitFume(npc, spawnTile.transform(-1, -4), target, 3);
            spitFume(npc, spawnTile.transform(-4, -3), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            // East
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(6, 2));
            spitFume(npc, spawnTile.transform(6, -1), target, 3);
            spitFume(npc, spawnTile.transform(6, 2), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            // West
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-4, 2));
            spitFume(npc, spawnTile.transform(-4, 3), target, 3);
            spitFume(npc, spawnTile.transform(-4, 0), target, 3);
        });

    }

    private static void snakelingsCloudsSnakelings(NPC npc, Entity target) {
        Tile spawnTile = npc.spawnTile();

        // Fix facing first
        // npc.setPositionToFace(null);
        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-3, 4));
            createSnakeling(npc, spawnTile.transform(-3, 4), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.setPositionToFace(spawnTile.transform(-3, 1));
            createSnakeling(npc, spawnTile.transform(-3, 1), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-4, -3));
            spitFume(npc, spawnTile.transform(-4, -3), target, 3);
            spitFume(npc, spawnTile.transform(-1, -4), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(5, -2));
            spitFume(npc, spawnTile.transform(5, -4), target, 3);
            spitFume(npc, spawnTile.transform(6, -1), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            // Snakelings
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(7, 3));
            createSnakeling(npc, spawnTile.transform(7, 3), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(7, 6));
            createSnakeling(npc, spawnTile.transform(7, 6), 4, target);
        });
    }

    private static void eastSnakelingsRestFumes(NPC npc, Entity target) {
        Tile spawnTile = npc.spawnTile();
        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(3, -4));
            spitFume(npc, spawnTile.transform(2, -4), target, 3);
            spitFume(npc, spawnTile.transform(5, -4), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-2, -3));
            spitFume(npc, spawnTile.transform(-4, -3), target, 3);
            spitFume(npc, spawnTile.transform(-1, -4), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-4, 2));
            spitFume(npc, spawnTile.transform(-4, 0), target, 3);
            spitFume(npc, spawnTile.transform(-4, 3), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(6, -3));
            createSnakeling(npc, spawnTile.transform(6, -3), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(7, 3));
            createSnakeling(npc, spawnTile.transform(7, 3), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(7, 6));
            createSnakeling(npc, spawnTile.transform(7, 6), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(7, 0));
            createSnakeling(npc, spawnTile.transform(7, 0), 4, target);
        });

    }

    private static void snakelingFumeMix(NPC npc, Entity target) {
        Tile spawnTile = npc.spawnTile();
        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-3, -2));
            createSnakeling(npc, spawnTile.transform(-3, -2), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(1, -4));
            spitFume(npc, spawnTile.transform(-1, -4), target, 3);
            spitFume(npc, spawnTile.transform(2, -4), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-3, 4));
            createSnakeling(npc, spawnTile.transform(-3, 4), 4, target);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(5, -4));
            spitFume(npc, spawnTile.transform(5, -4), target, 3);
            spitFume(npc, spawnTile.transform(6, -1), target, 3);
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            npc.animate(5069);
            npc.setPositionToFace(spawnTile.transform(-3, 1));
            createSnakeling(npc, spawnTile.transform(-3, 1), 4, target);
        });
    }

    private static void spitFume(NPC npc, Tile tile, Entity target, int delay) {
        GameObject obj = new GameObject(11700, tile, 10, 0);
        Area area = tile.transform(1, 1).area(1); // Center, 3x3

        var tileDist = npc.tile().distance(obj.tile());
        int duration = (40 + (5 * tileDist));
        Projectile projectile = new Projectile(npc.tile().transform(2, 2, 0), tile.transform(1, 1), 1045, 40, duration, 92, 5, 10, 5, 5);
        projectile.send(projectile.getStart(), projectile.getEnd());
        TaskManager.submit(new TickAndStop(delay) {
            @Override
            public void executeAndStop() {
                ObjectManager.addObj(obj);
                TaskManager.submit(new Task() {
                    int internalCounter = 30;

                    @Override
                    protected void execute() {
                        if (instanceFinished(npc)) {
                            ObjectManager.removeObj(obj);
                            stop();
                            return;
                        }
                        if (internalCounter-- > 0) {
                            if (area.contains(target, true)) {
                                // just standing here causes damage, not only when venom ticker is applied
                                target.hit(npc, 1 + Utils.getRandom(3), HitMark.VENOM);
                                target.venom(npc); // apply venom
                            }
                        } else {
                            ObjectManager.removeObj(obj);
                            stop();
                        }
                    }
                });
            }
        });
    }

    private static void createSnakeling(NPC npc, Tile tile, int delay, Entity target) {
        new Projectile(npc.tile().transform(2, 2, 0), tile, 0, 1047, 12 + (16 * 6), 40, 92, 5, 0).sendProjectile();
        MutableObject<NPC> snakeling = new MutableObject<>();
        snakeling.setValue(new NPC(NpcIdentifiers.SNAKELING, tile.copy()));
        Chain.noCtx().cancelWhen(() -> instanceFinished(npc)).runFn(delay, () -> {
            Player player = (Player) target;
            player.getInstancedArea().addNpc(snakeling.getValue());
            World.getWorld().registerNpc(snakeling.getValue());
            snakeling.getValue().putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
            snakeling.getValue().setIgnoreOccupiedTiles(true);
            snakeling.getValue().respawns(false);
            snakeling.getValue().animate(2413);
            Long uid = player.<Long>getAttribOr(PLAYER_UID, 0L);
            snakeling.getValue().putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(uid, player));
            snakeling.getValue().lockNoDamage();
        }).cancelWhen(() -> instanceFinished(npc)).then(3, () -> {
            snakeling.getValue().unlock();
            snakeling.getValue().getCombat().attack(target);
        }).cancelWhen(() -> instanceFinished(npc)).then(1, () -> {
            snakeling.getValue().runUninterruptable(60, () -> {
                snakeling.getValue().hit(snakeling.getValue(), snakeling.getValue().hp());
            });
        });
    }

    private static final Tile EASTERN_SAFESPOT2 = new Tile(2273, 3072, 0);
    private static final Tile EASTERN_SAFESPOT = new Tile(2272, 3072, 0);
    private static final Tile WESTERN_SAFESPOT = new Tile(2264, 3072, 0);
    private static final Tile WESTERN_SAFESPOT2 = new Tile(2263, 3072, 0);

    private static boolean isMeleeSafespot(NPC npc, Tile tile) {
        Tile spawnTile = npc.spawnTile();
        return tile.equals(EASTERN_SAFESPOT) || tile.equals(EASTERN_SAFESPOT2) || tile.equals(WESTERN_SAFESPOT) || tile.equals(WESTERN_SAFESPOT2);
    }
}

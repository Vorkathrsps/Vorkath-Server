package com.cryptic.model.content.areas.wilderness;

import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.TickAndStop;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import java.util.function.BooleanSupplier;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_26769;

/**
 * Created by Nick on 8/28/2016.
 */
public class WildernessGodWarsDungeon extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == 6621) {
            if (option == 1) {
                int animation = player.getAbsX() == 3055 ? 3065 : 6130;
                int transformX = player.getAbsX() == 3055 ? -3 : 3;
                player.lockMoveDamageOk();
                Direction direction = Direction.resolveForLargeNpc(npc.tile(), npc);
                BooleanSupplier wait = () -> npc.tile().equals(new Tile(3053, 10166, 3));
                BooleanSupplier until = () -> player.tile().nextTo(npc.tile());
                player.waitUntil(1, until, () -> {
                    player.animate(animation);
                    Chain.noCtx().runFn(1, () -> {
                        npc.step(direction.x, direction.y + 1, MovementQueue.StepType.FORCED_WALK);
                        player.setEntityInteraction(null);
                        player.waitUntil(4, wait, () -> {
                            player.agilityWalk(false);
                            player.stepAbs(player.tile().transform(transformX, 0), MovementQueue.StepType.FORCED_WALK);
                        }).then(2, () -> {
                            npc.step(direction.x, direction.y - 1, MovementQueue.StepType.FORCED_WALK);
                            player.agilityWalk(true);
                            player.unlock();
                        });
                    });
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == 40386) {
                player.lockDelayDamage();
                BooleanSupplier wait = () -> player.tile().nextTo(obj.tile());
                player.waitUntil(1, wait, () -> {
                    Chain.noCtx()
                        .runFn(1, () -> player.teleport(new Tile(3186, 10127, 0)))
                        .then(1, player::unlock);
                });
            }
            if (obj.getId() == CREVICE_26769) {
                player.lockDelayDamage();
                Tile tile = player.getAbsX() == 3062 ? new Tile(3066, 10143, 3) : new Tile(3050, 10165, 3);
                BooleanSupplier wait = () -> player.tile().nextTo(obj.tile());
                player.animate(2796);
                player.waitUntil(1, wait, () ->
                    Chain.noCtx()
                        .runFn(1, () -> {
                            player.resetAnimation();
                            player.teleport(tile);
                        })
                        .then(1, player::unlock));
                return true;
            }
            if (obj.getId() == 26767) {
                player.lockDelayDamage();
                Tile tile = player.getAbsX() == 3065 ? new Tile(3017, 3740, 0) : player.getAbsX() == 3050 ? new Tile(3034, 10158, 0) : new Tile(3062, 10130, 0);
                BooleanSupplier wait = () -> player.tile().nextTo(obj.tile());
                player.waitUntil(1, wait, () -> {
                    player.animate(2796);
                    Chain.noCtx()
                        .runFn(1, () -> {
                            player.teleport(tile);
                            player.resetAnimation();
                        })
                        .then(1, player::unlock);
                });
                return true;
            }
            if (obj.getId() == 26768) {
                int transformY = player.getAbsY() == 10147 ? 2 : -2;
                int animation = transformY == -2 ? 3276 : 3277;
                int direction = transformY == 2 ? 0 : 2;
                player.lockDelayDamage();
                BooleanSupplier wait = () -> player.tile().nextTo(obj.tile());
                player.waitUntil(1, wait, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, transformY), 45, 120, animation, direction);
                    player.setForceMovement(forceMovement);
                    Chain.noCtx().runFn(5, () -> {
                        player.resetAnimation();
                        player.unlock();
                    });
                });
                return true;
            }
            if (obj.getId() == TUNNEL_ENTRANCE) {
                if (obj.tile().equals(3016, 3738)) {
                    player.lockDelayDamage();
                    BooleanSupplier wait = () -> player.tile().nextTo(obj.tile());
                    player.waitUntil(1, wait, () -> {
                        player.animate(2796);
                        Chain.noCtx().runFn(1, () -> {
                            player.teleport(new Tile(3065, 10159, 3));
                            player.resetAnimation();
                            player.unlock();
                        });
                    });
                }
                return true;
            }

            if (obj.getId() == TUNNEL_EXIT) {
                if (obj.tile().equals(3065, 10160)) {
                    if (!player.tile().equals(obj.tile().transform(0, -1, 0))) {
                        player.getMovementQueue().walkTo(obj.tile().transform(0, -1, 0));
                    }
                    player.lockDelayDamage();
                    TaskManager.submit(new TickAndStop(1) {
                        @Override
                        public void executeAndStop() {
                            teleportPlayer(player, 3017, 3740, 0);
                            player.unlock();
                        }
                    });
                }
                return true;
            }

            if (obj.getId() == JUTTING_WALL) {
                //TODO
                player.message("This wall is not supported yet, report this to an Administrator.");
                return true;
            }
        }
        return false;
    }

    private static final int TUNNEL_ENTRANCE = 26766;
    private static final int TUNNEL_EXIT = 26767;
    private static final int JUTTING_WALL = 26768;

    private void teleportPlayer(Player player, int x, int y, int z) {
        player.animate(2796);
        TaskManager.submit(new TickAndStop(2) {
            @Override
            public void executeAndStop() {
                player.animate(-1);
                player.teleport(new Tile(x, y, z));
            }
        });
    }

    private void passJuttingWall(Player player, int x, int y) {// Doesn't work from south side TODO
        /*r.onObject(JUTTING_WALL) @Suspendable {
            it.player().lockDelayDamage()
            it.player().animate(753)
            it.player().faceTile(Tile(0, it.player().tile().z))
            it.player().pathQueue().clear()
            it.player().pathQueue().interpolate(3066, 10147, PathQueue.StepType.FORCED_WALK)
            it.player().looks().render(756, 756, 756, 756, 756, 756, -1)
            it.delay(3)
            it.waitForTile(Tile(3066, 10147 ))
            it.player().looks().resetRender()
            it.addXp(Skills.AGILITY, 6.0)
            it.player().unlock()
        }*/
    }
}

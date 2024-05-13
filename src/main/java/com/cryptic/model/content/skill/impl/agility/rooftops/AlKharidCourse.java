package com.cryptic.model.content.skill.impl.agility.rooftops;

import com.cryptic.model.content.skill.impl.agility.MarksOfGrace;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin
 * mei 07, 2020
 */
public class AlKharidCourse extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(
        new Tile(3276, 3188, 3),
        new Tile(3271, 3191, 3),
        new Tile(3273, 3182, 3),
        new Tile(3267, 3171, 3),
        new Tile(3271, 3170, 3),
        new Tile(3268, 3163, 3),
        new Tile(3266, 3166, 3),
        new Tile(3291, 3163, 3),
        new Tile(3297, 3168, 3),
        new Tile(3301, 3164, 3),
        new Tile(3316, 3161, 1),
        new Tile(3318, 3163, 1),
        new Tile(3315, 3176, 2),
        new Tile(3317, 3178, 2),
        new Tile(3315, 3183, 3),
        new Tile(3313, 3181, 3),
        new Tile(3302, 3189, 3),
        new Tile(3300, 3190, 3)
    );

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == ROUGH_WALL_11633) {
            startCourse(player, obj);
            return true;
        }

        Tile tightropeTile = new Tile(3272, 3181, 3);
        if (obj.getId() == TIGHTROPE_14398 && obj.tile().equals(tightropeTile)) {
            player.lockDelayDamage();
            Chain.noCtx().delay(1, () -> {
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.stepAbs(player.tile().transform(0, -10), MovementQueue.StepType.FORCED_WALK);
                BooleanSupplier wait = () -> player.tile().equals(new Tile(3272, 3172, 3));
                player.waitUntil(1, wait, () -> {
                    player.getCombat().reset();
                    player.setPositionToFace(null);
                    player.looks().resetRender();
                    player.stepAbs(player.tile().transform(-1, 0), MovementQueue.StepType.FORCED_WALK);
                }).then(1, () -> {
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 30.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
                });
            });
            return true;
        }

        if (obj.getId() == CABLE) {
            Tile endPos = new Tile(3283, 3166);
            player.tile().faceObjectTile(obj);
            jumpCableTask(player, obj, endPos);
            return true;
        }

        if (obj.getId() == ZIP_LINE_14403) {
            player.tile().faceObjectTile(obj);
            forceMoveZipLine(player);
            return true;
        }

        if (obj.getId() == TROPICAL_TREE_14404) {
            player.tile().faceObjectTile(obj);
            forceMoveTropicalTree(player);
            return true;
        }

        if (obj.getId() == ROOF_TOP_BEAMS) {
            player.lock();
            player.tile().faceObjectTile(obj);
            roofTopBeamTeleport(player);
            return true;
        }

        if (obj.getId() == TIGHTROPE_14409) {
            player.lockDelayDamage();
            Chain.noCtx().delay(1, () -> {
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.stepAbs(player.tile().transform(-12, 0), MovementQueue.StepType.FORCED_WALK);
                BooleanSupplier wait = () -> player.tile().equals(new Tile(3302, 3186, 3));
                player.waitUntil(1, wait, () -> {
                    player.getCombat().reset();
                    player.setPositionToFace(null);
                    player.looks().resetRender();
                    player.stepAbs(player.tile().transform(0, 1), MovementQueue.StepType.FORCED_WALK);
                }).then(1, () -> {
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 30.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
                });
            });
            return true;
        }

        if (obj.getId() == GAP_14399) {
            player.tile().faceObjectTile(obj);
            player.lock();
            finishCourse(player);
            return true;
        }
        return false;
    }

    private void finishCourse(Player player) {
        Chain.bound(player).name("AlKharidJumpDownTask").runFn(1, () -> {
            player.animate(2586);
        }).then(1, () -> {
            player.teleport(3299, 3194, 0);
            player.animate(-1);
            player.getSkills().addXp(Skills.AGILITY, 30.0);
            player.unlock();
            MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);

        });
    }

    private void sendTightRopeTaskTwo(Player player, String AlKharidTightrope3Task, Tile stepTo, Tile tile, double amt) {
        Chain
            .bound(player)
            .name(AlKharidTightrope3Task)
            .runFn(1, () -> {
                player.lockDelayDamage();
                player.agilityWalk(false);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.getMovementQueue().clear();
                player.stepAbs(stepTo.transform(8, 0), MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(tile, () -> {
                player.looks().resetRender();
                player.agilityWalk(true);
                player.getSkills().addXp(Skills.AGILITY, amt);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
                player.unlock();
            });
    }

    private void startCourse(Player player, GameObject obj) {
        if (player.getSkills().xpLevel(Skills.AGILITY) < 20) {
            player.message("You need an Agility level of 20 to attempt this.");
        } else {
            player.tile().faceObjectTile(obj);
            climWall(player);
        }
    }

    private void climWall(Player player) {
        Chain
            .bound(player)
            .name("ClimbWallTask")
            .delay(1, () -> {
                player.lockDamageOk();
                player.animate(828);
            })
            .then(1, () -> {
                player.unlock();
                player.teleport(3273, 3192, 3);
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            });
    }

    private void roofTopBeamTeleport(Player player) {
        Chain.bound(player).name("AlKharidWallClimb3Task").runFn(1, () -> player.animate(828, 15)).then(2, () -> {
            player.teleport(3316, 3180, 3);
            player.animate(-1);
            player.getSkills().addXp(Skills.AGILITY, 5.0);
            player.unlock();
            MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
        });
    }

    private void forceMoveTropicalTree(Player player) {
        Chain.bound(player).name("AlKharidPalmTreeSwingTask").runFn(1, () -> {
            player.lock();
            ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 4), 5, 30, 1124, Direction.NORTH);
            player.setForceMovement(forceMovement);
        }).waitForTile(new Tile(3318, 3169), () -> {
            player.animate(1124);
        }).then(1, () -> {
            ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 1), 5, 30, 1124, Direction.EAST);
            player.setForceMovement(forceMovement);
        }).waitForTile(new Tile(3318, 3170), () -> {
            ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 3), 15, 30, 1124, Direction.NORTH);
            player.setForceMovement(forceMovement);
        }).waitForTile(new Tile(3318, 3173), () -> {
            player.teleport(new Tile(3317, 3174, 2));
            player.unlock();
        });
    }

    private static void forceMoveZipLine(Player player) {
        player.animate(2586);
        BooleanSupplier wait1 = () -> player.tile().equals(new Tile(3304 + 2, 3163, 1));
        BooleanSupplier wait2 = () -> player.tile().equals(new Tile(3308, 3163, 1));
        BooleanSupplier wait3 = () -> player.tile().equals(new Tile(3310, 3163, 1));
        BooleanSupplier wait4 = () -> player.tile().equals(new Tile(3312, 3163, 1));
        BooleanSupplier wait5 = () -> player.tile().equals(new Tile(3314, 3163, 1));
        AtomicReference<ForceMovement> forceMovement = new AtomicReference<>();
        Chain
            .bound(player)
            .name("ZipLineTaskAlKharid")
            .runFn(1, () -> {
                player.lock();
                player.teleport(3304, 3163, 1);
                player.animate(1601);
            }).then(1, () -> {
                forceMovement.set(new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 1602, Direction.EAST));
                player.setForceMovement(forceMovement.get());
                player.waitUntil(1, wait1, () -> {
                    forceMovement.set(new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 1602, Direction.EAST));
                    player.setForceMovement(forceMovement.get());
                    player.waitUntil(1, wait2, () -> {
                        forceMovement.set(new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 1602, Direction.EAST));
                        player.setForceMovement(forceMovement.get());
                        player.waitUntil(1, wait3, () -> {
                            forceMovement.set(new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 1602, Direction.EAST));
                            player.setForceMovement(forceMovement.get());
                            player.waitUntil(1, wait4, () -> {
                                forceMovement.set(new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 1602, Direction.EAST));
                                player.setForceMovement(forceMovement.get());
                                player.waitUntil(1, wait5, () -> {
                                    player.animate(-1);
                                    player.stepAbs(player.tile().transform(1, 0), MovementQueue.StepType.FORCED_WALK);
                                }).then(1, () -> {
                                    player.getSkills().addXp(Skills.AGILITY, 40.0);
                                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
                                    player.unlock();
                                });
                            });
                        });
                    });
                });
            });
    }

    private void jumpCableTask(Player player, GameObject obj, Tile endPos) {
        var toTile = new Tile(3266, 3166, 3);
        player.lockDelayDamage();
        player.getMovementQueue().clear();
        player.stepAbs(toTile.transform(0, 0), MovementQueue.StepType.FORCED_RUN);
        BooleanSupplier wait1 = () -> player.tile().equals(new Tile(3266, 3166, 3));
        BooleanSupplier wait2 = () -> player.tile().equals(obj.tile());
        BooleanSupplier wait3 = () -> player.tile().equals(new Tile(3283, 3166, 3));
        Chain.bound(player).name("AlKharidCableTask").runFn(1, () -> {
            player.message("You begin an almighty run-up...");
            player.waitUntil(2, wait1, () -> {
                player.getMovementQueue().clear();
                player.looks().render(1995, 1995, 1995, 1995, 1995, 1995, -1);
                player.stepAbs(obj.tile().transform(0, 0), MovementQueue.StepType.FORCED_RUN);
                player.waitUntil(1, wait2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(14, 0), 20, 60, 751, Direction.EAST);
                    player.setForceMovement(forceMovement);
                    player.waitUntil(2, wait3, () -> {
                        player.looks().resetRender();
                    }).then(1, () -> {
                        player.stepAbs(player.tile().transform(1, 0), MovementQueue.StepType.FORCED_WALK);
                    }).then(1, () -> {
                        player.getSkills().addXp(Skills.AGILITY, 40.0);
                        MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
                        player.unlock();
                    });
                });
            });
        });
    }

}

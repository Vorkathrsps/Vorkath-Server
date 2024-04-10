package com.cryptic.model.content.skill.impl.agility.rooftops;

import com.cryptic.model.content.skill.impl.agility.MarksOfGrace;
import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.ForceMovementTask;
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

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin
 * juni 14, 2020
 */
public class RellekkaRooftop extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(new Tile(2622, 3676, 3), new Tile(2617, 3664, 3), new Tile(2618, 3660, 3), new Tile(2628, 3652, 3), new Tile(2628, 3655, 3), new Tile(2641, 3649, 3), new Tile(2643, 3651, 3), new Tile(2649, 3659, 3), new Tile(2644, 3662, 3), new Tile(2658, 3674, 3), new Tile(2656, 3681, 3));

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        // Wall climb
        if (obj.getId() == ROUGH_WALL_14946) {
            if (player.getSkills().level(Skills.AGILITY) >= 80) {
                player.lock();
                player.setPositionToFace(player.tile().transform(0, -1));
                Chain.bound(player).name("RellekkaRooftopWallClimbTask").runFn(1, () -> {
                    player.setPositionToFace(player.tile().transform(0, -1));
                    player.animate(828, 15);
                }).then(2, () -> {
                    player.teleport(2626, 3676, 3);
                    player.animate(-1);
                    player.getSkills().addXp(Skills.AGILITY, 20.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 42, 80);
                    player.unlock();
                });
            } else {
                player.message("You need at least 80 Agility to attempt this course.");
            }
            return true;
        }

        // Gap leap
        if (obj.getId() == GAP_14947) {
            player.smartPathTo(new Tile(2622, 3672, 3));
            player.waitForTile(new Tile(2622, 3672, 3), () -> {
                player.lock();
                player.setPositionToFace(player.tile().transform(0, -1));
                Chain.bound(player).name("RellekkaRooftopGapLeapTask").runFn(1, () -> player.animate(1995, 15)).then(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile().clone(), new Tile(-1, -4), 10, 35, 1603, Direction.SOUTH.toInteger());
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 30.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 42, 80);
                    player.unlock();
                });
            });
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14987) {
            Chain.bound(player).name("RellekkaRooftopTightropeTask").runFn(1, () -> {
                player.lock();
                player.agilityWalk(false);
                player.getMovementQueue().clear();
            }).waitForTile(new Tile(2622, 3658), () -> {
                player.stepAbs(new Tile(2622, 3658).transform(0, 0), MovementQueue.StepType.FORCED_WALK);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                Chain.bound(player).name("rellekarooftoptightropetask2").runFn(1, () -> {
                    player.getMovementQueue().step(2627, 3654, MovementQueue.StepType.FORCED_WALK);
                }).waitForTile(new Tile(2626, 3654), () -> {
                    player.agilityWalk(true);
                    player.looks().resetRender();
                    player.getSkills().addXp(Skills.AGILITY, 40.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 42, 80);
                    player.unlock();
                });
            });
            return true;
        }

        // Gap jump + tightrope
        if (obj.getId() == GAP_14990) {
            Tile startPos = obj.tile().transform(0, -1);
            player.smartPathTo(startPos);
            player.waitForTile(startPos,
                () -> {
                    player.lockDamageOk();
                    player.setPositionToFace(new Tile(0, -1));
                    Chain.bound(player)
                        .name("rellekarooftoptasktightrope")
                        .runFn(1, () -> {
                            ForceMovement forceMovement = new ForceMovement(player.tile().clone(), new Tile(0, 3), 5, 30, 1603, Direction.SOUTH.toInteger());
                            player.setForceMovement(forceMovement);
                        })
                        .then(2, () -> {
                            player.looks().render(754, 754, 754, 754, 754, 754, -1);
                            player.stepAbs(new Tile(2629, 3658).transform(3, 0), MovementQueue.StepType.FORCED_WALK);
                        })
                        .waitForTile(new Tile(2629, 3658)
                            .transform(3, 0), () ->
                            Chain.bound(player)
                                .name("rellekarooftoptasktightrope2")
                                .runFn(2,
                                    () ->
                                        player.stepAbs(
                                            new Tile(2629, 3658)
                                                .transform(3, 0)
                                                .transform(2, 0), MovementQueue.StepType.FORCED_WALK))
                                .waitForTile(
                                    new Tile(2629, 3658)
                                        .transform(3, 0)
                                        .transform(2, 0),
                                    () ->
                                        Chain.bound(player)
                                            .name("rellekarooftoptasktightrope3")
                                            .runFn(1,
                                                () -> {
                                                    player.looks().resetRender();
                                                    player.stepAbs(
                                                        new Tile(2629, 3658)
                                                            .transform(3, 0)
                                                            .transform(2, 0)
                                                            .transform(1, 0), MovementQueue.StepType.FORCED_WALK);
                                                })
                                            .waitForTile(
                                                new Tile(2629, 3658)
                                                    .transform(3, 0)
                                                    .transform(2, 0)
                                                    .transform(1, 0),
                                                () ->
                                                    Chain.bound(player)
                                                        .name("rellekarooftoptasktightrope4")
                                                        .runFn(1,
                                                            () -> {
                                                                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                                                                player.stepAbs(new Tile(2640, 3653).transform(0, 0), MovementQueue.StepType.FORCED_WALK);
                                                            })
                                                        .waitForTile(
                                                            new Tile(2640, 3653)
                                                                .transform(0, 0),
                                                            () -> {
                                                                player.looks().resetRender();
                                                                player.unlock();
                                                            }))));
                });
            return true;
        }

        // Gap jump
        if (obj.getId() == GAP_14991) {
            Tile startPos = obj.tile().transform(0, -1);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
                player.lock();
                player.setPositionToFace(new Tile(0, -1));
                Chain.bound(player).name("RellekkaRooftopGapLeapTask").runFn(1, () -> player.animate(1995, 15)).then(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile().clone(), new Tile(1, 4), 10, 35, 1603, Direction.SOUTH.toInteger());
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 30.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 42, 80);
                    player.unlock();
                });
            });
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14992) {
            Tile startPos = obj.tile().transform(0, -1);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
                player.lock();
                player.stepAbs(startPos.transform(0, 1), MovementQueue.StepType.FORCED_WALK);
                Chain.bound(player).name("RellekkaRooftopTightropeTask").runFn(1, () -> {
                    player.agilityWalk(false);
                    player.getMovementQueue().clear();
                }).waitForTile(startPos.transform(0, 1), () -> {
                    player.stepAbs(new Tile(2654, 3670).transform(0, 0), MovementQueue.StepType.FORCED_WALK);
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                    Chain.bound(player).name("rellekarooftoptightropetask2").runFn(1, () -> {
                        player.getMovementQueue().step(2655, 3670, MovementQueue.StepType.FORCED_WALK);
                    }).waitForTile(new Tile(2655, 3670), () -> {
                        player.agilityWalk(true);
                        player.looks().resetRender();
                        player.getSkills().addXp(Skills.AGILITY, 40.0);
                        MarksOfGrace.trySpawn(player, MARK_SPOTS, 42, 80);
                        player.unlock();
                    });
                });
            });
            return true;
        }

        // Jump fish pile
        if (obj.getId() == PILE_OF_FISH) {
            Tile startPos = obj.tile().transform(1, 0);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
            }).then(1, () -> {
                player.lock();
                player.animate(2586, 15);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2653, 3676, 0);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 475.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 42, 80);
                player.getMovementQueue().step(Direction.WEST);

            }).then(1, player::unlock);
            return true;
        }
        return false;
    }

}

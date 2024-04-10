package com.cryptic.model.content.skill.impl.agility.rooftops;

import com.cryptic.model.content.skill.impl.agility.MarksOfGrace;
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
 * juni 13, 2020
 */
public class VarrockRooftop extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(
        new Tile(3214, 3417, 3),
        new Tile(3202, 3417, 3),
        new Tile(3194, 3416, 1),
        new Tile(3194, 3404, 3),
        new Tile(3196, 3394, 3),
        new Tile(3205, 3395, 3),
        new Tile(3226, 3402, 3),
        new Tile(3236, 3407, 3)
    );

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == ROUGH_WALL_14412) {
            if (player.getSkills().xpLevel(Skills.AGILITY) < 30) {
                player.message("You need an Agility level of 30 to attempt this.");
            } else {
                player.setPositionToFace(null);
                player.lock();
                Chain.bound(player).name("VarrockWallclimbTask").runFn(1, () -> player.animate(828, 20)).then(2, () -> {
                    player.teleport(3220, 3414, 3);
                    player.animate(2585, 0);
                }).then(2, () -> {
                    player.teleport(3219, 3414, 3);
                    player.getSkills().addXp(Skills.AGILITY, 12.0);
                    player.unlock();
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
                });
            }
            return true;
        }

        if (obj.getId() == CLOTHES_LINE) {
            player.lockNoDamage();
            Chain.bound(player).name("VarrockClotheslineTask").runFn(1, () -> {
                player.lock();
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 15, 30, 741, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 15, 30, 741, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 15, 30, 741, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 21.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
            });
            return true;
        }

        // Jump down
        if (obj.getId() == GAP_14414) {
            player.setPositionToFace(null);
            player.lock();
            Chain.bound(player).name("VarrockJumpdownTask").runFn(1, () -> {
                player.animate(2586, 15);
            }).then(1, () -> {
                player.teleport(3197, 3416, 1);
                player.animate(2588);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 17.0);
                player.unlock();

                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
            });
            return true;
        }

        if (obj.getId() == WALL_14832) {
            player.setPositionToFace(null);
            Tile startPos = obj.tile().transform(3, 1);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
                    player.getMovementQueue().step(3194, 3416, MovementQueue.StepType.FORCED_WALK);
                }).name("VarrockSwingwall1Task")
                .waitForTile(new Tile(3194, 3416), player::lock).then(1, () -> {
                    player.agilityWalk(true);
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 1995, 4);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3192, 3416, 1), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, -2), 0, 60, 1124, 4);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3190, 3414, 1), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -1), 30, 60, 1124, 4);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3190, 3413, 1), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -1), 30, 60, 1124, 4);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3190, 3412, 1), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -1), 30, 60, 1124, 4);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3190, 3411, 1), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -1), 30, 60, 1124, 4);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3190, 3410, 1), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -1), 30, 60, 1124, 4);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3190, 3409, 1), () -> {
                    player.setPositionToFace(new Tile(1, 0));
                    player.agilityWalk(false);
                    player.looks().render(756, 756, 756, 756, 756, 756, -1);
                    player.stepAbs(new Tile(3190, 3407).transform(0, -2), MovementQueue.StepType.FORCED_WALK);
                    Chain.bound(player).name("innerLedgeTask1").runFn(2, () -> {
                    }).waitForTile(new Tile(3190, 3407).transform(0, -2), () -> {
                        player.setPositionToFace(new Tile(-1, 0));
                        player.animate(741, 0);
                        player.looks().resetRender();
                        player.agilityWalk(true);
                    }).then(1, () -> {
                        player.teleport(3192, 3406, 3);
                        player.getSkills().addXp(Skills.AGILITY, 25.0);
                        player.unlock();
                        MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
                    });
                });
        }

        if (obj.getId() == GAP_14833) {
            player.setPositionToFace(null);
            player.lock();
            Chain.bound(player).name("VarrockJumpgap1Task").runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -3), 15, 40, 2583, 2);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                ForceMovement forceMovement2 = new ForceMovement(player.tile(), new Tile(0, -1), 30, 60, 2585, 2);
                player.setForceMovement(forceMovement2);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 9.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
            });
            return true;
        }

        if (obj.getId() == GAP_14834) {
            player.setPositionToFace(null);
            player.lockDamageOk();
            Chain.noCtx().runFn(1, () -> {
                player.agilityWalk(false);
                player.getMovementQueue().clear();
                player.stepAbs(new Tile(3208, 3397, 3).transform(0, 2), MovementQueue.StepType.FORCED_WALK);
            }).then(1, () -> {
                player.setPositionToFace(player.tile().faceObject(obj));
            }).then(1, () -> {
                Chain.bound(player).waitForTile(new Tile(3208, 3397, 3).transform(0, 2), () -> {
                    player.animate(1995);
                }).then(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(6, 2).transform(0, -2), 5, 25, 4789, 1);
                    player.setForceMovement(forceMovement);
                }).then(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(1, 0), 0, 15, 2584, 1);
                    player.setForceMovement(forceMovement);
                }).waitForTile(new Tile(3215, 3399, 3), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 0), 0, 15, 2586, 1);
                    player.setForceMovement(forceMovement);
                    player.teleport(3216, 3399, 3);
                }).waitForTile(new Tile(3216, 3399, 3), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(1, 0), 10, 25, 2585, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    player.getMovementQueue().clear();
                    player.stepAbs(new Tile(3217, 3399, 3).transform(1, 0), MovementQueue.StepType.FORCED_WALK);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
                }).then(1, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 22.0);
                    player.unlock();
                    player.agilityWalk(true);
                });
            });
            return true;
        }

        if (obj.getId() == GAP_14835) {
            player.setPositionToFace(null);
            Tile startPos = obj.tile().transform(-1, 0);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
            }).then(() -> {
                player.lock();
                player.animate(2586, 15);
            }).then(1, () -> {
                player.teleport(3236, 3403, 3);
                player.animate(2588);
            }).then(1, () -> {
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 4.0);
                player.unlock();

                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
            });
            return true;
        }

        if (obj.getId() == LEDGE_14836) {
            player.setPositionToFace(null);
            Tile startPos = obj.tile().transform(new Tile(0, -1, 0));
            player.smartPathTo(startPos);
            player.waitForTile(startPos, player::lock).name("VarrockJumpgap4Task").then(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 1603, 0);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.teleport(3236, 3410, 3);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 3.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);
            });
            return true;
        }

        if (obj.getId() == EDGE) {
            player.setPositionToFace(null);
            Tile startPos = obj.tile().transform(new Tile(0, -1, 0));
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
            }).then(player::lock).name("VarrockEdgeTask").then(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 1), 0, 30, 741, 0);
                player.setForceMovement(forceMovement);
            }).then(1, () -> player.teleport(3236, 3416, 3)).then(1, () -> {
                player.animate(2586, 15);
            }).then(1, () -> {
                player.teleport(3236, 3417, 0);
                player.animate(2588);
                player.getSkills().addXp(Skills.AGILITY, 125.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 30);

            });
            return true;
        }
        return false;
    }

}

package com.aelous.model.content.skill.impl.agility.rooftops;

import com.aelous.model.content.skill.impl.agility.MarksOfGrace;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.FaceDirection;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import java.util.Arrays;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
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
        // Wall climb
        if (obj.getId() == ROUGH_WALL_11633) {
            if (player.getSkills().xpLevel(Skills.AGILITY) < 20) {
                player.message("You need an Agility level of 20 to attempt this.");
            } else {
                player.lockNoDamage();
                player.setPositionToFace(player.tile().transform(0, -1));
                Chain.bound(player).name("AlKharidWallClimb1Task").runFn(1, () -> player.animate(828, 15));
                Chain.bound(player).name("AlKharidWallClimb2Task").runFn(2, () -> player.teleport(3273, 3192, 3));
                player.animate(-1);
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 10.0);

                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            }
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14398 && obj.tile().equals(3272,3181,3)) {
            //Ensure the action only activates if tile has been reached.
            Chain.bound(player).name("AlKharidTightrope1Task").waitForTile(new Tile(3272, 3182, 3), () -> Chain.bound(player).name("AlKharidTightrope2Task").runFn(1, () -> {
                player.agilityWalk(false);
                player.lockNoDamage();
                player.getMovementQueue().clear();
                player.getMovementQueue().interpolate(3272, 3172, MovementQueue.StepType.FORCED_WALK);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
            }).waitForTile(new Tile(3272, 3172, 3), () -> {
                player.looks().resetRender();
                player.agilityWalk(true);
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 30.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            }));
            return true;
        }

        // Cable swing
        if (obj.getId() == CABLE) {
            Tile startPos = new Tile(3265, 3166, player.tile().getZ());
            player.walkAndWait(startPos, () -> player.getMovementQueue().interpolate(3266, 3166, MovementQueue.StepType.FORCED_WALK))
                .name("AlKharidCableSwingTask").waitForTile(new Tile(3266, 3166), () -> {
                player.lock();
                player.setPositionToFace(player.tile().transform(20, 0));
            }).then(1, () -> {
                player.message("You begin an almighty run-up...");
                player.animate(1995);
                TaskManager.submit(new ForceMovementTask(player, 0, new ForceMovement(player.tile().clone(), new Tile(3, 0), 30, 0, Direction.NORTH.toInteger())));
            }).then(1, () -> {
                player.message("You gained enough momentum to swing to the other side!");
                player.animate(751);
                TaskManager.submit(new ForceMovementTask(player, 0, new ForceMovement(player.tile().clone(), new Tile(15, 0), 60, 0, Direction.NORTH.toInteger())));
            }).then(2, () -> {
                player.getSkills().addXp(Skills.AGILITY, 40.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            });
            return true;
        }

        // Teeth-rope :) zipline
        if (obj.getId() == ZIP_LINE_14403) {
            //player.smartPathTo(obj.tile(), obj.getSize()); // why tf is this 10 moves? height level issue? TODO
            player.smartPathTo(new Tile(obj.tile().x - 1, obj.tile().y));
            player.waitForTile(obj.tile().transform(-1, 0), () -> {
                player.lock();
                player.setPositionToFace(player.tile().transform(1, 0));
            }).then(1, () -> player.animate(2586, 10)).then(1, () -> {
                player.teleport(3303, 3163, 1);
                player.animate(1601);
            }).then(1, () -> {
                for (int i = 0; i < 11; i++) {
                    Chain.bound(player).name("AlKharidTeethrope2Task").runFn(i, () -> {//TODO this is ugly wonder how OSRS does this. This litteraly moves the player 1 tile the whole time (teleporting)
                        player.animate(1602);
                        TaskManager.submit(new ForceMovementTask(player, 0, new ForceMovement(player.tile().clone(), new Tile(1, 0), 30, 0, Direction.NORTH.toInteger())));
                    });
                }
            }).waitForTile(new Tile(3314, 3163), () -> {
                player.animate(-1);
                player.getMovementQueue().step(3315, 3163, MovementQueue.StepType.FORCED_WALK);
                player.getSkills().addXp(Skills.AGILITY, 40.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            });
            return true;
        }

        // Palm tree swing
        if (obj.getId() == TROPICAL_TREE_14404) {
            Tile startPos = new Tile(3318, 3165, 1);
            //player.smartPathTo(startPos, obj.getSize());
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> player.getMovementQueue().interpolate(3318, 3165, MovementQueue.StepType.REGULAR))
                .name("AlKharidPalmtreeswingTask").waitForTile(new Tile(3318, 3165), () -> {
                player.lock();
                player.animate(2583);
                //TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, 4), 35, 49, FaceDirection.NORTH)));
            }).then(1, () -> {
                player.setPositionToFace(new Tile(3320, 3169, 1));
                player.animate(1122);
                //TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, 1), 34, 42, FaceDirection.NORTH)));
            }).then(1, () -> {
                player.animate(1124);
               // TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-1, 4), 34, 52, FaceDirection.SOUTH)));
            }).then(1, () -> {
                player.animate(2588);
            }).then(1, () -> player.teleport(new Tile(3317, 3174, 2))).waitForTile(new Tile(3317, 3174, 2), () -> {//Reward exp when tile has been reached.
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            });
            return true;
        }

        // Wall climb v2
        if (obj.getId() == ROOF_TOP_BEAMS) {
            player.lock();
            player.setPositionToFace(player.tile().transform(0, 1));
            Chain.bound(player).name("AlKharidWallClimb3Task").runFn(1, () -> player.animate(828, 15)).then(2, () -> {
                player.teleport(3316, 3180, 3);
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 5.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            });
            return true;
        }

        // Last tightrope
        if (obj.getId() == TIGHTROPE_14409) {
            Chain.bound(player).name("AlKharidTightrope3Task").runFn(1, () -> {
                player.agilityWalk(false);
                player.lockNoDamage();
                player.getMovementQueue().clear();
                player.getMovementQueue().step(3313, 3186, MovementQueue.StepType.FORCED_WALK);
                player.getMovementQueue().interpolate(3302, 3186, MovementQueue.StepType.FORCED_WALK);
                player.getMovementQueue().step(3302, 3187, MovementQueue.StepType.FORCED_WALK);
            }).then(1, () -> player.looks().render(763, 762, 762, 762, 762, 762, -1)).waitForTile(new Tile(3302, 3187), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 15.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);
            });
            return true;
        }

        // Jump down
        if (obj.getId() == GAP_14399) {
            player.lock();
            Chain.bound(player).name("AlKharidJumpDownTask").runFn(1, () -> {
                player.setPositionToFace(player.tile().transform(-1, 1));
                player.animate(2586);
            }).then(1, () -> {
                player.teleport(3299, 3194, 0);
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 30.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 20);

            });
            return true;
        }
        return false;
    }

}

package com.aelous.model.content.skill.impl.agility.rooftops;

import com.aelous.model.content.skill.impl.agility.MarksOfGrace;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.Direction;
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
 * juni 14, 2020
 */
public class SeersRooftop extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(new Tile(2726, 3492, 3), new Tile(2728, 3495, 3), new Tile(2707, 3493, 2), new Tile(2708, 3489, 2), new Tile(2712, 3481, 2), new Tile(2710, 3478, 2), new Tile(2710, 3472, 3), new Tile(2702, 3474, 3), new Tile(2698, 3462, 2));

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        // Wall climb
        if(obj.getId() == WALL_14927) {
            if (player.getSkills().xpLevel(Skills.AGILITY) < 60) {
                player.message("You need an Agility level of 60 to attempt this.");
            } else {
                player.lock();
                player.setPositionToFace(player.tile().transform(0, +1));
                Chain.bound(player).name("SeersRooftopWallClimbTask").runFn(1, () -> player.animate(737, 15)).then(2, () -> {
                    player.teleport(2729, 3488, 1);
                    player.animate(1118);
                }).then(2, () -> {
                    player.teleport(2729, 3491, 3);
                    player.animate(-1);
                    player.getSkills().addXp(Skills.AGILITY, 45.0);
                    player.unlock();
                    player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE,1);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
                });
            }
            return true;
        }

        // Rooftop jumping
        if(obj.getId() == GAP_14928) {
            player.lock();
            Chain.bound(player).name("SeersRooftopJumpTask").runFn(1, () -> {
                player.animate(2586, 15);
            }).then(1, () -> {
                player.teleport(2719, 3495, 2);
                player.animate(2588);
            }).then(1, () -> {
                player.animate(2586, 15);
            }).then(1, () -> {
                player.teleport(2713, 3494, 2);
                player.animate(2588);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                player.unlock();
                player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE,2);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
            });
            return true;
        }

        // Tightrope
        if(obj.getId() == TIGHTROPE_14932) {
            Chain.bound(player).name("SeersRooftopTightrope").runFn(1, () -> {
                player.lock();
                player.getMovementQueue().clear();
                player.getMovementQueue().interpolate(2710, 3481, MovementQueue.StepType.FORCED_WALK);
            }).then(1, () -> {
                player.agilityWalk(false);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
            }).waitForTile(new Tile(2710, 3481), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                player.unlock();
                player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE,3);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
            });
            return true;
        }

        // Gap jump
        if(obj.getId() == GAP_14929) {
            player.lock();
            Chain.bound(player).name("SeersRooftopGapJump1Task").runFn(1, () -> player.setPositionToFace(player.tile().transform(0 , -1))).then(1, () -> TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, -3), 25, 30, Direction.SOUTH.toInteger())))).then(1, () -> player.animate(2585)).then(1, () -> {
                player.teleport(2710, 3474, 3);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, -2), 17, 26, Direction.SOUTH.toInteger())));
            }).then(1, () -> {
                player.teleport(2710, 3472, 3);
                player.getSkills().addXp(Skills.AGILITY, 35.0);
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
                player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE,4);
            });
            return true;
        }

        // Small gap jump
        if(obj.getId() == GAP_14930) {
            player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> {
                player.lock();
                Chain.bound(player).name("SeersRooftopGapJump2Task").runFn(1, () -> {
                    player.setPositionToFace(player.tile().transform(+1, 0));
                    player.animate(2586, 15);
                }).then(1, () -> {
                    player.animate(2588);
                    player.teleport(2702, 3465, 2);
                }).then(1, () -> {
                    player.animate(-1);
                    player.getSkills().addXp(Skills.AGILITY, 15.0);
                    player.unlock();
                    player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE,5);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
                });
            });
            return true;
        }

        // Final gap jump down
        if(obj.getId() == EDGE_14931) {
            player.lock();
            player.setPositionToFace(obj.tile());
            Chain.bound(player).name("SeersRooftopGapJump3Task").runFn(1, () -> {
                player.animate(2462, 15);
            }).then(1, () -> {
                player.teleport(2704, 3464, 0);
                player.animate(2588);
            }).then(1, () -> {
                player.animate(-1);

                int stage = player.getAttribOr(AttributeKey.SEERS_ROOFTOP_COURSE_STATE, 0);
                if (stage == 5) {
                    player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE, 0);
                    player.getSkills().addXp(Skills.AGILITY, 435.0);

                } else {
                    player.getSkills().addXp(Skills.AGILITY, 15.0);
                }
                player.unlock();
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
            });
            return true;
        }
        return false;
    }

}

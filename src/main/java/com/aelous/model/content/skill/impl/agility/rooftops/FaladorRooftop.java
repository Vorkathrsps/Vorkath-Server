package com.aelous.model.content.skill.impl.agility.rooftops;

import com.aelous.model.content.skill.impl.agility.MarksOfGrace;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
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
public class FaladorRooftop extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(
        new Tile(3038, 3343, 3),
        new Tile(3049, 3348, 3),
        new Tile(3049, 3357, 3),
        new Tile(3045, 3365, 3),
        new Tile(3035, 3362, 3),
        new Tile(3028, 3353, 3),
        new Tile(3017, 3345, 3),
        new Tile(3011, 3339, 3),
        new Tile(3016, 3333, 3)
    );

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        // Wall climb
        if (obj.getId() == ROUGH_WALL_14898) {
            if (player.getSkills().xpLevel(Skills.AGILITY) < 50) {
                player.message("You need an Agility level of 50 to attempt this.");
                return true;
            }
            player.lockDelayDamage();
            player.animate(828, 15);
            Chain.bound(player).name("FaladorRooftopWallclimbTask").runFn(2, () -> {
                player.teleport(3036, 3342, 3);
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 8.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14899) {
            player.lockDelayDamage();
            Chain.bound(player).name("FaladorTightrope1Task").runFn(1, () -> {
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.agilityWalk(false);
                player.stepAbs(3047, 3343, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3047, 3343), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.getSkills().addXp(Skills.AGILITY, 17.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Wall bricks
        if (obj.getId() == HAND_HOLDS_14901) {
            Tile startPos = obj.tile().transform(0, -2);
            player.smartPathTo(startPos);
            //Jump position
            player.waitForTile(startPos, player::lock).name("FaladorJumpposition1Task").then(() -> {
                player.teleport(3050, 3351, 2);
                //1 tick later activates anim
            }).then(1, () -> {
                player.animate(1118);
            }).then(1, () -> {
                //Move to correct coords right side of wall. 3051 3352, 2
                TaskManager.submit(new ForceMovementTask(player, 0, new ForceMovement(player.tile().clone(), new Tile(1, 1), 13, 22, Direction.WEST.toInteger())));
            }).then(1, () -> {
                //Move character 4 times each time we add up 1 to the y coord
                player.setPositionToFace(player.tile().transform(0, 1));
                for (int i = 0; i < 3; i++) {
                    Chain.bound(player).name("FaladorJumpposition2Task").runFn(i * 2, () -> {
                        player.animate(1118);
                        TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, 1), 34, 52, Direction.WEST.toInteger())));
                    });
                }
            });
            Chain.bound(player).name("FaladorJumpposition3Task").waitForTile(new Tile(3051, 3355, 2), () -> {
                player.animate(1120);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-1, 2), 34, 60, Direction.WEST.toInteger())));
                Chain.bound(player).name("FaladorJumpposition4Task").runFn(3, () -> {
                    player.teleport(3050, 3357, 3);

                    player.getSkills().addXp(Skills.AGILITY, 45.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 50);
                    player.unlock();
                });
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == GAP_14903) {
            player.lockDelayDamage();
            player.animate(741);
            TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, 3), 15, 30, Direction.EAST.toInteger())));
            Chain.bound(null).name("FaladorGapjump1Task").runFn(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            /*Tile startPos = obj.tile().transform(0, -1);
            player.smartPathTo(startPos, obj.getSize());
            player.waitForTile(startPos, player::lock)
                .name("FaladorGapjump1Task").then(1, () -> {
                player.animate(741);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, 3), 15, 30, Direction.EAST.toInteger())));
            }).then(1, () -> {
                player.skills().addXp(Skills.AGILITY, 20.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });*/
            return true;
        }

        // Gap jump 2
        if (obj.getId() == GAP_14904) {
            player.lock();
            Chain.bound(player).name("FaladorGapjump2Task").runFn(1, () -> {
                player.animate(741);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-5, 0), 15, 30, Direction.WEST.toInteger())));
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14905) {
            Chain.bound(player).name("FaladorTightrope2Task").runFn(1, () -> {
                player.lock();
                player.getMovementQueue().clear();
                player.agilityWalk(false);
                player.getMovementQueue().interpolate(3033, 3361, MovementQueue.StepType.FORCED_WALK);
                player.getMovementQueue().interpolate(3028, 3356, MovementQueue.StepType.FORCED_WALK);
                player.getMovementQueue().interpolate(3028, 3354, MovementQueue.StepType.FORCED_WALK);
            }).then(1, () -> player.looks().render(763, 762, 762, 762, 762, 762, -1)).waitForTile(new Tile(3028, 3355), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.getSkills().addXp(Skills.AGILITY, 45.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14911) {
            player.lock();
            player.getMovementQueue().interpolate(3026, 3353, MovementQueue.StepType.FORCED_WALK);
            Chain.bound(player).name("FaladorTightrope3Task").runFn(2, () -> {
                player.animate(7134);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-5, 0), 0, 89, Direction.WEST.toInteger())));
            }).then(3, () -> {
                player.getMovementQueue().step(3020, 3353, MovementQueue.StepType.FORCED_WALK);
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 40.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == GAP_14919) {
            player.lock();
            Chain.bound(player).name("FaladorGapjump3Task").runFn(1, () -> {
                player.animate(1603);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, -4), 15, 30, Direction.SOUTH.toInteger())));
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 25.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == LEDGE_14920) {
            Tile startPos = new Tile(3016, 3346, 3);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, player::lock)
                .name("FaladorGapjump4Task").then(1, () -> {
                player.animate(1603);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-2, 0, 3), 15, 30, Direction.WEST.toInteger())));
            }).then(1, () -> {
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == LEDGE_14921) {
            player.lock();
            Chain.bound(player).name("FaladorGapjump5Task").runFn(1, () -> {
                player.animate(1603);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, -2), 15, 30, Direction.SOUTH.toInteger())));
            }).then(1, () -> {
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == LEDGE_14922) {
            Tile startPos = new Tile(3013, 3335, 3);
            player.smartPathTo(startPos);
            player.waitUntil(() -> player.tile().y == 3335, player::lock)
                .name("FaladorGapjump6Task").then(1, () -> {
                player.animate(1603);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, -2), 15, 30, Direction.SOUTH.toInteger())));
            }).then(1, () -> {
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == LEDGE_14924) {
            if (!player.tile().equals(3017, 3333, 3))
                return false; // Stop people doing it over and over from wrong side
            player.lock();
            Chain.bound(player).name("FaladorGapjump7Task").runFn(1, () -> {
                player.animate(1603);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(2, 0), 15, 30, Direction.EAST.toInteger())));
            }).then(1, () -> {
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == EDGE_14925) {
            player.lock();
            Chain.bound(player).name("FaladorGapjump8Task").runFn(1, () -> {
                player.animate(1603);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(3, 0), 15, 30, Direction.EAST.toInteger())));
            }).then(1, () -> player.animate(-1)).then(1, () -> {
                player.animate(2586, 15);
            }).then(1, () -> {
                player.teleport(3029, 3333, 0);
                player.getSkills().addXp(Skills.AGILITY, 180.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 50);

                player.unlock();
            });
            return true;
        }
        return false;
    }

}

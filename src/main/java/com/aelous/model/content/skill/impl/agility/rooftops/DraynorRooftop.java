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
 * juni 13, 2020
 */
public class DraynorRooftop extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(
        new Tile(3099, 3280, 3),
        new Tile(3089, 3274, 3),
        new Tile(3094, 3266, 3),
        new Tile(3088, 3259, 3),
        new Tile(3092, 3255, 3),
        new Tile(3099, 3257, 3),
        new Tile(3098, 3259, 3)
    );

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        // Wall climb
        if(obj.getId() == ROUGH_WALL) {
            if (player.getSkills().level(Skills.AGILITY) >= 10) {
                player.lockNoDamage();
                Chain.bound(player).name("DraynorWallClimbTask").runFn(1, () -> player.animate(828, 15)).then(2, () -> {
                    player.teleport(3102, 3279, 3);
                    player.animate(-1);
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 5.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
                });
            } else {
                player.message("You need at least 10 Agility to attempt this course.");
            }
            return true;
        }

        // Tightrope
        if(obj.getId() == TIGHTROPE) {
            player.lockNoDamage();
            player.getMovementQueue().clear();
            player.getMovementQueue().interpolate(3090, 3277, MovementQueue.StepType.FORCED_WALK);
            Chain.bound(player).name("DraynorTightrope1Task").runFn(1, () -> {
                player.agilityWalk(false);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
            }).waitForTile(new Tile(3090, 3277), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.getMovementQueue().interpolate(3090, 3276);
                Chain.bound(player).name("DraynorTightrope2Task").runFn(1, () -> {
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 8.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
                });
            });
            return true;
        }

        // Second tightrope
        if(obj.getId() == TIGHTROPE_11406) {
            Chain.bound(player).name("DraynorTightrope3Task").runFn(1, () -> {
                player.lockNoDamage();
                player.getMovementQueue().clear();
                player.getMovementQueue().interpolate(3092, 3276, MovementQueue.StepType.FORCED_WALK);
            }).then(1, () -> {
                player.agilityWalk(false);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.getMovementQueue().interpolate(3092, 3266, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3092, 3267), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                Chain.bound(player).name("DraynorTightrope4Task").runFn(1, () -> {
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 7.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
                });
            });
            return true;
        }

        // Narrow wall
        if(obj.getId() == NARROW_WALL) {
            Tile startPos = obj.tile().transform(0, 1);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {})
                .name("DraynorNarrowWall1Task").then(1, () -> {
                player.lockNoDamage();
                player.animate(753);
                player.agilityWalk(false);
                player.looks().render(757, 757, 756, 756, 756, 756, -1);
                player.getMovementQueue().clear();
                player.getMovementQueue().interpolate(3089, 3262, MovementQueue.StepType.FORCED_WALK);
                player.getMovementQueue().interpolate(3088, 3261, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3088, 3261), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.animate(759);
                Chain.bound(player).name("DraynorNarrowWall2Task").runFn(1, () -> {
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 7.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
                });
            });
            return true;
        }

        // Wall jump
        if(obj.getId() == WALL_11630) {
            Tile startPos = obj.tile().transform(0, 2);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {}).then(1, () -> {
                player.lockNoDamage();
                player.getMovementQueue().step(3088, 3256);
                player.animate(2583, 20);
                TaskManager.submit(new ForceMovementTask(player, 0, new ForceMovement(player.tile().clone(), new Tile(0, -1), 25, 30, Direction.NORTH.toInteger(), 2583)));
            }).then(1, () -> player.animate(2585)).then(1, () -> {
                player.getMovementQueue().step(3088, 3255);
                TaskManager.submit(new ForceMovementTask(player, 0, new ForceMovement(player.tile().clone(), new Tile(0, -1), 17, 26, Direction.NORTH.toInteger(), -1)));
            }).then(1, () -> {
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
            });
            return true;
        }

        // Gap jump
        if(obj.getId() == GAP_11631) {
            Tile startPos = obj.tile().transform(-1, 0);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {})
                .name("DraynorGapJumpTask").then(1, () -> {
                player.lockNoDamage();
                player.setPositionToFace(new Tile(3096, 3256));
            }).then(1, () -> player.animate(2586, 15)).then(1, () -> {
                player.teleport(new Tile(3096, 3256, 3));
                player.animate(2588);
            }).then(1, () -> {
                player.animate(-1);
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 4.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
            });
            return true;
        }

        // Crate jump
        if(obj.getId() == CRATE_11632) {
            Tile startPos = obj.tile().transform(-1, 0);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {})
                .name("DraynorCrateJumpTask").then(1, () -> {
                player.lockNoDamage();
                player.animate(2586, 15);
            }).then(1, () -> {
                player.teleport(new Tile(3102, 3261, 1));
                player.animate(2588);
            }).then(1, () -> player.animate(-1)).then(1, () -> player.animate(2586, 15)).then(1, () -> {
                player.teleport(new Tile(3103, 3261, 0));
                player.animate(2588);
            }).then(1, () -> {
                player.animate(-1);
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 79.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);

            });
            return true;
        }
        return false;
    }
}

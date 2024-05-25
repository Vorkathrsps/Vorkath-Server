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
        if (obj.getId() == ROUGH_WALL) {
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
        if (obj.getId() == TIGHTROPE) {
            player.waitForTile(new Tile(3099, 3277, 3), () -> {
                player.getMovementQueue().step(3098, 3277, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3098, 3277, 3), () -> {
                player.lockNoDamage();
                player.agilityWalk(true);
                player.getMovementQueue().clear();
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.getMovementQueue().step(3090, 3277, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3090, 3277, 3), () -> {
                player.agilityWalk(false);
                player.looks().resetRender();
            }).then(1, () -> {
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 8.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
            });
            return true;
        }

        // Second tightrope
        if (obj.getId() == TIGHTROPE_11406) {
            player.waitForTile(new Tile(3091, 3276, 3), () -> {
                player.lockNoDamage();
                player.agilityWalk(true);
                player.getMovementQueue().clear();
                player.getMovementQueue().step(3092, 3276, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3092, 3276, 3), () -> {
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.getMovementQueue().step(3092, 3267, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3092, 3267, 3), () -> {
                player.agilityWalk(false);
                player.looks().resetRender();
            }).then(1, () -> {
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 7.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
            });
            return true;
        }

        if (obj.getId() == NARROW_WALL) {
            Tile startPos = obj.tile().transform(0, 1);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
                player.lockNoDamage();
                player.agilityWalk(true);
                player.getMovementQueue().clear();
            }).waitForTile(new Tile(3089, 3265, 3), () -> {
                player.looks().render(757, 757, 756, 756, 756, 756, -1);
                player.getMovementQueue().step(3089, 3261, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(3089, 3261, 3), () -> {
                player.looks().resetRender();
                player.animate(759);
                player.getMovementQueue().step(3088, 3261, MovementQueue.StepType.FORCED_WALK);
            }).then(1, () -> {
                player.unlock();
                player.agilityWalk(false);
                player.getSkills().addXp(Skills.AGILITY, 7.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
            });
            return true;
        }

        if (obj.getId() == WALL_11630) {
            Tile startPos = obj.tile().transform(0, 1);
            player.waitForTile(startPos, () -> {
            }).then(1, () -> {
                player.lockNoDamage();
                player.getMovementQueue().clear();
            }).waitForTile(new Tile(3088, 3257, 3), () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -1), 15, 30, 2583, 2);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.getMovementQueue().clear();
                player.getMovementQueue().step(3088, 3255);
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -1), 30, 60, 2585, 2);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 40, 10);
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == GAP_11631) {
            Tile startPos = obj.tile().transform(-1, 0);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
                player.lockNoDamage();
                player.getMovementQueue().clear();
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
        if (obj.getId() == CRATE_11632) {
            Tile startPos = obj.tile().transform(-1, 0);
            player.smartPathTo(startPos);
            player.waitForTile(startPos, () -> {
                })
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

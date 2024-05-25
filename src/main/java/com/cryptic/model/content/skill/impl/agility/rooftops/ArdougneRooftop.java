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
 * juni 14, 2020
 */
public class ArdougneRooftop extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(new Tile(2671, 3304, 3), new Tile(2663, 3318, 3), new Tile(2654, 3318, 3), new Tile(2653, 3313, 3), new Tile(2653, 3302, 3));

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {

        if (obj.getId() == WOODEN_BEAMS) {
            if (player.getSkills().level(Skills.AGILITY) >= 90) {
                Chain.noCtx().runFn(1, () -> {
                    player.lockDamageOk();
                    player.animate(737, 15);
                }).then(1, () -> {
                    player.teleport(2673, 3298, 1);
                    player.animate(737);
                }).then(1, () -> {
                    player.teleport(2673, 3298, 2);
                    player.animate(737);
                }).then(1, () -> {
                    player.teleport(2671, 3299, 3);
                    player.animate(2588);
                }).then(1, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 43.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 90);
                    player.unlock();
                });
            } else {
                player.message("You need at least 90 Agility to attempt this course.");
            }
            return true;
        }

        if (obj.getId() == GAP_15609) {
            Chain.noCtx().runFn(1, () -> {
                player.lockDamageOk();
                player.animate(2586, 15);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2667, 3311, 1);
            }).then(1, () -> {
                player.animate(2586, 15);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2665, 3315, 1);
            }).then(1, () -> {
                player.animate(2583, 15);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2665, 3318, 3);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 65.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 90);
                player.unlock();
            });
            return true;
        }

        if (obj.getId() == PLANK_26635) {

            Chain.noCtx().runFn(1, () -> {
                player.lock();
                player.getMovementQueue().clear();
                player.agilityWalk(false);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.stepAbs(2656, 3318, MovementQueue.StepType.FORCED_WALK);
            });

            player.waitForTile(new Tile(2656, 3318), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.getSkills().addXp(Skills.AGILITY, 50.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 90);
                player.unlock();
            });
            return true;
        }

        if (obj.getId() == GAP_15610) {
            Chain.noCtx().runFn(1, () -> {
                player.lockDamageOk();
                player.animate(2586, 15);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2653, 3314, 3);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 21.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 90);
                player.unlock();
            });
            return true;
        }

        if (obj.getId() == GAP_15611) {
            Chain.noCtx().runFn(1, () -> {
                player.lock();
                player.animate(7133, 15);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2651, 3309, 3);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 28.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 90);
                player.unlock();
            });
            return true;
        }

        if (obj.getId() == STEEP_ROOF) {
            if (!player.tile().equals(2653, 3300, 3))
                return false;
            Chain.noCtx().runFn(1, () -> {
                player.lockNoDamage();
                player.animate(753);
                player.looks().render(757, 757, 756, 756, 756, 756, -1);
            }).then(1, () -> {
                player.agilityWalk(false);
                player.getMovementQueue().clear();
                player.stepAbs(2656, 3297, MovementQueue.StepType.FORCED_WALK);
            });
            player.waitForTile(new Tile(2656, 3297), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.animate(759);
                player.unlock();
                player.getSkills().addXp(Skills.AGILITY, 57.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 90);
            });
            return true;
        }

        if (obj.getId() == GAP_15612) {
            Chain.bound(player).name("ardougne_gap_walk").runFn(1, () -> {
                player.lockNoDamage();
                player.animate(2586, 15);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2658, 3298, 1);
            }).then(2, () -> {
                player.agilityWalk(false);
                player.stepAbs(2661, 3298, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(2661, 3298), () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, -1), 15, 30, 741, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.teleport(2663, 3297, 1);
            }).then(1, () -> {
                player.resetWalkSteps();
                player.stepAbs(2666, 3297, MovementQueue.StepType.FORCED_WALK);
            }).waitForTile(new Tile(2666, 3297), () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(1, 0), 15, 30, 741, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.teleport(2667, 3297, 1);
                player.animate(2586);
            }).then(1, () -> {
                player.animate(2588);
                player.teleport(2668, 3297, 0);
                player.getSkills().addXp(Skills.AGILITY, 529.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 90);
                player.unlock();
            });
            return true;
        }
        return false;
    }

}

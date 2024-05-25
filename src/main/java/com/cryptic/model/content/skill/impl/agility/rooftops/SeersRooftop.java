package com.cryptic.model.content.skill.impl.agility.rooftops;

import com.cryptic.model.content.skill.impl.agility.MarksOfGrace;
import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.ForceMovementTask;
import com.cryptic.model.entity.attributes.AttributeKey;
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
 * juni 14, 2020
 */
public class SeersRooftop extends PacketInteraction {

    private static final List<Tile> MARK_SPOTS = Arrays.asList(new Tile(2726, 3492, 3), new Tile(2728, 3495, 3), new Tile(2707, 3493, 2), new Tile(2708, 3489, 2), new Tile(2712, 3481, 2), new Tile(2710, 3478, 2), new Tile(2710, 3472, 3), new Tile(2702, 3474, 3), new Tile(2698, 3462, 2));

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == WALL_14927) {
            if (player.getSkills().xpLevel(Skills.AGILITY) < 60) {
                player.message("You need an Agility level of 60 to attempt this.");
            } else {
                player.lockDelayDamage();
                player.tile().faceObjectTile(obj);
                player.waitForTile(new Tile(2729, 3489, 0), () -> {
                    player.animate(737, 0);
                    Chain.noCtx().delay(1, () -> {
                        player.animate(1118);
                        player.teleport(2729, 3488, 1);
                    }).then(1, () -> {
                        player.teleport(2729, 3491, 3);
                        player.animate(-1);
                        player.getSkills().addXp(Skills.AGILITY, 45.0);
                        player.unlock();
                        player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE, 1);
                        MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
                    });
                });
            }
            return true;
        }

        // Rooftop jumping
        if (obj.getId() == GAP_14928) {
            player.lockDelayDamage();
            player.animate(2586, 0);
            Chain.bound(player).name("SeersRooftopJumpTask").runFn(1, () -> {
                player.animate(2588);
                player.teleport(2719, 3495, 2);
            }).then(1, () -> {
                player.animate(2586, 0);
            }).then(1, () -> {
                player.teleport(2713, 3494, 2);
                player.animate(2588);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                player.unlock();
                player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE, 2);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
            });
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14932) {
            Chain.bound(player).name("SeersRooftopTightrope").runFn(1, () -> {
                player.lockDelayDamage();
                player.getMovementQueue().clear();
                player.stepAbs(player.tile().transform(0, -10), MovementQueue.StepType.FORCED_WALK);
            }).then(1, () -> {
                player.agilityWalk(false);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
            }).waitForTile(new Tile(2710, 3481), () -> {
                player.agilityWalk(true);
                player.looks().resetRender();
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                player.unlock();
                player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE, 3);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == GAP_14929) {
            player.lockDelayDamage();
            player.setPositionToFace(player.tile().transform(0, 1));
            player.stepAbs(new Tile(2710, 3477, 2).transform(0, 0), MovementQueue.StepType.FORCED_WALK);
            BooleanSupplier wait1 = () -> player.tile().equals(new Tile(2710, 3474, 2));
            BooleanSupplier waitfor = () -> player.tile().equals(new Tile(2710, 3477, 2));
            Chain.noCtx().delay(1, () -> {
                player.waitUntil(1, waitfor, () -> {
                    player.setPositionToFace(new Tile(1, 1));
                    AtomicReference<ForceMovement> forceMovement = new AtomicReference<>();
                    forceMovement.set(new ForceMovement(player.tile(), new Tile(0, -3), 0, 15, 2583, Direction.SOUTH));
                    player.setForceMovement(forceMovement.get());
                    player.waitUntil(1, wait1, () -> {
                        player.teleport(2710, 3474, 3);
                        forceMovement.set(new ForceMovement(player.tile(), new Tile(0, -2), 40, 60, 2585, Direction.SOUTH));
                        player.setForceMovement(forceMovement.get());
                    }).then(2, () -> {
                        player.unlock();
                        player.getSkills().addXp(Skills.AGILITY, 35.0);
                        MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
                        player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE, 4);
                    });
                });
            });
            return true;
        }

        // Small gap jump
        if (obj.getId() == GAP_14930) {
            player.lockDelayDamage();
            player.setPositionToFace(new Tile(0, 1));
            player.animate(2586, 0);
            Chain.bound(player).name("SeersRooftopGapJump2Task").runFn(1, () -> {
                player.animate(2588);
                player.teleport(2702, 3465, 2);
            }).then(1, () -> {
                player.animate(-1);
                player.getSkills().addXp(Skills.AGILITY, 15.0);
                player.unlock();
                player.putAttrib(AttributeKey.SEERS_ROOFTOP_COURSE_STATE, 5);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 60, 20);
            });
            return true;
        }

        // Final gap jump down
        if (obj.getId() == EDGE_14931) {
            player.lockDelayDamage();
            player.tile().faceObject(obj);
            Chain.bound(player).name("SeersRooftopGapJump3Task").runFn(1, () -> {
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

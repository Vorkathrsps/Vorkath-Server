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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin
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
            Chain.noCtx().runFn(1, () -> {
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
            Chain.noCtx().runFn(1, () -> {
                player.getMovementQueue().clear();
                player.stepAbs(3040, 3343, MovementQueue.StepType.FORCED_WALK);
            }).then(2, () -> {
                Tile position = getFaceTile(obj);
                player.getCombat().reset();
                player.setPositionToFace(position);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.stepAbs(3047, 3343, MovementQueue.StepType.FORCED_WALK);
            });
            player.waitForTile(new Tile(3047, 3343), () -> {
                player.looks().resetRender();
                player.getSkills().addXp(Skills.AGILITY, 17.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Wall bricks
        if (obj.getId() == HAND_HOLDS_14901) {
            player.smartPathTo(new Tile(3050, 3349, 3));
            Chain.bound(player).runFn(1, () -> {
                player.teleport(3050, 3351, 2);
                player.lock();
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(1, 0), 15, 30, 1118, 0);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 1), 5, 30, 1118, 3);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 1), 5, 30, 1118, 4);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 1), 5, 30, 1118, 4);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 1), 5, 30, 1118, 4);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-1, 1), 5, 30, 1118, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.teleport(3049, 3358, 3);
                ForceMovement forceMovement = new ForceMovement(player.tile(), null, 5, 30, -1, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == GAP_14903) {
            player.lockDelayDamage();
            Chain.noCtx().runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 3), 15, 30, 741, 4);
                player.setForceMovement(forceMovement);
            }).then(1, () -> {
                player.getSkills().addXp(Skills.AGILITY, 20.0);
                player.animate(-1);
                player.unlock();
            });
            return true;
        }

        // Gap jump 2
        if (obj.getId() == GAP_14904) {
            player.lock();
            player.smartPathTo(new Tile(3046, 3362, 3));
            player.waitForTile(new Tile(3046, 3362, 3), () -> {
                Tile position = getFaceTile(obj);
                player.getCombat().reset();
                player.setPositionToFace(position);
                Chain.noCtx().runFn(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-5, 0), 15, 30, 741, 4);
                    player.setForceMovement(forceMovement);
                }).then(1, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 20.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                    player.animate(-1);
                    player.unlock();
                });
            });
            return true;
        }

        // Tightrope
        if (obj.getId() == TIGHTROPE_14905) {
            player.lock();
            Chain.noCtx().runFn(1, () -> {
                player.getMovementQueue().clear();
                player.stepAbs(3034, 3362, MovementQueue.StepType.FORCED_WALK);
            }).then(2, () -> {
                Tile position = getFaceTile(obj);
                player.getCombat().reset();
                player.setPositionToFace(position);
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.stepAbs(3027, 3355, MovementQueue.StepType.FORCED_WALK);
            });
            player.waitForTile(new Tile(3027, 3355), () -> {
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
            Chain.noCtx().runFn(1, () -> {
                player.getMovementQueue().clear();
                player.stepAbs(3026, 3353, MovementQueue.StepType.FORCED_WALK);
            }).then(2, () -> {
                player.looks().render(763, 762, 762, 762, 762, 762, -1);
                player.stepAbs(3020, 3353, MovementQueue.StepType.FORCED_WALK);
            });
            player.waitForTile(new Tile(3020, 3353), () -> {
                player.looks().resetRender();
                player.getSkills().addXp(Skills.AGILITY, 40.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        // Gap jump
        if (obj.getId() == GAP_14919) {
            player.lock();
            player.stepAbs(new Tile(3017, 3353).transform(0, 0), MovementQueue.StepType.FORCED_WALK);
            player.waitForTile(new Tile(3017, 3353).transform(0, 0), () -> {
                Chain.noCtx().runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -4), 15, 30, 1603, 2);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 25.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                    player.unlock();
                });
            });
            return true;
        }

        if (obj.getId() == LEDGE_14920) {
            player.smartPathTo(new Tile(3016, 3346, 3));
            player.lock();
            player.waitForTile(new Tile(3016, 3346, 3), () -> {
                Chain.noCtx().runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), null, 0, 15, 1603, 3);
                    player.setForceMovement(forceMovement);
                }).then(1, () -> {
                    player.teleport(3014, 3346, 3);
                }).then(2, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 10.0);
                    MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                }).then(1, player::unlock);
            });
            return true;
        }

        if (obj.getId() == LEDGE_14921) {
            player.lock();
            Chain.noCtx().runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 15, 30, 1603, 2);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
            }).then(1, player::unlock);
            return true;
        }

        // Gap jump
        if (obj.getId() == LEDGE_14922) {
            Tile startPos = new Tile(3013, 3335, 3);
            player.smartPathTo(startPos);
            Chain.noCtx().runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 15, 30, 1603, 2);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
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

            Chain.noCtx().runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 15, 30, 1603, 1);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                player.getSkills().addXp(Skills.AGILITY, 10.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 50, 50);
                player.unlock();
            });
            return true;
        }

        if (obj.getId() == EDGE_14925) {
            player.lock();
            Chain.noCtx().runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(3, 0), 15, 30, 1603, 1);
                player.setForceMovement(forceMovement);
            }).then(2, () -> player.animate(2586, 15)).then(1, () -> {
                player.teleport(3029, 3333, 0);
                player.getSkills().addXp(Skills.AGILITY, 180.0);
                MarksOfGrace.trySpawn(player, MARK_SPOTS, 35, 50);
                player.unlock();
            });
            return true;
        }
        return false;
    }

    @NotNull
    private static Tile getFaceTile(GameObject obj) {
        int sizeX = obj.definition().sizeX;
        int sizeY = obj.definition().sizeY;
        boolean inversed = (obj.getRotation() & 0x1) != 0;
        int faceCoordX = obj.x * 2 + (inversed ? sizeY : sizeX);
        int faceCoordY = obj.y * 2 + (inversed ? sizeX : sizeY);
        Tile position = new Tile(faceCoordX, faceCoordY);
        return position;
    }

}

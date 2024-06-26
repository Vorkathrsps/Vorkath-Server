package com.cryptic.model.content.skill.impl.agility.course;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * Created by Situations on 10/28/2015
 */
public class GnomeStronghold extends PacketInteraction {
    
    private void putStage(Player player, int stageBit) {
        int stage = player.getAttribOr(AttributeKey.GNOME_COURSE_STATE, 0);
        stage = stage | stageBit;
        player.putAttrib(AttributeKey.GNOME_COURSE_STATE, stage);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            //Log Balance
            if (obj.getId() == LOG_BALANCE_23145) {
                Chain.bound(player).name("GnomeStrongholdLogBalanceTask").waitForTile(new Tile(2474, 3436, 0), () -> Chain.bound(player).name("AlKharidTightrope2Task").runFn(1, () -> {
                    player.agilityWalk(false);
                    player.lockNoDamage();
                    player.message("You walk carefully across the slippery log...");
                    player.getMovementQueue().clear();
                    player.getMovementQueue().step(2474, 3429, MovementQueue.StepType.FORCED_WALK);
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                }).waitForTile(new Tile(2474, 3429, 0), () -> {
                    player.looks().resetRender();
                    player.agilityWalk(true);
                    player.unlock();
                    player.message("...You make it safely to the other side.");
                    putStage(player, 1);
                    player.getSkills().addXp(Skills.AGILITY, 7.5);
                }));
                return true;
            }

            // Obstacle Net
            if (obj.getId() == OBSTACLE_NET_23134) {
                player.lockNoDamage();
                player.message("You climb the netting...");
                Chain.bound(player).name("GnomeStrongholdObstacleNetTask").runFn(1, () -> player.animate(828)).then(1, () -> {
                    player.teleport(player.tile().x, 3424, 1);
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 7.5);
                });
                return true;
            }

            // Tree Branch
            if (obj.getId() == TREE_BRANCH_23559) {
                player.lockNoDamage();
                player.message("You climb the tree...");
                Chain.bound(player).name("GnomeStrongholdTreeBranchTask").runFn(1, () -> player.animate(828)).then(1, () -> {
                    player.message("...To the platform above.");
                    player.teleport(2473, 3420, 2);
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 5.0);
                });
                return true;
            }

            //Balancing Rope
            if (obj.getId() == BALANCING_ROPE_23557) {
                Chain.bound(player).name("GnomeStrongholdBalancingRopeTask").waitForTile(new Tile(2477, 3420, 2), () -> Chain.bound(player).name("GnomeStrongholdBalancingRope2Task").runFn(1, () -> {
                    player.agilityWalk(false);
                    player.lockNoDamage();
                    player.message("You carefully cross the tightrope.");
                    player.getMovementQueue().clear();
                    player.getMovementQueue().step(2483, 3420, MovementQueue.StepType.FORCED_WALK);
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                }).waitForTile(new Tile(2483, 3420, 0), () -> {
                    player.looks().resetRender();
                    player.agilityWalk(true);
                    player.unlock();
                    player.getSkills().addXp(Skills.AGILITY, 7.5);
                }));
                return true;
            }

            // Tree Branch
            if (obj.getId() == TREE_BRANCH_23560) {
                player.lockNoDamage();
                player.message("You climb the tree...");
                Chain.bound(player).name("GnomeStrongholdTreeBranchTask2").runFn(1, () -> player.animate(828)).then(1, () -> {
                    player.teleport(2485, 3419, 0);
                    player.message("You land on the ground.");
                    player.unlock();
                    putStage(player, 2);
                    player.getSkills().addXp(Skills.AGILITY, 5.0);
                });
                return true;
            }

            // Final netting
            if (obj.getId() == OBSTACLE_NET_23135) {
                if (player.tile().y != 3425) {
                    player.message("You can not do that from here.");
                    return false;
                }

                player.lockNoDamage();
                player.message("You climb the netting...");
                Chain.bound(player).name("GnomeStrongholdFinalNettingTask").runFn(1, () -> player.animate(828)).then(2, () -> {
                    player.animate(-1);
                    player.teleport(obj.tile().x, obj.tile().y + 2);
                    player.unlock();
                    putStage(player, 4);
                    player.getSkills().addXp(Skills.AGILITY, 7.5);
                });
                return true;
            }

            // Exit pipes
            if (obj.getId() == 23138 || obj.getId() == 23139) {

                //Ensure our player is entering the obstacle from the correct side
                if (obj.tile().equals(new Tile(2484, 3435)) || obj.tile().equals(new Tile(2487, 3435))) {
                    return true;
                }

                if(!player.tile().equals(new Tile(2484,3430)) && !player.tile().equals(new Tile(2487,3430))) {
                    player.message("You can't enter the pipe from here.");
                    return true;
                }

                player.lockNoDamage();
                Chain.bound(player).name("GnomeStrongholdExitPipesTask").waitForTile(obj.tile().transform(0, -1, 0), () -> Chain.bound(player).name("GnomeStrongholdExitPipes2Task").runFn(1, () -> {
                    if (!player.tile().equals(obj.tile().transform(0, -1, 0))) {
                        player.getMovementQueue().walkTo(obj.tile().transform(0, -1, 0));
                    }
                }).waitForTile(obj.tile().transform(0, -1, 0), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 3), 30, 60, 749, 0);
                    player.setForceMovement(forceMovement);
                })).then(3, () -> player.getMovementQueue().interpolate(player.tile().x, player.tile().y + 1)).then(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 4), 30, 60, 749, 0);
                    player.setForceMovement(forceMovement);
                }).then(3, () -> {
                    player.unlock();
                    putStage(player, 8);

                    int stage = player.getAttribOr(AttributeKey.GNOME_COURSE_STATE, 0);
                    if (stage == 15) {
                        player.message("You successfully completed the course.");
                        player.putAttrib(AttributeKey.GNOME_COURSE_STATE, 0);
                        player.getSkills().addXp(Skills.AGILITY, 39.0);


                    } else {
                        player.getSkills().addXp(Skills.AGILITY, 7.5);
                    }
                });
                return true;
            }
        }
        return false;
    }

}

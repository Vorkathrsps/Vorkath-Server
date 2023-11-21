package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin | Zerikoth | PVE
 * @date maart 17, 2020 16:19
 */
public class RevenantsCave extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            switch (obj.getId()) {
                case CAVERN_31555 -> {
                    player.lockDelayDamage();
                    Chain.bound(player)
                        .runFn(1, () -> {
                            player.teleport(3197, 10056);
                            player.unlock();
                            player.message("You enter the cave.");
                        });
                    return true;
                }
                case 43868 -> {
                    player.lockDelayDamage();
                    Chain.bound(player)
                        .runFn(1, () -> {
                            player.teleport(3124, 3806, 0);
                            player.unlock();
                            player.message("You leave the cave.");
                        });
                    return true;
                }
                case OPENING_31558 -> {
                    player.lockDelayDamage();
                    Chain.bound(player)
                        .runFn(1, () -> {
                            player.teleport(3102, 3656, 0);
                            player.unlock();
                            player.message("You exit the cave.");
                        });
                    return true;
                }
                case CAVERN_31556 -> {
                    player.lockDelayDamage();
                    Chain.bound(player)
                        .runFn(1, () -> {
                            player.teleport(3241, 10233);
                            player.unlock();
                            player.message("You enter the cave.");
                        });
                    return true;
                }
                case PILLAR_31561 -> {
                    player.smartPathTo(obj.tile());
                    if (obj.tile().equals(3241, 10145)) {
                        if (player.getSkills().level(Skills.AGILITY) < 89) {
                            player.message("You need an agility level of at least 89 to jump this pillar.");
                            return true;
                        }
                        pillarJump(player);
                    }
                    if (obj.tile().equals(3202, 10196) || obj.tile().equals(3180, 10209) || obj.tile().equals(3200, 10136)) {
                        if (player.getSkills().level(Skills.AGILITY) < 75) {
                            player.message("You need an agility level of at least 75 to jump this pillar.");
                            return true;
                        }
                        pillarJump(player);
                    }
                    if (obj.tile().equals(3220, 10086)) {
                        if (player.getSkills().level(Skills.AGILITY) < 65) {
                            player.message("You need an agility level of at least 65 to jump this pillar.");
                            return true;
                        }
                        pillarJump(player);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void pillarJump(Player player) {
        if (player.tile().x == 3220 && player.tile().y == 10084) {
            player.getMovementQueue().step(3220, 10084, MovementQueue.StepType.FORCED_WALK);
            player.lockDelayDamage();
            Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                player.setForceMovement(forceMovement);
            }).then(2, player::unlock);
        } else {
            if (player.tile().x == 3220 && player.tile().y == 10088) {
                player.getMovementQueue().step(3220, 10088, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            }
        }
        if (player.tile().x == 3202 && player.tile().y == 10136) {
            player.getMovementQueue().step(3202, 10136, MovementQueue.StepType.FORCED_WALK);
            player.lockDelayDamage();
            Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                player.setForceMovement(forceMovement);
            }).then(2, player::unlock);
        } else {
            if (player.tile().x == 3198 && player.tile().y == 10136) {
                player.getMovementQueue().step(3198, 10136, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            }
        }
        if (player.tile().x == 3243 && player.tile().y == 10145) {
            player.getMovementQueue().step(3243, 10145, MovementQueue.StepType.FORCED_WALK);
            player.lockDelayDamage();
            Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                player.setForceMovement(forceMovement);
            }).then(2, player::unlock);
        } else {
            if (player.tile().x == 3239 && player.tile().y == 10145) {
                player.getMovementQueue().step(3239, 10145, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            }
        }
        if (player.tile().x == 3180 && player.tile().y == 10207) {
            player.getMovementQueue().step(3180, 10207, MovementQueue.StepType.FORCED_WALK);
            player.lockDelayDamage();
            Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                player.setForceMovement(forceMovement);
            }).then(2, player::unlock);
        } else {
            if (player.tile().x == 3180 && player.tile().y == 10211) {
                player.getMovementQueue().step(3180, 10211, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            }
        }
        if (player.tile().x == 3204 && player.tile().y == 10196) {
            player.getMovementQueue().step(3204, 10196, MovementQueue.StepType.FORCED_WALK);
            player.lockDelayDamage();
            Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                player.setForceMovement(forceMovement);
            }).then(2, () -> {
                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 35, 741, 3);
                player.setForceMovement(forceMovement);
            }).then(2, player::unlock);
        } else {
            if (player.tile().x == 3200 && player.tile().y == 10196) {
                player.getMovementQueue().step(3200, 10196, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            }
        }
    }
}

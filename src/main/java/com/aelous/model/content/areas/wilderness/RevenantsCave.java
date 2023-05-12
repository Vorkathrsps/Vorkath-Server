package com.aelous.model.content.areas.wilderness;

import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.REVENANT_MALEDICTUS;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 17, 2020 16:19
 */
public class RevenantsCave extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            switch (obj.getId()) {
                case CAVERN_31555 -> {
                    Chain.bound(player)
                        .runFn(1, player::lockDelayDamage)
                        .then(2, () -> {
                            player.teleport(3197, 10056);
                            player.message("You enter the cave.");
                        }).then(2, player::unlock);
                    return true;
                }
                case 43868 -> {
                    Chain.bound(player)
                        .runFn(1, player::lockDelayDamage)
                        .then(2, () -> {
                            player.teleport(3124, 3806, 0);
                            player.message("You enter the cave.");
                        }).then(2, player::unlock);
                    return true;
                }
                case OPENING_31558 -> {
                    Chain.bound(player)
                        .runFn(1, player::lockDelayDamage)
                        .then(2, () -> {
                            player.teleport(3102, 3656, 0);
                            player.message("You exit the cave.");
                        }).then(2, player::unlock);
                    return true;
                }
                case CAVERN_31556 -> {
                    Chain.bound(player)
                        .runFn(1, player::lockDelayDamage)
                        .then(2, () -> {
                            player.teleport(3241, 10233);
                            player.message("You enter the cave.");
                        }).then(2, player::unlock);
                    return true;
                }
                case PILLAR_31561 -> {
                    player.smartPathTo(obj.tile());
                    player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> {
                        if (obj.tile().equals(3241, 10145)) {
                            if (player.getSkills().level(Skills.AGILITY) < 89) {
                                player.message("You need an agility level of at least 89 to jump this pillar.");
                            } else {
                                pillarJump(player);
                            }
                        }
                        if (obj.tile().equals(3202, 10196) || obj.tile().equals(3180, 10209) || obj.tile().equals(3200, 10136)) {
                            if (player.getSkills().level(Skills.AGILITY) < 75) {
                                player.message("You need an agility level of at least 75 to jump this pillar.");
                            } else {
                                pillarJump(player);
                            }
                        }
                        if (obj.tile().equals(3220, 10086)) {
                            if (player.getSkills().level(Skills.AGILITY) < 65) {
                                player.message("You need an agility level of at least 65 to jump this pillar.");
                            } else {
                                pillarJump(player);
                            }
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }
    private void pillarJump(Player player) {
        if (player.tile().x == 3220 && player.tile().y == 10084) {
            player.waitUntil(() -> player.tile().equals(3220, 10084), () -> {
                player.getMovementQueue().step(3220, 10084, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
            }).then(1, () -> {
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            });
        } else {
            if (player.tile().x == 3220 && player.tile().y == 10088) {
                player.waitUntil(() -> player.tile().equals(3220, 10088), () -> {
                    player.getMovementQueue().step(3220, 10088, MovementQueue.StepType.FORCED_WALK);
                    player.lockDelayDamage();
                }).then(1, () -> {
                    Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, player::unlock);
                });
            }
        }
        if (player.tile().x == 3202 && player.tile().y == 10136) {
            player.waitUntil(() -> player.tile().equals(3202, 10136), () -> {
                player.getMovementQueue().step(3202, 10136, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
            }).then(1, () -> {
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            });
        } else {
            if (player.tile().x == 3198 && player.tile().y == 10136) {
                player.waitUntil(() -> player.tile().equals(3198, 10136), () -> {
                    player.getMovementQueue().step(3198, 10136, MovementQueue.StepType.FORCED_WALK);
                    player.lockDelayDamage();
                }).then(1, () -> {
                    Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, player::unlock);
                });
            }
        }
        if (player.tile().x == 3243 && player.tile().y == 10145) {
            player.waitUntil(() -> player.tile().equals(3243, 10145), () -> {
                player.getMovementQueue().step(3243, 10145, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
            }).then(1, () -> {
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            });
        } else {
            if (player.tile().x == 3239 && player.tile().y == 10145) {
                player.waitUntil(() -> player.tile().equals(3239, 10145), () -> {
                    player.getMovementQueue().step(3239, 10145, MovementQueue.StepType.FORCED_WALK);
                    player.lockDelayDamage();
                }).then(1, () -> {
                    Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, player::unlock);
                });
            }
        }
        if (player.tile().x == 3180 && player.tile().y == 10207) {
            player.waitUntil(() -> player.tile().equals(3180, 10207), () -> {
                player.getMovementQueue().step(3180, 10207, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
            }).then(1, () -> {
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 2), 0, 30, 741, 0);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            });
        } else {
            if (player.tile().x == 3180 && player.tile().y == 10211) {
                player.waitUntil(() -> player.tile().equals(3180, 10211), () -> {
                    player.getMovementQueue().step(3180, 10211, MovementQueue.StepType.FORCED_WALK);
                    player.lockDelayDamage();
                }).then(1, () -> {
                    Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, player::unlock);
                });
            }
        }
        if (player.tile().x == 3204 && player.tile().y == 10196) {
            player.waitUntil(() -> player.tile().equals(3204, 10196), () -> {
                player.getMovementQueue().step(3204, 10196, MovementQueue.StepType.FORCED_WALK);
                player.lockDelayDamage();
            }).then(1, () -> {
                Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-2, 0), 0, 30, 741, 3);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);
            });
        } else {
            if (player.tile().x == 3200 && player.tile().y == 10196) {
                player.waitUntil(() -> player.tile().equals(3200, 10196), () -> {
                    player.getMovementQueue().step(3200, 10196, MovementQueue.StepType.FORCED_WALK);
                    player.lockDelayDamage();
                }).then(1, () -> {
                    Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 0, 30, 741, 1);
                        player.setForceMovement(forceMovement);
                    }).then(2, player::unlock);
                });
            }
        }
    }
}

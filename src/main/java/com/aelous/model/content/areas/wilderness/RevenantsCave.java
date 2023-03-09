package com.aelous.model.content.areas.wilderness;

import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 17, 2020 16:19
 */
public class RevenantsCave extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            // Cave entrance
            // Cave exit
            // Northern cave exit
            // Northern cave entrance
            switch (obj.getId()) {
                case CAVERN_31555 -> {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Enter normal cave", "Enter donator cave");
                            setPhase(0);
                        }

                        @Override
                        protected void select(int option) {
                            if(isPhase(0)) {
                                if(option == 1) {
                                    stop();
                                    player.animate(2796);
                                    Chain.bound(null).runFn(2, () -> {
                                        player.animate(-1);
                                        player.teleport(3196, 10056);
                                        player.message("You enter the cave.");
                                    });
                                }
                                if(option == 2) {
                                    Tile tile = new Tile(2335, 9795);

                                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
                                        return;
                                    }

                                    player.getDialogueManager().start(new Dialogue() {
                                        @Override
                                        protected void start(Object... parameters) {
                                            send(DialogueType.STATEMENT, "This teleport will send you to a dangerous area.", "Do you wish to continue?");
                                            setPhase(1);
                                        }

                                        @Override
                                        protected void next() {
                                            if (isPhase(1)) {
                                                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                                                setPhase(2);
                                            }
                                        }

                                        @Override
                                        protected void select(int option) {
                                            if (option == 1) {
                                                if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                                                    stop();
                                                    return;
                                                }

                                                Chain.bound(null).runFn(1, () -> {
                                                    player.animate(2796);
                                                }).then(2, () -> {
                                                    player.animate(-1);
                                                    player.teleport(2335, 9795);
                                                    player.message("You enter the cave.");
                                                });
                                            }
                                            stop();
                                        }
                                    });
                                }
                            }
                        }
                    });
                    return true;
                }
                case OPENING_31557 -> {
                    player.animate(2796);
                    TaskManager.submit(new TickAndStop(2) {
                        @Override
                        public void executeAndStop() {
                            player.animate(-1);
                            player.teleport(3075, 3653);
                            player.message("You exit the cave.");
                        }
                    });
                    return true;
                }
                case OPENING_31558 -> {
                    player.animate(2796);
                    TaskManager.submit(new TickAndStop(2) {
                        @Override
                        public void executeAndStop() {
                            player.animate(-1);
                            player.teleport(3126, 3832);
                            player.message("You exit the cave.");
                        }
                    });
                    return true;
                }
                case CAVERN_31556 -> {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Enter normal cave", "Enter donator cave");
                            setPhase(0);
                        }

                        @Override
                        protected void select(int option) {
                            if(isPhase(0)) {
                                stop();
                                if(option == 1) {
                                    Chain.bound(null).runFn(1, () -> {
                                        player.animate(2796);
                                    }).then(2, () -> {
                                        player.animate(-1);
                                        player.teleport(3241, 10234);
                                        player.message("You enter the cave.");
                                    });
                                }
                                if(option == 2) {
                                    Tile tile = new Tile(2335, 9795);

                                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
                                        return;
                                    }

                                    player.getDialogueManager().start(new Dialogue() {
                                        @Override
                                        protected void start(Object... parameters) {
                                            send(DialogueType.STATEMENT, "This teleport will send you to a dangerous area.", "Do you wish to continue?");
                                            setPhase(1);
                                        }

                                        @Override
                                        protected void next() {
                                            if (isPhase(1)) {
                                                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                                                setPhase(2);
                                            }
                                        }

                                        @Override
                                        protected void select(int option) {
                                            if (option == 1) {
                                                if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                                                    stop();
                                                    return;
                                                }

                                                Chain.bound(null).runFn(1, () -> {
                                                    player.animate(2796);
                                                }).then(2, () -> {
                                                    player.animate(-1);
                                                    player.teleport(2335, 9795);
                                                    player.message("You enter the cave.");
                                                });
                                            }
                                            stop();
                                        }
                                    });
                                }
                            }
                        }
                    });
                    return true;
                }
                case PILLAR_31561 -> {
                    player.smartPathTo(obj.tile());
                    // lazy wait until we stop moving
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
            TaskManager.submit(new Task("pillar_revs_task", 1) {
                int ticks = 0;

                @Override
                protected void execute() {
                    ticks++;
                    player.getMovementQueue().clear();
                    if (ticks == 1) {
                        player.getMovementQueue().interpolate(new Tile(3220, 10084), MovementQueue.StepType.FORCED_WALK);
                    }

                    if (ticks == 3) {
                        player.animate(741, 15);
                    }

                    if (ticks == 4) {
                        player.teleport(new Tile(3220, 10086));
                    }

                    if (ticks == 6) {
                        player.animate(741, 15);
                    }

                    if (ticks == 7) {
                        player.teleport(new Tile(3220, 10088));
                        player.message("You make it across the pillar without any problems.");
                        this.stop();
                    }
                }
            });
        } else {
            if (player.tile().x == 3220 && player.tile().y == 10088) {
                TaskManager.submit(new Task("pillar_revs_task", 1) {
                    int ticks = 0;

                    @Override
                    protected void execute() {
                        ticks++;
                        player.getMovementQueue().clear();
                        if (ticks == 1) {
                            player.getMovementQueue().interpolate(new Tile(3220, 10088), MovementQueue.StepType.FORCED_WALK);
                        }

                        if (ticks == 3) {
                            player.animate(741, 15);
                        }

                        if (ticks == 4) {
                            player.teleport(new Tile(3220, 10086));
                        }

                        if (ticks == 6) {
                            player.animate(741, 15);
                        }

                        if (ticks == 7) {
                            player.teleport(new Tile(3220, 10084));
                            player.message("You make it across the pillar without any problems.");
                            this.stop();
                        }
                    }
                });
            }
        }

        if (player.tile().x == 3202 && player.tile().y == 10136) {
            TaskManager.submit(new Task("pillar_revs_task", 1) {
                int ticks = 0;

                @Override
                protected void execute() {
                    ticks++;
                    player.getMovementQueue().clear();
                    if (ticks == 1) {
                        player.getMovementQueue().interpolate(new Tile(3202, 10136), MovementQueue.StepType.FORCED_WALK);
                    }

                    if (ticks == 3) {
                        player.animate(741, 15);
                    }

                    if (ticks == 4) {
                        player.teleport(new Tile(3200, 10136));
                    }

                    if (ticks == 6) {
                        player.animate(741, 15);
                    }

                    if (ticks == 7) {
                        player.teleport(new Tile(3198, 10136));
                        player.message("You make it across the pillar without any problems.");
                        this.stop();
                    }
                }
            });
        } else {
            if (player.tile().x == 3198 && player.tile().y == 10136) {
                TaskManager.submit(new Task("pillar_revs_task", 1) {
                    int ticks = 0;

                    @Override
                    protected void execute() {
                        ticks++;
                        player.getMovementQueue().clear();
                        if (ticks == 1) {
                            player.getMovementQueue().interpolate(new Tile(3198, 10136), MovementQueue.StepType.FORCED_WALK);
                        }

                        if (ticks == 3) {
                            player.animate(741, 15);
                        }

                        if (ticks == 4) {
                            player.teleport(new Tile(3200, 10136));
                        }

                        if (ticks == 6) {
                            player.animate(741, 15);
                        }

                        if (ticks == 7) {
                            player.teleport(new Tile(3202, 10136));
                            player.message("You make it across the pillar without any problems.");
                            this.stop();
                        }
                    }
                });
            }
        }

        if (player.tile().x == 3243 && player.tile().y == 10145) {
            TaskManager.submit(new Task("pillar_revs_task", 1) {
                int ticks = 0;

                @Override
                protected void execute() {
                    ticks++;
                    player.getMovementQueue().clear();
                    if (ticks == 1) {
                        player.getMovementQueue().interpolate(new Tile(3243, 10145), MovementQueue.StepType.FORCED_WALK);
                    }

                    if (ticks == 3) {
                        player.animate(741, 15);
                    }

                    if (ticks == 4) {
                        player.teleport(new Tile(3241, 10145));
                    }

                    if (ticks == 6) {
                        player.animate(741, 15);
                    }

                    if (ticks == 7) {
                        player.teleport(new Tile(3239, 10145));
                        player.message("You make it across the pillar without any problems.");
                        this.stop();
                    }
                }
            });
        } else {
            if (player.tile().x == 3239 && player.tile().y == 10145) {
                TaskManager.submit(new Task("pillar_revs_task", 1) {
                    int ticks = 0;

                    @Override
                    protected void execute() {
                        ticks++;
                        player.getMovementQueue().clear();
                        if (ticks == 1) {
                            player.getMovementQueue().interpolate(new Tile(3239, 10145), MovementQueue.StepType.FORCED_WALK);
                        }

                        if (ticks == 3) {
                            player.animate(741, 15);
                        }

                        if (ticks == 4) {
                            player.teleport(new Tile(3241, 10145));
                        }

                        if (ticks == 6) {
                            player.animate(741, 15);
                        }

                        if (ticks == 7) {
                            player.teleport(new Tile(3243, 10145));
                            player.message("You make it across the pillar without any problems.");
                            this.stop();
                        }
                    }
                });
            }
        }

        if (player.tile().x == 3180 && player.tile().y == 10211) {
            TaskManager.submit(new Task("pillar_revs_task", 1) {
                int ticks = 0;

                @Override
                protected void execute() {
                    ticks++;
                    player.getMovementQueue().clear();
                    if (ticks == 1) {
                        player.getMovementQueue().interpolate(new Tile(3180, 10211), MovementQueue.StepType.FORCED_WALK);
                    }

                    if (ticks == 3) {
                        player.animate(741, 15);
                    }

                    if (ticks == 4) {
                        player.teleport(new Tile(3180, 10209));
                    }

                    if (ticks == 6) {
                        player.animate(741, 15);
                    }

                    if (ticks == 7) {
                        player.teleport(new Tile(3180, 10207));
                        player.message("You make it across the pillar without any problems.");
                        this.stop();
                    }
                }
            });
        } else {
            if (player.tile().x == 3180 && player.tile().y == 10207) {
                TaskManager.submit(new Task("pillar_revs_task", 1) {
                    int ticks = 0;

                    @Override
                    protected void execute() {
                        ticks++;
                        player.getMovementQueue().clear();
                        if (ticks == 1) {
                            player.getMovementQueue().interpolate(new Tile(3180, 10207), MovementQueue.StepType.FORCED_WALK);
                        }

                        if (ticks == 3) {
                            player.animate(741, 15);
                        }

                        if (ticks == 4) {
                            player.teleport(new Tile(3180, 10209));
                        }

                        if (ticks == 6) {
                            player.animate(741, 15);
                        }

                        if (ticks == 7) {
                            player.teleport(new Tile(3180, 10211));
                            player.message("You make it across the pillar without any problems.");
                            this.stop();
                        }
                    }
                });
            }
        }

        if (player.tile().x == 3204 && player.tile().y == 10196) {
            TaskManager.submit(new Task("pillar_revs_task", 1) {
                int ticks = 0;

                @Override
                protected void execute() {
                    ticks++;
                    player.getMovementQueue().clear();
                    if (ticks == 1) {
                        player.getMovementQueue().interpolate(new Tile(3204, 10196), MovementQueue.StepType.FORCED_WALK);
                    }

                    if (ticks == 3) {
                        player.animate(741, 15);
                    }

                    if (ticks == 4) {
                        player.teleport(new Tile(3202, 10196));
                    }

                    if (ticks == 6) {
                        player.animate(741, 15);
                    }

                    if (ticks == 7) {
                        player.teleport(new Tile(3200, 10196));
                        player.message("You make it across the pillar without any problems.");
                        this.stop();
                    }
                }
            });
        } else {
            if (player.tile().x == 3200 && player.tile().y == 10196) {
                TaskManager.submit(new Task("pillar_revs_task", 1) {
                    int ticks = 0;

                    @Override
                    protected void execute() {
                        ticks++;
                        player.getMovementQueue().clear();
                        if (ticks == 1) {
                            player.getMovementQueue().interpolate(new Tile(3200, 10196), MovementQueue.StepType.FORCED_WALK);
                        }

                        if (ticks == 3) {
                            player.animate(741, 15);
                        }

                        if (ticks == 4) {
                            player.teleport(new Tile(3202, 10196));
                        }

                        if (ticks == 6) {
                            player.animate(741, 15);
                        }

                        if (ticks == 7) {
                            player.teleport(new Tile(3204, 10196));
                            player.message("You make it across the pillar without any problems.");
                            this.stop();
                        }
                    }
                });
            }
        }
    }
}

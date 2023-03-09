package com.aelous.model.content.areas.dungeons.brimhaven;

import com.aelous.model.content.skill.impl.woodcutting.Woodcutting;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 07, 2020 17:14
 */
public class BrimhavenDungeon extends PacketInteraction {

    private static void chopDown(Player player, GameObject vines) {
        Woodcutting.Hatchet axe = Woodcutting.findAxe(player).orElse(null);
        if (axe == null) {
            player.message("You need an axe to chop down these vines.");
            player.message("You do not have an axe which you have the Woodcutting level to use.");
            return;
        }
        if (player.getSkills().level(Skills.WOODCUTTING) < 10) {
            player.message("You need a Woodcutting level of at least 10 to chop down these vines.");
            return;
        }

        Chain.bound(player).repeatingTask(1, event -> {
            //Extra safety
            if (!player.isRegistered()) {
                event.stop();
                return;
            }
            int diffX = vines.tile().x - player.getAbsX();
            int diffY = vines.tile().y - player.getAbsY();
            final int srcX = player.getAbsX();
            final int srcY = player.getAbsY();
            player.animate(axe.anim);
            Chain.bound(null).runFn(3, () -> {
                if (Math.abs(diffX) > 1 || Math.abs(diffY) > 1 || (diffX + diffY) > 1)
                    return;
                if (vines.getId() != -1) {
                    vines.remove();
                    Chain.bound(null).runFn(4, vines::add);
                    event.stop();
                }
                int targetX = srcX;
                int targetY = srcY;
                if (vines.getRotation() == 1 || vines.getRotation() == 3) {
                    if (diffX == -1) {
                        targetX -= 2;
                    } else if (diffX == 1) {
                        targetX += 2;
                    }
                } else {
                    if (diffY == -1) {
                        targetY -= 2;
                    } else if (diffY == 1) {
                        targetY += 2;
                    }
                }

                player.animate(-1);
                player.stepAbs(targetX, targetY, MovementQueue.StepType.FORCED_WALK);
                player.waitForTile(new Tile(targetX, targetY), player::unlock);
            });
        });
    }

    private void agilityShortcutMessage(Player player, int item, String line1) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, Item.of(item), "", line1);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    private void agilityShortcutMessage(Player player, int item, String line1, String line2) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, Item.of(item), "", line1, line2);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    private void agilityShortcutMessage(Player player, int item, String line1, String line2, String line3) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, Item.of(item), "", line1, line2, line3);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == STEPPING_STONE_19040) {
                if (obj.tile().x == 2684 && obj.tile().y == 9548) {
                    Tile startPos = new Tile(2682, 9548);
                    player.smartPathTo(startPos);
                    player.waitForTile(startPos, () -> {
                        if (player.getSkills().level(Skills.AGILITY) < 83) {
                            agilityShortcutMessage(player, 6514, "You need a agility level of 83 to cross these stepping stones.");
                        } else {
                            TaskManager.submit(new Task("stepping_stone_task", 1) {
                                int ticks = 0;

                                @Override
                                protected void execute() {
                                    ticks++;
                                    if (ticks == 1) {
                                        player.setPositionToFace(obj.tile());
                                        player.animate(769);
                                    } else if (ticks == 3) {
                                        player.teleport(2684, 9548, 0);
                                    } else if (ticks == 4) {
                                        player.animate(769);
                                    } else if (ticks == 6) {
                                        player.teleport(2686, 9548, 0);
                                    } else if (ticks == 7) {
                                        player.animate(769);
                                    } else if (ticks == 9) {
                                        player.teleport(2688, 9547, 0);
                                    } else if (ticks == 10) {
                                        player.animate(769);
                                    } else if (ticks == 12) {
                                        player.teleport(2690, 9547, 0);
                                        stop();
                                    }
                                }
                            });
                        }
                    });
                } else if (obj.tile().x == 2688 && obj.tile().y == 9547) {
                    Tile startPos = new Tile(2690, 9547);
                    player.smartPathTo(startPos);
                    player.waitForTile(startPos, () -> {
                        if (player.getSkills().level(Skills.AGILITY) < 83) {
                            agilityShortcutMessage(player, 6514, "You need a agility level of 83 to cross these stepping stones.");
                        } else {
                            TaskManager.submit(new Task("stepping_stone_task", 1) {
                                int ticks = 0;

                                @Override
                                protected void execute() {
                                    ticks++;
                                    if (ticks == 1) {
                                        player.setPositionToFace(obj.tile());
                                        player.animate(769);
                                    } else if (ticks == 3) {
                                        player.teleport(2688, 9547, 0);
                                    } else if (ticks == 4) {
                                        player.animate(769);
                                    } else if (ticks == 6) {
                                        player.teleport(2686, 9548, 0);
                                    } else if (ticks == 7) {
                                        player.animate(769);
                                    } else if (ticks == 9) {
                                        player.teleport(2684, 9548, 0);
                                    } else if (ticks == 10) {
                                        player.animate(769);
                                    } else if (ticks == 12) {
                                        player.teleport(2682, 9548, 0);
                                        stop();
                                    }
                                }
                            });
                        }
                    });
                } else if (obj.tile().x == 2695 && obj.tile().y == 9531) {
                    Tile startPos = new Tile(2695, 9533);
                    player.smartPathTo(startPos);
                    player.waitForTile(startPos, () -> {
                        if (player.getSkills().level(Skills.AGILITY) < 83) {
                            agilityShortcutMessage(player, 6514, "You need a agility level of 83 to cross these stepping stones.");
                        } else {
                            TaskManager.submit(new Task("stepping_stone_task", 1) {
                                int ticks = 0;

                                @Override
                                protected void execute() {
                                    ticks++;
                                    if (ticks == 1) {
                                        player.setPositionToFace(obj.tile());
                                        player.animate(769);
                                    } else if (ticks == 3) {
                                        player.teleport(2695, 9531, 0);
                                    } else if (ticks == 4) {
                                        player.animate(769);
                                    } else if (ticks == 6) {
                                        player.teleport(2695, 9529, 0);
                                    } else if (ticks == 7) {
                                        player.animate(769);
                                    } else if (ticks == 9) {
                                        player.teleport(2696, 9527, 0);
                                    } else if (ticks == 10) {
                                        player.animate(769);
                                    } else if (ticks == 12) {
                                        player.teleport(2697, 9525, 0);
                                        stop();
                                    }
                                }
                            });
                        }
                    });
                } else if (obj.tile().x == 2696 && obj.tile().y == 9527) {

                    Tile startPos = new Tile(2697, 9525);
                    player.smartPathTo(startPos);
                    player.waitForTile(startPos, () -> {
                        if (player.getSkills().level(Skills.AGILITY) < 83) {
                            agilityShortcutMessage(player, 6514, "You need a agility level of 83 to cross these stepping stones.");
                        } else {
                            TaskManager.submit(new Task("stepping_stone_task", 1) {
                                int ticks = 0;

                                @Override
                                protected void execute() {
                                    ticks++;
                                    if (ticks == 1) {
                                        player.setPositionToFace(obj.tile());
                                        player.animate(769);
                                    } else if (ticks == 3) {
                                        player.teleport(2696, 9527, 0);
                                    } else if (ticks == 4) {
                                        player.animate(769);
                                    } else if (ticks == 6) {
                                        player.teleport(2695, 9529, 0);
                                    } else if (ticks == 7) {
                                        player.animate(769);
                                    } else if (ticks == 9) {
                                        player.teleport(2695, 9531, 0);
                                    } else if (ticks == 10) {
                                        player.animate(769);
                                    } else if (ticks == 12) {
                                        player.teleport(2695, 9533, 0);
                                        stop();
                                    }
                                }
                            });
                        }
                    });
                }
                return true;
            }
            //Entrance
            if (obj.getId() == 20877) {
                player.setPositionToFace(obj.tile());
                player.animate(832);
                Chain.bound(null).runFn(2, () -> player.teleport(2713, 9564));
                return true;
            }

            //Exit
            if (obj.getId() == EXIT_20878) {
                player.teleport(2745, 3152);
                return true;
            }

            if (obj.getId() == VINES_21731) {
                chopDown(player, obj);
                return true;
            }

            if (obj.getId() == VINES_21732) {
                chopDown(player, obj);
                return true;
            }

            if (obj.getId() == VINE_26880) {
                if (player.getSkills().level(Skills.AGILITY) < 87) {
                    agilityShortcutMessage(player, 6517, "it's a long way up and the vine is hard to grip.", "You'll need an Agility level of 87 to climb up.");
                    return true;
                }
                player.teleport(2670, 9583, 2);
                return true;
            }

            if (obj.getId() == VINE_26882) {
                if (player.getSkills().level(Skills.AGILITY) < 87) {
                    agilityShortcutMessage(player, 6517, "it's a long way up and the vine is hard to grip.", "You'll need an Agility level of 87 to climb up.");
                    return true;
                }
                player.teleport(2673, 9583, 0);
                return true;
            }
            if (obj.getId() == STAIRS_21722) {
                player.teleport(new Tile(2643, 9595, 2));
                return true;
            }

            if (obj.getId() == STAIRS_21724) {
                player.teleport(new Tile(2649, 9591, 0));
                return true;
            }

            if (obj.getId() == PIPE_21728) {
                if (!player.getSkills().check(Skills.AGILITY, 22, "use this shortcut"))
                    return true;
                player.teleport(2655, player.getAbsY() >= 9572 ? 9566 : 9573, 0);
                return true;
            }

            if (obj.getId() == PIPE_21727) {
                if (!player.getSkills().check(Skills.AGILITY, 34, "use this shortcut"))
                    return true;
                player.teleport(2698, player.getAbsY() >= 9499 ? 9492 : 9500, 0);
                return true;
            }

            if (obj.getId() == STEPPING_STONE_21738) {
                if (player.tile().x == 2649 && player.tile().y == 9562) {
                    if (player.getSkills().level(Skills.AGILITY) < 12) {
                        agilityShortcutMessage(player, 6514, "You need a agility level of 12 to cross these stepping stones.");
                        return true;
                    }
                    TaskManager.submit(new Task("stepping_stone_task", 1) {
                        int ticks = 0;

                        @Override
                        protected void execute() {
                            ticks++;
                            if (ticks == 1) {
                                player.setPositionToFace(obj.tile());
                            } else if (ticks == 2) {
                                player.animate(769);
                            } else if (ticks == 3) {
                                player.teleport(2649, 9561, 0);
                            } else if (ticks == 5) {
                                player.animate(769);
                            } else if (ticks == 6) {
                                player.teleport(2649, 9560, 0);
                            } else if (ticks == 8) {
                                player.setPositionToFace(obj.tile());
                            } else if (ticks == 9) {
                                player.animate(769);
                            } else if (ticks == 10) {
                                player.teleport(2648, 9560, 0);
                            } else if (ticks == 12) {
                                player.animate(769);
                            } else if (ticks == 13) {
                                player.teleport(2647, 9560, 0);
                            } else if (ticks == 15) {
                                player.setPositionToFace(obj.tile());
                            } else if (ticks == 16) {
                                player.animate(769);
                            } else if (ticks == 17) {
                                player.teleport(2647, 9559, 0);
                            } else if (ticks == 19) {
                                player.animate(769);
                            } else if (ticks == 20) {
                                player.teleport(2647, 9558, 0);
                            } else if (ticks == 22) {
                                player.animate(769);
                            } else if (ticks == 23) {
                                player.teleport(2647, 9557, 0);
                                player.getSkills().addXp(Skills.AGILITY, 8.0);
                                stop();
                            }
                        }
                    });
                }
                return true;
            }

            if (obj.getId() == STEPPING_STONE_21739) {
                if (player.tile().x == 2647 && player.tile().y == 9557) {
                    TaskManager.submit(new Task("stepping_stone_task", 1) {
                        int ticks = 0;

                        @Override
                        protected void execute() {
                            ticks++;
                            if (ticks == 1) {
                                player.setPositionToFace(obj.tile());
                            } else if (ticks == 2) {
                                player.animate(769);
                            } else if (ticks == 3) {
                                player.teleport(2647, 9558, 0);
                            } else if (ticks == 5) {
                                player.animate(769);
                            } else if (ticks == 6) {
                                player.teleport(2647, 9559, 0);
                            } else if (ticks == 8) {
                                player.animate(769);
                            } else if (ticks == 9) {
                                player.teleport(2647, 9560, 0);
                            } else if (ticks == 11) {
                                player.setPositionToFace(obj.tile());
                            } else if (ticks == 12) {
                                player.animate(769);
                            } else if (ticks == 13) {
                                player.teleport(2648, 9560, 0);
                            } else if (ticks == 15) {
                                player.animate(769);
                            } else if (ticks == 16) {
                                player.teleport(2649, 9560, 0);
                            } else if (ticks == 18) {
                                player.setPositionToFace(obj.tile());
                            } else if (ticks == 19) {
                                player.animate(769);
                            } else if (ticks == 20) {
                                player.teleport(2649, 9561, 0);
                            } else if (ticks == 22) {
                                player.animate(769);
                            } else if (ticks == 23) {
                                player.teleport(2649, 9562, 0);
                                player.getSkills().addXp(Skills.AGILITY, 7.0);
                                stop();
                            }
                        }
                    });
                }
                return true;
            }

            if (obj.getId() == STAIRS_21725) {
                player.teleport(2636, 9510, 2);
                return true;
            }

            if (obj.getId() == STAIRS_21726) {
                player.teleport(2636, 9517, 0);
                return true;
            }
            if (obj.getId() == VINES_21733) {
                chopDown(player, obj);
                return true;
            }
            if (obj.getId() == VINES_21734) {
                chopDown(player, obj);
                return true;
            }

            if (obj.getId() == VINES_21735) {
                chopDown(player, obj);
                return true;
            }

            if (obj.getId() == CREVICE_30201) {
                player.message("It doesn't look like there's a way out there.");
                return true;
            }

            if (obj.getId() == LOG_BALANCE_20884) {
                if(!player.getSkills().check(Skills.AGILITY, 30, "use this shortcut"))
                    return true;
                player.teleport(2687, 9506, 0);
                return true;
            }

            if (obj.getId() == LOG_BALANCE_20882) {
                if(!player.getSkills().check(Skills.AGILITY, 30, "use this shortcut"))
                    return true;
                player.teleport(2687, 9506, 0);
                return true;
            }
            if (obj.getId() == CREVICE_30198) {
                if (obj.tile().equals(2696, 9436)) {
                    if (player.tile().equals(2697, 9436)) {
                        player.teleport(player.tile().transform(-13, 0, 0));
                    }
                } else if (obj.tile().equals(2685, 9436)) {
                    if (player.tile().equals(2684, 9436)) {
                        player.teleport(player.tile().transform(+13, 0, 0));
                    }
                }
                return true;
            }
        }
        return false;
    }
}

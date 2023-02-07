package com.aelous.model.content.skill.impl.slayer.master.impl;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.master.SlayerMaster;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.model.entity.player.QuestTab.InfoTab.TASK_STREAK;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.DURADEL;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.KRYSTILIA;

/**
 * @author Patrick van Elderen | May, 30, 2021, 18:43
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Krystalia extends PacketInteraction {

    public static final int BM_CANCEL_FEE = 2_500;

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if (npc.id() == KRYSTILIA) {
                displayOptions(player);
                return true;
            }
        }
        if(option == 2) {
            if (npc.id() == KRYSTILIA) {
                taskOptions(player);
                return true;
            }
        }
        if(option == 3) {
            if (npc.id() == KRYSTILIA) {
                World.getWorld().shop(14).open(player);
                return true;
            }
        }
        if(option == 4) {
            if (npc.id() == KRYSTILIA) {
                player.getSlayerRewards().open();
                return true;
            }
        }
        return false;
    }

    private static void assignTask(Player player, String taskType) {
        int numleft = player.slayerTaskAmount();

        if (numleft > 0) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.NPC_STATEMENT, KRYSTILIA, Expression.HAPPY, "You're still hunting " + Slayer.taskName(player.slayerTaskId()) + "; you have " + numleft + " to go.", "Come back when you've finished your task.");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        send(DialogueType.NPC_STATEMENT, KRYSTILIA, Expression.HAPPY, "Come back to me when you've completed your task", "for a new one.");
                        setPhase(1);
                    } else if (isPhase(1)) {
                        stop();
                    }
                }
            });
            return;
        }

        if(taskType.equalsIgnoreCase("boss")) {
            SlayerMaster.assign(player, DURADEL);
        } else {
            SlayerMaster.assign(player, KRYSTILIA);
        }

        SlayerCreature task = SlayerCreature.lookup(player.slayerTaskId());
        int num = player.slayerTaskAmount();
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.NPC_STATEMENT, KRYSTILIA, Expression.HAPPY, "Ok, great. Your new task is to kill " + num + " " + Slayer.taskName(task.uid) + ".");
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

    private static void taskOptions(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I'd like a boss task, please.", "I'd like a regular task, please.", "Nothing");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        stop();
                        assignTask(player, "boss");
                    } else if (option == 2) {
                        stop();
                        assignTask(player, "regular");
                    } else if (option == 3) {
                        stop();
                    }
                }
            }
        });
    }

    private static void displayOptions(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I'd like a boss task, please.", "I'd like a regular task, please.", "Can you cancel my current task for blood money?", "Nothing");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        stop();
                        assignTask(player, "boss");
                    } else if (option == 2) {
                        stop();
                        assignTask(player, "regular");
                    } else if (option == 3) {
                        stop();
                        boolean hasTask = player.slayerTaskAmount() > 0;
                        if(!hasTask) {
                            player.message("It appears that you do not have a slayer task.");
                           return;
                        }
                        cancelForBloodMoney(player);
                    } else if (option == 4) {
                        stop();
                    }
                }
            }
        });
    }

    private static void cancelForBloodMoney(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'd like to cancel my current slayer task with", "blood money.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.NPC_STATEMENT, KRYSTILIA, Expression.HAPPY, "Certainly, just as a reminder, the fee is " + BM_CANCEL_FEE + " blood money.", "This action is irreversible.");
                    setPhase(1);
                } else if (isPhase(1)) {
                    send(DialogueType.OPTION, "Cancel current slayer task?", "Yes, pay the " + BM_CANCEL_FEE + " blood money cancel fee.", "Nevermind, I'll keep my money.");
                    setPhase(2);
                } else if (isPhase(3)) {
                    send(DialogueType.NPC_STATEMENT, KRYSTILIA, Expression.HAPPY, "There you go. Your current slayer task has been cleared.", "Come speak to me when you're ready for a new one.");
                    setPhase(4);
                } else if (isPhase(4)) {
                    stop();
                } else if (isPhase(5)) {
                    send(DialogueType.NPC_STATEMENT, KRYSTILIA, Expression.HAPPY, "Come speak to me when you have enough blood money.");
                    setPhase(4);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(2)) {
                    if (option == 1) {
                        if (player.getInventory().contains(new Item(13307)) && player.getInventory().byId(13307).getAmount() >= BM_CANCEL_FEE) {
                            player.putAttrib(AttributeKey.SLAYER_TASK_ID, 0);
                            player.putAttrib(AttributeKey.SLAYER_TASK_AMT, 0);
                            player.putAttrib(AttributeKey.SLAYER_TASK_SPREE, 0);
                            player.getPacketSender().sendString(TASK_STREAK.childId, QuestTab.InfoTab.INFO_TAB.get(TASK_STREAK.childId).fetchLineData(player));
                            player.getInventory().remove(new Item(13307, BM_CANCEL_FEE), true);
                            send(DialogueType.ITEM_STATEMENT, new Item(13307,2500), "", "You hand over the blood money to Krystilia.", "She swiftly takes the money and performs her service.");
                            setPhase(3);
                        } else {
                            send(DialogueType.NPC_STATEMENT, KRYSTILIA, Expression.HAPPY, "Sorry, but it appears you do not have enough blood money", "to cover the cancellation fee.");
                            setPhase(5);
                        }
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

}

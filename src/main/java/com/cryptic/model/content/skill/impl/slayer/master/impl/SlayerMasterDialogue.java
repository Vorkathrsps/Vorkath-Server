package com.cryptic.model.content.skill.impl.slayer.master.impl;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.model.entity.player.Player;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.DURADEL;

/**
 * @author PVE
 * @Since juli 21, 2020
 */
public class SlayerMasterDialogue extends Dialogue {

    public static void giveTask(Player player) {
        SlayerTask slayer = World.getWorld().getSlayerTasks();
        SlayerTask assignment = slayer.getCurrentAssignment(player);
        if (assignment != null) {
            int numleft = assignment.getRemainingTaskAmount(player);
            if (numleft > 0) {
                DialogueManager.npcChat(player, Expression.H, player.getInteractingNpcId(), "You're still hunting " + assignment.getTaskName() + "; you have " + numleft + " to go.", "Come back when you've finished your task.");
            }
        } else {
            slayer.getRandomTask(player, NpcIdentifiers.NIEVE);
            assignment = slayer.getCurrentAssignment(player);
            if (assignment == null) return;
            int num = slayer.getRemainingTaskAmount(player);
            SlayerTask finalAssignment = assignment;
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.NPC_STATEMENT, NpcIdentifiers.NIEVE, Expression.DEFAULT, "Excellent, you're doing great.", "Your new task is to kill " + num + " " + finalAssignment.getTaskName());
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Great, thanks!");
                        setPhase(1);
                    } else if (isPhase(1)) {
                        stop();
                    }
                }
            });
        }
    }

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.NODDING_ONE, "'Ello, and what are you after then?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I need another assignment.", "Have you any rewards for me, or anything to trade?", "Er... Nothing...");
            setPhase(1);
        } else if (isPhase(2)) {
            giveTask(player);
        } else if (isPhase(5)) {
            send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.NODDING_FIVE, "I have quite a few rewards you can earn, and a wide", "variety of Slayer equipment for sale.");
            setPhase(6);
        } else if (isPhase(6)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Look at rewards.", "Look at shop.");
            setPhase(7);
        } else if (isPhase(8)) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(1)) {
            if (option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I need another assignment.");
                setPhase(2);
            } else if (option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Have you any rewards for me, or anything to trade?");
                setPhase(5);
            } else if (option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Er... Nothing...");
                setPhase(8);
            }
        } else if (isPhase(7)) {
            if (option == 1) {
                stop();
                player.getSlayerRewards().open();
            } else if (option == 2) {
                stop();
                World.getWorld().shop(14).open(player);
            }
        }
    }
}

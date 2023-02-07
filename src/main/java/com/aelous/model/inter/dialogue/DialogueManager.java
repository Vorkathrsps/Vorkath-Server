package com.aelous.model.inter.dialogue;

import com.aelous.model.entity.player.Player;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * Handles the {@link Player}s current {@link Dialogue}
 *
 * @author Erik Eide
 */
public class DialogueManager {

    /**
     * The player object.
     */
    private final Player player;

    /**
     * The current dialogue.
     */
    private Dialogue dialogue = null;

    public DialogueManager(final Player player) {
        this.player = player;
    }

    public boolean input(final int value) {
        if (dialogue != null) {
            dialogue.input(value);
            return true;
        }

        return false;
    }

    public boolean input(final String value) {
        if (dialogue != null) {
            dialogue.input(value);
            return true;
        }

        return false;
    }

    public void interrupt() {
        if (dialogue != null) {
            dialogue.finish();
            dialogue = null;
        }
    }

    public boolean isActive() {
        return dialogue != null;
    }

    public boolean next() {
        if (dialogue != null) {
            dialogue.next();
            return true;
        }

        return false;
    }

    public boolean select(final int index) {
        if (dialogue != null) {
            dialogue.select(index);
            return true;
        }
        if (dialogue==null){
           System.out.println(dialogue);
}
        return false;
    }

    /**
     * Starts a dialogue with a new dialogue block instead of repository.
     *
     * @param dialogue
     *            The dialogue to start for the player
     * @param parameters
     *            Parameters to pass on to the dialogue
     */
    public void start(Dialogue dialogue, Object... parameters) {
        this.dialogue = dialogue;
        if (dialogue != null) {
            dialogue.player = player;
            dialogue.start(parameters);
        } else {
            player.message("Invalid dialogue");
        }
    }

    /**
     * Starts a new {@link Dialogue} without any parameters
     *
     * @param dialogue
     *            The {@link Dialogue} to start
     */
    public void start(Dialogue dialogue) {
        start(dialogue, 0);
    }

    public static void sendStatement(Player player, Object... strings) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, strings);
                setPhase(0);
            }

            @Override
            public void next() {
                if (getPhase() == 0) {
                    stop();
                }
            }
        });
    }

    public static void npcChat(Player player, Expression expression, int id, String... strings) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                Deque<Object> objs = new LinkedList<>(Arrays.asList(strings));
                objs.addFirst(expression);
                objs.addFirst(id);
                send(DialogueType.NPC_STATEMENT, Iterables.toArray(objs, Object.class));
                setPhase(0);
            }

            @Override
            public void next() {
                if(getPhase() == 0) {
                    stop();
                }
            }
        });
    }

}

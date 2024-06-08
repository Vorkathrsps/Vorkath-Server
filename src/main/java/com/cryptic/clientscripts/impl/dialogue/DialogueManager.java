package com.cryptic.clientscripts.impl.dialogue;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.entity.player.Player;
import com.cryptic.clientscripts.impl.dialogue.information.DialogueInformation;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DialogueManager {

    private final Player player;
    public DialogueInformation<?> record = null;
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

    public void remove() {
        if (dialogue != null) {
            dialogue.stop();
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

        return false;
    }

    /**
     * Starts a dialogue with a new dialogue block instead of repository.
     *
     * @param dialogue   The dialogue to start for the player
     * @param parameters Parameters to pass on to the dialogue
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
     * @param dialogue The {@link Dialogue} to start
     */
    public void start(Dialogue dialogue) {
        start(dialogue, 0);
    }

    public void sendStatement(String... strings) {
        start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendStatement(strings);
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

    public void npcChat(final Expression expression, final int id, final String... strings) {
        npcChat(NpcDefinition.cached.get(id).name, expression, id, strings);
    }


    public void npcChat(final String title, final Expression expression, final int id, final String... strings) {
        start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendNpcChat(title, id, expression, strings);
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

}
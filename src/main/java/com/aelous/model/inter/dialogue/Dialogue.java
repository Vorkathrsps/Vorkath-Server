package com.aelous.model.inter.dialogue;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

/**
 * Represents a single dialogue
 *
 * @author Erik Eide
 */
public abstract class Dialogue {

    /**
     * The default option for the choice type
     */
    protected static final String DEFAULT_OPTION_TITLE = "Select an Option";

    /**
     * The player sending the dialogue too
     */
    protected Player player;

    /**
     * The current phase of the dialogue, used to keep track of where you are
     */
    protected int phase = 0;

    /**
     * An overrideable method for what happens when the dialogue is closed
     */
    public void finish() {
    }

    /**
     * An overrideable method for inputing an integer value
     *
     * @param value
     *            The value to input
     */
    protected void input(int value) {
    }

    /**
     * An overrideable method for inputing a string value
     *
     * @param value
     *            The value to input
     */
    protected void input(String value) {
    }

    /**
     * An overrideable method for handling the next dialogue, if its not
     * overriden, it will automatically stop the dialogue
     */
    protected void next() {
        stop();
    }

    /**
     * An overrideable method for selecting an option on a choice dialogue
     *
     * @param option
     *            The index of the choice, can be between index 1 to 5
     */
    protected void select(int option) {
    }

    /**
     * Sends a dialogue to the player
     *
     * @param type
     *            The type of dialogue to send
     * @param parameters
     *            The parameters for the dialogue
     */
    protected void send(DialogueType type, Object... parameters) {
        if (type == DialogueType.OPTION) { //Re did option widget looked @ Jasons code for swords
            if (parameters.length < 3 || parameters.length > 6) {
                return;
            }
            int frame = parameters.length == 3 ? 2460 : parameters.length == 4 ? 2470 : parameters.length == 5 ? 2481 : 2493;
            int middle_swords_frame = frame + 3 + parameters.length - 1;
            int wide_swords_frame = parameters.length == 3 ? frame + 8 : parameters.length == 4 ? frame + 9 : parameters.length == 5 ? frame + 8 : frame + 9;

            //Send title first
            player.getPacketSender().sendString(frame, parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE);

            //Set text frames
            for (int index = 0; index < parameters.length; index++) {
                player.getPacketSender().sendString(frame + index, (String) parameters[index]);
            }

            //Always hide the middle swords OSRS uses the wide once
            player.getPacketSender().sendInterfaceDisplayState(middle_swords_frame, true);

            //Show wide swords ;)
            player.getPacketSender().sendInterfaceDisplayState(wide_swords_frame, false);

            player.getPacketSender().sendChatboxInterface(frame - 1);
        } else if (type == DialogueType.ITEM_STATEMENT) {
            if (parameters.length == 3) {
                player.getPacketSender().sendString(4884, (String) parameters[1]);
                player.getPacketSender().sendString(4885, (String) parameters[2]);
                player.getPacketSender().sendInterfaceModel(4883, 150, (Item) parameters[0]);
                player.getPacketSender().sendChatboxInterface(4882);
            } else if (parameters.length == 4) {
                player.getPacketSender().sendString(4889, (String) parameters[1]);
                player.getPacketSender().sendString(4890, (String) parameters[2]);
                player.getPacketSender().sendString(4891, (String) parameters[3]);
                player.getPacketSender().sendInterfaceModel(4888, 175, (Item) parameters[0]);
                player.getPacketSender().sendChatboxInterface(4887);
            } else if (parameters.length == 5) {
                player.getPacketSender().sendString(4895, (String) parameters[1]);
                player.getPacketSender().sendString(4896, (String) parameters[2]);
                player.getPacketSender().sendString(4897, (String) parameters[3]);
                player.getPacketSender().sendString(4898, (String) parameters[4]);
                player.getPacketSender().sendInterfaceModel(4894, 175, (Item) parameters[0]);
                player.getPacketSender().sendChatboxInterface(4893);
            } else if (parameters.length == 6) {
                player.getPacketSender().sendString(4902, (String) parameters[1]);
                player.getPacketSender().sendString(4903, (String) parameters[2]);
                player.getPacketSender().sendString(4904, (String) parameters[3]);
                player.getPacketSender().sendString(4905, (String) parameters[4]);
                player.getPacketSender().sendString(4906, (String) parameters[5]);
                player.getPacketSender().sendInterfaceModel(4901, 175, (Item) parameters[0]);
                player.getPacketSender().sendChatboxInterface(4900);
            } else {
                System.err.println("bad args");
            }
        } else if (type == DialogueType.DOUBLE_ITEM_STATEMENT) {
            if (parameters.length == 3) {
                player.getPacketSender().sendString(6232, (String) parameters[2]);
                player.getPacketSender().sendString(6233,"");
                Item firstItem = (Item) parameters[0];
                Item secondItem = (Item) parameters[1];
                player.getPacketSender().sendItemOnInterface(37850, firstItem);
                player.getPacketSender().sendItemOnInterface(37851, secondItem);
                player.getPacketSender().sendInterfaceModel(6235, 125, -1);
                player.getPacketSender().sendInterfaceModel(6236, 125, -1);
                player.getPacketSender().sendChatboxInterface(6231);
            } else if (parameters.length == 4) {
                player.getPacketSender().sendString(6232, (String) parameters[2]);
                player.getPacketSender().sendString(6233, (String) parameters[3]);
                Item firstItem = (Item) parameters[0];
                Item secondItem = (Item) parameters[1];
                player.getPacketSender().sendItemOnInterface(37850, firstItem);
                player.getPacketSender().sendItemOnInterface(37851, secondItem);
                player.getPacketSender().sendInterfaceModel(6235, 125, -1);
                player.getPacketSender().sendInterfaceModel(6236, 125, -1);
                player.getPacketSender().sendChatboxInterface(6231);
            }
        } else if (type == DialogueType.NPC_STATEMENT) {
            if (parameters == null || parameters.length == 0 || parameters.length > 6) {
                throw new IllegalArgumentException("Messages cannot be null and must contain between 1 and 6 elements.");
            }
            NpcDefinition def = World.getWorld().definitions().get(NpcDefinition.class, (Integer) parameters[0]);
            if (parameters.length == 3) {
                player.getPacketSender().sendString(4884, def.name);
                player.getPacketSender().sendString(4885, (String) parameters[2]);
                player.getPacketSender().sendInterfaceAnimation(4883, ((Expression) parameters[1]).getAnimation());
                player.getPacketSender().sendNpcHeadOnInterface((Integer) parameters[0], 4883);
                player.getPacketSender().sendChatboxInterface(4882);
            } else if (parameters.length == 4) {
                player.getPacketSender().sendString(4889, def.name);
                player.getPacketSender().sendString(4890, (String) parameters[2]);
                player.getPacketSender().sendString(4891, (String) parameters[3]);
                player.getPacketSender().sendInterfaceAnimation(4888, ((Expression) parameters[1]).getAnimation());
                player.getPacketSender().sendNpcHeadOnInterface((Integer) parameters[0], 4888);
                player.getPacketSender().sendChatboxInterface(4887);
            } else if (parameters.length == 5) {
                player.getPacketSender().sendString(4895, def.name);
                player.getPacketSender().sendString(4896, (String) parameters[2]);
                player.getPacketSender().sendString(4897, (String) parameters[3]);
                player.getPacketSender().sendString(4898, (String) parameters[4]);
                player.getPacketSender().sendInterfaceAnimation(4894, ((Expression) parameters[1]).getAnimation());
                player.getPacketSender().sendNpcHeadOnInterface((Integer) parameters[0], 4894);
                player.getPacketSender().sendChatboxInterface(4893);
            } else if (parameters.length == 6) {
                player.getPacketSender().sendString(4902, def.name);
                player.getPacketSender().sendString(4903, (String) parameters[2]);
                player.getPacketSender().sendString(4904, (String) parameters[3]);
                player.getPacketSender().sendString(4905, (String) parameters[4]);
                player.getPacketSender().sendString(4906, (String) parameters[5]);
                player.getPacketSender().sendInterfaceAnimation(4901, ((Expression) parameters[1]).getAnimation());
                player.getPacketSender().sendNpcHeadOnInterface((Integer) parameters[0], 4901);
                player.getPacketSender().sendChatboxInterface(4900);
            }
        } else if (type == DialogueType.PLAYER_STATEMENT) {
            if (parameters.length == 2) {
                player.getPacketSender().sendString(970, player.getUsername());
                player.getPacketSender().sendString(971, (String) parameters[1]);
                player.getPacketSender().sendInterfaceAnimation(969, ((Expression) parameters[0]).getAnimation());
                player.getPacketSender().sendPlayerHeadOnInterface(969);
                player.getPacketSender().sendChatboxInterface(968);
            } else if (parameters.length == 3) {
                player.getPacketSender().sendString(975, player.getUsername());
                player.getPacketSender().sendString(976, (String) parameters[1]);
                player.getPacketSender().sendString(977, (String) parameters[2]);
                player.getPacketSender().sendInterfaceAnimation(974, ((Expression) parameters[0]).getAnimation());
                player.getPacketSender().sendPlayerHeadOnInterface(974);
                player.getPacketSender().sendChatboxInterface(973);
            } else if (parameters.length == 4) {
                player.getPacketSender().sendString(981, player.getUsername());
                player.getPacketSender().sendString(982, (String) parameters[1]);
                player.getPacketSender().sendString(983, (String) parameters[2]);
                player.getPacketSender().sendString(984, (String) parameters[3]);
                player.getPacketSender().sendInterfaceAnimation(980, ((Expression) parameters[0]).getAnimation());
                player.getPacketSender().sendPlayerHeadOnInterface(980);
                player.getPacketSender().sendChatboxInterface(979);
            } else if (parameters.length == 5) {
                player.getPacketSender().sendString(988, player.getUsername());
                player.getPacketSender().sendString(989, (String) parameters[1]);
                player.getPacketSender().sendString(990, (String) parameters[2]);
                player.getPacketSender().sendString(991, (String) parameters[3]);
                player.getPacketSender().sendString(992, (String) parameters[4]);
                player.getPacketSender().sendInterfaceAnimation(987, ((Expression) parameters[0]).getAnimation());
                player.getPacketSender().sendPlayerHeadOnInterface(987);
                player.getPacketSender().sendChatboxInterface(986);
            } else {
                throw new IllegalArgumentException("Invalid Arguments");
            }
        } else if (type == DialogueType.STATEMENT) {
            if (parameters == null || parameters.length == 0 || parameters.length > 5) {
                throw new IllegalArgumentException("Messages cannot be null and must contain between 1 and 5 elements.");
            }

            int frame = parameters.length == 1 ? 357 : parameters.length == 2 ? 360 : parameters.length == 3 ? 364 : parameters.length == 4 ? 369 : parameters.length == 5 ? 375 : 0;

            if (frame == 0) {
                return;
            }
            for (int index = 0; index < parameters.length; index++) {
                player.getPacketSender().sendString(frame + index, (String) parameters[index]);
            }
            player.getPacketSender().sendChatboxInterface(frame - 1);
            player.putAttrib(AttributeKey.INTERACTION_OPTION, 1);
        } else {
            throw new InternalError();
        }
    }

    /**
     * Returns if this current phase is active
     *
     * @param phase
     *            The phase to check
     * @return If the current phase matches the provided phase
     */
    public boolean isPhase(int phase) {
        return getPhase() == phase;
    }

    /**
     * Starts the dialogue for the player
     *
     * @param parameters
     *            The parameters to pass on to the dialogue
     */
    protected abstract void start(Object... parameters);

    /**
     * Stops the current dialogue where it is
     */
    protected final void stop() {
        player.getInterfaceManager().closeDialogue();
    }

    /**
     * Gets the current phase of the dialogue
     *
     * @return The current phase of the dialogue
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Sets the current phase of the dialogue
     *
     * @param phase
     *            The current phase of the dialogue
     * @return
     */
    public void setPhase(int phase) {
        this.phase = phase;
    }

    public static void send(Player player, NPC npc, String[] parameters) {
        if (parameters == null) {
            System.err.println("No parameters sent!");
            return;
        }
        System.err.println(parameters.length+" <-- size");
        int startLine = 0;

        if (parameters.length > 3)
            startLine = 4902;
        else if (parameters.length > 2)
            startLine = 4895;
        else if (parameters.length > 1)
            startLine = 4889;
        else
            startLine = 4884;
        System.err.println(startLine+" <-- start line..");
        if (parameters.length > 4) {
            player.getPacketSender().sendInterfaceAnimation(4883, Animation.DEFAULT_RESET_ANIMATION);
            player.getPacketSender().sendNpcHeadOnInterface(npc.getId(), 4883);
            player.getPacketSender().sendChatboxInterface(4882);
        } else if (parameters.length > 3) {
            player.getPacketSender().sendInterfaceAnimation(4888, Animation.DEFAULT_RESET_ANIMATION);
            player.getPacketSender().sendNpcHeadOnInterface(npc.getId(), 4888);
            player.getPacketSender().sendChatboxInterface(4887);
        } else if (parameters.length > 2) {
            player.getPacketSender().sendInterfaceAnimation(4894, Animation.DEFAULT_RESET_ANIMATION);
            player.getPacketSender().sendNpcHeadOnInterface(npc.getId(), 4894);
            player.getPacketSender().sendChatboxInterface(4893);
        } else if (parameters.length > 1) {
            player.getPacketSender().sendInterfaceAnimation(4901, Animation.DEFAULT_RESET_ANIMATION);
            player.getPacketSender().sendNpcHeadOnInterface(npc.getId(), 4901);
            player.getPacketSender().sendChatboxInterface(4900);
        }
        player.getPacketSender().sendString(startLine, npc.getMobName());
        int offset = startLine + 1;
        for (String line : parameters) {
            System.err.println(offset+" <-- offset line..");
            player.getPacketSender().sendString(offset++, line);
        }
    }

}

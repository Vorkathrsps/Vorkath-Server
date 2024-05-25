package com.cryptic.model.inter.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.*;
import com.cryptic.model.items.Item;

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
     * @param value The value to input
     */
    protected void input(int value) {
    }

    /**
     * An overrideable method for inputing a string value
     *
     * @param value The value to input
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
     * @param option The index of the choice, can be between index 1 to 5
     */
    protected void select(int option) {
    }


    protected void sendProduceItem(String title, int total, int lastAmt, int... items) {
        player.activeDialogueProduceItemItemRecord = new DialogueProduceItemItemRecord(title, total,lastAmt, items);
        GameInterface.PRODUCE_ITEM.open(player);
    }

    protected void sendItemDestroy(Item item, String note) {
        player.activeDialogueDestroyItemRecord = new DialogueDestroyItemRecord(item,"Are you sure you want to destroy this item?",note);
        GameInterface.DESTROY_ITEM.open(player);
    }

    protected void sendNpcChat(String title, int npcId, Expression expression, String... chats) {
        sendNpcChat(title, npcId, expression, true, chats);
    }

    protected void sendNpcChat(int npcId, Expression expression, String... chats) {
        sendNpcChat("", npcId, expression, true, chats);
    }

    protected void sendNpcChat(int npcId, Expression expression, boolean continueButton, String... chats) {
        sendNpcChat("", npcId, expression, continueButton, chats);
    }

    protected void sendNpcChat(String title, int npcId, Expression expression, boolean continueButton, String... chats) {
        player.activeNpcDialogue = new DialogueNPCRecord(npcId, title, chats, expression, continueButton);
        GameInterface.DIALOGUE_NPC.open(player);
    }

    protected void sendOption(String title, String... options) {
        player.activeOptionDialogue = new DialogueOptionRecord(title, options);
        GameInterface.DIALOGUE_OPTIONS.open(player);
    }

    protected void sendPlayerChat(String title, Expression expression, String... chats) {
        sendPlayerChat(title, expression, true, chats);
    }

    protected void sendPlayerChat(Expression expression, String... chats) {
        sendPlayerChat("", expression, true, chats);
    }

    protected void sendPlayerChat(Expression expression, boolean continueButton, String... chats) {
        sendPlayerChat("", expression, continueButton, chats);
    }

    protected void sendPlayerChat(String title, Expression expression, boolean continueButton, String... chats) {
        player.activePlayerDialogue = new DialoguePlayerRecord(title, chats, expression, continueButton);
        GameInterface.DIALOGUE_PLAYER.open(player);
    }

    protected void sendStatement(String... chats) {
        sendStatement(true, chats);
    }

    protected void sendStatement(boolean continueButton, String... chats) {
        player.activeStatementRecord = new DialogueStatementRecord(chats, continueButton);
        GameInterface.DIALOGUE_STATEMENT.open(player);
    }


    protected void sendItemStatement(Item item, String... chats) {
        sendItemStatement(item,true,chats);
    }

    protected void sendItemStatement(Item item, boolean continueButton,String... chats) {
        player.activeSingleItemRecord = new DialogueSingleItemRecord(item,chats,continueButton);
        GameInterface.DIALOGUE_ITEM_SINGLE.open(player);
    }

    protected void sendItemStatement(Item firstItem, Item secondItem,String... chats) {
        sendItemStatement(firstItem,secondItem,true,chats);
    }

    protected void sendItemStatement(Item firstItem,Item secondItem, boolean continueButton,String... chats) {
        player.activeDoubleItemRecord = new DialogueDoubleItemRecord(firstItem,secondItem,chats,continueButton);
        GameInterface.DIALOGUE_ITEM_DOUBLE.open(player);
    }

    /**
     * Returns if this current phase is active
     *
     * @param phase The phase to check
     * @return If the current phase matches the provided phase
     */
    public boolean isPhase(int phase) {
        return getPhase() == phase;
    }

    /**
     * Starts the dialogue for the player
     *
     * @param parameters The parameters to pass on to the dialogue
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
     * @param phase The current phase of the dialogue
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
        System.err.println(parameters.length + " <-- size");
        int startLine = 0;

        if (parameters.length > 3)
            startLine = 4902;
        else if (parameters.length > 2)
            startLine = 4895;
        else if (parameters.length > 1)
            startLine = 4889;
        else
            startLine = 4884;
        System.err.println(startLine + " <-- start line..");
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
            System.err.println(offset + " <-- offset line..");
            player.getPacketSender().sendString(offset++, line);
        }
    }

    public void begin(Player p) {
        p.getDialogueManager().start(this);
    }
}

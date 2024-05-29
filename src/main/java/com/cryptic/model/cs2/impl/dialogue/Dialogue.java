package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.InterfacePosition;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.cs2.impl.dialogue.information.DialogueInformation;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.items.Item;
import lombok.Getter;
import lombok.Setter;

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
     * -- GETTER --
     *  Gets the current phase of the dialogue
     *
     *
     * -- SETTER --
     *  Sets the current phase of the dialogue
     *
     @return The current phase of the dialogue
      * @param phase The current phase of the dialogue
      *

     */
    @Setter
    @Getter
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
        player.getDialogueManager().setRecord(DialogueInformation.buildProduceItem(title, total,lastAmt, items));
        GameInterface.PRODUCE_ITEM.open(player);
    }

    protected void sendItemDestroy(Item item, String note) {
        player.getDialogueManager().setRecord(DialogueInformation.buildDestroyItem(item, "Are you sure you want to destroy this item?", note));
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
        player.getDialogueManager().setRecord(DialogueInformation.buildNpc(npcId, title, chats, expression, continueButton));
        GameInterface.DIALOGUE_NPC.open(player);
    }

    protected void sendOption(String title, String... options) {
        player.getDialogueManager().setRecord(DialogueInformation.buildOptions(title, options));
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
        player.getDialogueManager().setRecord(DialogueInformation.buildPlayer(title, chats, expression, continueButton));
        GameInterface.DIALOGUE_PLAYER.open(player);
    }

    protected void sendStatement(String... chats) {
        sendStatement(true, chats);
    }

    protected void sendStatement(boolean continueButton, String... chats) {
        player.getDialogueManager().setRecord(DialogueInformation.buildStatement(chats, continueButton));
        GameInterface.DIALOGUE_STATEMENT.open(player);
    }

    protected void sendItemStatement(Item item, String... chats) {
        sendItemStatement(item,true,chats);
    }

    protected void sendItemStatement(Item item, boolean continueButton,String... chats) {
        player.getDialogueManager().setRecord(DialogueInformation.buildSingleItem(item, chats, continueButton));
        GameInterface.DIALOGUE_ITEM_SINGLE.open(player);
    }

    protected void sendItemStatement(Item firstItem, Item secondItem,String... chats) {
        sendItemStatement(firstItem,secondItem,true,chats);
    }

    protected void sendItemStatement(Item firstItem,Item secondItem, boolean continueButton,String... chats) {
        player.getDialogueManager().setRecord(DialogueInformation.buildDoubleItem(firstItem, secondItem, chats, continueButton));
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
        player.getDialogueManager().setRecord(null);
        player.interfaces.closeInterface(InterfacePosition.DIALOGUE);
        player.getInterfaceManager().closeDialogue();
    }


    public void begin(Player p) {
        p.getDialogueManager().start(this);
    }
}

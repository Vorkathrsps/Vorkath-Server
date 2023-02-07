package com.aelous.model.inter.dialogue;

/**
 * Represents a type of dialogue.
 *
 * @author relex lawl
 */
public enum DialogueType {

    /*
     * Sends a variable options for a player to choose.
     */
    OPTION,

    /*
     * Sends a statement.
     */
    STATEMENT,

    /*
     * Sends a dialogue said by an npc.
     */
    NPC_STATEMENT,

    /*
     * Sends a dialogue with an item model next to it.
     */
    ITEM_STATEMENT,

    /*
     * Sends a dialogue with an item model next to it.
     */
    DOUBLE_ITEM_STATEMENT,

    /*
     * Sends a dialogue said by a player.
     */
    PLAYER_STATEMENT;
}

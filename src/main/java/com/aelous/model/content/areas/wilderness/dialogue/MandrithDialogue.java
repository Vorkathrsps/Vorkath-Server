package com.aelous.model.content.areas.wilderness.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.MANDRITH;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 07, 2020
 */
public class MandrithDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.PLAYER_STATEMENT,Expression.HAPPY, "Who are you and what is this place?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.NODDING_ONE, "My name is Mandrith.");
            setPhase(1);
        } else if (getPhase() == 1) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.ANXIOUS, "I collect valuable resources and pawn off access to them", "to foolish adventurers, like yourself.");
            setPhase(2);
        } else if (getPhase() == 2) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.NODDING_TWO, "You should take a look inside my arena. Theres an", "abundance of valuable resources inside.");
            setPhase(3);
        } else if (getPhase() == 3) {
            send(DialogueType.PLAYER_STATEMENT,Expression.NODDING_ONE, "And I can take whatever I want?");
            setPhase(4);
        } else if (getPhase() == 4) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.HAPPY, "It's all yours. All I ask is you pay the upfront fee.");
            setPhase(5);
        } else if (getPhase() == 5) {
            send(DialogueType.PLAYER_STATEMENT,Expression.HAPPY, "Will others be able to kill me inside?");
            setPhase(6);
        } else if (getPhase() == 6) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.HAPPY, "Yes. These walls will only hold them back for so long.");
            setPhase(7);
        } else if (getPhase() == 7) {
            send(DialogueType.PLAYER_STATEMENT,Expression.NODDING_ONE, "You'll stop them though, right?");
            setPhase(8);
        } else if (getPhase() == 8) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.NODDING_FIVE, "Haha! For the right price, I won't deny any one access", "to my arena. Even if their intention is to kill you.");
            setPhase(9);
        } else if (getPhase() == 9) {
            send(DialogueType.PLAYER_STATEMENT,Expression.NODDING_FOUR, "Right...");
            setPhase(10);
        } else if (getPhase() == 10) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.CALM_TALK, "My arena holds many treasures that I've acquired at", "great expense, adventurer. Their bounty can come at a", "price.");
            setPhase(11);
        } else if (getPhase() == 11) {
            send(DialogueType.NPC_STATEMENT,MANDRITH, Expression.CALM_TALK, "One day, adventurer, I will boast ownership of a much", "larger, much more dangerous arena than this. Take", "advantage of this offer while it lasts.");
            setPhase(12);
        } else if (getPhase() == 12) {
            stop();
        }
    }
}

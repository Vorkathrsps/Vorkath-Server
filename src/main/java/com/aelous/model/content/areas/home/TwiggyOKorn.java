package com.aelous.model.content.areas.home;

import com.aelous.GameConstants;
import com.aelous.model.World;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.TWIGGY_OKORN;

/**
 * @author Patrick van Elderen | April, 23, 2021, 13:03
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class TwiggyOKorn extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Hello, what are you?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (isPhase(0)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "Hi there, my name is Twiggy O'Korn and I'm the Master", "of Achievements here at " + GameConstants.SERVER_NAME + ".");
            setPhase(1);
        } else if (isPhase(1)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "What is an achievement?", "Do you sell anything?", "Can you tell me about your cape?");
            setPhase(2);
        } else if (isPhase(3)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "They're an ever growing set of challenges for you to", "fulfill which I often reward for completing.");
            setPhase(4);
        } else if (isPhase(4)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "These challenges involve doing all sorts of random stuff,", "varying in different difficulties. Some might have you", "jumping from roof to roof, while another might", "just have you cutting down trees.");
            setPhase(5);
        } else if (isPhase(5)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Okay.. how can I view the challenges?");
            setPhase(6);
        } else if (isPhase(6)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "To view the achievements, simply head to your quest tab", "and click on the green icon near the top. You'll see", "they're all sorted according to difficulty.");
            setPhase(7);
        } else if (isPhase(7)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "Achievements that've yet to be started will be in <col=FF0000>red</col>,", "in progress will be <col=ffff00>yellow</col>, and completed will be", "<col=00FF00>green</col>.");
            setPhase(8);
        } else if (isPhase(8)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "When you start or complete an achievement,", "you'll receive a notification in your chat box.", "If it's rewarded, come find me!");
            setPhase(9);
        } else if (isPhase(9)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Great, thanks!");
            setPhase(10);
        } else if (isPhase(10)) {
            stop();
        } else if (isPhase(11)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "Yes, but only if you've completed the corresponding", "achievement. You can claim your reward in my shop");
            setPhase(12);
        } else if (isPhase(12)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "But if you lose it, you'll have to purchase it back!");
            setPhase(13);
        } else if (isPhase(13)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "Would you like to view the achievement rewards shop?");
            setPhase(14);
        } else if (isPhase(14)) {
            send(DialogueType.OPTION, "View the Achievement Rewards?", "Yes", "No");
            setPhase(15);
        } else if (isPhase(16)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "Thank you! This is the achievement cape.");
            setPhase(17);
        } else if (isPhase(17)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "How would I go about getting one for myself?");
            setPhase(18);
        } else if (isPhase(18)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "You'd have to complete every achievement! Once you've", "managed that, I'll be able to give you a cape of your", "very own.");
            setPhase(19);
        } else if (isPhase(19)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I see.. so I can't just wear it anyways?");
            setPhase(20);
        } else if (isPhase(20)) {
            send(DialogueType.NPC_STATEMENT, TWIGGY_OKORN, Expression.HAPPY, "That is correct!");
            setPhase(21);
        } else if (isPhase(21)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Awww..");
            setPhase(10);
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(2)) {
            if (option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "What is an achievement?");
                setPhase(3);
            }
            if (option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Do you sell anything?");
                setPhase(11);
            }
            if (option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I really like your cape!");
                setPhase(16);
            }
        } else if(isPhase(15)) {
            if (option == 1) {
                stop();
                World.getWorld().shop(41).open(player);
            }
            if (option == 2) {
                stop();
            }
        }
    }
}

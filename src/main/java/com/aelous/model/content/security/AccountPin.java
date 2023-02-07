package com.aelous.model.content.security;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.AccountPinFrozenTask;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * @author Ynneh | 06/04/2022 - 17:41
 * <https://github.com/drhenny>
 */
public class AccountPin {

    public static void prompt(Player player) {
        int ticks = player.<Integer>getAttribOr(ACCOUNT_PIN_FREEZE_TICKS, 0);
        int convertTicksToSeconds = Utils.getSeconds(ticks);

        if (player.<Integer>getAttribOr(ACCOUNT_PIN_FREEZE_TICKS, 0) > 0) {
            DialogueManager.sendStatement(player, "Try again in " + Utils.convertSecondsToDuration(convertTicksToSeconds, false) + ".");
            return;
        }
        player.getInterfaceManager().closeDialogue();
        player.setAmountScript("", new InputScript() {

            @Override
            public boolean handle(Object value) {
                int input = (Integer) value;
                String pinToString = Integer.toString(input);

                if(pinToString.equalsIgnoreCase(Integer.toString(player.<Integer>getAttribOr(ACCOUNT_PIN,0)))) {
                    player.message(Color.GREEN.wrap("You have entered the correct account pin."));
                    player.putAttrib(ASK_FOR_ACCOUNT_PIN,false);
                    player.putAttrib(ACCOUNT_PIN_ATTEMPTS_LEFT,5);
                } else {
                    //Invalid pin, ask again
                    player.putAttrib(ASK_FOR_ACCOUNT_PIN,true);
                    int attemptsLeft = player.<Integer>getAttribOr(ACCOUNT_PIN_ATTEMPTS_LEFT,5) - 1;
                    player.putAttrib(ACCOUNT_PIN_ATTEMPTS_LEFT, attemptsLeft);

                    if(attemptsLeft == 0) {
                        //set waiting task
                        player.putAttrib(ACCOUNT_PIN_FREEZE_TICKS,600);
                        TaskManager.submit(new AccountPinFrozenTask(player));
                        return false;
                    }
                }
                return true;
            }
        });
    }

}

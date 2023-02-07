package com.aelous.core.task.impl;

import com.aelous.model.content.security.AccountPin;
import com.aelous.core.task.Task;
import com.aelous.model.entity.player.Player;

import static com.aelous.model.entity.attributes.AttributeKey.ACCOUNT_PIN_ATTEMPTS_LEFT;
import static com.aelous.model.entity.attributes.AttributeKey.ACCOUNT_PIN_FREEZE_TICKS;

/**
 * @author Patrick van Elderen | May, 01, 2021, 15:34
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class AccountPinFrozenTask extends Task {

    private final Player player;

    public AccountPinFrozenTask(Player player) {
        super("AccountPinFrozenTask",1, player,false);
        this.player = player;
    }

    @Override
    protected void execute() {
        int ticksLeft = player.getAttribOr(ACCOUNT_PIN_FREEZE_TICKS, 0);

        if(ticksLeft > 0) {
            ticksLeft--;

            player.putAttrib(ACCOUNT_PIN_FREEZE_TICKS, ticksLeft);

            if(ticksLeft == 0) {
                player.putAttrib(ACCOUNT_PIN_ATTEMPTS_LEFT,5);
                AccountPin.prompt(player);
                stop();
            }
        }
    }
}

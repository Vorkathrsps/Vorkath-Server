package com.aelous.core.task.impl;

import com.aelous.core.task.Task;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Color;

import static com.aelous.model.entity.attributes.AttributeKey.DOUBLE_EXP_TICKS;

/**
 * @author Patrick van Elderen | April, 21, 2021, 15:10
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DoubleExpTask extends Task {

    private final Player player;

    public DoubleExpTask(Player player) {
        super("DoubleExpTask", 1, player, false);
        this.player = player;
    }

    @Override
    protected void execute() {
        int ticksLeft = player.getAttribOr(DOUBLE_EXP_TICKS, 0);

        if(ticksLeft > 0) {
            ticksLeft--;

            player.putAttrib(DOUBLE_EXP_TICKS, ticksLeft);

            if(ticksLeft == 0) {
                player.message(Color.RED.tag()+"Your double exp bonus has ended.");
                stop();
            }
        }
    }
}

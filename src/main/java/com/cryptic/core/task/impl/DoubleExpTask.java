package com.cryptic.core.task.impl;

import com.cryptic.core.task.Task;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;

import static com.cryptic.model.entity.attributes.AttributeKey.DOUBLE_EXP_TICKS;

/**
 * @author Origin | April, 21, 2021, 15:10
 * 
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

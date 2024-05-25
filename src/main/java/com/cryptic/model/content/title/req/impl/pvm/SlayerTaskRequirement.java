package com.cryptic.model.content.title.req.impl.pvm;

import com.cryptic.model.content.title.req.TitleRequirement;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

/**
 * Created by Kaleem on 25/03/2018.
 */
public class SlayerTaskRequirement extends TitleRequirement {

    private final int tasks;

    public SlayerTaskRequirement(int tasks) {
        super("Complete " + tasks + " Slayer<br>tasks");
        this.tasks = tasks;
    }

    @Override
    public boolean satisfies(Player player) {
        int slayerTasksCompleted = player.getAttribOr(AttributeKey.COMPLETED_SLAYER_TASKS, 0);

        return slayerTasksCompleted >= tasks;
    }
}

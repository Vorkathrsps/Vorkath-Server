package com.aelous.model.content.title.req.impl.other;

import com.aelous.model.content.title.req.TitleRequirement;
import com.aelous.model.entity.player.Player;

/**
 * Created by Kaleem on 25/03/2018.
 */
public class AllAchievementsRequirement extends TitleRequirement {

    public AllAchievementsRequirement() {
        super("Complete all <br>achievements");
    }

    @Override
    public boolean satisfies(Player player) {
        return player.completedAllAchievements();
    }
}

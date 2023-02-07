package com.aelous.model.content.title.req;

import com.aelous.model.entity.player.Player;

/**
 * Created by Kaleem on 25/03/2018.
 */
public abstract class TitleRequirement {

    private final String requirementName;

    public TitleRequirement(String requirementName) {
        this.requirementName = requirementName;
    }

    public abstract boolean satisfies(Player player);

    public String getRequirementName() {
        return requirementName;
    }
}

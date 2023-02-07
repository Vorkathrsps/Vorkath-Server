package com.aelous.model.content.title;

import com.aelous.model.content.title.req.TitleRequirement;
import com.aelous.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaleem on 25/03/2018.
 */
public class AvailableTitle {

    public String name;

    private final TitleCategory category;

    private final List<TitleRequirement> requirementList = new ArrayList<>();

    public AvailableTitle(String name, TitleCategory category) {
        this.name = name;
        this.category = category;
    }

    public AvailableTitle addRequirement(TitleRequirement requirement) {
        requirementList.add(requirement);
        return this;
    }

    public boolean satisfies(Player player) {
        return requirementList.stream().allMatch(requirement -> requirement.satisfies(player));
    }

    public String getName() {
        return name;
    }

    public TitleCategory getCategory() {
        return category;
    }

    public List<TitleRequirement> getRequirementList() {
        return requirementList;
    }
}

package com.aelous.model.content.areas.wilderness.content.activity.impl;

import com.aelous.model.content.areas.wilderness.content.activity.WildernessActivity;
import com.aelous.model.content.areas.wilderness.content.activity.WildernessLocations;
import com.aelous.model.entity.player.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MysteriousActivity extends WildernessActivity {

    private WildernessLocations wildernessLocation;

    @Override
    public String description() {
        return "Mysterious Wilderness";
    }

    @Override
    public String announcement() {
        return "<img=2011>" + wildernessLocation.location() +" is the new Wilderness Hotspot for the next hour!</col>";
    }

    /**
     * This method saves all the {@link WildernessLocations} in a list.
     * After that it filters the list so we can't get "Edgeville" as location.
     *
     * returns a location that isn't Edgeville.
     */
    @Override
    public void onCreate() {
        List<WildernessLocations> wildernessLocations = Arrays.stream(WildernessLocations.values()).filter(locations -> locations != WildernessLocations.EDGEVILLE).collect(Collectors.toList());
        Collections.shuffle(wildernessLocations);
        wildernessLocation = wildernessLocations.get(0);
        wildernessLocations.clear();
    }

    @Override
    public void process() {

    }

    /**
     * When the activity has been finished clear the location.
     */
    @Override
    public void onFinish() {
        wildernessLocation = null;
    }

    /**
     * Each and every activity lasts for 60 minutes.
     * @return The time duration in this case 1 hour.
     */
    @Override
    public long activityTime() {
        return TimeUnit.MINUTES.toMillis(60);
    }

    /**
     * This method checks if the player meets the requirements in order to be awarded with a activity casket.
     *
     * @param player The player
     * @return true if we can receive a reward false otherwise.
     */
    @Override
    public boolean canReward(Player player) {
        return wildernessLocation.isInArea(player);
    }
}

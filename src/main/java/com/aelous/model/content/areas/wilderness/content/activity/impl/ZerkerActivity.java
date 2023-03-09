package com.aelous.model.content.areas.wilderness.content.activity.impl;

import com.aelous.model.content.areas.wilderness.content.activity.WildernessActivity;
import com.aelous.model.content.areas.wilderness.content.activity.WildernessLocations;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrick van Elderen | November, 21, 2020, 10:15
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ZerkerActivity extends WildernessActivity {

    private WildernessLocations wildernessLocation;

    @Override
    public String description() {
        return "Zerker Pking";
    }

    @Override
    public String announcement() {
        return "Zerker Pking is the new wilderness activity for one hour! ("+wildernessLocation.location()+")";
    }

    @Override
    public void onCreate() {
        List<WildernessLocations> wildernessLocations = new ArrayList<>(Arrays.asList(WildernessLocations.values()));
        Collections.shuffle(wildernessLocations);
        wildernessLocation = wildernessLocations.get(0);
        wildernessLocations.clear();
    }

    @Override
    public void process() {
    }

    @Override
    public void onFinish() {
        wildernessLocation = null;
    }

    @Override
    public long activityTime() {
        return TimeUnit.MINUTES.toMillis(60);
    }

    @Override
    public boolean canReward(Player player) {
        int combatLevel = player.getSkills().combatLevel();
        int defenceLevel = player.getSkills().level(Skills.DEFENCE);
        boolean isZerker = defenceLevel == 45 && combatLevel >= 95;
        return wildernessLocation.isInArea(player) && isZerker;
    }
}

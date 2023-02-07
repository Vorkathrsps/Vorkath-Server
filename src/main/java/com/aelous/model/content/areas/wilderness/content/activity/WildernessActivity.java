package com.aelous.model.content.areas.wilderness.content.activity;

import com.aelous.model.entity.player.Player;

/**
 * @author Zerikoth
 * @Since september 24, 2020
 */
public abstract class WildernessActivity {

    /**
     * The description of the wilderness activity. This description is sent to
     * the player's information tab
     *
     * @return A {@code String} {@code Object}
     */
    public abstract String description();

    /**
     * The announcement that will display throughout the server when the
     * wilderness activity has been created
     *
     * @return A {@code String} {@code Object}
     */
    public abstract String announcement();

    /**
     * What the wilderness activity will perform when it is created
     */
    public abstract void onCreate();

    /**
     * If the wilderness activity overridse this method, it will have its own
     * processing system
     */
    public abstract void process();

    /**
     * What the wilderness activity should do when it's time is up
     */
    public abstract void onFinish();

    /**
     * The time that the activity was started at
     */
    private long activityInitializeTime;

    /**
     * The amount of time the activity exists for
     *
     * @return A {@code Long} {@code Object}
     */
    public abstract long activityTime();

    /**
     * If the player receives any bonus because of their activity in this
     * wilderness activity.
     *
     * @param player
     *            The player
     */
    public abstract boolean canReward(Player player);

    /**
     * @return the activityInitializeTime
     */
    long getActivityInitializeTime() {
        return activityInitializeTime;
    }

    /**
     * @param activityInitializeTime
     *            the activityInitializeTime to set
     */
    void setActivityInitializeTime(long activityInitializeTime) {
        this.activityInitializeTime = activityInitializeTime;
    }

}

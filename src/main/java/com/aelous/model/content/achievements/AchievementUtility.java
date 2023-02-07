package com.aelous.model.content.achievements;

import com.aelous.GameServer;
import com.aelous.model.items.Item;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.aelous.utility.ItemIdentifiers.COINS_995;

/**
 * @author PVE | Zerikoth
 * A utility class for our achievements system.
 */
public class AchievementUtility {

    public static final int ACHIEVEMENT_NAME_ID = 39405;
    public static final int ACHIEVEMENT_PROGRESS_ID = 39416;
    public static final int ACHIEVEMENT_DESCRIPTION_ID = 39417;
    public static final int CONTAINER_ID = 39414;
    public static final int PROGRESS_BAR_CHILD = 39415;
    public static final int ACHIEVEMENT_SCROLL_BAR = 39430;
    public static final int ACHIEVEMENTS_LIST_START_ID = 39431;
    public static final int ACHIEVEMENTS_COMPLETED = 39404;
    public static final int REWARD_STRING = 39418;
    public static final String RED = "<col=FF0000>";
    public static final String ORANGE = "<col=FF9900>";
    public static final String GREEN = "<col=00FF00>";

    public static final Item DEFAULT_REWARD = GameServer.properties().pvpMode ? new Item(BLOOD_MONEY, 1_000) : new Item(COINS_995, 2_500_000);
}

package com.cryptic.model.content.members;

import com.cryptic.GameConstants;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;

import java.util.Calendar;

import static com.cryptic.utility.CustomItemIdentifiers.*;

public class MemberFeatures {

    /**
     * Every first day of a new month sponsor players receive a free legendary mystery box and key of drops.
     * @param player The sponsor player
     */
    public static void checkForMonthlySponsorRewards(Player player) {
        if(player.<Boolean>getAttribOr(AttributeKey.RECEIVED_MONTHLY_SPONSOR_REWARDS,false)) {
            return;
        }

        if(World.getWorld().getCalendar().get(Calendar.DAY_OF_MONTH) == 1) {
            player.putAttrib(AttributeKey.RECEIVED_MONTHLY_SPONSOR_REWARDS, true);
            player.inventory().addOrBank(new Item(DONATOR_MYSTERY_BOX), new Item(LEGENDARY_MYSTERY_BOX));
            player.message(Color.PURPLE.wrap("You have received your monthly sponsor rewards. Thank you for supporting "+ GameConstants.SERVER_NAME+"!"));
        }
    }
}

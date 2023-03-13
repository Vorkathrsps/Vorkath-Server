package com.aelous.model.content.skill.impl.agility;

import com.aelous.GameServer;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

import java.util.List;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 07, 2020
 */
public class MarksOfGrace {

    private static final int MARK_LIFETIME = 10 * 60 * 1000; // 10 minutes

    public static void trySpawn(Player player, List<Tile> tiles, int rarity, int threshold) {
        // Base odds depend on the player's game mode
        int odds = switch (player.getGameMode()) {
            case TRAINED_ACCOUNT, DARK_LORD -> 3;
        };

        if(!GameServer.properties().pvpMode) {
            odds = 1;
        }

        // Donator perks grant extra odds
        switch (player.getMemberRights()) {
            case ZENYTE_MEMBER -> odds += 17;
            case ONYX_MEMBER -> odds += 15;
            case DRAGONSTONE_MEMBER -> odds += 10;
            case DIAMOND_MEMBER -> odds += 8;
            case EMERALD_MEMBER -> odds += 6;
            case SAPPHIRE_MEMBER -> odds += 4;
            case RUBY_MEMBER -> odds += 2;
        }

        if (player.getSkills().level(Skills.AGILITY) > threshold + 20) {
            odds = (int) Math.max(1, (odds * 0.7)); // You don't want to end up in this :)
        }

        // Check for the odds. :)
        if (Utils.rollDie(rarity, odds)) {

            int MARK_OF_GRACE = 11849;
            GroundItem item = new GroundItem(new Item(MARK_OF_GRACE), Utils.randomElement(tiles), player);
            item.setTimer(MARK_LIFETIME);

            GroundItemHandler.createGroundItem(item);
        }
    }
}

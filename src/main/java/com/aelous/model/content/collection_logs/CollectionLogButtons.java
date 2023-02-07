package com.aelous.model.content.collection_logs;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;

import java.util.HashMap;

/**
 * @author PVE
 * @Since juli 15, 2020
 */
public class CollectionLogButtons {

    private static final HashMap<Integer, Collection> BOSS_LOG_BUTTONS = new HashMap<>();
    private static final HashMap<Integer, Collection> MYSTERY_BOX_LOG_BUTTONS = new HashMap<>();
    private static final HashMap<Integer, Collection> KEYS_LOG_BUTTONS = new HashMap<>();
    private static final HashMap<Integer, Collection> OTHER_LOG_BUTTONS = new HashMap<>();

    static {
        int button_index;//The start button
        button_index = 61051;
        //We must store the button twice, using prefix increment operator, once for the background sprite.
        for (final Collection collection : Collection.getAsList(LogType.BOSSES)) {
            BOSS_LOG_BUTTONS.put(button_index++, collection);
        }
        button_index = 61051;
        for (final Collection collection : Collection.getAsList(LogType.MYSTERY_BOX)) {
            MYSTERY_BOX_LOG_BUTTONS.put(button_index++, collection);
        }
        button_index = 61051;
        for (final Collection collection : Collection.getAsList(LogType.KEYS)) {
            KEYS_LOG_BUTTONS.put(button_index++, collection);
        }
        button_index = 61051;
        for (final Collection collection : Collection.getAsList(LogType.OTHER)) {
            OTHER_LOG_BUTTONS.put(button_index++, collection);
        }
    }

    public static boolean onButtonClick(Player player, int button) {
        if(button == 61003) {
            player.getCollectionLog().open(LogType.BOSSES);
            player.getPacketSender().sendConfig(1106, 0);
            return true;
        }

        if(button == 61004) {
            player.getCollectionLog().open(LogType.MYSTERY_BOX);
            player.getPacketSender().sendConfig(1106, 1);
            return true;
        }

        if(button == 61005) {
            player.getCollectionLog().open(LogType.KEYS);
            player.getPacketSender().sendConfig(1106, 2);
            return true;
        }

        if(button == 61006) {
            player.getCollectionLog().open(LogType.OTHER);
            player.getPacketSender().sendConfig(1106, 3);
            return true;
        }

        if(button == 61020) {
            var collection = player.<Collection>getAttribOr(AttributeKey.VIEWING_COLLECTION_LOG,null);
            player.getCollectionLog().claimReward(collection);
            return true;
        }

        if (player.getAttribOr(AttributeKey.COLLECTION_LOG_OPEN, null) == LogType.BOSSES && BOSS_LOG_BUTTONS.containsKey(button)) {
            player.getCollectionLog().sendInterface(BOSS_LOG_BUTTONS.get(button));
            return true;
        } else if (player.getAttribOr(AttributeKey.COLLECTION_LOG_OPEN, null) == LogType.MYSTERY_BOX && MYSTERY_BOX_LOG_BUTTONS.containsKey(button)) {
            player.getCollectionLog().sendInterface(MYSTERY_BOX_LOG_BUTTONS.get(button));
            return true;
        } else if (player.getAttribOr(AttributeKey.COLLECTION_LOG_OPEN, null) == LogType.KEYS && KEYS_LOG_BUTTONS.containsKey(button)) {
            player.getCollectionLog().sendInterface(KEYS_LOG_BUTTONS.get(button));
            return true;
        } else if (player.getAttribOr(AttributeKey.COLLECTION_LOG_OPEN, null) == LogType.OTHER && OTHER_LOG_BUTTONS.containsKey(button)) {
            player.getCollectionLog().sendInterface(OTHER_LOG_BUTTONS.get(button));
            return true;
        }
        return false;
    }

}

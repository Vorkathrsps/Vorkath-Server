package com.cryptic.model.content.packet_actions.interactions.items;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.util.Unbox.box;

/**
 * @author Origin
 * mei 05, 2020
 */
public class ItemOnObject {

    private static final Logger logger = LogManager.getLogger(ItemOnObject.class);

    public static void itemOnObject(Player player, Item item, GameObject object) {
        //If the object doesn't exist, we probably shouldn't do anything about it.
        if (object == null) {
            return;
        }

        if (object.definition() == null) {
            logger.error("ObjectDefinition for object {} is null for player " + player.toString() + ".", box(object.getId()));
            return;
        }

        if (PacketInteractionManager.checkItemOnObjectInteraction(player, item, object)) {
            return;
        }

        player.getFarming().handleItemOnObject(item.getId(), object.tile().x, object.tile().y);

        player.message("Nothing interesting happens.");
    }
}

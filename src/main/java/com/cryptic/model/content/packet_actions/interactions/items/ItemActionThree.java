package com.cryptic.model.content.packet_actions.interactions.items;

import com.cryptic.model.content.items.RockCake;
import com.cryptic.model.content.items.teleport.ArdyCape;
import com.cryptic.model.content.skill.impl.slayer.content.SlayerHelm;
import com.cryptic.model.content.skill.impl.slayer.content.SlayerRing;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin
 * juni 04, 2020
 */
public class ItemActionThree {

    public static void click(Player player, Item item) {
        int id = item.getId();

        if (PacketInteractionManager.checkItemInteraction(player, item, 3)) {
            return;
        }

        ArdyCape.onItemOption3(player, item);

        if (player.getRunePouch().quickFill(item.getId())) {
            return;
        }

        if (SlayerRing.onItemOption3(player, item)) {
            return;
        }

        if (RockCake.onItemOption3(player, item)) {
            return;
        }

        if (SlayerHelm.onItemOption3(player, item)) {
            return;
        }

        switch (id) {
            case LOOTING_BAG, LOOTING_BAG_22586 -> player.getLootingBag().depositWidget();
        }
    }
}

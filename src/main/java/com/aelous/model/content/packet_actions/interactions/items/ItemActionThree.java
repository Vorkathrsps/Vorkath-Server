package com.aelous.model.content.packet_actions.interactions.items;

import com.aelous.model.content.items.RockCake;
import com.aelous.model.content.items.teleport.ArdyCape;
import com.aelous.model.content.skill.impl.slayer.content.SlayerHelm;
import com.aelous.model.content.skill.impl.slayer.content.SlayerRing;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
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

package com.cryptic.model.content.packet_actions.interactions.items;

import com.cryptic.model.content.consumables.potions.Potions;
import com.cryptic.model.content.items.combinations.EldritchNightmareStaff;
import com.cryptic.model.content.items.combinations.HarmonisedNightmareStaff;
import com.cryptic.model.content.items.combinations.VolatileNightmareStaff;
import com.cryptic.model.content.items.teleport.ArdyCape;
import com.cryptic.model.content.skill.impl.slayer.content.SlayerRing;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin
 * mei 08, 2020
 */
public class ItemActionTwo {

    public static void click(Player player, Item item) {
        int id = item.getId();

        if (PacketInteractionManager.checkItemInteraction(player, item, 2)) {
            return;
        }

        ArdyCape.onItemOption2(player, item);

        if(Potions.onItemOption2(player, item)) {
            return;
        }

        if (VolatileNightmareStaff.dismantle(player, item)) {
            return;
        }

        if (EldritchNightmareStaff.dismantle(player, item)) {
            return;
        }

        if (HarmonisedNightmareStaff.dismantle(player, item)) {
            return;
        }

        if(SlayerRing.onItemOption2(player, item)) {
            return;
        }

        switch (id) {
            case RUNE_POUCH -> player.getRunePouch().empty();
            case LOOTING_BAG, LOOTING_BAG_22586 -> player.getLootingBag().setSettings();
        }
    }
}

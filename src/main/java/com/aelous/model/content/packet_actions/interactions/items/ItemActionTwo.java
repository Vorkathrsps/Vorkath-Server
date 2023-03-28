package com.aelous.model.content.packet_actions.interactions.items;

import com.aelous.model.content.consumables.potions.Potions;
import com.aelous.model.content.items.combine.EldritchNightmareStaff;
import com.aelous.model.content.items.combine.HarmonisedNightmareStaff;
import com.aelous.model.content.items.combine.VolatileNightmareStaff;
import com.aelous.model.content.items.teleport.ArdyCape;
import com.aelous.model.content.sigils.SigilHandler;
import com.aelous.model.content.skill.impl.slayer.content.SlayerRing;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
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

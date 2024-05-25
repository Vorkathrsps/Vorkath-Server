package com.cryptic.model.content.packet_actions.interactions.items;

import com.cryptic.model.content.consumables.potions.Potions;
import com.cryptic.model.content.items.combinations.EldritchNightmareStaff;
import com.cryptic.model.content.items.combinations.HarmonisedNightmareStaff;
import com.cryptic.model.content.items.combinations.VolatileNightmareStaff;
import com.cryptic.model.content.items.teleport.ArdyCape;
import com.cryptic.model.content.skill.impl.slayer.content.SlayerRing;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import dev.openrune.cache.CacheManager;
import dev.openrune.cache.filestore.definition.data.ItemType;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin
 * mei 08, 2020
 */
public class ItemActionFour {

    public static void click(Player player, Item item) {
        final int id = item.getId();
        final int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, -1);
        if (slot == -1) {
            return;
        }

        ItemType definition = CacheManager.INSTANCE.getItem(id);
        if (definition.getInterfaceOptions().get(3) == null) {
            return;
        }

        // TODO option should be 4, but a lot of things rely on it being 2 currently and i cba
        //      josh wuz her
        if (PacketInteractionManager.checkItemInteraction(player, item, 2)) {
            return;
        }

        ArdyCape.onItemOption4(player, item);

        if(Potions.onItemOption4(player, item)) {
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

        if(SlayerRing.onItemOption4(player, item)) {
            return;
        }

        switch (id) {
            case RUNE_POUCH -> player.getRunePouch().empty();
            case LOOTING_BAG, LOOTING_BAG_22586 -> player.getLootingBag().setSettings();
        }
    }
}

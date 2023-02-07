package com.aelous.model.content.items;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.loot.LootItem;
import com.aelous.model.items.loot.LootTable;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 22, 2021
 */
public class SeedPack extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 1) {
            if(item.getId() == SEED_PACK) {
                open(player, item);
                return true;
            }
        }
        return false;
    }

    private void open(Player player, Item herbBox) {
        for (int i = 0; i < 20; i++)
            player.getBank().depositFromNothing(new Item(table.rollItem().getId(),1));

        player.inventory().remove(herbBox,true);
        player.message("20 seeds have been deposited into your bank.");
    }

    private static final LootTable table = new LootTable().addTable(1,
        new LootItem(GUAM_SEED, World.getWorld().random(1, 5), 25),
        new LootItem(MARRENTILL_SEED, World.getWorld().random(1, 5), 24),
        new LootItem(TARROMIN_SEED, World.getWorld().random(1, 5), 23),
        new LootItem(HARRALANDER_SEED, World.getWorld().random(1, 5), 22),
        new LootItem(RANARR_SEED, World.getWorld().random(1, 5), 21),
        new LootItem(IRIT_SEED, World.getWorld().random(1, 5), 20),
        new LootItem(AVANTOE_SEED, World.getWorld().random(1, 5), 19),
        new LootItem(KWUARM_SEED, World.getWorld().random(1, 5), 18),
        new LootItem(CADANTINE_SEED, World.getWorld().random(1, 5), 17),
        new LootItem(LANTADYME_SEED, World.getWorld().random(1, 5), 16),
        new LootItem(DWARF_WEED_SEED, World.getWorld().random(1, 5), 15),
        new LootItem(SNAPDRAGON_SEED, World.getWorld().random(1, 5), 14)
    );
}

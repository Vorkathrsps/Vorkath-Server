package com.aelous.model.content.items;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.loot.LootItem;
import com.aelous.model.items.loot.LootTable;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | April, 02, 2021, 12:04
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class HerbBox extends PacketInteraction {


    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 1) {
            if (item.getId() == HERB_BOX) {
                if (player.inventory().isFull()) {
                    player.message("You need at least one inventory space to take a herb from your box.");
                    return true;
                } else {
                    if(item.getId() == HERB_BOX) {
                        open(player, item);
                        return true;
                    }
                }
            }
        }
        if(option == 2) {
            if(item.getId() == HERB_BOX) {
                check(player);
                return true;
            }
        }
        if(option == 3) {
            if(item.getId() == HERB_BOX) {
                open(player, item);
                return true;
            }
        }
        return false;
    }

    private void open(Player player, Item herbBox) {
        int currentCharges = player.<Integer>getAttribOr(AttributeKey.HERB_BOX_CHARGES,20);
        int amtToAdd = currentCharges == -1 || currentCharges == 0 ? 20 : currentCharges;
        for (int i = 0; i < amtToAdd; i++)
            player.getBank().depositFromNothing(new Item(table.rollItem().getId(),1));

        player.inventory().remove(herbBox,true);
        player.message(amtToAdd + " herb" + (amtToAdd == 1 ? " has" : "s have") + " been deposited into your bank.");
    }

    private void check(Player player) {
        int charges = player.<Integer>getAttribOr(AttributeKey.HERB_BOX_CHARGES,20);
        if(charges == 0)
            player.putAttrib(AttributeKey.HERB_BOX_CHARGES,20);
        charges = player.<Integer>getAttribOr(AttributeKey.HERB_BOX_CHARGES,20);
        player.message("Your box has " + charges + " herb" + (charges == 1 ? "" : "s") + " left.");
    }

    private static final LootTable table = new LootTable().addTable(1,
        new LootItem(GRIMY_GUAM_LEAF, World.getWorld().random(1, 5), 25),
        new LootItem(GRIMY_MARRENTILL, World.getWorld().random(1, 5), 24),
        new LootItem(GRIMY_TARROMIN, World.getWorld().random(1, 5), 23),
        new LootItem(GRIMY_HARRALANDER, World.getWorld().random(1, 5), 22),
        new LootItem(GRIMY_RANARR_WEED, World.getWorld().random(1, 5), 21),
        new LootItem(GRIMY_IRIT_LEAF, World.getWorld().random(1, 5), 20),
        new LootItem(GRIMY_AVANTOE, World.getWorld().random(1, 5), 19),
        new LootItem(GRIMY_KWUARM, World.getWorld().random(1, 5), 18),
        new LootItem(GRIMY_CADANTINE, World.getWorld().random(1, 5), 17),
        new LootItem(GRIMY_LANTADYME, World.getWorld().random(1, 5), 16),
        new LootItem(GRIMY_DWARF_WEED, World.getWorld().random(1, 5), 15),
        new LootItem(GRIMY_TOADFLAX, World.getWorld().random(1, 5), 14),
        new LootItem(GRIMY_SNAPDRAGON, World.getWorld().random(1, 5), 13)
    );
}

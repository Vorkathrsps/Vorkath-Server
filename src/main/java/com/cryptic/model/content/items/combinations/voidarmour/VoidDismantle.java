package com.cryptic.model.content.items.combinations.voidarmour;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;

public class VoidDismantle extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 3) {
            if (isEqualTo(item, ItemIdentifiers.VOID_KNIGHT_TOP_OR)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.VOID_KNIGHT_TOP));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            } else if (isEqualTo(item, ItemIdentifiers.VOID_KNIGHT_ROBE_OR)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.VOID_KNIGHT_ROBE));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            } else if (isEqualTo(item, ItemIdentifiers.VOID_KNIGHT_GLOVES_OR)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.VOID_KNIGHT_GLOVES));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            } else if (isEqualTo(item, ItemIdentifiers.VOID_RANGER_HELM)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.VOID_RANGER_HELM));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            } else if (isEqualTo(item, ItemIdentifiers.VOID_MELEE_HELM_OR)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.VOID_MELEE_HELM));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            } else if (isEqualTo(item, ItemIdentifiers.VOID_MAGE_HELM_OR)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.VOID_MAGE_HELM));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            } else if (isEqualTo(item, ItemIdentifiers.ELITE_VOID_TOP_OR)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.ELITE_VOID_TOP));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            } else if (isEqualTo(item, ItemIdentifiers.ELITE_VOID_ROBE_OR)) {
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.ELITE_VOID_ROBE));
                player.getInventory().add(new Item(ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT));
                return true;
            }
        }
        return false;
    }

    private static boolean isEqualTo(Item item, int id) {
        return item.getId() == id;
    }

}

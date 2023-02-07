/*
package net.aelous.game.content.items.equipment;

import net.aelous.model.entity.mob.player.Player;
import net.aelous.game.world.items.Item;
import net.aelous.net.packet.interaction.PacketInteraction;
import net.aelous.util.CustomItemIdentifiers;
import net.aelous.util.ItemIdentifiers;

//import static net.aelous.util.CustomItemIdentifiers.INQUISITORS_MACE_ORNAMENT_KIT;
//import static net.aelous.util.CustomItemIdentifiers.SHADOW_INQUISITOR_ORNAMENT_KIT;
import static net.aelous.util.ItemIdentifiers.*;

public class ShadowInquisitor extends PacketInteraction {

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == INQUISITORS_MACE || usedWith.getId() == INQUISITORS_MACE) && (use.getId() == INQUISITORS_MACE_ORNAMENT_KIT || usedWith.getId() == INQUISITORS_MACE_ORNAMENT_KIT)) {
            player.inventory().remove(new Item(INQUISITORS_MACE_ORNAMENT_KIT),true);
            player.inventory().remove(new Item(INQUISITORS_MACE),true);
            player.inventory().add(new Item(CustomItemIdentifiers.SHADOW_MACE),true);
            return true;
        }
        if ((use.getId() == SHADOW_INQUISITOR_ORNAMENT_KIT && usedWith.getId() == INQUISITORS_GREAT_HELM) || (use.getId() == INQUISITORS_GREAT_HELM && usedWith.getId() == SHADOW_INQUISITOR_ORNAMENT_KIT)) {
            player.inventory().remove(new Item(SHADOW_INQUISITOR_ORNAMENT_KIT), true);
            player.inventory().remove(new Item(INQUISITORS_GREAT_HELM), true);
            player.inventory().add(new Item(CustomItemIdentifiers.SHADOW_GREAT_HELM), true);
            return true;
        }
        if ((use.getId() == SHADOW_INQUISITOR_ORNAMENT_KIT && usedWith.getId() == INQUISITORS_HAUBERK) || (use.getId() == INQUISITORS_HAUBERK && usedWith.getId() == SHADOW_INQUISITOR_ORNAMENT_KIT)) {
            player.inventory().remove(new Item(SHADOW_INQUISITOR_ORNAMENT_KIT), true);
            player.inventory().remove(new Item(INQUISITORS_HAUBERK), true);
            player.inventory().add(new Item(CustomItemIdentifiers.SHADOW_HAUBERK), true);
            return true;
        }
        if ((use.getId() == SHADOW_INQUISITOR_ORNAMENT_KIT && usedWith.getId() == INQUISITORS_PLATESKIRT) || (use.getId() == ItemIdentifiers.INQUISITORS_PLATESKIRT && usedWith.getId() == SHADOW_INQUISITOR_ORNAMENT_KIT)) {
            player.inventory().remove(new Item(SHADOW_INQUISITOR_ORNAMENT_KIT), true);
            player.inventory().remove(new Item(ItemIdentifiers.INQUISITORS_PLATESKIRT), true);
            player.inventory().add(new Item(CustomItemIdentifiers.SHADOW_PLATESKIRT), true);
            return true;
        }
        return false;
    }
}
*/

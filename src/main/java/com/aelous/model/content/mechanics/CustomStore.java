package com.aelous.model.content.mechanics;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.shop.Shop;
import com.aelous.model.items.container.shop.impl.ShopReference;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import java.util.stream.IntStream;

/**
 * @author Patrick van Elderen | May, 24, 2021, 12:34
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class CustomStore extends PacketInteraction {

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        /** Config handler **/
        resetButtons(player, button);
        /** Shop object **/
        Shop shop = null;
        /** Shop Reference **/
        ShopReference reference = player.shopReference;
        /**
         * Options
         */
        if (button >= 28061 && button <= 28063) {
            if (button == 28061)
                shop = World.getWorld().shop(reference == ShopReference.GEAR ? 12 : 43);
            if (button == 28062)
                shop = World.getWorld().shop(reference == ShopReference.GEAR ? 10 : 44);
            if (button == 28063)
                shop = World.getWorld().shop(reference == ShopReference.GEAR ? 11 : 45);

            if (shop != null) {
                shop.open(player);
                return true;
            }
        }
        return false;
    }

    private void resetButtons(Player player, int buttonId) {
        IntStream.of(1125, 1126, 1127).forEach(s -> player.getPacketSender().sendConfig(s, 0));
        int highlight = buttonId - 26936;
        if (highlight >= 1125 && highlight <= 1127)
            player.getPacketSender().sendConfig(highlight, 1);
    }

}

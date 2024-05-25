package com.cryptic.model.content.mechanics;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.model.items.container.shop.impl.ShopReference;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import java.util.stream.IntStream;

/**
 * @author Origin | May, 24, 2021, 12:34
 * 
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

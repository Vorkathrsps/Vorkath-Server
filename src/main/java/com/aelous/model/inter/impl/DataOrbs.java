package com.aelous.model.inter.impl;

import com.aelous.model.content.areas.edgevile.BobBarter;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;
import com.aelous.utility.SecondsTimer;

public class DataOrbs extends PacketInteraction {

    private final SecondsTimer button_delay = new SecondsTimer();

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (button == 1510) {
            if(WildernessArea.inWild(player)) {
                player.message(Color.RED.wrap("What the hell are you thinking? That doesn't work in dangerous areas!"));
                return true;
            }

            if (button_delay.active()) {
                player.message(Color.RED.wrap("You can only use this every 5 seconds."));
                return true;
            }

            if(!player.tile().homeRegion() || player.tile().region() == 9772) {
                player.message(Color.RED.wrap("You can't heal yourself here!"));
                return true;
            }
            button_delay.start(5);
            player.getBank().depositInventory();
            player.getBank().depositeEquipment();
            return true;
        }

        if (button == 1511) {
            if(WildernessArea.inWild(player)) {
                player.message(Color.RED.wrap("What the hell are you thinking? That doesn't work in dangerous areas!"));
                return true;
            }

            if (button_delay.active()) {
                player.message(Color.RED.wrap("You can only use this every 5 seconds."));
                return true;
            }

            if(!player.tile().homeRegion() || player.tile().region() == 9772) {
                player.message(Color.RED.wrap("You can't heal yourself here!"));
                return true;
            }
            button_delay.start(5);
            BobBarter.decant(player);
            return true;
        }

        if (button == 1512) {
            if(WildernessArea.inWild(player)) {
                player.message(Color.RED.wrap("What the hell are you thinking? That doesn't work in dangerous areas!"));
                return true;
            }

            if (button_delay.active()) {
                player.message(Color.RED.wrap("You can only use this every 5 seconds."));
                return true;
            }

            if(!player.tile().homeRegion() || player.tile().region() == 9772) {
                player.message(Color.RED.wrap("You can't heal yourself here!"));
                return true;
            }
            player.heal();
            button_delay.start(5);
            return true;
        }
        return false;
    }
}

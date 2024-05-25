package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.cs2.interfaces.InterfaceHandler;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.player.InterfaceManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class CloseModelPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        InterfaceHandler.closeModals(player);

        System.out.println("Close Models");
    }
}

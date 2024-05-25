package com.cryptic.model.cs2.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class DestroyItem extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return  GameInterface.DESTROY_ITEM;
    }

    @Override
    public void beforeOpen(Player player) {
        player.getPacketSender().runClientScriptNew(2379);
        player.getPacketSender().runClientScriptNew(814, 995, 10000, 0, "HEY", "Boop");
    }


}

package com.cryptic.clientscripts.impl.skills.smithing;

import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

public class SmithingInterface extends InterfaceBuilder {

    public static int getTierForBar(final Item item) {
        final int id = item.getId();
        return id == 2349 ? 1 : id == 2351 ? 2 : id == 2353 ? 3 : id == 2359 ? 4 : id == 2361 ? 5 : id == 2363 ? 6 : id == 9467 ? 7 : 0;
    }

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SMITHING_INTERFACE;
    }

    @Override
    public void beforeOpen(Player player) {
        //player.getPacketSender().runClientScriptNew(6123); //cant type state?
        setEvents(new EventNode(3, 0, 0));
        setEvents(new EventNode(4, 0, 0));
        setEvents(new EventNode(5, 0, 0));
        player.varps().setVarp(2224, 1);
        player.varps().setVarp(210, 7);
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }

}

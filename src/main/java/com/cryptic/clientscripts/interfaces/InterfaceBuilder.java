package com.cryptic.clientscripts.interfaces;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;

import java.util.ArrayList;

public abstract class InterfaceBuilder {
    ArrayList<EventNode> events = new ArrayList<>();

    protected GameInterface gameInterface() {
        return null;
    }

    protected boolean sendInterface() {
        return true;
    }

    public void beforeOpen(Player player) {
    }

    public void open(Player player) {
        player.activeInterface.put(gameInterface().getId(), this);
        beforeOpen(player);
        initialize(player);
        if (sendInterface()) {
            player.interfaces.sendInterface(gameInterface());
        }
    }

    public void onButton(Player player, int button, int option, int slot, int itemId) {
    }

    public void close(Player player) {
        if (sendInterface()) {
            player.activeInterface.remove(gameInterface().getId());
        }
    }

    public void setEvents(final EventNode node) {
        if (!this.events.contains(node)) this.events.add(node);
    }

    public void initialize(Player player) {
        for (final EventNode node : events) {
            node.interfaceID = gameInterface().getId();
            node.setButtons().setFlags().send(player);
        }
    }

}

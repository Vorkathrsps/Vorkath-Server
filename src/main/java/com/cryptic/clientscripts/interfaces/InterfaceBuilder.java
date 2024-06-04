package com.cryptic.clientscripts.interfaces;

import com.cryptic.core.event.Event;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.region.RegionManager;
import com.displee.cache.index.Index;

import java.util.ArrayList;
import java.util.List;

public abstract class InterfaceBuilder {
    ArrayList<EventNode> events = new ArrayList<>();

    protected GameInterface gameInterface() {
        return null;
    }

    protected boolean sendInterface() {
        return true;
    }

    public void beforeOpen(final Player player) {

    }

    public void open(final Player player) {
        player.activeInterface.put(gameInterface().getId(), this);
        beforeOpen(player);
        initialize(player);
        if (sendInterface()) {
            player.interfaces.sendInterface(gameInterface());
        }
    }

    public void onButton(Player player, final int button, final int option, final int slot, final int itemId) {
    }


    public void close(final Player player) {
        if (sendInterface()) {
            player.activeInterface.remove(gameInterface().getId());
        }
    }

    public void setEvents(final EventNode node) {
        if (!this.events.contains(node)) this.events.add(node);
    }

    public void setEvents(final List<EventNode> nodes) {
        if (!this.events.containsAll(nodes)) this.events.addAll(nodes);
    }

    public void initialize(final Player player) {
        for (final EventNode node : events) {
            node.interfaceID = gameInterface().getId();
            node.setButtons().setFlags().send(player);
        }
    }

    public static int find(final String name) {
        Index clientscripts = RegionManager.cache.index(12);
        int scriptId = clientscripts.archiveId("[clientscript," + name + "]");
        if (scriptId == -1) {
            throw new RuntimeException("unable to find [clientscript," + name + "]");
        }
        return scriptId;
    }
}

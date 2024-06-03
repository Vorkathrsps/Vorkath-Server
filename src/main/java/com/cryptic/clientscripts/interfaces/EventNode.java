package com.cryptic.clientscripts.interfaces;

import com.cryptic.model.entity.player.Player;
import kotlin.ranges.IntRange;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventNode {
    final int componentId;
    int interfaceID;
    final ArrayList<EventConstants> events;
    final int childFrom;
    final int childTo;

    int event;

    public EventNode(int componentId, int childFrom, int childTo) {
        this.componentId = componentId;
        this.childFrom = childFrom;
        this.childTo = childTo;
        this.events = new ArrayList<>();
    }

    public EventNode(int componentId, int childFrom, int childTo, ArrayList<EventConstants> events) {
        this.componentId = componentId;
        this.childFrom = childFrom;
        this.childTo = childTo;
        this.events = events;
    }

    public final EventNode setButtons() {
        events.addAll(List.of(
            EventConstants.ClickOp1,
            EventConstants.ClickOp2,
            EventConstants.ClickOp3,
            EventConstants.ClickOp4,
            EventConstants.ClickOp5,
            EventConstants.ClickOp6,
            EventConstants.ClickOp7,
            EventConstants.ClickOp8,
            EventConstants.ClickOp9,
            EventConstants.ClickOp10));
        return this;
    }

    public final EventNode setFlags() {
        for (EventConstants constant : events) event |= constant.getFlag();
        return this;
    }

    public final void send(Player player) {
        player.getPacketSender().setInterfaceEvents(interfaceID,this.componentId,new IntRange(childFrom,childTo), this.event);
    }
}

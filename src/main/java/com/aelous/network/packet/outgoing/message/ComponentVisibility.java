package com.aelous.network.packet.outgoing.message;

import com.aelous.network.packet.outgoing.PacketMessage;

/**
 * @author Jason MacKeigan
 * @date Nov 15, 2014, 3:14:01 AM
 */
public class ComponentVisibility implements PacketMessage<ComponentVisibility> {

    private boolean state;

    private int componentId;

    public ComponentVisibility(boolean state, int componentId) {
        this.state = state;
        this.componentId = componentId;
    }

    public boolean getState() {
        return state;
    }

    public int getComponentId() {
        return componentId;
    }

    @Override
    public boolean matches(ComponentVisibility message) {
        return message.componentId == componentId;
    }

    @Override
    public boolean requiresUpdate(ComponentVisibility message) {
        return message.state != state;
    }
}

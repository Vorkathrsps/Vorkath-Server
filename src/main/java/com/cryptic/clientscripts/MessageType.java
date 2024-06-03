package com.cryptic.clientscripts;

public enum MessageType {
    UNFILTERABLE(0),
    GLOBAL_BROADCAST(14),
    EXAMINE_ITEM(27),
    EXAMINE_NPC(28),
    EXAMINE_OBJECT(29),
    AUTOTYPER(90),
    TRADE_REQUEST(101),
    CHALLENGE_REQUEST(103),
    FILTERABLE(105);
    private final int type;

    MessageType(final int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}

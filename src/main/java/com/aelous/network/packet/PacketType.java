package com.aelous.network.packet;

public enum PacketType {

    /**
     * A fixed size packet where the size never changes.
     */
    FIXED,

    /**
     * A variable packet where the size is described by a byte.
     */
    VARIABLE,

    /**
     * A variable packet where the size is described by a word.
     */
    VARIABLE_SHORT;
}


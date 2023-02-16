package com.aelous.model.content.account;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum AccountType {

    IRON_MAN(42402),
    HARDCORE_IRON_MAN(42403),
    TRAINED_ACCOUNT(42423),
    PVP_ACCOUNT(42405),
    DARKLORD_ACCOUNT(42406),
    NONE(-1);

    private final int button;

    /**
     * We don't have to set a constructor because the Enum only consists of Types
     */
    AccountType(int button) {
        this.button = button;
    }

    /**
     * Gets the spriteId from the client.
     *
     * @return The spriteId
     */
    public int getSpriteId() {
        return 42402 + (ordinal());
    }

    /**
     * The buttonId
     *
     * @return The button we receive from the client.
     */
    public int getButtonId() {
        return button;
    }

    //A set of game types
    public static final Set<AccountType> TYPE = Collections.unmodifiableSet(EnumSet.allOf(AccountType.class));

}

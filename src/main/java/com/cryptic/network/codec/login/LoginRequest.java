package com.cryptic.network.codec.login;

import com.cryptic.model.entity.player.Player;
import lombok.Getter;

/**
 * The login request model. multiple requests for the same player IS ALLOWED - but inner sync code
 * will stop both logging in.
 */
public final class LoginRequest {

    /** The player. */
    @Getter
    public final Player player;

    /** The login request message. */
    @Getter
    public final LoginDetailsMessage message;

    private long delayedUntil;
    private int retries;
    /**
     * Creates a new {@link LoginRequest}.
     *
     * @param player The player.
     * @param message The login request message.
     */
    public LoginRequest(Player player, LoginDetailsMessage message) {
        this.player = player;
        this.message = message;
    }

    public long delayedUntil() {
        return delayedUntil;
    }

    public void delayedUntil(long l) {
        delayedUntil = l;
    }

    public int retries() {
        return retries;
    }

    public void addRetry() {
        retries++;
    }
}

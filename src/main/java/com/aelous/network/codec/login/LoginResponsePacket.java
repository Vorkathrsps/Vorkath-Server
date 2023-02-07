package com.aelous.network.codec.login;

import com.aelous.model.entity.player.rights.PlayerRights;

/**
 * The packet that contains information about a players login attempt.
 * 
 * @author Vult-R
 */
public final class LoginResponsePacket {

    /**
     * The login response that was indicated.
     */
    private final int response;

    /**
     * The rights of the player logging in.
     */
    private final PlayerRights playerRights;

    /**
     * Creates a new {@link LoginResponsePacket}.
     *
     * @param response The response that was indicated.
     *
     * @param playerRights The rights of the player logging in.
     *
     */
    public LoginResponsePacket(int response, PlayerRights playerRights) {
        this.response = response;
        this.playerRights = playerRights;
    }

    public LoginResponsePacket(int response) {
        this.response = response;
        this.playerRights = PlayerRights.PLAYER;
    }

    public int getResponse() {
        return response;
    }

    public PlayerRights getPlayerRights() {
        return playerRights;
    }
}


package com.aelous.network;

import com.aelous.network.codec.login.LoginDetailsMessage;

/**
 * Represents a player's current session state.
 *
 * @author Professor Oak
 */
public enum SessionState {

    /**
     * the default state, when netty opens a connection from login screen
     */
    CONNETED,

    /**
     * The client is currently decoding the login protocol. via {@link Session#finalizeLogin(LoginDetailsMessage)}
     */
    LOGGING_IN,

    /**
     * The client is now a player, and is logged in. from {@link PlayerLogin#preLogin()}
     */
    LOGGED_IN,
    
    /**
     * The player requested a logout using the logout button.
     */
    REQUESTED_LOG_OUT,
    
    /**
     * A request has been sent to disconnect the client.
     */
    LOGGING_OUT,

    /**
     * The client has disconnected from the server.
     */
    LOGGED_OUT
}

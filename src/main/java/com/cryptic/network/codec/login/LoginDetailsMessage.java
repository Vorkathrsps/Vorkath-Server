package com.cryptic.network.codec.login;

import com.cryptic.network.packet.Packet;
import com.cryptic.network.security.IsaacRandom;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.nio.channels.Channel;

/**
 * The {@link Packet} implementation that contains data used for the final
 * portion of the login protocol.
 * 
 * @author lare96 <http://github.org/lare96>
 */
@Getter
public final class LoginDetailsMessage {

    /**
     * The context to which this player is going through.
     */
    private final ChannelHandlerContext context;

    /**
     * The username of the player.
     */
    private final String username;

    /**
     * The password of the player.
     */
    private final String password;

    /**
     * The player's host address
     */
    private final String host;
    
    /**
     * The player's mac address
     */
    private final String mac;

    private final String clientVersion;

    /**
     * The encrypting isaac
     */
    private final IsaacRandom encryptor;

    /**
     * The decrypting isaac
     */
    private final IsaacRandom decryptor;

    /**
     * Creates a new {@link LoginDetailsMessage}.
     *
     * @param context
     *            the {@link ChannelHandlerContext} that holds our
     *            {@link Channel} instance.
     * @param username
     *            the username of the player.
     * @param password
     *            the password of the player.
     * @param encryptor
     *            the encryptor for encrypting messages.
     * @param decryptor
     *            the decryptor for decrypting messages.
     */
    public LoginDetailsMessage(ChannelHandlerContext context, String username, String password, String ipAddress, String mac, String clientVersion,
            IsaacRandom encryptor, IsaacRandom decryptor) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.host = ipAddress;
        this.mac = mac;
        this.clientVersion = clientVersion;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }


    @Override
    public String toString() {
        return "LoginDetailsMessage{" +
            "context=" + context +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", host='" + host + '\'' +
            ", mac='" + mac + '\'' +
            ", clientVersion='" + clientVersion + '\'' +
            '}';
    }
}

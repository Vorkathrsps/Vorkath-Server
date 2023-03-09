package com.aelous.network.codec.login;

import co.paralleluniverse.strands.Strand;
import com.aelous.GameEngine;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.network.Session;
import com.aelous.network.codec.game.PacketDecoder;
import com.aelous.network.codec.game.PacketEncoder;
import com.aelous.utility.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Created by Bart on 8/1/2015. */
public class LoginWorker implements Runnable {

    private static final Logger loginLogs = LogManager.getLogger("LoginLogs");
    private static final Level LOGIN;

    static {
        LOGIN = Level.getLevel("LOGIN");
    }

    private static final Logger logger = LogManager.getLogger(LoginWorker.class);

    private LoginService service;

    /**
     * Will show 'sever is being updated' if false. After a Production restart, do ::acceptlogins to
     * enable.
     */
    public static boolean acceptLogins = false;

    /**
     * only names that can do ::freelogin (login with a PW!!!) .. basically dev only. Way of getting
     * admin before profile load and you can do a rights=2 check
     */
    public static List<String> hardcodeAdmins = Collections.emptyList();

    public LoginWorker(LoginService service) {
        this.service = service;
    }

    @Override
    public void run() {
        while (true) {
            try {
                processLoginJob();
            } catch (Exception e) {
                logger.error("Error processing login worker job!", e);
            }
        }
    }

    private void processLoginJob() throws Exception {
        LoginRequest request = service.messages().take();

        if (request == null) {
            return;
        } else if (request.delayedUntil() > System.currentTimeMillis()) {
            service.enqueue(request);
            Strand.sleep(30);
            return;
        }

        logger.debug("Attempting to process login request for {}.", request.message.getUsername());
        final Player player = request.player;

        int response = LoginResponses.evaluateAsync(player, request.message);
        loginLogs.log(
                LOGIN, "First Login response code for " + player.getUsername() + " is " + response);
        if (response != LoginResponses.LOGIN_SUCCESSFUL)
            // logger.trace("Login response code for " + player.getUsername() + " is " + response);
        {
            // Load wasn't successful, disconnect with login response.
            if (player.getSession().getChannel() != null) {
                sendCodeAndClose(player.getSession().getChannel(), response);
                return;
            }
        }
        // Send the final login response.
        Session session = player.getSession();
        Channel channel = session.getChannel();
        LoginDetailsMessage message = request.message;
        complete(request, player, channel, message);
    }

    private void sendCodeAndClose(Channel channel, int response) {
        ByteBuf buffer = Unpooled.buffer(Byte.BYTES);
        buffer.writeByte(response);
        channel.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
    }

    private void initForGame(LoginDetailsMessage message, Channel channel) {
        if (channel != null) {
            channel.pipeline()
                    .replace("encoder", "encoder", new PacketEncoder(message.getEncryptor()));
            channel.pipeline()
                    .replace("decoder", "decoder", new PacketDecoder(message.getDecryptor()));
            channel.pipeline().remove("login-handler");
            channel.pipeline().addFirst("timeout", new ReadTimeoutHandler(30, TimeUnit.SECONDS));
        }
    }

    private void complete(
            LoginRequest request, Player player, Channel channel, LoginDetailsMessage message) {
        GameEngine.getInstance()
                .addSyncTask(
                        () -> {
                            int response = LoginResponses.evaluateOnGamethread(player);
                            ChannelFuture future = player.getSession().sendOkLogin(response);

                            if (future == null) {//TODO test.
                                player.getSession().ctx.close();
                                return;
                            }
                            if (response != LoginResponses.LOGIN_SUCCESSFUL) {
                                if (player.getSession().getChannel() != null) {
                                    sendCodeAndClose(player.getSession().getChannel(), response);
                                    // logger.trace("Login response 2nd code for " +
                                    // player.getUsername() + " is " + response);
                                    // reply is sent either way above, no need here
                                    return;
                                }
                            }
                            initForGame(message, channel);
                            World.getWorld().getPlayers().add(player);
                            Utils.sendDiscordInfoLog(
                                    "```Login successful for player "
                                            + request.message.getUsername()
                                            + " with IP "
                                            + request.message.getHost()
                                            + "```",
                                    "login");
                            loginLogs.log(
                                    LOGIN,
                                    "Login successful for player {}.",
                                    request.player.getUsername());
                        });
    }
}

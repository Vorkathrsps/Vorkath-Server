package com.cryptic.network.codec.login;

import co.paralleluniverse.strands.Strand;
import com.cryptic.GameEngine;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.Session;
import com.cryptic.network.codec.game.PacketDecoder;
import com.cryptic.network.codec.game.PacketEncoder;
import com.cryptic.network.security.IPv4AddressExtensionsKt;
import com.cryptic.utility.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bart on 8/1/2015.
 */
public class LoginWorker implements Runnable {
    private static final String[] staff = new String[]{"192.168.1.56", "192.168.1.57", "185.142.58.231"};
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

        if (request.delayedUntil() > System.currentTimeMillis()) {
            service.enqueue(request);
            Strand.sleep(30);
            return;
        }

        logger.debug("Attempting to process login request for {}.", request.message.getUsername());
        final Player player = request.player;

        int response = LoginResponses.evaluateAsync(player, request.message);
        loginLogs.log(
            LOGIN, "First Login response code for " + player.getUsername() + " is " + response);
        if (response != LoginResponses.LOGIN_SUCCESSFUL) {
            if (player.getSession().getChannel() != null) {
                sendCodeAndClose(player.getSession().getChannel(), response);
                return;
            }
        }

        Session session = player.getSession();
        Channel channel = session.getChannel();
        LoginDetailsMessage message = request.message;
        complete(request, player, channel, message);
    }

    private void sendCodeAndClose(Channel channel, int response) {
        ByteBuf buffer = channel.alloc().buffer(Byte.BYTES);
        buffer.writeByte(response);
        channel.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
    }

    private void initForGame(LoginDetailsMessage message, Channel channel) {
        if (channel != null) {
            final ChannelPipeline pipeline = channel.pipeline();
            pipeline.replace("decoder", "decoder", new PacketDecoder(message.getDecryptor()));
            pipeline.replace("encoder", "encoder", new PacketEncoder(message.getEncryptor()));
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
                    loginLogs.log(LOGIN, "Login successful for player {}.", request.player.getUsername());
                });
    }
}

package com.aelous.network.codec.login;

import com.aelous.GameServer;
import com.aelous.network.SessionHandler;
import com.aelous.network.Session;
import com.aelous.network.codec.ByteBufUtils;
import com.aelous.network.NetworkUtils;
import com.aelous.network.SessionState;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author os-scape team
 */
@Sharable
public final class LoginHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getLogger(LoginHandler.class);

    /**
     * a map of ip:count of connections open in netty. not 1:1 with attempting to login because the client
     * might send a js5 or cache request or something else
     */
    private static ConcurrentHashMap<String, Integer> connections = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> login_timestamps = new ConcurrentHashMap<>();
    public static long timeLimitForMaxConnections = 15_000;
    public static int maximumShortTermOpenChannels = 25;

    // Used to check if the user has a username which 100% an admin. Means you don't have
    // to bother loading the profile _just_ to check rights.
    public static List<String> hardcodeAdmins = Arrays.asList("zoo");

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        String host = ByteBufUtils.getHost(ctx.channel());
        connections.compute(host, (k, v) -> v == null ? 1 : ++v);
        //System.out.println("login size: "+connections.get(host));
        login_timestamps.compute(host, (k, v) -> {
            if (v == null)
                v = new ConcurrentLinkedDeque<>();
            v.add(System.currentTimeMillis());
            return v;
        });


        final int count = connections.get(host);
        if (count > GameServer.properties().connectionLimit) {
            //logger.trace("Maximum number of connections reached for: " + host+" : "+count);
            LoginDecoder.sendCodeAndClose(ctx, LoginResponses.LOGIN_CONNECTION_LIMIT);
            return;
        }
        final ConcurrentLinkedDeque<Long> longs = login_timestamps.get(host);
        longs.removeIf(time -> System.currentTimeMillis() - time >= timeLimitForMaxConnections);
        final int count2 = longs.size();
        if (count2 >= maximumShortTermOpenChannels) { // someone is spamming logins
            //logger.trace("Maximum number of connections in {} ms timespan reached for: {}: {}", timeLimitForMaxConnections, host, count2);
            LoginDecoder.sendCodeAndClose(ctx, LoginResponses.LOGIN_CONNECTION_LIMIT);
            return;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (msg instanceof LoginDetailsMessage) {
            ctx.channel().attr(NetworkUtils.SESSION_KEY).setIfAbsent(new Session(ctx.channel()));
            Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();
            session.finalizeLogin((LoginDetailsMessage) msg);
        }
    }

    /**
     *channelUnregistered has no affect here because the last Netty.Handler in the pipeline is {@link SessionHandler#channelUnregistered(ChannelHandlerContext)}
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        //logger.info("channel closed on login screen");

        Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();

        // only reduce when on login screen. if ingame, SessionHandler adapter will call this.
        // keeps connection count 1:1 with real connection.
        if (session != null && session.getState() == SessionState.CONNETED) {
            reduceIPConnectedCount(ctx);
        }
    }

    public static void reduceIPConnectedCount(ChannelHandlerContext ctx) {
        String host = ByteBufUtils.getHost(ctx.channel());
        connections.compute(host, (k, v) -> v == null ? 0 : Math.max(0, --v));
       // System.out.println("removed? " + host + ": size: " + connections.get(host));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        try {
            // ignore on socket exception (typically indicated by message starting with read0)
            if (throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("read0")) return;
            if(throwable.getMessage() != null && throwable.getMessage().equalsIgnoreCase("Connection reset")) return;
            if (throwable instanceof java.nio.channels.ClosedChannelException) return; // dc

            if (throwable instanceof ReadTimeoutException) {
                //logger.info("Channel disconnected due to read timeout (30s): {}.", ctx.channel());
                ctx.channel().close();
            } else {
                //logger.error("An exception has been caused in the pipeline: ", throwable);
            }
        } catch (Exception e) {
            logger.error("Uncaught server exception!", e);
        }

        // dont close on exception, continue
        super.exceptionCaught(ctx, throwable);
    }

}

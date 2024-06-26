package com.cryptic.network;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.codec.login.LoginDetailsMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author os-scape team
 */
public final class SessionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SessionHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof LoginDetailsMessage message) {
            final Channel channel = ctx.channel();
            if (channel != null && channel.isActive() && !message.getHost().isEmpty()) {
                Session session = channel.attr(NetworkUtils.SESSION_KEY).get();
                if (session == null) {
                    session = new Session(ctx.channel());
                    channel.attr(NetworkUtils.SESSION_KEY).set(session);
                }
                session.finalizeLogin(message);
            }
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        if (!channel.isActive()) return;

        if (channel.isWritable()) {
            Session session = channel.attr(NetworkUtils.SESSION_KEY).get();
            if (session != null) session.flushQueuedPackets();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        final Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();
        if (session != null) onUnregisteredIngame(session);
    }

    private void onUnregisteredIngame(Session session) {
        session.clearQueues();
        Player player = session.getPlayer();
        if (player != null && player.getUsername() != null && !player.getUsername().isEmpty() && session.getState() == SessionState.LOGGED_IN) {
            player.getForcedLogoutTimer().start(60);
            player.putAttrib(AttributeKey.LOGOUT_CLICKED, true);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();
        if (throwable instanceof ReadTimeoutException) {
            logger.debug("Channel disconnected due to read timeout (30s): {}", ctx.channel());
            ctx.channel().close();
        } else {
            logger.error("An exception occurred in the pipeline: {}", session, throwable);
            ctx.close();
        }
    }
}

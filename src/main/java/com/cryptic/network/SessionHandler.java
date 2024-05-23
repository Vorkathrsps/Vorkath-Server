package com.cryptic.network;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.codec.login.LoginDetailsMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
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
        final Channel channel = ctx.channel();
        if (!channel.isActive()) return;
        if (msg instanceof LoginDetailsMessage) {
            ctx.channel().attr(NetworkUtils.SESSION_KEY).setIfAbsent(new Session(ctx.channel()));
            Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();
            session.finalizeLogin((LoginDetailsMessage) msg);
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        if (!channel.isActive()) return;
        if (channel.isWritable()) {
            Session session = channel.attr(NetworkUtils.SESSION_KEY).get();
            if (session != null) {
                session.flushQueuedPackets();
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        final Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();
        if (session == null) return;
        onUnregisteredIngame(session);
    }

    private void onUnregisteredIngame(Session session) {
        session.clearQueues();
        Player player = session.getPlayer();
        if (player == null) return;
        if (player.getUsername() == null || player.getUsername().isEmpty()) return;
        if (session.getState() != SessionState.LOGGED_IN) return;

        player.getForcedLogoutTimer().start(60);
        player.putAttrib(AttributeKey.LOGOUT_CLICKED, true);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        try {
            final Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();
            // ignore on traditional socket exception (typically indicated by message starting with read0 but may change..)
            if (throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("read0")) {
                return;
            }

            if (throwable instanceof SocketException && throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("throwConnectionReset")) {
                return;
            }
            if (throwable instanceof IOException && throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("writev0")) {
                return;
            }
            if (throwable instanceof ReadTimeoutException) {
                logger.debug("Channel disconnected due to read timeout (30s): {}.", ctx.channel());
                ctx.channel().close();
            } else {
                logger.error("An exception has been caused in the pipeline: {} {}", session, throwable);
                ctx.channel().close();
            }
        } catch (Exception e) {
            logger.error("Uncaught server exception!", e);
        }
    }

}

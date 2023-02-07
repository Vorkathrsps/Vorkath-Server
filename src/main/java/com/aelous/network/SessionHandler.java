package com.aelous.network;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.network.codec.login.LoginHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author os-scape team
 */
@Sharable
public final class SessionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(SessionHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // oss: read is handled directly in decoder rather than passed to handler
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        LoginHandler.reduceIPConnectedCount(ctx);

        Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();

        if (session != null) {
            onUnregisteredIngame(session);
        }
    }

    private void onUnregisteredIngame(Session session) {
        Player player = session.getPlayer();

        if (player == null) {
            //logger.error("channelInactive not possible: "+player);
            return;
        }
        if (player.getUsername() == null || player.getUsername().length() == 0) {
            //logger.error("channelInactive wtf: "+player);
            return;
        }
        //logger.trace("channelInactive for Player {} state:{}", player, player.getSession().getState());
        if (session.getState() != SessionState.LOGGED_IN) {
            // during login the connection is dropped and sent again a couple times (handshake > update > rsa > login)
            return;
        }
        // trigger logout only when your state is CONNECTED, aka you're ingame
        player.getForcedLogoutTimer().start(60);
        player.putAttrib(AttributeKey.LOGOUT_CLICKED, true);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        try {
            Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();
            // ignore on traditional socket exception (typically indicated by message starting with read0 but may change..)
            if (throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("read0")) {
                return;
            }

            if (throwable instanceof SocketException && throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("throwConnectionReset")) {
                //logger.error("connection reset: "+throwable+" : "+session);
                return; // dc
            }
            if (throwable instanceof IOException && throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("writev0")) {
                //logger.error("connection aborted: "+throwable+" : "+session);
                return; // dc
            }
            if (throwable instanceof ReadTimeoutException) {
                //logger.info("Channel disconnected due to read timeout (30s): {}.", ctx.channel());
                ctx.channel().close();
            }
            else {
                //logger.error("An exception has been caused in the pipeline: {} {}", session, throwable);
            }
        } catch (Exception e) {
            //logger.error("Uncaught server exception!", e);
        }

        // dont close on exception, continue
        super.exceptionCaught(ctx, throwable);

    }


}

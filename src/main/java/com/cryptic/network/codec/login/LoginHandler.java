package com.cryptic.network.codec.login;

import com.cryptic.network.SessionHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author os-scape team
 */
public final class LoginHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(LoginHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

    }

    /**
     *channelUnregistered has no affect here because the last Netty.Handler in the pipeline is {@link SessionHandler#channelUnregistered(ChannelHandlerContext)}
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        //logger.info("channel closed on login screen");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        try {
            // ignore on socket exception (typically indicated by message starting with read0)
            if (throwable.getStackTrace().length > 0 && throwable.getStackTrace()[0].getMethodName().equals("read0")) return;
            if(throwable.getMessage() != null && throwable.getMessage().equalsIgnoreCase("Connection reset")) return;
            if (throwable instanceof java.nio.channels.ClosedChannelException) return; // dc

            if (throwable instanceof ReadTimeoutException) {
                logger.debug("Channel disconnected due to read timeout (30s): {}.", ctx.channel());
                ctx.channel().close();
            } else {
                logger.error("An exception has been caused in the pipeline: ", throwable);
            }
        } catch (Exception e) {
            logger.error("Uncaught server exception!", e);
        }

        // don't close on exception, continue
        super.exceptionCaught(ctx, throwable);
    }

}

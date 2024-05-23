package com.cryptic.network.pipeline;

import com.cryptic.network.SessionHandler;
import com.cryptic.network.codec.login.LoginDecoder;
import com.cryptic.network.codec.login.LoginEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.TimeUnit;

public final class ChannelPipelineHandler extends ChannelInitializer<Channel> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
            .addLast("timeout", new ReadTimeoutHandler(30, TimeUnit.SECONDS))
            .addLast("decoder", new LoginDecoder())
            .addLast("encoder", new LoginEncoder())
            .addLast("handler", new SessionHandler());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), cause);
        ctx.close();
    }

}

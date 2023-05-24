package com.aelous.network.pipeline;

import com.aelous.network.SessionHandler;
import com.aelous.network.codec.login.LoginDecoder;
import com.aelous.network.codec.login.LoginEncoder;
import com.aelous.network.codec.login.LoginHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author os-scape team
 */
public final class ChannelPipelineHandler extends ChannelInitializer<Channel> {

    /**
     * The part of the pipeline that handles exceptions caught, channels being read, in-active
     * channels, and channel triggered events.
     */
    private final SessionHandler sessionHandler = new SessionHandler();

    private final LoginHandler loginHandler = new LoginHandler();

    @Override
    protected void initChannel(Channel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("timeout", new ReadTimeoutHandler(30, TimeUnit.SECONDS));
        pipeline.addLast("decoder", new LoginDecoder());
        pipeline.addLast("encoder", new LoginEncoder());
        pipeline.addLast("login-handler", loginHandler);
        pipeline.addLast("session-handler", sessionHandler);
    }

}

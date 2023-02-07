package com.aelous.network.pipeline;

import com.aelous.network.SessionHandler;
import com.aelous.network.codec.login.LoginDecoder;
import com.aelous.network.codec.login.LoginEncoder;
import com.aelous.network.codec.login.LoginHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

import java.util.concurrent.Executors;

/**
 *
 * @author @author os-scape team
 */
public class ChannelPipelineHandler extends ChannelInitializer<Channel> {

    /**
     * The part of the pipeline that handles exceptions caught, channels being read, in-active
     * channels, and channel triggered events.
     */
    private final SessionHandler HANDLER = new SessionHandler();
    private final LoginHandler LOGIN_HANDLER = new LoginHandler();
    private final GlobalTrafficShapingHandler trafficHandler = new GlobalTrafficShapingHandler(Executors.newSingleThreadScheduledExecutor(), 0, 0, 1000);

    @Override
    protected void initChannel(Channel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("traffic", trafficHandler);
        pipeline.addLast("decoder", new LoginDecoder());
        pipeline.addLast("encoder", new LoginEncoder());
        pipeline.addLast("login-handler", LOGIN_HANDLER);
        pipeline.addLast("channel-handler", HANDLER);
    }
}

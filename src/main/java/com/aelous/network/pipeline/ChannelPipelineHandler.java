package com.aelous.network.pipeline;

import com.aelous.network.SessionHandler;
import com.aelous.network.codec.login.LoginDecoder;
import com.aelous.network.codec.login.LoginEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author os-scape team
 */
public final class ChannelPipelineHandler extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
            .addLast("timeout", new ReadTimeoutHandler(30, TimeUnit.SECONDS))
            .addLast("decoder", new LoginDecoder())
            .addLast("encoder", new LoginEncoder())
            .addLast("handler", new SessionHandler());
    }

}

package com.aelous.network.pipeline;

import com.aelous.utility.timers.TimerKey;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The network builder for the Runescape #317 protocol. This class is used to
 * start and configure the {@link ServerBootstrap} that will control and manage
 * the entire network.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class NetworkBuilder {

    /**
     * Logger instance for this class.
     */
    private static final Logger logger = LogManager.getLogger(NetworkBuilder.class);

    /**
     * The bootstrap that will oversee the management of the entire network.
     */
    private final ServerBootstrap bootstrap = new ServerBootstrap();

    /**
     * The {@link ChannelInitializer} that will determine how channels will be
     * initialized when registered to the event loop group.
     */
    private final ChannelInitializer<Channel> connectionInitializer = new ChannelPipelineHandler();

    /**
     * Initializes this network handler effectively preparing the server to
     * listen for connections and handle network events.
     *
     * @param port
     *            the port that this network will be bound to.
     * @throws Exception
     *             if any issues occur while starting the network.
     */
    public void initialize(final int port) throws Exception {
        // Set up uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Uncaught server exception in thread {}!", t, e);
        });

        // Verify data/code integrity
        TimerKey.verifyIntegrity();

        final boolean epoll = Epoll.isAvailable();

        // Construct bootstrap
        final EventLoopGroup parentGroup = epoll
            ? new EpollEventLoopGroup(1)
            : new NioEventLoopGroup(1);
        final EventLoopGroup childGroup = epoll
            ? new EpollEventLoopGroup()
            : new NioEventLoopGroup();

        bootstrap.group(parentGroup, childGroup);
        bootstrap.channel(epoll ?
            EpollServerSocketChannel.class
            : NioServerSocketChannel.class);
        bootstrap.childHandler(connectionInitializer);
        bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);

        //TODO: research what this does
        //bootstrap.handler(new LoggingHandler(LogLevel.DEBUG));
        bootstrap.bind(port)
            .sync()
            .awaitUninterruptibly();
    }
}

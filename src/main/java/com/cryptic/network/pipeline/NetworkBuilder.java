package com.cryptic.network.pipeline;

import com.cryptic.utility.timers.TimerKey;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;
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
     * @param port the port that this network will be bound to.
     * @throws Exception if any issues occur while starting the network.
     */
    public void initialize(final int port) throws Exception {
        System.setProperty("org.jboss.netty.epollBugWorkaround", "true");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error("Uncaught server exception in thread {}!", t, e));
        TimerKey.verifyIntegrity();
        final boolean epoll = Epoll.isAvailable();
        final EventLoopGroup parentGroup = epoll ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
        final EventLoopGroup childGroup = epoll ? new EpollEventLoopGroup(2) : new NioEventLoopGroup(2);
        bootstrap.group(parentGroup, childGroup).handler(new LoggingHandler());
        bootstrap.channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class).handler(new LoggingHandler());
        bootstrap.childHandler(connectionInitializer);
        bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.AUTO_READ, false);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 65536);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 65536);
        bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(65535, 65535 * 10));
        final ByteBufAllocator allocator = new UnpooledByteBufAllocator(false);
        bootstrap.option(ChannelOption.ALLOCATOR, allocator);
        bootstrap.childOption(ChannelOption.ALLOCATOR, allocator);
        try {
            bootstrap.bind(port).sync().await();
            logger.info("Server bound to port {}", port);
        } catch (InterruptedException e) {
            logger.error("Failed to bind to port {}", port, e);
            throw e;
        }
    }
}

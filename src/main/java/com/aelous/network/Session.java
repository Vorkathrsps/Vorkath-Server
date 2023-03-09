package com.aelous.network;

import com.aelous.GameServer;
import com.aelous.core.TimesCycle;
import com.aelous.core.task.impl.PlayerTask;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.rights.PlayerRights;
import com.aelous.network.codec.game.PacketDecoder;
import com.aelous.network.codec.login.LoginDetailsMessage;
import com.aelous.network.codec.login.LoginResponsePacket;
import com.aelous.network.codec.login.LoginResponses;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketBuilder;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.IncomingHandler;
import com.aelous.utility.Utils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;

import com.aelous.network.codec.login.LoginRequest;
import static com.aelous.model.entity.attributes.AttributeKey.MAC_ADDRESS;

/**
 * The session handler dedicated to a player that will handle input and output
 * operations.
 *
 * @author Lare96
 * @author Swiffy
 * @editor Professor Oak
 */
public class Session {

    private static final Logger logger = LogManager.getLogger(Session.class);

    /**
     * The queue of packets that will be handled on the next sequence.
     */
    private final LinkedList<Packet> packetsQueue = new LinkedList<>();

    /**
     * The channel that will manage the connection for this player.
     */
    private final Channel channel;

    /**
     * The player I/O operations will be executed for.
     */
    private final Player player;
    public ChannelHandlerContext ctx;

    /**
     * The amount of packets read this cycle.
     */
    public int packetCounter;

    /**
     * The current state of this I/O session.
     */
    private SessionState state = SessionState.CONNETED;

    public LoginDetailsMessage getMsg() {
        return msg;
    }

    private LoginDetailsMessage msg;

    /**
     * Creates a new {@link Session}.
     */
    public Session(Channel channel) {
        this.channel = channel;
        this.player = new Player(this);
    }

    /**
     * Attempts to finalize a player's login.
     *
     * @param msg The player's login information.
     */
    public void finalizeLogin(LoginDetailsMessage msg) {
        this.msg = msg;
        state = SessionState.LOGGING_IN;
        String username = msg.getUsername();
        String password = msg.getPassword();

        // Passed initial check, submit login request.
        player.setUsername(username).setLongUsername(Utils.stringToLong(username)).setHostAddress(msg.getHost());
        player.putAttrib(MAC_ADDRESS, msg.getMac());
        ctx = msg.getContext();
        World.getWorld().ls.enqueue(new LoginRequest(player, msg));
    }

    public ChannelFuture sendOkLogin(int response) {
        if (channel == null)
            return null;
        ChannelFuture future = channel.writeAndFlush(new LoginResponsePacket(response, player.getPlayerRights()));
        if (response != LoginResponses.LOGIN_SUCCESSFUL) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        return future;
    }

    /**
     * Processes a packet immediately to be sent to the client.
     *
     * @param builder 	the packet to send.
     */
    public void writeAndFlush(PacketBuilder builder) {
        channel.writeAndFlush(builder.toPacket());
    }

    /**
     * Queues a recently decoded packet received from the channel.
     *
     * @param msg The packet that should be queued.
     */
    public void queuePacket(Packet msg) {

        int queuedSize = packetsQueue.size();

        if (queuedSize >= GameServer.properties().packetProcessLimit) {
            logger.error("Packet limit reached for " + getPlayer().getUsername() + ", disconnecting this player.  (Packet limit: " + GameServer.properties().packetProcessLimit + " )");
            getPlayer().requestLogout();
            return;
        }
        packetsQueue.add(msg);

    }

    private static final DecimalFormat df = new DecimalFormat("#.##");
    public static long threshold = 50_500_000;

    /**
     * Processes all of the queued messages from the {@link PacketDecoder} by
     * polling the internal queue, and then handling them via the handleInputMessage.
     * This method is called EACH GAME CYCLE.
     */
    public void handleQueuedPackets() {

        setPacketCounter(0);
        for (int i = 0; i < GameServer.properties().packetProcessLimit; i++) {
            Packet packet = packetsQueue.poll();
            if (packet == null) {
                break;
            }
            try {
                int opcode = packet.getOpcode();
                int size = packet.getSize();

                PacketListener listener = IncomingHandler.PACKETS[opcode];

                if (listener == null) {
                    String msg = "-> Error processing Opcode="+opcode+" Size="+size+" doesn't have a handler.";
                    if (PlayerRights.is(player, PlayerRights.ADMINISTRATOR))
                        player.getPacketSender().sendMessage("<col=ff0000>"+msg);
                    System.err.println("Error processing Opcode="+opcode+" Size="+size+" DOESN'T HAVE A HANDLER!");
                    return;
                }

                if (GameServer.broadcast != null) {
                    player.getPacketSender().sendBroadcast(GameServer.broadcast);
                }

                Entity.accumulateRuntimeTo(() -> {
                    try {
                        listener.handleMessage(player, packet);
                    } catch (Throwable t) {
                        logger.catching(t);
                    }
                    if (player.getCurrentTask() != null) {
                        if (player.getCurrentTask() instanceof PlayerTask task) {
                            if (task.stops(listener.getClass())) {
                                task.stop();
                            }
                        }
                    }
                }, taken -> {
                    if (!TimesCycle.BENCHMARKING_ENABLED)
                        return;
                    if (taken.toNanos() > threshold) { // 0.5ms
                        final double taken2 = taken.toNanos() / 1_000_000.;
                        final String frm = df.format(taken2);
                        final String time = frm.equals("0") || frm.equals("0.0") ? taken2 + "" : frm;
                        final String name = IncomingHandler.PACKETS[packet.getOpcode()].getClass().getSimpleName();
                        final String data = Arrays.toString(packet.getBuffer().array()); // cant release before calling this
                        System.err.println(time + " ms to process packet id " + name+" warning");
                    }
                });
            } catch (Throwable t) {
                logger.catching(t);
            } finally {
                packet.getBuffer().release();
            }
        }
    }

    /**
     * Queues the {@code msg} for this session to be encoded and sent to the
     * client.
     *
     * @param builder the packet to queue.
     */
    public void write(PacketBuilder builder) {
        if (channel == null || !channel.isOpen())
            return;
        try {
            Packet packet = builder.toPacket();

            if (!channel.isActive())
                return;

            channel.write(packet);
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    /**
     * Flushes this channel.
     */
    public void flush() {
        if (channel == null || !channel.isOpen())
            return;
        channel.flush();
    }

    /**
     * Gets the player I/O operations will be executed for.
     *
     * @return the player I/O operations.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the current state of this I/O session.
     *
     * @return the current state.
     */
    public SessionState getState() {
        return state;
    }

    /**
     * Sets the value for {@link Session#state}.
     *
     * @param state the new value to set.
     */
    public void setState(SessionState state) {
        this.state = state;
    }

    @Nullable
    public Channel getChannel() {
        return channel;
    }

    public void setPacketCounter(int packetCounter) {
        this.packetCounter = packetCounter;
    }

    public int getPacketCounter() {
        return packetCounter;
    }

    @Override
    public String toString() {
        return "Session{" +
            "player=" + player +
            ", state=" + state +
            '}';
    }
}

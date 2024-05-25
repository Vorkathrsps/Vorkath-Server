package com.cryptic.network.codec.game;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.NetworkUtils;
import com.cryptic.network.Session;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.incoming.IncomingHandler;
import com.cryptic.network.security.IsaacRandom;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Decodes packets that are received from the player's channel.
 * These packets are received from the client C2S packets.
 * These are the packets from the Clients "PacketSender".
 * Those packets need to have their sizes written in the array below.
 * <p>
 * Size calculations: byte is 1, short is 2, int is 4, long is 8, String is -1 and -3 is skip.
 *
 * @author Swiffy
 */
public final class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(PacketDecoder.class);

    private final IsaacRandom random;

    private State state = State.OPCODE;

    private int opcode = -1;
    private int size;

    public PacketDecoder(IsaacRandom random) {
        this.random = random;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        final Channel channel = ctx.channel();

        if (!channel.isActive()) return;

        final Session session = channel.attr(NetworkUtils.SESSION_KEY).get();

        if (session == null) return;

        final Player player = session.getPlayer();

        if (player == null) return;

        if (state == State.OPCODE) {
            opcode = (buffer.readUnsignedByte() - random.nextInt()) & 0xFF;
            size = IncomingHandler.PACKET_SIZES[opcode];

            state = State.LENGTH;
        }

        if (state == State.LENGTH) {
            switch (size) {
                case -1 -> {
                    if (!buffer.isReadable()) {
                        return;
                    }
                    size = buffer.readUnsignedByte();
                }
                case -2 -> {
                    if (!buffer.isReadable(2)) {
                        return;
                    }
                    size = buffer.readUnsignedShort();
                }
                case -3 -> {
                    logger.error("Unhandled size for OpCode=" + opcode + " size=" + size + " Info=" + player);
                    ctx.close();
                    return;
                }
            }

            state = State.PAYLOAD;
        }

        if (state == State.PAYLOAD) {
            if (size != 0 && !buffer.isReadable(size)) {
                return;
            }

            final ByteBuf payload = buffer.readRetainedSlice(size);
            state = State.OPCODE;

            try {
                session.queuePacket(new Packet(opcode, payload));
                //logger.info("Decoded packet with OpCode=" + opcode + " and size=" + size);
            } catch (Exception e) {
                logger.error("Exception occurred while handling packet. OpCode=" + opcode + " size=" + size, e);
            }
        }
    }

    private enum State {
        OPCODE,
        LENGTH,
        PAYLOAD
    }

}

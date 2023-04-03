package com.aelous.network.codec.game;

import com.aelous.GameServer;
import com.aelous.network.NetworkUtils;
import com.aelous.network.Session;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.incoming.IncomingHandler;
import com.aelous.network.security.IsaacRandom;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(PacketDecoder.class);
    private int opcode = -1;
    private int size = -1;
    private final IsaacRandom random;


    public PacketDecoder(IsaacRandom random) {
        this.random = random;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        Session session = ctx.channel().attr(NetworkUtils.SESSION_KEY).get();

        if (!buffer.isReadable() || session == null || session.getPlayer() == null || !ctx.channel().isOpen())
            return;

        buffer.markReaderIndex();

        if (opcode == -1) {
            if (!buffer.isReadable())
                return;
            opcode = buffer.readUnsignedByte();
            opcode = opcode - random.nextInt() & 0xFF;
            size = IncomingHandler.PACKET_SIZES[opcode];
        }

        if (size == -1) {
            if (buffer.readableBytes() < 1)
                return;
            size = buffer.readUnsignedByte() & 0xFF;
        } else if (size == -2) {
            if (buffer.readableBytes() < 2)
                return;
            size = buffer.readUnsignedByte() & 0xFF;
        } else if (size == -3) {
            logger.error("Unhandled size for OpCode="+opcode+" size="+size+" Info=" + session.getPlayer().toString());
            ctx.close();
            return;
        }

        if(buffer.readableBytes() < size) {
            /**
             * Not enough bytes
             */
            return;
        }

        try {
            byte[] payload = new byte[size];

            buffer.readBytes(payload);

            session.packetCounter++;

            if (session.getPacketCounter() < GameServer.properties().packetProcessLimit)
                session.queuePacket(new Packet(opcode, Unpooled.copiedBuffer(payload)));

        } finally {
            opcode = -1;
            size = -1;
        }

    }
}

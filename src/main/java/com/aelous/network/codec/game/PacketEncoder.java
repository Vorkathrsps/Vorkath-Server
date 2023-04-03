package com.aelous.network.codec.game;

import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketType;
import com.aelous.network.security.IsaacRandom;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Encodes packets before they're sent to the channel.
 * @author Swiffy
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    private static final Logger logger = LogManager.getLogger(PacketEncoder.class);
    /**
     * The encoder used for encryption of the packet.
     */
    private final IsaacRandom rand;

    /**
     * The GamePacketEncoder constructor.
     * @param rand    The encoder used for the packets.
     */
    public PacketEncoder(IsaacRandom rand) {
        this.rand = rand;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out)
        throws Exception {

        final int opcode = (packet.getOpcode() + rand.nextInt()) & 0xFF;
        PacketType type = packet.getType();
        final int size = packet.getSize();
        if (size == -3) {
            logger.error("{PacketEncoder} Opcode " + packet.getOpcode() + " is not whitelisted.");

            return;
        }
        // Used for finding incorrect client pkt sizes
        if (type == PacketType.FIXED) {
            int currSize = PACKET_SIZES[packet.getOpcode()];
            if (size != currSize) {
                logger.error("{PacketEncoder} Opcode " + packet.getOpcode() + " has defined size " + currSize + " but is actually " + size + ".");
                return;
            }
        } else if (type == PacketType.VARIABLE) {
            int currSize = PACKET_SIZES[packet.getOpcode()];
            if (currSize != -1) {
                logger.error("{PacketEncoder} Opcode " + packet.getOpcode() + "'s size needs to be -1, it's currently " + currSize + ".");
                return;
            }
        } else if (type == PacketType.VARIABLE_SHORT) {
            int currSize = PACKET_SIZES[packet.getOpcode()];
            if (currSize != -2) {
                logger.error("{PacketEncoder} Opcode " + packet.getOpcode() + "'s size needs to be -2, it's currently " + currSize + ".");
                return;
            }
        }

        int finalSize = size + 1;
        switch (type) {
            case VARIABLE -> {
                if (size > 255) { // trying to send more data then we can represent with 8 bits!
                    throw new IllegalArgumentException("Tried to send packet #" + packet.getOpcode() + " length " + size + " in variable-byte packet");
                }
                finalSize++;
            }
            case VARIABLE_SHORT -> {
                if (size > 65535) { // trying to send more data then we can represent with 8 bits!
                    throw new IllegalArgumentException("Tried to send packet #" + packet.getOpcode() + " length " + size + " in variable-short packet");
                }
                finalSize += 2;
            }
            default -> {
            }
        }

        // Create a new buffer
        ByteBuf buffer = Unpooled.buffer(finalSize);

        // Write opcode
        buffer.writeByte(opcode);

        // Write packet size
        switch (type) {
            case VARIABLE -> buffer.writeByte((byte) size);
            case VARIABLE_SHORT -> buffer.writeShort((short) size);
            default -> {
            }
        }

        // Write packet
        buffer.writeBytes(packet.getBuffer());

        // Write the packet to the out buffer
        out.writeBytes(buffer);

    }
    public static final int[] PACKET_SIZES = new int[256];
    //The server PacketEncoder class PACKET_SIZES array should match the Client ServerToClientPackets class PACKET_SIZES array.
    static {
        PACKET_SIZES[0] = 0;
        PACKET_SIZES[1] = 0;
        PACKET_SIZES[2] = -3;
        PACKET_SIZES[3] = 0;
        PACKET_SIZES[4] = 6;
        PACKET_SIZES[5] = -3;
        PACKET_SIZES[6] = 1;
        PACKET_SIZES[7] = 5;
        PACKET_SIZES[8] = 4;
        PACKET_SIZES[9] = 4;
        PACKET_SIZES[10] = 6;
        PACKET_SIZES[11] = 0;
        PACKET_SIZES[12] = -3;
        PACKET_SIZES[13] = -1;
        PACKET_SIZES[14] = -3;
        PACKET_SIZES[15] = -3;
        PACKET_SIZES[16] = -3;
        PACKET_SIZES[17] = -3;
        PACKET_SIZES[18] = -3;
        PACKET_SIZES[19] = -3;
        PACKET_SIZES[20] = -3;
        PACKET_SIZES[21] = -3;
        PACKET_SIZES[22] = -3;
        PACKET_SIZES[23] = -3;
        PACKET_SIZES[24] = 1;
        PACKET_SIZES[25] = -3;
        PACKET_SIZES[26] = -3;
        PACKET_SIZES[27] = -1;
        PACKET_SIZES[28] = -3;
        PACKET_SIZES[29] = -3;
        PACKET_SIZES[30] = -3;
        PACKET_SIZES[31] = -3;
        PACKET_SIZES[32] = -3;
        PACKET_SIZES[33] = -3;
        PACKET_SIZES[34] = -2;
        PACKET_SIZES[35] = 4;
        PACKET_SIZES[36] = 3;
        PACKET_SIZES[37] = -3;
        PACKET_SIZES[38] = 2;
        PACKET_SIZES[39] = -3;
        PACKET_SIZES[40] = -3;
        PACKET_SIZES[41] = -3;
        PACKET_SIZES[42] = -3;
        PACKET_SIZES[43] = -3;
        PACKET_SIZES[44] = 8;
        PACKET_SIZES[45] = 0;
        PACKET_SIZES[46] = -3;
        PACKET_SIZES[47] = -3;
        PACKET_SIZES[48] = -3;
        PACKET_SIZES[49] = -3;
        PACKET_SIZES[50] = -1;
        PACKET_SIZES[51] = -1;
        PACKET_SIZES[52] = -3;
        PACKET_SIZES[53] = -2;
        PACKET_SIZES[54] = 4;
        PACKET_SIZES[55] = 30;
        PACKET_SIZES[56] = -3;
        PACKET_SIZES[57] = 0;
        PACKET_SIZES[58] = -3;
        PACKET_SIZES[59] = -3;
        PACKET_SIZES[60] = -2;
        PACKET_SIZES[61] = 1;
        PACKET_SIZES[62] = 0;
        PACKET_SIZES[63] = -3;
        PACKET_SIZES[64] = 2;
        PACKET_SIZES[65] = -2;
        PACKET_SIZES[66] = -2;
        PACKET_SIZES[67] = -3;
        PACKET_SIZES[68] = 0;
        PACKET_SIZES[69] = -3;
        PACKET_SIZES[70] = 8;
        PACKET_SIZES[71] = 5;
        PACKET_SIZES[72] = 2;
        PACKET_SIZES[73] = 4;
        PACKET_SIZES[74] = 2;
        PACKET_SIZES[75] = 4;
        PACKET_SIZES[76] = -3;
        PACKET_SIZES[77] = -3;
        PACKET_SIZES[78] = 0;
        PACKET_SIZES[79] = 6;
        PACKET_SIZES[80] = -3;
        PACKET_SIZES[81] = -2;
        PACKET_SIZES[82] = -3;
        PACKET_SIZES[83] = -3;
        PACKET_SIZES[84] = 7;
        PACKET_SIZES[85] = 2;
        PACKET_SIZES[86] = 0;
        PACKET_SIZES[87] = 6;
        PACKET_SIZES[88] = -3;
        PACKET_SIZES[89] = -3;
        PACKET_SIZES[90] = -3;
        PACKET_SIZES[91] = -3;
        PACKET_SIZES[92] = -3;
        PACKET_SIZES[93] = -3;
        PACKET_SIZES[94] = -3;
        PACKET_SIZES[95] = -3;
        PACKET_SIZES[96] = -3;
        PACKET_SIZES[97] = 4;
        PACKET_SIZES[98] = -3;
        PACKET_SIZES[99] = 1;
        PACKET_SIZES[100] = -3;
        PACKET_SIZES[101] = 2;
        PACKET_SIZES[102] = -3;
        PACKET_SIZES[103] = -3;
        PACKET_SIZES[104] = -1;
        PACKET_SIZES[105] = 4;
        PACKET_SIZES[106] = 1;
        PACKET_SIZES[107] = 0;
        PACKET_SIZES[108] = -3;
        PACKET_SIZES[109] = 0;
        PACKET_SIZES[110] = 1;
        PACKET_SIZES[111] = 1;
        PACKET_SIZES[112] = -3;
        PACKET_SIZES[113] = 1;
        PACKET_SIZES[114] = 2;
        PACKET_SIZES[115] = 1;
        PACKET_SIZES[116] = 7;
        PACKET_SIZES[117] = 15;
        PACKET_SIZES[118] = -3;
        PACKET_SIZES[119] = -3;
        PACKET_SIZES[120] = -3;
        PACKET_SIZES[121] = 4;
        PACKET_SIZES[122] = 6;
        PACKET_SIZES[123] = -3;
        PACKET_SIZES[124] = -3;
        PACKET_SIZES[125] = -3;
        PACKET_SIZES[126] = -2;
        PACKET_SIZES[127] = 3;
        PACKET_SIZES[128] = 6;
        PACKET_SIZES[129] = -2;
        PACKET_SIZES[130] = -3;
        PACKET_SIZES[131] = -3;
        PACKET_SIZES[132] = -3;
        PACKET_SIZES[133] = -3;
        PACKET_SIZES[134] = 6;
        PACKET_SIZES[135] = 2;
        PACKET_SIZES[136] = -3;
        PACKET_SIZES[137] = 1;
        PACKET_SIZES[138] = 1;
        PACKET_SIZES[139] = -3;
        PACKET_SIZES[140] = -3;
        PACKET_SIZES[141] = -3;
        PACKET_SIZES[142] = 2;
        PACKET_SIZES[143] = 3;
        PACKET_SIZES[144] = -3;
        PACKET_SIZES[145] = -3;
        PACKET_SIZES[146] = -3;
        PACKET_SIZES[147] = 14;
        PACKET_SIZES[148] = -3;
        PACKET_SIZES[149] = -3;
        PACKET_SIZES[150] = -3;
        PACKET_SIZES[151] = 4;
        PACKET_SIZES[152] = -3;
        PACKET_SIZES[153] = -3;
        PACKET_SIZES[154] = -3;
        PACKET_SIZES[155] = -3;
        PACKET_SIZES[156] = 3;
        PACKET_SIZES[157] = -3;
        PACKET_SIZES[158] = -3;
        PACKET_SIZES[159] = -3;
        PACKET_SIZES[160] = 4;
        PACKET_SIZES[161] = -3;
        PACKET_SIZES[162] = -3;
        PACKET_SIZES[163] = -3;
        PACKET_SIZES[164] = 2;
        PACKET_SIZES[165] = -3;
        PACKET_SIZES[166] = 6;
        PACKET_SIZES[167] = 0;
        PACKET_SIZES[168] = -3;
        PACKET_SIZES[169] = -3;
        PACKET_SIZES[170] = -3;
        PACKET_SIZES[171] = 5;
        PACKET_SIZES[172] = -3;
        PACKET_SIZES[173] = -3;
        PACKET_SIZES[174] = 7;
        PACKET_SIZES[175] = -1;
        PACKET_SIZES[176] = 10;
        PACKET_SIZES[177] = 6;
        PACKET_SIZES[178] = 0;
        PACKET_SIZES[179] = -3;
        PACKET_SIZES[180] = 1;
        PACKET_SIZES[181] = -3;
        PACKET_SIZES[182] = -3;
        PACKET_SIZES[183] = 1;
        PACKET_SIZES[184] = -3;
        PACKET_SIZES[185] = 2;
        PACKET_SIZES[186] = -3;
        PACKET_SIZES[187] = -1;
        PACKET_SIZES[188] = 2;
        PACKET_SIZES[189] = 4;
        PACKET_SIZES[190] = 5;
        PACKET_SIZES[191] = 4;
        PACKET_SIZES[192] = -3;
        PACKET_SIZES[193] = 6;
        PACKET_SIZES[194] = -3;
        PACKET_SIZES[195] = -3;
        PACKET_SIZES[196] = -2;
        PACKET_SIZES[197] = -3;
        PACKET_SIZES[198] = -1;
        PACKET_SIZES[199] = -3;
        PACKET_SIZES[200] = 4;
        PACKET_SIZES[201] = -3;
        PACKET_SIZES[202] = 0;
        PACKET_SIZES[203] = 2;
        PACKET_SIZES[204] = -3;
        PACKET_SIZES[205] = -3;
        PACKET_SIZES[206] = 3;
        PACKET_SIZES[207] = -1;
        PACKET_SIZES[208] = 4;
        PACKET_SIZES[209] = -1;
        PACKET_SIZES[210] = 0;
        PACKET_SIZES[211] = -3;
        PACKET_SIZES[212] = -3;
        PACKET_SIZES[213] = 4;
        PACKET_SIZES[214] = -1;
        PACKET_SIZES[215] = -1;
        PACKET_SIZES[216] = -3;
        PACKET_SIZES[217] = -2;
        PACKET_SIZES[218] = 2;
        PACKET_SIZES[219] = 0;
        PACKET_SIZES[220] = 0;
        PACKET_SIZES[221] = 1;
        PACKET_SIZES[222] = -2;
        PACKET_SIZES[223] = -2;
        PACKET_SIZES[224] = -3;
        PACKET_SIZES[225] = -3;
        PACKET_SIZES[226] = -3;
        PACKET_SIZES[227] = -3;
        PACKET_SIZES[228] = -3;
        PACKET_SIZES[229] = -3;
        PACKET_SIZES[230] = 8;
        PACKET_SIZES[231] = -3;
        PACKET_SIZES[232] = -3;
        PACKET_SIZES[233] = -3;
        PACKET_SIZES[234] = -3;
        PACKET_SIZES[235] = -3;
        PACKET_SIZES[236] = -3;
        PACKET_SIZES[237] = 6;
        PACKET_SIZES[238] = -3;
        PACKET_SIZES[239] = 5;
        PACKET_SIZES[240] = 2;
        PACKET_SIZES[241] = -2;
        PACKET_SIZES[242] = -3;
        PACKET_SIZES[243] = 4;
        PACKET_SIZES[244] = -3;
        PACKET_SIZES[245] = -3;
        PACKET_SIZES[246] = 6;
        PACKET_SIZES[247] = -3;
        PACKET_SIZES[248] = 8;
        PACKET_SIZES[249] = 3;
        PACKET_SIZES[250] = 0;
        PACKET_SIZES[251] = -1;
        PACKET_SIZES[252] = -2;
        PACKET_SIZES[253] = -1;
        PACKET_SIZES[254] = 6;
    }
}

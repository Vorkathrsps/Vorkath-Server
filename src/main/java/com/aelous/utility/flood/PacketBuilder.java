package com.aelous.utility.flood;

public class PacketBuilder {

    private final int opcode;
    private int position;
    private byte[] buffer;

    public PacketBuilder(int opcode) {
        this.opcode = opcode;
        this.position = 0;
        this.buffer = new byte[2000];
    }

    public PacketBuilder put(int i) {
        buffer[position++] = (byte) i;
        return this;
    }

    public void puts(int i) {
        buffer[position - i - 1] = (byte) i;
    }

    public PacketBuilder putSignedByteMin(int j) {
        buffer[position++] = (byte) (128 - j);
        return this;
    }

    public PacketBuilder writeReverseDataA(int i, byte abyte0[], int j) {
        for (int k = (i + j) - 1; k >= i; k--)
            buffer[position++] = (byte) (abyte0[k] + 128);
        return this;
    }

    public PacketBuilder putLong(long value) {
        try {
            buffer[position++] = (byte) (value >> 56);
            buffer[position++] = (byte) (value >> 48);
            buffer[position++] = (byte) (value >> 40);
            buffer[position++] = (byte) (value >> 32);
            buffer[position++] = (byte) (value >> 24);
            buffer[position++] = (byte) (value >> 16);
            buffer[position++] = (byte) (value >> 8);
            buffer[position++] = (byte) value;
        } catch (RuntimeException runtimeexception) {
            throw new RuntimeException();
        }
        return this;
    }

    public PacketBuilder putShort(int value) {
        buffer[position++] = (byte) (value >> 8);
        buffer[position++] = (byte) value;
        return this;
    }

    public PacketBuilder putString(String s) {
        System.arraycopy(s.getBytes(), 0, buffer, position, s.length());
        position += s.length();
        buffer[position++] = 10;
        return this;
    }

    public PacketBuilder putVariableSizeByte(int size) {
        buffer[position - size - 1] = (byte) size;
        return this;
    }

    public PacketBuilder puts(byte[] tmp, int len, int off) {
        for (int i = off; i < off + len; i++) {
            buffer[position++] = tmp[i];
        }
        return this;
    }

    public PacketBuilder putDWordBigEndian(int value) {
        buffer[position++] = (byte) (value >> 16);
        buffer[position++] = (byte) (value >> 8);
        buffer[position++] = (byte) value;
        return this;
    }

    public PacketBuilder putInt(int i) {
        buffer[position++] = (byte) (i >> 24);
        buffer[position++] = (byte) (i >> 16);
        buffer[position++] = (byte) (i >> 8);
        buffer[position++] = (byte) i;
        return this;
    }

    public PacketBuilder putUnsignedWordBigEndian(int i) {
        buffer[position++] = (byte) i;
        buffer[position++] = (byte) (i >> 8);
        return this;
    }

    public PacketBuilder putUnsignedWordA(int j) {
        buffer[position++] = (byte) (j >> 8);
        buffer[position++] = (byte) (j + 128);
        return this;
    }

    public PacketBuilder putSignedBigEndian(int j) {
        buffer[position++] = (byte) (j + 128);
        buffer[position++] = (byte) (j >> 8);
        return this;
    }

    public PacketBuilder putC(int i) {
        buffer[position++] = (byte) -i;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getOpcode() {
        return opcode;
    }
}

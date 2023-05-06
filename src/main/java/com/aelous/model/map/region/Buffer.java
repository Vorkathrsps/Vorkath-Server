package com.aelous.model.map.region;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a buffer.
 * <p>
 * Taken from the 317 client.
 *
 * @author Professor Oak
 */
public class Buffer {
    private static final Logger logger = LogManager.getLogger(Buffer.class);
    private byte[] buffer;
    public int offset;

    public Buffer(byte[] buffer) {
        this.buffer = buffer;
        this.offset = 0;
    }

    public void skip(int length) {
        offset += length;
    }

    public void setOffset(int location) {
        offset = location;
    }

    public void setOffset(long location) {
        offset = (int) location;
    }

    public int length() {
        return buffer.length;
    }

    public byte getByte() {
        return buffer[offset++];
    }

    public int getUByte() {
        return buffer[offset++] & 0xff;
    }

    public int getShort() {
        return (getByte() << 8) + getByte();
    }

    public int getUShort() {
        return (getUByte() << 8) + getUByte();
    }

    public int getInt() {
        return (getUByte() << 24) + (getUByte() << 16) + (getUByte() << 8) + getUByte();
    }

    public long getLong() {
        return ((long)getUByte() << 56) + ((long)getUByte() << 48) + ((long)getUByte() << 40) + ((long)getUByte() << 32) + (getUByte() << 24) + (getUByte() << 16) + (getUByte() << 8) + getUByte();
    }

    public int readUnsignedWord() {
        offset += 2;
        return ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] & 0xff);
    }

    public int getUnsignedShortSmart() {
        int i = buffer[offset] & 0xff;
        if (i < 128) {
            return getUByte();
        } else {
            return getUShort() - 32768;
        }
    }
    public int readUnsignedShortSmart() {
        int peek = buffer[offset] & 0xFF;
        return peek < 128 ? getUByte() : getUShort() - 0x8000;
    }

    public int readUnsignedIntSmartShortCompat() {
        int var1 = 0;

        int var2;
        for (var2 = readUnsignedShortSmart(); var2 == 32767; var2 = readUnsignedShortSmart())
        {
            var1 += 32767;
        }

        var1 += var2;
        return var1;
    }
    public int readSmart() {
        try {
            int value = 0;
            int ptr;
            for (ptr = getUnsignedShortSmart(); 32767 == ptr; ptr = getUnsignedShortSmart())
                value += 32767;
            value += ptr;
            return value;
        } catch (Exception e) {
            logger.catching(e);
        }
        return -1;
    }

    public String getString() {
        int i = offset;
        while (buffer[offset++] != 10)
            ;
        return new String(buffer, i, offset - i - 1);
    }

    public byte[] getBytes() {
        int i = offset;
        while (buffer[offset++] != 10)
            ;
        byte abyte0[] = new byte[offset - i - 1];
        System.arraycopy(buffer, i, abyte0, i - i, offset - 1 - i);
        return abyte0;
    }

    public byte[] read(int length) {
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            b[i] = buffer[offset++];
        }
        return b;
    }
}

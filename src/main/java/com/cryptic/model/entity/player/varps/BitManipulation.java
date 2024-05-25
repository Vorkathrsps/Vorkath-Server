package com.cryptic.model.entity.player.varps;

public class BitManipulation {

    public static final int[] BIT_SIZES = new int[32];

    static {
        int size = 2;
        for (int i = 0; i < BIT_SIZES.length; i++) {
            BIT_SIZES[i] = size - 1;
            size += size;
        }
    }

    /**
     * Extracts the value from the specified bits of the packed integer.
     *
     * @param packed the integer containing the bit-packed values.
     * @param startBit the starting bit position (inclusive).
     * @param endBit the ending bit position (inclusive).
     * @return the value extracted from the specified bit range.
     */
    public static int getBit(int packed, int startBit, int endBit) {
        int position = BIT_SIZES[endBit - startBit];
        return (packed >>> startBit) & position;
    }

    /**
     * Sets the value in the specified bits of the packed integer.
     *
     * @param packed the original packed integer.
     * @param startBit the starting bit position (inclusive).
     * @param endBit the ending bit position (inclusive).
     * @param value the value to set in the specified bit range.
     * @return the new packed integer with the updated bit range.
     */
    public static int setBit(int packed, int startBit, int endBit, int value) {
        int area = BIT_SIZES[endBit - startBit] << startBit;
        return (packed & (~area)) | value << startBit & area;
    }
}

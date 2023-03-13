package com.aelous.model.entity.combat.formula.accuracy.seed;

import java.security.SecureRandom;

public class SeedGenerator {

    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] seed = random.generateSeed(20); // generate a 20-byte seed value
        String hex = bytesToHex(seed); // convert the seed value to hexadecimal
        System.out.println(hex); // print the seed value
    }

    private static final char[] HEX_ARRAY = "B05F09FB015155F6A10B61F0105109F581FA1FB8".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}

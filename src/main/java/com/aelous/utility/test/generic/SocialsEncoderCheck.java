package com.aelous.utility.test.generic;

import com.aelous.utility.Utils;

public class SocialsEncoderCheck {

    public static void main(String[] args) {
        String name = "te1tINg3n";
        long ln = Utils.stringToLong(name);
        System.out.printf("%s %s %s = %s %s%n",
            ln,
            Utils.stringToLong(name.toLowerCase()),
            Utils.stringToLong(name.toUpperCase()),
            longToString(ln),
            longToStringClient(ln)
           );
    }

    public static final char[] VALID_CHARACTERS = {'_', 'a', 'b', 'c', 'd',
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&',
        '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"',
        '[', ']', '|', '?', '/', '`'
    };

    public static String longToString(long l) {
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = VALID_CHARACTERS[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    private static final char[] validChars = { '_', 'a', 'b', 'c', 'd', 'e',
        'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
        's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
        '5', '6', '7', '8', '9' };

    public static String longToStringClient(long l) {
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = validChars[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }
}

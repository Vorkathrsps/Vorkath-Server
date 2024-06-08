package com.cryptic.clientscripts.util;

import lombok.Getter;

import java.awt.*;

@Getter
public class JagexColor {

    public final int packed;

    public JagexColor(int red, int green, int blue) {
        this.packed = (red & 0x1F) << 10
            | (green & 0x1F) << 5
            | (blue & 0x1F);
    }

    public int getRed() {
        return (packed >> 10) & 0x1F;
    }

    public int getGreen() {
        return (packed >> 5) & 0x1F;
    }

    public int getBlue() {
        return packed & 0x1F;
    }

    @Override
    public String toString() {
        return "Rs15BitColour(" +
            "red=" + getRed() + ", " +
            "green=" + getGreen() + ", " +
            "blue=" + getBlue() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JagexColor that = (JagexColor) o;
        return packed == that.packed;
    }

    public static JagexColor hex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() == 3) {
            hex = "" + hex.charAt(0) + hex.charAt(0) +
                hex.charAt(1) + hex.charAt(1) +
                hex.charAt(2) + hex.charAt(2);
        }
        if (hex.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color format");
        }
        int red = Integer.parseInt(hex.substring(0, 2), 16) >> 3;
        int green = Integer.parseInt(hex.substring(2, 4), 16) >> 3;
        int blue = Integer.parseInt(hex.substring(4, 6), 16) >> 3;
        return new JagexColor(red, green, blue);
    }

    public static JagexColor color(Color color) {
        int red = color.getRed() >> 3;
        int green = color.getGreen() >> 3;
        int blue = color.getBlue() >> 3;
        return new JagexColor(red, green, blue);
    }


    @Override
    public int hashCode() {
        return Integer.hashCode(packed);
    }

    public static final JagexColor BLACK = new JagexColor(0, 0, 0);
    public static final JagexColor WHITE = new JagexColor(31, 31, 31);
    public static final JagexColor RED = new JagexColor(31, 0, 0);
    public static final JagexColor GREEN = new JagexColor(0, 31, 0);
    public static final JagexColor BLUE = new JagexColor(0, 0, 31);
    public static final JagexColor YELLOW = new JagexColor(31, 31, 0);
    public static final JagexColor CYAN = new JagexColor(0, 31, 31);
    public static final JagexColor MAGENTA = new JagexColor(31, 0, 31);
    public static final JagexColor ORANGE = new JagexColor(31, 16, 0);
    public static final JagexColor PURPLE = new JagexColor(15, 0, 15);
    public static final JagexColor BROWN = new JagexColor(15, 8, 0);
    public static final JagexColor PINK = new JagexColor(31, 15, 15);
    public static final JagexColor LIGHT_GREEN = new JagexColor(15, 31, 15);
    public static final JagexColor LIGHT_BLUE = new JagexColor(15, 15, 31);
    public static final JagexColor DARK_RED = new JagexColor(15, 0, 0);
    public static final JagexColor DARK_GREEN = new JagexColor(0, 15, 0);
    public static final JagexColor DARK_BLUE = new JagexColor(0, 0, 15);
    public static final JagexColor GRAY = new JagexColor(15, 15, 15);
    public static final JagexColor LIGHT_GRAY = new JagexColor(23, 23, 23);
    public static final JagexColor DARK_GRAY = new JagexColor(8, 8, 8);
    public static final JagexColor LIGHT_YELLOW = new JagexColor(31, 31, 15);
    public static final JagexColor LIGHT_CYAN = new JagexColor(15, 31, 31);
    public static final JagexColor LIGHT_MAGENTA = new JagexColor(31, 15, 31);
    public static final JagexColor TEAL = new JagexColor(0, 15, 15);
    public static final JagexColor NAVY = new JagexColor(0, 0, 15);
    public static final JagexColor MAROON = new JagexColor(15, 0, 0);
    public static final JagexColor OLIVE = new JagexColor(15, 15, 0);
    public static final JagexColor AQUA = new JagexColor(0, 31, 23);
    public static final JagexColor CORAL = new JagexColor(31, 11, 8);
    public static final JagexColor SALMON = new JagexColor(31, 17, 17);
    public static final JagexColor GOLD = new JagexColor(31, 27, 0);
    public static final JagexColor SILVER = new JagexColor(27, 27, 27);
    public static final JagexColor PEACH = new JagexColor(31, 21, 17);
    public static final JagexColor LAVENDER = new JagexColor(27, 20, 31);
    public static final JagexColor MINT = new JagexColor(20, 31, 20);
    public static final JagexColor INDIGO = new JagexColor(11, 0, 23);
    public static final JagexColor VIOLET = new JagexColor(23, 0, 23);
    public static final JagexColor OSRS_VIOLET = new JagexColor(31, 25, 31);

}

package com.aelous.utility;

public enum Color {

    GOLD("AD800F"), MEDIUMGREY("888888"), BLUE("0000FF"), ROYAL("4C00AA"), RUNITE("5287A5"),
    HOTPINK("A30072"), NAVYBLUE("00003A"), VIOLET("FF00FF"), DARKBROWN("2D0E00"), BURNTORANGE("912A01"),
    GRASS("03700C"), BLACK("000000"), LIGHTBROWN("6B2100"), BROWN("421400"), WHITE("FFFFFF"),
    ANCIENTWARRIOR("DDC996"), PURPLE("7800FF"), LIGHTGREY("CCCCCC"), RED("FF0000"), GOLDENROD("E5A910"),
    LIME("33FF00"), MITHRIL("374D96"), DARKGREY("444444"), DRAGON("930A0A"), MEDRED("CA0D0D"),
    LIGHTORANGE("FF9040"), YELLOW("FFFF00"), BARROWS("131613"), CYAN("00FFFF"), ADAMANTITE("63965E"),
    MAROON("800000"), GREEN("65280"), LIGHT_GREEN("006000"), DARK_RED("6f0000"), DARK_GREEN("006600"),
    OLIVE("808000"), ORANGE("ff981f"), ORANGE_2("ff7000"), ORANGE_RED("FF4500"), COOL_BLUE("0040ff"),

    RAID_PURPLE("ef20ff")
    ;

    Color(String colorValue) {
        this.colorValue = colorValue;
    }

    public String getColorValue() {
        return colorValue;
    }

    private String colorValue;

    public String wrapValue(Object obj, String tagName) {
        return "<" + tagName + "=" + colorValue + ">" + obj.toString() + "</" + tagName + ">";
    }

    public String wrap(String s) {
        return tag() + s + "</col>";
    }

    public String tag() {
        return "<col=" + colorValue + ">";
    }

    public String wrapCol(Object obj) {
        return wrapValue(obj, "col");
    }

    public String wrapShad(Object obj) {
        return wrapValue(obj, "shad");
    }

    public String wrapUl(Object obj) {
        return wrapValue(obj, "ul");
    }
}

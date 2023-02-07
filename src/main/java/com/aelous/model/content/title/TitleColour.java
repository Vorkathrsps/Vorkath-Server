package com.aelous.model.content.title;

public class TitleColour {

    private final String name;

    private final String format;

    public TitleColour(String name, String format) {
        this.name = name;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }
}

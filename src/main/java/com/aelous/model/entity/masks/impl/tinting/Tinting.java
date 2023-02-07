package com.aelous.model.entity.masks.impl.tinting;

public class Tinting {
    private final short delay;
    private final short duration;
    private final byte hue;
    private final byte saturation;
    private final byte luminance;
    private final byte opacity;

    public Tinting(short delay, short duration, byte hue, byte saturation, byte luminance, byte opacity) {
        this.delay = delay;
        this.duration = duration;
        this.hue = hue;
        this.saturation = saturation;
        this.luminance = luminance;
        this.opacity = opacity;
    }

    //tinting(hue = 0, saturation = 6, luminance = 28, opacity = 112, delay = 0, duration = 240)

    public short delay() {
        return delay;
    }

    public short duration() {
        return duration;
    }

    public byte hue() {
        return hue;
    }

    public byte saturation() {
        return saturation;
    }
    public byte luminance() {
        return luminance;
    }
    public byte opacity() {
        return opacity;
    }

}

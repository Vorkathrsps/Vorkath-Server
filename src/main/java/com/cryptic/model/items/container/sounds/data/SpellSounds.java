package com.cryptic.model.items.container.sounds.data;

import lombok.Data;

@Data
public class SpellSounds {
    private int cast_sound;
    private int hit_sound;
    private int fail_sound;

    @Override
    public String toString() {
        return "SpellSounds{" +
            "cast_sound=" + cast_sound +
            ", hit_sound=" + hit_sound +
            ", fail_sound=" + fail_sound +
            '}';
    }

    public int getCastSound() {
        return cast_sound;
    }

    public int getHitSound() {
        return hit_sound;
    }

    public int getFailSound() {
        return fail_sound;
    }
}

package com.cryptic.model.items.container.presets;

import com.cryptic.model.entity.player.MagicSpellbook;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class SpellBookTypeAdapter extends TypeAdapter<MagicSpellbook> {
    @Override
    public void write(JsonWriter out, MagicSpellbook value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.ordinal());
        }
    }

    @Override
    public MagicSpellbook read(JsonReader in) throws IOException {
        int ordinal = in.nextInt();
        return MagicSpellbook.values()[ordinal];
    }
}

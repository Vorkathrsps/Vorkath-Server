package com.cryptic.model.items.container.presets;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class AttributeKeyTypeAdapter extends TypeAdapter<AttributeKey> {
    @Override
    public void write(JsonWriter out, AttributeKey value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.ordinal());
        }
    }
    // yeah

    @Override
    public AttributeKey read(JsonReader in) throws IOException {
        int ordinal = in.nextInt();
        return AttributeKey.values()[ordinal];
    }
}

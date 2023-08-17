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
            out.value(value.name());
        }
    }


    @Override
    public AttributeKey read(JsonReader in) throws IOException {
        String enumName = in.nextString();
        try {
            return AttributeKey.valueOf(AttributeKey.class, enumName);
        } catch (IllegalArgumentException e) {
            // Handle the case of an invalid enum name here
            throw new IllegalArgumentException("Invalid enum name: " + enumName, e);
        }
    }

}

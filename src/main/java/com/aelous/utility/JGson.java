package com.aelous.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author PVE
 * @Since augustus 15, 2020
 */
public class JGson {

    public static Gson get() {
        return new GsonBuilder().registerTypeAdapterFactory(new GsonPropertyValidator()).setPrettyPrinting().create();
    }
}

package com.aelous.model.inter.clan;

import com.aelous.GameEngine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the saving and loading of all clan chat channels.
 *
 * @author PVE
 * @Since juli 07, 2020
 */
public class ClanRepository {

    private static final Logger logger = LogManager.getLogger(ClanRepository.class);

    /**
     * Map of all active clan chat channels.
     */
    private static Map<String, Clan> CLANS = new HashMap<>();

    /**
     * Returns the clan.
     *
     * @param name
     * @return
     */
    public static Clan get(String name) {
        return CLANS.get(name);
    }

    /**
     * Adds the clan.
     *
     * @param clan
     */
    public static void add(Clan clan) {
        CLANS.put(clan.getOwner(), clan);
    }

    private static final Gson LOADER, SAVER;
    static {
        LOADER = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        SAVER = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().disableHtmlEscaping().create();
    }

    /**
     * Loads all clans and puts them into the map.
     */
    public static void load() {
        try {
            if (!new File("./data/saves/clans/world_clan_list.json").exists())
                return;
            CLANS = LOADER.fromJson(new FileReader("./data/saves/clans/world_clan_list.json"), new TypeToken<Map<String, Clan>>() {
            }.getType());
            if (CLANS == null)
                CLANS = new HashMap<>();
            for (Clan clan : CLANS.values()) {
                clan.init();
            }
        } catch (FileNotFoundException e) {
            logger.catching(e);
        }
    }

    /**
     * Saves all clans into a json file.
     */
    public static void save() {
        GameEngine.getInstance().submitLowPriority(() -> {
            try {
                try (FileWriter fileWriter = new FileWriter("./data/saves/clans/world_clan_list.json")) {
                    fileWriter.write(SAVER.toJson(CLANS));
                } catch (final Exception e) {
                    logger.catching(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

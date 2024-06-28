package com.cryptic.filestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.areas.burthope.warriors_guild.dialogue.Shanomi;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.NpcSpawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.SHANOMI;

public class NpcLoader {

    private static final Logger logger = LogManager.getLogger(World.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new AfterburnerModule());

    public static void loadAllNpcSpawns() {
        long start = System.currentTimeMillis();
        int totalNpcs = 0;

        String globalFilePath = "data/def/npcs/worldspawns/npc_spawns_global.json";
        int globalNpcCount = countNpcsInFile(globalFilePath);
        totalNpcs += globalNpcCount;

        try (ProgressBar progressBar = new ProgressBarBuilder()
            .setTaskName("Loading NPC Spawns Global")
            .setInitialMax(globalNpcCount)
            .setStyle(ProgressBarStyle.ASCII)
            .build()) {
            loadNpcSpawns(globalFilePath, progressBar);
        }

        String directoryPath = "data/def/npcs/worldspawns/" + GameServer.serverType.name().toLowerCase();
        File dir = new File(directoryPath);

        if (dir.exists() && dir.isDirectory()) {
            FilenameFilter jsonFilter = (d, name) -> name.toLowerCase().endsWith(".json");
            File[] files = dir.listFiles(jsonFilter);

            if (files != null) {
                int dirNpcCount = 0;
                for (File file : files) {
                    dirNpcCount += countNpcsInFile(file.getAbsolutePath());
                }
                totalNpcs += dirNpcCount;

                try (ProgressBar progressBar = new ProgressBarBuilder()
                    .setTaskName("Loading NPC Spawns: " + GameServer.serverType.getName())
                    .setInitialMax(dirNpcCount)
                    .setStyle(ProgressBarStyle.ASCII)
                    .build()) {
                    for (File file : files) {
                        progressBar.setExtraMessage(file.getName());
                        loadNpcSpawns(file.getAbsolutePath(), progressBar);
                    }
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        logger.info("Loaded {} World Npc Spawns. It took {}ms.", totalNpcs, elapsed);
    }

    private static void loadNpcSpawns(String filePath, ProgressBar progressBar) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            NpcSpawn[] spawns = objectMapper.readValue(reader, NpcSpawn[].class);
            for (NpcSpawn sp : spawns) {
                Tile spawnTile = new Tile(sp.x, sp.y, sp.z);
                NPC npc = NPC.of(sp.id, spawnTile);
                npc.spawnDirection(sp.dir());
                npc.walkRadius(sp.walkRange);

                handleSpecialNpcCases(npc);

                World.getWorld().registerNpc(npc);
                progressBar.step();
            }
        } catch (Exception e) {
            logger.error("Failed to load NPC spawns from file: {}", filePath, e);
        }
    }

    private static void handleSpecialNpcCases(NPC npc) {
        if (npc.id() == SHANOMI) {
            Shanomi.shoutMessage(npc);
        }

        KrakenBoss.onNpcSpawn(npc);

        if (npc.id() == NpcIdentifiers.VENENATIS_6610) {
            npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
        }

        if (npc.def().gwdRoomNpc) {
            npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 40);
        }
    }

    private static int countNpcsInFile(String filePath) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            NpcSpawn[] spawns = objectMapper.readValue(reader, NpcSpawn[].class);
            return spawns.length;
        } catch (Exception e) {
            logger.error("Failed to count NPCs in file: {}", filePath, e);
            return 0;
        }
    }
}

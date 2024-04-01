package com.cryptic.model.entity.npc.droptables;

import com.cryptic.cache.definitions.NpcDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class NpcDropRepository {

    public static Int2ObjectMap<NpcDropTable> tables = new Int2ObjectArrayMap<>();
    private static final Logger logger = LogManager.getLogger(NpcDropRepository.class);
    public static NpcDropTable forNPC(int npc) {
        return tables.getOrDefault(npc, null);
    }

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static void loadAll(File dir) {
        mapper.findAndRegisterModules();
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.isDirectory()) {
                loadAll(f);
            } else {
                try {
                    if (f.getName().endsWith(".yaml")) {
                        NpcDropTable t = load(f);
                        if (t == null) continue;
                        t.postLoad();
                        for (var id : t.getNpcId()) tables.put(id, t);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading drop file " + f);
                    e.printStackTrace();
                }
            }
        }
        logger.info("Loaded " + tables.size() + " npc drop tables.");
    }

    public static NpcDropTable load(File file) {
        try {
            return mapper.readValue(file, NpcDropTable.class);
        } catch (IOException e) {
            System.out.println("Error loading drops in " + file.getName());
        }
        return null;
    }

    public static int getDropNpcId(int id) {
        return NpcDefinition.cached.get(id).id;
    }
}

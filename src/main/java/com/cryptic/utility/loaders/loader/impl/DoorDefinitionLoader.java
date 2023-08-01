package com.cryptic.utility.loaders.loader.impl;

import com.cryptic.GameServer;
import com.cryptic.utility.loaders.loader.DefinitionLoader;
import com.cryptic.model.map.object.doors.Door;
import com.cryptic.model.map.object.doors.Doors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.FileInputStream;

public class DoorDefinitionLoader extends DefinitionLoader {

    private static final Logger logger = LogManager.getLogger(DoorDefinitionLoader.class);

    @Override
    public void load() throws Exception {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("./data/map/doorpairs.bin"));
            while(dis.available() > 0) {
                int id = dis.readInt();
                int toId = dis.readInt();
                boolean closed = dis.readBoolean();
                boolean open = dis.readBoolean();
                Door door = new Door(id, toId, closed, open);
                Doors.CACHE.add(door);
            }
            //logger.info("Loaded "+ Doors.CACHE.size() +" door definitions.");
        } catch (Exception e) {
            logger.error("sadge", e);
        }
    }

    @Override
    public String file() {
        return GameServer.properties().definitionsDirectory + "door_definitions.json";
    }

}

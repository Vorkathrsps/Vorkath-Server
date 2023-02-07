package com.aelous.utility;

import com.aelous.cache.definitions.DefinitionRepository;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.cache.definitions.ObjectDefinition;
import com.aelous.model.items.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Scanner;

/**
 * Created by Bart on 8/9/2015.
 * <p>
 * Holds all the examines we gathered.
 */
public class ExamineRepository {

    private static final Logger logger = LogManager.getLogger(ExamineRepository.class);

    private String[] items;
    private String[] objects;
    private String[] npcs;

    public ExamineRepository(DefinitionRepository defrepo) {
        items = new String[defrepo.total(ItemDefinition.class)];
        objects = new String[defrepo.total(ObjectDefinition.class)];
        npcs = new String[defrepo.total(NpcDefinition.class)];

        readFile("data/examine/item_examines.txt", items);
        readFile("data/examine/object_examines.txt", objects);
        readFile("data/examine/npc_examines.txt", npcs);

        logger.info("Loaded {} item examines.", items.length);
        logger.info("Loaded {} object examines.", objects.length);
        logger.info("Loaded {} npc examines.", npcs.length);
    }

    private void readFile(String path, String[] out) {
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int id = Integer.parseInt(line.substring(0, line.indexOf(':')));
                String examine = line.substring(line.indexOf(':') + 1);
                out[id] = examine;
            }
        } catch (Exception e) {
            logger.error("Could not file examines.", e);
        }
    }

    public String item(Item item) {
        return item == null ? "Something." : item(item.getId());
    }

    public String item(int id) {
        if (id < 0 || id >= items.length) {
            System.err.println("Item id out of range! " + id);
            return "Something.";
        }
        if (id == 5020) {
            return "A voucher that converts into 1 vote point for the redeemer.";
        }
        if (id == 6990)
            return "It's a bag of dice.";

        if (id == 6808) {
            return "It gives off a strange aura.";
        }

        String examine = items[id];
        return examine == null ? "Something." : examine;
    }

    public String object(int id) {
        if (id < 0 || id >= objects.length) {
            System.err.println("Mapobj id out of range! " + id);
            return "Something.";
        }

        String examine = objects[id];
        return examine == null ? "Something." : examine;
    }

    public String npc(int id) {
        if (id < 0 || id >= npcs.length) {
            System.err.println("NPC id out of range! " + id);
            return "Something.";
        }

        String examine = npcs[id];
        return examine == null ? "Something." : examine;
    }

}

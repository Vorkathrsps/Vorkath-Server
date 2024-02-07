package com.cryptic.utility;

import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.cache.definitions.ObjectDefinition;
import com.cryptic.cache.DataStore;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Bart on 11/26/2015.
 */
public class DoorPairer {

    public static void main(String[] args) throws Exception {
		DataInputStream input = new DataInputStream(new FileInputStream("data/map/doorpairs.bin"));
		while (input.available() > 0) {
			int id = input.readInt();
			int toid = input.readInt();
			boolean closed = input.readBoolean();
			boolean open = input.readBoolean();

			System.out.println(id +", "+toid +", "+closed+", "+open);
		}

        DataStore ds = new DataStore("data/cache");
        DefinitionRepository repo = new DefinitionRepository(ds, true);

        // This poor method is O(n2).. :(

        // Find the doors we should resolve
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/map/doorpairs.bin"));

        for (int i = 0; i < repo.total(ObjectDefinition.class); i++) {
            ObjectDefinition def = repo.get(ObjectDefinition.class, i);
            if (def == null) continue; // skip

            if (def.name.equals("Door") || def.name.equals("Large door") || def.name.equals("Doors") || def.name.equals("Large Doors") || def.name.equals("gate")) { // NOTE: there are plenty of other types of stuff which you might need to redump
                // when they become required.

                boolean isClosed = Arrays.asList(def.options).contains("Open");
                boolean isOpen = Arrays.asList(def.options).contains("Close");
                int other = findOtherDoor(def, repo);

                if (i == 1550 && other == 23555) {
                    other = 1546;
                }
                // Other being the opposite state of 'open' - find the closed on and vice versa.
                if (other != -1) {
                    dos.writeInt(i); // Current obj id we're investigating
                    dos.writeInt(other); // The open/closed version.. same model, height, but with opposite option of open/close.
                    dos.writeBoolean(isClosed);
                    dos.writeBoolean(isOpen);
                }
            } else if (def.name.toLowerCase().contains("door")) {
                System.err.println("possible other door type: " + def.name + " (" + i + ")");
            }
        }

        dos.close();
        System.out.println("complete!");
    }

    private static int findOtherDoor(@NotNull ObjectDefinition door, DefinitionRepository repo) {
        int newId = -1;
        boolean weHaveOpen = Arrays.asList(door.options).contains("Open");
        search:
        for (int i = 0; i < repo.total(ObjectDefinition.class); i++) {
            ObjectDefinition def = repo.get(ObjectDefinition.class, i);
            //We want an object.. that's not the same definition... has the same vflip and has the same amount of models
            //Hmm some ardougne castle doors might have different vflip on closed version..? In that case we would have to spawn them on the SAME tile... 34809 -> 34808 on same tile. All single doors in ardy castle seem to open in the same way, from some video
            if (def == door || def.vflip != door.vflip || def.models == null || door.models == null || def.models.length != door.models.length) {
                continue;
            }

            boolean hasOpen = Arrays.asList(def.options).contains("Open");
            boolean hasClose = Arrays.asList(def.options).contains("Close");
            if ((hasOpen && weHaveOpen) || (!hasOpen && !weHaveOpen) || (weHaveOpen && !hasClose) || (!weHaveOpen && hasClose)) {
                continue;
            }

            //The models should also be the same...
            if (!Arrays.equals(door.models, def.models) || !Arrays.equals(door.modeltypes, def.modeltypes))
                continue;

            //And if the object we clicked is a recolor...
            if (door.recol_d != null) {
                //Then we also want a recolor...
                if (def.recol_d == null || def.recol_d.length != door.recol_d.length) {
                    continue;
                }
                //And have it recolored the same way
                for (int t = 0; t < def.recol_d.length; t++) {
                    if (def.recol_d[t] != door.recol_d[t]) {
                        continue search;
                    }
                }
            } else if (def.recol_d != null) {
                //If it's not a recolor, then we don't want a recolor.
                continue;
            }

            //It should also be named the same..
            if (!door.name.equals(def.name)) {
                continue;
            }

            //But have different options
            if (Arrays.equals(door.options, def.options))
                continue;
            boolean other1 = Arrays.asList(door.options).contains("Pick-lock");
            boolean other2 = Arrays.asList(def.options).contains("Pick-lock");
            if ((other1 && !other2) || (!other1 && other2)) { // Almost the right door .. but mismatch on other options. Specifically theiving ones
                continue;
            }

            if (Math.abs(def.op66Render0x2 - door.op66Render0x2) > 5) { //some doors change height slightly...
                System.out.println("BADHeight0x2?" + door.id + " -> " + def.id + " " + def.op66Render0x2 + " - " + door.op66Render0x2);
                continue;
            }

            //System.out.println(door.id + " -> " + def.id + " " + def.name + " " + Arrays.toString(def.options));
            newId = def.id;
        }
        return newId;
    }

}

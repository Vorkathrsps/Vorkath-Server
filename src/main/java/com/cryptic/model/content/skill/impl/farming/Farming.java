package com.cryptic.model.content.skill.impl.farming;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.farming.impl.Patch;
import com.cryptic.model.content.skill.impl.farming.impl.Plant;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.base.Preconditions;

import java.io.*;
import java.util.Arrays;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Sharky
 * @Since June 16, 2023
 */
public class Farming {

    private final Player player;
    private final Planting[] plants = new Planting[50];
    private final FarmingPatch[] patches = new FarmingPatch[50];
    private static final int FARMING_CONFIG_ID = 529;

    public static double xpBonus(Player player) {
        double multiplier = 1;
        multiplier *= farmerSetBonus(player);
        return multiplier;
    }

    private static double farmerSetBonus(Player player) {
        double bonus = 1.0;
        Item helmet = player.getEquipment().get(EquipSlot.HEAD);
        Item jacket = player.getEquipment().get(EquipSlot.BODY);
        Item legs = player.getEquipment().get(EquipSlot.LEGS);
        Item boots = player.getEquipment().get(EquipSlot.FEET);

        if (helmet != null && helmet.getId() == FARMERS_STRAWHAT)
            bonus += 0.4;
        if (jacket != null && jacket.getId() == FARMERS_JACKET)
            bonus += 0.8;
        if (legs != null && legs.getId() == FARMERS_BORO_TROUSERS)
            bonus += 0.6;
        if (boots != null && boots.getId() == FARMERS_BOOTS)
            bonus += 0.2;

        /* Whole set gives an additional 0.5% exp bonus */
        if (bonus >= 2.0)
            bonus += 0.5;

        return bonus;
    }

    public Farming(Player player) {
        this.player = player;
        for (int index = 0; index < patches.length; index++) {
            if (patches[index] == null) {
                patches[index] = new FarmingPatch();
            }
        }
    }

    public void handleLogin() {
        varbitUpdate();
        Chain.noCtxRepeat().repeatingTask(1, t -> {
            if(player == null || !player.isRegistered()) {
                t.stop();
                return;
            }
            sequence();
        });
    }

    public void handleObjectClick(int x, int y, int option) {
        player.getFarming().click(player, x, y, option);
    }

    public void handleItemOnObject(int itemId, int objectX, int objectY) {
        if (plant(itemId, objectX, objectY))
            return;
        useItemOnPlant(itemId, objectX, objectY);
    }

    public void regionChanged() {
        player.getFarming().varbitUpdate();
    }

    public void sequence() {
        for (Planting plant : plants) {
            if (plant != null) {
                plant.process(player);
            }
        }
        for (int index = 0; index < patches.length; index++) {
            if (index >= Patch.values().length)
                break;
            if ((patches[index] != null) && (!inhabited(Patch.values()[index].bottomLeft.getX(), Patch.values()[index].bottomLeft.getY()))) {
                patches[index].process(player);
            }
        }
    }

    public int findVarbit(Patch patch) {
        //System.out.println("search for patch found patch "+patch+" at  "+patches[patch.ordinal()]);
        if (inhabited(patch.bottomLeft.getX(), patch.bottomLeft.getY())) {
            for (Planting plant : plants) {
                if (plant != null && plant.patchId() == patch) {
                    return plant.getConfig();
                }
            }
        }
        return patches[patch.ordinal()].stage;
    }

    public void varbitUpdate() {
        Patch[] patches = {
            Patch.FALADOR_HERB,
            Patch.CATHERBY_HERB,
            Patch.ARDOUGNE_HERB,
            Patch.PHAS_HERB
        };

        Patch closest = null;
        int lowest = 0;
        for (Patch patch : patches) {
            int distance = player.tile().distance(patch.bottomLeft);
            if (closest == null || distance < lowest) {
                closest = patch;
                lowest = distance;
            }
        }

        int state = switch (closest) {
            case FALADOR_HERB -> (findVarbit(Patch.FALADOR_HERB) << 24)
                + (findVarbit(Patch.FALADOR_FLOWER) << 16)
                + (findVarbit(Patch.FALADOR_ALLOTMENT_SOUTH) << 8)
                + (findVarbit(Patch.FALADOR_ALLOTMENT_NORTH));
            case CATHERBY_HERB ->
                (findVarbit(Patch.CATHERBY_HERB) << 24)
                    + (findVarbit(Patch.CATHERBY_FLOWER) << 16)
                    + (findVarbit(Patch.CATHERBY_ALLOTMENT_SOUTH) << 8)
                    + (findVarbit(Patch.CATHERBY_ALLOTMENT_NORTH));
            case ARDOUGNE_HERB -> (findVarbit(Patch.ARDOUGNE_HERB) << 24)
                + (findVarbit(Patch.ARDOUGNE_FLOWER) << 16)
                + (findVarbit(Patch.ARDOUGNE_ALLOTMENT_SOUTH) << 8)
                + (findVarbit(Patch.ARDOUGNE_ALLOTMENT_NORTH));
            case PHAS_HERB -> (findVarbit(Patch.PHAS_HERB) << 24)
                + (findVarbit(Patch.PHAS_FLOWER) << 16)
                + (findVarbit(Patch.PHAS_ALLOTMENT_EAST) << 8)
                + (findVarbit(Patch.PHAS_ALLOTMENT_WEST));
            default -> 0;
        };

        //System.out.println("config state "+state+" of "+closest+" links to varbit value "+config(FarmingPatches.FALADOR_ALLOTMENT_SOUTH));

        player.getPacketSender().sendConfig(FARMING_CONFIG_ID, state);
    }

    public void clear() {
        Arrays.fill(plants, null);
        for (int index = 0; index < patches.length; index++) {
            patches[index] = new FarmingPatch();
        }
    }

    public void insert(Planting patch) {
        for (int index = 0; index < plants.length; index++) {
            if (plants[index] == null) {
                plants[index] = patch;
                break;
            }
        }
    }

    public boolean inhabited(int x, int y) {
        for (Planting plant : plants) {
            if (plant != null) {
                Patch patch = plant.patchId();
                if ((x >= patch.bottomLeft.getX()) && (y >= patch.bottomLeft.getY()) && (x <= patch.topRight.getX()) && (y <= patch.topRight.getY())) {
                    if (isPatchException(x, y, patch)) {
                        continue;
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public int getGrassyPatch(int x, int y) {
        for (int index = 0; index < Patch.values().length; index++) {
            Patch patch = Patch.values()[index];
            if (x >= patch.bottomLeft.getX() && y >= patch.bottomLeft.getY() && x <= patch.topRight.getX() && y <= patch.topRight.getY()) {
                if (!isPatchException(x, y, patch)) {
                    if (inhabited(x, y) || patches[index] == null) {
                        break;
                    }
                    return index;
                }
            }
        }

        return -1;
    }

    public Planting findPlantedPatch(int x, int y) {
        for (int index = 0; index < Patch.values().length; index++) {
            Patch patch = Patch.values()[index];
            if (x >= patch.bottomLeft.getX() && y >= patch.bottomLeft.getY() && x <= patch.topRight.getX() && y <= patch.topRight.getY()) {
                if (!isPatchException(x, y, patch)) {
                    for (Planting plant : plants) {
                        if (plant != null && plant.patchId == patch.ordinal()) {
                            return plant;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isPatchException(int x, int y, Patch patch) {
        if(x == 3054 && y == 3307 && patch != Patch.FALADOR_FLOWER)
            return true;
        return x == 3601 && y == 3525 && patch != Patch.PHAS_FLOWER;
    }

    public boolean click(Player player, int x, int y, int option) {
        int grass = getGrassyPatch(x, y);
        if (grass != -1) {
            if (option == 1) {
                patches[grass].click(player, option);
            }
            return true;
        } else {
            Planting plant = findPlantedPatch(x, y);

            if (plant != null) {
                plant.click(player, option);
                return true;
            }
        }

        return false;
    }

    public void removePlant(Planting plant) {
        for (int index = 0; index < plants.length; index++) {
            if ((plants[index] != null) && (plants[index] == plant)) {
                patches[plants[index].patchId().ordinal()].setTime();
                plants[index] = null;
                varbitUpdate();
                return;
            }
        }
    }

    public void useItemOnPlant(int item, int x, int y) {
        if (item == RAKE) {
            int patch = getGrassyPatch(x, y);
            if (patch != -1) {
                patches[patch].rake(player);
                return;
            }
        }

        Planting planting = findPlantedPatch(x, y);
        if (planting != null) {
            planting.useItemOnPlant(player, item);
        }
    }

    public boolean plant(int seed, int x, int y) {
        if (!Plant.isSeed(seed)) {
            return false;
        }

        for (Patch patch : Patch.values()) {
            if ((x >= patch.bottomLeft.getX()) && (y >= patch.bottomLeft.getY()) && (x <= patch.topRight.getX()) && (y <= patch.topRight.getY())) {
                if (isPatchException(x, y, patch)) {
                    continue;
                }
                if (!patches[patch.ordinal()].isRaked()) {
                    player.message("This patch needs to be raked before anything can grow in it.");
                    return true;
                }

                for (Plant plant : Plant.values()) {
                    if (plant.seed == seed) {
                        if (player.skills().level(Skills.FARMING) >= plant.level) {
                            if (inhabited(x, y)) {
                                player.message("There are already seeds planted here.");
                                return true;
                            }

                            if (patch.seed != plant.type) {
                                player.message("You can't plant this type of seed here.");
                                return true;
                            }

                            if (player.inventory().contains(patch.planter)) {
                                player.animate(2291);
                                player.message("You bury the seed in the dirt.");
                                player.inventory().remove(seed, 1);
                                Planting planted = new Planting(patch.ordinal(), plant.ordinal());
                                planted.setTime();
                                insert(planted);
                                varbitUpdate();
                                player.skills().addXp(Skills.FARMING,plant.plantExperience * xpBonus(player), true);
                            } else {
                                String name = World.getWorld().definitions().get(ItemDefinition.class, patch.planter).name;
                                player.message("You need " + Utils.getAOrAn(name) + " " +name+ " to plant seeds.");
                            }
                        } else {
                            player.message("You need a Farming level of " + plant.level + " to plant this.");
                        }

                        return true;
                    }
                }

                return false;
            }
        }

        return false;
    }

    private String getDirectory() {
        return "./data/saves/farming/";
    }

    private String getFile() {
        return getDirectory() + player.getUsername() + ".txt";
    }

    public void save() {
        try {
            if (!new File(getDirectory()).exists()) {
                Preconditions.checkState(new File(getDirectory()).mkdirs());
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFile()));
            for (int i = 0; i < patches.length; i++) {
                if (i >= Patch.values().length)
                    break;
                if (patches[i] != null) {
                    writer.write("[PATCH]");
                    writer.newLine();
                    writer.write("patch: "+i);
                    writer.newLine();
                    writer.write("stage: "+patches[i].stage);
                    writer.newLine();
                    writer.write("time: "+patches[i].time);
                    writer.newLine();
                    writer.write("END PATCH");
                    writer.newLine();
                    writer.newLine();
                }
            }
            for (Planting planting : plants) {
                if (planting != null) {
                    writer.write("[PLANT]");
                    writer.newLine();
                    writer.write("patch: " + planting.patchId);
                    writer.newLine();
                    writer.write("plant: " + planting.plantId);
                    writer.newLine();
                    writer.write("stage: " + planting.stage);
                    writer.newLine();
                    writer.write("watered: " + planting.watered);
                    writer.newLine();
                    writer.write("harvested: " + planting.harvested);
                    writer.newLine();
                    writer.write("time: " + planting.time);
                    writer.newLine();
                    writer.write("END PLANT");
                    writer.newLine();
                    writer.newLine();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            if (!new File(getFile()).exists())
                return;
            BufferedReader r = new BufferedReader(new FileReader(getFile()));
            int stage = -1, patch = -1, plant = -1, watered = -1, harvested = -1;
            long time = -1;
            while(true) {
                String line = r.readLine();
                if(line == null) {
                    break;
                } else {
                    line = line.trim();
                }
                if(line.startsWith("patch"))
                    patch = Integer.parseInt(line.substring(line.indexOf(":")+2));
                else if(line.startsWith("stage"))
                    stage = Integer.parseInt(line.substring(line.indexOf(":")+2));
                else if(line.startsWith("plant"))
                    plant = Integer.parseInt(line.substring(line.indexOf(":")+2));
                else if(line.startsWith("watered"))
                    watered = Integer.parseInt(line.substring(line.indexOf(":")+2));
                else if(line.startsWith("harvested"))
                    harvested = Integer.parseInt(line.substring(line.indexOf(":")+2));
                else if(line.startsWith("time"))
                    time = Long.parseLong(line.substring(line.indexOf(":")+2));
                else if(line.equals("END PATCH") && patch >= 0) {
                    patches[patch].stage = (byte)stage;
                    patches[patch].time = time;
                    patch = -1;
                }
                else if(line.equals("END PLANT") && patch >= 0) {
                    plants[patch] = new Planting(patch, plant);
                    plants[patch].watered = (byte) watered;
                    plants[patch].stage = (byte) stage;
                    plants[patch].harvested = (byte) harvested;
                    plants[patch].time = time;
                    patch = -1;
                }
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

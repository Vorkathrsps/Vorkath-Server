package com.aelous.model.content.skill.impl.farming;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.content.skill.impl.farming.impl.Patch;
import com.aelous.model.content.skill.impl.farming.impl.Plant;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.google.common.base.Preconditions;

import java.io.*;
import java.util.Arrays;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Sharky
 * @Since June 16, 2023
 */
public class Farming extends PacketInteraction {

    private final Planting[] plants = new Planting[50];
    private final FarmingPatch[] patches = new FarmingPatch[50];
    private static final int FARMING_CONFIG_ID = 529;
    private static final String SAVING_PATH = "./data/saves/farming/";

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

    public Farming() {
        for (int i = 0; i < patches.length; i++)
            if (patches[i] == null)
                patches[i] = new FarmingPatch();
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            int grass = getGrassyPatch(object.x, object.y);
            if (grass != -1) {
                patches[grass].rake(player);
                return true;
            }
        }
        if(option == 2) {
            Planting plant = findPlantedPatch(object.x, object.y);
            if (plant != null) {
                plant.click(player, option);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        if (plant(player, item, object.tile()))
            return true;

        if (item.getId() == RAKE) {
            int patch = getGrassyPatch(object.x, object.y);
            if (patch != -1) {
                patches[patch].rake(player);
                return true;
            }
        }

        Planting plant = findPlantedPatch(object.x, object.y);
        if (plant != null) {
            if (item.getId() == ItemIdentifiers.SPADE) {
                player.animate(830);
                removePlant(player,plant);
                Chain.bound(player).runFn(2, () -> {
                    player.message("You remove your plants from the plot.");
                    player.resetAnimation();
                });
                return true;
            }

            if (item.getId() == PLANT_CURE) {
                if (plant.isDead()) {
                    player.message("Your plant is dead!");
                } else if (plant.diseased()) {
                    player.message("You cure the plant.");
                    player.animate(2288);
                    player.inventory().remove(PLANT_CURE);
                    plant.setDisease((byte) -1);
                    player.getFarming().updateVarpFor(player);
                } else {
                    player.message("Your plant does not need this.");
                }
                return true;
            }

            if (item.getId() >= WATERING_CAN && item.getId() <= WATERING_CAN8) {
                plant.water(player, item);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRegionChange(Player player) {
        updateVarpFor(player);
    }

    @Override
    public void onPlayerProcess(Player player) {
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

    @Override
    public void onLogin(Player player) {
        updateVarpFor(player);
        Chain.noCtxRepeat().repeatingTask(1, t -> {
            if(player == null || !player.isRegistered()) {
                t.stop();
                return;
            }
            super.onPlayerProcess(player);
        });
    }

    private int findVarbit(Patch patch) {
        //System.out.println("search for patch found patch "+patch+" at  "+patches[patch.ordinal()]);
        if (inhabited(patch.bottomLeft.getX(), patch.bottomLeft.getY())) {
            for (Planting plant : plants) {
                if (plant != null && plant.patchId() == patch) {
                    return plant.config();
                }
            }
        }
        return patches[patch.ordinal()].stage;
    }

    public void updateVarpFor(Player player) {
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

        //System.out.println("config state "+state+" of "+closest+" links to varbit value "+config(Patch.FALADOR_ALLOTMENT_SOUTH));

        player.getPacketSender().sendConfig(FARMING_CONFIG_ID, state);
    }

    public void clear() {
        Arrays.fill(plants, null);
        for (int i = 0; i < patches.length; i++) {
            patches[i] = new FarmingPatch();
        }
    }

    private void insert(Planting patch) {
        for (int i = 0; i < plants.length; i++) {
            if (plants[i] == null) {
                plants[i] = patch;
                break;
            }
        }
    }

    private boolean inhabited(int x, int y) {
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

    private int getGrassyPatch(int x, int y) {
        for (int i = 0; i < Patch.values().length; i++) {
            Patch patch = Patch.values()[i];
            if (x >= patch.bottomLeft.getX() && y >= patch.bottomLeft.getY() && x <= patch.topRight.getX() && y <= patch.topRight.getY()) {
                if (!isPatchException(x, y, patch)) {
                    if (inhabited(x, y) || patches[i] == null)
                        break;
                    return i;
                }
            }
        }

        return -1;
    }

    private Planting findPlantedPatch(int x, int y) {
        for (int i = 0; i < Patch.values().length; i++) {
            Patch patch = Patch.values()[i];
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

    public void removePlant(Player player, Planting plant) {
        for (int index = 0; index < plants.length; index++) {
            if ((plants[index] != null) && (plants[index] == plant)) {
                patches[plants[index].patchId().ordinal()].setTime();
                plants[index] = null;
                updateVarpFor(player);
                return;
            }
        }
    }

    private boolean plant(Player player, Item seed, Tile tile) {
        if (!Plant.isSeed(seed.getId())) {
            return false;
        }

        for (Patch patch : Patch.values()) {
            if ((tile.x >= patch.bottomLeft.getX()) && (tile.y >= patch.bottomLeft.getY()) && (tile.x <= patch.topRight.getX()) && (tile.y <= patch.topRight.getY())) {
                if (isPatchException(tile.x, tile.y, patch)) {
                    continue;
                }
                if (!patches[patch.ordinal()].raked()) {
                    player.message("This patch needs to be raked before anything can grow in it.");
                    return true;
                }

                for (Plant plant : Plant.values()) {
                    if (plant.seed == seed.getId()) {
                        if (player.skills().level(Skills.FARMING) >= plant.level) {
                            if (inhabited(tile.x, tile.y)) {
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
                                updateVarpFor(player);
                                player.skills().addXp(Skills.FARMING, (int)plant.plantExperience * xpBonus(player), true);
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

    public void save(String username) { // Yes username never display name.
        try {
            File directory = new File(SAVING_PATH);
            if (!directory.getParentFile().exists()) {
                Preconditions.checkState(directory.getParentFile().mkdirs());
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(SAVING_PATH + username + ".txt"));
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
                    writer.write("time: "+patches[i].getTime());
                    writer.newLine();
                    writer.write("END PATCH");
                    writer.newLine();
                    writer.newLine();
                }
            }
            for (Planting plant : plants) {
                if (plant != null) {
                    writer.write("[PLANT]");
                    writer.newLine();
                    writer.write("patch: " + plant.patchId);
                    writer.newLine();
                    writer.write("plant: " + plant.plantId);
                    writer.newLine();
                    writer.write("stage: " + plant.stage);
                    writer.newLine();
                    writer.write("watered: " + plant.watered);
                    writer.newLine();
                    writer.write("harvested: " + plant.harvested);
                    writer.newLine();
                    writer.write("time: " + plant.time);
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

    public void load(String username) { // Yes username never display name.
        try {
            File file = new File(SAVING_PATH + username + ".txt");
            if (!file.exists())
                return;
            BufferedReader r = new BufferedReader(new FileReader(file));
            int stage = -1, patch = -1, plant = -1, watered = -1, harvested = -1;
            long time = -1;
            while(true) {
                String line = r.readLine();
                if(line == null) {
                    break;
                } else {
                    line = line.trim();
                }
                String substring = line.substring(line.indexOf(":") + 2);
                int index = Integer.parseInt(substring);
                if(line.startsWith("patch"))
                    patch = index;
                else if(line.startsWith("stage"))
                    stage = index;
                else if(line.startsWith("plant"))
                    plant = index;
                else if(line.startsWith("watered"))
                    watered = index;
                else if(line.startsWith("harvested"))
                    harvested = index;
                else if(line.startsWith("time"))
                    time = Long.parseLong(substring);
                else if(line.equals("END PATCH") && patch >= 0) {
                    patches[patch].stage = (byte)stage;
                    patches[patch].setTime(time);
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

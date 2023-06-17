package com.aelous.model.content.skill.impl.farming;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.content.skill.impl.farming.impl.Patch;
import com.aelous.model.content.skill.impl.farming.impl.Plant;
import com.aelous.model.content.skill.impl.farming.impl.Seed;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Sharky
 * @Since June 16, 2023
 */
public class Planting {

    public int patchId;
    public int plantId;
    public long time = 0;
    public byte stage = 0;
    @Setter
    public byte disease = -1;
    public byte watered = 0;
    @Getter
    private final boolean dead = false;
    public byte harvested = 0;

    public Planting(int patchId, int plantId) {
        this.patchId = patchId;
        this.plantId = plantId;
    }

    public void water(Player player, int item) {
        if (patchId().seed == Seed.HERB) {
            player.message("This patch doesn't need watering.");
            return;
        }

        if (watered()) {
            player.message("Your plants have already been watered.");
            return;
        }

        if (item == WATERING_CAN) {
            player.message("Your watering can is empty.");
            return;
        }

        player.message("You water the plant.");
        player.animate(2293);
        watered = -1;
        doConfig(player);
    }

    public void setTime() {
        time = System.currentTimeMillis();
    }

    public void click(Player player, int option) {
        Plant plant = Plant.values()[this.plantId];
        if (option == 1) {
            if (stage == plant.stages) {
                harvest(player);
            } else if (plant.type == Seed.HERB) {
                statusMessage(player, plant);
            }
        } else if ((option == 2)) {
            statusMessage(player, plant);
        }
    }

    private void statusMessage(Player player, Plant plant) {
        if (dead) {
            player.message("Oh dear, your plants have died!");
        } else if (diseased()) {
            player.message("Your plants are diseased!");
        } else if (stage == plant.stages) {
            player.message("Your plants are healthy and ready to harvest.");
        } else {
            int stagesLeft = plant.stages - stage;
            String message = "Your plants are healthy";
            if(!watered() && patchId().seed != Seed.HERB)
                message += " but need some water to survive.";
            else {
                message += " and are currently growing (about " + (stagesLeft * (plant.getMinutes() / plant.stages)) + " minutes remain).";
            }
            player.message(message);
        }
    }

    public void harvest(Player player) {
        List<Integer> harvestItemNeeded = Lists.newArrayList();
        harvestItemNeeded.add(patchId().harvestItem);
        if (patchId().harvestItem == SECATEURS) {
            harvestItemNeeded.add(MAGIC_SECATEURS);
        }

        if (harvestItemNeeded.stream().anyMatch(item -> player.inventory().contains(item) || player.getEquipment().contains(item))) {
            final Planting planting = this;
            Chain.bound(player).repeatingTask(3, t -> {
                if (player.getInventory().getFreeSlots() == 0) {
                    player.message("Your inventory is full.");
                    t.stop();
                    return;
                }
                player.animate(2282);
                Item add;
                int id = Plant.values()[plantId].harvest;
                int amount = 1; // Local variable so we can always add boosts
                ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, id);
                add = def.noted() ? new Item(def.notelink, amount) : new Item(id, amount);

                //Add or drop for QoL
                player.inventory().addOrDrop(add);

                int chance = 10_000; // Can always move this to the enum and add pet chances per plant
                if (World.getWorld().rollDie(chance,1)) {
                    UnlockFarmingPet.unlockTangleroot(player);
                }

                Plant plant = Plant.values()[plantId];

                def = World.getWorld().definitions().get(ItemDefinition.class, plant.harvest);
                String name = def.name;
                if (name.endsWith("s"))
                    name = name.substring(0, name.length() - 1);
                player.message("You harvest " + Utils.getAOrAn(name) + " " + name + ".");
                player.skills().addXp(Skills.FARMING,plant.harvestExperience * Farming.xpBonus(player));

                harvested++;

                int min = 7;
                if (player.getEquipment().hasAt(EquipSlot.WEAPON, MAGIC_SECATEURS))
                    min += 4;

                if (id == LIMPWURT_ROOT) {
                    player.getInventory().addOrDrop(new Item(LIMPWURT_ROOT, 2));
                    player.getFarming().removePlant(planting);
                    t.stop();
                    return;
                }
                if (patchId().seed == Seed.FLOWER || harvested >= min && World.getWorld().random(4) <= 1) {
                    player.getFarming().removePlant(planting);
                    t.stop();
                }
            });
        } else {
            ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, Patch.values()[patchId].harvestItem);
            player.message("You need " + Utils.getAOrAn(def.name) + " " + def.name + " to harvest these plants.");
        }
    }

    public void useItemOnPlant(final Player player, int item) {
        if (item == SPADE) {
            player.animate(830);
            player.getFarming().removePlant(this);
            Chain.bound(player).runFn(2, () -> {
                player.message("You remove your plants from the plot.");
                player.animate(65535);
            });
            return;
        }
        if (item == PLANT_CURE) {
            if (dead) {
                player.message("Your plant is dead!");
            } else if (diseased()) {
                player.message("You cure the plant.");
                player.animate(2288);
                player.inventory().remove(PLANT_CURE, 1);
                disease = -1;
                doConfig(player);
            } else {
                player.message("Your plant does not need this.");
            }
            return;
        }
        if (item >= WATERING_CAN && item <= WATERING_CAN8) {
            water(player, item);
        }
    }

    public void process(Player player) {
        if (dead || stage >= Plant.values()[plantId].stages) {
            return;
        }

        long elapsed = (System.currentTimeMillis() - time) / 60_000;

        Plant plant = Plant.values()[this.plantId];
        int grow = plant.getMinutes() / plant.stages;
        if (grow == 0)
            grow = 1;

        if (elapsed >= grow) {
            for (int i = 0; i < elapsed / grow; i++) {
                if(watered() || patchId().seed == Seed.HERB) {
                    stage++;
                    player.getFarming().varbitUpdate();
                    if (stage >= plant.stages) {
                        player.message(Color.BLUE.wrap("A seed you planted has finished growing!"));
                        return;
                    }
                }

            }
            setTime();
        }
    }

    public void doConfig(Player player) {
        player.getFarming().varbitUpdate();
    }

    public int getConfig() {
        Plant plant = Plant.values()[plantId];
        return (plant.healthy + stage + (watered() && stage == 0 ? 64 : 0));
    }

    public Patch patchId() {
        return Patch.values()[patchId];
    }

    public boolean diseased() {
        return disease > -1;
    }

    public boolean watered() {
        return watered == -1;
    }
}

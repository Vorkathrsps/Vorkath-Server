package com.aelous.model.content.skill.impl.farmingOld;

import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.COMPOST_BIN_7813;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.POISON_IVY_BUSH_7674;

/**
 * @author Jason http://https://rune-server.org/members/jason
 * @date Oct 27, 2013
 */
public class FarmingOld {

    public static final int MAX_PATCHES = 4;

    private final Player player;

    private int weeds;

    private long lastPoisonBerryFarm;

    public FarmingOld(Player player) {
        this.player = player;
    }

    private static double farmersOutfitBonus(Player player) {
        double bonus = 1.0;

        Item hat = player.getEquipment().get(EquipSlot.HEAD);
        Item top = player.getEquipment().get(EquipSlot.BODY);
        Item legs = player.getEquipment().get(EquipSlot.LEGS);
        Item boots = player.getEquipment().get(EquipSlot.FEET);

        if (hat != null && hat.getId() == ItemIdentifiers.FARMERS_STRAWHAT)
            bonus += 0.4;
        if (top != null && top.getId() == ItemIdentifiers.FARMERS_JACKET)
            bonus += 0.8;
        if (legs != null && legs.getId() == ItemIdentifiers.FARMERS_BORO_TROUSERS)
            bonus += 0.6;
        if (boots != null && boots.getId() == ItemIdentifiers.FARMERS_BOOTS)
            bonus += 0.2;

        //If we've got the whole set, it's an additional 0.5% exp bonus
        if (bonus >= 2.0)
            bonus += 0.5;

        return bonus;
    }

    private boolean hasMagicSecateurs() {
        return player.inventory().contains(MAGIC_SECATEURS) || player.getEquipment().hasAt(EquipSlot.WEAPON, MAGIC_SECATEURS) || player.getEquipment().wearingMaxCape();
    }

    private void fillCompostBucket() {
        if (!player.inventory().contains(BUCKET)) {
            DialogueManager.sendStatement(player, "You need an empty bucket to fill it with compost.");
            return;
        }
        TaskManager.submit(new FarmingCompostTask(player, 3));
    }

    public boolean handleItemOnObjectInteraction(int objectId, int itemId, int x, int y) {
        if (objectId == Constants.GRASS_OBJECT) {
            Patch patch = Patch.get(x, y);
            if (patch == null)
                return true;
            int id = patch.getId();
            if (player.getFarmingState(id) < State.RAKED.getId()) {
                if (!player.inventory().contains(RAKE, 1))
                    player.message("You need to rake this patch to remove all the weeds.");
                else if (itemId == RAKE || player.inventory().contains(RAKE)) {
                    player.animate(Constants.RAKING_ANIM);

                    if (weeds <= 0)
                        weeds = 3;
                    TaskManager.submit(new Task("rake_task", 3, player, false) {

                        public void execute() {
                            if (weeds > 0) {
                                weeds--;

                                player.inventory().add(WEEDS, 1);
                                player.animate(Constants.RAKING_ANIM);
                                switch (weeds) {
                                    case 3 -> ObjectManager.addObj(new GameObject(Constants.SEED_PATCH_RAKE_STATE_THREE, new Tile(patch.getX(), patch.getY(), player.getZ()), 10, 0));
                                    case 2 -> ObjectManager.addObj(new GameObject(Constants.SEED_PATCH_RAKE_STATE_TWO, new Tile(patch.getX(), patch.getY(), player.getZ()), 10, 0));
                                    case 1 -> ObjectManager.addObj(new GameObject(Constants.SEED_PATCH_RAKE_STATE_ONE, new Tile(patch.getX(), patch.getY(), player.getZ()), 10, 0));
                                    case 0 -> ObjectManager.addObj(new GameObject(Constants.EMPTY_PATCH, new Tile(patch.getX(), patch.getY(), player.getZ()), 10, 0));
                                }
                            } else if (weeds == 0) {
                                player.setFarmingState(id, State.RAKED.getId());
                                player.message("<col=" + Color.BLUE.getColorValue() + ">You raked the patch of all it's weeds, now the patch is ready for compost.");
                                player.animate(65535);
                                updateObjects();
                                stop();
                            }
                        }
                    });
                }
            }
            return true;
        }
        if (objectId == Constants.EMPTY_PATCH) {
            Patch patch = Patch.get(x, y);
            if (patch == null)
                return true;
            int id = patch.getId();
            if(player.getFarmingState(id) >= State.RAKED.getId() && player.getFarmingState(id) < State.COMPOST.getId()) {
                boolean hasWateringCan = IntStream.of(Constants.WATERING_CAN).anyMatch(identification -> player.inventory().contains(identification));
                if (player.getFarmingState(id) >= State.SEEDED.getId() && player.getFarmingState(id) < State.GROWTH.getId()) {
                    if (!hasWateringCan) {
                        player.message("You need to water the herb before you can harvest it.");
                        return true;
                    }
                    int time = (int) Math.round(player.getFarmingTime(id) * .6);
                    //player.setPositionToFace(gm);
                    player.animate(Constants.WATERING_CAN_ANIM);
                    player.setFarmingState(id, State.GROWTH.getId());
                    player.inventory().replace(itemId, itemId == 5333 ? 5331 : itemId - 1, true);
                    DialogueManager.sendStatement(player, "You water the herb, wait " + Math.round(player.getFarmingTime(id) * .6) + " seconds for the herb to mature.");
                    player.getPacketSender().sendEffectTimer(time, EffectTimer.FARMING);
                    return true;
                }

                if (itemId == COMPOST || (player.inventory().contains(COMPOST) && itemId == -1) || itemId == ULTRACOMPOST || (player.inventory().contains(ULTRACOMPOST) && itemId == -1) || itemId == SUPERCOMPOST || (player.inventory().contains(SUPERCOMPOST) && itemId == -1)) {
                    player.animate(Constants.PUTTING_COMPOST);
                    if (player.inventory().contains(ULTRACOMPOST)) {
                        player.inventory().remove(ULTRACOMPOST);
                    } else if (player.inventory().contains(SUPERCOMPOST)) {
                        player.inventory().remove(SUPERCOMPOST);
                    } else if (player.inventory().contains(COMPOST)) {
                        player.inventory().remove(COMPOST);
                    }
                    player.inventory().add(BUCKET, 1);
                    player.setFarmingState(id, State.COMPOST.getId());
                    player.message("<col=" + Color.BLUE.getColorValue() + ">You put compost on the soil, it is now time to seed it.");
                } else {
                    player.message("You need to put compost on this to enrich the soil.");
                }
        } else if (player.getFarmingState(id) >= State.COMPOST.getId() && player.getFarmingState(id) < State.SEEDED.getId()) {
                if (!player.inventory().contains(SEED_DIBBER)) {
                    player.message("You need to use a seed dibber with a seed on this patch.");
                    return false;
                }
                final FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(itemId);
                if (herb == null) {
                    player.message("You must use an appropriate seed on the patch at this stage.");
                    return true;
                }
                if (player.getSkills().level(Skills.FARMING) < herb.getLevelRequired()) {
                    player.message("You need a farming level of " + herb.getLevelRequired() + " to grow " + herb.getSeedName().replaceAll(" seed", "") + ".");
                    return false;
                }
                if (itemId == herb.getSeedId() && player.inventory().contains(SEED_DIBBER)) {
                    player.animate(Constants.SEED_DIBBING);
                    double exp = herb.getPlantingXp() * farmersOutfitBonus(player);

                    TaskManager.submit(new Task("planting_task", 3, player, false) {

                        public void execute() {
                            if (!player.inventory().contains(herb.getSeedId()))
                                return;
                            player.inventory().remove(herb.getSeedId(), 1);
                            player.setFarmingState(id, State.SEEDED.getId());
                            player.setFarmingSeedId(id, herb.getSeedId());
                            player.setFarmingTime(id, hasMagicSecateurs() ? herb.getGrowthTime() / 2 : herb.getGrowthTime());
                            player.setFarmingHarvest(id, 3 + World.getWorld().random(hasMagicSecateurs() ? 7 : 4));
                            player.getSkills().addXp(Skills.FARMING, exp);

                            player.setFarmingState(id, State.GROWTH.getId());
                            player.message("You plant the herb, wait " + Math.round(player.getFarmingTime(id) * .6) + " seconds for the herb to mature.");
                            int time = (int) Math.round(player.getFarmingTime(id) * .6);
                            player.getPacketSender().sendEffectTimer(time, EffectTimer.FARMING);
                            updateObjects();
                            stop();
                        }
                    });
                }
            }
        }
        return false;
    }

    public boolean handleObjectInteraction(int objectId, int x, int y, int option) {
        if(option == 1) {
            if (objectId == Constants.HERB_OBJECT) {
                Patch patch = Patch.get(x, y);
                if (patch == null)
                    return true;
                int id = patch.getId();

                if (player.getFarmingState(id) == State.GROWTH.getId()) {
                    if (player.getFarmingTime(id) > 0) {
                        player.message("You need to wait another " + Math.round(player.getFarmingTime(id) * .6) + " seconds until the herb is mature.");
                        return true;
                    }
                }
                if (player.getFarmingState(id) == State.HARVEST.getId()) {
                    if (player.inventory().getFreeSlots() < 1) {
                        DialogueManager.sendStatement(player, "You need at least 1 free space to harvest some herbs.");
                        return true;
                    }
                    if (player.getFarmingHarvest(id) == 0 || player.getFarmingState(id) != State.HARVEST.getId()) {
                        resetValues(id);
                        updateObjects();
                        return true;
                    }
                    FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(player.getFarmingSeedId(id));
                    if (herb == null) {
                        return true;
                    }

                    double exp = herb.getHarvestingXp() * farmersOutfitBonus(player);
                    TaskManager.submit(new Task("pickup_herbs_task", 1, player, false) {

                        public void execute() {
                            if (player.inventory().getFreeSlots() < 1) {
                                DialogueManager.sendStatement(player, "You need atleast 1 free space to harvest some herbs.");
                                player.animate(65535);
                                stop();
                                return;
                            }
                            if (player.getFarmingHarvest(id) <= 0) {
                                player.message("<col=600000>The herb patch has completely depleted...");
                                player.animate(65535);
                                resetValues(id);
                                updateObjects();
                                player.getPacketSender().sendConfig(529, 0);
                                stop();
                                return;
                            }
                            player.animate(Constants.PICKING_HERB_ANIM);
                            player.setFarmingHarvest(id, player.getFarmingHarvest(id) - 1);
                            player.inventory().add(herb.getGrimyId(), 1);
                            player.getSkills().addXp(Skills.FARMING, (int) exp);


                            if (herb == FarmingHerb.Herb.TORSTOL) {
                                player.getTaskMasterManager().increase(Tasks.PLANT_TORSTOL_SEED);
                            }
                        }
                    });
                }
                return true;
            }

            if (objectId == COMPOST_BIN_7813) {
                fillCompostBucket();
                return true;
            }

            if (objectId == POISON_IVY_BUSH_7674) {
                farmPoisonBerry();
                return true;
            }
        }
        return false;
    }

    private void farmPoisonBerry() {
        if (System.currentTimeMillis() - lastPoisonBerryFarm < TimeUnit.MINUTES.toMillis(5)) {
            player.message("You can only pick berries from this bush every 5 minutes.");
            return;
        }
        int level = player.getSkills().level(Skills.FARMING);
        if (level < 70) {
            player.message("You need a farming level of 70 to get this.");
            return;
        }
        if (player.inventory().getFreeSlots() < (hasMagicSecateurs() ? 2 : 1)) {
            player.message("You need at least " + (hasMagicSecateurs() ? 2 : 1) + " free slot " + (hasMagicSecateurs() ? "s" : "") + " to do this.");
            return;
        }
        int maximum = player.getSkills().xpLevel(Skills.FARMING);
        if (World.getWorld().random(100) < (10 + (maximum - level))) {
            player.poison(6);
        }
        player.animate(881);
        lastPoisonBerryFarm = System.currentTimeMillis();
        player.inventory().add(new Item(6018, hasMagicSecateurs() ? 2 : 1));
        player.getSkills().addXp(450, Skills.FARMING, true);
    }

    public void farmingProcess() {
        for (int index = 0; index < FarmingOld.MAX_PATCHES; index++) {
            if (player.getFarmingTime(index) > 0 && player.getFarmingState(index) == FarmingOld.State.GROWTH.getId()) {
                player.setFarmingTime(index, player.getFarmingTime(index) - 1);
                if (player.getFarmingTime(index) == 0) {
                    FarmingHerb.Herb herb = FarmingHerb.getHerbForSeed(player.getFarmingSeedId(index));
                    if (herb != null)
                        player.message("<col=255>Your farming patch of " + herb.getSeedName().replaceAll(" seed", "") + " is ready to be harvested.");
                    player.setFarmingState(index, FarmingOld.State.HARVEST.getId());
                }
            }
        }
    }

    private void resetValues(int id) {
        player.setFarmingHarvest(id, 0);
        player.setFarmingSeedId(id, 0);
        player.setFarmingState(id, 0);
        player.setFarmingTime(id, 0);
    }

    public void updateObjects() {
        for (int index = 0; index < FarmingOld.MAX_PATCHES; index++) {
            Patch patch = Patch.get(index);
            if (patch == null)
                continue;
            if (player.distanceToPoint(patch.getX(), patch.getY()) > 60)
                continue;
            if (player.getFarmingState(index) < State.RAKED.getId()) {
                GameObject grass = new GameObject(Constants.GRASS_OBJECT, new Tile(patch.getX(), patch.getY(), player.getZ()), 10, 0).setSpawnedfor(Optional.of(player));
                ObjectManager.addObj(grass);
            } else if (player.getFarmingState(index) >= State.RAKED.getId() && player.getFarmingState(index) < State.SEEDED.getId()) {
                GameObject empty = new GameObject(Constants.EMPTY_PATCH, new Tile(patch.getX(), patch.getY(), player.getZ()), 10, 0).setSpawnedfor(Optional.of(player));
                ObjectManager.addObj(empty);
            } else if (player.getFarmingState(index) >= State.SEEDED.getId()) {
                GameObject herb = new GameObject(Constants.HERB_OBJECT, new Tile(patch.getX(), patch.getY(), player.getZ()), 10, 0).setSpawnedfor(Optional.of(player));
                ObjectManager.addObj(herb);
            }
        }
    }

    public boolean isHarvestable(int id) {
        return player.getFarmingState(id) == State.HARVEST.getId();
    }

    public long getLastBerryFarm() {
        return lastPoisonBerryFarm;
    }

    public void setLastBerryFarm(long millis) {
        this.lastPoisonBerryFarm = millis;
    }

    public enum State {
        NONE(0), RAKED(1), COMPOST(2), SEEDED(3), WATERED(4), GROWTH(5), HARVEST(6);

        private final int id;

        State(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    enum Patch {
        FALADOR_PATCH2(0, 3003, 3382),
        FALADOR_PATCH(1, 3003, 3372),
        DONATOR_PATCH(2, 2862, 2953),
        CATHERBY_PATCH(3, 2813, 3463);

        int id, x, y;

        Patch(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public int getId() {
            return this.id;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        static List<Patch> patches = new ArrayList<>();

        static {
            Collections.addAll(patches, Patch.values());
        }

        public static Patch get(int x, int y) {
            for (Patch patch : patches)
                if (patch.getX() == x && patch.getY() == y)
                    return patch;
            return null;
        }

        public static Patch get(int id) {
            for (Patch patch : patches)
                if (patch.getId() == id)
                    return patch;
            return null;
        }
    }
}

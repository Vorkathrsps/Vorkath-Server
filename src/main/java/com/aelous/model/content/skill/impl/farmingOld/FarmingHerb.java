package com.aelous.model.content.skill.impl.farmingOld;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;

/**
 *
 * @author Jason http://https://rune-server.org/members/jason
 * @date Oct 27, 2013
 */
public class FarmingHerb {

    public enum Herb {

        GUAM(5291, 199, 100, 30, 1, 25, 5000),
        MARRENTIL(5292, 201, 200, 50, 7, 25, 4800),
        TARROMIN(5293, 203, 300, 100, 19, 25, 4600),
        HARRALANDER(5294, 205, 500, 150, 26, 25, 4400),
        RANARR(5295, 207, 600, 200, 32, 50, 4200),
        TOADFLAX(5296, 3049, 1000, 250, 38, 50, 4000),
        IRIT(5297, 209, 1250, 300, 44, 50, 3800),
        AVANTOE(5298, 211, 1500, 350, 50, 50, 3600),
        KWUARM(5299, 213, 1750, 375, 56, 50, 3400),
        SNAP_DRAGON(5300, 3051, 2000, 400, 62, 100, 3200),
        CADANTINE(5301, 215, 2250, 425, 67, 100, 3000),
        LANTADYME(5302, 2485, 2300, 450, 73, 100, 2800),
        DRAWF_WEED(5303, 217, 2400, 475, 79, 100, 2600),
        TORSTOL(5304, 219, 2500, 500, 85, 100, 1000);

        int seedId, grimyId, levelRequired, time, petChance;
        double plantXp, harvestXp;

        Herb(int seedId, int grimyId, double plantXp, double harvestXp, int levelRequired, int time, int petChance) {
            this.seedId = seedId;
            this.grimyId = grimyId;
            this.plantXp = plantXp;
            this.harvestXp = harvestXp;
            this.levelRequired = levelRequired;
            this.time = time;
            this.petChance = petChance;
        }

        public int getPetChance() {
            return petChance;
        }

        public int getSeedId() {
            return seedId;
        }

        public int getGrimyId() {
            return grimyId;
        }

        public double getPlantingXp() {
            return plantXp;
        }

        public double getHarvestingXp() {
            return harvestXp;
        }

        public int getLevelRequired() {
            return levelRequired;
        }

        public int getGrowthTime() {
            return time;
        }

        public String getSeedName() {
            return World.getWorld().definitions().get(ItemDefinition.class, seedId).name;
        }

        public String getGrimyName() {
            return World.getWorld().definitions().get(ItemDefinition.class, grimyId).name;
        }
    }

    public static Herb getHerbForSeed(int seedId) {
        for (Herb h : Herb.values())
            if (h.getSeedId() == seedId)
                return h;
        return null;
    }

    public static Herb getHerbForGrimy(int grimyId) {
        for (Herb h : Herb.values())
            if (h.getGrimyId() == grimyId)
                return h;
        return null;
    }

}

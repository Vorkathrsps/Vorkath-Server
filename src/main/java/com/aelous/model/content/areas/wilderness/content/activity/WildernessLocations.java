package com.aelous.model.content.areas.wilderness.content.activity;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.areas.impl.WildernessArea;

/**
 * @author Zerikoth
 * @Since september 24, 2020
 */
public enum WildernessLocations {

    EDGEVILLE {
        @Override
        public String location() {
            return "Edgeville";
        }

        @Override
        public int[] regionIds() {
            return new int[] { 12343, 12087 };
        }

    },

    PIRATES {
        @Override
        public String location() {
            return "Pirates Hideout";
        }

        @Override
        public int[] regionIds() {
            return new int[] { 12093};
        }

    },

    ROGUE_CASTLE {
        @Override
        public String location() {
            return "Rogues' Castle";
        }

        @Override
        public int[] regionIds() {
            return new int[] { 13117};
        }

    },


    GDZ {
        @Override
        public String location() {
            return "Demonic Ruins";
        }

        @Override
        public int[] regionIds() {
            return new int[] { 13116};
        }

    },

    LEVER {
        @Override
        public String location() {
            return "Edge Lever";
        }

        @Override
        public int[] regionIds() {
            return new int[] {12605};
        }

    },

    FORTY_FOUR_PORTAL {
        @Override
        public String location() {
            return "44's";
        }

        @Override
        public int[] regionIds() {
            return new int[] {11836};
        }

    },

    GRAVEYARD {
        @Override
        public String location() {
            return "Graveyard of Shadows";
        }

        @Override
        public int[] regionIds() {
            return new int[] { 12601 };
        }

    },

    MAGE_BANK {

        @Override
        public String location() {
            return "Mage bank";
        }

        @Override
        public int[] regionIds() {
            return new int[] { 12605, 12349, 12093 };
        }
    };

    /**
     * If the player is in the region
     *
     * @param player
     *            The player
     */
    public boolean isInArea(Player player) {
        for (int region : regionIds()) {
            if (player.tile().region() == region && WildernessArea.inWild(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The location
     */
    public abstract String location();

    /**
     * The region ids relevant
     */
    public abstract int[] regionIds();

}

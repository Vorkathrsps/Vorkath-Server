package com.aelous.model.content.teleport.world_teleport_manager;

import com.aelous.model.map.position.Tile;

/**
 * @author Ynneh | 08/03/2022 - 20:51
 * <https://github.com/drhenny>
 */
public enum TeleportData implements TeleportActions {
    // for now.
    BANDIT_CAMP(new Tile(3034, 3690), 1001, " Bandit Camp", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    CHAOS_TEMPLE(new Tile(3235, 3643), 1000, "Chaos Temple", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    DEMONIC_RUINS(new Tile(3287, 3884), 999, "Demonic Ruins", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    EAST_DRAGONS(new Tile(3343, 3664), 998, "East Dragons", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    GRAVEYARD(new Tile(3161, 3670), 997, "  Graveyard", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    MAGEBANK_WILD(new Tile(3091, 3957), 995, "   Magebank", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    REV_CAVES(new Tile(3127, 3832), 994, "   Rev Caves", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    THE_GATE(new Tile(3225, 3903), 993, "    The Gate", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    WEST_DRAGONS(new Tile(2978, 3598), 992, "West Dragons", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    BLACK_CHINS(new Tile(3149, 3779), 1101, "  Black Chins", 3) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    COWS(new Tile(3261, 3271), 1008, "        Cows", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    DAGANNOTHS(new Tile(2443, 10146, 0), 1007, " Dagannoths", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    EXPERIMENTS(new Tile(3556, 9944), 1006, " Experiments", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    LIZARDMEN(new Tile(1453, 3694, 0), 1005, "  Lizardmen", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    ROCK_CRABS(new Tile(2706, 3713), 1004, "  Rock Crabs", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    SKELE_WYVERNS(new Tile(3056, 9562, 0), 1003, "Skele Wyverns", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    YAKS(new Tile(2323, 3800), 1002, "        Yaks", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    SMOKE_DEVILS(new Tile(2404, 9417, 0), 1043, "Smoke Devils", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    SLAYER_TOWER(new Tile(3420, 3535, 0), 1092, " Slayer Tower", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    BRIMHAVEN_DUNGEON(new Tile(2709, 9564, 0), 1095, "Brimhaven Du", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    TAVERLEY_DUNGEON(new Tile(2884, 9799, 0), 1094, " Taverley Du", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    CATACOMBS(new Tile(1666, 10048, 0), 1090, " Catacombs", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    SAND_CRABS(new Tile(1728, 3463, 0), 1158, "  Sand Crabs", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    FIRE_GIANTS(new Tile(2568, 9892, 0), 1159, "  Fire Giants", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    SLAYER_STRONGHOLD(new Tile(2431, 3421, 0), 1160, "Slayer Strong", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    RELLEKKA_SLAYER(new Tile(2803, 9998, 0), 1161, "Rellekka Du", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    DARK_BEAST(new Tile(2023, 4635, 0), 1162, "  Dark Beast", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    KALPHITE_LAIR(new Tile(3485, 9509, 2), 1163, "Kalphite Lair", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    ANCIENT_CAVERN(new Tile(1768, 5366, 1), 1190, "Ancient Cave", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    WYVERNS(new Tile(3609, 10278, 0), 1009, "Wyvern Cave", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    KARUULM_SLAYER_DUNGEON(new Tile(1311, 3812, 0), 1448, "Karuulm Du", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    LITHKREN(new Tile(3565, 3998, 0), 1449, "    Lithkren", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    LUMBRIDGE_SWAMP_CAVE(new Tile(3169, 9570, 0), 1814, "Lumbridge Sw", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    BRINE_RAT_CAVE(new Tile(2692, 10124, 0), 1818, "Brine Rat Cav", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    MOSS_LE_HARMLESS_CAVE(new Tile(3826, 9425, 0), 1820, "Mos Le'Harm", 4) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    CALLISTO(new Tile(3307, 3837), 1031, "  Callisto", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    CERBERUS(new Tile(1315, 1251), 1030, "   Cerberus", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    CHAOS_FAN(new Tile(2992, 3851), 1029, "   Chaos Fan", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    CORP_BEAST(new Tile(2969, 4382, 2), 1028, "  Corp Beast", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    CRAZY_ARCH(new Tile(2976, 3694), 1027, "  Crazy Arch", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    DAGG_KINGS(new Tile(1912, 4367), 1026, "  Dagg Kings", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    DEMON_GORILLAS(new Tile(2128, 5647), 1025, "Demon Gorillas", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    GWD(null, 1024, "         GWD", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    KBD(new Tile(3005, 3848), 1023, "         KBD", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    KRAKEN(new Tile(2280, 10016), 1021, "     Kraken", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    SHAMAN(new Tile(1420, 3715), 1020, "     Shaman", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    THERMO(new Tile(2379, 9452), 1018, "     Thermo", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    VENENATIS(new Tile(3319, 3745), 1017, "   Venenatis", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    VETION(new Tile(3239, 3783), 1016, "      Vet'ion", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    SCORPIA(new Tile(3232, 3950), 1042, "      Scorpia", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    CHAOS_ELEMENTAL(new Tile(3269, 3927), 1044, "   Chaos Ele", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    ZULRAH(new Tile(2204, 3056), 1015, "      Zulrah", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    VORKATH(new Tile(2273, 4049), 1104, "   Vorkath", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    WORLD_BOSS(null, 1019, "   World boss", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    KQ(new Tile(3507, 9494,2), 1022, "          KQ", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    GIANT_MOLE(new Tile(1752, 5234), 1447, "Giant Mole", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    ALCHEMICAL_HYDRA(new Tile(1354, 10258), 1758, "Alchy Hydra", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    BARRELCHEST(new Tile(3287, 3884), 1795, "Barrelchest", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return true;
        }
    },
    CORRUPTED_NECHRYARCH(new Tile(1885, 3869), 1819, "Corrupted nechryarch", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    RAIDS_AREA(new Tile(1245, 3561), 1819, "Raids area", 5) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    BARROWS(new Tile(3565, 3306), 1014, "     Barrows", 6) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    FIGHT_CAVE(new Tile(2440, 5172), 1012, "    Fight Cave", 6) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    MAGEBANK(new Tile(2540, 4716), 1011, "   Magebank", 6) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    WARRIORS_GUILD(new Tile(2879, 3546), 1808, "Warriors Guild", 6) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    PEST_CONTROL(new Tile(2662, 2647), 1804, "Pest Control", 6) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    PORT_PISCARILIUS(new Tile(1815, 3690), 1093, "Port Piscaril", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    GNOME_AGILITY(new Tile(2474, 3438), 1096, "Gnome Agility", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    BARB_AGILITY(new Tile(2552, 3563), 1097, " Barb Agility", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    FARMING_AREA(new Tile(2815, 3457), 1116, " Farming Area", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    CATHERBY(new Tile(2809, 3439), 1450, " Catherby", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    KARAMJA(new Tile(2918, 3176), 1816, " Karamja", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    LUNAR_ISLE(new Tile(2108, 3914), 1817, "Lunar Isle", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    TZHAAR_CITY(new Tile(2451, 5152), 1806, "Tzhaar City", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    EDGEVILE(new Tile(3086, 3490), 1798, "Edgeville", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    LUMBRIDGE(new Tile(3222, 3218), 1815, "Lumbridge", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    VARROCK(new Tile(3210, 3424), 1807, "Varrock", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    FALADOR(new Tile(2964, 3378), 1799, "Falador", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    CAMELOT(new Tile(2757, 3477), 1796, "Camelot", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    ARDOUGNE(new Tile(2662, 3305), 1794, "Ardougne", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    CANIFIS(new Tile(3495, 3486), 1797, "Canifis", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    KELDAGRIM(new Tile(2843, 10204), 1802, "Keldagrim", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    YANILLE(new Tile(2544, 3092), 1810, "Yanille", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    FISHING_AREAS(null, 1801, "Fishing Areas", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    MINING_AREAS(null, 1803, "Mining Areas", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    WOODCUTTING_AREAS(null, 1809, "Woodcutting", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    HUNTER_AREAS(null, 1101, "Hunter Areas", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    },
    SMITHING_ANVIL(new Tile(3189, 3425), 1805, "Smithing Anvil", 7) {
        @Override
        public int paymentAmount() {
            return 0;
        }

        @Override
        public boolean dangerous() {
            return false;
        }
    };

    TeleportData(Tile tile, int spriteID, String teleportName, int tabIndex) {
        this.tile = tile;
        this.spriteID = spriteID;
        this.teleportName = teleportName;
        this.tabIndex = tabIndex;
    }

    public final Tile tile;
    public final int spriteID;
    public final String teleportName;
    public final int tabIndex;


}

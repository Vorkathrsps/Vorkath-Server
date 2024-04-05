package com.cryptic.model.content.teleport.newinterface;

import com.cryptic.model.map.position.Tile;

import static com.cryptic.model.content.teleport.newinterface.NewTeleportInterface.*;
import static com.cryptic.model.content.teleport.newinterface.TeleDifficulty.*;

public enum NewTeleData {
    //Training Teleports
    COWS(new Tile(3261, 3271), "Lumbridge Cows", "", TRAINING, MEDIUM),
    DAGANNOTHS(new Tile(2443, 10146), "Dagannoths Cave", "", TRAINING, MEDIUM),
    EXPERIMENTS(new Tile(3556, 9944), "Experiments Cave", "", TRAINING, MEDIUM),
    ROCK_CRABS(new Tile(2706, 3713), "Rock Crabs", "", TRAINING, MEDIUM),
    SAND_CRABS(new Tile(1728, 3463), "Sand Crabs", "", TRAINING, MEDIUM),
    YAKS(new Tile(2323, 3800), "Yaks", "", TRAINING, MEDIUM),
    FIRE_GIANTS(new Tile(2568, 9892), "Fire Giants", "", TRAINING, MEDIUM),
    DARK_BEASTS(new Tile(2568, 9892), "Mourner Tunnels", "", TRAINING, MEDIUM),
    LITHKREN(new Tile(3550, 10450), "Lithkren Vault", "", TRAINING, MEDIUM),
    LUMBRIDGE_SWAMP_CAVE(new Tile(3169, 9570), "Lumbridge Swamps Cave", "", TRAINING, MEDIUM),
    BRINE_RAT_CAVE(new Tile(2692, 10124), "Brine Rat Cavern", "", TRAINING, MEDIUM),
    SUQAH(new Tile(2112, 3862), "Suqah", "", SLAYING, MEDIUM),


    //Slayer teleports
    SKELE_WYVERNS(new Tile(3056, 9562), "Skeletal Wyverns", "", SLAYING, MEDIUM),
    SMOKE_DEVILS(new Tile(2404, 9417), "Smoke Devils", "", SLAYING, MEDIUM),
    LIZARDMEN(new Tile(1453, 3694), "Lizardmen", "", SLAYING, MEDIUM),
    SLAYER_TOWER(new Tile(3420, 3535), "Slayer Tower", "", SLAYING, MEDIUM),
    CATACOMBS(new Tile(1666, 10048), "Catacombs of Kourend", "", SLAYING, MEDIUM),
    RELLEKKA_SLAYER(new Tile(2803, 9998), "Fremennik Slayer Dungeon", "", SLAYING, MEDIUM),
    SLAYER_STRONGHOLD(new Tile(2431, 3421), "Stronghold Slayer Cave", "", SLAYING, MEDIUM),
    KALPHITE_LAIR(new Tile(3485, 9509), "Kalphite Lair", "", SLAYING, MEDIUM),
    ANCIENT_CAVERN(new Tile(1772, 5366), "Ancient Cavern", "", SLAYING, MEDIUM),
    BRIMHAVEN_DUNGEON(new Tile(2709, 9564), "Brimhaven Dungeon", "", TRAINING, MEDIUM),
    TAVERLY_DUNGEON(new Tile(2884, 9799), "Taverly Dungeon", "", TRAINING, MEDIUM),
    ANCIENT_WYVERNS(new Tile(3609, 10278), "Wyvern Cave", "", SLAYING, MEDIUM),
    KARUULM_SLAYER_DUNGEON(new Tile(1311, 3812), "Karuulm Slayer Dungeon", "", SLAYING, MEDIUM),
    MOSS_LE_HARMLESS_CAVE(new Tile(3826, 9425), "Cave Horrors", "", SLAYING, MEDIUM),
    WILDERNESS_SLAYER_CAVE(new Tile(3385, 10053),"Wilderness Slayer Cave", "", SLAYING, MEDIUM),
    TROLLS(new Tile(2849, 3679),"Troll StrongHold", "", SLAYING, MEDIUM),

    //Bossing teleports
    CALLISTO(new Tile(3267, 3844), "Callisto", "@red@Dangerous Teleport Level:35 Multi-combat Wilderness", BOSSING, MEDIUM),
    CHAOS_FAN(new Tile(2992, 3851), "Chaos Fanatic", "@red@Dangerous Teleport Level:42 Wilderness", BOSSING, MEDIUM),
    CRAZY_ARCH(new Tile(2976, 3694), "Crazy archaeologist", "@red@Dangerous Teleport Level:22 Wilderness", BOSSING, MEDIUM),

    KBD(new Tile(2271, 4682), "King Black Dragon", "@red@Dangerous Teleport Level:42 Wilderness", BOSSING, MEDIUM),

    VENENATIS(new Tile(3343, 3813), "Venenatis", "@red@Dangerous Teleport Level:35 Multi-combat Wilderness", BOSSING, MEDIUM),

    VETION(new Tile(3222, 3799, 0), "Vet'ion", "@red@Dangerous Teleport Level:35 Multi-combat Wilderness", BOSSING, MEDIUM),

    SCORPIA(new Tile(3232, 3950), "Scorpia", "@red@Dangerous Teleport Level:54 Multi-combat Wilderness", BOSSING, MEDIUM),

    CHAOS_ELEMENTAL(new Tile(3269, 3927), "Chaos Elemental", "@red@Dangerous Teleport Level:51 Multi-combat Wilderness", BOSSING, MEDIUM),
    BARRELCHEST(new Tile(3287, 3884), "Barrelchest", "@red@Dangerous Teleport Level:46 Multi-combat Wilderness", BOSSING, MEDIUM),
    WORLD_BOSS(new Tile(1,1), "Wilderness Event Boss", "@red@This teleport has different wilderness locations BEWARE!", BOSSING, MEDIUM),
    NIGHTMARE(new Tile(3808, 9752, 1), "The Nightmare", "", BOSSING, MEDIUM),
    CERBERUS(new Tile(1315, 1251), "Cerberus", "", BOSSING, MEDIUM),
    CORP_BEAST(new Tile(2969, 4382, 2), "Corporal Beast", "", BOSSING, MEDIUM),
    DAGG_KINGS(new Tile(1912, 4367), "Dagannoth Kings", "", BOSSING, MEDIUM),
    DEMON_GORILLAS(new Tile(2128, 5647), "Demonic gorillas", "", BOSSING, MEDIUM),

    GWD(new Tile(1,1), "GodWars Bosses", "", BOSSING, MEDIUM),

    KRAKEN(new Tile(2280, 10016), "Kraken", "", BOSSING, MEDIUM),
    SHAMAN(new Tile(1420, 3715), "Lizardmen Shaman", "", BOSSING, MEDIUM),
    THERMO(new Tile(2379, 9452), "Thermonuclear smoke devil", "", BOSSING, MEDIUM),

    ZULRAH(new Tile(2204, 3056), "Zulrah", "", BOSSING, MEDIUM),

    VORKATH(new Tile(2273, 4049), "Vorkath", "", BOSSING, MEDIUM),

    KQ(new Tile(3507, 9494), "Kalphite Queen", "", BOSSING, MEDIUM),

    GIANT_MOLE(new Tile(1752, 5234), "Giant Mole", "", BOSSING, MEDIUM),

    ALCHEMICAL_HYDRA(new Tile(1354, 10258), "Alchemical Hydra", "", BOSSING, MEDIUM),
    NEX(new Tile(2903, 5203), "Nex", "", BOSSING, HARD),
    SCURRIUS(new Tile(3279, 9869), "Scurrius", "", BOSSING, MEDIUM),
    //Skilling Teleports
    GNOME_AGILITY(new Tile(2474, 3438),"Gnome Stronghold Agility Course", "", SKILLING, MEDIUM),
    BARB_AGILITY(new Tile(2552, 3563),"Barbarian Outpost Agility Course", "", SKILLING, MEDIUM),
    FARMING_AREA(new Tile(2815, 3457),"Farming Area", "", SKILLING, MEDIUM),
    FISHING_AREAS(new Tile(1,1),"Fishing Areas", "", SKILLING, MEDIUM),
    MINING_AREAS(new Tile(1,1),"Mining Areas", "", SKILLING, MEDIUM),
    WOODCUTTING_AREAS(new Tile(1,1),"Woodcutting Areas", "", SKILLING, MEDIUM),

    HUNTER_AREAS(new Tile(1,1),"Hunter Areas", "", SKILLING, MEDIUM),
    SMITHING_ANVIL(new Tile(3189, 3425),"Varrock Smithing Anvil", "", SKILLING, MEDIUM),



    //Minigame teleports
    RAIDS_AREA(new Tile(1245, 3561),"Chambers of Xeric", "", MINIGAMES, MEDIUM),
    TOB(new Tile(3667, 3219),"Theatre Of Blood", "", MINIGAMES, HARD),

    BARROWS(new Tile(3565, 3306),"Barrows", "", MINIGAMES, MEDIUM),
    FIGHT_CAVE(new Tile(2440, 5172),"Fight Cave", "", MINIGAMES, MEDIUM),
    DUEL_ARENA(new Tile(3367, 3266), "Duel Arena", "", MINIGAMES, MEDIUM),

    //Wilderness teleports
    MAGEBANK_WILD(new Tile(2539, 4715),"Magebank", "", WILDERNESS, MEDIUM),
    FEROX_ENCLAVE(new Tile(3131, 3629),"Ferox Enclave", "", WILDERNESS, MEDIUM),
    BOUNTY_HUNTER(new Tile(3419, 4067),"Bounty Hunter", "", WILDERNESS, MEDIUM),
    BATTLE_MAGES(new Tile(3105, 3956),"Battle Mages", "@red@Dangerous Teleport Level:52 Wilderness", WILDERNESS, EASY),
    EAST_DRAGONS(new Tile(3343, 3664),"East Green Dragons", "@red@Dangerous Teleport Level:19 Wilderness", WILDERNESS, MEDIUM),
    WEST_DRAGONS(new Tile(2978, 3598),"West Green Dragons", "@red@Dangerous Teleport Level:10 Wilderness", WILDERNESS, MEDIUM),
    REV_CAVES(new Tile(3127, 3832),"Revenant Cave", "@red@Dangerous Teleport Level:40 Wilderness", WILDERNESS, MEDIUM),
    GRAVEYARD(new Tile(3161, 3670),"Graveyard", "@red@Dangerous Teleport Level:19 Wilderness", WILDERNESS, MEDIUM),
    BLACK_CHINS(new Tile(3149, 3779),"Black chinchompas", "@red@Dangerous Teleport Level:33 Wilderness", WILDERNESS, MEDIUM),
    FOURTY_FOUR(new Tile(2980, 3871),"44'S Obelisk", "@red@Dangerous Teleport Level:44 Wilderness", WILDERNESS, MEDIUM),
    DEMONIC_RUINS(new Tile(3287, 3884),"Demonic Ruins", "@red@Dangerous Teleport Level:46 Multi-combat Wilderness", WILDERNESS, MEDIUM),
    BANDIT_CAMP(new Tile(3034, 3690),"Bandit Camp", "@red@Dangerous Teleport Level:22 Multi-combat Wilderness", WILDERNESS, MEDIUM),
    CHAOS_TEMPLE(new Tile(3235, 3643),"Chaos Temple", "@red@Dangerous Teleport Level:16 Multi-combat Wilderness", WILDERNESS, MEDIUM),
    THE_GATE(new Tile(3225, 3903),"Wilderness Gates", "@red@Dangerous Teleport Level:48 Multi-combat Wilderness", WILDERNESS, MEDIUM),

    //City Teleports
    CATHERBY(new Tile(2809, 3439),"Catherby", "", CITIES, MEDIUM),

    KARAMJA(new Tile(2918, 3176),"Karamja", "", CITIES, MEDIUM),
    LUNAR_ISLE(new Tile(2108, 3914),"Lunar Isle", "", CITIES, MEDIUM),
    TZHAAR_CITY(new Tile(2451, 5152),"Tzhaar City", "", CITIES, MEDIUM),

    LUMBRIDGE(new Tile(3222, 3218),"Lumbridge", "", CITIES, MEDIUM),
    VARROCK(new Tile(3210, 3424),"Varrock", "", CITIES, MEDIUM),
    DRAYNOR(new Tile(3092, 3249),"Draynor", "", CITIES, MEDIUM),

    FALADOR(new Tile(2964, 3378),"Falador", "", CITIES, MEDIUM),
    CAMELOT(new Tile(2757, 3477),"Camelot", "", CITIES, MEDIUM),
    ARDOUGNE(new Tile(2662, 3305),"Ardougne", "", CITIES, MEDIUM),
    CANIFIS(new Tile(3495, 3486),"Canifis", "", CITIES, MEDIUM),
    KELDAGRIM(new Tile(2843, 10204),"Keldagrim", "", CITIES, MEDIUM),
    YANILLE(new Tile(2544, 3092),"Yanille", "", CITIES, MEDIUM),


    //Misc Teleports
    WARRIORS_GUILD(new Tile(2879, 3546),"Warriors Guild", "", MISCELLANEOUS, EASY),
    GAMBLING_ZONE(new Tile(3090, 3466),"Gambling Area", "", MISCELLANEOUS, EASY),
    TOURNAMENT(new Tile(3113, 3513),"PVP Tournament", "", MISCELLANEOUS, EASY)

    ;

    NewTeleData(Tile tile, String text, String description, int category, TeleDifficulty difficulty) {
        this.tile = tile;
        this.text = text;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
    }

    public final Tile tile;
    public String text;
    public String description;
    public final int category;
    public TeleDifficulty difficulty;

}

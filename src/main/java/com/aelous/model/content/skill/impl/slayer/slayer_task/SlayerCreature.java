package com.aelous.model.content.skill.impl.slayer.slayer_task;

import com.aelous.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.CustomNpcIdentifiers;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author PVE
 * @Since juli 19, 2020
 */
public enum SlayerCreature {

    /*
    1=Monkeys, 2=Goblins, 3=Rats, 4=Spiders, 5=Birds, 6=Cows, 7=Scorpions, 8=Bats, 9=Wolves, 10=Zombies,
    11=Skeletons, 12=Ghosts, 13=Bears, 14=Hill Giants, 15=Ice Giants, 16=Fire Giants, 17=Moss Giants, 18=Trolls,
    19=Ice Warriors, 20=Ogres, 21=Hobgoblins, 22=Dogs, 23=Ghouls, 24=Green Dragons, 25=Blue Dragons, 26=Red Dragons,
    27=Black Dragons, 28=Lesser Demons, 29=Greater Demons, 30=Black Demons, 31=Hellhounds, 32=Shadow Warriors,
    33=Werewolves, 34=Vampires, 35=Dagannoth, 36=Turoth, 37=Cave Crawlers, 38=Banshees, 39=Crawling Hands, 40=Infernal Mages,
    41=Aberrant Spectres, 42=Abyssal Demons, 43=Basilisks, 44=Cockatrice, 45=Kurask, 46=Gargoyles, 47=Pyrefiends, 48=Bloodveld,
    49=Dust Devils, 50=Jellies, 51=Rockslugs, 52=Nechryael, 53=Kalphite, 54=Earth Warriors, 55=Otherworldly Beings, 56=Elves,
    57=Dwarves, 58=Bronze Dragons, 59=Iron Dragons, 60=Steel Dragons, 61=Wall Beasts, 62=Cave Slimes, 63=Cave Bugs, 64=Shades,
    65=Crocodiles, 66=Dark Beasts, 67=Mogres, 68=Desert Lizards, 69=Fever Spiders, 70=Harpie Bug Swarms, 71=Sea Snakes,
    72=Skeletal Wyverns, 73=Killerwatts, 74=Mutated Zygomites, 75=Icefiends, 76=Minotaurs, 77=Fleshcrawlers, 78=Catablepon,
    79=Ankou, 80=Cave Horrors, 81=Jungle Horrors, 82=Goraks, 83=Suqahs, 84=Brine Rats, 85=Minions of Scabaras, 86=Terror Dogs,
    87=Molanisks, 88=Waterfiends, 89=Spiritual Creatures, 90=Lizardmen, 92=Cave Kraken, 93=Mithril Dragons, 94=Aviansies,
    95=Smoke Devils, 96=TzHaar, 97=TzTok-Jad, 98=Bosses
     */

    MONKEYS(1, 1, 3, null, NpcIdentifiers.MONKEY_1038, NpcIdentifiers.MONKEY_2848, NpcIdentifiers.MONKEY_5279, NpcIdentifiers.MONKEY_5280),
    GOBLINS(2, 1, 3, null, NpcIdentifiers.GOBLIN, NpcIdentifiers.GOBLIN_656, NpcIdentifiers.GOBLIN_657, NpcIdentifiers.GOBLIN_658, NpcIdentifiers.GOBLIN_659, NpcIdentifiers.GOBLIN_660, NpcIdentifiers.GOBLIN_661, NpcIdentifiers.GOBLIN_662, NpcIdentifiers.GOBLIN_663, NpcIdentifiers.GOBLIN_664, NpcIdentifiers.GOBLIN_665, NpcIdentifiers.GOBLIN_666, NpcIdentifiers.GOBLIN_657, NpcIdentifiers.GOBLIN_658, NpcIdentifiers.GOBLIN_674, NpcIdentifiers.GOBLIN_677, NpcIdentifiers.GOBLIN_678, NpcIdentifiers.GOBLIN_2245, NpcIdentifiers.GOBLIN_2246, NpcIdentifiers.GOBLIN_2247, NpcIdentifiers.GOBLIN_2248, NpcIdentifiers.GOBLIN_2249, NpcIdentifiers.GOBLIN_2484, NpcIdentifiers.GOBLIN_2485, NpcIdentifiers.GOBLIN_2486, NpcIdentifiers.GOBLIN_2487, NpcIdentifiers.GOBLIN_2488, NpcIdentifiers.GOBLIN_2489, NpcIdentifiers.GOBLIN_3028, NpcIdentifiers.GOBLIN_3029, NpcIdentifiers.GOBLIN_3030, NpcIdentifiers.GOBLIN_3031, NpcIdentifiers.GOBLIN_3032, NpcIdentifiers.GOBLIN_3033, NpcIdentifiers.GOBLIN_3034, NpcIdentifiers.GOBLIN_3035, NpcIdentifiers.GOBLIN_3036, NpcIdentifiers.GOBLIN_3037, NpcIdentifiers.GOBLIN_3038, NpcIdentifiers.GOBLIN_3039, NpcIdentifiers.GOBLIN_3040, NpcIdentifiers.GOBLIN_3041, NpcIdentifiers.GOBLIN_3042, NpcIdentifiers.GOBLIN_3043, NpcIdentifiers.GOBLIN_3044, NpcIdentifiers.GOBLIN_3045, NpcIdentifiers.GOBLIN_3046, NpcIdentifiers.GOBLIN_3047, NpcIdentifiers.GOBLIN_3048, NpcIdentifiers.GOBLIN_3051, NpcIdentifiers.GOBLIN_3052, NpcIdentifiers.GOBLIN_3053, NpcIdentifiers.GOBLIN_3054, NpcIdentifiers.GOBLIN_3073, NpcIdentifiers.GOBLIN_3074, NpcIdentifiers.GOBLIN_3075, NpcIdentifiers.GOBLIN_3076, NpcIdentifiers.GOBLIN_CHAMPION, NpcIdentifiers.GOBLIN_5192, NpcIdentifiers.GOBLIN_5193, NpcIdentifiers.GOBLIN_5195, NpcIdentifiers.GOBLIN_5196, NpcIdentifiers.GOBLIN_5197, NpcIdentifiers.GOBLIN_5198, NpcIdentifiers.GOBLIN_5199, NpcIdentifiers.GOBLIN_5200, NpcIdentifiers.GOBLIN_5201, NpcIdentifiers.GOBLIN_5202, NpcIdentifiers.GOBLIN_5203, NpcIdentifiers.GOBLIN_5204, NpcIdentifiers.GOBLIN_5205, NpcIdentifiers.GOBLIN_5206, NpcIdentifiers.GOBLIN_5207, NpcIdentifiers.GOBLIN_5208, NpcIdentifiers.SERGEANT_STRONGSTACK, NpcIdentifiers.SERGEANT_GRIMSPIKE, NpcIdentifiers.SERGEANT_STEELWILL),
    RATS(3, 1, 3, null, NpcIdentifiers.RAT_2854, NpcIdentifiers.GIANT_RAT_2864, NpcIdentifiers.GIANT_RAT_2863, NpcIdentifiers.GIANT_RAT_2856, NpcIdentifiers.GIANT_RAT_2859),
    SPIDERS(4, 1, 3, new Tile(3170, 3883), NpcIdentifiers.GIANT_SPIDER_3017, NpcIdentifiers.GIANT_SPIDER_3018, NpcIdentifiers.GIANT_SPIDER, NpcIdentifiers.SHADOW_SPIDER, NpcIdentifiers.JUNGLE_SPIDER, NpcIdentifiers.JUNGLE_SPIDER_5243, NpcIdentifiers.JUNGLE_SPIDER_6267, NpcIdentifiers.JUNGLE_SPIDER_6271, NpcIdentifiers.DEADLY_RED_SPIDER, NpcIdentifiers.POISON_SPIDER, NpcIdentifiers.POISON_SPIDER_5373, NpcIdentifiers.BLESSED_SPIDER, NpcIdentifiers.BLESSED_SPIDER_8978, NpcIdentifiers.KALRAG, NpcIdentifiers.CRYPT_SPIDER, NpcIdentifiers.GIANT_CRYPT_SPIDER, NpcIdentifiers.TEMPLE_SPIDER, NpcIdentifiers.SARACHNIS, NpcIdentifiers.VENENATIS_6610, NpcIdentifiers.VENENATIS),
    CHICKENS(5, 1, 3, null, NpcIdentifiers.CHICKEN, NpcIdentifiers.CHICKEN_1174, NpcIdentifiers.CHICKEN_2804, NpcIdentifiers.CHICKEN_2805, NpcIdentifiers.CHICKEN_2806, NpcIdentifiers.CHICKEN_3316, NpcIdentifiers.CHICKEN_3661, NpcIdentifiers.CHICKEN_3662),
    COWS(6, 1, 3, null, NpcIdentifiers.COW, NpcIdentifiers.COW_2791, NpcIdentifiers.COW_CALF, NpcIdentifiers.COW_2793, NpcIdentifiers.COW_CALF_2794, NpcIdentifiers.COW_2795, NpcIdentifiers.COW_5842, NpcIdentifiers.COW_6401, NpcIdentifiers.COW_CALF_2801),
    SCORPION(7, 1, 7, new Tile(3232, 3940), NpcIdentifiers.SCORPION, NpcIdentifiers.SCORPION_2480, NpcIdentifiers.SCORPION_3024, NpcIdentifiers.SCORPION_5242, NpcIdentifiers.SCORPIA, NpcIdentifiers.SCORPIAS_OFFSPRING),
    BATS(8, 1, 5, null, NpcIdentifiers.BAT, NpcIdentifiers.GIANT_BAT, NpcIdentifiers.GIANT_BAT_4504, NpcIdentifiers.GIANT_BAT_6824),
    WOLF(9, 1, 20, null, NpcIdentifiers.WOLF, NpcIdentifiers.WOLF_110, NpcIdentifiers.WOLF_116, NpcIdentifiers.WOLF_117, NpcIdentifiers.WOLF_231, NpcIdentifiers.WOLF_2490, NpcIdentifiers.WOLF_2491, NpcIdentifiers.WOLF_3912, NpcIdentifiers.BIG_WOLF_115, NpcIdentifiers.DESERT_WOLF_4650, NpcIdentifiers.DESERT_WOLF_4651, NpcIdentifiers.ICE_WOLF, NpcIdentifiers.ICE_WOLF_646, NpcIdentifiers.ICE_WOLF_647, NpcIdentifiers.ICE_WOLF_710, NpcIdentifiers.ICE_WOLF_711, NpcIdentifiers.ICE_WOLF_712, NpcIdentifiers.ICE_WOLF_713, NpcIdentifiers.ICE_WOLF_714, NpcIdentifiers.ICE_WOLF_715),
    ZOMBIE(10, 1, 10, new Tile(3161, 3670), NpcIdentifiers.ZOMBIE, NpcIdentifiers.ZOMBIE_27, NpcIdentifiers.ZOMBIE_28, NpcIdentifiers.ZOMBIE_29, NpcIdentifiers.ZOMBIE_30, NpcIdentifiers.ZOMBIE_31, NpcIdentifiers.ZOMBIE_32, NpcIdentifiers.ZOMBIE_33, NpcIdentifiers.ZOMBIE_34, NpcIdentifiers.ZOMBIE_35, NpcIdentifiers.ZOMBIE_36, NpcIdentifiers.ZOMBIE_37, NpcIdentifiers.ZOMBIE_38, NpcIdentifiers.ZOMBIE_39, NpcIdentifiers.ZOMBIE_40, NpcIdentifiers.ZOMBIE_41, NpcIdentifiers.ZOMBIE_42, NpcIdentifiers.ZOMBIE_43, NpcIdentifiers.ZOMBIE_44, NpcIdentifiers.ZOMBIE_45, NpcIdentifiers.ZOMBIE_46, NpcIdentifiers.ZOMBIE_47, NpcIdentifiers.ZOMBIE_48, NpcIdentifiers.ZOMBIE_49, NpcIdentifiers.ZOMBIE_50, NpcIdentifiers.ZOMBIE_51, NpcIdentifiers.ZOMBIE_52, NpcIdentifiers.ZOMBIE_53, NpcIdentifiers.ZOMBIE_54, NpcIdentifiers.ZOMBIE_55, NpcIdentifiers.ZOMBIE_56, NpcIdentifiers.ZOMBIE_57, NpcIdentifiers.ZOMBIE_58, NpcIdentifiers.ZOMBIE_59, NpcIdentifiers.ZOMBIE_60, NpcIdentifiers.ZOMBIE_61, NpcIdentifiers.ZOMBIE_62, NpcIdentifiers.ZOMBIE_63, NpcIdentifiers.ZOMBIE_64, NpcIdentifiers.ZOMBIE_65, NpcIdentifiers.ZOMBIE_66, NpcIdentifiers.ZOMBIE_67, NpcIdentifiers.ZOMBIE_68),
    SKELETON(11, 1, 15, new Tile(3012, 3590), NpcIdentifiers.SKELETON, NpcIdentifiers.SKELETON_71, NpcIdentifiers.SKELETON_72, NpcIdentifiers.SKELETON_73, NpcIdentifiers.SKELETON_74, NpcIdentifiers.SKELETON_75, NpcIdentifiers.SKELETON_76, NpcIdentifiers.SKELETON_77, NpcIdentifiers.SKELETON_78, NpcIdentifiers.SKELETON_79, NpcIdentifiers.SKELETON_80, NpcIdentifiers.SKELETON_81, NpcIdentifiers.SKELETON_82, NpcIdentifiers.SKELETON_83, NpcIdentifiers.SKELETON_CHAMPION, NpcIdentifiers.VETION, NpcIdentifiers.VETION_REBORN),
    GHOSTS(12, 1, 13, null, NpcIdentifiers.GHOST, NpcIdentifiers.GHOST_86, NpcIdentifiers.GHOST_87, NpcIdentifiers.GHOST_88, NpcIdentifiers.GHOST_89, NpcIdentifiers.GHOST_90, NpcIdentifiers.GHOST_91, NpcIdentifiers.GHOST_92, NpcIdentifiers.GHOST_93, NpcIdentifiers.GHOST_94, NpcIdentifiers.GHOST_96, NpcIdentifiers.GHOST_97, NpcIdentifiers.GHOST_7263, NpcIdentifiers.GHOST_7264),
    BEARS(13, 1, 13, new Tile(3098, 3603), NpcIdentifiers.GRIZZLY_BEAR, NpcIdentifiers.BLACK_BEAR, NpcIdentifiers.BEAR_CUB, NpcIdentifiers.BEAR_CUB_3909, NpcIdentifiers.BEAR_CUB_9199, NpcIdentifiers.GRIZZLY_BEAR_CUB_3425, NpcIdentifiers.GRIZZLY_BEAR_3423, NpcIdentifiers.GRIZZLY_BEAR_CUB, NpcIdentifiers.CALLISTO, NpcIdentifiers.CALLISTO_6609),
    HILL_GIANTS(14, 1, 25, new Tile(3300, 3646), NpcIdentifiers.HILL_GIANT, NpcIdentifiers.HILL_GIANT_2099, NpcIdentifiers.HILL_GIANT_2100, NpcIdentifiers.HILL_GIANT_2101, NpcIdentifiers.HILL_GIANT_2102, NpcIdentifiers.HILL_GIANT_2103, NpcIdentifiers.HILL_GIANT_7261),
    ICE_GIANT(15, 1, 50, new Tile(2950, 3902), NpcIdentifiers.ICE_GIANT, NpcIdentifiers.ICE_GIANT_2086, NpcIdentifiers.ICE_GIANT_2087, NpcIdentifiers.ICE_GIANT_2088, NpcIdentifiers.ICE_GIANT_2089, NpcIdentifiers.ICE_GIANT_7878, NpcIdentifiers.ICE_GIANT_7879, NpcIdentifiers.ICE_GIANT_7880),
    FIRE_GIANTS(16, 1, 65, new Tile(3043, 10342), NpcIdentifiers.FIRE_GIANT, NpcIdentifiers.FIRE_GIANT_2076, NpcIdentifiers.FIRE_GIANT_2077, NpcIdentifiers.FIRE_GIANT_2078, NpcIdentifiers.FIRE_GIANT_2079, NpcIdentifiers.FIRE_GIANT_2080, NpcIdentifiers.FIRE_GIANT_2081, NpcIdentifiers.FIRE_GIANT_2082, NpcIdentifiers.FIRE_GIANT_2083, NpcIdentifiers.FIRE_GIANT_2084, NpcIdentifiers.FIRE_GIANT_7251, NpcIdentifiers.FIRE_GIANT_7252),
    MOSS_GIANT(17, 1, 40, new Tile(3147, 3825), NpcIdentifiers.MOSS_GIANT, NpcIdentifiers.MOSS_GIANT_2091, NpcIdentifiers.MOSS_GIANT_2092, NpcIdentifiers.MOSS_GIANT_2093, NpcIdentifiers.MOSS_GIANT_3851, NpcIdentifiers.MOSS_GIANT_3852, NpcIdentifiers.MOSS_GIANT_7262, NpcIdentifiers.MOSS_GIANT_8736),
    TROLLS(18, 1, 60, null, NpcIdentifiers.TROLL, NpcIdentifiers.TROLL_8470, NpcIdentifiers.TROLL_8471, NpcIdentifiers.TROLL_8472, NpcIdentifiers.TROLL_8473),
    ICE_WARRIOR(19, 1, 45, new Tile(2950, 3902), NpcIdentifiers.ICE_WARRIOR, NpcIdentifiers.ICE_WARRIOR_2842, NpcIdentifiers.ICE_WARRIOR_2851),
    OGRES(20, 1, 20, null, NpcIdentifiers.OGRE_1153, NpcIdentifiers.OGRE_2095, NpcIdentifiers.OGRE_2096, NpcIdentifiers.OGRE_2233),
    HOBGOBLIN(21, 1, 20, null, NpcIdentifiers.HOBGOBLIN_2241, NpcIdentifiers.HOBGOBLIN_3049, NpcIdentifiers.HOBGOBLIN_3050, NpcIdentifiers.HOBGOBLIN_3286, NpcIdentifiers.HOBGOBLIN_3287, NpcIdentifiers.HOBGOBLIN_3288, NpcIdentifiers.HOBGOBLIN_3289, NpcIdentifiers.HOBGOBLIN_4805, NpcIdentifiers.HOBGOBLIN_CHAMPION),
    DOGS(22, 1, 15, null, NpcIdentifiers.WILD_DOG, NpcIdentifiers.WILD_DOG_113),
    GHOULS(23, 1, 25, null, NpcIdentifiers.GHOUL, NpcIdentifiers.GHOUL_CHAMPION),
    GREEN_DRAGONS(24, 1, 52, new Tile(3347, 3672), NpcIdentifiers.GREEN_DRAGON, NpcIdentifiers.GREEN_DRAGON_261, NpcIdentifiers.GREEN_DRAGON_262, NpcIdentifiers.GREEN_DRAGON_263, NpcIdentifiers.GREEN_DRAGON_264, NpcIdentifiers.GREEN_DRAGON_7868, NpcIdentifiers.GREEN_DRAGON_7869, NpcIdentifiers.GREEN_DRAGON_7870, NpcIdentifiers.GREEN_DRAGON_8073, NpcIdentifiers.GREEN_DRAGON_8076, NpcIdentifiers.GREEN_DRAGON_8082, NpcIdentifiers.BRUTAL_GREEN_DRAGON, NpcIdentifiers.BRUTAL_GREEN_DRAGON_8081),
    BLUE_DRAGONS(25, 1, 65, null, NpcIdentifiers.BLUE_DRAGON, NpcIdentifiers.BLUE_DRAGON_266, NpcIdentifiers.BLUE_DRAGON_267, NpcIdentifiers.BLUE_DRAGON_268, NpcIdentifiers.BLUE_DRAGON_269, NpcIdentifiers.BLUE_DRAGON_4385, NpcIdentifiers.BLUE_DRAGON_5878, NpcIdentifiers.BLUE_DRAGON_5879, NpcIdentifiers.BLUE_DRAGON_5880, NpcIdentifiers.BLUE_DRAGON_5881, NpcIdentifiers.BLUE_DRAGON_5882, NpcIdentifiers.BLUE_DRAGON_8074, NpcIdentifiers.BLUE_DRAGON_8077, NpcIdentifiers.BLUE_DRAGON_8083, NpcIdentifiers.BRUTAL_BLUE_DRAGON),
    RED_DRAGONS(26, 1, 3, null, NpcIdentifiers.RED_DRAGON, NpcIdentifiers.RED_DRAGON_248, NpcIdentifiers.RED_DRAGON_249, NpcIdentifiers.RED_DRAGON_250, NpcIdentifiers.RED_DRAGON_251, NpcIdentifiers.RED_DRAGON_8075, NpcIdentifiers.RED_DRAGON_8078, NpcIdentifiers.RED_DRAGON_8079, NpcIdentifiers.BRUTAL_RED_DRAGON, NpcIdentifiers.BRUTAL_RED_DRAGON_8087),
    BLACK_DRAGONS(27, 1, 3, new Tile(3222, 10200, 0), NpcIdentifiers.BLACK_DRAGON, NpcIdentifiers.BLACK_DRAGON_253, NpcIdentifiers.BLACK_DRAGON_254, NpcIdentifiers.BLACK_DRAGON_255, NpcIdentifiers.BLACK_DRAGON_256, NpcIdentifiers.BLACK_DRAGON_257, NpcIdentifiers.BLACK_DRAGON_258, NpcIdentifiers.BLACK_DRAGON_259, NpcIdentifiers.BLACK_DRAGON_7861, NpcIdentifiers.BLACK_DRAGON_7862, NpcIdentifiers.BLACK_DRAGON_7863, NpcIdentifiers.BLACK_DRAGON_8084, NpcIdentifiers.BLACK_DRAGON_8085, NpcIdentifiers.BRUTAL_BLACK_DRAGON, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092, NpcIdentifiers.KING_BLACK_DRAGON, CustomNpcIdentifiers.ANCIENT_KING_BLACK_DRAGON),
    LESSER_DEMONS(28, 1, 60, new Tile(3006, 3849), NpcIdentifiers.LESSER_DEMON, NpcIdentifiers.LESSER_DEMON_2006, NpcIdentifiers.LESSER_DEMON_2007, NpcIdentifiers.LESSER_DEMON_2008, NpcIdentifiers.LESSER_DEMON_3982, NpcIdentifiers.LESSER_DEMON_7247, NpcIdentifiers.LESSER_DEMON_7248, NpcIdentifiers.LESSER_DEMON_7656, NpcIdentifiers.LESSER_DEMON_7657, NpcIdentifiers.LESSER_DEMON_7664, NpcIdentifiers.LESSER_DEMON_7865, NpcIdentifiers.LESSER_DEMON_7866, NpcIdentifiers.LESSER_DEMON_7867),
    GREATER_DEMONS(29, 1, 85, new Tile(3289, 3883), NpcIdentifiers.GREATER_DEMON, NpcIdentifiers.GREATER_DEMON_2026, NpcIdentifiers.GREATER_DEMON_2027, NpcIdentifiers.GREATER_DEMON_2028, NpcIdentifiers.GREATER_DEMON_2029, NpcIdentifiers.GREATER_DEMON_2030, NpcIdentifiers.GREATER_DEMON_2031, NpcIdentifiers.GREATER_DEMON_2032, NpcIdentifiers.GREATER_DEMON_7244, NpcIdentifiers.GREATER_DEMON_7245, NpcIdentifiers.GREATER_DEMON_7246, NpcIdentifiers.GREATER_DEMON_7871, NpcIdentifiers.GREATER_DEMON_7872, NpcIdentifiers.GREATER_DEMON_7873),
    BLACK_DEMONS(30, 1, 80, new Tile(3191, 10155), NpcIdentifiers.BLACK_DEMON, NpcIdentifiers.BLACK_DEMON_1432, NpcIdentifiers.BLACK_DEMON_2048, NpcIdentifiers.BLACK_DEMON_2049, NpcIdentifiers.BLACK_DEMON_2050, NpcIdentifiers.BLACK_DEMON_2051, NpcIdentifiers.BLACK_DEMON_2052, NpcIdentifiers.BLACK_DEMON_5874, NpcIdentifiers.BLACK_DEMON_5875, NpcIdentifiers.BLACK_DEMON_5876, NpcIdentifiers.BLACK_DEMON_5877, NpcIdentifiers.BLACK_DEMON_6357, NpcIdentifiers.BLACK_DEMON_7242, NpcIdentifiers.BLACK_DEMON_7243, NpcIdentifiers.BLACK_DEMON_7874, NpcIdentifiers.BLACK_DEMON_7875, NpcIdentifiers.BLACK_DEMON_7876, NpcIdentifiers.DEMONIC_GORILLA, NpcIdentifiers.DEMONIC_GORILLA_7145, NpcIdentifiers.DEMONIC_GORILLA_7146, NpcIdentifiers.DEMONIC_GORILLA_7147, NpcIdentifiers.DEMONIC_GORILLA_7148, NpcIdentifiers.DEMONIC_GORILLA_7149, NpcIdentifiers.DEMONIC_GORILLA_7152),
    HELLHOUNDS(31, 1, 75, new Tile(3200, 10071), NpcIdentifiers.HELLHOUND, NpcIdentifiers.HELLHOUND_105, NpcIdentifiers.HELLHOUND_135, NpcIdentifiers.HELLHOUND_3133, NpcIdentifiers.HELLHOUND_7256, NpcIdentifiers.HELLHOUND_7877, NpcIdentifiers.SKELETON_HELLHOUND, NpcIdentifiers.SKELETON_HELLHOUND_6387, NpcIdentifiers.SKELETON_HELLHOUND_6613),
    SHADOW_WARRIORS(32, 1, 60, null, NpcIdentifiers.SHADOW_WARRIOR),
    WEREWOLVES(33, 1, 60, null, NpcIdentifiers.WEREWOLF, NpcIdentifiers.WEREWOLF_2594, NpcIdentifiers.WEREWOLF_2595, NpcIdentifiers.WEREWOLF_2596, NpcIdentifiers.WEREWOLF_2597, NpcIdentifiers.WEREWOLF_2598, NpcIdentifiers.WEREWOLF_2599, NpcIdentifiers.WEREWOLF_2600, NpcIdentifiers.WEREWOLF_2601, NpcIdentifiers.WEREWOLF_2602, NpcIdentifiers.WEREWOLF_2603, NpcIdentifiers.WEREWOLF_2604, NpcIdentifiers.WEREWOLF_2605, NpcIdentifiers.WEREWOLF_2606, NpcIdentifiers.WEREWOLF_2607, NpcIdentifiers.WEREWOLF_2608, NpcIdentifiers.WEREWOLF_2609, NpcIdentifiers.WEREWOLF_2610, NpcIdentifiers.WEREWOLF_2611, NpcIdentifiers.WEREWOLF_2612, NpcIdentifiers.WEREWOLF_3135, NpcIdentifiers.WEREWOLF_3136, NpcIdentifiers.WEREWOLF_5928),
    VAMPIRES(34, 1, 3, null, NpcIdentifiers.VAMPYRE_JUVENILE, NpcIdentifiers.VAMPYRE_JUVENILE_3693, NpcIdentifiers.VAMPYRE_JUVENILE_3696, NpcIdentifiers.VAMPYRE_JUVENILE_3697, NpcIdentifiers.VAMPYRE_JUVENILE_4436, NpcIdentifiers.VAMPYRE_JUVENILE_4437, NpcIdentifiers.VAMPYRE_JUVENILE_4438, NpcIdentifiers.VAMPYRE_JUVENILE_4439, NpcIdentifiers.VAMPYRE_JUVENILE_8326, NpcIdentifiers.VAMPYRE_JUVENILE_8327, NpcIdentifiers.VAMPYRE_JUVENILE, NpcIdentifiers.VAMPYRE_JUVINATE, NpcIdentifiers.VAMPYRE_JUVINATE_3691, NpcIdentifiers.VAMPYRE_JUVINATE_3694, NpcIdentifiers.VAMPYRE_JUVINATE_3695, NpcIdentifiers.VAMPYRE_JUVINATE_3698, NpcIdentifiers.VAMPYRE_JUVINATE_3699, NpcIdentifiers.VAMPYRE_JUVINATE_3700, NpcIdentifiers.VAMPYRE_JUVINATE_4427, NpcIdentifiers.VAMPYRE_JUVINATE_4428, NpcIdentifiers.VAMPYRE_JUVINATE_4429, NpcIdentifiers.VAMPYRE_JUVINATE_4430, NpcIdentifiers.VAMPYRE_JUVINATE_4432, NpcIdentifiers.VAMPYRE_JUVINATE_4433, NpcIdentifiers.VAMPYRE_JUVINATE_4434, NpcIdentifiers.VAMPYRE_JUVINATE_4442, NpcIdentifiers.VAMPYRE_JUVINATE_4443, NpcIdentifiers.VAMPYRE_JUVINATE_4481, NpcIdentifiers.VAMPYRE_JUVINATE_4482, NpcIdentifiers.VAMPYRE_JUVINATE_4486, NpcIdentifiers.VAMPYRE_JUVINATE_4487, NpcIdentifiers.VAMPYRE_JUVINATE_5634, NpcIdentifiers.VAMPYRE_JUVINATE_5635, NpcIdentifiers.VAMPYRE_JUVINATE_5636, NpcIdentifiers.VAMPYRE_JUVINATE_5637, NpcIdentifiers.VAMPYRE_JUVINATE_5638, NpcIdentifiers.VAMPYRE_JUVINATE_5639),
    DAGANNOTH(35, 1, 75, null, NpcIdentifiers.DAGANNOTH_PRIME, NpcIdentifiers.DAGANNOTH_REX, NpcIdentifiers.DAGANNOTH_SUPREME, NpcIdentifiers.DAGANNOTH_970, NpcIdentifiers.DAGANNOTH_971, NpcIdentifiers.DAGANNOTH_972, NpcIdentifiers.DAGANNOTH_973, NpcIdentifiers.DAGANNOTH_974, NpcIdentifiers.DAGANNOTH_975, NpcIdentifiers.DAGANNOTH_976, NpcIdentifiers.DAGANNOTH_977, NpcIdentifiers.DAGANNOTH_978, NpcIdentifiers.DAGANNOTH_979, NpcIdentifiers.DAGANNOTH_2259, NpcIdentifiers.DAGANNOTH_3185, NpcIdentifiers.DAGANNOTH_5942, NpcIdentifiers.DAGANNOTH_5943, NpcIdentifiers.DAGANNOTH_FLEDGELING, NpcIdentifiers.DAGANNOTH_7259, NpcIdentifiers.DAGANNOTH_7260),
    TUROTH(36, 55, 60, null, NpcIdentifiers.TUROTH, NpcIdentifiers.TUROTH_427, NpcIdentifiers.TUROTH_428, NpcIdentifiers.TUROTH_429, NpcIdentifiers.TUROTH_430, NpcIdentifiers.TUROTH_431, NpcIdentifiers.TUROTH_432),
    CAVE_CRAWLER(37, 10, 10, null, NpcIdentifiers.CAVE_CRAWLER, NpcIdentifiers.CAVE_CRAWLER_407, NpcIdentifiers.CAVE_CRAWLER_408, NpcIdentifiers.CAVE_CRAWLER_409, NpcIdentifiers.CHASM_CRAWLER),
    BANSHEES(38, 15, 15, null, NpcIdentifiers.BANSHEE, NpcIdentifiers.SCREAMING_BANSHEE, NpcIdentifiers.SCREAMING_TWISTED_BANSHEE, NpcIdentifiers.TWISTED_BANSHEE),
    CRAWLING_HANDS(39, 5, 10, null, NpcIdentifiers.CRAWLING_HAND_448, NpcIdentifiers.CRAWLING_HAND_449, NpcIdentifiers.CRAWLING_HAND_450, NpcIdentifiers.CRAWLING_HAND_451, NpcIdentifiers.CRAWLING_HAND_452, NpcIdentifiers.CRAWLING_HAND_453, NpcIdentifiers.CRAWLING_HAND_454, NpcIdentifiers.CRAWLING_HAND_455, NpcIdentifiers.CRAWLING_HAND_456, NpcIdentifiers.CRAWLING_HAND_457, NpcIdentifiers.CRUSHING_HAND),
    INFERNAL_MAGES(40, 45, 40, null, NpcIdentifiers.INFERNAL_MAGE, NpcIdentifiers.INFERNAL_MAGE_444, NpcIdentifiers.INFERNAL_MAGE_445, NpcIdentifiers.INFERNAL_MAGE_446, NpcIdentifiers.INFERNAL_MAGE_447, NpcIdentifiers.MALEVOLENT_MAGE),
    ABERRANT_SPECRES(41, 60, 65, new Tile(3421, 3550, 1), NpcIdentifiers.ABERRANT_SPECTRE, NpcIdentifiers.ABERRANT_SPECTRE_3, NpcIdentifiers.ABERRANT_SPECTRE_4, NpcIdentifiers.ABERRANT_SPECTRE_5, NpcIdentifiers.ABERRANT_SPECTRE_6, NpcIdentifiers.ABERRANT_SPECTRE_7, NpcIdentifiers.ABHORRENT_SPECTRE, NpcIdentifiers.DEVIANT_SPECTRE, NpcIdentifiers.REPUGNANT_SPECTRE),
    ABYSSAL_DEMON(42, 85, 85, new Tile(3416, 3558, 2), NpcIdentifiers.ABYSSAL_DEMON_415, NpcIdentifiers.ABYSSAL_DEMON_416, NpcIdentifiers.ABYSSAL_DEMON_7241, NpcIdentifiers.GREATER_ABYSSAL_DEMON, NpcIdentifiers.ABYSSAL_SIRE, NpcIdentifiers.ABYSSAL_SIRE_5887, NpcIdentifiers.ABYSSAL_SIRE_5888, NpcIdentifiers.ABYSSAL_SIRE_5889, NpcIdentifiers.ABYSSAL_SIRE_5890, NpcIdentifiers.ABYSSAL_SIRE_5891, NpcIdentifiers.ABYSSAL_SIRE_5908),
    BASILISKS(43, 40, 40, null, NpcIdentifiers.BASILISK_417, NpcIdentifiers.BASILISK_418, NpcIdentifiers.BASILISK_9283, NpcIdentifiers.BASILISK_9284, NpcIdentifiers.BASILISK_9285, NpcIdentifiers.BASILISK_9286, NpcIdentifiers.BASILISK_KNIGHT, NpcIdentifiers.BASILISK_SENTINEL, NpcIdentifiers.BASILISK_YOUNGLING, NpcIdentifiers.MONSTROUS_BASILISK, NpcIdentifiers.MONSTROUS_BASILISK_9287, NpcIdentifiers.MONSTROUS_BASILISK_9288),
    COCKATRICE(44, 25, 25, null, NpcIdentifiers.COCKATRICE_419, NpcIdentifiers.COCKATRICE_420, NpcIdentifiers.COCKATHRICE),
    KURASK(45, 70, 65, null, NpcIdentifiers.KURASK_410, NpcIdentifiers.KURASK_411, NpcIdentifiers.KING_KURASK),
    GARGOYLE(46, 75, 80, null, NpcIdentifiers.GARGOYLE, NpcIdentifiers.GARGOYLE_1543, NpcIdentifiers.MARBLE_GARGOYLE_7408),
    PYREFIEND(47, 30, 25, null, NpcIdentifiers.PYREFIEND, NpcIdentifiers.PYREFIEND_434, NpcIdentifiers.PYREFIEND_435, NpcIdentifiers.PYREFIEND_436, NpcIdentifiers.PYREFIEND_3139, NpcIdentifiers.FLAMING_PYRELORD),
    BLOODVELDS(48, 50, 50, new Tile(3428, 3556, 1), NpcIdentifiers.BLOODVELD, NpcIdentifiers.BLOODVELD_485, NpcIdentifiers.BLOODVELD_486, NpcIdentifiers.BLOODVELD_487, NpcIdentifiers.BLOODVELD_3138, NpcIdentifiers.INSATIABLE_BLOODVELD, NpcIdentifiers.INSATIABLE_MUTATED_BLOODVELD),
    DUST_DEVILS(49, 65, 70, null, NpcIdentifiers.DUST_DEVIL, NpcIdentifiers.DUST_DEVIL_7249, NpcIdentifiers.CHOKE_DEVIL),
    JELLIES(50, 52, 57, null, NpcIdentifiers.JELLY, NpcIdentifiers.JELLY_438, NpcIdentifiers.JELLY_439, NpcIdentifiers.JELLY_440, NpcIdentifiers.JELLY_441, NpcIdentifiers.JELLY_442, NpcIdentifiers.WARPED_JELLY, NpcIdentifiers.VITREOUS_JELLY, NpcIdentifiers.VITREOUS_WARPED_JELLY),
    ROCKSLUG(51, 20, 20, null, NpcIdentifiers.ROCKSLUG, NpcIdentifiers.ROCKSLUG_422, NpcIdentifiers.GIANT_ROCKSLUG),
    NECHRYAEL(52, 80, 85, new Tile(3445, 3557, 2), NpcIdentifiers.NECHRYAEL, NpcIdentifiers.NECHRYAEL_11, NpcIdentifiers.NECHRYARCH, NpcIdentifiers.GREATER_NECHRYAEL),
    KALPHITES(53, 1, 15, null, NpcIdentifiers.KALPHITE_QUEEN_6500, NpcIdentifiers.KALPHITE_QUEEN_6501, NpcIdentifiers.KALPHITE_WORKER, NpcIdentifiers.KALPHITE_WORKER_956, NpcIdentifiers.KALPHITE_SOLDIER_957, NpcIdentifiers.KALPHITE_SOLDIER_958, NpcIdentifiers.KALPHITE_GUARDIAN, NpcIdentifiers.KALPHITE_GUARDIAN_960, NpcIdentifiers.KALPHITE_WORKER_961, NpcIdentifiers.KALPHITE_GUARDIAN_962),
    EARTH_WARRIORS(54, 1, 35, null, NpcIdentifiers.EARTH_WARRIOR),
    OTHERWORLDLY_BEINGS(55, 1, 40, null, NpcIdentifiers.OTHERWORLDLY_BEING),
    ELVES(56, 1, 70, null, NpcIdentifiers.ELF_ARCHER, NpcIdentifiers.ELF_ARCHER_5296, NpcIdentifiers.ELF_WARRIOR, NpcIdentifiers.ELF_WARRIOR_5294),
    DWARVES(57, 1, 6, null, NpcIdentifiers.DWARF, NpcIdentifiers.DWARF_292, NpcIdentifiers.DWARF_294, NpcIdentifiers.DWARF_295, NpcIdentifiers.DWARF_296, NpcIdentifiers.DWARF_1401, NpcIdentifiers.DWARF_1402, NpcIdentifiers.DWARF_1403, NpcIdentifiers.DWARF_1404, NpcIdentifiers.DWARF_1405, NpcIdentifiers.DWARF_1406, NpcIdentifiers.DWARF_1407, NpcIdentifiers.DWARF_1408),
    BRONZE_DRAGONS(58, 1, 75, null, NpcIdentifiers.BRONZE_DRAGON, NpcIdentifiers.BRONZE_DRAGON_271, NpcIdentifiers.BRONZE_DRAGON_7253),
    IRON_DRAGONS(59, 1, 80, null, NpcIdentifiers.IRON_DRAGON, NpcIdentifiers.IRON_DRAGON_273, NpcIdentifiers.IRON_DRAGON_7254, NpcIdentifiers.IRON_DRAGON_8080),
    STEEL_DRAGONS(60, 1, 80, null, NpcIdentifiers.STEEL_DRAGON_274, NpcIdentifiers.STEEL_DRAGON_275, NpcIdentifiers.STEEL_DRAGON_7255, NpcIdentifiers.STEEL_DRAGON_8086),
    WALL_BEASTS(61, 35, 30, null, NpcIdentifiers.WALL_BEAST),
    CAVE_SLIMES(62, 17, 15, null, NpcIdentifiers.CAVE_SLIME),
    CAVE_BUGS(63, 7, 7, null, NpcIdentifiers.CAVE_BUG, NpcIdentifiers.CAVE_BUG_483),
    SHADES(64, 1, 30, null, NpcIdentifiers.SHADE),
    CROCODILES(65, 1, 50, null, NpcIdentifiers.CROCODILE),
    DARK_BEASTS(66, 90, 90, new Tile(2023, 4635, 0), NpcIdentifiers.DARK_BEAST, NpcIdentifiers.DARK_BEAST_7250, NpcIdentifiers.NIGHT_BEAST),
    MOGRES(67, 1, 30, null, NpcIdentifiers.MOGRE),
    DESERT_LIZARDS(68, 22, 3, null, NpcIdentifiers.DESERT_LIZARD, NpcIdentifiers.DESERT_LIZARD_460, NpcIdentifiers.DESERT_LIZARD_461),
    FEVER_SPIDERS(69, 42, 40, null, NpcIdentifiers.FEVER_SPIDER),
    HARPIE_BUG_SWARM(70, 33, 45, null, NpcIdentifiers.HARPIE_BUG_SWARM),
    SEA_SNAKES(71, 1, 40, null, NpcIdentifiers.GIANT_SEA_SNAKE, NpcIdentifiers.SEA_SNAKE_YOUNG, NpcIdentifiers.SEA_SNAKE_HATCHLING),
    WYVERN(72, 72, 70, null, NpcIdentifiers.SKELETAL_WYVERN, NpcIdentifiers.SKELETAL_WYVERN_466, NpcIdentifiers.SKELETAL_WYVERN_467, NpcIdentifiers.SKELETAL_WYVERN_468, NpcIdentifiers.TALONED_WYVERN, NpcIdentifiers.SPITTING_WYVERN, NpcIdentifiers.LONGTAILED_WYVERN, NpcIdentifiers.ANCIENT_WYVERN),
    KILLERWATTS(73, 37, 50, null, NpcIdentifiers.KILLERWATT, NpcIdentifiers.KILLERWATT_470),
    MUTATED_ZYGOMITES(74, 57, 60, null, NpcIdentifiers.ZYGOMITE),
    ICEFIENDS(75, 1, 20, null, NpcIdentifiers.ICEFIEND, NpcIdentifiers.ICEFIEND_4813, NpcIdentifiers.ICEFIEND_7586),
    MINOTAURS(76, 1, 7, null, NpcIdentifiers.MINOTAUR, NpcIdentifiers.MINOTAUR_2482, NpcIdentifiers.MINOTAUR_2483),
    FLESH_CRAWLER(77, 1, 15, null, NpcIdentifiers.FLESH_CRAWLER, NpcIdentifiers.FLESH_CRAWLER_2499, NpcIdentifiers.FLESH_CRAWLER_2500),
    CATABLEPON(78, 1, 35, null, NpcIdentifiers.CATABLEPON, NpcIdentifiers.CATABLEPON_2475, NpcIdentifiers.CATABLEPON_2476),
    ANKOUS(79, 1, 40, new Tile(2983, 3755, 0), NpcIdentifiers.ANKOU, NpcIdentifiers.ANKOU_2515, NpcIdentifiers.ANKOU_2516, NpcIdentifiers.ANKOU_2517, NpcIdentifiers.ANKOU_2518, NpcIdentifiers.ANKOU_2519, NpcIdentifiers.ANKOU_6608, NpcIdentifiers.ANKOU_7257, NpcIdentifiers.ANKOU_7864, NpcIdentifiers.DARK_ANKOU),
    CAVE_HORRORS(80, 58, 85, null, NpcIdentifiers.CAVE_HORROR, NpcIdentifiers.CAVE_HORROR_1048, NpcIdentifiers.CAVE_HORROR_1049, NpcIdentifiers.CAVE_HORROR_1050, NpcIdentifiers.CAVE_HORROR_1051, NpcIdentifiers.CAVE_ABOMINATION),
    JUNGLE_HORRORS(81, 1, 65, null, NpcIdentifiers.JUNGLE_HORROR, NpcIdentifiers.JUNGLE_HORROR_1043, NpcIdentifiers.JUNGLE_HORROR_1044, NpcIdentifiers.JUNGLE_HORROR_1045, NpcIdentifiers.JUNGLE_HORROR_1046),
    GORAK(82, 1, 3, null, NpcIdentifiers.GORAK, NpcIdentifiers.GORAK_3141),
    SUQAHS(83, 1, 3, null, NpcIdentifiers.SUQAH, NpcIdentifiers.SUQAH_788, NpcIdentifiers.SUQAH_789, NpcIdentifiers.SUQAH_790, NpcIdentifiers.SUQAH_791, NpcIdentifiers.SUQAH_792, NpcIdentifiers.SUQAH_793),
    BRINE_RAT(84, 47, 47, null, NpcIdentifiers.BRINE_RAT),
    MINIONS_OF_SCABARAS(85, 1, 85, null, NpcIdentifiers.SCARABS, NpcIdentifiers.SCARAB_SWARM_4192, NpcIdentifiers.LOCUST_RIDER, NpcIdentifiers.LOCUST_RIDER_796, NpcIdentifiers.LOCUST_RIDER_800, NpcIdentifiers.LOCUST_RIDER_801, NpcIdentifiers.SCARAB_MAGE, NpcIdentifiers.SCARAB_MAGE_799, NpcIdentifiers.GIANT_SCARAB, NpcIdentifiers.GIANT_SCARAB_798, NpcIdentifiers.GIANT_SCARAB_6343),
    TERROR_DOGS(86, 40, 60, null, NpcIdentifiers.TERROR_DOG, NpcIdentifiers.TERROR_DOG_6474),
    MOLANISK(87, 39, 50, null, NpcIdentifiers.MOLANISK),
    WATERFIENDS(88, 1, 75, null, NpcIdentifiers.WATERFIEND, NpcIdentifiers.WATERFIEND_2917),
    SPIRITUAL_CREATURES(89, 63, 60, null, NpcIdentifiers.SPIRITUAL_MAGE, NpcIdentifiers.SPIRITUAL_MAGE_2244, NpcIdentifiers.SPIRITUAL_MAGE_3161, NpcIdentifiers.SPIRITUAL_MAGE_3168, NpcIdentifiers.SPIRITUAL_WARRIOR, NpcIdentifiers.SPIRITUAL_WARRIOR_2243, NpcIdentifiers.SPIRITUAL_WARRIOR_3159, NpcIdentifiers.SPIRITUAL_WARRIOR_3166, NpcIdentifiers.SPIRITUAL_RANGER, NpcIdentifiers.SPIRITUAL_RANGER_2242, NpcIdentifiers.SPIRITUAL_RANGER_3160, NpcIdentifiers.SPIRITUAL_RANGER_3167),
    LIZARDMAN_SHAMAN(90, 1, 3, true, new Tile(1453, 3694, 0), NpcIdentifiers.LIZARDMAN, NpcIdentifiers.LIZARDMAN_6915, NpcIdentifiers.LIZARDMAN_6916, NpcIdentifiers.LIZARDMAN_6917, NpcIdentifiers.LIZARDMAN_BRUTE, NpcIdentifiers.LIZARDMAN_BRUTE_6919, NpcIdentifiers.LIZARDMAN_BRUTE_8564, NpcIdentifiers.LIZARDMAN_SHAMAN, NpcIdentifiers.LIZARDMAN_SHAMAN_6767, NpcIdentifiers.LIZARDMAN_SHAMAN_7573, NpcIdentifiers.LIZARDMAN_SHAMAN_7574, NpcIdentifiers.LIZARDMAN_SHAMAN_7744, NpcIdentifiers.LIZARDMAN_SHAMAN_7745, NpcIdentifiers.LIZARDMAN_SHAMAN_8565),
    MAGIC_AXES(91, 1, 3, new Tile(3191, 3959), NpcIdentifiers.MAGIC_AXE, NpcIdentifiers.MAGIC_AXE_7269),
    CAVE_KRAKENS(92, 87, 80, null, NpcIdentifiers.CAVE_KRAKEN, NpcIdentifiers.KRAKEN, NpcIdentifiers.KRAKEN_6640, NpcIdentifiers.KRAKEN_6656),
    MITHRIL_DRAGONS(93, 1, 80, null, NpcIdentifiers.MITHRIL_DRAGON, NpcIdentifiers.MITHRIL_DRAGON_8088, NpcIdentifiers.MITHRIL_DRAGON_8089),
    AVIANSIES(94, 1, 3, null, NpcIdentifiers.AVIANSIE, NpcIdentifiers.AVIANSIE_3170, NpcIdentifiers.AVIANSIE_3171, NpcIdentifiers.AVIANSIE_3172, NpcIdentifiers.AVIANSIE_3173, NpcIdentifiers.AVIANSIE_3174, NpcIdentifiers.AVIANSIE_3175, NpcIdentifiers.AVIANSIE_3176, NpcIdentifiers.AVIANSIE_3177, NpcIdentifiers.AVIANSIE_3178, NpcIdentifiers.AVIANSIE_3179, NpcIdentifiers.AVIANSIE_3180, NpcIdentifiers.AVIANSIE_3181, NpcIdentifiers.AVIANSIE_3182, NpcIdentifiers.AVIANSIE_3183, NpcIdentifiers.KREEARRA, NpcIdentifiers.WINGMAN_SKREE, NpcIdentifiers.FLOCKLEADER_GEERIN, NpcIdentifiers.FLIGHT_KILISA),
    SMOKE_DEVILS(95, 93, 85, new Tile(2404, 9417, 0), NpcIdentifiers.SMOKE_DEVIL, NpcIdentifiers.SMOKE_DEVIL_6639, NpcIdentifiers.SMOKE_DEVIL_6655, NpcIdentifiers.SMOKE_DEVIL_8482, NpcIdentifiers.SMOKE_DEVIL_8483, NpcIdentifiers.NUCLEAR_SMOKE_DEVIL),
    TZHAAR(96, 1, 85, null, NpcIdentifiers.TZHAARKET_2174, NpcIdentifiers.TZHAARKET_2174, NpcIdentifiers.TZHAARKET_2176, NpcIdentifiers.TZHAARKET_2177, NpcIdentifiers.TZHAARKET_2178, NpcIdentifiers.TZHAARKET_2179, NpcIdentifiers.TZHAARXIL, NpcIdentifiers.TZHAARXIL_2168, NpcIdentifiers.TZHAARXIL_2169, NpcIdentifiers.TZHAARXIL_2170, NpcIdentifiers.TZHAARXIL_2171, NpcIdentifiers.TZHAARXIL_2172, NpcIdentifiers.TZHAARHUR, NpcIdentifiers.TZHAARHUR_2162, NpcIdentifiers.TZHAARHUR_2163, NpcIdentifiers.TZHAARHUR_2164, NpcIdentifiers.TZHAARHUR_2165, NpcIdentifiers.TZHAARHUR_2166, NpcIdentifiers.TZKIH_3116, NpcIdentifiers.TZKIH_3117, NpcIdentifiers.TZKEK_3118, NpcIdentifiers.TZKEK_3119, NpcIdentifiers.TZKEK_3120, NpcIdentifiers.TOKXIL_3121, NpcIdentifiers.TOKXIL_3122, NpcIdentifiers.YTMEJKOT, NpcIdentifiers.YTMEJKOT_3124, NpcIdentifiers.KETZEK, NpcIdentifiers.KETZEK_3126, NpcIdentifiers.TZTOKJAD, NpcIdentifiers.YTHURKOT),
    TZTOK_JAD(97, 1, 100, true, new Tile(2440, 5172), NpcIdentifiers.TZTOKJAD),
    CORPOREAL_BEAST(98, 1, 100, true, new Tile(2969, 4382, 2), NpcIdentifiers.CORPOREAL_BEAST),
    DEMONIC_GORILLA(99, 1, 100, true, new Tile(3109, 3676), NpcIdentifiers.DEMONIC_GORILLA, NpcIdentifiers.DEMONIC_GORILLA_7145, NpcIdentifiers.DEMONIC_GORILLA_7146, NpcIdentifiers.DEMONIC_GORILLA_7147, NpcIdentifiers.DEMONIC_GORILLA_7148, NpcIdentifiers.DEMONIC_GORILLA_7149, NpcIdentifiers.DEMONIC_GORILLA_7152),
    ELVARG(100, 75, 3, true, new Tile(3338, 3902), NpcIdentifiers.ELVARG_HARD),
    LAVA_BEAST(102, 1, 3, new Tile(3092, 3883), NpcIdentifiers.LAVA_BEAST),
    ALCHEMICAL_HYDRA(103, 95, 85, true, new Tile(1354, 10258), NpcIdentifiers.ALCHEMICAL_HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA_8616, NpcIdentifiers.ALCHEMICAL_HYDRA_8617, NpcIdentifiers.ALCHEMICAL_HYDRA_8618, NpcIdentifiers.ALCHEMICAL_HYDRA_8619, NpcIdentifiers.ALCHEMICAL_HYDRA_8620, NpcIdentifiers.ALCHEMICAL_HYDRA_8621, NpcIdentifiers.ALCHEMICAL_HYDRA_8622, NpcIdentifiers.ALCHEMICAL_HYDRA_8634),
    LAVA_DRAGONS(104, 1, 85, new Tile(3198, 3851), NpcIdentifiers.LAVA_DRAGON),
    CHAOS_DRUID(105, 1, 3, null, NpcIdentifiers.CHAOS_DRUID, NpcIdentifiers.ELDER_CHAOS_DRUID, NpcIdentifiers.ELDER_CHAOS_DRUID_7995),
    FOSSIL_ISLAND_WYVERNS(106, 66, 60, null, NpcIdentifiers.TALONED_WYVERN, NpcIdentifiers.SPITTING_WYVERN, NpcIdentifiers.LONGTAILED_WYVERN, NpcIdentifiers.ANCIENT_WYVERN),
    REVENANTS(107, 1, 50, new Tile(3244, 10145), NpcIdentifiers.REVENANT_IMP, NpcIdentifiers.REVENANT_GOBLIN, NpcIdentifiers.REVENANT_PYREFIEND, NpcIdentifiers.REVENANT_HOBGOBLIN, NpcIdentifiers.REVENANT_CYCLOPS, NpcIdentifiers.REVENANT_HELLHOUND, NpcIdentifiers.REVENANT_CYCLOPS, NpcIdentifiers.REVENANT_DEMON, NpcIdentifiers.REVENANT_ORK, NpcIdentifiers.REVENANT_DARK_BEAST, NpcIdentifiers.REVENANT_DRAGON, NpcIdentifiers.REVENANT_KNIGHT, CustomNpcIdentifiers.ANCIENT_REVENANT_DARK_BEAST, CustomNpcIdentifiers.ANCIENT_REVENANT_ORK, CustomNpcIdentifiers.ANCIENT_REVENANT_CYCLOPS, CustomNpcIdentifiers.ANCIENT_REVENANT_DRAGON, CustomNpcIdentifiers.ANCIENT_REVENANT_KNIGHT),
    ADAMANT_DRAGONS(108, 1, 75, null, NpcIdentifiers.ADAMANT_DRAGON, NpcIdentifiers.ADAMANT_DRAGON_8090),
    RUNE_DRAGONS(109, 1, 75, null, NpcIdentifiers.RUNE_DRAGON, NpcIdentifiers.RUNE_DRAGON_8031, NpcIdentifiers.RUNE_DRAGON_8091),
    BANDITS(110, 1, 3, new Tile(3032, 3681), NpcIdentifiers.BANDIT, NpcIdentifiers.BANDIT_691, NpcIdentifiers.BANDIT_692, NpcIdentifiers.BANDIT_693, NpcIdentifiers.BANDIT_694, NpcIdentifiers.BANDIT_695, NpcIdentifiers.BANDIT_734, NpcIdentifiers.BANDIT_735, NpcIdentifiers.BANDIT_736, NpcIdentifiers.BANDIT_737, NpcIdentifiers.BANDIT_1026, NpcIdentifiers.GUARD_BANDIT, NpcIdentifiers.BANDIT_6605),
    BLACK_KNIGHT(111, 1, 3, null, NpcIdentifiers.BLACK_KNIGHT, NpcIdentifiers.BLACK_KNIGHT_1545, NpcIdentifiers.BLACK_KNIGHT_4331, NpcIdentifiers.BLACK_KNIGHT_4959, NpcIdentifiers.BLACK_KNIGHT_4960),
    DARK_WARRIOR(112, 1, 3, null, NpcIdentifiers.DARK_WARRIOR, NpcIdentifiers.DARK_WARRIOR_6606),
    MAMMOTH(113, 1, 3, null, NpcIdentifiers.MAMMOTH),
    PIRATE(114, 1, 3, new Tile(3046, 3955), NpcIdentifiers.PIRATE, NpcIdentifiers.PIRATE, NpcIdentifiers.PIRATE_522, NpcIdentifiers.PIRATE_523, NpcIdentifiers.PIRATE_524, NpcIdentifiers.PIRATE_1447, NpcIdentifiers.PIRATE_4043, NpcIdentifiers.PIRATE_4044, NpcIdentifiers.PIRATE_4045, NpcIdentifiers.PIRATE_4046, NpcIdentifiers.PIRATE_4047, NpcIdentifiers.PIRATE_4048, NpcIdentifiers.PIRATE_4049, NpcIdentifiers.PIRATE_4050, NpcIdentifiers.PIRATE_4051, NpcIdentifiers.PIRATE_4052, NpcIdentifiers.PIRATE_6993, NpcIdentifiers.PIRATE_6994, NpcIdentifiers.PIRATE_6995, NpcIdentifiers.PIRATE_7282, NpcIdentifiers.PIRATE_7917, NpcIdentifiers.PIRATE_7918),
    ENTS(115, 1, 3, null, NpcIdentifiers.ENT, NpcIdentifiers.ENT_7234),
    ROGUES(116, 1, 3, new Tile(3285, 3922), NpcIdentifiers.ROGUE, NpcIdentifiers.ROGUE_6603),
    WYRM(117, 62, 3, null, NpcIdentifiers.WYRM, NpcIdentifiers.WYRM_8611),
    DRAKES(118, 84, 3, null, NpcIdentifiers.DRAKE_8612, NpcIdentifiers.DRAKE_8613),
    BARRELCHEST(119, 1, 100, true, new Tile(3287, 3884), NpcIdentifiers.BARRELCHEST_6342, CustomNpcIdentifiers.ANCIENT_BARRELCHEST),
    HYDRA(120, 95, 85, null, NpcIdentifiers.HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA_8616, NpcIdentifiers.ALCHEMICAL_HYDRA_8617, NpcIdentifiers.ALCHEMICAL_HYDRA_8618, NpcIdentifiers.ALCHEMICAL_HYDRA_8619, NpcIdentifiers.ALCHEMICAL_HYDRA_8620, NpcIdentifiers.ALCHEMICAL_HYDRA_8621, NpcIdentifiers.ALCHEMICAL_HYDRA_8622, NpcIdentifiers.ALCHEMICAL_HYDRA_8634),
    ABYSSAL_SIRE(121, 85, 100, true, null, NpcIdentifiers.ABYSSAL_SIRE, NpcIdentifiers.ABYSSAL_SIRE_5887, NpcIdentifiers.ABYSSAL_SIRE_5888, NpcIdentifiers.ABYSSAL_SIRE_5889, NpcIdentifiers.ABYSSAL_SIRE_5890, NpcIdentifiers.ABYSSAL_SIRE_5891, NpcIdentifiers.ABYSSAL_SIRE_5908),
    BARROWS(122, 1, 85, true, new Tile(3565, 3306), NpcIdentifiers.AHRIM_THE_BLIGHTED, NpcIdentifiers.DHAROK_THE_WRETCHED, NpcIdentifiers.GUTHAN_THE_INFESTED, NpcIdentifiers.KARIL_THE_TAINTED, NpcIdentifiers.TORAG_THE_CORRUPTED, NpcIdentifiers.VERAC_THE_DEFILED),
    CALLISTO(123, 1, 100, true, new Tile(3307, 3837), NpcIdentifiers.CALLISTO, NpcIdentifiers.CALLISTO_6609),
    CERBERUS(124, 91, 100, true, new Tile(1241, 1248), NpcIdentifiers.CERBERUS, NpcIdentifiers.CERBERUS_5863, NpcIdentifiers.CERBERUS_5866),
    CHAOS_ELEMENTAL(125, 1, 100, true, new Tile(3269, 3927), NpcIdentifiers.CHAOS_ELEMENTAL, NpcIdentifiers.CHAOS_ELEMENTAL_6505, CustomNpcIdentifiers.ANCIENT_CHAOS_ELEMENTAL),
    CHAOS_FANATIC(126, 1, 85, true, new Tile(2992, 3851), NpcIdentifiers.CHAOS_FANATIC),
    COMMANDER_ZILYANA(127, 1, 100, true, new Tile(2911, 5267, 0), NpcIdentifiers.COMMANDER_ZILYANA),
    CRAZY_ARCHAEOLOGIST(128, 1, 85, true, new Tile(2976, 3694), NpcIdentifiers.CRAZY_ARCHAEOLOGIST),
    DAGANNOTH_KINGS(129, 1, 85, true, null, NpcIdentifiers.DAGANNOTH_PRIME, NpcIdentifiers.DAGANNOTH_REX, NpcIdentifiers.DAGANNOTH_SUPREME),
    GENERAL_GRAARDOR(130, 1, 100, true, new Tile(2860, 5354, 2), NpcIdentifiers.GENERAL_GRAARDOR),
    GIANT_MOLE(131, 1, 85, true, new Tile(1752, 5234), NpcIdentifiers.GIANT_MOLE, NpcIdentifiers.GIANT_MOLE_6499),
    GROTESQUE_GUARDIANS(132, 75, 100, true, null, NpcIdentifiers.DUSK, NpcIdentifiers.DAWN),
    KRIL_TSUTSAROTH(133, 1, 100, true, new Tile(2925, 5336, 2), NpcIdentifiers.KRIL_TSUTSAROTH),
    KALPHITE_QUEEN(134, 1, 100, true, new Tile(3507, 9494, 2), NpcIdentifiers.KALPHITE_QUEEN_6500, NpcIdentifiers.KALPHITE_QUEEN_6501),
    KING_BLACK_DRAGON(135, 1, 85, true, new Tile(3016, 3849), NpcIdentifiers.KING_BLACK_DRAGON, NpcIdentifiers.KING_BLACK_DRAGON_6502, CustomNpcIdentifiers.ANCIENT_KING_BLACK_DRAGON),
    KRAKEN(136, 87, 85, true, new Tile(3344, 3822), NpcIdentifiers.KRAKEN),
    KREE_ARRA(137, 1, 100, true, new Tile(2841, 5291, 2), NpcIdentifiers.KREEARRA),
    SARACHNIS(138, 1, 3, null, NpcIdentifiers.SARACHNIS),
    SCORPIA(139, 1, 3, true, new Tile(3232, 3950), NpcIdentifiers.SCORPIA),
    THERMONUCLEAR_SMOKE_DEVIL(140, 93, 100, true, new Tile(2379, 9452), NpcIdentifiers.THERMONUCLEAR_SMOKE_DEVIL),
    VENENATIS(141, 1, 100, true, new Tile(3319, 3745), NpcIdentifiers.VENENATIS, NpcIdentifiers.VENENATIS_6610),
    VETION(142, 1, 100, true, new Tile(3239, 3783), NpcIdentifiers.VETION, NpcIdentifiers.VETION_REBORN),
    VORKATH(143, 1, 112, true, new Tile(2273, 4049), NpcIdentifiers.VORKATH_8061),
    ZULRAH(144, 1, 100, true, new Tile(2201, 3057, 0), NpcIdentifiers.ZULRAH, NpcIdentifiers.ZULRAH_2043, NpcIdentifiers.ZULRAH_2044),
    WORLD_BOSS(145, 1, 85, true, null, NpcIdentifiers.SKOTIZO, NpcIdentifiers.ZOMBIES_CHAMPION, NpcIdentifiers.TEKTON_7542),
    ;

    public int uid, req, cbreq;
    public boolean bossTask;
    public Tile teleportLocation;
    public int[] ids;

    SlayerCreature(int uid, int req, int cbreq, Tile teleportLocation, int... ids) {
        this.uid = uid;
        this.req = req;
        this.cbreq = cbreq;
        this.bossTask = false;
        this.teleportLocation = teleportLocation;
        this.ids = ids;
    }

    SlayerCreature(int uid, int req, int cbreq, boolean bossTask, Tile teleportLocation, int... ids) {
        this.uid = uid;
        this.req = req;
        this.cbreq = cbreq;
        this.bossTask = bossTask;
        this.teleportLocation = teleportLocation;
        this.ids = ids;
    }

    public boolean matches(int id) {
        return Arrays.stream(ids).anyMatch(npc_id -> npc_id == id);
    }

    private static Map<Integer, SlayerCreature> lookup = new HashMap<>();

    static {
        for (SlayerCreature slayerCreature : SlayerCreature.values()) {
            lookup.put(slayerCreature.uid, slayerCreature);
        }
    }

    public static SlayerCreature lookup(int uid) {
        if (lookup == null) {
            // Build lookup map now.
            Map<Integer, SlayerCreature> ltemp = new HashMap<>();

            for (SlayerCreature slayerCreature : values()) {
                ltemp.put(slayerCreature.uid, slayerCreature);
            }

            lookup = ltemp;
        }
        return lookup.get(uid);
    }

    public static int slayerReq(int id) {
        for (SlayerCreature value : values()) {
            for (int i : value.ids) {
                if (i == id) {
                    return value.req;
                }
            }
        }
        return 1;
    }

    public static void teleport(Player player) {
        SlayerCreature task = SlayerCreature.lookup(player.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0));
        int num = player.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0);

        if (task != null) {
            if (task.teleportLocation == null) {
                player.message("This task has no teleport to location set yet.");
                return;
            }
            if (num > 0) {
                //Check if we can teleport
                if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                    //Method already sents block message, no need for a block message here
                    return;
                }

                //We passed the teleport requirements
                Tile tile = task.teleportLocation;//teleport tile
                if (task == WORLD_BOSS) {//World boss uses custom tile code
                    if (WildernessBossEvent.getINSTANCE().getActiveNpc().isPresent() && WildernessBossEvent.currentSpawnPos != null) {
                        tile = WildernessBossEvent.currentSpawnPos;
                    } else {
                        player.message("The world boss recently died and will respawn shortly.");
                    }
                }

                var isWilderness = WildernessArea.inWilderness(tile);//Check if the tile is in the wilderness
                if(isWilderness) {
                    Tile tileToTeleportTo = tile;
                    var wildernessLevel = WildernessArea.wildernessLevel(tileToTeleportTo);
                    player.optionsTitled(""+ Color.RED.wrap("Wilderness ("+wildernessLevel+")!")+" would you like to proceed?", "Yes.","No.", () -> {
                        Teleports.basicTeleport(player, tileToTeleportTo);
                        player.message("You have teleported to your slayer task.");
                    });
                    return;//Stop any code from processing
                }

                Teleports.basicTeleport(player, tile);
                player.message("You have teleported to your slayer task.");
            } else {
                player.message("You need something new to hunt.");
            }
        } else {
            player.message("You need something new to hunt.");
        }
    }

    @Override
    public String toString() {
        return "SlayerCreature{" +
            "uid=" + uid +
            ", req=" + req +
            ", cbreq=" + cbreq +
            ", ids=" + Arrays.toString(ids) +
            '}';
    }
}

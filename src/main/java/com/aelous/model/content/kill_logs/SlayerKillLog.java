package com.aelous.model.content.kill_logs;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Utils;

import java.util.Arrays;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 09, 2020 11:14
 */
public class SlayerKillLog {

    /**
     * The player this is relative to
     */
    private Player player;

    /**
     * Creates a new {@link SlayerKillLog} object for a singular player
     *
     * @param player the player
     */
    public SlayerKillLog(Player player) {
        this.player = player;
    }

    private static int STARTING_NAME_LINE = 46321;

    public enum SlayerMonsters {
        CRAWLING_HAND("Crawling hands", AttributeKey.KC_CRAWL_HAND, 0 , STARTING_NAME_LINE,NpcIdentifiers.CRAWLING_HAND_448, NpcIdentifiers.CRAWLING_HAND_449, NpcIdentifiers.CRAWLING_HAND_450, NpcIdentifiers.CRAWLING_HAND_451, NpcIdentifiers.CRAWLING_HAND_452, NpcIdentifiers.CRAWLING_HAND_453, NpcIdentifiers.CRAWLING_HAND_454, NpcIdentifiers.CRAWLING_HAND_455, NpcIdentifiers.CRAWLING_HAND_456, NpcIdentifiers.CRAWLING_HAND_457),
        CAVE_BUG("Cave bugs", AttributeKey.KC_CAVE_BUG, 0, STARTING_NAME_LINE + (6 * 1), NpcIdentifiers.CAVE_BUG, NpcIdentifiers.CAVE_BUG_483),
        CAVE_CRAWLER("Cave crawlers", AttributeKey.KC_CAVE_CRAWLER, 0, STARTING_NAME_LINE + (6 * 2), NpcIdentifiers.CAVE_CRAWLER, NpcIdentifiers.CAVE_CRAWLER_407, NpcIdentifiers.CAVE_CRAWLER_408, NpcIdentifiers.CAVE_CRAWLER_409),
        BANSHEE("Banshees", AttributeKey.KC_BANSHEE, 0, STARTING_NAME_LINE + (6 * 3), NpcIdentifiers.BANSHEE, NpcIdentifiers.SCREAMING_BANSHEE),
        CAVE_SLIME("Cave slimes", AttributeKey.KC_CAVE_SLIME, 0, STARTING_NAME_LINE + (6 * 4), NpcIdentifiers.CAVE_SLIME),
        ROCKSLUG("Rockslugs", AttributeKey.KC_ROCKSLUG, 0, STARTING_NAME_LINE + (6 * 5), NpcIdentifiers.ROCKSLUG, NpcIdentifiers.ROCKSLUG_422),
        DESERT_LIZARD("Desert lizards", AttributeKey.KC_DESERT_LIZARD, 0, STARTING_NAME_LINE + (6 * 6), NpcIdentifiers.DESERT_LIZARD, NpcIdentifiers.DESERT_LIZARD_460, NpcIdentifiers.DESERT_LIZARD_461),
        COCKATRICE("Cockatrices", AttributeKey.KC_COCKATRICE, 0, STARTING_NAME_LINE + (6 * 7), NpcIdentifiers.COCKATRICE_419, NpcIdentifiers.COCKATRICE_420),
        PYREFIEND("Pyrefiends", AttributeKey.KC_PYREFRIEND, 0, STARTING_NAME_LINE + (6 * 8), NpcIdentifiers.PYREFIEND, NpcIdentifiers.PYREFIEND_434, NpcIdentifiers.PYREFIEND_435, NpcIdentifiers.PYREFIEND_436, NpcIdentifiers.PYREFIEND_3139),
        MOGRE("Mogres", AttributeKey.KC_MOGRE, 0,STARTING_NAME_LINE + (6 * 9), NpcIdentifiers.MOGRE),
        HARPIE_BUG_SWARM("Harpie bug swarms", AttributeKey.KC_HARPIE_BUG, 0,STARTING_NAME_LINE + (6 * 10), NpcIdentifiers.HARPIE_BUG_SWARM),
        WALL_BEAST("Wall beasts", AttributeKey.KC_WALL_BEAST, 0,STARTING_NAME_LINE + (6 * 11), NpcIdentifiers.WALL_BEAST),
        KILLERWATT("Killerwatts", AttributeKey.KC_KILLERWATT, 0,STARTING_NAME_LINE + (6 * 12), NpcIdentifiers.KILLERWATT, NpcIdentifiers.KILLERWATT_470),
        MOLANISK("Molanisks", AttributeKey.KC_MOLANISK, 0,STARTING_NAME_LINE + (6 * 13), NpcIdentifiers.MOLANISK),
        BASILISK("Basilisks", AttributeKey.KC_BASILISK, 0,STARTING_NAME_LINE + (6 * 14), NpcIdentifiers.BASILISK_417, NpcIdentifiers.BASILISK_418, NpcIdentifiers.BASILISK_9283, NpcIdentifiers.BASILISK_9284, NpcIdentifiers.BASILISK_9285, NpcIdentifiers.BASILISK_9286),
        SEA_SNAKES("Sea snakes", AttributeKey.KC_SEASNAKE, 0,STARTING_NAME_LINE + (6 * 15), NpcIdentifiers.GIANT_SEA_SNAKE),
        TERROR_DOG("Terror dogs", AttributeKey.KC_TERRORDOG, 0,STARTING_NAME_LINE + (6 * 16), NpcIdentifiers.TERROR_DOG, NpcIdentifiers.TERROR_DOG_6474),
        FEVER_SPIDER("Fever spiders", AttributeKey.KC_FEVER_SPIDER, 0,STARTING_NAME_LINE + (6 * 17), NpcIdentifiers.FEVER_SPIDER),
        INFERNAL_MAGE("Infernal mages", AttributeKey.KC_INFERNAL_MAGE, 0,STARTING_NAME_LINE + (6 * 18), NpcIdentifiers.INFERNAL_MAGE, NpcIdentifiers.INFERNAL_MAGE_444, NpcIdentifiers.INFERNAL_MAGE_445, NpcIdentifiers.INFERNAL_MAGE_446, NpcIdentifiers.INFERNAL_MAGE_447),
        BRINE_RAT("Brine rats", AttributeKey.KC_BRINERAT, 0,STARTING_NAME_LINE + (6 * 19), NpcIdentifiers.BRINE_RAT),
        BLOODVELD("Bloodvelds", AttributeKey.KC_BLOODVELD, 0,STARTING_NAME_LINE + (6 * 20), NpcIdentifiers.BLOODVELD, NpcIdentifiers.BLOODVELD_485, NpcIdentifiers.BLOODVELD_486, NpcIdentifiers.BLOODVELD_487, NpcIdentifiers.MUTATED_BLOODVELD),
        JELLY("Jellys", AttributeKey.KC_JELLY, 0,STARTING_NAME_LINE + (6 * 21), NpcIdentifiers.JELLY, NpcIdentifiers.JELLY_438, NpcIdentifiers.JELLY_439, NpcIdentifiers.JELLY_440, NpcIdentifiers.JELLY_441, NpcIdentifiers.JELLY_442, NpcIdentifiers.JELLY_7518, NpcIdentifiers.VITREOUS_JELLY),
        TUROTH("Turoths", AttributeKey.KC_TUROTH, 0,STARTING_NAME_LINE + (6 * 22), NpcIdentifiers.TUROTH, NpcIdentifiers.TUROTH_427, NpcIdentifiers.TUROTH_428, NpcIdentifiers.TUROTH_429, NpcIdentifiers.TUROTH_430, NpcIdentifiers.TUROTH_431, NpcIdentifiers.TUROTH_432),
        ZYGOMITE("Zygomites", AttributeKey.KC_ZYGOMITE, 0, STARTING_NAME_LINE + (6 * 23), NpcIdentifiers.ZYGOMITE, NpcIdentifiers.ZYGOMITE_1024, NpcIdentifiers.ANCIENT_ZYGOMITE),
        CAVE_HORROR("Cave horrors", AttributeKey.KC_CAVEHORROR, 0,STARTING_NAME_LINE + (6 * 24), NpcIdentifiers.CAVE_HORROR, NpcIdentifiers.CAVE_HORROR_1048, NpcIdentifiers.CAVE_HORROR_1049, NpcIdentifiers.CAVE_HORROR_1050, NpcIdentifiers.CAVE_HORROR_1051),
        ABERRANT_SPECTRE("Aberrant spectres", AttributeKey.KC_ABERRANT_SPECTRE, 0, STARTING_NAME_LINE + (6 * 25), NpcIdentifiers.ABERRANT_SPECTRE, NpcIdentifiers.ABERRANT_SPECTRE_3, NpcIdentifiers.ABERRANT_SPECTRE_4, NpcIdentifiers.ABERRANT_SPECTRE_5, NpcIdentifiers.ABERRANT_SPECTRE_6, NpcIdentifiers.ABERRANT_SPECTRE_7),
        SPIRITUAL_WARRIOR("Spirutal warriors", AttributeKey.KC_SPIRITUAL_WARRIOR, 0, STARTING_NAME_LINE + (6 * 26), NpcIdentifiers.SPIRITUAL_WARRIOR, NpcIdentifiers.SPIRITUAL_WARRIOR_2243, NpcIdentifiers.SPIRITUAL_WARRIOR_3159, NpcIdentifiers.SPIRITUAL_WARRIOR_3166),
        KURASK("Kurasks", AttributeKey.KC_KURASK, 0,STARTING_NAME_LINE + (6 * 27), NpcIdentifiers.KURASK_410, NpcIdentifiers.KURASK_411),
        SKELETAL_WYVERN("Skeletal wyverns", AttributeKey.KC_SKELETAL_WYVERN, 0, STARTING_NAME_LINE + (6 * 28), NpcIdentifiers.SKELETAL_WYVERN, NpcIdentifiers.SKELETAL_WYVERN_466, NpcIdentifiers.SKELETAL_WYVERN_467, NpcIdentifiers.SKELETAL_WYVERN_468),
        GARGOYLE("Gargoyles", AttributeKey.KC_GARGOYLE, 0, STARTING_NAME_LINE + (6 * 29), NpcIdentifiers.GARGOYLE),
        NECHRYAEL("Nechryaels", AttributeKey.KC_NECHRYAEL, 0, STARTING_NAME_LINE + (6 * 30), NpcIdentifiers.NECHRYAEL, NpcIdentifiers.NECHRYAEL_11, NpcIdentifiers.GREATER_NECHRYAEL),
        SPIRITUAL_MAGE("Spirutal mages", AttributeKey.KC_SPIRITUAL_MAGE, 0, STARTING_NAME_LINE + (6 * 31), NpcIdentifiers.SPIRITUAL_MAGE, NpcIdentifiers.SPIRITUAL_MAGE_2244, NpcIdentifiers.SPIRITUAL_MAGE_3161, NpcIdentifiers.SPIRITUAL_MAGE_3168),
        ABYSSAL_DEMON("Abyssal demons", AttributeKey.KC_ABYSSALDEMON, 0, STARTING_NAME_LINE + (6 * 32), NpcIdentifiers.ABYSSAL_DEMON_415, NpcIdentifiers.ABYSSAL_DEMON_416, NpcIdentifiers.ABYSSAL_DEMON_7241),
        CAVE_KRAKEN("Cave krakens", AttributeKey.KC_CAVEKRAKEN, 0, STARTING_NAME_LINE + (6 * 33), NpcIdentifiers.CAVE_KRAKEN),
        DARK_BEAST("Dark beasts", AttributeKey.KC_DARKBEAST, 0,STARTING_NAME_LINE + (6 * 34), NpcIdentifiers.DARK_BEAST, NpcIdentifiers.DARK_BEAST_7250),
        SMOKE_DEVIL("Smoke devils", AttributeKey.KC_SMOKEDEVIL, 0,STARTING_NAME_LINE + (6 * 35), NpcIdentifiers.SMOKE_DEVIL, NpcIdentifiers.SMOKE_DEVIL_6639, NpcIdentifiers.SMOKE_DEVIL_6655, NpcIdentifiers.SMOKE_DEVIL_8482, NpcIdentifiers.SMOKE_DEVIL_8483),
        SUPERIOR_CREATURES("Superior creatures", AttributeKey.SUPERIOR, 0,STARTING_NAME_LINE + (6 * 36), NpcIdentifiers.CRUSHING_HAND, NpcIdentifiers.CHASM_CRAWLER, NpcIdentifiers.SCREAMING_BANSHEE, NpcIdentifiers.SCREAMING_TWISTED_BANSHEE, NpcIdentifiers.GIANT_ROCKSLUG, NpcIdentifiers.COCKATHRICE, NpcIdentifiers.FLAMING_PYRELORD, NpcIdentifiers.MONSTROUS_BASILISK, NpcIdentifiers.MALEVOLENT_MAGE, NpcIdentifiers.INSATIABLE_BLOODVELD, NpcIdentifiers.VITREOUS_JELLY, NpcIdentifiers.VITREOUS_WARPED_JELLY, NpcIdentifiers.CAVE_ABOMINATION, NpcIdentifiers.ABHORRENT_SPECTRE, NpcIdentifiers.REPUGNANT_SPECTRE, NpcIdentifiers.BASILISK_SENTINEL, NpcIdentifiers.CHOKE_DEVIL, NpcIdentifiers.KING_KURASK, NpcIdentifiers.MARBLE_GARGOYLE, NpcIdentifiers.NECHRYARCH, NpcIdentifiers.GREATER_ABYSSAL_DEMON, NpcIdentifiers.NIGHT_BEAST, NpcIdentifiers.NUCLEAR_SMOKE_DEVIL),
        BRUTAL_BLACK_DRAGON("Brutal black dragons", AttributeKey.BRUTAL_BLACK_DRAGON, 0,STARTING_NAME_LINE + (6 * 37), NpcIdentifiers.BRUTAL_BLACK_DRAGON, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8093),
        FOSSIL_ISLAND_WYVERNS("Fossil island wyverns", AttributeKey.FOSSIL_WYVERN, 0,STARTING_NAME_LINE + (6 * 38), NpcIdentifiers.ANCIENT_WYVERN, NpcIdentifiers.LONGTAILED_WYVERN, NpcIdentifiers.SPITTING_WYVERN, NpcIdentifiers.TALONED_WYVERN),
        REVENANTS("Revenants", AttributeKey.REVENANTS_KILLED, 0,STARTING_NAME_LINE + (6 * 39), NpcIdentifiers.REVENANT_IMP, NpcIdentifiers.REVENANT_CYCLOPS, NpcIdentifiers.REVENANT_DARK_BEAST, NpcIdentifiers.REVENANT_DEMON, NpcIdentifiers.REVENANT_DRAGON, NpcIdentifiers.REVENANT_GOBLIN, NpcIdentifiers.REVENANT_HELLHOUND, NpcIdentifiers.REVENANT_HOBGOBLIN, NpcIdentifiers.REVENANT_KNIGHT, NpcIdentifiers.REVENANT_ORK, NpcIdentifiers.REVENANT_PYREFIEND),
        WYRM("Wyrms", AttributeKey.WYRM, 0,STARTING_NAME_LINE + (6 * 40), NpcIdentifiers.WYRM_8611),
        DRAKE("Drakes", AttributeKey.DRAKE, 0,STARTING_NAME_LINE + (6 * 41), NpcIdentifiers.DRAKE_8612, NpcIdentifiers.DRAKE_8613),
        HYDRA("Hydras", AttributeKey.HYDRA, 0, STARTING_NAME_LINE + (6 * 42), NpcIdentifiers.HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA, NpcIdentifiers.ALCHEMICAL_HYDRA_8616, NpcIdentifiers.ALCHEMICAL_HYDRA_8617, NpcIdentifiers.ALCHEMICAL_HYDRA_8618, NpcIdentifiers.ALCHEMICAL_HYDRA_8619, NpcIdentifiers.ALCHEMICAL_HYDRA_8620, NpcIdentifiers.ALCHEMICAL_HYDRA_8621, NpcIdentifiers.ALCHEMICAL_HYDRA_8622),
        BASILISK_KNIGHT("Basilist knights", AttributeKey.BASILISK_KNIGHT, 0, STARTING_NAME_LINE + (6 * 43), NpcIdentifiers.BASILISK_KNIGHT);

        private final String name;
        private final AttributeKey kc;
        private final int streak;
        private final int startLine;
        private final int[] npcs;

        SlayerMonsters(String name, AttributeKey kc, int streak, int nameLine, int... npcs) {
            this.name = name;
            this.kc = kc;
            this.streak = streak;
            this.startLine = nameLine;
            this.npcs = npcs;
        }

        public String getName() {
            return name;
        }

        public AttributeKey getKc() {
            return kc;
        }

        public int getStreak() {
            return streak;
        }

        public int getStartLine() {
            return startLine;
        }

        public int[] getNpcs() {
            return npcs;
        }
    }

    /**
     * This method checks if we can add a kill to our tracking variables.
     *
     * @param npc    The slayer monster being tracked
     */
    public void addKill(NPC npc) {
        for (SlayerMonsters slayerMonster : SlayerMonsters.values()) {
            if (Arrays.stream(slayerMonster.getNpcs()).anyMatch(npc_id -> npc_id == npc.id())) {
                int killCount = player.getAttribOr(slayerMonster.getKc(),0);
                killCount++;
                player.putAttrib(slayerMonster.getKc(), (killCount));
                player.message("Your " + slayerMonster.getName() + " kill count is: <col=ca0d0d>" + Utils.format(killCount) + "</col>.");
                if(npc.getCombatInfo() == null) return;
                int deathLength = npc.getCombatInfo().deathlen;
                break;
            }
        }
    }

    public void openLog() {
        player.getInterfaceManager().open(46300);
        player.getPacketSender().changeWidgetText(46304, "Slayer Kill Log");
        player.getPacketSender().sendScrollbarHeight(46310, 792);

        for (SlayerMonsters slayerMonsters : SlayerMonsters.values()) {
            player.getPacketSender().sendString(slayerMonsters.getStartLine(), slayerMonsters.getName());
            int kc = player.getAttribOr(slayerMonsters.getKc(), 0);
            player.getPacketSender().sendString(slayerMonsters.getStartLine()+1, ""+ Utils.formatNumber(kc));
            player.getPacketSender().sendString(slayerMonsters.getStartLine()+2, ""+ Utils.formatNumber(slayerMonsters.getStreak()));
        }
    }

}

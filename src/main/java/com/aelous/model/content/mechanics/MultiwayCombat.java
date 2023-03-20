package com.aelous.model.content.mechanics;

import com.aelous.model.content.raids.party.Party;
import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

import java.util.Arrays;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.BLURITE_ORE;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.THE_NIGHTMARE_9430;

/**
 * @author Patrick van Elderen | January, 26, 2021, 14:22
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MultiwayCombat {

    private static final List<Area> exclusions = List.of(
        // Rev cave enter holes
        new Area(3236, 10229, 3248, 10236), new Area(3193, 10051, 3207, 10061));

    private static boolean exclusion(Tile tile) {
        return exclusions.stream().anyMatch(a -> a.contains(tile));
    }

    public static boolean includes(Entity entity) {
        // Skotizo is an exception. He's ALWAYS MULTIIII.
        if (entity.isNpc()) {
            if (entity.getAsNpc().isWorldBoss() || entity.getAsNpc().isCombatDummy() || entity.getAsNpc().id() == THE_NIGHTMARE_9430) {
                return true;
            }
        }
        return includes(entity.tile());
    }

    public static boolean includes(Tile tile) {
        return !exclusion(tile) && (REGIONS.stream().anyMatch(id -> id == tile.region()) || CHUNKS.stream().anyMatch(id -> tile.chunk() == id || tile.equals(new Tile(3021, 3855)) || tile.equals(new Tile(3022, 3855))));
    }

    public static boolean region_includes(Tile tile) {
        return !exclusion(tile) && REGIONS.stream().anyMatch(id -> id == tile.region());
    }

    public static boolean regionStrict(Tile tile) {
        return REGIONS.stream().anyMatch(id -> id == tile.region());
    }
    public static boolean chunkStrict(Tile tile) {
        return CHUNKS.stream().anyMatch(id -> id == tile.chunk());
    }

    public static boolean chunk_includes(Tile tile) {
        return !exclusion(tile) && CHUNKS.stream().anyMatch(id -> tile.chunk() == id);
    }

    private static final List<Integer> tileChangeListenChunks = List.of(
        // bottom of caves
        26215657, 26281193,
        // top of caves
        26543359, 26543358, 26477822
    );

    /**
     * Always something hey? Single lines in rev caves DO NOT follow region OR chunk border lol. So check tile changes
     * when in these dirty chunks for exact changes.
     */
    public static void tileChanged(Player player) {
        if (tileChangeListenChunks.stream().anyMatch(c -> c == player.tile().chunk())) {
            var state = includes(player.tile()) ? 1 : 0;
            //player.message("state " + state);

            if (player.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA,-1) != state) {
                player.getPacketSender().sendMultiIcon(state);
            }
        }
    }

    public static void refresh(Player player, int lastregion, int lastChunk) {
        final boolean multiRegion = regionStrict(player.tile());
        final boolean multiChunk = chunkStrict(player.tile());
        final boolean onTileExcludedFromMulti = exclusion(player.tile());
        final Tile tile = player.tile();
        final boolean onMultiTile = tile.equals(new Tile(3021, 3855)) || tile.equals(new Tile(3022, 3855));
        final boolean inMulti = onMultiTile || (!onTileExcludedFromMulti && (multiChunk || multiRegion));

        if (inMulti) {
            player.getPacketSender().sendMultiIcon(1);
            player.putAttrib(AttributeKey.MULTIWAY_AREA,1);
        } else {
            player.getPacketSender().sendMultiIcon(0);
            player.putAttrib(AttributeKey.MULTIWAY_AREA,0);
        }

        if(lastregion == 13103 && lastregion != player.tile().region()) { // actually changed now
            // Make sure blurite removed when leaving this area
            for (Item i : player.inventory().getItems()) {// Clear blurite ores.
                if (i == null) continue;
                if(i.getId() == BLURITE_ORE) {
                    player.inventory().remove(i, true);
                }
            }
            player.message("<col=FF0000>Any blurite you were carrying was taken by Irena.");
        }

        //When leaving the raids area remove from party
        if(player.raidsParty != null) {
            boolean raiding = player.getRaids() != null && player.getRaids().raiding(player);
            if (!raiding) {
                // defo not raiding
                Party.leaveParty(player, true);
            }
        }

        if(lastregion == 13118) {
            if(!inMulti) {
                player.getPacketSender().sendMultiIcon(0);
                player.putAttrib(AttributeKey.MULTIWAY_AREA, 0);
            }
        }

        if(lastregion == 12190) {
            // Leaving wildy gwd. Has to be hardcoded because in the same region, it is partly single.
            if (!inMulti) {
                player.getPacketSender().sendMultiIcon(0);
                player.putAttrib(AttributeKey.MULTIWAY_AREA,0);
            }
        }
    }

    private static final List<Integer> REGIONS = Arrays.asList(
        /* Safe: **/
        6992, 6993, //Giant mole
        9551, //Fight Caves
        11601,
        6812, 6813, 6556, 6557, //Catacombs
        10536, //Pest Control
        11827, 11828, 11829, //Falador
        12341, //Barbarian Village
        8253, 8252, 8508, 8509, 8254, //Lunar Isle:
        9273, 9017, //Piscatoris Fishing Colony
        9532, 9276, //Fremennik Isles
        10809, 10810, 10554, //Relleka
        10549, //Ranging Guild
        10034, //Battlefield
        10029, //Feldip hills
        11318, //White wolf mountain
        11575, //Burthope
        11577, 11578, //Trollheim
        11050, 11051, 10794, 10795, //Apeatoll
        12590, //Bandit camp
        13105, //Al Kharid
        12337, //Wizards tower
        12338, //Draynor Village
        11602, 11603, 11346, 11347, //Godwars Dungeon
        13131, 13387, //FFA clan wars, top half
        11844, //Corporeal beast
        11589, //Dagannoths
        5690, 5689, //Zeah lizanman pit
        9116, //Kraken cave
        9619, 9363, //new thermo room
        8023, //Gnome Stronghold crash site (monkey madness)
        13972, // Kalphite queen lair

        /* Wildy: (uses 8x8 chunks for some sections as well as chunks) **/
        5789,
        12599, 12600, //Wilderness Ditch
        12855, 12856, //Mammoths (lvl 9)
        13111, 13112, 13113, 13114, 13115, 13116, 13117, //Varrock -> GDZ
        12857, 12858, 12859, 12860, 12861, //East graveyard (lvl 17)
        13372, 13373, //East of Callisto (lvl 41)
        12604, //Black chins (lvl 33)
        12348, //Wildy GWD & Center wildy north of lava maze
        12088, 12089, //North of dark warriors (lvl 17)
        12961, //Scorpia pit
        9033, // KBD zone
        12363, 12362, 12106, 11851, 11850, // Abyssal Sire
        14938, 14939, // Smokedevil room in Nieve's cave + kalphite hive room
        12701, 12702, 12703, 12957, 12958, 12959, // Revenants! Not 100% accurate but.. Yeah
        9886, 10142, // waterbirth dungeon / dagannoth cave
        9007, 9008, // zulrah
        9023, // vorkath
        12889, 13136, 13137, 13138, 13139, //COX
        12869, 12613, 13125, 13122, 13123, 12611, 12612, 12867,//TOB
        12615, 12616 // HP raids
    );

    private static final List<Integer> CHUNKS = Arrays.asList(
        // Kourend catacomb exceptions
        13567211, 13567210, 13567209, 13567208,
        13436136, 13370600, 13305064, 13239528,
        13239529, 13305065, 13305066, 13370602,
        13370601, 13436137, 13436138, 13436139,
        13501674, 13501673,

        // Chaos temple - Crazy Arch 44s
        24117724, 24117725, 24117726,
        24183260, 24183261, 24183262,

        //Black chins
        25756120, 25756121, 25756122, 25756123, 25756124, 25756125, 25756126, 25756127,
        25821656, 25821657, 25821658, 25821659, 25821660, 25821661, 25821662, 25821663,
        25887192, 25887193, 25887194, 25887195, 25887196, 25887197, 25887198, 25887199,
        25952728, 25952729, 25952730, 25952731, 25952732, 25952733, 25952734, 25952735,
        26018264, 26018265, 26018266, 26018267, 26018268, 26018269, 26018270, 26018271,
        26083800, 26083801, 26083802, 26083803, 26083804, 26083805, 26083806, 26083807,
        26149336, 26149337, 26149338, 26149339, 26149340, 26149341, 26149342, 26149343,


        //KBD Cage
        24642018, 24642019, 24642020, 24642021, 24642022, 24642023,
        24707554, 24707555, 24707556, 24707557, 24707558, 24707559,
        24773090, 24773091, 24773092, 24773093, 24773094, 24773095,
        24838626, 24838627, 24838628, 24838629, 24838630, 24838631,
        24904162, 24904163, 24904164, 24904165, 24904166, 24904167,

        //Rune rocks north of KBD cage
        24969699, 24969700, 24969702, 24969703, 25035238, 25035239,
        25100774, 25100775,

        // Wilderness agility course at 55 wilderness
        24445417, 24510953, 24576489,
        24445418, 24510954, 24576490,
        24445419

        // TODO wildy gwd dungeon needs to be chunks not region because same region (12190) upstairs height 3 is single

    );
}

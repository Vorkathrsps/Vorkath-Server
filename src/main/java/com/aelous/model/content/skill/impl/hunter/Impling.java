package com.aelous.model.content.skill.impl.hunter;

import com.aelous.model.content.items.ImplingLoot;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.Arrays;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | March, 08, 2021, 09:00
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum Impling {

    BABY(1635, 17, 27, 18, 20, 11238, 0, 10),
    YOUNG(1636, 22, 32, 20, 22, 11240, 0, 10),
    GOURMET(1637, 28, 38, 22, 24, 11242, 0, 10),
    EARTH(1638, 36, 46, 25, 27, 11244, 0, 10),
    ESSENCE(1639, 42, 52, 27, 29, 11246, 100, 10),
    ECLECTIC(1640, 50, 60, 30, 32, 11248, 0, 10),
    NATURE(1641, 58, 68, 34, 36, 11250, 100, 10),
    MAGPIE(1642, 65, 75, 44, 54, 11252, 50, 10),
    NINJA(1643, 74, 85, 50, 60, 11254, 40, 10),
    DRAGON(1644, 83, 93, 65, 75, 11256, 10, 10),
    LUCKY(7233, 89, 99, 0, 0, 19732, 1, 1);

    public final int npcId, levelReq, bareHandLevelReq, jarId, puroPuroSpawnWeight, overworldSpawnWeight;
    public final double puroExp, worldExp;

    Impling(int npcId, int levelReq, int bareHandLevelReq, double puroExp, double worldExp, int jarId, int puroPuroSpawnWeight, int overworldSpawnWeight) {
        this.npcId = npcId;
        this.levelReq = levelReq;
        this.bareHandLevelReq = bareHandLevelReq;
        this.puroExp = puroExp;
        this.worldExp = worldExp;
        this.jarId = jarId;
        this.puroPuroSpawnWeight = puroPuroSpawnWeight;
        this.overworldSpawnWeight = overworldSpawnWeight;
    }

    private static final Area PURO_PURO = new Area(2562, 4290, 2621, 4349, 0);

    private static void attemptCatch(Player player, NPC npc, Impling impling) {
        boolean barehands = !hasButterflyNet(player) && !hasMagicButterflyNet(player);

        /* check for impling jar */
        boolean impJar = player.inventory().contains(IMPLING_JAR);
        if(!barehands && !impJar) {
            player.message("You don't have an empty impling jar in which to keep an impling.");
            return;
        }

        /* check for level req */
        int hunterLevel = player.getSkills().level(Skills.HUNTER);
        int levelReq = barehands ? impling.bareHandLevelReq : impling.levelReq;
        if(hunterLevel < levelReq) {
            player.message("You need a Hunter level of at least " + levelReq + " to catch this impling" + (barehands ? " barehanded." : "."));
            return;
        }

        if(!player.tile().isWithinDistance(npc.tile(), 1))
            return;

        player.runFn(1, () -> {
            player.lock();
            if(barehands) {
                player.animate(7171);
            } else {
                player.animate(hasMagicButterflyNet(player) ? 6605 : 6606);
            }
            Chain.bound(null).runFn(2, () -> {
                if(World.getWorld().rollDie(2, 1)) { //TODO: proper chance

                    if (barehands) {
                        ImplingLoot.ImplingJar jar = ImplingLoot.ImplingJar.forJarId(impling.jarId);
                        Item loot;
                        if (jar != null) {
                            loot = jar.lootTable.rollItem();
                            player.inventory().add(loot);
                        } else {
                            player.inventory().add(new Item(impling.jarId));
                        }
                    } else {
                        int slot = player.getAttribOr(AttributeKey.ITEM_SLOT,-1);
                        player.inventory().add(new Item(impling.jarId), slot, true);
                    }

                    despawnImpling(npc);
                    player.getSkills().addXp(Skills.HUNTER, player.tile().inArea(PURO_PURO) ? impling.puroExp : impling.worldExp,true);
                    var counter = player.<Integer>getAttribOr(AttributeKey.IMPLINGS_CAUGHT, 0) + 1;
                    player.putAttrib(AttributeKey.IMPLINGS_CAUGHT, counter);
                }
                player.unlock();
            });
        });
    }

    private static boolean hasButterflyNet(Player player) {
        return player.getEquipment().contains(BUTTERFLY_NET);
    }

    private static boolean hasMagicButterflyNet(Player player) {
        return player.getEquipment().contains(MAGIC_BUTTERFLY_NET);
    }

    /**
     * Impling spawning
     */
    private static final int PURO_PURO_STATIC_RESPAWN_DELAY = 50; // respawn time for static spawns in puro puro (baby, young gourmet, earth, eclectic)
    private static final int PURO_PURO_TOTAL_SPAWN_WEIGHT = Arrays.stream(values()).mapToInt(imp -> imp.puroPuroSpawnWeight).sum();

    private static final int OVERWORLD_SPAWN_DELAY = 200; // spawn time for random spawns in the overworld (implings will attempt to spawn at this interval, if the active number is below maximum)
    private static final int OVERWORLD_MAX_IMPLINGS = 200; // maximum number of overworld spawns that can be active at one time
    private static final int OVERWORLD_TOTAL_SPAWN_WEIGHT = Arrays.stream(values()).mapToInt(imp -> imp.overworldSpawnWeight).sum();

    private static int ACTIVE_OVERWORLD_IMPLINGS = 0;

    private static void despawnImpling(NPC npc) {
        Impling type = get(npc.id());
        if (type == null)
            return;
        if (isInPuroPuro(npc)) {
            if (type == BABY || type == YOUNG || type == GOURMET || type == EARTH || type == ECLECTIC) { // these have static spawns
                npc.runUninterruptable(1, () -> {
                    World.getWorld().unregisterNpc(npc);
                    Chain.bound(null).runFn(PURO_PURO_STATIC_RESPAWN_DELAY, () -> {
                        World.getWorld().registerNpc(npc);
                        npc.teleport(npc.spawnTile());
                    });
                });
            } else {
                World.getWorld().unregisterNpc(npc);
            }
        } else {
            ACTIVE_OVERWORLD_IMPLINGS--;
            World.getWorld().unregisterNpc(npc);
        }
    }

    public static Impling get(int id) {
        for (Impling value : values()) {
            if (value.npcId == id)
                return value;
        }
        return null;
    }

    private static boolean isInPuroPuro(Entity entity) {
        return entity.tile().region() == 10307; // puro puro region
    }

    private static Impling getRandomOverworldSpawn() {
        int roll = Utils.random(OVERWORLD_TOTAL_SPAWN_WEIGHT);
        for (Impling impling : values()) {
            if (impling.overworldSpawnWeight == 0)
                continue;
            roll -= impling.overworldSpawnWeight;
            if (roll <= 0) {
                return impling;
            }
        }
        return Utils.randomElement(values()); // should be unreachable
    }

    private static void spawnRandomImplingOverworld() {
        Impling type = getRandomOverworldSpawn();
        Tile tile = Utils.randomElement(OVERWORLD_RANDOM_SPAWN_TILES);
        NPC impling = new NPC(type.npcId, new Tile(tile.getX(), tile.getY(), tile.getZ()));
        impling.walkRadius(16);
        impling.def().flightClipping = true;
        World.getWorld().registerNpc(impling);
        ACTIVE_OVERWORLD_IMPLINGS++;
    }

    public static void onServerStartup() {
        //spawn a few on startup
        for (int index = 0; index < 50; index++)
            spawnRandomImplingOverworld();

        Chain.bound(null).name("ImplingTask").repeatingTask(OVERWORLD_SPAWN_DELAY, t -> {
            if (ACTIVE_OVERWORLD_IMPLINGS < OVERWORLD_MAX_IMPLINGS) {
                spawnRandomImplingOverworld();
            }
        });
    }

    private static final Tile[] OVERWORLD_RANDOM_SPAWN_TILES = {

        new Tile(3079, 3503, 0), //edgeville spawns
        new Tile(3104, 3506, 0),
        new Tile(3087, 3485, 0),
        new Tile(3126, 3498, 0),

        new Tile(3170, 3454, 0), // varrock spawns
        new Tile(3208, 3434, 0),
        new Tile(3283, 3453, 0),

        new Tile(3086, 3235, 0), // draynor village spawn

        new Tile(3223, 3223, 0), // lumbridge spawns
        new Tile(3197, 3232, 0),
        new Tile(3234, 3226, 0),

        new Tile(3276, 3166, 0), // al kharid spawn
        new Tile(3367, 3268, 0), // duel arena

        new Tile(3035, 3256, 0), // port sarim spawn

        new Tile(2957, 3224, 0), // rimmington spawn

        new Tile(2965, 3401, 0), // falador spawns
        new Tile(3010, 3381, 0),
        new Tile(3005, 3327, 0),
        new Tile(3050, 3317, 0),

        new Tile(2930, 3449, 0), // taverley spawn

        new Tile(2720, 3484, 0), // seers village spawns
        new Tile(2719, 3461, 0),
        new Tile(2722, 3504, 0),

        new Tile(2851, 3432, 0), // catherby spawns
        new Tile(2815, 3466, 0),

        new Tile(2596, 3411, 0), // fishing guild spawn

        new Tile(2508, 3524, 0), // barbarian outpost spawns
        new Tile(2497, 3506, 0),

        new Tile(2463, 3481, 0), // gnome stronghold spawns
        new Tile(2479, 3448, 0),
        new Tile(2475, 3427, 0),
        new Tile(2437, 3427, 0),

        new Tile(2658, 3311, 0), // ardougne spawns
        new Tile(2664, 3301, 0),
        new Tile(2668, 3376, 0),

        new Tile(2542, 3090, 0), // yanille spawn

        new Tile(2454, 3087, 0), // castle wars spawns
        new Tile(2455, 3096, 0),

        new Tile(2344, 3167, 0), // lletya spawn

        new Tile(2340, 3698, 0), // piscatoris colony spawn
        new Tile(2348, 3586, 0), // piscators hunter area spawn

        new Tile(2765, 3207, 0), // brimhaven/karamja spawns
        new Tile(2760, 3171, 0),
        new Tile(2797, 3097, 0),
        new Tile(2852, 2960, 0),

        new Tile(2611, 2905, 0), // feldip hills hunter area spawns
        new Tile(2558, 2894, 0),
        new Tile(2545, 2914, 0),

        new Tile(3495, 3493, 0), // canifis spawn
        new Tile(3604, 3533, 0), // morytania farming patch spawn

        new Tile(3111, 3542, 0), // wilderness spawns
        new Tile(3237, 3622, 0),
        new Tile(3029, 3724, 0),
        new Tile(3203, 3868, 0),
        new Tile(3006, 3865, 0),
        new Tile(2998, 3932, 0),
        new Tile(3187, 3936, 0),
        new Tile(3180, 3934, 0),

        new Tile(1821, 3774, 0), // Zeah spawns
        new Tile(1712, 3880, 0),
        new Tile(1249, 3731, 0),
        new Tile(1553, 3445, 0),
        new Tile(1646, 3503, 0),
        new Tile(1586, 3487, 0),
        new Tile(1788, 3473, 0),
        new Tile(1825, 3487, 0),
        new Tile(1632, 3666, 0),

    };

    public static boolean onNpcOption1(Player player, NPC npc) {
        for (Impling impling : values()) {
            if (npc.id() == impling.npcId) {
                attemptCatch(player, npc, impling);
                return true;
            }
        }
        return false;
    }

}

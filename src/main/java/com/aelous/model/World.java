package com.aelous.model;

import com.aelous.GameConstants;
import com.aelous.GameEngine;
import com.aelous.GameServer;
import com.aelous.cache.definitions.DefinitionRepository;
import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.core.GameSyncExecutor;
import com.aelous.core.GameSyncTask;
import com.aelous.core.TimesCycle;
import com.aelous.core.task.TaskManager;
import com.aelous.model.content.areas.burthope.warriors_guild.dialogue.Shanomi;
import com.aelous.model.content.bountyhunter.BountyHunter;
import com.aelous.model.content.minigames.MinigameManager;
import com.aelous.model.content.skill.impl.fishing.Fishing;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.EntityList;
import com.aelous.model.entity.NodeType;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.masks.impl.updating.NPCUpdating;
import com.aelous.model.entity.masks.impl.updating.PlayerUpdating;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCCombatInfo;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.PlayerPerformanceTracker;
import com.aelous.model.items.Item;
import com.aelous.model.items.ItemWeight;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.items.container.shop.Shop;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.OwnedObject;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.Flags;
import com.aelous.model.map.region.Region;
import com.aelous.model.map.region.RegionManager;
import com.aelous.network.codec.login.LoginService;
import com.aelous.utility.*;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SHANOMI;

/**
 * Represents the world, processing it and its mobs.
 *
 * @author Professor Oak
 * @author lare96
 */
public class World {

    public static boolean SYNCMODE1 = false;
    public final Tile HOME = Tile.of(2028, 3577, 0);
    public final Tile EDGEHOME = Tile.of(3085, 3492, 0);

    private static final Logger logger = LogManager.getLogger(World.class);

    public World() {
        definitionRepository = GameServer.definitions();
        examineRepository = new ExamineRepository(definitionRepository);
    }

    /**
     * World instance.
     */
    private static final World world = new World();

    /**
     * Gets the world instance.
     *
     * @return The world instance.
     */
    public static World getWorld() {
        return world;
    }

    public Map<Integer, Shop> shops = new HashMap<>();

    public Shop shop(int id) {
        return shops.get(id);
    }

    public static long LAST_FLUSH;

    /**
     * The collection of active {@link Player}s.
     */
    private final EntityList<Player> players = new EntityList<>(GameConstants.PLAYERS_LIMIT);

    /**
     * The collection of active {@link NPC}s. Be careful when adding NPCs directly to the list without using the queue, try not to bypass the queue.
     */
    private final EntityList<NPC> npcs = new EntityList<>(GameConstants.NPCS_LIMIT);

    /**
     * The collection of active {@link GameObject}s..
     */
    private final List<GameObject> spawnedObjs = new LinkedList<>();
    private final List<GameObject> removedObjs = new LinkedList<>();

    /**
     * The manager for game synchronization.
     */
    private static final GameSyncExecutor executor = new GameSyncExecutor();

    /**
     * The sections of the World sequence, used for logging successful completion of the World sequence.
     */
    public boolean[] section = new boolean[11];

    private final Calendar calendar = new GregorianCalendar();

    public Calendar getCalendar() {
        return calendar;
    }

    private boolean applyDoubleExperience() {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return (day == 5 || day == 6 || day == 7) || GameServer.properties().doubleExperienceEvent;
    }

    private boolean applyDoubleSlayerRewardPoints() {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return (day == 5 || day == 6 || day == 7) || GameServer.properties().doubleSlayerRewardPointsEvent;
    }

    private boolean applyDoubleBM() {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return (day == 5 || day == 6 || day == 7) || GameServer.properties().doubleBMEvent;
    }

    public boolean doubleVotePoints() {
        return isFirstWeekofMonth() && GameServer.properties().doubleBMEvent;
    }

    private boolean isFirstWeekofMonth() {
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth <= 7;
    }

    /**
     * Timing
     */
    public long currentTick() {
        return cycleCount();
    }

    public long getEnd(long ticks) {
        return currentTick() + ticks;
    }

    public boolean isPast(long end) {
        return currentTick() >= end;
    }

    public int xpMultiplier = applyDoubleExperience() ? 2 : 1;
    public int slayerRewardPointsMultiplier = applyDoubleSlayerRewardPoints() ? 2 : 1;
    public int bmMultiplier = applyDoubleBM() ? 2 : 1;

    private int elapsedTicks;

    private int lastPidUpdateTick;

    private long lastMinuteScan;

    private final DefinitionRepository definitionRepository;

    public DefinitionRepository definitions() {
        return definitionRepository;
    }

    private final ExamineRepository examineRepository;

    public ExamineRepository examineRepository() {
        return examineRepository;
    }

    protected final Map<String, OwnedObject> ownedObjects = Maps.newConcurrentMap();

    public double get() {
        return ThreadLocalRandom.current().nextDouble();
    }
    public int get(int maxRange) {
        return (int) (get() * (maxRange + 1D));
    }
    public <T> T get(List<T> list) {
        return list.get(get(list.size() - 1));
    }
    public <T> T get(T[] values) {
        return values[get(values.length - 1)];
    }


    public static class WorldPerfTracker {
        public long skulls, tasks, login, logout, objects, packets, players,
            allNpcsProcess, gpi, flush, reset, games;

        // track totals for all 2048 players this cycle
        public PlayerPerformanceTracker allPlayers = new PlayerPerformanceTracker();

        public void reset() {
            skulls = tasks = login = logout = objects = packets = players = allNpcsProcess =
                gpi = flush = reset = games = 0;
        }

        static final DecimalFormat df = new DecimalFormat("#.##");

        @Override
        public String toString() {
            return breakdown();
        }

        public String breakdown() {
            if (!TimesCycle.BENCHMARKING_ENABLED) return "N/A";
            StringBuilder sb2 = new StringBuilder();
            if ((int) (1. * skulls / 1_000_000.) > 0)
                sb2.append(String.format("skulls:%s ms, ", df.format(1. * skulls / 1_000_000.)));
            if ((int) (1. * tasks / 1_000_000.) > 0)
                sb2.append(String.format("tasks:%s ms, ", df.format(1. * tasks / 1_000_000.)));
            if ((int) (1. * login / 1_000_000.) > 0)
                sb2.append(String.format("login:%s ms, ", df.format(1. * login / 1_000_000.)));
            if ((int) (1. * objects / 1_000_000.) > 0)
                sb2.append(String.format("objects:%s ms, ", df.format(1. * objects / 1_000_000.)));
            if ((int) (1. * packets / 1_000_000.) > 0)
                sb2.append(String.format("packets:%s ms, ", df.format(1. * packets / 1_000_000.)));
            // if ((int) (1. * players / 1_000_000.) > 0) // already printed
            //     sb2.append(String.format("players.process:%s ms, ", df.format(1. * players / 1_000_000.)));
            // if ((int) (1. * allNpcsProcess / 1_000_000.) > 0) // already printed
            //   sb2.append(String.format("npcs.process:%s ms, ", df.format(1. * allNpcsProcess / 1_000_000.)));
            if ((int) (1. * gpi / 1_000_000.) > 0)
                sb2.append(String.format("gpi:%s ms, ", df.format(1. * gpi / 1_000_000.)));
            if ((int) (1. * flush / 1_000_000.) > 0)
                sb2.append(String.format("flush:%s ms, ", df.format(1. * flush / 1_000_000.)));
            if ((int) (1. * reset / 1_000_000.) > 0)
                sb2.append(String.format("reset:%s ms, ", df.format(1. * reset / 1_000_000.)));
            if ((int) (1. * games / 1_000_000.) > 0)
                sb2.append(String.format("games:%s ms, ", df.format(1. * games / 1_000_000.)));

            if (sb2.toString().length() > 0)
                return sb2 + allPlayers.breakdown();
            return "World:N/A";
        }
    }

    List<Integer> playerRenderOrder;

    List<Integer> npcRenderOrder;

    public WorldPerfTracker benchmark = new WorldPerfTracker();

    Runnable skull = () -> {

        //Temporary minute check until a better system is created.
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastMinuteScan) >= 60) {
            players.forEach(p -> {
                if (Skulling.skulled(p)) {
                    Skulling.decrementSkullCycle(p);
                }
            });

            lastMinuteScan = System.currentTimeMillis();
        }
    },
        tasks = () -> {
            TaskManager.sequence();
        }, objs = () -> {

        for (OwnedObject object : ownedObjects.values()) {
            try {
                object.tick();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }, packets = () -> {
        try {
            executor.sync(new GameSyncTask(NodeType.PLAYER, false, playerRenderOrder) {
                @Override
                public void execute(int index) {
                    Player player = players.get(index);
                    player.getSession().handleQueuedPackets();
                    player.syncContainers();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, npcProcess = () -> {
        NpcPerformance.resetWorldTime();
        try {
            executor.sync(new GameSyncTask(NodeType.NPC, false, npcRenderOrder) {
                @Override
                public void execute(int index) {
                    NPC npc = npcs.get(index);
                    if (npc != null && !npc.hidden()) {
                        npc.sequence();
                        synchronized (npcs) {//Assume viewport is false, we set it in NPC Updating below.
                            npc.inViewport(false);
                            npc.processed = true;
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, playerProcess = () -> {
        try {
            executor.sync(new GameSyncTask(NodeType.PLAYER, false, playerRenderOrder) {
                @Override
                public void execute(int index) {
                    Player player = players.get(index);
                    player.sequence();
                    synchronized (players) {
                        player.processed = true;
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, gpi = () -> {
        try {
            executor.sync(new GameSyncTask(NodeType.PLAYER, playerRenderOrder) {
                @Override
                public void execute(int index) {
                    synchronized (players) {
                        Player player = players.get(index);
                        PlayerUpdating.update(player);
                        NPCUpdating.update(player);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, reset = () -> {
        try {
            executor.sync(new GameSyncTask(NodeType.NPC, false, npcRenderOrder) {
                @Override
                public void execute(int index) {
                    NPC npc = npcs.get(index);
                    try {
                        npc.resetUpdating();
                        npc.clearAttrib(AttributeKey.CACHED_PROJECTILE_STATE);
                        npc.performance.reset();
                        synchronized (npcs) {
                            npc.processed = false;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        World.getWorld().getNpcs().remove(npc);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, flush = () -> {
        try {
            executor.sync(new GameSyncTask(NodeType.PLAYER, false, playerRenderOrder) {
                @Override
                public void execute(int index) {
                    Player player = players.get(index);
                    player.resetUpdating();
                    player.clearAttrib(AttributeKey.CACHED_PROJECTILE_STATE);
                    player.setCachedUpdateBlock(null);
                    player.getSession().flush();
                    player.perf.pulse();
                    synchronized (players) {
                        player.processed = false;
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, games = () -> {
        MinigameManager.onTick();
    };

    public int getTickCount() {
        return elapsedTicks;
    }

    /**
     * Processes the world.
     */
    public void sequence() {
        Arrays.fill(section, false);

        long startTime = System.currentTimeMillis();

        Entity.time(t -> benchmark.skulls += t.toNanos(), skull);
        Entity.time(t -> benchmark.tasks += t.toNanos(), tasks);
        Entity.time(t -> benchmark.objects += t.toNanos(), objs);

        //Handle synchronization tasks.
        if (GameServer.properties().enablePidShuffling && (lastPidUpdateTick == 0 || elapsedTicks - lastPidUpdateTick >= GameServer.properties().pidIntervalTicks)) {
            lastPidUpdateTick = elapsedTicks;
            players.shuffleRenderOrder();
        }

        npcRenderOrder = npcs.getRenderOrder();
        playerRenderOrder = players.getRenderOrder();

        Entity.time(t -> GameEngine.profile.wp.player_process += t.toMillis(), () -> {
            Entity.time(t -> benchmark.packets += t.toNanos(), packets);
            Entity.time(t -> benchmark.players += t.toNanos(), playerProcess);
        });

        Entity.time(t -> {
            benchmark.allNpcsProcess += t.toNanos();
            GameEngine.profile.wp.npc_process = t.toMillis();
        }, npcProcess);

        Entity.time(t -> {
            benchmark.gpi += t.toNanos();
            GameEngine.profile.wp.player_npc_updating = t.toMillis();
        }, gpi);

        Entity.time(t -> benchmark.flush += t.toNanos(), flush);
        Entity.time(t -> benchmark.reset += t.toNanos(), reset);
        Entity.time(t -> benchmark.games += t.toNanos(), games);

        GameEngine.profile.world = System.currentTimeMillis() - startTime;
        elapsedTicks++;
    }

    /**
     * Gets a player by their username, using {@link String#equalsIgnoreCase(String)}. case-sensitive usercase Does Not enforce uniqueness.
     * <br> by using ignoreCase we can be sure we get a player even if they have a capitalized name like HITEST.
     *
     * @param username The username of the player.
     * @return The player with the matching username.
     */
    public Optional<Player> getPlayerByName(String username) {
        return players.search(p -> p != null && p.getUsername().equalsIgnoreCase(username));
    }

    public void clearBroadcast() {
        sendBroadcast("");
    }

    public void sendBroadcast(String broadcast) {
        sendBroadcast(broadcast, false, "no_link");
    }

    public void sendBroadcast(String broadcast, boolean hasUrl, String link) {
        if (hasUrl)
            World.getWorld().sendWorldMessage("osrsbroadcast##" + broadcast + "%%" + link);
        else
            World.getWorld().sendWorldMessage("osrsbroadcast##" + broadcast + "%%" + "no_link");
    }

    /**
     * Broadcasts a message to all players in the game.
     *
     * @param message The message to broadcast.
     */
    public void sendWorldMessage(String message) {
        players.forEach(p -> p.getPacketSender().sendMessage(message));
    }

    /**
     * Broadcasts a message to all staff-members in the game.
     *
     * @param message The message to broadcast.
     */
    public void sendStaffMessage(String message) {
        players.stream().filter(p -> !Objects.isNull(p) && p.getPlayerRights().isStaffMember(p)).forEach(p -> p.getPacketSender().sendMessage(message));
    }

    /**
     * Appearance for the amount of players inside the wilderness area
     *
     * @return players_in_wilderness
     */
    public int getPlayersInWild() {
        int players_in_wilderness = 0;

        for (Player player : BountyHunter.PLAYERS_IN_WILD) {
            if (player != null) {
                players_in_wilderness++;
            }
        }

        return players_in_wilderness;
    }

    public EntityList<Player> getPlayers() {
        return players;
    }

    public int getRegularPlayers() {
        int regular_players = 0;

        for (Player player : players) {
            if (player != null && !player.getPlayerRights().isStaffMember(player)) {
                regular_players++;
            }
        }

        return regular_players;
    }

    public NPC findNPC(int id) {
        Optional<NPC> toGet = npcs.stream().filter(n -> n != null).filter(n -> n.getId() == id).findFirst();
        if (!toGet.isPresent()) {
            System.err.println("couldn't find any npc for id=" + id);
            return null;
        }
        return toGet.get();
    }

    public EntityList<NPC> getNpcs() {
        return npcs;
    }

    public List<GameObject> getSpawnedObjs() {
        return spawnedObjs;
    }

    public List<GameObject> getRemovedObjs() {
        return removedObjs;
    }

    public LoginService ls = new LoginService();

    public int cycleCount() {
        return elapsedTicks;
    }

    public boolean registerNpc(NPC npc) {
        npcs.add(npc);
        Tile.occupy(npc);
        npc.spawnStack = new Throwable().getStackTrace()[1].toString();
        return true;
    }

    public void unregisterNpc(NPC npc) {
        npcs.remove(npc);
        Tile.unoccupy(npc);
        npc.setInstance(null);
    }

    private EquipmentInfo equipmentInfo;

    public EquipmentInfo equipmentInfo() {
        return equipmentInfo;
    }

    public void loadEquipmentInfo() {
        equipmentInfo = new EquipmentInfo(
            new File("data/list/equipment_info.json"),
            new File("data/list/renderpairs.txt"),
            new File("data/list/bonuses.json"),
            new File("data/list/weapon_types.txt"),
            new File("data/list/weapon_speeds.txt"));
    }

    public NPCCombatInfo combatInfo(int id) {
        return id > combatInfo.length - 1 ? null : combatInfo[id];
    }

    private NPCCombatInfo[] combatInfo;

    public void loadNpcCombatInfo() {
        combatInfo = new NPCCombatInfo[definitionRepository.total(NpcDefinition.class)];

        int total = 0;
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonPropertyValidator()).create();
        File defs = new File("data/combat/npc");
        for (File def : defs.listFiles()) {
            if (def.getName().endsWith(".json")) {
                NPCCombatInfo[] s = null;
                try {
                    s = gson.fromJson(new FileReader(def), NPCCombatInfo[].class);
                } catch (JsonParseException e) {
                    throw new RuntimeException("Failed to parse npc combat def: " + def.getAbsolutePath() + " (" + e.getMessage() + ")");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (s == null)
                    continue;

                int entryIndex = 0;
                for (NPCCombatInfo cbInfo : s) {
                    if (cbInfo == null)
                        continue;

                    if (cbInfo.ids == null) {
                        logger.error("Failed to parse entry {} in NPCCombatInfo {} due to missing field 'ids'", entryIndex, def.getName());
                        continue;
                    }

                    // Store original stats to restore after respawning.
                    cbInfo.originalStats = cbInfo.stats.clone();
                    cbInfo.originalBonuses = cbInfo.bonuses.clone();

                    // Resolve scripts
                    if (cbInfo.scripts != null) {
                        cbInfo.scripts.resolve();
                    }

                    // Insert the combat info reference into the array at the index respective to the concerning npc ids
                    for (int i : cbInfo.ids) {
                        combatInfo[i] = cbInfo;
                    }

                    entryIndex++;
                }
                total += s.length;
            }
        }
        logger.info("Loaded {} NPC combat info sheets.", total);
    }

    public void loadItemSpawns(File dir) {
        int total = 0;
        Gson gson = JGson.get();
        for (File spawn : dir.listFiles()) {
            if (spawn.getName().endsWith(".json")) {
                try {
                    ItemSpawn[] s = gson.fromJson(new FileReader(spawn), ItemSpawn[].class);
                    for (ItemSpawn sp : s) {
                        if (sp == null)
                            continue;

                        Tile spawnTile = new Tile(sp.x, sp.y, sp.level);
                        GroundItem item = new GroundItem(new Item(sp.id, sp.amount), spawnTile, null);
                        item.respawns(true).respawnTimer(sp.delay);
                        GroundItemHandler.createGroundItem(item);
                    }
                    total += s.length;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (spawn.isDirectory()) {
                loadItemSpawns(spawn);
            }
        }
        logger.info("Loaded {} item spawns.", total);
    }

    public void loadDrops() {
        ScalarLootTable.loadAll(new File("data/combat/drops"));
        System.out.println(ScalarLootTable.registered.size() + " loaded drops");
    }

    public static void loadNpcSpawns(File dir) {
        long start = System.currentTimeMillis();
        Gson gson = new Gson();

        for (File spawn : dir.listFiles()) {
            if (spawn.getName().endsWith(".json")) {
                try {
                    NpcSpawn[] s = gson.fromJson(new FileReader(spawn), NpcSpawn[].class);

                    for (NpcSpawn sp : s) {
                        if (sp == null)
                            continue;

                        Tile spawnTile = new Tile(sp.x, sp.y, sp.z);
                        NPC npc = NPC.of(sp.id, spawnTile);
                        npc.spawnDirection(sp.dir());
                        npc.walkRadius(sp.walkRange);
                        npc.ancientSpawn(sp.ancientSpawn);

                        if (npc.id() == SHANOMI) {
                            Shanomi.shoutMessage(npc);
                        }

                        // successfully added to game world
                        KrakenBoss.onNpcSpawn(npc);

                        if (npc.id() == NpcIdentifiers.VENENATIS_6610) {
                            npc.putAttrib(AttributeKey.MAX_DISTANCE_FROM_SPAWN, 30);
                        }

                        // Set the max return to spawnpoint distance for gwd room npcs
                        if (npc.def().gwdRoomNpc) {
                            npc.putAttrib(AttributeKey.MAX_DISTANCE_FROM_SPAWN, 40);
                        }

                        // successfully added to game world
                        World.getWorld().registerNpc(npc);
                    }
                } catch (JsonParseException e) {
                    throw new RuntimeException("Failed to parse npc spawn: " + spawn.getAbsolutePath() + " (" + e + ")");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (spawn.isDirectory()) {
                loadNpcSpawns(spawn);
            }
        }
        long elapsed = System.currentTimeMillis() - start;
        logger.info("  Loaded definitions for ./data/map/npcs. It took {}ms.", elapsed);
    }

    public void postLoad() {
        try {
            loadEquipmentInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loadNpcCombatInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loadNpcSpawns(new File("data/map/npcs"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Fishing.respawnAllSpots(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("Loaded {} NPC spawns.", npcs.size());

        try {
            loadItemSpawns(new File("data/map/items"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ItemWeight.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loadDrops();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int meleeClip(int absx, int absy, int z) {
        return clipInMap(absx, absy, z, false);
    }

    public int projectileClip(int absx, int absy, int z) {
        return clipInMap(absx, absy, z, true);
    }

    private int clipInMap(int x, int y, int z, boolean projectile) {
        return projectile ? RegionManager.getClippingProj(x, y, z) : RegionManager.getClipping(x, y, z);
    }

    /*
     * checks clip
     */
    public boolean canMoveNPC(int plane, int x, int y, int size) {
        for (int tileX = x; tileX < x + size; tileX++)
            for (int tileY = y; tileY < y + size; tileY++)
                if (getMask(plane, tileX, tileY) != 0)
                    return false;
        return true;
    }

    /*
     * checks clip
     */
    public boolean isNotCliped(int plane, int x, int y, int size) {
        for (int tileX = x; tileX < x + size; tileX++)
            for (int tileY = y; tileY < y + size; tileY++)
                if ((getMask(plane, tileX, tileY) & 2097152) != 0)
                    return false;
        return true;
    }

    public int getMask(int plane, int x, int y) {
        Tile tile = new Tile(x, y, plane);
        int regionId = tile.region();
        Region region = RegionManager.getRegion(regionId);
        if (region == null)
            return -1;
        int clip = region.getClip(x, y, tile.getLevel());
        //("gm %s,%s,%s = %s aka %s%n", x, y, tile.level, clip, clipstr(clip));
        return clip;
        // int baseLocalX = x - ((regionId >> 8) * 64);
        // int baseLocalY = y - ((regionId & 0xff) * 64);
    }

    public static String clipstr(final int clip) {
        StringBuilder sb = new StringBuilder();
        Flags.getMASKS().forEach((s, integer) -> {
            if ((clip & integer) != 0) {
                sb.append(s).append(",");
            }
        });
        return sb.toString().length() == 0 ? "none" : sb.toString();
    }

    public static String clipstrMethods(Tile tile) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RegionManager.blockedEast(tile) ? "E, " : "");
        stringBuilder.append(RegionManager.blockedNorth(tile) ? "N, " : "");
        stringBuilder.append(RegionManager.blockedSouth(tile) ? "S, " : "");
        stringBuilder.append(RegionManager.blockedWest(tile) ? "W, " : "");
        stringBuilder.append(RegionManager.blockedNorthEast(tile) ? "NE, " : "");
        stringBuilder.append(RegionManager.blockedNorthWest(tile) ? "NW, " : "");
        stringBuilder.append(RegionManager.blockedSouthEast(tile) ? "SE, " : "");
        stringBuilder.append(RegionManager.blockedSouthWest(tile) ? "SW, " : "");
        return "blocked in dirs: " + stringBuilder.toString();
    }

    public Tile randomTileAround(Tile base, int radius) {
        int[][] clip = clipSquare(base.transform(-radius, -radius, 0), radius * 2 + 1);

        for (int i = 0; i < 100; i++) {
            int x = Utils.RANDOM_GEN.nextInt(radius * 2 + 1), z = Utils.RANDOM_GEN.nextInt(radius * 2 + 1);
            if (clip[x][z] == 0) {
                return base.transform(x - radius, z - radius, 0);
            }
        }

        return base;
    }

    public int[][] clipAround(Tile base, int radius) {
        Tile src = base.transform(-radius, -radius, 0);
        return clipSquare(src, radius * 2 + 1);
    }

    public int[][] clipSquare(Tile base, int size) {
        int[][] clipping = new int[size][size];

        Region active = RegionManager.getRegion(base.region());
        int activeId = base.region();

        for (int x = base.x; x < base.x + size; x++) {
            for (int y = base.y; y < base.y + size; y++) {
                int reg = Tile.coordsToRegion(x, y);
                if (reg != activeId) {
                    activeId = reg;
                    active = RegionManager.getRegion(activeId);
                }

                if (active != null && active.clips[base.level % 4] != null)
                    clipping[x - base.x][y - base.y] = active.clips[base.level % 4][x & 63][y & 63];
            }
        }

        return clipping;
    }

    public int clipAt(int x, int z, int level) {
        return clipAt(new Tile(x, z, level));
    }

    public int clipAt(Tile tile) {
        Region active = RegionManager.getRegion(tile.region());
        return active.clips == null ? 0 : active.clips[tile.level % 4] == null ? 0 : active.clips[tile.level % 4][tile.x & 63][tile.y & 63];
    }

    public int floorAt(Tile tile) {
        Region active = RegionManager.getRegion(tile.region());
        return active.heightMap[tile.level % 4][tile.x & 63][tile.y & 63];
    }

    public void tileGraphic(int id, Tile tile, int height, int delay) {
        players.forEach(p -> {
            if (p.getZ() != tile.getZ()) return;
            // if (p.activeArea().contains(tile)) {
            p.getPacketSender().sendTileGraphic(id, tile, height, delay);
            //  }
        });
    }

    private final Random random = new SecureRandom();

    public Random random() {
        return random;
    }

    /**
     * @param i Maximum - INCLUSIVE!
     * @return Integer between 1 - MAX
     */
    public int random(int i) {
        if (i < 1) {
            return 0;
        }

        return random.nextInt(i + 1);
    }

    public int random(final int min, final int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n + 1));
    }

    public double randomDouble() {
        return random.nextDouble();
    }

    public <T> T random(T[] i) {
        return i[random.nextInt(i.length)];
    }

    public int random(int[] i) {
        return i[random.nextInt(i.length)];
    }

    public boolean rollDie(int dieSides, int chance) {
        return random(dieSides) < chance;
    }

    public boolean rollDie(int maxRoll) {
        return rollDie(maxRoll, 1);
    }

    public Optional<Player> getPlayerByUid(int userId) {
        return players.filter(plr -> plr.getIndex() == userId).findFirst();
    }

    public Player getPlayer(int userId, boolean onlineReq) {
        if (onlineReq) {
            for (Player player : players) {
                if (player != null && player.getIndex() == userId)
                    return player;
            }
        } else {
            for (Player player : players.entities) {
                if (player != null && player.getIndex() == userId)
                    return player;
            }
        }
        return null;
    }

    public Map<String, OwnedObject> getOwnedObjects() {
        return ownedObjects;
    }

    public void registerOwnedObject(OwnedObject object) {
        ownedObjects.put(object.getOwnerUID() + ":" + object.getIdentifier(), object);
    }

    public OwnedObject getOwnedObject(Player owner, String identifier) {
        return ownedObjects.get(owner.getIndex() + ":" + identifier);
    }

    public void deregisterOwnedObject(OwnedObject object) {
        ownedObjects.remove(object.getOwnerUID() + ":" + object.getIdentifier());
    }
}

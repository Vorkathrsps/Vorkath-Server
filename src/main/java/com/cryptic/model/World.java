package com.cryptic.model;

import com.cryptic.GameConstants;
import com.cryptic.GameServer;
import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.core.TimesCycle;
import com.cryptic.core.event.EventWorker;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.content.areas.burthope.warriors_guild.dialogue.Shanomi;
import com.cryptic.model.content.bountyhunter.BountyHunter;
import com.cryptic.model.content.minigames.MinigameManager;
import com.cryptic.model.content.presets.newpreset.PresetHandler;
import com.cryptic.model.content.skill.impl.fishing.Fishing;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.EntityList;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.NodeType;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss;
import com.cryptic.model.entity.events.star.StarEventTask;
import com.cryptic.model.entity.masks.impl.updating.NPCUpdating;
import com.cryptic.model.entity.masks.impl.updating.PlayerUpdating;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.NPCCombatInfo;
import com.cryptic.model.entity.npc.droptables.ItemRepository;
import com.cryptic.model.entity.npc.droptables.NpcDropRepository;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.PlayerPerformanceTracker;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.def.EquipmentLoader;
import com.cryptic.model.items.container.presets.PresetData;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.model.items.container.sounds.SoundLoader;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.OwnedObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.Flags;
import com.cryptic.model.map.region.Region;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.Direction;
import com.cryptic.network.codec.login.LoginService;
import com.cryptic.utility.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.SHANOMI;

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

    /**
     * The collection of active {@link Player}s.
     */
    private final EntityList<Player> players = new EntityList<>(GameConstants.PLAYERS_LIMIT);

    /**
     * The collection of active {@link NPC}s. Be careful when adding NPCs directly to the list without using the queue, try not to bypass the queue.
     */
    private final EntityList<NPC> npcs = new EntityList<>(GameConstants.NPCS_LIMIT);

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

    public WorldPerfTracker benchmark = new WorldPerfTracker();

    protected boolean checkIndex(int index, NodeType type) {
        return type == NodeType.PLAYER ? World.getWorld().getPlayers().get(index) != null : World.getWorld().getNpcs().get(index) != null;
    }

    public int getTickCount() {
        return elapsedTicks;
    }

    /**
     * Executes the game sequence.
     */
    public void sequence() {
        resetSection();
        shufflePlayerRenderOrder();

        readPlayerPackets();

        processTasks();

        processObjects();

        readNpcs();
        readPlayers();

        processEntityUpdating();

        flushEntities();

        incrementElapsedTicks();
    }

    /**
     * Resets the section array to false.
     */
    private void resetSection() {
        Arrays.fill(section, false);
    }

    /**
     * Shuffles the player render order if enabled in the server properties.
     */
    private void shufflePlayerRenderOrder() {
        if (GameServer.properties().enablePidShuffling) {
            long pidShuffleCounter = World.getWorld().random().nextInt(100);
            long pidIntervalTicks = GameServer.properties().pidIntervalTicks;
            if (pidShuffleCounter % pidIntervalTicks == 0) {
                players.shuffleRenderOrder();
            }
        }
    }

    /**
     * Processes all scheduled tasks.
     */
    private static void processTasks() {
        try {
            TaskManager.sequence();
            MinigameManager.onTick();
            StarEventTask.checkDepletionTask();
        } catch (Exception e) {
            logger.error("Error occurred while processing tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes all objects owned by the game.
     */
    private void processObjects() {
        for (OwnedObject object : ownedObjects.values()) {
            if (object != null) {
                try {
                    object.tick();
                } catch (Exception e) {
                    logger.error("Error occurred while processing object: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Reads packets from all connected players.
     */
    private void readPlayerPackets() {
        for (Player player : players) {
            if (player != null) {
                try {
                    player.getSession().read();
                    player.getSession().handleQueuedPackets();
                } catch (Exception e) {
                    logger.error("Error occurred while reading player packets: " + e.getMessage());
                    e.printStackTrace();
                    World.getWorld().getPlayers().remove(player);
                }
            }
        }
    }

    /**
     * Reads and processes all NPCs in the game.
     */
    private void readNpcs() {
        for (NPC npc : npcs) {
            if (npc != null && !npc.hidden() && checkIndex(npc.getIndex(), NodeType.NPC)) {
                try {
                    npc.sequence();
                    npc.inViewport(false);
                    npc.processed = true;
                } catch (Exception e) {
                    logger.error("Error occurred while processing NPC: " + e.getMessage());
                    e.printStackTrace();
                    World.getWorld().unregisterNpc(npc);
                }
            }
        }
    }

    /**
     * Reads and processes all players in the game.
     */
    private void readPlayers() {
        for (Player player : players) {
            if (player != null && checkIndex(player.getIndex(), NodeType.PLAYER)) {
                try {
                    player.sequence();
                    player.processed = true;
                } catch (Exception e) {
                    logger.error("Error occurred while processing player: " + e.getMessage());
                    e.printStackTrace();
                    World.getWorld().getPlayers().remove(player);
                }
            }
        }
    }

    /**
     * Processes game packet updates for all players and NPCs.
     */
    private void processEntityUpdating() {
        for (Player player : players) {
            if (player != null && checkIndex(player.getIndex(), NodeType.PLAYER)) {
                try {
                    PlayerUpdating.update(player);
                } catch (Exception e) {
                    logger.error("Error occurred while processing GPI: " + e.getMessage());
                    e.printStackTrace();
                    World.getWorld().getPlayers().remove(player);
                }
            }
        }
        for (Player player : players) {
            if (player != null && checkIndex(player.getIndex(), NodeType.PLAYER)) {
                try {
                    NPCUpdating.update(player);
                } catch (Exception e) {
                    logger.error("Error occurred while processing GPI: " + e.getMessage());
                    e.printStackTrace();
                    World.getWorld().getPlayers().remove(player);
                }
            }
        }
    }

    /**
     * Flushes all entities that need to be updated.
     */
    private void flushEntities() {
        flushNpcs();
        flushPlayers();
    }

    /**
     * Flushes all NPCs that need to be updated.
     */
    private void flushNpcs() {
        for (NPC npc : npcs) {
            if (npc != null && checkIndex(npc.getIndex(), NodeType.NPC)) {
                try {
                    npc.resetUpdating();
                    npc.clearAttrib(AttributeKey.CACHED_PROJECTILE_STATE);
                    npc.performance.reset();
                    npc.processed = false;
                } catch (Exception e) {
                    logger.error("Error occurred while flushing NPC: " + e.getMessage());
                    e.printStackTrace();
                    World.getWorld().unregisterNpc(npc);
                }
            }
        }
    }

    /**
     * Flushes all players that need to be updated.
     */
    private void flushPlayers() {
        for (Player player : players) {
            if (player != null && checkIndex(player.getIndex(), NodeType.PLAYER)) {
                try {
                    player.resetUpdating();
                    player.clearAttrib(AttributeKey.CACHED_PROJECTILE_STATE);
                    player.setCachedUpdateBlock(null);
                    player.getSession().flush();
                    player.perf.pulse();
                    player.processed = false;
                } catch (Exception e) {
                    logger.error("Error occurred while flushing player: " + e.getMessage());
                    e.printStackTrace();
                    World.getWorld().getPlayers().remove(player);
                }
            }
        }
    }

    /**
     * Increments the elapsed ticks counter.
     */
    private void incrementElapsedTicks() {
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
        Optional<NPC> toGet = npcs.stream().filter(Objects::nonNull).filter(n -> n.getId() == id).findFirst();
        if (toGet.isEmpty()) {
            System.err.println("couldn't find any npc for id=" + id);
            return null;
        }
        return toGet.get();
    }

    public EntityList<NPC> getNpcs() {
        return npcs;
    }

    public LoginService ls = new LoginService();

    public int cycleCount() {
        return elapsedTicks;
    }

    public boolean registerNpc(NPC npc) {
        npcs.add(npc);
        Tile.occupy(npc);
        npc.tile().getRegion().getNpcs().add(npc);
        npc.setNeedsPlacement(true);
        npc.spawnStack = new Throwable().getStackTrace()[1].toString();
        return true;
    }

    public void unregisterNpc(NPC npc) {
        npc.tile().getRegion().getNpcs().remove(npc);
        npcs.remove(npc);
        Tile.unoccupy(npc);
        if (npc.getInstancedArea() != null) npc.getInstancedArea().removeNpc(npc);
    }

    private com.cryptic.model.items.container.equipment.EquipmentInfo equipmentInfo;

    public com.cryptic.model.items.container.equipment.EquipmentInfo equipmentInfo() {
        return equipmentInfo;
    }

    public void loadEquipmentInfo() {
        equipmentInfo = new com.cryptic.model.items.container.equipment.EquipmentInfo(
            new File("data/combat/equipment/equipment_info.json"),
            new File("data/combat/weapons/animations/renderpairs.txt"),
            new File("data/combat/weapons/weapon_types.txt"));
    }

    public NPCCombatInfo combatInfo(int id) {
        return id > combatInfo.length - 1 ? null : combatInfo[id];
    }

    private NPCCombatInfo[] combatInfo;

    public void loadNpcCombatInfo() {
        combatInfo = new NPCCombatInfo[definitionRepository.total(NpcDefinition.class)];

        int total = 0;
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonPropertyValidator()).create();
        File defs = new File("data/combat/scriptloader");
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
        ItemRepository.load();
        NpcDropRepository.loadAll(new File("data/combat/drops"));
    }

    public static final ThreadLocal<Gson> gson = ThreadLocal.withInitial(Gson::new);

    public static void loadNpcSpawns(String dirPath) {
        long start = System.currentTimeMillis();

        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new AfterburnerModule());
        try (BufferedReader r = Files.newBufferedReader(Path.of(dirPath))) {
            NpcSpawn[] s = objectMapper.readValue(r, NpcSpawn[].class);
            for (NpcSpawn sp : s) {
                Tile spawnTile = new Tile(sp.x, sp.y, sp.z);
                NPC npc = NPC.of(sp.id, spawnTile);
                npc.spawnDirection(sp.dir());
                npc.walkRadius(sp.walkRange);

                if (npc.id() == SHANOMI) {
                    Shanomi.shoutMessage(npc);
                }

                // successfully added to game world
                KrakenBoss.onNpcSpawn(npc);

                if (npc.id() == NpcIdentifiers.VENENATIS_6610) {
                    npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
                }

                // Set the max return to spawnpoint distance for gwd room npcs
                if (npc.def().gwdRoomNpc) {
                    npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 40);
                }

                // successfully added to game world
                World.getWorld().registerNpc(npc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long elapsed = System.currentTimeMillis() - start;
        logger.info("Loaded World Npc Spawns. It took {}ms.", elapsed);
    }

    @Getter
    EquipmentLoader equipmentLoader = new EquipmentLoader();
    @Getter
    SlayerTask slayerTasks = new SlayerTask();
    @Getter
    SoundLoader soundLoader = new SoundLoader();

    public void postLoad() {
        try {
            loadEquipmentInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PresetHandler.defaultKits = PresetData.loadDefaultPresets(new File("data/combat/preset/presets.json")).toArray(new PresetData[0]);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {
            equipmentLoader.loadEquipmentDefinitions(new File("data/combat/equipment/bonuses.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            slayerTasks.loadSlayerTasks(new File("data/combat/slayer/SlayerTask.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            soundLoader.loadWeaponSounds(new File("data/combat/combatsounds/weapon_sounds.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            soundLoader.loadSpellSounds(new File("data/combat/combatsounds/spell_sounds.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            DynamicClassLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loadNpcCombatInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loadNpcSpawns("data/def/npcs/worldspawns/npc_spawns.json");
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
            loadItemSpawns(new File("data/def/items/worldspawns/"));
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
        int clip = region.getClip(x, y, tile.getLevel());
        return clip;
    }

    public static int getMasks(final int plane, final int x, final int y) {
        final int regionId = (((x & 16383) >> 6) << 8) | ((y & 16383) >> 6);
        final Region region = RegionManager.regions.get(regionId);
        return region.getClip(plane & 3, x & 63, y & 63);
    }

    public static String clipstr(final int clip) {
        StringBuilder sb = new StringBuilder();
        Flags.getMASKS().forEach((s, integer) -> {
            if ((clip & integer) != 0) {
                sb.append(s).append(",");
            }
        });
        return sb.toString().isEmpty() ? "none" : sb.toString();
    }

    public static String clipstrMethods(Tile tile) {
        String stringBuilder = (RegionManager.blockedEast(tile) ? "E, " : "") +
                (RegionManager.blockedNorth(tile) ? "N, " : "") +
                (RegionManager.blockedSouth(tile) ? "S, " : "") +
                (RegionManager.blockedWest(tile) ? "W, " : "") +
                (RegionManager.blockedNorthEast(tile) ? "NE, " : "") +
                (RegionManager.blockedNorthWest(tile) ? "NW, " : "") +
                (RegionManager.blockedSouthEast(tile) ? "SE, " : "") +
                (RegionManager.blockedSouthWest(tile) ? "SW, " : "");
        return STR."blocked in dirs: \{stringBuilder}";
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

                if (active.baseZData.clips[base.level % 4] != null)
                    clipping[x - base.x][y - base.y] = active.baseZData.clips[base.level % 4][x & 63][y & 63];
            }
        }

        return clipping;
    }

    public int clipAt(int x, int z, int level) {
        return clipAt(new Tile(x, z, level));
    }

    public int clipAt(Tile tile) {
        Region active = RegionManager.getRegion(tile.region());
        return active.baseZData.clips == null ? 0 : active.baseZData.clips[tile.level % 4] == null ? 0 : active.baseZData.clips[tile.level % 4][tile.x & 63][tile.y & 63];
    }

    public int floorAt(Tile tile) {
        Region active = RegionManager.getRegion(tile.region());
        return active.heightMap[tile.level % 4][tile.x & 63][tile.y & 63];
    }

    public void syncTileGraphic(int id, Tile tile, int height, int delay) {
        players.forEach(p -> {
            if (p.getZ() != tile.getZ()) return;
            // if (p.activeArea().contains(tile)) {
            p.getPacketSender().sendTileGraphic(id, tile, height, delay);
            //  }
        });
    }

    public void tileGraphic(int id, Tile tile, int height, int delay) {
        players.forEach(p -> {
            if (p.getZ() != tile.getZ()) return;
            if (!p.tile().isViewableFrom(tile)) return;
            if (World.getWorld().clipAt(tile) != 0) return;
            p.getPacketSender().sendTileGraphic(id, tile, height, delay);
        });
    }

    public Random random() {
        return Utils.THREAD_LOCAL_RANDOM.get();
    }

    /**
     * @param i Maximum - INCLUSIVE!
     * @return Integer between 1 - MAX
     */
    public int random(int i) {
        if (i < 1) {
            return 0;
        }

        return this.random().nextInt(i + 1);
    }

    public int random(final int min, final int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : this.random().nextInt(n + 1));
    }

    public double randomDouble() {
        return this.random().nextDouble();
    }

    public <T> T random(T[] i) {
        return i[this.random().nextInt(i.length)];
    }

    public int random(int[] i) {
        return i[this.random().nextInt(i.length)];
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
            for (Player player : players.entities.values()) {
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

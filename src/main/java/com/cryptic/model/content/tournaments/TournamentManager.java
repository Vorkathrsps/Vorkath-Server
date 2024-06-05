package com.cryptic.model.content.tournaments;

import com.cryptic.GameEngine;
import com.cryptic.GameServer;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.weapon.WeaponInterfaces;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.container.inventory.Inventory;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;
import com.typesafe.config.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.LISA;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.EXIT_PORTAL_27096;
import static com.cryptic.model.content.tournaments.TournamentUtils.*;
import static java.lang.String.format;

/**
 * A tournament system. Configured via /data/def/arcutus/tournaments.conf file
 * <br>
 * You can start tournys via commands ::settornhours 05.50,05.51 etc {@link com.ferox.game.world.entity.mob.player.commands.impl.dev.SetTornLobbyTime}
 * <br>
 * ::settornlobbytime 5 {@link com.ferox.game.world.entity.mob.player.commands.impl.dev.SetTornLobbyTime}
 * <br>
 * to start a tourny right away use ::cia2
 *
 * @author Shadowrs (tardisfan121@gmail.com)
 */
@SuppressWarnings("ALL")
public class TournamentManager extends PacketInteraction {

    public static void revertStats(Player player) {
        Map<Player, Map<double[], int[]>> skillMap = player.getParticipatingTournament().playerSkillMap;
        for (Map.Entry<Player, Map<double[], int[]>> map : skillMap.entrySet()) {
            Player hashedPlayer = map.getKey();
            Map<double[], int[]> skillCache = map.getValue();
            if (!hashedPlayer.equals(player)) continue;
            for (Map.Entry<double[], int[]> entry : skillCache.entrySet()) {
                double[] xp = entry.getKey();
                int[] lvl = entry.getValue();
                hashedPlayer.skills().restoreLevels(xp, lvl);
            }
        }
        Skills skills = player.skills();
        skills.update();
        skills.recalculateCombat();
        player.setSavedTornamentXp(null);
        player.setSavedTornamentLevels(null);
        skillMap.remove(player);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject gameObject, int option) {
        if (option == 1) {
            //You can only leave if you have no tournament opponent
            if (gameObject.getId() == EXIT_PORTAL_27096 && player.getTournamentOpponent() != null) {
                player.message(Color.RED.wrap("The only way out is death!"));
                return true;
            } else if (gameObject.getId() == EXIT_PORTAL_27096 && player.getTournamentOpponent() == null) {
                player.debugMessage("lets leave lobby");
                leaveLobby(player);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == THORVALD || npc.id() == LISA) {
            if (option == 1) {
                //player.getDialogueManager().start(new TournamentEntryDialogue());
                TournamentManager.openTournamentWidget(player);
                return true;
            } else if (option == 2) {
                TournamentManager.quickJoinLobby(player);
                return true;
            } else if (option == 3) {
                TournamentManager.quickSpectate(player);
                return true;
            }
        }
        return false;
    }

    private static final Object LOCK = new Object();
    private static final Logger logger = LogManager.getLogger(TournamentManager.class);
    private static String nextTime = "";

    /**
     * List of all in progress or pending tournaments.
     */
    private final static List<Tournament> tournaments = new ArrayList<>(0);

    /**
     * The tournament which the current open waiting room is for.
     */
    private static Tournament waitingRoomTournament;

    /**
     * Should all hell break loose and it needs to be temporarily disabled
     * We can set this in properties.json, we do the opposite (!) of tournamentsEnabled for setting tournamentsDisabled.
     */
    private static boolean tournamentsDisabled = !GameServer.properties().tournamentsEnabled;

    private final static List<WinnerHistory> winnerHistories = new ArrayList<>(0);

    private static Tournament nextTorn;

    private static Tournament prevTorn;

    public static String nextTornStartsInMessage() { // where the fuck did i leave that print
        long difference = timeTillNext();
        if (difference == -1)
            return "Check back at midnight for the next tournament time.";

        String timeLeft = format("Tournament will open in %s", difference >= 3600 ? difference / 3600 + " hour(s)" : difference >= 60 ? difference / 60 + " minute(s)" : difference + " second(s)");
        return timeLeft;
    }

    private static int contendersLeft() {
        //Get the current tournament
        Tournament tournament = tournaments.get(0);
        return tournament.getFighters().size();
    }

    public static long timeTillNext() {
        final ZonedDateTime next = nextEvent();
        if (next == null)
            return -1;
        return next.toEpochSecond() - nowSec;
    }

    static boolean gameInProgress() {
        for (Tournament tournament : tournaments) {
            if (tournament.inProgress()) {
                return true;
            }
        }
        return false;
    }

    static String inProgressNpcMessage() {
        Tournament torn = tournaments.get(0); // can adapt for multiple running tournaments in future
        return format("A %s tournament is in progress. Would you like to spectate?", torn.getTypeName());
    }

    public static boolean teleportBlocked(Player player, boolean sendMessage) {
        if (player.getTournamentOpponent() != null) {
            if (sendMessage)
                player.message("You can't teleport out of a tournament");
            return true;
        }
        return false;
    }

    static boolean canEnterLobby(Player player) {
        if (player.getPetEntity().getEntity() != null) {
            player.message("You can't bring any pets into the tournament.");
            return false;
        }
        return true;
    }

    public static void leaveLobby(Player player) {
        final Tournament t = player.getParticipatingTournament();
        if (t == null)
            return;
        leaveTourny(player, false);
        player.message(format("You've left the lobby of the %s tournament.", t.getConfig().key));
    }

    public static void onLogin1(Player player) {
        boolean insideTournyArea = player.tile().region() == 13133;
        if (insideTournyArea) {
            //Wipe loadout
            wipeLoadout(player);
            //We're no longer participating
            player.setInTournamentLobby(false);
            player.setParticipatingTournament(null);
            //Clear rune pouch
            player.getRunePouch().clear();
            //Reset attributes
            player.getPacketSender().sendInteractionOption("null", 2, true); //Remove attack option
            player.getPacketSender().sendEntityHintRemoval(true);
            //Exit the area
            player.teleport(EXIT_TILE);
        }
    }

    public static void leaveTourny(Player player, boolean logout) {
        leaveTourny(player, logout, false);
    }

    public static void leaveTourny(Player player, boolean logout, boolean teleport) {
        final Tournament torn = player.getParticipatingTournament();
        if (torn == null) return;
        revertStats(player);
        if (player.isInTournamentLobby()) {
            wipeLoadout(player);
            torn.inLobby.remove(player);
            player.setInTournamentLobby(false);
            player.getRunePouch().clear();
            player.getPacketSender().sendInteractionOption("null", 2, true); //Remove attack option
            player.getPacketSender().sendEntityHintRemoval(true);
            if (teleport) {
                player.teleport(EXIT_TILE);
            }
            player.setParticipatingTournament(null);
            return;
        }
        if (player.inActiveTournament()) {
            wipeLoadout(player);
            if (torn.fighters.contains(player)) {
                torn.fighters.remove(player);
                if (torn.winner == player) {
                    torn.winner.inventory().add(torn.reward.copy());
                    torn.winner.message("Reward: " + torn.reward.getAmount() + " x " + torn.reward.name() + ".");
                    Utils.sendDiscordInfoLog("Player " + player.getUsername() + " received a tournament reward: " + torn.reward.getAmount() + " x " + torn.reward.name() + ".", "tournaments");
                    if (torn.reward != null) {
                        String rewardX = "" + torn.reward.getAmount(), rewardName = torn.reward.unnote().name();
                        TournamentManager.getWinnerHistories().add(new TournamentManager.WinnerHistory(player.getUsername(), rewardName, rewardX, torn.fullName(), System.currentTimeMillis()));
                    } else {
                        torn.winner.message("This tournament had no rewards!");
                        TournamentManager.getWinnerHistories().add(new TournamentManager.WinnerHistory(player.getUsername(), "nothing", "1", torn.fullName(), System.currentTimeMillis()));
                    }
                }
            }
            if (torn.spectators.contains(player)) {
                torn.spectators.remove(player);
                if (player.isTournamentSpectating())
                    player.looks().transmog(-1);
                player.setTournamentSpectating(false);
            }
            player.setQueuedAppearanceUpdate(true);
            Player opp = player.getTournamentOpponent();
            if (opp != null && !opp.dead()) {
                opp.getCombat().getHitQueue().clear();
                opp.message(logout ? "Your opponent has disconnected. You win the round!" : "Your opponent has forfeit. You win the round!");
                torn.checkForWinner();
                torn.resetAllVars(opp);
                opp.setTournamentOpponent(null);
            }
            player.setTournamentOpponent(null);
            player.setParticipatingTournament(null);
            // TODO skill restore
            player.getPrayer().clear();
            restorePreTournyState(player, torn);
            player.getPacketSender().sendInteractionOption("null", 2, true); //Remove attack option
            player.getPacketSender().sendEntityHintRemoval(true);
            Tile tile = GameServer.settings().getHomeTile();
            player.teleport(tile);
        } else {
            logger.error(player.getUsername() + " tried to leave but isn't in a torny");
        }
    }

    /**
     * note: rune pouch is not restored because you it must be empty before entering.
     */
    private static void restorePreTournyState(Player player, Tournament t) {
        TournamentManager.Loadout loadout = TournamentManager.loadouts.stream().filter(l -> l.key.equalsIgnoreCase(t.getTypeName())).findFirst().orElse(null);
        if (loadout == null) {
            logger.error("no loadout exists for tournaments loadout key " + t.getTypeName() + "!");
            loadout = TournamentManager.loadouts.get(0);
        }

        MagicSpellbook.changeSpellbook(player, player.getPreviousSpellbook() == null ? MagicSpellbook.NORMAL : player.getPreviousSpellbook(), false);
        player.setPreviousSpellbook(null);
    }

    static void joinSpectating(Player player, Tournament tournament) {
        if (player == null)
            return;
        if (tournament == null)
            return;
        bankEverything(player); // only way to join spectating as a participant is death, which deletes the loadout. this is safe.
        player.setParticipatingTournament(tournament);
        player.looks().transmog(GHOST_ID);
        player.message("You've become a ghost as a spectator. Players still fighting cannot see you.");
        player.setTournamentSpectating(true);
        tournament.getSpectators().add(player);
        player.teleport(TORN_START_TILE);
        player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_WALK_TIMER, "Spectating");
        player.getInterfaceManager().close();
    }

    public static boolean handleDeath(Player player) {
        if (!player.inActiveTournament())
            return false;
        Player killer = player.getTournamentOpponent();
        Tournament torn = player.getParticipatingTournament();
        wipeLoadout(player);
        torn.fighters.remove(player);
        player.setTournamentOpponent(null);
        player.getPacketSender().sendInteractionOption("null", 2, true);
        player.getPacketSender().sendEntityHintRemoval(true);
        joinSpectating(player, torn);
        switch (torn.fighters.size()) {
            case 1 -> {
                var points = player.<Integer>getAttribOr(AttributeKey.TOURNAMENT_POINTS, 0);
                points += 3;
                player.putAttrib(AttributeKey.TOURNAMENT_POINTS, points);
                player.message("You've received 3 tournament point! You now have " + Color.BLUE.wrap("" + points) + " tournament points.");
            }
            case 2 -> {
                var points = player.<Integer>getAttribOr(AttributeKey.TOURNAMENT_POINTS, 0);
                points += 2;
                player.putAttrib(AttributeKey.TOURNAMENT_POINTS, points);
                player.message("You've received 2 tournament point! You now have " + Color.BLUE.wrap("" + points) + " tournament points.");
            }
            default -> {
                var points = player.<Integer>getAttribOr(AttributeKey.TOURNAMENT_POINTS, 0);
                points += 1;
                player.putAttrib(AttributeKey.TOURNAMENT_POINTS, points);
                player.message("You've received 1 tournament point! You now have " + Color.BLUE.wrap("" + points) + " tournament points.");
            }
        }
        if (killer != null && torn != null) {
            killer.setTournamentOpponent(null);
            if (torn.fighters.size() > 1) killer.message("The next round of battles will start when the current round has finished.");
            killer.getParticipatingTournament().resetAllVars(killer);
            killer.getPacketSender().sendEntityHintRemoval(true);
            torn.checkForWinner();
        }
        return true;
    }

    static void wipeLoadout(Player player) {
        final Tournament t = player.getParticipatingTournament();
        if (t != null)
            t.resetAllVars(player);
        player.inventory().clear();
        player.getEquipment().clear();
        player.getRunePouch().clear();
        player.setQueuedAppearanceUpdate(true);
        WeaponInterfaces.updateWeaponInterface(player);
    }

    /**
     * @return true if the waiting room instance exists
     */
    static boolean lobbyActive() {
        return waitingRoomTournament != null;
    }

    /**
     * Attempts to insert all inventory, equipment, rune pouch items into the players bank.
     * Picks up the pet and also banks it.
     */
    static void bankEverything(Player player) {
        player.getBank().depositInventory();
        player.getBank().depositeEquipment();
        if (player.getPetEntity().getEntity() != null) {
            player.getPetEntity().clearSpawnedEntity(player);
        }
        //We don't have to bank runes, since they are spawnable
        //player.getRunePouch().bankRunesFromNothing();
        player.getRunePouch().clear();
        player.getBank().depositInventory();
        player.inventory().refresh();
        player.getEquipment().refresh();
        player.setQueuedAppearanceUpdate(true);
        WeaponInterfaces.updateWeaponInterface(player);
    }

    /*
     * configuration classes
     */
    public static TornSystemSettings settings;
    final static List<Loadout> loadouts = new ArrayList<>(0);

    public static boolean canAttack(Entity attacker, Entity target) {
        if (attacker.isPlayer() && attacker.getAsPlayer().getParticipatingTournament() != null) {
            if (target.isPlayer() && target.getAsPlayer().getTournamentOpponent() == attacker && target.getTimers().has(TimerKey.TOURNAMENT_FIGHT_IMMUNE)) {
                attacker.getAsPlayer().message("The round has not begun yet.");
                return false;
            }
            if (attacker.getAsPlayer().isInTournamentLobby()) {
                attacker.getAsPlayer().message("The Tournament has not begun yet.");
                return false;
            }
            return target.isPlayer() && target.getAsPlayer().getTournamentOpponent() == attacker;
        }
        return true;
    }

    private static void quickSpectate(Player player) {
        if (TournamentManager.isTournamentsDisabled()) {
            player.message("The tournament system is currently disabled. Try again later to find out if it is open.");
        } else if (!TournamentManager.gameInProgress()) {
            player.message("The tournament hasn't started yet.");
        } else if (!TournamentManager.canEnterLobby(player)) {
            player.message("You do not meet all the requirements to enter the lobby.");
        } else if (canEnterLobby(player)) {
            joinSpectating(player, tournaments.get(0));
        }
    }

    private static void quickJoinLobby(Player player) {
        if (TournamentManager.isTournamentsDisabled()) {
            player.message("The tournament system is currently disabled. Try again later to find out if it is open.");
        } else if (TournamentManager.gameInProgress()) {
            //System.out.println("Game in progress");
            player.message(TournamentManager.inProgressNpcMessage());
        } else if (!TournamentManager.lobbyActive()) {
            player.message(TournamentManager.nextTornStartsInMessage());
        } else {
            if (TournamentManager.getWaitingRoomTournament().lobbyFull()) {
                player.message(TournamentManager.getWaitingRoomTournament().lobbyFullMessage());
            } else if (!TournamentManager.canEnterLobby(player)) {
                player.message("You do not meet all the requirements to enter the lobby.");
            } else {
                TournamentManager.getWaitingRoomTournament().enterLobby(player);
            }
        }
    }

    public static boolean canTrade(Player player, Player p2) {
        if (player.getParticipatingTournament() != null || p2.getParticipatingTournament() != null) {
            player.message("You can't trade in tournaments.");
            return false;
        }
        return true;
    }

    public static boolean canPickupItem(Player player, @SuppressWarnings("unused") GroundItem groundItem) {
        return player.getParticipatingTournament() == null;
    }

    public static boolean stopDamage(Player player) {
        if (player.getParticipatingTournament() != null) {
            if (player.getTournamentOpponent() == null || (player.getTournamentOpponent().dead()))
                return true;
        }
        return false;
    }

    public static List<Tournament> getTournaments() {
        return TournamentManager.tournaments;
    }

    public static Tournament getWaitingRoomTournament() {
        return TournamentManager.waitingRoomTournament;
    }

    public static boolean isTournamentsDisabled() {
        return TournamentManager.tournamentsDisabled;
    }

    public static List<WinnerHistory> getWinnerHistories() {
        return TournamentManager.winnerHistories;
    }

    public static Tournament getNextTorn() {
        return TournamentManager.nextTorn;
    }

    public static TornSystemSettings getSettings() {
        return TournamentManager.settings;
    }

    public static void setWaitingRoomTournament(Tournament waitingRoomTournament) {
        TournamentManager.waitingRoomTournament = waitingRoomTournament;
    }

    public static void setTournamentsDisabled(boolean tournamentsDisabled) {
        TournamentManager.tournamentsDisabled = tournamentsDisabled;
    }

    public static void setNextTorn(Tournament nextTorn) {
        TournamentManager.nextTorn = nextTorn;
    }

    public static void setSettings(TornSystemSettings settings) {
        TournamentManager.settings = settings;
    }

    /*
     * Data classes
     */
    public static class TornSystemSettings {
        public static String[] defaultStartTimes;
        int globalTimerSecs, lobbyTime;

        public TornConfig[] getTornConfigs() {
            return tornConfigs;
        }

        final TornConfig[] tornConfigs; // hybrid, nhpure, dharok etc

        public String[] getStartTimes() {
            return startTimes;
        }

        public boolean usingOverrideTimes;

        public void setStartTimes(String[] startTimes) {
            this.startTimes = startTimes;
        }

        public void setLobbyTime(int lobbyTime) {
            this.lobbyTime = lobbyTime;
        }

        private String[] startTimes;

        public TornSystemSettings(int globalTimerSecs, int lobbyTime, TornConfig[] tornConfigs, String[] startTimes) {
            this.globalTimerSecs = globalTimerSecs;
            this.lobbyTime = lobbyTime;
            this.tornConfigs = tornConfigs;
            this.startTimes = startTimes;
        }

        public String toString() {
            return "TournamentManager.TornSystemSettings(globalTimerSecs=" + this.globalTimerSecs + ", lobbyTime=" + this.lobbyTime + ", tornConfigs=" + Arrays.deepToString(this.tornConfigs) + ", startTimes=" + Arrays.deepToString(this.startTimes) + ")";
        }
    }

    public static class TornConfig {
        public final String key;
        public final boolean enabled, prayers;

        public TornConfig(String key, boolean enabled, boolean prayers) {
            this.key = key;
            this.enabled = enabled;
            this.prayers = prayers;
        }

        public String toString() {
            return "TournamentManager.TornConfig(key=" + this.key + ", enabled=" + this.enabled + ", prayers=" + this.prayers + ")";
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public boolean canUseOverheadPrayers() {
            return this.prayers;
        }
        // loadouts can be accessed via key
    }

    static class Loadout {
        final String key;
        final int spellbook;
        final ItemContainer inv, equip, runepouch;
        final List<Tuple<Integer, Integer>> stats;

        public Loadout(String key, int spellbook, ItemContainer inv, ItemContainer equip, ItemContainer runepouch, List<Tuple<Integer, Integer>> stats) {
            this.key = key;
            this.spellbook = spellbook;
            this.inv = inv;
            this.equip = equip;
            this.runepouch = runepouch;
            this.stats = stats;
        }

        public String toString() {
            return "TournamentManager.Loadout(key=" + this.key + ", spellbook=" + this.spellbook + ", inv=" + this.inv + ", equip=" + this.equip + ", runepouch=" + this.runepouch + ", stats=" + this.stats + ")";
        }
    }

    public static final class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "TournamentManager.Tuple(x=" + this.x + ", y=" + this.y + ")";
        }
    }

    public static final class WinnerHistory {
        final String username, rewardName, rewardAmt, tornName;
        final long time;

        public WinnerHistory(String username, String rewardName, String rewardAmt, String tornName, long time) {
            this.username = username;
            this.rewardName = rewardName;
            this.rewardAmt = rewardAmt;
            this.tornName = tornName;
            this.time = time;
        }

        public String toString() {
            return "TournamentManager.WinnerHistory(username=" + this.username + ", rewardName=" + this.rewardName + ", rewardAmt=" + this.rewardAmt + ", tornName=" + this.tornName + ", time=" + this.time + ")";
        }
    }

    public static void initalizeTournaments() {
        loadTournamentConfigFile();
        cancelAllTournaments();
        startTournamentTask();
    }

    public static void startTournamentTask() {
        TaskManager.submit(new TournamentCycleTask().bind(LOCK)); // like this? idk how your locks work I have no idea i didnt add that didnt even have
        TaskManager.submit(new TournamentLobbyTask().bind(LOCK)); // not a sync related lock, poor naming if i did it, its the task owner
    }

    /**
     * Loads settings from torn.conf and initializes the {@link TournamentCycle}
     */
    public static void loadTournamentConfigFile() {
        final File file = new File("data/combat/tournaments/tournaments.conf");
        if (!file.exists()) {
            try {
                throw new FileNotFoundException("missing tournaments.conf!");
            } catch (FileNotFoundException e) {
                logger.catching(e);
            }
        }
        logger.info("Config path: {}", file.getAbsolutePath());
        Config config = ConfigFactory.systemProperties().withFallback(ConfigFactory.parseFileAnySyntax(file));

        int systemTimerSecs = config.getInt("global_timer_check_every_seconds");
        String[] startTimes = config.getStringList("start_times").toArray(new String[0]);
        int lobbyTime = config.getInt("lobby_timer_secs");
        //Set lobby time to 20 seconds if we're in development mode.
        if (!GameServer.properties().production) {
            lobbyTime = 20;
        }

        List<TornConfig> configs = new ArrayList<>(0);

        ConfigList tornTypes = config.getList("torn_types");
        for (ConfigValue o : tornTypes) {
            Config object = ((ConfigObject) o).toConfig();
            String key = object.getString("key");
            boolean enabled = object.getBoolean("enabled");
            boolean overheadprayers = object.getBoolean("overheadprayers");
            if (enabled) {
                configs.add(new TornConfig(key, enabled, overheadprayers));
            }
        }
        settings = new TornSystemSettings(systemTimerSecs,
            lobbyTime,
            configs.toArray(new TornConfig[0]), startTimes
        );
        settings.defaultStartTimes = startTimes;
        loadouts.clear();

        ConfigList loadoutList = config.getList("loadouts");
        for (ConfigValue o : loadoutList) {
            Config object = ((ConfigObject) o).toConfig();
            String key = object.getString("key");
            int spellbook = object.getInt("spellbook");

            ItemContainer inv = new ItemContainer(Inventory.SIZE, ItemContainer.StackPolicy.STANDARD);
            ConfigList invList = object.getList("inv");
            for (ConfigValue o2 : invList) {
                Config v = ((ConfigObject) o2).toConfig();
                inv.getItems()[v.getInt("slot")] = new Item(v.getInt("item"), v.getInt("amount"));
            }

            ItemContainer equip = new ItemContainer(Equipment.SIZE, ItemContainer.StackPolicy.STANDARD);
            ConfigList equipList = object.getList("equip");
            for (ConfigValue o2 : equipList) {
                Config v = ((ConfigObject) o2).toConfig();
                equip.getItems()[v.getInt("slot")] = new Item(v.getInt("item"), v.getInt("amount"));
            }

            final List<Tuple<Integer, Integer>> statList = new ArrayList<>(0);
            ConfigList stats = object.getList("stats");
            for (ConfigValue o2 : stats) {
                Config v = ((ConfigObject) o2).toConfig();
                statList.add(new Tuple<>(v.getInt("id"), v.getInt("level")));
            }

            ItemContainer pouch = new ItemContainer(3, ItemContainer.StackPolicy.STANDARD);
            ConfigList rpList = object.getList("runepouch");
            for (ConfigValue o2 : rpList) {
                Config v = ((ConfigObject) o2).toConfig();
                pouch.getItems()[v.getInt("slot")] = new Item(v.getInt("item"), v.getInt("amount"));
            }
            TournamentManager.loadouts.add(new Loadout(key, spellbook, inv, equip, pouch, statList));
        }
    }

    public static void cancelAllTournaments() {
        //System.out.println("Cancel all tournaments.");
        waitingRoomTournament = null;
        for (Tournament tournament : tournaments) {
            List<Player> temp = new ArrayList<>(0);
            temp.addAll(tournament.getInLobby());
            temp.addAll(tournament.getSpectators());
            temp.addAll(tournament.getFighters());
            for (Player player : temp) {
                TournamentManager.leaveTourny(player, false);
            }
        }
        tournaments.clear();
        nextTorn = null;
        nextEvent = null;
        prevTorn = null;
        //System.out.println("Key is " + LOCK);
        TaskManager.cancelTasks(LOCK);
    }

    public static class TournamentLobbyTask extends Task {
        public TournamentLobbyTask() {
            super("TournamentLobbyTask", 1);
        }

        @Override
        protected void execute() {
            for (Iterator<Tournament> it = tournaments.iterator(); it.hasNext(); ) {
                final Tournament next = it.next();

                moveLobbyPlayersToGame(next);

                if ((System.currentTimeMillis() - next.lobbyOpenTimeMs) / 1000 >= TournamentManager.getSettings().lobbyTime) {
                    return;
                }
                if (next.inLobby.size() < next.minimumParticipants) {
                    return;
                }
                if (!next.finished() && next.fighters.size() == 0 && next.inLobby.size() > 0) {
                    for (Player player : next.inLobby) {
                        int time = (int) (TournamentManager.getSettings().lobbyTime - (System.currentTimeMillis() - next.lobbyOpenTimeMs) / 1000);
                        player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_WALK_TIMER, "Starting in " + time + "...");
                    }
                }
            }
        }
    }

    private static void moveLobbyPlayersToGame(Tournament next) {
        if (!next.finished() && next.fighters.size() == 0 && next.inLobby.size() > 0) {
            if ((System.currentTimeMillis() - next.lobbyOpenTimeMs) / 1000 >= TournamentManager.getSettings().lobbyTime) {
                // start!
                if (next.inLobby.size() < next.minimumParticipants) {
                    next.lobbyOpenTimeMs = System.currentTimeMillis();
                    for (Player player : next.inLobby) {
                        player.message("The tournament requires at least " + next.minimumParticipants + " players to start. It will begin again in " + next.lobbyTimeMessage());
                    }
                } else {
                    for (Iterator<Player> iterator = next.inLobby.iterator(); iterator.hasNext(); ) {
                        final Player player = iterator.next();
                        next.fighters.add(player);
                        iterator.remove();
                        player.setInTournamentLobby(false);
                        player.teleport(TORN_START_TILE);
                        player.message("You have been teleported to the combat area!");
                        Utils.sendDiscordInfoLog("Player " + player.getUsername() + " been teleported to the combat area!.", "tournaments");
                        player.getMovementQueue().clear();
                        player.getTimers().extendOrRegister(TimerKey.TOURNAMENT_FIGHT_IMMUNE, TournamentUtils.FIGHT_IMMUME_TIMER);
                        player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_WALK_TIMER, "00:30");
                        player.getPacketSender().sendInteractionOption("Attack", 2, true);
                    }
                    next.checkForNextRoundStart();
                    //logger.info(next.getTypeName() + " tournament has started.");
                    TournamentManager.setWaitingRoomTournament(null); // stops joining
                    //logger.trace("tournament started");
                }
            }
        }
    }

    public static ZonedDateTime now = ZonedDateTime.now();
    public static long nowSec = now.toEpochSecond();

    /**
     * Running every 20 seconds, handles the tournament system
     */
    public static class TournamentCycleTask extends Task implements Runnable {

        public TournamentCycleTask() {
            //A delay of 8 ticks seems reasonable here.
            super("TournamentCycleTask", 8);
        }

        @Override
        public void run() {
            process();
        }

        private void process() {
            now = ZonedDateTime.now();
            nowSec = now.toEpochSecond();
            //System.out.printf("Core openEventLobby: %s lobbyActive: %s tournaments.size: %s timeTillNext: %s | startTimes: %s nextTorn: %s %s%n", openEventLobby(), lobbyActive(), tournaments.size(), timeTillNext(), Arrays.toString(settings.startTimes), nextTorn != null ? nextTorn.getConfig().key : "none", nextEvent());
            setNextTournyType();
            if (checkAndOpenLobby(false)) return;
            tickTournys();
        }

        /**
         * Performs this task's action.
         */
        @Override
        protected void execute() {
            run();
        }
    }

    private static void tickTournys() {
        for (Iterator<Tournament> it = tournaments.iterator(); it.hasNext(); ) {
            final Tournament next = it.next();
            next.tick();
            if (next.finished()) {
                next.onTournyClosed();
                it.remove();
            }
        }
    }

    public static void setNextTournyType() {
        if (nextEvent != null) {
            if (nowSec >= nextEvent.toEpochSecond()) // gone past, reset for next
                nextEvent = null;
        }
        final ZonedDateTime nextEvent = nextEvent();
        if (nextEvent != null && nextTorn == null) {

            // tomorrow, was using overrides, lets reset
            if (nextEvent.getDayOfWeek() != ZonedDateTime.now(ZoneId.of("Europe/London")).getDayOfWeek() && settings.usingOverrideTimes) {
                // times were overridden, but now they've been completed and the next due tourny is tomorrow, revert to default.
                settings.usingOverrideTimes = false;
                settings.setStartTimes(TornSystemSettings.defaultStartTimes);
                logger.info("reverted back to default tournament times, discarding overrides");
                setNextTournyType();
                return;
            }

            nextTorn = new Tournament(settings.tornConfigs[Utils.random(settings.tornConfigs.length)]);
            TournamentManager.nextTime = nextEvent.format(DateTimeFormatter.ofPattern("dd MMM YYYY h:mm:ss a"));
            //Check that we don't have back to back tournament types.
            if (prevTorn != null && prevTorn.getTypeName().equals(nextTorn.getTypeName())) {
                int max = 5;

                int iterations = 0;

                while (++iterations < max && prevTorn.getTypeName().equals(nextTorn.getTypeName())) {
                    nextTorn = new Tournament(settings.tornConfigs[Utils.random(settings.tornConfigs.length)]);
                } // this should stop them ever being the same
                TournamentManager.nextEvent = null;
            }
            if (waitingRoomTournament != null)
                prevTorn = waitingRoomTournament;

            //logger.info("Next torn (" + nextTorn.getTypeName() + ") scheduled for " + TournamentManager.nextTime);
        }
    }


    public static boolean checkAndOpenLobby(boolean force) {
        if ((openEventLobby() || force) && !lobbyActive()) {
            if (tournaments.size() > 0 && tournaments.get(0) != null && !force) {
                // in progress
                //World.getWorld().sendWorldMessage("The next tournament is postponed until the current one finishes.");
                logger.debug("waiting next");
                return true;
            }
            Tournament t = nextTorn;
            if (t != null) {
                nextTorn = null;
                nextEvent = null;
                tournaments.add(t);
                waitingRoomTournament = t;
                World.getWorld().sendWorldMessage(format("<lsprite=1082>[<col=" + Color.MEDRED.getColorValue() + ">Tournament</col>]: A %s tournament will start in %s.", t.fullName(), t.lobbyTimeMessage()));
                //logger.info(format("A %s tournament will start in %s.", t.fullName(), t.lobbyTimeMessage()));
                t.setLobbyOpenTimeMs(System.currentTimeMillis());
            }
        }
        return false;
    }

    /**
     * For player updating
     *
     * @return true by default, false if Player is alive and pking while otherPlayer is a ghost spectator, or vice versa.
     */
    public static boolean canSee(Player player, Player otherPlayer) {
        if (player.inActiveTournament() && otherPlayer.inActiveTournament() && player.getParticipatingTournament().getWinner() == null) { // only apply to tournament checks
            if (!player.isTournamentSpectating() && otherPlayer.isTournamentSpectating()) // if other people are spectating while we're fighting, hide them
                return false;
            // If the other player's opponent isn't us, they must be a spectator Or fighting someone else.
            if (!player.isTournamentSpectating() && !player.isInTournamentLobby() && otherPlayer.getTournamentOpponent() != player)
                return false;
        }
        return true;
    }

    public static String tournamentInfo() {
        String text = "";

        //Tournament is active, but needs players
        if (tournaments.size() > 0 && tournaments.get(0) != null) {
            Tournament tournament = tournaments.get(0);
            if (tournament.inLobby.size() < tournament.minimumParticipants) {
                text = (tournament.minimumParticipants - tournament.inLobby.size() + " more players are needed to start");
            }
        } else {
            //Waiting for next tournament
            text = TournamentManager.nextTornStartsInMessage();
        }
        return text;
    }

    public static void openTournamentWidget(Player player) {
        if (TournamentManager.isTournamentsDisabled()) {
            player.message("The tournament system is currently disabled. Try again later to find out if it is open.");
            return;
        }
        player.getInterfaceManager().open(TournamentUtils.TOURNAMENT_INTERFACE);
        player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_TIME_LEFT_FRAME, tournamentInfo());

        List<String> historyTxt = new ArrayList<>(0);
        List<String> historyTxt2 = new ArrayList<>(0);

        for (WinnerHistory winnerHistory : getWinnerHistories()) {
            long difference = (System.currentTimeMillis() - winnerHistory.time) / 1000;
            historyTxt.add(format("%s won <col=ffff00>%sx %s",
                winnerHistory.username, winnerHistory.rewardAmt, winnerHistory.rewardName)
            );
            historyTxt2.add(format("%s won the <col=ff7000>%s<col=ffff00> %s ago.", winnerHistory.username, winnerHistory.tornName, difference >= 3600 ? difference / 3600 + " hours" : difference >= 60 ? difference / 60 + " minutes" : difference + " seconds"));
        }
        //System.out.printf("Rewards %s -> %s %s%n", historyTxt.size(), Arrays.toString(historyTxt.toArray(new String[0])), Arrays.toString(historyTxt2.toArray(new String[0])));
        int tick = 0, lineIdx = 0;
        for (int index = 0; index < 35; index++) {
            if (tick == 2) {
                player.getPacketSender().sendString(20009, "");
                tick = 0;
                lineIdx++;
            } else if (tick == 0) {
                player.getPacketSender().sendString(20009 + index, lineIdx >= historyTxt.size() ? "" : historyTxt.get(lineIdx));
                //addText(21009 + index, "Patrick won <col=ffff00>x1 $10.00 bond", font, 0, 16750623, true);
                tick++;
            } else {
                player.getPacketSender().sendString(20009 + index, lineIdx >= historyTxt2.size() ? "" : historyTxt2.get(lineIdx));
                //addText(21009 + index, "from <col=ff7000>Dharok PK Tournament <col=ffff00>" + (index + 1) * 6 + " hours ago", font, 0, 16750623, true);
                tick++;
            }
        }
        if (historyTxt.size() == 0) {
            player.getPacketSender().sendString(20009, "None!");
        }
        Tournament t = nextTorn;
        if (t != null) {
            String title = t.fullName();
            player.getPacketSender().sendItemOnInterfaceSlot(TournamentUtils.PRIZE_FRAME, t.reward.getId(), t.reward.getAmount(), 0);
            player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_TEXT_FRAME, "Next world tournament: <col=ffff00>" + nextTorn.getTypeName());
        }
    }

    public static boolean handleWidgetButton(Player player, int btnId) {
        if (btnId == 20047) {
            // join
            if (TournamentManager.isTournamentsDisabled()) {
                player.message("The tournament system is currently disabled. Try again later to find out if it is open.");
                return true;
            } else if (TournamentManager.gameInProgress()) {
                player.message("There is a tournament in progress, you cannot join yet.");
                return true;
            } else if (TournamentManager.lobbyActive() && TournamentManager.getWaitingRoomTournament().lobbyFull()) {
                player.message(TournamentManager.getWaitingRoomTournament().lobbyFullMessage());
                return true;
            } else if (!TournamentManager.lobbyActive()) {
                player.message(TournamentManager.nextTornStartsInMessage());
                return true;
            } else if (!canEnterLobby(player)) {
                player.message("You do not meet all the requirements to enter the lobby.");
                return true;
            } else if (canEnterLobby(player)) {
                tournaments.get(0).enterLobby(player);
                return true;
            }
        } else if (btnId == 20052) {
            // spectate
            if (TournamentManager.isTournamentsDisabled()) {
                player.message("The tournament system is currently disabled. Try again later to find out if it is open.");
                return true;
            } else if (!TournamentManager.gameInProgress()) {
                player.message("There is no tournament in progress to spectate.");
                return true;
            } else if (!canEnterLobby(player)) {
                player.message("You do not meet all the requirements to enter the lobby.");
                return true;
            } else if (canEnterLobby(player)) {
                joinSpectating(player, tournaments.get(0));
                return true;
            } else {
                player.message("Nothing interesting happens");
                return true;
            }
        }
        return false;
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    /**
     * @return true if the hour matches when an event should start
     */
    private static boolean openEventLobby() {
        var now = ZonedDateTime.now();
        for (String startTime : settings.startTimes) {
            if (startTime == null || startTime.length() != 5 || !startTime.contains(":")) {
                System.err.println("bad start for tournament! " + startTime);
                startTime = "00:00";
            }
            try {
                ZonedDateTime next = calForTime(now, startTime);
                if (active(now, next))
                    return true;
            } catch (ParseException e) {
                logger.catching(e);
            }
        }
        return false;
    }
    private static ZonedDateTime nextEvent = null;

    private static ZonedDateTime nextEvent() {
        if (nextEvent != null) // cached, nullified on event change
            return nextEvent;
        // today
        for (String startTime : settings.startTimes) {
            try {
                ZonedDateTime next = calForTime(now, startTime);
                // first one in list that is larger than current time
                if (next.toEpochSecond() > nowSec) {
                    return nextEvent = next;
                }
            } catch (ParseException e) {
                logger.catching(e);
            }
        }
        // tomorrow!
        for (String startTime : settings.startTimes) {
            try {
                ZonedDateTime next = calForTime(now, startTime, true);
                // first one in list that is larger than current time
                if (next.toEpochSecond() > nowSec) {
                    return nextEvent = next;
                }
            } catch (ParseException e) {
                logger.catching(e);
            }
        }
        return null;
    }

    public static boolean active(ZonedDateTime now, String current, String check) throws ParseException {
        return active(calForTime(now, current), calForTime(now, check));
    }

    public static ZonedDateTime calForTime(ZonedDateTime now, String v) throws ParseException {
        return calForTime(now, v, false);
    }

    public static ZonedDateTime calForTime(ZonedDateTime now, String hours, boolean tomorrow) throws ParseException {
        ZonedDateTime next = now.withHour(Integer.parseInt(hours.substring(0, 2))).withMinute(Integer.parseInt(hours.substring(3, 5))).withSecond(0);
        if (tomorrow)
            next = next.plusDays(1);
        return next;
    }

    public static boolean active(ZonedDateTime current, ZonedDateTime check) {
        return current.getHour() == check.getHour() && current.getMinute() == check.getMinute();
    }
}

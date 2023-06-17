package com.aelous.model.content.tournaments;

import com.aelous.GameServer;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.inter.impl.BonusesInterface;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.aelous.model.content.tournaments.TournamentUtils.LOBBY_TILE;
import static com.aelous.utility.CustomItemIdentifiers.LEGENDARY_MYSTERY_BOX;
import static com.aelous.utility.CustomItemIdentifiers.MYSTERY_TICKET;
import static java.lang.String.format;

/**
 * To read how it works see {@link TournamentManager}
 *
 * @author Shadowrs (tardisfan121@gmail.com)
 */
public class Tournament {

    private static final Logger logger = LogManager.getLogger(Tournament.class);

    public int maxParticipants = GameServer.properties().tournamentMaxParticipants, minimumParticipants = (GameServer.properties().production ? GameServer.properties().tournamentMinProdParticipants : GameServer.properties().tournamentMinDevParticipants);

    final List<Player> inLobby = new ArrayList<>(0);

    private final TournamentManager.TornConfig config;

    public long lobbyOpenTimeMs;

    final List<Player> fighters = new ArrayList<>(0);

    final List<Player> spectators = new ArrayList<>(0);

    private long startedAt;
    Player winner;

    final Item reward;
    private int tick;

    public Tournament(TournamentManager.TornConfig config) {
        this.config = config;
        reward = setRewards();
        Utils.sendDiscordInfoLog("Tournament reward has been set to "+reward.toString()+" .", "tournaments");
    }

    public Tournament(TournamentManager.TornConfig config, Item reward) {
        this.config = config;
        this.reward = reward;
    }

    boolean inProgress() {
        return fighters.size() > 0;
    }

    String getTypeName() {
        return config.key;
    }

    boolean lobbyFull() {
        return inLobby.size() >= maxParticipants;
    }

    String lobbyFullMessage() {
        return format("The lobby has reached its maximum participants (%s). Be sure to arrive earlier next time!", maxParticipants);
    }

    void enterLobby(Player player) {
        if (inLobby.size() >= maxParticipants) {
            return;
        }
        if (fighters.size() > 0) { // game in progress
            return;
        }
        if (player.getParticipatingTournament() != null)
            return;

        player.teleport(LOBBY_TILE);
        player.setInTournamentLobby(true);
        player.setParticipatingTournament(this);
        inLobby.add(player);
        // TODO store skills
        player.getRunePouch().clear();
        resetAllVars(player);
        player.getInterfaceManager().close();
        Skulling.unskull(player);
        player.message(format("You've entered the %s tournament. Good luck %s.", config.key, player.getUsername()));
        if (inLobby.size() < minimumParticipants) {
            player.message("The tournament requires at least " + (minimumParticipants - inLobby.size()) + " more players to start.");
        }
        player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_WALK_TIMER, "Starting in 30...");
        setLoadoutOnPlayer(player);
    }

    /**
     * Resets various combat vars
     */
    public void resetAllVars(Player player) {
        player.resetAttributes();
    }

    /**
     * Called from the primary tournament system timer every {@literal Tournament.settings.globalTimerSecs} seconds
     */
    public void tick() {
        // System.out.printf(this.config.key+" tick fighters:%s lobby:%s  spec:%s %s mins ago | lobby sec %s | %s %n",
        //      fighters.size(), inLobby.size(), spectators.size(), (System.currentTimeMillis() - lobbyOpenTimeMs)/60_000, TournamentManager.getSettings().lobbyTime,
        //     (disposeTournyAtTime - System.currentTimeMillis()) / 1000);
        if (tick++ == Integer.MAX_VALUE) {
            tick = 0;
        }
        //System.out.println("Ticking");
        // First, check the lobby. Minimum people required to start. Move players into main game and clear lobby.
        if (fighters.size() == 0 && inLobby.size() > 0) {
            //Every 60 ticks, we print the message to the players, if necessary.
            if (tick % 60 == 0) {
                if (inLobby.size() < minimumParticipants) {
                    for (Player player : inLobby) {
                        player.message("The tournament requires at least " + (minimumParticipants - inLobby.size()) + " more players to start.");
                    }
                }
            }
            /**
             * lobby starting moved to {@link TournamentManager.TournamentLobbyTask}
             */
        }

        // Check if fights are ongoing. If everyone has finished fighting, progress to next stage.
        checkForNextRoundStart();
    }

    public void checkForWinner() {
        if (fighters.size() == 1) {
            winner = fighters.get(0);
            var wins = winner.<Integer>getAttribOr(AttributeKey.TOURNAMENT_WINS, 0) + 1;
            winner.putAttrib(AttributeKey.TOURNAMENT_WINS, wins);
            var points = winner.<Integer>getAttribOr(AttributeKey.TOURNAMENT_POINTS, 0) + 1;
            winner.putAttrib(AttributeKey.TOURNAMENT_POINTS, points);
            winner.message("You have won the " + fullName() + " Tournament! You now have won "+ Color.BLUE.wrap(""+wins)+" tournaments.");
            winner.message("You've received 1 tournament point! You now have won "+Color.BLUE.wrap(""+points)+" tournament points.");
            World.getWorld().sendWorldMessage(format("<img=505>[<col=" + Color.MEDRED.getColorValue() + ">Tournament</col>]: %s has won the %s tournament!", winner.getUsername(), fullName()));
            logger.info(format("<img=505>[<col=" + Color.MEDRED.getColorValue() + ">Tournament</col>]: %s has won the %s tournament!", winner.getUsername(), fullName()));
            logger.info("PvP tournament rewards: " + reward.toString());

            winner.getPacketSender().sendInteractionOption("null", 2, true); //Remove attack option
            winner.getPacketSender().sendEntityHintRemoval(true);
            onTournyClosed();
        }
    }

    public boolean firstRoundStarted;

    public void checkForNextRoundStart() {
        if (fighters.size() >= 2) {
            boolean fightInProgress = false;
            for (Player player : fighters) {
                if (player.getTournamentOpponent() != null) {
                    fightInProgress = true;
                    break;
                }
            }

            // progress to next stage. assign opponents.
            if (!fightInProgress) {
                startedAt = System.currentTimeMillis();
                Player last = null;
                Collections.shuffle(fighters);
                for (int i = 0; i < fighters.size(); i++) {
                    final Player player = fighters.get(i);
                    if (i % 2 == 0) {
                        last = player;
                        if (i == fighters.size() - 1) { // we're the last one.. no other opponent available
                            player.message("There was no opponent available. You will fight the winner of next match.");
                            player.getMovementQueue().setBlockMovement(false); // allow walk again
                            player.setTournamentSpectating(true); // allow to watch other fights
                            break; // should be the end of the list anyway
                        }
                    } else {
                        if (last != null) {
                            prepareMatch(player, last);
                            prepareMatch(last, player);
                            //logger.trace(player.getUsername() + " vs " + last.getUsername());
                            last = null;
                        } else {
                            player.message("There was no opponent available, so you will fight the winner of next match.");
                            player.getMovementQueue().setBlockMovement(false); // allow walk again
                            player.setTournamentSpectating(true); // allow to watch other fights
                            break; // should be the end of the list anyway
                        }
                    }
                }
                if (!firstRoundStarted) {
                    firstRoundStarted = true;
                }

                //logger.trace("the next round of fights have been decided!");
            }
        }
    }

    public void onTournyClosed() {
        //logger.info("Closing tourny " + config.key + " by winner " + winner);
        // they havent left manually yet
        List<Player> temp = new ArrayList<>(0);
        temp.addAll(inLobby);
        temp.addAll(spectators);
        temp.addAll(fighters);
        for (Player player : temp) {
            TournamentManager.leaveTourny(player, false);
        }
    }

    public void setLoadoutOnPlayer(Player player) {
        resetAllVars(player);
        TournamentManager.wipeLoadout(player); // clear the loadout and re-load below it we get new food. yes players will have to micro manage their inv setup every round.
        player.setSpecialAttackPercentage(100);
        CombatSpecial.updateBar(player);

        TournamentManager.Loadout loadout = TournamentManager.loadouts.stream().filter(l -> l.key.equalsIgnoreCase(config.key)).findFirst().orElse(null);
        if (loadout == null) {
            logger.error("no loadout exists for tournaments loadout key " + config.key + "!");
            loadout = TournamentManager.loadouts.get(0);
        }
        // first set stats, for equipment requirements
        for (TournamentManager.Tuple<Integer, Integer> stat : loadout.stats) {
            int skill = stat.x;
            int level = stat.y;
            player.skills().setLevel(skill, level);
            player.skills().setXp(skill, Skills.levelToXp(level));
            player.skills().update(skill);
        }
        for (int i = 0; i < loadout.equip.capacity(); i++) {
            if (loadout.equip.getItems()[i] == null)
                continue;
            player.inventory().set(i, loadout.equip.getItems()[i].clone(), false);
        }
        for (int i = 0; i < player.inventory().capacity(); i++) {
            if (player.inventory().get(i) == null) continue;
            //logger.trace("Trying to equip " + player.inventory().get(i));
            player.getEquipment().equip(i);
            BonusesInterface.sendBonuses(player);
        }
        for (int i = 0; i < loadout.inv.capacity(); i++) {
            if (loadout.inv.getItems()[i] == null)
                continue;
            player.inventory().set(i, loadout.inv.getItems()[i].clone(), false);
        }
        for (int i = 0; i < loadout.runepouch.capacity(); i++) {
            if (loadout.runepouch.getItems()[i] == null) continue;
            player.getRunePouch().deposit(loadout.runepouch.getItems()[i].clone());
            player.getRunePouch().refresh();
            //logger.trace("tournament: rune pouch: slot {} is {}", i, player.getRunePouch().get(i));
        }
        player.getRunePouch().refresh();
        //logger.trace("Setting spellbook to: " + MagicSpellbook.forId(loadout.spellbook));
        player.setPreviousSpellbook(player.getSpellbook());
        MagicSpellbook.changeSpellbook(player, MagicSpellbook.forId(loadout.spellbook), false);
        player.inventory().refresh();
        player.getEquipment().refresh();
        player.setQueuedAppearanceUpdate(true);
    }

    /**
     * Configure the player for battle: set equipment, inventory, spellbook, runepouch etc to Tournament Preset
     *
     * @param player
     * @param last
     */
    private void prepareMatch(Player player, Player last) {
        if (firstRoundStarted) {
            // only give loadout on rounds after 1. they get the loadout in lobby for round 1, allowing them to move inv about.
            setLoadoutOnPlayer(player);
        }
        //Reset spectating state, we could of been spectating when we had a free tournament by
        player.setTournamentSpectating(false);

        player.setTournamentOpponent(last);
        //logger.trace("Sending entity hint for tournament");
        player.getPacketSender().sendEntityHint(last);

        //Set new waiting timers
        player.getTimers().extendOrRegister(TimerKey.TOURNAMENT_FIGHT_IMMUNE, TournamentUtils.FIGHT_IMMUME_TIMER);
        player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_WALK_TIMER, "00:30");
        // TODO task uniquenness based on current round
        TaskManager.submit(new Task() {
            // does this repeat or is it a 1 time thing? Task one time thing
            @Override
            protected void execute() {

                if (player.getIndex() < 0) {
                    logger.error("Player index less than 0 for prepareMatch");
                    stop();
                    return;
                }

                if (!player.inActiveTournament()) {
                    stop();
                    return;
                }

                int secs = (int) (player.getTimers().left(TimerKey.TOURNAMENT_FIGHT_IMMUNE) * 0.6); //times 0.6 makes it secs but interface / updates are every 0.6 so it needs to be a client side timer
                player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_WALK_TIMER, secs > 9 ? "00:"+secs : "00:0"+secs);

                if (secs == 0) {
                    player.forceChat("Fight!");
                    player.getPacketSender().sendString(TournamentUtils.TOURNAMENT_WALK_TIMER, "Fight!");
                    player.clearAttrib(AttributeKey.TOURNAMENT_COUNTDOWN);
                    stop();
                } else {
                    if (secs != (int) player.getAttribOr(AttributeKey.TOURNAMENT_COUNTDOWN, -1)) {
                        //If maximum seconds is greater than 20 we use do % 10 otherwise use % 5.
                        if (secs % 10 == 0) {
                            player.message(format("Fight starts in seconds %s...", secs));
                        }
                        player.putAttrib(AttributeKey.TOURNAMENT_COUNTDOWN, secs);
                    }
                }
            }
        }.bind(player));
    }

    String lobbyTimeMessage() {
        int secs = TournamentManager.settings.lobbyTime;
        return secs < 60 ? secs + " seconds" : secs % 60 == 1 ? "1 minute" : secs / 60 + " minutes";
    }

    /**
     * @return true when the tournament has run, battles fought and winner announced
     */
    public boolean finished() {
        return winner != null && startedAt > 0;
    }

    /**
     * Translates the config.key ("nhpure") to a full name "Nh Pure Tournament"
     */
    public String fullName() {
        return switch (config.key) {//done1
            case "NH Pure" -> "Pure No Honor PK tournament";
            default -> config.key;
        };
    }

    /**
     * The list of items rewarded from a tournament
     */
    private Item setRewards() {
        var possible_rewards = new Item[] {new Item(LEGENDARY_MYSTERY_BOX, 1), new Item(MYSTERY_TICKET, 1)};
        return World.getWorld().random(possible_rewards);
    }

    public int getMaxParticipants() {
        return this.maxParticipants;
    }

    public int getMinimumParticipants() {
        return this.minimumParticipants;
    }

    public List<Player> getInLobby() {
        return this.inLobby;
    }

    public TournamentManager.TornConfig getConfig() {
        return this.config;
    }

    public long getLobbyOpenTimeMs() {
        return this.lobbyOpenTimeMs;
    }

    public List<Player> getFighters() {
        return this.fighters;
    }

    public List<Player> getSpectators() {
        return this.spectators;
    }

    public Player getWinner() {
        return this.winner;
    }

    public Item getReward() {
        return this.reward;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setMinimumParticipants(int minimumParticipants) {
        this.minimumParticipants = minimumParticipants;
    }

    public void setLobbyOpenTimeMs(long lobbyOpenTimeMs) {
        this.lobbyOpenTimeMs = lobbyOpenTimeMs;
    }
}

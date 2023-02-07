package com.aelous.model.content.minigames.tournaments;

import com.aelous.model.entity.player.Player;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;

/**
 * @author Ynneh | 19/04/2022 - 18:44
 * <https://github.com/drhenny>
 */
public class TournamentHandler {

    public static TournamentType type;

    /**
     * Generates a tournament type upon init/finish
     * @return
     */
    public static TournamentType selectRandomTournamentType() {
        double random = new SecureRandom().nextDouble();
        if (random < .5)
            return TournamentType.PURE;
        return TournamentType.MAXED;
    }

    /**
     * Used upon server load
     */
    public static void init() {
        type = selectRandomTournamentType();
    }

    /**
     * Used to update the quest tab and other interfaces
     * @return
     */
    public static String updateTabs() {
        return "TODO";
    }

    /**
     * A list to store players
     */
    @Getter
    private static List<TournamentTarget> lobbyPlayers = Lists.newArrayList();

    /**
     * When the player joins the tournament
     * @param player
     */
    public static void join(Player player) {

        TournamentTarget t = new TournamentTarget(player);

        if (lobbyPlayers.contains(t))
            return;

        lobbyPlayers.add(t);
    }

    /**
     * When a player leaves the tournament
     * @param player
     */
    public static void leave(Player player) {

    }

    /**
     * Assigns a tournament target
     */
    public static void assignTarget() {

    }

    /**
     * Moves players to the tournament when assigned a target.
     */
    public static void onMove() {
        lobbyPlayers.stream().forEach(l -> {
            /**
             * TODO give gear, move to location, ect
             */
        });
    }

    public static void sendLobbyMessage(String message) {
        lobbyPlayers.stream().filter(Objects::nonNull).forEach(p -> p.player.getPacketSender().sendMessage(message));
    }
}

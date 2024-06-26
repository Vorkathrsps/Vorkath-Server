package com.cryptic.model.map.position.areas.impl;

import com.cryptic.model.content.tournaments.TournamentManager;
import com.cryptic.model.content.tournaments.TournamentUtils;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.areas.Controller;

import java.util.Arrays;

/**
 * @author Origin | Zerikoth | PVE
 * @date maart 23, 2020 13:19
 */
public class TournamentArea extends Controller {

    public TournamentArea() {
        super(Arrays.asList(new Area(3321, 4940, 3325, 4979), new Area(3267, 4931, 3318, 4988)));
    }

    @Override
    public void enter(Player player) {
        player.getInterfaceManager().sendOverlay(TournamentUtils.TOURNAMENT_WALK_INTERFACE);
    }

    @Override
    public void leave(Player player) {
        player.getInterfaceManager().sendOverlay(-1);
        TournamentManager.leaveTourny(player, false, false);
    }

    @Override
    public void process(Player player) {
        if (player.getParticipatingTournament() != null) {
            if (player.getParticipatingTournament().getFighters().size() == 1) {
                player.getParticipatingTournament().checkForWinner();
            }
        }
        if (!player.isTournamentSpectating()) {
            player.getInterfaceManager().sendOverlay(TournamentUtils.TOURNAMENT_WALK_INTERFACE);
        }
    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        return player.getPlayerRights().isCommunityManager(player);
    }

    @Override
    public boolean canAttack(Player attacker, Entity target) {
        return true;
    }

    @Override
    public void defeated(Player player, Entity entity) {

    }


    @Override
    public boolean canTrade(Player player, Player target) {
        return false;
    }

    @Override
    public boolean isMulti(Entity entity) {
        return false;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject object, int type) {
        return false; // dealt with elsewhere
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int type) {
        return false;
    }

    @Override
    public boolean useInsideCheck() {
        return true; // no need, assuming coords are accurate
    }

    @Override
    public boolean inside(Entity entity) {
        return entity.tile().region() == TournamentUtils.TOURNAMENT_REGION;
    }
}

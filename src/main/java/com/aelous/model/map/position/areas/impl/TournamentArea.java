package com.aelous.model.map.position.areas.impl;

import com.aelous.model.content.tournaments.TournamentManager;
import com.aelous.model.content.tournaments.TournamentUtils;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.areas.Controller;

import java.util.List;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 23, 2020 13:19
 */
public class TournamentArea extends Controller {

    public static final Area TOURNAMENT_AREA = new Area(1698, 4690, 1722, 4716);

    public TournamentArea() {
        super(List.of(TOURNAMENT_AREA));
    }

    @Override
    public void enter(Player mob) {

    }

    @Override
    public void leave(Player mob) {
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            player.getInterfaceManager().sendOverlay(-1);

            //Clear all items when leaving the area, players could use reflection run out and bank.
            TournamentManager.leaveTourny(player, false, false);
        }
    }

    @Override
    public void process(Player mob) {
        if (mob.isPlayer()) {
            Player player = mob.getAsPlayer();
            //No interface for spectators
            if(!player.isTournamentSpectating()) {
                player.getInterfaceManager().sendOverlay(TournamentUtils.TOURNAMENT_WALK_INTERFACE);
            }
        }
    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        return player.getPlayerRights().isDeveloper(player);
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
        return false; // no need, assuming coords are accurate
    }

    @Override
    public boolean inside(Entity entity) {
        return false;
    }
}

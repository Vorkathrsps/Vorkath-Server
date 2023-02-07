package com.aelous.model.content.minigames.tournaments;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.areas.Controller;

import java.util.List;

/**
 * @author Ynneh | 19/04/2022 - 19:12
 * <https://github.com/drhenny>
 */
public class TournmentLobbyController extends Controller {
    /**
     * TODO
     * @param areas
     */
    public TournmentLobbyController(List<Area> areas) {
        super(areas);
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player) {

    }

    @Override
    public void process(Player player) {

    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        return false;
    }

    @Override
    public boolean canAttack(Player attacker, Entity target) {
        return false;
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
        return false;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return false;
    }

    @Override
    public void onPlayerRightClick(Player player, Player other, int option) {

    }

    @Override
    public boolean handleObjectClick(Player player, GameObject object, int option) {
        return false;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int option) {
        return false;
    }

    @Override
    public boolean useInsideCheck() {
        return false;
    }

    @Override
    public boolean inside(Entity entity) {
        return false;
    }
}

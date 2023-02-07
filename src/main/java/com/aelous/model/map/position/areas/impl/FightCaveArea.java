package com.aelous.model.map.position.areas.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.areas.Controller;

import java.util.Collections;

/**
 * @author Patrick van Elderen | December, 23, 2020, 15:49
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class FightCaveArea extends Controller {

    public FightCaveArea() {
        super(Collections.singletonList(new Area(2360, 5045, 2445, 5125)));
    }

    @Override
    public void enter(Player entity) {

    }

    @Override
    public void leave(Player player) {
        var minigame = player.getMinigame();
        if (minigame != null) {
            minigame.end(player);
        }
    }

    @Override
    public void process(Player player) {

    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        player.message("Please use the southern exit if you wish to leave the caves.");
        return false;
    }

    @Override
    public boolean canAttack(Player attacker, Entity target) {
        return true;
    }

    @Override
    public void defeated(Player player, Entity entity) {
        var minigame = player.getMinigame();
        if (minigame != null) {
            minigame.end(player);
        }
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return false;
    }

    @Override
    public boolean isMulti(Entity entity) {
        return true;
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
        return false;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int type) {
        return false;
    }

    @Override
    public boolean useInsideCheck() {
        return false;// no need, assuming coords are accurate
    }

    @Override
    public boolean inside(Entity entity) {
        return false;// no need, assuming coords are accurate
    }
}

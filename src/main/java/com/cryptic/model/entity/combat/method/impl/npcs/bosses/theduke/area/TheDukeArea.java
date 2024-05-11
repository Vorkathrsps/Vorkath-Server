package com.cryptic.model.entity.combat.method.impl.npcs.bosses.theduke.area;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.areas.Controller;

import java.util.List;

public class TheDukeArea extends Controller {
    public TheDukeArea(List<Area> areas) {
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

package com.cryptic.model.content.areas.edgevile;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.utility.timers.TimerKey;

import java.util.Collections;

public class AFKZoneArea extends Controller {

    public static final Area ROOM = Skills.AFK_ZONE;

    public AFKZoneArea() {
        super(Collections.emptyList());
    }

    @Override
    public void enter(Player player) {
        player.putAttrib(AttributeKey.AFK, true);
    }

    @Override
    public void leave(Player player) {
        player.getTimers().cancel(TimerKey.AFK_TIMEOUT);
        player.clearAttrib(AttributeKey.AFK);
        player.stopActions(true);
    }

    @Override
    public void process(Player player) {
        final boolean timeout = player.getTimers().left(TimerKey.AFK_TIMEOUT) <= 0;
        if (timeout) {
            player.endCurrentTask();
            player.stopActions(true);
            player.teleport(new Tile(3087, 3489, 0));
        }
    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
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
        return true;
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
    public void onPlayerRightClick(Player player, Player other, int option) {

    }

    @Override
    public boolean handleObjectClick(Player player, GameObject object, int option) {
        return true;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int option) {
        return true;
    }

    @Override
    public boolean useInsideCheck() {
        return true;
    }

    @Override
    public boolean inside(Entity entity) {
        Player player = (Player) entity;
        return ROOM.contains(player.tile());
    }
}

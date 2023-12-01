package com.cryptic.model.map.position.areas.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.utility.Color;
import com.cryptic.utility.TimeClock;
import com.cryptic.utility.Utils;

import java.util.Collections;

import static com.cryptic.model.content.raids.party.Party.*;
import static com.cryptic.model.entity.attributes.AttributeKey.PERSONAL_POINTS;

/**
 * @author Origin | May, 10, 2021, 18:44
 */
public class COXArea extends Controller {
    public static final int POINTS_WIDGET = 12000;
    TimeClock timeClock = new TimeClock();

    public COXArea() {
        super(Collections.emptyList());
    }

    @Override
    public void enter(Player player) {
    }

    @Override
    public void leave(Player player) {
    }

    @Override
    public void process(Player player) {
        var party = player.raidsParty;
        if (party != null) {
            player.getPacketSender().sendString(TOTAL_POINTS, Color.WHITE.wrap("Total: " + Utils.formatNumber(party.totalPoints())));
            player.getPacketSender().sendString(POINTS, Color.WHITE.wrap(player.getUsername() + ": " + Utils.formatNumber(player.<Integer>getAttribOr(PERSONAL_POINTS, 0))));
            player.getPacketSender().sendString(12005, Color.WHITE.wrap("Time: " + timeClock.currentTimeClock()));
        }
        player.getInterfaceManager().sendOverlay(POINTS_WIDGET);
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
        return !target.isPlayer();
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
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
    public void defeated(Player player, Entity entity) {
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
    public boolean inside(Entity entity) {
        return entity.tile().region() == 12889 || entity.tile().region() == 13136 || entity.tile().region() == 13137 || entity.tile().region() == 13138 || entity.tile().region() == 13139;
    }

    @Override
    public boolean useInsideCheck() {
        return true;
    }
}

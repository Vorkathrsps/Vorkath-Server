package com.aelous.model.map.position.areas.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.areas.Controller;
import com.aelous.utility.Utils;

import java.util.Collections;

import static com.aelous.model.content.raids.party.Party.*;
import static com.aelous.model.entity.attributes.AttributeKey.PERSONAL_POINTS;

/**
 * @author Patrick van Elderen | May, 10, 2021, 18:44
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class COXArea extends Controller {

    private static final int POINTS_WIDGET = 12000;

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
            player.getPacketSender().sendString(NAME_FRAME, player.getUsername() + ":");
            player.getPacketSender().sendString(TOTAL_POINTS, "" + Utils.formatNumber(party.totalPoints()));
            player.getPacketSender().sendString(POINTS, "" + Utils.formatNumber(player.<Integer>getAttribOr(PERSONAL_POINTS, 0)));
        }
        player.getInterfaceManager().sendOverlay(POINTS_WIDGET);

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

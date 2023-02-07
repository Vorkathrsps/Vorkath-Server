package com.aelous.model.map.position.areas.impl;

import com.aelous.GameServer;
import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.areas.Controller;

import java.util.Arrays;

import static com.aelous.model.content.duel.DuelState.IN_DUEL;
import static com.aelous.model.content.duel.DuelState.STARTING_DUEL;

public class DuelArenaArea extends Controller {

    // In any of the 6 challenge areas.
    public DuelArenaArea() {
        super(Arrays.asList(
            new Area(3318, 3247, 3327, 3247),
            new Area(3324, 3247, 3328, 3264),
            new Area(3327, 3262, 3342, 3270),
            new Area(3342, 3262, 3387, 3280),
            new Area(3387, 3262, 3394, 3271),
            new Area(3312, 3224, 3325, 3247),
            new Area(3326, 3200, 3398, 3267)
        ));
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player) {
        player.getInterfaceManager().sendOverlay(-1);
        //System.out.println(player.getUsername() + " is leaving duel arena");
        if (player.getDueling().inDuel()) {
            player.getDueling().onDeath();
        }
        player.getPacketSender().sendInteractionOption("null", 1, false);
        player.getPacketSender().sendInteractionOption("null", 2, true);
    }

    @Override
    public void process(Player player) {
        if (player.getInterfaceManager().getWalkable() != 201) // therefore it won't send, it thinks its already 201
            player.getInterfaceManager().sendOverlay(201);
        if (!player.getDueling().inDuel() && GameServer.properties().enableDueling) {
            player.getPacketSender().sendInteractionOption("Challenge", 1, false);
            player.getPacketSender().sendInteractionOption("null", 2, true);
        } else {
            player.getPacketSender().sendInteractionOption("null", 1, true);
            player.getPacketSender().sendInteractionOption("Attack", 2, true);
        }
    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        if (player.getDueling().inDuel()) {
            DialogueManager.sendStatement(player, "You cannot teleport out of a duel!");
            return false;
        }
        return true;
    }

    @Override
    public boolean canAttack(Player player, Entity t) {
        if (t instanceof NPC)
            return false;
        Player target = (Player) t;

        if (target.isPlayer()) {
            if (player.getDueling().getState() == IN_DUEL && target.getDueling().getState() == IN_DUEL) {
                return true;
            } else if (player.getDueling().getState() == STARTING_DUEL || target.getDueling().getState() == STARTING_DUEL) {
                player.message("The duel hasn't started yet!");
                return false;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        if (player.getDueling().inDuel()) {
            DialogueManager.sendStatement(player, "You cannot trade during a duel!");
            return false;
        }
        return true;
    }

    @Override
    public boolean isMulti(Entity entity) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_FOOD.ordinal()]) {
            DialogueManager.sendStatement(player, "Food has been disabled in this duel!");
            return true;
        }
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_POTIONS.ordinal()]) {
            DialogueManager.sendStatement(player, "Potions have been disabled in this duel!");
            return true;
        }
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
        if (option == 1) {
            //System.out.println("Duel right click");
            if (player.busy()) {
                player.message("You cannot do that right now.");
                return;
            }
            if (rightClicked.busy()) {
                player.message("That player is currently busy.");
                return;
            }
            if (!GameServer.properties().enableDueling) {
                player.message("Dueling is currently disabled until we have a larger playerbase.");
                return;
            }
            var rightClickedAttribOr = rightClicked.<Integer>getAttribOr(AttributeKey.CUSTOM_DUEL_RULE, 0);
            player.putAttrib(AttributeKey.CUSTOM_DUEL_RULE, rightClickedAttribOr);
            player.getDueling().requestDuel(rightClicked);
        }
    }

    @Override
    public void defeated(Player player, Entity entity) {
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject object, int type) {
        if (type == 1 && object.getId() == 3203) {
            player.message("Forfeit is currently disabled.");
            /*if (player.getController() instanceof DuelArenaArea) {
                if (player.getDueling().getRules()[DuelRule.NO_FORFEIT.ordinal()]) {
                    DialogueManager.sendStatement(player, "This duel cannot be forfeited.");
                    return true;
                }

                if (Dueling.in_duel(player)) {
                    player.getDialogueManager().start(new DuelForfeitDialogue());
                } else {
                    DialogueManager.sendStatement(player, "The duel has not yet begun.");
                    return true;
                }
            }*/
            return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int type) {
        return false;
    }

    @Override
    public boolean inside(Entity entity) {
        return false; // no need to use this, super(area) works fine (if coords are accurate)
    }

    @Override
    public boolean useInsideCheck() {
        return false; // no need, assuming coords are accurate
    }
}

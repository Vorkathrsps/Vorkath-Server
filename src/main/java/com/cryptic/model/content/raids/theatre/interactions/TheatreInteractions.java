package com.cryptic.model.content.raids.theatre.interactions;

import com.cryptic.model.content.raids.theatre.boss.maiden.utils.MaidenUtils;
import com.cryptic.model.content.raids.theatre.interactions.dialogue.TheatreDialogue;
import com.cryptic.model.content.raids.theatre.stage.RoomState;
import com.cryptic.model.content.raids.theatre.stage.TheatreState;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;

public class TheatreInteractions extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == 32655) {
            if (player.getTheatreInterface() == null) {
                player.setTheatreInterface(new TheatreInterface(player, new ArrayList<>()).open(player));
            } else {
                player.getTheatreInterface().open(player);
            }
            return true;
        } else if (obj.getId() == 32653) {
            if (player.getTheatreParty() != null) {
                player.getDialogueManager().start(new TheatreDialogue());
            }
            return true;
        } else if (obj.getId() == 32755) {
            if (player.tile().region() == 12613) {
                if (player.tile().getX() < 3186) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(2, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(-2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                }
                return true;
            } else if (player.tile().region() == 13125) {
                if (player.tile().getX() < 3288 && player.tile().getX() < 3303) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(-2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(2, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                }
                return true;
            }
        } else if (obj.getId() == 33113) {
            if (player.getRoomState().equals(RoomState.COMPLETE)) {
                if (player.tile().region() == 12613) {
                    var party = player.getTheatreInstance().getPlayers();
                    for (var p : party) {
                        p.teleport(new Tile(3271, 4448, player.getTheatreInstance().getzLevel()));
                    }
                }
            } else {
                player.message(Color.RED.wrap("You must complete this room to advance to the next fight."));
            }
            return true;
        }
        return false;
    }
}

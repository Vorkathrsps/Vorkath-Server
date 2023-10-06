package com.cryptic.model.content.raids.theatre.interactions;

import com.cryptic.model.content.raids.theatre.interactions.dialogue.TheatreDialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
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
            if (player.tile().getX() < 3186) {
                Chain.noCtx().runFn(1, () -> {
                    player.agilityWalk(false);
                    player.lock();
                    player.forceChat("stepping");
                    player.getMovementQueue().step(new Tile(player.tile().getX(), player.tile().getY()).transform(2, 0));
                }).then(1, () -> {
                    player.unlock();
                    player.agilityWalk(true);
                });
            } else {

            }
            return true;
        }
        return false;
    }
}

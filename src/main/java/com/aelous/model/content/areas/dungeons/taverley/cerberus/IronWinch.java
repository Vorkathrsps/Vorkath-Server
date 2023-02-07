package com.aelous.model.content.areas.dungeons.taverley.cerberus;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import java.util.LinkedList;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.IRON_WINCH;

public class IronWinch extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == IRON_WINCH) {
            Tile objectTile = obj.tile();

            Tile destination = objectTile.equals(new Tile(1291, 1254)) ? new Tile(1240, 1226) : //West
                objectTile.equals(new Tile(1328, 1254)) ? new Tile(1368, 1226) : //East
                    objectTile.equals(new Tile(1307, 1269)) ? new Tile(1304, 1290) : //North
                        new Tile(0, 0);

            int region = objectTile.equals(new Tile(1291, 1254)) ? 4883 : //West
                objectTile.equals(new Tile(1328, 1254)) ? 5395 : //East
                    objectTile.equals(new Tile(1307, 1269)) ? 5140 : //North
                        0;

            if (option == 1)
                teleportPlayer(player, destination);
            else
                peek(player, region);
            return true;
        }
        return false;
    }

    private void teleportPlayer(Player player, Tile tile) {
        Chain.bound(null).runFn(2, () -> player.animate(4506)).then(2, () -> player.teleport(tile));
    }

    private void peek(Player player, int region) {
        List<Player> count = new LinkedList<>();

        for (Player p : player.getLocalPlayers()) {
            if (p.tile().region() == region) {
                count.add(p);
            }
        }

        if (count.size() == 0) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.NPC_STATEMENT, 5870, Expression.HAPPY, "No adventurers are inside the cave.");
                    setPhase(0);
                }

                @Override
                public void next() {
                    if (getPhase() == 0) {
                        stop();
                    }
                }
            });
        } else {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.NPC_STATEMENT, 5870, Expression.HAPPY, count.size() + " adventurer is inside the cave.");
                    setPhase(0);
                }

                @Override
                public void next() {
                    if (getPhase() == 0) {
                        stop();
                    }
                }
            });
        }
    }
}

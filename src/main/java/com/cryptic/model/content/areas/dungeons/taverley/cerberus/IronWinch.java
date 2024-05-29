package com.cryptic.model.content.areas.dungeons.taverley.cerberus;

import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import java.util.LinkedList;
import java.util.List;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.IRON_WINCH;
import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;

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

            var task_id = player.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
            var task = SlayerCreature.lookup(task_id);

            if (option == 1) {
//                if (task == null) {
//                    player.message(Color.RED.wrap("You need a slayer task to enter cerberus's dungeon."));
//                    return false;
//                }
//                if (!Slayer.creatureMatches(player, 494)) {
//                    if (!task.matches(task_id)) {
//                        player.message(Color.RED.wrap("You need a slayer task to enter the cerberus's dungeon."));
//                        return false;
//                    }
//                } else {
                    teleportPlayer(player, destination);
                    return true;
//                }
            }
            if (option == 2) {
                peek(player, region);
            }
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
                    sendNpcChat(5870, Expression.HAPPY, "No adventurers are inside the cave.");
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
                    sendNpcChat(5870, Expression.HAPPY, count.size() + " adventurer is inside the cave.");
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

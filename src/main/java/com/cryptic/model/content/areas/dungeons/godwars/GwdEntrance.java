package com.cryptic.model.content.areas.dungeons.godwars;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.ROPE_26370;

public class GwdEntrance extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == 26419) {//TODO find id
                boolean godwarsDungeon = player.getAttribOr(AttributeKey.GOD_WARS_DUNGEON, false);
                Chain.bound(null).runFn(1, () -> {
                    //Check to see if the player has a rope or a grapple
                    if (!godwarsDungeon) {
                        if (player.inventory().contains(new Item(954, 1))) {
                            player.putAttrib(AttributeKey.GOD_WARS_DUNGEON, true);
                            player.inventory().remove(new Item(954, 1), true);
                        } else {
                            player.message("You aren't carrying a rope with you.");
                        }
                    } else {
                        player.animate(828);
                        Chain.bound(null).runFn(1, () -> player.teleport(new Tile(2882, 5311, 2)));
                    }
                });
                return true;
            }
            //Rope to leave the dungeon
            if(obj.getId() == ROPE_26370) {
                player.animate(828);
                Chain.bound(null).runFn(1, () -> player.teleport(new Tile(2916, 3746, 0)));
                return true;
            }
        }
        return false;
    }
}

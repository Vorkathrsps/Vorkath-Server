package com.cryptic.model.entity.npc.droptables;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.Tile;

import static com.cryptic.model.content.collection_logs.LogType.BOSSES;
import static com.cryptic.model.content.collection_logs.LogType.OTHER;

/**
 * Created by Bart on 10/6/2015.
 */
public interface Droptable {

    public void reward(NPC npc, Player killer);

    default void drop(NPC npc, Player player, Item item) {
        drop(npc, npc.tile(), player, item);
    }

    default void drop(NPC npc, Tile tile, Player player, Item item) {
        GroundItemHandler.createGroundItem(new GroundItem(item, tile, player));
        BOSSES.log(player, npc.id(), item);
        OTHER.log(player, npc.id(), item);
    }

}

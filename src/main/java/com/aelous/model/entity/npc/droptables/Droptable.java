package com.aelous.model.entity.npc.droptables;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;

import static com.aelous.model.content.collection_logs.LogType.BOSSES;
import static com.aelous.model.content.collection_logs.LogType.OTHER;

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

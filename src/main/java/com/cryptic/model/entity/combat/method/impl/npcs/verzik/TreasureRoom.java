package com.cryptic.model.entity.combat.method.impl.npcs.verzik;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;

public class TreasureRoom extends PacketInteraction {
    int[] treasure_loot = new int[]{
        33086,
        33087,
        33088,
        33089,
        33090
    };
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == TREASURE_ROOM) {
            player.teleport(3237, 4307, player.getTheatreInstance().getzLevel());
            return true;
        }
        if (ArrayUtils.contains(treasure_loot, object.getId())) {
            object.replaceWith(new GameObject(32994, object.tile(), object.getType(), object.getRotation()), false);
            return true;
        }
        return false;
    }
}

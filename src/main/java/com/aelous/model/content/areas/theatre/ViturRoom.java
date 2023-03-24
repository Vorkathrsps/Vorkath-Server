package com.aelous.model.content.areas.theatre;

import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.content.raids.RaidsNpc;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.VERZIK_VITUR_8369;

public class ViturRoom extends PacketInteraction {

    private final int THEATRE_ENTRACE = 32653;

    public static final Area VERZIK_AREA = new Area(3135, 4288, 3263, 4351);

    //32653, 3678, 3216

    public static final Tile ENTRANCE_POINT = new Tile(3168, 4303);

    public static final Tile VERZIK_SPAWN_TILE = new Tile(3168, 4316);

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj != null && obj.getId() == THEATRE_ENTRACE) { // tob lobby 32653, 3678, 3216
            boolean insideCheck = player.getAbsX() >= 3138;
            player.lockMovement();
            Chain.bound(null).runFn(1, () -> {
                player.unlock();
                player.getCombat().clearDamagers();
                var instance = InstancedAreaManager.getSingleton().createInstancedArea(VERZIK_AREA);
                instance.addPlayer(player);
                player.teleport(ENTRANCE_POINT.transform(0, 0, instance.getzLevel()));
                NPC verzik = new RaidsNpc(VERZIK_VITUR_8369, new Tile(3166, 4323, instance.getzLevel()), 1, false);
                verzik.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);
                verzik.spawn();
                instance.addNpc(verzik);
            });
        }
        return false;
    }

}


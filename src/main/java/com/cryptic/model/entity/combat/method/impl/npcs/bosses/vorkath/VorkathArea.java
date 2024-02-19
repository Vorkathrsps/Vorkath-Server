package com.cryptic.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.cryptic.model.World;
import com.cryptic.model.content.instance.InstancedAreaManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

public class VorkathArea extends PacketInteraction {

    public static final Tile ENTRANCE_POINT = new Tile(2272, 4054);
    public static final Area VORKATH_AREA = new Area(2260, 4054, 2286, 4077);

    private static final Animation POKE_ANIMATION = new Animation(827, Priority.HIGH);
    private static final Animation WAKE_ANIMATION = new Animation(7950, Priority.HIGH);

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        //Vorkath ice chunks
        if (object.getId() == 31990) {
            if (player.tile().y == 4052) {
                var instance = InstancedAreaManager.getSingleton().createInstancedArea(VORKATH_AREA);
                player.teleport(ENTRANCE_POINT.transform(0, 0, instance.getzLevel()));
                player.setInstancedArea(instance);
                instance.addPlayer(player);

                //Create a Vorkath instance
                var tile = ENTRANCE_POINT.transform(-3, 8, instance.getzLevel());
                var sleepingVorkath = new NPC(VORKATH_8059, tile);
                sleepingVorkath.spawnDirection(7);
                sleepingVorkath.getMovementQueue().setBlockMovement(true);
                World.getWorld().registerNpc(sleepingVorkath);
                instance.addNpc(sleepingVorkath);

            } else {
                player.teleport(2272, 4052, 0);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == 8059 && option == 1) {
            if (npc.locked() || npc.id() != VORKATH_8059)
                return true;

            Chain.noCtx().runFn(1, () -> {
                player.animate(POKE_ANIMATION);
                player.message("You poke the dragon..");
            }).then(1, () -> {
                npc.transmog(VORKATH_8058, false);
            }).then(1, () -> {
                npc.animate(WAKE_ANIMATION);
            }).then(7, () -> {
                npc.transmog(VORKATH_8061, false);
                npc.setPositionToFace(player.tile());
                npc.setCombatInfo(World.getWorld().combatInfo(npc.id()));
                npc.setHitpoints(npc.getCombatInfo().stats.hitpoints);
                npc.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));
                npc.getMovementQueue().setBlockMovement(true);
                npc.setCombatMethod(new VorkathCombat());
                npc.getCombat().attack(player);
            });
            return true;
        }
        return false;
    }
}

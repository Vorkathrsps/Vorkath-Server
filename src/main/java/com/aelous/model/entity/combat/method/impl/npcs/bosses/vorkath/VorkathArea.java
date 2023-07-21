package com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.FaceDirection;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Tuple;
import com.aelous.utility.chainedwork.Chain;

import java.util.function.BooleanSupplier;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

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
                player.setInstance(instance);
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
                npc.transmog(VORKATH_8058);
            }).then(1, () -> {
                npc.animate(WAKE_ANIMATION);
            }).then(7, () -> {
                npc.transmog(VORKATH_8061);
                npc.setPositionToFace(player.tile());
                npc.setCombatInfo(World.getWorld().combatInfo(npc.id()));
                npc.setHitpoints(npc.getCombatInfo().stats.hitpoints);
                npc.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));
                npc.getMovementQueue().setBlockMovement(true);
                npc.setCombatMethod(new Vorkath());
                npc.getCombat().attack(player);
            });
            return true;
        }
        return false;
    }
}

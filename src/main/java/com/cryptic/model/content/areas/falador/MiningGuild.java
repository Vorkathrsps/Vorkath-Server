package com.cryptic.model.content.areas.falador;

import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

/**
 * @author Origin
 * mei 06, 2020
 */
public class MiningGuild extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == 30364) {
                door(player, obj);
                return true;
            }
        }
        return false;
    }

    private void door(Player player, GameObject door) {
        if (player.getSkills().level(Skills.MINING) < 60) {
            DialogueManager.npcChat(player, Expression.HAPPY, NpcIdentifiers.GUARD_6561, "Sorry, but you need level 60 Mining to get in there.");
        } else {
            player.lock();

            if (player.tile().y >= 9757) {
                if (!player.tile().equals(door.tile().transform(0, 1, 0))) {
                    player.getMovementQueue().walkTo(door.tile().transform(0, 1, 0));
                }

                GameObject old = new GameObject(door.getId(), door.tile(), door.getType(), door.getRotation());
                GameObject spawned = new GameObject(door.getId(), new Tile(3046, 9757), door.getType(), 2);
                ObjectManager.removeObj(old);
                ObjectManager.addObj(spawned);
                Chain.bound(player).name("MiningGuildDoor1Task").runFn(2, () -> {
                    ObjectManager.removeObj(spawned);
                    ObjectManager.addObj(old);
                });

                player.getMovementQueue().interpolate(3046, 9756, MovementQueue.StepType.FORCED_WALK);
                Chain.bound(player).name("MiningGuildDoor2Task").runFn(1, player::unlock);
            } else {
                if (!player.tile().equals(door.tile().transform(0, 0, 0))) {
                    player.getMovementQueue().walkTo(door.tile().transform(0, 0, 0));
                }
                GameObject old = new GameObject(door.getId(), door.tile(), door.getType(), door.getRotation());
                GameObject spawned = new GameObject(door.getId(), new Tile(3046, 9757), door.getType(), 2);
                ObjectManager.removeObj(old);
                ObjectManager.addObj(spawned);
                Chain.bound(player).name("MiningGuildDoor3Task").runFn(2, () -> {
                    ObjectManager.removeObj(spawned);
                    ObjectManager.addObj(old);
                });
                player.getMovementQueue().interpolate(3046, 9757, MovementQueue.StepType.FORCED_WALK);
                Chain.bound(player).name("MiningGuildDoor4Task").runFn(1, player::unlock);
            }
        }
    }

}

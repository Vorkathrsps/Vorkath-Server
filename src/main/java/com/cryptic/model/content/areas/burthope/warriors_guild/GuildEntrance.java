package com.cryptic.model.content.areas.burthope.warriors_guild;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_24318;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_24319;

/**
 * @author Origin | March, 26, 2021, 09:49
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class GuildEntrance extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(object.getId() == DOOR_24318) {
            door(player);
            return true;
        }
        return false;
    }

    private void door(Player player) {
        GameObject door = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);

        int attack_lvl = player.getSkills().level(Skills.ATTACK);
        int strength_lvl = player.getSkills().level(Skills.STRENGTH);

        if (player.tile().x >= 2877) {
            if (attack_lvl + strength_lvl < 130) {
                player.getDialogueManager().sendStatement( "You are not a high enough level to enter the guild. Work on your", "combat skills some more. You need to have a combined attack and", "strength level of at least 130.");
            } else {
                if (!player.tile().equals(door.tile().transform(0, 0, 0))) {
                    player.getMovementQueue().walkTo(door.tile().transform(0, 0, 0));
                }
                player.lock();

                GameObject old = new GameObject(door.getId(), door.tile(), door.getType(), door.getRotation());
                GameObject spawned = new GameObject(DOOR_24319, new Tile(2876, 3546), door.getType(), 1);
                ObjectManager.removeObj(old);
                ObjectManager.addObj(spawned);
                Chain.bound(player).name("WarriorGuildDoorEnterTask").runFn(2, () -> {
                    ObjectManager.removeObj(spawned);
                    ObjectManager.addObj(old);
                });
                player.getMovementQueue().interpolate(2876, 3546, MovementQueue.StepType.FORCED_WALK);
                Chain.bound(player).name("WarriorGuildDoorWalk1Task").waitForTile(new Tile(2876, 3546), () -> Chain.bound(player).name("WarriorGuildDoorWalk2Task").runFn(1, player::unlock));
            }
        } else {
            if (!player.tile().equals(door.tile().transform(-1, 0, 0))) {
                player.getMovementQueue().walkTo(door.tile().transform(-1, 0, 0));
            }
            player.lock();
            GameObject old = new GameObject(door.getId(), door.tile(), door.getType(), door.getRotation());
            GameObject spawned = new GameObject(DOOR_24319, new Tile(2876, 3546), door.getType(), 1);
            ObjectManager.removeObj(old);
            ObjectManager.addObj(spawned);
            Chain.bound(player).name("WarriorGuildDoorExitTask").runFn(2, () -> {
                ObjectManager.removeObj(spawned);
                ObjectManager.addObj(old);
            });

            player.getMovementQueue().interpolate(2877, 3546, MovementQueue.StepType.FORCED_WALK);
            Chain.bound(player).name("WarriorGuildDoorWalk3Task").waitForTile(new Tile(2877, 3546), () -> Chain.bound(player).name("WarriorGuildDoorWalk4Task").runFn(1, player::unlock));
        }
    }
}

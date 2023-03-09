package com.aelous.model.content.areas.dungeons.godwars;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.BIG_DOOR;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.ICE_BRIDGE;

public class Obstacles extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == ICE_BRIDGE) {
                if (player.hp() < 70 || player.getSkills().xpLevel(Skills.HITPOINTS) < 70) {
                    player.message("Without at least 70 Hitpoints, you would never survive the icy water."); // TODO correct message
                    return false;
                }

                if (player.tile().y >= 5344) {
                    Chain.bound(null).runFn(1, () -> {
                        // Go to the right spot if we're not there
                        if (!player.tile().equals(obj.tile().transform(0, 0, 0))) {
                            player.smartPathTo(obj.tile().transform(0, 0, 0));
                        }
                    }).then(1, () -> {
                        player.teleport(2885, 5343, 2);
                        player.graphic(68);
                        player.looks().render(6993, 6993, 6993, 6993, 6993, 6993, 6993);
                    }).then(6, () -> {
                        player.looks().resetRender();
                        player.teleport(2885, 5332, 2);
                        player.message("Dripping, you climb out of the water.");
                    });
                } else {
                    Chain.bound(null).runFn(1, () -> {
                        // Go to the right spot if we're not there
                        if (!player.tile().equals(obj.tile().transform(0, 0, 0))) {
                            player.smartPathTo(obj.tile().transform(0, 0, 0));
                        }
                    }).then(1, () -> {
                        player.teleport(2885, 5334, 2);
                        player.graphic(68);
                        player.looks().render(6993, 6993, 6993, 6993, 6993, 6993, 6993);
                    }).then(6, () -> {
                        player.teleport(2885, 5345, 2);
                        if (player.getSkills().level(Skills.PRAYER) > 0) {
                            player.message("Dripping, you climb out of the water.");
                            player.message("The extreme evil of this area leaves your Prayer drained.");
                            player.getSkills().setLevel(Skills.PRAYER, 0);
                        } else {
                            player.message("Dripping, you climb out of the water.");
                        }
                    });
                }
                return true;
            }

            if(obj.getId() == BIG_DOOR) {
                if (!player.inventory().contains(2347)) {
                    player.message("You need a hammer to ring the gong.");
                } else {
                    if (player.tile().x >= 2851) {
                        Chain.bound(null).runFn(1, () -> {
                            player.animate(7012);
                        }).then(2, () -> {
                            GameObject opendoor = new GameObject(obj.getId(), obj.tile().transform(0, -2, 0), obj.getType(), 1);
                            GameObject closedoor = new GameObject(opendoor.getId(), opendoor.tile().transform(0, 0, 0), opendoor.getType(), 0);
                            ObjectManager.openAndCloseDoor(opendoor, closedoor);
                        }).then(2, () -> {
                            //Walk trough
                            player.getMovementQueue().walkTo(new Tile(2850, 5333, 2));
                        });
                    } else {
                        //System.out.println("NO");
                   /* GameObject bigDoor = new GameObject(obj.getId(), obj.getPosition(), obj.getType(), obj.getFace());
                    GameObject rotatedDoor = new GameObject(bigDoor.getId(), bigDoor.getPosition().transform(-1, 0, 0), bigDoor.getType(), 3);
                    //Remove door
                    ObjectManager.perform(bigDoor, ObjectManager.OperationType.DESPAWN);
                    //Spawn door but rotated
                    ObjectManager.perform(rotatedDoor, ObjectManager.OperationType.SPAWN);
                    //Remove rotated door
                    ObjectManager.perform(rotatedDoor, ObjectManager.OperationType.DESPAWN);
                    //Door changed back to normal once we walked trough
                    ObjectManager.perform(bigDoor, ObjectManager.OperationType.SPAWN);*/

                        //Walk trough
                   /* SmartPathFinder finder = new SmartPathFinder();
                    Path path = finder.find(player.getPosition(), new Position(2850, 5333, 2), 1);
                    player.getMovementQueue().walk(path.getMoves());*/
                    }
                }
                return true;
            }
        }
        return false;
    }
}

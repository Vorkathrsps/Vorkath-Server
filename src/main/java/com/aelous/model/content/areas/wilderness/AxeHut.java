package com.aelous.model.content.areas.wilderness;

import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.DOOR_11726;

public class AxeHut extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(obj.getId() == DOOR_11726) {
            if (option == 1) { // "Open"
                open(player, obj);
                return true;
            } else if (option == 2) { // pick-lock
                picklock(player, obj);
                return true;
            }
        }
        return false;
    }

    private static final int OPENED_GATE = 1548;

    private void picklock(Player player, GameObject obj) {
        if (WildernessArea.inside_axehut(player.tile())) {
            player.message("The door is already unlocked.");
            return;
        }

        // Not on the target tile. Cos of doors, yakno.
        if (!player.tile().equals(obj.tile())) {
            player.getMovementQueue().walkTo(new Tile(obj.tile().x, obj.tile().y));
        }

        if (player.getSkills().level(Skills.THIEVING) < 37) {
            player.message("You need a Thieving level of 37 to pick lock this door.");
            return;
        }

        // North side
        if (player.tile().y == 3963) {
            //Check if the player has a lockpick
            if (player.inventory().contains(1523)) {
                player.message("You attempt to pick the lock.");

                //Create a chance to picklock the door
                if (Utils.random(100) >= 50) {
                    GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
                    GameObject spawned = new GameObject(OPENED_GATE, new Tile(3191, 3962), obj.getType(), 0);
                    spawned.interactAble(false);
                    ObjectManager.removeObj(old);
                    ObjectManager.addObj(spawned);
                    Chain.bound(null).name("AxeHutNorthDoorTask").runFn(2, () -> {
                        ObjectManager.removeObj(spawned);
                        ObjectManager.addObj(old);
                    });
                    //Move the player outside of the axe hut
                    player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, -1).y, MovementQueue.StepType.FORCED_WALK);

                    player.message("You manage to pick the lock.");
                    //Add thieving experience for a successful lockpick
                    player.getSkills().addXp(Skills.THIEVING, 22);
                    return;
                } else {
                    player.message("You fail to pick the lock.");
                    return;
                }
            } else {
                player.message("You need a lockpick for this lock.");
            }
            player.setPositionToFace(obj.tile());
        } else if (player.tile().y == 3957) {
            // South side
            //Check if the player has a lockpick
            if (player.inventory().contains(1523)) {

                player.message("You attempt to pick the lock.");
                //Create a chance to picklock the door
                if (Utils.random(100) >= 50) {
                    GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
                    GameObject spawned = new GameObject(OPENED_GATE, new Tile(3190, 3958), obj.getType(), 2);
                    spawned.interactAble(false);
                    ObjectManager.removeObj(old);
                    ObjectManager.addObj(spawned);
                    Chain.bound(null).name("AxeHutSouthDoorTask").runFn(2, () -> {
                        ObjectManager.removeObj(spawned);
                        ObjectManager.addObj(old);
                    });

                    //Move the player outside of the axe hut
                    player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, 1).y, MovementQueue.StepType.FORCED_WALK);
                    player.message("You manage to pick the lock.");
                    //Add thieving experience for a successful lockpick
                    player.getSkills().addXp(Skills.THIEVING, 22);
                    return;
                } else {
                    //Send the player a message
                    player.message("You fail to pick the lock.");
                }
            } else {
                player.message("You need a lockpick for this lock.");
            }
            player.setPositionToFace(player.tile().transform(0, 2));
        } else if (player.tile().y == 3958 || player.tile().y == 3962) {
            //Send the player a message
            player.message("The door is already unlocked.");
        }
    }

    private void open(Player player, GameObject obj) {
        if (player.tile().equals(obj.tile())) {
            player.message("This door is locked."); // You're outside
            return;
        }
        if (player.tile().y > obj.tile().y) {
            player.message("You go through the door.");
            //Replace the object with an open door
            GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
            GameObject spawned = new GameObject(OPENED_GATE, new Tile(3190, 3958), obj.getType(), 2);
            spawned.interactAble(false);
            ObjectManager.removeObj(old);
            ObjectManager.addObj(spawned);
            Chain.bound(null).name("AxeHutDoor1Task").runFn(2, () -> {
                ObjectManager.removeObj(spawned);
                ObjectManager.addObj(old);
            });
            //Move the player outside of the axe hut
            player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, -1).y, MovementQueue.StepType.FORCED_WALK);
        }

        if (player.tile().y < obj.tile().y) {
            player.message("You go through the door.");

            //Replace the object with an open door
            GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
            GameObject spawned = new GameObject(OPENED_GATE, new Tile(3191, 3962), obj.getType(), 0);
            ObjectManager.removeObj(old);
            ObjectManager.addObj(spawned);
            spawned.interactAble(false);
            Chain.bound(null).name("AxeHutDoor2Task").runFn(2, () -> {
                ObjectManager.removeObj(spawned);
                ObjectManager.addObj(old);
            });

            //Move the player outside of the axe hut
            player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, 1).y, MovementQueue.StepType.FORCED_WALK);
        }
    }
}

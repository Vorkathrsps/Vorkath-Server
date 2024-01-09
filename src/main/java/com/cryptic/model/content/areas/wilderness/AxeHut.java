package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_11726;

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
                    //spawned.interactAble(false);
                    ObjectManager.replace(old, spawned, 5);
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
            int sizeX = obj.definition().sizeX;
            int sizeY = obj.definition().sizeY;
            boolean inversed = (obj.getRotation() & 0x1) != 0;
            int faceCoordX = obj.x * 2 + (inversed ? sizeY : sizeX);
            int faceCoordY = obj.y * 2 + (inversed ? sizeX : sizeY);
            Tile position = new Tile(faceCoordX, faceCoordY);
            player.setPositionToFace(position);
        } else if (player.tile().y == 3957) {
            // South side
            //Check if the player has a lockpick
            if (player.inventory().contains(1523)) {

                player.message("You attempt to pick the lock.");
                //Create a chance to picklock the door
                if (Utils.random(100) >= 50) {
                    GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
                    GameObject spawned = new GameObject(OPENED_GATE, new Tile(3190, 3958), obj.getType(), 2);
                    //spawned.interactAble(false);
                    ObjectManager.replace(old, spawned, 5);
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
            int sizeX = obj.definition().sizeX;
            int sizeY = obj.definition().sizeY;
            boolean inversed = (obj.getRotation() & 0x1) != 0;
            int faceCoordX = obj.x * 2 + (inversed ? sizeY : sizeX);
            int faceCoordY = obj.y * 2 + (inversed ? sizeX : sizeY);
            Tile position = new Tile(faceCoordX, faceCoordY);
            player.setPositionToFace(position);
        } else if (player.tile().y == 3958 || player.tile().y == 3962) {
            //Send the player a message
            player.message("The door is already unlocked.");
        }
    }

    private void open(Player player, GameObject obj) {
        if (player.tile().equals(obj.tile())) {
            player.message("This door is locked.");
            return;
        }
        if (player.tile().y > obj.tile().y) {
            player.message("You go through the door.");

            GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
            System.out.println("obj: " + obj.getId() + " tile: " + obj.tile() + " type: " + obj.getType() + " rotation: " + obj.getRotation());
            GameObject spawned = new GameObject(OPENED_GATE, new Tile(3190, 3958), obj.getType(), 2);
            System.out.println("obj: " + spawned.getId() + " tile: " + spawned.tile() + " type: " + spawned.getType() + " rotation: " + spawned.getRotation());

            //spawned.interactAble(false);
            ObjectManager.replace(old, spawned, 5);

            player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, -1).y, MovementQueue.StepType.FORCED_WALK);
        }

        if (player.tile().y < obj.tile().y) {
            player.message("You go through the door.");

            GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
            System.out.println("obj: " + obj.getId() + " tile: " + obj.tile() + " type: " + obj.getType() + " rotation: " + obj.getRotation());
            GameObject spawned = new GameObject(OPENED_GATE, new Tile(3191, 3962), obj.getType(), 0);
            ObjectManager.replace(old, spawned, 5);

            player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, 1).y, MovementQueue.StepType.FORCED_WALK);
        }
    }
}

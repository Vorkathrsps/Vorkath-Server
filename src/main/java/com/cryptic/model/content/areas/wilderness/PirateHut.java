package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_11727;

/**
 * @author Origin | June, 19, 2021, 18:58
 * 
 */
public class PirateHut extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == DOOR_11727) {
            if (obj.tile().distance(new Tile(3041, 3957)) <= 10) { //Pirate hut doors
                if (option == 1)
                    pirateHutFirst(player, obj);
                if (option == 2)
                    pirateHutSecond(player, obj);
            }
            return true;
        }
        return false;
    }

    // Option 1 (open)
    private void pirateHutFirst(Player player, GameObject obj) {
        var closedId = obj.getId();
        var northDoorTile = new Tile(3041, 3959);
        var eastDoorTile = new Tile(3044, 3956);
        var westDoorTile = new Tile(3038, 3956);

        var eastDoor = obj.tile().equals(eastDoorTile);
        var westDoor = obj.tile().equals(westDoorTile);
        var northDoor = obj.tile().equals(northDoorTile);

        var rotation = westDoor ? 1 : northDoor ? 2 : 3;
        var spawnTile = westDoor ? new Tile(3037, 3956) : northDoor ? new Tile(3041, 3960) : new Tile(3045, 3956);
        var openDoor = new GameObject(spawnTile, 11728, obj.getType(), rotation);
        openDoor.interactAble(false); // Because it's just a temporary door. Alternatively you could find the alt object-id with no "open" option.
        var x = 0;
        if (WildernessArea.inside_pirates_hideout(player.tile())) {
            if (northDoor) {
                x = player.tile().transform(0, 0).x;
            } else if (westDoor) {
                x = player.tile().transform(-1, 0).x;
            } else {
                x = player.tile().transform(1, 0).x;
            }
        }

        var z = 0;
        if (WildernessArea.inside_pirates_hideout(player.tile())) {
            if (northDoor) {
                z = player.tile().transform(0, 1).y;
            } else if (westDoor) {
                z = player.tile().transform(0, 0).y;
            } else {
                z = player.tile().transform(0, 0).y;
            }
        }

        if (!WildernessArea.inside_pirates_hideout(player.tile())) {
            player.message("The door is locked.");
            return;
        } else {
            // Inside
            if (!player.tile().equals(obj.tile())) {
                Chain.bound(null).runFn(1, () -> {
                    player.getMovementQueue().interpolate(obj.tile().x, obj.tile().y, MovementQueue.StepType.FORCED_WALK);
                });
            }
        }

        player.message("You go through the door.");

        //Replace the object with an open door
        var closedOgDoor = new GameObject(obj.tile(), obj.getId(), obj.getType(), obj.getRotation());
        ObjectManager.removeObj(closedOgDoor);
        ObjectManager.addObj(openDoor);
        int finalX = x;
        int finalZ = z;
        player.stepAbs(finalX, finalZ, MovementQueue.StepType.FORCED_WALK);
        Chain.bound(null).runFn(2, () -> {
            ObjectManager.removeObj(openDoor);
            closedOgDoor.setId(closedId);
        });
    }

    // Option 2 (picklock)
    private void pirateHutSecond(Player player, GameObject obj) {
        var closedId = obj.getId();
        var northDoorTile = new Tile(3041, 3959);
        var eastDoorTile = new Tile(3044, 3956);
        var westDoorTile = new Tile(3038, 3956);

        var eastDoor = obj.tile().equals(eastDoorTile);
        var westDoor = obj.tile().equals(westDoorTile);
        var northDoor = obj.tile().equals(northDoorTile);

        var rotation = westDoor ? 1 : northDoor ? 2 : 3;
        var spawnTile = westDoor ? new Tile(3037, 3956) : northDoor ? new Tile(3041, 3960) : new Tile(3045, 3956);
        var spawnObj = new GameObject(spawnTile, 11728, obj.getType(), rotation);
        spawnObj.interactAble(false);

        var x = 0;
        if (WildernessArea.inside_pirates_hideout(player.tile())) {
            if (northDoor) {
                x = player.tile().transform(0, 0).x;
            } else if (westDoor) {
                x = player.tile().transform(-1, 0).x;
            } else {
                x = player.tile().transform(1, 0).x;
            }
        } else {
            if (northDoor) {
                x = player.tile().transform(0, 0).x;
            } else if (westDoor) {
                x = player.tile().transform(1, 0).x;
            } else {
                x = player.tile().transform(-1, 0).x;
            }
        }

        var z = 0;
        if (WildernessArea.inside_pirates_hideout(player.tile())) {
            if (northDoor) {
                z = player.tile().transform(0, 1).y;
            } else if (westDoor) {
                z = player.tile().transform(0, 0).y;
            } else {
                z = player.tile().transform(0, 0).y;
            }
        } else {
            if (northDoor) {
                z = player.tile().transform(0, -1).y;
            } else if (westDoor) {
                z = player.tile().transform(0, 0).y;
            } else {
                z = player.tile().transform(0, 0).y;
            }
        }

        //If the player is inside the hut
        if (WildernessArea.inside_pirates_hideout(player.tile())) {
            player.message("The door is already unlocked.");
            return;
        }
        if (player.getSkills().xpLevel(Skills.THIEVING) < 39) {
            player.message("You need a Thieving level of 39 to pick lock this door.");
            return;
        }
        //Check if the player has a lockpick
        if (player.inventory().contains(ItemIdentifiers.LOCKPICK)) {
            player.message("You attempt to pick the lock.");

            //Create a chance to picklock the door
            if (World.getWorld().random(100) >= 50) {
                var old = new GameObject(obj.tile(), obj.getId(), obj.getType(), obj.getRotation());
                ObjectManager.removeObj(old);
                ObjectManager.addObj(spawnObj);
                int finalX = x;
                int finalZ = z;
                player.message("You manage to pick the lock.");
                player.stepAbs(finalX, finalZ, MovementQueue.StepType.FORCED_WALK);
                Chain.bound(null).runFn(2, () -> {
                    ObjectManager.removeObj(spawnObj);
                    old.setId(closedId);
                    player.getSkills().addXp(Skills.THIEVING, 22.0);
                });
            } else {
                //Send the player a message
                player.message("You fail to pick the lock.");
            }
        } else {
            player.message("You need a lockpick for this lock.");
        }
    }
}

package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.PlayerMovement;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.function.BooleanSupplier;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_11726;

public class AxeHut extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == DOOR_11726) {
            if (option == 2) {
                if (!requirement(player)) return false;
                BooleanSupplier fail = () -> !roll(player);
                GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
                GameObject spawned = new GameObject(OPENED_GATE, new Tile(obj.tile().getX(), obj.tile().getY()), obj.getType(), 0);
                Tile position = player.tile();
                PlayerMovement queue = player.getMovementQueue();
                if (!position.equals(obj.tile())) {
                    queue.walkTo(new Tile(obj.tile().x, obj.tile().y));
                }
                int tile_y = position.y;
                if (tile_y == 3957) {
                    var transform = position.transform(0, 1).y;
                    openGate(player, old, spawned, transform, fail);
                    return true;
                } else if (tile_y == 3958) {
                    var transform = position.transform(0, -1).y;
                    openGate(player, old, spawned, transform, fail);
                    return true;
                } else if (tile_y == 3962) {
                    var transform = position.transform(0, 1).y;
                    openGate(player, old, spawned, transform, fail);
                    return true;
                } else if (tile_y == 3963) {
                    var transform = position.transform(0, -1).y;
                    openGate(player, old, spawned, transform, fail);
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    private static boolean roll(Player player) {
        if (!World.getWorld().rollDie(2, 1)) {
            player.message(Color.RED.wrap("You failed to pick the lock."));
            return false;
        }
        return true;
    }

    private static boolean requirement(Player player) {
        if (player.getSkills().level(Skills.THIEVING) < 37) {
            player.message("You need a Thieving level of 37 to pick lock this door.");
            return false;
        }
        return true;
    }

    private static void openGate(Player player, GameObject old, GameObject spawned, int transform, BooleanSupplier fail) {
        player.message(Color.RUNITE.wrap("You attempt to pick the lock."));
        Chain.bound(null).name("AxeHutSouthDoorTask").runFn(2, () -> {
        }).cancelWhen(fail).runFn(1, () -> {
            player.lockMoveDamageOk();
            spawned.setId(old.getId());
        }).then(1, () -> {
            player.stepAbs(player.tile().x, transform, MovementQueue.StepType.FORCED_WALK);
            player.message(Color.BLUE.wrap("You manage to pick the lock."));
        }).then(2, () -> {
            player.unlock();
            old.setId(spawned.getId());
            player.getSkills().addXp(Skills.THIEVING, 22);
        });
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


        var originalId = obj.getId();
        var newId = OPENED_GATE;

        // North side
        if (player.tile().y == 3963) {
            //Check if the player has a lockpick
            if (player.inventory().contains(1523)) {
                player.message("You attempt to pick the lock.");

                //Create a chance to picklock the door
                if (Utils.random(100) >= 50) {
                    Chain.bound(null).runFn(1, () -> {
                        obj.setId(newId);
                    }).then(1, () -> {
                        player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, -1).y, MovementQueue.StepType.FORCED_WALK);
                        player.message("You manage to pick the lock.");
                    }).then(3, () -> {
                        obj.setId(originalId);
                        player.getSkills().addXp(Skills.THIEVING, 22);
                    });
                    return;
                } else {
                    player.message("You fail to pick the lock.");
                    return;
                }
            } else {
                player.message("You need a lockpick for this lock.");
            }
            setPosition(player, obj);
        } else if (player.tile().y == 3957) {
            // South side
            //Check if the player has a lockpick
            if (player.inventory().contains(1523)) {

                player.message("You attempt to pick the lock.");
                //Create a chance to picklock the door
                if (Utils.random(100) >= 50) {
                    Chain.bound(null).runFn(1, () -> {
                        obj.setId(newId);
                    }).then(1, () -> {
                        player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, 1).y, MovementQueue.StepType.FORCED_WALK);
                        player.message("You manage to pick the lock.");
                    }).then(3, () -> {
                        obj.setId(originalId);
                        player.getSkills().addXp(Skills.THIEVING, 22);
                    });
                    return;
                } else {
                    //Send the player a message
                    player.message("You fail to pick the lock.");
                }
            } else {
                player.message("You need a lockpick for this lock.");
            }
            setPosition(player, obj);
        } else if (player.tile().y == 3958 || player.tile().y == 3962) {
            //Send the player a message
            player.message("The door is already unlocked.");
        }
    }

    private static void setPosition(Player player, GameObject obj) {
        int sizeX = obj.definition().sizeX;
        int sizeY = obj.definition().sizeY;
        boolean inversed = (obj.getRotation() & 0x1) != 0;
        int faceCoordX = obj.x * 2 + (inversed ? sizeY : sizeX);
        int faceCoordY = obj.y * 2 + (inversed ? sizeX : sizeY);
        Tile position = new Tile(faceCoordX, faceCoordY);
        player.setPositionToFace(position);
    }

    private void open(Player player, GameObject obj) {
        if (player.tile().equals(obj.tile())) {
            player.message("This door is locked.");
            return;
        }

        var originalId = 11726;
        var newId = OPENED_GATE;

        if (obj.getId() == originalId) {
            if (player.tile().y > obj.tile().y) {
                player.message("You go through the door.");

                Chain.bound(null).runFn(1, () -> {
                    obj.setId(newId);
                }).then(1, () -> {
                    player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, -1).y, MovementQueue.StepType.FORCED_WALK);
                }).then(3, () -> {
                    obj.setId(originalId);
                });
            }

            if (player.tile().y < obj.tile().y) {
                player.message("You go through the door.");
                Chain.bound(null).runFn(1, () -> {
                    obj.setId(newId);
                }).then(1, () -> {
                    player.getMovementQueue().interpolate(player.tile().x, player.tile().transform(0, 1).y, MovementQueue.StepType.FORCED_WALK);
                }).then(3, () -> {
                    obj.setId(originalId);
                });
            }
        }
    }
}

package com.cryptic.model.content.areas.zeah;

import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.ForceMovementTask;
import com.cryptic.core.task.impl.TickAndStop;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.HANDHOLDS;

/**
 * @author Origin
 * april 20, 2020
 */
public class HandHolds extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            Tile west_handholds_down = new Tile(1459, 3690);
            Tile wast_handholds_up = new Tile(1455, 3690);

            if (obj.getId() == HANDHOLDS) {
                if (obj.tile().equals(west_handholds_down)) {
                    if (!player.tile().equals(obj.tile().transform(1, 0, 0))) {
                        player.getMovementQueue().walkTo(obj.tile().transform(1, 0, 0));
                    }
                    player.setPositionToFace(new Tile(1462, 3690));
                    TaskManager.submit(new TickAndStop(1) {
                        @Override
                        public void executeAndStop() {
                            player.animate(1148, 20);
                            TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-6, 0), 25, 135, Direction.EAST.toInteger())));
                        }
                    });
                    TaskManager.submit(new TickAndStop(5) {
                        @Override
                        public void executeAndStop() {
                            player.teleport(west_handholds_down.x - 4, west_handholds_down.y);
                        }
                    });
                    return true;
                } else if (obj.tile().equals(wast_handholds_up)) {
                    if (!player.tile().equals(obj.tile().transform(-1, 0, 0))) {
                        player.getMovementQueue().walkTo(obj.tile().transform(-1, 0, 0));
                    }
                    player.setPositionToFace(new Tile(1462, 3690));

                    TaskManager.submit(new TickAndStop(1) {
                        @Override
                        public void executeAndStop() {
                            player.animate(1148, 15);
                            TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(6, 0), 25, 135, Direction.EAST.toInteger())));
                        }
                    });
                    TaskManager.submit(new TickAndStop(5) {
                        @Override
                        public void executeAndStop() {
                            player.teleport(wast_handholds_up.x + 4, wast_handholds_up.y);
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }
}

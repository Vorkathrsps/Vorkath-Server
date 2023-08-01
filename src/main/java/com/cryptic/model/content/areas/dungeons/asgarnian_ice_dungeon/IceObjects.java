package com.cryptic.model.content.areas.dungeons.asgarnian_ice_dungeon;

import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.TickAndStop;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

public class IceObjects extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == TRAPDOOR_1738) {
                // Trapdoor down
                if (obj.tile().equals(3008, 3150)) {
                    TaskManager.submit(new TickAndStop(1) {
                        @Override
                        public void executeAndStop() {
                            player.teleport(new Tile(3007, 9550));
                        }
                    });
                }
                return true;
            }
            if(obj.getId() == ICY_CAVERN) {
                TaskManager.submit(new TickAndStop(1) {
                    @Override
                    public void executeAndStop() {
                        player.teleport(new Tile(player.tile().x, 9562));
                    }
                });
                return true;
            }
            if(obj.getId() == ICY_CAVERN_10596) {
                TaskManager.submit(new TickAndStop(1) {
                    @Override
                    public void executeAndStop() {
                        player.teleport(new Tile(player.tile().x, 9555));
                    }
                });
                return true;
            }
        }
        return false;
    }
}

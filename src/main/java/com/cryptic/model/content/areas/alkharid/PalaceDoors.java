package com.cryptic.model.content.areas.alkharid;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

/**
 * @author Origin | April, 14, 2021, 18:18
 * 
 */
public class PalaceDoors extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == 1511 || obj.getId() == 1513) {
                if (obj.tile().equals(3293, 3167) || obj.tile().equals(3292, 3167)) {
                    openGate();
                    return true;
                }
            }

            if (obj.getId() == 1516 || obj.getId() == 1512) {
                if (obj.tile().equals(3292, 3167) || obj.tile().equals(3293, 3167)) {
                    closeGate();
                    return true;
                }
            }
        }
        return false;
    }

    private void openGate() {
        ObjectManager.removeObj(new GameObject(1513, new Tile(3293,3167),0,3));
        ObjectManager.removeObj(new GameObject(1511, new Tile(3292,3167),0,0));

        ObjectManager.addObj(new GameObject(1516, new Tile(3293,3167),0,2));
        ObjectManager.addObj(new GameObject(1512, new Tile(3292,3167),0,0));
    }

    private void closeGate() {
        ObjectManager.removeObj(new GameObject(1516, new Tile(3293,3167),0,2));
        ObjectManager.removeObj(new GameObject(1512, new Tile(3292,3167),0,0));

        ObjectManager.addObj(new GameObject(1513, new Tile(3293,3167),0,3));
        ObjectManager.addObj(new GameObject(1511, new Tile(3292,3167),0,3));
    }
}

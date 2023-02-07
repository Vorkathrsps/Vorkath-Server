package com.aelous.model.map.object.gates;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

public class Woodengates extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        // Opening gates
        if (obj.getId() == 1558 || obj.getId() == 1560) {
            if (obj.tile().equals(3080, 3501) || obj.tile().equals(3079, 3501)) {
                openEdgevilleGate();
                return true;
            }
        }

        // Opening gate
        if (obj.getId() == 21600) {
            if (obj.tile().equals(2326, 3802)) {
                openYaksGate();
                return true;
            }
        }

        // Closing gate
        if (obj.getId() == 21601) {
            if (obj.tile().equals(2326, 3802)) {
                closeYaksGate();
                return true;
            }
        }

        // Closing gates
        if (obj.getId() == 1559 || obj.getId() == 1567) {
            if (obj.tile().equals(3080, 3500) || obj.tile().equals(3080, 3499)) {
                closeEdgevilleGate();
                return true;
            }
        }
        return false;
    }

    private static void openEdgevilleGate() {

        GameObject gate1 = new GameObject(1558, new Tile(3080, 3501), 0, 3);
        GameObject gate2 = new GameObject(1560, new Tile(3079, 3501), 0, 3);

        GameObject replacement1 = new GameObject(1559, new Tile(3080, 3500), 0, 2);
        GameObject replacement2 = new GameObject(1567, new Tile(3080, 3499), 0, 2);

        // First remove old objects
        ObjectManager.removeObj(gate1);
        ObjectManager.removeObj(gate2);

        // Add new objects after
        ObjectManager.addObj(replacement1);
        ObjectManager.addObj(replacement2);
    }

    private static void closeEdgevilleGate() {
        GameObject gate1 = new GameObject(1559, new Tile(3080, 3500), 0, 2);
        GameObject gate2 = new GameObject(1567, new Tile(3080, 3499), 0, 2);

        GameObject replacement1 = new GameObject(1558, new Tile(3080, 3501), 0, 3);
        GameObject replacement2 = new GameObject(1560, new Tile(3079, 3501), 0, 3);

        // First remove old objects
        ObjectManager.removeObj(gate1);
        ObjectManager.removeObj(gate2);

        // Add new objects after
        ObjectManager.addObj(replacement1);
        ObjectManager.addObj(replacement2);
    }

    private static void openYaksGate() {
        ObjectManager.removeObj(new GameObject(21600, new Tile(2326, 3802), 0, 3));
        ObjectManager.addObj(new GameObject(21601, new Tile(2326, 3802), 0, 2));
    }

    private static void closeYaksGate() {
        ObjectManager.removeObj(new GameObject(21601, new Tile(2326, 3802), 0, 2));
        ObjectManager.addObj(new GameObject(21600, new Tile(2326, 3802), 0, 3));
    }
}

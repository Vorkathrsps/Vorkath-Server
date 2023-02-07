package com.aelous.model.map.object.gates;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

public class IronGates extends PacketInteraction {

    // 1 = north, 2 = east, 3 = south, 0 = west

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == 1727 || obj.getId() == 1728) {
            if (obj.tile().equals(3201, 3856) || obj.tile().equals(3202, 3856)) {
                openLavaDragonsGate();
                return true;
            } else if (obj.tile().equals(3071, 3856) || obj.tile().equals(3071, 3857)) {
                openLavaDragonsGate2();
                return true;
            } else if (obj.tile().x == 3008
                    && (obj.tile().y == 3849 || obj.tile().y == 3850)) { // open kbd cage gate
                openKbdCageGate(player, obj);
                return true;
            } else if (obj.tile().y == 3904
                    && (obj.tile().x == 3225 || obj.tile().x == 3224)) { // open midgate
                openMidgate();
                return true;
            } else if (obj.tile().y == 3896
                    && (obj.tile().x == 3336 || obj.tile().x == 3337)) { // open newgate
                openNewgate();
                return true;
            } else if (obj.tile().y == 3904
                    && (obj.tile().x == 2947 || obj.tile().x == 2948)) { // open icegate
                openIcegate();
                return true;
            } else if (obj.tile().y == 9917) {
                openEdgevilleDungeonGate(player);
                return true;
            } else if (obj.tile().equals(2935, 3451) || obj.tile().equals(2935, 3450)) {
                openTaverlyGate();
                return true;
            }
        }

        if (obj.getId() == 2143 || obj.getId() == 2144) {
            if (obj.tile().x == 2888 || obj.tile().x == 2889) {
                openTaverlyDungeonPrisonGate(player);
                return true;
            }
        }

        if (obj.getId() == 33572) {
            if (obj.tile().x == 3043 || obj.tile().x == 3789) {
                openWildyTowerRedDoor();
                return true;
            }
        }

        if (obj.getId() == 1568 || obj.getId() == 1569) {
            if (obj.tile().equals(3103, 9910)
                    || obj.tile().equals(3103, 9909)) { // edge dungeon gates 1
                openGateWest();
                return true;
            } else if (obj.tile().equals(3145, 9871)
                    || obj.tile().equals(3145, 9870)) { // edge dungeon gates towards hill giants
                openGateSouth();
                return true;
            } else if (obj.tile().equals(3075, 3867) || obj.tile().equals(3076, 3867)) {
                openLavaDragonsGate3();
                return true;
            } else if (obj.tile().equals(3105, 9944)
                    || obj.tile().equals(3106, 9944)) { // edge dungeon
                openGateNorth();
                return true;
            }
        }

        if (obj.getId() == 1571 || obj.getId() == 1572) {
            if (obj.tile().equals(3104, 9910) || obj.tile().equals(3104, 9909)) {
                closeGateWest();
                return true;
            } else if (obj.tile().equals(2936, 3451) || obj.tile().equals(2936, 3450)) {
                closeTaverlyGate();
                return true;
            } else if (obj.tile().equals(3146, 9871) || obj.tile().equals(3146, 9870)) {
                closeGateSouth();
                return true;
            } else if (obj.tile().equals(3106, 9944) || obj.tile().equals(3105, 9944)) {
                closeGateNorth();
                return true;
            } else if (obj.tile().equals(3202, 3856) || obj.tile().equals(3201, 3856)) {
                closeLavaDragonsGate();
                return true;
            } else if (obj.tile().equals(3071, 3857) || obj.tile().equals(3071, 3856)) {
                closeLavaDragonsGate2();
                return true;
            } else if (obj.tile().equals(3075, 3867) || obj.tile().equals(3076, 3867)) {
                closeLavaDragonsGate3();
                return true;
            } else if (obj.tile().x == 3008
                    && (obj.tile().y == 3849 || obj.tile().y == 3850)) { // close kbd cage gate
                closeKbdCageGate(player, obj);
                return true;
            } else if (obj.tile().y == 3904
                    && (obj.tile().x == 3225 || obj.tile().x == 3224)) { // close midgate
                closeMidgate();
                return true;
            } else if (obj.tile().y == 3896
                    && (obj.tile().x == 3336 || obj.tile().x == 3337)) { // close newgate
                closeNewgate();
                return true;
            } else if (obj.tile().y == 3904
                    && (obj.tile().x == 2947 || obj.tile().x == 2948)) { // close icegate
                closeIcegate();
            }
        }
        return false;
    }

    private static void openIcegate() {
        ObjectManager.removeObj(new GameObject(1727, new Tile(2948, 3904), 0, 3));
        ObjectManager.removeObj(new GameObject(1728, new Tile(2947, 3904), 0, 3));
        ObjectManager.addObj(new GameObject(1571, new Tile(2948, 3904), 0, 2));
        ObjectManager.addObj(new GameObject(1572, new Tile(2947, 3904), 0, 4));
    }

    private static void closeIcegate() {
        ObjectManager.removeObj(new GameObject(1571, new Tile(2948, 3904), 0, 2));
        ObjectManager.removeObj(new GameObject(1572, new Tile(2947, 3904), 0, 4));
        ObjectManager.addObj(new GameObject(1727, new Tile(2948, 3904), 0, 3));
        ObjectManager.addObj(new GameObject(1728, new Tile(2947, 3904), 0, 3));
    }

    private static void openNewgate() {
        ObjectManager.removeObj(new GameObject(1727, new Tile(3337, 3896), 0, 3));
        ObjectManager.removeObj(new GameObject(1728, new Tile(3336, 3896), 0, 3));
        ObjectManager.addObj(new GameObject(1571, new Tile(3337, 3896), 0, 2));
        ObjectManager.addObj(new GameObject(1572, new Tile(3336, 3896), 0, 4));
    }

    private static void closeNewgate() {
        ObjectManager.removeObj(new GameObject(1571, new Tile(3337, 3896), 0, 2));
        ObjectManager.removeObj(new GameObject(1572, new Tile(3336, 3896), 0, 4));
        ObjectManager.addObj(new GameObject(1727, new Tile(3337, 3896), 0, 3));
        ObjectManager.addObj(new GameObject(1728, new Tile(3336, 3896), 0, 3));
    }

    private static void openMidgate() {
        ObjectManager.removeObj(new GameObject(1727, new Tile(3225, 3904), 0, 3));
        ObjectManager.removeObj(new GameObject(1728, new Tile(3224, 3904), 0, 3));
        ObjectManager.addObj(new GameObject(1571, new Tile(3225, 3904), 0, 2));
        ObjectManager.addObj(new GameObject(1572, new Tile(3224, 3904), 0, 4));
    }

    private static void closeMidgate() {
        ObjectManager.removeObj(new GameObject(1571, new Tile(3225, 3904), 0, 2));
        ObjectManager.removeObj(new GameObject(1572, new Tile(3224, 3904), 0, 4));
        ObjectManager.addObj(new GameObject(1727, new Tile(3225, 3904), 0, 3));
        ObjectManager.addObj(new GameObject(1728, new Tile(3224, 3904), 0, 3));
    }

    private static void openGateNorth() {
        ObjectManager.removeObj(new GameObject(1568, new Tile(3105, 9944), 0, 1));
        ObjectManager.removeObj(new GameObject(1569, new Tile(3106, 9944), 0, 1));
        ObjectManager.addObj(new GameObject(1571, new Tile(3105, 9944), 0, 4));
        ObjectManager.addObj(new GameObject(1572, new Tile(3106, 9944), 0, 2));
    }

    private static void closeGateNorth() {
        ObjectManager.removeObj(new GameObject(1571, new Tile(3105, 9944), 0, 4));
        ObjectManager.removeObj(new GameObject(1572, new Tile(3106, 9944), 0, 2));
        ObjectManager.addObj(new GameObject(1568, new Tile(3105, 9944), 0, 1));
        ObjectManager.addObj(new GameObject(1569, new Tile(3106, 9944), 0, 1));
    }

    private static void openGateSouth() {
        ObjectManager.removeObj(new GameObject(1568, new Tile(3145, 9871), 0, 2));
        ObjectManager.removeObj(new GameObject(1569, new Tile(3145, 9870), 0, 2));
        ObjectManager.addObj(new GameObject(1571, new Tile(3146, 9871), 0, 1));
        ObjectManager.addObj(new GameObject(1572, new Tile(3146, 9870), 0, 3));
    }

    private static void closeGateSouth() {
        ObjectManager.addObj(new GameObject(1568, new Tile(3145, 9871), 0, 2));
        ObjectManager.addObj(new GameObject(1569, new Tile(3145, 9870), 0, 2));
        ObjectManager.removeObj(new GameObject(1571, new Tile(3146, 9871), 0, 1));
        ObjectManager.removeObj(new GameObject(1572, new Tile(3146, 9870), 0, 3));
    }

    private static void openGateWest() {
        // Cache objects
        GameObject gate1 = new GameObject(1568, new Tile(3103, 9910), 0, 2);
        GameObject gate2 = new GameObject(1569, new Tile(3103, 9909), 0, 2);

        GameObject replacement1 = new GameObject(1571, new Tile(3104, 9910), 0, 1);
        GameObject replacement2 = new GameObject(1572, new Tile(3104, 9909), 0, 3);

        // Remove objects first
        ObjectManager.removeObj(gate1);
        ObjectManager.removeObj(gate2);

        // Add new objects
        ObjectManager.addObj(replacement1);
        ObjectManager.addObj(replacement2);
    }

    private static void closeGateWest() {
        // Cache objects
        GameObject gate1 = new GameObject(1571, new Tile(3104, 9910), 0, 1);
        GameObject gate2 = new GameObject(1572, new Tile(3104, 9909), 0, 3);

        GameObject replacement1 = new GameObject(1568, new Tile(3103, 9910), 0, 2);
        GameObject replacement2 = new GameObject(1569, new Tile(3103, 9909), 0, 2);

        // Remove objects first
        ObjectManager.removeObj(gate1);
        ObjectManager.removeObj(gate2);

        // Add new objects
        ObjectManager.addObj(replacement1);
        ObjectManager.addObj(replacement2);
    }

    private static void openTaverlyGate() {
        ObjectManager.removeObj(new GameObject(1727, new Tile(2935, 3451), 0, 2));
        ObjectManager.removeObj(new GameObject(1728, new Tile(2935, 3450), 0, 2));
        ObjectManager.addObj(new GameObject(1571, new Tile(2936, 3451), 0, 1));
        ObjectManager.addObj(new GameObject(1572, new Tile(2936, 3450), 0, 3));
    }

    private static void openWildyTowerRedDoor() {
        ObjectManager.removeObj(new GameObject(33572, new Tile(3043, 3789), 0, 1));
        ObjectManager.addObj(new GameObject(28470, new Tile(3043, 3790), 0, 2));
    }

    private static void closeTaverlyGate() {
        ObjectManager.addObj(new GameObject(1727, new Tile(2935, 3451), 0, 2));
        ObjectManager.addObj(new GameObject(1728, new Tile(2935, 3450), 0, 2));
        ObjectManager.removeObj(new GameObject(1571, new Tile(2936, 3451), 0, 1));
        ObjectManager.removeObj(new GameObject(1572, new Tile(2936, 3450), 0, 3));
    }

    private static void openTaverlyDungeonPrisonGate(Player player) {
        GameObject openDoor1 = new GameObject(2143, new Tile(2889, 9831, 0), 0, 0);
        GameObject rotateDoor1 = new GameObject(2143, new Tile(2889, 9831, 0), 0, 1);
        ObjectManager.replace(openDoor1, rotateDoor1, 3);

        GameObject openDoor2 = new GameObject(2144, new Tile(2889, 9830, 0), 0, 0);
        GameObject rotateDoor2 = new GameObject(2144, new Tile(2889, 9830, 0), 0, 3);
        ObjectManager.replace(openDoor2, rotateDoor2, 3);

        TaskManager.submit(
                new TickAndStop(1) {

                    @Override
                    public void executeAndStop() {
                        // Walk trough
                        player.getMovementQueue()
                                .walkTo(new Tile(player.tile().x == 2888 ? 2889 : 2888, 9830, 0));
                    }
                });
    }

    private static void openEdgevilleDungeonGate(Player player) {
        boolean outside = player.tile().y <= 9917;
        int x = player.tile().x;
        GameObject openDoor1 = new GameObject(1727, new Tile(3131, 9917, 0), 0, 1);
        GameObject rotateDoor1 = new GameObject(1727, new Tile(3131, 9917, 0), 0, 4);
        ObjectManager.replace(openDoor1, rotateDoor1, 3);

        GameObject openDoor2 = new GameObject(1728, new Tile(3132, 9917, 0), 0, 1);
        GameObject rotateDoor2 = new GameObject(1728, new Tile(3132, 9917, 0), 0, 2);
        ObjectManager.replace(openDoor2, rotateDoor2, 3);

        TaskManager.submit(
                new TickAndStop(1) {

                    @Override
                    public void executeAndStop() {
                        // Walk trough
                        player.getMovementQueue()
                                .walkTo(
                                        new Tile(
                                                x == 3131 ? 3131 : 3132, outside ? 9918 : 9917, 0));
                    }
                });
    }

    private static void openLavaDragonsGate() {
        ObjectManager.removeObj(new GameObject(1727, new Tile(3202, 3856), 0, 3));
        ObjectManager.removeObj(new GameObject(1728, new Tile(3201, 3856), 0, 3));
        ObjectManager.addObj(new GameObject(1571, new Tile(3202, 3856), 0, 2));
        ObjectManager.addObj(new GameObject(1572, new Tile(3201, 3856), 0, 4));
    }

    private static void closeLavaDragonsGate() {
        ObjectManager.removeObj(new GameObject(1571, new Tile(3202, 3856), 0, 2));
        ObjectManager.removeObj(new GameObject(1572, new Tile(3201, 3856), 0, 4));
        ObjectManager.addObj(new GameObject(1727, new Tile(3202, 3856), 0, 3));
        ObjectManager.addObj(new GameObject(1728, new Tile(3201, 3856), 0, 3));
    }

    private static void openLavaDragonsGate2() {
        ObjectManager.removeObj(new GameObject(1727, new Tile(3071, 3857), 0, 2));
        ObjectManager.removeObj(new GameObject(1728, new Tile(3071, 3856), 0, 2));
        ObjectManager.addObj(new GameObject(1571, new Tile(3071, 3857), 0, 1));
        ObjectManager.addObj(new GameObject(1572, new Tile(3071, 3856), 0, 3));
    }

    private static void closeLavaDragonsGate2() {
        ObjectManager.removeObj(new GameObject(1571, new Tile(3071, 3857), 0, 1));
        ObjectManager.removeObj(new GameObject(1572, new Tile(3071, 3856), 0, 3));
        ObjectManager.addObj(new GameObject(1727, new Tile(3071, 3857), 0, 2));
        ObjectManager.addObj(new GameObject(1728, new Tile(3071, 3856), 0, 2));
    }

    private static void openLavaDragonsGate3() {
        ObjectManager.removeObj(new GameObject(1568, new Tile(3075, 3867), 0, 1));
        ObjectManager.removeObj(new GameObject(1569, new Tile(3076, 3867), 0, 1));
        ObjectManager.addObj(new GameObject(1571, new Tile(3075, 3867), 0, 4));
        ObjectManager.addObj(new GameObject(1572, new Tile(3076, 3867), 0, 2));
    }

    private static void closeLavaDragonsGate3() {
        ObjectManager.removeObj(new GameObject(1571, new Tile(3075, 3867), 0, 4));
        ObjectManager.removeObj(new GameObject(1572, new Tile(3076, 3867), 0, 2));
        ObjectManager.addObj(new GameObject(1568, new Tile(3075, 3867), 0, 1));
        ObjectManager.addObj(new GameObject(1569, new Tile(3076, 3867), 0, 1));
    }

    private static void openKbdCageGate(Player player, GameObject gate) {
        boolean primary = gate.getId() == 1728; // closed -> 1st door
        // now get the 2nd door next to it, also closed
        GameObject gate2 =
                new GameObject(primary ? 1727 : 1728, new Tile(3008, primary ? 3849 : 3850, 0));

        GameObject replacement1 = new GameObject(1571, new Tile(3008, 3849), 0, 3);
        GameObject replacement2 = new GameObject(1572, new Tile(3008, 3850), 0, 1);

        // Shouldn't happen, but you never know!
        if (gate2 == null) {
            player.message("This door is stuck...");
            return;
        }

        // Remove old gates
        ObjectManager.removeObj(gate);
        ObjectManager.removeObj(gate2);

        // Send new gates
        ObjectManager.addObj(replacement1);
        ObjectManager.addObj(replacement2);

        // TODO we need a cooldown which auto closes gates if no interaction
    }

    private static void closeKbdCageGate(Player player, GameObject gate) {
        boolean primary = gate.getId() == 1572;
        GameObject gate2 =
                new GameObject(primary ? 1571 : 1572, new Tile(3008, primary ? 3849 : 3850, 0));

        GameObject replacement1 = new GameObject(1727, new Tile(3008, 3849), 0, 0);
        GameObject replacement2 = new GameObject(1728, new Tile(3008, 3850), 0, 0);

        // Shouldn't happen, but you never know!
        if (gate2 == null) return;

        // Remove old objects
        ObjectManager.removeObj(gate);
        ObjectManager.removeObj(gate2);

        // Replace
        ObjectManager.addObj(replacement1);
        ObjectManager.addObj(replacement2);
    }
}

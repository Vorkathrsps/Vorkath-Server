package com.cryptic.model.map.object.doors;

import com.cryptic.GameServer;
import com.cryptic.annotate.Init;
import com.cryptic.cache.definitions.ObjectDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

// ~~~ DIRECTIONS ~~~ \\
// 0 = east, 1 = south \\
// 2 = west, 3 = north \\
// ~~~~~~~~~~~~~~~~~~~~ \\
/**
 * @author Runite team
 */
public class Door {

    public static void handle(Player player, GameObject obj) {
        handle(player, obj, false);
    }

    public static void handle(Player player, GameObject obj, boolean skipJammedCheck) {
        ObjectDefinition def = obj.definition();
        if (def.id == 2144 || def.id == 2143) {
            if (player.getAbsX() <= 2888) {
                player.teleport(2889, player.getAbsY());
                return;
            }
            if (player.getAbsX() >= 2889) {
                player.teleport(2888, player.getAbsY());
                return;
            }
            player.message("Unhandled door, report this to a staff member! ID: "+def.id);
        }
        if (def.doorOppositeId == -1) {
            player.message("The " + (def.gateType ? "gate" : "door") + " won't seem to budge.");
            return;
        }
//        if (obj.conOwner != -1 && obj.conOwner != player.getUserId()) {
//            player.message("Only the host can " + (def.doorClosed ? "open" : "close") + " these doors.");
//            return;
//        }
        int dir = obj.getRotation();
        if (def.doorReversed)
            dir = (dir + 2) & 0x3;
        GameObject pairObj = null;
        boolean verticalFlip = def.verticalFlip;
        if (def.reversedConstructionDoor)
            verticalFlip = !verticalFlip;
        if (def.doorClosed) {
            if (dir == 3) { //North
                if (verticalFlip)
                    pairObj = findPair(obj, 1, 0);
                else
                    pairObj = findPair(obj, -1, 0);
            } else if (dir == 1) { //South
                if (verticalFlip)
                    pairObj = findPair(obj, -1, 0);
                else
                    pairObj = findPair(obj, 1, 0);
            } else if (dir == 0) { //East
                if (verticalFlip)
                    pairObj = findPair(obj, 0, -1);
                else
                    pairObj = findPair(obj, 0, 1);
            } else if (dir == 2) { //West
                if (verticalFlip)
                    pairObj = findPair(obj, 0, 1);
                else
                    pairObj = findPair(obj, 0, -1);
            }
        } else {
            if (def.longGate) {
                if (dir == 3) { //North
                    if (def.verticalFlip)
                        pairObj = findPair(obj, 1, 0);
                    else
                        pairObj = findPair(obj, -1, 0);
                } else if (dir == 1) { //South
                    if (def.verticalFlip)
                        pairObj = findPair(obj, -1, 0);
                    else
                        pairObj = findPair(obj, 1, 0);
                } else if (dir == 0) { //East
                    if (def.verticalFlip)
                        pairObj = findPair(obj, 0, -1);
                    else
                        pairObj = findPair(obj, 0, 1);
                } else if (dir == 2) { //West
                    if (def.verticalFlip)
                        pairObj = findPair(obj, 0, 1);
                    else
                        pairObj = findPair(obj, 0, -1);
                }
            } else {
                if (dir == 3) { //North
                    if (def.doorReversed)
                        pairObj = findPair(obj, 0, -1);
                    else
                        pairObj = findPair(obj, 0, 1);
                } else if (dir == 1) { //South
                    if (def.doorReversed)
                        pairObj = findPair(obj, 0, 1);
                    else
                        pairObj = findPair(obj, 0, -1);
                } else if (dir == 0) { //East
                    if (def.doorReversed)
                        pairObj = findPair(obj, -1, 0);
                    else
                        pairObj = findPair(obj, 1, 0);
                } else if (dir == 2) { //West
                    if (def.doorReversed)
                        pairObj = findPair(obj, 1, 0);
                    else
                        pairObj = findPair(obj, -1, 0);
                }
            }
        }
        if (pairObj != null) {
            if (!def.doorClosed) {
                if (!skipJammedCheck && isJammed(player, def.verticalFlip ? pairObj : obj)) {
                    player.message("The " + (def.gateType ? "gates" : "doors") + " seem to be stuck.");
                    return;
                }
                if (def.doorCloseSound != -1)
                    player.sendPrivateSound(def.doorCloseSound);
            } else {
                if (def.doorOpenSound != -1)
                    player.sendPrivateSound(def.doorOpenSound);
            }
            changeState(obj, !def.reversedConstructionDoor);
            changeState(pairObj, !pairObj.definition().reversedConstructionDoor);
        } else {
            if (!def.doorClosed) {
                if (!skipJammedCheck && isJammed(player, obj)) {
                    player.message("The " + (def.gateType ? "gate" : "door") + " seems to be stuck.");
                    return;
                }
                if (def.doorCloseSound != -1)
                    player.sendPrivateSound(def.doorCloseSound);
            } else {
                if (def.doorOpenSound != -1)
                    player.sendPrivateSound(def.doorOpenSound);
            }
            if (obj.getType() == 9) {
                int playerX = player.getAbsX();
                int playerY = player.getAbsY();

                int objectX = obj.x;
                int objectY = obj.y;
                int doorDirection = obj.getRotation();

                if (!def.doorClosed) {
                    if (doorDirection == 0 && playerX > objectX && playerY == objectY) {
                        walkDiagonal(player, obj, playerX + 1, playerY);
                        return;
                    } else if (doorDirection == 2 && playerX < objectX && playerY == objectY) {
                        walkDiagonal(player, obj, playerX - 1, playerY);
                        return;
                    } else if (doorDirection == 1 && playerY < objectY) {
                        walkDiagonal(player, obj, playerX + 1, objectY - 2);
                        return;
                    } else if (doorDirection == 3 && playerY > objectY) {
                        walkDiagonal(player, obj, playerX - 1, objectY + 2);
                        return;
                    }
                } else {
                    if (doorDirection == 0 && playerX == objectX && playerY > objectY) {
                        walkDiagonal(player, obj, objectX - 1, objectY + 1);
                        return;
                    } else if (doorDirection == 1 && playerX > objectX && playerY == objectY) {
                        walkDiagonal(player, obj, objectX, objectY + 1);
                        return;
                    } else if (doorDirection == 2 && playerX == objectX && playerY < objectY) {
                        walkDiagonal(player, obj, objectX + 1, objectY - 1);
                        return;
                    } else if (doorDirection == 3 && playerX < objectX && playerY == objectY) {
                        walkDiagonal(player, obj, playerX + 1, playerY - 1);
                        return;
                    }
                }
            }

            changeState(obj, false);
        }
    }

    private static void walkDiagonal(Player player, GameObject door, int x, int y) {
        player.runFn(1, () -> {
            player.stepAbs(x, y, MovementQueue.StepType.FORCED_WALK);
            player.waitUntil(() -> player.getMovement().isAtDestination(), () -> {
                changeState(door, false);
                player.setPositionToFace(door.tile());
            });
        });
    }

    private static GameObject findPair(GameObject obj, int offsetX, int offsetY) {
        Tile tile = Tile.get(obj.x + offsetX, obj.y + offsetY, obj.z, false);
        if (tile == null || tile.gameObjects == null)
            return null;
        int size = tile.gameObjects.size();
        if (size == 0)
            return null;
        for (int i = (size - 1); i >= 0; i--) { //keep backwards loop
            GameObject pairedObj = tile.gameObjects.get(i);
            if (pairedObj.getId() != -1 && pairedObj.getType() == obj.getType() && pairedObj.definition().doorOppositeId != -1)
                return pairedObj;
        }
        return null;
    }

    private static boolean isJammed(Player pCloser, GameObject obj) {
        if (obj.doorReplaced != null)
            obj = obj.doorReplaced;
        if (World.getWorld().isPast(obj.doorJamEnd)) {
            obj.doorCloses = 0;
            obj.doorJamEnd = World.getWorld().getEnd(WildernessArea.wildernessLevel(pCloser.tile()) == 0 ? 500 : 50);
        }
        return ++obj.doorCloses >= 5;
    }

    public static void changeState(GameObject obj, boolean paired) {
        if (obj.doorReplaced != null) {
            if (obj.doorReplaced.getId() == -1) {
                obj.remove();
                int prev = obj.doorReplaced.getAttribOr(AttributeKey.DOOR_ORIG_ID, -1);
                if (prev != -1) {
                    obj.doorReplaced.setId(prev);
                    obj.clearAttrib(AttributeKey.DOOR_ORIG_ID);
                } else {
                    obj.doorReplaced.restore();
                }
            } else {
                Tile tile = obj.linkedTile();
                tile.removeObject(obj);
                for (Player player : tile.getRegion().players)
                    obj.doorReplaced.sendCreate(player);
            }
            obj.doorReplaced = null;
            return;
        }
        ObjectDefinition def = obj.definition();
        int dir = obj.getRotation();
        int diffX = 0, diffY = 0, diffDir = 0;
        if (paired) {
            /**
             * Double
             */
            if (def.longGate) {
                if (def.doorClosed) {
                    if (def.verticalFlip) {
                        diffDir--;

                        if (dir == 0) {
                            diffX -= 2;
                            diffY--;
                        } else if (dir == 1) {
                            diffX--;
                            diffY += 2;
                        } else if (dir == 2) {
                            diffY++;
                            diffX += 2;
                        } else if (dir == 3) {
                            diffX++;
                            diffY -= 2;
                        }
                    } else {
                        diffDir--;
                        if (dir == 0) {
                            diffX--;
                        } else if (dir == 1) {
                            diffY++;
                        } else if (dir == 2) {
                            diffX++;
                        } else if (dir == 3) {
                            diffY--;
                        }
                    }
                } else {
                    if (def.verticalFlip)
                        diffDir--;
                    else
                        diffDir++;
                    if (dir == 0) {
                        if (def.verticalFlip)
                            diffY--;
                        else
                            diffY++;
                    } else if (dir == 1) {
                        if (def.verticalFlip)
                            diffX++;
                        else
                            diffX--;
                    } else if (dir == 2) {
                        if (def.verticalFlip)
                            diffY++;
                        else
                            diffY--;
                    } else if (dir == 3) {
                        if (def.verticalFlip)
                            diffX--;
                        else
                            diffX++;
                    }
                }
            } else if (def.doorClosed) {
                if (def.verticalFlip)
                    diffDir++;
                else
                    diffDir--;
                if (dir == 0)
                    diffX--;
                else if (dir == 1)
                    diffY++;
                else if (dir == 2)
                    diffX++;
                else if (dir == 3)
                    diffY--;
            } else {
                if (def.verticalFlip)
                    diffDir--;
                else
                    diffDir++;
                if (dir == 0) {
                    if (def.verticalFlip)
                        diffY--;
                    else
                        diffY++;
                } else if (dir == 1) {
                    if (def.verticalFlip)
                        diffX++;
                    else
                        diffX--;
                } else if (dir == 2) {
                    if (def.verticalFlip)
                        diffY++;
                    else
                        diffY--;
                } else if (dir == 3) {
                    if (def.verticalFlip)
                        diffX--;
                    else
                        diffX++;
                }
            }
        } else if (obj.getType() == 9) {
            /**
             * Single diagonal
             */
            if (def.doorClosed) {
                if (def.verticalFlip) {
                    diffDir--;
                } else {
                    diffDir++;
                    if (dir == 0)
                        diffY++;
                    if (dir == 1)
                        diffX++;
                    if (dir == 2)
                        diffY--;
                    if (dir == 3)
                        diffX--;
                }
            } else {
                if (def.verticalFlip) {
                    diffDir++;
                } else {
                    diffDir--;
                    if(dir == 0) {
                        diffX++;
                    }
                    if (dir == 2) {
                        diffX--;
                    }
                }
            }
        } else {
            /**
             * Single regular
             */
            if (def.doorClosed) {
                if (def.verticalFlip)
                    diffDir--;
                else
                    diffDir++;
                if (dir == 0)
                    diffX--;
                else if (dir == 1)
                    diffY++;
                else if (dir == 2)
                    diffX++;
                else if (dir == 3)
                    diffY--;
            } else {
                if (def.verticalFlip)
                    diffDir++;
                else
                    diffDir--;
                if (dir == 0)
                    diffY++;
                else if (dir == 1)
                    diffX++;
                else if (dir == 2)
                    diffY--;
                else if (dir == 3)
                    diffX--;
            }
        }
        if (obj.conOwner == -1 && def.doorReversed != ObjectDefinition.get(def.doorOppositeId).doorReversed) {
            diffX = diffY = 0;
            diffDir += 2;
        } else if (def.doorReversed) {
            diffDir += 2;
        }
        if (diffX == 0 && diffY == 0)
            obj.clip(true);
        else {
            if (obj.getId() != obj.originalId)
                obj.putAttrib(AttributeKey.DOOR_ORIG_ID, obj.getId());
            obj.remove();
        }
        GameObject replacement = GameObject.spawn(def.doorOppositeId, obj.x + diffX, obj.y + diffY, obj.z, obj.getType(), (dir + diffDir) & 0x3);
        replacement.doorReplaced = obj;
    }

    @Init
    public static void init() {
        /**
         * This array is used to manually override opposite ids.
         * Example: Sometimes an open door will use a different model id from it's closed version, causing it not to pair.
         */
        int[][] oppositeOverrideIds = {
                {24060, 24061}, //Double doors on the top of the Falador castle.
                {24062, 24063}, //Double doors on the top of the Falador castle.
                {13314, 13315}, // oak cage door
                {13317, 13318}, // oak and steel cage door
                {13320, 13321}, // steel cage door
                {13323, 13324}, // spiked cage door
                {13326, 13327}, // bone cage door
                {13344, 13350}, {13345, 13351}, // oak dungeon door
                {13346, 13352}, {13347, 13353}, // steel dungeon door
                {13348, 13354}, {13349, 13355}, // marble dungeon door
                {9038, 9039}, //Karamja teak tree entrance
                {1511, 1511}, {1513, 1513}
        };
        /**
         * These objects face 180 degrees different than others.
         * Example: An object in this list with a direction of 0 (East) will look as if it's facing direction 2 (West)
         */
        int[] reversedIds = {
                24060, 24062,   //Double doors on the top of the Falador castle.
                22435, 22437,   //Double (closed) doors (Not sure what island these are on..)
                22436, 22438,   //Double (opened) doors (Not sure what island these are on..)
                14233, 14235,   //Double (closed) gates in Pest Control.
                14234, 14236,   //Double (opened) gates in Pest Control.
                13314, 13315, // oak cage door
                13317, 13318, // oak and steel cage door
                13320, 13321, // steel cage door
                13323, 13324, // spiked cage door
                13006, 13007, 13008, 13009, // whitewashed stone style doors
//                13344, 13350, 13345, 13351, // oak dungeon door

                /* construction doors (deathly mansion doors not reversed!) */
//                HouseStyle.BASIC_WOOD.doorId1, HouseStyle.BASIC_WOOD.doorId2,
//                HouseStyle.BASIC_STONE.doorId1, HouseStyle.BASIC_STONE.doorId2,
//                HouseStyle.WHITEWASHED_STONE.doorId1, HouseStyle.WHITEWASHED_STONE.doorId2,
//                HouseStyle.FREMENNIK_WOOD.doorId1, HouseStyle.FREMENNIK_WOOD.doorId2,
//                HouseStyle.TROPICAL_WOOD.doorId1, HouseStyle.TROPICAL_WOOD.doorId2,
//                HouseStyle.FANCY_STONE.doorId1, HouseStyle.FANCY_STONE.doorId2,
        };

        int[] reversedConstructionDoors = {
                13345, 13351, // oak dungeon door
                13347, 13353, // steel dungeon door
                13349, 13355, // marble dungeon door
        };
        /**
         * Registering
         */
        for (int i = 0; i < World.getWorld().definitions().total(ObjectDefinition.class); i++) {
            ObjectDefinition def = World.getWorld().definitions().get(ObjectDefinition.class, i);
            if (def.id >= 26502 && def.id <= 26505) // gwd doors
                return;
            if (def.id == 34553 || def.id == 34554) // alchemical hydra doors
                return;
            String name = def.name.toLowerCase();
            if (name.contains("gate")) {
                def.gateType = true;
                if (def.models[0] == 7371 || (def.models[0] == 966 && def.models[1] == 967 && def.models[2] == 968)) {
                    def.longGate = true;
                    setSound(def, 67, 66);
                } else {
                    setSound(def, 69, 68);
                }
            } else if (!name.contains("door")) {
                return;
            } else {
                setSound(def, 62, 60);
            }

            if(def.id == 12657)
                def.doorOppositeId = 12658;
            else if (def.id == 12658)
                def.doorOppositeId = 12657;
            else
                def.doorOppositeId = findOppositeId(def);

            for (int[] opp : oppositeOverrideIds) {
                int id1 = opp[0];
                int id2 = opp[1];
                if (def.id == id1) {
                    def.doorOppositeId = id2;
                    break;
                }
                if (def.id == id2) {
                    def.doorOppositeId = id1;
                    break;
                }
            }
            for (int reversedId : reversedIds) {
                if (def.id == reversedId) {
                    def.doorReversed = true;
                    break;
                }
            }
            for (int reversedId : reversedConstructionDoors) {
                if (def.id == reversedId) {
                    def.reversedConstructionDoor = true;
                    break;
                }
            }
            if (def.doorClosed = def.hasOption("open"))
                PacketInteractionManager.addObjectInteraction((player, object, option) -> {
                    if (object.getId() == def.id && object.definition().getOption("open") == option) {
                        Door.handle(player, object);
                        return true;
                    }
                    return false;
                });
            else {
                PacketInteractionManager.addObjectInteraction((player, object, option) -> {
                    if (object.getId() == def.id && object.definition().getOption("close") == option) {
                        Door.handle(player, object);
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    private static void setSound(ObjectDefinition def, int open, int close) {
        if (def.hasOption("open"))
            def.doorOpenSound = open;
        else
            def.doorCloseSound = close;
    }

    private static int findOppositeId(ObjectDefinition originalDef) {
        if (originalDef.getOption("open", "close") == -1)
            return -1;
        ArrayList<Integer> ids = new ArrayList<>();
        defs:
        for (int objIdx = 0; objIdx < World.getWorld().definitions().total(ObjectDefinition.class); objIdx++) {
            ObjectDefinition def = World.getWorld().definitions().get(ObjectDefinition.class, objIdx);
            if (def == null || def.id == originalDef.id)
                continue;
            if (!Objects.equals(def.name, originalDef.name))
                continue;
            if (def.op66Render0x2 != originalDef.op66Render0x2)
                continue;
            if (!Arrays.equals(def.models, originalDef.models))
                continue;
            if (!Arrays.equals(def.modeltypes, originalDef.modeltypes))
                continue;
            if (!Arrays.equals(def.recol_d, originalDef.recol_d))
                continue;
            if (def.verticalFlip != originalDef.verticalFlip)
                continue;
            if (Arrays.equals(def.options, originalDef.options))
                continue;
            for (int i = 0; i < def.options.length; i++) {
                String s1 = def.options[i];
                String s2 = originalDef.options[i];
                if (!Objects.equals(s1, s2)) {
                    if ("open".equalsIgnoreCase(s1) && "close".equalsIgnoreCase(s2))
                        continue;
                    if ("close".equalsIgnoreCase(s1) && "open".equalsIgnoreCase(s2))
                        continue;
                    continue defs;
                }
            }
            ids.add(def.id);
        }
        if (!ids.isEmpty()) {
            ids.sort((i1, i2) -> {
                int diff1 = Math.abs(i1 - originalDef.id);
                int diff2 = Math.abs(i2 - originalDef.id);
                return Integer.compare(diff1, diff2);
            });
            return ids.get(0);
        }
        return -1;
    }

}

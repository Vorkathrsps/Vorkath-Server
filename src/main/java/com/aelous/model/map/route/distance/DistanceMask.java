package com.aelous.model.map.route.distance;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.collisionmaps.CollisionMap;
import com.aelous.network.packet.ByteOrder;
import com.aelous.network.packet.PacketBuilder;
import com.aelous.network.packet.ValueType;
import com.aelous.network.packet.outgoing.PacketSender;

public class DistanceMask extends Tile {


    DistanceMask(int x, int y, int height) {
        super(x, y, height);
        this.region_x = (byte) regionX();
        this.region_y = (byte) regionY();
        this.x = getX();
        this.y = getY();
        this.local_x_path = getLocalX();
        this.local_y_path = getLocalY();
        this.next_region_start = getBaseLocalX();
        this.next_region_end = getBaseLocalY();
    }
    Player player;

    static PacketBuilder out = new PacketBuilder();

    int writeOpcode = 164;

    byte region_x, region_y;
    private final int local_x_path, local_y_path;
    private static int travel_destination_x;
    private static int travel_destination_y;

    public static int next_region_start;
    public static int next_region_end;
    static int x, y, height;
    static int next_pos = 0, current_pos = 0;
    private static int[] walking_queue_x, walking_queue_y;
    private static int[][] travel_distances, waypoints;
    static final int[] key_status = new int[128];
    static boolean reached = false;
    static int path_length = walking_queue_x.length;
    private static CollisionMap[] collisionMaps;
    static int[][] adjacencies = collisionMaps[height].adjacencies;

    private static int current_walking_queue_length;
    private static int destination_mask;

    public static boolean walk(int opcode, int obstruction_orientation, int obstruction_height, int obstruction_type, int local_y_path, int obstruction_width, int orientation_mask, int path_to_y_position, int local_x_path, boolean minimap_click, int path_to_x_position) {
        try {
            byte region_x = 104;
            byte region_y = 104;
            for (int x = 0; x < region_x; x++) {
                for (int y = 0; y < region_y; y++) {
                    waypoints[x][y] = 0;
                    travel_distances[x][y] = 0x5f5e0ff;//99999999
                }
            }
            int x = local_x_path;
            int y = local_y_path;
            waypoints[local_x_path][local_y_path] = 99;
            travel_distances[local_x_path][local_y_path] = 0;
            int next_pos = 0;
            int current_pos = 0;
            walking_queue_x[next_pos] = local_x_path;
            walking_queue_y[next_pos++] = local_y_path;
            boolean reached = false;
            int path_length = walking_queue_x.length;
            int[][] adjacencies = collisionMaps[height].adjacencies;
            while (current_pos != next_pos) {
                x = walking_queue_x[current_pos];
                y = walking_queue_y[current_pos];
                current_pos = (current_pos + 1) % path_length;
                if (x == path_to_x_position && y == path_to_y_position) {
                    reached = true;
                    break;
                }
                if (obstruction_type != 0) {
                    if ((obstruction_type < 5 || obstruction_type == 10) && collisionMaps[height].obstruction_wall(path_to_x_position, x, y, obstruction_orientation, obstruction_type - 1, path_to_y_position)) {
                        reached = true;
                        break;
                    }
                    if (obstruction_type < 10 && collisionMaps[height].obstruction_decor(path_to_x_position, path_to_y_position, y, obstruction_type - 1, obstruction_orientation, x)) {
                        reached = true;
                        break;
                    }
                }
                if (obstruction_width != 0 && obstruction_height != 0 && collisionMaps[height].obstruction(path_to_y_position, path_to_x_position, x, obstruction_height, orientation_mask, obstruction_width, y)) {
                    reached = true;
                    break;
                }
                int updated_distance = travel_distances[x][y] + 1;
                if (x > 0 && waypoints[x - 1][y] == 0 && (adjacencies[x - 1][y] & 0x1280108) == 0) {
                    walking_queue_x[next_pos] = x - 1;
                    walking_queue_y[next_pos] = y;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x - 1][y] = 2;
                    travel_distances[x - 1][y] = updated_distance;
                }
                if (x < region_x - 1 && waypoints[x + 1][y] == 0 && (adjacencies[x + 1][y] & 0x1280180) == 0) {
                    walking_queue_x[next_pos] = x + 1;
                    walking_queue_y[next_pos] = y;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x + 1][y] = 8;
                    travel_distances[x + 1][y] = updated_distance;
                }
                if (y > 0 && waypoints[x][y - 1] == 0 && (adjacencies[x][y - 1] & 0x1280102) == 0) {
                    walking_queue_x[next_pos] = x;
                    walking_queue_y[next_pos] = y - 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x][y - 1] = 1;
                    travel_distances[x][y - 1] = updated_distance;
                }
                if (y < region_y - 1 && waypoints[x][y + 1] == 0 && (adjacencies[x][y + 1] & 0x1280120) == 0) {
                    walking_queue_x[next_pos] = x;
                    walking_queue_y[next_pos] = y + 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x][y + 1] = 4;
                    travel_distances[x][y + 1] = updated_distance;
                }
                if (x > 0 && y > 0 && waypoints[x - 1][y - 1] == 0 && (adjacencies[x - 1][y - 1] & 0x128010e) == 0 && (adjacencies[x - 1][y] & 0x1280108) == 0 && (adjacencies[x][y - 1] & 0x1280102) == 0) {
                    walking_queue_x[next_pos] = x - 1;
                    walking_queue_y[next_pos] = y - 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x - 1][y - 1] = 3;
                    travel_distances[x - 1][y - 1] = updated_distance;
                }
                if (x < region_x - 1 && y > 0 && waypoints[x + 1][y - 1] == 0 && (adjacencies[x + 1][y - 1] & 0x1280183) == 0 && (adjacencies[x + 1][y] & 0x1280180) == 0 && (adjacencies[x][y - 1] & 0x1280102) == 0) {
                    walking_queue_x[next_pos] = x + 1;
                    walking_queue_y[next_pos] = y - 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x + 1][y - 1] = 9;
                    travel_distances[x + 1][y - 1] = updated_distance;
                }
                if (x > 0 && y < region_y - 1 && waypoints[x - 1][y + 1] == 0 && (adjacencies[x - 1][y + 1] & 0x1280138) == 0 && (adjacencies[x - 1][y] & 0x1280108) == 0 && (adjacencies[x][y + 1] & 0x1280120) == 0) {
                    walking_queue_x[next_pos] = x - 1;
                    walking_queue_y[next_pos] = y + 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x - 1][y + 1] = 6;
                    travel_distances[x - 1][y + 1] = updated_distance;
                }
                if (x < region_x - 1 && y < region_y - 1 && waypoints[x + 1][y + 1] == 0 && (adjacencies[x + 1][y + 1] & 0x12801e0) == 0 && (adjacencies[x + 1][y] & 0x1280180) == 0 && (adjacencies[x][y + 1] & 0x1280120) == 0) {
                    walking_queue_x[next_pos] = x + 1;
                    walking_queue_y[next_pos] = y + 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x + 1][y + 1] = 12;
                    travel_distances[x + 1][y + 1] = updated_distance;
                }
            }
            destination_mask = 0;
            if (!reached) {
                if (minimap_click) {
                    int steps = 100;
                    for (int deviation_offset = 1; deviation_offset < 2; deviation_offset++) {
                        for (int deviation_x = path_to_x_position - deviation_offset; deviation_x <= path_to_x_position + deviation_offset; deviation_x++) {
                            for (int deviation_y = path_to_y_position - deviation_offset; deviation_y <= path_to_y_position + deviation_offset; deviation_y++) {
                                if (deviation_x >= 0 && deviation_y >= 0 && deviation_x < 104 && deviation_y < 104 && travel_distances[deviation_x][deviation_y] < steps) {
                                    steps = travel_distances[deviation_x][deviation_y];
                                    x = deviation_x;
                                    y = deviation_y;
                                    destination_mask = 1;
                                    reached = true;
                                }
                            }
                        }
                        if (reached) break;
                    }
                }
                if (!reached) {
                    return false;
                }
            }
            current_pos = 0;
            walking_queue_x[current_pos] = x;
            walking_queue_y[current_pos++] = y;
            int skip;
            for (int waypoint = skip = waypoints[x][y]; x != local_x_path || y != local_y_path; waypoint = waypoints[x][y]) {
                if (waypoint != skip) {
                    skip = waypoint;
                    walking_queue_x[current_pos] = x;
                    walking_queue_y[current_pos++] = y;
                }
                if ((waypoint & 2) != 0) x++;
                else if ((waypoint & 8) != 0) x--;
                if ((waypoint & 1) != 0) y++;
                else if ((waypoint & 4) != 0) y--;
            }
            if (current_pos > 0) {
                int max_path = current_pos;
                if (max_path > 25) max_path = 25;
                current_pos--;
                int walking_x = walking_queue_x[current_pos];
                int walking_y = walking_queue_y[current_pos];
                current_walking_queue_length += max_path;
                if (current_walking_queue_length >= 92) {
                    current_walking_queue_length = 0;
                }
                if (opcode == 0) {
                    out.put(max_path + max_path + 4);
                } else if (opcode == 1) {
                    out.put(max_path + max_path + 4);
                } else if (opcode == 2) {
                    out.put(max_path + max_path + 4);
                }
                out.put(height);
                out.putShort(walking_x + next_region_start, ValueType.A, ByteOrder.LITTLE);
                travel_destination_x = walking_queue_x[0];
                travel_destination_y = walking_queue_y[0];
                for (int step = 1; step < max_path; step++) {
                    current_pos--;
                    out.put(walking_queue_x[current_pos] - walking_x);
                    out.put(walking_queue_y[current_pos] - walking_y);
                }
                out.putShort(walking_y + next_region_end, ByteOrder.LITTLE);
                out.put(key_status[5] != 1 ? 0 : 1);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return opcode != 1;
    }
}

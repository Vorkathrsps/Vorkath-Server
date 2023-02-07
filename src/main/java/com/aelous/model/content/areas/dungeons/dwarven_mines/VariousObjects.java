package com.aelous.model.content.areas.dungeons.dwarven_mines;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;
/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 06, 2020
 */
public class VariousObjects extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
            if (option == 1) {
                if (obj.getId() == STAIRCASE_23969) {
                    // Stairs up from mines
                    if (obj.tile().x == 3059 && obj.tile().y == 9776) {
                        player.teleport(new Tile(3061, 3376));
                    }
                    return true;
                }

                //THIS LADDER IS IN FALLY AND WILDY. CHECK COORDS OF OBJECT.
                if (obj.getId() == STAIRCASE_16664) {
                    if (obj.tile().x == 3058 && obj.tile().y == 3376) {
                        //fally down
                        player.teleport(new Tile(3058, 9776));
                    }
                    if (obj.tile().x == 3044 && obj.tile().y == 3924) {
                        //mb stairs down
                        player.teleport(new Tile(3044, 10322));
                    }

                    // Yanile dungeon down
                    if (obj.tile().equals(2603, 3078)) {
                        player.teleport(2602, 9479);
                    }
                    return true;
                }

                //THIS LADDER IS IN FALLY AND WILDY. CHECK COORDS OF OBJECT.
                if (obj.getId() == STAIRCASE_16665) {
                    if (obj.tile().x == 3044 && obj.tile().y == 10324) {
                        //mb stairs up
                        player.teleport(new Tile(3044, 3927));
                    }

                    // Yanile dungeon up
                    if (obj.tile().equals(2603, 9478)) {
                        player.teleport(2603, 3078);
                    }
                    return true;
                }
                if (obj.getId() == LADDER_18989) {
                    if (obj.tile().x == 3069 && obj.tile().y == 3856) {
                        player.animate(827);
                        player.teleport(new Tile(3017, 10248));
                    }
                    return true;
                }
                if (obj.getId() == LADDER_18990) {
                    if (obj.tile().x == 3017 && obj.tile().y == 10249) {
                        player.animate(828);
                        player.teleport(new Tile(3069, 3857));
                    }
                    return true;
                }

                if (obj.getId() == DOOR_24057) {
                    ObjectManager.replaceWith(obj, new GameObject(24058, new Tile(3061, 3375), obj.getType(), 2));
                    return true;
                }
                if (obj.getId() == DOOR_24058) {
                    ObjectManager.replaceWith(obj, new GameObject(24057, new Tile(3061, 3374), obj.getType(), 1));
                    return true;
                }

                if (obj.getId() == STAIRS_33574) {
                    if (obj.tile().x == 3042 && obj.tile().y == 3805) {
                        player.teleport(new Tile(3043, 3811,1));
                    }
                    return true;
                }
                if (obj.getId() == STAIRS_33575) {
                    if (obj.tile().x == 3044 && obj.tile().y == 3805) {
                        player.teleport(new Tile(3044, 3811,1));
                    }
                    return true;
                }
                if (obj.getId() == STAIRS_33576) {
                    if (obj.tile().x == 3042 && obj.tile().y == 3807) {
                        player.teleport(new Tile(3043, 3804));
                    }
                    return true;
                }
                if (obj.getId() == STAIRS_33577) {
                    if (obj.tile().x == 3044 && obj.tile().y == 3807) {
                        player.teleport(new Tile(3044, 3804));
                    }
                    return true;
                }
            }
            return false;
        }
}

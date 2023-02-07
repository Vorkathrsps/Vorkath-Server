package com.aelous.model.content.areas.dungeons.waterbirth;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.DOOR_8963;

/**
 * @author Patrick van Elderen | March, 04, 2021, 13:08
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WaterbirthDungeon extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == STEPS_8966) {
                player.teleport(2523, 3739, 0);
                return true;
            }

            if (obj.getId() == CAVE_ENTRANCE_8929) {
                player.teleport(2442, 10146, 0);
                return true;
            }

            if (obj.getId() == KINGS_LADDER_10230 || obj.getId() == 3831) {
                Ladders.ladderDown(player, new Tile(2900, 4449, 0), true);
                return true;
            }

            if (obj.getId() == DOOR_8958 || obj.getId() == DOOR_8959 || obj.getId() == DOOR_8960) {
                ObjectManager.replace(obj, new GameObject(DOOR_8963, obj.tile(), obj.getType(), obj.getRotation()), 50);
                return true;
            }

            if (obj.getId() == IRON_LADDER_10177) {
                Ladders.ladderDown(player, new Tile(1799, 4407, 3), true);
                return true;
            }

            if (obj.getId() == LADDER_10193) {
                Ladders.ladderUp(player, new Tile(2545, 10143, 0), true);
                return true;
            }

            if (obj.getId() == LADDER_10195) {
                Ladders.ladderDown(player, new Tile(1810, 4405, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10196) {
                Ladders.ladderUp(player, new Tile(1807, 4405, 3), true);
                return true;
            }

            if (obj.getId() == LADDER_10197) {
                Ladders.ladderDown(player, new Tile(1822, 4404, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10198) {
                Ladders.ladderUp(player, new Tile(1825, 4404, 3), true);
                return true;
            }

            if (obj.getId() == LADDER_10199) {
                Ladders.ladderDown(player, new Tile(1834, 4388, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10200) {
                Ladders.ladderUp(player, new Tile(1834, 4390, 3), true);
                return true;
            }

            if (obj.getId() == LADDER_10201) {
                Ladders.ladderDown(player, new Tile(1810, 4394, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10202) {
                Ladders.ladderUp(player, new Tile(1812, 4394, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10203) {
                Ladders.ladderUp(player, new Tile(1799, 4386, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10204) {
                Ladders.ladderDown(player, new Tile(1799, 4389, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10205) {
                Ladders.ladderDown(player, new Tile(1798, 4382, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10206) {
                Ladders.ladderUp(player, new Tile(1796, 4382, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10207) {
                Ladders.ladderUp(player, new Tile(1800, 4369, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10208) {
                Ladders.ladderDown(player, new Tile(1802, 4369, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10209) {
                Ladders.ladderDown(player, new Tile(1827, 4362, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10210) {
                Ladders.ladderUp(player, new Tile(1825, 4362, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10211) {
                Ladders.ladderUp(player, new Tile(1863, 4373, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10212) {
                Ladders.ladderDown(player, new Tile(1863, 4371, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10213) {
                Ladders.ladderDown(player, new Tile(1864, 4389, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10214) {
                Ladders.ladderUp(player, new Tile(1864, 4387, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10215) {
                Ladders.ladderDown(player, new Tile(1890, 4408, 0), true);
                return true;
            }

            if (obj.getId() == LADDER_10216) {
                Ladders.ladderUp(player, new Tile(1890, 4406, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10217) {
                Ladders.ladderUp(player, new Tile(1957, 4373, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10218) {
                Ladders.ladderDown(player, new Tile(1957, 4371, 0), true);
                return true;
            }

            if (obj.getId() == LADDER_10219) {
                Ladders.ladderUp(player, new Tile(1824, 4379, 3), true);
                return true;
            }

            if (obj.getId() == LADDER_10220) {
                Ladders.ladderDown(player, new Tile(1824, 4381, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10221) {
                Ladders.ladderDown(player, new Tile(1838, 4375, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10222) {
                Ladders.ladderDown(player, new Tile(1838, 4377, 3), true);
                return true;
            }

            if (obj.getId() == LADDER_10223) {
                Ladders.ladderDown(player, new Tile(1850, 4386, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10224) {
                Ladders.ladderUp(player, new Tile(1850, 4387, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10225) {
                Ladders.ladderDown(player, new Tile(1932, 4378, 1), true);
                return true;
            }

            if (obj.getId() == LADDER_10226) {
                Ladders.ladderUp(player, new Tile(1932, 4380, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10227) {
                Ladders.ladderDown(player, new Tile(1961, 4391, 2), true);
                return true;
            }

            if (obj.getId() == LADDER_10228) {
                Ladders.ladderUp(player, new Tile(1961, 4393, 3), true);
                return true;
            }

            if (obj.getId() == KINGS_LADDER) {
                Ladders.ladderUp(player, new Tile(1912, 4367, 0), true);
                return true;
            }
        }

        if (option == 2) {
            if (obj.getId() == IRON_LADDER_10177) {
                Ladders.ladderUp(player, new Tile(2544, 3741, 0), true);
                return true;
            }
        }

        if (option == 3) {
            if (obj.getId() == IRON_LADDER_10177) {
                Ladders.ladderDown(player, new Tile(1799, 4407, 3), true);
                return true;
            }
        }
        return false;
    }
}

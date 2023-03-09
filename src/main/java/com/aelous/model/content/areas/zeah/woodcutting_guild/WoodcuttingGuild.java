package com.aelous.model.content.areas.zeah.woodcutting_guild;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 20, 2020
 */
public class WoodcuttingGuild extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            //entrance
            if(obj.getId() == GATE_28851 || obj.getId() == GATE_28852) {
                enterGuild(player);
                return true;
            }

            // Rope ladders up
            if(obj.getId() == ROPE_LADDER_28857) {
                Ladders.ladderUp(player, player.tile().x >= 1571 ? obj.tile().transform(-1, 0, 1) : obj.tile().transform(1, 0, 1),true);
                return true;
            }

            // Rope ladders down
            if(obj.getId() == ROPE_LADDER_28858) {
                Tile startPos = obj.tile().transform(-1, 0);
                player.smartPathTo(startPos);
                player.waitForTile(startPos, () -> {
                    Ladders.ladderUp(player, player.tile().x >= 1571 ? obj.tile().transform(1, 0, -1) : obj.tile().transform(-1, 0, -1),true);
                });
                return true;
            }

            // Dungeon exit
            if(obj.getId() == VINE_28856) {
                Ladders.ladderUp(player, new Tile(1606, 3508),true);
                return true;
            }

            // Dungeon entrance
            if(obj.getId() == CAVE_28855) {
                player.teleport(1596, 9900);
                return true;
            }
        }
        return false;
    }

    public static Area AREA_EAST = new Area(1608, 3487, 1655, 3517);
    public static Area AREA_WEST = new Area(1564, 3477, 1600, 3503);

    private void enterGuild(Player player) {
        if (player.getSkills().xpLevel(Skills.WOODCUTTING) < 60) {
            DialogueManager.sendStatement(player, "You need a Woodcutting level of 60 to access this guild.");
        } else {
            ObjectManager.removeObj(new GameObject(GATE_28851, new Tile(1657, 3505), 0, 2));
            ObjectManager.removeObj(new GameObject(GATE_28852, new Tile(1657, 3504), 0, 2));

            ObjectManager.addObj(new GameObject(28853, new Tile(1658, 3505), 0, 1));
            ObjectManager.addObj(new GameObject(28854, new Tile(1658, 3504), 0, 3));

            if (player.tile().x >= 1658) {
                player.getMovementQueue().walkTo(new Tile(1657, player.tile().y));
            } else {
                player.getMovementQueue().walkTo(new Tile(1658, player.tile().y));
            }

            TaskManager.submit(new TickAndStop(2) {
                @Override
                public void executeAndStop() {
                    ObjectManager.removeObj(new GameObject(28853, new Tile(1658, 3505), 0, 1));
                    ObjectManager.removeObj(new GameObject(28854, new Tile(1658, 3504), 0, 3));

                    ObjectManager.addObj(new GameObject(GATE_28851, new Tile(1657, 3505), 0, 2));
                    ObjectManager.addObj(new GameObject(GATE_28852, new Tile(1657, 3504), 0, 2));
                }
            });
            if (player.tile().x >= 1658)
                DialogueManager.npcChat(player, Expression.HAPPY, 7235, "Welcome to the woodcutting guild, adventurer.");
        }
    }
}

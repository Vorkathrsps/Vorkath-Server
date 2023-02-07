package com.aelous.model.content.areas.burthope.warriors_guild;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;

import static com.aelous.utility.ItemIdentifiers.WARRIOR_GUILD_TOKEN;

/**
 * @author Patrick van Elderen | March, 26, 2021, 09:48
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class CyclopsRoom {

    public static void handle_time_spent(Player player, boolean basement) {
        //Entry to the Cyclops room requires 100 Warrior guild tokens (will not be charged) and then 10 tokens are charged for every following minute you stay.
        if (basement) {
            TaskManager.submit(new Task("handle_time_spent_task", 1) {
                int internalTimer = 100;

                @Override
                protected void execute() {
                    //Player is offline
                    if (!player.isRegistered()) {
                        stop();
                    }

                    //Not in cyclops room
                    if (!insideBasementCyclopsRoom(player.tile())) {
                        stop();
                    }

                    if (internalTimer-- == 0) {
                        if (player.inventory().remove(new Item(WARRIOR_GUILD_TOKEN, 10), true)) {
                            player.message("<col=804080>10 of your tokens crumble away.");
                        } else {
                            player.teleport(new Tile(2909, 9968, 0));
                            player.message("<col=804080>Next time, please leave as soon as your time is up.");
                        }
                        internalTimer = 100;//Reset timer
                    }
                }
            });

            //10 tokens are spent upon entry to the room, with another 10 spent every following minute in the room.
        } else {
            player.inventory().remove(new Item(WARRIOR_GUILD_TOKEN, 10));
            TaskManager.submit(new Task("handle_time_spent_task", 1) {
                int internalTimer = 100;

                @Override
                protected void execute() {
                    //Player is offline
                    if (!player.isRegistered()) {
                        stop();
                    }

                    //Not in cyclops room
                    if (!insideTopFloorCyclopsRoom(player.tile())) {
                        stop();
                    }

                    if (internalTimer-- == 0) {
                        if (player.inventory().remove(new Item(WARRIOR_GUILD_TOKEN, 10), true)) {
                            player.message("<col=804080>10 of your tokens crumble away.");
                        } else {
                            player.teleport(new Tile(2846, 3540, 2));
                            player.message("<col=804080>Next time, please leave as soon as your time is up.");
                        }
                        internalTimer = 100;//Reset timer
                    }
                }
            });
        }
    }

    private static boolean insideTopFloorCyclopsRoom(Tile tile) {
        return tile.inArea(2838, 3543, 2876, 3556) || tile.inArea(2847, 3534, 2876, 3542);
    }

    private static boolean insideBasementCyclopsRoom(Tile tile) {
        return tile.inArea(2905, 9965, 2940, 9973) && !tile.inArea(2905, 9966, 2911, 9973);
    }

    //In the basement of the Warriors' Guild, accessible by a trap door west of the guild (exit through the food shop), is another large room containing higher level Cyclopes. Lorelai will require you to have a rune defender before she will allow access to the room. Only the Cyclopes that are located in the basement will drop the dragon defender.
}

package com.aelous.model.content.areas.wilderness;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.timers.TimerKey;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 12, 2020
 */
public class DesertedKeepLever extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == 1815) {

                //Check to see if the player is teleblocked
                if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.teleblockMessage();
                    return true;
                }

                TaskManager.submit(new TickAndStop(1) {
                    @Override
                    public void executeAndStop() {
                        player.animate(2140);
                        player.message("You pull the lever...");
                    }
                });

                GameObject spawned = new GameObject(88, obj.tile(), obj.getType(), obj.getRotation());
                TaskManager.submit(new TickAndStop(1) {
                    @Override
                    public void executeAndStop() {
                        ObjectManager.addObj(spawned);
                    }
                });

                TaskManager.submit(new TickAndStop(5) {
                    @Override
                    public void executeAndStop() {
                        ObjectManager.removeObj(spawned);
                        ObjectManager.addObj(obj);
                    }
                });

                TaskManager.submit(new TickAndStop(2) {
                    @Override
                    public void executeAndStop() {
                        player.animate(714);
                        player.graphic(111);
                    }
                });

                TaskManager.submit(new TickAndStop(4) {
                    @Override
                    public void executeAndStop() {
                        Tile targetTile = new Tile(3092,3488);
                        player.teleport(targetTile);
                        player.animate(-1);
                        player.message("...And teleport out of the wilderness.");
                    }
                });
                return true;
            }
        }
        return false;
    }
}

package com.aelous.model.content.raids.theatre.interactions;

import com.aelous.model.content.instance.InstanceConfiguration;
import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

public class TheatreInteractions extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        Theatre theatre = new Theatre(player, null, new TheatreArea(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, Theatre.rooms));

        if (obj.getId() == 32653) {
            theatre.startRaid();
            return true;
        }
        if (obj.getId() == 32755) {
            if (player.getClickDelay().elapsed(3000)) {
                if (player.tile().x < obj.tile().x) {
                    player.lock();
                    player.stepAbs(obj.getX() + 1, obj.getY(), MovementQueue.StepType.FORCED_WALK);
                    Chain.noCtx().runFn(2, () -> {
                        player.getClickDelay().reset();
                        player.unlock();
                    });
                } else {
                    player.lock();
                    player.stepAbs(obj.getX() - 1, obj.getY(), MovementQueue.StepType.FORCED_WALK);
                    Chain.noCtx().runFn(2, () -> {
                        player.getClickDelay().reset();
                        player.unlock();
                    });
                }
                return true;
            }
        }
        if (obj.getId() == 33113) {
            if (player.getClickDelay().elapsed(3000)) {
                if (player.tile().region() == 12613) {
                    player.lock();
                    player.teleport(3269, 4447);
                    player.waitForTile(new Tile(3269, 4447), () -> {
                        player.getClickDelay().reset();
                        player.unlock();
                    });
                } else if (player.tile().region() == 13125) {
                    player.lock();
                    player.teleport(3295, 4283);
                    player.waitForTile(new Tile(3295, 4283), () -> {
                        player.getClickDelay().reset();
                        player.unlock();
                    });
                } else if (player.tile().region() == 13122) {
                    player.lock();
                    player.teleport(3280, 4294);
                    player.waitForTile(new Tile(3280, 4294), () -> {
                        player.getClickDelay().reset();
                        player.unlock();
                    });
                } else if (player.tile().region() == 13123) {
                    player.lock();
                    player.teleport(3170, 4377);
                    player.waitForTile(new Tile(3170, 4377), () -> {
                        player.getClickDelay().reset();
                        player.unlock();
                    });
                }
                return true;
            }
        }
        return false;
    }
}

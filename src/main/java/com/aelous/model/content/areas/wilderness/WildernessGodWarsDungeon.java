package com.aelous.model.content.areas.wilderness;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

/**
 * Created by Nick on 8/28/2016.
 */
public class WildernessGodWarsDungeon extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == TUNNEL_ENTRANCE) {

                if (obj.tile().equals(3016, 3738)) {
                    if (!player.tile().equals(obj.tile().transform(1, 2, 0))) {
                        player.getMovementQueue().walkTo(obj.tile().transform(1, 2, 0));
                    }
                    player.lockDelayDamage();
                    TaskManager.submit(new TickAndStop(1) {
                        @Override
                        public void executeAndStop() {
                            teleportPlayer(player, 3065, 10159, 3);
                            player.unlock();
                        }
                    });
                }
                return true;
            }

            if (obj.getId() == TUNNEL_EXIT) {
                if (obj.tile().equals(3065, 10160)) {
                    if (!player.tile().equals(obj.tile().transform(0, -1, 0))) {
                        player.getMovementQueue().walkTo(obj.tile().transform(0, -1, 0));
                    }
                    player.lockDelayDamage();
                    TaskManager.submit(new TickAndStop(1) {
                        @Override
                        public void executeAndStop() {
                            teleportPlayer(player, 3017, 3740, 0);
                            player.unlock();
                        }
                    });
                }
                return true;
            }

            if (obj.getId() == JUTTING_WALL) {
                //TODO
                player.message("This wall is not supported yet, report this to an Administrator.");
                return true;
            }
        }
        return false;
    }

    private static final int TUNNEL_ENTRANCE = 26766;
    private static final int TUNNEL_EXIT = 26767;
    private static final int JUTTING_WALL = 26768;

    private void teleportPlayer(Player player, int x, int y, int z) {
        player.animate(2796);
        TaskManager.submit(new TickAndStop(2) {
            @Override
            public void executeAndStop() {
                player.animate(-1);
                player.teleport(new Tile(x, y, z));
            }
        });
    }

    private void passJuttingWall(Player player, int x, int y) {// Doesn't work from south side TODO
        /*r.onObject(JUTTING_WALL) @Suspendable {
            it.player().lockDelayDamage()
            it.player().animate(753)
            it.player().faceTile(Tile(0, it.player().tile().z))
            it.player().pathQueue().clear()
            it.player().pathQueue().interpolate(3066, 10147, PathQueue.StepType.FORCED_WALK)
            it.player().looks().render(756, 756, 756, 756, 756, 756, -1)
            it.delay(3)
            it.waitForTile(Tile(3066, 10147 ))
            it.player().looks().resetRender()
            it.addXp(Skills.AGILITY, 6.0)
            it.player().unlock()
        }*/
    }
}

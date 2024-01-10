package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.route.routes.DumbRoute;

/**
 * @author Origin | February, 24, 2021, 19:13
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ScorpiaGuardian {

    public static void heal(NPC scorpia, NPC minion) {
        minion.ignoreOccupiedTiles = true;

        //If they do not heal Scorpia in 15 seconds, they will despawn.
        TaskManager.submit(new Task("ScorpiaGuardianTask", 1) {
            int no_heal_ticks = 0;
            @Override
            protected void execute() {
                if(minion.dead() || minion.finished() || scorpia.dead() || scorpia.finished()) {
                    stop();
                    return;
                }

                if(!minion.tile().isWithinDistance(scorpia.tile(), 2) && !minion.finished()) {
                    no_heal_ticks++;
                }
                if(minion.tile().isWithinDistance(scorpia.tile(), 2)) {
                    scorpia.heal(1);
                  //  new Projectile(minion, scorpia,109,50,100,53,31,0).sendProjectile((Player) target);
                }

                if(no_heal_ticks == 25) {
                    World.getWorld().unregisterNpc(minion);
                    stop();
                    return;
                }
                // manually follow the boss, it's not our combat target.
                DumbRoute.step(minion, scorpia, 1);
            }
        });
    }
}

package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SCORPIA;

/**
 * @author Patrick van Elderen | February, 24, 2021, 19:13
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ScorpiaGuardian {

    public static void heal(NPC scorpia, NPC minion) {
        if (scorpia.id() == SCORPIA) {
            Chain.bound(null).runFn(8, () -> {
                if(minion.tile().isWithinDistance(scorpia.tile(), 2)) {
                    scorpia.heal(1);
                    new Projectile(minion, scorpia,109,50,100,53,31,0).sendProjectile();
                }
            });
        }

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

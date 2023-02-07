package com.aelous.model.content.skill.impl.slayer.superior_slayer;

import com.aelous.core.task.Task;
import com.aelous.model.World;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.timers.TimerKey;

/**
 * @author Patrick van Elderen | December, 21, 2020, 13:06
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RemoveSuperiorTask extends Task {

    private final Player player;
    private final NPC superior;

    public RemoveSuperiorTask(Player player, NPC superior) {
        super("RemoveSuperiorTask", 1);
        this.player = player;
        this.superior = superior;
    }

    @Override
    public void execute() {
        //The player or superior can't be found anymore stop task
        if (player.getIndex() < 1 || superior.getIndex() < 1) {
            stop();
            return;
        }

        //Despawn superior because he is no longer in combat
        boolean underAttack = CombatFactory.lastAttacker(superior) != null;

        //Timer passed we can do the in combat check
        if (!player.getTimers().has(TimerKey.SUPERIOR_BOSS_DESPAWN)) {
            if (!underAttack) {
                World.getWorld().unregisterNpc(superior);
                stop();
            }
        }
    }
}

package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 09, 2020 19:36
 */
public class LoopGFX implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        final int startId = Integer.parseInt(parts[1]);
        final int range = Integer.parseInt(parts[2]);

        if (startId < 0 || range <= 0) {
            return;
        }
        TaskManager.submit(new Task("LoopGFX", 3) {
            int gfx = startId;

            @Override
            protected void execute() {
                player.graphic(gfx, GraphicHeight.HIGH, 0);
                player.forceChat(""+gfx);
                gfx++;
                if (gfx >= range) {
                    this.stop();
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}

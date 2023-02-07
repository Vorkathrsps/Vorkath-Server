package com.aelous.model.content.skill.impl.hunter.trap;

import com.aelous.model.content.skill.impl.hunter.Hunter;
import com.aelous.model.content.skill.impl.hunter.trap.Trap.TrapState;
import com.aelous.core.task.Task;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.RandomGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single task which will run for each trap.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 *
 */
public final class TrapTask extends Task {

    private static final Logger logger = LogManager.getLogger(TrapTask.class);

    /**
     * The player this task is dependant of.
     */
    private final Player player;

    /**
     * The random generator which will generate random values.
     */
    private final RandomGen gen = new RandomGen();

    /**
     * Constructs a new {@link TrapTask}.
     * @param player    {@link #player}.
     */
    public TrapTask(Player player) {
        super("trap_task", 10, true);
        this.player = player;
    }

    @Override
    public void execute() {
        try {
            TrapProcessor trapProcessor = Hunter.GLOBAL_TRAPS.get(player);
            if (trapProcessor == null) {
                stop();
                return;
            }

            if (!trapProcessor.getTask().isPresent() || trapProcessor.getTraps().isEmpty()) {
                stop();
                return;
            }

            List<Trap> traps = new ArrayList<>(trapProcessor.getTraps());
            for (Trap trap : traps) {
                boolean withinDistance = player.getZ() == trap.getObject().getHeight() && Math.abs(player.getX() - trap.getObject().getX()) <= 15 && Math.abs(player.getY() - trap.getObject().getY()) <= 15;
                if (!withinDistance && !trap.isAbandoned()) {
                    Hunter.abandon(player, trap, false);
                }
            }
            trapProcessor.getTraps().removeIf(Trap::isAbandoned);

            Trap trap = gen.random(trapProcessor.getTraps());

            if (trap == null)
                return;

            if (!Hunter.getTrap(player, trap.getObject()).isPresent() || !trap.getState().equals(TrapState.PENDING)) {
                return;
            }
            trap.onSequence();
        } catch (Exception e) {
            logger.catching(e);
        }
    }
}

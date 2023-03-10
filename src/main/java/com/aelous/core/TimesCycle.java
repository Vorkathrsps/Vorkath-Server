package com.aelous.core;

import com.aelous.GameEngine;
import com.aelous.model.World;
import com.aelous.core.task.TaskManager;
import com.aelous.utility.NpcPerformance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;

import java.util.function.Consumer;

import static org.apache.logging.log4j.util.Unbox.box;

/**
 * Formerly known as TimesCx.
 *
 */
public class TimesCycle {

    private static final Logger logger = LogManager.getLogger(TimesCycle.class);

    public static class WorldPro {
        public long player_process, npc_process, player_npc_updating;
    }

    public WorldPro wp;

    public TimesCycle() {
        wp = new WorldPro();
    }

    //Objects don't use processing in this server.
    public long login, objs, tasks, world, gitems, total;

    public String COMPUTED_MSG = "";
    public static int lastComputedMsgTick = -1;
    public static boolean APPEND_WORLDINFO = false;
    public static boolean BENCHMARKING_ENABLED = false;

    public void computeAnd(Consumer<TimesCycle> c) {
        if (!BENCHMARKING_ENABLED) return;

        // already computed this tick
        if (lastComputedMsgTick == GameEngine.gameTicksIncrementor) {
            c.accept(this);
            return;
        }

        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long maxMem = Runtime.getRuntime().maxMemory();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sms cycle. Average: %sms. players: %s, npcs: %s, tasks: %s, objects: %s, Memory usage: %sMB/%sMB. ",
            box(total), box(Math.max(1, GameEngine.totalCycleTime) / Math.max(1, GameEngine.gameTicksIncrementor)), Unbox.box(World.getWorld().getPlayers().size()), box(World.getWorld().getNpcs().size()), box(TaskManager.getTaskAmount()), box(World.getWorld().getSpawnedObjs().size()), box((totalMem - freeMem) / 1024 / 1024), box(totalMem / 1024 / 1024)
        ));

        var sb3 = new StringBuilder();
        if (login > 0) sb3.append("login:").append(login).append(" ");
        if (objs > 0) sb3.append("objs:").append(objs).append(" ");
        if (tasks > 0) sb3.append("tasks:").append(tasks).append(" ");
        if (gitems > 0) sb3.append("gitems:").append(gitems).append(" ");
        if (sb3.length() > 0)
            sb.append("["+sb3+"] ");

        sb.append(String.format("[npc process:%s, player process:%s, gpi:%s][cycle #%s] ", box(wp.npc_process), box(wp.player_process), box(wp.player_npc_updating), box(GameEngine.gameTicksIncrementor)));

        StringBuilder sb2 = new StringBuilder();
        if ((int)(1. * NpcPerformance.npcA / 1_000_000.) > 0) sb2.append(String.format("prepath:%s ms, ", (int)(1. * NpcPerformance.npcA / 1_000_000.)));
        if ((int)(1. * NpcPerformance.cumeNpcB / 1_000_000.) > 0) sb2.append(String.format("route:%s ms, ", (int)(1. * NpcPerformance.cumeNpcB / 1_000_000.)));
        if ((int)(1. * NpcPerformance.cumeNpcC / 1_000_000.) > 0) sb2.append(String.format("homewalk:%s ms, ", (int)(1. * NpcPerformance.cumeNpcC / 1_000_000.)));
        if ((int)(1. * NpcPerformance.cumeNpcD / 1_000_000.) > 0) sb2.append(String.format("cb:%s ms, ", (int)(1. * NpcPerformance.cumeNpcD / 1_000_000.)));
        //if ((int)(1. * NpcPerformance.cumeNpcE / 1_000_000.) > 0) sb2.append(String.format("core:%s ms, ", (int)(1. * NpcPerformance.cumeNpcE / 1_000_000.)));
        if ((int)(1. * NpcPerformance.F / 1_000_000.) > 0) sb2.append(String.format("tasks:%s ms, ", (int)(1. * NpcPerformance.F / 1_000_000.)));
        if ((int)(1. * NpcPerformance.G / 1_000_000.) > 0) sb2.append(String.format("timers:%s ms, ", (int)(1. * NpcPerformance.G / 1_000_000.)));
        if ((int)(1. * NpcPerformance.H / 1_000_000.) > 0) sb2.append(String.format("agro+retreat:%s ms, ", (int)(1. * NpcPerformance.H / 1_000_000.)));
        if (sb2.length() > 0)
            sb.append("[npc sections: "+ sb2+"] ");

        if (APPEND_WORLDINFO)
            sb.append(World.getWorld().benchmark.breakdown());

        final String message = sb.toString();
        COMPUTED_MSG = message;
        lastComputedMsgTick = GameEngine.gameTicksIncrementor;

        //Change to logger.info to view this in the IntelliJ console.
        //if (GameServer.properties().displayCycleTime && (!GameServer.properties().linuxOnlyDisplayCycleTime || GameServer.isLinux())) {
            c.accept(this);
        //}
    }
}

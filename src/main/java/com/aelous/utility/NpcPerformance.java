package com.aelous.utility;

import com.google.common.base.Stopwatch;
import com.aelous.GameServer;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.Entity;
import com.aelous.utility.timers.TimerKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;

/**
 *
 * note that im putting {@link #targetVerification} / {@link #targetVerifMsg} down to internal clock inaccuracy. 
 *
 * @author Jak shadowrs tardisfan121@gmail.com
 */
public class NpcPerformance {

    // tracked globally
    public static boolean PERF_CHECK_MODE_ENABLED = true;
    private static final Logger logger = LogManager.getLogger(NpcPerformance.class);
    public static boolean DETAL_LOG_ENABLED = true;
    public static long npcA, cumeNpcB, cumeNpcC, cumeNpcD, cumeNpcE, F, G, H;

    // tracked per npc
    public Stopwatch actionSequence;
    public String action;
    public TaskManagerPerformance tmLog = new TaskManagerPerformance();
    public Stopwatch targetVerification;
    public String targetVerifMsg;
    public Stopwatch aggression;

    public ArrayList<TimerPerfEntry> offendersTimer;
    public Duration sumRuntimeTimers;
    public int timers;

    public static void resetWorldTime() {
        npcA = cumeNpcB = cumeNpcC = cumeNpcD = cumeNpcE = F = G = H = 0;
    }

    public void addTimerOffender(TimerPerfEntry e) {
        if (!DETAL_LOG_ENABLED)
            return;
        if (offendersTimer == null) {
            offendersTimer = new ArrayList<TimerPerfEntry>(0);
        }
        offendersTimer.add(e);
    }

    public static class TaskManagerPerformance {
        public Duration sumRuntimeTasks;
        public ArrayList<TaskPerfEntry> offenders;
        public int tasks;

        public void addOffender(TaskPerfEntry e) {
            if (!DETAL_LOG_ENABLED)
                return;
            if (offenders == null) {
                offenders = new ArrayList<TaskPerfEntry>(0);
            }
            offenders.add(e);
        }

        public String build() {
            if (offenders == null)
                return "none";
            StringBuilder sb = new StringBuilder();
            for (TaskPerfEntry offender : offenders) {
                if (GameServer.properties().displayCycleLag)
                    sb.append(String.format("task took %s ns code source: %s ", offender.name, offender.duration.toNanos()));
            }
            return sb.toString();
        }
    }

    public static class TaskPerfEntry {
        public String name;
        public Duration duration;
        public Task task;
    }

    public static class TimerPerfEntry {
        public TimerKey name;
        public Duration duration;
    }

    public void assess(Entity entity) {
        if (!DETAL_LOG_ENABLED)
            return;
        StringBuilder sb = new StringBuilder();
        // disabled for now cant figure out the micro source
        /*if (actionSequence != null && actionSequence.elapsed().toNanos() > 100_000) {
            sb.append("action in "+actionSequence.elapsed().toNanos()+" ns by "+action+". ");
        }*/
        if (tmLog.sumRuntimeTasks != null &&
            tmLog.sumRuntimeTasks.toMillis() > 0 && GameServer.properties().displayCycleLag) {
            sb.append("npc tasks sum "+ tmLog.sumRuntimeTasks.toNanos()+" ns by "+ tmLog.tasks+"/"+ TaskManager.getTaskAmount()+" tasks. ");
            if (tmLog.offenders != null) {
                sb.append(tmLog.build());
            }
        }
        if (tmLog != null && sumRuntimeTimers != null &&
            sumRuntimeTimers.toMillis() > 0 && GameServer.properties().displayCycleLag) {
            sb.append("npc timers sum "+sumRuntimeTimers.toNanos()+" ns by "+timers+" timers. ");
            if (offendersTimer != null) {
                for (TimerPerfEntry offender : offendersTimer) {
                    sb.append(String.format("task took %s ns key: %s ", offender.name.name(), offender.duration.toNanos()));
                }
            }
        }
        if (targetVerifMsg != null) {
            sb.append(targetVerifMsg);
        }
        if (aggression != null && aggression.elapsed().toMillis() > 0 && GameServer.properties().displayCycleLag) {
            sb.append("agro in "+aggression.elapsed().toNanos()+" ns. ");
        }
        final String res = sb.toString();
        if (res.length() > 0) {
            logger.error("Mob {} performance: {}", entity, res);
        }
    }

    public void reset() {
        actionSequence = null;
        tmLog.sumRuntimeTasks = null;
        tmLog.tasks = 0;
        if (tmLog.offenders != null)
            tmLog.offenders.clear();
        if (offendersTimer != null)
            offendersTimer.clear();
        timers = 0;
        action = null;
        targetVerifMsg = null;
    }
}

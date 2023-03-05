package com.aelous.model.entity.player;

import com.aelous.core.TimesCycle;

import java.text.DecimalFormat;

public class PlayerPerformanceTracker {
    long logout, qtStuffs, timers, actions, tasks, walkto, bmove, move, regions, controllers, cbBountyFlush, prayers, end, total;

    @Override
    public String toString() {
        return breakdown();
    }
    static final DecimalFormat df = new DecimalFormat("#.##");

    public String breakdown() {
        if (!TimesCycle.BENCHMARKING_ENABLED) return "N/A";
        StringBuilder sb2 = new StringBuilder();
        if ((int)(1. * logout / 1_000_000.) > 0)
            sb2.append(String.format("logout:%s ms, ", df.format(1. * logout / 1_000_000.)));
        if ((int)(1. * qtStuffs / 1_000_000.) > 0)
            sb2.append(String.format("qtStuffs:%s ms, ",df.format(1. * qtStuffs / 1_000_000.)));
        if ((int)(1. * timers / 1_000_000.) > 0)
            sb2.append(String.format("timers:%s ms, ",df.format(1. * timers / 1_000_000.)));
        if ((int)(1. * actions / 1_000_000.) > 0)
            sb2.append(String.format("actions:%s ms, ",df.format(1. * actions / 1_000_000.)));
        if ((int)(1. * tasks / 1_000_000.) > 0)
            sb2.append(String.format("tasks:%s ms, ",df.format(1. * tasks / 1_000_000.)));
        if ((int)(1. *walkto / 1_000_000.) > 0)
            sb2.append(String.format("walkto:%s ms, ",df.format(1. *walkto / 1_000_000.)));
        if ((int)(1. * bmove / 1_000_000.) > 0)
            sb2.append(String.format("bmove:%s ms, ",df.format(1. * bmove / 1_000_000.)));
        if ((int)(1. * move / 1_000_000.) > 0)
            sb2.append(String.format("move:%s ms, ",df.format(1. * move / 1_000_000.)));
        if ((int)(1. * regions / 1_000_000.) > 0)
            sb2.append(String.format("regions:%s ms, ",df.format(1. * regions / 1_000_000.)));
        if ((int)(1. * controllers / 1_000_000.) > 0)
            sb2.append(String.format("controllers:%s ms, ",df.format(1. * controllers / 1_000_000.)));
        if ((int)(1. * cbBountyFlush / 1_000_000.) > 0)
            sb2.append(String.format("cbBountyFlush:%s ms, ",df.format(1. * cbBountyFlush / 1_000_000.)));
        if ((int)(1. * end / 1_000_000.) > 0)
            sb2.append(String.format("end:%s ms, ",df.format(1. * end / 1_000_000.)));
       // if ((int)(1. * total / 1_000_000.) > 0) // confusing sum, just have breakdown only
       //     sb2.append(String.format("player total:%s ms, ",df.format(1. * total / 1_000_000.)));
        if (sb2.toString().length() > 0)
            return sb2.toString();
        return "N/A";
    }

    public void pulse() {
        logout = qtStuffs =  timers =  actions = tasks =  walkto =  bmove = move = regions =  controllers = cbBountyFlush =  prayers =  end = total = 0;
    }

    public void reset() {
        logout = qtStuffs = timers = actions = tasks = walkto = bmove = move = regions = controllers = cbBountyFlush
            = end = total = 0;
    }
}

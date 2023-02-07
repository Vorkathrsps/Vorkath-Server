package com.aelous.model.content.skill.impl.slayer.slayer_task;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;

/**
 * @author PVE
 * @Since juli 19, 2020
 */
public class SlayerTaskDef {

    public SlayerTaskDef() {

    }

    private int weighing, min, max, creatureUid;

    public int getWeighing() {
        return weighing;
    }

    public void setWeighing(int weighing) {
        this.weighing = weighing;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getCreatureUid() {
        return creatureUid;
    }

    public void setCreatureUid(int creatureUid) {
        this.creatureUid = creatureUid;
    }

    public int range(Player player) {
        var defaultMin = 0;
        var defaultMax = 0;
        var task = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_ID,0);
        if(task > 0) {
            SlayerCreature taskdef = SlayerCreature.lookup(task);
            if(taskdef != null) {
                if(taskdef.bossTask) {
                    defaultMin = 20;
                    defaultMax = 35;
                } else {
                    defaultMin = 25;
                    defaultMax = 60;
                }
            }
        }
        return World.getWorld().random(defaultMin, defaultMax);
    }

    @Override
    public String toString() {
        return "SlayerTaskDef{" +
            "weighing=" + weighing +
            ", min=" + min +
            ", max=" + max +
            ", creatureUid=" + creatureUid +", slayreq="+SlayerCreature.lookup(getCreatureUid()).req+", cbreq="+SlayerCreature.lookup(getCreatureUid()).cbreq+
            '}';
    }
}

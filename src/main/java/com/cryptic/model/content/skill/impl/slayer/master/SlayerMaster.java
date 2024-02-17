package com.cryptic.model.content.skill.impl.slayer.master;

import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTaskDef;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.QuestTab;
import com.cryptic.model.entity.player.Skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.cryptic.model.entity.player.QuestTab.InfoTab.SLAYER_TASK;

/**
 * @author PVE
 * @Since juli 19, 2020
 */
public class SlayerMaster {

    public int npcId;
    public int id;

    public final List<SlayerTaskDef> defs = new ArrayList<>();

    /*private SlayerTaskDef randomTask(Player player) {
        int last = player.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0);

        final int[] tmp = {0};

       *//* TreeMap<Integer, SlayerTaskDef> taskMap = new TreeMap<>();
        defs.forEach(task -> {
            if (task != null && task.getCreatureUid() != last &&
                player.getSkills().xpLevel(Skills.SLAYER) >= SlayerCreature.lookup(task.getCreatureUid()).req &&
                player.getSkills().combatLevel() >= SlayerCreature.lookup(task.getCreatureUid()).cbreq &&
                //!player.getSlayerRewards().isTaskBlocked(task) && player.getSlayerRewards().canAssign(task)) {
                taskMap.put(tmp[0], task);
                tmp[0] += task.getWeighing();
            }
        });

        if (tmp[0] == 0) {
            return null;
        }*//*

        int rnd = World.getWorld().random(tmp[0] - 1);
        Map.Entry<Integer, SlayerTaskDef> entry = taskMap.floorEntry(rnd);
        return entry.getValue();
    }*/

    public static void assign(Player player, int id) {
        var master = Slayer.master(id);
        if (master == null) return;
        SlayerTaskDef def = null;

        if(def == null) {
            System.out.println("no task available.");
            return;
        }

        player.putAttrib(AttributeKey.SLAYER_TASK_ID, def.getCreatureUid());
        int task_amt = player.getSlayerRewards().slayerTaskAmount(player, def);

        player.putAttrib(AttributeKey.SLAYER_TASK_AMT, task_amt);
        player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
        Slayer.displayCurrentAssignment(player);
    }
}

package com.cryptic.model.content.skill.impl.slayer;

import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

/**
 * @author PVE
 * @Since juli 20, 2020
 */
public class Slayer {

    public static boolean creatureMatches(Player player, int id) {
        SlayerCreature task = SlayerCreature.lookup(player.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0));
        return task != null && task.matches(id);
    }

    public static String taskName(int id) {
        return SlayerCreature.lookup(id) != null ? Utils.formatEnum(SlayerCreature.lookup(id).name()) : "None";
    }

}

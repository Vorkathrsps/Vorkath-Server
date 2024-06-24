package com.cryptic.model.content.skill.impl.slayer.superior_slayer;

import com.cryptic.GameServer;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;

import static com.cryptic.model.entity.attributes.AttributeKey.PLAYER_UID;

public class SuperiorSlayer {

    public static void trySpawn(Player player, SlayerTask task, NPC npc) {
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.BIGGER_AND_BADDER)) {
            return;
        }
        int superior = findSuperior(task);
        if (superior == -1) return;
        if (superior == npc.id()) return;
        int odds = 80;
        if (player.getPlayerRights().isAdministrator(player) && !GameServer.properties().production) odds = 3;
        if (Utils.rollDie(odds, 1)) {
            NPC boss = new NPC(superior, npc.tile());
            World.getWorld().registerNpc(boss);
            boss.respawns(false);
            Long uid = player.<Long>getAttribOr(PLAYER_UID, 0L);
            boss.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(uid, player));
            boss.getCombatInfo().aggressive = true;
            TaskManager.submit(new RemoveSuperiorTask(player, boss));
            player.getTimers().register(TimerKey.SUPERIOR_BOSS_DESPAWN, 200);
            player.message("<col=" + Color.RED.getColorValue() + ">A superior foe has appeared...");
        }
    }

    private static int findSuperior(SlayerTask task) {
        var npc = -1;
        switch (task.getUid()) {
            case 34 -> npc = 7410;
            case 25 -> npc = 7401;
            case 23 -> npc = 7397;
            case 28 -> npc = 7404;
            case 41 -> npc = 7338;
            case 36 -> npc = 7409;
            case 32 -> npc = 7411;
            case 26 -> npc = 7402;
        }
        return npc;
    }

    private static int getSuperior(SlayerCreature taskDef) {
        return switch (taskDef) {
            case ABYSSAL_DEMON -> 7410;
            case CAVE_HORRORS -> 7401;
            case BLOODVELDS -> 7397;
            case DUST_DEVILS -> 7404;
            case CAVE_CRAWLER -> 7389;
            case CRAWLING_HANDS -> 7388;
            case ROCKSLUG -> 7392;
            case DARK_BEASTS -> 7409;
            case NECHRYAEL -> 7411;
            case SMOKE_DEVILS -> 7406;
            case ABERRANT_SPECRES -> 7402;
            case PYREFIEND -> 7394;
            case JELLIES -> 7399;
            default -> -1;
        };
    }
}

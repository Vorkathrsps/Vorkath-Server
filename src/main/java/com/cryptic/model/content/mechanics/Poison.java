package com.cryptic.model.content.mechanics;

import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.attributes.AttributeKey;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.Venom;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.InfectionType;
import com.cryptic.model.entity.player.Player;

/**
 * Created by Bart on 11/18/2015.
 */
public class Poison {

    private static int determineHit(int poisonticks) {
        return poisonticks / 5 + 1;
    }

    public static int ticksForDamage(int damage) {
        return damage * 5 - 4;
    }

    public static boolean poisoned(Entity e) {
        return (int) e.getAttribOr(AttributeKey.POISON_TICKS, 0) > 0;
    }

    public static void cure(Player player) {
        player.putAttrib(AttributeKey.POISON_TICKS,0);
        player.setInfection(InfectionType.HEALTHY);
    }

    public static void cureAndImmune(Player player, int immunityTicks) {
        player.setInfection(InfectionType.HEALTHY);
        player.putAttrib(AttributeKey.POISON_TICKS, -immunityTicks);
    }

    public static void onLogin(Player me) {
        setTimer(me);
        int ticks = me.getAttribOr(AttributeKey.POISON_TICKS, 0);
        if(ticks > 0) {
            me.setInfection(InfectionType.POISON_INFECTION);
        }
    }

    public static void setTimer(Entity entity) {
        if (entity.getAttribOr(AttributeKey.POISON_TASK_RUNNING, false))
            return;
        entity.putAttrib(AttributeKey.POISON_TASK_RUNNING, true);
        TaskManager.submit(new Task("PoisonTask", 30, false) {//Every 18 seconds

            @Override
            protected void execute() {
                if(!entity.isRegistered() || entity.dead()) {
                    stop();
                    entity.clearAttrib(AttributeKey.POISON_TASK_RUNNING);
                    return;
                }

                if (Venom.venomed(entity))
                    return;

                if (entity.isPlayer()) {
                    Player player = (Player) entity;

                    // Grab value. More than 0 means we're poisoned for X ticks, lower than X means we're immune.
                    var poisonTicks = player.<Integer>getAttribOr(AttributeKey.POISON_TICKS, 0);

                    if (poisonTicks > 0) {
                        player.hit(player, Math.min(20, determineHit(poisonTicks)), HitMark.POISON);
                        player.putAttrib(AttributeKey.POISON_TICKS, poisonTicks - 1); // reduce as normal
                    } else if (poisonTicks < 0) {
                        player.putAttrib(AttributeKey.POISON_TICKS, poisonTicks + 1); // increment it back to 0 from negative (from being immune)
                    }
                } else {
                    NPC npc = (NPC) entity;
                    // Grab value. More than 0 means we're poisoned for X ticks, lower than X means we're immune.
                    var poisonTicks = npc.<Integer>getAttribOr(AttributeKey.POISON_TICKS, 0);

                    if (poisonTicks > 0) {
                        npc.hit(null, Math.min(20, determineHit(poisonTicks)), HitMark.POISON);
                        npc.putAttrib(AttributeKey.POISON_TICKS, poisonTicks - 1);
                    } else if (poisonTicks < 0) {
                        npc.putAttrib(AttributeKey.POISON_TICKS, poisonTicks + 1);
                    }
                }
            }
        });
    }
}

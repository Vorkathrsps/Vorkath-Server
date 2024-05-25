package com.cryptic.model.content.areas.edgevile;

import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

public class AFKZone extends PacketInteraction {
    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == 7785) {
            handleHunterAndFarming(player);
            return true;
        }
        if (npc.id() == 10571) {
            handleFishing(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == 4469) {
            handleEntrance(player);
            return true;
        }
        if (obj.getId() == 35877) {
            handleCooking(player);
            return true;
        }
        if (obj.getId() == 25824) {
            handleCrafting(player);
            return true;
        }
        if (obj.getId() == 9221) {
            handleWoodcutting(player);
            return true;
        }
        if (obj.getId() == 33321) {
            handleFiremaking(player);
            return true;
        }
        if (obj.getId() == 36555) {
            handleSmithing(player);
            return true;
        }
        if (obj.getId() == 40384) {
            handleThieving(player);
            return true;
        }
        if (obj.getId() == 31984) {
            handleSlayer(player);
            return true;
        }
        return false;
    }

    private static void handleEntrance(final Player player) {
        final float time = Utils.ticksToSeconds(14400);
        int transformX = player.getAbsX() == 3087 ? -1 : 1;
        player.lock();
        player.getTimers().register(TimerKey.AFK_TIMEOUT, (int) time);
        player.stepAbs(player.tile().transform(transformX, 0), MovementQueue.StepType.FORCED_WALK);
        Chain.noCtx().runFn(1, player::unlock);
    }


    private static void handleFishing(final Player player) {
        Chain.bound(player).name("afk_zone_spirit_pool_task").runFn(1, () -> {
            player.animate(618);
        }).repeatingTask(8, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(618);
            player.skills().addXp(Skill.FISHING.getId(), 5);
        });
    }

    private static void handleHunterAndFarming(final Player player) {
        Chain.bound(player).name("afk_zone_herbiboar_task").runFn(1, () -> {
            player.animate(2282);
        }).repeatingTask(8, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(2282);
            player.skills().addXp(Skill.FARMING.getId(), 5);
            player.skills().addXp(Skill.HUNTER.getId(), 2.5);
            player.skills().addXp(Skill.HERBLORE.getId(), 1.5);
        });
    }

    private static void handleSlayer(final Player player) {
        Chain.bound(player).name("afk_zone_slayer_task").runFn(1, () -> {
            player.animate(7514);
            player.graphic(1171);
        }).repeatingTask(8, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(7514);
            player.graphic(1171);
            player.skills().addXp(Skill.SLAYER.getId(), 5);
        });
    }

    private static void handleThieving(final Player player) {
        final float time = Utils.ticksToSeconds(7200);
        player.getTimers().register(TimerKey.AFK_TIMER, (int) time);
        player.putAttrib(AttributeKey.AFK, true);
        Chain.bound(player).name("afk_zone_thieving_task").runFn(1, () -> {
            player.animate(881);
        }).repeatingTask(8, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(881);
            player.skills().addXp(Skill.THIEVING.getId(), 5);
        });
    }

    private static void handleSmithing(final Player player) {
        Chain.bound(player).name("afk_zone_smithing_task").runFn(1, () -> {
            player.animate(899);
        }).repeatingTask(7, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(899);
            player.skills().addXp(Skill.SMITHING.getId(), 5);
        });
    }

    private static void handleFiremaking(final Player player) {
        Chain.bound(player).name("afk_zone_firemaking_task").runFn(1, () -> {
            player.animate(10570);
        }).repeatingTask(7, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(10570);
            player.skills().addXp(Skill.FIREMAKING.getId(), 5);
        });
    }

    private static void handleWoodcutting(final Player player) {
        Chain.bound(player).name("afk_zone_woodcutting_task").runFn(1, () -> {
            player.animate(10074);
        }).repeatingTask(7, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(10074);
            player.skills().addXp(Skill.WOODCUTTING.getId(), 5);
        });
    }

    private static void handleCrafting(final Player player) {
        Chain.bound(player).name("afk_zone_crafting_task").runFn(1, () -> {
            player.animate(894);
        }).repeatingTask(8, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(894);
            player.skills().addXp(Skill.CRAFTING.getId(), 5);
            player.skills().addXp(Skill.FLETCHING.getId(), 2.5);
        });
    }

    private static void handleCooking(final Player player) {
        Chain.bound(player).name("afk_zone_cooking_task").runFn(1, () -> {
            player.animate(896);
        }).repeatingTask(8, task -> {
            if (task.isStopped()) {
                task.stop();
                return;
            }
            player.animate(896);
            player.skills().addXp(Skill.COOKING.getId(), 5);
        });
    }
}

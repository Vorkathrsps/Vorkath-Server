package com.cryptic.model.content.skill.impl.firemaking.forestry;

import com.cryptic.model.World;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.chainedwork.Chain;

public class Bonfire extends PacketInteraction {

    public void sequence(Player player, Logs logs, GameObject gameObject) {
        if (logs == null) return;
        Chain.bound(player).runFn(2, () -> {
            animateAndRemove(player, logs);
            addExperience(player, gameObject, logs.experience);
        }).repeatingTask(5, burn -> {
            if (!player.getInventory().contains(logs.id)) {
                burn.stop();
                return;
            }
            handleFiremakingAchievements(player);
            animateAndRemove(player, logs);
            addExperience(player, gameObject, logs.experience);
        });
    }

    private static void handleFiremakingAchievements(Player player) {
        AchievementsManager.activate(player, Achievements.FIREMAKING_I, 1);
        AchievementsManager.activate(player, Achievements.FIREMAKING_II, 1);
        AchievementsManager.activate(player, Achievements.FIREMAKING_III, 1);
        AchievementsManager.activate(player, Achievements.FIREMAKING_IV, 1);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == 49927) {
            if (option == 1) {
                for (var log : Logs.values()) {
                    if (log == null) return false;
                    if (player.getInventory().contains(log.id)) {
                        if (skillRequirement(player, log)) return false;
                        sequence(player, log, object);
                        return true;
                    }
                }
            }
            if (option == 3) {
                player.animate(10083);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        if (object.getId() == 49927) {
            for (var log : Logs.values()) {
                if (log == null) return false;
                if (log.id == item.getId()) {
                    if (skillRequirement(player, log)) return false;
                    sequence(player, log, object);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void animateAndRemove(Player player, Logs logs) {
        player.animate(logs.animation);
        player.getInventory().remove(logs.id, 1);
    }

    private void addExperience(Player player, GameObject gameObject, double experience) {
        Chain.noCtx().runFn(4, () -> World.getWorld().sendClippedTileGraphic(2576, gameObject.tile(), 0, 30)).then(1, () -> player.getSkills().addXp(Skill.FIREMAKING.getId(), experience));
    }

    private boolean skillRequirement(Player player, Logs log) {
        if (player.getSkills().level(Skills.FIREMAKING) < log.level) {
            player.message(Color.RED.wrap("You need a Firemaking level of " + log.level + " to burn this log."));
            return true;
        }
        return false;
    }
}

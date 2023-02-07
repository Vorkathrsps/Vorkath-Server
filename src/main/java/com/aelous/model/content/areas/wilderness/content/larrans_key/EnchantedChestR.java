/*
package net.aelous.game.content.areas.wilderness.content.larrans_key;

import net.aelous.game.content.achievements.Achievements;
import net.aelous.game.content.achievements.AchievementsManager;
import net.aelous.game.world.World;
import net.aelous.model.entity.AttributeKey;
import net.aelous.model.entity.combat.skull.SkullType;
import net.aelous.model.entity.combat.skull.Skulling;
import net.aelous.model.entity.mob.player.Player;
import net.aelous.game.world.items.Item;
import net.aelous.game.world.object.GameObject;
import net.aelous.net.packet.interaction.PacketInteraction;
import net.aelous.util.Color;
import net.aelous.util.Utils;
import net.aelous.util.chainedwork.Chain;

import static net.aelous.game.content.collection_logs.LogType.KEYS;
import static net.aelous.util.CustomItemIdentifiers.*;

public class EnchantedChestR extends PacketInteraction {

    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == 4121) {
            player.teleblock( 250, true);
            Skulling.assignSkullState(player, SkullType.WHITE_SKULL);
            if (player.hasPetOut("Deranged archaeologist")) {
                Skulling.assignSkullState(player, SkullType.NO_SKULL);
            }
            if (player.skills().combatLevel() < 126) {
                player.message(Color.RED.wrap("You need to be at least level 126 to open this chest."));
                return true;
            }
            if (player.inventory().contains(ENCHANTED_KEY_I)) {
                open(player, ENCHANTED_KEY_I);
            } else {
                player.message("This enchanted chest wont budge, I think I need to find a key that fits.");
            }
            return true;
        }
        return false;
    }

    private static void open(Player player, int key) {
        if(!player.inventory().contains(key)) {
            return;
        }

        player.animate(536);
        player.lock();
        Chain.bound(player).runFn(1, () -> {
            player.inventory().remove(new Item(key, 1), true);
            int roll = Utils.percentageChance(player.extraItemRollChance()) ? 2 : 1;
            for (int i = 0; i < roll; i++) {
                Item reward = EnchantedChestRLootTable.rewardTables(key);

                if (reward == null)
                    return;

                //Collection logs
                KEYS.log(player, key, reward);
                player.inventory().addOrDrop(reward);
            }

            if (key == ENCHANTED_KEY_I) {
                int keysUsed = (Integer) player.getAttribOr(AttributeKey.ENCHANTED_KEYS_R_OPENED, 0) + 1;
                player.putAttrib(AttributeKey.ENCHANTED_KEYS_R_OPENED, keysUsed);
            }

            //Update achievements
            AchievementsManager.activate(player, Achievements.ENCHANTED_LOOTER_R_I, 1);
            AchievementsManager.activate(player, Achievements.ENCHANTED_LOOTER_R_II, 1);
            AchievementsManager.activate(player, Achievements.ENCHANTED_LOOTER_R_III, 1);
            player.unlock();
        });
    }
}
*/

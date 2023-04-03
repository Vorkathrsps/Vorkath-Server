package com.aelous.model.content.areas.wilderness.content.larrans_key;

import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.model.content.collection_logs.LogType.KEYS;

/**
 * @author Patrick van Elderen | February, 17, 2021, 14:17
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class LarransChest extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == 34832) {
            if (player.getSkills().combatLevel() < 3 && !player.getIronManStatus().ironman()) {
                player.message(Color.RED.wrap("You need to be at least level 126 to open this chest."));
                return true;
            }
            if (player.inventory().contains(ItemIdentifiers.LARRANS_KEY)) {
                open(player, ItemIdentifiers.LARRANS_KEY);
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
                Item reward = LarransKeyLootTable.rewardTables(key);

                if (reward == null)
                    return;

                //Collection logs
                KEYS.log(player, key, reward);

                //Send a world message that someone opened the Larran's chest
                World.getWorld().sendWorldMessage("<img=2010>[<col=" + Color.MEDRED.getColorValue() + ">Larran's chest</col>]: " + "<col=1e44b3>" + player.getUsername() + " has just looted the Larran's chest with a Larran's key!");

                //When we receive a rare loot send a world message
                if (reward.getValue() >= 30_000) {
                    boolean amOverOne = reward.getAmount() > 1;
                    String amtString = amOverOne ? "x " + Utils.format(reward.getAmount()) + "" : Utils.getAOrAn(reward.name());
                    String msg = "<img=2010>[<col=" + Color.MEDRED.getColorValue() + ">Larran's chest</col>]: " + "<col=1e44b3>" + player.getUsername() + " has received " + amtString + " " + reward.unnote().name() + "!";
                    World.getWorld().sendWorldMessage(msg);
                }
                player.inventory().addOrDrop(reward);
            }

            //Give half a teleblock for tier I and a full for tier II and III when opening the Larran's chest.
            player.teleblock(key == ItemIdentifiers.LARRANS_KEY ? 250 : 500, true);

            if (key == ItemIdentifiers.LARRANS_KEY) {
                int keysUsed = (Integer) player.getAttribOr(AttributeKey.LARRANS_KEYS_TIER_ONE_USED, 0) + 1;
                player.putAttrib(AttributeKey.LARRANS_KEYS_TIER_ONE_USED, keysUsed);
            }

            //Update achievements
            AchievementsManager.activate(player, Achievements.LARRANS_LOOTER_I, 1);
            player.unlock();
        });
    }
}

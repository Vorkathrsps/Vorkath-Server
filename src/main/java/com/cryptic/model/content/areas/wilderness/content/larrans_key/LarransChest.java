package com.cryptic.model.content.areas.wilderness.content.larrans_key;

import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.World;
import com.cryptic.model.content.items.loot.CollectionItemHandler;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.model.content.collection_logs.LogType.KEYS;

/**
 * @author Origin | February, 17, 2021, 14:17
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
                if (CollectionItemHandler.rollKeyReward(player, ItemIdentifiers.LARRANS_KEY)) {
                    return true;
                }
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
                World.getWorld().sendWorldMessage("<img=2010><img=2016>[<col=" + Color.MEDRED.getColorValue() + ">Larran's chest</col>]: " + "<col=1e44b3>" + player.getUsername() + " has just looted the Larran's chest with a Larran's key!");

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
            //AchievementsManager.activate(player, Achievements.LARRANS_LOOTER_I, 1);
            player.unlock();
        });
    }
}

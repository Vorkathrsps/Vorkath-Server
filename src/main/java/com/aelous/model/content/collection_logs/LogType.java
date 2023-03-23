package com.aelous.model.content.collection_logs;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.rights.PlayerRights;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;

/**
 * @author PVE
 * @Since juli 15, 2020
 */
public enum LogType {

    BOSSES, MYSTERY_BOX, KEYS, OTHER;

    public void log(Player killer, int keyId, Item reward) {
        if (reward == null) {
            System.out.println("Reward was null.");
            return;
        }
        reward = reward.copy(); // make sure not to handle the same instance as other game code uses

        Item finalReward = reward;
        Arrays.stream(Collection.values()).filter(e -> e.getLogType() == this && Arrays.stream(e.getObtainables()).anyMatch(colItem -> colItem.getId() == finalReward.getId()) && Arrays.stream(e.getKey()).anyMatch(n -> n == keyId)).findFirst().ifPresent(c -> {
            int before = killer.getCollectionLog().totalObtained(c);
            killer.getCollectionLog().collectionLog.compute(c, (k, v) -> {
                if (v == null) {
                    v = new ArrayList<Item>();
                    v.add(finalReward);
                } else {
                    Optional<Item> first = v.stream().filter(loot -> loot.getId() == finalReward.getId()).findFirst();
                    if (first.isPresent()) {
                        first.get().setAmount(first.get().getAmount() + finalReward.getAmount());
                    } else {
                        v.add(finalReward);
                    }
                }

                //Let the killer know when we completed a collection log.
                final int totalCollectables = c.totalCollectables();
                int after = killer.getCollectionLog().totalObtained(c);
                if(after != before && after == totalCollectables) {
                    killer.message("<col=297A29>Congratulations! You have completed the "+c.getName()+" collection log.");
                    World.getWorld().sendWorldMessage(format("<img=1953>[<col="+ Color.MEDRED.getColorValue()+">Collection log</col>]: %s just completed the %s collection log.", (PlayerRights.getCrown(killer) + killer.getUsername()), c.getName()));
                }
                return v;
            });
        });
    }
}

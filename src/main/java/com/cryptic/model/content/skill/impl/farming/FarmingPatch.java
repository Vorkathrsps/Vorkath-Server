package com.cryptic.model.content.skill.impl.farming;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.utility.chainedwork.Chain;

/**
 * @author Sharky
 * @Since June 16, 2023
 */
public class FarmingPatch {

    public byte stage = 0;
    long time = System.currentTimeMillis();

    public void setTime() {
        time = System.currentTimeMillis();
    }

    public boolean isRaked() {
        return stage == 3;
    }

    public void process(Player player) {
        if (stage == 0)
            return;
        long elapsed = (System.currentTimeMillis() - time) / 60_000;
        int grow = 1;

        if (elapsed >= grow) {
            for (int i = 0; i < elapsed / grow; i++) {
                if (stage == 0) {
                    player.getFarming().varbitUpdate();
                    return;
                }

                stage = ((byte) (stage - 1));
                player.getFarming().varbitUpdate();
            }
            setTime();
        }
    }

    public void click(Player player, int option) {
        if (option == 1)
            rake(player);
    }

    boolean raking = false;
    public void rake(final Player p) {
        if(raking)
            return;
        if (isRaked()) {
            p.message("This plot is fully raked. Try planting a seed.");
            return;
        }
        if (!p.inventory().contains(5341)) {
            p.message("This patch needs to be raked before anything can grow in it.");
            p.message("You do not have a rake in your inventory.");
            return;
        }
        raking = true;
        p.animate(2273);
        Chain.bound(p).repeatingTask(3, t -> {
            if (!p.inventory().contains(5341)) {
                p.message("This patch needs to be raked before anything can grow in it.");
                p.message("You do not have a rake in your inventory.");
                t.stop();
                return;
            }
            p.animate(2273);
            setTime();
            FarmingPatch farmingPatch = FarmingPatch.this;
            farmingPatch.stage = ((byte) (farmingPatch.stage + 1));
            doConfig(p);
            p.skills().addXp(Skills.FARMING, 4);
            p.getInventory().addOrDrop(new Item(6055, 1));
            if (isRaked()) {
                p.message("Your patch is raked, no compost is required, just plant and water!");
                p.animate(65_535);
                t.stop();
            }
        });
        raking = false;
        p.animate(65535);
    }

    public static void doConfig(Player player) {
        player.getFarming().varbitUpdate();
    }

    public int getStage() {
        return stage;
    }
}

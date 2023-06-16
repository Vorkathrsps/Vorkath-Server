package com.aelous.model.content.skill.impl.farming;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.model.entity.attributes.AttributeKey.RAKING;
import static com.aelous.utility.ItemIdentifiers.RAKE;
import static com.aelous.utility.ItemIdentifiers.WEEDS;

/**
 * @author Sharky
 * @Since June 16, 2023
 */
public class FarmingPatch {

    public byte stage = 0;
    private long time = System.currentTimeMillis();

    public long getTime() {
        return time;
    }

    /**
     * Only used for saving.
     * @param time The time being saved
     */
    public void setTime(long time) {
        this.time = time;
    }

    public void setTime() {
        time = System.currentTimeMillis();
    }

    public boolean raked() {
        return stage == 3;
    }

    public void process(Player player) {
        if (stage == 0)
            return;
        long elapsed = (System.currentTimeMillis() - time) / 60_000;
        int grow = 1;

        if (elapsed >= grow) {
            for (int index = 0; index < elapsed / grow; index++) {
                if (stage == 0) {
                    player.getFarming().updateVarpFor(player);
                    return;
                }

                stage = ((byte) (stage - 1));
                player.getFarming().updateVarpFor(player);
            }
            setTime();
        }
    }

    public void rake(Player player) {
        if(player.getAttribOr(RAKING, false))
            return;

        if (raked()) {
            player.message("This plot is fully raked. Try planting a seed.");
            return;
        }
        boolean hasRake = player.inventory().contains(RAKE);
        if (!hasRake) {
            player.message("This patch needs to be raked before anything can grow in it.");
            player.message("You do not have a rake in your inventory.");
            return;
        }
        player.putAttrib(RAKING,true);
        player.animate(2273);
        Chain.bound(player).repeatingTask(3, t -> {
            player.animate(2273);
            setTime();
            FarmingPatch grassyPatch = FarmingPatch.this;
            grassyPatch.stage = ((byte) (grassyPatch.stage + 1));
            player.getFarming().updateVarpFor(player);
            player.skills().addXp(Skills.FARMING,4, true);
            player.getInventory().addOrDrop(new Item(WEEDS,1));
            if (raked()) {
                player.message(Color.BLUE.wrap("Your patch is raked, no compost is required, just plant and water!"));
                player.resetAnimation();
                t.stop();
            }
        });
        player.clearAttrib(RAKING);
        player.resetAnimation();
    }

    public int getStage() {
        return stage;
    }
}

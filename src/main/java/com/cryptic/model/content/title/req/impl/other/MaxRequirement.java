package com.cryptic.model.content.title.req.impl.other;

import com.cryptic.model.content.items.equipment.max_cape.MaxCape;
import com.cryptic.model.content.title.req.TitleRequirement;
import com.cryptic.model.entity.player.Player;

/**
 * Created by Kaleem on 25/03/2018.
 */
public class MaxRequirement extends TitleRequirement {

    public MaxRequirement() {
        super("Reach level 99 in all stats<br>");
    }

    @Override
    public boolean satisfies(Player player) {
        return MaxCape.hasTotalLevel(player);
    }

}

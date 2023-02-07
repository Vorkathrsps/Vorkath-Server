package com.aelous.model.content.title.req.impl.pk;

import com.aelous.model.content.title.req.TitleRequirement;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;

/**
 * Created by Kaleem on 25/03/2018.
 */
public class KillstreakRequirement extends TitleRequirement {

    private final int killstreak;

    public KillstreakRequirement(int killstreak) {
        super(killstreak + " killstreak");
        this.killstreak = killstreak;
    }

    @Override
    public boolean satisfies(Player player) {
        int killstreak_record = player.getAttribOr(AttributeKey.KILLSTREAK_RECORD, 0);
        return killstreak_record >= killstreak;
    }

}

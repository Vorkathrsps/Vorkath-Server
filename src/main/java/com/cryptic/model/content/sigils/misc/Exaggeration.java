package com.cryptic.model.content.sigils.misc;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class Exaggeration extends AbstractSigil {
    @Override
    public void onRemove(Player player) {
        player.clearAttrib(AttributeKey.EXAGGERATION_BOOST);
        player.getSkills().resetStats();
    }

    @Override
    public void processMisc(Player player) {
        if (!attuned(player)) return;
        player.putAttrib(AttributeKey.EXAGGERATION_BOOST, true);
        for (int index = 0; index < 23; index++) {
            if (index > 6) player.getSkills().alterSkill(index, 5);
        }
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_EXAGGERATION);
    }

}

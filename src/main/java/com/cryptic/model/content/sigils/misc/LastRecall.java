package com.cryptic.model.content.sigils.misc;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class LastRecall extends AbstractSigil {
    @Override
    public void onRemove(Player player) {
        player.clearAttrib(AttributeKey.RECALL_ATTUNE_ACTIVE);
    }

    @Override
    public void processMisc(Player player) {
        if (!attuned(player)) return;
        player.putAttrib(AttributeKey.RECALL_ATTUNE_ACTIVE, true);
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_LAST_RECALL);
    }

}

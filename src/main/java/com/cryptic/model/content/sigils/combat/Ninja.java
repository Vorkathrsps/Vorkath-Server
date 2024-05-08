package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class Ninja extends AbstractSigil {
    @Override
    public void onRemove(Player player) {
        player.clearAttrib(AttributeKey.NINJA);
    }

    @Override
    public void processMisc(Player player) {
        if (!attuned(player)) return;
        player.putAttrib(AttributeKey.NINJA, true);
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_THE_NINJA);
    }

}

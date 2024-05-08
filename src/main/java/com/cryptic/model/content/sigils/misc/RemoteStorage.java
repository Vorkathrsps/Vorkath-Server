package com.cryptic.model.content.sigils.misc;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class RemoteStorage extends AbstractSigil {
    @Override
    public void onRemove(Player player) {
        player.clearAttrib(AttributeKey.REMOTE_STORAGE);
    }

    @Override
    public void processMisc(Player player) {
        if (!attuned(player)) return;
        player.putAttrib(AttributeKey.REMOTE_STORAGE, true);
    }


    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_REMOTE_STORAGE);
    }


}

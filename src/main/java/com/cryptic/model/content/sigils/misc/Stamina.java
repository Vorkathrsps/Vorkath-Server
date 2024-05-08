package com.cryptic.model.content.sigils.misc;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class Stamina extends AbstractSigil {
    @Override
    public void onRemove(Player player) {
        player.getPacketSender().sendStamina(false);
        player.clearAttrib(AttributeKey.STAMINA_POTION_TICKS);
    }

    @Override
    public void processMisc(Player player) {
        if (!attuned(player)) return;
        player.getPacketSender().sendStamina(true);
        player.putAttrib(AttributeKey.STAMINA_POTION_TICKS, Integer.MAX_VALUE);
    }


    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_STAMINA);
    }

}

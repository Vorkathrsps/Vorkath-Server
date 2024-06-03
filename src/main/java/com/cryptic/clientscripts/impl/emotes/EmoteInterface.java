package com.cryptic.clientscripts.impl.emotes;

import com.cryptic.GameServer;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import dev.openrune.cache.filestore.definition.data.EnumType;

public class EmoteInterface extends InterfaceBuilder {

    private final EnumType EMOTES_ENUM = GameServer.getCacheManager().getEnum(1000);

    @Override
    public GameInterface gameInterface() {
        return GameInterface.EMOTE_TAB;
    }


    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(2, 0, EMOTES_ENUM.getSize()));
        if (player.varps().getVarp(Emote.STAR_JUMP.getVarbit()) == 0) {
            for (Emote value : Emote.values()) {
                if (value.getVarbit() != -1 && value.getRequiredVarbitValue() != -1) {
                    player.varps().setVarbit(value.getVarbit(),value.getRequiredVarbitValue());
                } else if (value.getVarbit() != -1){
                    player.varps().setVarbit(value.getVarbit(),0);
                }
            }
        }
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.EMOTES_EMOTE_CONTAINER) {
            Emote.Companion.doEmote(player,slot);
        }
    }
}

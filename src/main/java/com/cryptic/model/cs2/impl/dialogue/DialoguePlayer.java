package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.args.PlayerArgs;

public class DialoguePlayer extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_PLAYER;
    }

    @Override
    public void beforeOpen(Player player) {
        var record = player.dialogueRecord.getType();
        if (record instanceof PlayerArgs dialogue) {
            player.varps().sendTempVarbit(10670, 0);
            dialogue.send(player);
            player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.PLAYER_CHAT_MESSAGE);
        }
    }
}

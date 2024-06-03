package com.cryptic.clientscripts.impl.dialogue.information.types.impl;

import com.cryptic.GameServer;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.impl.dialogue.information.types.DialogueType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.utility.Utils;

public record NpcType(int npcId, String title, String[] chats, Expression expression, boolean continueButtons) implements DialogueType {
    @Override
    public void send(Player player) {
        player.getPacketSender().setNpcHeadMessage(ComponentID.NPC_CHAT_HEAD, this.npcId);
        player.getPacketSender().setAnimMessage(ComponentID.NPC_CHAT_HEAD, this.expression.getAnimation().getId());
        String finalTitle = this.title.isEmpty() ? GameServer.getCacheManager().getNpc(npcId).getName() : this.title;
        player.getPacketSender().setComponentText(ComponentID.NPC_CHAT_TITLE, finalTitle);
        player.getPacketSender().setComponentText(ComponentID.NPC_CHAT_MESSAGE, Utils.joinWithBr(this.chats));
        player.getPacketSender().setComponentVisability(ComponentID.NPC_CHAT_CONTINUE, !this.continueButtons);
        player.getPacketSender().setComponentText(ComponentID.NPC_CHAT_CONTINUE, "Click here to continue");
        player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.NPC_CHAT_MESSAGE);
    }
}

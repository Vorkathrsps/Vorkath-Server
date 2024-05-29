package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.GameServer;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.utility.Utils;
import dev.openrune.cache.CacheManager;

public record NpcArgs(int npcId, String title, String[] chats, Expression expression, boolean continueButtons) implements Arguements {
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

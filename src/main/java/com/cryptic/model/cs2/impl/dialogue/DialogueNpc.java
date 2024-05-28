package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.args.NpcArgs;
import dev.openrune.cache.CacheManager;

public class DialogueNpc extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_NPC;
    }

    @Override
    public void beforeOpen(Player player) {
        var record = player.dialogueRecord.getType();
        if (record instanceof NpcArgs dialogueNPCRecord) {

            String dialogTitle = !dialogueNPCRecord.title().isBlank() ? dialogueNPCRecord.title() : CacheManager.INSTANCE.getNpc(dialogueNPCRecord.npcId()).getName();
            String message = joinWithBr(dialogueNPCRecord.chats());
            player.varps().sendTempVarbit(10670, 0);

            player.getPacketSender().setNpcHeadMessage(ComponentID.NPC_CHAT_HEAD, dialogueNPCRecord.npcId());
            player.getPacketSender().setAnimMessage(ComponentID.NPC_CHAT_HEAD, dialogueNPCRecord.expression().getAnimation().getId());

            player.getPacketSender().setComponentText(ComponentID.NPC_CHAT_TITLE, dialogTitle);
            player.getPacketSender().setComponentText(ComponentID.NPC_CHAT_MESSAGE, message);

            player.getPacketSender().setComponentVisability(ComponentID.NPC_CHAT_CONTINUE, !dialogueNPCRecord.continueButtons());
            player.getPacketSender().setComponentText(ComponentID.NPC_CHAT_CONTINUE, "Click here to continue");

            //int lineHeight = getLineHeight(message);
            player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.NPC_CHAT_MESSAGE);

        }
    }

    public static String joinWithBr(String... chats) {
        if (chats == null || chats.length == 0) {
            return "";
        }

        return String.join("<br>", chats);
    }


}

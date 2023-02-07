package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.mechanics.AntiSpam;
import com.aelous.model.content.mechanics.Censor;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.masks.impl.chat.ChatMessage;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.utility.Utils;
import com.aelous.utility.flood.Buffer;

/**
 * This packet listener manages the spoken text by a player.
 * 
 * @author Gabriel Hannason
 */
public class ChatMessagePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        //int junk = packet.readByte();//Edited no longer needs to be read.
        int size = packet.getSize() - 2;
        int color = packet.readByteS();
        int effect = packet.readByteS();
        byte[] text = packet.readReversedBytesA(
            size);
        String raw = Utils.textUnpack(text, size);
        String chatMessage = Utils.ucFirst(Utils.textUnpack(text, size).toLowerCase());

        if (chatMessage.length() <= 0) {
            return;
        }

        if (chatMessage.length() > 80) {
            chatMessage = chatMessage.substring(0, 79);
        }

        if (AntiSpam.isNewPlayerSpamming(player, chatMessage)) {
            return;
        }

        boolean newAccount = player.getAttribOr(AttributeKey.NEW_ACCOUNT, false);
        if (newAccount) {
            player.message("You can chat with friends after you have chosen your game mode.");
            return;
        }

        player.afkTimer.reset();
        
        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        if (chatMessage.toLowerCase().contains("img=") || chatMessage.toLowerCase().contains("col=")) {
            return;
        }

        if (player.muted()) {
            player.message("You are muted and cannot chat. Please try again later.");
            return;
        }

        String filtered = Censor.starred(raw);
        if (filtered != null)
            text = Utils.encode(filtered, Buffer.create());
        
        if (Utils.blockedWord(chatMessage)) {
            DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
            return;
        }

        if (player.getChatMessageQueue().size() >= 5) {
            player.message("Please do not spam.");
            return;
        }

        player.getChatMessageQueue().add(new ChatMessage(color, effect, text));
        Utils.sendDiscordInfoLog(player.getUsername() + ": " + chatMessage, "chat");
    }

}

package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.inter.clan.ClanManager;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.Color;

public class InputFieldPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) { // first byte is varsize -1 shouldnt get read here
        final int component = packet.readInt();
        final String context = packet.readString();

        if (component < 0) {
            return;
        }

        player.debugMessage("[InputField] - Text: " + context + " Component: " + component);

        switch (component) {
            case 47828 -> ClanManager.kickMember(player, context);
            case 47830 -> {
                if (World.getWorld().getPlayerByName(context).isPresent()) {
                    Player other = World.getWorld().getPlayerByName(context).get();
                    player.setClanPromote(other.getUsername());
                    player.message("You are now promoting " + Color.RED.wrap(other.getUsername()) + "</col>.");
                }
            }
            case 47843 -> ClanManager.changeSlogan(player, context);
            case 47845 -> {
                int amount = context.length() == 0 ? 0 : Integer.parseInt(context);
                ClanManager.setMemberLimit(player, amount);
            }
            case 81273 -> TradingPost.searchByUsername(player, context, true);
            case 81274 -> TradingPost.searchByItemName(player, context, true);
        }
    }

}

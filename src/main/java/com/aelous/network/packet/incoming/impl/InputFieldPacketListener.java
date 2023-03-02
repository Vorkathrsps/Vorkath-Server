package com.aelous.network.packet.incoming.impl;

import com.aelous.model.inter.clan.ClanManager;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.utility.Color;

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
            /* Clan Chat */
            case 47828:
                ClanManager.kickMember(player, context);
                break;
            case 47830:
                if (World.getWorld().getPlayerByName(context).isPresent()) {
                    Player other = World.getWorld().getPlayerByName(context).get();
                    player.setClanPromote(other.getUsername());
                    player.message("You are now promoting "+ Color.RED.wrap(other.getUsername())+"</col>.");
                }
                break;
            case 47843:
                ClanManager.changeSlogan(player, context);
                break;
            case 47845:
                int amount = context.length() == 0 ? 0 : Integer.parseInt(context);
                ClanManager.setMemberLimit(player, amount);
                break;
        }
    }

}

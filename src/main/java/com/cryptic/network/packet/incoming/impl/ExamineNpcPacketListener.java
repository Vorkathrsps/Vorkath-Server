package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.content.DropsDisplay;
import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPCCombatInfo;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExamineNpcPacketListener implements PacketListener {
    private static final Logger logger = LogManager.getLogger(ExamineNpcPacketListener.class);

    @Override
    public void handleMessage(Player player, Packet packet) {
        int npcId = packet.readShort();
        if (npcId <= 0) {
            return;
        }

        if (player == null || player.dead()) {
            return;
        }

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        NPCCombatInfo combatInfo = World.getWorld().combatInfo(npcId);
        NpcDefinition def = World.getWorld().definitions().get(NpcDefinition.class, npcId);

        if(!player.locked() && def != null && combatInfo != null && !combatInfo.unattackable) {
            DropsDisplay.start(player, def.name, npcId);
        }

        player.message(World.getWorld().examineRepository().npc(npcId));
    }
}

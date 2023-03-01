package com.aelous.network.packet.incoming.impl;

import com.aelous.model.content.items.RottenPotato;
import com.aelous.model.content.skill.impl.slayer.content.BagOfSalt;
import com.aelous.model.content.skill.impl.slayer.content.FungicideSpray;
import com.aelous.model.content.skill.impl.slayer.content.IceCooler;
import com.aelous.model.content.skill.impl.slayer.content.RockHammer;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.route.routes.TargetRoute;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 24, 2020
 */
public class ItemOnNpcPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        final int itemId = packet.readShortA();
        final int npcIdx = packet.readShortA();
        final int itemSlot = packet.readLEShort();
        final int fromInterfaceId = packet.readShortA();//interfaceitemSelectionTypeIn


        NPC npc = World.getWorld().getNpcs().get(npcIdx);
        if (npc == null) {
            return;
        }

        // priority code
        if(itemId == 5733 && RottenPotato.onItemOnMob(player, npc)) {
            return;
        }

        if ( player.locked() || player.dead()) {
            return;
        }

        player.stopActions(true);
        player.setEntityInteraction(npc);

        if (npc.dead()) {
            return;
        }

        Item item = player.inventory().get(itemSlot);
        if (item == null || item.getId() != itemId)
            return;

        // Store attribs
        player.putAttrib(AttributeKey.ITEM_SLOT, itemSlot);
        player.putAttrib(AttributeKey.INTERACTION_OPTION, -1); // secret key
        player.putAttrib(AttributeKey.ITEM_ID, itemId);
        player.putAttrib(AttributeKey.FROM_ITEM, item);
        player.putAttrib(AttributeKey.TARGET, new WeakReference<>(npc));

        //Do actions below
        TargetRoute.set(player, npc, () -> {
            player.setPositionToFace(npc.tile());

            if (PacketInteractionManager.checkItemOnNpcInteraction(player, item, npc)) {
                return;
            }

            if (RockHammer.onItemOnNpc(player, npc)) {
                return;
            }

            if(IceCooler.onItemOnNpc(player, npc)) {
                return;
            }

            if (BagOfSalt.onItemOnNpc(player, npc)) {
                return;
            }

            if (FungicideSpray.onItemOnNpc(player, npc)) {
                return;
            }

            player.message("Nothing interesting happens...");
        });
    }
}

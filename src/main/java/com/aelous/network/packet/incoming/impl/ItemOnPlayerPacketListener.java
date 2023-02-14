package com.aelous.network.packet.incoming.impl;

import com.aelous.model.content.items.RottenPotato;
import com.aelous.model.content.skill.impl.slayer.slayer_partner.SlayerPartner;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import com.aelous.utility.ItemIdentifiers;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 24, 2020
 */
public class ItemOnPlayerPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int interfaceId = packet.readUnsignedShortA();
        int playerIdx = packet.readUnsignedShort();
        int itemId = packet.readUnsignedShort();
        int slot = packet.readLEShort();

        if (player.locked()) {
            return;
        }

        player.debugMessage(String.format("itemId %d on player from %d pid %d inv-slot %d.", itemId, interfaceId, playerIdx, slot));

        Player other = World.getWorld().getPlayers().get(playerIdx);

        if (other == null) {
            player.message("Unable to find player.");
        } else {

            if(itemId == 5733 && RottenPotato.onItemOnMob(player, other)) {
                return;
            }

            player.stopActions(false);

            // Verify item
            Item item = player.inventory().get(slot);
            if (item == null || item.getId() != itemId) {
                return;
            }

            if (!player.locked() && !player.dead()) {
                player.setEntityInteraction(other);
                if (!other.dead()) {
                    player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(other));
                    player.putAttrib(AttributeKey.INTERACTION_OPTION, -1); // Special action
                    player.putAttrib(AttributeKey.ITEM_SLOT, slot);
                    player.putAttrib(AttributeKey.ITEM_ID, item.getId());
                    player.putAttrib(AttributeKey.FROM_ITEM, item);

                    //Do actions...

                    if (PacketInteractionManager.checkItemOnPlayerInteraction(player, item, other)) {
                        return;
                    }

                    if(item.getId() == ItemIdentifiers.ENCHANTED_GEM) {
                        SlayerPartner.invite(player, other, false);
                        return;
                    }
                }
            }
        }
    }
}

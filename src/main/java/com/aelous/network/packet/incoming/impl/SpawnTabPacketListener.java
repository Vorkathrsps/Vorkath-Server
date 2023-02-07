package com.aelous.network.packet.incoming.impl;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.spawn_tab.SpawnTab;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

/**
 * This packet listener reads a item spawn request
 * from the spawn tab.
 * @author Professor Oak
 */
public class SpawnTabPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        final int item = packet.readInt();
        final boolean spawnX = packet.readByte() == 1;
        final boolean toBank = packet.readByte() == 1;

        if (player == null || player.dead() || player.locked()) {
            return;
        }

        if (player.<Boolean>getAttribOr(AttributeKey.NEW_ACCOUNT,false)) {
            player.message("You have to select your game mode before you can continue.");
            return;
        }

        player.afkTimer.reset();

        ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, item);

        if (def == null) {
            player.message("This item is currently unavailable.");
            return;
        }
// i think we can just delete all in gameconstants pretty well
        //Check if player busy..
        if (player.busy()) {
            player.message("You cannot do that right now.");
            return;
        }

        if (!player.tile().homeRegion() && !player.getPlayerRights().isAdministrator(player)) {
            player.message("Spawning items is only allowed at home.");
            return;
        }

        boolean spawnable = def.pvpAllowed;

        if (!spawnable) {
            player.message("This item cannot be spawned.");
            return;
        }

        //Spawn item
        if (!spawnX) {
            SpawnTab.spawn(player, item, 1, toBank);
        } else {
            player.setAmountScript("How many "+def.name+" would you like to spawn?", new InputScript() {

                @Override
                public boolean handle(Object value) {
                    int amount = (Integer) value;
                    if (amount <= 0 || amount > Integer.MAX_VALUE) {
                        player.message("You can't spawn a negative amount or any more than "+Integer.MAX_VALUE+" of a item.");
                        return false;
                    }
                    SpawnTab.spawn(player, item, amount, toBank);
                    return true;
                }
            });
        }
    }
}

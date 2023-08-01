package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class AttackPlayerPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        player.afkTimer.reset();

        if (player.busy()) {
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

        player.stopActions(false);

        int index = packet.readLEShort();
        if (index > World.getWorld().getPlayers().capacity() || index < 0)
            return;

        if(player.locked() || player.dead()) {
            return;
        }

        final Player attacked = World.getWorld().getPlayers().get(index);

        if (attacked == null || attacked.dead() || attacked.equals(player)) {
            player.getMovementQueue().clear();
            return;
        }

        if (player.getEquipment().containsAny(ItemIdentifiers.TUMEKENS_SHADOW, ItemIdentifiers.TRIDENT_OF_THE_SEAS, ItemIdentifiers.TRIDENT_OF_THE_SEAS_E,
            ItemIdentifiers.TRIDENT_OF_THE_SEAS_FULL, ItemIdentifiers.TRIDENT_OF_THE_SWAMP, ItemIdentifiers.TRIDENT_OF_THE_SWAMP_E, ItemIdentifiers.SANGUINESTI_STAFF, ItemIdentifiers.HOLY_SANGUINESTI_STAFF)) {
            player.message(Color.RED.wrap("You cannot attack players with a " + player.getEquipment().getWeapon().name() + "."));
            return;
        }

        if (player.getEquipment().contains(ItemIdentifiers.DINHS_BULWARK) && player.getCombat().getFightType().getStyle() == FightStyle.DEFENSIVE) {
            player.message(Color.RED.wrap("Your bulwark gets in the way."));
            return;
        }

        player.putAttrib(AttributeKey.INTERACTION_OPTION, 2);
        player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(attacked));
        player.setEntityInteraction(attacked);
        player.getCombat().setCastSpell(null);
        player.getCombat().attack(attacked);

    }
}

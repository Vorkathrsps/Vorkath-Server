package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.combat.magic.spells.CombatSpells;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.Tuple;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 27, 2020
 */
public class MagicOnNpcPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int targetIndex = packet.readLEShortA();
        int spellId = packet.readShortA();

        if (targetIndex < 0 || spellId < 0 || targetIndex > World.getWorld().getNpcs().capacity()) {
            return;
        }

        if (player == null || player.dead()) {
            return;
        }

        player.afkTimer.reset();

        if (player.busy()) {
            return;
        }

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        NPC other = World.getWorld().getNpcs().get(targetIndex);
        if (other == null) {
            player.message("Unable to find npc.");
        } else {
            var def = NpcDefinition.cached.get(other.getId());
            if (def.isPet || !def.isInteractable || def.actions[1] == null) {
                player.stopActions(true);
                player.getCombat().reset();
                return;
            }
            if (!player.locked() && !player.dead()) {
                if (!other.dead()) {
                    player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(other));
                    player.putAttrib(AttributeKey.INTERACTION_OPTION, 2);

                    if (other.getCombatInfo() == null) {
                        player.message("Without combat attributes this monster is unattackable.");
                        return;
                    }

                    if (other.cantInteract()) {
                        return;
                    }

                    // See if it's exclusively owned
                    Tuple<Long, Player> ownerLink = other.getAttribOr(AttributeKey.OWNING_PLAYER, new Tuple<>(-1L, null));
                    if (ownerLink.first() != null && ownerLink.first() >= 0 && ownerLink.first() != player.getIndex()) {
                        player.message("They don't seem interested in fighting you.");
                        player.getCombat().reset();
                        return;
                    }

                    CombatSpell spellSelected = CombatSpells.getCombatSpell(spellId);

                    if (spellSelected == null) {
                        player.getMovementQueue().clear();
                        return;
                    }

                    player.setEntityInteraction(other);
                    player.getCombat().setCastSpell(spellSelected);
                    player.getCombat().attack(other);
                    other.getMovementQueue().setBlockMovement(false);
                }
            }
        }
    }
}

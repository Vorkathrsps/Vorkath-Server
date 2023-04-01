package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.magic.spells.MagicClickSpells;
import com.aelous.model.entity.combat.magic.spells.Spell;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.utility.Color;

import java.lang.ref.WeakReference;

public class MagicOnPlayerPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int targetIndex = packet.readShortA();
        int spellId = packet.readLEShort();
        
        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        if (player.locked()) {
            return;
        }

        Player other = World.getWorld().getPlayers().get(targetIndex);
        if (other == null) {
            player.message("Unable to find player.");
        } else {
            //player.stopActions(false);
            if (!player.locked() && !player.dead()) {
                if (!other.dead()) {
                    player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(other));
                    player.putAttrib(AttributeKey.INTERACTION_OPTION, 1);

                    //Do actions...
                    boolean newAccount = player.getAttribOr(AttributeKey.NEW_ACCOUNT, false);
                    if (newAccount) {
                        player.message("You have to select your game mode before you can continue.");
                        return;
                    }

                    CombatSpell spell = CombatSpells.getCombatSpell(spellId);

                    if (spell == null) {
                        //player.getMovementQueue().reset();
                        Spell clickSpell = MagicClickSpells.getMagicSpell(spellId);

                        if (clickSpell != null) {
                            MagicClickSpells.handleSpellOnPlayer(player, other, clickSpell);
                        }
                        return;
                    }
                    player.setEntityInteraction(other);
                    player.getCombat().setCastSpell(spell);
                    if (player.getCombat().getPoweredStaffSpell() != null) {
                        player.message(Color.RED.wrap("You cannot attack players in the wilderness with this weapon."));
                        return;
                    }
                    player.getCombat().attack(other);
                }
            }
        }
    }
}

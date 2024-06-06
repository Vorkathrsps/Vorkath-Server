package com.cryptic.clientscripts.impl.magic;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.Varbits;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.combat.magic.data.AncientSpells;
import com.cryptic.model.entity.combat.magic.data.ModernSpells;
import com.cryptic.model.entity.combat.magic.spells.CombatSpells;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Tuple;

public class MagicTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SPELLBOOK_TAB;
    }

    @Override
    public void beforeOpen(Player player) {
        player.varps().setVarp(Varbits.SPELLBOOK, 1);
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }

    @Override
    public void onTargetNpc(Player player, final int selectedButton, final int selectedSub, final int selectedItemId, NPC target) {
        if (player == null || player.locked() || player.dead() || player.busy()) {
            return;
        }

        if (target.dead()) {
            player.getCombat().reset();
            return;
        }

        player.afkTimer.reset();

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        var def = NpcDefinition.cached.get(target.getId());
        if (def.isPet || !def.isInteractable || def.actions[1] == null) {
            player.stopActions(true);
            player.getCombat().reset();
            return;
        }

        if (target.getCombatInfo() == null) {
            player.message("Without combat attributes this monster is unattackable.");
            return;
        }

        if (target.cantInteract()) {
            return;
        }

        Tuple<Long, Player> ownerLink = target.getAttribOr(AttributeKey.OWNING_PLAYER, new Tuple<>(-1L, null));
        if (ownerLink.first() != null && ownerLink.first() >= 0 && ownerLink.first() != player.getIndex()) {
            player.message("They don't seem interested in fighting you.");
            player.getCombat().reset();
            return;
        }

        int modernSpell = ModernSpells.MODERN_SPELL_COMPONENT_MAP.get(selectedButton);
        int ancientSpell = AncientSpells.ANCIENT_SPELL_COMPONENT_MAP.get(selectedButton);

        ModernSpells moderns = ModernSpells.findSpell(modernSpell);
        AncientSpells ancients = AncientSpells.findSpell(ancientSpell);

        CombatSpell selected = null;

        if (moderns != null) {
            selected = CombatSpells.getCombatSpell(moderns.spellID);
        }

        if (ancients != null) {
            selected = CombatSpells.getCombatSpell(ancients.spellID);
        }

        if (selected == null || player.getSpellbook() != selected.spellbook()) {
            player.getMovementQueue().clear();
            return;
        }

        player.getCombat().setCastSpell(selected);
        player.setEntityInteraction(target);
        player.getCombat().attack(target);
        target.getMovementQueue().setBlockMovement(false);
    }
}

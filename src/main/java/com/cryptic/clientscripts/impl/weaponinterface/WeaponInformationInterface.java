package com.cryptic.clientscripts.impl.weaponinterface;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.constants.InterfaceID;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.interfaces.Varps;
import com.cryptic.model.entity.player.Player;

public class WeaponInformationInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.COMBAT_TAB;
    }

    @Override
    public void beforeOpen(Player player) {
        //player.getPacketSender().runClientScriptNew(2498, 1, 0, 0);
        player.updateWeaponInfo();
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.COMBAT_STYLE_ONE || button == ComponentID.COMBAT_STYLE_TWO || button == ComponentID.COMBAT_STYLE_THREE || button == ComponentID.COMBAT_STYLE_FOUR) {
            //player.getPacketSender().runClientScriptNew(ScriptID.WEAPON_INFORMATION_COMBAT_LEVEL, player.skills().combatLevel());
            //player.varps().toggleVarp(Varps.ATTACK_STYLE);
            //player.varps().toggleVarp(Varps.LAST_ATTACK_STYLE);
        } else if (button == ComponentID.COMBAT_AUTO_RETALIATE) {
            player.varps().toggleVarp(172);
            final boolean autoRetaliate = player.varps().getVarp(172) == 0;
            player.getCombat().setAutoRetaliate(autoRetaliate);
        } else if (button == ComponentID.COMBAT_SPECIAL_ATTACK_BUTTON) {
            player.toggleSpecialAttack();
        }
    }
}

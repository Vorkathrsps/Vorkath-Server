package com.cryptic.clientscripts.impl.weaponinterface;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.InterfaceID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class WeaponInformationInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.COMBAT_TAB;
    }

    @Override
    public void beforeOpen(Player player) {
        updateWeaponInfo(player);
    }

    public static void updateWeaponInfo(Player player) {
        boolean hasWeapon = player.getEquipment().getWeapon() != null;
        String name = "Unarmed";
        int varbitValue = 0;
        String category = "Unarmed";
        if (hasWeapon) {
            ItemDefinition def = ItemDefinition.getInstance(player.getEquipment().getWeapon().getId());
            name = def.name;
            varbitValue = AttackStyleDefinition.getVarbit(def.category);
            category = AttackStyleDefinition.getName(def.category);
        }

        var combatLevel = String.valueOf(player.skills().combatLevel());
        player.getPacketSender().setComponentText(InterfaceID.COMBAT, 1, name);
        player.getPacketSender().setComponentText(InterfaceID.COMBAT, 2, "Category: " + category);
        player.getPacketSender().setComponentText(InterfaceID.COMBAT, 3, combatLevel);
        player.varps().setVarp(172, 0);
        player.varps().setVarbit(357, varbitValue);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.COMBAT_STYLE_ONE || button == ComponentID.COMBAT_STYLE_TWO || button == ComponentID.COMBAT_STYLE_THREE || button == ComponentID.COMBAT_STYLE_FOUR) {
            player.varps().toggleVarp(43);
            player.varps().toggleVarp(46);
        } else if (button == ComponentID.COMBAT_AUTO_RETALIATE) {
            player.varps().toggleVarp(172);
            final boolean autoRetaliate = player.varps().getVarp(172) == 0;
            player.getCombat().setAutoRetaliate(autoRetaliate);
        } else if (button == ComponentID.COMBAT_SPECIAL_ATTACK_BUTTON) {
            player.toggleSpecialAttack();
        }
    }
}

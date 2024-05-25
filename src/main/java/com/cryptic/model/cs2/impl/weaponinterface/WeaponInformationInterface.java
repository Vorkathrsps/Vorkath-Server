package com.cryptic.model.cs2.impl.weaponinterface;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
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
            ItemDefinition def = ItemDefinition.cached.get(player.getEquipment().getWeapon().getId());
            name = def.name;
            varbitValue = AttackStyleDefinition.getVarbit(def.category);
            category = AttackStyleDefinition.getName(def.category);
        }

        player.getPacketSender().setComponentText(593, 1, name);
        player.getPacketSender().setComponentText(593, 2, "Category: " + category);
        player.varps().setVarp(301, 1);
        player.varps().setVarp(172, 0);
        player.varps().setVarbit(357, varbitValue);

    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        var style = player.sessionVarps()[43];
        var config = player.sessionVarps()[46];
        if (button == ComponentID.COMBAT_STYLE_ONE) {
            player.varps().setVarp(43, 0);
            player.varps().setVarp(46, 0);
        }
        if (button == ComponentID.COMBAT_STYLE_TWO) {
            player.varps().setVarp(43, 1);
            player.varps().setVarp(46, 2);
        }
        if (button == ComponentID.COMBAT_STYLE_THREE) {
            player.varps().setVarp(43, 2);
            player.varps().setVarp(46, 3);
        }
        if (button == ComponentID.COMBAT_STYLE_FOUR) {
            player.varps().setVarp(43, 3);
            player.varps().setVarp(46, 3);
        }
        System.out.println(style);
        System.out.println(config);
        if (button == ComponentID.COMBAT_AUTO_RETALIATE) {
            player.varps().toggleVarp(172);
        }
    }
}

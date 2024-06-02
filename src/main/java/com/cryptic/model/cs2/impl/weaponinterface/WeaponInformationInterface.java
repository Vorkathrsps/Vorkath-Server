package com.cryptic.model.cs2.impl.weaponinterface;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.InterfaceID;
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

        player.getPacketSender().setComponentText(ComponentID.COMBAT_LEVEL, 0, String.valueOf(player.skills().combatLevel()));
        player.getPacketSender().setComponentText(InterfaceID.COMBAT, 1, name);
        player.getPacketSender().setComponentText(InterfaceID.COMBAT, 2, "Category: " + category);
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
        } else if (button == 38862885) { //TODO find component name & true id to add to our componentId generation
            player.toggleSpecialAttack();
        }
    }
}

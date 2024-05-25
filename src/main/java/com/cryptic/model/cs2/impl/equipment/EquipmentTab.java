package com.cryptic.model.cs2.impl.equipment;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.content.duel.DuelRule;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.utility.WidgetUtil;

import java.util.HashMap;
import java.util.Map;

public class EquipmentTab extends InterfaceBuilder {

    private static final Map<Integer, Integer> componentToEquipmentMap = createComponentToEquipmentMap();

    private static Map<Integer, Integer> createComponentToEquipmentMap() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(ComponentID.EQUIPMENT_HEAD, 0);
        map.put(ComponentID.EQUIPMENT_CAPE, 1);
        map.put(ComponentID.EQUIPMENT_AMULET, 2);
        map.put(ComponentID.EQUIPMENT_WEAPON, 3);
        map.put(ComponentID.EQUIPMENT_CHEST, 4);
        map.put(ComponentID.EQUIPMENT_SHIELD, 5);
        map.put(ComponentID.EQUIPMENT_LEGS, 7);
        map.put(ComponentID.EQUIPMENT_GLOVES, 9);
        map.put(ComponentID.EQUIPMENT_BOOTS, 10);
        map.put(ComponentID.EQUIPMENT_RING, 12);
        map.put(ComponentID.EQUIPMENT_AMMO, 13);
        return map;
    }


    @Override
    public GameInterface gameInterface() {
        return GameInterface.EQUIPMENT_TAB;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (componentToEquipmentMap.containsKey(button)) {
            player.getEquipment().unequip(componentToEquipmentMap.get(button));
        } else if (button == ComponentID.EQUIPMENT_STATS) {
            GameInterface.EQUIPMENT_STATS.open(player);
        } else if (button == ComponentID.EQUIPMENT_CALL) {
            player.message("Come Here boiii");
        }

    }
}

package com.cryptic.clientscripts.impl.equipment;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

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

        }

        if (button == ComponentID.EQUIPMENT_DEATH) {
            GameInterface.EQUIPMENT_KEPT_ON_DEATH.open(player);
        }

        if (button == ComponentID.EQUIPMENT_PRICE) {
            GameInterface.GUIDE_PRICE.open(player);
        }

    }
}

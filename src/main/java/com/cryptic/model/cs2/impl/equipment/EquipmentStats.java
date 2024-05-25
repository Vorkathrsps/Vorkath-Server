package com.cryptic.model.cs2.impl.equipment;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.World;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;

import java.util.HashMap;
import java.util.Map;

public class EquipmentStats extends InterfaceBuilder {

    private static final Map<Integer, Integer> componentToEquipmentMap = createComponentToEquipmentMap();

    private static Map<Integer, Integer> createComponentToEquipmentMap() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(ComponentID.EQUIPMENT_STATS_HEAD, 0);
        map.put(ComponentID.EQUIPMENT_STATS_CAPE, 1);
        map.put(ComponentID.EQUIPMENT_STATS_AMULET, 2);
        map.put(ComponentID.EQUIPMENT_STATS_WEAPON, 3);
        map.put(ComponentID.EQUIPMENT_STATS_CHEST, 4);
        map.put(ComponentID.EQUIPMENT_STATS_SHIELD, 5);
        map.put(ComponentID.EQUIPMENT_STATS_LEGS, 7);
        map.put(ComponentID.EQUIPMENT_STATS_GLOVES, 9);
        map.put(ComponentID.EQUIPMENT_STATS_BOOTS, 10);
        map.put(ComponentID.EQUIPMENT_STATS_RING, 12);
        map.put(ComponentID.EQUIPMENT_STATS_AMMO, 13);
        return map;
    }


    @Override
    public void beforeOpen(Player player) {
        player.interfaces.setInterfaceUnderlay(-1, -1);
        GameInterface.EQUIPMENT_INVENTORY.open(player);
        player.getPacketSender().runClientScriptNew(149, 5570560, 93, 4, 7, 1, -1, "Equip", "", "", "", "");
        player.bonusInterface.sendBonuses();
        player.getPacketSender().runClientScriptNew(7065, 5505075, 5505064, "Increases your effective accuracy and damage against undead creatures. For multi-target Ranged and Magic attacks, this applies only to the primary target. It does not stack with the Slayer multiplier.");
    }

    @Override
    public GameInterface gameInterface() {
        return GameInterface.EQUIPMENT_STATS;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (componentToEquipmentMap.containsKey(button)) {
            int slotID = componentToEquipmentMap.get(button);
            player.getEquipment().unequip(slotID);
        }
    }
}

package com.cryptic.clientscripts.impl.equipment;

import com.cryptic.clientscripts.impl.equipment.util.ToggleManager;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.content.mechanics.death.DeathResult;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.container.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class KeptOnDeathInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.EQUIPMENT_KEPT_ON_DEATH;
    }

    @Override
    public void beforeOpen(Player player) {
        EventNode node = new EventNode(12, 0, 3);
        node.setContinue();
        setEvents(node);
        ToggleManager manager = player.getToggleManager();
        player.getPacketSender().runClientScriptNew(468, 0, 0, 0, 0, 0);
        player.getPacketSender().runClientScriptNew(972, manager.args[0], manager.args[1], manager.args[2], manager.args[3], "");
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }

    @Override
    public void onResumePause(Player player, int slot) {
        ToggleManager manager = player.getToggleManager();
        manager.toggle(slot);

        final boolean isSkulled = manager.args[0] == 1;
        final boolean isProtectItem = manager.args[1] == 1;
        final Inventory inventory = player.getInventory();
        final Equipment equipment = player.getEquipment();
        DeathResult deathResult = DeathResult.create(player, isProtectItem, isSkulled, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        deathResult.populateScenario(inventory.toArray()).populateScenario(equipment.toArray()).sortValue();
        List<Item> items = deathResult.itemList;
        int[] args = new int[items.size()];
        int[] argsToSend = new int[Math.max(args.length, 4)];
        if (!items.isEmpty()) {
            for (int i = 0; i < args.length; i++) {
                args[i] = items.get(i).getId();
            }
            for (int i = 0; i < 4; i++) {
                argsToSend[i] = i < args.length ? args[i] : -1;
            }
        }
        player.getPacketSender().runClientScriptNew(972, manager.args[1], manager.args[0], manager.args[3], manager.args[2], "", argsToSend.length, argsToSend[0], argsToSend[1], argsToSend[2], argsToSend[3], argsToSend[4], -1);
    }
}

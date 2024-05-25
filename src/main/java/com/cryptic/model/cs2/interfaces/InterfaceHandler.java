package com.cryptic.model.cs2.interfaces;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.InterfaceType;
import com.cryptic.model.cs2.InterfaceID;
import com.cryptic.model.cs2.impl.*;
import com.cryptic.model.cs2.impl.dialogue.*;
import com.cryptic.model.cs2.impl.emotes.EmoteInterface;
import com.cryptic.model.cs2.impl.equipment.EquipmentInventory;
import com.cryptic.model.cs2.impl.equipment.EquipmentStats;
import com.cryptic.model.cs2.impl.equipment.EquipmentTab;
import com.cryptic.model.cs2.impl.weaponinterface.WeaponInformationInterface;
import com.cryptic.model.cs2.impl.inventory.InventoryInterface;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.DialogueSingleItemRecord;

import java.util.HashMap;
import java.util.Map;

public class InterfaceHandler {
    public static final Map<Integer, InterfaceBuilder> interfaces = new HashMap<>();
    public final Map<Integer, InterfaceBuilder> visible = new HashMap<>();

    static {
        interfaces.put(InterfaceID.EMOTES, new EmoteInterface());
        interfaces.put(InterfaceID.LOGOUT_PANEL, new LogoutTab());
        interfaces.put(InterfaceID.SPELLBOOK, new MagicTab());
        interfaces.put(InterfaceID.PRAYER, new PrayerTab());
        interfaces.put(InterfaceID.FIXED_VIEWPORT, new ViewportFixed());
        interfaces.put(InterfaceID.COMBAT,new WeaponInformationInterface());
        interfaces.put(InterfaceID.INVENTORY, new InventoryInterface());
        interfaces.put(InterfaceID.EQUIPMENT, new EquipmentTab());
        interfaces.put(InterfaceID.EQUIPMENT_STATS, new EquipmentStats());
        interfaces.put(InterfaceID.EQUIPMENT_INVENTORY, new EquipmentInventory());
        interfaces.put(InterfaceID.DIALOG_NPC, new DialogueNpc());
        interfaces.put(InterfaceID.DIALOG_OPTION, new DialogueOptions());
        interfaces.put(InterfaceID.DIALOG_PLAYER, new DialoguePlayer());
        interfaces.put(InterfaceID.DIALOG_MESSAGE_BOX, new DialogueStatement());
        interfaces.put(InterfaceID.DIALOG_SPRITE, new DialogueItemSingle());
        interfaces.put(InterfaceID.DIALOG_DOUBLE_SPRITE, new DialogueItemDouble());
    }

    public static InterfaceBuilder find(int interfaceId) {
        return interfaces.getOrDefault(interfaceId, null);
    }

    public static void closeModals(Player player) {
        for (InterfaceBuilder inter : player.activeInterface.values()) {
            GameInterface gameInterface = inter.gameInterface();
            if (gameInterface.getPosition().getType() == InterfaceType.MODAL) {
                gameInterface.close(player);
                inter.close(player);
            }
        }
    }
}

package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.InterfaceID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;
import com.cryptic.utility.WidgetUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class If1ButtonPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int option = packet.readUnsignedByte();
        CombinedComponent combinedId = new CombinedComponent(packet.readInt());
        int shortSlot = packet.readUnsignedShort();
        int shortItem = packet.readUnsignedShort();

        if (shortSlot == 65535) {
            shortSlot = -1;
        }

        if (shortItem == 65535) {
            shortItem = -1;
        }

        int interfaceId = combinedId.getInterfaceId();
        int component = combinedId.getComponentId();
        int packed = WidgetUtil.packComponentId(interfaceId, component);

        log.info("Click button: component=[{}:{}:{}], option={}, slot={}, item={}, packed={}", findInterfaceFieldName(interfaceId), component, findComponentFieldName(packed), option, shortSlot, shortItem,packed);
        if (player.activeInterface.containsKey(interfaceId)) {
            var active = player.activeInterface.get(interfaceId);
            active.onButton(player,packed, option, shortSlot, shortItem);
        }
    }

    public static String findComponentFieldName(int packedValue) {
        Class<?> widgetUtilClass = ComponentID.class;
        Field[] fields = widgetUtilClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == int.class) {
                try {
                    int fieldValue = field.getInt(null);
                    if (fieldValue == packedValue) {
                        return field.getName();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return String.valueOf(packedValue);
    }

    public static String findInterfaceFieldName(int packedValue) {
        Class<?> widgetUtilClass = InterfaceID.class; // Assuming WidgetUtil is the class containing these fields

        Field[] fields = widgetUtilClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == int.class) {
                try {
                    int fieldValue = field.getInt(null); // Assuming the fields are static
                    if (fieldValue == packedValue) {
                        return field.getName();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return String.valueOf(packedValue);
    }


}

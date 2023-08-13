package com.cryptic.model.content.presets;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @Author: Origin
 * @Date: 8/13/2023
 */
public class PresetHandler extends PacketInteraction {

    public ItemContainer container;
    final int INVENTORY_SIZE = 28;
    PresetKits[] kits = PresetKits.values();
    int[] preMadeKitButtons = new int[]{73271, 73272, 73273, 73275, 73277, 73279, 73281, 73283, 73285};
    int[] createKitStrings = new int[]{73272, 73274, 73276, 73278, 73280, 73282, 73284, 73286};
    int[] createKitButtons = new int[]{73296, 73297, 73298, 73299, 73300, 73301, 73302, 73303};
    int[] equipmentChildIds = new int[]{73251, 73252, 73253, 73254, 73255, 73256, 73257, 73258, 73259, 73260, 73261};
    String[] presetNames = {
        "Main - Melee", "Zerker - Melee", "Pure - Melee",
        "Main - Tribrid", "Zerker - Tribrid", "Pure - Tribrid",
        "Main - Hybrid", "Zerker - Hybrid"
    };
    AttributeKey[] attributeKeys = new AttributeKey[]{AttributeKey.PURE_MELEE_PRESET, AttributeKey.MAIN_MELEE_PRESET, AttributeKey.ZERKER_MELEE_PRESET};

    /**
     * Handles Packet Interaction
     *
     * @param player the player
     * @param button the button
     * @return true / false
     */
    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (ArrayUtils.contains(preMadeKitButtons, button)) {
            findMatchingButtonIdentificationFor(button).ifPresent(p -> {
                clearAttributesExcept(player, p.getAttributeKey());
                player.putAttrib(p.getAttributeKey(), true);
                rebuildInterface(player, p);
            });
            return true;
        }

        if (button == 73235) {
            findMatchingAttributeFor(player).ifPresent(p -> {
                applyExperience(player, p);
                applyEquipment(player, p);
                applyInventory(player, p);
                updatePlayer(player);
            });
            return true;
        }

        return false;
    }

    /**
     * Clear the attributes that we're not currently matching with and apply / keep the correct one
     *
     * @param player
     * @param attributeKeyToKeep
     */
    private void clearAttributesExcept(Player player, AttributeKey attributeKeyToKeep) {
        Arrays.stream(attributeKeys)
            .filter(a -> player.hasAttrib(a) && !a.equals(attributeKeyToKeep))
            .forEach(player::clearAttrib);
    }

    /**
     * Identify which unique button is tied to the button we're interacting with
     *
     * @param button
     * @return
     */
    Optional<PresetKits> findMatchingButtonIdentificationFor(int button) {
        return Arrays.stream(kits).filter(f -> f.buttonIdentification == button).findAny();
    }

    /**
     * Identify which preset we're currently on
     *
     * @param player
     * @return
     */
    Optional<PresetKits> findMatchingAttributeFor(Player player) {
        return Arrays.stream(kits).filter(f -> player.hasAttrib(f.getAttributeKey())).findFirst();
    }

    /**
     * Rebuilds the interface
     *
     * @param player
     */
    void rebuildInterface(Player player, PresetKits presetKits) {
        if (presetKits != null) {
            if (!WildernessArea.isInWilderness(player)) {
                resetContainers(player);
                sendPreMadePresetStrings(player);
                sendInventory(player, presetKits);
                sendEquipment(player, presetKits);
                sendSpellbook(player, presetKits);
                sendPrayers(player);
            } else {
                player.message(Color.RED.wrap("You cannot perform this action while in the wilderness."));
            }
        }
    }

    /**
     * Clears The Item Containers
     */
    void resetContainers(Player player) {
        for (int index = 0; index < 11; index++) {
            int id = 73251;
            player.getPacketSender().sendItemOnInterfaceSlot(id + index, -1, 0, 0);
        }
        if (container != null) {
            container.clear();
        }
    }

    /**
     * Sends the pre-made kit strings
     *
     * @param player
     */
    void sendPreMadePresetStrings(Player player) {
        for (int i = 0; i < presetNames.length; i++) {
            int id = 73271;
            player.getPacketSender().sendString(id + i, presetNames[i]);
        }
    }

    /**
     * Sends the inventory item container
     *
     * @param player
     */
    void sendInventory(Player player, PresetKits presetKits) {
        container = new ItemContainer(INVENTORY_SIZE, ItemContainer.StackPolicy.STANDARD);
        container.addAll(presetKits.getInventoryItemList());
        player.getPacketSender().sendItemOnInterface(73239, container.toArray());
    }

    /**
     * Sends the equipment item container
     *
     * @param player
     */
    void sendEquipment(Player player, PresetKits presetKits) {
        container = new ItemContainer(INVENTORY_SIZE, ItemContainer.StackPolicy.STANDARD);
        container.addAll(presetKits.getEquipmentItemList());
        for (int i = 0; i < 11; i++) {
            int id = 73251;
            player.getPacketSender().sendItemOnInterfaceSlot(id + i, container.get(i).getId(), 1, 0);
        }
    }

    /**
     * Sends The Spellbook String
     *
     * @param player
     */
    void sendSpellbook(Player player, PresetKits presetKits) {
        player.getPacketSender().sendString(73237, presetKits.getSpellbook());
    }

    /**
     * Sends the prayers string
     *
     * @param player
     */
    void sendPrayers(Player player) {
        player.getPacketSender().sendString(73238, "Regular");
    }

    /**
     * Applys Experience To Designated Skills
     *
     * @param player
     */
    void applyExperience(Player player, PresetKits presetKits) {
        int[] changeLevelsTo = presetKits.getAlterLevels();
        if (!WildernessArea.isInWilderness(player)) {
            IntStream.range(0, presetKits.getCurrentLevels().length).forEachOrdered(i -> player.skills().alterSkillsArray(player, presetKits.getCurrentLevels()[i], changeLevelsTo[i]));
        } else {
            player.message(Color.RED.wrap("You cannot perform this action while in the wilderness."));
        }
    }

    /**
     * Apply the Preset Equipment
     *
     * @param player
     * @param presetKits
     */
    void applyEquipment(Player player, PresetKits presetKits) {
        if (!WildernessArea.isInWilderness(player)) {
            if (player.getEquipment() != null && !player.getEquipment().hasNoEquipment()) {
                player.getBank().depositEquipment();
            }
            presetKits.getEquipmentItemList().forEach(item -> player.getEquipment().manualWear(item, true, false));
        }
    }

    /**
     * Apply the preset inventory
     * @param player
     * @param presetKits
     */
    void applyInventory(Player player, PresetKits presetKits) {
        boolean inventoryCheck = player.getInventory().getFreeSlots() > -1;
        if (!WildernessArea.isInWilderness(player)) {
            if (inventoryCheck) {
                player.getBank().depositInventory();
            }
            Arrays.stream(presetKits.getInventoryItemList()).forEach(i -> player.getInventory().add(i));
        }
    }

    /**
     * Update Our Players Attributes
     *
     * @param player
     */
    void updatePlayer(Player player) {
        ItemWeight.calculateWeight(player);
        player.getEquipment().refresh();
        player.inventory().refresh();
        player.heal();
        player.looks().update();
    }

}

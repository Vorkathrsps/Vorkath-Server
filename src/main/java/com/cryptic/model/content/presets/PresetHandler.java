package com.cryptic.model.content.presets;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.timers.TimerKey;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Author: Origin
 * @Date: 8/13/2023
 */
@SuppressWarnings("unused")
public class PresetHandler extends PacketInteraction {

    public ItemContainer container;
    private static final int PRESET_BUTTON_ID = 73235;
    private static final int EDIT_BUTTON_ID = 73234;
    private static final int SPELLBOOK_STRING_ID = 73237;
    private static final int EQUIPMENT_CONTAINER_ID = 73251;
    private static final int INVENTORY_CONTAINER_ID = 73239;
    private static final int PRE_MADE_PRESET_NAME_STRINGS = 73271;
    private static final int PRAYER_STRING_ID = 73238;
    private static final int INVENTORY_SIZE = 28;
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
    PresetKits[] kits = PresetKits.values();
    boolean canEdit = true;

    /**
     * Handles the interaction when a button is clicked.
     *
     * @param player the player
     * @param button the button
     * @return true if interaction is handled, false otherwise
     */
    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (player.getTimers().has(TimerKey.ANTI_SPAM)) {
            return false;
        }

        player.getTimers().register(TimerKey.ANTI_SPAM, 3);

        var isPreMadeKit = ArrayUtils.contains(preMadeKitButtons, button);

        if (isPreMadeKit) {
            handleButtonValidation(player, button);
            return true;
        } else if (button == PRESET_BUTTON_ID) {
            handlePresetFunction(player);
            return true;
        } else if (button == EDIT_BUTTON_ID) {
            for (var a : attributeKeys) {
                if (player.hasAttrib(a)) {
                    player.message(Color.RED.wrap("You cannot edit a pre-made preset."));
                    canEdit = false;
                    break;
                }
            }
            return canEdit;
        }

        return false;
    }

    /**
     * Validates the clicked button and applies the corresponding action.
     *
     * @param player the player
     * @param button the button
     */
    void handleButtonValidation(Player player, int button) {
        findMatchingButtonIdentificationFor(button).ifPresent(p -> {
            clearAttributesExcept(player, p.getAttributeKey());
            player.putAttrib(p.getAttributeKey(), true);
            rebuildInterface(player, p);
        });
    }

    /**
     * Handles the action when a preset button is clicked.
     *
     * @param player the player
     */
    void handlePresetFunction(Player player) {
        findMatchingAttributeFor(player).ifPresent(presetKits -> applyPreset(player, presetKits));
    }

    /**
     * Applies the selected preset to the player's attributes and inventory.
     *
     * @param player     the player
     * @param presetKits the selected preset
     */
    void applyPreset(Player player, PresetKits presetKits) {
        applyExperience(player, presetKits);
        applyEquipment(player, presetKits);
        applyInventory(player, presetKits);
        applySpellBook(player, presetKits);
        updatePlayer(player);
    }

    /**
     * Clear the attributes that we're not currently matching with and apply / keep the correct one
     *
     * @param player the player
     * @param attributeKeyToKeep the attributekey
     */
    void clearAttributesExcept(Player player, AttributeKey attributeKeyToKeep) {
        Arrays.stream(attributeKeys)
            .filter(a -> player.hasAttrib(a) && !a.equals(attributeKeyToKeep))
            .forEach(player::clearAttrib);
    }

    /**
     * Identify which unique button is tied to the button we're interacting with
     *
     * @param button the button
     * @return optional
     */
    Optional<PresetKits> findMatchingButtonIdentificationFor(int button) {
        return Arrays.stream(kits).filter(f -> f.buttonIdentification == button).findAny();
    }

    /**
     * Identify which preset we're currently on
     *
     * @param player the player
     * @return optional
     */
    Optional<PresetKits> findMatchingAttributeFor(Player player) {
        return Arrays.stream(kits).filter(f -> player.hasAttrib(f.getAttributeKey())).findFirst();
    }

    /**
     * Rebuilds the interface
     *
     * @param player the player
     */
    void rebuildInterface(Player player, PresetKits presetKits) {
        if (presetKits != null) {
            if (!WildernessArea.isInWilderness(player)) {
                resetContainers(player);
                sendPreMadePresetStrings(player);
                sendInventoryContainer(player, presetKits);
                sendEquipmentContainer(player, presetKits);
                sendSpellbookString(player, presetKits);
                sendPrayerString(player);
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
            sendItemsToInterface(player, EQUIPMENT_CONTAINER_ID, -1, index,  1);
        }
        if (container != null) {
            container.clear();
        }
    }

    /**
     * Sends the pre-made kit strings
     *
     * @param player the player
     */
    void sendPreMadePresetStrings(Player player) {
        for (int index = 0; index < presetNames.length; index++) {
            player.getPacketSender().sendString(PRE_MADE_PRESET_NAME_STRINGS + index, presetNames[index]);
        }
    }

    /**
     * Sends the inventory item container
     *
     * @param player the player
     */
    void sendInventoryContainer(Player player, PresetKits presetKits) {
        container = new ItemContainer(INVENTORY_SIZE, ItemContainer.StackPolicy.STANDARD);
        container.addAll(presetKits.getInventoryItemList());
        player.getPacketSender().sendItemOnInterface(INVENTORY_CONTAINER_ID, container.toArray());
    }

    /**
     * Sends the equipment item container
     *
     * @param player the player
     */
    void sendEquipmentContainer(Player player, PresetKits presetKits) {
        container = new ItemContainer(INVENTORY_SIZE, ItemContainer.StackPolicy.STANDARD);
        container.addAll(presetKits.getEquipmentItemList());
        for (int index = 0; index < 11; index++) {
            sendItemsToInterface(player, EQUIPMENT_CONTAINER_ID, container.get(index).getId(), index, container.get(index).getAmount());
        }
    }

    /**
     * Sends items to an interface
     * @param player the player
     * @param id the item id
     * @param item the item instance
     * @param index the item index
     * @param amount the item amount
     */
    void sendItemsToInterface(Player player, int id, int item, int index, int amount) {
        player.getPacketSender().sendItemOnInterfaceSlot(id + index, item, amount, 0);
    }

    /**
     * Sends The Spellbook String
     *
     * @param player the player
     */
    void sendSpellbookString(Player player, PresetKits presetKits) {
        player.getPacketSender().sendString(SPELLBOOK_STRING_ID, presetKits.getSpellbook().name().toLowerCase());
    }

    /**
     * Sends the prayers string
     *
     * @param player the player
     */
    void sendPrayerString(Player player) {
        player.getPacketSender().sendString(PRAYER_STRING_ID, "Regular");
    }

    /**
     * Applys Experience To Designated Skills
     *
     * @param player the player
     */
    void applyExperience(Player player, PresetKits presetKits) {
        int[] changeLevelsTo = presetKits.getAlterLevels();
        if (!WildernessArea.isInWilderness(player)) {
            checkInclusivesFor(presetKits).forEachOrdered(i -> player.skills().alterSkillsArray(player, presetKits.getCurrentLevels()[i], changeLevelsTo[i]));
        } else {
            player.message(Color.RED.wrap("You cannot perform this action while in the wilderness."));
        }
    }

    /**
     * a sequential IntStream for the range of int elements
     *
     * @param presetKits the presetkit instance
     * @return inclusive
     */
    IntStream checkInclusivesFor(PresetKits presetKits) {
        return IntStream.range(0, presetKits.getCurrentLevels().length);
    }

    /**
     * Apply the Preset Equipment
     *
     * @param player the player
     * @param presetKits the presetkit instance
     */
    void applyEquipment(Player player, PresetKits presetKits) {
        if (!WildernessArea.isInWilderness(player)) {
            if (player.getEquipment() != null && !player.getEquipment().hasNoEquipment()) {
                player.getBank().depositEquipment();
            }
            getEquipmentItemsOf(presetKits).forEach(item -> {
                if (bankDoesntContain(player, item)) {
                    player.message(item.getAmount() == 0 ? "Item not found: " + Color.RED.wrap("" + item.name()) : "Item not found: " + Color.RED.wrap("" + item.name()) + " Amount: " + Color.RED.wrap("x" + item.getAmount()));
                } else {
                    removeFromBank(player, item);
                    player.getEquipment().manualWear(item, true, false);
                }
            });
        }
    }

    /**
     * Method to change our spellbook
     *
     * @param player the player
     * @param presetKits the presetkit instance
     */
    void applySpellBook(Player player, PresetKits presetKits) {
        if (!WildernessArea.isInWilderness(player)) {
            sendSpellbookString(player, presetKits);
            MagicSpellbook.changeSpellbook(player, presetKits.getSpellbook(), true);
        }
    }

    /**
     * Apply the preset inventory
     *
     * @param player the player
     * @param presetKits the presetkit instance
     */
    void applyInventory(Player player, PresetKits presetKits) {
        boolean inventoryCheck = player.getInventory().getFreeSlots() > -1;
        if (!WildernessArea.isInWilderness(player)) {
            if (inventoryCheck) {
                player.getBank().depositInventory();
            }
            getInventoryItemsOf(presetKits).forEach(item -> {
                if (bankDoesntContain(player, item)) {
                    player.message(item.getAmount() == 0 ? "Item not found: " + Color.RED.wrap("" + item.name()) : "Item not found: " + Color.RED.wrap("" + item.name()) + " Amount: " + Color.RED.wrap("x" + item.getAmount()));
                } else {
                    removeFromBank(player, item);
                    player.getInventory().add(item);
                }
            });
        }
    }

    /**
     * Remove item from the players bank
     *
     * @param player the player
     * @param item   the item
     */
    void removeFromBank(Player player, Item item) {
        player.getBank().remove(new Item(item, item.getAmount()));
    }

    /**
     * Returns true/false if player's bank contains an item
     *
     * @param player the player
     * @param item the item
     * @return true/false
     */
    boolean bankDoesntContain(Player player, Item item) {
        return !player.getBank().contains(item);
    }

    /**
     * Returns presets equipment list
     *
     * @param presetKits the presetkit instance
     * @return equipment items
     */
    List<Item> getEquipmentItemsOf(PresetKits presetKits) {
        return presetKits.getEquipmentItemList();
    }

    /**
     * Streams inventory list
     *
     * @param presetKits the presetkits instance
     * @return inventory items
     */
    Stream<Item> getInventoryItemsOf(PresetKits presetKits) {
        return Arrays.stream(presetKits.getInventoryItemList());
    }

    /**
     * Updates the player's attributes and equipment after applying the preset.
     *
     * @param player the player
     */
    void updatePlayer(Player player) {
        ItemWeight.calculateWeight(player);
        player.getInterfaceManager().setSidebar(6, player.getSpellbook().getInterfaceId());
        player.getEquipment().refresh();
        player.inventory().refresh();
        player.heal();
        player.looks().update();
        player.synchronousSave();
    }

}

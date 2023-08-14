package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.timers.TimerKey;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @Author: Origin
 * @Date: 8/13/2023
 */
@SuppressWarnings("unused")
public class PresetHandler extends PacketInteraction { //TODO add region array for wildy checks
    //TODO add support for degrading items to replace the item in the container with the degradable if they have that available in their bank / inventory

    public ItemContainer container;
    ItemContainer equipmentContainer = new ItemContainer(EQUIPMENT_SIZE, ItemContainer.StackPolicy.STANDARD);
    ItemContainer inventoryContainer = new ItemContainer(INVENTORY_SIZE, ItemContainer.StackPolicy.STANDARD);
    private static final int PRESET_BUTTON_ID = 73235;
    private static final int EDIT_BUTTON_ID = 73234;
    private static final int SPELLBOOK_STRING_ID = 73237;
    private static final int EQUIPMENT_CONTAINER_ID = 73251;
    private static final int INVENTORY_CONTAINER_ID = 73239;
    private static final int PRE_MADE_PRESET_NAME_STRINGS = 73271;
    private static final int PRAYER_STRING_ID = 73238;
    private static final int INVENTORY_SIZE = 28;
    private static final int EQUIPMENT_SIZE = 11;
    int[] preMadeKitButtons = new int[]{73274, 73275, 73276, 73277, 73277, 73279, 73281, 73283, 73285};
    int[] createKitStrings = new int[]{73291, 73274, 73276, 73278, 73280, 73282, 73284, 73286};
    int[] createKitButtons = new int[]{73296, 73297, 73298, 73299, 73300, 73301, 73302, 73303};
    int[] equipmentChildIds = new int[]{73251, 73252, 73253, 73254, 73255, 73256, 73257, 73258, 73259, 73260, 73261};
    String[] presetNames = {"Main - Melee", "Zerker - Melee", "Pure - Melee", "Main - Tribrid", "Zerker - Tribrid", "Pure - Tribrid", "Main - Hybrid", "Zerker - Hybrid"};
    AttributeKey[] attributeKeys = new AttributeKey[]{AttributeKey.PURE_MELEE_PRESET, AttributeKey.MAIN_MELEE_PRESET, AttributeKey.ZERKER_MELEE_PRESET};
    DefaultKits[] defaultKits = DefaultKits.values();
    SavedKits[] savedKits = SavedKits.values();
    CreatePreset create;
    boolean canEdit = true;
    List<Skill> savedPresetSkills = new ArrayList<>(24);
    HashMap<Integer, Integer> itemAmounts = new HashMap<>();

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

        if (!player.getInterfaceManager().isInterfaceOpen(73230)) {
            return false;
        }

        player.getTimers().register(TimerKey.ANTI_SPAM, 3);

        var isPreMadeKit = ArrayUtils.contains(preMadeKitButtons, button);

        if (button == 73274 || button == 73291) {
            player.message("is pre-made");
            clearInterfaceAndContainers(player);
            validateAndBuildPreset(player, button);
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
     * Loads the container submissions
     * @param player the player
     * @param equipmentContainer the equipment container
     * @param inventoryContainer the inventory container
     * @param kits the preset kit
     */
    void load(Player player, ItemContainer equipmentContainer, ItemContainer inventoryContainer, Kit kits) {
        submitEquipment(player, equipmentContainer, kits);
        submitItems(player, inventoryContainer, kits);
    }

    /**
     * Handles the equipment container
     * @param player the player
     * @param equipmentContainer the equipment container
     * @param kit the preset kit
     */
    void handleEquipmentContainer(Player player, ItemContainer equipmentContainer, Kit kit) {
        if (equipmentContainer == null) {
            return;
        }

        var size = (kit instanceof SavedKits) ? EQUIPMENT_SIZE : 14;
        for (int index = 0; index < EQUIPMENT_SIZE; index++) {
            Item item = equipmentContainer.get(index);
            if (item != null) {
                int slot = World.getWorld().equipmentInfo().slotFor(item.getId());
                int amount = item.getAmount();
                int interfaceID = (kit instanceof SavedKits) ? EQUIPMENT_CONTAINER_ID + slot : EQUIPMENT_CONTAINER_ID + index;
                player.getPacketSender().sendItemOnInterfaceSlot(EQUIPMENT_CONTAINER_ID + slot, item, 0);
            }
        }
    }

    /**
     * Populates the equipment container
     * @param player the player
     * @param equipmentContainer the item container
     * @param kits the preset kit
     */
    void populateEquipmentContainer(Player player, ItemContainer equipmentContainer, Kit kits) {
        if (equipmentContainer == null) {
            return;
        }

        var itemList = (kits instanceof SavedKits) ? player.getEquipment() : kits.getEquipmentList();

        for (var equipment : itemList) {
            if (equipment == null) {
                continue;
            }
            equipmentContainer.add0(equipment);
        }
    }

    /**
     * Handles the inventory container
     * @param player the player
     * @param inventoryContainer the item container
     * @param kits the preset kit
     */
    void handleInventoryContainer(Player player, ItemContainer inventoryContainer, Kit kits) {
        itemAmounts.clear();

        if (player == null) {
            return;
        }

        var itemList = (kits instanceof SavedKits) ? player.getInventory() : kits.getInventoryItemList();

        if (itemList != null) {
            for (Item item : itemList) {
                if (item != null) {
                    itemAmounts.put(item.getId(), item.getAmount());
                }
            }
        }

        var itemCapacity = (kits instanceof SavedKits) ? player.getInventory().capacity() : kits.getInventoryItemList().size();

        for (int index = 0; index < itemCapacity; index++) {

            var item = (kits instanceof SavedKits) ? player.getInventory().get(index) : kits.getInventoryItemList().get(index);

            if (item != null) {
                if (!item.stackable() || (!item.noted() && item.getAmount() > 1)) {
                    int amount = itemAmounts.get(item.getId());
                    for (int i = 0; i < amount; i++) {
                        player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, item, index);
                    }
                } else {
                    player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, item, index);
                }
            }
        }
    }

    /**
     * Populates the inventory container
     * @param player the player
     * @param inventoryContainer the item container
     * @param kits the preset kit
     */
    void populateInventoryContainer(Player player, ItemContainer inventoryContainer, Kit kits) {
        if (inventoryContainer != null) {
            if (!inventoryContainer.isEmpty()) {
                inventoryContainer.clear(true);
            }

            var itemList = (kits instanceof SavedKits) ? player.getInventory() : kits.getEquipmentList();

            for (var items : player.getInventory()) {
                if (items != null) {
                    inventoryContainer.add0(items);
                }
            }
        }
    }


    /**
     * Validates the clicked button and applies the corresponding action.
     *
     * @param player the player
     * @param button the button
     */
    void validateAndBuildPreset(Player player, int button) {
        findMatchingButtonIdentificationFor(defaultKits, savedKits, button).ifPresent(kit -> {
            clearAttributesExcept(player, kit.getAttributeKey());

            if (kit instanceof DefaultKits) {
                player.message("Matched with DefaultKits instance");
                // player.putAttrib(kit.getAttributeKey(), true);
                // rebuildInterface(player, equipmentContainer, inventoryContainer, kit);
            } else if (kit instanceof SavedKits) {
                player.message("Matched with SavedKits instance");
                //  rebuildInterface(player, equipmentContainer, inventoryContainer, kit);
            }

            rebuildInterface(player, equipmentContainer, inventoryContainer, kit);
        });
    }


    /**
     * Handles the action when a preset button is clicked.
     *
     * @param player the player
     */
    void handlePresetFunction(Player player) {
        findMatchingAttributeFor(player, defaultKits, savedKits).ifPresent(defaultKits -> applyPreset(player, (DefaultKits) defaultKits));
    }

    /**
     * Applies the selected preset to the player's attributes and inventory.
     *
     * @param player      the player
     * @param defaultKits the selected preset
     */
    void applyPreset(Player player, DefaultKits defaultKits) {
        applyExperience(player, defaultKits);
        applyEquipment(player, defaultKits);
        applyInventory(player, defaultKits);
        applySpellBook(player, defaultKits);
        updatePlayer(player);
    }

    /**
     * Clear the attributes that we're not currently matching with and apply / keep the correct one
     *
     * @param player             the player
     * @param attributeKeyToKeep the attributekey
     */
    void clearAttributesExcept(Player player, AttributeKey attributeKeyToKeep) {
        Arrays.stream(attributeKeys).filter(a -> player.hasAttrib(a) && !a.equals(attributeKeyToKeep)).forEach(player::clearAttrib);
    }

    /**
     * Identify which unique button is tied to the button we're interacting with
     *
     * @param button the button
     * @return optional
     */
    Optional<Kit> findMatchingButtonIdentificationFor(DefaultKits[] kits, SavedKits[] savedKits, int button) {
        for (DefaultKits kit : kits) {
            if (kit.getButtonIdentification() == button) {
                return Optional.of(kit);
            }
        }

        for (SavedKits kit : savedKits) {
            if (kit.getButtonIdentification() == button) {
                return Optional.of(kit);
            }
        }

        return Optional.empty();
    }


    /**
     * Identify which preset we're currently on
     *
     * @param player the player
     * @return optional
     */
    Optional<Kit> findMatchingAttributeFor(Player player, DefaultKits[] defaultKits, SavedKits[] savedKits) {
        for (DefaultKits kit : defaultKits) {
            if (kit.getAttributeKey() == player.getAttrib(kit.getAttributeKey())) {
                return Optional.of(kit);
            }
        }

        for (SavedKits kit : savedKits) {
            if (kit.getAttributeKey() == player.getAttrib(kit.getAttributeKey())) {
                return Optional.of(kit);
            }
        }

        return Optional.empty();
    }

    /**
     * Rebuilds the interface
     *
     * @param player the player
     */
    void rebuildInterface(Player player, ItemContainer equipmentContainer, ItemContainer inventoryContainer, Kit kits) {
        if (kits != null) {
            if (!WildernessArea.isInWilderness(player)) {
                //sendPreMadePresetStrings(player);
                load(player, equipmentContainer, inventoryContainer, kits);
                /// sendSpellbookString(player, kits);
                // sendPrayerString(player);
            } else {
                player.message(Color.RED.wrap("You cannot perform this action while in the wilderness."));
            }
        }
    }

    /**
     * Clears The Item Containers
     */
    void clearInterfaceAndContainers(Player player) {
        if (equipmentContainer != null) {
            equipmentContainer.clear(true);
        }
        if (inventoryContainer != null) {
            inventoryContainer.clear(true);
        }
        for (int index = 0; index < 14; index++) {
            player.getPacketSender().sendItemOnInterfaceSlot(EQUIPMENT_CONTAINER_ID + index, -1, 0, 0);
        }
        for (int index = 0; index < INVENTORY_SIZE; index++) {
            player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, -1, 0, index);
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
     * Submits the items for loading
     * @param player the player
     * @param inventoryContainer the inventory container
     * @param kits the preset kit
     */
    void submitItems(Player player, ItemContainer inventoryContainer, Kit kits) {
        populateInventoryContainer(player, inventoryContainer, kits);
        handleInventoryContainer(player, inventoryContainer, kits);
    }

    /**
     * Submits the equipment for loading
     * @param player the player
     * @param equipmentContainer the equipment container
     * @param kits the preset kit
     */
    void submitEquipment(Player player, ItemContainer equipmentContainer, Kit kits) {
        populateEquipmentContainer(player, equipmentContainer, kits);
        handleEquipmentContainer(player, equipmentContainer, kits);
    }

    /**
     * Sends items to an interface
     *
     * @param player the player
     * @param item   the item instance
     * @param index  the item index
     * @param amount the item amount
     */
    void sendItemsToInterface(Player player, int item, int index, int amount) {
        player.getPacketSender().sendItemOnInterfaceSlot(EQUIPMENT_CONTAINER_ID + index, item, amount, 0);
    }

    /**
     * Sends The Spellbook String
     *
     * @param player the player
     */
    void sendSpellbookString(Player player, Kit kits) {
        player.getPacketSender().sendString(SPELLBOOK_STRING_ID, kits instanceof SavedKits ? player.getSpellbook().name().toLowerCase() : kits.getSpellbook().name().toLowerCase());
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
    void applyExperience(Player player, DefaultKits defaultKits) {
        int[] changeLevelsTo = defaultKits.getAlteredLevels();
        if (!WildernessArea.isInWilderness(player)) {
            checkInclusivesFor(defaultKits).forEachOrdered(i -> player.skills().alterSkillsArray(player, defaultKits.getCurrentLevels()[i], changeLevelsTo[i]));
        } else {
            player.message(Color.RED.wrap("You cannot perform this action while in the wilderness."));
        }
    }

    /**
     * a sequential IntStream for the range of int elements
     *
     * @param defaultKits the presetkit instance
     * @return inclusive
     */
    IntStream checkInclusivesFor(DefaultKits defaultKits) {
        return IntStream.range(0, defaultKits.getCurrentLevels().length);
    }

    /**
     * Apply the Preset Equipment
     *
     * @param player      the player
     * @param defaultKits the presetkit instance
     */
    void applyEquipment(Player player, DefaultKits defaultKits) {
        if (!WildernessArea.isInWilderness(player)) {
            if (player.getEquipment() != null && !player.getEquipment().hasNoEquipment()) {
                player.getBank().depositEquipment();
            }
            defaultKits.getEquipmentList().forEach(item -> {
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
     * @param player      the player
     * @param defaultKits the presetkit instance
     */
    void applySpellBook(Player player, DefaultKits defaultKits) {
        if (!WildernessArea.isInWilderness(player)) {
            sendSpellbookString(player, defaultKits);
            MagicSpellbook.changeSpellbook(player, defaultKits.getSpellbook(), true);
        }
    }

    /**
     * Apply the preset inventory
     *
     * @param player      the player
     * @param defaultKits the presetkit instance
     */
    void applyInventory(Player player, DefaultKits defaultKits) {
        boolean inventoryCheck = player.getInventory().getFreeSlots() > -1;
        if (!WildernessArea.isInWilderness(player)) {
            if (inventoryCheck) {
                player.getBank().depositInventory();
            }
            defaultKits.getInventoryItemList().forEach(item -> {
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
     * @param item   the item
     * @return true/false
     */
    boolean bankDoesntContain(Player player, Item item) {
        return !player.getBank().contains(item);
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

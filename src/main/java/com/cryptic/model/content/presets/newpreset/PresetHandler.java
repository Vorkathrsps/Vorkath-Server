package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.presets.PresetData;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.timers.TimerKey;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

import static com.cryptic.model.entity.attributes.AttributeKey.LAST_PRESET_BUTTON_CLICKED;

/**
 * @Author: Origin
 * @Date: 8/13/2023
 */
@SuppressWarnings("unused")
public class PresetHandler extends PacketInteraction { //TODO add region array for wildy checks
    //TODO add support for degrading items to replace the item in the container with the degradable if they have that available in their bank / inventory

    private static final int PRESET_BUTTON_ID = 73235;
    private static final int EDIT_BUTTON_ID = 73234;
    private static final int SPELLBOOK_STRING_ID = 73237;
    private static final int EQUIPMENT_CONTAINER_ID = 73251;
    private static final int INVENTORY_CONTAINER_ID = 73239;
    private static final int PRE_MADE_PRESET_NAME_STRINGS = 73274;
    private static final int PRAYER_STRING_ID = 73238;
    public static final int INVENTORY_SIZE = 28;
    public static final int EQUIPMENT_SIZE = 11;
    int[] preMadeKitButtons = new int[]{73274, 73275, 73276, 73277, 73277, 73279, 73281, 73283, 73285};
    int[] createKitStrings = new int[]{73291, 73274, 73276, 73278, 73280, 73282, 73284, 73286};
    int[] createKitButtons = new int[]{73296, 73297, 73298, 73299, 73300, 73301, 73302, 73303};
    int[] equipmentChildIds = new int[]{73251, 73252, 73253, 73254, 73255, 73256, 73257, 73258, 73259, 73260, 73261};
    static String[] presetNames = {"Main - Melee", "Zerker - Melee", "Pure - Melee", "Main - Tribrid", "Zerker - Tribrid", "Pure - Tribrid", "Main - Hybrid", "Zerker - Hybrid"};
    AttributeKey[] attributeKeys = new AttributeKey[]{AttributeKey.PURE_MELEE_PRESET, AttributeKey.MAIN_MELEE_PRESET, AttributeKey.ZERKER_MELEE_PRESET};
    public static PresetData[] defaultKits;
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
        player.putAttrib(LAST_PRESET_BUTTON_CLICKED, button);//i assigned uhm, individual preset attributes & it differentiated off that
        // like 1  sec ill show you

        if (isPreMadeKit) {
            player.message("is pre-made");
            clearInterfaceAndContainers(player);
            validateAndBuildPreset(player, button);
            return true;
        } else if (button == PRESET_BUTTON_ID) {
            loadViewedPreset(player);
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

    public static void open(Player player) {
        if (player == null) {
            return;
        }

        sendStrings(player);
        clearInterfaceAndContainers(player);
        player.getInterfaceManager().open(73230);
        player.getPacketSender().sendInterfaceDisplayState(15150, false);
    }

    /**
     * Loads the container submissions
     *
     * @param player             the player
     * @param equipmentContainer the equipment container
     * @param inventoryContainer the inventory container
     * @param kits               the preset kit
     */
    void load(Player player, ItemContainer equipmentContainer, ItemContainer inventoryContainer, PresetData kits) {

        if (player == null || equipmentContainer == null || kits ==  null) {
            return;
        }

        submitEquipment(player, equipmentContainer, kits);
        submitItems(player, inventoryContainer, kits);
    }

    /**
     * Handles the equipment container
     *
     * @param player             the player
     * @param equipmentContainer the equipment container
     * @param kit                the preset kit
     */
    void handleEquipmentContainer(Player player, ItemContainer equipmentContainer, PresetData kit) {
        if (player == null || equipmentContainer == null || kit == null) {
            return;
        }

        var size = EQUIPMENT_SIZE;
        for (int index = 0; index < EQUIPMENT_SIZE; index++) {
            Item item = equipmentContainer.get(index);
            if (item != null) {
                int slot = World.getWorld().equipmentInfo().slotFor(item.getId());
                int amount = item.getAmount();// wasnt even used LKUL
                player.getPacketSender().sendItemOnInterfaceSlot(EQUIPMENT_CONTAINER_ID + slot, item, 0);
            }
        }
    }

    /**
     * Populates the equipment container
     *
     * @param player             the player
     * @param equipmentContainer the item container
     * @param kits               the preset kit
     */
    void populateEquipmentContainer(Player player, ItemContainer equipmentContainer, PresetData kits) {

        if (player == null || kits == null) {
            return;
        }

        var itemList = kits.getEquipment();

        Arrays.stream(itemList).map(item -> new Item(item.getId(), item.getAmount())).forEach(equipmentContainer::add0);
    }

    /**
     * Handles the inventory container
     *
     * @param player             the player
     * @param inventoryContainer the item container
     * @param kits               the preset kit
     */
    void handleInventoryContainer(Player player, ItemContainer inventoryContainer, PresetData kits) {
        if (player == null || inventoryContainer == null || kits == null) {
            return;
        }

        if (!itemAmounts.isEmpty()) {
            itemAmounts.clear();
        }

        for (int index = 0; index < inventoryContainer.capacity(); index++) {
            var container = inventoryContainer.get(index);
            if (container != null) {
                if (!container.stackable() || (!container.noted() && container.getAmount() > 1)) {
                    itemAmounts.put(container.getId(), container.getAmount());
                    int amount = itemAmounts.get(container.getId());
                    player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, container, index);
                } else {
                    player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, container, index);
                }
            }
        }
    }

    /**
     * Populates the inventory container
     *
     * @param player             the player
     * @param inventoryContainer the item container
     * @param kits               the preset kit
     */
    void populateInventoryContainer(Player player, ItemContainer inventoryContainer, PresetData kits) {

        if (player == null || kits == null) {
            return;
        }

        itemAmounts.clear();

        Arrays.stream(kits.getInventory()).map(item -> new Item(item.getId(), item.getAmount())).forEach(inventoryContainer::add0);
    }


    /**
     * Validates the clicked button and applies the corresponding action.
     *
     * @param player the player
     * @param button the button
     */
    void validateAndBuildPreset(Player player, int button) {
        if (player == null) {
            return;
        }

        var kit =
            Arrays.stream(defaultKits).filter(e -> e.button == button)
                .findFirst()
                .orElse(player.<PresetData>getAttribOr(AttributeKey.CUSTOM_PRESETS, null));

        //  clearAttributesExcept(player, kit.getAttributeKey());

        rebuildInterface(player, player.presetUiequipmentContainer, player.presetUiinventoryContainer, kit);

    }


    /**
     * Handles the action when a preset button is clicked.
     *
     * @param player the player
     */
    void loadViewedPreset(Player player) {
        if (player == null) {
            return;
        }

        try {
            var kit =
                Arrays.stream(defaultKits)
                    .filter(e -> e.button == player.<Integer>getAttrib(LAST_PRESET_BUTTON_CLICKED))
                    .findFirst()
                    .orElse(player.<PresetData>getAttribOr(AttributeKey.CUSTOM_PRESETS, null));
            player.message("" + LAST_PRESET_BUTTON_CLICKED.describeConstable());
            applyPreset(player, kit);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Applies the selected preset to the player's attributes and inventory.
     *
     * @param player      the player
     * @param kits the selected preset
     */
    void applyPreset(Player player, PresetData kits) {
        if (player == null || kits == null) {
            return;
        }
        //applyExperience(player, defaultKits);
        applyEquipment(player, kits);
        applyInventory(player, kits);
        applySpellBook(player, kits);
        updatePlayer(player);
    }

    /**
     * Clear the attributes that we're not currently matching with and apply / keep the correct one
     *
     * @param player             the player
     * @param attributeKeyToKeep the attributekey
     */
    void clearAttributesExcept(Player player, AttributeKey attributeKeyToKeep) {
        if (player == null) {
            return;
        }

        Arrays.stream(attributeKeys).filter(a -> player.hasAttrib(a) && !a.equals(attributeKeyToKeep)).forEach(player::clearAttrib);
    }

    /**
     * Rebuilds the interface
     *
     * @param player the player
     */
    void rebuildInterface(Player player, ItemContainer equipmentContainer, ItemContainer inventoryContainer, PresetData kits) {
        if (player == null) {
            return;
        }

        if (!WildernessArea.isInWilderness(player)) {
            //sendPreMadePresetStrings(player);
            load(player, equipmentContainer, inventoryContainer, kits);
            /// sendSpellbookString(player, kits);
            // sendPrayerString(player);
        } else {
            player.message(Color.RED.wrap("You cannot perform this action while in the wilderness."));
        }
    }

    /**
     * Clears The Item Containers
     */
    static void clearInterfaceAndContainers(Player player) {
        if (player == null) {
            return;
        }

        if (player.presetUiequipmentContainer != null) {
            player.presetUiequipmentContainer.clear(true);
        }

        if (player.presetUiinventoryContainer != null) {
            player.presetUiinventoryContainer.clear(true);
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
    static void sendStrings(Player player) {
        if (player == null) {
            return;
        }

        for (int index = 0; index < presetNames.length; index++) {
            player.getPacketSender().sendString(PRE_MADE_PRESET_NAME_STRINGS + index, presetNames[index]);
        }
    }

    /**
     * Submits the items for loading
     *
     * @param player             the player
     * @param inventoryContainer the inventory container
     * @param kits               the preset kit
     */
    void submitItems(Player player, ItemContainer inventoryContainer, PresetData kits) {
        if (player == null || inventoryContainer == null || kits == null) {
            return;
        }

        populateInventoryContainer(player, inventoryContainer, kits);
        handleInventoryContainer(player, inventoryContainer, kits);
    }

    /**
     * Submits the equipment for loading
     *
     * @param player             the player
     * @param equipmentContainer the equipment container
     * @param kits               the preset kit
     */
    void submitEquipment(Player player, ItemContainer equipmentContainer, PresetData kits) {
        if (player == null || equipmentContainer == null || kits == null) {
            return;
        }

        populateEquipmentContainer(player, equipmentContainer, kits);
        handleEquipmentContainer(player, equipmentContainer, kits);
    }

    /**
     * Sends The Spellbook String
     *
     * @param player the player
     */
    void sendSpellbookString(Player player, PresetData kits) {
        if (player == null || kits == null) {
            return;
        }

        player.getPacketSender().sendString(SPELLBOOK_STRING_ID, kits.getSpellbook().name().toLowerCase());
    }

    /**
     * Apply the Preset Equipment
     *
     * @param player      the player
     * @param kits the presetkit instance
     */
    void applyEquipment(Player player, PresetData kits) {
        if (player == null || kits == null) {
            return;
        }

        Arrays.stream(kits.getEquipment()).filter(f -> !WildernessArea.isInWilderness(player)).map(i -> new Item(i, 1)).forEach(item -> {
            if (player.getEquipment() != null && !player.getEquipment().hasNoEquipment()) {
                player.getBank().depositEquipment();
            }
            if (bankDoesntContain(player, item)) {
                player.message(item.getAmount() == 0 ? "Item not found: " + Color.RED.wrap("" + item.name()) : "Item not found: " + Color.RED.wrap("" + item.name()) + " Amount: " + Color.RED.wrap("x" + item.getAmount()));
            } else {
                removeFromBank(player, item);
                player.getEquipment().manualWear(item, true, false);
            }
        });
    }

    /**
     * Method to change our spellbook
     *
     * @param player      the player
     * @param kits the presetkit instance
     */
    void applySpellBook(Player player, PresetData kits) {
        if (player == null || kits == null) {
            return;
        }

        if (!WildernessArea.isInWilderness(player)) {
            sendSpellbookString(player, kits);
            MagicSpellbook.changeSpellbook(player, kits.getSpellbook(), true);
        }
    }

    /**
     * Apply the preset inventory
     *
     * @param player      the player
     * @param kits the presetkit instance
     */
    void applyInventory(Player player, PresetData kits) {
        if (player == null || kits == null) {
            return;
        }

        boolean inventoryCheck = player.getInventory().getFreeSlots() > -1;
        if (!WildernessArea.isInWilderness(player)) {
            if (inventoryCheck) {
                player.getBank().depositInventory();
            }
            Arrays.stream(kits.getInventory()).map(i -> new Item(i, 1)).forEach(item -> {
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
        if (player != null && item != null) {
            player.getBank().remove(new Item(item, item.getAmount()));
        }
    }

    /**
     * Returns true/false if player's bank contains an item
     *
     * @param player the player
     * @param item   the item
     * @return true/false
     */
    boolean bankDoesntContain(Player player, Item item) {
        return player != null && !player.getBank().contains(item);
    }

    /**
     * Updates the player's attributes and equipment after applying the preset.
     *
     * @param player the player
     */
    void updatePlayer(Player player) {
        if (player == null) {
            return;
        }

        ItemWeight.calculateWeight(player);
        player.getInterfaceManager().setSidebar(6, player.getSpellbook().getInterfaceId());
        player.getEquipment().refresh();
        player.inventory().refresh();
        player.heal();
        player.looks().update();
        player.synchronousSave();
    }

}

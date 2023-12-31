package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.presets.PresetData;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.cryptic.model.entity.attributes.AttributeKey.LAST_PRESET_BUTTON_CLICKED;
import static com.cryptic.utility.ItemIdentifiers.RUNE_ARMOUR_SET_LG;

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
    int[] preMadeKitButtons = new int[]{73274, 73275, 73276, 73277, 73278, 73279, 73280, 73281};
    int[] createKitStrings = new int[]{73291, 73274, 73276, 73278, 73280, 73282, 73284, 73286};
    int[] createKitButtons = new int[]{73291, 73292, 73293, 73294, 73295, 73296, 73297, 73298};
    int[] equipmentChildIds = new int[]{73251, 73252, 73253, 73254, 73255, 73256, 73257, 73258, 73259, 73260, 73261};
    static String[] presetNames = {"Main - Melee", "Zerker - Melee", "Pure - Melee", "Main - Tribrid", "Zerker - Tribrid", "Pure - Tribrid", "Main - Hybrid", "Zerker - Hybrid"};
    AttributeKey[] attributeKeys = new AttributeKey[]{AttributeKey.PURE_MELEE_PRESET, AttributeKey.MAIN_MELEE_PRESET, AttributeKey.ZERKER_MELEE_PRESET};
    public static PresetData[] defaultKits;
    boolean canEdit = true;

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
        var isCreatedKit = ArrayUtils.contains(createKitButtons, button);

        if (isPreMadeKit) {
            clearInterfaceAndContainers(player);
            player.putAttrib(LAST_PRESET_BUTTON_CLICKED, button);
            display(player, button);
            return true;
        } else if (isCreatedKit) {
            clearInterfaceAndContainers(player);
            player.putAttrib(LAST_PRESET_BUTTON_CLICKED, button);
            display(player, button);
            return true;
        } else if (button == PRESET_BUTTON_ID) {
            loadPreset(player);
            return true;
        } else if (button == EDIT_BUTTON_ID) {
            if (isEditNotAllowed(player)) {
                player.message(Color.RED.wrap("You cannot edit a pre-made preset."));
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isEditNotAllowed(Player player) {
        for (var a : attributeKeys) {
            if (player.hasAttrib(a)) {
                return true;
            }
        }
        return false;
    }

    public static void open(Player player) {
        if (player == null) return;
        sendStrings(player);
        clearInterfaceAndContainers(player);
        player.getInterfaceManager().open(73230);
        player.getPacketSender().sendInterfaceDisplayState(15150, false);
    }

    private static final Logger logger = LogManager.getLogger(PresetHandler.class);

    void display(Player player, int button) {
        int index = 0;

        var isPreMadeKit = ArrayUtils.contains(preMadeKitButtons, button);
        var isCreatedKit = ArrayUtils.contains(createKitButtons, button);

        if (isPreMadeKit) index = ArrayUtils.indexOf(preMadeKitButtons, button);
        else if (isCreatedKit) index = ArrayUtils.indexOf(createKitButtons, button);

        PresetData[] data = null;

        if (isPreMadeKit) data = defaultKits;
        else if (isCreatedKit) data = player.getPresetData();

        if (data != null) {
            PresetData kit = data[index];

            AttributeKey attributeKey = AttributeKey.CUSTOM_PRESETS;

            if (isPreMadeKit) {
                attributeKey = kit.getAttribute();
            }

            sendStrings(player);
            sendCreatedStrings(player);
            if (kit != null) {
                load(player, kit, attributeKey, button, index);
            } else {
                dialogue(player, data, index, button);
            }
        }

        logger.info("create method executed with button: " + button);
        logger.info("data: " + data);
        logger.debug("Index: " + index);
        logger.debug("isPreMadeKit: " + isPreMadeKit);
        logger.debug("isCreatedKit: " + isCreatedKit);
    }


    void dialogue(Player player, PresetData[] presetData, int presetIndex, int button) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, "Would You Like To Create A Preset?");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes", "No");
                    setPhase(1);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        create(player, presetData, presetIndex, button);
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    void create(Player player, PresetData[] presetData, int presetIndex, int button) {
        player.setNameScript("Set your preset name", value -> {
            PresetData newPreset = new PresetData();
            newPreset.name((String) value);
            newPreset.button = button;
            presetData[presetIndex] = newPreset;
            load(player, newPreset, AttributeKey.CUSTOM_PRESETS, button, presetIndex);
            return false;
        });
    }


    void purchase(Player player, PresetData kits) {
        if (player == null || kits == null || player.getDialogueManager().isActive()) {
            return;
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, new Item(RUNE_ARMOUR_SET_LG), "Purchase A Preset", "Would you like to purchase this preset?", "");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes", "No");
                    setPhase(1);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        if (player.getBank() != null) {
                            if (player.getInventory().contains(ItemIdentifiers.COINS_995, kits.getCost())) {
                                player.getInventory().remove(ItemIdentifiers.COINS_995, kits.getCost());
                                applyPreset(player, kits);
                                stop();
                            } else if (player.getBank().contains(ItemIdentifiers.COINS_995, kits.getCost())) {
                                player.getBank().remove(ItemIdentifiers.COINS_995, kits.getCost());
                                applyPreset(player, kits);
                                stop();
                            } else if (player.getInventory().contains(ItemIdentifiers.PLATINUM_TOKEN, kits.getCost())) {
                                player.getInventory().remove(ItemIdentifiers.PLATINUM_TOKEN, kits.getCost() / 1000);
                                applyPreset(player, kits);
                                stop();
                            } else if (player.getBank().contains(ItemIdentifiers.PLATINUM_TOKEN, kits.getCost() / 1000)) {
                                player.getBank().remove(ItemIdentifiers.PLATINUM_TOKEN, kits.getCost() / 1000);
                                applyPreset(player, kits);
                                stop();
                            } else {
                                player.message(Color.RED.wrap("You do not have enough to purchase this preset kit."));
                                stop();
                            }
                        }
                    } else {
                        stop();
                    }
                }
            }
        });
    }

    void build(Player player, ItemContainer equipmentContainer, ItemContainer inventoryContainer, PresetData data, AttributeKey attributeKey, int button, int index) {
        if (player == null || data == null) {
            logger.info("{} {} {} {}", equipmentContainer, inventoryContainer, data, attributeKey);
            return;
        }

        data
            .build()
            .id(index)
            .inventory(sendInventory(player, inventoryContainer, data))
            .equipment(sendEquipment(player, equipmentContainer, data))
            .spellBook(sendSpellbook(player, data))
            .attribute(sendAttribute(player, data, attributeKey))
            .button(sendButton(player, data, button));
    }

    Integer sendId(Player player, PresetData kits, int index) {
        if (kits.getId() == -1) {
            kits.setId(index);
            return kits.getId();
        }
        return kits.getId();
    }

    AttributeKey sendAttribute(Player player, PresetData kits, AttributeKey attributeKey) {
        if (kits.getAttribute() == null) {
            kits.setAttribute(attributeKey);
            return kits.getAttribute();
        }
        return kits.getAttribute();
    }

    Integer sendButton(Player player, PresetData kits, int button) {
        if (kits.getButton() == -1) {
            kits.setButton(button);
            return kits.getButton();
        }
        return kits.getButton();
    }

    void handleEquipmentContainer(Player player, ItemContainer equipmentContainer, PresetData kit) {
        if (player == null) return;
        var size = EQUIPMENT_SIZE;
        for (int index = 0; index < EQUIPMENT_SIZE; index++) {
            Item item = equipmentContainer.get(index);
            if (item != null) {
                int slot = World.getWorld().equipmentInfo().slotFor(item.getId());
                int amount = item.getAmount();
                player.getPacketSender().sendItemOnInterfaceSlot(EQUIPMENT_CONTAINER_ID + slot, item, 0);
            }
        }
    }

    void populateEquipmentContainer(Player player, ItemContainer equipmentContainer, PresetData kits) {
        if (player == null) return;
        equipmentContainer.clear(true);
        Item[] itemList = kits.getEquipment();
        if (itemList == null) {
            itemList = player.getEquipment().toArray();
            kits.setEquipment(itemList);
        }
        Arrays.stream(itemList).filter(Objects::nonNull).map(item -> new Item(item.getId(), item.getAmount())).forEach(equipmentContainer::add0);
    }

    void handleInventoryContainer(Player player, ItemContainer inventoryContainer, PresetData kits) {
        if (player == null) return;
        Item[] itemList = kits.getInventory();
        if (itemList == null) {
            itemList = player.getInventory().toArray();
            kits.setInventory(itemList);
        }
        for (int index = 0; index < 28; index++) {
            Item item = itemList[index];
            if (item != null) {
                if (!item.stackable() || (!item.noted() && item.getAmount() > 1)) {
                    player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, item, index);
                } else {
                    player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, item, index);
                }
            }
        }
    }

    void populateInventoryContainer(Player player, ItemContainer inventoryContainer, PresetData kits) {
        if (player == null || kits == null) return;

        inventoryContainer.clear(true);

        Item[] itemList = kits.getInventory();

        if (itemList == null) {
            itemList = player.getInventory().toArray();
            kits.setInventory(itemList);
        }

        inventoryContainer.addAll(itemList);
    }

    void validate(Player player, PresetData data, AttributeKey attributeKey, int button, int index) {
        var equipment = player.getPresetEquipment();
        var inventory = player.getPresetInventory();
        if (WildernessArea.isInWilderness(player)) {
            player.message(Color.RED.wrap("You cannot perform this action while in the wilderness."));
            return;
        }
        build(player, equipment, inventory, data, data.getAttribute(), data.getButton(), data.getId());
    }

    void load(Player player, PresetData kit, AttributeKey attributeKey, int button, int index) {
        if (player == null || kit == null) return;
        var inventory = player.getPresetInventory();
        var equipment = player.getPresetEquipment();
        interruptDialogue(player);
        sendStrings(player);
        sendCreatedStrings(player);
        if (kit.getButton() == button) validate(player, kit, attributeKey, button, index);
    }

    void interruptDialogue(@NotNull Player player) {
        if (!player.getDialogueManager().isActive() || player.getDialogueManager() == null) {
            return;
        }

        player.getDialogueManager().remove();
    }

    void loadPreset(Player player) {
        if (player == null) return;
        Arrays.stream(defaultKits)
            .filter(k -> k.getButton() == player.<Integer>getAttrib(LAST_PRESET_BUTTON_CLICKED))
            .findFirst()
            .ifPresentOrElse(k -> purchase(player, k),
                () -> Arrays.stream(player.getPresetData())
                    .filter(k -> k.getButton() == player.<Integer>getAttrib(LAST_PRESET_BUTTON_CLICKED))
                    .findFirst()
                    .ifPresent(k -> applyPreset(player, k)));
    }

    void applyPreset(Player player, PresetData kits) {
        if (player == null || kits == null) return;
        sendCreatedStrings(player);
        applyEquipment(player, kits);
        applyInventory(player, kits);
        applySpellBook(player, kits);
        updatePlayer(player);
    }

    void clearAttributesExcept(Player player, AttributeKey attributeKeyToKeep) {
        if (player == null) return;
        Arrays.stream(attributeKeys).filter(a -> player.hasAttrib(a) && !a.equals(attributeKeyToKeep)).forEach(player::clearAttrib);
    }

    static void clearInterfaceAndContainers(Player player) {
        if (player == null) return;
        if (player.presetEquipment != null) player.presetEquipment.clear(true);
        if (player.presetInventory != null) player.presetInventory.clear(true);
        for (int index = 0; index < 14; index++) player.getPacketSender().sendItemOnInterfaceSlot(EQUIPMENT_CONTAINER_ID + index, -1, 0, 0);
        for (int index = 0; index < INVENTORY_SIZE; index++) player.getPacketSender().sendItemOnInterfaceSlot(INVENTORY_CONTAINER_ID, -1, 0, index);
    }

    void sendCreatedStrings(Player player) {
        for (int index = 0; index < player.getPresetData().length; index++) {
            if (player.getPresetData()[index] != null) {
                player.getPacketSender().sendString(73291 + index, player.getPresetData()[index].getName());
            } else {
                player.getPacketSender().sendString(73291 + index, "Click to create");
            }
        }
    }

    static void sendStrings(Player player) {
        if (player == null) return;
        for (int index = 0; index < presetNames.length; index++) player.getPacketSender().sendString(PRE_MADE_PRESET_NAME_STRINGS + index, presetNames[index]);
    }

    Item[] sendInventory(Player player, ItemContainer inventoryContainer, PresetData kits) {
        if (player == null) return null;
        populateInventoryContainer(player, inventoryContainer, kits);
        handleInventoryContainer(player, inventoryContainer, kits);
        if (kits.getInventory() != null) logger.info("inventory found: " + Arrays.toString(kits.getInventory()));
        return kits.getInventory();
    }

    MagicSpellbook sendSpellbook(Player player, PresetData kits) {
        var spellbook = kits.getSpellbook();

        if (spellbook == null) {
            spellbook = player.getSpellbook();
            kits.setSpellbook(spellbook);
            logger.info("Set spellbook for player: " + player.getDisplayName());
            return kits.getSpellbook();
        }

        logger.info("spellbook found: " + kits.getSpellbook());

        return kits.getSpellbook();
    }

    Item[] sendEquipment(Player player, ItemContainer equipmentContainer, PresetData kits) {
        if (player == null) {
            return null;
        }

        populateEquipmentContainer(player, equipmentContainer, kits);
        handleEquipmentContainer(player, equipmentContainer, kits);

        if (kits.getEquipment() != null) {
            logger.info("equipment found: " + Arrays.toString(kits.getEquipment()));
        }

        logger.info("Populated equipment container for player: " + player.getDisplayName());

        return kits.getEquipment();
    }

    void sendSpellbookString(Player player, PresetData kits) {
        if (player == null || kits == null) {
            return;
        }

        player.getPacketSender().sendString(SPELLBOOK_STRING_ID, Utils.capitalizeFirst(kits.getSpellbook().name()));
    }

    void applyEquipment(Player player, PresetData kits) {
        if (player == null) {
            return;
        }

        if (WildernessArea.isInWilderness(player)) {
            return;
        }

        player.getBank().depositeEquipment();

        Arrays.stream(kits.getEquipment())
            .forEach(item -> {
                if (item != null)
                    if (doesBankContain(player, item)) {
                        player.message(item.getAmount() == 0 ? "Item not found: " + Color.RED.wrap("" + item.name()) : "Item not found: " + Color.RED.wrap("" + item.name()) + " Amount: " + Color.RED.wrap("x" + item.getAmount()));
                    } else {
                        removeFromBank(player, item);
                        player.getEquipment().manualWear(item, true, false);
                    }
            });
    }

    void applySpellBook(Player player, PresetData kits) {
        if (player == null || kits == null) {
            return;
        }

        if (!WildernessArea.isInWilderness(player)) {
            sendSpellbookString(player, kits);
            MagicSpellbook.changeSpellbook(player, kits.getSpellbook(), true);
        }
    }

    void applyInventory(Player player, PresetData kits) {
        if (player == null || kits == null || WildernessArea.isInWilderness(player)) {
            return;
        }

        boolean inventoryCheck = player.getInventory().getFreeSlots() > -1;

        player.getBank().depositInventory();

        Item[] items = kits.getInventory();

        for (int index = 0; index < items.length; index++) {
            Item item = items[index];
            if (item != null) {
                if (doesBankContain(player, item)) {
                    player.message(item.getAmount() == 0 ? "Item not found: " + Color.RED.wrap("" + item.name()) : "Item not found: " + Color.RED.wrap("" + item.name()) + " Amount: " + Color.RED.wrap("x" + item.getAmount()));
                } else {
                    removeFromBank(player, item);
                    if (!item.noted()) {
                        player.getInventory().add(item, index, false);
                    } else {
                        player.getInventory().add(item.note(), index, false);
                    }
                }
            }
        }
    }

    void removeFromBank(Player player, Item item) {
        if (player != null && item != null) {
            player.getBank().remove(new Item(item, item.getAmount()));
        }
    }

    boolean doesBankContain(Player player, Item item) {
        return player != null && !player.getBank().contains(item);
    }

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

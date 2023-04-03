package com.aelous.model.content.presets;

import com.aelous.GameServer;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.duel.Dueling;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.player.*;
import com.aelous.model.items.Item;
import com.aelous.model.items.ItemWeight;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.aelous.model.entity.attributes.AttributeKey.VIEWING_RUNE_POUCH_I;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;


/**
 * A class for handling {@code Presetable}
 * sets.
 *
 * @author Professor Oak
 */
public class PresetManager {

    private static final Logger presetLogs = LogManager.getLogger("PresetLogs");
    private static final Level PRESET;

    static {
        PRESET = Level.getLevel("PRESET");
    }

    // Items that are blacklisted from being created within a preset.
    public static final int[] ILLEGAL_ITEMS = new int[]{
        COINS_995,
        PLATINUM_TOKEN,
        BLOOD_MONEY,
        BLOODY_TOKEN,
    };

    private static final Logger logger = LogManager.getLogger(PresetManager.class);

    /**
     * The player instance.
     */
    private final Player player;

    /**
     * Constructs a new <code>PresetManager<code>.
     */
    public PresetManager(Player player) {
        this.player = player;
    }

    /**
     * The max amount of premade/custom presets.
     */
    public static final int MAX_PRESETS = 20;

    /**
     * The presets interface id.
     */
    private static final int INTERFACE_ID = 62500;

    /**
     * Pre-made sets by the server which everyone can use.
     */
    public static final Presetable[] GLOBAL_PRESETS = new Presetable[MAX_PRESETS];

    private boolean saveLevels = false;

    public boolean saveLevels() {
        return saveLevels;
    }

    public void setSaveLevels(boolean saveLevels) {
        this.saveLevels = saveLevels;
    }

    /**
     * The preset opening on death flag.
     */
    private boolean openOnDeath;

    public boolean openOnDeath() {
        return openOnDeath;
    }

    public void setOpenOnDeath(boolean openOnDeath) {
        this.openOnDeath = openOnDeath;
    }

    /**
     * Opens the presets interface
     * for a player.
     */
    public void open() {
        open(player.getCurrentPreset());
    }

    /**
     * Opens the specified preset for a player.
     */
    public void open(Presetable preset) {
        if (preset != null) {
            // Send name
            player.getPacketSender().sendString(62591, "Preset Name: " + preset.getName());

            // Send stats
            for (int i = 0; i < 7; i++) {
                player.getPacketSender().sendString(62534 + i * 2, "" + preset.getStats()[i]);
                player.getPacketSender().sendString(62535 + i * 2, "" + preset.getStats()[i]);
            }

            // Send spellbook
            player.getPacketSender().sendString(62515, Utils.formatText(preset.getSpellbook().name().toLowerCase()));
            String accountType = "Main";

            if (preset.getStats()[1] <= 45 && preset.getStats()[1] != 1) {
                accountType = "Zerker";
            } else if (preset.getStats()[1] == 1) {
                accountType = "Pure";
            }

            // Stat text
            player.getPacketSender().sendString(62549, accountType);
        } else {
            // Reset name
            player.getPacketSender().sendString(62591, "Presets Manager");

            // Reset stats
            for (int i = 0; i < 7; i++) {
                player.getPacketSender().sendString(62534 + i * 2, "");
                player.getPacketSender().sendString(62535 + i * 2, "");
            }

            // Reset spellbook
            player.getPacketSender().sendString(62515, "Normal");
        }

        //Send inventory
        for (int i = 0; i < 28; i++) {

            //Get item..
            Item item = null;
            if (preset != null) {
                if (i < preset.getInventory().length) {
                    item = preset.getInventory()[i];
                }
            }

            //If it isn't null, send it. Otherwise, send empty slot.
            if (item != null) {

                // retroactive fix until the cause of non-stackables somehow being saved in stacks is found
                if (!item.stackable() && item.getAmount() > 1) {
                    logger.error(String.format("Player %s had amount %s of non-stackable item in inv setup %s %s",
                        player.getMobName(), item.getAmount(), item.getId(), item.name()));
                    item.setAmount(1);
                }
                player.getPacketSender().sendItemOnInterfaceSlot(62592 + i, item.getId(), item.getAmount(), 0);
            } else {
                player.getPacketSender().sendItemOnInterfaceSlot(62592 + i, -1, 1, 0);
            }
        }

        //Send equipment
        for (int i = 0; i < 14; i++) {
            player.getPacketSender().sendItemOnInterfaceSlot(62620 + i, -1, 1, 0);
        }

        if (preset != null) {
            Arrays.stream(preset.getEquipment()).filter(t -> !Objects.isNull(t) && t.isValid())
                .forEach(item -> {

                    // retroactive fix until the cause of non-stackables somehow being saved in stacks is found
                    if (!item.stackable() && item.getAmount() > 1) {
                        logger.error(String.format("Player %s had amount %s of non-stackable item in equip setup %s %s",
                            player.getMobName(), item.getAmount(), item.getId(), item.name()));
                        item.setAmount(1);
                    }
                    player.getPacketSender().sendItemOnInterfaceSlot(
                        62620 + World.getWorld().equipmentInfo().slotFor(item.getId()),
                        item.getId(), item.getAmount(), 0);
                });
        }

        // Send all available global presets
        if (GameServer.properties().pvpMode) {
            for (int i = 0; i < MAX_PRESETS; i++) {
                player.getPacketSender().sendString(62712 + i,
                    GLOBAL_PRESETS[i] == null ? "<col=FF0000>Disabled" : GLOBAL_PRESETS[i].getName());
            }
        }

        for (int index = 0; index < MAX_PRESETS; index++) {
            player.getPacketSender().sendString(GameServer.properties().pvpMode ? 62722 + index : 62712 + index, player.getPresets()[index] == null ? "[Create Preset]" : player.getPresets()[index].getName());
        }

        // Send on death toggle
        player.getPacketSender().sendString(62517, player.getPresetManager().saveLevels ? "Yes" : "No");

        // Send on death toggle
        player.getPacketSender().sendString(62519, player.getPresetManager().openOnDeath ? "Yes" : "No");

        //Send interface
        player.getInterfaceManager().open(INTERFACE_ID);

        //Update current preset
        player.setCurrentPreset(preset);
        player.getPacketSender().updateSpecialAttackOrb();
    }

    private boolean equipment(final Presetable preset) {
        boolean itemNotFound = false;
        StringBuilder missingItemBuilder = new StringBuilder();

        for (int index = 0; index < 14; index++) {
            if (index >= preset.getEquipment().length)
                break;
            Item item = preset.getEquipment()[index];
            if (item == null)
                continue;

            //Skip an item if the slot was already occupied
            EquipmentInfo info = World.getWorld().equipmentInfo();
            int targetSlot = info.slotFor(item.getId());

            if (player.getEquipment().isSlotOccupied(targetSlot)) {
                continue;
            }

            // Check if we're fit enough to equip this item
            boolean[] needsreq = new boolean[1];
            Map<Integer, Integer> reqs = World.getWorld().equipmentInfo().requirementsFor(item.getId());
            if (reqs != null && reqs.size() > 0) {
                reqs.forEach((key, value) -> {
                    if (!needsreq[0] && player.getSkills().xpLevel(key) < value) {
                        player.message("You need %s %s level of %d to equip this.", Skills.SKILL_INDEFINITES[key], Skills.SKILL_NAMES[key], value);
                        needsreq[0] = true;
                    }
                });
            }

            // We don't meet a requirement.
            if (needsreq[0]) {
                player.message("<col=FF0000>You don't have the level requirements to wear: %s.", World.getWorld().definitions().get(ItemDefinition.class, item.getId()).name);
                continue;
            }

            //Global presets, have no checks.
            if (preset.isGlobal()) {
                player.getEquipment().manualWear(item, true, false);
                continue;
            }

            final Item presetItem = item;
            // Dont allow illegal items to be obtained from an existing preset.
            if (Arrays.stream(ILLEGAL_ITEMS).anyMatch(id -> id == presetItem.getId())) {
                continue;
            }

            if (item.definition(World.getWorld()).pvpAllowed) {
                player.getEquipment().manualWear(item, true, false);
                continue;
            }

            item = item.copy();

            // retroactive fix until the cause of non-stackables somehow being saved in stacks is found
            if (!item.stackable() && item.getAmount() > 1) {
                logger.error(String.format("Player %s had amount %s of non-stackable item in equip %s %s",
                    player.getMobName(), item.getAmount(), item.getId(), item.name()));
                item.setAmount(1);
            }

            int tabSlot = player.getBank().getSlot(item.getId());
            if (tabSlot <= -1) {
                //Item doesn't exist in tab slot skip item..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            int tab = player.getBank().tabForSlot(tabSlot);
            if (tab <= -1) {
                //Item doesn't exist in bank tabs skip item..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            Item bankItem = player.getBank().get(tabSlot);
            if (bankItem == null) {
                //item isn't found in the bank at all, skip..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            if (bankItem.getAmount() <= 0) {
                //item isn't found in the bank with any quantity, skip..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            // item.unnote shouldnt be needed cos notes cant go in presets but w.e
            if (player.getBank().remove(new Item(item.unnote(), item.getAmount()), tabSlot, false)) {
                if (!player.getBank().indexOccupied(tabSlot)) {
                    player.getBank().changeTabAmount(tab, -1);
                    player.getBank().shift();
                }
                if (bankItem.getAmount() < item.getAmount()) {
                    item.setAmount(bankItem.getAmount());
                }
                player.getEquipment().manualWear(item.copy(), true, false);
            }
        }
        if (itemNotFound) {
            for (String s : missingItemBuilder.toString().split("\\|")) {
                player.message("Couldn't find " + s + " in your bank.");
            }
        }
        return true;
    }

    private boolean inventory(final Presetable preset) {
        boolean itemNotFound = false;
        Item[] inventory = preset.getInventory();
        StringBuilder missingItemBuilder = new StringBuilder();
        for (int index = 0; index < inventory.length; index++) {
            Item item = inventory[index];
            if (item == null)
                continue;

            item = item.copy();

            //Skip an item if the slot was already occupied
            if(player.inventory().isSlotOccupied(index)) {
                continue;
            }

            //Global presets go over anything, they have no checks, all spawnies.
            if (preset.isGlobal()) {
                player.inventory().add(item.copy(), index, false);
                continue;
            }

            final Item presetItem = item;
            if (Arrays.stream(ILLEGAL_ITEMS).anyMatch(id -> id == presetItem.getId())) {
                continue;
            }

            if (item.definition(World.getWorld()).pvpAllowed) {
                player.inventory().add(item.copy(), index, false);
                continue;
            }

            // retroactive fix until the cause of non-stackables somehow being saved in stacks is found
            if (!item.stackable() && item.getAmount() > 1) {
                logger.error(String.format("Player %s had amount %s of non-stackable item in equip %s %s",
                    player.getMobName(), item.getAmount(), item.getId(), item.name()));
                item.setAmount(1);
            }

            int tabSlot = player.getBank().getSlot(item.getId());
            if (tabSlot <= -1) {
                //Item doesn't exist in tab slot skip item..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            int tab = player.getBank().tabForSlot(tabSlot);
            if (tab <= -1) {
                //Item doesn't exist in bank tabs skip item..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            Item bankItem = player.getBank().get(tabSlot);
            if (bankItem == null) {
                //item isn't found in the bank at all, skip..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            if (bankItem.getAmount() <= 0) {
                //item isn't found in the bank with any quantity, skip..
                itemNotFound = true;
                appendMissingItemReport(missingItemBuilder, item);
                continue;
            }

            if (player.getBank().remove(new Item(item.unnote(), item.getAmount()), tabSlot, false)) {
                if (!player.getBank().indexOccupied(tabSlot)) {
                    player.getBank().changeTabAmount(tab, -1);
                    player.getBank().shift();
                }
                if (bankItem.getAmount() < item.getAmount()) {
                    item.setAmount(bankItem.getAmount());
                }
                player.inventory().add(item.copy(), index, false);

                if (item.getId() == RUNE_POUCH_23650) {
                    player.putAttrib(VIEWING_RUNE_POUCH_I, true);
                } else if (item.getId() == RUNE_POUCH) {
                    player.putAttrib(VIEWING_RUNE_POUCH_I, false);
                }
            }
        }
        if (itemNotFound) {
            for (String s : missingItemBuilder.toString().split("\\|")) {
                player.message("Couldn't find " + s + " in your bank.");
            }
        }
        return true;
    }

    private void appendMissingItemReport(StringBuilder bldr, Item item) {
        bldr.append(item.definition(World.getWorld()).name).append("|");
    }

    private void runepouch(final Presetable preset) {
        Item[] stack = preset.getRunePouch();
        if (stack == null) {
            return;
        }
        if (!player.inventory().contains(RUNE_POUCH)) {
            return;
        }
        for (Item value : stack) {
            Item item = value;

            if (item == null)
                continue;

            item = item.copy();

            // retroactive fix until the cause of non-stackables somehow being saved in stacks is found
            if (!item.stackable() && item.getAmount() > 1) {
                logger.error(String.format("Player %s had amount %s of non-stackable item in equip %s %s",
                    player.getMobName(), item.getAmount(), item.getId(), item.name()));
                item.setAmount(1);
            }
            player.getRunePouch().deposit(item.copy());
        }
    }

    /**
     * Edits a preset.
     *
     * @param index The preset(to edit)'s index
     */
    private void edit(final int index) {

        if (player.getPresets()[index] == null) {
            player.message("This preset cannot be edited.");
            return;
        }

        player.setPresetIndex(index);
        player.getDialogueManager().start(new PresetEditDialogue());
    }

    // If a player used a spawn setup in the last X (given) seconds.
    public static boolean lastTimeDied(Player player, int secs) {
        long time = Long.parseLong(player.getAttribOr(AttributeKey.DEATH_TELEPORT_TIMER, "0"));
        if (time != 0L) {
            return System.currentTimeMillis() - time < secs * 1000L;
        }
        return false;
    }

    public static boolean inPresetBypassable(Player player, String msg, boolean send) {
        if (player.getPlayerRights().isAdministrator(player)) {
            player.message("As an admin, you bypass wilderness restrictions.");
            return false;
        }
        if (WildernessArea.inAttackableArea(player)) {
            if (send) {
                player.message(msg);
            }
            return true;
        }
        return false;
    }

    public void load(final Presetable preset) {
        if (player.locked() || player.dead() || player.hp() < 0 || player.finished())
            return;

        //Global presets only work in PvP world.
        if (!GameServer.properties().pvpMode && preset.isGlobal()) {
            if (!(player.getPlayerRights().isAdministrator(player))) {
                return;
            }
            if (player.getIronManStatus() != IronMode.NONE) {
                player.message("As ironman you cannot load global presets.");
                return;
            }
        }

        // More security
        if (inPresetBypassable(player, "You can't use presets in this area.", true)) {
            return;
        }

        if (Dueling.in_duel(player)) {
            player.message("You cannot use presets during a duel.");
            return;
        }

        if (player.jailed()) {
            player.message("You cannot use a preset when you're in jail.");
            return;
        }

        if (!player.tile().inSafeZone()) {
            player.message("You cannot withdraw any presets outside of safe zones.");
            return;
        }

        //Trained accounts; block global preset until we've reached maxed combat.
        var maxedCombat = player.<Boolean>getAttribOr(AttributeKey.COMBAT_MAXED, false);
        if (player.getGameMode() == GameMode.TRAINED_ACCOUNT && !maxedCombat && preset.isGlobal()) {
            player.message("You can only use presets when you've maxed out your combat stats.");
            return;
        }

        player.stopActions(true);

        //Turn off prayers when applying a new preset.
        Prayers.closeAllPrayers(player);

        //Reset vars
        player.clearAttrib(AttributeKey.VENGEANCE_ACTIVE);

        // Bank everything boys

        //Before banking the inventory first bank looting bag and clear rune pouch
        player.getLootingBag().depositLootingBag();
        player.getRunePouch().clear();

        //When the preset is global auto bank
        player.getBank().depositInventory();
        player.getBank().depositeEquipment();

        player.message("Loading preset...");
        //Only load levels when we have this enabled.
        if(player.getPresetManager().saveLevels() && !preset.isGlobal()) {
            IntStream.range(0, preset.getStats().length).forEach(i -> {
                if (i < preset.getStats().length) {
                    int level = preset.getStats()[i];
                    player.getSkills().setLevel(i, level);
                    player.getSkills().setXp(i, Skills.levelToXp(level));
                }
            });
        }

        boolean equipmentLoaded = equipment(preset);
        boolean inventoryLoaded = inventory(preset);
        runepouch(preset);

        if (equipmentLoaded && inventoryLoaded) {
            player.getInterfaceManager().close();
            player.message("Preset loaded!");
            presetLogs.log(PRESET, "Player: " + player.getUsername() + " successfully loaded a preset with the following items -> equipment: " + Arrays.toString(preset.getEquipment()) + " inventory: " + Arrays.toString(preset.getInventory()));

            MagicSpellbook.changeSpellbook(player, preset.getSpellbook(), false);
            player.getInterfaceManager().setSidebar(6, player.getSpellbook().getInterfaceId());
            ItemWeight.calculateWeight(player);
            player.getEquipment().refresh();
            player.inventory().refresh();
            player.getSkills().update();
            player.heal();

            if (player.getMemberRights().isEliteMemberOrGreater(player)) {
                player.restoreSpecialAttack(100);
            }
            player.getPacketSender().updateSpecialAttackOrb();

            //Store last preset
            if (player.getCurrentPreset() != null) {
                player.setLastPreset(new Object[]{
                    player.getCurrentPreset().isGlobal(),
                    (double) player.getCurrentPreset().getIndex()
                });
            }
        }
    }

    /**
     * Handles a clicked button on the interface.
     */
    public boolean handleButton(int button, int action) {
        //System.out.println("Handle preset button: " + button+" action "+action);
        if (player.getAttribOr(AttributeKey.NEW_ACCOUNT, false)) {
            return true;
        }

        if (button == 72015) {
            if (player.locked()) {
                return true;
            }
            player.getPresetManager().open();
            return true;
        }

        //This isn't a "button" but a text frame, we do not check if the interface is open here
        if (button == 23450) {
            switch (action) {
                case 0 -> player.getPresetManager().open();
                case 1 -> {
                    if (player.getLastPreset() != null) {
                        final boolean isGlobal = (boolean) player.getLastPreset()[0];
                        final Double index = (Double) player.getLastPreset()[1];
                        if (GameServer.properties().pvpMode) {
                            player.getPresetManager().load(isGlobal ? GLOBAL_PRESETS[index.intValue()] : player.getPresets()[index.intValue()]);
                        } else {
                            //In eco mode we only have custom presets
                            player.getPresetManager().load(player.getPresets()[index.intValue()]);
                        }
                    } else {
                        player.message("You have not loaded a preset yet.");
                    }
                }
            }
            return true;
        }

        if (!player.getInterfaceManager().isInterfaceOpen(INTERFACE_ID)) {
            return false;
        }

        switch (button) {
            //Toggle save levels
            case 62510 -> {
                player.getPresetManager().setSaveLevels(!player.getPresetManager().saveLevels);
                player.getPacketSender().sendString(62517, player.getPresetManager().saveLevels ? "Yes" : "No");
                return true;
            }

            //Toggle on death show
            case 62512 -> {
                player.getPresetManager().setOpenOnDeath(!player.getPresetManager().openOnDeath);
                player.getPacketSender().sendString(62519, player.getPresetManager().openOnDeath ? "Yes" : "No");
                return true;
            }

            //Edit preset
            case 62583 -> {
                if (player.getCurrentPreset() == null) {
                    player.message("You haven't selected a preset yet.");
                    return true;
                }
                if (player.getCurrentPreset().isGlobal()) {
                    player.message("You can only edit your own presets.");
                    return true;
                }
                edit(player.getCurrentPreset().getIndex());
                return true;
            }

            //Load preset
            case 62586 -> {
                if (player.getCurrentPreset() == null) {
                    player.message("You haven't selected a preset yet.");
                    return true;
                }
                load(player.getCurrentPreset());
                return true;
            }
        }

        //Global presets selection, this only applies in the pvp mode.
        if (GameServer.properties().pvpMode) {
            if (button >= 62712 && button <= 62721) {
                final int index = button - 62712;

                if (GLOBAL_PRESETS[index] == null) {//brb toilet kk
                    player.message("<col=ff0000> Disabled.");
                    return true;
                }

                //Check if already in set, no need to re-open
                if (player.getCurrentPreset() != null && player.getCurrentPreset() == GLOBAL_PRESETS[index]) {
                    return true;
                }

                open(GLOBAL_PRESETS[index]);
                return true;
            }
        }

        //Custom presets selection
        var startIndex = GameServer.properties().pvpMode ? 62722 : 62712;
        if (button >= startIndex && button <= 62741) {
            final int index = button - startIndex;
            player.setPresetIndex(index);
            if (player.getPresets()[index] == null) {
                player.getDialogueManager().start(new PresetCreateDialogue());
                return true;
            }

            //Check if already in set, no need to re-open
            if (player.getCurrentPreset() != null && player.getCurrentPreset() == player.getPresets()[index]) {
                return true;
            }

            open(player.getPresets()[index]);
            return true;
        }

        return false;
    }
}


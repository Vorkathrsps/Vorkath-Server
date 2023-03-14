package com.aelous.model.inter.lootkeys;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;
import static com.aelous.model.inter.lootkeys.LootKey.infoForPlayer;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <<a href="https://github.com/PVE95">...</a>>
 * @Since March 29, 2022
 */
public class LootChest extends PacketInteraction {

    private static final List<Integer> LOOT_CHEST = Arrays.asList(43469, 43484, 43485, 43486, 44780, 44781);

    private static final int INTERFACE_ID = 69500;
    private static final int GP_ID = 69505;
    private static final int BANK_AMOUNT_ID = 69507;
    private static final int BANK_MAX_ID = 69508;
    private static final int CONTAINER_ID = 69516;
    public static final int WITHDRAW_AS_ITEM_LOOT_KEY = 1145;
    public static final int WITHDRAW_AS_NOTE_LOOT_KEY = 1146;

    private void sendInterfaceInfo(Player player) {
        player.getInterfaceManager().open(INTERFACE_ID);
        player.getPacketSender().sendString(BANK_AMOUNT_ID, "" + player.getBank().size());
        player.getPacketSender().sendString(BANK_MAX_ID, "816");
        player.getPacketSender().sendConfig(WITHDRAW_AS_ITEM_LOOT_KEY, 1);
        player.getPacketSender().sendConfig(WITHDRAW_AS_NOTE_LOOT_KEY, 0);
        player.putAttrib(AttributeKey.LOOT_KEY_WITHDRAW_LOOT_TYPE, 0);
    }

    private void open(Player player) {
        if (!player.inventory().containsAny(LOOT_KEY, LOOT_KEY_26652, LOOT_KEY_26653, LOOT_KEY_26654, LOOT_KEY_26655)) {
            DialogueManager.npcChat(player, Expression.NODDING_ONE, SKULLY, "You don't seem to have any loot keys on you there,", "mate.");
            return;
        }
        player.animate(832);
        player.lock();

        Chain.bound(null).runFn(2, () -> {
            // Open a tab where we have a key
            for (int keyIdx : LootKey.KEYS) {
                if (player.inventory().contains(keyIdx)) {
                    player.putAttrib(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, keyIdx - LOOT_KEY);
                    break;
                }
            }

            var info = infoForPlayer(player);
            var idx = player.<Integer>getAttribOr(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, -1);
            refreshKey(player, info, idx);

            sendInterfaceInfo(player);

            player.unlock();
        });
    }

    /**
     * There are 5 in-game items for each of the 5 keys you can hold at once.
     */
    private static int keyIdForIdx(int idx) {
        return switch (idx) {
            case 1 -> LOOT_KEY_26652;
            case 2 -> LOOT_KEY_26653;
            case 3 -> LOOT_KEY_26654;
            case 4 -> LOOT_KEY_26655;
            default -> LOOT_KEY;
        };
    }

    private static void refreshKey(Player player, LootKeyContainers info, int idx) {
        player.getPacketSender().sendString(GP_ID, Utils.formatNumber(info.keys[idx].value) + "gp");
        player.getPacketSender().sendItemOnInterface(CONTAINER_ID, info.keys[idx].lootContainer.getItems());
    }

    private static void clear(Player player) {
        var info = infoForPlayer(player);
        var idx = player.<Integer>getAttribOr(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, -1);
        info.keys[idx].lootContainer.clear();
        info.keys[idx].value = 0L;
        refreshKey(player, info, idx);
        player.inventory().remove(new Item(keyIdForIdx(idx)), true);
        player.putAttrib(AttributeKey.LOOT_KEYS_CARRIED, Math.max(0, player.<Integer>getAttribOr(AttributeKey.LOOT_KEYS_CARRIED, 0) - 1));
        player.looks().update();
    }

    public static void destroyKey(Player player, Item item) {
        if (LootKey.KEYS.stream().anyMatch(keyId -> item.getId() == keyId)) {
            for (int keyIdx : LootKey.KEYS) {
                if (item.getId() == keyIdx) {
                    player.putAttrib(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, keyIdx - LOOT_KEY);
                    break;
                }
            }
            clear(player);
        }
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (LootKey.KEYS.stream().anyMatch(keyId -> item.getId() == keyId)) {
                for (int keyIdx : LootKey.KEYS) {
                    if (item.getId() == keyIdx) {
                        player.putAttrib(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, keyIdx - LOOT_KEY);
                        break;
                    }
                }

                var info = infoForPlayer(player);
                var idx = player.<Integer>getAttribOr(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, -1);
                var key = info.keys[idx];

                player.message("Your loot key contains items that are worth approximately " + Utils.formatRunescapeStyle(key.value) + " gp.");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (option == 1) {
            if (LOOT_CHEST.stream().anyMatch(chestId -> object.getId() == chestId)) {
                open(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        //Withdraw as item
        if (button == 69509) {
            player.putAttrib(AttributeKey.LOOT_KEY_WITHDRAW_LOOT_TYPE, 0);
            player.getPacketSender().sendConfig(1145, 1);
            player.getPacketSender().sendConfig(1146, 0);
            return true;
        }

        //Withdraw as note
        if (button == 69510) {
            player.putAttrib(AttributeKey.LOOT_KEY_WITHDRAW_LOOT_TYPE, 1);
            player.getPacketSender().sendConfig(1145, 0);
            player.getPacketSender().sendConfig(1146, 1);
            return true;
        }

        //Withdraw to inventory
        if (button == 69513) {
            var info = infoForPlayer(player);
            var idx = player.<Integer>getAttribOr(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, -1);
            if (idx == -1) // -1 if you opened screen as an admin
                return true;
            var asNote = player.<Integer>getAttribOr(AttributeKey.LOOT_KEY_WITHDRAW_LOOT_TYPE, 0) == 1;
            var key = info.keys[idx];
            var toRemove = new ArrayList<Item>(0);
            for (Item loot : key.lootContainer) {
                if (loot == null) continue;
                var added = player.inventory().add(asNote ? loot.note() : loot, true);
                if (added)
                    toRemove.add(new Item(loot.unnote()));
            }

            for (Item looted : toRemove) {
                key.lootContainer.remove(looted, true);
                var itemValue = looted.unnote().getAmount() * looted.unnote().getValue();
                key.value -= itemValue;
            }

            refreshKey(player, info, idx);
            if (player.inventory().getFreeSlots() < key.lootContainer.size()) {
                player.message("Not all of the key's items could be added to your inventory.");
            } else {
                player.inventory().remove(new Item(keyIdForIdx(idx)), true);
                player.putAttrib(AttributeKey.LOOT_KEYS_CARRIED, Math.max(0, player.<Integer>getAttribOr(AttributeKey.LOOT_KEYS_CARRIED, 0) - 1));
                player.looks().update();
                player.getInterfaceManager().close();
            }
            return true;
        }

        //Withdraw to bank
        if (button == 69514) {
            var info = infoForPlayer(player);
            var idx = player.<Integer>getAttribOr(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, -1);
            if (idx == -1) // -1 if you opened screen as an admin
                return true;
            var key = info.keys[idx];
            var toRemove = new ArrayList<Item>(0);
            for (Item loot : key.lootContainer) {
                if (loot == null) continue;
                var added = player.getBank().depositFromNothing(loot).completed();
                if (added > 0)
                    toRemove.add(new Item(loot.unnote()));
            }

            for (Item looted : toRemove) {
                key.lootContainer.remove(looted, true);
                var itemValue = looted.unnote().getAmount() * looted.unnote().getValue();
                key.value -= itemValue;
            }

            refreshKey(player, info, idx);
            if (player.getBank().getFreeSlots() < key.lootContainer.size()) {
                player.message("Not all of the key's items could be added to your bank.");
            } else {
                player.inventory().remove(new Item(keyIdForIdx(idx)), true);
                player.putAttrib(AttributeKey.LOOT_KEYS_CARRIED, Math.max(0, player.<Integer>getAttribOr(AttributeKey.LOOT_KEYS_CARRIED, 0) - 1));
                player.looks().update();
                player.getInterfaceManager().close();
            }
            return true;
        }

        //Clear
        if (button == 69515) {
            player.optionsTitled("Are you sure you wish to clear the loot key?", "Yes.", "No.", () -> {
                clear(player);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        if (LOOT_CHEST.stream().anyMatch(chestId -> object.getId() == chestId)) {
            if (LootKey.KEYS.stream().anyMatch(keyId -> item.getId() == keyId)) {
                // Open a tab where we have a key
                for (int keyIdx : LootKey.KEYS) {
                    if (item.getId() == keyIdx) {
                        player.putAttrib(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, keyIdx - LOOT_KEY);
                        break;
                    }
                }

                var info = infoForPlayer(player);
                var idx = player.<Integer>getAttribOr(AttributeKey.LOOT_KEY_ACTIVE_VIEWED, -1);
                refreshKey(player, info, idx);

                sendInterfaceInfo(player);
                return true;
            }
        }
        return false;
    }
}

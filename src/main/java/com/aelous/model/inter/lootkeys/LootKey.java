package com.aelous.model.inter.lootkeys;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.model.inter.lootkeys.LootKeyContainers.createLootKey;
import static com.aelous.utility.ItemIdentifiers.*;

public class LootKey {

    public static final int LOOT_KEY_CONTAINER_SIZE = 28;
    public ItemContainer lootContainer;
    public long value;

    public LootKey() {
        this.lootContainer = new ItemContainer(LOOT_KEY_CONTAINER_SIZE, ItemContainer.StackPolicy.ALWAYS);
    }

    public LootKey(ItemContainer contents, long value) {
        this.lootContainer = contents;
        this.value = value;
    }

    public LootKey copy() {
        return new LootKey(lootContainer, value);
    }

    public static LootKey @NotNull [] filteredKeys(LootKey @NotNull [] keys) {
        LinkedList<LootKey> keyList = new LinkedList<>();
        for (LootKey key : keys)
            if (key != null)
                Collections.addAll(keyList, key);
        return filteredKeys(keyList);
    }

    public static LootKey @NotNull [] filteredKeys(@NotNull LinkedList<LootKey> keyList) {
        keyList.sort((o1, o2) -> Long.compare(o2.value, o1.value));
        return keyList.toArray(new LootKey[0]);
    }

    // Item ID of loot keys.
    public final static List<Integer> KEYS = Arrays.asList(LOOT_KEY, LOOT_KEY_26652, LOOT_KEY_26653, LOOT_KEY_26654, LOOT_KEY_26655);

    /**
     * Grab the LootKey instance which itself links together the multiple ItemContainers used in loot keys per player.
     */
    public static LootKeyContainers infoForPlayer(@NotNull Player player) {
        var info = player.<LootKeyContainers>getAttribOr(AttributeKey.LOOT_KEY_INFO, new LootKeyContainers(player));
        player.putAttrib(AttributeKey.LOOT_KEY_INFO, info);
        return info;
    }

    // Called on death
    public static void handleDeath(@NotNull Player dead, Player killer) {
        try {
            Tile killersTile = dead.getAttribOr(KILLERS_TILE_ON_DEATH, null);
            boolean killedInWilderness = (killersTile != null && !WildernessArea.inWilderness(killersTile.copy()));
            if (dead == killer || killedInWilderness) {
                System.out.println("return");
                return;// Suicide / pvm death / outside of wild
            }
            if (!killer.deadRecently()) {
                System.out.println("reeeeee");
                // they won't get shit
                var killerInfo = infoForPlayer(killer);
                var deadInfo = infoForPlayer(dead).updateLootKey(killer);//this is null already
                handleDangerousZoneDeath(dead, killer, deadInfo, killerInfo);
            }

            // Since in test worlds we don't lose items, remove keys from inventory manually
            for (int keyId : KEYS) {
                dead.inventory().remove(new Item(keyId, Integer.MAX_VALUE), true);
            }
            dead.clearAttrib(AttributeKey.LOOT_KEYS_CARRIED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        //Should I put this boolean check surrounding that code?
        public static boolean lootKeysEnabled (@NotNull Player killer){//This prevents dupes, ur missing this
            // First check if we have loot keys unlocked
            var lootKeyUnlocked = killer.<Boolean>getAttribOr(LOOT_KEYS_UNLOCKED, false);

            //A second check to see if we have them enabled
            var lootKeyEnabled = killer.<Boolean>getAttribOr(LOOT_KEYS_ACTIVE, false);

            return killer.isPlayer() && (lootKeyUnlocked && lootKeyEnabled);
        }

        /**
         * When you have <5 keys and target has none, instantly reward their loot key.
         * Not as complex as the above system for dealing with the possibility of carrying over 5 keys.
         */
        private static void giveOrDropDeadLootKey(Player dead, Player killer, @NotNull LootKeyContainers deadInfo, LootKeyContainers killerInfo,int carried) {
            try {
                //TODO when a player dies, don't send a loot key but drop the loot to the ground instead.
                //TODO including all the loot from the loot keys they was carrying ----> containervalue is 0 because container value isnt being written do on death, in itemsondeath, thats done before this code
                if (deadInfo.lootKeyItems != null && deadInfo.lootKeyItems.lootContainer != null && deadInfo.lootKeyItems.lootContainer.containerValue() <= 0) { //this is where its breaking,
                    System.out.println("loot key container value is 0 ");
                    return;
                }//Sec lemme check on my end real quick
                if (deadInfo.lootKeyItems != null) {
                    var keyId = switch (carried) {
                        case 0 -> LOOT_KEY; // Key 1
                        case 1 -> LOOT_KEY_26652; // Key 2
                        case 2 -> LOOT_KEY_26653; // Key 3
                        case 3 -> LOOT_KEY_26654; // Key 4
                        case 4 -> LOOT_KEY_26655; // Key 5
                        default -> LOOT_KEY; // Key 1 default
                    };
                    var lootKey = new Item(keyId);
                    var deadKey = createLootKey(killer, deadInfo);
                    killer.message("You are awarded the loot key of %s.", dead.getUsername());

                    if (killer.inventory().add0(lootKey).success()) { //TODO  && !killer.getGameMode().ironman()
                        killerInfo.keys[carried] = deadKey;
                        killer.putAttrib(AttributeKey.LOOT_KEYS_CARRIED, carried + 1);
                        killer.looks().update();
                    } else {
                        var groundItem = new GroundItem(lootKey, dead.tile(), killer).pkedFrom(dead.getUsername()).hidden().linkLootKey(deadKey);
                        GroundItemHandler.createGroundItem(groundItem);
                        killer.message("Your inventory is full, and so the key has remained where your victim died.");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

            // Give keys to killer when in a dangerous zone.
        private static void handleDangerousZoneDeath (@NotNull Player dead, @NotNull Player killer, LootKeyContainers
        deadInfo, LootKeyContainers killerInfo){
            var dead_carried = dead.<Integer>getAttribOr(AttributeKey.LOOT_KEYS_CARRIED, 0);
            var carried = killer.<Integer>getAttribOr(AttributeKey.LOOT_KEYS_CARRIED, 0);
            var possible_total = carried + dead_carried + 1;// Plus one for target's personal loot key.
            var lootKeysEnabled = LootKey.lootKeysEnabled(killer);

            //Don't handle loot key logic, when we don't have them enabled.
            if (!lootKeysEnabled) {
                System.out.println("can get key drop");
                var rewarded = dead_carried + 1; // Carried plus their own loot key
                var toDrop = Math.min(5 - carried, rewarded);

                if (toDrop > 1) {
                    for (int i = 0; i < 5; i++) {
                        var key = deadInfo.keys[i];
                        if (key == null) {
                            continue;
                        }
                        for (Item loot : key.lootContainer) {
                            if (loot == null) continue;
                            var gitem = new GroundItem(loot, dead.tile(), killer).pkedFrom(dead.getUsername());
                            GroundItemHandler.createGroundItem(gitem);
                        }
                        key.lootContainer.clear(); // Clear
                    }
                }
                return;
            }

            if (carried != 5 && dead_carried == 0) {
                System.out.println("can receive key, keys carried "+carried+" vs dead carried "+dead_carried);
                giveOrDropDeadLootKey(dead, killer, deadInfo, killerInfo, carried);
            } else if (carried != 5 && dead_carried > 0) { // Player who died carried keys are handled here
                killer.message("You have been awarded " + dead_carried + " loot keys that " + dead.getUsername() + " was carrying.");

                var filteredKeys = LootKey.filteredKeys(deadInfo.keys); // Possible 5

                // source: https://www.youtube.com/watch?v=mUWWzzUQsBQ
                // (1) see 4:05 for holding 3, killed 2 (total 6, lost 1)
                // (2) 5:05 for holding 0, killed 5 (total 6, lost 1)
                // (3) 6:33 for holding 4, killed 5 (total 10, lost 5)

                var destroyed = possible_total - 5; // Keys we cant carry. Example: Possible total 10 (us 4, them 5) minus 5 we're carrying = 5 will be destroyed
                var rewarded = dead_carried + 1; // Carried plus their own loot key
                var toAdd = Math.min(5 - carried, rewarded); // 5-3 carried = 2 slots free for keys this is me explaining it for dmm logic
                int keycount = carried;

                var keysToGive = new ArrayList<LootKey>(toAdd);
                keysToGive.add(createLootKey(killer, deadInfo));

                if (toAdd > 1) {
                    keysToGive.addAll(Arrays.asList(filteredKeys).subList(0, toAdd - 1));

                    // Step 1: Add as many keys to inventory as possible until inventory full.
                    while (toAdd > 0 && killer.inventory().getFreeSlots() > 0) {
                        var itemId = switch (keycount) {
                            case 0 -> LOOT_KEY; // Key 1
                            case 1 -> LOOT_KEY_26652; // Key 2
                            case 2 -> LOOT_KEY_26653; // Key 3
                            case 3 -> LOOT_KEY_26654; // Key 4
                            case 4 -> LOOT_KEY_26655; // Key 5
                            default -> LOOT_KEY; // Key 1 default
                        };
                        var keyitem = new Item(itemId);
                        var deadkey = keysToGive.get(keycount - carried);
                        if (!killer.inventory().isFull()) {
                            // if(killer.getGameMode().isDarklord()) { //TODO IRONMAN DROPPING
                            //    var gitem = new GroundItem(keyitem, dead.tile(), killer).pkedFrom(dead.getUsername());
                            //    GroundItemHandler.createGroundItem(gitem);
                            //  } else {
                            killer.inventory().add(keyitem);
                        }
                        killerInfo.keys[keycount] = deadkey;
                        killer.debug("Your key #%d value is %d. Total keys now %d", keycount + 1, deadkey.value, keycount + 1);
                        killer.putAttrib(AttributeKey.LOOT_KEYS_CARRIED, keycount + 1);
                        killer.looks().update();
                        toAdd--;
                        keycount++;
                    }
                    //}
                    // Step 2: If there are remaining keys to add, notify the player.
                    if (toAdd > 0 && killer.inventory().getFreeSlots() == 0) {
                        killer.message("Your inventory is full, and so the keys have remained where your victim died.");

                        // Step 3: Drop the remaining keys that should have been added.. up until we have 5 keys. Any more than the 5th is destroyed.
                        while (toAdd-- > 0) {
                            var itemId = switch (keycount) {
                                case 0 -> LOOT_KEY; // Key 1
                                case 1 -> LOOT_KEY_26652; // Key 2
                                case 2 -> LOOT_KEY_26653; // Key 3
                                case 3 -> LOOT_KEY_26654; // Key 4
                                case 4 -> LOOT_KEY_26655; // Key 5
                                default -> LOOT_KEY; // Key 1 default
                            };
                            var gitem = new GroundItem(new Item(itemId), dead.tile(), killer).pkedFrom(dead.getUsername()).hidden().linkLootKey(keysToGive.get(keycount - carried));
                            GroundItemHandler.createGroundItem(gitem);
                            keycount++;
                        }
                    }
                    // Step 4: Notify how many were lost.
                    if (keycount == 5) {
                        var countText = switch (destroyed) {
                            case 2 -> "Two";
                            case 3 -> "Three";
                            case 4 -> "Four";
                            case 5 -> "Five";
                            default -> "One";
                        };
                        killer.message("You have reached the limit of 5 loot keys. " + countText + " more key%s with the least value %s...", destroyed == 1 ? "" : "s", destroyed == 1 ? "has" : "have");
                        killer.message("been destroyed.");
                    }
                }
            } else if (carried == 5) {
                // We already have 5, dead player has 0 keys. Drop their bank raw, no key. We can't carry another key. It's not filtered against our current.
                // (Incentive to wait out skull)
                killer.message("You have reached the limit of 5 loot keys.");
                var deadkey = createLootKey(killer, deadInfo);
                for (Item loot : deadkey.lootContainer) {
                    if (loot == null) continue;
                    var gitem = new GroundItem(loot, dead.tile(), killer).pkedFrom(dead.getUsername());
                    GroundItemHandler.createGroundItem(gitem);
                }
                deadkey.lootContainer.clear(); // Clear

                // Also convert dead players keys into items and drop (yeah there can be like 200 items on the floor at once...)
                if (dead_carried > 1) {
                    for (int i = 0; i < 4; i++) {
                        var key = deadInfo.keys[i];
                        if (key == null) continue;
                        for (Item loot : key.lootContainer) {
                            if (loot == null) continue;
                            var gitem = new GroundItem(loot, dead.tile(), killer).pkedFrom(dead.getUsername());
                            GroundItemHandler.createGroundItem(gitem);
                        }
                        deadkey.lootContainer.clear(); // Clear
                    }
                }
            }
            killer.debug("[Danger death] New key total: %d  dead:%d  possible_total:%d", killer.<Integer>getAttribOr(AttributeKey.LOOT_KEYS_CARRIED, 0), dead_carried, possible_total);
        }

        /**
         * When picking up a key from the floor, obtain the LootKey and items that are linked with it.
         */
        public static void pickupKey(Player player, @NotNull GroundItem item){//does in pickup packet
            var count = switch (item.getItem().getId()) {
                case LOOT_KEY_26652 -> 2;
                case LOOT_KEY_26653 -> 3;
                case LOOT_KEY_26654 -> 4;
                case LOOT_KEY_26655 -> 5;
                default -> 1;
            };
            player.putAttrib(AttributeKey.LOOT_KEYS_CARRIED, count);
            player.looks().update();
            var info = infoForPlayer(player);
            info.keys[count - 1] = item.lootKey();
            item.linkLootKey(null);
        }

        @Override
        public String toString () {
            return "LootKey{" +
                "lootContainer=" + lootContainer +
                ", value=" + value +
                '}';
        }
    }

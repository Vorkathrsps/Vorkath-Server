package com.cryptic.model.content.mechanics.death;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.mechanics.death.ornaments.OrnamentKits;
import com.cryptic.model.content.mechanics.death.repair.Breakable;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.cryptic.utility.ItemIdentifiers.*;

public class DeathResult {
    public static final Logger logger = LogManager.getLogger(DeathResult.class);
    public final Player player;
    public final Entity killer;
    public final List<Item> itemList;
    public final List<Item> untradeables;
    public List<Item> itemsKeptOnDeath;
    public Item[] lootingBag;
    public Item[] runePouch;
    public boolean skulled;

    public DeathResult(Player player, Entity killer, boolean skulled, List<Item> itemList, List<Item> untradeables) {
        this.player = player;
        this.killer = killer;
        this.skulled = skulled;
        this.itemList = itemList;
        this.untradeables = untradeables;
    }

    public static DeathResult create(Player player, Entity killer, boolean skulled, List<Item> items, List<Item> untradeables) {
        return new DeathResult(player, killer, skulled, items, untradeables);
    }

    public DeathResult withLootingBag(Item[] lootingBag) {
        this.lootingBag = lootingBag;
        return this;
    }

    public DeathResult processLootingBag() {
        if (player.getInventory().containsAny(LOOTING_BAG, LOOTING_BAG_22586)) {
            processItems(lootingBag);
        }
        return this;
    }

    public DeathResult withRunePouch(Item[] runePouch) {
        this.runePouch = runePouch;
        return this;
    }

    public DeathResult addBones() {
        itemList.add(new Item(BONES));
        return this;
    }

    public DeathResult processRunePouch() {
        if (player.getInventory().containsAny(RUNE_POUCH, RUNE_POUCH_23650, RUNE_POUCH_23650, RUNE_POUCH_27086, RUNE_POUCH_L, DIVINE_RUNE_POUCH, DIVINE_RUNE_POUCH_L)) {
            processItems(runePouch);
        }
        return this;
    }

    public DeathResult processItems(Item[] items) {
        for (var item : items) {
            if (item != null) {
                if (!item.untradable()) {
                    itemList.add(item);
                    continue;
                }
                for (var breakable : Breakable.values()) {
                    if (breakable.brokenId == item.getId()) {
                        untradeables.add(new Item(breakable.brokenId));
                        break;
                    }
                    if (breakable.id == item.getId()) {
                        untradeables.add(new Item(breakable.brokenId));
                        if (breakable.coinAmount != -1) {
                            itemList.add(new Item(COINS_995, breakable.coinAmount));
                        }
                        if (breakable.itemConversion != -1) {
                            itemList.add(new Item(breakable.itemConversion));
                        }
                        break;
                    }
                }
                for (var kitItem : OrnamentKits.values()) {
                    if (kitItem.id == item.getId()) {
                        for (var replacement : kitItem.conversion) {
                            itemList.add(new Item(replacement));
                        }
                        break;
                    }
                }
            }
        }
        return this;
    }

    public DeathResult sortValue() {
        itemList.sort((o1, o2) -> {
            o1 = o1.unnote();
            o2 = o2.unnote();
            ItemDefinition def = o1.definition(World.getWorld());
            ItemDefinition def2 = o2.definition(World.getWorld());
            int v1 = 0;
            int v2 = 0;
            if (def != null) {
                v1 = o1.getValue();
                if (v1 <= 0 && !GameServer.properties().pvpMode) {
                    v1 = o1.getBloodMoneyPrice().value();
                }
            }
            if (def2 != null) {
                v2 = o2.getValue();
                if (v2 <= 0 && !GameServer.properties().pvpMode) {
                    v2 = o2.getBloodMoneyPrice().value();
                }
            }
            return Integer.compare(v2, v1);
        });
        return this;
    }

    public DeathResult calculateItemsKept() {
        if (player.getIronManStatus().isUltimateIronman()) return this;
        int itemsToRemove = 0;
        if (skulled && Prayers.usingPrayer(player, Prayers.PROTECT_ITEM)) {
            itemsToRemove = Math.min(itemList.size(), 1);
        } else if (!skulled && Prayers.usingPrayer(player, Prayers.PROTECT_ITEM)) {
            itemsToRemove = Math.min(itemList.size(), 4);
        } else if (!skulled) {
            itemsToRemove = Math.min(itemList.size(), 3);
        }
        itemsKeptOnDeath = new ArrayList<>(itemList.subList(0, itemsToRemove));
        itemList.subList(0, itemsToRemove).clear();
        return this;
    }

    public DeathResult clearItems() {
        player.getEquipment().clear();
        player.getInventory().clear();
        return this;
    }

    public DeathResult checkIronManStatus() {
        if (player.getIronManStatus().isHardcoreIronman()) {
            player.setIronmanStatus(IronMode.REGULAR);
            player.getPacketSender().sendRights();
            World.getWorld().sendBroadcast("<img=504>" + player.getDisplayName() + " has lost their hardcore ironman status! Total Level: " + player.getSkills().totalLevel());
        }
        return this;
    }

    public void process() {
        player.getUpdateFlag().flag(Flag.APPEARANCE);
        untradeables.forEach(i -> player.getInventory().add(i));
        itemsKeptOnDeath.forEach(i -> player.getInventory().add(i));
        if (killer instanceof Player attacker) {
            itemList.forEach(i -> GroundItemHandler.createGroundItem(new GroundItem(i, player.tile(), attacker)));
            return;
        }
        itemList.forEach(i -> GroundItemHandler.createGroundItem(new GroundItem(i, player.tile(), player)));
    }

}

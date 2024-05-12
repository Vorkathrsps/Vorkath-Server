package com.cryptic.model.content.mechanics.death;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.duel.Dueling;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.mechanics.death.ornaments.OrnamentKits;
import com.cryptic.model.content.mechanics.death.repair.Breakable;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.GameMode;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.cryptic.utility.ItemIdentifiers.*;

public class DeathResult {

    public static final Logger logger = LogManager.getLogger(DeathResult.class);
    final Player player;
    final Entity killer;
    final List<Item> itemList;
    final List<Item> untradeables;
    final List<Item> alwaysDropped;
    List<Item> itemsKeptOnDeath;
    Item[] lootingBag;
    Item[] runePouch;
    boolean skulled;
    int[] always_lost = new int[]{LARRANS_KEY, MYSTERIOUS_EMBLEM_TIER_1, MYSTERIOUS_EMBLEM_TIER_2, MYSTERIOUS_EMBLEM_TIER_3, MYSTERIOUS_EMBLEM_TIER_4, MYSTERIOUS_EMBLEM_TIER_5};

    public DeathResult(Player player, Entity killer, boolean skulled, List<Item> itemList, List<Item> untradeables, List<Item> alwaysDropped) {
        this.player = player;
        this.killer = killer;
        this.skulled = skulled;
        this.itemList = itemList;
        this.untradeables = untradeables;
        this.alwaysDropped = alwaysDropped;
    }

    public static DeathResult create(Player player, Entity killer, boolean skulled, List<Item> items, List<Item> untradeables, List<Item> alwaysLost) {
        return new DeathResult(player, killer, skulled, items, untradeables, alwaysLost);
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
        if (isSafeDeath()) return this;
        alwaysDropped.add(new Item(BONES));
        return this;
    }

    public DeathResult processRunePouch() {
        if (player.getInventory().containsAny(RUNE_POUCH, RUNE_POUCH_23650, RUNE_POUCH_23650, RUNE_POUCH_27086, RUNE_POUCH_L, DIVINE_RUNE_POUCH, DIVINE_RUNE_POUCH_L)) {
            processItems(runePouch);
        }
        return this;
    }

    final int[] NEVER_LOST = new int[]{RING_OF_WEALTH_I, OVERLOAD_4_20996, REVITALISATION_4_20960, RUNE_POUCH, RUNE_POUCH_23650, RUNE_POUCH_23650, RUNE_POUCH_27086, RUNE_POUCH_L, DIVINE_RUNE_POUCH, DIVINE_RUNE_POUCH_L, SALVE_AMULET, SALVE_AMULETI,SALVE_AMULET_E,SALVE_AMULETEI,SALVE_AMULETEI_25278,SALVE_AMULETI_25250,SALVE_AMULETI_26763};

    public DeathResult processItems(Item[] items) {
        if (isSafeDeath()) return this;
        for (var item : items) {
            if (item != null) {

                if (ArrayUtils.contains(always_lost, item.getId())) {
                    alwaysDropped.add(item);
                    continue;
                }

                if (!item.untradable() && !alwaysDropped.contains(item)) {
                    if (item.stackable()) {
                        itemList.add(new Item(item.getId(), item.getAmount()));
                        continue;
                    }
                    itemList.add(item);
                    continue;
                }

                for (var breakable : Breakable.values()) {
                    if (breakable.brokenId == item.getId()) {
                        final Item broken = new Item(breakable.brokenId);
                        untradeables.add(broken);
                        break;
                    }
                    if (breakable.id == item.getId()) {
                        final Item broken = new Item(breakable.brokenId);
                        untradeables.add(broken);
                        if (breakable.coinAmount != -1) {
                            itemList.add(new Item(COINS_995, breakable.coinAmount));
                        }
                        final Item conversion = new Item(breakable.itemConversion);
                        if (breakable.itemConversion != -1) {
                            itemList.add(conversion);
                        }
                        if (item.untradable() && !untradeables.contains(broken)) {
                            untradeables.add(new Item(item.getId(), item.getAmount()));
                        }
                        if (ArrayUtils.contains(NEVER_LOST, item.getId())) {
                            untradeables.add(new Item(item.getId(), item.getAmount()));
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
        if (isSafeDeath()) return this;
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
        if (player == null || itemList == null) throw new NullPointerException("Player or Item List is null.");
        if (isSafeDeath()) return this;
        if (player.getIronManStatus().isUltimateIronman() || player.getSkullType().equals(SkullType.RED_SKULL))
            return this;
        int itemsToRemove = 0;
        if (skulled && Prayers.usingPrayer(player, Prayers.PROTECT_ITEM)) itemsToRemove = Math.min(itemList.size(), 1);
        else if (!skulled && Prayers.usingPrayer(player, Prayers.PROTECT_ITEM))
            itemsToRemove = Math.min(itemList.size(), 4);
        else if (!skulled) itemsToRemove = Math.min(itemList.size(), 3);
        List<Item> subList = new ArrayList<>();
        List<Item> tempList = new ArrayList<>();
        for (Item item : itemList.subList(0, itemsToRemove)) {
            if (item == null) {
                continue;
            }
            if (item.stackable() && item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
                tempList.add(item);
                subList.add(new Item(item, 1));
            } else {
                subList.add(item);
            }
        }
        itemsKeptOnDeath = subList;
        itemList.subList(0, itemsToRemove).clear();
        itemList.addAll(tempList);
        return this;
    }

    public DeathResult clearItems() {
        if (isSafeDeath()) return this;
        player.getEquipment().clear();
        player.getInventory().clear();
        player.getRunePouch().clear();
        return this;
    }

    public boolean isSafeDeath(InstancedArea instance) {
        return instance != null || Dueling.in_duel(player);
    }

    public DeathResult checkIronManStatus() {
        var instance = player.getInstancedArea();
        if (isSafeDeath(instance)) return this;
        if (player.getGameMode().equals(GameMode.HARDCORE_REALISM)) {
            player.setGameMode(GameMode.REALISM);
            player.getPacketSender().sendRights();
            World.getWorld().sendWorldMessage(STR."\{player.getDisplayName()} has lost their hardcore Realism status! Total Level: \{player.getSkills().totalLevel()}");
        }
        if (player.getIronManStatus().isHardcoreIronman()) {
            player.setIronmanStatus(IronMode.REGULAR);
            player.getPacketSender().sendRights();
            World.getWorld().sendWorldMessage(STR."<img=504>\{player.getDisplayName()} has lost their hardcore ironman status! Total Level: \{player.getSkills().totalLevel()}");
        }
        return this;
    }

    public void process() {
        player.getUpdateFlag().flag(Flag.APPEARANCE);
        if (isSafeDeath()) return;
        untradeables.forEach(i -> player.getInventory().add(i));
        itemsKeptOnDeath.forEach(i -> player.getInventory().add(i));
        if (killer instanceof Player attacker) {
            itemList.forEach(i -> GroundItemHandler.createGroundItem(new GroundItem(i, player.tile(), attacker)));
            alwaysDropped.forEach(i -> GroundItemHandler.createGroundItem(new GroundItem(i, player.tile(), attacker)));
            return;
        }
        itemList.forEach(i -> GroundItemHandler.createGroundItem(new GroundItem(i, player.tile(), player)));
    }

    private boolean isSafeDeath() {
        return Dueling.in_duel(player) || !WildernessArea.inWilderness(player.tile());
    }
}

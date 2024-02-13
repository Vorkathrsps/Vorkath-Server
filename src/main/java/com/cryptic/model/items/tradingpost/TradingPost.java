package com.cryptic.model.items.tradingpost;

import com.cryptic.GameConstants;
import com.cryptic.GameEngine;
import com.cryptic.cache.definitions.identifiers.NumberUtils;
import com.cryptic.model.entity.player.InputScript;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.World;
import com.cryptic.utility.loaders.BloodMoneyPrices;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.cryptic.model.entity.attributes.AttributeKey.*;
import static com.cryptic.utility.CustomItemIdentifiers.BLOODY_TOKEN;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Ynneh
 */
public class TradingPost {

    private static final Logger logger = LogManager.getLogger(TradingPost.class);

    private static final Logger tradingPostLogs = LogManager.getLogger("TradingPostLogs");
    private static final Level TRADING_POST;

    static {
        TRADING_POST = Level.getLevel("TRADING_POST");
    }

    // Items that are blacklisted from being created within a preset.
    public static final int[] ILLEGAL_ITEMS = new int[]{
        COINS_995,
        PLATINUM_TOKEN,
        BLOOD_MONEY,
        BLOODY_TOKEN
    };

    public static boolean TRADING_POST_ENABLED = true;
    public static boolean TRADING_POST_LISTING_ENABLED = true;
    public static boolean TRADING_POST_VALUE_ENABLED = false;
    public static final boolean TESTING = false;
    public static final boolean BLOOD_MONEY_CURRENCY = true;

    private static final int INTERFACE_ID = 81050, HISTORY_ID = 81400, BUY_ID = 81250;
    /**
     * username: data
     */

    public static Map<String, PlayerListing> sales;

    public static List<TradingPostListing> recentTransactions;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Map<Integer, Integer> protection_prices;

    public static void init() {
        try {
            sales = Maps.newHashMap();
            recentTransactions = Lists.newArrayList();
            protection_prices = Maps.newHashMap();
            File folder = new File("./data/saves/tradingpost/listings/");

            if (!folder.exists())
                folder.mkdirs();

            for (File f : Objects.requireNonNull(folder.listFiles())) {
                try {
                    String name = FilenameUtils.removeExtension(f.getName());
                    Type type = new TypeToken<PlayerListing>() {
                    }.getType();
                    PlayerListing listings = gson.fromJson(new FileReader(f), type);
                    sales.put(name.toLowerCase(), listings);
                } catch (IOException e) {
                    e.printStackTrace();
                } // looks fine idk you'd have to look into it
            }
            System.out.println("TradingPost " + sales.size() + " Sale Listings loaded");
            loadRecentSales();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int SALES_REQUIRED_FOR_AVERAGE_PRICE = 4;

    /**
     * calculates the average of first 5 available prices of a specific item in the GE
     * <br> or falls back to {@code Item(itemId).definition(World.getWorld()).bm.getBloodmoneyValue()}
     * <br> when sum 5 are unavailable
     * <p>
     * this can be massively abused if u just list 5 at like 2b price so do u still want this? xd
     * i could list bronze darts x5 @ 2.1b, give 5 darts to a player, kill them, they'd lose AGS cos darts
     * prot over LULW
     *
     * @param itemId
     * @return
     */
    public static int getProtectionPrice(int itemId) {
        List<TradingPostListing> transactions = recentTransactions;

        //Gets newest first
        Collections.reverse(recentTransactions);

        //System.out.println("transactions: "+Arrays.toString(transactions.toArray()));

        //Creates temp list to store first 5.
        List<TradingPostListing> prices = Lists.newArrayList();
        for (TradingPostListing listing : transactions) {
            //System.out.println("enter loop");
            if (listing != null) {
                //System.out.println("listing not null");
                Item protItem = new Item(itemId);
                /*if (prices.size() >= SALES_REQUIRED_FOR_AVERAGE_PRICE) {
                    System.out.println("ofc u fucker");
                    continue;
                }*/
                if (protItem.noted()) {
                    protItem.setId(protItem.unnote().getId());
                }
                if (listing.getSaleItem().getId() == protItem.getId()) {
                    //System.out.println("item matches");
                    prices.add(listing);
                }
            }
        }
        //System.out.println("prices: "+prices.size());
        if (prices.isEmpty() || prices.size() < SALES_REQUIRED_FOR_AVERAGE_PRICE) {
            BloodMoneyPrices bm = new Item(itemId).definition(World.getWorld()).bm;
            if (bm == null)
                return 0;
            return bm.value();
        }

        List<Long> paidPrices = Lists.newArrayList();

        for (TradingPostListing listing : prices) {
            if (listing != null) {
                paidPrices.add(listing.getPrice());
            }
        }
        int value = findAverage(Ints.toArray(paidPrices));
        return value;
    }

    private static int findAverage(int[] list) {
        return (int) Arrays.stream(list).average().orElse(0);
    }

    /**
     * export to .json
     *
     * @param listing
     */
    public static void save(PlayerListing listing) {
        GameEngine.getInstance().submitLowPriority(() -> {
            try {
                List<String> savedFiles = new ArrayList<>();
                final List<Map.Entry<String, PlayerListing>> objects = sales.entrySet().stream().filter(entry -> entry.getValue() == listing).toList();
                for (Map.Entry<String, PlayerListing> entry : objects) {
                    String fileName = "./data/saves/tradingpost/listings/" + entry.getKey() + ".json";
                    try (FileWriter fw = new FileWriter(fileName)) {
                        gson.toJson(entry.getValue(), fw);
                        savedFiles.add(fileName);
                    } catch (IOException e) {
                        logger.error("sadge", e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static AtomicBoolean saved = new AtomicBoolean(false);

    public static void save() {
        GameEngine.getInstance().submitLowPriority(() -> {
            try {
                final List<Map.Entry<String, PlayerListing>> objects = sales.entrySet().stream().toList();
                for (Map.Entry<String, PlayerListing> entry : objects) {
                    try (FileWriter fw = new FileWriter("./data/saves/tradingpost/listings/" + entry.getKey() + ".json")) {
                        gson.toJson(entry.getValue(), fw);
                        saved.getAndSet(true);
                        logger.info("Trading Post Repository Saved.");
                    } catch (IOException e) {
                        logger.error("sadge", e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void saveRecentSales() {
        GameEngine.getInstance().submitLowPriority(() -> {
            try {
                try (FileWriter fw = new FileWriter("./data/saves/tradingpost/recentTransactions.json")) {
                    gson.toJson(recentTransactions, fw);
                } catch (IOException e) {
                    logger.error("sadge", e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void loadRecentSales() {
        GameEngine.getInstance().submitLowPriority(() -> {
            try {
                Type type = new TypeToken<List<TradingPostListing>>() {
                }.getType();
                List<TradingPostListing> sales = new Gson().fromJson(new FileReader("./data/saves/tradingpost/recentTransactions.json"), type);
                if (sales != null) {
                    recentTransactions.addAll(sales);
                    //System.out.println("recent sales info = " + recentTransactions.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void open(Player player) {
        if (!TRADING_POST_LISTING_ENABLED) {
            player.message(Color.RED.wrap("The trading post is currently disabled."));
            return;
        }

        if (player.getUsername().equalsIgnoreCase("Box test")) {
            player.message(Color.RED.wrap("You can't open the trading post."));
            return;
        }

        if (player.getIronManStatus() != IronMode.NONE) {
            player.message(Color.RED.wrap("As an ironman you stand alone."));
            return;
        }

        //printRecentTransactions();
        player.getInterfaceManager().close();
        resetSearchVars(player);
        player.getInterfaceManager().open(INTERFACE_ID);
        refreshInventory(player);
        player.putAttrib(AttributeKey.USING_TRADING_POST, true);
        if (!isValid(player)) { // first time, init a new listing
            PlayerListing listings = new PlayerListing();
            sales.put(player.getUsername().toLowerCase(), listings);
            save(listings);
        }
        sendOverviewTab(player);
    }

    private static void refreshInventory(Player player) {
        player.getInterfaceManager().openInventory(INTERFACE_ID, InterfaceConstants.REMOVE_INVENTORY_ITEM - 1);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.REMOVE_INVENTORY_ITEM, player.inventory().toArray());
    }

    private static void sendOverviewTab(Player p) { // TODO merge send overview
        String user = p.getUsername().toLowerCase();
        final var c = getListings(user);
        List<TradingPostListing> list = c.getListedItems();

        int start = 81124, finish = 81124 + (10 * 5);

        for (int i = start; i < finish; i += 5) { // 5 total entries on this screen
            var idx = (i - start) == 0 ? 0 : (i - start) / 5;
            var item = idx >= list.size() ? null : list.get(idx);
            //Item name
            p.getPacketSender().sendString(i, item == null ? "" : item.getSaleItem().unnote().name()); // Iron Full Helm
            //amt sold/total, Price per - in one string
            p.getPacketSender().sendString(i + 2,  item == null ? "" : "%d/%d | %d (ea)"
                    .formatted(item.getAmountSold(), item.getTotalAmount(), item.getPrice())); // 5/6 | 2k (ea)
            //Hide 'cancel listing' btn
            p.getPacketSender().sendInterfaceDisplayState(i + 3, item == null ? false : true);
        }
        p.getPacketSender().sendString(81073, NumberUtils.formatNumber(123_000_000L)); // coffer
        p.getPacketSender().sendString(81074, "Active: "+NumberUtils.formatNumber(123)); // my trades
        p.getPacketSender().sendString(81075, NumberUtils.formatNumber(456)); // global trades #

    }

    public static void showRecents(Player p, List<TradingPostListing> recentTransactions) {
        for (int i = 0; i < 20; i++) {
            var tpl = i >= recentTransactions.size() ? null : recentTransactions.get(i);
            TradingPost.sendRecentListingIndex(tpl == null ? null : tpl.getSaleItem().unnote(),
                    tpl == null ? "" : tpl.getSellerName(),
                    tpl == null ? "" : "%d | %d (ea)".formatted(tpl.getTotalAmount(), tpl.getPrice()),
                    i,
                    p);
        }
    }

    enum Kys {
        EXIT(0, p -> p.getInterfaceManager().close()),
        OVERVIEW(1, p -> TradingPost.open(p)),
        BUY(2, p -> {}),
        SELL(3, p -> {}),
        TRADE_HISTORY(4, p -> showTradeHistory(p)),
        RECENT_LISTINGS(5, p -> showRecents(p, recentTransactions))
        ;

        private final Function<Integer, Boolean> o;
        private final int i;
        private final Consumer<Player> open;

        Kys(Function<Integer, Boolean> o) {

            this.o = o;
            i = 69699;
            open = null;
        }
        Kys(int i, Consumer<Player> open) {

            this.i = i;
            this.open = open;
            o = null;
        }

        public void open(Player p) {
            open.accept(p);
        }
    }

    static final int[] BASE_TAB_BUTTONS = new int[] {81053, 81253, 81803, 81403, 81603};

    public static boolean handleButtons(Player p, int buttonId) {
        if (!p.<Boolean>getAttribOr(USING_TRADING_POST, false))
            return false;

        for (int i = 0; i < BASE_TAB_BUTTONS.length; i++) {
            var base = BASE_TAB_BUTTONS[i];
            if (buttonId >= base && buttonId <= base + 6) {
                for (Kys value : Kys.values()) {
                    var delta = buttonId - base;
                    logger.info("holy fuck found {} by {} on base {}", value.name(), buttonId, base);
                    if (delta == value.i) {
                        value.open(p);
                        return true;
                    }
                }
            }
        }
        if (buttonId == 81069) { // TODO overview tab: add to coffer dialogue
            return true;
        }
        if (buttonId == 81077 || buttonId == 81078) {
            // feature spot 1
        }
        if (buttonId == 81086 || buttonId == 81087) {
            // feature spot 2
        }
        if (buttonId == 810807 || buttonId == 81081) {
            // feature spot 3
        }
        if (buttonId == 81089 || buttonId == 81090) {
            // feature spot 4
        }
        if (buttonId == 81083 || buttonId == 81084) {
            // feature spot 5
        }

        // overview: remove listing red X button
        for (int i = 0; i < 10; i++) {
            var btn = 81127 + (i * 5);
            if (buttonId == btn) {
                // cancel listing
                logger.info("cancel idx {}", i);
                return true;
            }
        }

        if (buttonId == 66854) {
            refreshListing(p);
            return true;
        }

        if (buttonId == 81053 || buttonId == 81253 || buttonId == 81803 || buttonId == 81403 || buttonId == 81603) {//X button
            p.getInterfaceManager().close();
            return true;
        }
        if (buttonId == 66011) {//search item
            p.setNameScript("Which item would you like to buy?", value -> {
                TradingPost.searchByItemName(p, (String) value, false);
                return true;
            });
            return true;
        }
        if (buttonId == 66014) {//search user
            p.setNameScript("Which persons shop would you like to view? (username)", value -> {
                TradingPost.searchByUsername(p, (String) value, false);
                return true;
            });
            return true;
        }
        if (buttonId == 66017) {//recent sales
            p.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "View recent items sold.", "View recent listed items.", "Nevermind.");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            //printRecentTransactions();
                            // displayResults(p, recentTransactions); // TODO
                            stop();
                        } else if (option == 2) {
                            showTradeHistory(p);
                            stop();
                        } else if (option == 3) {
                            stop();
                        }
                    }
                }
            });
            return true;
        }
        if (buttonId >= 66036 && buttonId <= 66228) {
            handleListingEdits(p, buttonId);
            return true;
        }

        if (handleBuyButtons(p, buttonId))
            return true;
        return false;
    }

    public static void featureSpotText(Player player, String a, String b, int index) {
        int a1 = 81080;
        int a2 = 81081;
        switch (index) {
            case 0 -> {
                a1 = 81077;
                a2 = 81078;
            }
            case 1 -> {
                a1 = 81086;
                a2 = 81087;
            }
            case 2 -> {
                a1 = 81080;
                a2 = 81081;
            }
            case 3 -> {
                a1 = 81089;
                a2 = 81090;
            }
            case 4 -> {
                a1 = 81083;
                a2 = 81084;
            }
        }
        player.getPacketSender().sendString(a1, a);
        player.getPacketSender().sendString(a2, b);
    }

    private static void displayHistory(Player player) {
            player.setNameScript("Which item would you like to view the history of?", new InputScript() {

                @Override
                public boolean handle(Object value) {
                    String itemName = (String) value;
                    if (itemName.length() < 2)
                        return false;

                    TradingPost.handleQueryItemHistory(player, itemName);
                    return true;
                }
            });
    }

    private static void handleQueryItemHistory(Player player, String itemName) {
            List<TradingPostListing> stored = Lists.newArrayList();

            recentTransactions.stream().filter(Objects::nonNull).forEach(history -> {

                Item i = history.getSaleItem().unnote();

                if (i.name().toLowerCase().contains(itemName.toLowerCase())) {
                    stored.add(history);
                }
            });

            if (stored.size() == 0) {
                player.message("<col=ff0000>No results found for '" + itemName + "'");
                return;
            }

            // displayResults(player, stored); // TODO
    }

    public static void showTradeHistory(Player player) {
        player.getInterfaceManager().open(HISTORY_ID);

        List<TradingPostListing> list = Lists.newArrayList();

        sales.entrySet().stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getValue() != null)
                .forEach(recent -> {
                    recent.getValue().getListedItems().forEach(item -> {
                        //System.out.println("Item: " + item.getSaleItem().unnote().name() + " seller: " + item.getSellerName() +" "+ item.getSellerName()+" listed at: "+item.getListedTime()+" "+item.getTimeListed());
                        Item i = item.getSaleItem().unnote();
                        if (i.name().toLowerCase().contains(i.name().toLowerCase())) {
                            list.add(item);
                        }
                    });
                });

        list.sort(Comparator.comparingLong(TradingPostListing::getTimeListed));
        Collections.reverse(list);
        showTradeHistory(player, list);
    }

    public static void showTradeHistory(Player player, List<TradingPostListing> list) {
        for (int i = 0; i < 20; i++) {
            if (i >= list.size()) {
                sendTradeHistoryIndex(null, "None", "", "", "", i, player); // blank
                continue;
            }
            var trade = list.get(i);
            sendTradeHistoryIndex(trade.getSaleItem(),
                    trade.getSaleItem().name(),
                    trade.getSellerName(),
                    trade.buyersInfo.stream().findFirst().orElse("?"),
                    NumberUtils.formatNumber(trade.getPrice()),
                    i,
                    player);
        }
    }


    private static void printRecentTransactions() {
        List<TradingPostListing> list = Lists.newArrayList();

        recentTransactions.stream().filter(Objects::nonNull).forEach(recent -> {
            Item i = recent.getSaleItem().unnote();
            if (i.name().toLowerCase().contains(i.name().toLowerCase())) {
                list.add(recent);
            }
        });

        list.sort(Comparator.comparingLong(TradingPostListing::getTimeListed));
        Collections.reverse(list);

        list.forEach(t -> {
            if (t.getLastBuyerName().equalsIgnoreCase("Band")) {
                var profitClaimed = t.profit <= 0;
                var plural = profitClaimed ? "claimed" : "unclaimed";
                System.out.println(t.getLastBuyerName() + " bought: " + t.getSaleItem().unnote().name() + " from: " + t.getSellerName() + " price: " + t.getPrice() + " amount: " + t.getAmountSold() + " price * amount: " + Utils.formatNumber(t.getPrice() * t.getAmountSold()) + " at date: " + t.getListedTime() + " claimed: " + plural);
            }
        });
    }

    public static int length = 20;
    public static int offset = 6;
    public static int ITEM_NAME_CHILD = 81641;
    public static int NAME_CHILD = 81643;
    public static int AMOUNT_CHILD = 81645;
    public static int ITEM_CONTAINER = 81640;
    public static int TRADE_HISTORY_ITEM_CONTAINER = 81440;

    public static void sendTradeHistoryIndex(Item itemid, String itemname, String seller, String buyer, String price, int idx, Player player) {
        if (idx > 20)
            return;
        var base = 81440;
        base += (6 * idx);
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemid, 0);
        player.getPacketSender().sendString(base + 1, Utils.capitalizeFirst(itemname));
        player.getPacketSender().sendString(base + 2, itemid == null ? "" : Utils.capitalizeFirst(seller)+" sold to");
        player.getPacketSender().sendString(base + 3, Utils.capitalizeFirst(buyer));
        player.getPacketSender().sendString(base + 4, itemid == null ? "" : "Price");
        player.getPacketSender().sendString(base + 5, price); // TODO convert to k, m, b
    }

    /**
     *
     * @param itemname
     * @param seller
     * @param pricePer format "10M | 10k (ea)"
     * @param idx
     * @param player
     */
    public static void sendRecentListingIndex(Item itemname, String seller, String pricePer, int idx, Player player) {
        if (idx > 20)
            return;
        var base = 81640;
        base += (6 * idx);
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemname == null ? null : itemname.unnote(), 0);
        player.getPacketSender().sendString(base + 1, Utils.capitalizeFirst(itemname == null ? "None" : itemname.unnote().name()));
        player.getPacketSender().sendString(base + 2, itemname == null ? "" : "Seller");
        player.getPacketSender().sendString(base + 3, Utils.capitalizeFirst(seller));
        player.getPacketSender().sendString(base + 4, itemname == null ? "" : "Price");
        player.getPacketSender().sendString(base + 5, pricePer); // TODO convert to k, m, b
    }

    public static void handleSellX(Player player, int itemId, long amount) {
        handleSellingItem(player, 3322, itemId, amount);
    }

    public static boolean handleSellingItem(Player player, int interfaceId, int itemId, long amount) {
            if (!player.<Boolean>getAttribOr(USING_TRADING_POST, false) || interfaceId != 3322) {
                return false;
            }

            if (!player.getInterfaceManager().isInterfaceOpen(INTERFACE_ID)) {
                return false;
            }

            if (!TradingPost.TRADING_POST_LISTING_ENABLED) {
                player.message(Color.RED.wrap("The trading post is currently in maintenance mode, u can't sell items."));
                return false;
            }

            if (!player.inventory().contains(itemId)) {
                return false;
            }

            PlayerListing list = getListings(player.getUsername().toLowerCase());

            var totalSalesAllowed = 8;

            switch (player.getMemberRights()) {
                case RUBY_MEMBER -> totalSalesAllowed = 10;
                case SAPPHIRE_MEMBER -> totalSalesAllowed = 12;
                case EMERALD_MEMBER -> totalSalesAllowed = 14;
                case DIAMOND_MEMBER -> totalSalesAllowed = 16;
                case DRAGONSTONE_MEMBER, ONYX_MEMBER, ZENYTE_MEMBER -> totalSalesAllowed = 25;
            }

            //Developers can hold 25 sales by default.
            if (player.getPlayerRights().isCommunityManager(player))
                totalSalesAllowed = 25;

            if (list.getListedItems().size() >= totalSalesAllowed) {
                player.message(Color.RED.wrap("You have already listed " + totalSalesAllowed + " items, remove one to list another."));
                return false;
            }

            List<TradingPostListing> currentListings = list.getSalesMatchingByItemId(new Item(itemId).unnote().getId());

            Item offerItem = new Item(itemId, (int) amount);

            if (!player.inventory().contains(offerItem)) {
                return false;
            }

            if (!offerItem.rawtradable()) {
                player.message("<col=ff0000>You can't offer this item.");
                return false;
            }

            //Pker accounts can't offer free items.
            if (Arrays.stream(GameConstants.DONATOR_ITEMS).anyMatch(donator_item -> donator_item == itemId)) {
                player.message("<col=ff0000>You can't offer this item.");
                return false;
            }

            for (Item bankItem : GameConstants.BANK_ITEMS) {
                if (bankItem.note().getId() == itemId) {
                    player.message("You can't sell this item.");
                    return false;
                }
                if (bankItem.getId() == itemId) {
                    player.message("You can't sell this item.");
                    return false;
                }
            }

            if (offerItem.unnote().definition(World.getWorld()).pvpAllowed) {
                player.message("You can't trade spawnable items.");
                return false;
            }

            if (offerItem.getValue() <= 0) {
                player.message("You can't sell spawnable items.");
                return false;
            }

            // Dont allow illegal items to inserted into a trading post.
            if (Arrays.stream(ILLEGAL_ITEMS).anyMatch(id -> id == offerItem.getId())) {
                player.message("You can't sell illegal items.");
                return false;
            }

            //System.out.println("unnoted id: "+offerItem.unnote().getId()+" match "+ Arrays.toString(currentListings.stream().map(cl -> cl.getSaleItem().unnote().getId()).toArray()));

            if (currentListings.stream().anyMatch(cl -> cl.getSaleItem().unnote() == offerItem.unnote())) {
                player.message("<col=ff0000>You already have a listing of this item. You cannot list it again..");
                player.message("<col=ff0000>.. You will need to edit ur current listing and change quantity.");
                return false;
            }

            if (currentListings.size() > 0) {
                player.message("<col=ff0000>You already have a listing of this item. You cannot list it again..");
                player.message("<col=ff0000>.. You will need to edit ur current listing and change quantity.");
                return false;
            }

            int foundAmount = player.inventory().count(itemId);

            if (amount > foundAmount)
                amount = foundAmount;

            player.tradingPostListedItemId = itemId;
            player.tradingPostListedAmount = (int) amount;//no longer needs to be a long due to it being item Amount

            player.setAmountScript("Enter price to list the item for:", new InputScript() {

                @Override
                public boolean handle(Object value) {
                    TradingPost.handleSalePrice(player, (Integer) value);
                    return true;
                }
            });
        return true;
    }

    public static void handleSalePrice(Player player, long requestedPrice) {
            //System.out.println("handling price setting..");
            int itemId = player.tradingPostListedItemId;
            int amount = player.tradingPostListedAmount;

            if (!player.inventory().contains(itemId)) {
                //System.out.println("blocked.. itemid=" + itemId + " doesn't exist.");
                return;
            }
            int containerAmount = player.inventory().count(itemId);

            if (amount > containerAmount)
                amount = containerAmount;

            if (amount < 1) {
                //System.out.println("Item amount is 0...");
                return;
            }

            Item sale = new Item(itemId, amount);

            if (!sale.isValid()) {
                player.message("<col=ff0000>An error occurred with ur listing.. try again.");
                return;
            }
            player.getDialogueManager().start(new TradingPostConfirmDialogue(sale, requestedPrice));
    }

    public static void listSale(Player player, Item sale, long price) {
            if (player == null || sale == null || !isValid(player) || price <= 0) {
                logger.info("player: " + player.getUsername() + " sale: " + sale.getId() + " price: " + price);
                return;
            }

            TradingPostListing tpl = new TradingPostListing(player.getUsername().toLowerCase(), sale, price);

            PlayerListing listing = sales.getOrDefault(player.getUsername().toLowerCase(), getListings(player.getUsername().toLowerCase()));

            if (!isValid(player)) {
                player.message("Invalid listing. Please try again.");
                return;
            }

            if (!player.inventory().contains(sale.getId(), sale.getAmount())) {
                logger.info("player: " + player.getUsername() + " sale: " + sale.getId() + " price: " + price);
                return;
            }

            if (listing != null) {
                if (tpl.getSaleItem() != null) {
                    if (listing.submit(tpl)) {
                        player.inventory().remove(sale.getId(), sale.getAmount());
                        sales.put(player.getUsername().toLowerCase(), listing);
                        Utils.sendDiscordInfoLog(player.getUsername() + " listed: ItemName=" + sale.name() + " ItemAmount=" + sale.getAmount() + " Price=" + Utils.formatRunescapeStyle(price), "trading_post_sales");
                        tradingPostLogs.log(TRADING_POST, player.getUsername() + " listed: ItemName=" + sale.name() + " ItemAmount=" + sale.getAmount() + " Price=" + Utils.formatRunescapeStyle(price));
                        save(listing);
                        player.message("You've successfully listed your offer to the " + GameConstants.SERVER_NAME + " marketplace!");
                    }
                }
            }
            open(player);
    }

    public static void searchByItemName(Player player, String itemName, boolean refresh) {
            if (itemName == null)
                return;

            if (itemName.length() < 3) {
                player.message("A minimal requirement of 3 letters is required for a search entry.");
                return;
            }

            List<TradingPostListing> list = getSalesForItemName(player, itemName);

            int foundSize = list.size();

            if (foundSize == 0) {
                if (refresh) {
                    open(player);
                    return;
                }
                player.message("<col=ff0000>0 Items found.. with the synx '" + itemName + "'");
                return;
            }
            player.lastTradingPostItemSearch = itemName;
            player.getInterfaceManager().open(BUY_ID);
            displayQuery(player, list);
            if (!refresh) {
                player.getPacketSender().sendString(66603, "Showing offers for item: " + itemName);
                player.message("<col=ff0000>Found " + foundSize + " starting with the synx: '" + itemName + "'");
            }
    }

    /**
     * Used to store the lists of the type search and to display the query
     *
     * @param player
     * @param list
     */
    public static void displayQuery(Player player, List<TradingPostListing> list) {
            //This list is populated but, it some how doesn't send properly when switching pages
            player.putAttrib(AttributeKey.BUY_LISTING_RESULTS, list);
            player.putAttrib(TRADING_POST_BUY_PAGE, 1);
            displayBuyPage(player, list);
    }

    private static void displayBuyPage(Player player, List<TradingPostListing> list) {
        /* To sort from highest to lowest. **/
        list.sort(Comparator.comparingLong(TradingPostListing::getPrice));

        player.tempList = list;

        final int CHILD_LENGTH = 25 * 8;

        for (int i = 66630; i < 66630 + CHILD_LENGTH; i += 8) {
            player.getPacketSender().sendItemOnInterface(i + 1);
            player.getPacketSender().sendString(i + 2, "");
            player.getPacketSender().sendString(i + 3, "");
            player.getPacketSender().sendString(i + 4, "");
            player.getPacketSender().sendInterfaceDisplayState(i + 5, true);
            player.getPacketSender().sendString(i + 7, "");
        }

        int count = 0, start = 66630;

        int itemCount = 0;
        for (TradingPostListing trade : list) {
            itemCount++;
            if (trade == null || trade.getAmountSold() >= trade.getTotalAmount() || itemCount > 25)
                continue;

            player.getPacketSender().sendItemOnInterfaceSlot(start + 1 + (8 * count), new Item(trade.getSaleItem().unnote().getId(), trade.getRemaining()), 0);
            String name = trade.getSaleItem().unnote().name().length() > 20 ? trade.getSaleItem().unnote().name().substring(0, 19) + "<br>" + trade.getSaleItem().unnote().name().substring(19) : trade.getSaleItem().unnote().name();
            player.getPacketSender().sendString(start + 2 + (8 * count), name);
            player.getPacketSender().sendString(start + 3 + (8 * count), "" + Utils.formatRunescapeStyle(trade.getPrice()));
            player.getPacketSender().sendString(start + 4 + (8 * count), trade.getSellerName());
            player.getPacketSender().sendInterfaceDisplayState(start + 5 + (8 * count), false);
            player.getPacketSender().sendString(start + 7 + (8 * count), "Buy");
            count++;
        }
        player.getPacketSender().sendScrollbarHeight(66612, itemCount * 38);
    }

    public static void searchByUsername(Player player, String username, boolean refresh) {
            if (username == null)
                return;

            if (player.getUsername().equalsIgnoreCase(username)) {
                player.getPacketSender().sendMessage("<col=ff0000>You cannot search for your own sales.");
                return;
            }

            username = Utils.capitalizeFirst(username);

            List<TradingPostListing> list = getSalesByUsername(username.toLowerCase());

            int foundSize = list.size();

            if (foundSize == 0) {
                player.message("<col=ff0000>" + username + " doesn't have any items listed.");
                return;
            }
            player.lastTradingPostUserSearch = username;
            player.getInterfaceManager().open(BUY_ID);
            displayQuery(player, list);
            if (!refresh) {
                player.getPacketSender().sendString(66603, "Showing " + username + "'s Trade Post Listings");
                player.message("<col=ff0000>Displaying " + username + "'s " + foundSize + " trade post listings..");
            }
    }

    public static void handleXOptionInput(Player player, int id, int slot) {
            player.setAmountScript("How many of this item would you like to sell?", value -> {
                TradingPost.handleSellX(player, id, (Integer) value);
                return true;
            });
    }

    public static boolean handleBuyButtons(Player player, int buttonId) {
            //System.out.println("buttonId=" + buttonId);

            if (!player.<Boolean>getAttribOr(USING_TRADING_POST, false))
                return false;

            if (!player.getInterfaceManager().isInterfaceOpen(BUY_ID)) {
                //System.out.println("interface not open... " + player.getInterfaceManager().getMain());
                return false;
            }

            if (!TradingPost.TRADING_POST_LISTING_ENABLED) {
                player.message(Color.RED.wrap("The trading post is currently in maintenance mode, u can't buy items."));
                return false;
            }

            List<TradingPostListing> list2 = null;
            if (player.lastTradingPostUserSearch != null && player.lastTradingPostUserSearch.length() > 0) {
                list2 = getSalesByUsername(Utils.capitalizeFirst(player.lastTradingPostUserSearch).toLowerCase());
            } else {
                list2 = getSalesForItemName(player, player.lastTradingPostItemSearch);
            }

            List<TradingPostListing> listDisplay = new ArrayList<>(list2);
            listDisplay.removeIf(o -> o.getRemaining() == 0);

            /* To sort from highest to lowest. **/
            listDisplay.sort(Comparator.comparingLong(TradingPostListing::getPrice));

            player.tempList = listDisplay;

            List<TradingPostListing> offer = player.tempList;

            if (offer == null) {
                player.message("<col=ff0000>That offer no longer exists.");
                return false;
            }

            int offerSize = offer.size();

            buttonId -= 66635;

            if (buttonId > 0)
                buttonId /= 8;

            //System.out.println("ButtonId=" + buttonId + " offerSize=" + offerSize);

            if (buttonId < 0 || buttonId >= offerSize) {
                player.message("<col=ff0000>sale doesn't exist.");
                return false;
            }

            var page = player.<Integer>getAttribOr(TRADING_POST_BUY_PAGE, 1);
            if (page > 1)
                buttonId += (25 * page) - 25;

            int index = buttonId;
            //System.out.println("index: " +index+" "+page+" adding "+ ((25*page)-25));
            TradingPostListing selected = offer.get(index);

            if (selected.getRemaining() == 0) {
                player.message("<col=ff0000>This offer has already been purchased by another player.");
                return false;
            }

            player.putAttrib(AttributeKey.TRADING_POST_ORIGINAL_AMOUNT, selected.getRemaining());
            player.putAttrib(AttributeKey.TRADING_POST_ORIGINAL_PRICE, selected.getPrice());

            if (selected.getRemaining() == 1) {
                handlePurchasing(player, selected, 1);
                return false;
            }

            player.setAmountScript("How many of this item would you like to purchase?", new InputScript() {

                @Override
                public boolean handle(Object value) {
                    handlePurchasing(player, selected, (Integer) value);
                    return true;
                }
            });
        return true;
    }

    private static void handlePurchasing(Player player, TradingPostListing selected, int amount) {
            if (selected == null) {
                player.message(Color.RED.wrap("this offer no longer exists"));
                return;
            }

            if (selected.getSellerName().equalsIgnoreCase(player.getUsername())) {
                player.message(Color.RED.wrap("You can't buy your own items."));
                return;
            }

            if (amount < 1)
                amount = 1;

            int amountRemaining = selected.getTotalAmount() - selected.getAmountSold();

            if (amount > amountRemaining)
                amount = amountRemaining;

            long price = selected.getPrice() * amount;
            player.getDialogueManager().start(new TradingPostConfirmSale(amount, price, selected));
    }

    public static void finishPurchase(Player player, TradingPostListing selected, long totalPrice, int amount, boolean noted) {
            long currency = player.inventory().count(BLOOD_MONEY_CURRENCY ? BLOOD_MONEY : COINS_995);

            long tokens = player.inventory().count(BLOOD_MONEY_CURRENCY ? BLOODY_TOKEN : PLATINUM_TOKEN);

            long totalPriceInPlat = tokens * 1_000;

            long totalAmount = currency + totalPriceInPlat;//price

            //System.out.println("Enough=" + (totalPrice > totalAmount) + " coins=" + coins + " platTokens=" + platTokens + " totalPriceInPlat=" + totalPriceInPlat);
            if (totalPrice > totalAmount) {
                player.message("You don't have enough <col=ff0000>Blood money</col> to complete this transaction...");
                player.message(".. You need a combined value of <col=ff0000>" + Utils.formatRunescapeStyle(totalPrice) + "</col> to complete this transaction.");
                return;
            }

            Item purchased = selected.getSaleItem().unnote();

            String seller = selected.getSellerName();

            selected.setLastBuyerName(player.getUsername());

            selected.buyersInfo.add(player.getUsername());

            selected.setLastTransactionTime(System.currentTimeMillis());

            PlayerListing sellerListing = sales.getOrDefault(seller.toLowerCase(), getListings(seller));

            if (!sellerListing.getListedItems().contains(selected) || selected.getRemaining() == 0) {
                player.message("<col=ff0000>This offer no longer exists.");
                return;
            }

            //Passed checks, now check if the item listed had any recent changes:
            var originalListingAmount = player.<Integer>getAttribOr(AttributeKey.TRADING_POST_ORIGINAL_AMOUNT, 0);
            var originalListingPrice = player.<Long>getAttribOr(AttributeKey.TRADING_POST_ORIGINAL_PRICE, 0L);

            if (selected.getRemaining() != originalListingAmount) {
                player.message(Color.RED.wrap("The quantity has been changed by " + selected.getSellerName() + ", there for your purchase has been..."));
                player.message(Color.RED.wrap("canceled."));
                TradingPost.refreshListing(player);//Refresh
                return;
            }

            if (selected.getPrice() != originalListingPrice) {
                player.message(Color.RED.wrap("The price has been changed by " + selected.getSellerName() + ", there for your purchase has been..."));
                player.message(Color.RED.wrap("canceled."));
                TradingPost.refreshListing(player);//Refresh
                return;
            }

            long remaining = totalPrice;

            int platTokensToRemove = 0, coinsToRemove;

            if (currency >= totalPrice) {
                coinsToRemove = (int) remaining;
            } else {
                remaining -= currency;
                coinsToRemove = (int) currency;
                platTokensToRemove = (int) (remaining / 1_000);
                //logger.info("Remaining=" + Utils.formatNumber(remaining) + " ToRemove=" + Utils.formatNumber(platTokensToRemove) + " PriceInPlat=" + Utils.formatNumber(totalPriceInPlat));
            }

            if (coinsToRemove > 0) {
                player.inventory().remove(BLOOD_MONEY_CURRENCY ? BLOOD_MONEY : COINS_995, coinsToRemove);
            }

            if (platTokensToRemove > 0) {
                player.inventory().remove(BLOOD_MONEY_CURRENCY ? BLOODY_TOKEN : PLATINUM_TOKEN, platTokensToRemove);
            }

            if (noted) {
                //If inv full send to bank or drop!
                Item notedItem = new Item(purchased.getId(), amount).note();
                player.inventory().addOrBank(notedItem);
            } else {
                Item item = new Item(purchased.getId(), amount);
                player.inventory().addOrBank(item);
            }

            sellerListing.updateListing(selected, amount);

            Optional<Player> sel = World.getWorld().getPlayerByName(seller);

            if (sel.isPresent()) {
                sel.get().message("One or more of your trading post offers have been updated.");
                sendOverviewTab(sel.get());
                sel.get().tradePostHistory.add(selected);
            }

            Utils.sendDiscordInfoLog(player.getUsername() + " bought: ItemName=" + purchased.name() + " ItemAmount=" + amount + " Price=" + Utils.formatRunescapeStyle(totalPrice), "trading_post_purchases");
            tradingPostLogs.log(TRADING_POST, player.getUsername() + " bought: ItemName=" + purchased.name() + " ItemAmount=" + amount + " Price=" + Utils.formatRunescapeStyle(totalPrice));

            //System.out.println("Info BOUGHT: ItemName=" + purchased.name() + " ItemAmount=" + amount + " Price=" + Utils.formatRunescapeStyle(totalPrice));
            recentTransactions.add(selected);
            player.tradePostHistory.add(selected);
            //System.out.println("history: " + player.tradePostHistory.toString());
            saveRecentSales();
            refreshListing(player);
            //Clear previously stored attributes
            player.clearAttrib(AttributeKey.TRADING_POST_ORIGINAL_AMOUNT);
            player.clearAttrib(AttributeKey.TRADING_POST_ORIGINAL_PRICE);
    }

    private static boolean offerExists(TradingPostListing selected) {
        return false;
    }

    public static boolean isValid(Player player) {
        return sales.get(player.getUsername().toLowerCase()) != null;
    }

    public static List<TradingPostListing> getSalesByUsername(String username) {
        PlayerListing list = getListings(username);
        return list == null ? Lists.newLinkedList() : list.getListedItems();
    }

    public static List<TradingPostListing> getSalesForItemName(Player player, String itemName) {
        List<TradingPostListing> items = Lists.newArrayList();
        sales.values().stream().filter(Objects::nonNull).map(listing -> listing.getSalesMatchingByString(player, itemName)).forEach(items::addAll);
        return items;
    }

    public static PlayerListing getListings(String username) {
        return sales.get(username);
    }

    private static void handleClaimOffer(Player p, int buttonId) {
        List<TradingPostListing> list = getSalesByUsername(p.getUsername().toLowerCase());

        int size = list.size();

        if (buttonId > size) {
            p.message("<col=ff0000>No offers found to claim..");
            return;
        }

        TradingPostListing offer = list.get(buttonId);

        if (offer == null) {
            p.message("You've already claimed your listing.");
            return;
        }

       /* if (offer.profit <= 0) {
            if (offer.profit < 0)
                offer.profit = 0;
            p.message("<col=ff0000>You don't have any funds to claim from this sell offer.</col>");
            return;
        }*/

        long profit = offer.profit;

        if (profit > Integer.MAX_VALUE) {
            var profitInPlatTokens = profit / 1000;
            var remainingCoins = profit - profitInPlatTokens * 1000;
            p.inventory().addOrBank(new Item(BLOOD_MONEY_CURRENCY ? BLOODY_TOKEN : PLATINUM_TOKEN, (int) profitInPlatTokens));
            tradingPostLogs.log(TRADING_POST, p.getUsername() + " offer claimed for: " + offer.getSaleItem().unnote().name() + " Received=" + (int) profitInPlatTokens + " bloody tokens");

            if (remainingCoins >= 1) {
                p.inventory().addOrBank(new Item(BLOOD_MONEY_CURRENCY ? BLOOD_MONEY : COINS_995, (int) remainingCoins));
                tradingPostLogs.log(TRADING_POST, p.getUsername() + " offer claimed for: " + offer.getSaleItem().unnote().name() + " Received=" + (int) remainingCoins + " blood money");
            }
        } else {
            //Below max int add coins.
            if (profit > 0) {
                p.inventory().addOrBank(new Item(BLOOD_MONEY_CURRENCY ? BLOOD_MONEY : COINS_995, (int) profit));
                tradingPostLogs.log(TRADING_POST, p.getUsername() + " offer claimed for: " + offer.getSaleItem().unnote().name() + " Received=" + (int) profit + " blood money");
            }
        }

        offer.resetProfit(); // probably did this first time

        PlayerListing listing = sales.getOrDefault(offer.getSellerName().toLowerCase(), getListings(offer.getSellerName()));

        if (listing != null) {
            if (offer.getRemaining() == 0) {
                listing.removeListedItem(offer);
            } else {
                listing.saveListing(offer);
            }
            save(listing);
        }
        open(p);
    }

    public static void handleListingEdits(Player player, int buttonId) {
        buttonId -= 66036;

        if (buttonId > 0)
            buttonId /= 8;

        player.getDialogueManager().start(new TradingPostOptions(buttonId));
    }

    public static void modifyListing(Player player, int listIndex, int optionId) {
        if (optionId == 5) {
            /*
             * Never mind
             */
            return;
        }

        PlayerListing listing = sales.getOrDefault(player.getUsername().toLowerCase(), getListings(player.getUsername().toLowerCase()));

        TradingPostListing offer = listing.getSaleBySlot(listIndex);

        if (offer == null) {
            return;
        }

        Item offerItem = offer.getSaleItem();

        if (optionId == 1) {
            handleClaimOffer(player, listIndex);
            return;
        }

        /*if (optionId == 2) {
            if (offer.getRemaining() == 0) {
                player.message(Color.RED.wrap("Your " + offerItem.unnote().name() + " have already been sold."));
                return;
            }

            if (!TradingPost.TRADING_POST_LISTING_ENABLED) {
                player.message(Color.RED.wrap("The trading post is currently in maintenance mode, u can't modify items."));
                return;
            }

            //Edit Quantity
            player.setEnterSyntax(new EnterSyntax() {

                @Override
                public void handleSyntax(Player player, long newSellAmount) {
                    if (newSellAmount == 0 || newSellAmount < 0)
                        return;

                    var oldSellAmount = offer.getRemaining(); // sold 5/10, 5 left
                    var amtToRemove = newSellAmount - oldSellAmount; // try to sell 100. 100-5 = 95 to remove from inventory

                    //Withdrawal...
                    // 100 < 5, false, this code wont run
                    if (newSellAmount < oldSellAmount) {
                        // when you want to Reduce the amount of items your selling, example selling 100, reducing to 50 so youre keeping 50
                        amtToRemove = oldSellAmount - newSellAmount;
                        offer.setQuantity((int) newSellAmount); // its using the
                        player.inventory().addOrBank(new Item(offerItem.getId(), (int) amtToRemove));
                        refresh(player);
                        return;
                    }

                    //Offering more...

                    // count both types rather than either or

                    var carrying = player.inventory().count(offerItem.unnote().getId()) + player.inventory().count(offerItem.note().getId());
                    // only remove as many as are carried in inventory, dont consider those already in the offer yet
                    if (carrying < amtToRemove)
                        amtToRemove = carrying;

                    // just thinking, here you need to only remove carried or amt carried
                    // remove smallest: amt of unnoted you have with you or the request amt if you have more than the request
                    var unnotedAmt = Math.min(amtToRemove, player.inventory().count(offerItem.unnote().getId()));
                    player.inventory().remove(new Item(offerItem.unnote().getId(), (int) unnotedAmt), true);
                    amtToRemove -= unnotedAmt; // remove amt removed, just tracking how many left

                    //
                    var notedAmt = Math.min(amtToRemove, player.inventory().count(offerItem.note().getId()));
                    player.inventory().remove(new Item(offerItem.note().getId(), (int) notedAmt), true);

                    // old 10, + noted taken + unnoted taken
                    offer.setQuantity((int) (oldSellAmount + (notedAmt + unnotedAmt)));
                    refresh(player);
                    save(listing);
                }
            });
            player.getPacketSender().sendEnterAmountPrompt("What quantity would you like to change your offer to?");
            return;
        }

        if (optionId == 3) {
            if (offer.getRemaining() == 0) {
                player.message(Color.RED.wrap("Your " + offerItem.unnote().name() + " have already been sold."));
                return;
            }

            if (!TradingPost.TRADING_POST_LISTING_ENABLED) {
                player.message(Color.RED.wrap("The trading post is currently in maintenance mode, u can't modify items."));
                return;
            }

            // Edit Price
            player.setEnterSyntax(new EnterSyntax() {

                @Override
                public void handleSyntax(Player player, long newPrice) {
                    if (newPrice == 0 || newPrice < 0)
                        return;

                    if (offer.getRemaining() < offer.getTotalAmount()) {
                        player.getPacketSender().sendMessage("<col=ff0000>Your offer already has completed transactions. If you wish to edit the price, cancel it first.");
                        return;
                    }

                    long oldPrice = offer.getPrice();

                    if (newPrice == oldPrice) {
                        player.message(Color.RED.wrap("You're already selling the " + offerItem.unnote().name() + " for " + Utils.formatRunescapeStyle(newPrice) + "."));
                        return;
                    }
                    offer.setPrice(newPrice);
                    refresh(player);
                    save(listing);
                }
            });
            player.getPacketSender().sendEnterAmountPrompt("What would you like to edit this sale price to?");
            return;
        }*/

        if (optionId == 2) {
            if (offer.getRemaining() == 0) {
                player.message(Color.RED.wrap("Your " + offerItem.unnote().name() + " have already been sold."));
                return;
            }

            /*
             * Cancel Listing
             */

            int remaining = offer.getRemaining();

            final Item refund = new Item(offerItem.getId(), remaining);

            long unclaimedProfit = offer.profit;

            int inventoryAmount = player.inventory().getAmountOf(BLOOD_MONEY_CURRENCY ? BLOOD_MONEY : COINS_995);

            long total = inventoryAmount + unclaimedProfit;

            if (remaining == 0) {
                player.message("<col=ff0000>Your sell offer has already been completed, you cannot cancel it.");
                return;
            }

            listing.removeListedItem(offer);
            player.getInventory().addOrBank(refund);
            tradingPostLogs.log(TRADING_POST, player.getUsername() + " successfully canceled the offer for: " + refund.unnote().name());
            player.message("You have successfully canceled your listing for x" + remaining + " " + refund.unnote().name() + "!");

            if (unclaimedProfit > 0) {
                boolean isOver = total > Integer.MAX_VALUE;
                int refundId = isOver ? BLOOD_MONEY_CURRENCY ? BLOODY_TOKEN : PLATINUM_TOKEN : BLOOD_MONEY_CURRENCY ? BLOOD_MONEY : COINS_995;
                Item item = new Item(refundId, isOver ? (int) (unclaimedProfit / 1_000) : (int) unclaimedProfit);
                player.inventory().addOrBank(item);
                tradingPostLogs.log(TRADING_POST, player.getUsername() + " After canceling the offer there was already some unclaimed profits for: " + refund.unnote().name() + " Received: " + item.getAmount() + " blood money!");
                player.message("<col=ff0000>You also had " + Utils.formatNumber(unclaimedProfit) + " blood money unclaimed..");
            }
            refresh(player);
            save(listing);
        }
    }

    public static void refreshListing(Player player) {
        if (player.lastTradingPostUserSearch != null) {
            //System.out.println("Searching by username... DEBUG");
            searchByUsername(player, player.lastTradingPostUserSearch, true);
        } else if (player.lastTradingPostItemSearch != null) {
            //System.out.println("Searching by item name... DEBUG");
            searchByItemName(player, player.lastTradingPostItemSearch, true);
        }
        //System.out.println("Searching by NUN... DEBUG");
    }

    public static void refresh(Player player) {
        open(player);
    }

    public static void resetSearchVars(Player player) {
        player.lastTradingPostItemSearch = null;
        player.lastTradingPostUserSearch = null;
    }
}

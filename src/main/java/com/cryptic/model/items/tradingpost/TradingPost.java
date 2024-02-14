package com.cryptic.model.items.tradingpost;

import com.cryptic.GameConstants;
import com.cryptic.GameEngine;
import com.cryptic.cache.definitions.identifiers.NumberUtils;
import com.cryptic.model.entity.player.InputScript;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.World;
import com.cryptic.utility.loaders.BloodMoneyPrices;
import com.cryptic.model.entity.attributes.AttributeKey;
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

    private static final int OVERVIEW = 81050, HISTORY_ID = 81400, BUY_ID = 81250, SELL_ID = 81800;
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
        player.getInterfaceManager().open(OVERVIEW);
        sendOfferInventory(player, OVERVIEW); // TODO sell tab inventoy doesnt work without this-why
        player.putAttrib(AttributeKey.USING_TRADING_POST, true);
        if (!isValid(player)) { // first time, init a new listing
            PlayerListing listings = new PlayerListing();
            sales.put(player.getUsername().toLowerCase(), listings);
            save(listings);
        }
        sendOverviewTab(player);
    }

    private static void sendOfferInventory(Player player, int main) {
        player.getInterfaceManager().openInventory(main, InterfaceConstants.REMOVE_INVENTORY_ITEM - 1);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.REMOVE_INVENTORY_ITEM, player.inventory().toArray());
    }

    private static void sendOverviewTab(Player player) {
        String user = player.getUsername().toLowerCase();
        final var c = getListings(user);
        List<TradingPostListing> list = c.getListedItems();

        for (int i = 0; i < 10; i++) { // 10 total entries on this screen
            var item = i >= list.size() ? null : list.get(i);

            sendOverviewIndex(item == null ? null : item.getSaleItem(),
                    item == null ? "" : item.getSaleItem().unnote().name(),
                    item == null ? "" : "%d/%d | %d (ea)"
                            .formatted(item.getAmountSold(), item.getTotalAmount(), item.getPrice()),
                    item == null ? -1 :  (int) (item.amountSold * 100 / (double) item.getTotalAmount()),
                    i,
                    player);
        }
        player.getPacketSender().sendString(81073, NumberUtils.formatNumber(
                1L * player.inventory().count(995)
                        + (long) (1000L * player.inventory().count(13307))
                        + (long) (1000L * player.inventory().count(PLATINUM_TOKEN)))); // TODO my coins- coffer
        player.getPacketSender().sendString(81074, "Active: "+NumberUtils.formatNumber(list == null ? 0 : list.size())); // TODO my trades
        player.getPacketSender().sendString(81075, NumberUtils.formatNumber(sales.size())); // global trades
    }

    public static void sendOverviewIndex(Item item, String name, String priceper, int progress, int idx, Player player) {
        int base = 81123;
        base += (5 * idx);
        player.getPacketSender().sendItemOnInterfaceSlot(base, item, 0);
        player.getPacketSender().sendString(base + 1, name); // Iron Full Helm
        if (progress > -1)
            player.getPacketSender().sendProgressBar(base + 2, progress);
        player.getPacketSender().sendInterfaceDisplayState(base + 2, progress == -1);
        player.getPacketSender().sendString(base + 3, priceper); // 5/6 | 2k (ea)
        player.getPacketSender().sendInterfaceDisplayState(base + 4, item == null); //Hide 'cancel listing' btn
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
        BUY(2, p -> openBuyUI(p)),
        SELL(3, p -> openSellUI(p)),
        TRADE_HISTORY(4, p -> showTradeHistory(p)),
        RECENT_LISTINGS(5, p -> showRecents(p, recentTransactions))
        ;

        private final int delta;
        private final Consumer<Player> open;

        Kys(int delta, Consumer<Player> open) {
            this.delta = delta;
            this.open = open;
        }

        public void open(Player p) {
            open.accept(p);
        }
    }

    public static void openSellUI(Player p) {
        p.getInterfaceManager().open(SELL_ID);
        p.getPacketSender().sendItemOnInterfaceSlot(81819, null, 0);
        p.getPacketSender().sendString(81820, "");
        p.getPacketSender().sendString(81822, sales.size()+""); // current sales
        p.getPacketSender().sendString(81824, ""); // TODO avg sell time
        p.getPacketSender().sendString(81826, ""); // TODO market price
        p.getPacketSender().sendString(81828, ""); // TODO quantity
        p.getPacketSender().sendString(81830, ""); // TODO price per item green text
        // TODO what to show?
        for (int i = 0; i < 5; i++) {
            sendSellItemIndex(null, "", "", "", i, p);
        }
        sendOfferInventory(p, SELL_ID);
    }

    public static void openBuyUI(Player p) {
        p.getInterfaceManager().open(BUY_ID);
        p.getPacketSender().sendString(81271, "2344"); // open offers
        p.getPacketSender().sendString(81272, "127k"); // item volume
        p.getPacketSender().sendString(81273, "Type username"); // username wipe
        p.getPacketSender().sendString(81274, "Type itemname"); // item wipe
        for (int i = 0; i < 10; i++) { // TODO show what
            sendBuyIndex(null, "", "", i, p);
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
                    logger.debug("holy fuck found {} by {} on base {}", value.name(), buttonId, base);
                    if (delta == value.delta) {
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
            p.message("This feature is coming soon.");
            // feature spot 1
            return true;
        }
        if (buttonId == 81086 || buttonId == 81087) {
            p.message("This feature is coming soon.");
            // feature spot 2
            return true;
        }
        if (buttonId == 810807 || buttonId == 81081) {
            p.message("This feature is coming soon.");
            // feature spot 3
            return true;
        }
        if (buttonId == 81089 || buttonId == 81090) {
            p.message("This feature is coming soon.");
            // feature spot 4
            return true;
        }
        if (buttonId == 81083 || buttonId == 81084) {
            p.message("This feature is coming soon.");
            // feature spot 5
            return true;
        }
        if (buttonId == 81378) { // X buy specific item
            p.getInterfaceManager().close();
            return true;
        }

        // overview: remove listing red X button
        for (int i = 0; i < 10; i++) {
            var btn = 81127 + (i * 5);
            if (buttonId == btn) {
                TradingPost.modifyListing(p, i, 2);  // cancel listing
                //logger.debug("cancel idx {}", i);
                return true;
            }
        }

        if (buttonId == 81275) { // username wipe
            p.getPacketSender().sendString(81273, "");
            return true;
        }
        if (buttonId == 81276) { // search itemname wipe
            p.getPacketSender().sendString(81274, "");
            return true;
        }
        if (buttonId >= 81278 && buttonId <= 81278 + 20) {
            var index = buttonId - 81278;
            if (handleBuyButtons(p, index))
                return true;
        }
        if (buttonId == 81831) { // TODO sell tab- quantity minus 1
            return true;
        }
        if (buttonId == 81832) { // TODO sell tab- quantity plus 1
            return true;
        }
        if (buttonId == 81833) { // TODO sell tab- price minus 1
            return true;
        }
        if (buttonId == 81834) { // TODO sell tab- price minus 1
            return true;
        }
        if (buttonId == 81835) { // TODO sell tab- quantity +1 again
            return true;
        }
        if (buttonId == 81836) { // TODO sell tab- quantity +10
            return true;
        }
        if (buttonId == 81837) { // TODO sell tab- quantity +100
            return true;
        }
        if (buttonId == 81838) { // TODO sell tab- quantity custom enter
            return true;
        }
        if (buttonId == 81841) { // TODO sell tab- price custom
            return true;
        }
        if (buttonId == 81843) { // TODO sell tab- sell confirm 1st screen
            return true;
        }


        if (buttonId == 81379) { // TODO purchase confirm
            if (p.getDialogueManager().getDialogue() instanceof TradingPostConfirmSale tpcs) {
                tpcs.select(1);
            }
            return true;
        }
        if (buttonId == 81380) { // buy -1
            return true;
        }
        if (buttonId == 81381) { // buy +1
            return true;
        }
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

    public static void sendSellItemIndex(Item itemid, String itemname, String seller, String price, int idx, Player player) {
        if (idx > 20)
            return;
        var base = 81853;
        base += (6 * idx);
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemid, 0);
        player.getPacketSender().sendString(base + 1, Utils.capitalizeFirst(itemname));
        player.getPacketSender().sendString(base + 2, itemid == null ? "" : "Seller");
        player.getPacketSender().sendString(base + 3, itemid == null ? "" : Utils.capitalizeFirst(seller));
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

            if (!player.getInterfaceManager().isInterfaceOpen(OVERVIEW) && !player.getInterfaceManager().isInterfaceOpen(SELL_ID)) {
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

            Item offerItem = new Item(itemId, Math.min(player.inventory().count(itemId), (int) amount));

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
                    player.message("This free item cannot be sold.");
                    return false;
                }
                if (bankItem.getId() == itemId) {
                    player.message("This free item cannot be sold.");
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
            openSellUI(player);
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
                    showBuyTabOffers(player, null);
                    return;
                }
                player.message("<col=ff0000>0 Items found.. with the synx '" + itemName + "'");
                return;
            }
            player.lastTradingPostItemSearch = itemName;
            showBuyTabOffers(player, list);
            if (!refresh) {
                player.message("<col=ff0000>Found " + foundSize + " starting with the synx: '" + itemName + "'");
            }
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
                if (refresh) {
                    showBuyTabOffers(player, null);
                    return;
                }
                player.message("<col=ff0000>" + username + " doesn't have any items listed.");
                return;
            }
            player.lastTradingPostUserSearch = username;
            showBuyTabOffers(player, list);
            if (!refresh) {
                player.message("<col=ff0000>Displaying " + username + "'s " + foundSize + " trade post listings..");
            }
    }

    public static void showBuyTabOffers(Player player, List<TradingPostListing> saleMatches) {
        for (int i = 0; i < 10; i++) {
            var item = saleMatches == null ? null : i >= saleMatches.size() ? null : saleMatches.get(i);
            sendBuyIndex(item == null ? null : item.getSaleItem(),
                    item == null ? "" : item.getSellerName(),
                    item == null ? "" : Utils.formatNumber(item.getTotalAmount()),
                    i, player);
        }
    }

    public static void sendBuyIndex(Item itemname, String seller, String pricePer, int idx, Player player) {
        if (idx > 10)
            return;
        var base = 81288;
        base += (6 * idx);
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemname == null ? null : itemname.unnote(), 0);
        player.getPacketSender().sendString(base + 1, Utils.capitalizeFirst(itemname == null ? "None" : itemname.unnote().name()));
        player.getPacketSender().sendString(base + 2, itemname == null ? "" : "Seller");
        player.getPacketSender().sendString(base + 3, Utils.capitalizeFirst(seller));
        player.getPacketSender().sendString(base + 4, itemname == null ? "" : "Price");
        player.getPacketSender().sendString(base + 5, pricePer); // TODO convert to k, m, b
        player.getPacketSender().sendInterfaceDisplayState(81278 + idx, itemname == null); // TODO set not clickable instead of hiding entire widget, looks odd rn
        player.getPacketSender().sendParallelInterfaceVisibility(81278 + idx, itemname != null);
    }

    public static void handleXOptionInput(Player player, int id, int slot) {
            player.setAmountScript("How many of this item would you like to sell?", value -> {
                TradingPost.handleSellX(player, id, (Integer) value);
                return true;
            });
    }

    public static boolean handleBuyButtons(Player player, int index) {
            //System.out.println("buttonId=" + buttonId);

            if (!player.<Boolean>getAttribOr(USING_TRADING_POST, false))
                return false;

            if (!player.getInterfaceManager().isInterfaceOpen(BUY_ID) && !player.getInterfaceManager().isInterfaceOpen(OVERVIEW)) {
                player.debug("interface not open... " + player.getInterfaceManager().getMain());
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

            if (index >= offerSize) {
                player.message("<col=ff0000>No offer selected.");
                return true;
            }

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

            player.getPacketSender().sendItemOnInterfaceSlot(81383, selected.getSaleItem(), 0);
            player.getPacketSender().sendString(81384, Utils.capitalizeFirst(selected.getSaleItem().name()));
            player.getPacketSender().sendString(81385, "Price: "+selected.getPrice());
            player.getPacketSender().sendString(81386, "Total Cost: "+((long) selected.getPrice() * selected.getRemaining()));
            player.getPacketSender().sendString(81382, ""+selected.getRemaining());
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

        player.getPacketSender().sendItemOnInterfaceSlot(81383, selected.getSaleItem(), 0);
        player.getPacketSender().sendString(81384, Utils.capitalizeFirst(selected.getSaleItem().name()));
        player.getPacketSender().sendString(81385, "Price: "+selected.getPrice());
        player.getPacketSender().sendString(81386, "Total Cost: "+((long) selected.getPrice() * amount));
        player.getPacketSender().sendString(81382, ""+amount);
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
            open(player);
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
            sendOverviewTab(player);
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

    public static void resetSearchVars(Player player) {
        player.lastTradingPostItemSearch = null;
        player.lastTradingPostUserSearch = null;
    }
}

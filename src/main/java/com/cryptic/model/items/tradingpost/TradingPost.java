package com.cryptic.model.items.tradingpost;

import com.cryptic.GameConstants;
import com.cryptic.GameEngine;
import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.player.InputScript;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.World;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
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
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.extern.slf4j.Slf4j;
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
 * 95% rewrite for new UI
 * @author Jak Shadowrs tardisfan121@gmail.com
 */
@Slf4j
public class TradingPost extends PacketInteraction {

    @Override
    public void onLogin(Player player) {
        PlayerListing list = getListings(player.getUsername().toLowerCase());
        if (list != null) {
            list.getListedItems().forEach(l -> {
                if (l.profit > 0) {
                    TRADING_POST_COFFER.set(player, player.<Long>getAttribOr(TRADING_POST_COFFER, 0L) + l.profit);
                    player.message("%s coins were added to your Trading Post Coffer from successful sales.", l.profit);
                    l.resetProfit();
                }
            });
        }
    }

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

    public static final int OVERVIEW = 81050, HISTORY_ID = 81400, BUY_ID = 81250, SELL_ID = 81800, RECENT = 81600, BUY_CONFIRM_UI_ID = 81375;
    /**
     * username: data
     */

    public static Object2ObjectMap<String, PlayerListing> sales;

    /**
     * helpful for guide-price averaging
     */
    public static List<TradingPostListing> recentTransactions;

    public static List<String> featuredSpots; // key username

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Map<Integer, Integer> protection_prices;

    public static void init() {
        try {
            sales = new Object2ObjectOpenHashMap<>();
            recentTransactions = Lists.newArrayList();
            featuredSpots = Lists.newArrayList();
            protection_prices = Maps.newHashMap();
            File folder = new File("./data/saves/tradingpost/listings/");
            if (!folder.exists()) folder.mkdirs();
            for (File f : Objects.requireNonNull(folder.listFiles())) {
                try {
                    String name = FilenameUtils.removeExtension(f.getName());
                    Type type = new TypeToken<PlayerListing>() {
                    }.getType();
                    PlayerListing listings = gson.fromJson(new FileReader(f), type);
                    sales.put(name.toLowerCase(), listings);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            logger.info("Loaded TradingPost " + sales.size() + " Sale Listings loaded");
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

        Collections.reverse(recentTransactions);

        List<TradingPostListing> prices = Lists.newArrayList();
        for (TradingPostListing listing : transactions) {
            if (listing != null) {

                Item protItem = new Item(itemId);

                if (protItem.noted()) {
                    protItem.setId(protItem.unnote().getId());
                }

                if (listing.getSaleItem().getId() == protItem.getId()) {
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
                final List<Object2ObjectMap.Entry<String, PlayerListing>> objects = sales.object2ObjectEntrySet().stream().filter(entry -> entry.getValue() == listing).toList();
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
                final List<Object2ObjectMap.Entry<String, PlayerListing>> objects = sales.object2ObjectEntrySet().stream().toList();
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
                try (FileWriter fw = new FileWriter("./data/saves/tradingpost/featured.json")) {
                    gson.toJson(featuredSpots, fw);
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

                List<TradingPostListing> sales = new Gson().fromJson(new FileReader("./data/saves/tradingpost/recentTransactions.json"),
                    new TypeToken<List<TradingPostListing>>() {
                }.getType());
                if (sales != null) {
                    recentTransactions.addAll(sales);
                    //System.out.println("recent sales info = " + recentTransactions.size());
                }

                List<String> featured = new Gson().fromJson(new FileReader("./data/saves/tradingpost/featured.json"), new TypeToken<List<String>>() {
                }.getType());
                if (featured != null) {
                    featuredSpots.addAll(featured);
                    //System.out.println("featured = " + featured.size());
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
        player.putAttrib(AttributeKey.USING_TRADING_POST, true);
        if (!isValid(player)) { // first time, init a new listing
            PlayerListing listings = new PlayerListing();
            sales.put(player.getUsername().toLowerCase(), listings);
            save(listings);
        }
        player.tpClickedFeaturedSpotIdx = -1;
        sendOverviewTab(player);
    }

    public static void sendOfferInventory(Player player, int main) {
        player.getInterfaceManager().openInventory(main, InterfaceConstants.REMOVE_INVENTORY_ITEM - 1);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.REMOVE_INVENTORY_ITEM, player.inventory().toArray());
    }

    public static void sendOverviewTab(Player player) {
        sendOfferInventory(player, OVERVIEW);
        refreshOverview(player);
    }

    public static void refreshOverview(Player player) {
        String user = player.getUsername().toLowerCase();
        final var c = getListings(user);
        List<TradingPostListing> list = c.getListedItems();
        String s = "%s. Inv: %s".formatted(
                Utils.formatPriceKMB(player.<Long>getAttribOr(TRADING_POST_COFFER, 0L)),
                Utils.formatPriceKMB((long) player.inventory().count(995) + (1000L * player.inventory().count(13307)) + (1000L * player.inventory().count(PLATINUM_TOKEN)))
        );

        ObjectList<Player.TextData> strings = ObjectList.of(
            new Player.TextData(s, 81073),
            new Player.TextData("Active: " + Utils.formatPriceKMB(list == null ? 0 : list.size()), 81074),
            new Player.TextData(Utils.formatPriceKMB(sales.size()), 81075)
        );
        for (int i = 0; i < 10; i++) {
            var item = i >= (list != null ? list.size() : 0) ? null : list.get(i);
            sendOverviewIndex(item == null ? null : item.getSaleItem(),
                item == null ? "" : item.getSaleItem().unnote().name(),
                item == null ? "" : "%s/%s | %s (ea)"
                    .formatted(item.getAmountSold(), item.getTotalAmount(), Utils.formatValueCommas(item.getPrice())),
                item == null ? -1 : (int) (item.amountSold * 100 / (double) item.getTotalAmount()),
                i,
                player);
        }
        player.getPacketSender().sendMultipleStrings(strings);
        for (int i = 0; i < 5; i++) {
            var username = i >= featuredSpots.size() ? null : featuredSpots.get(i);
            featureSpotText(player,
                    username == null ? "Click to purchase" : "Featured:",
                    username == null ? "a featured spot" : Utils.capitalizeFirst(username)+"'s shop",
                    i);
        }
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

    public static void showRecents(Player p) {
        ObjectList<TradingPostListing> list = new ObjectArrayList<>();
        for (var sale : sales.values()) {
            if (sale == null) continue;
            var items = sale.getListedItems();
            if (items == null) continue;
            for (var item : items) {
                if (item == null) continue;
                logger.info("Item: %s seller: %s buyer:%s listed at: %s %d".formatted(item.getSaleItem().unnote().name(), item.getSellerName(), item.getLastBuyerName(), item.getListedTime(), item.getTimeListed()));
                Item i = item.getSaleItem().unnote();
                if (i == null) continue;
                if (i.name().equalsIgnoreCase(i.name())) { // no fucking idea what this check is about but we'll leave it
                    list.add(item);
                }
            }
        }
        list.sort(Comparator.comparingLong(TradingPostListing::getTimeListed).reversed());
        showRecents(p, list);
        p.getInterfaceManager().open(RECENT);
    }

    public static void showRecents(Player p, List<TradingPostListing> recentTransactions) {
        for (int i = 0; i < 20; i++) {
            var tpl = i >= recentTransactions.size() ? null : recentTransactions.get(i);
            TradingPost.sendRecentListingIndex(tpl == null ? null : tpl.getSaleItem().unnote(),
                tpl == null ? "" : tpl.getSellerName(),
                tpl == null ? "" : "%s | %s (ea)".formatted(Utils.formatPriceKMB(tpl.getTotalAmount()), Utils.formatPriceKMB(tpl.getPrice())),
                i,
                p);
        }
    }

    enum Kys {
        EXIT(0, p -> p.getInterfaceManager().close()),
        OVERVIEW(1, p -> {
            TradingPost.open(p);
            p.getPacketSender().sendConfig(1406, 0);
        }),
        BUY(2, p -> {
            TradingPost.openBuyUI(p);
            p.getPacketSender().sendConfig(1406, 1);
        }),
        SELL(3, p -> {
            TradingPost.openSellUI(p);
            p.getPacketSender().sendConfig(1406, 2);
        }),
        TRADE_HISTORY(4, p -> {
            var l = new ArrayList<>(recentTransactions);
            showTradeHistory(p, l);
            p.getPacketSender().sendConfig(1406, 3);
        }),
        RECENT_LISTINGS(5, p -> {
            TradingPost.showRecents(p);
            p.getPacketSender().sendConfig(1406, 4);
        });

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
        setSellUIText(p, null, "", "", "", "", "", "");
        for (int i = 0; i < 5; i++) sendSellItemIndex(null, "", "", "", i, p);
        sendOfferInventory(p, SELL_ID);
        p.tradingPostListedItemId = -1;
        p.tradingPostListedAmount = 0;
    }

    public static void setSellUIText(Player p, Item item, String itemname, String currentSales, String marketPrice, String avgSellTime, String pricePer, String quantity) {
        p.getPacketSender().sendItemOnInterfaceSlot(81819, item, 0);
        ObjectList<Player.TextData> list = ObjectList.of(
            new Player.TextData(itemname, 81820),
            new Player.TextData(currentSales, 81822),
            new Player.TextData(avgSellTime, 81824),
            new Player.TextData(avgSellTime, 81824),
            new Player.TextData(marketPrice, 81826),
            new Player.TextData(quantity, 81828),
            new Player.TextData(pricePer, 81830)
        );
        p.getPacketSender().sendMultipleStrings(list);
    }

    public static void openBuyUI(Player player) {
        String s = "%s. Inv: %s".formatted(
            Utils.formatPriceKMB(player.<Long>getAttribOr(TRADING_POST_COFFER, 0L)),
            Utils.formatPriceKMB((long) player.inventory().count(995) + (1000L * player.inventory().count(13307)) + (1000L * player.inventory().count(PLATINUM_TOKEN))));
        ObjectList<Player.TextData> list = ObjectList.of(
            new Player.TextData("My coins",81269),
            new Player.TextData(s,81271),
            new Player.TextData("127k",81272),
            new Player.TextData("Type username",81273),
            new Player.TextData("Type itemname",81274)
        );
        player.getPacketSender().sendMultipleStrings(list);
        for (int i = 0; i < 10; i++) sendBuyIndex(null, "", "", i, player);
        player.getInterfaceManager().open(BUY_ID);
    }

    static final int[] BASE_TAB_BUTTONS = new int[]{81053, 81253, 81803, 81403, 81603};

    public static boolean handleButtons(Player p, int buttonId) {
        if (!p.<Boolean>getAttribOr(USING_TRADING_POST, false))
            return false;

        for (int base : BASE_TAB_BUTTONS) {
            if (buttonId >= base && buttonId <= base + 6) {
                for (Kys value : Kys.values()) {
                    var delta = buttonId - base;
                    if (delta == value.delta) {
                        logger.debug("holy fuck found {} by {} on base {}", value.name(), buttonId, base);
                        value.open(p);
                        return true;
                    }
                }
            }
        }
        if (buttonId == 81069) {
            if (p.<Long>getAttribOr(TRADING_POST_COFFER, 0L) > 0) {
                new CofferChat().begin(p);
            } else {
                openCofferAddChat(p);
            }
            return true;
        }
        if (buttonId == 81077 || buttonId == 81078) {
            new FeatureSpot(0).begin(p);
            // feature spot 1
            return true;
        }
        if (buttonId == 81086 || buttonId == 81087) {
            new FeatureSpot(1).begin(p);
            // feature spot 2
            return true;
        }
        if (buttonId == 810807 || buttonId == 81081) {
            new FeatureSpot(2).begin(p);
            // feature spot 3
            return true;
        }
        if (buttonId == 81089 || buttonId == 81090) {
            new FeatureSpot(3).begin(p);
            // feature spot 4
            return true;
        }
        if (buttonId == 81083 || buttonId == 81084) {
            new FeatureSpot(4).begin(p);
            // feature spot 5
            return true;
        }
        if (buttonId == 81378) { // X button - buy specific item confirm overlay close
            p.getInterfaceManager().close(true, true);
            openBuyUI(p);
            return true;
        }

        // overview: remove listing red X button
        for (int i = 0; i < 10; i++) {
            var btn = 81127 + (i * 5);
            if (buttonId == btn) {
                TradingPost.claimOrCancel(p, i, 2);  // cancel listing
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
        if (buttonId == 81831) { // sell tab- quantity minus 1
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tradingPostListedAmount = Math.min(p.inventory().count(p.tradingPostListedItemId),
                Math.max(1, p.tradingPostListedAmount - 1));
            p.getPacketSender().sendString(81828, "" + p.tradingPostListedAmount); // quantity
            if (p.tradingPostListedAmount - 1 < p.inventory().count(p.tradingPostListedItemId))
                p.message("You can't list more than your are carry of this item.");
            return true;
        }
        if (buttonId == 81832) { // sell tab- quantity plus 1
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tradingPostListedAmount = Math.min(p.inventory().count(p.tradingPostListedItemId),
                Math.min(Integer.MAX_VALUE, 1 + p.tradingPostListedAmount));
            p.getPacketSender().sendString(81828, "" + p.tradingPostListedAmount); // quantity
            if (p.tradingPostListedAmount + 1 > p.inventory().count(p.tradingPostListedItemId))
                p.message("You can't list more than your are carry of this item.");
            return true;
        }
        if (buttonId == 81833) { //  sell tab- price minus 1
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tpListingPrice = Math.max(1, p.tpListingPrice - 1);
            p.getPacketSender().sendString(81830, Utils.formatValueCommas(p.tpListingPrice)); // price
            return true;
        }
        if (buttonId == 81840) { //  sell tab- restore default price whats default price code, its just uh bm=coins?, yes just unrenamed kk
            if (p.tradingPostListedItemId < 1)
                return true;
            var price = p.<Long>getAttribOr(AttributeKey.TRADING_POST_ORIGINAL_PRICE, (long)ItemDefinition.cached.get(p.tradingPostListedItemId).bm.value());
            p.tpListingPrice = (int) Math.min(Integer.MAX_VALUE, price);
            p.getPacketSender().sendString(81830, "" + price); // price
            return true;
        }
        if (buttonId == 81834) { //  sell tab- quantity +1 again
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tpListingPrice = Math.min(Integer.MAX_VALUE, 1 + p.tpListingPrice);
            p.getPacketSender().sendString(81830, Utils.formatValueCommas(p.tpListingPrice)); // quantity
            return true;
        }
        if (buttonId == 81836) { //  sell tab- quantity +10
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tradingPostListedAmount = Math.min(p.inventory().count(p.tradingPostListedItemId),
                Math.min(Integer.MAX_VALUE, p.tradingPostListedAmount + 10));
            p.getPacketSender().sendString(81828, "" + p.tradingPostListedAmount); // quantity
            if (p.tradingPostListedAmount + 10 > p.inventory().count(p.tradingPostListedItemId))
                p.message("You can't list more than your are carry of this item.");
            return true;
        }
        if (buttonId == 81837) { //  sell tab- quantity +100
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tradingPostListedAmount = Math.min(p.inventory().count(p.tradingPostListedItemId),
                Math.min(Integer.MAX_VALUE, p.tradingPostListedAmount + 100));
            p.getPacketSender().sendString(81828, "" + p.tradingPostListedAmount); // quantity
            if (p.tradingPostListedAmount + 100 > p.inventory().count(p.tradingPostListedItemId))
                p.message("You can't list more than your are carry of this item.");
            return true;
        }
        if (buttonId == 81838) { // sell tab- quantity custom enter
            if (p.tradingPostListedItemId < 1)
                return true;
            p.<Integer>setAmountScript("Enter amount to list:", (i) -> {
                p.tradingPostListedAmount = Math.min(p.inventory().count(p.tradingPostListedItemId),
                    Math.max(1, i));
                p.getPacketSender().sendString(81828, "" + p.tradingPostListedAmount); // price per item green text
                return true;
            });
            return true;
        }
        if (buttonId == 81839) { // sell tab- -5% price
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tpListingPrice = Math.max(1, (int) ((double)p.tpListingPrice * 0.95));
            p.getPacketSender().sendString(81830, Utils.formatValueCommas(p.tpListingPrice)); // price
            return true;
        }
        if (buttonId == 81842) { // sell tab- +5% price
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tpListingPrice = Math.max(1,  (int) ((double)p.tpListingPrice * 1.05));
            p.getPacketSender().sendString(81830, Utils.formatValueCommas(p.tpListingPrice)); // price
            return true;
        }
        if (buttonId == 81840) { // sell tab- guide price
            if (p.tradingPostListedItemId < 1)
                return true;
            p.tpListingPrice = new Item(p.tradingPostListedItemId).unnote().getBloodMoneyPrice().value();
            p.getPacketSender().sendString(81830, "" + p.tpListingPrice); // price per item green text
            return true;
        }
        if (buttonId == 81841) { // sell tab- price custom
            if (p.tradingPostListedItemId < 1)
                return true;
            p.<Integer>setAmountScript("Enter price to list the item for:", (i) -> {
                p.tpListingPrice = Math.max(1, i);
                p.getPacketSender().sendString(81830, Utils.formatValueCommas(p.tpListingPrice)); // price per item green text
                return true;
            });
            return true;
        }
        if (buttonId == 81843) { // sell tab- sell confirm 1st screen
            if (p.tradingPostListedItemId < 1 || p.tradingPostListedAmount < 1)
                return true;
            p.getDialogueManager().start(new TradingPostConfirmDialogue(new Item(
                p.tradingPostListedItemId,
                p.tradingPostListedAmount), p.tpListingPrice));
            return true;
        }


        if (buttonId == 81379) { // confirm buy on 2nd Buy Overlay UI
            if (p.getDialogueManager().getDialogue() instanceof TradingPostConfirmSale tpcs) {
                tpcs.select(1);
            } else {
                handlePurchasing(p, p.tradingPostSelectedListing, p.tradingPostListedAmount);
            }
            return true;
        }
        if (buttonId == 81380) { // buy -1
            p.tradingPostListedAmount = Math.min(p.tradingPostSelectedListing.getRemaining(), Math.max(1, p.tradingPostListedAmount - 1));
            p.getPacketSender().sendString(81382, "" + p.tradingPostListedAmount);
            p.getPacketSender().sendString(81386, "Total Cost: " + Utils.formatNumber(((long) p.tradingPostSelectedListing.getPrice() * p.tradingPostListedAmount)));
            return true;
        }
        if (buttonId == 81381) { // buy +1
            p.tradingPostListedAmount = Math.min(p.tradingPostSelectedListing.getRemaining(), (int) Math.min(Integer.MAX_VALUE, p.tradingPostListedAmount + 1L));
            if (p.tradingPostListedAmount == p.tradingPostSelectedListing.getRemaining())
                p.message("There are only %s remaining.", p.tradingPostSelectedListing.getRemaining());
            p.getPacketSender().sendString(81382, "" + p.tradingPostListedAmount);
            p.getPacketSender().sendString(81386, "Total Cost: " + Utils.formatNumber(((long) p.tradingPostSelectedListing.getPrice() * p.tradingPostListedAmount)));
            return true;
        }
        return false;
    }



    public static class FeatureSpot extends Dialogue {

        private final int idx;

        public FeatureSpot(int idx) {
            this.idx = idx;
        }

        @Override
        protected void start(Object... parameters) {
            player.tpClickedFeaturedSpotIdx = idx;
            if (idx < featuredSpots.size()) {

                var shopname = featuredSpots.get(idx);
                TradingPost.openBuyUI(player);
                player.getPacketSender().sendConfig(1406, 1);
                searchByUsername(player, shopname, false);
            } else {
                send(DialogueType.OPTION, "List your shop in a featured spot for 5M coins?", "Yes", "No");
            }
            setPhase(0);
        }

        @Override
        protected void select(int option) {
            if (isPhase(0)) {
                if (option == 1) {
                    if (player.tpClickedFeaturedSpotIdx != -1 && featuredSpots.size() < 5) {
                        if (featuredSpots.stream().anyMatch(e -> e.equalsIgnoreCase(player.getUsername()))) {
                            player.message("You already have a featured spot.");
                        }
                        else if (player.inventory().remove(995, 5_000_000)) {
                            featuredSpots.add(player.getUsername());
                            player.tpClickedFeaturedSpotIdx = -1;
                            player.message("Feature spot confirmed.");
                            TradingPost.open(player);
                        } else {
                            player.message("Not enough Coins.");
                        }
                    } else {
                        player.message("Featured spot taken.");
                    }
                }
                stop();
            }
        }
    }

    public static class CofferChat extends Dialogue {

        @Override
        protected void start(Object... parameters) {
            send(DialogueType.OPTION, "Edit Coffer", "Withdraw", "Add");
            setPhase(0);
        }

        @Override
        protected void select(int option) {
            if (isPhase(0)) {
                if (option == 1) {
                    stop();
                    player.<Integer>setAmountScript("Withdraw how many coins?", i -> {
                        if (!player.inventory().hasCapacity(new Item(995))) {
                            player.message("Your inventory is full.");
                            stop();
                            return true;
                        }
                        long current = player.<Long>getAttribOr(TRADING_POST_COFFER, 0L);
                        if (current == 0L)
                            return true;
                        long toAdd = Long.min(current, (long) Integer.MAX_VALUE - player.inventory().count(995));
                        if (player.inventory().add(995, (int) toAdd)) {
                            player.putAttrib(TRADING_POST_COFFER, Math.max(0, player.<Long>getAttribOr(TRADING_POST_COFFER, 0L) - toAdd));
                            player.message(Utils.formatNumber(toAdd)+" was removed from your coffer, it now holds "+Utils.formatNumber(player.<Long>getAttribOr(TRADING_POST_COFFER, 0L))+" gp.");
                            sendOverviewTab(player);
                        }
                        stop();
                        return true;
                    });
                } else if (option == 2) {
                    stop();
                    openCofferAddChat(player);
                }
            }
        }
    }

    private static void openCofferAddChat(Player p) {
        if (p.inventory().count(995) > 0) {
            p.<Integer>setAmountScript("Store how many coins?", i -> {
                int amt = Integer.min(i, p.inventory().count(995));
                long newAmt = p.<Long>getAttribOr(TRADING_POST_COFFER, 0L) + (long) amt;
                if (newAmt < 0 || newAmt > Long.MAX_VALUE) {
                    p.message("Your coffer cannot hold any more coins.");
                    return true;
                }
                if (p.inventory().remove(995, amt)) {
                    p.putAttrib(TRADING_POST_COFFER, newAmt);
                    p.message(Utils.formatNumber(amt)+" was added to your coffer, it now holds "+Utils.formatNumber(newAmt)+" gp.");
                    sendOverviewTab(p);
                }
                return true;
            });
        }
        else if (p.inventory().count(PLATINUM_TOKEN) > 0) {
            p.<Integer>setAmountScript("Store how many tokens?", i -> {
                int amt = Integer.min(i, p.inventory().count(PLATINUM_TOKEN));
                long newAmt = p.<Long>getAttribOr(TRADING_POST_COFFER, 0L) + (amt * 1000L);
                if (newAmt < 0 || newAmt > Long.MAX_VALUE) {
                    p.message("Your coffer cannot hold any more coins.");
                    return true;
                }
                if (p.inventory().remove(PLATINUM_TOKEN, amt)) {
                    p.putAttrib(TRADING_POST_COFFER, newAmt);
                    p.message(Utils.formatNumber(amt)+" was added to your coffer, it now holds "+Utils.formatNumber(newAmt)+" gp.");
                    sendOverviewTab(p);
                }
                return true;
            });
        }
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

    public static void showTradeHistory(Player player) {
        var l = new ArrayList<>(recentTransactions);
        Collections.reverse(l);
        showTradeHistory(player, l);
    }

    public static void showTradeHistory(Player player, List<TradingPostListing> list) {
        for (int i = 0; i < 20; i++) {
            if (i >= list.size()) {
                sendTradeHistoryIndex(null, "None", "", "", "", i, player); // blank
                continue;
            }
            var trade = list.get(i);
            if (trade.buyersInfo == null) {
                sendTradeHistoryIndex(null, "None", "", "", "", i, player); // blank
                continue;
            }
            sendTradeHistoryIndex(trade.getSaleItem(),
                trade.getSaleItem().name(),
                trade.getSellerName(),
                trade.buyersInfo.stream().findFirst().orElse("?"),
                Utils.formatPriceKMB(trade.getPrice()),
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
        if (idx > 20) return;
        var base = 81440;
        base += (6 * idx);
        ObjectList<Player.TextData> list = ObjectList.of(
            new Player.TextData(Utils.capitalizeFirst(itemname), base + 1),
            new Player.TextData(itemid == null ? "" : Utils.capitalizeFirst(seller) + " sold to", base + 2),
            new Player.TextData(Utils.capitalizeFirst(buyer), base + 3),
            new Player.TextData(itemid == null ? "" : "Price", base + 4),
            new Player.TextData(price, base + 5)
        );
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemid, 0);
        player.getPacketSender().sendMultipleStrings(list);
    }

    public static void sendSellItemIndex(Item itemid, String itemname, String seller, String price, int idx, Player player) {
        if (idx > 20)
            return;
        var base = 81853;
        base += (6 * idx);
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemid, 0);
        ObjectList<Player.TextData> list = ObjectList.of(
            new Player.TextData(Utils.capitalizeFirst(itemname), base + 1),
            new Player.TextData(itemid == null ? "" : "Seller", base + 2),
            new Player.TextData(itemid == null ? "" : Utils.capitalizeFirst(seller), base + 3),
            new Player.TextData(itemid == null ? "" : "Price", base + 4),
            new Player.TextData(price, base + 5)
        );
        player.getPacketSender().sendMultipleStrings(list);
    }

    /**
     * @param itemname
     * @param seller
     * @param pricePer format "10M | 10k (ea)"
     * @param idx
     * @param player
     */
    public static void sendRecentListingIndex(Item itemname, String seller, String pricePer, int idx, Player player) {
        if (idx > 20) return;
        var base = 81640;
        base += (6 * idx);
        ObjectList<Player.TextData> list = ObjectList.of(
            new Player.TextData(Utils.capitalizeFirst(itemname == null ? "None" : itemname.unnote().name()), base + 1),
            new Player.TextData(itemname == null ? "" : "Seller", base + 2),
            new Player.TextData(Utils.capitalizeFirst(seller), base + 3),
            new Player.TextData(itemname == null ? "" : "Price", base + 4),
            new Player.TextData(pricePer, base + 5)
        );
        //System.out.println("sending: " + itemname + " at base: " + base);
        player.getPacketSender().sendMultipleStrings(list);
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemname == null ? null : itemname.unnote(), 0);
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

        if (!offerItem.rawtradable() || !offerItem.unnote().rawtradable()) {
            player.message("<col=ff0000>You can't offer this item.");
            return false;
        }

        //Pker accounts can't offer free items.
        if (Arrays.stream(GameConstants.DONATOR_ITEMS).anyMatch(donator_item -> donator_item == itemId)) {
            player.message("<col=ff0000>You can't offer this item.");
            return false;
        }

        if (offerItem.getValue() <= 0) {
            player.message("You can't sell spawnable items.");
            return false;
        }

        // Dont allow illegal items to inserted into a trading post.
        if (Arrays.stream(ILLEGAL_ITEMS).anyMatch(id -> id == offerItem.getId() || id == offerItem.unnote().getId())) {
            player.message("You can't sell illegal items.");
            return false;
        }

        //System.out.println("unnoted id: "+offerItem.unnote().getId()+" match "+ Arrays.toString(currentListings.stream().map(cl -> cl.getSaleItem().unnote().getId()).toArray()));

        if (currentListings.stream().anyMatch(cl -> cl.getSaleItem().unnote() == offerItem.unnote())) {
            player.message("<col=ff0000>You already have a listing of this item. You cannot list it again..");
            player.message("<col=ff0000>.. You will need to edit ur current listing and change quantity.");
            return false;
        }

        if (!currentListings.isEmpty()) {
            player.message("<col=ff0000>You already have a listing of this item. You cannot list it again..");
            player.message("<col=ff0000>.. You will need to edit ur current listing and change quantity.");
            return false;
        }

        int foundAmount = player.inventory().count(itemId);

        if (amount > foundAmount)
            amount = foundAmount;

        player.tradingPostListedItemId = itemId;
        player.tradingPostListedAmount = (int) amount;//no longer needs to be a long due to it being item Amount
        player.tpListingPrice = offerItem.unnote().getBloodMoneyPrice().value();

        setSellUIText(player, offerItem.unnote(),
            offerItem.unnote().name(),
            "",
            "",
            "",
            Utils.formatValueCommas(offerItem.unnote().getBloodMoneyPrice().value()) + "",
            amount + "");
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
            logger.info("player: {} sale: {} price: {}", player.getUsername(), sale.getId(), price);
            return;
        }

        TradingPostListing tpl = new TradingPostListing(player.getUsername().toLowerCase(), sale, price);

        PlayerListing listing = sales.getOrDefault(player.getUsername().toLowerCase(), getListings(player.getUsername().toLowerCase()));

        if (!isValid(player)) {
            player.message("Invalid listing. Please try again.");
            return;
        }

        if (!player.inventory().contains(sale.getId(), sale.getAmount())) {
            logger.info("player: {} sale: {} price: {}", player.getUsername(), sale.getId(), price);
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
                    player.message("You've successfully listed your offer to the " + GameServer.getServerType().getName() + " marketplace!");
                    refreshOverview(player);
                    openSellUI(player);
                    player.tradingPostListedItemId = -1;
                    player.tradingPostListedAmount = -1;
                }
            }
        }
        if (player.tpClickedFeaturedSpotIdx != -1) {
            // when open?
        }
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

        var sumForSale = saleMatches == null ? 0L : saleMatches.stream().map(e -> (long) e.getRemaining()).reduce(0L, (subtotal, element) -> subtotal + element);
        player.getPacketSender().sendString(81272, Utils.formatPriceKMB(sumForSale));
        for (int i = 0; i < 10; i++) {
            var item = saleMatches == null ? null : i >= saleMatches.size() ? null : saleMatches.get(i);
            sendBuyIndex(item == null ? null : item.getSaleItem(),
                item == null ? "" : item.getSellerName(),
                item == null ? "" : Utils.formatPriceKMB(item.getPrice()),
                i, player);
        }
    }

    public static void sendBuyIndex(Item itemname, String seller, String pricePer, int idx, Player player) {
        if (idx > 10)
            return;
        var base = 81288;
        base += (6 * idx);
        player.getPacketSender().sendItemOnInterfaceSlot(base, itemname == null ? null : itemname.unnote(), 0);
        ObjectList<Player.TextData> list = ObjectList.of(
            new Player.TextData(Utils.capitalizeFirst(itemname == null ? "None" : itemname.unnote().name()), base + 1),
            new Player.TextData(itemname == null ? "" : "Seller", base + 2),
            new Player.TextData(Utils.capitalizeFirst(seller), base + 3),
            new Player.TextData(itemname == null ? "" : "Price", base + 4),
            new Player.TextData(pricePer, base + 5)
        );
        player.getPacketSender().sendMultipleStrings(list);
        player.getPacketSender().setInterClickable(81278 + idx, itemname != null);
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

        List<TradingPostListing> list2;
        if (player.lastTradingPostUserSearch != null && !player.lastTradingPostUserSearch.isEmpty()) {
            list2 = getSalesByUsername(Utils.capitalizeFirst(player.lastTradingPostUserSearch).toLowerCase());
        } else {
            list2 = getSalesForItemName(player, player.lastTradingPostItemSearch);
        }
        if (list2 == null) {
            player.message("<col=ff0000>What are you searching for?");
            return true;
        }

        List<TradingPostListing> listDisplay = new ArrayList<>(list2);
        listDisplay.removeIf(o -> o.getRemaining() == 0);

        /* To sort from highest to lowest. **/
        listDisplay.sort(Comparator.comparingLong(TradingPostListing::getPrice));

        player.tempList = listDisplay;

        List<TradingPostListing> offer = player.tempList;

        if (offer == null) {
            player.message("<col=ff0000>That offer no longer exists.");
            return true;
        }

        int offerSize = offer.size();

        if (index >= offerSize) {
            player.message("<col=ff0000>No offer selected.");
            return true;
        }

        TradingPostListing selected = offer.get(index);

        return showBuyConfirmUI(player, selected);
    }

    public static boolean showBuyConfirmUI(Player player, TradingPostListing selected) {

        if (selected.getRemaining() == 0) {
            player.message("<col=ff0000>This offer has already been purchased by another player.");
            return true;
        }

        player.putAttrib(AttributeKey.TRADING_POST_ORIGINAL_AMOUNT, selected.getRemaining());
        player.putAttrib(AttributeKey.TRADING_POST_ORIGINAL_PRICE, (long) selected.getPrice());
        player.tradingPostListedAmount = selected.getRemaining();
        player.tradingPostSelectedListing = selected;

        player.getInterfaceManager().open(BUY_CONFIRM_UI_ID);
        player.getPacketSender().resetParallelInterfaces();
        player.getPacketSender().sendParallelInterfaceVisibility(BUY_ID, true);
        player.getPacketSender().sendItemOnInterfaceSlot(81383, selected.getSaleItem().unnote(), 0);
        ObjectList<Player.TextData> list = ObjectList.of(
                new Player.TextData(Utils.capitalizeFirst(selected.getSaleItem().unnote().name()), 81384),
                new Player.TextData("Price: " +  Utils.formatValueCommas(selected.getPrice()), 81385),
                new Player.TextData("Total Cost: " + Utils.formatValueCommas(((long) selected.getPrice() * selected.getRemaining())), 81386),
                new Player.TextData("" + selected.getRemaining(), 81382)
        );
        player.getPacketSender().sendMultipleStrings(list);

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
        ObjectList<Player.TextData> list = ObjectList.of(
            new Player.TextData(Utils.capitalizeFirst(selected.getSaleItem().name()), 81384),
            new Player.TextData("Price: " + Utils.formatValueCommas(selected.getPrice()), 81385),
            new Player.TextData("Total Cost: " + Utils.formatValueCommas(((long) selected.getPrice() * amount)), 81386),
            new Player.TextData("" + amount, 81382)
        );
        player.getPacketSender().sendItemOnInterfaceSlot(81383, selected.getSaleItem(), 0);
        player.getPacketSender().sendMultipleStrings(list);
    }

    public static void finishPurchase(Player player, TradingPostListing selected, long totalPrice, int amount, boolean noted) {
        long currency = player.inventory().count( COINS_995);

        long tokens = player.inventory().count(PLATINUM_TOKEN);

        long totalPriceInPlat = tokens * 1_000;

        long totalAmount = currency + totalPriceInPlat;//price

        //System.out.println("Enough=" + (totalPrice > totalAmount) + " coins=" + coins + " platTokens=" + platTokens + " totalPriceInPlat=" + totalPriceInPlat);
        if (totalPrice > totalAmount) {
            player.message("You don't have enough <col=ff0000>coins</col> to complete this transaction...");
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
            player.inventory().remove(COINS_995, coinsToRemove);
        }

        if (platTokensToRemove > 0) {
            player.inventory().remove(PLATINUM_TOKEN, platTokensToRemove);
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
            var p2 = sel.get();
            if (selected.profit > 0) {
                TRADING_POST_COFFER.set(p2, p2.<Long>getAttribOr(TRADING_POST_COFFER, 0L) + selected.profit);
                p2.message("%s coins were added to your Trading Post Coffer from successful sales.", selected.profit);
                selected.resetProfit();
            }
            if (p2.getInterfaceManager().getMain() == OVERVIEW) // only refresh if open otherwise it'd interrupt our other work
                sendOverviewTab(p2);
            p2.tradePostHistory.add(selected);
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
        if (itemName == null) return null;
        ObjectList<TradingPostListing> items = new ObjectArrayList<>();
        for (var sale : sales.values()) {
            if (sale == null) continue;
            items.addAll(sale.getSalesMatchingByString(player, itemName));
        }
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

        long profit = offer.profit;

        if (profit > Integer.MAX_VALUE) {
            var profitInPlatTokens = profit / 1000;
            var remainingCoins = profit - profitInPlatTokens * 1000;
            p.inventory().addOrBank(new Item( PLATINUM_TOKEN, (int) profitInPlatTokens));
            tradingPostLogs.log(TRADING_POST, p.getUsername() + " offer claimed for: " + offer.getSaleItem().unnote().name() + " Received=" + (int) profitInPlatTokens + " bloody tokens");

            if (remainingCoins >= 1) {
                p.inventory().addOrBank(new Item( COINS_995, (int) remainingCoins));
                tradingPostLogs.log(TRADING_POST, p.getUsername() + " offer claimed for: " + offer.getSaleItem().unnote().name() + " Received=" + (int) remainingCoins + " coins");
            }
        } else {
            if (profit > 0) {
                p.inventory().addOrBank(new Item( COINS_995, (int) profit));
                tradingPostLogs.log(TRADING_POST, p.getUsername() + " offer claimed for: " + offer.getSaleItem().unnote().name() + " Received=" + (int) profit + " coins");
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

    public static void claimOrCancel(Player player, int listIndex, int optionId) {

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
                player.message("Your sale of " + offerItem.unnote().name() + " was complete.");
                handleClaimOffer(player, listIndex);
                return;
            }

            /*
             * Cancel Listing
             */

            int remaining = offer.getRemaining();

            final Item refund = new Item(offerItem.getId(), remaining);

            long unclaimedProfit = offer.profit;

            int inventoryAmount = player.inventory().getAmountOf(COINS_995);

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
                int refundId = isOver ?  PLATINUM_TOKEN :  COINS_995;
                Item item = new Item(refundId, isOver ? (int) (unclaimedProfit / 1_000) : (int) unclaimedProfit);
                player.inventory().addOrBank(item);
                tradingPostLogs.log(TRADING_POST, player.getUsername() + " After canceling the offer there was already some unclaimed profits for: " + refund.unnote().name() + " Received: " + item.getAmount() + " coins!");
                player.message("<col=ff0000" + Utils.formatNumber(unclaimedProfit) + " coins from sales was added to your inventory.");
            }

            sendOverviewTab(player);
            save(listing);
        }
    }

    public static void refreshListing(Player player) {
        if (player.lastTradingPostUserSearch != null) {
            searchByUsername(player, player.lastTradingPostUserSearch, true);
        } else if (player.lastTradingPostItemSearch != null) {
            searchByItemName(player, player.lastTradingPostItemSearch, true);
        }
    }

    public static void resetSearchVars(Player player) {
        player.lastTradingPostItemSearch = null;
        player.lastTradingPostUserSearch = null;
    }
}

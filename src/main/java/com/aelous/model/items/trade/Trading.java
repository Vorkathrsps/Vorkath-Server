package com.aelous.model.items.trade;

import com.aelous.GameServer;
import com.aelous.GameConstants;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.PlayerStatus;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.ItemContainer.StackPolicy;
import com.aelous.model.items.container.inventory.Inventory;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.SecondsTimer;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aelous.utility.CustomItemIdentifiers.ESCAPE_KEY;

/**
 * Handles the entire trading system.
 * Should be dupe-free.
 *
 * @author Swiffy
 */
public class Trading {

    private static final Logger logger = LogManager.getLogger(Trading.class);
    private static final Logger tradeLogs = LogManager.getLogger("TradeLogs");
    private static final Level TRADE;

    static {
        TRADE = Level.getLevel("TRADE");
    }

    //Interface data
    private static final int FIRST_TRADE_INTERFACE = 52000;
    public static final int CONTAINER_INTERFACE_ID = 52015;
    private static final int CONTAINER_INTERFACE_ID_2 = 52016;
    private static final int CONFIRM_TRADE_INTERFACE = 52250;

    //Frames data
    private static final int TRADING_WITH_FRAME = 52002;
    private static final int TRADING_WITH_FRAME_2 = 52259;
    private static final int ROW_1 = 52007;
    private static final int ROW_2 = 52008;
    private static final int ROW_3 = 52009;
    private static final int VALUE_1 = 52255;
    private static final int VALUE_2 = 52257;
    private static final int STATUS_FRAME_1 = 52010;
    private static final int STATUS_FRAME_2 = 52252;
    private static final int ITEM_LIST_1_FRAME = 52260;
    private static final int ITEM_LIST_2_FRAME = 52290;
    private static final int ITEM_VALUE_1_FRAME = 52004;
    private static final int ITEM_VALUE_2_FRAME = 52006;

    //Nonstatic
    private final Player player;
    private final ItemContainer container;
    private Player interact;
    private TradeState state = TradeState.NONE;

    //Delays!!
    private final SecondsTimer button_delay = new SecondsTimer();
    private final SecondsTimer request_delay = new SecondsTimer();

    //The possible states during a trade
    private enum TradeState {
        NONE,
        REQUESTED_TRADE,
        TRADE_SCREEN,
        ACCEPTED_TRADE_SCREEN,
        CONFIRM_SCREEN,
        ACCEPTED_CONFIRM_SCREEN
    }

    //Constructor
    public Trading(Player player) {
        this.player = player;

        //The container which will hold all our offered items.
        this.container = new ItemContainer(28, StackPolicy.STANDARD) {

            public void onRefresh() {
                player.getInterfaceManager().openInventory(FIRST_TRADE_INTERFACE, InterfaceConstants.REMOVE_INVENTORY_ITEM - 1);
                player.getPacketSender().sendItemOnInterface(InterfaceConstants.ADD_INVENTORY_ITEM, container.toArray());
                player.getPacketSender().sendItemOnInterface(InterfaceConstants.REMOVE_INVENTORY_ITEM, player.inventory().toArray());
                player.getPacketSender().sendItemOnInterface(CONTAINER_INTERFACE_ID, player.getTrading().getContainer().toArray());
                player.getPacketSender().sendItemOnInterface(CONTAINER_INTERFACE_ID_2, interact.getTrading().getContainer().toArray());
                interact.getPacketSender().sendItemOnInterface(CONTAINER_INTERFACE_ID_2, player.getTrading().getContainer().toArray());
            }
        };
    }

    public void requestTrade(Player otherPlayer) {
        if (player.getUsername().equalsIgnoreCase("Box test")) {
            player.message("This account can't trade other players.");
            return;
        }

        //Check to see if the player is special teleblocked
        if (player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
            player.teleblockMessage();
            return;
        }

        if (player.inventory().contains(ESCAPE_KEY) && WildernessArea.inWild(player)) {
            player.message(Color.RED.wrap("You can't send trade requests when holding the Escape key."));
            return;
        }

        // Ironman? fuck off lol!!
        if (player.getIronManStatus() != IronMode.NONE && (otherPlayer == null || !(otherPlayer.getPlayerRights().isOwner(otherPlayer) || otherPlayer.getIronManStatus().isGroupIronman()))) {
            player.message("You are an Iron Man. You stand alone.");
            return;
        }

        // Ironman? fuck off lol!!
        if (otherPlayer.getIronManStatus() != IronMode.NONE && !(player.getPlayerRights().isOwner(player) || player.getIronManStatus().isGroupIronman())) {
            player.message(otherPlayer.getUsername() + " is an Iron Man. They stand alone.");
            return;
        }

        // Starter trade prevention
       /* if (player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L) < 3000 && !player.getPlayerRights().isDeveloperOrGreater(player) && !otherPlayer.getPlayerRights().isDeveloperOrGreater(otherPlayer)) {
            player.message("You are restricted from trading until 30 minutes of play time. Only " +Math.ceil((int)(3000.0 - player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L)) / 100.0)+" minutes left.");
            return;
        }

        if (otherPlayer.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L) < 3000 && !otherPlayer.getPlayerRights().isDeveloperOrGreater(otherPlayer) && !player.getPlayerRights().isDeveloperOrGreater(player)) {
            player.message("Your partner is restricted from trading until 30 minutes of play time. Only "+Math.ceil((int)(3000.0 - otherPlayer.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L)) / 100.0)+" minutes left.");
            return;
        }*/

        if (player.jailed() || otherPlayer.jailed() && !otherPlayer.getPlayerRights().isStaffMember(otherPlayer) && !player.getPlayerRights().isStaffMember(player)) {
            player.message("You cannot trade when jailed.");
            return;
        }

        // Make sure you're not trading yourself.
        if (player == otherPlayer || player.getIndex() == otherPlayer.getIndex()) {
            return;
        }

        if (state == TradeState.NONE || state == TradeState.REQUESTED_TRADE) {

            if (player.getDueling().inDuel()) {
                player.message("You cannot trade during a duel!");
                return;
            }

            //Make sure to not allow flooding!
            if (request_delay.active()) {
                int seconds = request_delay.secondsRemaining();
                player.message("You must wait another " + (seconds == 1 ? "second" : "" + seconds + " seconds") + " before sending more trade requests.");
                return;
            }

            //The other players' current trade state.
            final TradeState t_state = otherPlayer.getTrading().getState();

            //Should we initiate the trade or simply send a request?
            boolean initiateTrade = false;

            //Update this instance...
            this.setInteract(otherPlayer);
            this.setState(TradeState.REQUESTED_TRADE);

            //Check if target requested a trade with us...
            if (t_state == TradeState.REQUESTED_TRADE) {
                if (otherPlayer.getTrading().getInteract() != null &&
                    otherPlayer.getTrading().getInteract() == player) {
                    initiateTrade = true;
                }
            }

            //Initiate trade for both players with eachother?
            if (initiateTrade) {
                player.getTrading().initiateTrade();
                otherPlayer.getTrading().initiateTrade();
            } else {
                player.message("You've sent a trade request to " + otherPlayer.getUsername() + ".");
                otherPlayer.getPacketSender().sendMessage(player.getUsername() + ":tradereq:");
            }

            //Set the request delay to 2 seconds at least.
            request_delay.start(2);
        } else {
            player.message("You cannot do that right now.");
        }
    }

    public void initiateTrade() {
        if (player.locked() || player.hp() < 1 || interact.locked() || interact.hp() < 1 || player.dead() || interact.dead())
            return;

        //Update statuses
        player.setStatus(PlayerStatus.TRADING);
        player.getTrading().setState(TradeState.TRADE_SCREEN);
        player.getTrading().container.onRefresh();

        //Update strings on interface
        player.getPacketSender().sendString(TRADING_WITH_FRAME, "Trading with: " + interact.getUsername());
        player.getPacketSender().sendString(STATUS_FRAME_1, "").sendString(STATUS_FRAME_2, "Are you sure you want to make this trade?").sendString(ITEM_VALUE_1_FRAME, "(Value: <col=ffffff>0</col> blood money)").sendString(ITEM_VALUE_2_FRAME, "(Value: <col=ffffff>0</col> blood money)");
        player.getPacketSender().sendString(ROW_1, interact.getUsername() + " has " + interact.inventory().getFreeSlots());
        player.getPacketSender().sendString(ROW_2, "free inventory");
        player.getPacketSender().sendString(ROW_3, "slots.");
        player.getPacketSender().sendString(52005, interact.getUsername() + " offers:");
        interact.getPacketSender().sendString(52005, player.getUsername() + " offers:");

        //Reset container
        container.clear(true);
    }

    /**
     * Aborts a trade, giving the other player a message that the trade has cancelled by this user.
     */
    public void abortTrading() {
        if (state != TradeState.NONE) {

            //Cache the current interact
            final Player partner = interact;

            //Return all items...
            for (Item t : container.toNonNullArray()) {
                player.inventory().addAll(t.clone());
            }
            tradeLogs.log(TRADE, "Player " + player.getUsername() + " aborted the trade returning items: " + Arrays.toString(container.toNonNullArray()));
            Utils.sendDiscordInfoLog("Player " + player.getUsername() + " aborted the trade returning items: " + Arrays.toString(container.toNonNullArray()), "trade");

            //Refresh inventory
            player.inventory().refresh();

            //Reset all attributes...
            resetAttributes();

            //Send decline message
            player.message("Trade declined.");
            player.getInterfaceManager().close(true, true);

            //Reset trade for other player as well (the cached interact)
            if (partner != null) {
                if (partner.getStatus() == PlayerStatus.TRADING) {
                    if (partner.getTrading().getInteract() != null && partner.getTrading().getInteract() == player) {
                        partner.getInterfaceManager().close(true, true);
                    }
                }
            }
        }
    }

    public void acceptTrade() {
        // Security
        if(interact != null && state != null && (!player.getInterfaceManager().isInterfaceOpen(FIRST_TRADE_INTERFACE) && !interact.getInterfaceManager().isInterfaceOpen(FIRST_TRADE_INTERFACE) && state == TradeState.TRADE_SCREEN)) {
            return;
        }

        // Security
        if(interact != null && state != null && (!player.getInterfaceManager().isInterfaceOpen(CONFIRM_TRADE_INTERFACE) && !interact.getInterfaceManager().isInterfaceOpen(CONFIRM_TRADE_INTERFACE) && state == TradeState.CONFIRM_SCREEN)) {
            return;
        }

        //Validate this trade action..
        if (!validate(player, interact, TradeState.TRADE_SCREEN, TradeState.ACCEPTED_TRADE_SCREEN, TradeState.CONFIRM_SCREEN, TradeState.ACCEPTED_CONFIRM_SCREEN)) {
            return;
        }

        //Check button delay...
        if (button_delay.active()) {
            return;
        }

        //Cache the interact...
        final Player partner = interact;

        //Interact's current trade state.
        final TradeState t_state = partner.getTrading().getState();

        if (player.inventory().isFull()) {
            player.getPacketSender().sendMessage("Trade cannot be accepted, you don't have enough free inventory space.");
            return;
        }

        //Check which action to take..
        if (state == TradeState.TRADE_SCREEN) {

            //Verify that the interact can receive all items first..
            int slotsNeeded = 0;
            for (Item t : container.toNonNullArray()) {
                slotsNeeded += t.stackable() && interact.getInventory().contains(t.getId()) ? 0 : 1;
            }

            int freeSlots = interact.getInventory().getFreeSlots();
            if (slotsNeeded > freeSlots) {
                player.message("Trade cannot be accepted, " + partner.getUsername() + " doesn't have enough free inventory space.");
                return;
            }

            //Both are in the same state. Do the first-stage accept.
            setState(TradeState.ACCEPTED_TRADE_SCREEN);

            //Update status...
            player.getPacketSender().sendString(STATUS_FRAME_1, "Waiting for other player..");
            partner.getPacketSender().sendString(STATUS_FRAME_1, "" + player.getUsername() + " has accepted.");

            //Check if both have accepted..
            if (state == TradeState.ACCEPTED_TRADE_SCREEN &&
                t_state == TradeState.ACCEPTED_TRADE_SCREEN) {

                //Technically here, both have accepted.
                //Go into confirm screen!
                player.getTrading().confirmScreen();
                partner.getTrading().confirmScreen();
            }
        } else if (state == TradeState.CONFIRM_SCREEN) {

            //Both are in the same state. Do the second-stage accept.
            setState(TradeState.ACCEPTED_CONFIRM_SCREEN);

            //Update status...
            player.getPacketSender().sendString(STATUS_FRAME_2, "Waiting for " + partner.getUsername() + "'s confirmation..");
            partner.getPacketSender().sendString(STATUS_FRAME_2, "" + player.getUsername() + " has accepted. Do you wish to do the same?");

            //Check if both have accepted..
            if (state == TradeState.ACCEPTED_CONFIRM_SCREEN &&
                t_state == TradeState.ACCEPTED_CONFIRM_SCREEN) {
                StringBuilder playerItems = new StringBuilder();
                StringBuilder interactItems = new StringBuilder();
                //Give items to both players...
                for (Item item : partner.getTrading().getContainer().toNonNullArray()) {
                    player.getInventory().add(item);
                    playerItems.append(Utils.insertCommasToNumber(String.valueOf(item.getAmount()))).append(" ").append(item.unnote().name()).append(" (id ").append(item.getId()).append("), ");
                }
                for (Item item : player.getTrading().getContainer().toNonNullArray()) {
                    partner.getInventory().add(item);
                    interactItems.append(Utils.insertCommasToNumber(String.valueOf(item.getAmount()))).append(" ").append(item.unnote().name()).append(" (id ").append(item.getId()).append("), ");
                }
                try {
                    tradeLogs.log(TRADE, "Player " + player.getUsername() + " gave " + interactItems + " in a trade to " + partner.getUsername());
                    Utils.sendDiscordInfoLog("Player " + player.getUsername() + " gave " + interactItems + " in a trade to " + partner.getUsername(), "trade");
                    tradeLogs.log(TRADE, "Player " + partner.getUsername() + " gave " + playerItems + " in a trade to " + player.getUsername());
                    Utils.sendDiscordInfoLog("Player " + partner.getUsername() + " gave " + playerItems + " in a trade to " + player.getUsername(), "trade");
                    long plr_value = player.getTrading().getContainer().containerValue();
                    long other_plr_value = interact.getTrading().getContainer().containerValue();
                    long difference;
                    difference = (plr_value > other_plr_value) ? plr_value - other_plr_value : other_plr_value - plr_value;
                    tradeLogs.log(TRADE, "Player " + player.getUsername() + " (lvl " + player.getSkills().combatLevel() + ") and " + partner.getUsername() + " (lvl " + partner.getSkills().combatLevel() + ") trade value difference of " + Utils.insertCommasToNumber(String.valueOf(difference)) + " blood money.");
                    Utils.sendDiscordInfoLog("Player " + player.getUsername() + " (lvl " + player.getSkills().combatLevel() + ") and " + partner.getUsername() + " (lvl " + partner.getSkills().combatLevel() + ") trade value difference of " + Utils.insertCommasToNumber(String.valueOf(difference)) + " blood money.", "trade");
                    if (difference > 1_000_000) {
                        tradeLogs.warn("Player " + player.getUsername() + " has traded with Player " + partner.getUsername() + " with a value difference of greater than 1.000.000 blood money, this was possibly RWT.");
                        Utils.sendDiscordInfoLog(GameServer.properties().discordNotifyId + " Player " + player.getUsername() + " has traded with Player " + partner.getUsername() + " with a value difference of greater than 1.000.000 blood money, this was possibly RWT.", "trade");
                    }
                } catch (Exception e) {
                    //The value shouldn't ever really be a string, but just in case, let's catch the exception.
                    logger.catching(e);
                    logger.error("Somehow there was an exception from logging trading between player " + player.getUsername() + " and " + partner.getUsername());
                }

                //Reset attributes for both players...
                player.getTrading().resetAttributes();
                partner.getTrading().resetAttributes();

                //Send interface removal for both players...
                player.getInterfaceManager().close();
                partner.getInterfaceManager().close();

                //Send successful trade message!
                player.message("Trade accepted!");
                partner.getPacketSender().sendMessage("Trade accepted!");
                player.getRisk().update();
                partner.getRisk().update();
            }
        }
        button_delay.start(1);
    }

    private void confirmScreen() {

        //Update state
        player.getTrading().setState(TradeState.CONFIRM_SCREEN);

        //Send new interface
        player.getInterfaceManager().openInventory(CONFIRM_TRADE_INTERFACE, InterfaceConstants.REMOVE_INVENTORY_ITEM - 1);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.REMOVE_INVENTORY_ITEM, player.inventory().toArray());
        String plr_value = "" + container.containerValue();
        String other_plr_value = "" + interact.getTrading().getContainer().containerValue();
        player.getPacketSender().sendString(TRADING_WITH_FRAME_2, interact.getUsername());
        interact.getPacketSender().sendString(TRADING_WITH_FRAME_2, player.getUsername());
        player.getPacketSender().sendString(VALUE_1, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(plr_value) + "</col> blood money)");
        player.getPacketSender().sendString(VALUE_2, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(other_plr_value) + "</col> blood money)");
        interact.getPacketSender().sendString(VALUE_1, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(other_plr_value) + "</col> blood money)");
        interact.getPacketSender().sendString(VALUE_2, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(plr_value) + "</col> blood money)");
        //Clear all interface frames to remove text of previously traded items.
        player.getPacketSender().sendString(ITEM_LIST_1_FRAME, "--clearall--"); // custom supported by client to clear all
        //Send new interface frames
        String this_items = listItems(container);
        String interact_item = listItems(interact.getTrading().getContainer());
        player.getPacketSender().sendString(ITEM_LIST_1_FRAME, this_items);
        player.getPacketSender().sendString(ITEM_LIST_2_FRAME, interact_item);
    }

    //Deposit or withdraw an item....
    public void handleItem(int id, int amount, int slot, ItemContainer from, ItemContainer to) {
        // Security
        if (!player.getInterfaceManager().isInterfaceOpen(FIRST_TRADE_INTERFACE) && !interact.getInterfaceManager().isInterfaceOpen(FIRST_TRADE_INTERFACE)) {
            return;
        }

        //Validate this trade action..
        if (!validate(player, interact, TradeState.TRADE_SCREEN, TradeState.ACCEPTED_TRADE_SCREEN)) {
            return;
        }
        Item tradeItem = new Item(id, amount);
        if (!tradeItem.rawtradable()) {
            player.message("You cannot trade that item.");
            return;
        }

        for (Item bankItem : GameConstants.BANK_ITEMS) {
            if (bankItem.note().getId() == tradeItem.getId()) {
                player.message("You can't trade this item.");
                return;
            }
            if (bankItem.getId() == tradeItem.getId()) {
                player.message("You can't trade this item.");
                return;
            }
        }

/*        if (tradeItem.unnote().definition(World.getWorld()).pvpAllowed) {
            player.message("You can't trade spawnable items.");
            return;
        }*/

/*        if (tradeItem.getValue() <= 0) {
            player.message("You can't trade spawnable items.");
            return;
        }*/

        //Check if the trade was previously accepted (and now modified)...
        boolean modified = false;
        if (state == TradeState.ACCEPTED_TRADE_SCREEN) {
            state = TradeState.TRADE_SCREEN;
            modified = true;
        }
        if (interact.getTrading().getState() == TradeState.ACCEPTED_TRADE_SCREEN) {
            interact.getTrading().setState(TradeState.TRADE_SCREEN);
            modified = true;
        }
        if (modified) {
            player.getPacketSender().sendString(STATUS_FRAME_1, "<col=ca0d0d>TRADE MODIFIED!");
            interact.getPacketSender().sendString(STATUS_FRAME_1, "<col=ca0d0d>TRADE MODIFIED!");
        }

        //Handle the item switch..
        if (state == TradeState.TRADE_SCREEN && interact.getTrading().getState() == TradeState.TRADE_SCREEN) {
            if (from.getItems()[slot] == null)
                return;
            //Check if the item is in the right place
            if (from.getItems()[slot].getId() == id) {
                //Let's not modify the amount.
                //amount = from.getAmount(id);

                //Make sure we can fit that amount in the trade
                if (from instanceof Inventory) {
                    if (!tradeItem.stackable()) {
                        if (amount > container.getFreeSlots()) {
                            amount = container.getFreeSlots();
                        }
                    }
                }

                if (amount <= 0) {
                    return;
                }

                if (amount > from.count(id)) {
                    amount = from.count(id);
                }

                final Item item = new Item(id, amount);

                //Do the switch!
                if (item.getAmount() == 1) {
                    from.remove(item, slot, true, false);
                } else {
                    from.remove(item, true);
                }
                to.add(item, true);

                //Update value frames for both players
                String plr_value = "" + container.containerValue();
                String other_plr_value = "" + interact.getTrading().getContainer().containerValue();
                player.getPacketSender().sendString(ITEM_VALUE_1_FRAME, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(plr_value) + "</col> blood money)");
                player.getPacketSender().sendString(ITEM_VALUE_2_FRAME, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(other_plr_value) + "</col> blood money)");
                interact.getPacketSender().sendString(ITEM_VALUE_1_FRAME, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(other_plr_value) + "</col> blood money)");
                interact.getPacketSender().sendString(ITEM_VALUE_2_FRAME, "(Value: <col=ffffff>" + Utils.insertCommasToNumber(plr_value) + "</col> blood money)");
                player.getPacketSender().sendString(ROW_1, interact.getUsername() + " has " + interact.inventory().getFreeSlots());
                player.getPacketSender().sendString(ROW_2, "free inventory");
                player.getPacketSender().sendString(ROW_3, "slots.");
                player.getPacketSender().sendString(ROW_1, interact.getUsername() + " has " + interact.inventory().getFreeSlots());
                interact.getPacketSender().sendString(ROW_1, player.getUsername() + " has " + player.inventory().getFreeSlots());
                interact.getPacketSender().sendString(ROW_2, "free inventory");
                interact.getPacketSender().sendString(ROW_3, "slots.");
                container.onRefresh();
            }
        } else {
            player.getInterfaceManager().close();
        }
    }

    public void resetAttributes() {
        //Reset trade attributes
        setInteract(null);
        setState(TradeState.NONE);

        //Reset player status if it's trading.
        if (player.getStatus() == PlayerStatus.TRADING) {
            player.setStatus(PlayerStatus.NONE);
        }

        //Reset container..
        container.clear(true);

        //Send the new empty container to the interface
        //Just to clear the items there.
        player.getPacketSender().sendItemOnInterface(CONTAINER_INTERFACE_ID, container.toArray());
    }

    public static String listItems(ItemContainer items) {
        StringBuilder string = new StringBuilder();
        int item_counter = 0;
        List<Item> list = new ArrayList<>();
        loop1:
        for (Item item : items.toNonNullArray()) {
            //Make sure the item isn't already in the list.
            for (Item item_ : list) {
                if (item_.getId() == item.getId()) {
                    continue loop1;
                }
            }
            list.add(new Item(item.getId(), items.count(item.getId())));
        }
        for (Item item : list) {
            if (item_counter > 0) {
                string.append("<br>");
            }
            string.append(item.unnote().name().replaceAll("_", " "));

            String amt = "" + Utils.format(item.getAmount());
            if (item.getAmount() >= 1000000000) {
                amt = "<col=65280>" + (item.getAmount() / 1000000000) + " billion <col=ffffff>(" + Utils.format(item.getAmount()) + ")";
            } else if (item.getAmount() >= 1000000) {
                amt = "<col=65280>" + (item.getAmount() / 1000000) + " million <col=ffffff>(" + Utils.format(item.getAmount()) + ")";
            } else if (item.getAmount() >= 1000) {
                amt = "<col=65535>" + (item.getAmount() / 1000) + "K <col=ffffff>(" + Utils.format(item.getAmount()) + ")";
            }
            string.append(" x <col=ca0d0d>").append(amt);

            item_counter++;
        }
        if (item_counter == 0) {
            string = new StringBuilder("Absolutely nothing!");
        }
        return string.toString();
    }

    /**
     * Validates a player. Basically checks that all specified params add up.
     */
    private static boolean validate(Player player, Player interact, TradeState... tradeState) {
        //Verify player...
        if (player == null || interact == null) {
            return false;
        }

        //Make sure we have proper status
        if (player.getStatus() != PlayerStatus.TRADING) {
            return false;
        }

        //Make sure we're interacting with each other
        if (interact.getStatus() != PlayerStatus.TRADING) {
            return false;
        }

        if (player.getTrading().getInteract() == null
            || player.getTrading().getInteract() != interact) {
            return false;
        }
        if (interact.getTrading().getInteract() == null
            || interact.getTrading().getInteract() != player) {
            return false;
        }

        //Make sure we have proper trade state.
        boolean found = false;
        for (TradeState duelState : tradeState) {
            if (player.getTrading().getState() == duelState) {
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }

        //Do the same for our interact
        found = false;
        for (TradeState duelState : tradeState) {
            if (interact.getTrading().getState() == duelState) {
                found = true;
                break;
            }
        }
        return found;
    }

    public TradeState getState() {
        return state;
    }

    public void setState(TradeState state) {
        this.state = state;
    }

    public SecondsTimer getButtonDelay() {
        return button_delay;
    }

    public Player getInteract() {
        return interact;
    }

    public void setInteract(Player interact) {
        this.interact = interact;
    }

    public ItemContainer getContainer() {
        return container;
    }
}

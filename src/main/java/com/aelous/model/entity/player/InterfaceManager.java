package com.aelous.model.entity.player;

import com.aelous.GameConstants;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.items.container.shop.Shop;
import com.aelous.utility.Utils;

import static com.aelous.model.entity.attributes.AttributeKey.PICKING_PVM_STARTER_WEAPON;
import static com.aelous.model.entity.attributes.AttributeKey.PICKING_PVP_STARTER_WEAPON;

/**
 * Contains information about the state of interfaces enter in the client.
 */
public class InterfaceManager {

    /**
     * The player instance.
     */
    private final Player player;

    /**
     * The current main interface.
     */
    private int main = -1;

    /**
     * The current overlay interface.
     */
    private int overlay = -1;

    /**
     * The current walkable-interface.
     */
    private int walkable = -1;

    private final int[] sidebars = new int[15];

    /**
     * Creates a new <code>InterfaceManager<code>.
     */
    InterfaceManager(Player player) {
        this.player = player;
    }

    /**
     * Opens an interface for the player.
     */
    public void open(int identification) {
        open(identification, true);
    }

    /**
     * Opens an interface for the player.
     */
    public void open(int identification, boolean secure) {
        if (secure) {

            if (main == identification) {
                return;
            }

            if (player.getDialogueManager().isActive()) {
                player.getDialogueManager().interrupt();
            }
        }
        player.unlock();
        main = identification;
        player.getMovementQueue().clear();
        player.getPacketSender().sendInterface(identification);
        setSidebar(GameConstants.LOGOUT_TAB, -1);
        int slayerRewardPoints = player.getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
        player.getPacketSender().sendString(64014, "Reward Points: " + Utils.formatNumber(slayerRewardPoints));
    }

    /**
     * Opens a walkable-interface for the player.
     */
    public void sendOverlay(int identification) {
        if (walkable == identification) {
            return;
        }
        walkable = identification;
        player.getPacketSender().sendWalkableInterface(identification);
    }

    public void removeOverlay() {
        sendOverlay(-1);
    }

    /**
     * Opens an inventory interface for the player.
     */
    public void openInventory(int identification, int overlay) {
        if (main == identification && this.overlay == overlay) {
            return;
        }

        main = identification;
        this.overlay = overlay;
        player.getMovementQueue().clear();
        player.getPacketSender().sendInterfaceSet(identification, overlay);
        setSidebar(GameConstants.LOGOUT_TAB, -1);
    }

    /**
     * Clears the player's screen.
     */
    public void close() {
        close(player.getInterfaceManager().getWalkable() <= 0, true);
    }

    public void close(boolean walkable) {
        close(walkable, true);
    }

    public void closeDialogue() {
        player.clearAttrib(PICKING_PVM_STARTER_WEAPON);
        player.clearAttrib(PICKING_PVP_STARTER_WEAPON);
        player.clearAttrib(AttributeKey.DIALOGUE_PHASE);
        player.getDialogueManager().interrupt();
        player.getPacketSender().closeDialogue();
    }

    /**
     * Handles clearing the screen.
     */
    public void close(boolean walkable, boolean closeDialogue) {
        if (player.hasAttrib(AttributeKey.SHOP)) {
            Shop.closeShop(player);
        }

        if (player.hasAttrib(AttributeKey.BANKING)) {
            player.getBank().close();
        }

        if (player.hasAttrib(AttributeKey.PRICE_CHECKING)) {
            player.getPriceChecker().close();
        }

        if (player.getStatus() == PlayerStatus.TRADING) {
            player.getTrading().abortTrading();
        }

        if (player.getStatus() == PlayerStatus.DUELING) {
            if (!player.getDueling().inDuel()) {
                player.getDueling().closeDuel();
            }
        }

        if (player.getBankPin().isEnteringPin()) {
            player.getBankPin().getPinInterface().close();
        }

        player.getBankPin().clearPinInterface();

        if (walkable) {
            sendOverlay(-1);
        }

        clean(walkable);

        if (player.getBankPin().isEnteringPin()) {
            player.getBankPin().getPinInterface().close();
        }

        //Also close rune pouch
        player.getRunePouch().close();
        player.putAttrib(AttributeKey.USING_TRADING_POST,false);
        player.lastTradingPostItemSearch = null;
        player.lastTradingPostUserSearch = null;
        if(player.getStatus() != PlayerStatus.GAMBLING) {
            player.setStatus(PlayerStatus.NONE);
        }
        player.removeInputScript();
        player.setDestroyItem(-1);
        if (closeDialogue)
            closeDialogue();
        player.getBankPin().clearPinInterface();
        player.getPacketSender().sendInterfaceRemoval();
        setSidebar(GameConstants.LOGOUT_TAB, 2449);
    }

    public void setSidebar(int tab, int id) {
        if (sidebars[tab] == id && id != -1) {
            return;
        }
        sidebars[tab] = id;
        player.getPacketSender().sendTabInterface(tab, id);
    }

    /**
     * Cleans the interfaces.
     */
    private void clean(boolean walkableFlag) {
        main = -1;
        overlay = -1;//for tp
        if (walkableFlag) {
            walkable = -1;
        }
    }

    /**
     * Checks if a certain interface is enter.
     */
    public boolean isInterfaceOpen(int id) {
        return main == id;
    }

    /**
     * Checks if the player's screen is clear.
     */
    public boolean isClear() {
        return main == -1 && walkable == -1;
    }

    /**
     * Checks if the main interface is clear.
     */
    public boolean isMainClear() {
        return main == -1;
    }

    /**
     * Sets the current interface.
     */
    public void setMain(int currentInterface) {
        this.main = currentInterface;
    }

    /**
     * gets the current main interface.
     */
    public int getMain() {
        return main;
    }

    /**
     * Gets the walkable interface.
     */
    public int getWalkable() {
        return walkable;
    }

    /**
     * Sets the walkable interface.
     */
    public void setWalkable(int walkableInterface) {
        this.walkable = walkableInterface;
    }

    public int getSidebar(int tab) {
        if (tab > sidebars.length) {
            return -1;
        }
        return sidebars[tab];
    }

    public boolean isSidebar(int tab, int id) {
        return tab <= sidebars.length && sidebars[tab] == id;
    }

    public boolean hasSidebar(int id) {
        for (int sidebar : sidebars) {
            if (sidebar == id) {
                return true;
            }
        }
        return false;
    }

    public void clearAllSidebars() {
        for (int i = 0; i < GameConstants.SIDEBAR_INTERFACE.length; i++) {
            int tab = GameConstants.SIDEBAR_INTERFACE[i][0];
            player.getInterfaceManager().setSidebar(tab, -1);
        }
    }

}

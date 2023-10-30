package com.cryptic.model.items.container.shop;

import com.cryptic.utility.ItemIdentifiers;

/**
 * This class represents all of the utility methods and variables for the shop
 * system.
 * 
 * @author Patrick van Elderen
 * @date 17-09-2019
 * @version 1.0
 *
 */
public class ShopUtility extends ItemIdentifiers {

    /**
     * The max amount of items a shop can have.
     */
    public static final int MAX_SHOP_ITEMS = 200;

    /**
     * The shop interface id.
     */
    public static final int INTERFACE_ID = 73150;

    /**
     * The starting interface child id of items.
     */
    public static final int ITEM_CHILD_ID = 73190;

    /**
     * The starting slayer buy interface child id of items.
     */
    public static final int SLAYER_BUY_ITEM_CHILD_ID = 64016;

    /**
     * The interface child id of the shop's name.
     */
    public static final int NAME_INTERFACE_CHILD_ID = 3901;

    /**
     * The scrollbar interface id
     */
    public static final int SCROLL_BAR_INTERFACE_ID = 73190;
    
    public static final int AMOUNT_STRING_ID = 22996;

    public static final int SLAYER_BUY_AMOUNT_STRING_ID = 64017;

    public static final int SHOP_INTERFACE = 73150;

    public static final int SLAYER_SHOP_INTERFACE = 64000;

    /**
     * Modern Shops
     */
    public static int SHOP_CONFIG_FRAME_ID = 1206;
    public static int GUNJORN_WEAPON_SHOP_ID = 33;
    public static int AUBURYS_MAGIC_SHOP_ID = 23;
    public static int HORVIKS_ARMOR_SHOP_ID = 32;
    public static int LOWES_ARCHERY_SHOP_ID = 35;
    public static int DONATOR_STORE_ID = 44;
    public static int KAQEMEEX_POTIONS_SHOP_ID = 36;
}

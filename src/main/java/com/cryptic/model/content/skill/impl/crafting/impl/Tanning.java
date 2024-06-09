package com.cryptic.model.content.skill.impl.crafting.impl;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.InputScript;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin
 * juni 16, 2020
 */
public class Tanning {

    /**
     * The tan data.
     */
    public enum TanData {
        LEATHER(new int[]{ComponentID.LEATHER_MAKE_ONE, ComponentID.LEATHER_MAKE_FIVE, ComponentID.LEATHER_MAKE_X, ComponentID.LEATHER_MAKE_ALL}, COWHIDE, ItemIdentifiers.LEATHER, 1),
        HARD_LEATHER(new int[]{ComponentID.HARD_LEATHER_MAKE_ONE, ComponentID.HARD_LEATHER_MAKE_FIVE, ComponentID.HARD_LEATHER_MAKE_X, ComponentID.HARD_LEATHER_MAKE_ALL}, COWHIDE, ItemIdentifiers.HARD_LEATHER, 3),
        SNAKE_HIDE(new int[]{ComponentID.SNAKEHIDE_MAKE_ONE, ComponentID.SNAKEHIDE_MAKE_FIVE, ComponentID.SNAKEHIDE_MAKE_X, ComponentID.SNAKEHIDE_MAKE_ALL}, ItemIdentifiers.SNAKE_HIDE, ItemIdentifiers.SNAKE_HIDE, 15),
        SNAKESKIN(new int[]{ComponentID.SNAKESKIN_MAKE_ONE, ComponentID.SNAKESKIN_MAKE_FIVE, ComponentID.SNAKESKIN_MAKE_X, ComponentID.SNAKESKIN_MAKE_ALL}, ItemIdentifiers.SNAKE_HIDE, ItemIdentifiers.SNAKESKIN, 20),
        GREEN_LEATHER(new int[]{ComponentID.GREEN_DHIDE_MAKE_ONE, ComponentID.GREEN_DHIDE_MAKE_FIVE, ComponentID.GREEN_DHIDE_MAKE_X, ComponentID.GREEN_DHIDE_MAKE_ALL}, GREEN_DRAGONHIDE, GREEN_DRAGON_LEATHER, 20),
        BLUE_LEATHER(new int[]{ComponentID.BLUE_DHIDE_MAKE_ONE, ComponentID.BLUE_DHIDE_MAKE_FIVE, ComponentID.BLUE_DHIDE_MAKE_X, ComponentID.BLUE_DHIDE_MAKE_ALL}, BLUE_DRAGONHIDE, BLUE_DRAGON_LEATHER, 20),
        RED_LEATHER(new int[]{ComponentID.RED_DHIDE_MAKE_ONE, ComponentID.RED_DHIDE_MAKE_FIVE, ComponentID.RED_DHIDE_MAKE_X, ComponentID.RED_DHIDE_MAKE_ALL}, RED_DRAGONHIDE, RED_DRAGON_LEATHER, 20),
        BLACK_LEATHER(new int[]{ComponentID.BLACK_DHIDE_MAKE_ONE, ComponentID.BLACK_DHIDE_MAKE_FIVE, ComponentID.BLACK_DHIDE_MAKE_X, ComponentID.BLACK_DHIDE_MAKE_ALL}, BLACK_DRAGONHIDE, BLACK_DRAGON_LEATHER, 20);

        /**
         * Button Linked To Data
         */
        public final int[] button;

        /**
         * The ingredient item.
         */
        public final int ingredient;

        /**
         * The product item
         */
        public final int product;

        /**
         * The tan cost.
         */
        public final int cost;

        /**
         * Constructs a new <code>TanData</code>.
         *
         * @param ingredient The ingredient item.
         * @param product    The product item.
         * @param cost       The tan cost.
         */
        TanData(int[] button, int ingredient, int product, int cost) {
            this.button = button;
            this.ingredient = ingredient;
            this.product = product;
            this.cost = cost;
        }

        public static TanData of(int button) {
            for (var data : values()) {
                if (ArrayUtils.contains(data.button, button)) return data;
            }
            return null;
        }
    }

    /**
     * Handles opening the tanning itemcontainer.
     *
     * @param player The player instance.
     */
    public static void open(Player player) {
        int count = 0;
        for (TanData data : TanData.values()) {
            player.getPacketSender().sendInterfaceModel(14769 + count, 250, data.ingredient);
            player.getPacketSender().sendString(14777 + count, (player.inventory().contains(data.ingredient) ? "<col=23db44>" : "<col=e0061c>") + Utils.formatEnum(data.name()));
            player.getPacketSender().sendString(14785 + count, "<col=23db44>FREE");
            count++;
        }

        player.getInterfaceManager().open(14670);
    }

    /**
     * Tans the leather.
     *
     * @param player The player instance.
     * @param amount The amount being tanned.
     * @param data   The tan data.
     */
    public static void tan(Player player, int amount, TanData data) {
        if (!player.inventory().contains(data.ingredient)) {
            ItemDefinition def = ItemDefinition.getInstance(data.ingredient);
            player.message("You do not have any " + def.name + " to do this.");
            return;
        }

        int contain = player.inventory().count(data.ingredient);

        System.out.println(amount);

        if (amount > contain)
            amount = contain;

        System.out.println(amount + " contain=" + contain);

        player.inventory().remove(data.ingredient, amount);
        player.inventory().add(data.product, amount);
        ItemDefinition def = ItemDefinition.getInstance(data.ingredient);
        player.message("You successfully tan all the " + def.name);
    }

    /**
     * Handles clicking the tan buttons on the itemcontainer.
     *
     * @param player The player instance.
     * @param button The button identification.
     * @return If a button was clicked.
     */
    public static boolean click(Player player, int button) {
        switch (button) {
            /** Leather */
            case 14817:
                tan(player, 1, TanData.LEATHER);
                return true;
            case 14809:
                tan(player, 5, TanData.LEATHER);
                return true;
            case 14801:
                player.setAmountScript("How many leathers would you like to tan?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.LEATHER);
                        return true;
                    }
                });
                return true;
            case 14793:
                tan(player, 28, TanData.LEATHER);
                return true;

            /** Hard leather */
            case 14818:
                tan(player, 1, TanData.HARD_LEATHER);
                return true;
            case 14810:
                tan(player, 5, TanData.HARD_LEATHER);
                return true;
            case 14802:
                player.setAmountScript("How many hard leathers would you like to tan?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.HARD_LEATHER);
                        return true;
                    }
                });
                return true;
            case 14794:
                tan(player, 28, TanData.HARD_LEATHER);
                return true;

            /** Snake hide */
            case 14819:
                tan(player, 1, TanData.SNAKE_HIDE);
                return true;
            case 14811:
                tan(player, 5, TanData.SNAKE_HIDE);
                return true;
            case 14803:
                player.setAmountScript("Enter amount:", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.SNAKE_HIDE);
                        return true;
                    }
                });
                return true;
            case 14795:
                tan(player, 28, TanData.SNAKE_HIDE);
                return true;

            /** Snakeskin */
            case 14820:
                tan(player, 1, TanData.SNAKESKIN);
                return true;
            case 14812:
                tan(player, 5, TanData.SNAKESKIN);
                return true;
            case 14804:
                player.setAmountScript("Enter amount:", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.SNAKESKIN);
                        return true;
                    }
                });
                return true;
            case 14796:
                tan(player, 28, TanData.SNAKESKIN);
                return true;

            /** Green leather */
            case 14821:
                tan(player, 1, TanData.GREEN_LEATHER);
                return true;
            case 14813:
                tan(player, 5, TanData.GREEN_LEATHER);
                return true;
            case 14805:
                player.setAmountScript("Enter amount:", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.GREEN_LEATHER);
                        return true;
                    }
                });
                return true;
            case 14797:
                tan(player, 28, TanData.GREEN_LEATHER);
                return true;

            /** Blue leather */
            case 14822:
                tan(player, 1, TanData.BLUE_LEATHER);
                return true;
            case 14814:
                tan(player, 5, TanData.BLUE_LEATHER);
                return true;
            case 14806:
                player.setAmountScript("Enter amount:", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.BLUE_LEATHER);
                        return true;
                    }
                });
                return true;
            case 14798:
                tan(player, 28, TanData.BLUE_LEATHER);
                return true;

            /** Red leather */
            case 14823:
                tan(player, 1, TanData.RED_LEATHER);
                return true;
            case 14815:
                tan(player, 5, TanData.RED_LEATHER);
                return true;
            case 14807:
                player.setAmountScript("Enter amount:", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.RED_LEATHER);
                        return true;
                    }
                });
                return true;
            case 14799:
                tan(player, 28, TanData.RED_LEATHER);
                return true;

            /** Black leather */
            case 14824:
                tan(player, 1, TanData.BLACK_LEATHER);
                return true;
            case 14816:
                tan(player, 5, TanData.BLACK_LEATHER);
                return true;
            case 14808:
                player.setAmountScript("Enter amount:", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        tan(player, (Integer) value, TanData.BLACK_LEATHER);
                        return true;
                    }
                });
                return true;
            case 14800:
                tan(player, 28, TanData.BLACK_LEATHER);
                return true;
        }
        return false;
    }


}

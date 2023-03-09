package com.aelous.model.content.skill.impl.crafting.impl;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.Optional;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 16, 2020
 */
public final class Jewellery {

    /**
     * The jewellery type.
     */
    public enum JewelleryType {
        RING(4233),
        NECKLACE(4239),
        AMULET(4245);

        /**
         * The itemcontainer identification.
         */
        public final int identification;

        /**
         * Constructs a new <code>JewelleryType</code>.
         *
         * @param identification The itemcontainer identification.
         */
        JewelleryType(int identification) {
            this.identification = identification;
        }
    }

    /**
     * The jewellery data.
     */
    public enum JewelleryData {
        GOLD_RING(ItemIdentifiers.GOLD_RING, 5, 15, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR)),
        SAPPHIRE_RING(ItemIdentifiers.SAPPHIRE_RING, 20, 40, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR), new Item(SAPPHIRE)),
        EMERALD_RING(ItemIdentifiers.EMERALD_RING, 27, 55, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR), new Item(EMERALD)),
        RUBY_RING(ItemIdentifiers.RUBY_RING, 34, 70, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR), new Item(RUBY)),
        DIAMOND_RING(ItemIdentifiers.DIAMOND_RING, 43, 85, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR), new Item(DIAMOND)),
        DRAGONSTONE_RING(ItemIdentifiers.DRAGONSTONE_RING, 55, 100, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR), new Item(DRAGONSTONE)),
        ONYX_RING(ItemIdentifiers.ONYX_RING, 67, 85, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR), new Item(ONYX)),
        ZENYTE_RING(ItemIdentifiers.ZENYTE_RING, 89, 150, JewelleryType.RING, new Item(RING_MOULD), new Item(GOLD_BAR), new Item(ZENYTE)),

        GOLD_NECKLACE(ItemIdentifiers.GOLD_NECKLACE, 6, 20, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR)),
        SAPPHIRE_NECKLACE(ItemIdentifiers.SAPPHIRE_NECKLACE, 22, 55, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(SAPPHIRE)),
        EMERALD_NECKLACE(ItemIdentifiers.EMERALD_NECKLACE, 29, 60, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(EMERALD)),
        RUBY_NECKLACE(ItemIdentifiers.RUBY_NECKLACE, 40, 75, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(RUBY)),
        DIAMOND_NECKLACE(ItemIdentifiers.DIAMOND_NECKLACE, 56, 90, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(DIAMOND)),
        DRAGONSTONE_NECKLACE(DRAGON_NECKLACE, 72, 105, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(DRAGONSTONE)),
        ONYX_NECKLACE(ItemIdentifiers.ONYX_NECKLACE, 82, 120, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(ONYX)),
        ZENYTE_NECKLACE(ItemIdentifiers.ZENYTE_NECKLACE, 92, 165, JewelleryType.NECKLACE, new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(ZENYTE)),

        GOLD_AMULET(ItemIdentifiers.GOLD_AMULET, 8, 30, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR)),
        SAPPHIRE_AMULET(ItemIdentifiers.SAPPHIRE_AMULET, 24, 65, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(SAPPHIRE)),
        EMERALD_AMULET(ItemIdentifiers.EMERALD_AMULET, 31, 61, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(EMERALD)),
        RUBY_AMULET(ItemIdentifiers.RUBY_AMULET, 50, 85, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(RUBY)),
        DIAMOND_AMULET(ItemIdentifiers.DIAMOND_AMULET, 70, 100, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(DIAMOND)),
        DRAGONSTONE_AMULET(ItemIdentifiers.DRAGONSTONE_AMULET, 80, 125, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(DRAGONSTONE)),
        ONYX_AMULET(ItemIdentifiers.ONYX_AMULET, 90, 150, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(ONYX)),
        ZENYTE_AMULET(ItemIdentifiers.ZENYTE_AMULET, 98, 4, JewelleryType.AMULET, new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(ZENYTE));

        /**
         * The production item.
         */
        public int product;

        /**
         * The level required.
         */
        public int level;

        /**
         * The experience rewarded.
         */
        public double experience;

        /**
         * The jewellery type.
         */
        public JewelleryType type;

        /**
         * The materials required.
         */
        public Item[] materials;

        /**
         * Constructs a new <code>JewelleryData</code>.
         *
         * @param product    The production item.
         * @param level      The level required.
         * @param experience The experience rewarded.
         * @param type       The jewellery type.
         * @param materials  The materials required.
         */
        JewelleryData(int product, int level, double experience, JewelleryType type, Item... materials) {
            this.product = product;
            this.level = level;
            this.experience = experience;
            this.type = type;
            this.materials = materials;
        }

        /**
         * Gets the size of all the jewellery types.
         *
         * @param type The jewellery type.
         * @return The jewellery size.
         */
        public static int getSize(JewelleryType type) {
            int count = 0;
            for (JewelleryData data : values()) {
                if (data.type == type) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Gets the product items based on the jewellery type.
         *
         * @param type The jewellery type.
         * @return The jewellery type production items.
         */
        public static int[] getItems(JewelleryType type) {
            int[] items = new int[getSize(type)];
            int count = 0;
            for (JewelleryData data : values()) {
                if (data.type == type) {
                    items[count] = data.product;
                    count++;
                }
            }
            return items;
        }

        /**
         * Gets the jewellery data based on the product item.
         *
         * @param item The item being searched.
         * @return The jewellery data.
         */
        public static Optional<JewelleryData> forItem(int item) {
            return Arrays.stream(values()).filter(i -> i.product == item).findAny();
        }
    }

    /**
     * Opens the jewellery creation itemcontainer.
     *
     * @param player The player instance.
     */
    public static void open(Player player) {
        int[] items = JewelleryData.getItems(JewelleryType.RING);
        for (int i = 0; i < items.length; i++) {
            player.getPacketSender().sendItemOnInterfaceSlot(JewelleryType.RING.identification, items[i], 1, i);
        }

        items = JewelleryData.getItems(JewelleryType.NECKLACE);
        for (int i = 0; i < items.length; i++) {
            player.getPacketSender().sendItemOnInterfaceSlot(JewelleryType.NECKLACE.identification, items[i], 1, i);
        }

        items = JewelleryData.getItems(JewelleryType.AMULET);
        for (int i = 0; i < items.length; i++) {
            player.getPacketSender().sendItemOnInterfaceSlot(JewelleryType.AMULET.identification, items[i], 1, i);
        }

        player.getPacketSender().sendInterfaceModel(4229, 0, -1);
        player.getPacketSender().sendInterfaceModel(4235, 0, -1);
        player.getPacketSender().sendInterfaceModel(4241, 0, -1);
        player.getInterfaceManager().open(4161);
    }

    /**
     * Handles clicking on the itemcontainer.
     *
     * @param player The player instance.
     * @param item   The item being crafted.
     * @param amount The amount being crafted.
     */
    public static void click(Player player, int item, int amount) {
        if (!JewelleryData.forItem(item).isPresent()) {
            return;
        }

        JewelleryData jewellery = JewelleryData.forItem(item).get();

        if (player.getSkills().level(Skills.CRAFTING) < jewellery.level) {
            ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, jewellery.product);
            String name = def.name;
            player.message("You need a crafting level of " + jewellery.level + " to craft " + Utils.getAOrAn(name) + " " + name + ".");
            return;
        }

        boolean contains = true;
        for (Item i : jewellery.materials) {
            if (!player.inventory().contains(i)) {
                String req = i.name();
                player.message("You need " + Utils.getAOrAn(req) + " " + req + " to make this!");
                contains = false;
            }
        }

        if (!contains)
            return;

        player.getInterfaceManager().close();
        player.action.execute(craft(player, jewellery, amount), true);
    }

    /**
     * The jewellery crafting animation.
     *
     * @param player    The player instance.
     * @param jewellery The jewellery data being crafted.
     * @param amount    The amount of jewellery being crafter.
     * @return The jewellery crafting action.
     */
    private static Action<Player> craft(Player player, JewelleryData jewellery, int amount) {
        return new Action<Player>(player, 4, true) {
            int ticks = 0;

            @Override
            public void execute() {
                if (!player.inventory().containsAll(jewellery.materials)) {
                    player.message("You do not have the required items to craft this!");
                    stop();
                    return;
                }

                player.animate(899);

                for (int index = 0; index < jewellery.materials.length; index++) {
                    if (index != 0) {
                        player.inventory().remove(jewellery.materials[index], -1, false);
                    }
                }

                player.inventory().add(new Item(jewellery.product), -1, true);
                player.getSkills().addXp(Skills.CRAFTING, jewellery.experience);
                player.message("You have crafted " + Utils.getAOrAn(new Item(jewellery.product).name()) + " " + new Item(jewellery.product).name() + ".");

                if (++ticks == amount) {
                    stop();
                }
            }

            @Override
            public String getName() {
                return "Jewellery crafting";
            }

            @Override
            public boolean prioritized() {
                return false;
            }

            @Override
            public WalkablePolicy getWalkablePolicy() {
                return WalkablePolicy.NON_WALKABLE;
            }
        };
    }
}

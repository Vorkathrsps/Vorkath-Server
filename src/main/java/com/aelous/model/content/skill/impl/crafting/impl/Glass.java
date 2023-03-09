package com.aelous.model.content.skill.impl.crafting.impl;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.World;
import com.aelous.model.entity.player.InputScript;
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
public class Glass {

    /**
     * Holds all the glass data.
     */
    public enum GlassData {
        VIAL(ItemIdentifiers.VIAL, 33, 35.0D, ItemIdentifiers.MOLTEN_GLASS),
        LIGHT_ORB(ItemIdentifiers.LIGHT_ORB, 87, 70.0D, ItemIdentifiers.MOLTEN_GLASS),
        BEER_GLASS(ItemIdentifiers.BEER_GLASS, 1, 17.5D, ItemIdentifiers.MOLTEN_GLASS),
        CANDLE_LANTERN(ItemIdentifiers.CANDLE_LANTERN, 4, 19.0D, ItemIdentifiers.MOLTEN_GLASS),
        OIL_LAMP(ItemIdentifiers.OIL_LAMP, 12, 25.0D, ItemIdentifiers.MOLTEN_GLASS),
        FISHBOWL(ItemIdentifiers.FISHBOWL, 42, 42.5D, ItemIdentifiers.MOLTEN_GLASS),
        LANTERN_LENS(ItemIdentifiers.LANTERN_LENS, 49, 55.0D, ItemIdentifiers.MOLTEN_GLASS),
        MOLTEN_GLASS(ItemIdentifiers.MOLTEN_GLASS, 1, 20.0D, SODA_ASH),
        UNPOWERED_ORB(ItemIdentifiers.UNPOWERED_ORB, 46, 52.5D, ItemIdentifiers.MOLTEN_GLASS);

        /**
         * The glass product.
         */
        private final int product;

        /**
         * The level required.
         */
        private final int level;

        /**
         * The experienced rewarded.
         */
        private final double experience;

        /**
         * The material required.
         */
        private final int material;

        /**
         * Constructs a new <code>GlassData</code>.
         */
        GlassData(int product, int level, double experience, int material) {
            this.product = product;
            this.level = level;
            this.experience = experience;
            this.material = material;
        }

        /**
         * Gets the glass data based on the material item.
         */
        public static Optional<GlassData> forGlass(int item) {
            return Arrays.stream(values()).filter(g -> g.material == item).findFirst();
        }
    }

    /**
     * Handles opening the glass crafting interface.
     */
    public static void open(Player player) {
        player.getInterfaceManager().open(11462);
    }

    /**
     * Handles clicking on the interface.
     */
    public static boolean click(Player player, int button) {
        switch (button) {
            /** Vial */
            case 11474:
                craft(player, GlassData.VIAL, 1);
                return true;
            case 11473:
                craft(player, GlassData.VIAL, 5);
                return true;
            case 11472:
                craft(player, GlassData.VIAL, 10);
                return true;
            case 11471:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        craft(player, GlassData.VIAL, (Integer) value);
                        return true;
                    }
                });
                return true;
            /** Orb */
            case 12396:
                craft(player, GlassData.UNPOWERED_ORB, 1);
                return true;
            case 12395:
                craft(player, GlassData.UNPOWERED_ORB, 5);
                return true;
            case 12394:
                craft(player, GlassData.UNPOWERED_ORB, 10);
                return true;
            case 11475:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        craft(player, GlassData.UNPOWERED_ORB, (Integer) value);
                        return true;
                    }
                });
                return true;
            /** Beer glass */
            case 12400:
                craft(player, GlassData.BEER_GLASS, 1);
                return true;
            case 12399:
                craft(player, GlassData.BEER_GLASS, 5);
                return true;
            case 12398:
                craft(player, GlassData.BEER_GLASS, 10);
                return true;
            case 12397:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        craft(player, GlassData.BEER_GLASS, (Integer) value);
                        return true;
                    }
                });
                return true;
            /** Candle lantern */
            case 12404:
                craft(player, GlassData.CANDLE_LANTERN, 1);
                return true;
            case 12403:
                craft(player, GlassData.CANDLE_LANTERN, 5);
                return true;
            case 12402:
                craft(player, GlassData.CANDLE_LANTERN, 10);
                return true;
            case 12401:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        craft(player, GlassData.CANDLE_LANTERN, (Integer) value);
                        return true;
                    }
                });
                return true;
            /** Oil lamp */
            case 12408:
                craft(player, GlassData.OIL_LAMP, 1);
                return true;
            case 12407:
                craft(player, GlassData.OIL_LAMP, 5);
                return true;
            case 12406:
                craft(player, GlassData.OIL_LAMP, 10);
                return true;
            case 12405:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        craft(player, GlassData.OIL_LAMP, (Integer) value);
                        return true;
                    }
                });
                return true;
            /** Fishbowl */
            case 6203:
                craft(player, GlassData.FISHBOWL, 1);
                return true;
            case 6202:
                craft(player, GlassData.FISHBOWL, 5);
                return true;
            case 6201:
                craft(player, GlassData.FISHBOWL, 10);
                return true;
            case 6200:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        craft(player, GlassData.FISHBOWL, (Integer) value);
                        return true;
                    }
                });
                return true;
            /** Lantern lens */
            case 12412:
                craft(player, GlassData.LANTERN_LENS, 1);
                return true;
            case 12411:
                craft(player, GlassData.LANTERN_LENS, 5);
                return true;
            case 12410:
                craft(player, GlassData.LANTERN_LENS, 10);
                return true;
            case 12409:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        craft(player, GlassData.LANTERN_LENS, (Integer) value);
                        return true;
                    }
                });
                return true;
        }
        return false;
    }

    /**
     * Handles crafting the glass.
     */
    public static void craft(Player player, GlassData glass, int amount) {
        if (player.getSkills().level(Skills.CRAFTING) < glass.level) {
            player.message("You need a crafting level of " + glass.level + " to craft this!");
            return;
        }

        if (!player.inventory().contains(GLASSBLOWING_PIPE) && glass != GlassData.MOLTEN_GLASS) {
            player.message("You need a glassblowing pipe to do this!");
            return;
        }

        if (!player.inventory().contains(glass.material)) {
            ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, glass.material);
            String name = def.name;
            player.message("You need " + Utils.getAOrAn(name) + " " + name + " to do this!");
            return;
        }

        player.getInterfaceManager().close();
        player.action.execute(blow(player, glass, amount), true);
    }

    /**
     * Handles blowing the glass data.
     */
    private static Action<Player> blow(Player player, GlassData glass, int amount) {
        return new Action<Player>(player, 3, true) {
            int ticks = 0;

            @Override
            public void execute() {
                boolean moltenGlass = glass == GlassData.MOLTEN_GLASS;

                if (moltenGlass && (!player.inventory().contains(BUCKET_OF_SAND) || !player.inventory().contains(SODA_ASH))) {
                    player.message("You need a bucket of sand and soda ash to make molten glass!");
                    stop();
                    return;
                }

                if (!player.inventory().contains(GLASSBLOWING_PIPE) && !moltenGlass) {
                    player.message("You need a glassblowing pipe to do this!");
                    stop();
                    return;
                }

                if (!player.inventory().contains(glass.material)) {
                    ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, glass.material);
                    if (def == null) return;
                    String name = def.name;
                    player.message("You need " + Utils.getAOrAn(name) + " " + name + " to do this!");
                    stop();
                    return;
                }

                player.inventory().remove(glass.material, 1);

                if (moltenGlass) {
                    player.inventory().replace(BUCKET_OF_SAND, BUCKET, true);
                }

                ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, glass.material);
                if (def == null) return;
                String name = def.name;
                player.animate(moltenGlass ? 899 : 884);
                player.inventory().add(new Item(glass.product));
                player.getSkills().addXp(Skills.CRAFTING, glass.experience);
                player.message("You make " + Utils.getAOrAn(name) + " " + name + ".");

                if (++ticks == amount) {
                    stop();
                }
            }

            @Override
            public String getName() {
                return "Glass";
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

package com.aelous.model.content.consumables;

import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.*;

public class FoodConsumable {

    public enum Food {
        SHRIMP(ItemIdentifiers.SHRIMPS, 3),
        COOKED_CHICKEN(ItemIdentifiers.COOKED_CHICKEN, 4),
        SARDINE(ItemIdentifiers.SARDINE, 4),
        COOKED_MEAT(ItemIdentifiers.COOKED_MEAT, 4),
        BREAD(ItemIdentifiers.BREAD, 5),
        HERRING(ItemIdentifiers.HERRING, 5),
        MACKEREL(ItemIdentifiers.MACKEREL, 6),
        TROUT(ItemIdentifiers.TROUT, 7),
        PIKE(ItemIdentifiers.PIKE, 8),
        COD(ItemIdentifiers.COD, 7),
        CURRY(ItemIdentifiers.CURRY, 19),
        SALMON(ItemIdentifiers.SALMON, 9),
        TUNA(ItemIdentifiers.TUNA, 10),
        PEACHES(ItemIdentifiers.PEACH, 8),
        BANANAS(ItemIdentifiers.BANANA, 2),
        CAKE(ItemIdentifiers.CAKE, 4, _23_CAKE),
        TWO_THIRDS_CAKE(_23_CAKE, 4, SLICE_OF_CAKE),
        ONE_THIRD_CAKE(SLICE_OF_CAKE, 4),
        CHOCOLATE_CAKE(ItemIdentifiers.CHOCOLATE_CAKE, 5, _23_CHOCOLATE_CAKE),
        TWO_THIRDS_CHOCOLATE_CAKE(_23_CHOCOLATE_CAKE, 5, CHOCOLATE_SLICE),
        ONE_THIRD_CHOCOLATE_CAKE(CHOCOLATE_SLICE, 5),
        LOBSTER(ItemIdentifiers.LOBSTER, 12),
        BASS(ItemIdentifiers.BASS, 13),
        SWORDFISH(ItemIdentifiers.SWORDFISH, 14),
        MONKFISH(ItemIdentifiers.MONKFISH, 16),
        EDIBLE_SEAWEED(ItemIdentifiers.EDIBLE_SEAWEED, 4),
        STRANGE_FRUIT(ItemIdentifiers.STRANGE_FRUIT, 0, -1, true),
        PURPLE_SWEETS(ItemIdentifiers.PURPLE_SWEETS, 3, -1, true),
        PINK_SWEETS(ItemIdentifiers.PINK_SWEETS, 3, -1, true),
        PLAIN_PIZZA(ItemIdentifiers.PLAIN_PIZZA, 7, _12_PLAIN_PIZZA),
        HALF_PLAIN_PIZZA(_12_PLAIN_PIZZA, 7),
        MEAT_PIZZA(ItemIdentifiers.MEAT_PIZZA, 8, _12_MEAT_PIZZA),
        HALF_MEAT_PIZZA(_12_MEAT_PIZZA, 8),
        ANCHOVY_PIZZA(ItemIdentifiers.ANCHOVY_PIZZA, 9, _12_ANCHOVY_PIZZA),
        HALF_ANCHOVY_PIZZA(_12_ANCHOVY_PIZZA, 9),
        PINEAPPLE_PIZZA(ItemIdentifiers.PINEAPPLE_PIZZA, 11, _12_PINEAPPLE_PIZZA),
        HALF_PINEAPPLE_PIZZA(_12_PINEAPPLE_PIZZA, 11),
        SHARK(ItemIdentifiers.SHARK, 20),
        SEA_TURTLE(ItemIdentifiers.SEA_TURTLE, 21),
        MANTA_RAY(ItemIdentifiers.MANTA_RAY, 22),
        TUNA_POTATO(ItemIdentifiers.TUNA_POTATO, 22),
        CHEESE_POTATO(POTATO_WITH_CHEESE, 16),
        DARK_CRAB(ItemIdentifiers.DARK_CRAB, 22),
        REDBERRY_PIE(ItemIdentifiers.REDBERRY_PIE, 5, HALF_A_REDBERRY_PIE),
        HALF_REDBERRY_PIE(HALF_A_REDBERRY_PIE, 5, PIE_DISH),
        MEAT_PIE(ItemIdentifiers.MEAT_PIE, 6, HALF_A_MEAT_PIE),
        HALF_MEAT_PIE(HALF_A_MEAT_PIE, 6, PIE_DISH),
        APPLE_PIE(ItemIdentifiers.APPLE_PIE, 7, HALF_AN_APPLE_PIE),
        HALF_APPLE_PIE(HALF_AN_APPLE_PIE, 7, PIE_DISH),
        GARDEN_PIE(ItemIdentifiers.GARDEN_PIE, 6, HALF_A_GARDEN_PIE),
        HALF_GARDEN_PIE(HALF_A_GARDEN_PIE, 6, PIE_DISH),
        FISH_PIE(ItemIdentifiers.FISH_PIE, 6, HALF_A_FISH_PIE),
        HALF_FISH_PIE(HALF_A_FISH_PIE, 6, PIE_DISH),
        ADMIRAL_PIE(ItemIdentifiers.ADMIRAL_PIE, 8, HALF_AN_ADMIRAL_PIE),
        HALF_ADMIRAL_PIE(HALF_AN_ADMIRAL_PIE, 8, PIE_DISH),
        WILD_PIE(ItemIdentifiers.WILD_PIE, 11, HALF_A_WILD_PIE),
        HALF_WILD_PIE(HALF_A_WILD_PIE, 11, PIE_DISH),
        SUMMER_PIE(ItemIdentifiers.SUMMER_PIE, 11, HALF_A_SUMMER_PIE),
        HALF_SUMMER_PIE(HALF_A_SUMMER_PIE, 11, PIE_DISH),
        KARAMBWAN(COOKED_KARAMBWAN, 18, -1, true),
        ANGLERFISH(ItemIdentifiers.ANGLERFISH, 22),
        FRIED_MUSHROOMS(ItemIdentifiers.FRIED_MUSHROOMS, 5, BOWL),
        CHILLI_POTATO(ItemIdentifiers.CHILLI_POTATO, 14),
        MUSHROOM_POTATO(ItemIdentifiers.MUSHROOM_POTATO, 20),
        CUP_OF_TEA(ItemIdentifiers.CUP_OF_TEA, 2, EMPTY_CUP, true),
        JUG_OF_WINE(ItemIdentifiers.JUG_OF_WINE, 11, JUG, true),
        BEER(ItemIdentifiers.BEER, 1, BEER_GLASS, true),
        POTATO(ItemIdentifiers.POTATO, 1, -1, true),
        ONION(ItemIdentifiers.ONION, 1, -1, true),
        CABBAGE(ItemIdentifiers.CABBAGE, 1, -1, true),
        UGTHANKI_KEBAB(ItemIdentifiers.UGTHANKI_KEBAB, 19, -1, true),
        UGTHANKI_KEBAB_2(UGTHANKI_KEBAB_1885, 19, -1, true);

        Food(int itemId, int heal) {
            this.itemId = itemId;
            this.heal = heal;
        }

        Food(int itemId, int heal, int replacement) {
            this.itemId = itemId;
            this.heal = heal;
            this.replacement = replacement;
        }

        Food(int itemId, int heal, int replacement, boolean effect) {
            this.itemId = itemId;
            this.heal = heal;
            this.replacement = replacement;
            this.effect = effect;
        }

        private final int itemId;
        private final int heal;
        private int replacement;
        private boolean effect;

        public int getItemId() {
            return itemId;
        }

        public int getHeal() {
            return heal;
        }
    }

    public static boolean onItemOption1(Player player, Item item) {
        for (Food food : Food.values()) {
            if (food.itemId == item.getId()) {
                eat(player, food);
                return true;
            }
        }
        return false;
    }


    /**
     * Handles the player eating said food type.
     */
    private static void eat(Player player, Food food) {
        if (!player.getInterfaceManager().isClear()) {
            player.getInterfaceManager().close(false);
        }

        //player.debugMessage("Eating food.");
        // Check timers and other things. Also karambwan.
        if (food == Food.KARAMBWAN) {
            if (player.getTimers().has(TimerKey.KARAMBWAN) || player.dead() || player.hp() < 1) {
                player.debugMessage("Your Karambwan timer is still active, " + player.getTimers().asSeconds(TimerKey.KARAMBWAN) + " remaining.");
                return;
            }
        } else {
            if (player.getTimers().has(TimerKey.FOOD) || player.dead() || player.hp() < 1) {
                player.debugMessage("Your food timer is still active, " + player.getTimers().asSeconds(TimerKey.FOOD) + " remaining.");
                return;
            }
        }

        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_FOOD.ordinal()]) {
            player.message("Food is disabled for this duel.");
            return;
        }

        if (player.locked()) {
            return;
        }

        if (player.stunned()) {
            player.message("You're currently stunned!");
            return;
        }

        //Eating stops combat
        player.getCombat().reset();

        Item foodItem = new Item(food.itemId);
        boolean healed = player.hp() < player.maxHp();
        String name = foodItem.definition(World.getWorld()).name.toLowerCase();

        if (player.getController() != null) {
            if (!player.getController().canEat(player, foodItem.getId())) {
                player.message("You cannot eat here.");
                return;
            }
        }

        int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, 0);

        player.inventory().remove(foodItem, slot, true);

        if (food.replacement > 0) {
            player.inventory().add(new Item(food.replacement), slot, true);
        }

        boolean fullpizza = food == Food.PLAIN_PIZZA || food == Food.MEAT_PIZZA || food == Food.ANCHOVY_PIZZA || food == Food.PINEAPPLE_PIZZA;
        boolean halfpizza = food == Food.HALF_PLAIN_PIZZA || food == Food.HALF_MEAT_PIZZA || food == Food.HALF_ANCHOVY_PIZZA || food == Food.HALF_PINEAPPLE_PIZZA;

        int ticks;

        if (fullpizza)
            ticks = 1;
        else if (halfpizza)
            ticks = 2;
        else ticks = 3;

        player.getTimers().extendOrRegister(TimerKey.FOOD, ticks);
        player.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 5);

        int increase = player.getEquipment().hpIncrease();
        boolean inWilderness = WildernessArea.inWilderness(player.tile());
        if (food == Food.ANGLERFISH) {
            if (inWilderness && CombatFactory.inCombat(player)) {
                player.heal(food.heal, 0);
            } else {
                player.heal(food.heal, increase > 0 ? increase : 22);
            }
        } else if (food == Food.PURPLE_SWEETS || food == Food.PINK_SWEETS) {
            player.heal(Utils.random(food.heal, player.getEquipment().hpIncrease()));
        } else {
            player.heal(food.heal, player.getEquipment().hpIncrease());
        }

        int eatAnim;
        if (player.getEquipment().contains(ItemIdentifiers.SLED_4084))
            eatAnim = 1469;
        else
            eatAnim = 829;

        player.animate(eatAnim);

        if (!food.effect) {
            if (fullpizza) {
                player.message("You eat half of the " + name + ".");
            } else if (halfpizza) {
                player.message("You eat the remaining " + name + ".");
            } else {
                player.message("You eat the " + name + ".");
            }

            if (healed)
                player.message("It heals some health.");
        } else {
            if (food == Food.BEER) {
                player.message("You drink the beer. You feel slightly reinvigorated...");
                player.message("...and slightly dizzy too.");
            } else if (food == Food.CUP_OF_TEA) {
                player.message("You drunk the cup of tea.");
                player.forceChat("Aaah, nothing like a nice cuppa tea!");
            } else if (food == Food.JUG_OF_WINE) {
                player.message("You drink the wine.");
                player.message("It makes you feel a bit dizzy.");
            } else if (food == Food.UGTHANKI_KEBAB || food == Food.UGTHANKI_KEBAB_2) {
                int random = Utils.random(4);
                switch (random) {
                    case 1 -> player.forceChat("Lovely!");
                    case 2 -> player.forceChat("Scrummy!");
                    case 3 -> player.forceChat("Delicious!");
                    default -> player.forceChat("Yum!");
                }
                player.message("You eat the kebab.");
            } else if (food == Food.KARAMBWAN) {
                player.getTimers().register(TimerKey.KARAMBWAN, 3); // Register karambwan timer too
                player.getTimers().register(TimerKey.POTION, 3); // Register the potion timer (karambwan blocks pots)
                player.message("You eat the Karambwan.");
                if (healed)
                    player.message("It heals some health.");
            } else if (food == Food.CABBAGE) {
                player.message("You eat the cabbage. Yuck!");
            } else if (food == Food.POTATO) {
                player.message("You eat the potato. Yuck!");
                if (healed)
                    player.message("It heals some health anyway.");
            } else if (food == Food.ONION) {
                player.message("You eat the onion.");
                player.message("It's always sad to see a grown man cry.");
            } else if (food == Food.PURPLE_SWEETS || food == Food.PINK_SWEETS) {
                double energy = player.getAttribOr(AttributeKey.RUN_ENERGY, 0.0);
                player.setRunningEnergy(energy + 10, true);
                player.message("You eat the sweets.");
                player.message("The sugary goodness heals some energy.");
            }
        }
        player.playSound(317);
    }
}

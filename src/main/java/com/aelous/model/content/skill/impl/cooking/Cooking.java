package com.aelous.model.content.skill.impl.cooking;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.ChatBoxItemDialogue;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * Created by Situations on 11/20/2015.
 */
public class Cooking extends PacketInteraction {

    private static int cookingChance(Player player, Cookable type) {
        int points = 60;
        int diff = player.getSkills().levels()[Skills.COOKING] - type.lvl;
        return Math.min(100, points + diff);
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item with) {
        if (JugOfWine.onItemOnItem(player, use, with)) {
            return true;
        }

        if(Pizza.makePizza(player, use, with)) {
            return true;
        }

        if(Potato.makePotato(player, use, with)) {
            return true;
        }

        if(Toppings.makeTopping(player, use, with)) {
            return true;
        }

        //Bread dough
        if ((use.getId() == ItemIdentifiers.POT_OF_FLOUR && with.getId() == ItemIdentifiers.BUCKET_OF_WATER) || (use.getId() == ItemIdentifiers.BUCKET_OF_WATER && with.getId() == ItemIdentifiers.POT_OF_FLOUR)) {
            player.inventory().remove(new Item(ItemIdentifiers.POT_OF_FLOUR));
            player.inventory().remove(new Item(ItemIdentifiers.BUCKET_OF_WATER));
            player.inventory().add(new Item(ItemIdentifiers.BREAD_DOUGH));
            return true;
        } else if ((use.getId() == ItemIdentifiers.POT_OF_FLOUR && with.getId() == ItemIdentifiers.BOWL_OF_WATER) || (use.getId() == ItemIdentifiers.BOWL_OF_WATER && with.getId() == ItemIdentifiers.POT_OF_FLOUR)) {
            player.inventory().remove(new Item(ItemIdentifiers.POT_OF_FLOUR));
            player.inventory().remove(new Item(ItemIdentifiers.BOWL_OF_WATER));
            player.inventory().add(new Item(ItemIdentifiers.BREAD_DOUGH));
            return true;
        } else if ((use.getId() == ItemIdentifiers.POT_OF_FLOUR && with.getId() == ItemIdentifiers.JUG_OF_WATER) || (use.getId() == ItemIdentifiers.JUG_OF_WATER && with.getId() == ItemIdentifiers.POT_OF_FLOUR)) {
            player.inventory().remove(new Item(ItemIdentifiers.POT_OF_FLOUR));
            player.inventory().remove(new Item(ItemIdentifiers.JUG_OF_WATER));
            player.inventory().add(new Item(ItemIdentifiers.BREAD_DOUGH));
            return true;
        }
        //Uncooked stew
        if ((use.getId() == ItemIdentifiers.BOWL_OF_WATER && with.getId() == ItemIdentifiers.POTATO) || (use.getId() == ItemIdentifiers.POTATO && with.getId() == ItemIdentifiers.BOWL_OF_WATER) ||
            (use.getId() == ItemIdentifiers.BOWL_OF_WATER && with.getId() == ItemIdentifiers.COOKED_MEAT) || (use.getId() == ItemIdentifiers.COOKED_MEAT && with.getId() == ItemIdentifiers.BOWL_OF_WATER) ||
            (use.getId() == ItemIdentifiers.COOKED_MEAT && with.getId() == ItemIdentifiers.POTATO) || (use.getId() == ItemIdentifiers.POTATO && with.getId() == ItemIdentifiers.COOKED_MEAT)) {
            player.inventory().remove(new Item(ItemIdentifiers.POTATO));
            player.inventory().remove(new Item(ItemIdentifiers.COOKED_MEAT));
            player.inventory().remove(new Item(ItemIdentifiers.BOWL_OF_WATER));
            player.inventory().add(new Item(ItemIdentifiers.UNCOOKED_STEW));
            return true;
        } else if ((use.getId() == ItemIdentifiers.BOWL_OF_WATER && with.getId() == ItemIdentifiers.POTATO) || (use.getId() == ItemIdentifiers.POTATO && with.getId() == ItemIdentifiers.BOWL_OF_WATER) ||
            (use.getId() == ItemIdentifiers.BOWL_OF_WATER && with.getId() == ItemIdentifiers.COOKED_CHICKEN) || (use.getId() == ItemIdentifiers.COOKED_CHICKEN && with.getId() == ItemIdentifiers.BOWL_OF_WATER) ||
            (use.getId() == ItemIdentifiers.COOKED_CHICKEN && with.getId() == ItemIdentifiers.POTATO) || (use.getId() == ItemIdentifiers.POTATO && with.getId() == ItemIdentifiers.COOKED_CHICKEN)) {
            player.inventory().remove(new Item(ItemIdentifiers.POTATO));
            player.inventory().remove(new Item(ItemIdentifiers.COOKED_CHICKEN));
            player.inventory().remove(new Item(ItemIdentifiers.BOWL_OF_WATER));
            player.inventory().add(new Item(ItemIdentifiers.UNCOOKED_STEW));
            return true;
        }
        //Curry
        if ((use.getId() == ItemIdentifiers.SPICE && with.getId() == ItemIdentifiers.UNCOOKED_STEW) || (use.getId() == ItemIdentifiers.UNCOOKED_STEW && with.getId() == ItemIdentifiers.SPICE)) {
            player.inventory().remove(new Item(ItemIdentifiers.SPICE));
            player.inventory().remove(new Item(ItemIdentifiers.UNCOOKED_STEW));
            player.inventory().add(new Item(ItemIdentifiers.CURRY));
            return true;
        } else if ((use.getId() == ItemIdentifiers.CURRY_LEAF && with.getId() == ItemIdentifiers.UNCOOKED_STEW) || (use.getId() == ItemIdentifiers.UNCOOKED_STEW && with.getId() == ItemIdentifiers.CURRY_LEAF)) {
            if(!player.inventory().contains(new Item(ItemIdentifiers.CURRY_LEAF, 3))) {
                player.message("You need atleast 3 curry leaves.");
                return true;
            }
            player.inventory().remove(new Item(ItemIdentifiers.CURRY_LEAF, 3));
            player.inventory().remove(new Item(ItemIdentifiers.UNCOOKED_STEW));
            player.inventory().add(new Item(ItemIdentifiers.CURRY));
            return true;
        }
        return false;
    }

    private static final List<Integer> RANGES = Arrays.asList(RANGE_27517, COOKING_RANGE_16893, RANGE_26181, COOKING_RANGE_4172, FIRE_26185, FIREPLACE_8712, STOVE_12269, SULPHUR_VENT, COOKING_POT_26180, COOKING_POT_12969, COOKING_RANGE, RANGE_7183, BONFIRE, UNCOOKING_POT, FIRE_26185);

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        for (int range : RANGES) {
            if (object.getId() == range) {
                int id = player.getAttribOr(AttributeKey.ITEM_ID, -1);
                GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);
                Cookable food = Cookable.get(id);

                if (food != null) {
                    //Check to see if the player has the level required to cook the food
                    if (player.getSkills().levels()[Skills.COOKING] < food.lvl) {
                        DialogueManager.sendStatement(player, "You need a cooking level of " + food.lvl + " to cook " + food.itemname + ".");
                    } else {
                        startCooking(player, food, obj);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean dry_out_meat = false;
    private final static int SINEW = 9436;

    private void startCooking(Player player, Cookable food, GameObject obj) {
        //If we're using raw beef or bear meat we..
        if (food.raw == 2132 || food.raw == 2136) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Dry the meat into sinew.", "Cook the meat");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            dry_out_meat = true;
                            stop();
                            player.action.execute(cook(player, food, obj, 1), true);
                        } else if (option == 2) {
                            stop();
                            player.action.execute(cook(player, food, obj, 1), true);
                        }
                    }
                }
            });
            return;
        }
        // Either 1 if we only have one, else ask.
        int amt = player.inventory().count(food.raw);
        if (amt == 1) {
            player.action.execute(cook(player, food, obj, 1), true);
        } else {
            ChatBoxItemDialogue.sendInterface(player, 1746, 175, food.raw);
            player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
                @Override
                public void firstOption(Player player) {
                    player.action.execute(cook(player, food, obj, 1), true);
                }

                @Override
                public void secondOption(Player player) {
                    player.action.execute(cook(player, food, obj, 5), true);
                }

                @Override
                public void thirdOption(Player player) {
                    player.setAmountScript("How many would you like to cook?", new InputScript() {
                        @Override
                        public boolean handle(Object value) {
                            player.action.execute(cook(player, food, obj, (Integer) value), true);
                            return true;
                        }
                    });
                }

                @Override
                public void fourthOption(Player player) {
                    player.action.execute(cook(player, food, obj,player.inventory().count(food.raw)), true);
                }
            };
        }
    }

    private Action<Player> cook(Player player, Cookable food, GameObject obj, int amount) {
        return new Action<>(player, 3) {
            int ticks = 0;

            @Override
            public void execute() {
                if (!player.inventory().remove(new Item(food.raw), true)) {
                    player.message("You don't have any more " + food.plural + " to cook.");
                    stop();
                    return;
                }

                if (obj.getId() == 26185 || obj.getId() == 29300) {
                    player.animate(897);
                } else {
                    player.animate(896);
                }

                // Cooking skillcape stops burning all food.
                if (player.getEquipment().containsAny(9801, 9802, 10658)
                    || player.getEquipment().wearingMaxCape()
                    || Utils.rollDie(100, cookingChance(player, food))
                    || food == Cookable.SEAWEED) {
                    if (!dry_out_meat) {
                        player.inventory().add(new Item(food.cooked), true);
                    } else {
                        player.inventory().add(new Item(SINEW), true);
                    }

                    String msg = switch (food) {
                        case RAW_LOBSTER -> "You roast a lobster.";
                        case PIE_MEAT -> "You successfully bake a tasty meat pie.";
                        case REDBERRY_PIE -> "You successfully bake a delicious redberry pie.";
                        case SEAWEED -> "You burn the seaweed to soda ash.";
                        default -> "You successfully cook " + food.itemname + ".";
                    };

                    switch (food) {
                        case RAW_SHRIMPS -> AchievementsManager.activate(player, Achievements.COOKING_I, 1);
                        case RAW_LOBSTER -> AchievementsManager.activate(player, Achievements.COOKING_II, 1);
                        case RAW_SHARK -> AchievementsManager.activate(player, Achievements.COOKING_III, 1);
                        case RAW_ANGLERFISH -> AchievementsManager.activate(player, Achievements.COOKING_IV, 1);
                    }

                    if(food == Cookable.RAW_SHARK) {
                        player.getTaskMasterManager().increase(Tasks.COOK_SHARKS);
                    }

                    player.message(msg);
                    player.getSkills().addXp(Skills.COOKING, food.exp);
                    //Caskets Money, money, money..
                    if (Utils.rollDie(20, 1)) {
                        player.inventory().addOrDrop(new Item(7956, 1));
                        player.message("Hmm strange, a casket appears on the cooking range. You took it before it burnt.");
                    }
                } else {
                    player.inventory().add(new Item(food.burnt), true);
                    player.message("You accidentally burn " + food.itemname.replaceFirst("an ", "the ").replaceFirst("a ", "the ") + ".");
                }

                if (++ticks == amount) {
                    stop();
                }
            }

            @Override
            public String getName() {
                return "Super combat potion";
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

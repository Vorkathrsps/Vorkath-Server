package com.aelous.model.content.skill.impl.herblore;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.ChatBoxItemDialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

/**
 * Created by Bart on 10/29/2015.
 * Converted by Zerikoth @ augustus 23, 2020
 */
public class PotionBrewing {

    public enum BrewablePotion {

        ATTACK_POTION(249, 221, new Item(91), 121, ItemIdentifiers.ATTACK_POTION4, 1 /* Actually 3 but we don't have the quest */, 25.0, "Guam Leaf", "eye of newt"),
        ANTIPOISON(251, 235, new Item(93), 175,  ItemIdentifiers.ANTIPOISON4,5, 37.5, "Marrentill", "unicorn horn"),
        RELICYMS_BALM(1534, 1526, new Item(4840), 4844, ItemIdentifiers.RELICYMS_BALM4, 8, 40.0, "Rogue's Purse", "snake weed"),
        STRENGTH_POTION(253, 225, new Item(95), 115, ItemIdentifiers.STRENGTH_POTION4, 12, 50.0, "Tarromin", "limpwurt root"),
        SERUM_207(-1, 592, new Item(95), 3410, ItemIdentifiers.SERUM_207_4, 15, 50.0, "Tarromin", "ashes"),
        RESTORE_POTION(255, 223, new Item(97), 127, ItemIdentifiers.RESTORE_POTION4, 22, 62.5, "Harralander", "eggs"),
        ENERGY_POTION(-1, 1975, new Item(97), 3010, ItemIdentifiers.ENERGY_POTION4, 22, 62.5, "Harralander", "chocolate dust"),
        DEFENCE_POTION(257, 239, new Item(99), 133, ItemIdentifiers.DEFENCE_POTION4, 30, 75.0, "Ranarr Weed", "white berries"),
        AGILITY_POTION(2998, 2152, new Item(3002), 3034, ItemIdentifiers.AGILITY_POTION4, 34, 80.0, "Toadflax", "toad legs"), //sic
        COMBAT_POTION(-1, 9736, new Item(97), 9741, ItemIdentifiers.COMBAT_POTION4, 36, 84.0, "Harralander", "goat horn dust"),
        PRAYER_POTION(-1, 231, new Item(99), 139, ItemIdentifiers.PRAYER_POTION4, 38, 87.5, "Ranarr Weed", "snape grass"),
        SUPER_ATTACK(259, 221, new Item(101), 145, ItemIdentifiers.SUPER_ATTACK4, 45, 100.0, "Irit Leaf", "eye of newt"),
        SUPER_ANTIPOISON(-1, 235, new Item(101), 181, ItemIdentifiers.SUPERANTIPOISON4, 48, 106.3, "Irit Leaf", "unicorn horn"),
        FISHING_POTION(261, 231, new Item(103), 151, ItemIdentifiers.FISHING_POTION4, 50, 112.5, "Avantoe", "snape grass"),
        SUPER_ENERGY(-1, 2970, new Item(103), 3018, ItemIdentifiers.SUPER_ENERGY4, 52, 117.5, "Avantoe", "Mort Myre fungi"),
        HUNTER_POTION(-1, 10111, new Item(103), 10000, ItemIdentifiers.HUNTER_POTION4, 53, 120.0, "Avantoe", "ground kebbit teeth"),
        SUPER_STRENGTH(263, 225, new Item(105), 157, ItemIdentifiers.SUPER_STRENGTH4, 55, 125.0, "Kwuarm", "limpwurt root"),
        WEAPON_POISON(-1, 243, new Item(105), 187, -1, 60, 137.5, "Kwuarm", "blue dragon scale"),
        SUPER_RESTORE(3000, 223, new Item(3004), 3026, ItemIdentifiers.SUPER_RESTORE4, 63, 142.5, "Snapdragon", "eggs"),
        SUPER_DEFENCE(265, 239, new Item(107), 163, ItemIdentifiers.SUPER_DEFENCE4, 66, 150.0, "Cadantine", "white berries"),
        ANTIDOTE_PLUS(2998, 6049, new Item(5942), 5943, ItemIdentifiers.ANTIDOTE4, 68, 155.0, "Toadflax", "yew roots"),
        ANTIFIRE(2481, 241, new Item(2483), 2454, ItemIdentifiers.ANTIFIRE_POTION4, 69, 157.5, "Lantadyme", "blue dragon scale"),
        RANGING_POTION(267, 245, new Item(109), 169, ItemIdentifiers.RANGING_POTION4, 72, 162.5, "Dwarf Weed", "wine of Zamorak"), // TODO does this consume the can? dont think so
        MAGIC_POTION(-1, 3138, new Item(2483), 3042, ItemIdentifiers.MAGIC_POTION4, 76, 172.5, "Lantadyme", "potato cactus"),
        ZAMORAK_BREW(269, 247, new Item(111), 189, ItemIdentifiers.ZAMORAK_BREW4, 78, 175.0, "Torstol", "jangerberries"),
        ANTIDOTE_PP(259, 6051, new Item(5951), 5952,  -1,79, 177.5, "Irit Leaf", "magic roots"),
        SARADOMIN_BREW(-1, 6693, new Item(3002), 6687, ItemIdentifiers.SARADOMIN_BREW4, 81, 180.0, "Toadflax", "crushed nest"),
        EXTENDED_ANTIFIRE_4(-1, 2452, new Item(11994, 4), 11951,-1,84, 110.0, "N/A", "lava scale shards x4"),
        EXTENDED_ANTIFIRE_3(-1, 2454, new Item(11994, 3), 11953,-1,84, 82.5, "N/A", "lava scale shards x1"),
        EXTENDED_ANTIFIRE_2(-1, 2456, new Item(11994, 2), 11955,-1,84, 55.0, "N/A", "lava scale shards x2"),
        EXTENDED_ANTIFIRE_1(-1, 2458, new Item(11994, 1), 11957,-1,84, 27.5, "N/A", "lava scale shards x1"),
        STAMINA_4(-1, 3016, new Item(12640, 4), 12625,-1,77, 104.0, "N/A", "amylase crystal x4"),
        STAMINA_3(-1, 3018, new Item(12640, 3), 12627,-1,77, 76.5, "N/A", "amylase crystal x3"),
        STAMINA_2(-1, 3020, new Item(12640, 2), 12629,-1,77, 51.0, "N/A", "amylase crystal x2"),
        STAMINA_1(-1, 3022, new Item(12640, 1), 12631,-1,77, 25.5, "N/A", "amylase crystal1"),
        ANTI_VENOM_FOUR_DOSE(-1, 5952, new Item(12934, 20),12905,-1, 87, 120.0, "Antidote++", "Zulrah's scales x20"),
        ANTI_VENOM_THREE_DOSE(-1, 5954, new Item(12934, 15),12907,-1, 87, 90.0, "Antidote++", "Zulrah's scales x15"),
        ANTI_VENOM_TWO_DOSE(-1, 5956, new Item(12934, 10),12909,-1, 87, 60.0, "Antidote++", "Zulrah's scales x10"),
        ANTI_VENOM_ONE_DOSE(-1, 5958, new Item(12934, 5),12911,-1, 87, 30.0, "Antidote++", "Zulrah's scales x5"),
        ANTI_VENOM_PLUS(-1, 12905, new Item(269), 12913,-1,94,125.0, "Torstol", "torstol"),
        SUPER_ANTIFIRE_POTION_4(-1, 2452, new Item(21975), 21978, -1, 92, 130.0, "N/A", "crushed superior dragon bones"),
        EXTENDED_SUPER_ANTIFIRE_POTION_4(-1, 21978, new Item(11994,4), 22209, -1, 98, 160.0, "N/A", "lava scale shards x4"),
        EXTENDED_SUPER_ANTIFIRE_POTION_3(-1, 21981, new Item(11994,3), 22212, -1, 98, 120.0, "N/A", "lava scale shards x3"),
        EXTENDED_SUPER_ANTIFIRE_POTION_2(-1, 21984, new Item(11994,2), 22215, -1, 98, 80.0, "N/A", "lava scale shards x2"),
        EXTENDED_SUPER_ANTIFIRE_POTION_1(-1, 21987, new Item(11994,1), 22218, -1, 98, 40.0, "N/A", "lava scale shards x1"),;

        private final int herb;
        private final int secondary;
        private final int result;
        private final int resultFourDose;
        private final int level;
        private final Item unfinished;
        private final double exp;
        private final String potName;
        private final String ingredientName;

        BrewablePotion(int herb, int secondary, Item unfinished, int result, int resultFourDose, int level, double exp, String potName, String ingredientName) {
            this.herb = herb;
            this.secondary = secondary;
            this.unfinished = unfinished;
            this.result = result;
            this.resultFourDose = resultFourDose;
            this.level = level;
            this.exp = exp;
            this.potName = potName;
            this.ingredientName = ingredientName;
        }
    }

    public static final int VIAL_OF_WATER = ItemIdentifiers.VIAL_OF_WATER;
    public static final int COCONUT_MILK = ItemIdentifiers.COCONUT_MILK;
    public static final int SCALE = ItemIdentifiers.LAVA_SCALE;
    public static final int SHARD = ItemIdentifiers.LAVA_SCALE_SHARD;
    public static final int CRYSTAL = ItemIdentifiers.AMYLASE_CRYSTAL;

    public static boolean onItemOnItem(Player player, Item use, Item with) {
        if (makeUnfinishedPotion(player, use, with)) {
            return true;
        }
        if (makeFinishedPotion(player, use, with)) {
            return true;
        }
        return false;
    }

    public static boolean makeUnfinishedPotion(Player player, Item use, Item with) {
        // ALl potion in the enum above
        for (BrewablePotion pot : BrewablePotion.values()) {
            if (pot.herb != -1) {
                int vial = (pot == BrewablePotion.ANTIDOTE_PLUS || pot == BrewablePotion.ANTIDOTE_PP) ? COCONUT_MILK : VIAL_OF_WATER;
                if ((use.getId() == pot.herb || with.getId() == pot.herb) && (use.getId() == vial || with.getId() == vial)) {
                    if (player.getSkills().level(Skills.HERBLORE) < pot.level) {
                        DialogueManager.sendStatement(player, "You need level " + pot.level + " Herblore to make this potion.");
                        return true;
                    }

                    int num = player.inventory().count(pot.herb);

                    if (num > 0) {
                        ChatBoxItemDialogue.sendInterface(player, 1746, 170, pot.unfinished);
                        player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
                            @Override
                            public void firstOption(Player player) {
                                player.action.execute(mix(player, pot, 1, false), true);
                            }

                            @Override
                            public void secondOption(Player player) {
                                player.action.execute(mix(player, pot, 5, false), true);
                            }

                            @Override
                            public void thirdOption(Player player) {

                                player.setAmountScript("Enter amount", new InputScript() {

                                    @Override
                                    public boolean handle(Object value) {
                                        player.action.execute(mix(player, pot, (Integer) value, false), true);
                                        return true;
                                    }
                                });
                            }

                            @Override
                            public void fourthOption(Player player) {
                                player.action.execute(mix(player, pot, 14, false), true);
                            }
                        };
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean makeFinishedPotion(Player player, Item use, Item with) {
        // ALl potion in the enum above
        for (BrewablePotion pot : BrewablePotion.values()) {
            if ((use.getId() == pot.unfinished.getId() || with.getId() == pot.unfinished.getId()) && (use.getId() == pot.secondary || with.getId() == pot.secondary)) {
                if (player.getSkills().level(Skills.HERBLORE) < pot.level) {
                    DialogueManager.sendStatement(player, "You need level " + pot.level + " Herblore to make this potion.");
                    return true;
                }

                int num = player.inventory().count(pot.unfinished.getId());

                if (num > 0) {
                    ChatBoxItemDialogue.sendInterface(player, 1746, 170, pot.result);
                    player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
                        @Override
                        public void firstOption(Player player) {
                            player.action.execute(mix(player, pot, 1, true), true);
                        }

                        @Override
                        public void secondOption(Player player) {
                            player.action.execute(mix(player, pot, 5, true), true);
                        }

                        @Override
                        public void thirdOption(Player player) {
                            player.setAmountScript("Enter amount", new InputScript() {

                                @Override
                                public boolean handle(Object value) {
                                    player.action.execute(mix(player, pot, (Integer) value, true), true);
                                    return true;
                                }
                            });
                        }

                        @Override
                        public void fourthOption(Player player) {
                            player.action.execute(mix(player, pot, 14, true), true);
                        }
                    };
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the potion mixing action.
     */
    private static Action<Player> mix(Player player, BrewablePotion pot, int amount, boolean finished) {
        return new Action<>(player, finished ? 2 : 1) {
            int ticks = 0;

            @Override
            public void execute() {
                if (finished) {
                    if (!player.inventory().contains(pot.unfinished) || !player.inventory().contains(pot.secondary)) {
                        stop();
                        return;
                    }

                    player.inventory().remove(pot.secondary);
                    player.inventory().remove(pot.unfinished);
                    if (player.getEquipment().contains(ItemIdentifiers.AMULET_OF_CHEMISTRY) && Utils.rollDie(100, 5)) {
                        player.inventory().add(new Item(pot.resultFourDose));
                        int charges = player.getAttribOr(AttributeKey.AMULET_OF_CHEMISTRY_CHARGES, 5);
                        charges -= 1;
                        player.putAttrib(AttributeKey.AMULET_OF_CHEMISTRY_CHARGES, charges);
                    } else {
                        player.inventory().add(new Item(pot.result));
                    }
                    player.animate(363);

                    player.message("You mix the " + pot.ingredientName + " into your potion.");
                    player.getSkills().addXp(Skills.HERBLORE, pot.exp);

                    if (++ticks == amount) {
                        stop();
                    }
                } else {
                    int vial = (pot == BrewablePotion.ANTIDOTE_PLUS || pot == BrewablePotion.ANTIDOTE_PP) ? COCONUT_MILK : VIAL_OF_WATER;
                    if (!player.inventory().contains(pot.herb) || !player.inventory().contains(vial)) {
                        stop();
                        return;
                    }

                    player.inventory().remove(pot.herb);

                    player.inventory().remove(vial);
                    player.inventory().add(pot.unfinished);
                    player.animate(363);

                    if (vial == VIAL_OF_WATER) {
                        player.message("You put the " + pot.potName + " into the vial of water.");
                    } else {
                        player.message("You put the " + pot.potName + " into the coconut milk.");
                    }

                    if (++ticks == amount) {
                        stop();
                    }
                }
            }

            @Override
            public String getName() {
                return "Herblore mix potion";
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

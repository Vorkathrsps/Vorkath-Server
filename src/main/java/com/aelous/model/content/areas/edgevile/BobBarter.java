package com.aelous.model.content.areas.edgevile;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 11, 2020
 */
public class BobBarter extends Dialogue {

    private final static int BOB_BARTER = NpcIdentifiers.BOB_BARTER_HERBS;

    @Override
    protected void start(Object... parameters) {
        int interaction = player.getAttrib(AttributeKey.INTERACTION_OPTION);
        if (interaction == 1) {
            send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "Hello, chum, fancy buyin' some designer jewellery?", "They've come all the way from Ardougne! Most pukka!");
            setPhase(0);
        } else if (interaction == 2) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Can you show me the prices for herbs and potions?");
            setPhase(15);
        } else if (interaction == 3) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Can you decant things for me?");
            setPhase(14);
        }
    }

    @Override
    protected void next() {
        if (isPhase(0)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Erm, no. I'm all set, thanks.");
            setPhase(1);
        } else if (isPhase(1)) {
            send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "Okay, chum, so what can I do for you?", "I can tell you the very latest herb & potion prices,", "or perhaps I could help you decant your potions.");
            setPhase(2);
        } else if (isPhase(2)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Who are you?", "Can you show me the prices for herbs and potions?", "Can you decant things for me?", "I'll leave you to it.");
            setPhase(3);
        } else if (isPhase(4)) {
            send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "Why I'm Bob! Your friendly seller of smashin' goods!");
            setPhase(5);
        } else if (isPhase(5)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "So what do you have to sell?");
            setPhase(6);
        } else if (isPhase(6)) {
            send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "Oh, not much at the moment. Cuz, ya know, business being", "so well and cushie.");
            setPhase(7);
        } else if (isPhase(7)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "You don't really look like you're being so successful.");
            setPhase(8);
        } else if (isPhase(8)) {
            send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "You plonka! It's all a show, innit!", "If I let people knows I'm in good business they'll want a", "share of the moolah!");
            setPhase(9);
        } else if (isPhase(9)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "You conveniently have a response for everything.");
            setPhase(10);
        } else if (isPhase(10)) {
            if (player.looks().female()) {
                send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "That's the Ardougne way, my darlin'.");
                setPhase(11);
            } else {
                send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "That's the Ardougne way, my good sir.");
                setPhase(11);
            }
        } else if (isPhase(11)) {
            stop();
        } else if (isPhase(12)) {
            send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "Sorry, that feature is currently unavailable.");
            setPhase(13);
        } else if (isPhase(13)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'll leave you to it.");
            setPhase(11);
        } else if (isPhase(14)) {
            if (!canDecant(player)) {
                send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "I don't think you've got anything that I can combine!");
                setPhase(11);
            } else {
                decant(player);
                send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "There, all done!");
                setPhase(11);
            }
        } else if (isPhase(15)) {
            send(DialogueType.NPC_STATEMENT, BOB_BARTER, Expression.CALM_TALK, "Sorry, that feature is currently unavailable.");
            setPhase(16);
        } else if (isPhase(16)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'll leave you to it.");
            setPhase(11);
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(3)) {
            if (option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Who are you?");
                setPhase(4);
            } else if (option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Can you show me the prices for herbs and potions?");
                setPhase(12);
            } else if (option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Can you decant things for me?");
                setPhase(14);
            } else if (option == 4) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'll leave you to it.");
                setPhase(11);
            }
        }
    }

    private enum Potions {
        ATTACK(125, 123, 121, 2428, 230),
        NOTED_ATTACK(126, 124, 122, 2429, 230),
        SUPER_ATTACK(149, 147, 145, 2436, 230),
        NOTED_SUPER_ATTACK(150, 148, 146, 2437, 230),
        STRENGTH(119, 117, 115, 113, 230),
        NOTED_STRENGTH(120, 118, 116, 114, 230),
        SUPER_STRENGTH(161, 159, 157, 2440, 230),
        NOTED_SUPER_STRENGTH(162, 160, 158, 2441, 230),
        DEFENCE(137, 135, 133, 2432, 230),
        NOTED_DEFENCE(138, 136, 134, 2433, 230),
        SUPER_DEFENCE(167, 165, 163, 2442, 230),
        NOTED_SUPER_DEFENCE(168, 166, 164, 2443, 230),
        RANGING(173, 171, 169, 2444, 230),
        NOTED_RANGING(174, 172, 170, 2445, 230),
        MAGIC(3046, 3044, 3042, 3040, 230),
        NOTED_MAGIC(3047, 3045, 3043, 3041, 230),
        COMBAT(9745, 9743, 9741, 9739, 230),
        NOTED_COMBAT(9746, 9744, 9742, 9740, 230),
        SUPER_COMBAT(12701, 12699, 12697, 12695, 230),
        NOTED_SUPER_COMBAT(12702, 12700, 12698, 12696, 230),
        PRAYER(143, 141, 139, 2434, 230),
        NOTED_PRAYER(144, 142, 140, 2435, 230),
        RESTORE(131, 129, 127, 2430, 230),
        NOTED_RESTORE(132, 130, 128, 2431, 230),
        SUPER_RESTORE(3030, 3028, 3026, 3024, 230),
        NOTED_SUPER_RESTORE(3031, 3029, 3027, 3025, 230),
        ANTI_FIRE(2458, 2456, 2454, 2452, 230),
        NOTED_ANTI_FIRE(2459, 2457, 2455, 2453, 230),
        ZAMORAK_BREW(193, 191, 189, 2450, 230),
        NOTED_ZAMORAK_BREW(194, 192, 190, 2451, 230),
        SARADOMIN_BREW(6691, 6689, 6687, 6685, 230),
        NOTED_SARADOMIN_BREW(6692, 6690, 6688, 6686, 230),
        ENERGY(3014, 3012, 3010, 3008, 230),
        NOTED_ENERGY(3015, 3013, 3011, 3009, 230),
        SUPER_ENERGY(3022, 3020, 3018, 3016, 230),
        NOTED_SUPER_ENERGY(3023, 3021, 3019, 3017, 230),
        ANTIPOSION(179, 177, 175, 2446, 230),
        NOTED_ANTIPOSION(180, 178, 176, 2447, 230),
        SUPER_ANTIPOISON(185, 183, 181, 2448, 230),
        NOTED_SUPER_ANTIPOISON(186, 184, 182, 2449, 230),
        RELICYMS_BALM(4848, 4846, 4844, 4842, 230),
        NOTED_RELICYMS_BALM(4849, 4847, 4845, 4843, 230),
        AGILITY(3038, 3036, 3034, 3032, 230),
        NOTED_AGILITY(3039, 3037, 3035, 3033, 230),
        FISHING(155, 153, 151, 2438, 230),
        NOTED_FISHING(156, 154, 152, 2439, 230),
        STAMINA(12631, 12629, 12627, 12625, 230),
        NOTED_STAMINA(12632, 12630, 12628, 12626, 230),
        SANFEW(10931, 10929, 10927, 10925, 230),
        //RECOVER_SPECIAL(15303, 15302, 15301, 15300, 230),
        NTOED_SANFEW(10932, 10930, 10928, 10926, 230),
        GUTHIX_REST(4423, 4421, 4419, 4417, 1980),

        // Divines
        DIVINE_SUPER_COMBAT(23694, 23691, 23688, 23685, 230),
        NOTED_DIVINE_SUPER_COMBAT(23695, 23692, 23689, 23686, 230),
        DIVINE_SUPER_ATTACK(23706, 23703, 23700, 23697, 230),
        NOTED_DIVINE_SUPER_ATTACK(23707, 23704, 23701, 23698, 230),
        DIVINE_SUPER_STRENGTH(23718, 23715, 23712, 23709, 230),
        NOTED_DIVINE_SUPER_STRENGTH(23719, 23716, 23713, 23710, 230),
        DIVINE_SUPER_DEFENCE(23730, 23727, 23724, 23721, 230),
        NOTED_DIVINE_SUPER_DEFENCE(23731, 23728, 23725, 23722, 230),
        DIVINE_SUPER_RANGED(23742, 23739, 23736, 23733, 230),
        NOTED_DIVINE_SUPER_RANGED(23743, 23740, 23737, 23734, 230),
        DIVINE_SUPER_MAGIC(23754, 23751, 23748, 23745, 230),
        NOTED_DIVINE_SUPER_MAGIC(23755, 23752, 23749, 23746, 230);



        private final int one, two, three, four, emptyItem;

        Potions(int one, int two, int three, int four, int emptyItem) {
            this.one = one;
            this.two = two;
            this.three = three;
            this.four = four;
            this.emptyItem = emptyItem;
        }
    }

    private static boolean canDecant(Player player) {
        for (Potions potion : Potions.values()) {
            if (player.inventory().contains(potion.one) || player.inventory().contains(potion.two) || player.inventory().contains(potion.three) || player.inventory().contains(potion.four)) {
                return true;
            }
        }
        return false;
    }

    public static void decant(Player player) {
        for (Potions potion : Potions.values()) {

            int four = potion.four;
            int three = potion.three;
            int two = potion.two;
            int one = potion.one;

            int totalDoses = 0;
            int leftOver = 0;
            int emptyPotions = 0;

            if (player.inventory().contains(three)) {
                totalDoses += (3 * player.inventory().count(potion.three));
                emptyPotions += player.inventory().count(potion.three);
                player.inventory().remove(new Item(three, player.inventory().count(potion.three)), true);
            }
            if (player.inventory().contains(two)) {
                totalDoses += (2 * player.inventory().count(potion.two));
                emptyPotions += player.inventory().count(potion.two);
                player.inventory().remove(new Item(two, player.inventory().count(potion.two)), true);
            }
            if (player.inventory().contains(one)) {
                totalDoses += (1 * player.inventory().count(potion.one));
                emptyPotions += player.inventory().count(potion.one);
                player.inventory().remove(new Item(one, player.inventory().count(potion.one)), true);
            }
            if (totalDoses > 0)
                if (totalDoses >= 4)
                    player.inventory().add(new Item(four, totalDoses / 4), true);
                else if (totalDoses == 3)
                    player.inventory().add(new Item(three, 1), true);
                else if (totalDoses == 2)
                    player.inventory().add(new Item(two, 1), true);
                else if (totalDoses == 1)
                    player.inventory().add(new Item(one, 1), true);
            if ((totalDoses % 4) != 0) {
                if (player.inventory().count(one) == 1 || player.inventory().count(two) == 1 || player.inventory().count(three) == 1) {
                    player.message(Color.RED.wrap("There are no potions to decant."));
                    continue;
                }
                emptyPotions -= 1;
                leftOver = totalDoses % 4;
                if (leftOver == 3)
                    player.inventory().add(new Item(three, 1), true);
                else if (leftOver == 2)
                    player.inventory().add(new Item(two, 1), true);
                else if (leftOver == 1)
                    player.inventory().add(new Item(one, 1), true);
            }
            emptyPotions -= (totalDoses / 4);
            if (emptyPotions != 0)
                player.inventory().add(new Item(potion.emptyItem, emptyPotions), true);
        }
        player.message(Color.BLUE.wrap("You decant the potions in your inventory."));
    }
}

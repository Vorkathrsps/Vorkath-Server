package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.account.AccountSelection;
import com.aelous.model.content.bountyhunter.BountyHunter;
import com.aelous.model.content.new_players.Tutorial;
import com.aelous.model.content.packet_actions.interactions.buttons.Buttons;
import com.aelous.core.task.Task;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import com.aelous.utility.ItemIdentifiers;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This packet listener manages a button that the player has clicked upon.
 *
 * @author Gabriel Hannason
 */
public class ButtonClickPacketListener implements PacketListener {

    private static final Logger logger = LogManager.getLogger(ButtonClickPacketListener.class);

    public static final int FIRST_DIALOGUE_OPTION_OF_FIVE = 2494;
    public static final int SECOND_DIALOGUE_OPTION_OF_FIVE = 2495;
    public static final int THIRD_DIALOGUE_OPTION_OF_FIVE = 2496;
    public static final int FOURTH_DIALOGUE_OPTION_OF_FIVE = 2497;
    public static final int FIFTH_DIALOGUE_OPTION_OF_FIVE = 2498;
    public static final int FIRST_DIALOGUE_OPTION_OF_FOUR = 2482;
    public static final int SECOND_DIALOGUE_OPTION_OF_FOUR = 2483;
    public static final int THIRD_DIALOGUE_OPTION_OF_FOUR = 2484;
    public static final int FOURTH_DIALOGUE_OPTION_OF_FOUR = 2485;
    public static final int FIRST_DIALOGUE_OPTION_OF_THREE = 2471;
    public static final int SECOND_DIALOGUE_OPTION_OF_THREE = 2472;
    public static final int THIRD_DIALOGUE_OPTION_OF_THREE = 2473;
    public static final int FIRST_DIALOGUE_OPTION_OF_TWO = 2461;
    public static final int SECOND_DIALOGUE_OPTION_OF_TWO = 2462;
    public static final int LOGOUT = 2458;
    public static final int DUEL_LOAD_PREVIOUS_SETTINGS = 24492;

    public static final int[] ALL = new int[]{2494, 2495, 2496, 2497, 2498, 2482, 2483, 2484, 2485, 2471, 2472, 2473, 2461, 2462, 2458, 24492};

    public final static Item[] BANK_ITEMS = {
        new Item(ItemIdentifiers.ANCESTRAL_HAT, 20000), // Scim
        new Item(ItemIdentifiers.ANCESTRAL_ROBE_TOP, 20000), // Dagger
        new Item(ItemIdentifiers.ANCESTRAL_ROBE_BOTTOM, 20000), // Mystic
        new Item(ItemIdentifiers.ARCANE_SPIRIT_SHIELD, 20000), // Mystic
        new Item(ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD, 20000), // Enchanted
        new Item(ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD, 20000), // farseer helm
        new Item(ItemIdentifiers.ANCIENT_WYVERN_SHIELD, 20000), // rune full helm
        new Item(ItemIdentifiers.DRAGONFIRE_SHIELD, 20000), // d long
        new Item(ItemIdentifiers.DRAGONFIRE_WARD, 20000), // ancient staff
        new Item(ItemIdentifiers.DINHS_BULWARK, 20000), // Mystic
        new Item(ItemIdentifiers.AVERNIC_DEFENDER, 20000), // Mystic
        new Item(ItemIdentifiers.DRAGON_DEFENDER, 20000), // Mystic
        new Item(ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD, 20000), // Mystic
        new Item(ItemIdentifiers.THAMMARONS_SCEPTRE, 20000), // Mystic
        new Item(ItemIdentifiers.ANCIENT_SCEPTRE, 20000), // Mystic
        new Item(ItemIdentifiers.STAFF_OF_LIGHT, 20000), // enchanted
        new Item(ItemIdentifiers.HARMONISED_NIGHTMARE_STAFF, 20000), // hat
        new Item(ItemIdentifiers.VOLATILE_NIGHTMARE_STAFF, 20000), // rune
        new Item(ItemIdentifiers.ELDRITCH_NIGHTMARE_STAFF, 20000), // mace
        new Item(ItemIdentifiers.STAFF_OF_THE_DEAD, 20000), // crossbow
        new Item(ItemIdentifiers.STAFF_OF_BALANCE, 20000), // Mystic
        new Item(ItemIdentifiers.DRAGON_CROSSBOW, 20000), // Mystic
        new Item(ItemIdentifiers.ARMADYL_CROSSBOW, 20000), // Mystic
        new Item(ItemIdentifiers.ZARYTE_CROSSBOW, 20000), // enchanted
        new Item(ItemIdentifiers.TWISTED_BOW, 20000), // enchanted
        new Item(ItemIdentifiers.DRAGON_HUNTER_CROSSBOW, 20000), // helm
        new Item(ItemIdentifiers.KARILS_CROSSBOW, 20000), // rune
        new Item(ItemIdentifiers.DHAROKS_ARMOUR_SET, 20000), // dagger
        new Item(ItemIdentifiers.KARILS_ARMOUR_SET, 20000), // avas
        new Item(ItemIdentifiers.AHRIMS_ARMOUR_SET, 20000), // avas
        new Item(ItemIdentifiers.GUTHANS_ARMOUR_SET, 20000), // Mystic
        new Item(ItemIdentifiers.VERACS_ARMOUR_SET, 20000), // Mystic
        new Item(ItemIdentifiers.VESTAS_LONGSWORD, 20000), // Mystic
        new Item(ItemIdentifiers.VESTAS_CHAINBODY, 20000), // wiz boots
        new Item(ItemIdentifiers.VESTAS_PLATESKIRT, 20000), // helm
        new Item(ItemIdentifiers.MORRIGANS_COIF, 20000), // rune boots
        new Item(ItemIdentifiers.MORRIGANS_LEATHER_BODY, 20000), // hides
        new Item(ItemIdentifiers.MORRIGANS_LEATHER_CHAPS, 20000), // hides
        new Item(ItemIdentifiers.STATIUSS_FULL_HELM, 20000), // hides
        new Item(ItemIdentifiers.STATIUSS_PLATEBODY, 20000), // hides
        new Item(ItemIdentifiers.STATIUSS_PLATELEGS, 20000), // hides
        new Item(ItemIdentifiers.STATIUSS_WARHAMMER, 20000), // climbers
        new Item(ItemIdentifiers.DRAGON_BOOTS, 20000), // rune
        new Item(ItemIdentifiers.PRIMORDIAL_BOOTS, 20000), // rune
        new Item(ItemIdentifiers.ETERNAL_BOOTS, 20000), // god book
        new Item(ItemIdentifiers.PEGASIAN_BOOTS, 20000), // god book
        new Item(ItemIdentifiers.BOOTS_OF_BRIMSTONE, 20000), // hides
        new Item(ItemIdentifiers.INFINITY_BOOTS, 20000), // hides
        new Item(ItemIdentifiers.AMULET_OF_BLOOD_FURY, 20000), // hides
        new Item(ItemIdentifiers.AMULET_OF_FURY_OR, 20000), // ghostly
        new Item(ItemIdentifiers.AMULET_OF_TORTURE, 20000), // ghostly
        new Item(ItemIdentifiers.NECKLACE_OF_ANGUISH, 20000),
        new Item(ItemIdentifiers.OCCULT_NECKLACE, 20000), // Scim
        new Item(ItemIdentifiers.BERSERKER_NECKLACE, 20000), // Dagger
        new Item(ItemIdentifiers.TORMENTED_BRACELET, 20000), // Mystic
        new Item(ItemIdentifiers.BARROWS_GLOVES, 20000), // Mystic
        new Item(ItemIdentifiers.FEROCIOUS_GLOVES, 20000), // Enchanted
        new Item(ItemIdentifiers.INFERNAL_CAPE, 20000), // farseer helm
        new Item(ItemIdentifiers.INFERNAL_MAX_CAPE, 20000), // rune full helm
        new Item(ItemIdentifiers.IMBUED_ZAMORAK_CAPE, 20000), // d long
        new Item(ItemIdentifiers.IMBUED_SARADOMIN_CAPE, 20000), // ancient staff
        new Item(ItemIdentifiers.IMBUED_GUTHIX_CAPE, 20000), // Mystic
        new Item(ItemIdentifiers.BERSERKER_RING_I_26770, 20000), // Mystic
        new Item(ItemIdentifiers.SEERS_RING_I_26767, 20000), // enchanted
        new Item(ItemIdentifiers.ARCHERS_RING_I_26768, 20000), // hat
        new Item(ItemIdentifiers.BRIMSTONE_RING, 20000), // rune
        new Item(ItemIdentifiers.MASORI_ASSEMBLER, 20000), // mace
        new Item(ItemIdentifiers.AVAS_ACCUMULATOR, 20000), // crossbow
        new Item(ItemIdentifiers.MASORI_MASK, 20000), // Mystic
        new Item(ItemIdentifiers.MASORI_BODY, 20000), // Mystic
        new Item(ItemIdentifiers.MASORI_CHAPS, 20000), // Mystic
        new Item(ItemIdentifiers.ARMADYL_CHESTPLATE, 20000), // enchanted
        new Item(ItemIdentifiers.ARMADYL_CHAINSKIRT, 20000), // helm
        new Item(ItemIdentifiers.ARMADYL_GODSWORD, 20000), // rune
        new Item(ItemIdentifiers.BANDOS_GODSWORD, 20000), // dagger
        new Item(ItemIdentifiers.ZAMORAK_GODSWORD, 20000), // avas
        new Item(ItemIdentifiers.ANCIENT_GODSWORD, 20000), // avas
        new Item(ItemIdentifiers.DRAGON_CLAWS, 20000), // Mystic
        new Item(ItemIdentifiers.VIGGORAS_CHAINMACE, 20000), // Mystic
        new Item(ItemIdentifiers.URSINE_CHAINMACE, 20000), // Mystic
        new Item(ItemIdentifiers.CRAWS_BOW, 20000), // wiz boots
        new Item(ItemIdentifiers.WEBWEAVER_BOW, 20000), // helm
        new Item(ItemIdentifiers.ACCURSED_SCEPTRE_A, 20000), // rune boots
        new Item(ItemIdentifiers.THAMMARONS_SCEPTRE, 20000), // hides
        new Item(ItemIdentifiers.SALVE_AMULETEI, 20000), // hides
        new Item(ItemIdentifiers.AMETHYST_ARROW, 1000000), // hides
        new Item(ItemIdentifiers.RUNE_ARROW, 1000000), // hides
        new Item(ItemIdentifiers.DRAGON_ARROW, 1000000), // hides
        new Item(2436, 20000), // pots
        new Item(2440, 20000), // pots
        new Item(2442, 20000), // pots
        new Item(2444, 20000), // pots
        new Item(3040, 20000), // pots
        new Item(10925, 20000), // pots
        new Item(3024, 20000), // pots
        new Item(6685, 20000), // pots
        new Item(145, 20000), // pots
        new Item(157, 20000), // pots
        new Item(163, 20000), // pots
        new Item(169, 20000), // pots
        new Item(3042, 20000), // pots
        new Item(10927, 20000), // pots
        new Item(3026, 20000), // pots
        new Item(6689, 20000), // pots
        new Item(147, 20000), // pots
        new Item(159, 20000), // pots
        new Item(165, 20000), // pots
        new Item(171, 20000), // pots
        new Item(3044, 20000), // pots
        new Item(10929, 20000), // pots
        new Item(3028, 20000), // pots
        new Item(6687, 20000), // pots
        new Item(149, 20000), // pots
        new Item(161, 20000), // pots
        new Item(167, 20000), // pots
        new Item(173, 20000), // pots
        new Item(3046, 20000), // pots
        new Item(10931, 20000), // pots
        new Item(3030, 20000), // pots
        new Item(6691, 20000), // pots
        new Item(385, 20000), // sharks
        new Item(3144, 20000), // karambwan
        new Item(560, 20000000), // runes
        new Item(565, 20000000), // runes
        new Item(555, 20000000), // runes
        new Item(562, 20000000), // runes
        new Item(557, 20000000), // runes
        new Item(559, 20000000), // runes
        new Item(564, 20000000), // runes
        new Item(554, 20000000), // runes
        new Item(9075, 20000000), // runes
        new Item(556, 20000000), // runes
        new Item(563, 20000000), // runes
        new Item(559, 20000000), // runes
        new Item(566, 20000000), // runes
        new Item(561, 20000000), // runes
        new Item(9241, 20000), // bolts
        new Item(9244, 20000), // bolts
        new Item(9245, 20000), // bolts
        new Item(9243, 20000), // bolts
        new Item(9242, 20000), // bolts
        new Item(ItemIdentifiers.DIAMOND_DRAGON_BOLTS_E, 1000000), // enchanted
        new Item(ItemIdentifiers.DRAGONSTONE_DRAGON_BOLTS_E, 1000000), // enchanted
        new Item(10828, 20000), // neit helm
        new Item(2412, 20000), // sara god cape
        new Item(7458, 20000), // mithril gloves for pures
        new Item(7462, 20000), // gloves
        new Item(11978, 20000), // glory (6)
    };

    public static void main(String[] args) {
        final Packet packet = new Packet(-1, Unpooled.copiedBuffer(new byte[]{(byte) 0, (byte) 0, (byte) 101, (byte) -9}));
        int r = packet.readInt();
        System.out.println("was " + Arrays.toString(packet.getBuffer().array()) + " -> " + r);
    }

    @Override
    public void handleMessage(Player player, Packet packet) {
        final int button = packet.readInt();
        parseButtonPacket(player, button);
    }

    public void parseButtonPacket(Player player, int button) {
        if (player.dead()) {
            return;
        }

        if (player.askForAccountPin() && button != 2458) {//Allowed to logout
            player.sendAccountPinMessage();
            return;
        }

        player.afkTimer.reset();

        player.debugMessage("button=" + button);
        //System.out.println("button=" + button);

        if (player.getTeleportInterface().handleButton(button, -1)) {
            return;
        }

        if (button == 53729) {
            BountyHunter.skip(player);
            return;
        }

        if (button == DUEL_LOAD_PREVIOUS_SETTINGS) {
            if (!GameServer.properties().enableLoadLastDuelPreset) {
                player.message("That feature is currently disabled.");
                return;
            }
            player.getDueling().handleSavedConfig();
        }

        if (button == FIRST_DIALOGUE_OPTION_OF_FIVE || button == FIRST_DIALOGUE_OPTION_OF_FOUR
            || button == FIRST_DIALOGUE_OPTION_OF_THREE || button == FIRST_DIALOGUE_OPTION_OF_TWO) {
            if (player.getDialogueManager().isActive()) {
                if (player.getDialogueManager().select(1)) {
                    return;
                }
            }
        }

        if (button == SECOND_DIALOGUE_OPTION_OF_FIVE || button == SECOND_DIALOGUE_OPTION_OF_FOUR
            || button == SECOND_DIALOGUE_OPTION_OF_THREE || button == SECOND_DIALOGUE_OPTION_OF_TWO) {
            if (player.getDialogueManager().isActive()) {
                if (player.getDialogueManager().select(2)) {
                    return;
                }
            }
        }

        if (button == THIRD_DIALOGUE_OPTION_OF_FIVE || button == THIRD_DIALOGUE_OPTION_OF_FOUR
            || button == THIRD_DIALOGUE_OPTION_OF_THREE) {
            if (player.getDialogueManager().isActive()) {
                if (player.getDialogueManager().select(3)) {
                    return;
                }
            }
        }

        if (button == FOURTH_DIALOGUE_OPTION_OF_FIVE || button == FOURTH_DIALOGUE_OPTION_OF_FOUR) {
            if (player.getDialogueManager().isActive()) {
                if (player.getDialogueManager().select(4)) {
                    return;
                }
            }
        }

        if (button == FIFTH_DIALOGUE_OPTION_OF_FIVE) {
            if (player.getDialogueManager().isActive()) {
                if (player.getDialogueManager().select(5)) {
                    return;
                }
            }
        }

        //If the player accepts their appearance then they can continue making their account.
        if (player.<Boolean>getAttribOr(AttributeKey.NEW_ACCOUNT, false) && button == 3651) {
            if (GameServer.properties().pvpMode) {
                //Tutorial.start(player);
                AccountSelection.open(player);
            }
            return;
        }

        if (player.locked()) {
            // unique case: since prayers always 'activate' when clicked client side, we'll try to just wait until
            // we unlock and trigger the button so the client stays in sync.
            DefaultPrayerData defaultPrayerData = DefaultPrayerData.getActionButton().get(button);
            if (defaultPrayerData != null) {

                // store btn
                HashSet<Integer> clicks = player.<HashSet<Integer>>getAttribOr(AttributeKey.PRAYER_DELAYED_ACTIVATION_CLICKS, new HashSet<Integer>());
                clicks.add(button); // one task but you can spam different prayers. queue them all up until task is over.
                player.putAttrib(AttributeKey.PRAYER_DELAYED_ACTIVATION_CLICKS, clicks);

                // fetch task
                Task task = player.<Task>getAttribOr(AttributeKey.PRAYER_DELAYED_ACTIVATION_TASK, null);
                if (task == null) {

                    // build task logic
                    task = Task.repeatingTask(t -> {

                        // this is a long ass pause homie
                        if (t.tick > 10) {
                            t.stop();
                            player.clearAttrib(AttributeKey.PRAYER_DELAYED_ACTIVATION_TASK);
                            for (Integer click : clicks) {
                                DefaultPrayerData p1 = DefaultPrayerData.getActionButton().get(click);
                                if (p1 != null) // resync previous state
                                    player.getPacketSender().sendConfig(p1.getConfigId(), player.getPrayerActive()[p1.ordinal()] ? 1 : 0);
                            }
                            clicks.clear();
                            return;
                        }

                        // tele has finished or w.e was locking us
                        if (!player.locked()) {
                            t.stop();
                            player.clearAttrib(AttributeKey.PRAYER_DELAYED_ACTIVATION_TASK);
                            // now trigger we are unlocked
                            for (Integer click : clicks) {
                                parseButtonPacket(player, click);
                            }
                            clicks.clear();
                        }
                    });
                    player.putAttrib(AttributeKey.PRAYER_DELAYED_ACTIVATION_TASK, task);
                }
            }
            return;
        }

        if (PacketInteractionManager.checkButtonInteraction(player, button)) {
            return;
        }

        Buttons.handleButton(player, button);
    }
}

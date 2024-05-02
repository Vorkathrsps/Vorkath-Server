package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.content.account.AccountSelection;
import com.cryptic.model.content.bountyhunter.BountyHunter;
import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.content.packet_actions.interactions.buttons.Buttons;
import com.cryptic.core.task.Task;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.rights.PlayerRights;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import com.cryptic.utility.ItemIdentifiers;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static com.cryptic.model.entity.attributes.AttributeKey.DAILY_TASKS_LIST;
import static com.cryptic.model.entity.attributes.AttributeKey.DAILY_TASKS_POINTS;

/**
 * This packet listener manages a button that the player has clicked upon.
 *
 * @author Gabriel Hannason
 */
public class ButtonClickPacketListener implements PacketListener {
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
    public static final int[] DIALOGUE_BUTTONS = new int[]{SECOND_DIALOGUE_OPTION_OF_TWO, FIRST_DIALOGUE_OPTION_OF_TWO, THIRD_DIALOGUE_OPTION_OF_THREE, SECOND_DIALOGUE_OPTION_OF_THREE, FIRST_DIALOGUE_OPTION_OF_THREE, FOURTH_DIALOGUE_OPTION_OF_FOUR, THIRD_DIALOGUE_OPTION_OF_FOUR, SECOND_DIALOGUE_OPTION_OF_FOUR, FIRST_DIALOGUE_OPTION_OF_FOUR, FIFTH_DIALOGUE_OPTION_OF_FIVE, FOURTH_DIALOGUE_OPTION_OF_FIVE, THIRD_DIALOGUE_OPTION_OF_FIVE, SECOND_DIALOGUE_OPTION_OF_FIVE, FIRST_DIALOGUE_OPTION_OF_FIVE};

    public static final int[] ALL = new int[]{2494, 2495, 2496, 2497, 2498, 2482, 2483, 2484, 2485, 2471, 2472, 2473, 2461, 2462, 2458, 24492};
    public static final int[] TELEPORT_BUTTONS = new int[]{13035, 13045, 13053, 13061, 13069, 13079, 13087, 13095, 1164, 1167, 1170, 1174, 1541, 7455, 31674, 30064, 30075, 30083, 30106, 30114, 30138, 30146, 30162, 30170, 30226, 30250, 30258, 30266, 30274, 40305, 40307, 40308, 40309, 40310, 40312, 40316, 40323, 40326, 40334, 40343};
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

        if (player.askForAccountPin() && button != 2458) {
            player.sendAccountPinMessage();
            return;
        }

        player.afkTimer.reset();

        if (player.getDialogueManager().isActive() && !ArrayUtils.contains(DIALOGUE_BUTTONS, button)) {
            player.getInterfaceManager().closeDialogue();
        }

        if (PlayerRights.OWNER.equals(player.getPlayerRights())) {
            player.debugMessage("button=" + button);
        }

        if (player.getTeleportInterface().handleButton(button, -1)) {
            return;
        }

        if (button == 80017) {
            player.getInterfaceManager().open(80750);
            var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
            var challengeListText = 80778;
            for (int i = 0; i < tasks.size(); i++) {
                player.getPacketSender().sendString(challengeListText + (i * 2), tasks.get(i).taskName);
            }
            DailyTaskManager.displayTaskInfo(player, tasks.getFirst());
            player.getPacketSender().sendString(80756, "Reward points: " + player.getAttribOr(DAILY_TASKS_POINTS, 0));
            return;
        }

        if (button == 53729) {
            BountyHunter.skip(player);
            return;
        }

        if (ArrayUtils.contains(TELEPORT_BUTTONS, button)) {
            player.setCurrentTabIndex(1);
            player.getInterfaceManager().open(88000);
            player.getnewteleInterface().drawInterface(88005);
            return;
        }

        if (button == 82002) {
            Shop.closeShop(player);
            return;
        }

        if (button == 27653) {
            ItemWeight.calculateWeight(player);
            player.getBonusInterface().showEquipmentInfo();
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

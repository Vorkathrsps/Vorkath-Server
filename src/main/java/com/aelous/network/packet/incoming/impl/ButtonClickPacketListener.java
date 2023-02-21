package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.account.AccountSelection;
import com.aelous.model.content.bountyhunter.BountyHunter;
import com.aelous.model.content.packet_actions.interactions.buttons.Buttons;
import com.aelous.core.task.Task;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
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

    public static final int[] ALL = new int[] {2494, 2495, 2496, 2497, 2498, 2482, 2483, 2484, 2485, 2471, 2472, 2473, 2461, 2462, 2458, 24492};

    public static void main(String[] args) {
        final Packet packet = new Packet(-1, Unpooled.copiedBuffer(new byte[]{(byte) 0, (byte) 0, (byte) 101, (byte) -9}));
        int r = packet.readInt();
        System.out.println("was "+ Arrays.toString(packet.getBuffer().array())+" -> "+r);
    }

    @Override
    public void handleMessage(Player player, Packet packet) {
        final int button = packet.readInt();
        parseButtonPacket(player, button);
        System.out.println("buttonId=" + button);
    }

    public void parseButtonPacket(Player player, int button) {
        if (player.dead()) {
            return;
        }

        if(player.askForAccountPin() && button != 2458) {//Allowed to logout
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
        if (player.<Boolean>getAttribOr(AttributeKey.NEW_ACCOUNT,false) && button == 3651) {
            if (GameServer.properties().pvpMode) {
                //Tutorial.start(player);
                AccountSelection.open(player);
            }
            return;
        }

        if (PacketInteractionManager.checkButtonInteraction(player, button)) {
            return;
        }

        Buttons.handleButton(player, button);
    }
}

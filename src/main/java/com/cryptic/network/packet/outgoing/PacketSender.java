package com.cryptic.network.packet.outgoing;

import com.cryptic.GameConstants;
import com.cryptic.interfaces.InterfaceType;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.content.teleport.world_teleport_manager.TeleportData;
import com.cryptic.clientscripts.MessageType;
import com.cryptic.clientscripts.interfaces.EventConstants;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.player.InfectionType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.PlayerInteractingOption;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.*;
import com.cryptic.network.packet.outgoing.message.ComponentVisibility;
import com.cryptic.utility.WidgetUtil;
import kotlin.ranges.IntRange;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class manages making the packets that will be sent (when called upon) onto
 * the associated player's client.
 * <p>
 * This class is used for S2C (Server to Client) packets.
 *
 * @author relex lawl & Gabbe
 */
@SuppressWarnings("UnusedReturnValue")
public final class PacketSender {
    private static final Logger logger = LogManager.getLogger(PacketSender.class);
    private final ArrayList<Integer> walkableInterfaceList = new ArrayList<>();

    public void resetParallelInterfaces() {
        new ArrayList<>(walkableInterfaceList).forEach(i -> {
            sendParallelInterfaceVisibility(i, false); // cant remove when iterating over (CCMex)
        });
        walkableInterfaceList.clear();
    }

    @Getter
    public boolean updateRegions;

    public void sendParallelInterfaceVisibility(int interfaceId, boolean visible) {
        PacketBuilder out = new PacketBuilder(210);
        if (player != null) {
            if (visible) {
                if (walkableInterfaceList.contains(interfaceId)) {
                    player.debug("skip sending walkable, already open " + interfaceId);
                } else {
                    walkableInterfaceList.add(interfaceId);
                }
            } else {
                if (!walkableInterfaceList.contains(interfaceId)) {
                    player.debug("skip sending walkable, not open " + interfaceId);
                }
                walkableInterfaceList.remove((Object) interfaceId); // MUSt BE OBJECT casted otherwise it'll try removing as index
            }
            out.putInt(interfaceId);
            out.put(visible ? 1 : 0);
            player.getSession().write(out);
        }
    }

    public void sendClientInstruction(int instruction, Object... args) {
        PacketBuilder bldr = new PacketBuilder(245, PacketType.VARIABLE_SHORT);
        bldr.putShort(instruction);
        StringBuilder argBuilder = new StringBuilder();
        final int argumentSize = args.length;
        Object[] arguments = new Object[argumentSize];
        int intVals = 0;
        int stringVals = 0;
        for (int index = args.length - 1; index >= 0; index--) {
            Object o = args[index];
            arguments[index] = args[argumentSize - index - 1];
            if (o instanceof String) {
                argBuilder.append("s");
                stringVals++;
            } else {
                argBuilder.append("i");
                intVals++;
            }
        }
        String argTypes = argBuilder.toString();
        char[] typeArray = argTypes.toCharArray();
        bldr.putString(argTypes);
        bldr.putShort(intVals);
        bldr.putShort(stringVals);
        for (int index = 0; index < typeArray.length; index++) {
            char type = typeArray[index];
            if (type == 's') {
                bldr.putString((String) arguments[index]);
            } else {
                bldr.putInt((int) arguments[index]);
            }
        }
        player.getSession().write(bldr);
    }

    public PacketSender sendEntityFeed(String opponent, int HP, int maxHP) {
        // so its literally gonna be going thru, checking anything > 0 is fixed,
        // anything -1 should be variable,
        // -2 will be var short
        PacketBuilder out = new PacketBuilder(175, PacketType.VARIABLE);
        out.putString(opponent == null ? "null" : opponent).putShort(HP).putShort(maxHP);
        player.getSession().write(out);
        return this;
    }

    public PacketSender resetEntityFeed() {
        sendEntityFeed("", 0, 0);
        return this;
    }

    /**
     * Sends some information to the client about screen fading.
     *
     * @param text    the text that will be displayed in the center of the screen
     * @param state   the state should be either 0, -1, or 1.
     * @param seconds the amount of time in seconds it takes for the fade to transition.
     *                <p>
     *                If the state is -1 then the screen fades from black to transparent. When the state is +1 the screen fades from transparent to black. If the state is 0 all drawing
     *                is stopped.
     */
    public PacketSender sendScreenFade(String text, int state, int seconds) {
        PacketBuilder out = new PacketBuilder(13, PacketType.VARIABLE);
        if (seconds < 1 && state != 0) {
            throw new IllegalArgumentException("The amount of seconds cannot be less than one.");
        }
        out.putString(text);
        out.put(state);
        out.put(seconds);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendProgressBar(int child, int progress) {
        PacketBuilder out = new PacketBuilder(237);
        out.putInt(child);
        out.putShort(progress);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendJingle(int id) {
        PacketBuilder out = new PacketBuilder(174);
        out.put(0);
        out.putShort(id);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendSong(int id) {
        PacketBuilder out = new PacketBuilder(74);
        out.putShort(id);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendSoundEffect(int trackId, int repeat, int delay) {
        PacketBuilder out = new PacketBuilder(105);
        out.putShort(trackId);
        out.put(repeat);
        out.putShort(delay);
        player.getSession().write(out);
        return this;
    }

    public PacketSender setWidgetActive(int child, boolean active) {
        PacketBuilder out = new PacketBuilder(143);
        out.put(active ? 1 : 0);
        out.putShort(child);
        player.getSession().write(out);
        return this;
    }


    public PacketSender setClickedText(int interfaceId, boolean state) {
        PacketBuilder out = new PacketBuilder(239);
        out.put(state ? 1 : 0);
        out.putInt(interfaceId);
        player.getSession().write(out);
        return this;
    }

    public PacketSender updateWidgetTooltipText(int id, String text) {
        PacketBuilder out = new PacketBuilder(207, PacketType.VARIABLE);
        out.putShort(id);
        out.putString(text);
        player.getSession().write(out);
        return this;
    }

    public PacketSender changeWidgetText(int id, String text) {
        PacketBuilder out = new PacketBuilder(209, PacketType.VARIABLE);
        out.putShort(id);
        out.putString(text);
        player.getSession().write(out);
        return this;
    }

    public PacketSender updateTab(int value, int id) {
        PacketBuilder out = new PacketBuilder(189);
        out.putShort(value).putShort(id);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendHitPredictor(int hit) {
        PacketBuilder out = new PacketBuilder(135);
        out.putShort(hit);
        player.getSession().write(out);
        return this;
    }

    public PacketSender clearFavorites() {
        PacketBuilder out = new PacketBuilder(66, PacketType.VARIABLE_SHORT);
        out.put(1);
        out.put(0);
        out.putShort(5);//0 for a set name
        out.putString("");
        System.err.println("clearing favorites...???");
        player.getSession().write(out);
        return this;
    }

    public PacketSender addFavoriteTeleport(TeleportData data) {
        addTPHistory(data, true);
        return this;
    }

    public PacketSender updateRecentTeleport(TeleportData data) {
        addTPHistory(data, false);
        return this;
    }

    public PacketSender addTPHistory(TeleportData data, boolean favorite) {
        PacketBuilder out = new PacketBuilder(66, PacketType.VARIABLE_SHORT);
        out.put(favorite ? 1 : 0)
            .put(0)
            .putShort(data.spriteID)
            .putString(data.teleportName);
        player.getSession().write(out);
        return this;
    }

    public PacketSender resetRecentTeleports() {
        PacketBuilder out = new PacketBuilder(66, PacketType.VARIABLE_SHORT)
            .put(0).put(1).putShort(0).putString("N/A");
        player.getSession().write(out);
        return this;
    }

    public PacketSender removeFavorite(TeleportData data) {
        PacketBuilder out = new PacketBuilder(66, PacketType.VARIABLE_SHORT)
            .put(1).put(1).putShort(data.spriteID).putString(data.teleportName);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendStamina(boolean active) {
        PacketBuilder out = new PacketBuilder(138);
        out.put(active ? 1 : 0);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendBroadcast(String message) {
        PacketBuilder out = new PacketBuilder(222, PacketType.VARIABLE_SHORT);
        out.putString(message);
        player.getSession().write(out);
        return this;
    }

    public PacketSender mysteryBoxSpinner() {
        PacketBuilder out = new PacketBuilder(11);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInfection(InfectionType infection) {
        PacketBuilder out = new PacketBuilder(6);
        out.put(infection.ordinal(), ValueType.C);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendScreenMode(int width, int height) {
        PacketBuilder out = new PacketBuilder(128);

        out.putShort(width, ValueType.A, ByteOrder.LITTLE).putInt(height);
        player.getSession().write(out);
        return this;
    }

    /**
     * Changes the main displaying sprite on an interface. The index represents
     * the location of the new sprite in the index of the sprite array.
     *
     * @param componentId the interface
     * @param index       the index in the array
     */
    public PacketSender sendChangeSprite(int componentId, byte index) {
        PacketBuilder out = new PacketBuilder(7);
        out.putInt(componentId);
        out.put(index);
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends information about the player to the client.
     *
     * @return The PacketSender instance.
     */
    public PacketSender sendDetails() {
        PacketBuilder out = new PacketBuilder(249);
        out.put(1, ValueType.A);
        out.putShort(player.getIndex());
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends the map region a player is located in and also
     * sets the player's first step position of said region as their
     * {@code lastKnownRegion}.
     *
     * @return The PacketSender instance.
     */
    public PacketSender sendMapRegion() {
        player.setRegionHeight(player.tile().getLevel());
        player.setAllowRegionChangePacket(true);
        player.setLastKnownRegion(player.tile().copy());
        PacketBuilder out = new PacketBuilder(73);
        out.putShort(player.tile().getRegionX() + 6, ValueType.A);
        out.putShort(player.tile().getRegionY() + 6);
        if (!player.getRegions().contains(player.tile().getRegion())) player.addRegion(player.tile().getRegion());
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends the logout packet for the player.
     *
     * @return The PacketSender instance.
     */
    public PacketSender sendLogout() {
        PacketBuilder out = new PacketBuilder(109);
        player.getSession().write(out);
        return this;
    }

    /**
     * Sets the world's system update time, once timer is 0, everyone will be disconnected.
     *
     * @param time The amount of seconds in which world will be updated in.
     * @return The PacketSender instance.
     */
    public PacketSender sendSystemUpdate(int time) {
        PacketBuilder out = new PacketBuilder(114);
        out.putShort(time, ByteOrder.LITTLE);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendAutocastId(int id) {
        PacketBuilder out = new PacketBuilder(38);
        out.putShort(id);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendEnableNoclip() {
        PacketBuilder out = new PacketBuilder(250);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendURL(String url) {
        PacketBuilder out = new PacketBuilder(251, PacketType.VARIABLE);
        out.putString(url);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendMessage(final String message) {
        return sendGameMessage(message, false);
    }

    public PacketSender sendFilteredMessage(final String message) {
        return sendGameMessage(message, true);
    }

    public PacketSender sendMessage(final String message, final MessageType type) {
        return sendGameMessage(message, type);
    }

    public PacketSender sendMessage(final String message, final MessageType type, final String extension) {

        PacketBuilder out = new PacketBuilder(253, PacketType.VARIABLE);
        out.putShort(type.getType());
        out.put(extension != null ? 1 : 0);
        if (extension != null) {
            out.putString(extension);
        }
        out.putString(message);
        player.getSession().write(out);
        return this;

    }

    public PacketSender sendGameMessage(final String message, final MessageType type) {
        return sendMessage(message, type, null);
    }

    public PacketSender sendGameMessage(final String message, final boolean filterable) {
        return sendMessage(message, filterable ? MessageType.FILTERABLE : MessageType.UNFILTERABLE, null);
    }

    public PacketSender sendGameMessage(final String message, final boolean filterable, final Object... params) {
        return sendMessage(params.length > 0 ? String.format(message, params) : message, filterable ? MessageType.FILTERABLE : MessageType.UNFILTERABLE, null);
    }

    public PacketSender sendTradeRequest(final String message, final String user) {
        return sendMessage(message, MessageType.TRADE_REQUEST, user);
    }

    public PacketSender sendChallengeRequest(final String message, final String user) {
        return sendMessage(message, MessageType.CHALLENGE_REQUEST, user);
    }

    /**
     * Sends a clan message to a player in the server.
     *
     * @param message The message they will receive in chat box.
     * @return The PacketSender instance.
     */
    public PacketSender sendClanMessage(String name, int type, String message) {
        PacketBuilder out = new PacketBuilder(252, PacketType.VARIABLE_SHORT);
        out.put(type);
        out.putString(name);
        out.putString(message);
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends a skill to a client.
     */
    public PacketSender updateSkill(int skill, int level, int xp) {
        PacketBuilder out = new PacketBuilder(134);
        out.put(skill);
        out.putInt(xp, ByteOrder.MIDDLE);
        out.put(level);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendFakeXPDrop(int skill, double exp) {
        sendExpDrop(skill, exp, false, true);
        return this;
    }

    public PacketSender sendXPDrop(int skill, double exp, boolean counter) {
        sendExpDrop(skill, exp, counter, false);
        return this;
    }

    public PacketSender sendExpDrop(int skill, double exp, boolean counter, boolean xpLocked) {
        PacketBuilder out = new PacketBuilder(116);
        out.put(skill).putInt((int) exp).put(counter ? 1 : 0).put(xpLocked ? 1 : 0, ValueType.S);
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends a configuration button's state.
     *
     * @param id    The id of the configuration button.
     * @param state The state to set it to.
     * @return The PacketSender instance.
     */
    public PacketSender sendConfig(int id, int state) {
        if (state < Byte.MIN_VALUE || state > 255) {
            return sendVarpIntSize(id, state);
        }

        PacketBuilder out = new PacketBuilder(36);
        out.putShort(id, ByteOrder.LITTLE);
        out.put(state); // value is over byte lol
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends a interface child's toggle.
     *
     * @param id    The id of the child.
     * @param state The state to set it to.
     * @return The PacketSender instance.
     */
    public PacketSender sendVarpIntSize(int id, int state) {
        player.sessionVarps()[id] = state;
        PacketBuilder out = new PacketBuilder(87);
        out.putShort(id, ByteOrder.LITTLE);
        out.putInt(state, ByteOrder.MIDDLE);
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends the state in which the player has their chat options, such as public, private, friends only.
     *
     * @param publicChat  The state of their public chat.
     * @param privateChat The state of their private chat.
     * @param tradeChat   The state of their trade chat.
     * @return The PacketSender instance.
     */
    public PacketSender sendChatOptions(int publicChat, int privateChat, int tradeChat) {
        PacketBuilder out = new PacketBuilder(206);
        out.put(publicChat).put(privateChat).put(tradeChat);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendRunEnergy(int energy) {
        PacketBuilder out = new PacketBuilder(110);
        out.put(energy);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendQuickPrayersState(boolean activated) {
        PacketBuilder out = new PacketBuilder(111);
        out.put(activated ? 1 : 0);
        player.getSession().write(out);
        return this;
    }

    public PacketSender updateSpecialAttackOrb() {
        PacketBuilder out = new PacketBuilder(137);
        out.put(player.getSpecialAttackPercentage());
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendRunStatus() {
        boolean running = player.getAttribOr(AttributeKey.IS_RUNNING, false);
        player.varps().setVarp(173, running ? 1 : 0);
        PacketBuilder out = new PacketBuilder(113);
        out.put(running ? 1 : 0);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendWeight(double weight) {
        PacketBuilder out = new PacketBuilder(240);
        out.putShort((int) weight);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterface(int id) {
        //System.out.println("sending interface");
        PacketBuilder out = new PacketBuilder(97);
        out.putInt(id);
        player.getSession().write(out);
        return this;
    }

    public void sendInterface(final int interfaceId, final int paneComponent, final PaneType pane, final boolean walkable) {
        sendInterfaceOSRS(interfaceId, paneComponent, pane, InterfaceType.OVERLAY);
        player.interfaces.getVisible().forcePut(pane.getId() << 16 | paneComponent, interfaceId);
    }

    public PacketSender sendWalkableInterface(int interfaceId) {
        PacketBuilder out = new PacketBuilder(208);
        out.putInt(interfaceId);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceDisplayState(int interfaceId, boolean hide) {
        if (player.getPacketDropper().requiresUpdate(171, new ComponentVisibility(hide, interfaceId))) {
            PacketBuilder out = new PacketBuilder(171);
            out.put(hide ? 1 : 0);
            out.putInt(interfaceId);
            player.getSession().write(out);
        }
        return this;
    }

    public PacketSender setInterClickable(int interfaceId, boolean clickable) {
        PacketBuilder out = new PacketBuilder(2);
        out.putInt(interfaceId);
        out.put(clickable ? 1 : 0);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendPlayerHeadOnInterface(int id) {
        PacketBuilder out = new PacketBuilder(185);
        out.putShort(id, ValueType.A, ByteOrder.LITTLE);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendNpcHeadOnInterface(int id, int interfaceId) {
        PacketBuilder out = new PacketBuilder(75);
        out.putShort(id, ValueType.A, ByteOrder.LITTLE);
        out.putShort(interfaceId, ValueType.A, ByteOrder.LITTLE);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendEnterAmountPrompt(String title) {
        PacketBuilder out = new PacketBuilder(27, PacketType.VARIABLE);
        out.putString(title);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendEnterInputPrompt(String title) {
        PacketBuilder out = new PacketBuilder(187, PacketType.VARIABLE);
        out.putString(title);
        player.getSession().write(out);
        return this;
    }

    /**
     * Closes a player's client.
     */
    public PacketSender sendExit() {
        PacketBuilder out = new PacketBuilder(62);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceComponentMoval(int x, int y, int id) {
        PacketBuilder out = new PacketBuilder(70);
        out.putShort(x).putShort(y).putInt(id);
        player.getSession().write(out);
        return this;
    }

    public void sendPane(PaneType pane) {
        sendPane(pane.getId());
        player.interfaces.setPane(pane);
    }

    public PacketSender sendPane(int top) {
        PacketBuilder out = new PacketBuilder(148);
        out.putShort(top);
        player.getSession().write(out);
        return this;
    }

    public PacketSender runClientScriptNew(int id, Object... args) {
        PacketBuilder out = new PacketBuilder(149, PacketType.VARIABLE_SHORT);

        char[] types = new char[args.length + 1];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                types[i] = 's';
            } else if (args[i] instanceof Number) {
                types[i] = 'i';
            } else {
                logger.error("Invalid argument type {} for script {}.", args[i].getClass().getName(), id);
            }
        }
        types[args.length] = 0; // Null terminator for the type array

        List<Byte> args1 = new ArrayList<>();
        for (int i = args.length - 1; i >= 0; i--) {
            Object value = args[i];
            if (value instanceof String) {
                for (byte b : ((String) value).getBytes()) {
                    args1.add(b);
                }
                args1.add((byte) 0); // Null terminator for strings
            } else if (value instanceof Number) {
                int num = ((Number) value).intValue();
                args1.add((byte) (num >> 24));
                args1.add((byte) (num >> 16));
                args1.add((byte) (num >> 8));
                args1.add((byte) num);
            }
        }
        byte[] bytes = new String(types).getBytes();

        out.writeByteArray(bytes, 0, bytes.length);
        byte[] argsArray = new byte[args1.size()];
        for (int i = 0; i < args1.size(); i++) {
            argsArray[i] = args1.get(i);
        }
        out.writeByteArray(argsArray, 0, argsArray.length);
        out.putInt(id);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceOSRS(int interfaceId, int paneChildId, PaneType pane, InterfaceType type) {
        PacketBuilder out = new PacketBuilder(150);
        out.putInt((pane.getId() << 16) | paneChildId);
        out.put(type == InterfaceType.OVERLAY ? (byte) 1 : (byte) 0);
        out.putShort(interfaceId);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendMoveInterface(int fromPaneId, int fromPaneChildId, int toPaneId, int toPaneChildId) {
        PacketBuilder out = new PacketBuilder(136);
        out.putInt((fromPaneId << 16) | fromPaneChildId);
        out.putInt((toPaneId << 16) | toPaneChildId);
        player.getSession().write(out);
        return this;
    }

    public PacketSender closeInterfaceOSRS(int interfaceId) {
        PacketBuilder out = new PacketBuilder(234);
        out.putInt(interfaceId);
        player.getSession().write(out);
        return this;
    }

    public void setInterfaceEvents(int packed, IntRange range, EventConstants setting) {
        setEventMessage(packed, range.getFirst(), range.getLast(), setting.getFlag());
    }

    public void setInterfaceEvents(int interfaceId, int component, IntRange range, EventConstants setting) {
        setEventMessage((interfaceId << 16) | component, range.getFirst(), range.getLast(), setting.getFlag());
    }

    public void setInterfaceEvents(int interfaceId, int component, IntRange range, List<EventConstants> settings) {
        int[] flags = new int[settings.size()];
        for (int i = 0; i < settings.size(); i++) {
            flags[i] = settings.get(i).getFlag();
        }

        int settingsValue = Arrays.stream(flags).reduce((flag1, flag2) -> flag1 | flag2).orElse(0);
        setEventMessage((interfaceId << 16) | component, range.getFirst(), range.getLast(), settingsValue);
    }

    public void setInterfaceEvents(int interfaceId, int component, IntRange range, int setting) {
        setEventMessage((interfaceId << 16) | component, range.getFirst(), range.getLast(), setting);
    }

    public PacketSender setEventMessage(int hash, int from, int to, int setting) {
        PacketBuilder out = new PacketBuilder(42);
        out.putShort(from);
        out.putInt(setting);
        out.putShort(to);
        out.putInt(hash);
        player.getSession().write(out);
        return this;
    }

    public PacketSender setComponentVisability(int packed, boolean hidden) {
        int interfaceId = WidgetUtil.componentToInterface(packed);
        int component = WidgetUtil.componentToId(packed);

        return setComponentVisability(interfaceId,component,hidden);
    }

    public PacketSender setComponentVisability(int parent, int child, boolean hidden) {
        PacketBuilder out = new PacketBuilder(232);
        out.putInt((parent << 16) | child);
        out.put(hidden ? 1: 0 );
        player.getSession().write(out);
        return this;
    }

    public PacketSender setComponentText(int packed, String text) {
        int interfaceId = WidgetUtil.componentToInterface(packed);
        int component = WidgetUtil.componentToId(packed);

        return setComponentText(interfaceId,component,text);
    }

    public PacketSender setComponentText(int parent, int child, String text) {
        PacketBuilder out = new PacketBuilder(43, PacketType.VARIABLE_SHORT);
        out.putInt((parent << 16) | child);
        out.putString(text);
        player.getSession().write(out);
        return this;
    }



    public PacketSender setItemMessage(int hash, int item, int amount) {
        PacketBuilder out = new PacketBuilder(47);
        out.putInt(amount);
        out.putShort(item);
        out.putInt(hash);
        player.getSession().write(out);
        return this;
    }

    public PacketSender setNpcHeadMessage(int hash, int npc) {
        PacketBuilder out = new PacketBuilder(48);
        out.putShort(npc);
        out.putInt(hash);
        player.getSession().write(out);
        return this;
    }

    public PacketSender setPlayerHeadMesssage(int hash) {
        PacketBuilder out = new PacketBuilder(49);
        out.putInt(hash);
        player.getSession().write(out);
        return this;
    }

    public PacketSender setAnimMessage(int hash, int anim) {
        PacketBuilder out = new PacketBuilder(59);
        out.putInt(anim);
        out.putInt(hash);
        player.getSession().write(out);
        return this;
    }

    public PacketSender moveSubMessage(int from, int to) {
        PacketBuilder out = new PacketBuilder(63);
        out.putInt(from);
        out.putInt(to);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceAnimation(int interfaceId, Animation animation) {
        PacketBuilder out = new PacketBuilder(200);
        out.putShort(interfaceId);
        out.putShort(animation.getId());
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceModel(int widget, int scale, Item item) {
        PacketBuilder out = new PacketBuilder(246);
        out.putShort(widget, ByteOrder.LITTLE);
        out.putShort(scale).putShort(item.getId());
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceModel(int widget, int scale, int item) {
        PacketBuilder out = new PacketBuilder(246);
        out.putShort(widget, ByteOrder.LITTLE);
        out.putShort(scale).putShort(item);
        player.getSession().write(out);
        return this;
    }

    /**
     * read the method name
     */

    public PacketSender sendInterfaceSpriteChange(int interfaceID, int spriteID) {

        PacketBuilder out = new PacketBuilder(193);
        out.putInt(interfaceID);
        out.putShort(spriteID);

        player.getSession().write(out);
        return this;
    }

    public PacketSender sendTabInterface(int tabId, int interfaceId) {
        PacketBuilder out = new PacketBuilder(71);
        out.putInt(interfaceId);
        out.put(tabId, ValueType.A);
        player.getSession().write(out);
        return this;
    }

    // kinda lazy place to put it
    public PacketSender sendTabs() {
        for (int i = 0; i < GameConstants.SIDEBAR_INTERFACE.length; i++) {
            int tab = GameConstants.SIDEBAR_INTERFACE[i][0];

            player.getInterfaceManager().setSidebar(tab, 56000);
        }
        return this;
    }

    public PacketSender sendWeapon(int weaponId, int ammoId) {
        PacketBuilder out = new PacketBuilder(191);
        out.putShort(weaponId).putShort(ammoId);
        player.getSession().write(out);
        return this;
    }

    public PacketSender setDefensiveAutocastState(int state) {
        PacketBuilder out = new PacketBuilder(188);
        out.putShort(state);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendTab(int id) {
        PacketBuilder out = new PacketBuilder(106);
        out.put(id, ValueType.C);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendChatboxInterface(int id) {
        PacketBuilder out = new PacketBuilder(164);
        out.putShort(id, ByteOrder.LITTLE);
        player.getSession().write(out);
        return this;
    }

    public PacketSender changeMapVisibility(int state) {
        PacketBuilder out = new PacketBuilder(99);
        out.put(state);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceRemoval() {
        //System.out.println("sending interface removal");
        //System.out.println("Send interface removal " + Misc.getStackTrace());
        player.getSession().write(new PacketBuilder(219));
        return this;
    }

    public PacketSender closeDialogue() {
        player.getSession().write(new PacketBuilder(220));
        return this;
    }

    public PacketSender sendInterfaceScrollReset(int interfaceId) {
        PacketBuilder out = new PacketBuilder(9);
        out.putInt(interfaceId);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendScrollbarHeight(int interfaceId, int scrollMax) {
        PacketBuilder out = new PacketBuilder(10);
        out.putInt(interfaceId);
        out.putShort(scrollMax);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendInterfaceSet(int interfaceId, int sidebarInterfaceId) {
        PacketBuilder out = new PacketBuilder(248);
        out.putInt(interfaceId);
        out.putInt(sidebarInterfaceId);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendEffectTimer(int delay, EffectTimer e) {
        PacketBuilder out = new PacketBuilder(54);
        out.putShort(delay);
        out.putShort(e.getClientSprite());
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendItemOnInterface(int interfaceId, Item... items) {
        PacketBuilder out = new PacketBuilder(53, PacketType.VARIABLE_SHORT);
        var totalItems = items.length;
        out.putInt(interfaceId).putShort(totalItems);
        for (int index = 0; index < items.length; index++) {
            if (items[index] == null) {
                out.putInt(0, ByteOrder.INVERSE_MIDDLE).putShort(0, ValueType.A, ByteOrder.LITTLE);
                continue;
            }
            out.putInt(items[index].getAmount(), ByteOrder.INVERSE_MIDDLE);
            out.putShort(items[index].getId() + 1, ValueType.A, ByteOrder.LITTLE);
        }
        var tabs = player.getBank().tabAmounts;
        if (tabs != null) {
            var tabamounts = player.getBank().tabAmounts.length;
            for (int index = 0; index < tabamounts; index++) {
                var amount = player.getBank().tabAmounts[index];
                if (player.getBank().tabAmounts == null) {
                    out.put(0 >> 8).putShort(0);
                    continue;
                }
                out.put(amount >> 8).putShort(amount & 0xFF);
            }
        }
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendUpdateInvFull(int inventoryId, Item... items) {
        PacketBuilder out = new PacketBuilder(204, PacketType.VARIABLE_SHORT);
        out.putInt(-1);
        out.putShort(inventoryId);
        out.putShort(items.length);
        for (Item item : items) {
            if (item != null) {
                out.putShort(item.getId() + 1);
                if (item.getAmount() < 0 || item.getAmount() >= 255) {
                    out.put(255).putInt(item.getAmount());
                } else {
                    out.put(item.getAmount());
                }
            } else {
                out.putShort(0);
                out.put(0);
            }
        }
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendUpdateInvPartial(int inventoryId, int slot, Item item) {
        return sendUpdateInvPartial(inventoryId, new int[] {slot}, new Item[] {item});
    }

    public PacketSender sendUpdateInvPartial(int inventoryId, int[] slots, Item[] items) {
        PacketBuilder out = new PacketBuilder(205, PacketType.VARIABLE_SHORT);
        out.putInt(-1);
        out.putShort(inventoryId);
        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            Item item = items[i];

            out.putSmart1or2(slot);

            if (item != null && item.getId() != -1) {
                out.putShort(item.getId() + 1);
                int amount = item.getAmount();
                if (amount < 0 || amount > 254) {
                    out.put(255).putInt(amount);
                } else {
                    out.put(amount);
                }
            } else {
                out.putShort(0);
            }
        }
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendItemOnInterface(int interfaceId, List<Item> items) {
        PacketBuilder out = new PacketBuilder(53, PacketType.VARIABLE_SHORT);
        out.putInt(interfaceId).putShort(items.size());
        var totalItems = items.size();
        out.putInt(interfaceId).putShort(totalItems);
        for (int index = 0; index < items.size(); index++) {
            if (items.get(index) == null) {
                out.putInt(0, ByteOrder.INVERSE_MIDDLE).putShort(0, ValueType.A, ByteOrder.LITTLE);
                continue;
            }
            out.putInt(items.get(index).getAmount(), ByteOrder.INVERSE_MIDDLE);
            out.putShort(items.get(index).getId() + 1, ValueType.A, ByteOrder.LITTLE);
        }
        var tabs = player.getBank().tabAmounts;
        if (tabs != null) {
            var tabamounts = player.getBank().tabAmounts.length;
            for (int index = 0; index < tabamounts; index++) {
                var amount = player.getBank().tabAmounts[index];
                if (player.getBank().tabAmounts == null) {
                    out.put(0 >> 8).putShort(0);
                    continue;
                }
                out.put(amount >> 8).putShort(amount & 0xFF);
            }
        }
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendBanktabs() {
        PacketBuilder out = new PacketBuilder(55, PacketType.FIXED);
        if (player.getBank().tabAmounts != null) {
            for (final int amount : player.getBank().tabAmounts) {
                out.put(amount >> 8).putShort(amount & 0xFF);
            }
        } else {
            // just cover the case when the above two for some reason dont trigger
            for (final int amount : new int[10]) {
                out.put(amount >> 8).putShort(amount & 0xFF);
            }
        }
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendItemOnInterfaceSlot(int interfaceId, int item, int amount, int slot) {
        return sendItemOnInterfaceSlot(interfaceId, new Item(item, amount), slot);
    }

    public PacketSender sendItemOnInterfaceSlot(int interfaceId, Item item, int slot) {
        PacketBuilder out = new PacketBuilder(34, PacketType.VARIABLE_SHORT);
        out.putInt(interfaceId).put(slot);
        if (item == null) {
            out.putShort(0);
            out.put(0);
        } else {
            out.putShort(item.getId() + 1);
            if (item.getAmount() > 254) {
                out.put(255).putInt(item.getAmount());
            } else {
                out.put(item.getAmount());
            }
        }
        player.getSession().write(out);
        return this;
    }

    public PacketSender clearItemOnInterface(int frame) {
        PacketBuilder out = new PacketBuilder(72);
        out.putShort(frame);
        player.getSession().write(out);
        return this;
    }


    public PacketSender sendInteractionOption(String option, int slot, boolean top) {
        PacketBuilder out = new PacketBuilder(104, PacketType.VARIABLE);
        out.put(slot, ValueType.C);
        out.put(top ? 1 : 0, ValueType.A);
        out.putString(option);
        player.getSession().write(out);
        PlayerInteractingOption interactingOption = PlayerInteractingOption.forName(option);
        player.setPlayerInteractingOption(interactingOption);
        return this;
    }

    public PacketSender sendLobbytimer(int seconds) {
        PacketBuilder out = new PacketBuilder(203);
        out.putShort(seconds);
        player.getSession().write(out);
        return this;
    }

    public void sendMultipleStrings(List<Player.TextData> stringList) {
        List<Player.TextData> filtered = stringList.stream().toList();

        PacketBuilder out = new PacketBuilder(129, PacketType.VARIABLE_SHORT);

        out.put(filtered.size());

        filtered.forEach(string -> {
            out.putString(string.text);
            out.putInt(string.id);
        });

        player.getSession().write(out);
    }

    public PacketSender sendString(int id, String string) {
        PacketBuilder out = new PacketBuilder(126, PacketType.VARIABLE_SHORT);
        out.putString(string);
        out.putInt(id);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendColor(int id, int color) {
        PacketBuilder out = new PacketBuilder(122);
        out.putShort(id);
        out.putInt(color);
        //System.out.printf("id: %d colour: %d%n", id, color);
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends the players rights ordinal to the client.
     *
     * @return The packetsender instance.
     */
    public PacketSender sendRights() {
        PacketBuilder out = new PacketBuilder(127);
        out.put(player.getPlayerRights().getRightsId());
        out.put(player.getMemberRights().ordinal());
        out.put(player.getIronManStatus().ordinal());
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends a hint to specified position.
     *
     * @param tile         The position to create the hint.
     * @param tilePosition The position on the square (middle = 2; west = 3; east = 4; south = 5; north = 6)
     * @return The Packet Sender instance.
     */
    public PacketSender sendPositionalHint(Tile tile, int tilePosition) {
        if (tile == null) return this;
        PacketBuilder out = new PacketBuilder(254);
        out.put(tilePosition);
        out.putShort(tile.getX());
        out.putShort(tile.getY());
        out.put(tile.getLevel());
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends a hint above an entity's head.
     *
     * @param entity The target entity to draw hint for.
     * @return The PacketSender instance.
     */
    public PacketSender sendEntityHint(Entity entity) {
        int type = entity instanceof Player ? 10 : 1;
        PacketBuilder out = new PacketBuilder(254);
        out.put(type);
        out.putShort(entity.getIndex());
        out.putInt(0, ByteOrder.TRIPLE_INT);
        player.getSession().write(out);

        // Tell client to prioritize the target.
        if (entity.getIndex() >= 0 && entity.isPlayer()) {
            player.message("prioritizetarget:" + entity.getIndex());
        }
        return this;
    }

    /**
     * Sends a hint removal above an entity's head.
     *
     * @param playerHintRemoval Remove hint from a player or an NPC?
     * @return The PacketSender instance.
     */
    public PacketSender sendEntityHintRemoval(boolean playerHintRemoval) {
        int type = playerHintRemoval ? 10 : 1;
        PacketBuilder out = new PacketBuilder(254);
        out.put(type).putShort(-1);
        out.putInt(0, ByteOrder.TRIPLE_INT);
        player.getSession().write(out);
        // Tell client to prioritize the target.
        player.message("prioritizetarget:-1");
        return this;
    }

    public PacketSender sendMultiIcon(int value) {
        PacketBuilder out = new PacketBuilder(61);
        out.put(value);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendPrivateMessage(int senderRights, int senderMemberRights, int senderIronmanRights, Player target, byte[] message, int size) {
        PacketBuilder out = new PacketBuilder(196, PacketType.VARIABLE_SHORT);
        out.putString(target.getUsername());
        out.putInt(target.getRelations().getPrivateMessageId());
        out.put(senderRights);
        out.put(senderMemberRights);
        out.put(senderIronmanRights);
        out.putBytes(message, size);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendFriendStatus(int status) {
        PacketBuilder out = new PacketBuilder(221);
        out.put(status);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendFriend(String name, int world) {
        // world 0 = offline
        world = world != 0 ? world + 9 : world;
        PacketBuilder out = new PacketBuilder(50, PacketType.VARIABLE);
        out.putNewString(name);
        out.put(world);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendDeleteFriend(String name) {
        PacketBuilder out = new PacketBuilder(51, PacketType.VARIABLE);
        out.putString(name);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendAddIgnore(String name) {
        PacketBuilder out = new PacketBuilder(214, PacketType.VARIABLE);
        out.putString(name);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendDeleteIgnore(String name) {
        PacketBuilder out = new PacketBuilder(215, PacketType.VARIABLE);
        out.putString(name);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendAnimationReset() {
        PacketBuilder out = new PacketBuilder(1);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendTileGraphic(int id, Tile tile, int height, int delay) {
        sendMapPacket(tile.x, tile.y, tile.level);
        PacketBuilder out = new PacketBuilder(4);
        out.put(0);
        out.putShort(id);
        out.put(height);
        out.putShort(delay);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendAreaSound(int id, int loop, int delay, int x, int y, int distance) {
        PacketBuilder out = new PacketBuilder(45);
        sendMapPacket(x, y, player.getZ());
        out.putShort(id, ByteOrder.LITTLE);
        out.put(delay, ValueType.A);
        out.put(distance << 4 | loop, ValueType.A);
        out.put(0, ValueType.A);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendObject(GameObject object) {
        sendMapPacket(object.getX(), object.getY(), object.getZ());
        PacketBuilder out = new PacketBuilder(151);
        out.put(0, ValueType.A);
        out.putShort(object.getId(), ByteOrder.LITTLE);
        out.put((object.getType() << 2) + (object.getRotation() & 3), ValueType.S);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendObjectRemoval(GameObject object) {
        sendMapPacket(object.getX(), object.getY(), object.getZ());
        PacketBuilder out = new PacketBuilder(101);
        out.put((object.getType() << 2) + (object.getRotation() & 3), ValueType.C);
        out.put(0);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendObjectAnimation(GameObject object, int anim) {
        sendMapPacket(object.x, object.y, object.z);
        PacketBuilder out = new PacketBuilder(160);
        out.put(0, ValueType.S);
        out.put((object.getType() << 2) + (object.getRotation() & 3), ValueType.S);
        out.putShort(anim, ValueType.A);
        player.getSession().write(out);
        return this;
    }

    public PacketSender createGroundItem(GroundItem groundItem) {
        sendMapPacket(groundItem.getTile().x, groundItem.getTile().y, groundItem.getTile().level);
        PacketBuilder out = new PacketBuilder(44);
        out.putShort(groundItem.getItem().getId(), ValueType.A, ByteOrder.LITTLE);
        out.putInt(groundItem.getItem().getAmount());
        out.putShort(0);
        player.getSession().write(out);
        return this;
    }

    public PacketSender deleteGroundItem(GroundItem groundItem) {
        sendMapPacket(groundItem.getTile().getX(), groundItem.getTile().getY(), groundItem.getTile().getZ());
        PacketBuilder out = new PacketBuilder(156);
        out.put(0, ValueType.A);
        out.putShort(groundItem.getItem().getId());
        player.getSession().write(out);
        return this;
    }

    public PacketSender updateGroundItemAmount(int oldAmt, GroundItem groundItem) {
        sendMapPacket(groundItem.getTile().getX(), groundItem.getTile().getY(), groundItem.getTile().getZ());
        PacketBuilder out = new PacketBuilder(84);
        out.put(groundItem.getTile().getLevel(), ValueType.STANDARD);
        out.putShort(groundItem.getItem().getId());
        out.putShort(oldAmt);
        out.putShort(groundItem.getItem().getAmount());
        player.getSession().write(out);
        return this;
    }

    /**
     * Deletes spawns related to regions, such as ground items and objects.
     *
     * @return
     */
    public PacketSender deleteRegionalSpawns() {
        PacketBuilder out = new PacketBuilder(178);
        player.getSession().write(out);
        return this;
    }

    /*
     * Sends the modified trade slot to the interacting player
     */
    public PacketSender sendModified(int slot) {
        PacketBuilder out = new PacketBuilder(180);
        out.put(slot);
        player.getSession().write(out);
        return this;
    }

    public PacketSender sendPosition(final Tile tile) {
        PacketBuilder out = new PacketBuilder(85);
        if (player.getZ() != tile.getZ()) return null;
        final Tile other = player.getLastKnownRegion();
        var playerLocalX = tile.getX() - 8 * other.getRegionX();
        var playerLocalY = tile.getY() - 8 * other.getRegionY();
        if (playerLocalX >= 0 && playerLocalX < 104 && playerLocalY >= 0 && playerLocalY < 104) {
            out.put(playerLocalY, ValueType.C);
            out.put(playerLocalX, ValueType.C);
            player.getSession().write(out);
        }
        return this;
    }

    public PacketSender(Player player) {
        this.player = player;
    }

    private final Player player;

    private PacketSender sendMapPacket(int x, int y, int z) {
        if (player.getZ() != z) return null;
        final Tile other = player.getLastKnownRegion();
        int playerLocalX = x - 8 * other.getRegionX();
        int playerLocalY = y - 8 * other.getRegionY();
        player.getSession().write(new PacketBuilder(85).put(playerLocalY, ValueType.C).put(playerLocalX, ValueType.C));
        return this;
    }

    public PacketSender sendProjectile(int startX, int startY, int destX, int destY, int angle, int duration, int gfxMoving, int startHeight, int endHeight, int lockon, int startDelay, int slope, int creatorSize, int startDistanceOffset) {
        sendMapPacket(startX, startY, player.getZ());
        player.getSession().write(new PacketBuilder(117).put(angle).put(destY).put(destX).putShort(lockon).putShort(gfxMoving).put(startHeight).put(endHeight).putShort(startDelay).putShort(duration).put(slope).put(startDistanceOffset));
        return this;
    }

    public PacketSender confirm(int state, int value) {
        PacketBuilder out = new PacketBuilder(213);
        out.putShort(state).putShort(value);
        player.getSession().write(out);
        return this;
    }

    /**
     * Sends packet 79 to set an interface's scroll position.
     *
     * @param childId        The childId of the interface.
     * @param scrollPosition The value of the scroll position.
     */
    public PacketSender setScrollPosition(final int childId, final int scrollPosition, final int scrollMax) { // ok wew s2c verified .. guess what now yep the other damn side
        PacketBuilder out = new PacketBuilder(79);
        out.putShort(childId);
        out.putShort(scrollPosition);
        out.putShort(scrollMax);
        player.getSession().write(out);
        return this;
    }

    public PacketSender darkenScreen(int opacity) {
        if (this.player.getSession() != null) { // custom packet
            PacketBuilder out = new PacketBuilder(190);
            out.put(opacity > 0 ? 1 : 0);
            out.putInt(opacity);
            this.player.getSession().write(out);
        }
        return this;
    }

    public void sendCameraNeutrality() {

    }

    public void shakeCamera(int i, int i1, int i2, int i3) {

    }
}

package com.aelous.model.content.packet_actions;

import com.aelous.model.entity.player.Player;

import java.util.HashMap;

/**
 * A utility class for sending strings via {@code player.getPacketSender().sendString(id, s);} which are sent extremely often.
 * Will only send the string if the text being sent is different from the previously sent string on the same interface-id.
 * @author Jak|shadowrs
 * @version Jan 6 2020
 */
public enum GlobalStrings {
    PLAYERS_ONLINE(12700, "<img=1400>Players Online: <col=ffffff>%s"),
    RUN_ENERGY(41553, "%s%%")
    ;

    private int id;
    private String text;
    GlobalStrings(int id, String s) {
        this.id = id;
        this.text = s;
    }

    public void send(Player player, Object ... args) {
        boolean[] changed = new boolean[1];
        String s = String.format(text, args);
        if (player.commonStringsCache == null)
            player.commonStringsCache = new HashMap<>(0);
        player.commonStringsCache.compute(id, (k, v) -> {
            if (v == null || v != s.hashCode()) {
                changed[0] = true;
                return s.hashCode();
            }
            return v;
        });
        if (changed[0]) {
            player.getPacketSender().sendString(id, s);
        }/* else {
            player.message("no need to send "+this);
        }*/
    }

    public static void send(int id, String text, Player player, Object ... args) {
        boolean[] changed = new boolean[1];
        String s = args != null && args.length > 0 ? String.format(text, args) : text;
        if (player.commonStringsCache == null)
            player.commonStringsCache = new HashMap<>(0);
        player.commonStringsCache.compute(id, (k, v) -> {
            if (v == null || v != s.hashCode()) {
                changed[0] = true;
                return s.hashCode();
            }
            return v;
        });
        if (changed[0]) {
            player.getPacketSender().sendString(id, s);
        }/* else {
            player.message("no need to send "+id+" -> "+text);
        }*/
    }
}

package com.cryptic.clientscripts.impl.settings;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.Varbits;
import com.cryptic.interfaces.Varps;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class SettingsTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SETTINGS;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(ComponentID.PLAYER_ATTACK_PRIORITY, 1, 5));
        setEvents(new EventNode(ComponentID.NPC_ATTACK_PRIORITY, 1, 4));
        setEvents(new EventNode(ComponentID.MAX_FPS, 1, 5));
        setEvents(new EventNode(ComponentID.CLIENT_LAYOUT, 1, 3));
        setEvents(new EventNode(ComponentID.MUSIC_SLIDER, 0, 21));
        setEvents(new EventNode(ComponentID.SETTINGS_SIDE_SOUND_EFFECT_SLIDER, 0, 21));
        setEvents(new EventNode(ComponentID.SETTINGS_SIDE_AREA_SOUND_SLIDER, 0, 21));
        setEvents(new EventNode(ComponentID.BRIGHTNESS_SLIDER, 0, 21));
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.SETTINGS_TAB_ONE) {
            player.varps().setVarbit(Varbits.SETTINGS_TAB_FOCUS, 0);
        } else if (button == ComponentID.SETTINGS_TAB_TWO) {
            player.varps().setVarbit(Varbits.SETTINGS_TAB_FOCUS, 1);
        } else if (button == ComponentID.SETTINGS_TAB_THREE) {
            player.varps().setVarbit(Varbits.SETTINGS_TAB_FOCUS, 2);
        } else if (button == ComponentID.ALL_SETTINGS_COMPONENT) {
            GameInterface.SETTINGS_INTERFACE.open(player);
        }

        switch (player.varps().getVarbit(Varbits.SETTINGS_TAB_FOCUS)) {
            case 0 -> {
                switch (button) {
                    case ComponentID.SETTINGS_SIDE_RUN_BUTTON -> {
                        boolean running = player.<Boolean>getAttribOr(AttributeKey.IS_RUNNING, false);
                        player.varps().toggleVarbit(Varbits.SETTINGS_TAB_RUN);
                        player.putAttrib(AttributeKey.IS_RUNNING, !running);
                        player.getPacketSender().sendRunStatus();
                    }
                    case ComponentID.SETTINGS_SIDE_PK_SKULL_PREVENTION -> {
                        player.varps().toggleVarbit(Varbits.PK_SKULL_PREVENTION);
                    }
                    case ComponentID.SETTINGS_SIDE_ACCEPT_AID -> {
                        player.varps().toggleVarbit(Varbits.ACCEPT_AID_VARBIT);
                    }
                    case ComponentID.SETTINGS_SIDE_NPC_ATTACK_OPTIONS -> {
                        player.varps().setVarp(1306, slot - 1);
                    }
                    case ComponentID.SETTINGS_SIDE_PLAYER_ATTACK_OPTIONS -> {
                        player.varps().setVarp(1107, slot - 1);
                    }
                }
            }
            case 1 -> {
                switch (button) {
                    case ComponentID.SETTINGS_SIDE_MUSIC_SLIDER_STEP_HOLDER -> {
                        player.varps().setVarp(Varps.MUSIC_VOLUME, slot * 5);
                        int volume = player.varps().getVarp(Varps.MUSIC_VOLUME);
                        player.varps().setVarbit(Varbits.PREV_MUSIC_VOLUME, volume);
                    }
                    case ComponentID.SETTINGS_SIDE_SOUND_EFFECT_SLIDER_STEP_HOLDER -> {
                        player.varps().setVarp(Varps.SOUND_VOLUME, slot * 5);
                        int volume = player.varps().getVarp(Varps.SOUND_VOLUME);
                        player.varps().setVarbit(Varbits.PREV_SOUND_VOLUME, volume);
                    }
                    case ComponentID.AREA_SOUND_SLIDER_SETTINGS_SIDE -> {
                        player.varps().setVarp(Varps.AREA_VOLUME, slot * 5);
                        int volume = player.varps().getVarp(Varps.AREA_VOLUME);
                        player.varps().setVarbit(Varbits.PREV_AREA_VOLUME, volume);
                    }
                    case ComponentID.SETTINGS_SIDE_UNLOCK_MESSAGE -> {
                        player.varps().toggleVarbit(Varbits.MUSIC_UNLOCK_MESSAGE);
                    }
                }
            }
            case 2 -> {
                switch (button) {
                    case ComponentID.BRIGHTNESS_SLIDER -> {

                    }
                }
            }
        }
    }
}

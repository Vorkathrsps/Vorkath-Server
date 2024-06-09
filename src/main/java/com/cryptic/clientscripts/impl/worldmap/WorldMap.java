package com.cryptic.clientscripts.impl.worldmap;

import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.constants.EventConstants;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.InterfacePosition;
import com.cryptic.interfaces.InterfaceType;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.chainedwork.Chain;
import kotlin.ranges.IntRange;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class WorldMap extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.WORLD_MAP;
    }

    public boolean sendInterface() {
        return false;
    }

    @Override
    public void beforeOpen(Player player) {
        boolean isFullscreen = player.<Boolean>getAttribOr(AttributeKey.WORLD_MAP_FULLSCREEN, false);
        setEvents(new EventNode(21, 0, 4).setOperations(EventConstants.ClickOp1));
        player.getPacketSender().runClientScriptNew(4717, 574042);
        player.getPacketSender().runClientScriptNew(4722, 574042);
        player.getPacketSender().runClientScriptNew(ScriptID.WORLD_MAP_POSITION, player.tile().getPositionHash(), -1, -1);

        if (isFullscreen) {
            player.animate(5354);
            player.interfaces.setPreviousPane(player.interfaces.getPane());
            if (player.interfaces.getPreviousPane() != null) {
                player.interfaces.sendPane(player.interfaces.getPreviousPane(), PaneType.FULL_SCREEN);
                player.getPacketSender().sendSubInterfaceModal(GameInterface.WORLD_MAP.getId() - 1, 39, PaneType.FULL_SCREEN);
                player.getPacketSender().sendSubInterface(GameInterface.WORLD_MAP.getId(), 40, PaneType.FULL_SCREEN);
            }
        }

        player.getPacketSender().sendSubInterface(gameInterface().getId(), 42, isFullscreen ? PaneType.FULL_SCREEN : PaneType.FIXED);
        player.putAttrib(AttributeKey.WORLD_MAP_ACTIVE, true);
    }

    @Override
    public void onModalClosed(Player player) {
        player.clearAttrib(AttributeKey.WORLD_MAP_ACTIVE);
        boolean isFullscreen = player.<Boolean>getAttribOr(AttributeKey.WORLD_MAP_FULLSCREEN, false);
        if (isFullscreen) {
            player.animate(7551);
            if (player.interfaces.getPane().equals(PaneType.FULL_SCREEN) && player.interfaces.getPreviousPane() != null) {
                player.interfaces.sendPane(PaneType.FULL_SCREEN, player.interfaces.getPreviousPane());
                player.interfaces.sendGameFrame();
            }
        }
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.WORLD_MAP_CLOSE || button == ComponentID.WORLD_MAP_CLOSE_ESC) {
            GameInterface.WORLD_MAP.close(player);
        }
    }

}

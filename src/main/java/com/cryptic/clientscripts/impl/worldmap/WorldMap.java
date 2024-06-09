package com.cryptic.clientscripts.impl.worldmap;

import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.constants.EventConstants;
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
        player.getPacketSender().setInterfaceEvents(gameInterface().getId(), 17, new IntRange(0, 3), List.of(EventConstants.ClickOp1));
        boolean isFullscreen = player.<Boolean>getAttribOr(AttributeKey.WORLD_MAP_FULLSCREEN, false);

        if (isFullscreen) {
            System.out.println("HERE");
            player.animate(5354);
            player.interfaces.sendInterface(594,21, PaneType.FULL_SCREEN, InterfaceType.MODAL);
        }
        player.interfaces.sendInterface(GameInterface.WORLD_MAP);

        player.putAttrib(AttributeKey.WORLD_MAP_ACTIVE, true);

    }

    @Override
    public void onModalClosed(Player player) {
        player.clearAttrib(AttributeKey.WORLD_MAP_ACTIVE);
        boolean isFullscreen = player.<Boolean>getAttribOr(AttributeKey.WORLD_MAP_FULLSCREEN, false);
        if (isFullscreen) {
            player.animate(7551);
        }
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.WORLD_MAP_CLOSE) {
            GameInterface.WORLD_MAP.close(player);
        }
    }

}

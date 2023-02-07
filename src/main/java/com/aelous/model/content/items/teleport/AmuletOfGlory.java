package com.aelous.model.content.items.teleport;

import com.aelous.GameServer;
import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import java.util.stream.IntStream;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | December, 28, 2020, 13:48
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class AmuletOfGlory extends PacketInteraction {

    public static final int[] GLORY = new int[] {AMULET_OF_GLORY, AMULET_OF_GLORY1, AMULET_OF_GLORY2, AMULET_OF_GLORY3, AMULET_OF_GLORY4, AMULET_OF_GLORY5, AMULET_OF_GLORY6, AMULET_OF_ETERNAL_GLORY};

    private void teleport(Player player) {
        Tile tile = GameServer.properties().defaultTile;

        if (Teleports.canTeleport(player,true, TeleportType.ABOVE_20_WILD)) {
            Teleports.basicTeleport(player, tile);
            player.message("You have been teleported to home.");
        }
    }

    @Override
    public boolean handleEquipmentAction(Player player, Item item, int slot) {
        if(IntStream.of(GLORY).anyMatch(glory -> item.getId() == glory) && slot == EquipSlot.AMULET) {
            teleport(player);
            return true;
        }
        else if (IntStream.of(GLORY).anyMatch(glory -> item.getId() == glory) && slot == EquipSlot.SHIELD) {
            teleport(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 2) {
            if(IntStream.of(GLORY).anyMatch(glory -> item.getId() == glory)) {
                teleport(player);
                return true;
            }
        }
        return false;
    }
}

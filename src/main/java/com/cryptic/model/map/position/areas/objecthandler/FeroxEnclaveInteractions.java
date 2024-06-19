package com.cryptic.model.map.position.areas.objecthandler;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class FeroxEnclaveInteractions extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == 39652 || obj.getId() == 39653) {
            final int xPos = player.getX();
            final int yPos = player.getY();
            switch (obj.getRotation()) {
                case 0 -> {
                    if (xPos == 3122) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos + 1, yPos));
                        }
                    }
                    if (xPos == 3123) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos - 1, yPos));
                        }
                    }
                }
                case 1 -> {
                    if (yPos == 3639) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos, yPos + 1));
                        }
                    }
                    if (yPos == 3640) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos, yPos - 1));
                        }
                    }
                }
                case 2 -> {
                    if (xPos == 3154) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos + 1, yPos));
                        }
                    }
                    if (xPos == 3155) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos - 1, yPos));
                        }
                    }
                }
                case 3 -> {
                    if (yPos == 3617) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos, yPos - 1));
                        }
                    }
                    if (yPos == 3616) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            player.teleport(new Tile(xPos, yPos + 1));
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}

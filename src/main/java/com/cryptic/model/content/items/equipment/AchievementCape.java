package com.cryptic.model.content.items.equipment;

import com.cryptic.GameServer;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;

/**
 * @author Origin | December, 28, 2020, 15:28
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class AchievementCape extends PacketInteraction {

    private void teleport(Player player) {
        Tile tile = GameServer.getServerType().getHomeTile();

        if (Teleports.canTeleport(player,true, TeleportType.ABOVE_20_WILD)) {
            Teleports.basicTeleport(player, tile);
            player.message("You have been teleported to home.");
        }
    }

    private void untrim(Player player) {
        if(!player.inventory().contains(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE_T)) {
            return;
        }

        player.inventory().remove(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE_T),true);
        player.inventory().add(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE),true);
        player.message("You untrim your Achievement cape.");
    }

    private void trim(Player player) {
        if(!player.inventory().contains(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE)) {
            return;
        }

        player.inventory().remove(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE),true);
        player.inventory().add(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE_T),true);
        player.message("You trim your Achievement cape.");
    }

    @Override
    public boolean handleEquipmentAction(Player player, Item item, int slot) {
        if(item.getId() == ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE_T || item.getId() == ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE && slot == EquipSlot.CAPE) {
            teleport(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 2) {
            if(item.getId() == ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE) {
                trim(player);
                return true;
            }

            if(item.getId() == ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE_T) {
                untrim(player);
                return true;
            }
        }

        if (option == 3) {
            if(item.getId() == ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE_T || item.getId() == ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE) {
                teleport(player);
                return true;
            }
        }
        return false;
    }
}

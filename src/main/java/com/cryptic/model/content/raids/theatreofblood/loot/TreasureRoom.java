package com.cryptic.model.content.raids.theatreofblood.loot;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;

public class TreasureRoom extends PacketInteraction {
    int[] monumental_chests = new int[]{33086, 33087, 33088, 33089, 33090};
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == TREASURE_ROOM) {
            System.out.println("id found?");
            player.teleport(3237, 4307, player.getTheatreInstance().getzLevel());
            return true;
        }
        if (ArrayUtils.contains(monumental_chests, object.getId())) {
            if (player.<Integer>getAttribOr(AttributeKey.TOB_LOOT_CHEST, 0) != object.getId()) {
                player.message(Color.RED.wrap("This chest was not meant for you to open."));
                return true;
            }
            int[] rare_loot = new int[]{ItemIdentifiers.CABBAGE, ItemIdentifiers.AVERNIC_DEFENDER_HILT, ItemIdentifiers.GHRAZI_RAPIER, ItemIdentifiers.SANGUINESTI_STAFF, ItemIdentifiers.JUSTICIAR_FACEGUARD, ItemIdentifiers.JUSTICIAR_CHESTGUARD, ItemIdentifiers.JUSTICIAR_LEGGUARDS, ItemIdentifiers.SANGUINE_ORNAMENT_KIT, ItemIdentifiers.HOLY_ORNAMENT_KIT, ItemIdentifiers.SANGUINE_DUST, ItemIdentifiers.LIL_ZIK, ItemIdentifiers.SCYTHE_OF_VITUR};
            player.getPacketSender().sendInterface(12220);
            player.getTheatreInstance().getLootMap().forEach((key, value) -> giveRewards(player, rare_loot, key, value));
            object.replaceWith(new GameObject(32994, object.tile(), object.getType(), object.getRotation()), false);
            player.clearAttrib(AttributeKey.RARE_TOB_REWARD);
            return true;
        }
        return false;
    }

    private void giveRewards(Player player, int[] rare_loot, Player key, Item[] value) {
        if (player == key) {
            for (var i : value) {
                if (i != null) {
                    broadcastWorldMessage(rare_loot, key, i);
                }
            }
            player.getInventory().addOrBank(value);
        }
    }

    private void broadcastWorldMessage(int[] rare_loot, Player key, Item i) {
        if (ArrayUtils.contains(rare_loot, i.getId())) {
            System.out.println(key.getUsername());
            World.getWorld().sendWorldMessage("<img=2010> " + Color.RAID_PURPLE.wrap(key.getUsername() + " has recieved " + i.getAmount() + "x: " + i.name())); //TODO raids kc
        }
    }
}
